import java.util.Map;

public class PlayerInventory {
    private Map<String, Integer> foodItems;
    private Map<String, Integer> giftItems;

    public Map<String, Integer> getFoodItems() { return foodItems; }
    public void setFoodItems(Map<String, Integer> foodItems) { this.foodItems = foodItems; }

    public Map<String, Integer> getGiftItems() { return giftItems; }
    public void setGiftItems(Map<String, Integer> giftItems) { this.giftItems = giftItems; }
}
