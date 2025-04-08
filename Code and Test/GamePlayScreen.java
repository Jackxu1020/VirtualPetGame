import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/**
 * The main gameplay screen that displays and manages the virtual pet’s state and interactive elements.
 * <p>
 * This class extends {@link javax.swing.JFrame} and requires a file path string in the constructor
 * for loading or saving the pet’s data. Users can view and interact with the pet within this screen
 * by updating health, sleepiness, happiness, and fullness.
 * </p>
 *
 * <p><strong>Primary features include:</strong></p>
 * <ul>
 *   <li>Display and update the pet’s status (health, sleepiness, happiness, fullness)</li>
 *   <li>Arrange and manage various interactive buttons within the UI</li>
 *   <li>Synchronize status with game progression and external data files</li>
 * </ul>
 *
 * @version 1.0.1
 * @author Zhenkang Xu
 */


public class GamePlayScreen extends JFrame {
    /** Represents the selected slot index for the game data. */
    private int slot;
    /** Displays the pet's each state level. */
    private JProgressBar healthBar, sleepinessBar, happinessBar, fullnessBar;
    /** The virtual pet managed by this screen. */
    private VirtualPet pet;

    private SaveLoadManager saveLoadManager;

    private Inventory inventory;
    /** Tracks the current state value of the game. */
    private GameState gameState;
    /** The panel aiding in pet display or interaction. */
    private JPanel petPanel;
    /** Icons used to represent various button. */
    ImageIcon warningIcon, homeIcon, settingIcon;

