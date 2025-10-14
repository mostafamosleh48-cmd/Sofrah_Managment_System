package Sofrah_Managment.src.main.java.com.example.sofrah_managment;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class InventoryService {
    private final SupplierDAO supplierDAO;
    private final InventoryOrderDAO inventoryOrderDAO;
    private final InventoryItemDAO inventoryItemDAO;

    public InventoryService() {
        this.supplierDAO = new SupplierDAO();
        this.inventoryOrderDAO = new InventoryOrderDAO();
        this.inventoryItemDAO = new InventoryItemDAO();
    }


    public int addSupplier(Supplier supplier) {
        try {
            return supplierDAO.addSupplier(supplier);
        } catch (SQLException e) {
            System.err.println("Error adding supplier: " + e.getMessage());
            e.printStackTrace();
            return -1; // Indicate failure
        }
    }


    public int placeNewInventoryOrder(int supplierId, List<InventoryOrderItem> items) {
        try {
            return inventoryOrderDAO.createInventoryOrder(supplierId, items);
        } catch (SQLException e) {
            System.err.println("Error placing new inventory order: " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }


    public void confirmInventoryOrderReceived(int orderId) {
        try {
            inventoryOrderDAO.receiveInventoryOrder(orderId);
            System.out.println("Successfully processed reception for order ID: " + orderId);
        } catch (SQLException e) {
            System.err.println("Error confirming inventory order reception: " + e.getMessage());
            e.printStackTrace();
        }
    }


    public List<InventoryItem> viewFullInventory() {
        try {
            return inventoryItemDAO.getAllInventoryItems();
        } catch (SQLException e) {
            System.err.println("Error retrieving full inventory: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }


    public List<InventoryItem> viewLowStockInventory(int threshold) {
        try {
            return inventoryItemDAO.getLowStockItems(threshold);
        } catch (SQLException e) {
            System.err.println("Error retrieving low stock inventory: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }


    }

    // In: InventoryService.java

    public int createNewInventoryItem(String name, int initialStock) {
        try {
            // Pass the initial stock to the DAO method
            return inventoryItemDAO.addNewInventoryItem(name, initialStock);
        } catch (SQLException e) {
            System.err.println("Error creating new inventory item: " + e.getMessage());
            e.printStackTrace();
            return -1;
        }
    }

    public InventoryItem findItemByName(String name) {
        try {
            return inventoryItemDAO.findByName(name);
        } catch (SQLException e) {
            System.err.println("Error finding item by name: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }


    public Supplier getSupplierById(int id) throws SQLException {
        return supplierDAO.getSupplierById(id);
    }

}