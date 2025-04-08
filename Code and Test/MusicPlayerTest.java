import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Unit tests is for the MusicPlayer class.
 *
 * @author Sze Wing Angel Zhang 
 * 251340454 
 * szha326
 */
public class MusicPlayerTest {

	// instance variables
    private MusicPlayer player;

    /**
     * Initializes a fresh MusicPlayer before each test.
     */
    @BeforeEach
    public void setUp() {
        player = MusicPlayer.getInstance();
        player.stop(); 
    }
    
    /**
     * Tests the default state of the MusicPlayer:
     * volume should be 100, not muted, and default track loaded.
     */
    @Test
    public void testInitialValues() {
        assertEquals(100, player.getVolume());
        assertFalse(player.isMuted());
        assertNull(player.getCurrentTrack()); 
    }


    /**
     * Tests setting volume to valid and invalid values.
     * Ensures that values are clamped within 0–100.
     */
    @Test
    public void testSetVolume() {
        player.setVolume(60);
        assertEquals(60, player.getVolume());

        player.setVolume(0);
        assertEquals(0, player.getVolume());

        player.setVolume(120); // exceeds max
        assertEquals(100, player.getVolume());

        player.setVolume(-10); // below min
        assertEquals(0, player.getVolume());
    }

    /**
     * Tests toggling mute and unmute.
     */
    @Test
    public void testMuteUnmute() {
        player.mute();
        assertTrue(player.isMuted());

        player.unmute();
        assertFalse(player.isMuted());
    }
    

    /**
     * Manual test to verify actual music playback, mute, and unmute.
     * This test plays a .wav file in loop mode. It plays for 6 seconds,
     * mutes for 5 seconds, then unmutes and continues playing for 8 more seconds.
     * To use: ensure there is a file named backgroundmusic.wav located in src/resources/
     */
    @Test
    public void testManualPlayMuteUnmute() {
        try {
            player.setVolume(60);
            player.play("src/resources/music1.wav");
            System.out.println("Looping music. Playing for 5 seconds...");
            Thread.sleep(5000);

            player.mute();
            System.out.println("Muted for 5 seconds."); 
            Thread.sleep(5000);

            player.unmute();
            System.out.println("Unmuted. Playing for 5 more seconds.");
            Thread.sleep(5000);

            System.out.println("Test complete.");

        } catch (Exception e) {
            e.printStackTrace();
            fail("Exception occurred during playback test.");
        }
    }
}
