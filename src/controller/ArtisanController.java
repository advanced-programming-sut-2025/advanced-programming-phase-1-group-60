//package controller;
////
////import org.example.Enums.ItemConsts.ItemAttributes;
////import org.example.Enums.ItemConsts.ItemIDs;
////import org.example.Models.App;
////import org.example.Models.Item.Inventory;
////import org.example.Models.Item.ItemDefinition;
////import org.example.Models.Item.ItemInstance;
////import org.example.Models.Player.Player;
////import org.example.Views.InGameMenus.ActionMenuView;
//
//import models.Inventory;
//
//import java.util.Objects;
//
//public class ArtisanController {
//    static ActionMenuView view = new ActionMenuView();
//
//    public static void beeHouse(String itemName, String ingredient, Player player) {
//        if (itemName.equals("honey")) {
//            ItemInstance honey = new ItemInstance(Objects.requireNonNull(App.getItemDefinition("honey")));
//            player.getInventory().setArtisan(honey);
//        } else {
//            view.showMessage("please enter a correct item or ingredient!");
//        }
//    }
//
//    public static void cheesePress(String itemName, String ingredient, Player player) {
//        switch (itemName) {
//            case "cheese":
//                if (ingredient.equalsIgnoreCase("milk")) {
//                    addArtisanToInventory(player.getInventory(), "milk",
//                            "cheese", ItemIDs.milk, 230);
//                } else if (ingredient.equalsIgnoreCase("large_milk")) {
//                    addArtisanToInventory(player.getInventory(), "large_milk",
//                            "cheese", ItemIDs.large_milk, 345);
//                }
//                break;
//            case "goat_cheese":
//                if (ingredient.equalsIgnoreCase("goat_milk")) {
//                    addArtisanToInventory(player.getInventory(), "goat_milk",
//                            "goat_cheese", ItemIDs.goat_milk, 400);
//                } else if (ingredient.equalsIgnoreCase("large_goat_milk")) {
//                    addArtisanToInventory(player.getInventory(), "large_goat_milk",
//                            "goat_cheese", ItemIDs.large_goat_milk, 600);
//                }
//                break;
//            default:
//                view.showMessage("please enter a correct item or ingredient!");
//                break;
//        }
//    }
//
//    public static void addArtisanToInventory(Inventory inventory, String ingredientId,
//                                             String artisanId, ItemIDs ingredient, int price) {
//        if (isInInventory(inventory, Objects.requireNonNull(App.getItemDefinition(ingredientId)))) return;
//        ItemInstance item = new ItemInstance(Objects.requireNonNull(App.getItemDefinition(artisanId)));
//        inventory.setArtisan(item);
//        inventory.trashItem(ingredient, 1);
//        item.setAttribute(ItemAttributes.price, price);
//    }
//
//    public static boolean isInInventory(Inventory inventory, ItemDefinition item) {
//        if (inventory.getItems().containsKey(item.getId())) {
//            view.showMessage("You don't have enough " + item.getDisplayName() + "!");
//            return false;
//        }
//        return true;
//    }
//}