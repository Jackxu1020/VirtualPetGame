import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
/**
 * GUI screen for displaying and interacting with the player's inventory.
 *
 * The InventoryScreen displays items like Food and Gift items that the player owns,
 * allows filtering by item type, and enables applying items directly to a virtual pet.
 * Used items update the pet's stats and inventory in real time.
 *
 * @Author: Jinke Li
 */

public class InventoryScreen {
    private JFrame frame;
    private Inventory inventory;
    private VirtualPet pet;
    private String filterType;

    /**
     * Constructs and displays the inventory screen.
     *
     * @param inventory The player's inventory containing usable items.
     * @param pet       The virtual pet to apply items to.
     * @param filterType Optional filter to only show certain item types ("food", "gift", or null for all).
     */
    public InventoryScreen(Inventory inventory, VirtualPet pet, String filterType) {
        this.inventory = inventory;
        this.pet = pet;
        this.filterType = filterType;
        initialize();
    }

    /**
     * Builds the inventory GUI layout and sets up UI components.
     */
    private void initialize() {
        frame = new JFrame("Inventory");
        frame.setSize(600, 400);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        JPanel itemPanel = new JPanel();
        itemPanel.setLayout(new GridLayout(0, 4, 10, 10));

        for (InventoryItem item : inventory.getItems()) {
            if (item.getQuantity() > 0) {
                if (filterType == null ||
                        (filterType.equals("food") && item instanceof FoodItem) ||
                        (filterType.equals("gift") && item instanceof GiftItem)) {
                    itemPanel.add(createItemCard(item));
                }
            }
        }

        JScrollPane scrollPane = new JScrollPane(itemPanel);
        frame.add(scrollPane, BorderLayout.CENTER);

        JButton backButton = new JButton("Go Back!");
        backButton.addActionListener(e -> {
            frame.dispose();
        });

        JPanel bottomPanel = new JPanel();
        bottomPanel.add(backButton);
        frame.add(bottomPanel, BorderLayout.SOUTH);

        frame.setVisible(true);
    }

    /**
     * Creates a visual card component for a specific inventory item.
     * Includes item details, a "use" button, and quantity.
     *
     * @param item The inventory item to display.
     * @return JPanel representing the item card.
     */
    private JPanel createItemCard(InventoryItem item) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        JLabel nameLabel = new JLabel(item.getName());
        JLabel typeLabel = new JLabel(item instanceof FoodItem ? "food" : "gift");
        JLabel effectLabel = new JLabel(item.getEffectDescription());
        JLabel quantityLabel = new JLabel("\uD83D\uDED2 " + item.getQuantity() + " in stock");

        JButton useButton = new JButton("use");
        useButton.addActionListener(e -> showConfirmationDialog(item));

        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        typeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        effectLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        quantityLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        useButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(Box.createVerticalStrut(10));
        card.add(nameLabel);
        card.add(typeLabel);
        card.add(effectLabel);
        card.add(useButton);
        card.add(quantityLabel);

        return card;
    }

    /**
     * Shows a confirmation dialog before using the selected item.
     * If confirmed, the item's effect is applied to the pet.
     *
     * @param item The item to use on the pet.
     */
    private void showConfirmationDialog(InventoryItem item) {
        JDialog dialog = new JDialog(frame, "Inventory", true);
        dialog.setSize(400, 200);
        dialog.setLayout(new BorderLayout());

        JLabel header = new JLabel("Use this to my pet!", SwingConstants.CENTER);
        header.setOpaque(true);
        header.setBackground(Color.LIGHT_GRAY);
        dialog.add(header, BorderLayout.NORTH);

        JTextArea message = new JTextArea(item.getEffectDescription());
        message.setEditable(false);
        message.setBackground(null);
        message.setLineWrap(true);
        message.setWrapStyleWord(true);
        message.setMargin(new Insets(10, 10, 10, 10));
        dialog.add(message, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton confirm = new JButton("Confirm");
        JButton cancel = new JButton("Cancel");

        confirm.addActionListener(e -> {
            item.use(pet); // apply item effect
            dialog.dispose();
            showPopupSuccessMessage(item);
            frame.dispose();
            new InventoryScreen(inventory, pet, filterType); // refresh screen
        });

        cancel.addActionListener(e -> dialog.dispose());

        buttonPanel.add(confirm);
        buttonPanel.add(cancel);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        dialog.setLocationRelativeTo(frame);
        dialog.setVisible(true);
    }

    /**
     * Displays a success popup after an item is used on the pet.
     * Message varies depending on whether the item is food or gift.
     *
     * @param item The item that was used.
     */
    private void showPopupSuccessMessage(InventoryItem item) {
        String message;
        if (item instanceof FoodItem) {
            message = "Item used successfully! Pet's fullness has increased.";
        } else if (item instanceof GiftItem) {
            message = "Item used successfully! Pet's happiness has increased.";
        } else {
            message = "Item used successfully!";
        }

        JOptionPane.showMessageDialog(frame, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    /**
     * Entry point for testing this screen with mock data.
     */
    public static void main(String[] args) {
        // Example usage with mock data
        VirtualPet pet = new VirtualPet("TestName", PetType.DOG);
        Inventory inventory = new Inventory();
        inventory.addItem(new GiftItem("lego", 1, 20));
        inventory.addItem(new FoodItem("cheese", 1, 15));
        inventory.addItem(new FoodItem("bread", 8, 10));
        inventory.addItem(new GiftItem("chips", 8, 20));
        inventory.addItem(new GiftItem("ball", 8, 25));

        SwingUtilities.invokeLater(() -> new InventoryScreen(inventory, pet, null));
    }
}