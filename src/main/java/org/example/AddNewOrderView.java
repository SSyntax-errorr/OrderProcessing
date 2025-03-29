package org.example;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import javax.swing.*;

public class AddNewOrderView extends JFrame {
    private JComboBox customers;
    private JComboBox products;
    private JLabel totalPrice;
    private int tempTotal;
    private int transportCost;
    private JTextField quantityField;

    // New payment method components:
    private JComboBox<String> paymentMethodCombo;
    private JTextField chequeNumberField;
    private JLabel chequeNumberLabel;

    public AddNewOrderView() {
        transportCost = 0;
        tempTotal = 0;

        this.setTitle("Add New Order");
        // Increase number of rows to accommodate new payment fields
        this.setSize(400, 300);
        this.setLayout(new GridLayout(7, 2));

        // Populate customers combobox and transport charges list
        this.customers = new JComboBox<>();
        ArrayList<TransportCharge> transportChargesList = new ArrayList<>();
        String sql_get_customers = "SELECT * FROM customers INNER JOIN transportcosts ON transportcosts.customerID = customers.customer_id";
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql_get_customers);
             ResultSet rs = stmt.executeQuery();) {
            while(rs.next()) {
                customers.addItem(new Customer(rs.getInt("customer_id"),
                        rs.getString("name"),
                        rs.getString("address"),
                        rs.getString("contact_details")));
                transportChargesList.add(new TransportCharge(rs.getInt("transportID"),
                        rs.getInt("customer_id"),
                        rs.getString("transportMethod"),
                        rs.getInt("transportCharge")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        JLabel customerLabel = new JLabel("Customer Name:");
        JLabel productLabel = new JLabel("Product Name:");
        this.products = new JComboBox<>();
        String sql = "SELECT * FROM items";
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery();) {
            while(rs.next()) {
                products.addItem(new Item(rs.getInt("item_id"),
                        rs.getString("name"),
                        rs.getInt("cost_price"),
                        rs.getInt("stock_level"),
                        rs.getInt("category_id"),
                        rs.getInt("low_stock_threshold")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        totalPrice = new JLabel("Total cost: Rs 0");
        JLabel quantityLabel = new JLabel("Quantity:");
        this.quantityField = new JTextField();
        quantityField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changePrice(transportChargesList);
            }
        });

        // New Payment Method combobox
        JLabel paymentMethodLabel = new JLabel("Payment Method:");
        paymentMethodCombo = new JComboBox<>(new String[]{"Cash", "Cheque", "Credit Card"});
        // When the payment method changes, show/hide cheque number field
        paymentMethodCombo.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String method = (String) paymentMethodCombo.getSelectedItem();
                boolean showCheque = "Cheque".equals(method);
                chequeNumberField.setVisible(showCheque);
                chequeNumberLabel.setVisible(showCheque);
                // Force layout update
                AddNewOrderView.this.revalidate();
                AddNewOrderView.this.repaint();
            }
        });

        // New Cheque Number field and label; initially hidden
        chequeNumberLabel = new JLabel("Cheque Number:");
        chequeNumberField = new JTextField();
        chequeNumberLabel.setVisible(false);
        chequeNumberField.setVisible(false);

        // Create buttons
        JButton submitButton = new JButton("Place Order");
        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                AddNewOrderView.this.placeOrder();
            }
        });
        JButton saveQuote = new JButton("Save as Quote");
        saveQuote.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveAsQuote();
            }
        });

        // Add components to frame (using GridLayout rows)
        this.add(customerLabel);
        this.add(this.customers);
        this.add(productLabel);
        this.add(products);
        this.add(quantityLabel);
        this.add(this.quantityField);
        this.add(totalPrice);
        this.add(saveQuote);
        // Payment method row
        this.add(paymentMethodLabel);
        this.add(paymentMethodCombo);
        // Cheque number row (hidden unless cheque is selected)
        this.add(chequeNumberLabel);
        this.add(chequeNumberField);
        // Order submit button
        this.add(submitButton);

        this.setVisible(true);
    }

    private void saveAsQuote() {
        Customer customer = (Customer) this.customers.getSelectedItem();
        Item product = (Item) this.products.getSelectedItem();
        int quantity = Integer.parseInt(this.quantityField.getText());
        String paymentMethod = (String) paymentMethodCombo.getSelectedItem();
        String chequeNumber = "";
        if ("Cheque".equals(paymentMethod)) {
            chequeNumber = chequeNumberField.getText().trim();
        }

        // Updated SQL: Assumes quotes table has payment_method and cheque_number columns.
        String sql = "INSERT INTO quotes (itemID, customerID, quantity, unitPrice, status, payment_method, cheque_number) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, product.getId());
            stmt.setInt(2, customer.getId());
            stmt.setInt(3, quantity);
            stmt.setInt(4, tempTotal);
            stmt.setString(5, "pending");
            stmt.setString(6, paymentMethod);
            stmt.setString(7, chequeNumber);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Quote added successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void changePrice(ArrayList<TransportCharge> transportCharges) {
        Item product = (Item) this.products.getSelectedItem();
        Customer customer = (Customer) this.customers.getSelectedItem();
        int transportCharge = 0;
        for (TransportCharge t : transportCharges) {
            if (t.getCustomerID() == customer.getId()) {
                transportCharge = t.getTransportCharge();
            }
        }
        tempTotal = (product.getCost() * Integer.parseInt(quantityField.getText())) + transportCharge;
        totalPrice.setText("Total cost: Rs " + tempTotal);
    }

    private void placeOrder() {
        Customer customer = (Customer) this.customers.getSelectedItem();
        Item product = (Item) this.products.getSelectedItem();
        int quantity = Integer.parseInt(this.quantityField.getText());
        String sql = "INSERT INTO orders (customer_id, item_id, quantity, status, total_price) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            if (quantity > product.getStock()) {
                // addBackOrder();
            }
            stmt.setInt(1, customer.getId());
            stmt.setInt(2, product.getId());
            stmt.setInt(3, quantity);
            stmt.setString(4, "pending");
            stmt.setInt(5, tempTotal);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Order placed successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
