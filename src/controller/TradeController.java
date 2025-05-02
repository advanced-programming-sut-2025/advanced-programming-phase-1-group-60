package controller;

import models.Inventory;
import models.Item;
import models.TimeSystem;
import models.Trade;
import models.User;
import repository.UserRepository;

import java.util.*;

public class TradeController {
    private Scanner sc;
    private User mainUser;
    private Map<User, List<Trade>> userTrades;
    private Map<User, Set<Integer>> shownTrades;
    private Map<User, Set<Integer>> respondedTrades;
    private int tradeIdCounter = 1;

    public TradeController(Scanner sc, User user) {
        this.sc = sc;
        this.mainUser = user;
        this.userTrades = new HashMap<>();
        this.shownTrades = new HashMap<>();
        this.respondedTrades = new HashMap<>();
        for (User u : UserRepository.getInstance().getAllUsers()) {
            userTrades.put(u, new ArrayList<>());
            shownTrades.put(u, new HashSet<>());
            respondedTrades.put(u, new HashSet<>());
        }
    }

    public void startTrade() {
        System.out.println("Entering trade menu. Type 'exit trade' to return.");
        while (true) {
            // Notify new incoming requests
            List<Trade> pending = getPendingFor(mainUser);
            for (Trade t : pending) {
                if (!shownTrades.get(mainUser).contains(t.getId())) {
                    System.out.println("New trade request #" + t.getId() + " from " + t.getFromUser().getUsername());
                    shownTrades.get(mainUser).add(t.getId());
                }
            }

            String input = sc.nextLine().trim();
            if ("exit trade".equalsIgnoreCase(input)) {
                System.out.println("Exiting trade menu.");
                break;
            }
            try {
                if (input.startsWith("trade ")) {
                    String[] parts = input.split(" ");
                    String cmd = parts[1];
                    switch (cmd) {
                        case "list":
                            listTrades();
                            break;
                        case "response":
                            respondToTrade(input);
                            break;
                        case "history":
                            history();
                            break;
                        default:
                            // assume new trade command
                            handleNewTrade(input);
                    }
                } else {
                    System.out.println("Unknown command in trade menu.");
                }
            } catch (Exception ex) {
                System.out.println("Error: " + ex.getMessage());
            }
        }
    }

    private void handleNewTrade(String input) {
        String[] parts = input.split(" ");
        Map<String, String> params = new HashMap<>();
        for (int i = 1; i < parts.length - 1; i++) {
            if (parts[i].startsWith("-")) {
                String key = parts[i];
                String value = parts[i + 1];
                if (!value.startsWith("-")) {
                    params.put(key, value);
                    i++;
                }
            }
        }
        User target = UserRepository.getInstance().getUserByUsername(params.get("-u"));
        if (target == null) {
            System.out.println("Target user not found.");
            return;
        }
        Trade trade = createTradeFromParams(params, target);
        if (trade == null) return;

        trade.setId(tradeIdCounter++);
        trade.setFromUser(mainUser);
        trade.setToUser(target);
        trade.setTimestamp(TimeSystem.getInstance().getDateTime());

        userTrades.get(target).add(trade);
        userTrades.get(mainUser).add(trade);

        System.out.println("Trade request #" + trade.getId() + " created.");
    }

    private Trade createTradeFromParams(Map<String, String> params, User target) {
        Trade trade = new Trade();
        String type = params.get("-t");
        boolean isOffer = "offer".equalsIgnoreCase(type);

        // main item
        String itemName = params.get("-i");
        int amount;
        try {
            amount = Integer.parseInt(params.get("-a"));
        } catch (NumberFormatException e) {
            System.out.println("Invalid amount.");
            return null;
        }
        Item mainItem = new Item();
        mainItem.setName(itemName);
        mainItem.setQuantity(amount);
        if (isOffer) trade.addOfferedItem(mainItem);
        else trade.addRequestedItem(mainItem);

        boolean hasMoney = params.containsKey("-p");
        boolean hasOtherItem = params.containsKey("-ti") && params.containsKey("-ta");
        if (hasMoney && hasOtherItem) {
            System.out.println("You can't have donkey and date at the same time.");
            return null;
        }
        if (hasMoney) {
            try {
                int money = Integer.parseInt(params.get("-p"));
                if (isOffer) trade.setRequestedMoney(money);
                else trade.setOfferedMoney(money);
            } catch (NumberFormatException e) {
                System.out.println("Invalid money amount.");
                return null;
            }
        }
        if (hasOtherItem) {
            String tname = params.get("-ti");
            int tamount;
            try { tamount = Integer.parseInt(params.get("-ta")); }
            catch (NumberFormatException e) {
                System.out.println("Invalid target amount.");
                return null;
            }
            Item targetItem = new Item();
            targetItem.setName(tname);
            targetItem.setQuantity(tamount);
            if (isOffer) trade.addRequestedItem(targetItem);
            else trade.addOfferedItem(targetItem);
        }
        return trade;
    }

