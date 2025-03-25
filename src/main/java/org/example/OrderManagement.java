package org.example;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.*;

public class OrderManagement extends JFrame {
    private JTextField customerField;
    private JTextField productField;
    private JTextField quantityField;
    private JComboBox products;

    public OrderManagement() {



        this.setTitle("Order Management");
        this.setSize(300, 200);
        this.setLayout(new GridLayout(4, 2));
        JLabel customerLabel = new JLabel("Customer Name:");
        this.customerField = new JTextField();
        JLabel productLabel = new JLabel("Product Name:");
        //this.productField = new JTextField();
        this.products = new JComboBox<>();
        String sql = "SELECT * FROM items";
        try (
                Connection conn = DatabaseConnector.connect();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery();
        ) {
            while(rs.next()) {
                //stockInfo.append(rs.getString("item_id")).append(": ").append(rs.getInt("quantity")).append(" units\n");

                products.addItem(new Item(rs.getInt("item_id"), rs.getString("name")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

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
//        this.add(this.productField);
        this.add(products);
        this.add(quantityLabel);
        this.add(this.quantityField);
        this.add(submitButton);
        this.setVisible(true);
    }

    private void placeOrder() {
        String customer = this.customerField.getText();
        Item product = (Item) this.products.getSelectedItem();
        int quantity = Integer.parseInt(this.quantityField.getText());
        String sql = "INSERT INTO orders ( customer_id, item_id, quantity, status) VALUES (?, ?, ?, ?)";

        try (
                Connection conn = DatabaseConnector.connect();
                PreparedStatement stmt = conn.prepareStatement(sql);
        ) {
            stmt.setInt(1, 1);
//            stmt.setInt(2,);
            stmt.setInt(2, product.getId());
            stmt.setInt(3, quantity);
            stmt.setString(4, "pending");
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Order placed successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
