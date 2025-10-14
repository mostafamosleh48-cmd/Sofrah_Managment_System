package Sofrah_Managment.src.main.java.com.example.sofrah_managment;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class MenuService {
    private final MenuItemDAO menuItemDAO;

    public MenuService() {
        this.menuItemDAO = new MenuItemDAO();
    }


    public int addMenuItem(MenuItem menuItem, List<MenuIngredient> ingredients) {
        try {
            return menuItemDAO.addMenuItem(menuItem, ingredients);
        } catch (SQLException e) {
            System.err.println("Error adding menu item: " + e.getMessage());
            e.printStackTrace();
            return -1; // Indicate failure
        }
    }


    public boolean updateMenuItem(MenuItem menuItem, List<MenuIngredient> newIngredients) {
        try {
            menuItemDAO.updateMenuItem(menuItem, newIngredients);
            return true;
        } catch (SQLException e) {
            System.err.println("Error updating menu item: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }


    public List<MenuItem> getAvailableMenuItems() {
        try {
            return menuItemDAO.getAvailableMenuItems();
        } catch (SQLException e) {
            System.err.println("Error retrieving available menu items: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }


    public List<MenuItem> getUnavailableMenuItems() {
        try {
            return menuItemDAO.getUnavailableMenuItems();
        } catch (SQLException e) {
            System.err.println("Error retrieving unavailable menu items: " + e.getMessage());
            e.printStackTrace();
            return Collections.emptyList();
        }
    }
}
