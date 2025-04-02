package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AddItemView extends JFrame {

    private JTextField itemName;
    private JTextField itemPrice;
    private JTextField quantity;
    private JButton confirmBtn;
    private JComboBox<Category> itemCategory;

    public AddItemView() {
        setTitle("Add New Item to Inventory");
        setSize(400, 350);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Main Panel Styling
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel titleLabel = new JLabel("Add New Item");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));

        itemName = createStyledTextField();
        itemPrice = createStyledTextField();
        quantity = createStyledTextField();
        itemCategory = new JComboBox<>();
        getItemCategories();
        confirmBtn = createStyledButton("Confirm");
        confirmBtn.addActionListener(this::addItem);

        // Add Components
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(titleLabel, gbc);

        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.WEST;

        gbc.gridy = 1;
        mainPanel.add(createLabel("Item Name:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(itemName, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(createLabel("Category: "), gbc);
        gbc.gridx = 1;
        mainPanel.add(itemCategory, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        mainPanel.add(createLabel("Price:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(itemPrice, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        mainPanel.add(createLabel("Stock:"), gbc);
        gbc.gridx = 1;
        mainPanel.add(quantity, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(confirmBtn, gbc);

        add(mainPanel, BorderLayout.CENTER);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void getItemCategories(){
        String sql_get_categories = "SELECT * FROM categories";
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql_get_categories)) {



            ResultSet rs = stmt.executeQuery();
            while (rs.next()){
                itemCategory.addItem(new Category(rs.getInt("category_id"), rs.getString("category_name")));
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }

    }

    private void addItem(ActionEvent e) {
        String sql_add_item = "INSERT INTO items (name, cost_price, stock_level, category_id) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql_add_item)) {
            Category category = (Category)itemCategory.getSelectedItem() ;

            stmt.setString(1, itemName.getText());
            stmt.setInt(2, Integer.parseInt(itemPrice.getText()));
            stmt.setInt(3, Integer.parseInt(quantity.getText()));
            stmt.setInt(4, category.getCategoryID());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Item Added Successfully!");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to Add Item!", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }

    private JTextField createStyledTextField() {
        JTextField textField = new JTextField(15);
        textField.setFont(new Font("Arial", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 0,0), 1),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return textField;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        return label;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(64, 128, 128));
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setFocusPainted(false);
        return button;
    }
}
