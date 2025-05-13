package models;

import repository.UserRepository;

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
    private List<Skill> skills;
    private Inventory inventory = new Inventory();
    private List<Game> games = new ArrayList<>();
    private List<String> craftInstructions;
    private List<String> cookRecipes;
    private Map<User, Integer> friendshipXpsWithoutUsers;
    private Map<Npc, Integer> friendshipXpsWithNPCs;
    private HashMap<User,String> unreadMessages;
    private HashMap<User,StringBuilder> allMessages;
    private List<Item> refrigeratorItems;
    private int money;
    private List<Question> securityQuestions;
    private String securityAnswer;
    private List<Quest> activeQuests;
    private int selectedMapId;
    private List<Tools> tools;
    private Item equippedTool;
    private List<Item> backpackItems;

    private Game currentGame;
    private Farm farm;


    // Registration part

    public User(String username, String password, String nickname, String email, String gender) throws Exception {
        this.username = username;
        this.plainPassword = password;
        this.hashPassword = hashedPassword(password);
        this.nickname = nickname;
        this.email = email;
        this.gender = gender;
        this.securityQuestions = new ArrayList<>();
        this.friendshipXpsWithoutUsers = new HashMap<>();
        this.unreadMessages = new HashMap<>();
        this.allMessages = new HashMap<>();

        // Create friendship entries with existing users
        for (User existingUser : UserRepository.getInstance().getAllUsers()) {
            // Add friendship entry to new user's map
            this.friendshipXpsWithoutUsers.put(existingUser, 0);
            // Add friendship entry to existing user's map
            existingUser.getFriendshipXpsWithoutUsers().put(this, 0);
        }
    }
    public String getPlainPassword() {
        return plainPassword;
    }
    public Map<User, Integer> getFriendshipXpsWithoutUsers() {
        return friendshipXpsWithoutUsers;
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

    public void faint() {

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
    }

    public void updateSkill(Skill skill, int exp) {
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
        if (!verifyPassword(newPassword)) {
            this.hashPassword = hashedPassword(newPassword);
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

    public void setFriendshipXpsWithoutUsers(User user, Integer xp) {
        friendshipXpsWithoutUsers.put(user, xp);
    }

    public Integer getFriendshipXpsWithThisUser(User user) {
        return friendshipXpsWithoutUsers.get(user);
    }

    public void setcraftInstructions(List<String> craftInstructions) {
        this.craftInstructions = craftInstructions;
    }

    public List<String> getCookRecipes() {
        return cookRecipes;
    }

    public void setCookRecipes(List<String> cookRecipes) {
        this.cookRecipes = cookRecipes;
    }

    public int getFriendshipLevelsWithUsers(User user) {
        return friendshipXpsWithoutUsers.get(user) / 100;
    }
    public void increaseFriendshipXpsWithUsers(User user, Integer xp) {
        friendshipXpsWithoutUsers.put(user, friendshipXpsWithoutUsers.get(user) + xp);
    }

    public void increaseFriendshipXpsWithNpc (Npc npc, Integer xp) {
        friendshipXpsWithNPCs.put(npc, friendshipXpsWithNPCs.get(npc) + xp);
    }

    public int getFriendshipLevelWithNpc (Npc npc) {
        return friendshipXpsWithNPCs.get(npc) / 200;
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
        for (User user : friendshipXpsWithoutUsers.keySet()) {
            sb.append(user.getNickname()).append(": ").append(friendshipXpsWithoutUsers.get(user)).append("\n");
        }
        return sb.toString();
    }

    public Result talk(User user, String message) {
        Result result = new Result();
        if (Math.abs(position.getPositionX() - user.getPosition().getPositionX()) > 1
                || Math.abs(position.getPositionY() - user.getPosition().getPositionY()) > 1) {
            result.setSuccess(false);
            result.setMessage("You are far away!");
            return result;
        }
        result.setSuccess(true);
        user.requestToTalk(this, message);
        friendshipXpsWithoutUsers.put(user, friendshipXpsWithoutUsers.get(user) + 20);
        user.increaseFriendshipXpsWithUsers(this, 20);
        return result;
    }

    public void requestToTalk(User user, String message) {
        unreadMessages.put(user,message);
        StringBuilder sb = new StringBuilder();
        sb.append(allMessages.get(user).toString());
        sb.append("\n");
        sb.append(message);
    }

    public String getUnreadMessage() {
        StringBuilder sb = new StringBuilder();
        for (User user : unreadMessages.keySet()) {
            sb.append(user.getNickname()).append(": ").append(unreadMessages.get(user)).append("\n");
        }
        return sb.toString();
    }
    public Item getEquippedTool() {
        return equippedTool;
    }

    public void setEquippedTool(Item tool) {
        this.equippedTool = tool;
    }

    public List<Item> getBackpackItems() {
        return backpackItems;
    }

    public void setBackpackItems(List<Item> backpackItems) {
        this.backpackItems = backpackItems;
    }
    public String showTalkHistory(User user) {
        return allMessages.get(user).toString();
    }

    public Game getCurrentGame() {
        return currentGame;
    }

    public void setCurrentGame(Game game) {
        this.currentGame = game;
    }

    public void equipTool(int toolId) {
        Item tool = getBackpackItems().stream()
                .filter(item -> item.getId() == toolId)
                .findFirst()
                .orElse(null);

        if (tool != null) {
            setEquippedTool(tool);
            System.out.println("Equipped: " + tool.getName());
        } else {
            System.out.println("Tool not found in backpack!");
        }
    }

    public Farm getFarm() {
        return farm;
    }

    public void setFarm(Farm farm) {
        this.farm = farm;
    }
}