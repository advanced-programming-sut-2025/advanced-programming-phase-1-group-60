package repository;

import models.ForagingCrop;
import models.ForagingMineral;
import models.ForagingTree;

import java.util.ArrayList;
import java.util.List;

public class ForagingRepository {
    public static final List<ForagingCrop> foragingCrops = new ArrayList<>();
    public static final List<ForagingMineral> foragingMinerals = new ArrayList<>();
    public static final List<ForagingTree> foragingTrees = new ArrayList<>();

    static {
        ForagingCrop commonMushroom = new ForagingCrop();
        commonMushroom.setName("Common Mushroom");
        commonMushroom.setSuitableSeasons(List.of("Spring", "Summer", "Fall", "Winter"));
        commonMushroom.setBaseSellPrice(40);
        commonMushroom.setEnergy(38);
        foragingCrops.add(commonMushroom);

        ForagingCrop daffodil = new ForagingCrop();
        daffodil.setName("Daffodil");
        daffodil.setSuitableSeasons(List.of("Spring"));
        daffodil.setBaseSellPrice(50);
        daffodil.setEnergy(13);
        foragingCrops.add(daffodil);

        ForagingCrop dandelion = new ForagingCrop();
        dandelion.setName("Dandelion");
        dandelion.setSuitableSeasons(List.of("Spring"));
        dandelion.setBaseSellPrice(40);
        dandelion.setEnergy(25);
        foragingCrops.add(dandelion);

        ForagingCrop leek = new ForagingCrop();
        leek.setName("Leek");
        leek.setSuitableSeasons(List.of("Spring"));
        leek.setBaseSellPrice(60);
        leek.setEnergy(40);
        foragingCrops.add(leek);

        ForagingCrop morel = new ForagingCrop();
        morel.setName("Morel");
        morel.setSuitableSeasons(List.of("Spring"));
        morel.setBaseSellPrice(150);
        morel.setEnergy(20);
        foragingCrops.add(morel);

        ForagingCrop salmonBerry = new ForagingCrop();
        salmonBerry.setName("Salmonberry");
        salmonBerry.setSuitableSeasons(List.of("Spring"));
        salmonBerry.setBaseSellPrice(5);
        salmonBerry.setEnergy(25);
        foragingCrops.add(salmonBerry);

        ForagingCrop springOnion = new ForagingCrop();
        springOnion.setName("Spring Onion");
        springOnion.setSuitableSeasons(List.of("Spring"));
        springOnion.setBaseSellPrice(8);
        springOnion.setEnergy(13);
        foragingCrops.add(springOnion);

        ForagingCrop wildHorseradish = new ForagingCrop();
        wildHorseradish.setName("Wild Horseradish");
        wildHorseradish.setSuitableSeasons(List.of("Spring"));
        wildHorseradish.setBaseSellPrice(50);
        wildHorseradish.setEnergy(13);
        foragingCrops.add(wildHorseradish);

        ForagingCrop fiddleheadFern = new ForagingCrop();
        fiddleheadFern.setName("Fiddlehead Fern");
        fiddleheadFern.setSuitableSeasons(List.of("Summer"));
        fiddleheadFern.setBaseSellPrice(90);
        fiddleheadFern.setEnergy(25);
        foragingCrops.add(fiddleheadFern);

        ForagingCrop grape = new ForagingCrop();
        grape.setName("Grape");
        grape.setSuitableSeasons(List.of("Summer"));
        grape.setBaseSellPrice(80);
        grape.setEnergy(38);
        foragingCrops.add(grape);

        ForagingCrop redMushroom = new ForagingCrop();
        redMushroom.setName("Red Mushroom");
        redMushroom.setSuitableSeasons(List.of("Summer"));
        redMushroom.setBaseSellPrice(75);
        redMushroom.setEnergy(-50);
        foragingCrops.add(redMushroom);

        ForagingCrop spiceBerry = new ForagingCrop();
        spiceBerry.setName("Spice Berry");
        spiceBerry.setSuitableSeasons(List.of("Summer"));
        spiceBerry.setBaseSellPrice(80);
        spiceBerry.setEnergy(25);
        foragingCrops.add(spiceBerry);

        ForagingCrop sweetPea = new ForagingCrop();
        sweetPea.setName("Sweet Pea");
        sweetPea.setSuitableSeasons(List.of("Summer"));
        sweetPea.setBaseSellPrice(50);
        sweetPea.setEnergy(0);
        foragingCrops.add(sweetPea);

        ForagingCrop blackberry = new ForagingCrop();
        blackberry.setName("Blackberry");
        blackberry.setSuitableSeasons(List.of("Fall"));
        blackberry.setBaseSellPrice(25);
        blackberry.setEnergy(25);
        foragingCrops.add(blackberry);

        ForagingCrop chanterelle = new ForagingCrop();
        chanterelle.setName("Chanterelle");
        chanterelle.setSuitableSeasons(List.of("Fall"));
        chanterelle.setBaseSellPrice(160);
        chanterelle.setEnergy(75);
        foragingCrops.add(chanterelle);

        ForagingCrop hazelnut = new ForagingCrop();
        hazelnut.setName("Hazelnut");
        hazelnut.setSuitableSeasons(List.of("Fall"));
        hazelnut.setBaseSellPrice(40);
        hazelnut.setEnergy(38);
        foragingCrops.add(hazelnut);

        ForagingCrop purpleMushroom = new ForagingCrop();
        purpleMushroom.setName("Purple Mushroom");
        purpleMushroom.setSuitableSeasons(List.of("Fall"));
        purpleMushroom.setBaseSellPrice(90);
        purpleMushroom.setEnergy(30);
        foragingCrops.add(purpleMushroom);

        ForagingCrop wildPlum = new ForagingCrop();
        wildPlum.setName("Wild Plum");
        wildPlum.setSuitableSeasons(List.of("Fall"));
        wildPlum.setBaseSellPrice(80);
        wildPlum.setEnergy(25);
        foragingCrops.add(wildPlum);

        ForagingCrop crocus = new ForagingCrop();
        crocus.setName("Crocus");
        crocus.setSuitableSeasons(List.of("Winter"));
        crocus.setBaseSellPrice(60);
        crocus.setEnergy(0);
        foragingCrops.add(crocus);

        ForagingCrop crystalFruit = new ForagingCrop();
        crystalFruit.setName("Crystal Fruit");
        crystalFruit.setSuitableSeasons(List.of("Winter"));
        crystalFruit.setBaseSellPrice(150);
        crystalFruit.setEnergy(63);
        foragingCrops.add(crystalFruit);

        ForagingCrop holly = new ForagingCrop();
        holly.setName("Holly");
        holly.setSuitableSeasons(List.of("Winter"));
        holly.setBaseSellPrice(80);
        holly.setEnergy(-37);
        foragingCrops.add(holly);

        ForagingCrop snowYam = new ForagingCrop();
        snowYam.setName("Snow Yam");
        snowYam.setSuitableSeasons(List.of("Winter"));
        snowYam.setBaseSellPrice(100);
        snowYam.setEnergy(30);
        foragingCrops.add(snowYam);

        ForagingCrop winterRoot = new ForagingCrop();
        winterRoot.setName("Winter Root");
        winterRoot.setSuitableSeasons(List.of("Winter"));
        winterRoot.setBaseSellPrice(70);
        winterRoot.setEnergy(25);
        foragingCrops.add(winterRoot);
    }
    static {
        ForagingTree acorns = new ForagingTree();
        acorns.setName("Acorns");
        acorns.setSuitableSeasons(List.of("Winter","Spring","Summer","Fall"));
        foragingTrees.add(acorns);

        ForagingTree mapleSeeds = new ForagingTree();
        mapleSeeds.setName("Maple Seeds");
        mapleSeeds.setSuitableSeasons(List.of("Winter","Spring","Summer","Fall"));
        foragingTrees.add(mapleSeeds);

        ForagingTree pineCones = new ForagingTree();
        pineCones.setName("Pine Cones");
        pineCones.setSuitableSeasons(List.of("Winter","Spring","Summer","Fall"));
        foragingTrees.add(pineCones);

        ForagingTree mahoganySeeds = new ForagingTree();
        mahoganySeeds.setName("Mahogany Seeds");
        mahoganySeeds.setSuitableSeasons(List.of("Winter","Spring","Summer","Fall"));
        foragingTrees.add(mahoganySeeds);

        ForagingTree mushroomTreeSeeds = new ForagingTree();
        mushroomTreeSeeds.setName("Mushroom Tree Seeds");
        mushroomTreeSeeds.setSuitableSeasons(List.of("Winter","Spring","Summer","Fall"));
        foragingTrees.add(mushroomTreeSeeds);
    }
    static {
        ForagingMineral quartz = new ForagingMineral();
        quartz.setName("Quartz");
        quartz.setBaseSellPrice(25);
        foragingMinerals.add(quartz);

        ForagingMineral earthCrystal = new ForagingMineral();
        earthCrystal.setName("Earth Crystal");
        earthCrystal.setBaseSellPrice(50);
        foragingMinerals.add(earthCrystal);

        ForagingMineral frozenTear = new ForagingMineral();
        frozenTear.setName("Frozen Tear");
        frozenTear.setBaseSellPrice(75);
        foragingMinerals.add(frozenTear);

        ForagingMineral fireQuartz = new ForagingMineral();
        fireQuartz.setName("Fire Quartz");
        fireQuartz.setBaseSellPrice(100);
        foragingMinerals.add(fireQuartz);

        ForagingMineral emerald = new ForagingMineral();
        emerald.setName("Emerald");
        emerald.setBaseSellPrice(250);
        foragingMinerals.add(emerald);

        ForagingMineral aquamarine = new ForagingMineral();
        aquamarine.setName("Aqua Marine");
        aquamarine.setBaseSellPrice(180);
        foragingMinerals.add(aquamarine);

        ForagingMineral amethyst = new ForagingMineral();
        amethyst.setName("Amethyst");
        amethyst.setBaseSellPrice(100);
        foragingMinerals.add(amethyst);

        ForagingMineral topaz = new ForagingMineral();
        topaz.setName("Topaz");
        topaz.setBaseSellPrice(80);
        foragingMinerals.add(topaz);

        ForagingMineral jade = new ForagingMineral();
        jade.setName("Jade");
        jade.setBaseSellPrice(200);
        foragingMinerals.add(jade);

        ForagingMineral diamond = new ForagingMineral();
        diamond.setName("Diamond");
        diamond.setBaseSellPrice(750);
        foragingMinerals.add(diamond);

        ForagingMineral prismaticShard = new ForagingMineral();
        prismaticShard.setName("Prismatic Shard");
        prismaticShard.setBaseSellPrice(2000);
        foragingMinerals.add(prismaticShard);

        ForagingMineral copper = new ForagingMineral();
        copper.setName("Copper");
        copper.setBaseSellPrice(5);
        foragingMinerals.add(copper);

        ForagingMineral iron = new ForagingMineral();
        iron.setName("Iron");
        iron.setBaseSellPrice(10);
        foragingMinerals.add(iron);

        ForagingMineral gold = new ForagingMineral();
        gold.setName("Gold");
        gold.setBaseSellPrice(25);
        foragingMinerals.add(gold);

        ForagingMineral iridium = new ForagingMineral();
        iridium.setName("Iridium");
        iridium.setBaseSellPrice(100);
        foragingMinerals.add(iridium);

        ForagingMineral coal = new ForagingMineral();
        coal.setName("Coal");
        coal.setBaseSellPrice(15);
        foragingMinerals.add(coal);
    }
}