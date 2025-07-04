// models/GameMap.java
package models;

import java.util.List;
import java.util.Optional;

/**
 * World map with four farms around a central village.
 */
public class GameMap {
    private final int width;
    private final int height;
    private final Farm[] farms;
    private final Village village;
    private int activeFarmIndex;

    private final int farmW = FarmTemplate.WIDTH;
    private final int farmH = FarmTemplate.HEIGHT;
    private final int vilW;
    private final int vilH;
    private final int vilX;
    private final int vilY;

    public GameMap(List<Farm> farmList, VillageTemplate villageTpl) {
        this.farms = farmList.toArray(new Farm[0]);
        this.village = new Village(villageTpl);
        this.vilW = village.getWidth();
        this.vilH = village.getHeight();
        this.width  = farmW * 2 + vilW;
        this.height = farmH * 2 + vilH;
        this.vilX = farmW;
        this.vilY = farmH;
        this.activeFarmIndex = 0;
    }

    public void setActiveFarm(int index) {
        this.activeFarmIndex = index;
    }

    public Tile getTile(int x, int y) {
        // ۱) آیا داخل محدودهٔ روستا هست؟
        if (x >= vilX && x < vilX + vilW
                && y >= vilY && y < vilY + vilH) {
            return village.getTile(x - vilX, y - vilY);
        }

        // ۲) تعیین ربع: fx=۰ یا ۱، fy=۰ یا ۱
        int fx = (x < vilX) ? 0 : 1;
        int fy = (y < vilY) ? 0 : 1;
        int idx = fy * 2 + fx;
        Farm farm = farms[idx];

        // ۳) مختصات محلی داخل هر فارم
        int localX = x - fx * (farmW + vilW);
        int localY = y - fy * (farmH + vilH);

        // ۴) اگر داخل بازهٔ [۰..عرض فارم) و [۰..ارتفاع فارم) بود، تایل را برگردان
        if (localX >= 0 && localX < farmW
                && localY >= 0 && localY < farmH) {
            return farm.getTile(localX, localY);
        }

        // ۵) در غیر این صورت فضای خالی‌ست
        return null;
    }

    public MapCoord getEntrance(int farmIndex) {
        switch(farmIndex) {
            case 0: return new MapCoord(vilX, vilY+vilH-1);
            case 1: return new MapCoord(vilX+vilW-1, vilY+vilH-1);
            case 2: return new MapCoord(vilX, vilY);
            case 3: return new MapCoord(vilX+vilW-1, vilY);
            default: throw new IllegalArgumentException("Invalid farm index");
        }
    }

    public Village getVillage() { return village; }
    public Farm getActiveFarm() { return farms[activeFarmIndex]; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }


    public static final String RESET        = "\u001B[0m";
    public static final String ORANGE       = "\u001B[38;5;208m"; // نارنجی (256-color)
    public static final String LIGHT_GREEN  = "\u001B[92m";       // سبز کمرنگ
    public static final String GREEN        = "\u001B[32m";       // سبز
    public static final String GRAY         = "\u001B[90m";       // طوسی
    public static final String YELLOW       = "\u001B[33m";       // زرد
    public static final String BLUE         = "\u001B[34m";       // آبی
    public static final String PURPLE       = "\u001B[35m";       // بنفش
    public static final String DEFAULT_COLOR= RESET;              // پیش‌فرض

    public void printRegion(int startX, int startY, int width, int height) {
        for (int y = startY; y < startY + height; y++) {
            for (int x = startX; x < startX + width; x++) {
                Tile t = getTile(x, y);
                char c;
                String color = DEFAULT_COLOR;

                if (t == null) {
                    c = ' ';
                } else {
                    Optional<StaticElement> se = t.getStaticElement();
                    Optional<RandomElement> re = t.getRandomElement();

                    if (se.isPresent()) {
                        c = se.get().symbol();
                    }
                    else if (re.isPresent()) {
                        c = re.get().symbol();
                    }
                    else {
                        c = '.';
                    }
                }

                // تعیین رنگ بر اساس سمبل
                switch (c) {
                    case 'C': color = ORANGE;      break;
                    case 'G': color = LIGHT_GREEN; break;
                    case 'T': color = GREEN;       break;
                    case 'S': color = GRAY;        break;
                    case 'F': color = YELLOW;      break;
                    case 'L': color = BLUE;        break;
                    case 'Q': color = PURPLE;      break;
                    case '.': color = YELLOW;      break;
                    default:  color = DEFAULT_COLOR;
                }

                System.out.print(color + c + RESET);
            }
            System.out.println();
        }
    }

    public int getVilX() { return vilX; }
    public int getVilY() { return vilY; }
    public int getVilW() { return vilW; }
    public int getVilH() { return vilH; }

}
