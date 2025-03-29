package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EditItemView extends JFrame {
    private JTextField newPrice;
    private JButton confirmBtn;
    private Item item;

    public EditItemView(Item item) {
        this.item = item;
        setTitle("Edit Price - " + item.getName());
        setSize(400, 200);
        setLayout(new GridLayout(3, 2));

        add(new JLabel("New Price for " + item.getName() + ":"));
        newPrice = new JTextField();
        add(newPrice);

        confirmBtn = new JButton("Confirm");
        confirmBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                editPrice();
            }
        });
        add(confirmBtn);

        setVisible(true);
    }

    private void editPrice() {
        String sql_edit_price = "UPDATE items SET cost_price = ? WHERE item_id = ?";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql_edit_price)) {

            stmt.setDouble(1, Double.parseDouble(newPrice.getText()));
            stmt.setInt(2, item.getId());

            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Price updated successfully!");
            } else {
                JOptionPane.showMessageDialog(null, "Item not found!", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
