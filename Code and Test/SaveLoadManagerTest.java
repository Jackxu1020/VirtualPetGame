import org.junit.jupiter.api.*;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

public class SaveLoadManagerTest {

    @BeforeEach
    void setup() throws IOException {
        Files.createDirectories(Paths.get("saves/"));
        Files.list(Paths.get("saves/"))
                .forEach(path -> path.toFile().delete());
    }

    @Test
    void loadGame_ValidSlot_ReturnsCorrectGameState() throws IOException {
        GameState expectedState = new GameState();
        VirtualPet testPet = new VirtualPet("test", PetType.DUCK);
        Inventory inventory = new Inventory();
        inventory.addItem(new FoodItem("Apple", 5, 15));
        inventory.addItem(new GiftItem("Ball", 3, 20));

        expectedState.updateGameState(testPet,inventory);
        SaveLoadManager.saveGame(expectedState, 420);
        GameState loadedState = SaveLoadManager.loadGame(420);

    }

    @Test
    void loadGame_NonExistentSlot_ReturnsNull() throws IOException {
        GameState loadedState = SaveLoadManager.loadGame(99);
        assertNull(loadedState);
    }

    @Test
    void loadGame_FileCorrupted_ThrowsException() throws IOException {
        Files.writeString(Paths.get("saves/slot1.json"), "{invalid_json}");

        assertThrows(Exception.class, () -> SaveLoadManager.loadGame(1));
    }
}