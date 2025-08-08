package com.StardewValley.models;

import com.StardewValley.Network.Client.ClientMain;
import com.StardewValley.Network.Message;
import com.StardewValley.repository.NpcRepository;
import com.StardewValley.repository.UserRepository;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class User {
    private String username;
    private String nickname;
    private String hashPassword;
    private String plainPassword;
    //private Question question;
    private String email;
    private String gender;
    private boolean stayLoggedIn;
    private Tile position;
    private Energy energy = new Energy();
    private List<Skill> skills = new ArrayList<>();
    private Inventory inventory = new Inventory(Inventory.InventoryType.NORMAL, this);
    private List<Game> games = new ArrayList<>();
    private List<String> craftInstructions;
    private Map<User, Integer> friendshipLevelWithUsers;
    private Map<User, Integer> friendshipXpsWithUsers;
    private Map<Npc, Integer> friendshipXpsWithNPCs;
    private HashMap<User,String> unreadMessages;
    private List<Item> refrigeratorItems = new ArrayList<>();
    private int money = 3000000;
    private List<Question> securityQuestions;
    private String securityAnswer;
    private List<Quest> activeQuests;
    private List<Quest> completedQuests = new ArrayList<>(); // Proper field to store completed quests
    private int selectedMapId;
    private List<Tools> tools;
    private Item equippedTool;

    private Game currentGame;
    private Farm farm;
    public boolean isInVillage = false;
    private List<Item> animalPlaces = new ArrayList<>();
    private List<Item> placedAnimalPlaces = new ArrayList<>();


    // Registration part

    public User(String username, String password, String nickname, String email, String gender) throws Exception {
        this.username = username;
        this.plainPassword = password;
        this.hashPassword = hashedPassword(password);
        this.nickname = nickname;
        this.email = email;
        this.gender = gender;
        this.securityQuestions = new ArrayList<>();
        this.friendshipXpsWithUsers = new HashMap<>();
        this.unreadMessages = new HashMap<>();
        this.friendshipXpsWithNPCs = new HashMap<>();
        this.friendshipXpsWithUsers = new HashMap<>();
        this.friendshipLevelWithUsers = new HashMap<>();
        this.skills = new ArrayList<>();
        skills.add(new Skill("Farming"));
        skills.add(new Skill("Mining"));
        skills.add(new Skill("Foraging"));
        skills.add(new Skill("Fishing"));

        for (User existingUser : UserRepository.getInstance().getAllUsers()) {
            this.friendshipLevelWithUsers.put(existingUser, 0);
            existingUser.friendshipLevelWithUsers.put(this, 0);
        }


        for (User existingUser : UserRepository.getInstance().getAllUsers()) {
            this.friendshipXpsWithUsers.put(existingUser, 0);
            existingUser.getFriendshipXpsWithUsers().put(this, 0);
        }

        for (Npc existingNpc : NpcRepository.getInstance().getAllNpcs()) {
            this.friendshipXpsWithNPCs.put(existingNpc, 0);
        }
    }
    public String getPlainPassword() {
        return plainPassword;
    }
    public Map<User, Integer> getFriendshipXpsWithUsers() {
        return friendshipXpsWithUsers;
    }
    public static boolean verifyEmail(String email) {
        String regex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
        return Pattern.matches(regex, email);
    }

    public static boolean verifyPassword(String inputPassword) {
        if (inputPassword.length() < 8) return false;
        boolean hasUpper = false, hasLower = false, hasDigit = false, hasSpecial = false;
        for (char c : inputPassword.toCharArray()) {
            if (Character.isUpperCase(c)) hasUpper = true;
            else if (Character.isLowerCase(c)) hasLower = true;
            else if (Character.isDigit(c)) hasDigit = true;
            else hasSpecial = true;
        }
        return hasUpper && hasLower && hasDigit && hasSpecial;
    }
    public static boolean verifyName(String inputName) {
        String regex = "^[a-zA-Z0-9_]+$";
        if(inputName == null || !(Pattern.matches(regex, inputName))){
            return false;
        }
        return true;
    }

    public static String hashedPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(password.getBytes());
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }

    // walk , energy and faint

    public void setUnlimitedEnergy(boolean u) {
        energy.setUnlimited(u);
    }

    public void consumeEnergy(int v) {
        if (!energy.isUnlimited()) energy.decreaseEnergy(v);
    }

    public void setPosition(Tile t) {
        position = t;
    }

    public Tile getPosition() {
        return position;
    }

    int startFaintDay;

    public void faint() {
        startFaintDay = TimeSystem.getInstance().getCurrentDay();
    }

    public boolean isFaint () {
        return startFaintDay == TimeSystem.getInstance().getCurrentDay();
    }

    public void faintAlong(Path path) {
        int rem = energy.getCurrentEnergy();
        for (var s : path.getSteps()) {
            if (rem <= 0) break;
            rem--;
            position = s;
        }
        energy.setCurrentEnergy(0);
        faint();
    }

    public void addItem(Item item) {
        this.inventory.addItem(item);
    }

    public void updateSkill(Skill skill, int exp) {
        // TODO: منطق به‌روزرسانی تجربه مهارت را اینجا پیاده‌سازی کنید.
        // این متد باید تجربه skill را افزایش دهد و در صورت نیاز سطح را ارتقا دهد.
        if (skill != null) {
            skill.gainExperience(exp);
        }
        // Moved sendPlayerDataUpdateToServer() to the end of actions that change data
        sendPlayerDataUpdateToServer();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getHashPassword() {
        return hashPassword;
    }

    public void setHashPassword(String newPassword) throws Exception {
        if (verifyPassword(newPassword)) {
            this.hashPassword = hashedPassword(newPassword);
            this.plainPassword = newPassword; // Also update plainPassword
        } else {
            throw new Exception("Password is too weak. Must contain uppercase, lowercase, number, and special character.");
        }
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public boolean isStayLoggedIn() {
        return stayLoggedIn;
    }

    public void setStayLoggedIn(boolean stayLoggedIn) {
        this.stayLoggedIn = stayLoggedIn;
    }

    public Energy getEnergy() {
        return energy;
    }

    public void setEnergy(Energy energy) {
        this.energy = energy;
    }


    public List<Skill> getSkills() {
        return skills;
    }

    public void setSkills(List<Skill> skills) {
        this.skills = skills;
    }

    public Inventory getInventory() {
        return inventory;
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
    }

    public List<Game> getGames() {
        return games;
    }

    public void setGames(List<Game> games) {
        this.games = games;
    }

    public List<String> getcraftInstructions() {
        return craftInstructions;
    }


    public void setcraftInstructions(List<String> craftInstructions) {
        this.craftInstructions = craftInstructions;
    }

    public int getFriendshipXpsWithUsers(User user) {
        return friendshipXpsWithUsers.get(user);
    }

    public void increaseFriendshipXpsWithUsers(User user, Integer xp) {
        friendshipXpsWithUsers.put(user, friendshipXpsWithUsers.getOrDefault(user, 0) + xp);
    }

    public void increaseFriendshipXpsWithNpc (Npc npc, Integer xp) {
        friendshipXpsWithNPCs.put(npc, friendshipXpsWithNPCs.get(npc) + xp);
    }

    public int getFriendshipLevelWithNpc (Npc npc) {
        return friendshipXpsWithNPCs.get(npc) / 200;
    }

    public Map<Npc, Integer> getFriendshipXpsWithNPCs() {
        return friendshipXpsWithNPCs;
    }

    public void setFriendshipLevelWithUsers (User user, int level) {
        friendshipLevelWithUsers.put(user, level);
    }

    public int getFriendshipLevelWithUsers (User user) {
        //   if (friendshipLevelWithUsers.containsKey(user)) return friendshipLevelWithUsers.get(user);
        int xp = friendshipXpsWithUsers.getOrDefault(user, 0);
        if (xp < 100) return 0;
        if (xp > 100 && xp < 300) return 1;
        else return 2;
    }

    public List<Item> getRefrigeratorItems() {
        return refrigeratorItems;
    }

    public void setRefrigeratorItems(List<Item> refrigeratorItems) {
        this.refrigeratorItems = refrigeratorItems;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
        sendPlayerDataUpdateToServer(); // Call update after change
    }
    public void setSecurityQuestion(String question, String answer) {
        if (this.securityQuestions == null) {
            this.securityQuestions = new ArrayList<>();
        }
        this.securityQuestions.add(new Question(question, answer));
    }
    public String getSecurityQuestionInfo() {
        if (securityQuestions == null || securityQuestions.isEmpty()) {
            return "No security question set";
        }
        Question question = securityQuestions.get(0);
        return "Security Question: " + question.getQuestion();
    }
    public boolean verifySecurityQuestion(String answer) {
        if (securityQuestions == null || securityQuestions.isEmpty()) {
            return false;
        }
        // We only have one security question per user
        return securityQuestions.get(0).checkAnswer(answer);
    }
    public List<Question> getSecurityQuestions() {
        return securityQuestions;
    }

    public void setSecurityQuestions(List<Question> securityQuestions) {
        this.securityQuestions = securityQuestions;
    }

    public List<Quest> getActiveQuests() {
        return activeQuests;
    }

    public void setActiveQuests(List<Quest> activeQuests) {
        this.activeQuests = activeQuests;
    }

    // This method should add a completed quest to the list
    public void addCompletedQuest(Quest quest) {
        if (quest != null && !completedQuests.contains(quest)) {
            completedQuests.add(quest);
            sendPlayerDataUpdateToServer(); // Call update after change
        }
    }

    // Get count of completed quests from the 'completedQuests' list
    public int getCompletedQuestsCount() {
        return completedQuests.size();
    }

    // This method should be called when average skill level changes (after skill update)
    public float getAverageSkillLevel() {
        if (skills == null || skills.isEmpty()) {
            return 0.0f;
        }
        float totalLevel = 0;
        for (Skill skill : skills) {
            totalLevel += skill.getLevel();
        }
        return totalLevel / skills.size();
    }


    public void selectMap(int mapId) {
        this.selectedMapId = mapId;
    }


    public int getSelectedMapId() {
        return selectedMapId;
    }

    public List<Tools> getTools() {
        return tools;
    }

    public String showFriendshipLevelsWithUsers() {
        StringBuilder sb = new StringBuilder();
        for (User user : friendshipXpsWithUsers.keySet()) {
            sb.append(user.getNickname()).append(": ").append(friendshipXpsWithUsers.get(user)).append("\n");
        }
        return sb.toString();
    }

    public String showFriendshipLevelsWithNpcs() {
        StringBuilder sb = new StringBuilder();
        for (Npc npc : friendshipXpsWithNPCs.keySet()) {
            sb.append(npc.getName()).append(": ").append(friendshipXpsWithNPCs.get(npc)).append("\n");
        }
        return sb.toString();
    }


    // talk
    private static Map<String, StringBuilder> allChatHistories = new HashMap<>();

    public Result talk(User receiver, String message) {
        Result result = new Result();

        if (position == null || receiver.getPosition() == null ||
            Math.abs(position.getPositionX() - receiver.getPosition().getPositionX()) > 1 ||
            Math.abs(position.getPositionY() - receiver.getPosition().getPositionY()) > 1) {
            result.setSuccess(false);
            result.setMessage("You are too far away to talk!");
            return result;
        }

        receiver.requestToTalk(this, message);

        friendshipXpsWithUsers.put(receiver, friendshipXpsWithUsers.getOrDefault(receiver, 0) + 20);
        receiver.friendshipXpsWithUsers.put(this, receiver.friendshipXpsWithUsers.getOrDefault(this, 0) + 20);

        result.setSuccess(true);
        result.setMessage("Message delivered successfully");
        return result;
    }

    public static void resetChatHistories() {
        allChatHistories.clear();
    }

    private static String getChatHistoryKey(User user1, User user2) {
        // نام‌های کاربری را مقایسه می‌کند تا ترتیب آن‌ها در کلید همیشه یکسان باشد
        if (user1.getUsername().compareTo(user2.getUsername()) > 0) {
            return user2.getUsername() + "::" + user1.getUsername();
        }
        // کد اصلاح‌شده: از user2.getUsername() استفاده می‌کند
        return user1.getUsername() + "::" + user2.getUsername();
    }

    public void requestToTalk(User sender, String message) {
        this.unreadMessages.put(sender, message); // `this` همان گیرنده پیام است

        String key = getChatHistoryKey(this, sender);
        // تاریخچه را از منبع مرکزی دریافت کرده یا در صورت عدم وجود، ایجاد می‌کند
        StringBuilder history = allChatHistories.computeIfAbsent(key, k -> new StringBuilder());

        if (history.length() > 0) {
            history.append("\n");
        }
        history.append(sender.getNickname()).append(": ").append(message);
    }


    public StringBuilder getAllMessages(User target) {
        String key = getChatHistoryKey(this, target);
        // همیشه تاریخچه را از منبع معتبر (نقشه استاتیک) باز می‌گرداند
        return allChatHistories.getOrDefault(key, new StringBuilder());
    }

    public String getUnreadMessage() {
        if (unreadMessages.isEmpty()) return "";

        StringBuilder sb = new StringBuilder();
        for (Map.Entry<User, String> entry : new HashMap<>(unreadMessages).entrySet()) {
            if (entry.getValue() != null && !entry.getValue().isEmpty()) {
                sb.append(entry.getKey().getNickname())
                    .append(": ")
                    .append(entry.getValue())
                    .append("\n");
            }
        }
        unreadMessages.clear();
        return sb.toString();
    }


    public Item getEquippedTool() {
        return equippedTool;
    }

    public void setEquippedTool(Item tool) {
        this.equippedTool = tool;
    }

    public Game getCurrentGame() {
        return currentGame;
    }

    public void setCurrentGame(Game game) {
        this.currentGame = game;
    }


    public Farm getFarm() {
        return farm;
    }

    public void setFarm(Farm farm) {
        this.farm = farm;
    }

    public void addAnimalPlace (Item animalPlace) {
        animalPlaces.add(animalPlace);
    }

    public void removeAnimalPlace (Item animalPlace) {
        animalPlaces.remove(animalPlace);
    }

    public List<Item> getAnimalPlaces() {
        return animalPlaces;
    }

    // marriage part :
    private User spouse;

    public void setSpouse(User spouse) { this.spouse = spouse; }
    public User getSpouse() { return spouse; }
    private Map<User, MarriageRequest> marriageRequests = new HashMap<>();
    public void addMarriageRequest(User sender, Item ring) {
        marriageRequests.put(sender, new MarriageRequest(ring));
    }

    public Map<User, MarriageRequest> getMarriageRequests() {
        return marriageRequests;
    }

    public Skill getSkill(String name) {
        for (Skill skill : skills) {
            if (skill.getName().equalsIgnoreCase(name)) {
                return skill;
            }
        }
        return null;
    }

    public static class MarriageRequest {
        private Item ring;
        private boolean responded;

        public MarriageRequest(Item ring) {
            this.ring = ring;
        }

        public boolean isResponded() { return responded; }
        public void setResponded(boolean responded) { this.responded = responded; }
        public Item getRing() { return ring; }
    }

    private String penaltyStartSeason;
    private int penaltyStartDay;
    private int penaltyStartYear;
    private boolean hasEnergyPenalty;

    public void applyEnergyPenalty() {
        TimeSystem time = TimeSystem.getInstance();
        this.penaltyStartSeason = time.getCurrentSeason();
        this.penaltyStartDay = time.getCurrentDay();
        this.penaltyStartYear = time.getCurrentYear();
        this.hasEnergyPenalty = true;
    }

    public boolean isEnergyPenaltyActive() {
        if (!hasEnergyPenalty) return false;

        TimeSystem time = TimeSystem.getInstance();
        int currentDay = time.getCurrentDay();
        String currentSeason = time.getCurrentSeason();
        int currentYear = time.getCurrentYear();

        int startTotal = calculateTotalDays(penaltyStartSeason, penaltyStartDay, penaltyStartYear);
        int currentTotal = calculateTotalDays(currentSeason, currentDay, currentYear);

        if (currentTotal - startTotal >= 7) {
            hasEnergyPenalty = false;
            return false;
        }
        return true;
    }

    private int calculateTotalDays(String season, int day, int year) {
        int seasonIndex = seasonToIndex(season);
        return (year - 1) * 112 + seasonIndex * 28 + day;
    }

    private int seasonToIndex(String season) {
        return switch (season) {
            case "Spring" -> 0;
            case "Summer" -> 1;
            case "Fall" -> 2;
            case "Winter" -> 3;
            default -> throw new IllegalArgumentException("Invalid season");
        };
    }

    public String getUnreadMarriageRequests() {
        StringBuilder sb = new StringBuilder();
        for (User requester : marriageRequests.keySet()) {
            if (!marriageRequests.get(requester).isResponded()) {
                sb.append(requester.getNickname())
                    .append(" wants to marry you (Ring: ")
                    .append(marriageRequests.get(requester).getRing().getName())
                    .append(")\n");
            }
        }
        return sb.toString();
    }

    // animals
    private List<Animal> animals = new ArrayList<>();

    public void addAnimal(Animal animal) {
        animals.add(animal);
    }

    public List<Animal> getAnimals() {
        return animals;
    }

    private List<Animal> putAnimals = new ArrayList<>();

    public List<Animal> getPutAnimals() {
        return putAnimals;
    }

    public void addPutAnimal(Animal putAnimal) {
        putAnimals.add(putAnimal);
    }

    public List<Item> getPlacedAnimalPlaces() {
        return placedAnimalPlaces;
    }

    public void addPlacedAnimalPlace(Item item) {
        placedAnimalPlaces.add(item);
    }

    public void placeAnimal(Animal animal) {
        if (animals.contains(animal)) {
            animals.remove(animal);
            putAnimals.add(animal);
        }
    }

    public void bringAnimalBack(Animal animal) {
        if (putAnimals.contains(animal)) {
            putAnimals.remove(animal);
            animals.add(animal);
            animal.bringInside(); // وضعیتش را به "داخل" تغییر می‌دهیم
        }
    }

    public void sellAnimal(Animal animal) {
        animals.remove(animal);
        putAnimals.remove(animal);


        for (Item building : placedAnimalPlaces) {
            if (building instanceof Coop) {
                ((Coop) building).removeAnimal(animal);
            } else if (building instanceof Barn) {
                ((Barn) building).removeAnimal(animal);
            }
        }
    }


    // SKILLS
    int fishingSkillsXp;
    public int getFishingSkills() { return fishingSkillsXp / 50; }
    public void increaseFishingSkills(int amount) { fishingSkillsXp += amount;
        sendPlayerDataUpdateToServer();}


    // Cook

    public Item getRefrigeratorItem(String itemName) {
        for (Item item : refrigeratorItems) {
            if (item.getName().equalsIgnoreCase(itemName)) {
                return item;
            }
        }
        return null;
    }

    public void addItemToRefrigerator(Item item) {
        Item existing = getRefrigeratorItem(item.getName());
        if (existing != null) {
            existing.setQuantity(existing.getQuantity() + item.getQuantity());
        } else {
            Item newItem = new Item();
            newItem.setName(item.getName());
            newItem.setType(item.getType());
            newItem.setQuantity(item.getQuantity());
            newItem.setPath(item.getPath());
            refrigeratorItems.add(newItem);
        }
    }

    public void removeItemFromRefrigerator(String itemName, int quantity) {
        Item existing = getRefrigeratorItem(itemName);
        if (existing == null) {
            throw new IllegalArgumentException("Item not found in refrigerator: " + itemName);
        }
        if (existing.getQuantity() < quantity) {
            throw new IllegalArgumentException("Not enough " + itemName + " in refrigerator");
        }
        existing.setQuantity(existing.getQuantity() - quantity);
        if (existing.getQuantity() == 0) {
            refrigeratorItems.remove(existing);
        }
    }

    public int getRefrigeratorItemQuantity(String itemName) {
        Item existing = getRefrigeratorItem(itemName);
        if (existing == null) {
            return 0;
        }
        return existing.getQuantity();
    }

    private List<String> cookRecipes = new ArrayList<>();

    public List<String> getCookRecipes() {
        return cookRecipes;
    }

    public void learnRecipe(String recipeName) {
        if (!cookRecipes.contains(recipeName)) {
            cookRecipes.add(recipeName);
        }
    }

    /**
     * Sends the current player's relevant data to the server for synchronization.
     * This should be called whenever the player's money, completed quests, or skill levels change.
     * IMPORTANT: This method retrieves the current values of money, quests, and skills.
     * It should NOT call the getters that trigger another call to this method.
     */
    public void sendPlayerDataUpdateToServer() {
        if (ClientMain.isConnected) { // Only send if connected to the server
            Map<String, Object> payload = new HashMap<>();
            payload.put("username", this.getUsername());
            payload.put("money", this.money); // Access the field directly
            payload.put("completedQuestsCount", this.completedQuests.size()); // Access the field directly or calculate without recursion
            payload.put("averageSkillLevel", calculateAverageSkillLevel()); // Call a non-recursive calculation

            ClientMain.sendMessage(new Message(Message.ActionType.PLAYER_DATA_UPDATE, payload));
            System.out.println("Sent PLAYER_DATA_UPDATE for user " + this.getUsername() + ": Money=" + this.money +
                ", Quests=" + this.completedQuests.size() + ", AvgSkill=" + calculateAverageSkillLevel());
        } else {
            System.out.println("Not connected to server. PLAYER_DATA_UPDATE not sent for " + this.getUsername());
        }
    }

    /**
     * Helper method to calculate average skill level without causing recursion.
     * This should be called by sendPlayerDataUpdateToServer().
     */
    private float calculateAverageSkillLevel() {
        if (skills == null || skills.isEmpty()) {
            return 0.0f;
        }
        float totalLevel = 0;
        for (Skill skill : skills) {
            totalLevel += skill.getLevel();
        }
        return totalLevel / skills.size();
    }
}
