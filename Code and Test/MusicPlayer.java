import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

/**
 * This class allows to plays a wav file, and allows mute/unmute and volume control.
 * The class supports looping playback, volume control from 0–100, and mute toggling.
 *
 * Example Use:
 *  MusicPlayer musicPlayer = new MusicPlayer();
 *
 * @author Sze Wing Angel Zhang 
 * 251340454 
 * szha326
 */
public class MusicPlayer {
    // instance variables
	private static MusicPlayer instance;
	private String currentTrack;
    private int volume = 100;            
    private boolean isMuted;
    private Clip clip;
    private FloatControl volumeControl;
  
    /**
     * Plays the wav file. If another track is already playing, it will be stopped.
     * The track will loop continuously until the program ends or the Clip is closed.
     *
     * @param trackPath relative path to the .wav file (e.g., "src/resources/backgroundmusic.wav")
     */
    public void play(String trackPath) {
        try {
            if (clip != null && clip.isOpen()) {
                clip.close();
            }

            this.currentTrack = trackPath;
            File file = new File(trackPath);
            AudioInputStream stream = AudioSystem.getAudioInputStream(file);
            clip = AudioSystem.getClip();
            clip.open(stream);

            if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                volumeControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
            }

            applyVolume();

            clip.setFramePosition(0);
            clip.loop(Clip.LOOP_CONTINUOUSLY); // Loop the music
            clip.start();
        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    /**
     * Mutes the currently playing track
     */
    public void mute() {
        isMuted = true;
        applyVolume();
    }

    /**
     * Unmutes the currently playing track
     */
    public void unmute() {
        isMuted = false;
        applyVolume();
    }

    /**
     * Sets the playback volume
     *
     * @param newVolume an integer between 0 and 100
     */
    public void setVolume(int newVolume) {
        volume = Math.max(0, Math.min(100, newVolume));
        applyVolume();
    }

    /**
     * Updates volume based on mute status and current volume level.
     */
    private void applyVolume() {
        if (volumeControl == null) return;

        if (isMuted) {
            volumeControl.setValue(volumeControl.getMinimum()); // Silence
        } else {
            float min = volumeControl.getMinimum(); // e.g., -50.0 dB
            float max = volumeControl.getMaximum(); // e.g., 50.0 dB
            float gain = min + (max - min) * (volume / 100.0f);
            volumeControl.setValue(gain);
        }
    }

    /**
     * Returns the path of the current track
     * @return track path string or null if none
     */
    public String getCurrentTrack() {
        return currentTrack;
    }

    /**
     * @return current volume level
     */
    public int getVolume() {
        return volume;
    }

    /**
     * Returns mute status
     * @return true if muted, false otherwise
     */
    public boolean isMuted() {
        return isMuted;
    }
    
    public void stop() {
        if (clip != null) {
            clip.stop();
            clip.flush();
            clip.close();
            clip = null;
        }
    }

    public static synchronized MusicPlayer getInstance() {
        if (instance == null) {
            instance = new MusicPlayer();
        }
        return instance;
    }

}
