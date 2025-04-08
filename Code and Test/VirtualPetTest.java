import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

class VirtualPetTest {
    private VirtualPet cat;
    private ByteArrayOutputStream outContent;
    private FoodItem testFood;

    @BeforeEach
    void setUp() {
        outContent = new ByteArrayOutputStream();
        System.setOut(new PrintStream(outContent));
        cat = new VirtualPet("TestPet", PetType.DUCK);
        testFood = new FoodItem("Treat", 3, 5); // Setup a generic food item
    }


    @Test
    void testGetName() {
        assertEquals("TestPet", cat.getName());
    }

    @Test
    void testPlayCooldown() throws InterruptedException {
        int initialHappiness = cat.getHappiness();

        cat.play(10);
        assertEquals(Math.min(initialHappiness + 10, cat.getHappiness()), cat.getHappiness(), "Happiness did not increase correctly after playing.");

        int happinessAfterCooldownViolation = cat.getHappiness();
        cat.play(10);
        assertEquals(happinessAfterCooldownViolation, cat.getHappiness(), "Happiness should not change during cooldown.");

        Thread.sleep(31_000);
        cat.play(10);

        assertEquals(Math.min(happinessAfterCooldownViolation + 10, cat.getMaxHappiness()), cat.getHappiness(), "Happiness did not increase correctly after cooldown.");
    }

    @Test
    void testPlayWhileSleeping() throws InterruptedException {
        int initialHappiness = cat.getHappiness();
        cat.setSleep(1);

        cat.goSleep();
        Thread.sleep(2000);

        assertEquals(VirtualPet.PetState.SLEEPING, cat.getCurrentState(), "Pet should be in sleeping state.");

        cat.play(10);
        assertEquals(initialHappiness, cat.getHappiness(), "Sleeping pets should not be able to play.");
    }

    @Test
    void testWarningForLowHealth() {
        cat.setHealth(10);
        cat.updateState();
        // Checking if warning is issued for low health
        String output = outContent.toString();
        assertTrue(output.contains("Warning: Health is critically low!"),
                "The correct warning for low health was not issued.");
    }

    @Test
    void testWarningForLowSleep() {
        cat.setSleep(10); // Set sleep to low value

        // Trigger state update to check for warnings
        cat.updateState();
        String output = outContent.toString();
        assertTrue(output.contains("Sleep level is critically low!"),
                "The correct warning for low sleep was not issued.");
    }

    @Test
    void testWarningForLowFullness() {
        cat.setFullness(5); // Set fullness to low value

        // Trigger state update to check for warnings
        cat.updateState();
        String output = outContent.toString();
        assertEquals("Warning: Fullness is critically low!\n", output);
    }

    @Test
    void testWarningForLowHappiness() {
        cat.setHappiness(10); // Attempt to reduce happiness below 25%

        // Trigger state update to check for warnings
        cat.updateState();
        String output = outContent.toString();
        assertEquals("Warning: Happiness is critically low!\n", output);
    }

    @Test
    void testPetGoesToSleepWhenSleepIsZero() throws InterruptedException {
        // Record health before sleep
        int healthBeforeSleep = cat.getHealth();

        // Set sleep level to 0
        cat.setSleep(0);

        // Trigger state update
        cat.updateState();
        // Ensure the pet goes to sleep
        assertEquals(VirtualPet.PetState.SLEEPING, cat.getCurrentState(), "Pet should automatically go to sleep when sleep is 0.");


        // Wait to allow sleep process to take effect
        Thread.sleep(12000);

        // Ensure health decreases
        assertTrue(cat.getHealth() < healthBeforeSleep, "Pet's health should decrease when it is forced to sleep.");
        // Ensure the sleep is recovery to maximum sleep
        assertEquals(50, cat.getSleep());
        // Ensure the state is normal
        assertEquals(VirtualPet.PetState.NORMAL, cat.getCurrentState());
    }

    @Test
    void testUpdateStatePetDiesWhenHealthIsZero() {
        // Set health to 0
        cat.setHealth(0);

        // Trigger state update
        cat.updateState();

        // Verify that the pet's state is DEAD
        assertEquals(VirtualPet.PetState.DEAD, cat.getCurrentState(), "Pet should be in DEAD state when health is 0.");
        String output = outContent.toString();
        assertEquals("Your pet has died! Game Over.\n", output);
    }

