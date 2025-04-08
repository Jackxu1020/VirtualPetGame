import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

/**
 * GUI screen for managing game settings.
 *
 * Allows the player to adjust background music, volume, mute/unmute, and trigger a manual game save.
 * Uses a card layout to switch between main settings and music-specific settings.
 *
 * Author: Jinke Li
 */
public class SettingScreen {
    private boolean isPlaying;
    private JFrame frame;
    private CardLayout cardLayout;
    private JPanel mainPanel;

    private final String[] musicFiles = {
        "resources/music1.wav",
        "resources/music2.wav",
        "resources/music3.wav"
    };

    private MusicPlayer musicPlayer = new MusicPlayer();

    /**
     * Constructs and displays the settings screen UI.
     */
    public SettingScreen() {
        this.isPlaying = false;

        frame = new JFrame("Setting");
        frame.setSize(600, 450);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(createMainSettingPanel(), "Main");
        mainPanel.add(createMusicSettingPanel(), "Music");

        frame.setContentPane(mainPanel);
        frame.setVisible(true);
    }


    /**
     * Returns whether music is currently playing.
     *
     * @return true if music is active, false otherwise.
     */
    public boolean isPlaying() {return isPlaying;}

    /**
     * Creates the main settings panel with options like
     * navigating to music settings or saving the game.
     *
     * @return JPanel containing main setting buttons.
     */
    private JPanel createMainSettingPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(30, 100, 30, 100));

        JButton musicBtn = new JButton("Music");
        JButton saveBtn = new JButton("Save Game");
        JButton backBtn = new JButton("Back");

        musicBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        saveBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        backBtn.setAlignmentX(Component.CENTER_ALIGNMENT);

        musicBtn.addActionListener(e -> cardLayout.show(mainPanel, "Music"));
        saveBtn.addActionListener(e -> showSaveConfirmation());
        backBtn.addActionListener(e -> {
            frame.dispose();
        });

        panel.add(musicBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(saveBtn);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(backBtn);

        return panel;
    }

    /**
     * Creates the panel to control background music.
     * Includes music file selector, volume slider, and toggle.
     *
     * @return JPanel containing music-related settings.
     */
    private JPanel createMusicSettingPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 80, 20, 80));

        String[] musicOptions = {"Music1", "Music2", "Music3"};
        JComboBox<String> musicCombo = new JComboBox<>(musicOptions);
        JSlider musicSlider = new JSlider(0, 100, musicPlayer.getVolume());

        JCheckBox musicToggle = new JCheckBox("Music", !musicPlayer.isMuted());

        JButton save = new JButton("Save");
        JButton cancel = new JButton("Cancel");

        save.addActionListener(e -> {
            isPlaying = true;
            int selectedIndex = musicCombo.getSelectedIndex();
            String selectedMusicFile = musicFiles[selectedIndex];
            MusicPlayer.getInstance().play(selectedMusicFile);
            MusicPlayer.getInstance().setVolume(musicSlider.getValue());
            if (musicToggle.isSelected()) {
                MusicPlayer.getInstance().unmute();
            } else {
                MusicPlayer.getInstance().mute();
            }
            cardLayout.show(mainPanel, "Main");
        });

        cancel.addActionListener(e -> cardLayout.show(mainPanel, "Main"));

        panel.add(musicToggle);
        panel.add(musicCombo);
        panel.add(musicSlider);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(save);
        buttonPanel.add(cancel);

        panel.add(Box.createRigidArea(new Dimension(0, 20)));
        panel.add(buttonPanel);

        return panel;
    }

    /**
     * Shows a popup confirmation that the game has been saved.
     */
    private void showSaveConfirmation() {
        JOptionPane.showOptionDialog(
                frame,
                "Game saved successfully",
                "",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                new Object[]{"Confirm"},
                "Confirm"
        );
    }

    /**
     * Entry point to launch the setting screen standalone.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(SettingScreen::new);
    }
}
