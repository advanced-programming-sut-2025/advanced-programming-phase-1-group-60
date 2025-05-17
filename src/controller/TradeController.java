package controller;

import models.Inventory;
import models.Item;
import models.TimeSystem;
import models.Trade;
import models.User;
import repository.UserRepository;

import java.util.*;

public class TradeController {
    // Singleton instances per user
    private static Map<User, TradeController> instances = new HashMap<>();

    // Shared data across all controllers
    private static Map<User, List<Trade>> userTrades = new HashMap<>();
    private static Map<User, Set<Integer>> shownTrades = new HashMap<>();
    private static Map<User, Set<Integer>> respondedTrades = new HashMap<>();
    private static int tradeIdCounter = 1;

    public static TradeController getInstance(Scanner sc, User user) {
        return instances.computeIfAbsent(user, u -> new TradeController(sc, u));
    }

    private Scanner sc;
    private User mainUser;

    private TradeController(Scanner sc, User user) {
        this.sc = sc;
        this.mainUser = user;
        // Initialize shared maps once
        if (userTrades.isEmpty()) {
            for (User u : UserRepository.getInstance().getAllUsers()) {
                userTrades.put(u, new ArrayList<>());
                shownTrades.put(u, new HashSet<>());
                respondedTrades.put(u, new HashSet<>());
            }
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
        trade.setType(type);

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
        if (isOffer) trade.setOfferedItems(mainItem);
        else trade.setRequestedItems(mainItem);

        boolean hasMoney = params.containsKey("-p");
        boolean hasOtherItem = params.containsKey("-ti") && params.containsKey("-ta");
        if (hasMoney && hasOtherItem) {
            System.out.println("You can't have both money and another item in the same trade.");
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
            try {
                tamount = Integer.parseInt(params.get("-ta"));
            } catch (NumberFormatException e) {
                System.out.println("Invalid target amount.");
                return null;
            }
            Item targetItem = new Item();
            targetItem.setName(tname);
            targetItem.setQuantity(tamount);
            if (isOffer) trade.setRequestedItems(targetItem);
            else trade.setOfferedItems(targetItem);
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

        int id;
        try {
            id = Integer.parseInt(parts[idIndex + 1]);
        } catch (NumberFormatException e) {
            System.out.println("Invalid trade ID.");
            return;
        }

        Trade target = null;
        for (Trade t : getPendingFor(mainUser)) {
            if (t.getId() == id) {
                target = t;
                break;
            }
        }

        if (target == null) {
            System.out.println("Trade not found or already responded.");
            return;
        }

        if (accept) {
            User from = target.getFromUser();
            User to = target.getToUser();
            Inventory invFrom = from.getInventory();
            Inventory invTo = to.getInventory();

            // بررسی موجودیت آیتم‌های پیشنهادی از فرستنده
            Item offeredItem = target.getOfferedItem();
            if (offeredItem != null) {
                if (!invFrom.hasItem(offeredItem.getName(), offeredItem.getQuantity())) {
                    System.out.println(from.getUsername() + " does not have enough " + offeredItem.getName());
                    return;
                }
            }

            // بررسی موجودیت آیتم‌های درخواستی از دریافت کننده
            Item requestedItem = target.getRequestedItems();
            if (requestedItem != null) {
                if (!invTo.hasItem(requestedItem.getName(), requestedItem.getQuantity())) {
                    System.out.println(to.getUsername() + " does not have enough " + requestedItem.getName());
                    return;
                }
            }

            // بررسی موجودی پول
            int offeredMoney = target.getOfferedMoney();
            int requestedMoney = target.getRequestedMoney();

            if (from.getMoney() < offeredMoney) {
                System.out.println(from.getUsername() + " does not have enough money.");
                return;
            }

            if (to.getMoney() < requestedMoney) {
                System.out.println(to.getUsername() + " does not have enough money.");
                return;
            }

            // انتقال آیتم‌ها
            if (offeredItem != null) {
                invFrom.removeItemByName(offeredItem.getName(), offeredItem.getQuantity());
                invTo.addItemByName(offeredItem.getName(), offeredItem.getQuantity());
            }

            if (requestedItem != null) {
                invTo.removeItemByName(requestedItem.getName(), requestedItem.getQuantity());
                invFrom.addItemByName(requestedItem.getName(), requestedItem.getQuantity());
            }

            // انتقال پول
            from.setMoney(from.getMoney() - offeredMoney + requestedMoney);
            to.setMoney(to.getMoney() - requestedMoney + offeredMoney);

            // افزایش تجربه دوستی
            mainUser.increaseFriendshipXpsWithUsers(from, 50);
            from.increaseFriendshipXpsWithUsers(mainUser, 50);
            System.out.println("Trade #" + id + " accepted.");
            target.setAccepted(true);
        } else if (reject) {
            target.rejectTrade();
            User from = target.getFromUser();
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
        String offered = "";
        if (t.getOfferedItem() != null) {
            offered = t.getOfferedItem().toString();
        }

        String requested = "";
        if (t.getRequestedItems() != null) {
            requested = t.getRequestedItems().toString();
        }

        return String.format(
                "#%d from:%s to:%s offers:%s moneyOffered:%d requests:%s moneyRequested:%d at %s",
                t.getId(),
                t.getFromUser().getUsername(),
                t.getToUser().getUsername(),
                offered,
                t.getOfferedMoney(),
                requested,
                t.getRequestedMoney(),
                t.getTimestamp()
        );
    }
}
