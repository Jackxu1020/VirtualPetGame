import java.time.LocalDateTime;
import java.util.List;

/**
 * Represents the saved state of the game, including information about the pet,
 * its attributes, the player’s score, and the inventory. This class is used for
 * serialization and deserialization when saving or loading a game session.
 * <p>
 * The GameState includes time-related data such as when it was first created
 * and the last time it was saved.
 * </p>
 *
 * <p>
 * Example usage:
 * <pre>{@code
 * VirtualPet pet = new VirtualPet("Buddy", PetType.SHEEP);
 * Inventory inventory = new Inventory();
 * GameState gameState = new GameState();
 * gameState.updateGameState(pet, inventory);
 * }</pre>
 * </p>
 *
 * @version 3.0
 * @author Yu Li
 */

public class GameState {
    private String petName;
    private String petType;
    private int health;
    private int sleep;
    private int fullness;
    private int happiness;
    private int score;
    private Inventory inventory;
    private LocalDateTime lastSavedTime;

    public void updateGameState(VirtualPet pet, Inventory inventory) {
        this.petName = pet.getName();
        this.petType = String.valueOf(pet.getPetType());
        this.health = pet.getHealth();
        this.sleep = pet.getSleep();
        this.fullness = pet.getFullness();
        this.happiness = pet.getHappiness();
        this.score = pet.getScore();
        this.inventory = inventory;
    }

    // Getters and Setters
    public String getPetName() { return petName; }
    public void setPetName(String petName) { this.petName = petName; }

    public String getPetType() { return petType; }
    public void setPetType(String petType) { this.petType = petType; }

    public int getHealth() { return health; }
    public void setHealth(int health) { this.health = health; }

    public int getSleep() { return sleep; }
    public void setSleep(int sleep) { this.sleep = sleep; }

    public int getFullness() { return fullness; }
    public void setFullness(int fullness) { this.fullness = fullness; }

    public int getHappiness() { return happiness; }
    public void setHappiness(int happiness) { this.happiness = happiness; }

    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }

    public Inventory getInventory() { return inventory; }
    public void setInventory(Inventory inventory) { this.inventory = inventory; }

    public LocalDateTime getLastSavedTime() { return lastSavedTime; }
    public void setLastSavedTime(LocalDateTime lastSavedTime) { this.lastSavedTime = lastSavedTime; }

    //Store creation time when a pet is firstly created
    private LocalDateTime creationTime;

    public LocalDateTime getCreationTime() {
        return creationTime;
    }

    public void setCreationTime(LocalDateTime creationTime) {
        this.creationTime = creationTime;
    }

}
