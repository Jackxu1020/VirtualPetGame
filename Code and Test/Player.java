import java.time.LocalTime;
import java.util.Timer;
import java.util.TimerTask;
import java.io.*;

/**
 * This class manages player accounts, parental controls, and gameplay time tracking.
 * It provides functionality for password protection, time limit enforcement, and 
 * gameplay statistics tracking.
 * 
 * The class uses a singleton pattern to ensure global settings are consistent
 * across all instances, storing configuration in a persistent .dat file.
 * 
 * Key features include:
 * - Password-protected parental controls
 * - Configurable gameplay time restrictions
 * - Play time tracking and statistics
 * - Pet revival functionality for parent accounts
 *
 * 
 * @author Haoxuan Suo 251103783 hsuo3
 */
public class Player {
    private boolean isParent;

    
    private static String globalPassword = "0000"; 
    private static boolean isPasswordInitialized = false;
    private static float globalTotalPlayTime = 0.0f; 
    private static int globalGameStartCount = 0; 
    private static boolean globalParentalControlsEnabled = false; 
    private static String globalPlayTimeLimit = "18:00 - 20:00"; 
    private static int globalStartHour = 18;
    private static int globalStartMinute = 0;
    private static int globalEndHour = 20;
    private static int globalEndMinute = 0;
    private static final String GLOBAL_SETTINGS_FILE = "global_settings.dat";

    
    private float totalPlayTime;
    private float averagePlayTime;
    private int sessionCount;

    // Parental control settings
    private boolean parentalControlsEnabled;
    private String playTimeLimit;
    private int startHour;
    private int startMinute;
    private int endHour;
    private int endMinute;

    // Variables for time check functionality
    private Timer timeCheckTimer;
    private TimeCheckCallback timeCheckCallback;
    private boolean isCurrentlyPlaying;
    private long playStartTime;

    /**
     * Callback interface for time checking.
     * Implementations of this interface receive notifications about
     * time restriction events during gameplay.
     */
    public interface TimeCheckCallback {
        /**
         * Called when a time restriction violation occurs
         * @param allowedTimeRange the configured time range when gameplay is allowed
         */
        void onTimeRestrictionViolation(String allowedTimeRange);
        
        /**
         * Called periodically to check if gameplay is still allowed
         * @param isAllowed whether gameplay is currently allowed
         */
        void onPeriodicCheck(boolean isAllowed);
    }

    /**
     * Creates a player object
     * @param isParent whether it is a parent account
     */
    public Player(boolean isParent) {
        this.isParent = isParent;
        this.totalPlayTime = 0.0f;
        this.sessionCount = 0;
        this.isCurrentlyPlaying = false;

        // Load global settings
        loadGlobalSettings();

        // Load parental control settings from global settings
        this.parentalControlsEnabled = globalParentalControlsEnabled;
        this.playTimeLimit = globalPlayTimeLimit;
        this.startHour = globalStartHour;
        this.startMinute = globalStartMinute;
        this.endHour = globalEndHour;
        this.endMinute = globalEndMinute;

        // Calculate average time using global data
        if (globalGameStartCount > 0) {
            this.averagePlayTime = globalTotalPlayTime / globalGameStartCount;
        } else {
            this.averagePlayTime = 0.0f;
        }
    }

    /**
     * Sets global password (can only be set once)
     * @param password new password to set
     * @return whether successfully set
     */
    public static boolean setGlobalPassword(String password) {
        // If the password has been initialized, do not allow setting again
        if (isPasswordInitialized) {
            return false;
        }

        globalPassword = password;
        isPasswordInitialized = true;
        saveGlobalSettings();

        return true;
    }

    /**
     * Checks if global password has been initialized
     * @return true if password has been set, false otherwise
     */
    public static boolean isGlobalPasswordInitialized() {
        return isPasswordInitialized;
    }

    /**
     * Verifies if the entered password matches the global password
     * @param enteredPassword the password to verify
     * @return true if password matches, false otherwise
     */
    public static boolean verifyGlobalPassword(String enteredPassword) {
        // Make sure to load the latest settings (including password)
        loadGlobalSettings();
        return globalPassword.equals(enteredPassword);
    }

    /**
     * Gets global total play time
     * @return total play time in hours
     */
    public static float getGlobalTotalPlayTime() {
        return globalTotalPlayTime;
    }

    /**
     * Gets global game start count
     * @return number of times the game has been started
     */
    public static int getGlobalGameStartCount() {
        return globalGameStartCount;
    }

