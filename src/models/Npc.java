package models;

import repository.UserRepository;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.Map;


public class Npc implements StaticElement {

    public boolean isPassable () {
        return false;
    };

    public char symbol () {
        return name.charAt(0);
    }
    // مشخصات اصلی
    private String name;
    private String personality;
    private List<String> favoriteItems;
    private List<Quest> quests = new ArrayList<>();
    private Map<String, Dialog> dialogs = new HashMap<>();
    private Set<String> spokenToday = new HashSet<>();

    private final int positionX;
    private final int positionY;

    public Npc(String name, String personality, List<String> favoriteItems, int x, int y) {
        this.name = name;
        this.personality = personality;
        this.favoriteItems = favoriteItems;
        this.positionX = x;
        this.positionY = y;
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


    public void defineDialog(String season,
                             String prompt,
                             List<String> validReplies,
                             Map<String,String> npcAnswers) {
        dialogs.put(season, new Dialog(prompt, validReplies, npcAnswers));
    }

    public String startConversation(User user) {
        String season = TimeSystem.getInstance().getCurrentSeason();
        String date = TimeSystem.getInstance().getCurrentDate();
        Dialog d = dialogs.get(season);
        if (d == null) {
            return "…";
        }
        String key = user.getUsername() + "#" + name + "#" + date;
        if (!spokenToday.contains(key)) {
            user.increaseFriendshipXpsWithNpc(this, 20);
            spokenToday.add(key);
        }
        return d.prompt;
    }

    public String replyConversation(String reply) {
        String season = TimeSystem.getInstance().getCurrentSeason();
        Dialog d = dialogs.get(season);
        if (d == null) {
            return "…";
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

    public int getPositionX() {
        return positionX;
    }

    public int getPositionY() {
        return positionY;
    }

    private class Dialog {
        String prompt;
        List<String> validReplies;
        Map<String,String> npcAnswers;
        Dialog(String p, List<String> vr, Map<String,String> na) {
            prompt = p; validReplies = vr; npcAnswers = na;
        }
    }

    public void gift() {
        Random random = new Random();
        for (User u : UserRepository.getInstance().getAllUsers()) {
            if (u.getFriendshipXpsWithNPCs().get(this) > 400) {
                if (Math.random() < 0.9) {
                    int randomIndex = random.nextInt(favoriteItems.size());
                    Item gift = new Item();
                    gift.setName(favoriteItems.get(randomIndex));
                    gift.setQuantity(1);
                    u.getInventory().addItem(gift);
                }
            }
        }
    }
}