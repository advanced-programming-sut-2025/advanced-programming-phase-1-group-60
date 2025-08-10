package com.StardewValley.Network.Client;

import com.StardewValley.AssetsManager.MenuManager;
import com.StardewValley.Network.JsonUtil;
import com.StardewValley.Network.Message;
import com.StardewValley.controller.LobbyController;
import com.StardewValley.controller.TradeController;
import com.StardewValley.exceptions.GameException;
import com.StardewValley.models.Lobby;
import com.StardewValley.models.User;
import com.StardewValley.repository.UserRepository;
import com.StardewValley.view.InLobbyView;
import com.StardewValley.view.MapSelectionView;
import com.StardewValley.view.MapView;
import com.StardewValley.models.*;
import com.StardewValley.view.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Dialog;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerListener implements Runnable {
    private final Socket socket;
    private final Game game;
    private final LobbyController lobbyController;
    private BufferedReader in;
    private final Gson gson = new Gson();

    public ServerListener(Socket socket, Game game, LobbyController lobbyController) {
        this.socket = socket;
        this.game = game;
        this.lobbyController = lobbyController;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            String serverResponse;
            while ((serverResponse = in.readLine()) != null) {
                Message message = JsonUtil.fromJson(serverResponse);
                if (message != null) {
                    Gdx.app.postRunnable(() -> {
                        try {
                            handleMessage(message);
                        } catch (GameException e) {
                            throw new RuntimeException(e);
                        }
                    });
                }
            }
        } catch (IOException e) {
            System.out.println("Disconnected from server: " + e.getMessage());
        } finally {
            ClientMain.isConnected = false;
        }
    }

    @SuppressWarnings("unchecked")
    private void handleMessage(Message message) throws GameException {
        if (!message.getAction().equals(Message.ActionType.PLAYER_POSITION_UPDATE)) {
            System.out.println("CLIENT_RECEIVE_MESSAGE: actionType=" + message.getAction() + ", payload=" + message.getPayload());
        }
        switch (message.getAction()) {
            case CREATE_LOBBY_SUCCESS: {
                Object createdLobbyData = message.getPayload().get("lobby");
                String createdLobbyJson = gson.toJson(createdLobbyData);
                Lobby createdLobby = gson.fromJson(createdLobbyJson, Lobby.class);
                if (createdLobby != null) {
                    game.setScreen(new InLobbyView(game, lobbyController, createdLobby));
                }
                break;
            }
            case PROCEED_TO_MAP_SELECTION: {
                Object lobbyData = message.getPayload().get("lobby");
                String lobbyJson = gson.toJson(lobbyData);
                Lobby lobby = gson.fromJson(lobbyJson, Lobby.class);
                if (lobby != null) {
                    game.setScreen(new MapSelectionView(game, lobbyController, lobby));
                }
                break;
            }
            case MAP_SELECTION_UPDATE: {
                if (game.getScreen() instanceof MapSelectionView) {
                    Map<String, Double> rawSelections =
                        (Map<String, Double>) message.getPayload().get("selections");
                    Map<String, Integer> selections = new HashMap<>();
                    for (Map.Entry<String, Double> entry : rawSelections.entrySet()) {
                        selections.put(entry.getKey(), entry.getValue().intValue());
                    }
                    ((MapSelectionView) game.getScreen()).updateMapSelections(selections);
                }
                break;
            }
            case GAME_STARTED: {
                System.out.println("Server commanded to start the game!");

                // Get the map selections from the payload
                Map<String, Double> rawSelections = (Map<String, Double>) message.getPayload().get("mapSelections");
                Map<String, Integer> mapSelections = new HashMap<>();

                // Convert Double to Integer (GSON limitation)
                for (Map.Entry<String, Double> entry : rawSelections.entrySet()) {
                    mapSelections.put(entry.getKey(), entry.getValue().intValue());
                }

                // Create the game instance and start gameplay
                lobbyController.handleGameStart(mapSelections);

                break;
            }
            case LOBBY_STATE_UPDATE: {
                Object lobbyData = message.getPayload().get("lobby");
                String lobbyJson = gson.toJson(lobbyData);
                Lobby lobby = gson.fromJson(lobbyJson, Lobby.class);

                if (lobby != null) {
                    if (game.getScreen() instanceof InLobbyView) {
                        ((InLobbyView) game.getScreen()).updateLobbyState(lobby);
                    } else {
                        game.setScreen(new InLobbyView(game, lobbyController, lobby));
                    }
                }
                break;
            }
            case PLAYER_POSITION_UPDATE: {
                if (game.getScreen() instanceof MapView) {
                    Map<String, Object> playerData = (Map<String, Object>) message.getPayload().get("player");
                    String username = (String) playerData.get("username");
                    double x = (double) playerData.get("x");
                    double y = (double) playerData.get("y");
                    int direction = ((Double) playerData.get("direction")).intValue();
                    boolean moving = (boolean) playerData.get("moving");
                    boolean inVillage = (boolean) playerData.get("inVillage");
                    int farmIndex = ((Double) playerData.get("farmIndex")).intValue();

                    MapView mapView = (MapView) game.getScreen();
                    mapView.updateNetworkPlayerPosition(
                        username, (float)x, (float)y, direction, moving, inVillage, farmIndex);
                }
                break;
            }
            case SHOW_REACTION: {
                if (game.getScreen() instanceof MapView) {
                    Map<String, Object> reactionData = (Map<String, Object>) message.getPayload().get("reaction");
                    String username = (String) reactionData.get("username");
                    int emojiId = ((Double) reactionData.get("emojiId")).intValue();
                    String text = (String) reactionData.get("text");

                    MapView mapView = (MapView) game.getScreen();
                    mapView.showEmojiReaction(username, emojiId, text);
                }
                break;
            } case ITEM_SOLD_UPDATE: { // <-- کیس جدید
                Map<String, Object> payload = message.getPayload();
                String storeName = (String) payload.get("storeName");
                String itemName = (String) payload.get("itemName");
                String itemType = (String) payload.get("itemType");
                int level = ((Double) payload.get("level")).intValue(); // Gson اعداد را Double می‌خواند

                // پیدا کردن فروشگاه مورد نظر و به‌روزرسانی آن
                com.StardewValley.models.Game gameInstance = com.StardewValley.models.Game.getInstance();
                if (gameInstance != null && gameInstance.getCurrentMap() != null) {
                    for (StaticElement element : gameInstance.getCurrentMap().getVillage().getElements()) {
                        if (element instanceof Store && ((Store) element).getName().equalsIgnoreCase(storeName)) {
                            ((Store) element).markAsSold(itemName, itemType, level);
                            break;
                        }
                    }
                }
                break;
            }
            case TRADE_INVITE:
                handleTradeInvite(message.getPayload());
                break;
            case TRADE_START:
                handleTradeStart(message.getPayload());
                break;
            case TRADE_OFFER_UPDATED:
                handleTradeOfferUpdated(message.getPayload());
                break;
            case TRADE_FINALIZE_REQUEST:
                handleTradeFinalizeRequest(message.getPayload());
                break;
            case TRADE_COMPLETE:
            case TRADE_CANCELLED:
                handleTradeEnd(message);
                break;
            case LOBBY_LIST_UPDATE:
                handlePlayerListUpdate(message.getPayload());
                break;
            case INVENTORY_UPDATE: {
                Map<String, Object> payload = message.getPayload();
                List<Map<String, Object>> rawInventory = (List<Map<String, Object>>) payload.get("inventory");
                List<Item> updatedInventory = new ArrayList<>();
                if (rawInventory != null) {
                    for (Map<String, Object> itemMap : rawInventory) {
                        String name = (String) itemMap.get("name");
                        int quantity = ((Double) itemMap.get("quantity")).intValue();
                        String path = (String) itemMap.get("path");
                        updatedInventory.add(new Item(name, quantity, path));
                    }
                }
                int updatedMoney = ((Double) payload.get("money")).intValue();

                User localPlayer = lobbyController.getLoginController().getLoggedInUser();
                localPlayer.getInventory().setItems(updatedInventory);
                localPlayer.setMoney(updatedMoney);
                System.out.println("CLIENT: Inventory updated for " + localPlayer.getUsername());
                break;
            }
            case PLAYER_DATA_UPDATE: { // NEW: Handle PLAYER_DATA_UPDATE from server
                Map<String, Object> updatedUserData = (Map<String, Object>) message.getPayload().get("updatedUser");
                if (updatedUserData != null) {
                    String username = (String) updatedUserData.get("username");
                    double money = ((Double) updatedUserData.get("money"));
                    double completedQuestsCount = ((Double) updatedUserData.get("completedQuestsCount"));
                    double averageSkillLevel = ((Double) updatedUserData.get("averageSkillLevel"));

                    // Update the local Game model's User object
                    User userToUpdate = com.StardewValley.models.Game.getInstance().getUserByUsername(username);
                    if (userToUpdate != null) {
                        userToUpdate.setMoney((int) money);
                        // Ensure User class has setters for these if you want to update them directly
                        // userToUpdate.setCompletedQuestsCount((int) completedQuestsCount);
                        // userToUpdate.setAverageSkillLevel((float) averageSkillLevel);

                        // If you have a more complex User object, you might need to deserialize it fully
                        // User updatedUser = gson.fromJson(gson.toJson(updatedUserData), User.class);
                        // Game.getInstance().updateUser(updatedUser); // A method to replace/update user in Game

                        System.out.println("CLIENT_DEBUG: Updated local User data for " + username + ": Money=" + userToUpdate.getMoney() +
                            ", Quests=" + userToUpdate.getCompletedQuestsCount() + ", AvgSkill=" + userToUpdate.getAverageSkillLevel());
                    }
                }
                break;
            }
            case CHAT_MESSAGE_PUBLIC:
            case CHAT_MESSAGE_PRIVATE: {
                String sender = (String) message.getPayload().get("sender");
                String msgContent = (String) message.getPayload().get("message");
                String localUsername = lobbyController.getLoginController().getLoggedInUser().getUsername();
                String formattedMessage;

                if (message.getAction() == Message.ActionType.CHAT_MESSAGE_PRIVATE) {
                    String recipient = (String) message.getPayload().get("recipient");
                    if (recipient == null) { // Fallback in case recipient is null in payload
                        formattedMessage = sender + " (Private): " + msgContent;
                    } else if (sender.equalsIgnoreCase(localUsername)) {
                        formattedMessage = "You (to " + recipient + "): " + msgContent;
                    } else if (recipient.equalsIgnoreCase(localUsername)) {
                        formattedMessage = sender + " (Private): " + msgContent;
                    } else {
                        return; // This private message is not for the current client.
                    }
                } else { // Public message
                    formattedMessage = sender + " (Public): " + msgContent;
                }

                if (game.getScreen() instanceof ChatView) {
                    ((ChatView) game.getScreen()).addMessageToHistory(formattedMessage);
                    System.out.println("CLIENT_RECEIVE_CHAT: Added message to chat history: " + formattedMessage);
                } else if (game.getScreen() instanceof MapView) {
                    String chatType = (message.getAction() == Message.ActionType.CHAT_MESSAGE_PUBLIC) ? "Public" : "Private";
                   // ((MapView) game.getScreen()).showNotification("New " + chatType + " Message from " + sender);
                    System.out.println("CLIENT_RECEIVE_CHAT: ChatView not active. Showing notification on MapView for: " + formattedMessage);
                } else {
                    System.out.println("CLIENT_RECEIVE_CHAT: Received chat message, but neither ChatView nor MapView is active: " + formattedMessage);
                }
                break;
            }

            case GROUP_MISSION_UPDATE:
                handleGroupMissionUpdate(message.getPayload());
                break;
            case ERROR: {
                String errorMessage = (String) message.getPayload().get("message");
                System.err.println("SERVER ERROR: " + errorMessage);
                break;
            }
            case TRADE_HISTORY_ADD:
                handleTradeHistoryAdd(message.getPayload());
            default: {
                System.out.println("Unhandled message from server: " + message.getAction());
                break;
            }
        }
    }

    private void handlePlayerListUpdate(Map<String, Object> payload) {
        if (game.getScreen() instanceof TradeMenuView) {
            List<String> players = (List<String>) payload.get("players");
            ((TradeMenuView) game.getScreen()).updatePlayerList(players);
        }
    }

    private void handleTradeInvite(Map<String, Object> payload) {
        String requester = (String) payload.get("requester");

        Dialog dialog = new Dialog("Trade Request", MenuManager.getInstance().getPixthulhuSkin()) {
            @Override
            protected void result(Object object) {
                boolean accepted = (Boolean) object;
                Map<String, Object> responsePayload = new HashMap<>();
                responsePayload.put("requester", requester);
                responsePayload.put("accepted", accepted);

                // NEW: If accepting, send the full inventory to the server
                if (accepted) {
                    User localPlayer = lobbyController.getLoginController().getLoggedInUser();
                    responsePayload.put("inventory", localPlayer.getInventory().getItems());
                }

                Message responseMessage = new Message(Message.ActionType.TRADE_RESPONSE, responsePayload);
                ClientMain.sendMessage(responseMessage);
            }
        };
        dialog.text(requester + " wants to trade with you.");
        dialog.button("Accept", true);
        dialog.button("Reject", false);

        Stage currentStage = null;
        Screen currentScreen = game.getScreen();

        if (currentScreen instanceof MapView) {
            currentStage = ((MapView) currentScreen).getStage();
        } else if (currentScreen instanceof GameView) {
            currentStage = ((GameView) currentScreen).getStage();
        } else if (currentScreen instanceof TradeMenuView) {
            currentStage = ((TradeMenuView) currentScreen).getStage();
        }

        if (currentStage != null) {
            dialog.show(currentStage);
        } else {
            System.err.println("Cannot show trade invite: No suitable stage found for the current screen.");
        }
    }

    @SuppressWarnings("unchecked")
    private List<Item> parseInventory(Object rawInventoryData) {
        List<Item> inventory = new ArrayList<>();
        if (rawInventoryData instanceof List) {
            List<Map<String, Object>> rawList = (List<Map<String, Object>>) rawInventoryData;
            for (Map<String, Object> itemMap : rawList) {
                String name = (String) itemMap.get("name");
                int quantity = ((Double) itemMap.get("quantity")).intValue();
                String path = (String) itemMap.get("path");
                inventory.add(new Item(name, quantity, path));
            }
        }
        return inventory;
    }

    // متد اصلاح شده handleTradeStart
    private void handleTradeStart(Map<String, Object> payload) {
        String tradeId = (String) payload.get("tradeId");
        String requester = (String) payload.get("requester");
        String receiver = (String) payload.get("receiver");

        // **دریافت و پردازش اینونتوری‌ها از پیام سرور**
        List<Item> requesterInventory = parseInventory(payload.get("requesterInventory"));
        List<Item> receiverInventory = parseInventory(payload.get("receiverInventory"));

        User localPlayer = lobbyController.getLoginController().getLoggedInUser();
        boolean isRequester = localPlayer.getUsername().equals(requester);

        String remoteUsername = isRequester ? receiver : requester;
        User remotePlayer = lobbyController.getUserByUsername(remoteUsername);

        // **آپدیت کردن اینونتوری بازیکن مقابل در کلاینت محلی**
        if (remotePlayer != null) {
            if (isRequester) {
                remotePlayer.getInventory().setItems(receiverInventory);
            } else {
                remotePlayer.getInventory().setItems(requesterInventory);
            }
        }

        GameView gameView = null;
        Screen currentScreen = game.getScreen();

        if (currentScreen instanceof MapView) {
            gameView = ((MapView) currentScreen).getGameView();
        } else if (currentScreen instanceof TradeMenuView) {
            gameView = ((TradeMenuView) currentScreen).getGameView();
        } else if (currentScreen instanceof GameView) {
            gameView = (GameView) currentScreen;
        }

        if (gameView != null) {
            final GameView finalGameView = gameView;
            final User finalRemotePlayer = remotePlayer;
            final String finalTradeId = tradeId;

            Gdx.app.postRunnable(() -> {
                finalGameView.showTradeView(localPlayer, finalRemotePlayer, isRequester, finalTradeId);
            });
        } else {
            System.err.println("Cannot switch to TradeView: GameView instance not found from current screen: " + currentScreen.getClass().getName());
        }
    }

    private void handleTradeOfferUpdated(Map<String, Object> payload) {
        if (game.getScreen() instanceof TradeView) {
            // Extract raw lists (GSON deserializes to List<Map<String, Object>>)
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> offeredRaw = (List<Map<String, Object>>) payload.get("offeredItems");
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> requestedRaw = (List<Map<String, Object>>) payload.get("requestedItems");

            // Convert to List<Item>
            List<Item> offeredItems = new ArrayList<>();
            if (offeredRaw != null) {
                for (Map<String, Object> itemMap : offeredRaw) {
                    String name = (String) itemMap.get("name"); // Adjust field names if different in Item class
                    int quantity = ((Double) itemMap.get("quantity")).intValue();
                    String path = (String) itemMap.get("path");
                    offeredItems.add(new Item(name, quantity, path));
                }
            }

            List<Item> requestedItems = new ArrayList<>();
            if (requestedRaw != null) {
                for (Map<String, Object> itemMap : requestedRaw) {
                    String name = (String) itemMap.get("name");
                    int quantity = ((Double) itemMap.get("quantity")).intValue();
                    String path = (String) itemMap.get("path");
                    requestedItems.add(new Item(name, quantity, path));
                }
            }

            int offeredMoney = ((Double) payload.get("offeredMoney")).intValue();
            int requestedMoney = ((Double) payload.get("requestedMoney")).intValue();

            ((TradeView) game.getScreen()).updateTradeOffer(offeredItems, requestedItems, offeredMoney, requestedMoney);
        }
    }

    private void handleTradeFinalizeRequest(Map<String, Object> payload) {
        if (game.getScreen() instanceof TradeView) {
            ((TradeView) game.getScreen()).showFinalizeButtons();
        }
    }

    private void handleTradeEnd(Message message) { // Now receives the message
        if (game.getScreen() instanceof TradeView) {
            boolean accepted = message.getAction() == Message.ActionType.TRADE_COMPLETE;
            ((TradeView) game.getScreen()).finalizeTrade(accepted);
        }
    }

    private void handleTradeHistoryAdd(Map<String, Object> payload) {
        String tradeOfferJson = gson.toJson(payload.get("tradeOffer"));
        TradeOffer offer = gson.fromJson(tradeOfferJson, TradeOffer.class);
        boolean accepted = (Boolean) payload.get("accepted");

        User fromUser = UserRepository.getInstance().getUserByUsername(offer.getRequester());
        User toUser = UserRepository.getInstance().getUserByUsername(offer.getReceiver());

        if (fromUser != null && toUser != null) {
            Trade trade = new Trade();
            trade.setId(TradeController.getNextTradeId());
            trade.setFromUser(fromUser);
            trade.setToUser(toUser);
            if (!offer.getOfferedItems().isEmpty()) {
                trade.setOfferedItems(offer.getOfferedItems().get(0));
            }
            if (!offer.getRequestedItems().isEmpty()) {
                trade.setRequestedItems(offer.getRequestedItems().get(0));
            }
            trade.setOfferedMoney(offer.getOfferedMoney());
            trade.setRequestedMoney(offer.getRequestedMoney());
            trade.setAccepted(accepted);
            trade.setTimestamp(TimeSystem.getInstance().getDateTime());

            TradeController.addUserTrade(fromUser, trade);
            TradeController.addUserTrade(toUser, trade);
            TradeController.addRespondedTrade(fromUser, trade.getId());
            TradeController.addRespondedTrade(toUser, trade.getId());
        }
    }

    private void handleGroupMissionUpdate(Map<String, Object> payload) {
        Gson gson = new Gson();
        Type missionListType = new TypeToken<ArrayList<GroupMission>>(){}.getType();

        List<GroupMission> available = gson.fromJson(gson.toJson(payload.get("available")), missionListType);
        List<GroupMission> active = gson.fromJson(gson.toJson(payload.get("active")), missionListType);

        if (game.getScreen() instanceof GroupMissionView) {
            ((GroupMissionView) game.getScreen()).updateMissions(available, active);
        } else if (game.getScreen() instanceof MapView) {
            MapView mapView = (MapView) game.getScreen();
            mapView.getGameView().showGroupMissionView(available, active);
        }
    }
}
