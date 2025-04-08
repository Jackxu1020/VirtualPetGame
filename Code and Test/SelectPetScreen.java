import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * SelectPetScreen provides a user interface for selecting a pet.
 * <p>
 * This screen displays a title and several pet options in a horizontal layout.
 * Each pet option is represented by an image, a descriptive text, and a select button.
 * When a pet is selected, a dialog prompts the user to give the pet a name.
 * After the pet is named, the game state is updated, saved, and the main gameplay screen is launched.
 * </p>
 *
 * @version 1.0
 * @author Zhenkang XU
 */
public class SelectPetScreen extends JFrame {

    public SelectPetScreen() {
        super("Select Pet");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 450);
        setLocationRelativeTo(null);
        setVisible(true);
        setResizable(false);

        // Title label at the top of the frame
        JLabel titleLabel = new JLabel("Pick Your Pet ^_^", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        add(titleLabel, BorderLayout.NORTH);

        // Main panel containing pet options arranged horizontally
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));

        // Add individual pet panels with image, description, and select button
        mainPanel.add(createPetPanel("resources/sheep/normal.png",
                "Sheep：\nCute little sheep\nLoves to eat but\ngets hungry more easily!", "Select"));

        mainPanel.add(createPetPanel("resources/duck/normal.png",
                "Duck：\nCute little duck\nLoves to sleep but\ngets sleepy more easily!", "Select"));

        mainPanel.add(createPetPanel("resources/dog/normal.png",
                "Dog：\nCute little dog\nLoves to play but\ngets sick more easily!", "Select"));

        getContentPane().add(mainPanel);

        // Bottom panel with a Home button to return to the main menu screen
        JPanel bottomPanel = new JPanel(new BorderLayout());
        JButton homeButton = new JButton("Home");
        homeButton.addActionListener(e -> {
            dispose();
            new MainMenuScreen();
        });

        bottomPanel.add(homeButton, BorderLayout.CENTER);
        getContentPane().add(bottomPanel, BorderLayout.SOUTH);
    }

    /**
     * Creates a panel representing a pet option.
     * <p>
     * The panel includes a scaled pet image, a non-editable text area displaying the pet's description,
     * and a button to select the pet. Spacing is added between components for visual clarity.
     * </p>
     *
     * @param imagePath   the file path to the pet's image.
     * @param description the description of the pet.
     * @param buttonText  the text to display on the select button.
     * @return a JPanel containing the pet image, description, and select button.
     */
    private JPanel createPetPanel(String imagePath, String description, String buttonText) {

        JPanel petPanel = new JPanel();
        petPanel.setLayout(new BoxLayout(petPanel, BoxLayout.Y_AXIS));
        petPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Pet image
        JLabel petImageLabel = new JLabel();
        ImageIcon icon = new ImageIcon(imagePath);
        Image scaledImage = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        icon = new ImageIcon(scaledImage);
        petImageLabel.setIcon(icon);
        petImageLabel.setAlignmentX(CENTER_ALIGNMENT);

        // Pet description text
        JTextArea petDescription = new JTextArea(description);
        petDescription.setEditable(false);
        petDescription.setOpaque(false);
        petDescription.setAlignmentX(CENTER_ALIGNMENT);

        // Select button that launches the naming dialog when clicked
        JButton selectButton = getSelectButton(description, buttonText);

        petPanel.add(petImageLabel);
        petPanel.add(Box.createVerticalStrut(10));
        petPanel.add(petDescription);
        petPanel.add(Box.createVerticalStrut(10));
        petPanel.add(selectButton);
        petPanel.add(Box.createVerticalStrut(20));

        return petPanel;
    }

    /**
     * Creates and returns a JButton for selecting a pet.
     * <p>
     * The button's action listener extracts the pet name from the provided description,
     * creates a new {@link VirtualPet} instance based on the pet type,
     * and opens a dialog to prompt the user to give the pet a name.
     * </p>
     *
     * @param description the pet description from which the pet's name is extracted.
     * @param buttonText  the text to display on the button.
     * @return a JButton configured with an action listener for pet selection.
     */
    private JButton getSelectButton(String description, String buttonText) {
        JButton selectButton = new JButton(buttonText);
        selectButton.setAlignmentX(CENTER_ALIGNMENT);

        selectButton.addActionListener(e -> {
            // Extract the pet name from the description (e.g., "Sheep：...")
            String petName = description.split("：")[0];
            PetType petType = PetType.valueOf(petName.toUpperCase());
            VirtualPet selectedPet = new VirtualPet(petName, petType);

            // Open a dialog to prompt the user to name the selected pet
            GivePetNameDialog nameDialog = new GivePetNameDialog(this, selectedPet);
            nameDialog.setVisible(true);
        });

        return selectButton;
    }

    /**
     * GivePetNameDialog is a modal dialog that prompts the user to provide a name for the selected pet.
     * <p>
     * Upon confirmation, the pet's name is updated, a new game state is created and saved,
     * and the main gameplay screen is launched. If the user cancels, the dialog is simply closed.
     * </p>
     */
    class GivePetNameDialog extends JDialog {

        /**
         * Constructs a new GivePetNameDialog.
         * <p>
         * This dialog is modal and requires the parent frame and the selected pet as parameters.
         * It sets up the UI to include a label, a text field for entering the pet's name,
         * and Confirm and Cancel buttons to handle user actions.
         * </p>
         *
         * @param parent      the parent JFrame from which the dialog is displayed.
         * @param selectedPet the VirtualPet instance for which a name is being set.
         */
        public GivePetNameDialog(JFrame parent, VirtualPet selectedPet) {
            super(parent, "Give Your Pet a Name!", true);
            setLayout(new BorderLayout());
            setSize(300, 150);
            setLocationRelativeTo(parent);

            // Label prompting the user to enter a pet name
            JLabel titleLabel = new JLabel("Please enter the name of your pet：", SwingConstants.CENTER);
            add(titleLabel, BorderLayout.NORTH);

            // Text field pre-filled with the pet's current name if available
            JTextField nameField = new JTextField(selectedPet.getName() != null ? selectedPet.getName() : "");
            add(nameField, BorderLayout.CENTER);

            // Panel containing the Confirm and Cancel buttons
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));

            // Confirm button: updates pet name, saves game state, and launches the gameplay screen
            JButton confirmButton = new JButton("Confirm");
            confirmButton.addActionListener(e -> {
                String newName = nameField.getText().trim();
                if (!newName.isEmpty()) {
                    selectedPet.setName(newName);

                    GameState gameState = new GameState();
                    Inventory inventory = new Inventory();
                    gameState.updateGameState(selectedPet, inventory);


                    try {
                        int fileIndex;
                        if (SaveLoadManager.getSaveFileCounts() < 3) {
                            fileIndex = SaveLoadManager.getSaveFileCounts() + 1;
                            System.out.println("Saving to next empty slot: slot " + fileIndex);
                        } else {
                            fileIndex = SaveLoadManager.findOldestSlot();
                            System.out.println("All slots full. Replacing oldest slot: slot " + fileIndex);
                        }

                        SaveLoadManager.saveGame(gameState, fileIndex);

                        // Launch the gameplay screen with the saved game file
                        GamePlayScreen gamePlayScreen = new GamePlayScreen("saves/slot" + fileIndex + ".json");
                        dispose();
                        parent.dispose();

                    } catch (IOException ex) {
                        throw new RuntimeException(ex);
                    }
                } else {
                    JOptionPane.showMessageDialog(this, "The name cannot be empty!");
                }
            });

            // Cancel button: closes the dialog without making changes
            JButton cancelButton = new JButton("Cancel");
            cancelButton.addActionListener(e -> dispose());

            buttonPanel.add(confirmButton);
            buttonPanel.add(cancelButton);
            add(buttonPanel, BorderLayout.SOUTH);
        }
    }
}
