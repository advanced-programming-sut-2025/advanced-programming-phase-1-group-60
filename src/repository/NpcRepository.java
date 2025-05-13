package repository;

import models.Npc;
import java.util.*;

public class NpcRepository {
    private static final NpcRepository INSTANCE = new NpcRepository();
    private Map<String, Npc> npcsByName;

    private NpcRepository() {
        npcsByName = new HashMap<>();
        initializeNpcs();
    }

    public static NpcRepository getInstance() {
        return INSTANCE;
    }

    private void initializeNpcs() {

        npcsByName.put("SEBASTIAN", new Npc(
                "Sebastian", "angry",
                Arrays.asList("Wool", "Pumpkin Pie", "Pizza"),4 , 19
        ));
        setDialogs(npcsByName.get("SEBASTIAN"));
        npcsByName.put("ABIGAIL", new Npc(
                "Abigail", "calm",
                Arrays.asList("Amethyst", "Pumpkin", "Coffee"), 15, 19
        ));
        setDialogs(npcsByName.get("ABIGAIL"));
        npcsByName.put("HARVEY", new Npc(
                "Harvey", "friendly",
                Arrays.asList("Coffee", "Pickles", "Wine"), 0, 17
        ));
        setDialogs(npcsByName.get("HARVEY"));
        npcsByName.put("LEAH", new Npc(
                "Leah", "rude",
                Arrays.asList("Salad", "Grapes", "Wine"), 19, 17
        ));
        setDialogs(npcsByName.get("LEAH"));
        npcsByName.put("ROBIN", new Npc(
                "Robin", "Sissy",
                Arrays.asList("Spaghetti", "Wood", "Iron Bar"), 10, 12
        ));
        setDialogs(npcsByName.get("ROBIN"));
    }

    public Npc getNpcByName(String name) {
        return npcsByName.get(name);
    }

    public Collection<Npc> getAllNpcs() {
        return Collections.unmodifiableCollection(npcsByName.values());
    }

