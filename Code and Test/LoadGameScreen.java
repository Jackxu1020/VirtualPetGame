import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * This class provides a graphical interface for selecting and loading a saved game slot.
 * The pet type, and icon are fixed, all other values are loaded from the save files.
 *
 * Example use:
 * SwingUtilities.invokeLater(() -> new LoadGameScreen().setVisible(true));
 * 
 * @author Sze Wing Angel Zhang 
 * 251340454 
 * szha326
 */
public class LoadGameScreen extends JFrame {
    // instance variables
	private int selectedSlot = -1;
    private final List<JPanel> cardPanels = new ArrayList<>();

    /**
     * Constructor initializes the screen and loads save data for each slot.
     */
    public LoadGameScreen() {
        setTitle("Load Game");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        setVisible(true);

        // Title at the top
        JLabel titleLabel = new JLabel("Load Game", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        add(titleLabel, BorderLayout.NORTH);
        
        SaveGameData[] saves = {
        	    new SaveGameData(), 
        	    new SaveGameData(), 
        	    new SaveGameData()
        	};

        // Load data from save files
        for (int i = 0; i < saves.length; i++) {
            try {
            	GameState loaded = SaveLoadManager.loadGame(i + 1);
            	if (loaded != null) {
            	    saves[i].petName = orPlaceholder(loaded.getPetName());
            	    saves[i].petType = orPlaceholder(loaded.getPetType());

            	    if (loaded.getPetType() != null && !loaded.getPetType().isEmpty()) {
            	        saves[i].imagePath = "resources/" + loaded.getPetType().toLowerCase() + "/normal.png";
            	    }

            	    saves[i].health = toDisplayValue(loaded.getHealth());
            	    saves[i].happiness = toDisplayValue(loaded.getHappiness());
            	    saves[i].sleep = toDisplayValue(loaded.getSleep());
            	    saves[i].fullness = toDisplayValue(loaded.getFullness());
            	    saves[i].score = toScoreDisplay(loaded.getScore());
            	    saves[i].lastSavedTime = (loaded.getLastSavedTime() != null)
            	            ? loaded.getLastSavedTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))
            	            : "--";
                }
            } catch (IOException e) {
                System.err.println("Failed to load slot " + (i + 1));
            }
        }

        // Pet cards
        JPanel cardsPanel = new JPanel(new GridLayout(1, saves.length, 5, 5));
        for (int i = 0; i < saves.length; i++) {
            JPanel card = createPetCard(saves[i], i);
            cardsPanel.add(card);
            cardPanels.add(card);
        }

        // Buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        JButton confirmButton = new JButton("Confirm");
        JButton homeButton = new JButton("Home");
        confirmButton.setPreferredSize(new Dimension(100, 30));
        homeButton.setPreferredSize(new Dimension(100, 30));

        confirmButton.addActionListener(e -> {
            if (selectedSlot == -1) {
                JOptionPane.showMessageDialog(this, "Please select a save slot.");
                return;
            }
            
            try {
                GamePlayScreen gamePlayScreen = new GamePlayScreen("saves/slot" + (selectedSlot + 1) + ".json");
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }

            JOptionPane.showMessageDialog(this,
                    "You have successfully loaded slot " + (selectedSlot + 1) + "!",
                    "Load Successful",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
        });

        homeButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this,
                    "Returning to main menu...",
                    "Back to Menu",
                    JOptionPane.INFORMATION_MESSAGE);
            
