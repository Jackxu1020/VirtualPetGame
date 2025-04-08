import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 
 *
 * @author Haoxuan Suo 251103783 hsuo3
 */
public class ParentalControlsScreen extends JFrame implements Player.TimeCheckCallback {
    private Player player;
    private CardLayout cardLayout;
    private JPanel cards;

    private JPanel passwordPanel;
    private JPanel controlPanel;

    private JPasswordField passwordField;
    private JLabel passwordStatusLabel;

    private JCheckBox enableControlsCheckBox;
    private JSpinner startHourSpinner;
    private JSpinner startMinuteSpinner;
    private JSpinner endHourSpinner;
    private JSpinner endMinuteSpinner;
    private JLabel timeRangeLabel;
    private JLabel totalPlayTimeLabel;
    private JLabel averagePlayTimeLabel;
    private JLabel sessionCountLabel;
    private JComboBox<String> saveSlotComboBox;
    private JLabel currentTimeLabel;
    private JLabel allowedStatusLabel;
    private Timer statusUpdateTimer;


    private static final String PASSWORD_PANEL = "Password Panel";
    private static final String CONTROL_PANEL = "Control Panel";

    /**
     * Constructs a new ParentalControlsScreen.
     * Initializes the UI with a password panel and control panel.
     * The screen starts with the password panel visible.
     */
    public ParentalControlsScreen() {
        super("Parental Controls");

        this.player = new Player(false);

        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        cardLayout = new CardLayout();
        cards = new JPanel(cardLayout);

        createPasswordPanel();
        createControlPanel();

        cards.add(passwordPanel, PASSWORD_PANEL);
        cards.add(controlPanel, CONTROL_PANEL);

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(cards, BorderLayout.CENTER);

        JButton backButton = new JButton("Back to Main Menu");
        backButton.addActionListener(e -> returnToMainMenu());
        getContentPane().add(backButton, BorderLayout.SOUTH);

        cardLayout.show(cards, PASSWORD_PANEL);

        setupStatusTimer();

        setVisible(true);
    }

