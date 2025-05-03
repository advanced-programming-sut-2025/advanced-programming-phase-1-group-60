package repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import models.Item;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ItemRepository {
    private static final Map<Integer, Item> itemsById = new HashMap<>();

    static {
        try {
            ObjectMapper mapper = new ObjectMapper();
            File file = new File("advanced-programming-phase-1-group-60/src/repository/items.JSON");
            if (file.exists()) {
                List<Item> items = mapper.readValue(file, new TypeReference<List<Item>>() {});
                for (Item item : items) {
                    itemsById.put(item.getId(), item);
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to load items from JSON: " + e.getMessage());
        }
    }

    public static Item getItemById(int id) {
        return itemsById.get(id);
    }
}