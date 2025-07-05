package com.StardewValley.models;

import java.util.*;
public class FarmTemplate {
    public static final int WIDTH = 50, HEIGHT = 50;
    public static class Placement { public final StaticElement element; public final int x,y,w,h;
        public Placement(StaticElement e,int x,int y,int w,int h){this.element=e;this.x=x;this.y=y;this.w=w;this.h=h;}
    }
    private final List<Placement> placements;
    public FarmTemplate(List<Placement> p){ this.placements = p; }

    // چهار تمپلیت مختلف (مکان متفاوت المان‌های ثابت)
    public static FarmTemplate template1() {
        return new FarmTemplate(List.of(
                new Placement(new Cabin(),      2, 2, 4,4),
                new Placement(new Greenhouse(), 10,3,5,6),
                new Placement(new Lake(),       20,5,6,4),
                new Placement(new Quarry(),     30,30,8,5)
        ));
    }
    public static FarmTemplate template2() {
        return new FarmTemplate(List.of(
                new Placement(new Cabin(),      5, 5,4,4),
                new Placement(new Greenhouse(), 15,2,5,6),
                new Placement(new Lake(),       2,20,6,4),
                new Placement(new Quarry(),     35,25,10,6)
        ));
    }
    public static FarmTemplate template3() {
        return new FarmTemplate(List.of(
                new Placement(new Cabin(),      3, 40,4,4),
                new Placement(new Greenhouse(), 20,10,5,6),
                new Placement(new Lake(),       10,25,8,5),
                new Placement(new Quarry(),     25,35,8,5)
        ));
    }
    public static FarmTemplate template4() {
        return new FarmTemplate(List.of(
                new Placement(new Cabin(),      40,2,4,4),
                new Placement(new Greenhouse(), 30,15,5,6),
                new Placement(new Lake(),       45,30,6,4),
                new Placement(new Quarry(),     5,30,12,8)
        ));
    }
    public List<Placement> getPlacements() { return placements; }
}
