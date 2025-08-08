package com.StardewValley.Network.Server;

import com.StardewValley.Network.JsonUtil;
import com.StardewValley.Network.LobbyManager;
import com.StardewValley.Network.Message;
import com.StardewValley.models.Item;
import com.StardewValley.models.Lobby;
import com.StardewValley.models.TradeOffer;
import com.StardewValley.models.User;
import com.StardewValley.repository.UserRepository;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.*;

public class ClientHandler implements Runnable {
    private final Socket clientSocket;
    private final ServerMain server;
    private PrintWriter out;
    private BufferedReader in;
    private String username;
    private final LobbyManager lobbyManager;
    private static final Map<String, TradeOffer> activeTrades = new HashMap<>();

    // NEW: To store map selections for each lobby
    private static final Map<String, Map<String, Integer>> lobbyMapSelections = new HashMap<>();

    public ClientHandler(Socket socket, ServerMain server) {
        this.clientSocket = socket;
        this.server = server;
        this.lobbyManager = LobbyManager.getInstance();
    }

    @Override
    public void run() {
        try {
            out = new PrintWriter(clientSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

            this.username = in.readLine();
            if (this.username == null || this.username.isEmpty()) {
                throw new IOException("Client did not send a username.");
            }
            UserRepository userRepo = UserRepository.getInstance();
            if (userRepo.getUserByUsername(this.username) == null) {
                System.out.println("SERVER: New user added to repository: " + this.username);
            } else {
                System.out.println("SERVER: Existing user connected: " + this.username);
            }
            server.addClient(this.username, this);
            System.out.println("User connected: " + this.username);
            System.out.println("Current users: " + server.getConnectedClients());

            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                Message receivedMessage = JsonUtil.fromJson(inputLine);
                if (receivedMessage != null) {
                    new Thread(() -> processMessage(receivedMessage)).start();
                }
            }
        } catch (IOException e) {
            System.out.println("Client " + (username != null ? username : "") + " disconnected: " + e.getMessage());
        } finally {
            try {
                if (in != null) in.close();
                if (out != null) out.close();
                if (clientSocket != null) clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            server.removeClient(username);
            System.out.println("User disconnected: " + username);
            System.out.println("Current users: " + server.getConnectedClients());
        }
    }

    private void processMessage(Message message) {
        System.out.println("Processing message from " + username + ": " + message.getAction());
        switch (message.getAction()) {
            case CREATE_LOBBY:
                handleCreateLobby(message.getPayload());
                break;
            case JOIN_LOBBY:
                handleJoinLobby(message.getPayload());
                break;
            case LEAVE_LOBBY:
                handleLeaveLobby(message.getPayload());
            case START_GAME:
                handleStartGame(message.getPayload());
                break;
            case SELECT_MAP:
                handleSelectMap(message.getPayload());
                break;
            case PLAYER_MOVE:
                handlePlayerMove(message.getPayload());
                break;
            case SHOW_REACTION:
                handleEmojiReaction(message.getPayload());
                break;
            case ITEM_SOLD_UPDATE:
                handleItemSoldUpdate(message.getPayload());
                break;
            case TRADE_REQUEST:
                handleTradeRequest(message.getPayload());
                break;
            case TRADE_RESPONSE:
                handleTradeResponse(message.getPayload());
                break;
            case TRADE_UPDATE_OFFER:
                handleTradeUpdate(message.getPayload());
                break;
            case TRADE_SUBMIT:
                handleTradeSubmit(message.getPayload());
                break;
            case TRADE_FINALIZE_RESPONSE:
                handleTradeFinalize(message.getPayload());
                break;
            case GET_PLAYER_LIST:
                handleGetPlayerList();
                break;
            default:
                System.out.println("Unknown action received by handler: " + message.getAction());
        }
    }

    private void handleCreateLobby(Map<String, Object> payload) {
        String name = (String) payload.get("name");
        boolean isPublic = (Boolean) payload.get("isPublic");
        String password = (String) payload.get("password");
        boolean isVisible = (Boolean) payload.get("isVisible");
        // GSON ممکن است اعداد را به صورت Double بخواند
        int capacity = ((Double) payload.get("capacity")).intValue();

        // سرور لابی را ایجاد می کند
        Lobby newLobby = new Lobby(name, isPublic, password, isVisible, this.username, capacity);
        lobbyManager.addLobby(newLobby);
        System.out.println("Server created lobby '" + name + "' with ID: " + newLobby.getId() + " for user " + this.username);

        // سرور به کلاینت ایجاد کننده پاسخ می دهد تا وارد لابی شود
        Map<String, Object> responsePayload = new HashMap<>();
        responsePayload.put("lobby", newLobby);
        Message successMessage = new Message(Message.ActionType.CREATE_LOBBY_SUCCESS, responsePayload);
        sendMessage(successMessage);
    }

    private void handleJoinLobby(Map<String, Object> payload) {
        String lobbyId = (String) payload.get("lobbyId");
        String password = (String) payload.get("password");

        Lobby lobby = lobbyManager.getLobbyById(lobbyId).orElse(null);

        if (lobby == null) {
            // Send error message back to client
            return;
        }
        if (lobby.isFull()) {
            // Send error message
            return;
        }
        if (!lobby.isPublic() && (lobby.getPassword() == null || !lobby.getPassword().equals(password))) {
            // Send error message
            return;
        }

        lobby.addMember(this.username);
        lobbyManager.updateLobby(lobby);

        // Broadcast the updated lobby state to all members
        broadcastLobbyState(lobby);
    }

    private void handleLeaveLobby(Map<String, Object> payload) {
        String lobbyId = (String) payload.get("lobbyId");
        Lobby lobby = lobbyManager.getLobbyById(lobbyId).orElse(null);

        if (lobby != null) {
            boolean lobbyIsEmpty = lobby.removeMember(this.username);
            if (lobbyIsEmpty) {
                lobbyManager.getLobbies().remove(lobby);
                lobbyManager.saveLobbies();
                System.out.println("Lobby " + lobbyId + " closed as it is empty.");
            } else {
                lobbyManager.updateLobby(lobby);
                // Broadcast the new state to remaining members
                broadcastLobbyState(lobby);
            }
        }
    }

    private void broadcastLobbyState(Lobby lobby) {
        Map<String, Object> payload = new HashMap<>();
        payload.put("lobby", lobby);
        Message updateMessage = new Message(Message.ActionType.LOBBY_STATE_UPDATE, payload);

        for (String member : lobby.getMembers()) {
            server.sendMessageTo(member, updateMessage);
        }
    }

    private void handleStartGame(Map<String, Object> payload) {
        // دیگر نیازی به بارگذاری مجدد فایل نیست چون سرور همیشه لیست بروز را در حافظه دارد
        String lobbyId = (String) payload.get("lobbyId");
        Lobby lobby = lobbyManager.getLobbyById(lobbyId).orElse(null);

        if (lobby == null || !lobby.getAdmin().equals(this.username)) {
            System.err.println("START_GAME validation failed for user " + this.username + " and lobby " + lobbyId);
            return;
        }

        // Initialize map selection for this lobby
        lobbyMapSelections.put(lobbyId, new HashMap<>());

        Map<String, Object> newPayload = new HashMap<>();
        newPayload.put("lobby", lobby);

        // Send a message to all lobby members to proceed to map selection
        Message proceedMessage = new Message(Message.ActionType.PROCEED_TO_MAP_SELECTION, newPayload);
        for (String memberUsername : lobby.getMembers()) {
            server.sendMessageTo(memberUsername, proceedMessage);
        }
        System.out.println("Handler for " + username + " instructed lobby " + lobbyId + " to proceed to map selection.");
    }
    private void handleEmojiReaction(Map<String, Object> payload) {
        // Create a new message to broadcast to all players
        Map<String, Object> broadcastPayload = new HashMap<>();
        broadcastPayload.put("reaction", payload);

        Message broadcastMessage = new Message(Message.ActionType.SHOW_REACTION, broadcastPayload);

        // Get the lobby for this player
        String playerUsername = (String) payload.get("username");
        Lobby playerLobby = null;

        for (Lobby lobby : lobbyManager.getLobbies()) {
            if (lobby.getMembers().contains(playerUsername)) {
                playerLobby = lobby;
                break;
            }
        }

        // If player is in a lobby, broadcast to all lobby members
        if (playerLobby != null) {
            for (String member : playerLobby.getMembers()) {
                server.sendMessageTo(member, broadcastMessage);
            }
        }
    }
    private void handleSelectMap(Map<String, Object> payload) {
        String lobbyId = (String) payload.get("lobbyId");
        // GSON might deserialize numbers as Double, so we handle both Integer and Double.
        int mapIndex;
        Object mapIndexObj = payload.get("mapIndex");
        if (mapIndexObj instanceof Double) {
            mapIndex = ((Double) mapIndexObj).intValue();
        } else {
            mapIndex = (Integer) mapIndexObj;
        }

        Lobby lobby = lobbyManager.getLobbyById(lobbyId).orElse(null);
        if (lobby == null) return;

        Map<String, Integer> selections = lobbyMapSelections.get(lobbyId);
        if (selections == null) {
            selections = new HashMap<>();
            lobbyMapSelections.put(lobbyId, selections);
        }

        // Don't allow duplicate map selections
        if (selections.containsValue(mapIndex)) {
            // Send error message back to the client
            Map<String, Object> errorPayload = new HashMap<>();
            errorPayload.put("message", "This map has already been selected by another player");
            Message errorMessage = new Message(Message.ActionType.ERROR, errorPayload);
            sendMessage(errorMessage);
            return;
        }

        selections.put(this.username, mapIndex);

        // Update all clients with the current selections
        Map<String, Object> updatePayload = new HashMap<>();
        updatePayload.put("selections", selections);
        Message updateMessage = new Message(Message.ActionType.MAP_SELECTION_UPDATE, updatePayload);
        for (String memberUsername : lobby.getMembers()) {
            server.sendMessageTo(memberUsername, updateMessage);
        }

        // If all players have selected maps, automatically start the game
        if (selections.size() == lobby.getMembers().size()) {
            Map<String, Object> gameStartPayload = new HashMap<>();
            gameStartPayload.put("mapSelections", selections);
            Message gameStartedMessage = new Message(Message.ActionType.GAME_STARTED, gameStartPayload);
            for (String memberUsername : lobby.getMembers()) {
                server.sendMessageTo(memberUsername, gameStartedMessage);
            }
            System.out.println("All players in lobby " + lobbyId + " have selected maps. Starting game.");
        }
    }
    private void handlePlayerMove(Map<String, Object> payload) {
        // Create a new message to broadcast to all players
        Map<String, Object> broadcastPayload = new HashMap<>();
        broadcastPayload.put("player", payload);

        Message broadcastMessage = new Message(Message.ActionType.PLAYER_POSITION_UPDATE, broadcastPayload);

        // Get the lobby for this player
        String playerUsername = (String) payload.get("username");
        Lobby playerLobby = null;

        for (Lobby lobby : lobbyManager.getLobbies()) {
            if (lobby.getMembers().contains(playerUsername)) {
                playerLobby = lobby;
                break;
            }
        }

        // If player is in a lobby, broadcast to all lobby members
        if (playerLobby != null) {
            for (String member : playerLobby.getMembers()) {
                server.sendMessageTo(member, broadcastMessage);
            }
        }
    }

    private void handleItemSoldUpdate(Map<String, Object> payload) {
        // پیدا کردن لابی که فرستنده در آن قرار دارد
        Lobby senderLobby = null;
        for (Lobby lobby : lobbyManager.getLobbies()) {
            if (lobby.getMembers().contains(this.username)) {
                senderLobby = lobby;
                break;
            }
        }

        if (senderLobby != null) {
            // ساخت پیام برای ارسال به دیگران
            Message broadcastMessage = new Message(Message.ActionType.ITEM_SOLD_UPDATE, payload);

            // ارسال پیام به تمام اعضای لابی به جز خود فرستنده
            for (String member : senderLobby.getMembers()) {
                if (!member.equals(this.username)) {
                    server.sendMessageTo(member, broadcastMessage);
                }
            }
        }
    }

    private void handleGetPlayerList() {
        Lobby lobby = lobbyManager.getLobbies().stream()
            .filter(l -> l.getMembers().contains(username))
            .findFirst().orElse(null);
        if (lobby != null) {
            Map<String, Object> payload = new HashMap<>();
            payload.put("players", lobby.getMembers());
            Message playerListMessage = new Message(Message.ActionType.LOBBY_LIST_UPDATE, payload);
            sendMessage(playerListMessage);
        }
    }

    private void handleTradeRequest(Map<String, Object> payload) {
        String receiverUsername = (String) payload.get("receiver");

        // NEW: Get requester's inventory from payload and update the user object on the server
        if (payload.containsKey("inventory")) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> rawInventory = (List<Map<String, Object>>) payload.get("inventory");
            List<Item> requesterInventory = new ArrayList<>();
            for (Map<String, Object> itemMap : rawInventory) {
                String name = (String) itemMap.get("name");
                int quantity = ((Double) itemMap.get("quantity")).intValue();
                String path = (String) itemMap.get("path");
                requesterInventory.add(new Item(name, quantity, path));
            }
            User requester = UserRepository.getInstance().getUserByUsername(this.username);
            if (requester != null) {
                requester.getInventory().setItems(requesterInventory);
            } else {
                System.out.println("SERVER_WARNING: Requester not found in repository during trade request: " + this.username);
            }
        }

        Map<String, Object> invitePayload = new HashMap<>();
        invitePayload.put("requester", this.username);
        Message inviteMessage = new Message(Message.ActionType.TRADE_INVITE, invitePayload);
        server.sendMessageTo(receiverUsername, inviteMessage);
    }

