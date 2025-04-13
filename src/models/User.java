package models;

import java.util.List;
import java.util.Map;

public class User {
    private String username;
    private String nickname;
    private String HashPassword;
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
    // +++
    private int highestMoney;
    private List<Question> securityQuestions;
    private List<Quest> activeQuests;
    private int selectedMapId;
    private List<Tools> tools;

    public boolean verifyPassword(String inputPassword) {
        return false;
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
        return HashPassword;
    }

    public void setHashPassword(String hashPassword) {
        HashPassword = hashPassword;
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
