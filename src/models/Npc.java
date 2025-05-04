package models;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.Map;


public class Npc {
    // مشخصات اصلی
    private String name;
    private String personality;
    private List<String> favoriteItems;
    private List<Quest> quests = new ArrayList<>();
    private Map<String, Dialog> dialogs = new HashMap<>();
    private Set<String> spokenToday = new HashSet<>();

    private Tile tile;  // TODO: مقداردهی اولیه‌ی tile در زمان ایجاد یا لود نقشه

    public Npc(String name, String personality, List<String> favoriteItems) {
        this.name = name;
        this.personality = personality;
        this.favoriteItems = favoriteItems;
        //this.tile = tile;
    }


    public String getName() {
        return name;
    }

    public String getPersonality() {
        return personality;
    }
    public List<String> getFavoriteItems() {
        return favoriteItems;
    }
    public Tile getTile() {
        return tile;
    }
    public void setTile(Tile tile) {
        this.tile = tile;
    }

    public void defineDialog(String season,
                             String prompt,
                             List<String> validReplies,
                             Map<String,String> npcAnswers) {
        dialogs.put(season, new Dialog(prompt, validReplies, npcAnswers));
    }

    public String converse(User user, String reply) {
        String season = TimeSystem.getInstance().getCurrentSeason();
        String date = TimeSystem.getInstance().getCurrentDate();
        Dialog d = dialogs.get(season);
        if (d == null) return "…";

        String key = user.getUsername() + "#" + name + "#" + date;
        if (!spokenToday.contains(key)) {
            user.increaseFriendshipXpsWithNpc(this, 20);
            spokenToday.add(key);
        }

        if (d.validReplies.contains(reply)) {
            return d.npcAnswers.get(reply);
        } else {
            return "I don't understand.";
        }
    }

    public void addQuest(Quest quest) {
        quests.add(quest);
    }

    public List<Quest> getQuests() {
        return quests;
    }

    public boolean isFavoriteItem(String itemName) {
        return favoriteItems.contains(itemName);
    }

    private class Dialog {
        String prompt;
        List<String> validReplies;
        Map<String,String> npcAnswers;
        Dialog(String p, List<String> vr, Map<String,String> na) {
            prompt = p; validReplies = vr; npcAnswers = na;
        }
    }
}