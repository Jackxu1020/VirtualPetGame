import com.google.gson.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Provides utility methods for saving and loading the state of the game, including player, pet,
 * and inventory information. Handles JSON serialization/deserialization using Gson, including
 * custom adapters for LocalDateTime and polymorphic inventory items.
 * <p>
 * The class supports up to three save slots and can determine the number of existing saves or
 * find the oldest one for replacement.
 * <p>
 * Example usage:
 * <pre>{@code
 * // Create a new GameState object
 * GameState gameState = new GameState();
 *
 * // Save the game to slot 1
 * SaveLoadManager.saveGame(gameState, 1);
 *
 * // Load the game from slot 1
 * GameState loaded = SaveLoadManager.loadGame(1);
 *
 * // Get how many slots are currently occupied
 * int count = SaveLoadManager.getSaveFileCounts();
 * }</pre>
 * </p>
 *
 * @version 1.0
 * @author Yu Li
 */

public class SaveLoadManager {

    // Directory to store all save files
    private static final String SAVE_DIR = "saves/";

    static RuntimeTypeAdapterFactory<InventoryItem> itemFactory = RuntimeTypeAdapterFactory
            .of(InventoryItem.class, "type")
            .registerSubtype(FoodItem.class, "FoodItem")
            .registerSubtype(GiftItem.class, "GiftItem");
    // Gson instance with custom serializers/deserializers for LocalDateTime
    private static final Gson gson = new GsonBuilder()
            .registerTypeAdapterFactory(itemFactory)
            .registerTypeAdapter(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
                public LocalDateTime deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) {
                    return LocalDateTime.parse(json.getAsString(), DateTimeFormatter.ISO_DATE_TIME);
                }
            })
            .registerTypeAdapter(LocalDateTime.class, new JsonSerializer<LocalDateTime>() {
                public JsonElement serialize(LocalDateTime src, java.lang.reflect.Type typeOfSrc, JsonSerializationContext context) {
                    return new JsonPrimitive(src.format(DateTimeFormatter.ISO_DATE_TIME));
                }
            })
            .setPrettyPrinting()
            .create();

    // Ensure the saves directory exists when class loads
    static {
        new File(SAVE_DIR).mkdirs();
    }

    /**
     * Saves the given GameState to the specified save slot (1–3).
     *
     * If creationTime is not yet set, it will be initialized and saved.
     * The lastSavedTime is updated every time the game is saved.
     *
     * @param state GameState object containing all current game data
     * @param slot  The save slot number (1, 2, or 3)
     * @throws IOException if saving fails
     */
    public static void saveGame(GameState state, int slot) throws IOException {
        String filename = SAVE_DIR + "slot" + slot + ".json";
        LocalDateTime now = LocalDateTime.now();

        // Only set creationTime the first time this slot is saved
        if (state.getCreationTime() == null) {
            state.setCreationTime(now);
        }

        // Always update lastSavedTime
        state.setLastSavedTime(now);

        try (FileWriter writer = new FileWriter(filename)) {
            gson.toJson(state, writer);
        }
    }

    /**
     * Loads a saved GameState from the given slot.
     *
     * @param slot The save slot number (1, 2, or 3)
     * @return GameState loaded from JSON file, or null if file doesn't exist
     * @throws IOException if reading fails
     */
    public static GameState loadGame(int slot) throws IOException {
        String filename = SAVE_DIR + "slot" + slot + ".json";
        File file = new File(filename);

        if (!file.exists()) return null;

        try (FileReader reader = new FileReader(file)) {
            return gson.fromJson(reader, GameState.class);
        }
    }

    /**
     * Returns the number of existing save files in the saves directory.
     * Used to determine whether a new slot is available or replacement is needed.
     *
     * @return the number of existing save slots (between 0–3)
     */
    public static int getSaveFileCounts() {
        int count = 0;
        for (int slot = 1; slot <= 3; slot++) {
            File file = new File(SAVE_DIR + "slot" + slot + ".json");
            if (file.exists()) {
                count++;
            }
        }
        return count;
    }

    /**
     * Finds the save slot (1–3) with the earliest creation time.
     * Useful for replacing the oldest pet when all save slots are full.
     *
     * @return the slot number with the oldest creation time, or -1 if none exist
     */
    public static int findOldestSlot() {
        LocalDateTime oldestTime = LocalDateTime.MAX;
        int oldestSlot = -1;

        for (int slot = 1; slot <= 3; slot++) {
            try {
                GameState state = loadGame(slot);
                if (state != null && state.getCreationTime() != null) {
                    if (state.getCreationTime().isBefore(oldestTime)) {
                        oldestTime = state.getCreationTime();
                        oldestSlot = slot;
                    }
                }
            } catch (IOException e) {
                // Ignore corrupted/missing files
            }
        }

        return oldestSlot;
    }
}