    private void handleTradeResponse(Map<String, Object> payload) {
        String requester = (String) payload.get("requester");
        boolean accepted = (Boolean) payload.get("accepted");

        if (accepted) {
            // دریافت اینونتوری بازیکن دوم (پذیرنده)
            List<Item> receiverInventory = new ArrayList<>();
            if (payload.containsKey("inventory")) {
                @SuppressWarnings("unchecked")
                List<Map<String, Object>> rawInventory = (List<Map<String, Object>>) payload.get("inventory");
                for (Map<String, Object> itemMap : rawInventory) {
                    String name = (String) itemMap.get("name");
                    int quantity = ((Double) itemMap.get("quantity")).intValue();
                    String path = (String) itemMap.get("path");
                    receiverInventory.add(new Item(name, quantity, path));
                }
            }

            // آپدیت کردن اینونتوری بازیکن دوم در سرور
            User receiverUser = UserRepository.getInstance().getUserByUsername(this.username);
            if (receiverUser != null) {
                receiverUser.getInventory().setItems(receiverInventory);
            } else {
                System.out.println("SERVER_WARNING: Receiver not found in repository during trade response: " + this.username);
            }

            // دریافت اینونتوری بازیکن اول (درخواست دهنده) از سرور
            User requesterUser = UserRepository.getInstance().getUserByUsername(requester);
            List<Item> requesterInventory = new ArrayList<>();
            if (requesterUser != null) {
                requesterInventory = requesterUser.getInventory().getItems();
            } else {
                System.out.println("SERVER_WARNING: Requester not found in repository during trade response: " + requester);
            }


            TradeOffer newTrade = new TradeOffer(requester, this.username);
            String tradeId = UUID.randomUUID().toString();
            activeTrades.put(tradeId, newTrade);

            Map<String, Object> startPayload = new HashMap<>();
            startPayload.put("tradeId", tradeId);
            startPayload.put("requester", requester);
            startPayload.put("receiver", this.username);

            // **اضافه کردن اینونتوری هر دو بازیکن به پیام**
            startPayload.put("requesterInventory", requesterInventory);
            startPayload.put("receiverInventory", receiverInventory);

            Message startMessage = new Message(Message.ActionType.TRADE_START, startPayload);
            server.sendMessageTo(requester, startMessage);
            server.sendMessageTo(this.username, startMessage);
        } else {
            // اطلاع رسانی به درخواست دهنده در صورت رد شدن ترید
            Map<String, Object> rejectPayload = new HashMap<>();
            rejectPayload.put("message", this.username + " rejected your trade request.");
            Message rejectMessage = new Message(Message.ActionType.TRADE_CANCELLED, rejectPayload);
            server.sendMessageTo(requester, rejectMessage);
        }
    }

