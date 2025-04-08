import java.io.File;
import java.time.LocalTime;

public class PlayerTest {

    public static void main(String[] args) {
        runAllTests();
    }

    private static void runAllTests() {
        System.out.println("TEST 1: Password Tests");
        testPasswordFunctionality();

        System.out.println("\nTEST 2: Parental Control Tests");
        testParentalControls();

        System.out.println("\nTEST 3: Time Tracking Tests");
        testTimeTracking();

        System.out.println("\nTEST 4: Time Limits Tests");
        testTimeLimits();

        System.out.println("\nTEST 5: Edge Cases Tests");
        testEdgeCases();
    }

    private static void testPasswordFunctionality() {
        resetGlobalSettings();

        Player player = new Player(false);
        boolean result1 = Player.setGlobalPassword("testpass123");
        System.out.println("1.1 Initialize password: " + (result1 ? "PASS" : "NOT PASS"));

        boolean result2 = Player.setGlobalPassword("newpass456");
        System.out.println("1.2 Set password twice: " + (!result2 ? "PASS" : "NOT PASS"));

        boolean result3 = Player.verifyGlobalPassword("testpass123");
        System.out.println("1.3 Verify correct password: " + (result3 ? "PASS" : "NOT PASS"));

        boolean result4 = Player.verifyGlobalPassword("wrongpass");
        System.out.println("1.4 Verify incorrect password: " + (!result4 ? "PASS" : "NOT PASS"));

        boolean result5 = player.accessParentalControlWithPassword("testpass123");
        System.out.println("1.5 Access with password: " + ((result5 && player.isParent()) ? "PASS" : "NOT PASS"));

        player.exitParentalControl();
        System.out.println("1.6 Exit parental control: " + (!player.isParent() ? "PASS" : "NOT PASS"));
    }

    private static void testParentalControls() {
        resetGlobalSettings();
        Player.setGlobalPassword("testpass123");

        Player normalPlayer = new Player(false);
        boolean result1 = normalPlayer.setParentalControls(true, "08:00 - 20:00");
        System.out.println("2.1 Set controls as non-parent: " + (!result1 ? "PASS" : "NOT PASS"));

        Player parentPlayer = new Player(true);
        boolean result2 = parentPlayer.setParentalControls(true, "08:00 - 20:00");
        System.out.println("2.2 Set controls as parent: " + ((result2 && Player.isGlobalParentalControlsEnabled()) ? "PASS" : "NOT PASS"));

        boolean result3 = parentPlayer.setParentalControls(false, "08:00 - 20:00");
        System.out.println("2.3 Disable controls: " + ((result3 && !Player.isGlobalParentalControlsEnabled()) ? "PASS" : "NOT PASS"));

        boolean result4 = parentPlayer.setParentalControls(true, "14:30 - 21:45");
        System.out.println("2.4 Change time range: " + ((result4 && "14:30 - 21:45".equals(Player.getGlobalPlayTimeLimit())) ? "PASS" : "NOT PASS"));
    }

    private static void testTimeTracking() {
        resetGlobalSettings();

        Player player = new Player(false);
        int startCount = Player.getGlobalGameStartCount();

        TestTimeCallback callback = new TestTimeCallback();
        boolean result1 = player.startPlaying(callback);
        System.out.println("3.1 Start tracking: " + ((result1 && Player.getGlobalGameStartCount() > startCount) ? "PASS" : "NOT PASS"));

        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            // Ignore
        }

        float totalTimeBefore = Player.getGlobalTotalPlayTime();
        player.stopPlaying();
        float totalTimeAfter = Player.getGlobalTotalPlayTime();
        System.out.println("3.2 Stop tracking: " + ((totalTimeAfter >= totalTimeBefore && !player.isCurrentlyPlaying()) ? "PASS" : "NOT PASS"));

        Player.setGlobalPassword("testpass");
        Player parentPlayer = new Player(true);
        parentPlayer.startPlaying(callback);
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            // Ignore
        }
        parentPlayer.stopPlaying();

        boolean result3 = parentPlayer.resetPlayTimeStats();
        System.out.println("3.3 Reset statistics: " + ((result3 && Player.getGlobalTotalPlayTime() == 0.0f) ? "PASS" : "NOT PASS"));
    }

    private static void testTimeLimits() {
        resetGlobalSettings();

        LocalTime now = LocalTime.now();
        int currentHour = now.getHour();

        Player parentPlayer = new Player(true);
        int startHour = (currentHour + 23) % 24;
        int endHour = (currentHour + 1) % 24;

        String allowedTimeRange = String.format("%02d:00 - %02d:00", startHour, endHour);
        parentPlayer.setParentalControls(true, allowedTimeRange);

        Player testPlayer = new Player(false);
        boolean isAllowed = testPlayer.isAllowedToPlay();
        System.out.println("4.1 Allow play now: " + (isAllowed ? "PASS" : "NOT PASS"));

        int disallowStartHour = (currentHour + 1) % 24;
        int disallowEndHour = (currentHour + 2) % 24;

        String disallowedTimeRange = String.format("%02d:00 - %02d:00", disallowStartHour, disallowEndHour);
        parentPlayer.setParentalControls(true, disallowedTimeRange);

        isAllowed = testPlayer.isAllowedToPlay();
        System.out.println("4.2 Disallow play now: " + (!isAllowed ? "PASS" : "NOT PASS"));

        parentPlayer.setParentalControls(false, disallowedTimeRange);
        isAllowed = testPlayer.isAllowedToPlay();
        System.out.println("4.3 Disable time limits: " + (isAllowed ? "PASS" : "NOT PASS"));
    }

    private static void testEdgeCases() {
        Player parentPlayer = new Player(true);
        boolean result1 = parentPlayer.setParentalControls(true, "invalid_time_format");
        System.out.println("5.1 Invalid time format: " + (!result1 ? "PASS" : "NOT PASS"));

        boolean result2 = parentPlayer.setParentalControls(true, "22:00 - 06:00");
        LocalTime midnight = LocalTime.of(0, 30);
        boolean wouldBeAllowed = isTimeAllowed(midnight, 22, 0, 6, 0);
        System.out.println("5.2 Cross-midnight time range: " + ((result2 && wouldBeAllowed) ? "PASS" : "NOT PASS"));

        boolean result3 = parentPlayer.setParentalControls(true, "14:00 - 14:00");
        LocalTime testTime = LocalTime.of(14, 0);
        boolean exactTimeAllowed = isTimeAllowed(testTime, 14, 0, 14, 0);
        System.out.println("5.3 Same start and end time: " + ((result3 && exactTimeAllowed) ? "PASS" : "NOT PASS"));
    }

    private static class TestTimeCallback implements Player.TimeCheckCallback {
        @Override
        public void onTimeRestrictionViolation(String allowedTimeRange) {
        }

        @Override
        public void onPeriodicCheck(boolean isAllowed) {
        }
    }

    private static void resetGlobalSettings() {
        try {
            File settingsFile = new File("global_settings.dat");
            if (settingsFile.exists()) {
                settingsFile.delete();
            }
        } catch (Exception e) {
            // Ignore
        }
    }

    private static boolean isTimeAllowed(LocalTime now, int startHour, int startMinute, int endHour, int endMinute) {
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
}