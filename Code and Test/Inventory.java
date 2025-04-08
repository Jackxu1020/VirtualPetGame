import java.util.ArrayList;
import java.util.List;

/**
 * Inventory System for Virtual Pet Game.
 * 
 * It represent and manage inventory items used within the virtual pet game.
 * Items are categorized into types like FoodItem and GiftItem, and can affect pet attributes
 * such as fullness and happiness.
 * 
 * @Author: Jinke Li
 */

/**
 * Abstract class representing a generic item in the inventory.
 */
abstract class InventoryItem {
    private String name;
    private int quantity;
    private final String type;

    /**
     * Constructor for InventoryItem.
     * @param name Name of the item.
     * @param quantity Initial quantity of the item.
     */
    public InventoryItem(String name, int quantity, String type) {
        this.name = name;
        this.quantity = quantity;
        this.type = type;
    }

    /**
     * Gets the name of the item.
     * @return Name of the item.
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the quantity of the item.
     * @return Quantity of the item.
     */
    public int getQuantity() {
        return quantity;
    }

    /**
     * Sets the quantity of the item.
     * @param quantity New quantity to set.
     */
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    /**
     * Defines the action taken when the item is used.
     */
    public abstract void use(VirtualPet pet);
    
    /**
     * Provides a description of the item's effect.
     */
    public abstract String getEffectDescription();
}

/**
 * Class managing the collection of items in the player's inventory.
 */
public class Inventory {
    private List<InventoryItem> items;

    /**
     * Constructor initializes an empty inventory.
     */
    public Inventory() {
        items = new ArrayList<>();
    }

    /**
     * Adds an item to the inventory. If the item exists, increases quantity.
     * @param item Item to add.
     */
    public void addItem(InventoryItem item) {
        for (InventoryItem i : items) {
            if (i.getName().equals(item.getName())) {
                i.setQuantity(i.getQuantity() + item.getQuantity());
                return;
            }
        }
        items.add(item);
    }

    /**
     * Removes a certain quantity of an item from the inventory by its name.
     * If the quantity after removal is greater than zero, the item's quantity is reduced.
     * If the quantity is zero or less, the item is removed from the inventory.
     *
     * @param name  the name of the item to remove
     * @param count the quantity to remove
     */
    public void removeItemByName(String name, int count) {
        for (InventoryItem i : items) {
            if (i.getName().equals(name)) {
                if (i.getQuantity() > count) {
                    i.setQuantity(i.getQuantity() - count);
                } else {
                    items.remove(i);
                }
                break;
            }
        }
    }

    /**
     * Returns a formatted string listing all inventory items and their quantities, or null if inventory is empty.
     * @return String listing all inventory items with name and quantity.
     */
    public String getInventoryContents() {
        if (items.isEmpty()) {
            return null;
        }

        StringBuilder sb = new StringBuilder("Inventory Contents:\n");
        for (InventoryItem item : items) {
            sb.append(String.format("- %s (x%d)\n", item.getName(), item.getQuantity()));
        }
        return sb.toString();
    }
    
    /**
     * Retrieves an item by its name.
     * @param itemName Name of the item.
     * @return InventoryItem object or null if not found.
     */
    public InventoryItem getItemByName(String itemName) {
        for (InventoryItem item : items) {
            if (item.getName().equals(itemName)) {
                return item;
            }
        }
        return null;
    }

    /**
     * Gets the quantity of a specific item.
     * @param itemName Name of the item.
     * @return Quantity of the specified item.
     */
    public int getItemCount(String itemName) {
        InventoryItem item = getItemByName(itemName);
        return (item != null) ? item.getQuantity() : 0;
    }

    /**
     * Returns a list of all items in the inventory.
     * @return List of InventoryItem objects.
     */
    public List<InventoryItem> getItems() {
        return new ArrayList<>(items);
    }
}

/**
 * Class representing gift items that increase pet happiness.
 */
class GiftItem extends InventoryItem {
    private int happinessIncrease;

    /**
     * Constructor for GiftItem.
     * @param name Name of the gift item.
     * @param quantity Quantity of the gift item.
     * @param happinessIncrease Amount of happiness the gift provides.
     */
    public GiftItem(String name, int quantity, int happinessIncrease) {
        super(name, quantity, "gift");
        this.happinessIncrease = happinessIncrease;
    }

    /**
     * Gets the amount of happiness this gift provides.
     * @return Happiness increase value.
     */
    public int getHappinessValue() {
        return happinessIncrease;
    }

    /**
     * Uses the gift item, increasing pet happiness.
     */
    @Override
    public void use(VirtualPet pet ) {
        pet.giveGift(this);
    }
    
    /**
     * Provides a description of the gift item's effect.
     * @return String describing the happiness increase.
     */
    @Override
    public String getEffectDescription() {
        return "+" + happinessIncrease + " happiness";
    }
}

/**
 * Class representing food items that increase pet fullness.
 */
class FoodItem extends InventoryItem {
    private int fullnessIncrease;

    /**
     * Constructor for FoodItem.
     * @param name Name of the food item.
     * @param quantity Quantity of the food item.
     * @param fullnessIncrease Amount of fullness the food provides.
     */
    public FoodItem(String name, int quantity, int fullnessIncrease) {
        super(name, quantity, "food");
        this.fullnessIncrease = fullnessIncrease;
    }

    /**
     * Gets the amount of fullness this food provides.
     * @return Fullness increase value.
     */
    public int getFoodValue() {
    	return fullnessIncrease;
    }

    /**
     * Uses the food item, increasing pet fullness.
     */
    @Override
    public void use(VirtualPet pet) {
        pet.feed(this);
    }
    
    /**
     * Provides a description of the food item's effect.
     * @return String describing the fullness increase.
     */
    @Override
    public String getEffectDescription() {
        return "+" + fullnessIncrease + " fullness";
    }
}
