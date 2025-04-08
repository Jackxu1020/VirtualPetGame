import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

/**
 * Displays the tutorial screen for the virtual pet game.
 * This screen provides new players with instructions on how to take care of their pet,
 * how the core mechanics work (feeding, playing, health), and explains the save system.
 *
 * <p>
 * This screen is implemented using Swing and appears in a scrollable text window.
 * It includes a "GO BACK" button that allows users to return to the previous screen.
 * </p>
 *
 * <p>
 * Example usage:
 * <pre>{@code
 * // Launch the tutorial screen
 * TutorialScreen tutorial = new TutorialScreen();
 * tutorial.setVisible(true);
 * }</pre>
 * </p>
 *
 * @version 1.0
 * @author Bella
 */

public class TutorialScreen extends JFrame {

    public TutorialScreen() {
        setTitle("How to Play");
        setSize(500, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Only close this screen

        // Create the text area with tutorial content
        JTextArea tutorialText = new JTextArea();
        tutorialText.setEditable(false);
        tutorialText.setLineWrap(true);
        tutorialText.setWrapStyleWord(true);
        tutorialText.setFont(new Font("SansSerif", Font.PLAIN, 14));
        tutorialText.setText("""
Welcome to the virtual pet game!
Your goal is to take care of your pet and keep it healthy and happy.

🔹 Basic Gameplay
• Your pet has three main needs: Hunger, Happiness, and Health.
• If any of these needs become too low, your pet will feel unhappy or get sick.
• Be sure to interact with your pet regularly to meet its needs.

🍖 Feeding Your Pet
• Click the “Feed” button to give your pet food.
• Different foods restore different levels of hunger.
• Avoid overfeeding — it can make your pet sick!

🎮 Playing with Your Pet
• Click the “Play” button to cheer your pet up and increase its happiness.
• Playing frequently keeps your pet entertained and emotionally healthy.

💊 Keeping Your Pet Healthy
• If your pet gets sick, give it medicine to recover.
• Feeding a balanced diet and allowing your pet to rest will help maintain its health.

💾 Saving System (Important!)
• The game supports 3 save slots only.
• If all 3 slots are full and you create a new pet, the system will automatically overwrite the slot with the oldest creation time.
• This means your earliest saved pet will be deleted to make room for the new one. Please choose carefully!

🔄 Auto-Save
• Your game is automatically saved every second.
• You don’t need to worry about losing your progress!

Enjoy taking care of your virtual companion! ❤
        """);

        JScrollPane scrollPane = new JScrollPane(tutorialText);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        // Create GO BACK button
        JButton backButton = new JButton("GO BACK");
        backButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        backButton.addActionListener((ActionEvent e) -> dispose()); // Closes this window

        // Layout
        JPanel bottomPanel = new JPanel();
        bottomPanel.add(backButton);

        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    // For testing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TutorialScreen().setVisible(true));
    }
}