    private void setDialogs (Npc npc) {
        switch (npc.getName()) {
            case "Sebastian" -> {
                npc.defineDialog(
                        "Spring",
                        "Back here again, huh? You want me to smash your jaw? (Yes, I do / No, I don't)\n",
                        List.of("Yes, I do", "No, I don't"),
                        Map.of(
                                "Yes, I do", "Get lost, kid.\n",
                                "No, I don't", "Then hurry the hell up and go.\n"
                        )
                );
                npc.defineDialog(
                        "Summer",
                        "I’m boiling in this heat—don’t bother me anymore.\n",
                        List.of("", ""),
                        Map.of(
                                "", ""
                        )
                );
                npc.defineDialog(
                        "Fall",
                        "Don’t walk on the leaves—the sound pisses me off.\n",
                        List.of("", ""),
                        Map.of(
                                "", ""
                        )
                );
                npc.defineDialog(
                        "Winter",
                        "It’s cold—want some tea? (Yes – No)\n",
                        List.of("Yes", "No"),
                        Map.of(
                                "Yes", "What am I, your damn barista? Go make it yourself.\n",
                                "No", "I wouldn't have brought it anyway.\n"
                        )
                );
            }
            case "Abigail" -> {
                npc.defineDialog(
                        "Spring",
                        "Hello, what a beautiful day! The blossoms, the river, and the cool air are amazing." +
                                "Isn't it?(Yes, it is / No, it is not\n",
                        List.of("Yes, it is", "No, it is not"),
                        Map.of(
                                "Yes, it is", "Nice\n",
                                "No, it is not", "Oh really\n"
                        )
                );
                npc.defineDialog(
                        "Summer",
                        "It’s really hot, but hey—we’ve got an AC.(but your ac is off)\n",
                        List.of("but your ac is off", ""),
                        Map.of(
                                "but your ac is off", "You're right, but we still have it.\n",
                                "", ""
                        )
                );
                npc.defineDialog(
                        "Fall",
                        "Ah, what a poetic day—do you know any poems to recite?(Yes, I do / No, I don't)\n",
                        List.of("Yes, I do", "No, I don't"),
                        Map.of(
                                "Yes, I do", "Alright then, don’t recite anything.\n",
                                "No, I don't", "Oh, what a shame.\n"
                        )
                );
                npc.defineDialog(
                        "Winter",
                        "It’s cold—let me bring you some borage tea.\n",
                        List.of("", ""),
                        Map.of(
                                "", ""
                        )
                );
            }
            case "Harvey" -> {
                npc.defineDialog(
                        "Spring",
                        "How’s it going, buddy? How are you today?(I'm good / Not bad)\n",
                        List.of("I'm good", "Not bad"),
                        Map.of(
                                "I'm good", "That’s great!\n",
                                "Not bad", "Forget it, buddy. A new year, a fresh start.\n"
                        )
                );
                npc.defineDialog(
                        "Summer",
                        "It’s hot, buddy. How about some ice cream?(Good suggestion / No thanks)\n",
                        List.of("Good suggestion", "No thanks"),
                        Map.of(
                                "Good suggestion", "I’ll pay, buddy.\n",
                                "No thanks", "Okay, I’ll eat it alone.\n"
                        )
                );
                npc.defineDialog(
                        "Fall",
                        "Shall we jump on the leaves, buddy?(No, I'm not a kid)\n",
                        List.of("No, I'm not a kid", ""),
                        Map.of(
                                "No, I'm not a kid", "But I am.\n",
                                "", ""
                        )
                );
                npc.defineDialog(
                        "Winter",
                        "Wow, it's snowing heavily man. Come under my umbrella.\n",
                        List.of("", ""),
                        Map.of(
                                "", ""
                        )
                );
            }
            case "Leah" -> {
                npc.defineDialog(
                        "Spring",
                        "Where the hell have you been, idiot?(It was raining—I couldn’t get here any sooner)\n",
                        List.of("It was raining—I couldn’t get here any sooner", ""),
                        Map.of(
                                "It was raining—I couldn’t get here any sooner", "To hell with the rain—I don’t need you anymore.\n",
                                "", ""
                        )
                );
                npc.defineDialog(
                        "Summer",
                        "Careful your ass doesn’t catch fire in this weather, idiot\n",
                        List.of("", ""),
                        Map.of(
                                "", ""
                        )
                );
                npc.defineDialog(
                        "Fall",
                        "The trees are hollow, just like your damn head, idiot.(And your head too)\n",
                        List.of("And your head too", ""),
                        Map.of(
                                "And your head too", "Watch your mouth when talking to someone younger, idiot.\n",
                                "", ""
                        )
                );
                npc.defineDialog(
                        "Winter",
                        "When I saw you coming from afar, I thought Santa was on his way—then I realized it wasn’t a reindeer, it was a damn donkey.\n",
                        List.of("", ""),
                        Map.of(
                                "", ""
                        )
                );
            }
            case "Robin" -> {
                npc.defineDialog(
                        "Spring",
                        "Wow, Sissy, do you think these blossoms make my hair look attractive?(Yes, they do / No, not really)\n",
                        List.of("Yes, they do", "No, not really"),
                        Map.of(
                                "Yes, they do", "Oh, thanks.\n",
                                "No, not really", "You really have no taste.\n"
                        )
                );
                npc.defineDialog(
                        "Summer",
                        "Wow, Sissy, this sun is so harsh, it's bad for my skin.\n",
                        List.of("", ""),
                        Map.of(
                                "", ""
                        )
                );
                npc.defineDialog(
                        "Fall",
                        "Oh, want to go for a walk in the rain?(Yes, let’s go / No, I’m not coming)\n",
                        List.of("Yes, let’s go", "No, I’m not coming"),
                        Map.of(
                                "Yes, let’s go", "Let’s go, Sissy.\n",
                                "No, I’m not coming", "You broke my heart.\n"
                        )
                );
                npc.defineDialog(
                        "Winter",
                        "Wow, Sissy, come on, don't catch a cold in this weather.\n",
                        List.of("", ""),
                        Map.of(
                                "", ""
                        )
                );
            }
        }
    }
}
