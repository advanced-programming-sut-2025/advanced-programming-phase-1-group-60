// models/GameMap.java
package models;

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


    public boolean isCellEmpty(int x, int y) {
        Tile tile = getTile(x, y);
        if (tile == null) {
            return false; // اگر خارج از محدوده باشد سلول خالی نیست
        }
        // اگر هیچ StaticElement یا RandomElement در سلول نباشد، خالی است
        return !(tile.getStaticElement().isPresent() || tile.getRandomElement().isPresent());
    }

    public void placeItemOnMap(String itemName, int x, int y) {
        Tile tile = getTile(x, y);
        if (tile == null) {
            throw new IllegalArgumentException("Position is out of bounds: (" + x + ", " + y + ")");
        }
        StaticElement staticElement = new ItemStaticElement(itemName.charAt(0), false); // نماد آیتم
        tile.setStaticElement(staticElement);
    }

    public void printRegion(int startX, int startY, int width, int height) {
        for (int y = startY; y < startY + height; y++) {
            for (int x = startX; x < startX + width; x++) {
                Tile t = getTile(x, y);
                char c;
                if (t == null) {
                    // بیرون از محدودهٔ فعالِ فارم
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
                System.out.print(c);
            }
            System.out.println();
        }
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
