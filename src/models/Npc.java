package models;

import java.util.HashMap;
import java.util.List;

public class Npc {
    private String name;
    private String job;
    private String address;
    private int friendshipLevel;
    private List<Item> favoriteItems;
    private List<Item> hatedItems;
    private HashMap<String, Dialog> dialogues;
    private List<Quest> quests;
    private TimeSystem timeSystem; // to choose dialogs


    public void increaseFriendship(int amount) {};


    public void decreaseFriendship(int amount) {};


    public String getDialogue(String condition) {
        return null;
    };


    public boolean likesGift(Item gift) {
        return false;
    };


    public String giveGift(Item gift) {
        return null;
    };


    public void addQuest(Quest quest) {};


    public boolean hasActiveRequest() {
        return false;
    };

    public Result meet (){
        return null;
    }

    private class Dialog {
        String dialogue;
        int levelRequired;
        TimeSystem timeSystem;
        String weather;
    }
}