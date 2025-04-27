package models;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class User {
    private String username;
    private String nickname;
    private String hashPassword;
    //private Question question;
    private String email;
    private String gender;
    private boolean stayLoggedIn;
    private Energy energy;
    private List<Skill> skills;
    private List<Inventory> inventories;  // private Inventory inventory;
    private List<Game> games;
    private List<String> craftInstructions;
    private List<String> cookRecipes;
    private Map<User, Integer> friendshipLevelsWithUsers;
    private Map<Npc, Integer> friendshipLevelsWithNPCs;
    private List<Item> refrigeratorItems;
    private int highestMoney;
    private List<Question> securityQuestions;
    private List<Quest> activeQuests;
    private int selectedMapId;
    private List<Tools> tools;

    public User(String username, String password, String nickname, String email, String gender) throws Exception {
        this.username = username;
        this.hashPassword = hashedPassword(password);
        this.nickname = nickname;
        this.email = email;
        this.gender = gender;
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

    private String hashedPassword(String password) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] hash = md.digest(password.getBytes());
        StringBuilder hexString = new StringBuilder();
        for (byte b : hash) {
            hexString.append(String.format("%02x", b));
        }
        return hexString.toString();
    }

    public void addItem(Item item) { }
    public void updateSkill(Skill skill, int exp) {  }

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

    public void setHashPassword(String newPassword)  throws Exception {
        if (!verifyPassword(newPassword)){
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

    public List<Inventory> getInventories() {
        return inventories;
    }

    public void setInventories(List<Inventory> inventories) {
        this.inventories = inventories;
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

    public List<String> getCookRecipes() {
        return cookRecipes;
    }

    public void setCookRecipes(List<String> cookRecipes) {
        this.cookRecipes = cookRecipes;
    }

    public Map<User, Integer> getFriendshipLevelsWithUsers() {
        return friendshipLevelsWithUsers;
    }

    public void setFriendshipLevelsWithUsers(Map<User, Integer> friendshipLevelsWithUsers) {
        this.friendshipLevelsWithUsers = friendshipLevelsWithUsers;
    }

    public Map<Npc, Integer> getFriendshipLevelsWithNPCs() {
        return friendshipLevelsWithNPCs;
    }

    public void setFriendshipLevelsWithNPCs(Map<Npc, Integer> friendshipLevelsWithNPCs) {
        this.friendshipLevelsWithNPCs = friendshipLevelsWithNPCs;
    }

    public List<Item> getRefrigeratorItems() {
        return refrigeratorItems;
    }

    public void setRefrigeratorItems(List<Item> refrigeratorItems) {
        this.refrigeratorItems = refrigeratorItems;
    }

    public int getHighestMoney() {
        return highestMoney;
    }

    public void setHighestMoney(int highestMoney) {
        this.highestMoney = highestMoney;
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
}