    @Test
    void testUpdateStatePetRemainsNormalAtMaximumAttributes() {
        // Set all attributes to maximum
        cat.setHealth(cat.getMaxHealth());
        cat.setHappiness(cat.getMaxHappiness());
        cat.setFullness(cat.getMaxFullness());
        cat.setSleep(cat.getMaxSleep());

        // Trigger state update
        cat.updateState();

        // Verify the pet is in NORMAL state
        assertEquals(VirtualPet.PetState.NORMAL, cat.getCurrentState(), "Pet should remain in NORMAL state at maximum attributes.");
    }

    @Test
    void testUpdateStatePetBecomesHungryWhenFullnessIsZero() {
        // Set fullness to 0
        cat.setFullness(0);

        // Trigger state update
        cat.updateState();

        // Verify the pet is in HUNGRY state
        assertEquals(VirtualPet.PetState.HUNGRY, cat.getCurrentState(), "Pet should transition to HUNGRY state when fullness is 0.");
        String output = outContent.toString();
        assertEquals("Your pet is starving! Feed it immediately.\n", output);
    }

    @Test
    void testUpdateStatePetBecomesAngryWhenHappinessIsZero() {
        // Set happiness to 0
        cat.setHappiness(0);

        // Trigger state update
        cat.updateState();

        // Verify the pet is in ANGRY state
        assertEquals(VirtualPet.PetState.ANGRY, cat.getCurrentState(), "Pet should transition to ANGRY state when happiness is 0.");
        String output = outContent.toString();
        assertEquals("Your pet is angry and refuses to listen! Make it happy!\n", output);
    }

    @Test
    void testUpdateStatePetReturnsToNormalAfterFixingAllZeroStates() {
        // Set zero states
        cat.setHappiness(0);
        cat.setFullness(0);
        cat.setSleep(0);

        // Trigger state update
        cat.updateState();
        assertNotEquals(VirtualPet.PetState.NORMAL, cat.getCurrentState(), "Pet should not be in NORMAL state initially.");

        // Fix all critical states
        cat.setSleep(cat.getMaxSleep());
        cat.updateState();
        assertEquals(VirtualPet.PetState.HUNGRY, cat.getCurrentState());

        cat.setFullness(cat.getMaxFullness());
        cat.updateState();
        assertEquals(VirtualPet.PetState.ANGRY, cat.getCurrentState());

        cat.setHappiness(cat.getMaxHappiness()/2);
        cat.updateState();
        assertEquals(VirtualPet.PetState.ANGRY, cat.getCurrentState());

        cat.setHappiness(cat.getMaxHappiness()/2+1);
        cat.updateState();
        assertEquals(VirtualPet.PetState.NORMAL, cat.getCurrentState());
    }

    @Test
    void testTakeToVetRestoresHealth() {
        // Reduce health to a lower value
        cat.setHealth(10);

        // Call takeToVet to restore health
        cat.takeToVet();

        // Verify health is restored to maximum value
        assertEquals(cat.getMaxHealth(), cat.getHealth(), "Health should be restored to maximum after visiting the vet.");
    }

    @Test
    void testTakeToVetUnderCooldown() throws InterruptedException {
        // Call takeToVet for the first time
        cat.takeToVet();

        // Reduce health slightly
        cat.setHealth(cat.getMaxHealth() - 20);

        // Ensure cooldown is effective by calling takeToVet again within the cooldown period
        cat.takeToVet();

        // Verify health hasn't been restored due to cooldown
        assertEquals(cat.getMaxHealth() - 20, cat.getHealth(), "Health should not be restored during cooldown.");

        // Wait for cooldown to complete
        Thread.sleep(31_000);

        // Call takeToVet after cooldown
        cat.takeToVet();

        // Verify health is restored after cooldown
        assertEquals(cat.getMaxHealth(), cat.getHealth(), "Health should be restored after cooldown period ends.");
    }