    /**
     * Creates the password panel for authentication.
     * This panel contains a password field and submit button.
     */
    private void createPasswordPanel() {
        passwordPanel = new JPanel();
        passwordPanel.setLayout(new BoxLayout(passwordPanel, BoxLayout.Y_AXIS));
        passwordPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Parental Controls");
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 20));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel instructionLabel = new JLabel(Player.isGlobalPasswordInitialized() ?
                "Enter Parental Password:" : "First time use, set parental password:");
        instructionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        passwordField = new JPasswordField(10);
        passwordField.setMaximumSize(new Dimension(200, 30));
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton submitButton = new JButton(Player.isGlobalPasswordInitialized() ? "Verify Password" : "Set Password");
        submitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        submitButton.addActionListener(e -> verifyOrSetPassword());

        passwordStatusLabel = new JLabel(" ");
        passwordStatusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordStatusLabel.setForeground(Color.RED);

        passwordPanel.add(Box.createVerticalGlue());
        passwordPanel.add(titleLabel);
        passwordPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        passwordPanel.add(instructionLabel);
        passwordPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        passwordPanel.add(passwordField);
        passwordPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        passwordPanel.add(submitButton);
        passwordPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        passwordPanel.add(passwordStatusLabel);
        passwordPanel.add(Box.createVerticalGlue());
    }

    /**
     * Creates the control panel that displays after successful authentication.
     * This panel contains time limit settings, statistics, and pet revival options.
     */
    private void createControlPanel() {
        controlPanel = new JPanel(new BorderLayout());
        controlPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Parental Control Settings");
        titleLabel.setFont(new Font("Dialog", Font.BOLD, 20));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);

        JPanel settingsPanel = new JPanel();
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));
        settingsPanel.setBorder(BorderFactory.createTitledBorder("Time Limit Settings"));

        enableControlsCheckBox = new JCheckBox("Enable Time Limits");
        enableControlsCheckBox.setSelected(Player.isGlobalParentalControlsEnabled());
        enableControlsCheckBox.addActionListener(e -> updateControlsState());

        
        JPanel checkboxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        checkboxPanel.add(enableControlsCheckBox);
        settingsPanel.add(checkboxPanel);
        settingsPanel.add(Box.createRigidArea(new Dimension(0, 10)));

     
        JPanel timeRangePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        timeRangePanel.add(new JLabel("Allowed Play Time: "));

        SpinnerNumberModel startHourModel = new SpinnerNumberModel(player.getStartHour(), 0, 23, 1);
        startHourSpinner = new JSpinner(startHourModel);

        SpinnerNumberModel startMinuteModel = new SpinnerNumberModel(player.getStartMinute(), 0, 59, 1);
        startMinuteSpinner = new JSpinner(startMinuteModel);

        SpinnerNumberModel endHourModel = new SpinnerNumberModel(player.getEndHour(), 0, 23, 1);
        endHourSpinner = new JSpinner(endHourModel);

        SpinnerNumberModel endMinuteModel = new SpinnerNumberModel(player.getEndMinute(), 0, 59, 1);
        endMinuteSpinner = new JSpinner(endMinuteModel);

        timeRangePanel.add(startHourSpinner);
        timeRangePanel.add(new JLabel(":"));
        timeRangePanel.add(startMinuteSpinner);
        timeRangePanel.add(new JLabel(" - "));
        timeRangePanel.add(endHourSpinner);
        timeRangePanel.add(new JLabel(":"));
        timeRangePanel.add(endMinuteSpinner);

        settingsPanel.add(timeRangePanel);
        settingsPanel.add(Box.createRigidArea(new Dimension(0, 5)));

       
        JPanel applyButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton applyButton = new JButton("Apply Settings");
        applyButton.addActionListener(e -> applyTimeSettings());
        applyButtonPanel.add(applyButton);
        settingsPanel.add(applyButtonPanel);
        settingsPanel.add(Box.createRigidArea(new Dimension(0, 15)));

     
        JPanel timeRangeLabelPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        timeRangeLabel = new JLabel();
        updateTimeRangeLabel();
        timeRangeLabelPanel.add(timeRangeLabel);
        settingsPanel.add(timeRangeLabelPanel);
        settingsPanel.add(Box.createRigidArea(new Dimension(0, 5)));

   
        JPanel statusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        allowedStatusLabel = new JLabel("Status: " +
                (player.isAllowedToPlay() ? "Play Allowed" : "Play Restricted"));
        allowedStatusLabel.setForeground(player.isAllowedToPlay() ? Color.GREEN.darker() : Color.RED);
        statusPanel.add(allowedStatusLabel);
        settingsPanel.add(statusPanel);

     
        JPanel statsPanel = new JPanel();
        statsPanel.setLayout(new BoxLayout(statsPanel, BoxLayout.Y_AXIS));
        statsPanel.setBorder(BorderFactory.createTitledBorder("Game Statistics"));

        DecimalFormat df = new DecimalFormat("#.##");

       
            JPanel statsInfoPanel = new JPanel(new GridLayout(3, 1, 0, 5));

        statsInfoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        totalPlayTimeLabel = new JLabel("Total Play Time: " +
                df.format(Player.getGlobalTotalPlayTime()) + " hours");
        averagePlayTimeLabel = new JLabel("Average Play Time: " +
                df.format(player.getAveragePlayTime()) + " hours/session");
        sessionCountLabel = new JLabel("Game Start Count: " +
                Player.getGlobalGameStartCount() + " times");

        statsInfoPanel.add(totalPlayTimeLabel);
        statsInfoPanel.add(averagePlayTimeLabel);
        statsInfoPanel.add(sessionCountLabel);
        statsPanel.add(statsInfoPanel);
        statsPanel.add(Box.createRigidArea(new Dimension(0, 10)));

       
        JPanel resetButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton resetStatsButton = new JButton("Reset Statistics");
        resetStatsButton.addActionListener(e -> resetStatistics());
        resetButtonPanel.add(resetStatsButton);
        statsPanel.add(resetButtonPanel);
        statsPanel.add(Box.createRigidArea(new Dimension(0, 15)));

       
        JPanel petRevivalPanel = new JPanel();
        petRevivalPanel.setBorder(BorderFactory.createTitledBorder("Pet Revival"));
        petRevivalPanel.setLayout(new BoxLayout(petRevivalPanel, BoxLayout.Y_AXIS));
        petRevivalPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JPanel slotPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        slotPanel.add(new JLabel("Select Save Slot: "));
        String[] saveSlots = {"Save 1", "Save 2", "Save 3"};
        saveSlotComboBox = new JComboBox<>(saveSlots);
        slotPanel.add(saveSlotComboBox);
        petRevivalPanel.add(slotPanel);
        petRevivalPanel.add(Box.createRigidArea(new Dimension(0, 5)));

      
        JPanel reviveButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton reviveButton = new JButton("Revive Pet");
        reviveButton.addActionListener(e -> {
            try {
                revivePet();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        });
        reviveButtonPanel.add(reviveButton);
        petRevivalPanel.add(reviveButtonPanel);

        statsPanel.add(petRevivalPanel);

      
        JSplitPane splitPane = new JSplitPane(
                JSplitPane.HORIZONTAL_SPLIT, settingsPanel, statsPanel);
        splitPane.setDividerLocation(350);

        controlPanel.add(titleLabel, BorderLayout.NORTH);
        controlPanel.add(splitPane, BorderLayout.CENTER);

        updateControlsState();
    }

    /**
     * Verifies the entered password or sets a new password if one hasn't been set.
     * Upon successful verification, shows the control panel.
     */
    private void verifyOrSetPassword() {
        String password = new String(passwordField.getPassword());

        if (password.isEmpty()) {
            passwordStatusLabel.setText("Password cannot be empty");
            return;
        }

        if (!Player.isGlobalPasswordInitialized()) {
            if (Player.setGlobalPassword(password)) {
                passwordStatusLabel.setText("Password set successfully");
                passwordStatusLabel.setForeground(Color.GREEN.darker());

                player.accessParentalControlWithPassword(password);

                cardLayout.show(cards, CONTROL_PANEL);

                updateStatisticsDisplay();
            } else {
                passwordStatusLabel.setText("Password already set, cannot change");
            }
        } else {
            if (player.accessParentalControlWithPassword(password)) {
                passwordStatusLabel.setText("Password verified successfully");
                passwordStatusLabel.setForeground(Color.GREEN.darker());

                updateStatisticsDisplay();

                cardLayout.show(cards, CONTROL_PANEL);
            } else {
                passwordStatusLabel.setText("Incorrect password");
                passwordStatusLabel.setForeground(Color.RED);
            }
        }
    }

    /**
     * Updates the enabled state of time control spinners based on the checkbox.
     */
    private void updateControlsState() {
        boolean enabled = enableControlsCheckBox.isSelected();

        startHourSpinner.setEnabled(enabled);
        startMinuteSpinner.setEnabled(enabled);
        endHourSpinner.setEnabled(enabled);
        endMinuteSpinner.setEnabled(enabled);
    }

    /**
     * Applies the time limit settings to the Player.
     * Shows a success or failure message.
     */
    private void applyTimeSettings() {
        boolean enabled = enableControlsCheckBox.isSelected();

        int startHour = (Integer) startHourSpinner.getValue();
        int startMinute = (Integer) startMinuteSpinner.getValue();
        int endHour = (Integer) endHourSpinner.getValue();
        int endMinute = (Integer) endMinuteSpinner.getValue();

        String timeRange = String.format("%02d:%02d - %02d:%02d",
                startHour, startMinute, endHour, endMinute);

        if (player.setParentalControls(enabled, timeRange)) {
            JOptionPane.showMessageDialog(this,
                    "Parental control settings updated", "Success", JOptionPane.INFORMATION_MESSAGE);

            updateTimeRangeLabel();
            updateTimeStatus();
        } else {
            JOptionPane.showMessageDialog(this,
                    "Unable to update parental control settings", "Failed", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Resets all game statistics after confirmation.
     */
    private void resetStatistics() {
        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to reset all game statistics?", "Confirm Reset",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            if (player.resetPlayTimeStats()) {
                JOptionPane.showMessageDialog(this,
                        "Game statistics have been reset", "Reset Successful", JOptionPane.INFORMATION_MESSAGE);

                updateStatisticsDisplay();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Unable to reset game statistics", "Reset Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Updates the statistics display labels with the latest values.
     */
    private void updateStatisticsDisplay() {
        DecimalFormat df = new DecimalFormat("#.##");

        totalPlayTimeLabel.setText("Total Play Time: " +
                df.format(Player.getGlobalTotalPlayTime()) + " hours");

        averagePlayTimeLabel.setText("Average Play Time: " +
                df.format(player.getAveragePlayTime()) + " hours/session");

        sessionCountLabel.setText("Game Start Count: " +
                Player.getGlobalGameStartCount() + " times");
    }

    /**
     * Revives a pet in the selected save slot after confirmation.
     */
    private void revivePet() throws IOException {
        String selectedSlot = (String) saveSlotComboBox.getSelectedItem();
        String saveId = selectedSlot.replace("Save ", "");
        GameState gameState = SaveLoadManager.loadGame(Integer.parseInt(saveId));

        if ( gameState.getHealth() > 0) {
            JOptionPane.showMessageDialog(this,
                    "Pet is still alive and cannot be revived.",
                    "Revival Not Allowed", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int choice = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to revive the pet in Save " + saveId + "?", "Confirm Revival",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (choice == JOptionPane.YES_OPTION) {
            if (player.revivePet(saveId)) {
                JOptionPane.showMessageDialog(this,
                        "Pet has been revived!", "Revival Successful", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this,
                        "Unable to revive pet", "Revival Failed", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /**
     * Returns to the main menu after cleaning up resources.
     */
    private void returnToMainMenu() {
        if (statusUpdateTimer != null) {
            statusUpdateTimer.cancel();
        }

        player.exitParentalControl();

        passwordField.setText("");
        passwordStatusLabel.setText(" ");

        cardLayout.show(cards, PASSWORD_PANEL);

        this.dispose();
    }

    /**
     * Called when a time restriction violation occurs.
     * Displays a warning message with the allowed time range.
     * 
     * @param allowedTimeRange the configured time range when gameplay is allowed
     */
    @Override
    public void onTimeRestrictionViolation(String allowedTimeRange) {
        JOptionPane.showMessageDialog(this,
                "Current time does not allow gameplay!\nAllowed time: " + allowedTimeRange,
                "Time Restriction", JOptionPane.WARNING_MESSAGE);
    }

    /**
     * Called periodically to check if gameplay is still allowed.
     * This implementation is empty as the screen handles status display separately.
     * 
     * @param isAllowed whether gameplay is currently allowed
     */
    @Override
    public void onPeriodicCheck(boolean isAllowed) {
    }

    /**
     * Updates the time range label, including a note if it crosses midnight.
     */
    private void updateTimeRangeLabel() {
        int startHour = (Integer) startHourSpinner.getValue();
        int startMinute = (Integer) startMinuteSpinner.getValue();
        int endHour = (Integer) endHourSpinner.getValue();
        int endMinute = (Integer) endMinuteSpinner.getValue();

        String formattedRange = String.format("%02d:%02d - %02d:%02d",
                startHour, startMinute, endHour, endMinute);

        boolean crossesMidnight = (startHour > endHour) ||
                (startHour == endHour && startMinute > endMinute);

        if (crossesMidnight) {
            timeRangeLabel.setText("Current Setting: " + formattedRange + " (Crosses midnight)");
        } else {
            timeRangeLabel.setText("Current Setting: " + formattedRange);
        }
    }

    /**
     * Sets up a timer to periodically update the time status display.
     */
    private void setupStatusTimer() {
        if (statusUpdateTimer != null) {
            statusUpdateTimer.cancel();
        }

        statusUpdateTimer = new Timer();
        statusUpdateTimer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> updateTimeStatus());
            }
        }, 0, 10000); // Update every 10 seconds
    }

    /**
     * Updates the time status display with the current allowed/restricted status.
     */
    private void updateTimeStatus() {
        boolean isAllowed = player.isAllowedToPlay();
        allowedStatusLabel.setText("Status: " +
                (isAllowed ? "Play Allowed" : "Play Restricted"));
        allowedStatusLabel.setForeground(isAllowed ? Color.GREEN.darker() : Color.RED);
    }

    /**
     * Disposes of this window and cleans up resources.
     */
    @Override
    public void dispose() {
        if (statusUpdateTimer != null) {
            statusUpdateTimer.cancel();
        }
        super.dispose();
    }
}