    private void handleTradeUpdate(Map<String, Object> payload) {
        String tradeId = (String) payload.get("tradeId");
        String senderUsername = this.username; // کاربری که این آپدیت را ارسال کرده است

        synchronized (activeTrades) {
            TradeOffer offer = activeTrades.get(tradeId);
            if (offer != null) {
                // بر اساس توضیحات، فقط درخواست‌دهنده می‌تواند ترید را تغییر دهد
                // این موضوع باید در سمت سرور کنترل شود
                if (!senderUsername.equals(offer.getRequester())) {
                    System.out.println("SERVER_WARNING: Received trade update from non-requester (" + senderUsername + "). Ignoring.");
                    return;
                }

                // از آنجایی که فقط درخواست‌دهنده آپدیت ارسال می‌کند، مستقیماً وضعیت ترید در سرور را به‌روز می‌کنیم
                offer.setOfferedItems(parseItemsFromPayload(payload, "offeredItems"));
                offer.setRequestedItems(parseItemsFromPayload(payload, "requestedItems"));
                offer.setOfferedMoney(((Double) payload.get("offeredMoney")).intValue());
                offer.setRequestedMoney(((Double) payload.get("requestedMoney")).intValue());

                // اکنون، وضعیت جدید را فقط برای گیرنده ارسال می‌کنیم
                // دیدگاه در پیام ارسالی برای گیرنده باید مناسب او باشد
                Map<String, Object> payloadForReceiver = new HashMap<>();
                payloadForReceiver.put("offeredItems", offer.getOfferedItems()); // آیتم‌هایی که درخواست‌دهنده پیشنهاد می‌دهد
                payloadForReceiver.put("requestedItems", offer.getRequestedItems()); // آیتم‌هایی که درخواست‌دهنده می‌خواهد
                payloadForReceiver.put("offeredMoney", offer.getOfferedMoney());
                payloadForReceiver.put("requestedMoney", offer.getRequestedMoney());

                // ارسال پیام به بازیکن دیگر (گیرنده)
                server.sendMessageTo(offer.getReceiver(), new Message(Message.ActionType.TRADE_OFFER_UPDATED, payloadForReceiver));
            }
        }
    }

