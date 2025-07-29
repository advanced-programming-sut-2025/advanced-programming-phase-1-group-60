package com.StardewValley.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    public void setActiveFarm(int farmIndex) {
        if (farmIndex >= 0 && farmIndex < farms.length) {
            this.activeFarmIndex = farmIndex;
        } else {
            System.err.println("Invalid farm index: " + farmIndex);
        }
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

        // بررسی اینکه آیا ایندکس مزرعه معتبر است یا خیر
        if (idx >= farms.length) {
            return null; // اگر مزرعه‌ای در این ربع وجود ندارد، فضای خالی است
        }
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
    public Farm getFarm(int farmIndex) {
        if (farmIndex >= 0 && farmIndex < farms.length) {
            return farms[farmIndex];
        }
        return null;
    }
    public Village getVillage() { return village; }
    public Farm getActiveFarm() { return farms[activeFarmIndex]; }
    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public boolean isCellEmpty(int x, int y) {
        Tile tile = getTile(x, y);
        if (tile == null) {
            return false; // اگر خارج از محدوده باشد سلول خالی نیست
        }
        // اگر هیچ StaticElement یا RandomElement در سلول نباشد، خالی است
        return !(tile.getStaticElement().isPresent() || tile.getRandomElement().isPresent());
    }

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
    // Add this new method:
    public boolean placeBuilding(StaticElement building, int x, int y) {
        Tile targetTile = getTile(x, y);

        // Ensure the tile exists and is available for building
        // (using the new method from Tile class)
        if (targetTile != null && targetTile.isAvailableForBuilding()) {
            // Set the static element (which will be a Building) on the tile
            targetTile.setStaticElement(building);

            // Assuming building has setX/setY methods if it's a direct StaticElement
            // If your Buildings have position, ensure they are set here.
            // Example: If 'building' is a 'Building' instance (subclass of StaticElement)
            // ((Building)building).setX(x);
            // ((Building)building).setY(y);

            // Also update the tile's occupied status explicitly.
            // setStaticElement might internally change passable, but isOccupied is separate.
            targetTile.setOccupied(true);

            System.out.println("Placed " + building.getClass().getSimpleName() + " at (" + x + "," + y + ")");
            return true;
        }
        System.out.println("Failed to place building at (" + x + "," + y + "). Tile might not be available.");
        return false;
    }
    public int getVilX() { return vilX; }
    public int getVilY() { return vilY; }
    public int getVilW() { return vilW; }
    public int getVilH() { return vilH; }
    private static final Map<String, Character> itemToCharMap = new HashMap<>();

    static {
        // آیتم‌های اصلی
        itemToCharMap.put("Loom", 'L');
        itemToCharMap.put("Bee_House", 'B');
        itemToCharMap.put("Scarecrow", 'S');
        itemToCharMap.put("Deluxe_Scarecrow", 'D');
        itemToCharMap.put("Charcoal_Kiln", 'C');
        itemToCharMap.put("Furnace", 'F');
        itemToCharMap.put("Sprinkler", 'P'); // P از کلمه "Pipe" برای نمایش اسپری استفاده شده
        itemToCharMap.put("Quality_Sprinkler", 'Q');
        itemToCharMap.put("Iridium_Sprinkler", 'I');
        itemToCharMap.put("Mayonnaise_Machine", 'M');
        itemToCharMap.put("Oil_Maker", 'O');
        itemToCharMap.put("Preserves_Jar", 'J');
        itemToCharMap.put("Dehydrator", 'H'); // H از کلمه "Hydration" برای نمایش دیهیدراتور
        itemToCharMap.put("Grass_Starter", 'G');
        itemToCharMap.put("Fish_Smoker", 'K'); // K از "Kiln" برای نمایش اسموکر
        itemToCharMap.put("Cheese_Press", 'E'); // E از "Cheese" برای نمایش
        itemToCharMap.put("Mega_Bomb", 'X');
        itemToCharMap.put("Bomb", 'B');
        itemToCharMap.put("Cherry_Bomb", 'C');

        // آیتم‌های خاص
        itemToCharMap.put("Mystic_Tree_Seed", 'T'); // T برای درخت جادویی
        itemToCharMap.put("Pickles", 'P'); // برای آیتم‌های خاص مثل ترشی، اینجا از P استفاده شده، اما می‌توانید تغییر دهید
        itemToCharMap.put("Jelly", 'J');
        itemToCharMap.put("Wine", 'W');

        // اضافه کردن آیتم‌های دیگر...
    }
}
