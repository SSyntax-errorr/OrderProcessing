package org.example;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;

public class OrderManagement extends JFrame {
    private JTextField customerField;
    private JTextField productField;
    private JTextField quantityField;

    public OrderManagement() {
        this.setTitle("Order Management");
        this.setSize(300, 200);
        this.setLayout(new GridLayout(4, 2));
        JLabel customerLabel = new JLabel("Customer Name:");
        this.customerField = new JTextField();
        JLabel productLabel = new JLabel("Product Name:");
        this.productField = new JTextField();
        JLabel quantityLabel = new JLabel("Quantity:");
        this.quantityField = new JTextField();
        JButton submitButton = new JButton("Place Order");
        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                OrderManagement.this.placeOrder();
            }
        });
        this.add(customerLabel);
        this.add(this.customerField);
        this.add(productLabel);
        this.add(this.productField);
        this.add(quantityLabel);
        this.add(this.quantityField);
        this.add(submitButton);
        this.setVisible(true);
    }

    private void placeOrder() {
        String customer = this.customerField.getText();
        String product = this.productField.getText();
        int quantity = Integer.parseInt(this.quantityField.getText());
        String sql = "INSERT INTO Orders (customer_name, product_name, quantity) VALUES (?, ?, ?)";

        try (
                Connection conn = DatabaseConnector.connect();
                PreparedStatement stmt = conn.prepareStatement(sql);
        ) {
            stmt.setString(1, customer);
            stmt.setString(2, product);
            stmt.setInt(3, quantity);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Order placed successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
