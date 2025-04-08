import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * MainMenuScreen represents the main menu interface of the application,
 * offering functionalities such as starting a new game, loading a game,
 * viewing the tutorial, accessing parental controls, opening settings, and exiting the application.
 * <p>
 * This class extends {@link javax.swing.JFrame} and sets up the main menu UI,
 * which includes displaying developer and game information as well as interactive buttons
 * to navigate through different screens of the application.
 * </p>
 *
 * <p><strong>Main Features:</strong></p>
 * <ul>
 *   <li>Start a new game with a time restriction check</li>
 *   <li>Load a previously saved game with a time restriction check</li>
 *   <li>Display the game tutorial</li>
 *   <li>Access the parental controls interface</li>
 *   <li>Open the settings interface</li>
 *   <li>Exit the application</li>
 * </ul>
 *
 * @version 1.0
 * @author Zhenkang Xu
 */
public class MainMenuScreen extends JFrame {
    /**
     * Constructs a new MainMenuScreen and initializes the UI components.
     * <p>
     * This constructor sets up the main menu window by configuring basic frame properties
     * (such as size, close operation, and location), creating a main panel to display developer
     * and game information, and building a button panel with various interactive buttons.
     * Each button is associated with an event listener that handles its specific action.
     * </p>
     */
    public MainMenuScreen() {
        super("Main Menu Screen");

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 600);
        setLocationRelativeTo(null); //Centers the window on the screen
        setVisible(true);

        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));

        JLabel developersLabel = new JLabel("Dvelopers：Zhenkang Xu, Yu Li, Bruno, Angel, Jinke");
        JLabel gameNameLabel = new JLabel("Game Name：Virtual Pet Game");
        JLabel teamNumberLabel = new JLabel("Group Number：58");
        JLabel semesterLabel = new JLabel(" Created in：March 2025");
        JLabel courseLabel = new JLabel("（ created as part of CS2212 at Western University.)");

        infoPanel.add(developersLabel);
        infoPanel.add(gameNameLabel);
        infoPanel.add(teamNumberLabel);
        infoPanel.add(semesterLabel);
        infoPanel.add(courseLabel);

        mainPanel.add(infoPanel, BorderLayout.NORTH);


        // Create a panel and set the layout
        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(6, 1, 10, 10));
        // 6 rows and 1 column, row spacing and column spacing are both set to 10

        // 1. Start a new game
        JButton startButton = new JButton("Start a new game");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == startButton) {
                    // Check if player is allowed to play before starting the game
                    Player player = new Player(false);
                    if (!player.isAllowedToPlay()) {
                        JOptionPane.showMessageDialog(MainMenuScreen.this,
                                "Current time does not allow gameplay!\nAllowed time: " + player.getPlayTimeLimit(),
                                "Time Restriction", JOptionPane.WARNING_MESSAGE);
                        return; // Don't start the game if not allowed
                    }

                    // Allowed to play, proceed with starting the game
                    dispose();
                    SelectPetScreen selectPetScreen = new SelectPetScreen();
                }
            }
        });
        panel.add(startButton);

        // 2. Load Game
        JButton loadButton = new JButton("Load Game");
        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (e.getSource() == loadButton) {
                    // Check if player is allowed to play before loading the game
                    Player player = new Player(false);
                    if (!player.isAllowedToPlay()) {
                        JOptionPane.showMessageDialog(MainMenuScreen.this,
                                "Current time does not allow gameplay!\nAllowed time: " + player.getPlayTimeLimit(),
                                "Time Restriction", JOptionPane.WARNING_MESSAGE);
                        return; // Don't load the game if not allowed
                    }

                    // Allowed to play, proceed with loading the game
                    LoadGameScreen loadGameScreen = new LoadGameScreen();
                    dispose();
                }
            }
        });
        panel.add(loadButton);

        // 3. Tutorial Screen
        JButton tutorialButton = new JButton("Tutorial");
        tutorialButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new TutorialScreen().setVisible(true);  // <-- just this line matters
            }
        });
        panel.add(tutorialButton);

        // Parental Control Screen
        JButton parentControlButton = new JButton("Parental Controls");
        parentControlButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                ParentalControlsScreen parentalControlsScreen = new ParentalControlsScreen();
            }
        });
        panel.add(parentControlButton);

        // Setting Screen
        JButton settingButton = new JButton("Setting");
        settingButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                SettingScreen settingScreen = new SettingScreen();
            }
        });
        panel.add(settingButton);

        // Exit Button
        JButton exitButton = new JButton("Exit");
        exitButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        panel.add(exitButton);

        mainPanel.add(panel, BorderLayout.CENTER);
        add(mainPanel);
    }
}