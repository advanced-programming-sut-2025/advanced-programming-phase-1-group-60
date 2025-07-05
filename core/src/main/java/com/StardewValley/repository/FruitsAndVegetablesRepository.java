// File: repository/FruitsAndVegetablesRepository.java
package com.StardewValley.repository;

import com.StardewValley.models.FruitsAndVegetables;
import com.StardewValley.models.Seeds;
import java.util.ArrayList;
import java.util.List;

public class FruitsAndVegetablesRepository {
    public static final List<FruitsAndVegetables> crops = new ArrayList<>();
    public static final List<Seeds> seeds = new ArrayList<>();
    public static FruitsAndVegetables getCropByName(String name) {
        for (FruitsAndVegetables crop : crops) {
            if (crop.getName().equalsIgnoreCase(name)) {
                return crop;
            }
        }
        return null;
    }
    static {
        //Blue jazz
        FruitsAndVegetables blueJazz = new FruitsAndVegetables();
        blueJazz.setName("Blue Jazz");
        blueJazz.setSource("Jazz Seeds");
        blueJazz.setGrowthStages(new int[]{1, 2, 2, 2});
        blueJazz.setTotalHarvestTime(7);
        blueJazz.setOneTime(true);
        blueJazz.setRegrowthTime(null);
        blueJazz.setSellPrice(50);
        blueJazz.setEdible(true);
        blueJazz.setBaseEnergy(45);
        blueJazz.setSuitableSeasons(List.of("Spring"));
        blueJazz.setCanBeGiant(false);
        crops.add(blueJazz);

        FruitsAndVegetables carrot = new FruitsAndVegetables();
        carrot.setName("Carrot");
        carrot.setSource("Carrot Seeds");
        carrot.setGrowthStages(new int[]{1, 1, 1});
        carrot.setTotalHarvestTime(3);
        carrot.setOneTime(true);
        carrot.setRegrowthTime(null);
        carrot.setSellPrice(35);
        carrot.setEdible(true);
        carrot.setBaseEnergy(75);
        carrot.setSuitableSeasons(List.of("Spring"));
        carrot.setCanBeGiant(false);
        crops.add(carrot);

        FruitsAndVegetables cauliflower = new FruitsAndVegetables();
        cauliflower.setName("Cauliflower");
        cauliflower.setSource("Cauliflower Seeds");
        cauliflower.setGrowthStages(new int[]{1, 2, 4, 4, 1});
        cauliflower.setTotalHarvestTime(12);
        cauliflower.setOneTime(true);
        cauliflower.setRegrowthTime(null);
        cauliflower.setSellPrice(175);
        cauliflower.setEdible(true);
        cauliflower.setBaseEnergy(75);
        cauliflower.setSuitableSeasons(List.of("Spring"));
        cauliflower.setCanBeGiant(true);
        crops.add(cauliflower);

        FruitsAndVegetables coffeeBean = new FruitsAndVegetables();
        coffeeBean.setName("Coffee Bean");
        coffeeBean.setSource("Coffee Bean");
        coffeeBean.setGrowthStages(new int[]{1, 2, 2, 3, 2});
        coffeeBean.setTotalHarvestTime(10);
        coffeeBean.setOneTime(false);
        coffeeBean.setRegrowthTime(2);
        coffeeBean.setSellPrice(15);
        coffeeBean.setEdible(false);
        coffeeBean.setBaseEnergy(0);
        coffeeBean.setSuitableSeasons(List.of("Spring", "Summer"));
        coffeeBean.setCanBeGiant(false);
        crops.add(coffeeBean);

        FruitsAndVegetables garlic = new FruitsAndVegetables();
        garlic.setName("Garlic");
        garlic.setSource("Garlic Seeds");
        garlic.setGrowthStages(new int[]{1, 1, 1, 1});
        garlic.setTotalHarvestTime(4);
        garlic.setOneTime(true);
        garlic.setRegrowthTime(null);
        garlic.setSellPrice(60);
        garlic.setEdible(true);
        garlic.setBaseEnergy(20);
        garlic.setSuitableSeasons(List.of("Spring"));
        garlic.setCanBeGiant(false);
        crops.add(garlic);

        FruitsAndVegetables greenBean = new FruitsAndVegetables();
        greenBean.setName("Green Bean");
        greenBean.setSource("Bean Starter");
        greenBean.setGrowthStages(new int[]{1, 1, 1, 3, 4});
        greenBean.setTotalHarvestTime(10);
        greenBean.setOneTime(false);
        greenBean.setRegrowthTime(3);
        greenBean.setSellPrice(40);
        greenBean.setEdible(true);
        greenBean.setBaseEnergy(25);
        greenBean.setSuitableSeasons(List.of("Spring"));
        greenBean.setCanBeGiant(false);
        crops.add(greenBean);

        FruitsAndVegetables kale = new FruitsAndVegetables();
        kale.setName("Kale");
        kale.setSource("Kale Seeds");
        kale.setGrowthStages(new int[]{1, 2, 2, 1});
        kale.setTotalHarvestTime(6);
        kale.setOneTime(true);
        kale.setRegrowthTime(null);
        kale.setSellPrice(110);
        kale.setEdible(true);
        kale.setBaseEnergy(50);
        kale.setSuitableSeasons(List.of("Spring"));
        kale.setCanBeGiant(false);
        crops.add(kale);

        FruitsAndVegetables parsnip = new FruitsAndVegetables();
        parsnip.setName("Parsnip");
        parsnip.setSource("Parsnip Seeds");
        parsnip.setGrowthStages(new int[]{1, 1, 1, 1});
        parsnip.setTotalHarvestTime(4);
        parsnip.setOneTime(true);
        parsnip.setRegrowthTime(null);
        parsnip.setSellPrice(35);
        parsnip.setEdible(true);
        parsnip.setBaseEnergy(25);
        parsnip.setSuitableSeasons(List.of("Spring"));
        parsnip.setCanBeGiant(false);
        crops.add(parsnip);

        FruitsAndVegetables potato = new FruitsAndVegetables();
        potato.setName("Potato");
        potato.setSource("Potato Seeds");
        potato.setGrowthStages(new int[]{1, 1, 1, 2, 1});
        potato.setTotalHarvestTime(6);
        potato.setOneTime(true);
        potato.setRegrowthTime(null);
        potato.setSellPrice(80);
        potato.setEdible(true);
        potato.setBaseEnergy(25);
        potato.setSuitableSeasons(List.of("Spring"));
        potato.setCanBeGiant(false);
        crops.add(potato);

        FruitsAndVegetables rhubarb = new FruitsAndVegetables();
        rhubarb.setName("Rhubarb");
        rhubarb.setSource("Rhubarb Seeds");
        rhubarb.setGrowthStages(new int[]{2, 2, 2, 3, 4});
        rhubarb.setTotalHarvestTime(13);
        rhubarb.setOneTime(true);
        rhubarb.setRegrowthTime(null);
        rhubarb.setSellPrice(220);
        rhubarb.setEdible(false);
        rhubarb.setBaseEnergy(0);
        rhubarb.setSuitableSeasons(List.of("Spring"));
        rhubarb.setCanBeGiant(false);
        crops.add(rhubarb);

        FruitsAndVegetables strawberry = new FruitsAndVegetables();
        strawberry.setName("Strawberry");
        strawberry.setSource("Strawberry Seeds");
        strawberry.setGrowthStages(new int[]{1, 1, 2, 2, 2});
        strawberry.setTotalHarvestTime(8);
        strawberry.setOneTime(false);
        strawberry.setRegrowthTime(4);
        strawberry.setSellPrice(120);
        strawberry.setEdible(true);
        strawberry.setBaseEnergy(50);
        strawberry.setSuitableSeasons(List.of("Spring"));
        strawberry.setCanBeGiant(false);
        crops.add(strawberry);

        FruitsAndVegetables tulip = new FruitsAndVegetables();
        tulip.setName("Tulip");
        tulip.setSource("Tulip Bulb");
        tulip.setGrowthStages(new int[]{1, 1, 2, 2});
        tulip.setTotalHarvestTime(6);
        tulip.setOneTime(true);
        tulip.setRegrowthTime(null);
        tulip.setSellPrice(30);
        tulip.setEdible(true);
        tulip.setBaseEnergy(45);
        tulip.setSuitableSeasons(List.of("Spring"));
        tulip.setCanBeGiant(false);
        crops.add(tulip);

        FruitsAndVegetables unmilledRice = new FruitsAndVegetables();
        unmilledRice.setName("Unmilled Rice");
        unmilledRice.setSource("Rice Shoot");
        unmilledRice.setGrowthStages(new int[]{1, 2, 2, 3});
        unmilledRice.setTotalHarvestTime(8);
        unmilledRice.setOneTime(true);
        unmilledRice.setRegrowthTime(null);
        unmilledRice.setSellPrice(30);
        unmilledRice.setEdible(true);
        unmilledRice.setBaseEnergy(3);
        unmilledRice.setSuitableSeasons(List.of("Spring"));
        unmilledRice.setCanBeGiant(false);
        crops.add(unmilledRice);

        FruitsAndVegetables blueberry = new FruitsAndVegetables();
        blueberry.setName("Blueberry");
        blueberry.setSource("Blueberry Seeds");
        blueberry.setGrowthStages(new int[]{1, 3, 3, 4, 2});
        blueberry.setTotalHarvestTime(13);
        blueberry.setOneTime(false);
        blueberry.setRegrowthTime(4);
        blueberry.setSellPrice(50);
        blueberry.setEdible(true);
        blueberry.setBaseEnergy(25);
        blueberry.setSuitableSeasons(List.of("Summer"));
        blueberry.setCanBeGiant(false);
        crops.add(blueberry);

        FruitsAndVegetables corn = new FruitsAndVegetables();
        corn.setName("Corn");
        corn.setSource("Corn Seeds");
        corn.setGrowthStages(new int[]{2, 3, 3, 3, 3});
        corn.setTotalHarvestTime(14);
        corn.setOneTime(false);
        corn.setRegrowthTime(4);
        corn.setSellPrice(50);
        corn.setEdible(true);
        corn.setBaseEnergy(25);
        corn.setSuitableSeasons(List.of("Summer","Fall"));
        corn.setCanBeGiant(false);
        crops.add(corn);

        FruitsAndVegetables hops = new FruitsAndVegetables();
        hops.setName("Hops");
        hops.setSource("Hops Starter");
        hops.setGrowthStages(new int[]{1, 1, 2, 3, 4});
        hops.setTotalHarvestTime(11);
        hops.setOneTime(false);
        hops.setRegrowthTime(1);
        hops.setSellPrice(25);
        hops.setEdible(true);
        hops.setBaseEnergy(45);
        hops.setSuitableSeasons(List.of("Summer"));
        hops.setCanBeGiant(false);
        crops.add(hops);

        FruitsAndVegetables hotPepper = new FruitsAndVegetables();
        hotPepper.setName("Hot Pepper");
        hotPepper.setSource("Pepper Seeds");
        hotPepper.setGrowthStages(new int[]{1, 1, 1, 1, 1});
        hotPepper.setTotalHarvestTime(5);
        hotPepper.setOneTime(false);
        hotPepper.setRegrowthTime(3);
        hotPepper.setSellPrice(40);
        hotPepper.setEdible(true);
        hotPepper.setBaseEnergy(13);
        hotPepper.setSuitableSeasons(List.of("Summer"));
        hotPepper.setCanBeGiant(false);
        crops.add(hotPepper);

        FruitsAndVegetables melon = new FruitsAndVegetables();
        melon.setName("Melon");
        melon.setSource("Melon Seeds");
        melon.setGrowthStages(new int[]{1, 2, 3, 3, 3});
        melon.setTotalHarvestTime(12);
        melon.setOneTime(true);
        melon.setRegrowthTime(null);
        melon.setSellPrice(250);
        melon.setEdible(true);
        melon.setBaseEnergy(113);
        melon.setSuitableSeasons(List.of("Summer"));
        melon.setCanBeGiant(true);
        crops.add(melon);

        FruitsAndVegetables poppy = new FruitsAndVegetables();
        poppy.setName("Poppy");
        poppy.setSource("Poppy Seeds");
        poppy.setGrowthStages(new int[]{1, 2, 2, 2});
        poppy.setTotalHarvestTime(7);
        poppy.setOneTime(true);
        poppy.setRegrowthTime(null);
        poppy.setSellPrice(140);
        poppy.setEdible(true);
        poppy.setBaseEnergy(45);
        poppy.setSuitableSeasons(List.of("Summer"));
        poppy.setCanBeGiant(false);
        crops.add(poppy);


        FruitsAndVegetables radish = new FruitsAndVegetables();
        radish.setName("Radish");
        radish.setSource("Radish Seeds");
        radish.setGrowthStages(new int[]{2, 1, 2, 1});
        radish.setTotalHarvestTime(6);
        radish.setOneTime(true);
        radish.setRegrowthTime(null);
        radish.setSellPrice(90);
        radish.setEdible(true);
        radish.setBaseEnergy(45);
        radish.setSuitableSeasons(List.of("Summer"));
        radish.setCanBeGiant(false);
        crops.add(radish);

        FruitsAndVegetables redCabbage = new FruitsAndVegetables();
        redCabbage.setName("Red Cabbage");
        redCabbage.setSource("Red Cabbage Seeds");
        redCabbage.setGrowthStages(new int[]{2, 1, 2, 2, 2});
        redCabbage.setTotalHarvestTime(9);
        redCabbage.setOneTime(true);
        redCabbage.setRegrowthTime(null);
        redCabbage.setSellPrice(260);
        redCabbage.setEdible(true);
        redCabbage.setBaseEnergy(75);
        redCabbage.setSuitableSeasons(List.of("Summer"));
        redCabbage.setCanBeGiant(false);
        crops.add(redCabbage);

        FruitsAndVegetables starfruit = new FruitsAndVegetables();
        starfruit.setName("Starfuit");
        starfruit.setSource("Starfruit Seeds");
        starfruit.setGrowthStages(new int[]{2, 3, 2, 3, 3});
        starfruit.setTotalHarvestTime(13);
        starfruit.setOneTime(true);
        starfruit.setRegrowthTime(null);
        starfruit.setSellPrice(750);
        starfruit.setEdible(true);
        starfruit.setBaseEnergy(125);
        starfruit.setSuitableSeasons(List.of("Summer"));
        starfruit.setCanBeGiant(false);
        crops.add(starfruit);

        FruitsAndVegetables summerSpangle = new FruitsAndVegetables();
        summerSpangle.setName("Summer Spangle");
        summerSpangle.setSource("Spangle Seeds");
        summerSpangle.setGrowthStages(new int[]{1, 2, 3, 1});
        summerSpangle.setTotalHarvestTime(7);
        summerSpangle.setOneTime(true);
        summerSpangle.setRegrowthTime(null);
        summerSpangle.setSellPrice(90);
        summerSpangle.setEdible(true);
        summerSpangle.setBaseEnergy(45);
        summerSpangle.setSuitableSeasons(List.of("Summer"));
        summerSpangle.setCanBeGiant(false);
        crops.add(summerSpangle);

        FruitsAndVegetables summerSquash = new FruitsAndVegetables();
        summerSquash.setName("Summer Squash");
        summerSquash.setSource("Summer Squash Seeds");
        summerSquash.setGrowthStages(new int[]{1, 1, 1, 2, 1});
        summerSquash.setTotalHarvestTime(6);
        summerSquash.setOneTime(false);
        summerSquash.setRegrowthTime(3);
        summerSquash.setSellPrice(45);
        summerSquash.setEdible(true);
        summerSquash.setBaseEnergy(63);
        summerSquash.setSuitableSeasons(List.of("Summer"));
        summerSquash.setCanBeGiant(false);
        crops.add(summerSquash);

        FruitsAndVegetables sunflower = new FruitsAndVegetables();
        sunflower.setName("Sunflower");
        sunflower.setSource("Sunflower Seeds");
        sunflower.setGrowthStages(new int[]{1, 2, 3 ,2});
        sunflower.setTotalHarvestTime(8);
        sunflower.setOneTime(true);
        sunflower.setRegrowthTime(null);
        sunflower.setSellPrice(80);
        sunflower.setEdible(true);
        sunflower.setBaseEnergy(45);
        sunflower.setSuitableSeasons(List.of("Summer","Fall"));
        sunflower.setCanBeGiant(false);
        crops.add(sunflower);

        FruitsAndVegetables tomato = new FruitsAndVegetables();
        tomato.setName("Tomato");
        tomato.setSource("Tomato Seeds");
        tomato.setGrowthStages(new int[]{2, 2, 2, 2, 3});
        tomato.setTotalHarvestTime(11);
        tomato.setOneTime(false);
        tomato.setRegrowthTime(4);
        tomato.setSellPrice(60);
        tomato.setEdible(true);
        tomato.setBaseEnergy(20);
        tomato.setSuitableSeasons(List.of("Summer"));
        tomato.setCanBeGiant(false);
        crops.add(tomato);

        FruitsAndVegetables wheat = new FruitsAndVegetables();
        wheat.setName("Wheat");
        wheat.setSource("Wheat Seeds");
        wheat.setGrowthStages(new int[]{1, 1, 1, 1});
        wheat.setTotalHarvestTime(4);
        wheat.setOneTime(true);
        wheat.setRegrowthTime(null);
        wheat.setSellPrice(25);
        wheat.setEdible(false);
        wheat.setBaseEnergy(0);
        wheat.setSuitableSeasons(List.of("Summer","Fall"));
        wheat.setCanBeGiant(false);
        crops.add(wheat);

        FruitsAndVegetables amaranth = new FruitsAndVegetables();
        amaranth.setName("Amaranth");
        amaranth.setSource("Amaranth Seeds");
        amaranth.setGrowthStages(new int[]{1, 2, 2, 2});
        amaranth.setTotalHarvestTime(7);
        amaranth.setOneTime(true);
        amaranth.setRegrowthTime(null);
        amaranth.setSellPrice(150);
        amaranth.setEdible(true);
        amaranth.setBaseEnergy(50);
        amaranth.setSuitableSeasons(List.of("Fall"));
        amaranth.setCanBeGiant(false);
        crops.add(amaranth);

        FruitsAndVegetables artichoke = new FruitsAndVegetables();
        artichoke.setName("Artichoke");
        artichoke.setSource("Artichoke Seeds");
        artichoke.setGrowthStages(new int[]{2, 2, 1, 2, 1});
        artichoke.setTotalHarvestTime(8);
        artichoke.setOneTime(true);
        artichoke.setRegrowthTime(null);
        artichoke.setSellPrice(160);
        artichoke.setEdible(true);
        artichoke.setBaseEnergy(30);
        artichoke.setSuitableSeasons(List.of("Fall"));
        artichoke.setCanBeGiant(false);
        crops.add(artichoke);

        FruitsAndVegetables beet = new FruitsAndVegetables();
        beet.setName("Beet");
        beet.setSource("Beet Seeds");
        beet.setGrowthStages(new int[]{1, 1, 2, 2});
        beet.setTotalHarvestTime(6);
        beet.setOneTime(true);
        beet.setRegrowthTime(null);
        beet.setSellPrice(100);
        beet.setEdible(true);
        beet.setBaseEnergy(30);
        beet.setSuitableSeasons(List.of("Fall"));
        beet.setCanBeGiant(false);
        crops.add(beet);

        FruitsAndVegetables bokChoy = new FruitsAndVegetables();
        bokChoy.setName("Bok Choy");
        bokChoy.setSource("Bok Choy Seeds");
        bokChoy.setGrowthStages(new int[]{1, 1, 1, 1});
        bokChoy.setTotalHarvestTime(4);
        bokChoy.setOneTime(true);
        bokChoy.setRegrowthTime(null);
        bokChoy.setSellPrice(80);
        bokChoy.setEdible(true);
        bokChoy.setBaseEnergy(25);
        bokChoy.setSuitableSeasons(List.of("Fall"));
        bokChoy.setCanBeGiant(false);
        crops.add(bokChoy);

        FruitsAndVegetables broccoli = new FruitsAndVegetables();
        broccoli.setName("Broccoli");
        broccoli.setSource("Broccoli Seeds");
        broccoli.setGrowthStages(new int[]{2, 2, 2, 2});
        broccoli.setTotalHarvestTime(8);
        broccoli.setOneTime(false);
        broccoli.setRegrowthTime(4);
        broccoli.setSellPrice(70);
        broccoli.setEdible(true);
        broccoli.setBaseEnergy(63);
        broccoli.setSuitableSeasons(List.of("Fall"));
        broccoli.setCanBeGiant(false);
        crops.add(broccoli);

        FruitsAndVegetables cranberries = new FruitsAndVegetables();
        cranberries.setName("Cranberries");
        cranberries.setSource("Cranberry Seeds");
        cranberries.setGrowthStages(new int[]{1, 2, 1, 1 ,2});
        cranberries.setTotalHarvestTime(7);
        cranberries.setOneTime(false);
        cranberries.setRegrowthTime(5);
        cranberries.setSellPrice(75);
        cranberries.setEdible(true);
        cranberries.setBaseEnergy(38);
        cranberries.setSuitableSeasons(List.of("Fall"));
        cranberries.setCanBeGiant(false);
        crops.add(cranberries);

        FruitsAndVegetables eggplant = new FruitsAndVegetables();
        eggplant.setName("Eggplant");
        eggplant.setSource("Eggplant Seeds");
        eggplant.setGrowthStages(new int[]{1, 1, 1, 1});
        eggplant.setTotalHarvestTime(4);
        eggplant.setOneTime(false);
        eggplant.setRegrowthTime(5);
        eggplant.setSellPrice(60);
        eggplant.setEdible(true);
        eggplant.setBaseEnergy(20);
        eggplant.setSuitableSeasons(List.of("Fall"));
        eggplant.setCanBeGiant(false);
        crops.add(eggplant);

        FruitsAndVegetables fairyRose = new FruitsAndVegetables();
        fairyRose.setName("Fairy Rose");
        fairyRose.setSource("Fairy Seeds");
        fairyRose.setGrowthStages(new int[]{1, 4, 4, 3});
        fairyRose.setTotalHarvestTime(12);
        fairyRose.setOneTime(true);
        fairyRose.setRegrowthTime(null);
        fairyRose.setSellPrice(290);
        fairyRose.setEdible(true);
        fairyRose.setBaseEnergy(45);
        fairyRose.setSuitableSeasons(List.of("Fall"));
        fairyRose.setCanBeGiant(false);
        crops.add(fairyRose);

        FruitsAndVegetables grape = new FruitsAndVegetables();
        grape.setName("Grape");
        grape.setSource("Grape Starter");
        grape.setGrowthStages(new int[]{1, 1, 2, 3, 3});
        grape.setTotalHarvestTime(10);
        grape.setOneTime(false);
        grape.setRegrowthTime(3);
        grape.setSellPrice(80);
        grape.setEdible(true);
        grape.setBaseEnergy(38);
        grape.setSuitableSeasons(List.of("Fall"));
        grape.setCanBeGiant(false);
        crops.add(grape);

        FruitsAndVegetables pumpkin = new FruitsAndVegetables();
        pumpkin.setName("Pumpkin");
        pumpkin.setSource("Pumpkin Seeds");
        pumpkin.setGrowthStages(new int[]{1, 2, 3, 4, 3});
        pumpkin.setTotalHarvestTime(13);
        pumpkin.setOneTime(true);
        pumpkin.setRegrowthTime(null);
        pumpkin.setSellPrice(320);
        pumpkin.setEdible(false);
        pumpkin.setBaseEnergy(0);
        pumpkin.setSuitableSeasons(List.of("Fall"));
        pumpkin.setCanBeGiant(true);
        crops.add(pumpkin);

        FruitsAndVegetables yam = new FruitsAndVegetables();
        yam.setName("Yam");
        yam.setSource("Yam Seeds");
        yam.setGrowthStages(new int[]{1, 3, 3, 3});
        yam.setTotalHarvestTime(10);
        yam.setOneTime(true);
        yam.setRegrowthTime(null);
        yam.setSellPrice(160);
        yam.setEdible(true);
        yam.setBaseEnergy(45);
        yam.setSuitableSeasons(List.of("Fall"));
        yam.setCanBeGiant(false);
        crops.add(yam);

        FruitsAndVegetables sweetGemBerry = new FruitsAndVegetables();
        sweetGemBerry.setName("Sweet Gem Berry");
        sweetGemBerry.setSource("Rare Seed");
        sweetGemBerry.setGrowthStages(new int[]{2, 4, 6, 6, 6});
        sweetGemBerry.setTotalHarvestTime(24);
        sweetGemBerry.setOneTime(true);
        sweetGemBerry.setRegrowthTime(null);
        sweetGemBerry.setSellPrice(3000);
        sweetGemBerry.setEdible(false);
        sweetGemBerry.setBaseEnergy(0);
        sweetGemBerry.setSuitableSeasons(List.of("Fall"));
        sweetGemBerry.setCanBeGiant(false);
        crops.add(sweetGemBerry);

        FruitsAndVegetables powderMelon = new FruitsAndVegetables();
        powderMelon.setName("Powdermelon");
        powderMelon.setSource("Powdermelon Seeds");
        powderMelon.setGrowthStages(new int[]{1, 2, 1, 2, 1});
        powderMelon.setTotalHarvestTime(7);
        powderMelon.setOneTime(true);
        powderMelon.setRegrowthTime(null);
        powderMelon.setSellPrice(60);
        powderMelon.setEdible(true);
        powderMelon.setBaseEnergy(63);
        powderMelon.setSuitableSeasons(List.of("Winter"));
        powderMelon.setCanBeGiant(true);
        crops.add(powderMelon);

        FruitsAndVegetables ancientFruit = new FruitsAndVegetables();
        ancientFruit.setName("Ancient Fruit");
        ancientFruit.setSource("Ancient Seeds");
        ancientFruit.setGrowthStages(new int[]{2, 7, 7, 7, 5});
        ancientFruit.setTotalHarvestTime(28);
        ancientFruit.setOneTime(false);
        ancientFruit.setRegrowthTime(7);
        ancientFruit.setSellPrice(550);
        ancientFruit.setEdible(false);
        ancientFruit.setBaseEnergy(0);
        ancientFruit.setSuitableSeasons(List.of("Spring","Fall","Summer"));
        ancientFruit.setCanBeGiant(false);
        crops.add(ancientFruit);
    }
    static {
        Seeds blueJazzSeeds = new Seeds();
        blueJazzSeeds.setName("Jazz Seeds");
        blueJazzSeeds.setGrowsInto("Blue Jazz");
        blueJazzSeeds.setSuitableSeasons(List.of("Spring"));
        blueJazzSeeds.setTotalHarvestTime(7);
        seeds.add(blueJazzSeeds);

        Seeds carrotSeeds = new Seeds();
        carrotSeeds.setName("Carrot Seeds");
        carrotSeeds.setGrowsInto("Carrot");
        carrotSeeds.setSuitableSeasons(List.of("Spring"));
        carrotSeeds.setTotalHarvestTime(3);
        seeds.add(carrotSeeds);

        Seeds cauliflowerSeeds = new Seeds();
        cauliflowerSeeds.setName("Cauliflower Seeds");
        cauliflowerSeeds.setGrowsInto("Cauliflower");
        cauliflowerSeeds.setSuitableSeasons(List.of("Spring"));
        cauliflowerSeeds.setTotalHarvestTime(12);
        seeds.add(cauliflowerSeeds);

        Seeds coffeeBeans = new Seeds();
        coffeeBeans.setName("Coffee Bean");
        coffeeBeans.setGrowsInto("Coffee Bean");
        coffeeBeans.setSuitableSeasons(List.of("Spring","Summer"));
        coffeeBeans.setTotalHarvestTime(10);
        seeds.add(coffeeBeans);

        Seeds garlicSeeds = new Seeds();
        garlicSeeds.setName("Garlic Seeds");
        garlicSeeds.setGrowsInto("Garlic");
        garlicSeeds.setSuitableSeasons(List.of("Spring"));
        garlicSeeds.setTotalHarvestTime(4);
        seeds.add(garlicSeeds);

        Seeds beanStarter = new Seeds();
        beanStarter.setName("Bean Starter");
        beanStarter.setGrowsInto("Green Bean");
        beanStarter.setSuitableSeasons(List.of("Spring"));
        beanStarter.setTotalHarvestTime(10);
        seeds.add(beanStarter);

        Seeds kaleSeeds = new Seeds();
        kaleSeeds.setName("Kale Seeds");
        kaleSeeds.setGrowsInto("Kale");
        kaleSeeds.setSuitableSeasons(List.of("Spring"));
        kaleSeeds.setTotalHarvestTime(6);
        seeds.add(kaleSeeds);

        Seeds parsnipSeeds = new Seeds();
        parsnipSeeds.setName("Parsnip Seeds");
        parsnipSeeds.setGrowsInto("Parsnip");
        parsnipSeeds.setSuitableSeasons(List.of("Spring"));
        parsnipSeeds.setTotalHarvestTime(4);
        seeds.add(parsnipSeeds);

        Seeds potatoSeeds = new Seeds();
        potatoSeeds.setName("Potato Seeds");
        potatoSeeds.setGrowsInto("Potato");
        potatoSeeds.setSuitableSeasons(List.of("Spring"));
        potatoSeeds.setTotalHarvestTime(6);
        seeds.add(potatoSeeds);

        Seeds rhubarbSeeds = new Seeds();
        rhubarbSeeds.setName("Rhubarb Seeds");
        rhubarbSeeds.setGrowsInto("Rhubarb");
        rhubarbSeeds.setSuitableSeasons(List.of("Spring"));
        rhubarbSeeds.setTotalHarvestTime(13);
        seeds.add(rhubarbSeeds);

        Seeds strawberrySeeds = new Seeds();
        strawberrySeeds.setName("Strawberry Seeds");
        strawberrySeeds.setGrowsInto("Strawberry");
        strawberrySeeds.setSuitableSeasons(List.of("Spring"));
        strawberrySeeds.setTotalHarvestTime(8);
        seeds.add(strawberrySeeds);

        Seeds tulipBulb = new Seeds();
        tulipBulb.setName("Tulip Bulb");
        tulipBulb.setGrowsInto("Tulip");
        tulipBulb.setSuitableSeasons(List.of("Spring"));
        tulipBulb.setTotalHarvestTime(6);
        seeds.add(tulipBulb);

        Seeds riceShoot = new Seeds();
        riceShoot.setName("Rice Shoot");
        riceShoot.setGrowsInto("Unmilled Rice");
        riceShoot.setSuitableSeasons(List.of("Spring"));
        riceShoot.setTotalHarvestTime(8);
        seeds.add(riceShoot);

        Seeds blueberrySeeds = new Seeds();
        blueberrySeeds.setName("Blueberry Seeds");
        blueberrySeeds.setGrowsInto("Blueberry");
        blueberrySeeds.setSuitableSeasons(List.of("Summer"));
        blueberrySeeds.setTotalHarvestTime(13);
        seeds.add(blueberrySeeds);

        Seeds cornSeeds = new Seeds();
        cornSeeds.setName("Corn Seeds");
        cornSeeds.setGrowsInto("Corn");
        cornSeeds.setSuitableSeasons(List.of("Summer","Fall"));
        cornSeeds.setTotalHarvestTime(14);
        seeds.add(cornSeeds);

        Seeds hopsStarter = new Seeds();
        hopsStarter.setName("Hops Starter");
        hopsStarter.setGrowsInto("Hops");
        hopsStarter.setSuitableSeasons(List.of("Summer"));
        hopsStarter.setTotalHarvestTime(11);
        seeds.add(hopsStarter);

        Seeds pepperSeeds = new Seeds();
        pepperSeeds.setName("Pepper Seeds");
        pepperSeeds.setGrowsInto("Hot Pepper");
        pepperSeeds.setSuitableSeasons(List.of("Summer"));
        pepperSeeds.setTotalHarvestTime(5);
        seeds.add(pepperSeeds);

        Seeds melonSeeds = new Seeds();
        melonSeeds.setName("Melon Seeds");
        melonSeeds.setGrowsInto("Melon");
        melonSeeds.setSuitableSeasons(List.of("Summer"));
        melonSeeds.setTotalHarvestTime(12);
        seeds.add(melonSeeds);

        Seeds poppySeeds = new Seeds();
        poppySeeds.setName("Poppy Seeds");
        poppySeeds.setGrowsInto("Poppy");
        poppySeeds.setSuitableSeasons(List.of("Summer"));
        poppySeeds.setTotalHarvestTime(7);
        seeds.add(poppySeeds);

        Seeds radishSeeds = new Seeds();
        radishSeeds.setName("Radish Seeds");
        radishSeeds.setGrowsInto("Radish");
        radishSeeds.setSuitableSeasons(List.of("Summer"));
        radishSeeds.setTotalHarvestTime(6);
        seeds.add(radishSeeds);

        Seeds redCabbageSeeds = new Seeds();
        redCabbageSeeds.setName("Red Cabbage Seeds");
        redCabbageSeeds.setGrowsInto("Red Cabbage");
        redCabbageSeeds.setSuitableSeasons(List.of("Summer"));
        redCabbageSeeds.setTotalHarvestTime(9);
        seeds.add(redCabbageSeeds);

        Seeds starfruitSeeds = new Seeds();
        starfruitSeeds.setName("Starfruit Seeds");
        starfruitSeeds.setGrowsInto("Starfruit");
        starfruitSeeds.setSuitableSeasons(List.of("Summer"));
        starfruitSeeds.setTotalHarvestTime(13);
        seeds.add(starfruitSeeds);

        Seeds spangleSeeds = new Seeds();
        spangleSeeds.setName("Spangle Seeds");
        spangleSeeds.setGrowsInto("Summer Spangle");
        spangleSeeds.setSuitableSeasons(List.of("Summer"));
        spangleSeeds.setTotalHarvestTime(8);
        seeds.add(spangleSeeds);

        Seeds summerSquashSeeds = new Seeds();
        summerSquashSeeds.setName("Summer Squash Seeds");
        summerSquashSeeds.setGrowsInto("Summer Squash");
        summerSquashSeeds.setSuitableSeasons(List.of("Summer"));
        summerSquashSeeds.setTotalHarvestTime(6);
        seeds.add(summerSquashSeeds);

        Seeds sunflowerSeeds = new Seeds();
        sunflowerSeeds.setName("Sunflower Seeds");
        sunflowerSeeds.setGrowsInto("Sunflower");
        sunflowerSeeds.setSuitableSeasons(List.of("Summer","Fall"));
        sunflowerSeeds.setTotalHarvestTime(8);
        seeds.add(sunflowerSeeds);

        Seeds tomatoSeeds = new Seeds();
        tomatoSeeds.setName("Tomato Seeds");
        tomatoSeeds.setGrowsInto("Tomato");
        tomatoSeeds.setSuitableSeasons(List.of("Summer"));
        tomatoSeeds.setTotalHarvestTime(11);
        seeds.add(tomatoSeeds);

        Seeds wheatSeeds = new Seeds();
        wheatSeeds.setName("Wheat Seeds");
        wheatSeeds.setGrowsInto("Wheat");
        wheatSeeds.setSuitableSeasons(List.of("Summer","Fall"));
        wheatSeeds.setTotalHarvestTime(4);
        seeds.add(wheatSeeds);

        Seeds amaranthSeeds = new Seeds();
        amaranthSeeds.setName("Amaranth Seeds");
        amaranthSeeds.setGrowsInto("Amaranth");
        amaranthSeeds.setSuitableSeasons(List.of("Fall"));
        amaranthSeeds.setTotalHarvestTime(7);
        seeds.add(amaranthSeeds);

        Seeds artichokeSeeds = new Seeds();
        artichokeSeeds.setName("Artichoke Seeds");
        artichokeSeeds.setGrowsInto("Artichoke");
        artichokeSeeds.setSuitableSeasons(List.of("Fall"));
        artichokeSeeds.setTotalHarvestTime(8);
        seeds.add(artichokeSeeds);

        Seeds beetSeeds = new Seeds();
        beetSeeds.setName("Beet Seeds");
        beetSeeds.setGrowsInto("Beet");
        beetSeeds.setSuitableSeasons(List.of("Fall"));
        beetSeeds.setTotalHarvestTime(6);
        seeds.add(beetSeeds);

        Seeds bokChoySeeds = new Seeds();
        bokChoySeeds.setName("Bok Choy Seeds");
        bokChoySeeds.setGrowsInto("Bok Choy");
        bokChoySeeds.setSuitableSeasons(List.of("Fall"));
        bokChoySeeds.setTotalHarvestTime(4);
        seeds.add(bokChoySeeds);

        Seeds broccoliSeeds = new Seeds();
        broccoliSeeds.setName("Broccoli Seeds");
        broccoliSeeds.setGrowsInto("Broccoli");
        broccoliSeeds.setSuitableSeasons(List.of("Fall"));
        broccoliSeeds.setTotalHarvestTime(8);
        seeds.add(broccoliSeeds);

        Seeds cranberrySeeds = new Seeds();
        cranberrySeeds.setName("Cranberry Seeds");
        cranberrySeeds.setGrowsInto("Cranberries");
        cranberrySeeds.setSuitableSeasons(List.of("Fall"));
        cranberrySeeds.setTotalHarvestTime(7);
        seeds.add(cranberrySeeds);

        Seeds eggplantSeeds = new Seeds();
        eggplantSeeds.setName("Eggplant Seeds");
        eggplantSeeds.setGrowsInto("Eggplant");
        eggplantSeeds.setSuitableSeasons(List.of("Fall"));
        eggplantSeeds.setTotalHarvestTime(5);
        seeds.add(eggplantSeeds);

        Seeds fairySeeds = new Seeds();
        fairySeeds.setName("Fairy Seeds");
        fairySeeds.setGrowsInto("Fairy Rose");
        fairySeeds.setSuitableSeasons(List.of("Fall"));
        fairySeeds.setTotalHarvestTime(12);
        seeds.add(fairySeeds);

        Seeds grapeStarter = new Seeds();
        grapeStarter.setName("Grape Starter");
        grapeStarter.setGrowsInto("Grape");
        grapeStarter.setSuitableSeasons(List.of("Fall"));
        grapeStarter.setTotalHarvestTime(10);
        seeds.add(grapeStarter);

        Seeds pumpkinSeeds = new Seeds();
        pumpkinSeeds.setName("Pumpkin Seeds");
        pumpkinSeeds.setGrowsInto("Pumpkin");
        pumpkinSeeds.setSuitableSeasons(List.of("Fall"));
        pumpkinSeeds.setTotalHarvestTime(13);
        seeds.add(pumpkinSeeds);

        Seeds yamSeeds = new Seeds();
        yamSeeds.setName("Yam Seeds");
        yamSeeds.setGrowsInto("Yam");
        yamSeeds.setSuitableSeasons(List.of("Fall"));
        yamSeeds.setTotalHarvestTime(10);
        seeds.add(yamSeeds);

        Seeds rareSeed = new Seeds();
        rareSeed.setName("Rare Seed");
        rareSeed.setGrowsInto("Sweet Gem Berry");
        rareSeed.setSuitableSeasons(List.of("Fall"));
        rareSeed.setTotalHarvestTime(24);
        seeds.add(rareSeed);

        Seeds powermelonSeeds = new Seeds();
        powermelonSeeds.setName("Powdermelon Seeds");
        powermelonSeeds.setGrowsInto("Powdermelon");
        powermelonSeeds.setSuitableSeasons(List.of("Winter"));
        powermelonSeeds.setTotalHarvestTime(7);
        seeds.add(powermelonSeeds);

        Seeds ancientSeeds = new Seeds();
        ancientSeeds.setName("Ancient Seeds");
        ancientSeeds.setGrowsInto("Ancient Fruit");
        ancientSeeds.setSuitableSeasons(List.of("Spring","Summer","Fall"));
        ancientSeeds.setTotalHarvestTime(28);
        seeds.add(ancientSeeds);

        Seeds mixedSeeds = new Seeds();
        mixedSeeds.setName("Mixed Seeds");
        mixedSeeds.setGrowsInto("Mixed");
        mixedSeeds.setSuitableSeasons(List.of("Spring", "Summer", "Fall", "Winter"));
        mixedSeeds.setTotalHarvestTime(0);
        seeds.add(mixedSeeds);
    }
}
