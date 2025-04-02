package org.example;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;
import java.util.ArrayList;

public class StockManagement extends JFrame {
    private JPanel itemsPanel;
    private ArrayList<Item> itemList;
    private String role;

    private JTextField searchField;
    private JComboBox<String> categoryFilter;
    private JButton searchButton;
    private JCheckBox lowStockCheckBox;

    private static final int ITEM_PANEL_HEIGHT = 50;
    private static final int ITEM_PANEL_WIDTH = 750;

    public StockManagement(String role) {
        this.role = role;
        itemList = new ArrayList<>();
        setTitle("Stock Management");
        setSize(800, 650);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout());
        searchField = new JTextField(15);
        searchButton = createStyledButton("Search");
        searchButton.addActionListener(e -> refreshItemsPanel());

        categoryFilter = new JComboBox<>();
        categoryFilter.addItem("All Categories");
        loadCategories();
        categoryFilter.addActionListener(e -> refreshItemsPanel());

        lowStockCheckBox = new JCheckBox("Show Low Stock Only");
        lowStockCheckBox.addActionListener(e -> refreshItemsPanel());

        topPanel.add(new JLabel("Search:"));
        topPanel.add(searchField);
        topPanel.add(searchButton);
        topPanel.add(new JLabel("Category:"));
        topPanel.add(categoryFilter);
        topPanel.add(lowStockCheckBox);
        add(topPanel, BorderLayout.NORTH);

        itemsPanel = new JPanel();
        itemsPanel.setLayout(new BoxLayout(itemsPanel, BoxLayout.Y_AXIS));
        itemsPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        JScrollPane scrollPane = new JScrollPane(itemsPanel);
        add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton addItemButton = createStyledButton("Add Item");
        addItemButton.addActionListener(e -> new AddItemView());
        bottomPanel.add(addItemButton);
        add(bottomPanel, BorderLayout.SOUTH);

        refreshItemsPanel();
        setVisible(true);
    }

    private void loadCategories() {
        String sql = "SELECT category_name FROM categories";
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                categoryFilter.addItem(rs.getString("category_name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void refreshItemsPanel() {
        itemsPanel.removeAll();
        itemList.clear();

        String searchText = searchField.getText().trim();
        String selectedCategory = (String) categoryFilter.getSelectedItem();
        boolean filterLowStock = lowStockCheckBox.isSelected();

        String sql = "SELECT * FROM items i JOIN categories c ON i.category_id = c.category_id ";
        boolean hasSearch = !searchText.isEmpty();
        boolean hasCategoryFilter = selectedCategory != null && !selectedCategory.equals("All Categories");
        boolean whereAdded = false;

        if (hasCategoryFilter) {
            sql += "WHERE c.category_name = ? ";
            whereAdded = true;
        }
        if (hasSearch) {
            sql += (whereAdded ? "AND " : "WHERE ") + "i.name LIKE ? ";
            whereAdded = true;
        }
        if (filterLowStock) {
            sql += (whereAdded ? "AND " : "WHERE ") + "i.stock_level < i.low_stock_threshold ";
        }

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            int paramIndex = 1;
            if (hasCategoryFilter) {
                stmt.setString(paramIndex++, selectedCategory);
            }
            if (hasSearch) {
                stmt.setString(paramIndex, "%" + searchText + "%");
            }

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int itemId = rs.getInt("item_id");
                    String name = rs.getString("name");
                    int costPrice = rs.getInt("cost_price");
                    int stockLevel = rs.getInt("stock_level");
                    int lowThreshold = rs.getInt("low_stock_threshold");
                    int category_id = rs.getInt("category_id");
                    String category = rs.getString("category_name");

                    Item item = new Item(itemId, name, costPrice, stockLevel, category_id, lowThreshold);
                    itemList.add(item);

                    JPanel itemPanel = new JPanel(new BorderLayout());
                    itemPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
                    itemPanel.setBackground(Color.WHITE);
                    itemPanel.setPreferredSize(new Dimension(ITEM_PANEL_WIDTH, ITEM_PANEL_HEIGHT));
                    itemPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, ITEM_PANEL_HEIGHT));

                    JLabel itemLabel = new JLabel(name + " - Price: Rs " + costPrice + " - Stock: " + stockLevel +
                            " units (Category: " + category + ")");
                    itemLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                    itemLabel.setBorder(new EmptyBorder(5, 10, 5, 5));
                    itemLabel.setForeground(stockLevel < lowThreshold ? Color.RED : Color.BLACK);

                    JPanel buttonPanel = new JPanel();
                    buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
                    buttonPanel.setBackground(Color.WHITE);

                    JButton editStockButton = createStyledButton("Edit Stock");
                    editStockButton.addActionListener(e -> editStock(item));

                    if ("IS Manager".equals(role)) {
                        JButton editPriceButton = createStyledButton("Edit Price");
                        editPriceButton.addActionListener(e -> new EditItemView(item));

                        buttonPanel.add(Box.createHorizontalGlue()); 
                        buttonPanel.add(editStockButton);
                        buttonPanel.add(Box.createRigidArea(new Dimension(10, 0))); 
                        buttonPanel.add(editPriceButton);
                        buttonPanel.add(Box.createHorizontalGlue());
                    } else {
                        buttonPanel.add(Box.createHorizontalGlue());
                        buttonPanel.add(editStockButton);
                        buttonPanel.add(Box.createHorizontalGlue());
                    }

                    itemPanel.add(itemLabel, BorderLayout.CENTER);
                    itemPanel.add(buttonPanel, BorderLayout.EAST);
                    itemsPanel.add(itemPanel);
                    itemsPanel.add(Box.createVerticalStrut(10));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        itemsPanel.revalidate();
        itemsPanel.repaint();
    }

    private void editStock(Item item) {
        String newStockStr = JOptionPane.showInputDialog(this,
                "Enter new stock level for " + item.getName() + ":", item.getStock());
        if (newStockStr != null) {
            try {
                int newStock = Integer.parseInt(newStockStr);
                updateStockForItem(item, newStock);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "Invalid stock value!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void updateStockForItem(Item item, int newStock) {
        String sql = "UPDATE items SET stock_level = ? WHERE item_id = ?";
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, newStock);
            stmt.setInt(2, item.getId());
            int updatedRows = stmt.executeUpdate();
            if (updatedRows > 0) {
                JOptionPane.showMessageDialog(this,
                        "Stock updated successfully for " + item.getName() + "!");
                refreshItemsPanel();
            } else {
                JOptionPane.showMessageDialog(this,
                        "Failed to update stock for " + item.getName() + "!", "Error",
                        JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(64, 128, 128));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        return button;
    }
}
