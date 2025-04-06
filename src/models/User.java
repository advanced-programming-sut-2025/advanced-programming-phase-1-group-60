package models;

import java.util.List;
import java.util.Map;

public class User {
    private String username;
    private String nickname;
    private String HashPassword;
    private Question question;
    private String email;
    private String gender;
    private boolean stayLoggedIn;
    private int energy;
    private int maxEnergy;
    private List<Skill> skills;
    private List<Inventory> inventories;
    private List<Game> games;
    private List<String> craftRecipes;
    private List<String> cookRecipes;
    private Map<User, Integer> friendShipLevelToUsers;
    private Map<NPC, Integer> friendshipLevelToNPCs;
    private List<Item> refrigerators;


}
