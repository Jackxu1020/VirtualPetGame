/**
 * Represents a virtual pet with a name, type, and various states such as health, sleep,
 * fullness, and happiness. Each pet type may have different decay rates and behavior patterns.
 * <p>
 * Example usage:
 * <pre>{@code
 * // Create a new virtual pet
 * VirtualPet myPet = new VirtualPet("Buddy", PetType.DOG);
 *
 * // Set the pet's name
 * myPet.setName("Charlie");
 *
 * // Get the current health of the pet
 * int currentHealth = myPet.getHealth();
 *
 * // Update the pet's sleep value
 * myPet.setSleep(80);
 * }</pre>
 * </p>
 *
 * @version 1.0
 * @author ZHenkang Xu
 */

public class VirtualPet {
    /** The Pet's name */
    private String name;
    /** The Pet's type*/
    private PetType petType;
    /** The Player's score*/
    private int score;
    /**  The value for each state of Pet */
    private int health, sleep, fullness, happiness;
    /**  The maximum value for each state of the Pet */
    private int maxHealth, maxSleep, maxFullness, maxHappiness;
    /**  The decay rate of each state value, different type of pet has different decat value  */
    private int hungerDecayRate, happinessDecayRate, sleepDecayRate;
    /** The current state of the Pet */
    private PetState currentState;
    /** The cooldown time (seconds) for play and take to vet */
    private static final int PLAY_COOLDOWN = 10;
    /** The duration (seconds) for sleep */
    private static final int SLEEP_DURATION = 10;
    /** The timestamp of the last play time */
    private long lastPlayTime = 0, lasyTakeToVetTime = 0;
    /** Indicates whether the pet is sleeping */
    private boolean isSleeping = false, stillAngry = false, isPlayColdDown;
    /** Different states that the pet can have */
    enum PetState {
        NORMAL, HUNGRY, SLEEPING, ANGRY, DEAD
    }

    /**
     * Constructs a new VirtualPet instance with specified attributes and initializes its state.
     *
     * @param name The name of the virtual pet.
     * @param petType The type of the virtual pet, defining its specific behavior and decay rates.
     */
    public VirtualPet(String name, PetType petType) {
        this.name = name;
        this.petType = petType;
        this.maxHealth = petType.getDefaultMaxHealth();
        this.maxSleep = petType.getDefaultMaxSleep();
        this.maxFullness = petType.getDefaultMaxFullness();
        this.maxHappiness = petType.getDefaultMaxHappiness();
        this.hungerDecayRate = petType.getHungerDecayRate();
        this.happinessDecayRate = petType.getHappinessDecayRate();
        this.sleepDecayRate = petType.getSleepDecayRate();

        // Initialize current values to maximums.
        this.health = maxHealth;
        this.sleep = maxSleep;
        this.fullness = maxFullness;
        this.happiness = maxHappiness;
        this.currentState = PetState.NORMAL;
    }

    /**
     * Retrieves the name of the virtual pet.
     *
     * @return The name of the pet.
     */
    public String getName() {
        return name;
    }

    /**
     * Checks if the cooldown condition is active.
     *
     * @return true if the cooldown is active, false otherwise.
     */
    public boolean getIsPlayColdDown() {
        return isPlayColdDown;
    }

    /**
     * Sets the name of the virtual pet to the specified value.
     *
     * @param name The new name to assign to the virtual pet.
     * @return The updated name of the virtual pet.
     */
    public String setName(String name) {
        return this.name = name;
    }

    /**
     * Retrieves the type of the virtual pet.
     *
     * @return The type of the pet as a {@code PetType} enumeration value.
     */
    public PetType getPetType() {
        return petType;
    }

    /**
     * Retrieves the current score of the virtual pet.
     *
     * @return The current score as an integer.
     */
    public int getScore() {
        return score;
    }

    /**
     * Retrieves the current health value of the pet.
     *
     * @return The current health value as an integer.
     */
    public int getHealth() {
        return health;
    }

    /**
     * Updates the health value of the virtual pet to the specified value.
     *
     * @param setValue The new health value to assign to the virtual pet.
     */
    public void setHealth(int setValue) { health = setValue; }

