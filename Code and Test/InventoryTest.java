import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

public class InventoryTest {

    private Inventory inventory;
    private FoodItem apple;
    private GiftItem ball;
    private VirtualPet pet;

    @BeforeEach
    void setUp() {
        inventory = new Inventory();
        apple = new FoodItem("Apple", 5, 15);
        ball = new GiftItem("Ball", 3, 20);

        inventory.addItem(apple);
        inventory.addItem(ball);

        pet = new VirtualPet("mimi", PetType.DOG);
        pet.setFullness(50);
        pet.setHappiness(50);
    }

    @Test
    void testAddItem() {
        inventory.addItem(new FoodItem("Banana", 2, 10));
        assertEquals(2, inventory.getItemCount("Banana"));
    }

    @Test
    void testAddExistingItem() {
        inventory.addItem(new FoodItem("Apple", 5, 15));
        assertEquals(10, inventory.getItemCount("Apple"));
    }

    @Test
    public void testRemoveItemPartially() {
        inventory.removeItemByName("Apple", 2);
        InventoryItem item = inventory.getItemByName("Apple");
        assertNotNull(item);
        assertEquals(3, item.getQuantity());
    }

    @Test
    public void testRemoveItemCompletely() {
        inventory.removeItemByName("Apple", 5);
        InventoryItem item = inventory.getItemByName("Apple");
        assertNull(item);
    }

    @Test
    void testGetItemCount() {
        assertEquals(5, inventory.getItemCount("Apple"));
        assertEquals(3, inventory.getItemCount("Ball"));
        assertEquals(0, inventory.getItemCount("NonExistingItem"));
    }

    @Test
    void testGetItemByName() {
        InventoryItem item = inventory.getItemByName("Apple");
        assertNotNull(item);
        assertEquals("Apple", item.getName());

        InventoryItem missingItem = inventory.getItemByName("Orange");
        assertNull(missingItem);
    }

    @Test
    void testGetItems() {
        List<InventoryItem> items = inventory.getItems();
        assertEquals(2, items.size());
        assertTrue(items.stream().anyMatch(item ->
            item.getName().equals("Apple") && item.getQuantity() == 5));
        assertTrue(items.stream().anyMatch(item ->
            item.getName().equals("Ball") && item.getQuantity() == 3));
    }

    @Test
    void testGetEffectDescription() {
        assertEquals("+15 fullness", apple.getEffectDescription());
        assertEquals("+20 happiness", ball.getEffectDescription());
    }

    @Test
    void testUseFoodItemWhenAlreadyFull() {
        int originalFullness = pet.getFullness(); // Both initial and maximum value are 50
        apple.use(pet);
        assertEquals(50, pet.getFullness());// no change
        assertEquals(4, apple.getQuantity());
    }

    @Test
    void testUseFoodItemCappedAtMaxFullness() {
        pet.setFullness(45);
        apple.use(pet);
        assertEquals(50, pet.getFullness()); // capped at max
        assertEquals(4, apple.getQuantity());
    }

    @Test
    void testUseFoodItemIncreasesFullnessNormally() {
        pet.setFullness(5);
        apple.use(pet);
        assertEquals(20, pet.getFullness()); // 5 + 15
        assertEquals(4, apple.getQuantity());
    }

    @Test
    void testUseGiftItemWhenAlreadyHappy() {
        pet.setHappiness(pet.getMaxHappiness()); // 50
        ball.use(pet);
        assertEquals(50, pet.getHappiness()); // no change
        assertEquals(2, ball.getQuantity());
    }

    @Test
    void testUseGiftItemCappedAtMaxHappiness() {
        pet.setHappiness(35); // 35 + 20 = 55 > max
        ball.use(pet);
        assertEquals(50, pet.getHappiness()); // capped
        assertEquals(2, ball.getQuantity());
    }

    @Test
    void testUseGiftItemIncreasesHappinessNormally() {
        pet.setHappiness(5);
        ball.use(pet);
        assertEquals(25, pet.getHappiness());// 5 + 20 = 25
        assertEquals(2, ball.getQuantity());
    }

    @Test
    void testInventoryContentsOutput() {
        String contents = inventory.getInventoryContents();
        assertNotNull(contents);
        assertTrue(contents.contains("Apple"));
        assertTrue(contents.contains("Ball"));
    }

    @Test
    void testInventoryContentsEmpty() {
        Inventory emptyInventory = new Inventory();
        assertNull(emptyInventory.getInventoryContents());
    }
}
