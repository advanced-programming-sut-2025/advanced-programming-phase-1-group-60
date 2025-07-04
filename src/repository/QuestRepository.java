package repository;

import models.Item;
import models.Npc;
import models.Quest;
import models.Reward;

public class  QuestRepository {
    private static final QuestRepository INSTANCE = new QuestRepository();
    NpcRepository npcRepository = NpcRepository.getInstance();

    public static QuestRepository getInstance() {
        return INSTANCE;
    }

    public void initialize() {
        // Sebastian
        Npc sebastian = npcRepository.getNpcByName("SEBASTIAN");

        Item s1item = new Item();
        s1item.setName("Iron");
        s1item.setQuantity(50);
        Item sR1Item = new Item();
        sR1Item.setName("Diamond");
        sR1Item.setQuantity(2);
        Reward s1Reward = new Reward();
        s1Reward.setItem(sR1Item);
        Quest s1quest = new Quest(1, sebastian, s1item, s1Reward, 0, 0);

        Item s2item = new Item();
        s2item.setName("pumpkin pie");
        s2item.setQuantity(1);
        Reward s2Reward = new Reward();
        s2Reward.setMoney(5000);
        Quest s2quest = new Quest(2, sebastian, s2item, s2Reward, 1, 0);

        Item s3item = new Item();
        s3item.setName("stone");
        s3item.setQuantity(150);
        Item sR3Item = new Item();
        sR3Item.setName("Quartz");
        sR3Item.setQuantity(50);
        Reward s3Reward = new Reward();
        s3Reward.setItem(sR3Item);
        Quest s3quest = new Quest(3, sebastian, s3item, s3Reward, 0, 1);

        sebastian.addQuest(s1quest); sebastian.addQuest(s2quest); sebastian.addQuest(s3quest);

        // Abigail
        Npc abigail = npcRepository.getNpcByName("ABIGAIL");

        Item a1Req = new Item();
        a1Req.setName("Gold Bar");
        a1Req.setQuantity(1);
        Reward a1Reward = new Reward();
        a1Reward.setFriendshipXp(200);
        Quest a1 = new Quest(1, abigail, a1Req, a1Reward, 0, 0);

        Item a2Req = new Item();
        a2Req.setName("Pumpkin");
        a2Req.setQuantity(1);
        Reward a2Reward = new Reward();
        a2Reward.setMoney(500);
        Quest a2 = new Quest(2, abigail, a2Req, a2Reward, 1, 0);

        Item a3Req = new Item();
        a3Req.setName("Wheat");
        a3Req.setQuantity(50);
        Item aR3Item = new Item();
        aR3Item.setName("Iridium Sprinkler");
        aR3Item.setQuantity(1);
        aR3Item.setType("tool");
        Reward a3Reward = new Reward();
        a3Reward.setItem(aR3Item);
        Quest a3 = new Quest(3, abigail, a3Req, a3Reward, 0, 1);

        abigail.addQuest(a1); abigail.addQuest(a2); abigail.addQuest(a3);

        // Harvey
        Npc harvey = npcRepository.getNpcByName("HARVEY");

        Item h1Req = new Item();
        h1Req.setType("foraging");
        h1Req.setQuantity(12);
        Reward h1Reward = new Reward();
        h1Reward.setMoney(750);
        Quest h1 = new Quest(1, harvey, h1Req, h1Reward, 0, 0);

        Item h2Req = new Item();
        h2Req.setName("Salmon");
        h2Req.setQuantity(1);
        Reward h2Reward = new Reward();
        h2Reward.setFriendshipXp(200);
        Quest h2 = new Quest(2, harvey, h2Req, h2Reward, 1, 0);

        Item h3Req = new Item();
        h3Req.setName("Wine Bottle");
        h3Req.setQuantity(1);
        Item hR3Item = new Item();
        hR3Item.setName("Salad");
        hR3Item.setQuantity(5);
        Reward h3Reward = new Reward();
        h3Reward.setItem(hR3Item);
        Quest h3 = new Quest(3, harvey, h3Req, h3Reward, 0, 1);

        harvey.addQuest(h1); harvey.addQuest(h2); harvey.addQuest(h3);

        // Leah
        Npc leah = npcRepository.getNpcByName("LEAH");

        Item l1Req = new Item();
        l1Req.setName("Hardwood");
        l1Req.setQuantity(10);
        Reward l1Reward = new Reward();
        l1Reward.setMoney(500);
        Quest l1 = new Quest(1, leah, l1Req, l1Reward, 0, 0);

        Item l2Req = new Item();
        l2Req.setName("Salmon");
        l2Req.setQuantity(1);
        // رسپی رو که درست کرد باید اینجا درست بشه و به ریوارد اضافه بشه
        Reward l2Reward = new Reward();
        Quest l2 = new Quest(2, leah, l2Req, l2Reward, 1, 0);

        Item l3Req = new Item();
        l3Req.setName("Wood");
        l3Req.setQuantity(200);
        Item lR3Item = new Item();
        lR3Item.setName("Deluxe Scarecrow");
        lR3Item.setQuantity(3);
        Reward l3Reward = new Reward();
        l3Reward.setItem(lR3Item);
        Quest l3 = new Quest(3, leah, l3Req, l3Reward, 0, 3);

        leah.addQuest(l1); leah.addQuest(l2); leah.addQuest(l3);

        // Robin
        Npc robin = npcRepository.getNpcByName("ROBIN");

        Item r1Req = new Item();
        r1Req.setName("Wood");
        r1Req.setQuantity(80);
        Reward r1Reward = new Reward();
        r1Reward.setMoney(1000);
        Quest r1 = new Quest(1, robin, r1Req, r1Reward, 0, 0);

        Item r2Req = new Item();
        r2Req.setName("Iron Bar");
        r2Req.setQuantity(10);
        Item rR2Item = new Item();
        rR2Item.setName("Bee House");
        rR2Item.setQuantity(3);
        Reward r2Reward = new Reward();
        r2Reward.setItem(rR2Item);
        Quest r2 = new Quest(2, robin, r2Req, r2Reward, 1, 0);

        Item r3Req = new Item();
        r3Req.setName("Wood");
        r3Req.setQuantity(1000);
        Reward r3Reward = new Reward();
        r3Reward.setMoney(25000);
        Quest r3 = new Quest(3, robin, r3Req, r3Reward, 0, 2);

        robin.addQuest(r1); robin.addQuest(r2); robin.addQuest(r3);
    }
}