    /**
     * Retrieves the maximum health value of the virtual pet.
     *
     * @return The maximum health value as an integer.
     */
    public int getMaxHealth() {
        return maxHealth;
    }

    /**
     * Retrieves the current sleep level of the pet.
     *
     * @return The current sleep value.
     */
    public int getSleep() {
        return sleep;
    }

    /**
     * Updates the sleep level of the virtual pet to the specified value.
     *
     * @param setValue The new sleep value to assign to the pet.
     */
    public void setSleep(int setValue) {sleep = setValue;}

    /**
     * Retrieves the maximum sleep level of the virtual pet.
     *
     * @return The maximum sleep value as an integer.
     */
    public int getMaxSleep() {
        return maxSleep;
    }

    /**
     * Retrieves the current fullness level of the pet.
     *
     * @return the fullness level as an integer.
     */
    public int getFullness() {
        return fullness;
    }

    /**
     * Updates the fullness level of the virtual pet to the specified value.
     *
     * @param setValue The new fullness value to assign to the pet.
     */
    public void setFullness(int setValue) { fullness = setValue; }

    /**
     * Retrieves the maximum fullness level of the virtual pet.
     *
     * @return The maximum fullness value as an integer.
     */
    public int getMaxFullness() {
        return maxFullness;
    }

    /**
     * Retrieves the current happiness level of the pet.
     *
     * @return The current happiness value as an integer.
     */
    public int getHappiness() {
        return happiness;
    }

    /**
     * Updates the happiness level of the virtual pet to the specified value.
     *
     * @param setValue The new happiness value to assign to the virtual pet.
     */
    public void setHappiness(int setValue) { happiness = setValue; }

    /**
     * Retrieves the maximum happiness level of the virtual pet.
     *
     * @return The maximum happiness value as an integer.
     */
    public int getMaxHappiness() {
        return maxHappiness;
    }

    /**
     * Retrieves the sleep decay rate of the virtual pet. This value determines
     * how quickly the pet's sleep level decreases over time.
     *
     * @return The sleep decay rate as an integer.
     */
    public int getSleepDecayRate() {
        return sleepDecayRate;
    }

    /**
     * Retrieves the happiness decay rate of the virtual pet.
     * This value represents how quickly the pet's happiness level decreases over time.
     *
     * @return The happiness decay rate as an integer.
     */
    public int getHappinessDecayRate() {
        return happinessDecayRate;
    }

    /**
     * Retrieves the hunger decay rate of the virtual pet.
     * This value indicates how quickly the pet's fullness level decreases over time.
     *
     * @return The hunger decay rate as an integer.
     */
    public int getHungerDecayRate() {
        return hungerDecayRate;
    }

    /**
     * Retrieves the current state of the virtual pet.
     *
     * @return The current state of the pet as a {@code PetState} enumeration value.
     */
    public PetState getCurrentState() {
        return currentState;
    }

