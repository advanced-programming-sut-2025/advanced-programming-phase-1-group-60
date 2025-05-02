package controller;

import models.Item;
import models.Trade;
import models.User;
import repository.UserRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class TradeController {
    Scanner sc;
    User mainUser;
    HashMap<User, List<Trade>> userTrades;

    public TradeController(Scanner sc, User user) {
        this.sc = sc;
        this.mainUser = user;
    }

    public void startTrade() {
        String input = sc.nextLine();
        if (input.equals("exit trade")) {
            return;
        }
        if (input.startsWith("trade")) {

            String[] parts = input.split(" ");
            Map<String, String> params = new HashMap<>();
            for (int i = 1; i < parts.length - 1; i++) {
                String key = parts[i];
                if (key.startsWith("-")) {
                    String value = parts[i + 1];
                    if (!value.startsWith("-")) {
                        params.put(key, value);
                        i++;
                    }
                }
            }

            Trade trade = new Trade();
            String type = params.get("-t");
            boolean isOffer = "offer".equalsIgnoreCase(type);

            String itemName = params.get("-i");
            int amount = Integer.parseInt(params.get("-a"));
            Item mainItem = new Item();
            mainItem.setName(itemName);
            mainItem.setQuantity(amount);
            if (isOffer) {
                trade.addOfferedItem(mainItem);
            } else {
                trade.addRequestedItem(mainItem);
            }

            boolean hasMoney = params.containsKey("-p");
            boolean hasOtherItem = params.containsKey("-ti") && params.containsKey("-ta");

            if (hasMoney && hasOtherItem) {
                System.out.println("You can't have donkey and date at the same time.");
                return;
            }

            if (hasMoney) {
                int money = Integer.parseInt(params.get("-p"));
                if (isOffer) {
                    trade.setRequestedMoney(money);
                } else {
                    trade.setOfferedMoney(money);
                }
            }

            if (hasOtherItem) {
                String targetName = params.get("-ti");
                int targetAmount = Integer.parseInt(params.get("-ta"));
                Item target = new Item();
                target.setName(targetName);
                target.setQuantity(targetAmount);
                if (isOffer) {
                    trade.addRequestedItem(target);
                } else {
                    trade.addOfferedItem(target);
                }
            }
            User targetUser = UserRepository.getInstance().getUserByUsername(params.get("-u"));
            userTrades.get(targetUser).add(trade);
            userTrades.get(mainUser).add(trade);
        }
    }


}
