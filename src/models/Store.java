package models;

import java.util.List;
import java.time.LocalTime;

public class Store {
    private String type;
    private Npc creator;
    private WorkTime workTime;
    private List<Product> products;
    private int upgradeLevel;

    public boolean isOpen(LocalTime currentTime);

    public boolean purchaseProduct(Player player, Product product, int quantity);


    public boolean sellProduct(Player player, Product product, int quantity);

    public boolean upgradeStore(Player player);

    public boolean hasProductInStock(Product product, int quantity);

    public void addProduct(Product product);

    public void removeProduct(Product product);

    public String getType();
    public Npc getCreator();
    public WorkTime getWorkTime();
    public List<Product> getAvailableProducts();
    public int getUpgradeLevel();
}

// ساعات کاری
//public class WorkTime {
//    private LocalTime openTime;
//    private LocalTime closeTime;
//    private boolean[] workingDays; // روزهای فعال در هفته
//
//    // متد بررسی باز بودن در زمان مشخص
//    public boolean isOpen(LocalTime time, DayOfWeek day);
//}