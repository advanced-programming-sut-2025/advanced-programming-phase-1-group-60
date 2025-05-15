package repository;

import models.Tree;
import java.util.ArrayList;
import java.util.List;

public class TreeRepository {
    public static final List<Tree> trees = new ArrayList<>();

    static {
        Tree apricotTree = new Tree();
        apricotTree.setName("Apricot Tree");
        apricotTree.setSource("Apricot Sapling");
        apricotTree.setStages(new int[]{7, 7, 7, 7});
        apricotTree.setTotalHarvestTime(28);
        apricotTree.setFruit("Apricot");
        apricotTree.setFruitHarvestCycle(1);
        apricotTree.setFruitBaseSellPrice(59);
        apricotTree.setFruitEdible(true);
        apricotTree.setFruitEnergy(38);
        apricotTree.setSuitableSeasons(List.of("Spring"));
        trees.add(apricotTree);

        Tree cherryTree = new Tree();
        cherryTree.setName("Cherry Tree");
        cherryTree.setSource("Cherry Sapling");
        cherryTree.setStages(new int[]{7, 7, 7, 7});
        cherryTree.setTotalHarvestTime(28);
        cherryTree.setFruit("Cherry");
        cherryTree.setFruitHarvestCycle(1);
        cherryTree.setFruitBaseSellPrice(80);
        cherryTree.setFruitEdible(true);
        cherryTree.setFruitEnergy(38);
        cherryTree.setSuitableSeasons(List.of("Spring"));
        trees.add(cherryTree);

        Tree bananaTree = new Tree();
        bananaTree.setName("Banana Tree");
        bananaTree.setSource("Banana Sapling");
        bananaTree.setStages(new int[]{7, 7, 7, 7});
        bananaTree.setTotalHarvestTime(28);
        bananaTree.setFruit("Banana");
        bananaTree.setFruitHarvestCycle(1);
        bananaTree.setFruitBaseSellPrice(150);
        bananaTree.setFruitEdible(true);
        bananaTree.setFruitEnergy(75);
        bananaTree.setSuitableSeasons(List.of("Summer"));
        trees.add(bananaTree);

        Tree mangoTree = new Tree();
        mangoTree.setName("Mango Tree");
        mangoTree.setSource("Mango Sapling");
        mangoTree.setStages(new int[]{7, 7, 7, 7});
        mangoTree.setTotalHarvestTime(28);
        mangoTree.setFruit("Mango");
        mangoTree.setFruitHarvestCycle(1);
        mangoTree.setFruitBaseSellPrice(130);
        mangoTree.setFruitEdible(true);
        mangoTree.setFruitEnergy(100);
        mangoTree.setSuitableSeasons(List.of("Summer"));
        trees.add(mangoTree);

        Tree orangeTree = new Tree();
        orangeTree.setName("Orange Tree");
        orangeTree.setSource("Orange Sapling");
        orangeTree.setStages(new int[]{7, 7, 7, 7});
        orangeTree.setTotalHarvestTime(28);
        orangeTree.setFruit("Orange");
        orangeTree.setFruitHarvestCycle(1);
        orangeTree.setFruitBaseSellPrice(100);
        orangeTree.setFruitEdible(true);
        orangeTree.setFruitEnergy(38);
        orangeTree.setSuitableSeasons(List.of("Summer"));
        trees.add(orangeTree);

        Tree peachTree = new Tree();
        peachTree.setName("Peach Tree");
        peachTree.setSource("Peach Sapling");
        peachTree.setStages(new int[]{7, 7, 7, 7});
        peachTree.setTotalHarvestTime(28);
        peachTree.setFruit("Peach");
        peachTree.setFruitHarvestCycle(1);
        peachTree.setFruitBaseSellPrice(140);
        peachTree.setFruitEdible(true);
        peachTree.setFruitEnergy(38);
        peachTree.setSuitableSeasons(List.of("Summer"));
        trees.add(peachTree);

        Tree appleTree = new Tree();
        appleTree.setName("Apple Tree");
        appleTree.setSource("Apple Sapling");
        appleTree.setStages(new int[]{7, 7, 7, 7});
        appleTree.setTotalHarvestTime(28);
        appleTree.setFruit("Apple");
        appleTree.setFruitHarvestCycle(1);
        appleTree.setFruitBaseSellPrice(100);
        appleTree.setFruitEdible(true);
        appleTree.setFruitEnergy(38);
        appleTree.setSuitableSeasons(List.of("Fall"));
        trees.add(appleTree);

        Tree pomegranateTree = new Tree();
        pomegranateTree.setName("Pomegranate Tree");
        pomegranateTree.setSource("Pomegranate Sapling");
        pomegranateTree.setStages(new int[]{7, 7, 7, 7});
        pomegranateTree.setTotalHarvestTime(28);
        pomegranateTree.setFruit("Pomegranate");
        pomegranateTree.setFruitHarvestCycle(1);
        pomegranateTree.setFruitBaseSellPrice(140);
        pomegranateTree.setFruitEdible(true);
        pomegranateTree.setFruitEnergy(38);
        pomegranateTree.setSuitableSeasons(List.of("Fall"));
        trees.add(pomegranateTree);

        Tree oakTree = new Tree();
        oakTree.setName("Oak Tree");
        oakTree.setSource("Acorn");
        oakTree.setStages(new int[]{7, 7, 7, 7});
        oakTree.setTotalHarvestTime(28);
        oakTree.setFruit("Oak Resin");
        oakTree.setFruitHarvestCycle(7);
        oakTree.setFruitBaseSellPrice(150);
        oakTree.setFruitEdible(false);
        oakTree.setFruitEnergy(0);
        oakTree.setSuitableSeasons(List.of("Spring","Summer","Fall","Winter"));
        trees.add(oakTree);

        Tree mapleTree = new Tree();
        mapleTree.setName("Maple Tree");
        mapleTree.setSource("Maple Seeds");
        mapleTree.setStages(new int[]{7, 7, 7, 7});
        mapleTree.setTotalHarvestTime(28);
        mapleTree.setFruit("Maple Syrup");
        mapleTree.setFruitHarvestCycle(9);
        mapleTree.setFruitBaseSellPrice(200);
        mapleTree.setFruitEdible(false);
        mapleTree.setFruitEnergy(0);
        mapleTree.setSuitableSeasons(List.of("Spring","Summer","Fall","Winter"));
        trees.add(mapleTree);

        Tree pineTree = new Tree();
        pineTree.setName("Pine Tree");
        pineTree.setSource("Pine Cones");
        pineTree.setStages(new int[]{7, 7, 7, 7});
        pineTree.setTotalHarvestTime(28);
        pineTree.setFruit("Pine Tar");
        pineTree.setFruitHarvestCycle(5);
        pineTree.setFruitBaseSellPrice(100);
        pineTree.setFruitEdible(false);
        pineTree.setFruitEnergy(0);
        pineTree.setSuitableSeasons(List.of("Spring","Summer","Fall","Winter"));
        trees.add(pineTree);

        Tree mahoganyTree = new Tree();
        mahoganyTree.setName("Mahogany Tree");
        mahoganyTree.setSource("Mahogany Seeds");
        mahoganyTree.setStages(new int[]{7, 7, 7, 7});
        mahoganyTree.setTotalHarvestTime(28);
        mahoganyTree.setFruit("Sap");
        mahoganyTree.setFruitHarvestCycle(1);
        mahoganyTree.setFruitBaseSellPrice(2);
        mahoganyTree.setFruitEdible(true);
        mahoganyTree.setFruitEnergy(-2);
        mahoganyTree.setSuitableSeasons(List.of("Spring","Summer","Fall","Winter"));
        trees.add(mahoganyTree);

        Tree mushroomTree = new Tree();
        mushroomTree.setName("Mushroom Tree");
        mushroomTree.setSource("Mushroom Tree Seeds");
        mushroomTree.setStages(new int[]{7, 7, 7, 7});
        mushroomTree.setTotalHarvestTime(28);
        mushroomTree.setFruit("Common Mushroom");
        mushroomTree.setFruitHarvestCycle(1);
        mushroomTree.setFruitBaseSellPrice(40);
        mushroomTree.setFruitEdible(true);
        mushroomTree.setFruitEnergy(38);
        mushroomTree.setSuitableSeasons(List.of("Spring","Summer","Fall","Winter"));
        trees.add(mushroomTree);

        Tree mysticTree = new Tree();
        mysticTree.setName("Mystic Tree");
        mysticTree.setSource("Mystic Tree Sapling");
        mysticTree.setStages(new int[]{7, 7, 7, 7});
        mysticTree.setTotalHarvestTime(28);
        mysticTree.setFruit("Mystic Syrup");
        mysticTree.setFruitHarvestCycle(7);
        mysticTree.setFruitBaseSellPrice(1000);
        mysticTree.setFruitEdible(true);
        mysticTree.setFruitEnergy(500);
        mysticTree.setSuitableSeasons(List.of("Spring","Summer","Fall","Winter"));
        trees.add(mysticTree);



    }
}