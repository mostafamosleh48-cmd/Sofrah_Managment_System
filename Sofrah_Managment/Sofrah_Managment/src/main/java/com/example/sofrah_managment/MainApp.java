package Sofrah_Managment.src.main.java.com.example.sofrah_managment;//import javafx.application.Application;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MainApp {

    private static final Scanner scanner = new Scanner(System.in);
    private static final InventoryService inventoryService = new InventoryService();
    private static final MenuService menuService = new MenuService();

    public static void main(String[] args) {
        while (true) {
            System.out.println("\nWelcome to Sofrah Management System!");
            System.out.println("1. Manage Inventory");
            System.out.println("2. Manage Menu");
            System.out.println("0. Exit");
            System.out.print("Choose an option: ");
            int choice = readInt();

            switch (choice) {
                case 1:
                    manageInventory();
                    break;
                case 2:
                    manageMenu();
                    break;
                case 0:
                    System.out.println("Exiting application. Goodbye!");
                    scanner.close();
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
    }

    private static void manageInventory() {
        while (true) {
            System.out.println("\n--- Inventory Management ---");
            System.out.println("1. Add Supplier");
            System.out.println("2. Create Inventory Order");
            System.out.println("3. Receive Inventory Order (Update Stock)");
            System.out.println("4. View Full Inventory");
            System.out.println("5. View Low Stock Items");
            System.out.println("0. Back to Main Menu");
            System.out.print("Choose an option: ");
            int choice = readInt();

            switch (choice) {
                case 1:
                    addSupplier();
                    break;
                case 2:
                    createInventoryOrder();
                    break;
                case 3:
                    receiveInventoryOrder();
                    break;
                case 4:
                    viewFullInventory();
                    break;
                case 5:
                    viewLowStockItems();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    private static void addSupplier() {
        System.out.println("\n--- Add New Supplier ---");
        System.out.print("Enter supplier name: ");
        String name = scanner.nextLine();
        System.out.print("Enter supplier phone number: ");
        String phone = scanner.nextLine();

        Supplier newSupplier = new Supplier(0, name, phone); // ID is auto-generated
        int supplierId = inventoryService.addSupplier(newSupplier);
        if (supplierId != -1) {
            System.out.println("Supplier added successfully with ID: " + supplierId);
        } else {
            System.out.println("Failed to add supplier.");
        }
    }

    // In: MainApplication.java

    private static void createInventoryOrder() {
        System.out.println("\n--- Create Inventory Order ---");
        System.out.print("Enter Supplier ID for this order: ");
        int supplierId = readInt();

        List<InventoryOrderItem> itemsToOrder = new ArrayList<>();
        while (true) {
            System.out.print("Enter Inventory Item NAME to add (or type 'done' to finish): ");
            String itemName = scanner.nextLine();
            if (itemName.equalsIgnoreCase("done")) break;

            InventoryItem item = inventoryService.findItemByName(itemName);
            int itemId;

            if (item == null) {
                // Item does not exist
                System.out.print("Item '" + itemName + "' not found in inventory. Add it now? (yes/no): ");
                String choice = scanner.nextLine();
                if (choice.equalsIgnoreCase("yes")) {

                    // ---[ THIS IS THE NEW PART ]---
                    System.out.print("Enter the initial stock for this new item: ");
                    int initialStock = readInt();
                    // ------------------------------

                    // Call the updated service method with the initial stock
                    itemId = inventoryService.createNewInventoryItem(itemName, initialStock);

                    if (itemId != -1) {
                        System.out.println("New item '" + itemName + "' added to inventory with ID: " + itemId + " and initial stock of " + initialStock + ".");
                    } else {
                        System.out.println("Failed to add new item. Please try again.");
                        continue; // Skip to next iteration
                    }
                } else {
                    System.out.println("Skipping item.");
                    continue; // Skip to next iteration
                }
            } else {
                // Item exists (no changes here)
                itemId = item.getId();
                System.out.println("Found '" + item.getName() + "' (ID: " + itemId + ", Current Stock: " + item.getStock() + ")");
            }

            // Proceed to get order details for this item
            System.out.print("Enter QUANTITY for this order: ");
            int quantity = readInt();
            System.out.print("Enter PRICE per unit for this order: ");
            BigDecimal price = readBigDecimal();

            itemsToOrder.add(new InventoryOrderItem(itemId, quantity, price));
        }

        if (!itemsToOrder.isEmpty()) {
            int orderId = inventoryService.placeNewInventoryOrder(supplierId, itemsToOrder);
            if (orderId != -1) {
                System.out.println("Inventory order created successfully with ID: " + orderId);
                System.out.println("Remember to 'Receive Inventory Order' to update stock when items arrive.");
            } else {
                System.out.println("Failed to create inventory order.");
            }
        } else {
            System.out.println("No items added to the order.");
        }
    }

    private static void receiveInventoryOrder() {
        System.out.println("\n--- Receive Inventory Order ---");
        System.out.print("Enter Inventory Order ID to receive: ");
        int orderId = readInt();
        inventoryService.confirmInventoryOrderReceived(orderId);
        // Success/failure messages are handled within the service/DAO for this one
    }


    private static void viewFullInventory() {
        System.out.println("\n--- Full Inventory List ---");
        List<InventoryItem> items = inventoryService.viewFullInventory();
        if (items.isEmpty()) {
            System.out.println("Inventory is currently empty or could not be retrieved.");
        } else {
            items.forEach(item -> System.out.println("ID: " + item.getId() + ", Name: " + item.getName() + ", Stock: " + item.getStock()));
        }
    }

    private static void viewLowStockItems() {
        System.out.println("\n--- Low Stock Items ---");
        System.out.print("Enter stock threshold (e.g., 10): ");
        int threshold = readInt();
        List<InventoryItem> items = inventoryService.viewLowStockInventory(threshold);
        if (items.isEmpty()) {
            System.out.println("No items found below the threshold of " + threshold + " or an error occurred.");
        } else {
            System.out.println("Items with stock less than " + threshold + ":");
            items.forEach(item -> System.out.println("ID: " + item.getId() + ", Name: " + item.getName() + ", Stock: " + item.getStock()));
        }
    }


    private static void manageMenu() {
        while (true) {
            System.out.println("\n--- Menu Management ---");
            System.out.println("1. Add Menu Item");
            System.out.println("2. Update Menu Item");
            System.out.println("3. View Today's Available Menu");
            System.out.println("4. View Unavailable Menu Items");
            System.out.println("0. Back to Main Menu");
            System.out.print("Choose an option: ");
            int choice = readInt();

            switch (choice) {
                case 1:
                    addMenuItem();
                    break;
                case 2:
                    updateMenuItem();
                    break;
                case 3:
                    viewAvailableMenu();
                    break;
                case 4:
                    viewUnavailableMenu();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("Invalid option.");
            }
        }
    }

    // Helper method to gather ingredients by name to reduce code duplication
    private static List<MenuIngredient> gatherIngredientsFromUserInput() {
        List<MenuIngredient> ingredients = new ArrayList<>();
        System.out.println("Enter ingredients for the menu item:");
        while (true) {
            System.out.print("Enter ingredient NAME (or type 'done' to finish): ");
            String ingredientName = scanner.nextLine();
            if (ingredientName.equalsIgnoreCase("done")) break;

            InventoryItem item = inventoryService.findItemByName(ingredientName);
            if (item == null) {
                System.out.println("Error: Ingredient '" + ingredientName + "' does not exist in the inventory. Please add it via Inventory Management first. Skipping.");
                continue;
            }

            System.out.println("Found '" + item.getName() + "' (ID: " + item.getId() + ")");
            System.out.print("Enter quantity of '" + item.getName() + "' needed for ONE serving: ");
            BigDecimal quantity = readBigDecimal();
            ingredients.add(new MenuIngredient(item.getId(), quantity));
        }
        return ingredients;
    }

    private static void addMenuItem() {
        System.out.println("\n--- Add New Menu Item ---");
        System.out.print("Enter item name: ");
        String name = scanner.nextLine();
        System.out.print("Enter description: ");
        String description = scanner.nextLine();
        System.out.print("Enter price: ");
        BigDecimal price = readBigDecimal();
        System.out.print("Is it available? (true/false): ");
        boolean isAvailable = readBoolean();

        MenuItem newItem = new MenuItem(name, description, isAvailable, price);
        List<MenuIngredient> ingredients = gatherIngredientsFromUserInput();

        int menuItemId = menuService.addMenuItem(newItem, ingredients);
        if (menuItemId != -1) {
            System.out.println("Menu item added successfully with ID: " + menuItemId);
        } else {
            System.out.println("Failed to add menu item.");
        }
    }

    private static void updateMenuItem() {
        System.out.println("\n--- Update Menu Item ---");
        System.out.print("Enter ID of the menu item to update: ");
        int menuItemId = readInt();

        System.out.print("Enter NEW item name: ");
        String name = scanner.nextLine();
        System.out.print("Enter NEW description: ");
        String description = scanner.nextLine();
        System.out.print("Enter NEW price: ");
        BigDecimal price = readBigDecimal();
        System.out.print("Is it NOW available? (true/false): ");
        boolean isAvailable = readBoolean();

        MenuItem updatedItem = new MenuItem(menuItemId, name, description, isAvailable, price);

        System.out.println("\nEnter NEW list of ingredients (this will replace the old list):");
        List<MenuIngredient> newIngredients = gatherIngredientsFromUserInput();

        if (menuService.updateMenuItem(updatedItem, newIngredients)) {
            System.out.println("Menu item updated successfully.");
        } else {
            System.out.println("Failed to update menu item.");
        }
    }

    private static void viewAvailableMenu() {
        System.out.println("\n--- Today's Available Menu ---");
        List<MenuItem> items = menuService.getAvailableMenuItems();
        if (items.isEmpty()) {
            System.out.println("No menu items are currently available or an error occurred.");
        } else {
            items.forEach(System.out::println);
        }
    }

    private static void viewUnavailableMenu() {
        System.out.println("\n--- Unavailable Menu Items ---");
        List<MenuItem> items = menuService.getUnavailableMenuItems();
        if (items.isEmpty()) {
            System.out.println("All menu items are currently available or an error occurred.");
        } else {
            items.forEach(System.out::println);
        }
    }

    // Helper methods for robust input
    private static int readInt() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a number: ");
            }
        }
    }

    private static BigDecimal readBigDecimal() {
        while (true) {
            try {
                return new BigDecimal(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("Invalid input. Please enter a decimal number (e.g., 10.50): ");
            }
        }
    }

    private static boolean readBoolean() {
        while (true) {
            String input = scanner.nextLine().trim().toLowerCase();
            if (input.equals("true")) return true;
            if (input.equals("false")) return false;
            System.out.print("Invalid input. Please enter 'true' or 'false': ");
        }
    }
}