    /**
     * The pet enters a state of sleep and remains in this state until the sleep value reaches its maximum.
     */
    public void goSleep() {
        if (isSleeping) {
            System.out.println("Your pet is sleeping, please wait for it to wake up!");
            return;
        }

        if (currentState == PetState.DEAD) {
            System.out.println("Your pet is already dead and cannot sleep!");
            return;
        }

        isSleeping = true;
        currentState = PetState.SLEEPING; // Update the pet to sleep state
        new Thread(() -> {
            try {
                int sleepIncreasePerSecond = maxSleep / SLEEP_DURATION; // Sleep value increased per second
                for (int i = 0; i <= SLEEP_DURATION; i++) {
                    Thread.sleep(1000); // Execute every second
                    sleep = Math.min(sleep + sleepIncreasePerSecond, maxSleep);
                    if (sleep == maxSleep) break; // Sleep value has reached its maximum.
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } finally {
                isSleeping = false;
                updateState();
                System.out.println("Your pet is up!");
            }
        }).start();
    }


    /**
     * Feeds the virtual pet with the provided food item to increase its fullness.
     * The pet cannot be fed if it is in an ANGRY, SLEEPING, or DEAD state.
     *
     * @param food The FoodItem to feed the pet. Must be non-null and have a positive quantity.
     */
    public void feed(FoodItem food) {
        if (currentState == PetState.ANGRY || currentState == PetState.SLEEPING ||currentState == PetState.DEAD) return;
        if (food == null || food.getQuantity() <= 0) {
            System.out.println("No food available in inventory!");
            return;
        }
        fullness = Math.min(fullness + food.getFoodValue(), maxFullness);
        consumeFood(food);// Reduce food quantity in inventory
        score += 100;
        updateState();
    }

    /**
     * Gives a gift to the virtual pet to increase its happiness level.
     * The action will only execute if the pet is not in a SLEEPING or DEAD state.
     * If the provided gift is null or has a non-positive quantity, the method will notify
     * that no gift is available and terminate.
     *
     * @param gift The gift item to be given to the pet. Must be non-null and have a positive quantity.
     */
    public void giveGift(GiftItem gift) {
        if (currentState == PetState.SLEEPING || currentState == PetState.DEAD) return;
        if (gift == null || gift.getQuantity() <= 0) {
            System.out.println("No gift available in inventory!");
            return;
        }
        happiness = Math.min(happiness + gift.getHappinessValue(), maxHappiness);
        consumeGift(gift); // Reduce gift quantity in inventory
        score += 100;
        updateState();
    }


    /**
     * If a player takes their pet to the veterinarian, the pet's health
     * will increase by a certain amount. After using this command, the pet
     * will be unable to use its abilities for a certain period of time until
     * the cooldown is over.
     */
    public void takeToVet() {
        long currentTime = System.currentTimeMillis() / 1000; // Current time in seconds
        if (currentTime - lasyTakeToVetTime < PLAY_COOLDOWN) {
            isPlayColdDown = true;
            return;
        }
        isPlayColdDown = false;
        health = maxHealth;
        lasyTakeToVetTime = currentTime; // Set cooldown time
        score -= 100;
        updateState();
    }

    /**
     * Allows the pet to play, increasing its happiness up to the maximum happiness level.
     * This method considers the pet's current state and ensures the play action respects cooldowns.
     *
     * @param playValue The amount of happiness to increase as a result of playing.
     */
    public void play(int playValue) {
        long currentTime = System.currentTimeMillis() / 1000; // Current time in seconds
        if (currentTime - lastPlayTime < PLAY_COOLDOWN) {
            isPlayColdDown = true;
            return;
        }
        // Execute normal play logic
        isPlayColdDown = false;
        happiness = Math.min(happiness + playValue, maxHappiness);
        lastPlayTime = currentTime; // Update last play time
        score += 100;
        updateState(); // Update pet state
    }


    /**
     * Performs exercise for the virtual pet, increasing its health and reducing its
     * sleep and fullness levels. The pet's state is updated after the exercise.
     * Exercise will have no effect if the pet is dead or sleeping.
     *
     * @param healthBoost The amount by which the pet's health should increase,
     *                    capped at the pet's maximum health.
     */
    public void exercise(int healthBoost) {
        if (currentState == PetState.DEAD || currentState == PetState.SLEEPING || currentState == PetState.ANGRY) {
            System.out.println("This function is currently unavailable.");
            return;
        }
        health = Math.min(health + healthBoost, maxHealth);
        sleep = Math.max(sleep - 10, 0);
        fullness = Math.max(fullness - 10, 0);
        updateState();
    }

    /**
     * Updates the current state of the virtual pet based on its health, sleep, fullness, and happiness levels.
     * The state is modified as follows:
     * - If health is zero, the pet transitions to the DEAD state.
     * - If sleep is zero and the pet is not already in the DEAD state, the pet transitions to the SLEEPING state.
     * - If fullness is zero and the pet is not already in the DEAD state, the pet transitions to the HUNGRY state.
     * - If happiness is zero and the pet is not already in the DEAD state, the pet transitions to a state representing low happiness.
     * - If none of the above conditions are met, the pet's state is checked and reset to NORMAL if applicable.
     */
    public void updateState() {
        if (health == 0) {
            handleDeathState();
        } else if (sleep == 0 && currentState != PetState.DEAD) {
            handleSleepState();
        } else if (happiness == 0 && currentState != PetState.DEAD && isSleeping == false) {
            handleHappinessState();
        } else if (fullness == 0 && currentState != PetState.DEAD) {
            handleHungryState();
        } else {
            checkAndSetNormalState();
        }
    }

    /**
     * Decreases the virtual pet's various attributes over time, based on its current state
     * and predefined decay rates. This method is responsible for adjusting the pet's health,
     * sleep, happiness, and fullness levels and ensures they do not fall below zero.
     *
     * The following changes are applied:
     * - If the pet is in a HUNGRY state and health is greater than zero, health decreases.
     * - Sleep decreases unless the pet is sleeping.
     * - Happiness decreases regardless of the pet's current state.
     * - Fullness decreases due to hunger over time.
     *
     * After modifying the attributes, the pet's state is updated by invoking the {@code updateState} method.
     */
    public void decreaseStatsOverTime() {
        // WHen a pet is in hungry state, its health value begins to decrease
        if (currentState==PetState.HUNGRY && health > 0) {
            health = Math.max(health -= 10, 0);
        }

        // When a pet is in hungry state, its happiness value decreases faster.
        if (currentState == PetState.HUNGRY && happiness > 0) {
            happiness = Math.max(happiness - 2 * this.getHappinessDecayRate(), 0);
        } else if (happiness > 0) {
            happiness = Math.max(happiness - this.getHappinessDecayRate(), 0);
        }

        // Decrease sleep value over time
        if (sleep > 0 && !isSleeping) {
            sleep = Math.max(sleep - this.getSleepDecayRate(), 0);
        }

        // Decrease fullness value over time
        if (fullness > 0) {
            fullness = Math.max(fullness - this.getHungerDecayRate(), 0);
        }

        updateState();
    }

    /**
     * Transitions the pet to the DEAD state, ensuring the pet is not sleeping
     * and resetting relevant status values to zero.
     */
    private void handleDeathState() {
        currentState = PetState.DEAD;
        isSleeping = false; // Ensure that pet does not go into SLEEPING state
        setSleep(0);
        setHappiness(0);
        setFullness(0);
    }


    /**
     * Decreases the pet's health to account for lack of sleep
     * and transitions the pet into a sleeping state.
     */

    private void handleSleepState() {
            health = Math.max(0, health - 5);
            goSleep();
    }

    /**
     * Sets the pet's state to HUNGRY.
     */
    private void handleHungryState() {
        if (!isSleeping && !stillAngry) {
        currentState = PetState.HUNGRY;
        }
    }

    /**
     * Sets the pet's state to ANGRY.
     */
    private void handleHappinessState() {
        currentState = PetState.ANGRY;
        stillAngry = true;
    }


    /**
     * Checks if the pet's attributes meet the criteria for a normal state
     * and updates the pet's state to NORMAL if applicable.
     *
     * The method evaluates the following conditions:
     * - The pet's health, sleep, fullness, and happiness levels must all be greater than zero.
     * - The pet must not currently be in a NORMAL state.
     * - The pet must not be sleeping.
     * - The pet must not still be in an angry state.
     *
     * If all conditions are satisfied, the pet's state is set to NORMAL, and a
     * message is displayed indicating the transition back to normal state.
     */
    private void checkAndSetNormalState() {
        if (happiness > maxHappiness/2) {
            stillAngry =false;
        }

        if (health > 0 && sleep > 0 && fullness > 0 && happiness > 0 &&
                currentState != PetState.NORMAL && !isSleeping && !stillAngry) {
            currentState = PetState.NORMAL;
            System.out.println("Your pet is back to its normal state!");
        }
    }

    /**
     * Consumes a specified quantity of the given food item by reducing its current quantity by one.
     *
     * @param foodItem the food item to be consumed, which will have its quantity reduced
     */
    private void consumeFood(FoodItem foodItem) {
        foodItem.setQuantity(foodItem.getQuantity()-1);
    }

    /**
     * Consumes a gift by reducing its quantity by one.
     *
     * @param giftItem the gift item whose quantity needs to be decreased
     */
    private void consumeGift(GiftItem giftItem) {
        giftItem.setQuantity(giftItem.getQuantity()-1);
    }
}