    @Test
    void testTakeToVetInInvalidState() {
        // Test while the pet is 'ANGRY'
        cat.setHappiness(0);
        cat.updateState();
        assertEquals(VirtualPet.PetState.ANGRY, cat.getCurrentState());
        cat.setHealth(10);
        cat.takeToVet();
        assertEquals(10, cat.getHealth(), "Health should not be restored when pet is ANGRY.");

        // Test while the pet is 'SLEEPING'
        cat.setHappiness(cat.getMaxHappiness());
        cat.setSleep(0);
        cat.updateState();
        assertEquals(VirtualPet.PetState.SLEEPING, cat.getCurrentState());
        cat.setHealth(10);
        cat.takeToVet();
        assertEquals(10, cat.getHealth(), "Health should not be restored when pet is SLEEPING.");

        // Test while the pet is 'DEAD'
        cat.setHealth(0);
        cat.updateState();
        assertEquals(VirtualPet.PetState.DEAD, cat.getCurrentState());
        cat.takeToVet();
        assertEquals(0, cat.getHealth(), "Health should not be restored when pet is DEAD.");
    }

    @Test
    void testExerciseIncreasesHealthAndDecreasesSleepAndFullness() {
        int initialHealth = cat.getHealth();
        int initialSleep = cat.getSleep();
        int initialFullness = cat.getFullness();

        cat.exercise(10); // Perform exercise with health boost

        assertEquals(Math.min(initialHealth + 10, cat.getMaxHealth()), cat.getHealth(), "Health did not increase correctly after exercising.");
        assertEquals(Math.max(initialSleep - 10, 0), cat.getSleep(), "Sleep did not decrease correctly after exercising.");
        assertEquals(Math.max(initialFullness - 10, 0), cat.getFullness(), "Fullness did not decrease correctly after exercising.");
    }

    @Test
    void testExerciseDoesNotWorkWhileSleepingOrDead() {
        // Case 1: Dead pet
        cat.setHealth(0);
        cat.updateState();
        assertEquals(VirtualPet.PetState.DEAD, cat.getCurrentState(), "Pet should be in DEAD state when health is 0.");
        int initialHealth = cat.getHealth();
        cat.exercise(10);
        assertEquals(initialHealth, cat.getHealth(), "Health should not change while the pet is dead.");

        // Case 2: Sleeping pet
        cat.setHealth(cat.getMaxHealth());
        cat.updateState();
        cat.setSleep(0);
        cat.goSleep();
        assertEquals(VirtualPet.PetState.SLEEPING, cat.getCurrentState(), "Pet should be in SLEEPING state.");
        initialHealth = cat.getHealth();
        cat.exercise(10);
        assertEquals(initialHealth, cat.getHealth(), "Health should not change while the pet is sleeping.");
    }

    @Test
    void testExerciseDoesNotExceedMaximumHealth() {
        cat.setHealth(cat.getMaxHealth() - 5); // Reduce health to just below max
        cat.exercise(10); // Perform exercise
        assertEquals(cat.getMaxHealth(), cat.getHealth(), "Health should not exceed max health after exercising.");
    }

    @Test
    void testExerciseHandlesMinimumSleepAndFullnessAfterUsage() {
        cat.setSleep(5); // Set sleep close to zero
        cat.setFullness(5); // Set fullness close to zero

        // Exercise with a health boost
        cat.exercise(10);

        assertEquals(0, cat.getSleep(), "Sleep should not drop below zero after exercising.");
        assertEquals(0, cat.getFullness(), "Fullness should not drop below zero after exercising.");
    }

    @Test
    void testFeedIncreasesFullness() {
        int initialFullness = cat.getFullness();

        cat.setFullness(20);
        // Feeding the pet with food
        cat.feed(testFood);

        // Ensuring fullness is increased and food quantity is decreased
        assertEquals(25,cat.getFullness(),"Fullness did not increase correctly after feeding.");

    }

    @Test
    void testFeedNotExceedMaxFullness() {
        cat.setFullness(49);
        cat.feed(testFood);

        assertEquals(50, cat.getFullness(), "Fullness should not exceed maximum value.");
    }

    @Test
    void testFeedQuantityDecreasedAfterConsumption() {
        int initialFoodQuantity = testFood.getQuantity();
        cat.feed(testFood);
        assertEquals(2, testFood.getQuantity(), "Food quantity should decrease after consumption.");
    }