    private List<Item> parseItemsFromPayload(Map<String, Object> payload, String key) {
        List<Item> items = new ArrayList<>();
        if (payload.get(key) instanceof List) {
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> rawItems = (List<Map<String, Object>>) payload.get(key);
            if (rawItems != null) {
                for (Map<String, Object> itemMap : rawItems) {
                    // اطمینان از وجود همه فیلدها و مدیریت خطاهای احتمالی در تبدیل نوع داده‌ها
                    String name = (String) itemMap.get("name");
                    int quantity = ((Double) itemMap.get("quantity")).intValue();
                    String path = (String) itemMap.get("path");
                    items.add(new Item(name, quantity, path));
                }
            }
        }
        return items;
    }

    private void handleTradeSubmit(Map<String, Object> payload) {
        String tradeId = (String) payload.get("tradeId");
        System.out.println("SERVER_RECEIVE_SUBMIT: tradeId=" + payload.get("tradeId") + ", requester=" + payload.get("requester") + ", receiver=" + payload.get("receiver"));
        synchronized (activeTrades) {
            TradeOffer offer = activeTrades.get(tradeId);
            if (offer != null && offer.getRequester().equals(this.username)) {
                offer.setSubmitted(true);

                Map<String, Object> finalizePayload = new HashMap<>();
                finalizePayload.put("tradeId", tradeId);
                finalizePayload.put("offer", offer);

                Message finalizeMessage = new Message(Message.ActionType.TRADE_FINALIZE_REQUEST, finalizePayload);
                server.sendMessageTo(offer.getReceiver(), finalizeMessage);
            }
        }
    }