    /**
     * Increments global game start count
     */
    public static void incrementGlobalGameStartCount() {
        globalGameStartCount++;
        saveGlobalSettings();
    }

    /**
     * Resets global statistics
     * @param password global password for verification
     * @return whether successfully reset
     */
    public static boolean resetGlobalStats(String password) {
        if (!verifyGlobalPassword(password)) {
            return false;
        }

        globalTotalPlayTime = 0.0f;
        globalGameStartCount = 0;
        saveGlobalSettings();

        return true;
    }

    /**
     * Saves global settings to persistent storage
     */
    private static void saveGlobalSettings() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(GLOBAL_SETTINGS_FILE))) {
            // Write whether password is initialized
            oos.writeBoolean(isPasswordInitialized);

            // Write password
            oos.writeObject(globalPassword);

            // Write global statistics
            oos.writeFloat(globalTotalPlayTime);
            oos.writeInt(globalGameStartCount);

            // Write parental control settings
            oos.writeBoolean(globalParentalControlsEnabled);
            oos.writeObject(globalPlayTimeLimit);
            oos.writeInt(globalStartHour);
            oos.writeInt(globalStartMinute);
            oos.writeInt(globalEndHour);
            oos.writeInt(globalEndMinute);

            // Ensure data is written to disk
            oos.flush();
        } catch (IOException e) {
            System.err.println("Failed to save global settings: " + e.getMessage());
        }
    }

    /**
     * Loads global settings from persistent storage
     */
    private static void loadGlobalSettings() {
        File file = new File(GLOBAL_SETTINGS_FILE);
        if (!file.exists()) {
            return; // File doesn't exist, use default values
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            // Read whether password is initialized
            isPasswordInitialized = ois.readBoolean();

            // Read password
            globalPassword = (String) ois.readObject();

            // Read global statistics
            globalTotalPlayTime = ois.readFloat();
            globalGameStartCount = ois.readInt();

            // Read parental control settings
            globalParentalControlsEnabled = ois.readBoolean();
            globalPlayTimeLimit = (String) ois.readObject();
            globalStartHour = ois.readInt();
            globalStartMinute = ois.readInt();
            globalEndHour = ois.readInt();
            globalEndMinute = ois.readInt();
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("Failed to load global settings: " + e.getMessage());
        }
    }

    /**
     * Starts gameplay tracking and time checking
     * @param callback interface to notify about time restriction events
     * @return true if allowed to play, false otherwise
     */
    public boolean startPlaying(TimeCheckCallback callback) {
        // Ensure using the latest parental control settings (loaded from .dat file)
        loadGlobalSettings();
        this.parentalControlsEnabled = globalParentalControlsEnabled;
        this.playTimeLimit = globalPlayTimeLimit;
        this.startHour = globalStartHour;
        this.startMinute = globalStartMinute;
        this.endHour = globalEndHour;
        this.endMinute = globalEndMinute;

        // Check if allowed to play
        if (!isAllowedToPlay()) {
            if (callback != null) {
                callback.onTimeRestrictionViolation(getPlayTimeLimit());
            }
            return false;
        }

        this.timeCheckCallback = callback;
        this.isCurrentlyPlaying = true;
        this.playStartTime = System.currentTimeMillis();

        // Increment global game start count
        incrementGlobalGameStartCount();

        // Start periodic check task
        startTimeCheck();

        return true;
    }

    /**
     * Stops gameplay tracking and updates statistics
     */
    public void stopPlaying() {
        if (isCurrentlyPlaying) {
            stopTimeCheck();

            // Calculate this session's time and update statistics
            long endTime = System.currentTimeMillis();
            float sessionHours = (endTime - playStartTime) / (1000.0f * 60.0f * 60.0f);

            // Update personal statistics
            updatePlayTime(sessionHours);

            // Update global statistics
            updateGlobalPlayTime(sessionHours);

            // Update average time (using global data)
            if (globalGameStartCount > 0) {
                this.averagePlayTime = globalTotalPlayTime / globalGameStartCount;
            }

            isCurrentlyPlaying = false;
        }
    }

    /**
     * Updates global total play time
     * @param duration duration to add to global play time
     */
    private static void updateGlobalPlayTime(float duration) {
        globalTotalPlayTime += duration;
        saveGlobalSettings();
    }

    /**
     * Starts periodic time check
     */
    private void startTimeCheck() {
        if (timeCheckTimer != null) {
            timeCheckTimer.cancel();
        }

        timeCheckTimer = new Timer();
        timeCheckTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                boolean allowed = isAllowedToPlay();

                if (timeCheckCallback != null) {
                    timeCheckCallback.onPeriodicCheck(allowed);

                    if (!allowed) {
                        timeCheckCallback.onTimeRestrictionViolation(getPlayTimeLimit());
                    }
                }
            }
        }, 0, 60000); // Check every minute
    }

    /**
     * Stops time check timer
     */
    private void stopTimeCheck() {
        if (timeCheckTimer != null) {
            timeCheckTimer.cancel();
            timeCheckTimer = null;
        }
    }

    /**
     * Checks if this player is a parent
     * @return true if parent, false otherwise
     */
    public boolean isParent() {
        return isParent;
    }

    /**
     * Sets the parent status of this player
     * @param isParent whether this player is a parent
     */
    public void setParent(boolean isParent) {
        this.isParent = isParent;
    }

    /**
     * Gets total play time for this player
     * @return total play time in hours
     */
    public float getTotalPlayTime() {
        return totalPlayTime;
    }

    /**
     * Gets average play time
     * @return average play time in hours per session
     */
    public float getAveragePlayTime() {
        // Calculate average time using global data
        if (globalGameStartCount > 0) {
            return globalTotalPlayTime / globalGameStartCount;
        }
        return 0.0f;
    }

    /**
     * Updates play time statistics
     * @param duration duration to add to play time
     */
    public void updatePlayTime(float duration) {
        totalPlayTime += duration;
        sessionCount++;
    }

    /**
     * Sets parental control settings
     * @param enabled whether parental controls are enabled
     * @param playTimeLimit time range when gameplay is allowed (format: "HH:MM - HH:MM")
     * @return true if settings were applied successfully, false otherwise
     */
    public boolean setParentalControls(boolean enabled, String playTimeLimit) {
        if (!isParent) {
            return false;
        }

        // Update instance variables
        this.parentalControlsEnabled = enabled;

        // Update global variables
        globalParentalControlsEnabled = enabled;

        if (playTimeLimit != null && !playTimeLimit.isEmpty()) {
            // Update instance variables
            this.playTimeLimit = playTimeLimit;

            // Update global variables
            globalPlayTimeLimit = playTimeLimit;

            String[] parts = playTimeLimit.split(" - ");
            if (parts.length == 2) {
                String[] startTime = parts[0].split(":");
                if (startTime.length == 2) {
                    try {
                        // Update instance variables
                        this.startHour = Integer.parseInt(startTime[0]);
                        this.startMinute = Integer.parseInt(startTime[1]);

                        // Update global variables
                        globalStartHour = this.startHour;
                        globalStartMinute = this.startMinute;
                    } catch (NumberFormatException e) {
                        System.err.println("Error parsing start time: " + e.getMessage());
                        return false;
                    }
                } else {
                    return false;
                }

                String[] endTime = parts[1].split(":");
                if (endTime.length == 2) {
                    try {
                        // Update instance variables
                        this.endHour = Integer.parseInt(endTime[0]);
                        this.endMinute = Integer.parseInt(endTime[1]);

                        // Update global variables
                        globalEndHour = this.endHour;
                        globalEndMinute = this.endMinute;
                    } catch (NumberFormatException e) {
                        System.err.println("Error parsing end time: " + e.getMessage());
                        return false;
                    }
                } else {
                    return false;
                }
            } else {
                return false;
            }
        }

        // Save updated global settings
        saveGlobalSettings();

        return true;
    }

    /**
     * Resets play time statistics
     * @return true if reset successful, false otherwise
     */
    public boolean resetPlayTimeStats() {
        if (!isParent) {
            return false;
        }

     
        this.totalPlayTime = 0.0f;
        this.sessionCount = 0;

        // Reset global statistics
        globalTotalPlayTime = 0.0f;
        globalGameStartCount = 0;

        // Save updated global settings to .dat file
        saveGlobalSettings();

        // Reset average time
        this.averagePlayTime = 0.0f;

        return true;
    }

    /**
     * Revives a pet in a save file
     * @param saveSlotId the ID of the save slot containing the pet
     * @return true if revival successful, false otherwise
     */
    public boolean revivePet(String saveSlotId) {
        if (!isParent) {
            return false;
        }

        try {

            int slotNumber = Integer.parseInt(saveSlotId);


            GameState gameState = SaveLoadManager.loadGame(slotNumber);


            if (gameState == null) {
                System.out.println("Save file does not exist in slot " + saveSlotId);
                return false;
            }


            gameState.setHealth(100);
            gameState.setSleep(100);
            gameState.setFullness(100);
            gameState.setHappiness(100);


            SaveLoadManager.saveGame(gameState, slotNumber);

            System.out.println("Successfully revived pet in save file " + saveSlotId);
            return true;
        } catch (NumberFormatException e) {
            System.out.println("Invalid save slot ID: " + saveSlotId);
            return false;
        } catch (IOException e) {
            System.out.println("Error accessing save file: " + e.getMessage());
            return false;
        } catch (Exception e) {
            System.out.println("Unexpected error during pet revival: " + e.getMessage());
            return false;
        }
    }

    /**
     * Checks if gameplay is allowed at the current time
     * @return true if allowed to play, false otherwise
     */
    public boolean isAllowedToPlay() {
        // Ensure using the latest global settings
        this.parentalControlsEnabled = globalParentalControlsEnabled;

        if (!parentalControlsEnabled) {
            return true;
        }

        LocalTime now = LocalTime.now();
        int currentHour = now.getHour();
        int currentMinute = now.getMinute();

        int currentTimeMinutes = currentHour * 60 + currentMinute;
        int startTimeMinutes = startHour * 60 + startMinute;
        int endTimeMinutes = endHour * 60 + endMinute;

        
        if (startTimeMinutes <= endTimeMinutes) {
          
            return currentTimeMinutes >= startTimeMinutes && currentTimeMinutes <= endTimeMinutes;
        } else {
          
            return currentTimeMinutes >= startTimeMinutes || currentTimeMinutes <= endTimeMinutes;
        }
    }

    /**
     * Checks if player is currently playing
     * @return true if playing, false otherwise
     */
    public boolean isCurrentlyPlaying() {
        return isCurrentlyPlaying;
    }

    /**
     * Checks if parental controls are enabled
     * @return true if enabled, false otherwise
     */
    public boolean isParentalControlsEnabled() {
        return parentalControlsEnabled;
    }

    /**
     * Gets the current play time limit setting
     * @return time range string in format "HH:MM - HH:MM"
     */
    public String getPlayTimeLimit() {
        return playTimeLimit;
    }

    /**
     * Gets the start hour for allowed play time
     * @return hour value (0-23)
     */
    public int getStartHour() {
        return startHour;
    }

    /**
     * Gets the start minute for allowed play time
     * @return minute value (0-59)
     */
    public int getStartMinute() {
        return startMinute;
    }

    /**
     * Gets the end hour for allowed play time
     * @return hour value (0-23)
     */
    public int getEndHour() {
        return endHour;
    }

    /**
     * Gets the end minute for allowed play time
     * @return minute value (0-59)
     */
    public int getEndMinute() {
        return endMinute;
    }

    /**
     * Checks if global parental controls are enabled
     * @return true if enabled, false otherwise
     */
    public static boolean isGlobalParentalControlsEnabled() {
        return globalParentalControlsEnabled;
    }

    /**
     * Gets the global play time limit setting
     * @return time range string in format "HH:MM - HH:MM"
     */
    public static String getGlobalPlayTimeLimit() {
        return globalPlayTimeLimit;
    }

    /**
     * Grants temporary parent access if password is correct
     * @param enteredPassword password to verify
     * @return true if access granted, false otherwise
     */
    public boolean accessParentalControlWithPassword(String enteredPassword) {
        boolean verified = verifyGlobalPassword(enteredPassword);
        if (verified) {
            // Temporarily set user as parent to access parental control features
            this.isParent = true;
        }
        return verified;
    }

    /**
     * Revokes parent access
     */
    public void exitParentalControl() {
        this.isParent = false;
    }

    /**
     * Returns a string representation of this player
     * @return string with player details
     */
    public String toString() {
        return "Player{" +
                "isParent=" + isParent +
                ", totalPlayTime=" + totalPlayTime +
                ", averagePlayTime=" + averagePlayTime +
                ", parentalControlsEnabled=" + parentalControlsEnabled +
                ", playTimeLimit='" + playTimeLimit + '\'' +
                ", isCurrentlyPlaying=" + isCurrentlyPlaying +
                '}';
    }
}