            new MainMenuScreen();
            dispose(); 
        });


        buttonPanel.add(confirmButton);
        buttonPanel.add(homeButton);

        add(cardsPanel, BorderLayout.CENTER);
        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Creates a save card panel for one slot, containing image, stats and select button.
     *
     * @param data      SaveGameData object containing data to display
     * @param slotIndex Index of the save slot (0-based)
     * @return JPanel representing the card
     */
    private JPanel createPetCard(SaveGameData data, int slotIndex) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createLineBorder(Color.GRAY));
        card.setBackground(Color.WHITE);

        // Slot number label
        JLabel slotLabel = new JLabel("Slot " + (slotIndex + 1));
        slotLabel.setFont(new Font("Comic Sans MS", Font.BOLD, 14));
        slotLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        card.add(Box.createVerticalStrut(10));
        card.add(slotLabel);
        card.add(Box.createVerticalStrut(5));

        // Check if this slot has data (all fields are "--")
        boolean hasData =
                !data.health.equals("--") ||
                !data.happiness.equals("--") ||
                !data.sleep.equals("--") ||
                !data.fullness.equals("--") ||
                !data.score.equals("--") ||
                !data.lastSavedTime.equals("--") ||
                !data.petName.equals("--");

        if (hasData) {
            // Pet icon
            JLabel iconLabel = new JLabel();
            if (data.imagePath != null) {
                iconLabel.setIcon(loadAndResizeIcon(data.imagePath, 64, 64));
            }
            iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

            JLabel nameLabel = new JLabel("Pet Name: " + orPlaceholder(data.petName));
            JLabel typeLabel = new JLabel("Type: " + data.petType);
            JLabel healthLabel = new JLabel("Health: " + orPlaceholder(data.health));
            JLabel happinessLabel = new JLabel("Happiness: " + orPlaceholder(data.happiness));
            JLabel sleepLabel = new JLabel("Sleep: " + orPlaceholder(data.sleep));
            JLabel fullnessLabel = new JLabel("Fullness: " + orPlaceholder(data.fullness));
            JLabel scoreLabel = new JLabel("Score: " + orPlaceholder(data.score));
            JLabel timeLabel = new JLabel("Last Saved: " + orPlaceholder(data.lastSavedTime));

            JButton selectButton = new JButton("Select");
            selectButton.addActionListener((ActionEvent e) -> highlightSelectedCard(slotIndex));

            // Center align all components
            for (JComponent comp : new JComponent[]{iconLabel, nameLabel, typeLabel, healthLabel,
                    happinessLabel, sleepLabel, fullnessLabel, scoreLabel, timeLabel, selectButton}) {
                comp.setAlignmentX(Component.CENTER_ALIGNMENT);
            }

            // Add components to card
            card.add(iconLabel);
            card.add(Box.createVerticalStrut(10));
            card.add(nameLabel);
            card.add(typeLabel);
            card.add(healthLabel);
            card.add(happinessLabel);
            card.add(sleepLabel);
            card.add(fullnessLabel);
            card.add(scoreLabel);
            card.add(timeLabel);
            card.add(Box.createVerticalStrut(10));
            card.add(selectButton);
        } else {
            // If no data, add some empty space so card height is balanced
            card.add(Box.createVerticalStrut(100));
        }

        return card;
    }


    /**
     * Highlights the selected card by updating its border.
     *
     * @param index Index of selected card
     */
    private void highlightSelectedCard(int index) {
        selectedSlot = index;
        for (int i = 0; i < cardPanels.size(); i++) {
            JPanel panel = cardPanels.get(i);
            panel.setBorder(BorderFactory.createLineBorder(i == index ? Color.PINK : Color.GRAY, i == index ? 5 : 1));
        }
    }

    /**
     * Resizes and loads an icon from file.
     *
     * @param path   File path of the image
     * @param width  Target width
     * @param height Target height
     * @return Scaled ImageIcon
     */
    private ImageIcon loadAndResizeIcon(String path, int width, int height) {
        ImageIcon icon = new ImageIcon(path);
        Image scaled = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    /**
     * Displays "--" for null/empty values or returns the actual string.
     *
     * @param value Input string
     * @return Original value or "--" if blank
     */
    private String orPlaceholder(String value) {
        return (value == null || value.trim().isEmpty()) ? "--" : value;
    }

    /**
     * Converts an Integer stat (health/happiness/etc.) to to a percentage string out of 100.
     *
     * @param value Stat value
     * @return Formatted value with '%' or "--"
     */
    private String toDisplayValue(Integer value) {
        if (value == null || value == 0) return "--";
//        int percentage = (int) Math.round((value / 100) * 100);
        return value + "%";
    }


    /**
     * Converts Integer score to string or "--".
     *
     * @param value Score value
     * @return Score as String or "--"
     */
    private String toScoreDisplay(Integer value) {
        return (value == null || value == 0) ? "--" : String.valueOf(value);
    }

    /**
     * Represents pet info and loaded attributes per save slot.
     */
    static class SaveGameData {
        String petName = "--";
        String petType = "--" ;
        String imagePath = null;
        String health = "--";
        String happiness = "--";
        String sleep = "--";
        String fullness = "--";
        String score = "--";
        String lastSavedTime = "--";

        public SaveGameData() {}
    }

    /**
     * Launches the LoadGameScreen as a standalone window.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoadGameScreen().setVisible(true));
    }
}