    private List<Trade> getPendingFor(User user) {
        List<Trade> all = userTrades.get(user);
        List<Trade> pending = new ArrayList<>();
        for (Trade t : all) {
            if (t.getToUser().equals(user) && !respondedTrades.get(user).contains(t.getId())) {
                pending.add(t);
            }
        }
        return pending;
    }

    private void listTrades() {
        List<Trade> pending = getPendingFor(mainUser);
        if (pending.isEmpty()) {
            System.out.println("No pending trades.");
        } else {
            System.out.println("Pending trades:");
            for (Trade t : pending) {
                System.out.println(formatTrade(t));
            }
        }
    }

    private void respondToTrade(String input) {
        String[] parts = input.split(" ");
        boolean accept = input.contains("--accept");
        boolean reject = input.contains("--reject");
        int idIndex = Arrays.asList(parts).indexOf("-i");
        if (idIndex < 0 || idIndex + 1 >= parts.length) {
            System.out.println("Usage: trade response (--accept|--reject) -i <id>");
            return;
        }
        int id = Integer.parseInt(parts[idIndex + 1]);
        Trade target = null;
        for (Trade t : getPendingFor(mainUser)) {
            if (t.getId() == id) { target = t; break; }
        }
        if (target == null) {
            System.out.println("Trade not found or already responded.");
            return;
        }
        User from = target.getFromUser();
        User to = target.getToUser();
        Inventory invFrom = from.getInventory();
        Inventory invTo = to.getInventory();

        if (accept) {
            // جابجایی آیتم‌ها
            for (Item it : target.getOfferedItems()) {
                invFrom.removeItem(it, it.getQuantity());
                invTo.addItem(it, it.getQuantity());
            }
            for (Item it : target.getRequestedItems()) {
                invTo.removeItem(it, it.getQuantity());
                invFrom.addItem(it, it.getQuantity());
            }
            // جابجایی پول
            int moneyOff = target.getOfferedMoney();
            int moneyReq = target.getRequestedMoney();
            from.setMoney(from.getMoney() - moneyOff + moneyReq);
            to.setMoney(to.getMoney() - moneyReq + moneyOff);

            mainUser.increaseFriendshipXpsWithUsers(from, 50);
            from.increaseFriendshipXpsWithUsers(mainUser, 50);
            System.out.println("Trade #" + id + " accepted.");
        } else if (reject) {
            target.rejectTrade();
            mainUser.increaseFriendshipXpsWithUsers(from, -30);
            from.increaseFriendshipXpsWithUsers(mainUser, -30);
            System.out.println("Trade #" + id + " rejected.");
        } else {
            System.out.println("Specify --accept or --reject.");
            return;
        }
        respondedTrades.get(mainUser).add(id);
    }

    private void history() {
        List<Trade> all = userTrades.get(mainUser);
        System.out.println("Trade history:");
        for (Trade t : all) {
            boolean isParticipant = t.getFromUser().equals(mainUser) || t.getToUser().equals(mainUser);
            boolean responded = respondedTrades.get(mainUser).contains(t.getId());
            if (isParticipant && responded) {
                String status = t.isAccepted() ? "ACCEPTED" : "REJECTED";
                System.out.println("#" + t.getId() + " " + status + " - " + formatTrade(t));
            }
        }
    }

    private String formatTrade(Trade t) {
        return String.format("#%d from:%s to:%s offers:%s moneyOffered:%d requests:%s moneyRequested:%d at %s",
                t.getId(), t.getFromUser().getUsername(), t.getToUser().getUsername(),
                t.getOfferedItems(), t.getOfferedMoney(), t.getRequestedItems(), t.getRequestedMoney(), t.getTimestamp());
    }
}