    private void handleTradeFinalize(Map<String, Object> payload) {
        String tradeId = (String) payload.get("tradeId");
        boolean accepted = (Boolean) payload.get("accepted");
        System.out.println("SERVER_RECEIVE_FINALIZE: tradeId=" + tradeId + ", accepted=" + accepted + ", requester=" + payload.get("requester") + ", receiver=" + payload.get("receiver"));

        synchronized (activeTrades) {
            TradeOffer offer = activeTrades.get(tradeId);
            if (offer != null && offer.getReceiver().equals(this.username)) {
                if (accepted) {
                    User requester = UserRepository.getInstance().getUserByUsername(offer.getRequester());
                    User receiver = UserRepository.getInstance().getUserByUsername(offer.getReceiver());

                    if (requester == null || receiver == null) {
                        System.out.println("SERVER_CHECK_FAIL: User not found.");
                        sendErrorToBoth(offer, "User not found during trade finalization.");
                        activeTrades.remove(tradeId);
                        return;
                    }

                    // VALIDATION: بررسی اینکه آیا هر دو بازیکن منابع کافی را دارند یا خیر
                    boolean requesterItemsOk = true;
                    for (Item item : offer.getOfferedItems()) {
                        if (!requester.getInventory().hasItem(item.getName(), item.getQuantity())) {
                            requesterItemsOk = false;
                            break;
                        }
                    }
                    boolean receiverItemsOk = true;
                    for (Item item : offer.getRequestedItems()) {
                        if (!receiver.getInventory().hasItem(item.getName(), item.getQuantity())) {
                            receiverItemsOk = false;
                            break;
                        }
                    }

                    if (!requesterItemsOk || !receiverItemsOk || requester.getMoney() < offer.getOfferedMoney() || receiver.getMoney() < offer.getRequestedMoney()) {
                        sendErrorToBoth(offer, "Not enough resources to complete trade.");
                        activeTrades.remove(tradeId);
                        return;
                    }

                    // TRANSACTION: انجام ترید
                    System.out.println("SERVER_CHECK_PASS: Applying trade changes.");
                    requester.setMoney(requester.getMoney() - offer.getOfferedMoney() + offer.getRequestedMoney());
                    receiver.setMoney(receiver.getMoney() + offer.getOfferedMoney() - offer.getRequestedMoney());

                    for (Item item : offer.getOfferedItems()) {
                        requester.getInventory().removeItemByName(item.getName(), item.getQuantity());
                        receiver.getInventory().addItem(item);
                    }
                    for (Item item : offer.getRequestedItems()) {
                        receiver.getInventory().removeItemByName(item.getName(), item.getQuantity());
                        requester.getInventory().addItem(item);
                    }

                    // PERSIST: ذخیره تغییرات در فایل
                    UserRepository.getInstance().saveUsers();
                    System.out.println("SERVER: User data saved after trade.");

                    // NOTIFY CLIENTS: ارسال پیام برای به‌روزرسانی موجودی به هر دو بازیکن

                    // ارسال پیام به‌روزرسانی به درخواست‌دهنده ترید
                    Map<String, Object> requesterUpdatePayload = new HashMap<>();
                    requesterUpdatePayload.put("inventory", requester.getInventory().getItems());
                    requesterUpdatePayload.put("money", requester.getMoney());
                    Message requesterUpdateMessage = new Message(Message.ActionType.INVENTORY_UPDATE, requesterUpdatePayload);
                    server.sendMessageTo(requester.getUsername(), requesterUpdateMessage);
                    System.out.println("SERVER: Sent INVENTORY_UPDATE to requester: " + requester.getUsername());

                    // ارسال پیام به‌روزرسانی به دریافت‌کننده ترید
                    Map<String, Object> receiverUpdatePayload = new HashMap<>();
                    receiverUpdatePayload.put("inventory", receiver.getInventory().getItems());
                    receiverUpdatePayload.put("money", receiver.getMoney());
                    Message receiverUpdateMessage = new Message(Message.ActionType.INVENTORY_UPDATE, receiverUpdatePayload);
                    server.sendMessageTo(receiver.getUsername(), receiverUpdateMessage);
                    System.out.println("SERVER: Sent INVENTORY_UPDATE to receiver: " + receiver.getUsername());

                    // ارسال پیام تکمیل ترید برای بستن رابط کاربری ترید
                    offer.setFinalized(true);
                    Map<String, Object> completePayload = new HashMap<>();
                    completePayload.put("offer", offer);
                    Message completeMessage = new Message(Message.ActionType.TRADE_COMPLETE, completePayload);
                    server.sendMessageTo(offer.getRequester(), completeMessage);
                    server.sendMessageTo(offer.getReceiver(), completeMessage);
                    System.out.println("SERVER: Sent TRADE_COMPLETE to both parties.");

                } else {
                    // رد شدن ترید
                    System.out.println("SERVER: Trade rejected by receiver.");
                    Message cancelMessage = new Message(Message.ActionType.TRADE_CANCELLED, new HashMap<>());
                    server.sendMessageTo(offer.getRequester(), cancelMessage);
                    server.sendMessageTo(offer.getReceiver(), cancelMessage);
                }
                // حذف ترید از لیست تریدهای فعال
                activeTrades.remove(tradeId);
            }
        }
    }

    private void sendErrorToBoth(TradeOffer offer, String errorMsg) {
        Map<String, Object> errorPayload = new HashMap<>();
        errorPayload.put("message", "Trade failed: " + errorMsg);
        Message errorMessage = new Message(Message.ActionType.TRADE_CANCELLED, errorPayload); // Use CANCELLED for fail
        server.sendMessageTo(offer.getRequester(), errorMessage);
        server.sendMessageTo(offer.getReceiver(), errorMessage);
    }

    public void sendMessage(Message message) {
        if (out != null) {
            String jsonMessage = JsonUtil.toJson(message);
            out.println(jsonMessage);
        }
    }

    public String getUsername() {
        return username;
    }
}