    /**
     * Constructs a new GamePlayScreen based on the given file path.
     * <p>
     * This constructor initializes the game state, inventory, and virtual pet from the specified save file.
     * It sets up the UI components including status bars, buttons, and a pet display panel. A timer is also started
     * to update the pet's status periodically.
     * </p>
     *
     * @param file The file path string used to determine the game slot and load game data.
     * @throws IOException if there is an error reading the file.
     */
    public GamePlayScreen(String file) throws IOException {
        // Get slot Number
        this.slot = Integer.parseInt(file.substring(10,file.length()-5));
        gameState = SaveLoadManager.loadGame(slot);

        // Load Inventory from save file
        inventory = new Inventory();
        inventory = gameState.getInventory();

        // Load Pet From save file
        this.pet = new VirtualPet(gameState.getPetName(), PetType.valueOf(gameState.getPetType()));
        pet.setHealth(gameState.getHealth());
        pet.setSleep(gameState.getSleep());
        pet.setHappiness(gameState.getHappiness());
        pet.setFullness(gameState.getFullness());

        String basePath = "resources/"+gameState.getPetName().toLowerCase()+"/" // TODO no susage
                +String.valueOf(pet.getCurrentState()).toLowerCase()+".png";

        // Initialize Screen
        setTitle("Game Screen");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        setResizable(false);
        setVisible(true);

        // Prepare warning icon to show the state is too low
        warningIcon = new ImageIcon("resources/warning.png");
        warningIcon = new ImageIcon(warningIcon.getImage().getScaledInstance(20, 20, Image.SCALE_SMOOTH));

        // Draw the Status Bar showing pet's state
        JPanel statusPanel = new JPanel();
        statusPanel.setLayout(new GridLayout(4, 2, 10, 10)); // For labels and bars

        JLabel healthLabel = new JLabel("Health");
        healthLabel.setIcon(null);
        healthBar = new JProgressBar(0, 100);
        healthBar.setValue(gameState.getHealth());
        healthBar.setStringPainted(true);

        JLabel sleepinessLabel = new JLabel("Sleepiness");
        sleepinessBar = new JProgressBar(0, 100);
        sleepinessBar.setValue(gameState.getSleep());
        sleepinessBar.setStringPainted(true);

        JLabel happinessLabel = new JLabel("Happiness");
        happinessBar = new JProgressBar(0, 100);
        happinessBar.setValue(gameState.getHappiness());
        happinessBar.setStringPainted(true);

        JLabel fullnessLabel = new JLabel("Fullness");
        fullnessBar = new JProgressBar(0, 100);
        fullnessBar.setValue(gameState.getFullness());
        fullnessBar.setStringPainted(true);

        JLabel scoredLabel = new JLabel("Scored: " + gameState.getScore());

        statusPanel.add(healthBar);
        statusPanel.add(healthLabel);
        statusPanel.add(sleepinessBar);
        statusPanel.add(sleepinessLabel);
        statusPanel.add(happinessBar);
        statusPanel.add(happinessLabel);
        statusPanel.add(fullnessBar);
        statusPanel.add(fullnessLabel);

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(statusPanel);
        topPanel.add(scoredLabel);

        add(topPanel, BorderLayout.NORTH);

        // Reduce the value is called at regular intervals (e.g. every 5 seconds).
        // Update status bar according to the changing of game state
        Timer statDecreaseTimer = new Timer(2000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pet.decreaseStatsOverTime();
                gameState.updateGameState(pet, inventory);
                updateBars(gameState.getHealth(), gameState.getSleep(),
                        gameState.getHappiness(), gameState.getFullness());

                scoredLabel.setText("Scored: " + gameState.getScore());

                if (gameState.getSleep() < 50*0.25) {
                    sleepinessLabel.setIcon(warningIcon);
                } else {sleepinessLabel.setIcon(null);}

                if (gameState.getFullness() < 50*0.25) {
                    fullnessLabel.setIcon(warningIcon);
                } else {fullnessLabel.setIcon(null);}

                if (gameState.getHappiness() < 50*0.25) {
                    happinessLabel.setIcon(warningIcon);
                } else {happinessLabel.setIcon(null);}

                if (gameState.getHealth() < 50*0.25) {
                    healthLabel.setIcon(warningIcon);
                } else {healthLabel.setIcon(null);}

                inventory.addItem(new FoodItem("apple",1,10));
                inventory.addItem(new FoodItem("banana",1,20));
                inventory.addItem(new FoodItem("orange",1,30));

                inventory.addItem(new GiftItem("ball",1,10));
                inventory.addItem(new GiftItem("car",1,20));
                inventory.addItem(new GiftItem("jellycat",1,30));

                // Save game state to file
                try {
                    SaveLoadManager.saveGame(gameState, slot);
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                displayPet();
            }
        });

        // Create and configure the main screen panel used to display the pet
        petPanel = new JPanel(new BorderLayout()){
          private final Image backgroundImage = new ImageIcon("resources/background1.jpg").getImage();
          @Override
          protected void paintComponent(Graphics g) {
              super.paintComponent(g);
              g.drawImage(backgroundImage, 0, 0,getWidth(),getHeight(), this);
          }
        };
        petPanel.setPreferredSize(new Dimension(100,100));
        petPanel.setBorder(BorderFactory.createLineBorder(Color.PINK, 5));
        add(petPanel, BorderLayout.CENTER);
        displayPet(); // Show pets immediately, otherwise there will be a one-second delay before they are displayed.

