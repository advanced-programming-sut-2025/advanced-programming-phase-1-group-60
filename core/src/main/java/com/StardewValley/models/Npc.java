package com.StardewValley.models;

import com.StardewValley.repository.UserRepository;

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
    private Map<String, String> dialogs = new HashMap<>(); // تغییر به نقشه ساده
    private Set<String> spokenToday = new HashSet<>();
    private long lastTalkTimestamp = -1; // زمان آخرین مکالمه برای اعمال cooldown

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


    public void defineDialog(String season, String prompt, List<String> replies, Map<String, String> answers) {
        // برای سادگی، فقط جمله اول را ذخیره می‌کنیم
        this.dialogs.put(season, prompt);
    }

    /**
     * بررسی می‌کند آیا NPC برای صحبت در دسترس است یا خیر (بر اساس cooldown یک ساعته)
     */
    public boolean isDialogueReady() {
        if (lastTalkTimestamp == -1) {
            return true;
        }
        long currentTime = TimeSystem.getInstance().getTotalHoursSinceStart();
        return (currentTime - lastTalkTimestamp) >= 1;
    }

    /**
     * زمان آخرین مکالمه را ثبت می‌کند
     */
    public void recordTalkTime() {
        this.lastTalkTimestamp = TimeSystem.getInstance().getTotalHoursSinceStart();
    }


    public String startConversation(User user) {
        String season = TimeSystem.getInstance().getCurrentSeason();
        String date = TimeSystem.getInstance().getCurrentDate();
        String prompt = dialogs.get(season);
        if (prompt == null) {
            return "…";
        }
        String key = user.getUsername() + "#" + name + "#" + date;
        if (!spokenToday.contains(key)) {
            user.increaseFriendshipXpsWithNpc(this, 20);
            spokenToday.add(key);
        }
        return prompt;
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
