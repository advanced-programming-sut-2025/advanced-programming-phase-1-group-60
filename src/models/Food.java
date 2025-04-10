package models;

public class Food extends Item{
    private String name;
    private String description;
    private int price;
    private Map<String, Integer> ingredients;
    private int energy;
    private String source;


    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void setIngredients(Map<String, Integer> ingredients) {
        this.ingredients = ingredients;
    }

    public void setEnergy(int energy) {
        this.energy = energy;
    }

    public String getSource() {
        return source;
    }

    public void consume() {}


    public boolean canBeCooked(Inventory inventory) { return false; }


    public void addIngredient(String itemName, int count) {}


    public void removeIngredient(String itemName) {}




    public void applyBuff(User player) {}


    public boolean isBuffActive() { return false; }


    public String getName() { return ""; }
    public int getEnergy() { return 0; }
    public Map<String, Integer> getIngredients() { }
    // public BuffEffect getBuff() { }
    public void setSource(String source) {}
}
}