        // Buttons for various actions
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);

        // Display Home Button
        JButton homeButton = new JButton("Home");
        homeButton.setPreferredSize( new Dimension( 200, 60 ));
        homeButton.setFont(new Font("Comic Sans MS", Font.PLAIN, 30));
        homeIcon = new ImageIcon("resources/home.png");
        homeIcon = new ImageIcon(homeIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH));
        homeButton.setIcon(homeIcon);
        homeButton.setMnemonic('H');
        homeButton.addActionListener(goToMainMenuScreen -> {
            statDecreaseTimer.stop();
            MainMenuScreen mainMenuScreen = new MainMenuScreen();
            dispose();
        });

        // Display Setting Button
        JButton settingButton = new JButton("Setting");
        settingButton.setPreferredSize( new Dimension( 200, 60 ));
        settingIcon = new ImageIcon("resources/setting.png");
        settingIcon = new ImageIcon(settingIcon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH));
        settingButton.setIcon(settingIcon);
        settingButton.setFont(new Font("Comic Sans MS", Font.PLAIN, 30));
        settingButton.setMnemonic('T'); // Alt + T for shortcut
        settingButton.addActionListener(e -> {
            SettingScreen settingScreen = new SettingScreen();
        });

        topPanel.add(homeButton);
        topPanel.add(settingButton);

        statDecreaseTimer.setRepeats(true);
        statDecreaseTimer.start();

        setVisible(true);
    }

    /**
     * Creates and returns a JPanel containing action buttons for various pet interactions.
     * <p>
     * The panel includes buttons for taking the pet to the vet, sleeping, feeding, giving gifts,
     * playing, exercising, and opening the inventory. Each button is configured with appropriate
     * fonts, sizes, mnemonics, and action listeners.
     * </p>
     *
     * @return a JPanel with all interactive buttons.
     */
    private JPanel createButtonPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        // [Take to Vet] Button
        JButton vetButton = new JButton("Take to Vet");
        vetButton.setFont(new Font("Comic Sans MS", Font.PLAIN, 15));
        vetButton.setMnemonic('V'); // Alt + V for shortcut
        vetButton.addActionListener(e -> {
            if (pet.getCurrentState() == VirtualPet.PetState.DEAD) {
                JOptionPane.showMessageDialog(null, "Your pet is dead, game over", "Virtual Pet", JOptionPane.INFORMATION_MESSAGE);
            } else if (pet.getCurrentState() == VirtualPet.PetState.SLEEPING) {
                JOptionPane.showMessageDialog(null, "Your pet is sleeping, please do not disturb.", "Virtual Pet", JOptionPane.INFORMATION_MESSAGE);
            } else if (pet.getCurrentState() == VirtualPet.PetState.ANGRY) {
                JOptionPane.showMessageDialog(null, "Your pet is angry, play or give gift to make him happy.", "Virtual Pet", JOptionPane.INFORMATION_MESSAGE);
            }

            pet.takeToVet();
            if (pet.getIsPlayColdDown() == true && pet.getCurrentState() != VirtualPet.PetState.DEAD) {
                JOptionPane.showMessageDialog(null, "Function in cold down", "Virtual Pet", JOptionPane.INFORMATION_MESSAGE);
            }else if (pet.getIsPlayColdDown() == false && pet.getCurrentState() != VirtualPet.PetState.DEAD) {
                JOptionPane.showMessageDialog(null, "Your pet has been taken to the vet. （COLDDOWN: 10sec）", "Virtual Pet", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // [Sleep] Button
        JButton sleepButton = new JButton("Sleep");
        sleepButton.setFont(new Font("Comic Sans MS", Font.PLAIN, 15));
        sleepButton.setMnemonic('S'); // Alt + S for shortcut
        sleepButton.addActionListener(e -> {
            if (pet.getCurrentState() == VirtualPet.PetState.DEAD) {
                JOptionPane.showMessageDialog(null, "Your pet is dead, game over", "Virtual Pet", JOptionPane.INFORMATION_MESSAGE);
            } else if (pet.getCurrentState() == VirtualPet.PetState.SLEEPING) {
                JOptionPane.showMessageDialog(null, "Your pet is sleeping, please do not disturb.", "Virtual Pet", JOptionPane.INFORMATION_MESSAGE);
            } else if (pet.getCurrentState() == VirtualPet.PetState.ANGRY) {
                JOptionPane.showMessageDialog(null, "Your pet is angry, play or give gift to make him happy.", "Virtual Pet", JOptionPane.INFORMATION_MESSAGE);
            } else {
                pet.goSleep();
                JOptionPane.showMessageDialog(null, "Your pet has gone to sleep.", "Virtual Pet", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // [Feed] Button
        JButton feedButton = new JButton("Feed");
        feedButton.setFont(new Font("Comic Sans MS", Font.PLAIN, 15));
        feedButton.setMnemonic('F'); // Alt + F for shortcut
        feedButton.addActionListener(feed -> {
            if (pet.getCurrentState() == VirtualPet.PetState.DEAD) {
                JOptionPane.showMessageDialog(null, "Your pet is dead, game over", "Virtual Pet", JOptionPane.INFORMATION_MESSAGE);
            } else if (pet.getCurrentState() == VirtualPet.PetState.SLEEPING) {
                JOptionPane.showMessageDialog(null, "Your pet is sleeping, please do not disturb.", "Virtual Pet", JOptionPane.INFORMATION_MESSAGE);
            } else if (pet.getCurrentState() == VirtualPet.PetState.ANGRY) {
                JOptionPane.showMessageDialog(null, "Your pet is angry, play or give gift to make him happy.", "Virtual Pet", JOptionPane.INFORMATION_MESSAGE);
            } else {
                InventoryScreen inventoryScreen = new InventoryScreen(inventory, pet, "food");
            }
        });

        // Gift Button
        JButton giftButton = new JButton("Gift");
        giftButton.setFont(new Font("Comic Sans MS", Font.PLAIN, 15));
        giftButton.setMnemonic('G'); // Alt + G for shortcut
        giftButton.addActionListener(giveGift -> {
            if (pet.getCurrentState() == VirtualPet.PetState.DEAD) {
                JOptionPane.showMessageDialog(null, "Your pet is dead, game over", "Virtual Pet", JOptionPane.INFORMATION_MESSAGE);
            } else if (pet.getCurrentState() == VirtualPet.PetState.SLEEPING) {
                JOptionPane.showMessageDialog(null, "Your pet is sleeping, please do not disturb.", "Virtual Pet", JOptionPane.INFORMATION_MESSAGE);
            } else {
                InventoryScreen inventoryScreen = new InventoryScreen(inventory, pet, "gift");
            }
        });

        // [Play] Button
        JButton playButton = new JButton("Play");
        playButton.setFont(new Font("Comic Sans MS", Font.PLAIN, 15));
        playButton.setMnemonic('P'); // Alt + P for shortcut
        playButton.addActionListener(e -> {
            if (pet.getCurrentState() == VirtualPet.PetState.DEAD) {
                JOptionPane.showMessageDialog(null, "Your pet is dead, game over", "Virtual Pet", JOptionPane.INFORMATION_MESSAGE);
            } else if (pet.getCurrentState() == VirtualPet.PetState.SLEEPING) {
                JOptionPane.showMessageDialog(null, "Your pet is sleeping, please do not disturb.", "Virtual Pet", JOptionPane.INFORMATION_MESSAGE);
            }
            pet.play(20);

            if (pet.getIsPlayColdDown() == true && pet.getCurrentState() != VirtualPet.PetState.DEAD) {
                JOptionPane.showMessageDialog(null, "Function in cold down", "Virtual Pet", JOptionPane.INFORMATION_MESSAGE);
            } else if (pet.getIsPlayColdDown() == false && pet.getCurrentState() != VirtualPet.PetState.DEAD) {
                JOptionPane.showMessageDialog(null, "Your pet has played.", "Virtual Pet", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // [Exercise] Button
        JButton exerciseButton = new JButton("Exercise");
        exerciseButton.setFont(new Font("Comic Sans MS", Font.PLAIN, 15));
        exerciseButton.setMnemonic('E'); // Alt + E for shortcut
        exerciseButton.addActionListener(e -> {
            if (pet.getCurrentState() == VirtualPet.PetState.DEAD) {
                JOptionPane.showMessageDialog(null, "Your pet is dead, game over", "Virtual Pet", JOptionPane.INFORMATION_MESSAGE);
            } else if (pet.getCurrentState() == VirtualPet.PetState.SLEEPING) {
                JOptionPane.showMessageDialog(null, "Your pet is sleeping, please do not disturb.", "Virtual Pet", JOptionPane.INFORMATION_MESSAGE);
            } else if (pet.getCurrentState() == VirtualPet.PetState.ANGRY) {
                JOptionPane.showMessageDialog(null, "Your pet is angry, play or give gift to make him happy.", "Virtual Pet", JOptionPane.INFORMATION_MESSAGE);
            } else {
                pet.exercise(20);
                JOptionPane.showMessageDialog(null, "Your pet is healthier, but feeling hungrier and more sleepy.", "Virtual Pet", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        // [Inventory] Button
        JButton inventoryButton = new JButton("Inventory");
        inventoryButton.setFont(new Font("Comic Sans MS", Font.PLAIN, 15));
        inventoryButton.setMnemonic('I'); // Alt + I for shortcut
        inventoryButton.addActionListener(e -> {
            if (pet.getCurrentState() == VirtualPet.PetState.DEAD) {
                JOptionPane.showMessageDialog(null, "Your pet is dead, game over", "Virtual Pet", JOptionPane.INFORMATION_MESSAGE);
            } else if (pet.getCurrentState() == VirtualPet.PetState.SLEEPING) {
                JOptionPane.showMessageDialog(null, "Your pet is sleeping, please do not disturb.", "Virtual Pet", JOptionPane.INFORMATION_MESSAGE);
            } else if (pet.getCurrentState() == VirtualPet.PetState.ANGRY) {
                JOptionPane.showMessageDialog(null, "Your pet is angry, play or give gift to make him happy.", "Virtual Pet", JOptionPane.INFORMATION_MESSAGE);
            } else {
                InventoryScreen inventoryScreen = new InventoryScreen(inventory, pet, null);
            }
        });

        buttonPanel.add(vetButton);
        buttonPanel.add(sleepButton);
        buttonPanel.add(feedButton);
        buttonPanel.add(giftButton);
        buttonPanel.add(playButton);
        buttonPanel.add(exerciseButton);
        buttonPanel.add(inventoryButton);
        return buttonPanel;
    }

    /**
     * Displays the pet's image on the pet panel.
     * <p>
     * This method removes any previous pet image, constructs the image path based on the pet's type
     * and current state, scales the image appropriately, and then adds it to the panel.
     * </p>
     * The images of Sprites from https://www.spriters-resource.com
     */
    private void displayPet() {
        // Clear the previous component each time.
        petPanel.removeAll();

        // Concatenate the image path based on the pet type (e.g., duck, cat, dog ...)
        // and its status (e.g., ANGRY, HUNGRY, SLEEP, DEAD...),
        // resulting in something like "resources/duck/angry.png"
        String imagePath = "resources/"+gameState.getPetType().toLowerCase()+"/"
                            +String.valueOf(pet.getCurrentState()).toLowerCase()+".png";
        // Create image and display
        ImageIcon petIcon = new ImageIcon(imagePath);
        petIcon.setImage(petIcon.getImage().getScaledInstance(200, 200, Image.SCALE_SMOOTH));

        // Show display area
        JLabel petLabel = new JLabel(petIcon);
        petLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        petPanel.add(petLabel, BorderLayout.CENTER);
        petPanel.revalidate();
        petPanel.repaint();
    }

    /**
     * Updates the status bars with the provided values.
     *
     * @param health    the new health value.
     * @param sleepiness the new sleepiness value.
     * @param happiness the new happiness value.
     * @param fullness  the new fullness value.
     */
    private void updateBars(int health, int sleepiness, int happiness, int fullness) {
        healthBar.setValue(health);
        sleepinessBar.setValue(sleepiness);
        happinessBar.setValue(happiness);
        fullnessBar.setValue(fullness);
    }
}