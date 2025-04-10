package models;

import java.util.List;
import java.util.Map;

public class Npc {
    private String name;
    private String job;
    private String address;
    private int friendshipLevel;
    private List<Item> favoriteItems;
    private List<Item> hatedItems;
    private Map<String, String> dialogues;
    private List<Quest> activeQuests;
    private List<Request> currentRequests;
    private List<Reward> possibleRewards;




    public void increaseFriendship(int amount);


    public void decreaseFriendship(int amount);


    public String getDialogue(String condition);


    public boolean likesGift(Item gift);


    public String giveGift(Item gift);


    public void addQuest(Quest quest);


    public Reward completeQuest(Quest quest);


    public boolean hasActiveRequest();


    public Reward getRandomReward();




//    public static class Quest {
//        private String id;
//        private String description;
//        private Item requiredItem;
//        private int requiredAmount;
//        private int expirationDay;
//    }

//    public static class Request {
//        private String description;
//        private Item requestedItem;
//        private int requestedAmount;
//    }

//    public static class Reward {
//        private Item item;
//        private int money;
//        private int friendshipPoints;
//    }
}