    @Test
    void testFeedDoesNotWorkWhileSleeping() throws InterruptedException {
        cat.setSleep(1);
        cat.goSleep();
        Thread.sleep(2000); // Allow pet to enter sleep state

        assertEquals(VirtualPet.PetState.SLEEPING, cat.getCurrentState(), "Pet should be in sleeping state.");

        int initialFullness = cat.getFullness();
        cat.feed(testFood);
        assertEquals(initialFullness, cat.getFullness(), "Fullness should not change while the pet is sleeping.");
    }

    @Test
    void testFeedDoesNotWorkWhileDead() {
        cat.setHealth(0);
        cat.updateState();
        assertEquals(VirtualPet.PetState.DEAD, cat.getCurrentState(), "Pet should be in dead state.");

        int initialFullness = cat.getFullness();
        cat.feed(testFood);
        assertEquals(initialFullness, cat.getFullness(), "Fullness should not change while the pet is dead.");
    }

    @Test
    void testFeedHandlesNullFood() {
        int initialFullness = cat.getFullness();

        // Feeding with null food
        cat.feed(null);

        // Fullness should not change
        assertEquals(initialFullness, cat.getFullness(), "Feeding with null food should not change fullness.");
    }


    @Test
    void testGiveGiftIncreasesHappiness() {
        GiftItem ball = new GiftItem("Ball", 3, 20);
        cat.setHappiness(10);
        cat.giveGift(ball);
        assertEquals(30, cat.getHappiness(), "Happiness did not increase correctly after giving a gift.");
    }

    @Test
    void testGiveGiftNotExceedMaxHappiness() {
        cat.setHappiness(cat.getMaxHappiness() - 10);
        GiftItem gift = new GiftItem("Teddy Bear", 1, 20);

        cat.giveGift(gift);

        assertEquals(cat.getMaxHappiness(), cat.getHappiness(), "Happiness should not exceed maximum value.");
    }

    @Test
    void testGiveGiftDecreaseQuantity() {
        GiftItem ball = new GiftItem("Ball", 3, 20);
        cat.setHappiness(10);
        cat.giveGift(ball);
        assertEquals(2, ball.getQuantity(), "Gift quantity should decrease after giving a gift.");
    }

    @Test
    void testGiveGiftHandlesNullGift() {
        int initialHappiness = cat.getHappiness();

        cat.giveGift(null);

        assertEquals(initialHappiness, cat.getHappiness(), "Happiness should not change when giving a null gift.");
        assertEquals(VirtualPet.PetState.NORMAL, cat.getCurrentState(), "Pet state should remain unchanged when giving a null gift.");
    }

    @Test
    void testGiveGiftDoesNotWorkWhileSleeping() throws InterruptedException {
        cat.setSleep(0);
        cat.goSleep();
        Thread.sleep(2000); // Pet goes to sleep

        assertEquals(VirtualPet.PetState.SLEEPING, cat.getCurrentState(), "Pet should be in sleeping state.");

        int initialHappiness = cat.getHappiness();
        GiftItem gift = new GiftItem("Rubber Duck", 1, 15);

        cat.giveGift(gift);

        assertEquals(initialHappiness, cat.getHappiness(), "Happiness should not change while pet is sleeping.");
    }

    @Test
    void testGiveGiftDoesNotWorkWhileDead() {
        cat.setHealth(0);
        cat.updateState();
        assertEquals(VirtualPet.PetState.DEAD, cat.getCurrentState(), "Pet should be in dead state.");

        int initialHappiness = cat.getHappiness();
        GiftItem gift = new GiftItem("Plush Toy", 1, 15);

        cat.giveGift(gift);

        assertEquals(initialHappiness, cat.getHappiness(), "Happiness should not change while pet is dead.");
    }

    @Test
    void testGiveGiftZeroQuantityWarning() {
        GiftItem gift = new GiftItem("Empty Box", 0, 10);

        cat.giveGift(gift);

        String output = outContent.toString();
        assertTrue(output.contains("No gift available in inventory!"), "Pet should issue a warning when trying to give a gift with zero quantity.");
    }
}