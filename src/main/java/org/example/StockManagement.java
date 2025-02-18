package org.example;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class StockManagement extends JFrame {
    private JTextArea stockDisplay;
    private JTextField itemNameField;
    private JTextField newStockField;

    public StockManagement() {
        this.setTitle("Stock Management");
        this.setSize(400, 300);
        this.setLayout(new BorderLayout());
        this.stockDisplay = new JTextArea();
        this.stockDisplay.setEditable(false);
        this.refreshStockDisplay();
        this.add(new JScrollPane(this.stockDisplay), "Center");
        JPanel updatePanel = new JPanel(new GridLayout(3, 2));
        JLabel itemLabel = new JLabel("Item Name:");
        this.itemNameField = new JTextField();
        JLabel stockLabel = new JLabel("New Stock Level:");
        this.newStockField = new JTextField();
        JButton updateButton = new JButton("Update Stock");
        updateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                StockManagement.this.updateStock();
            }
        });
        updatePanel.add(itemLabel);
        updatePanel.add(this.itemNameField);
        updatePanel.add(stockLabel);
        updatePanel.add(this.newStockField);
        updatePanel.add(new JLabel());
        updatePanel.add(updateButton);
        this.add(updatePanel, "South");
        this.setVisible(true);
    }

    private void refreshStockDisplay() {
        StringBuilder stockInfo = new StringBuilder();
        String sql = "SELECT * FROM Stock";

        try (
                Connection conn = DatabaseConnector.connect();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery();
        ) {
            while(rs.next()) {
                stockInfo.append(rs.getString("item_name")).append(": ").append(rs.getInt("stock_level")).append(" units\n");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        this.stockDisplay.setText(stockInfo.toString());
    }

    private void updateStock() {
        String itemName = this.itemNameField.getText();

        int newStock;
        try {
            newStock = Integer.parseInt(this.newStockField.getText());
        } catch (NumberFormatException var10) {
            JOptionPane.showMessageDialog(this, "Invalid stock value!", "Error", 0);
            return;
        }

        String sql = "UPDATE Stock SET stock_level = ? WHERE item_name = ?";

        try (
                Connection conn = DatabaseConnector.connect();
                PreparedStatement stmt = conn.prepareStatement(sql);
        ) {
            stmt.setInt(1, newStock);
            stmt.setString(2, itemName);
            int updatedRows = stmt.executeUpdate();
            if (updatedRows > 0) {
                JOptionPane.showMessageDialog(this, "Stock updated successfully!");
                this.refreshStockDisplay();
            } else {
                JOptionPane.showMessageDialog(this, "Item not found!", "Error", 0);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
