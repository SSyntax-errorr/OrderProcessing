package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.util.ArrayList;

public class AddNewOrderView extends JFrame {
    private JComboBox<Customer> customers;
    private JComboBox<Item> products;
    private JLabel totalPrice;
    private JTextField quantityField;
    private int tempTotal;
    private JTextField chequeNumberField;
    private JLabel chequeNumberLabel;
    private JComboBox<String> paymentMethodCombo;
    private ArrayList<TransportCharge> transportChargesList = new ArrayList<>();

    public AddNewOrderView() {
        setTitle("Add New Order");
        setSize(450, 450);
        setLocationRelativeTo(null);
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        // Title Label
        JLabel titleLabel = new JLabel("Order Processing System");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(64, 128, 128));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(titleLabel, gbc);

        // Customer Label & ComboBox
        JLabel customerLabel = new JLabel("Customer:");
        customerLabel.setFont(new Font("Arial", Font.BOLD, 14));
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        add(customerLabel, gbc);

        customers = new JComboBox<>();
        customers.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1;
        add(customers, gbc);

        loadCustomers();

        // Product Label & ComboBox
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel productLabel = new JLabel("Product:");
        productLabel.setFont(new Font("Arial", Font.BOLD, 14));
        add(productLabel, gbc);

        products = new JComboBox<>();
        products.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1;
        add(products, gbc);

        loadProducts();

        // Quantity Label & Field
        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel quantityLabel = new JLabel("Quantity:");
        quantityLabel.setFont(new Font("Arial", Font.BOLD, 14));
        add(quantityLabel, gbc);

        quantityField = new JTextField(10);
        quantityField.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1;
        add(quantityField, gbc);

        // Total Price Label
        gbc.gridx = 0;
        gbc.gridy = 4;
        totalPrice = new JLabel("Total cost: Rs 0");
        totalPrice.setFont(new Font("Arial", Font.BOLD, 14));
        add(totalPrice, gbc);

        // Payment Method
        gbc.gridx = 0;
        gbc.gridy = 5;
        JLabel paymentMethodLabel = new JLabel("Payment Method:");
        paymentMethodLabel.setFont(new Font("Arial", Font.BOLD, 14));
        add(paymentMethodLabel, gbc);

        paymentMethodCombo = new JComboBox<>(new String[]{"Cash", "Cheque", "Credit Card"});
        paymentMethodCombo.setFont(new Font("Arial", Font.PLAIN, 14));
        gbc.gridx = 1;
        add(paymentMethodCombo, gbc);

        paymentMethodCombo.addActionListener(e -> {
            boolean showCheque = "Cheque".equals(paymentMethodCombo.getSelectedItem());
            chequeNumberLabel.setVisible(showCheque);
            chequeNumberField.setVisible(showCheque);
            revalidate();
            repaint();
        });

        // Cheque Number Field
        gbc.gridx = 0;
        gbc.gridy = 6;
        chequeNumberLabel = new JLabel("Cheque Number:");
        chequeNumberLabel.setFont(new Font("Arial", Font.BOLD, 14));
        chequeNumberLabel.setVisible(false);
        add(chequeNumberLabel, gbc);

        gbc.gridx = 1;
        chequeNumberField = new JTextField(10);
        chequeNumberField.setFont(new Font("Arial", Font.PLAIN, 14));
        chequeNumberField.setVisible(false);
        add(chequeNumberField, gbc);

        // Save Quote Button
        gbc.gridx = 0;
        gbc.gridy = 7;
        JButton saveQuote = createStyledButton("Save as Quote");
        saveQuote.addActionListener(e -> saveAsQuote());
        add(saveQuote, gbc);

        // Submit Order Button
        gbc.gridx = 1;
        JButton submitButton = createStyledButton("Place Order");
        submitButton.addActionListener(e -> placeOrder());
        add(submitButton, gbc);

        // Add listeners for dynamic price updates
        quantityField.addKeyListener(new KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                changePrice();
            }
        });

        products.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                changePrice();
            }
        });

        customers.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                changePrice();
            }
        });

        setVisible(true);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(64, 128, 128));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(150, 40));
        return button;
    }

    private void loadCustomers() {
        String sql = "SELECT * FROM customers INNER JOIN transportcosts ON transportcosts.customerID = customers.customer_id";
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
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
    }

    private void loadProducts() {
        String sql = "SELECT * FROM items";
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
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
    }

    private void changePrice() {
        try {
            Item product = (Item) products.getSelectedItem();
            Customer customer = (Customer) customers.getSelectedItem();
            int quantity = Integer.parseInt(quantityField.getText().trim());
            int transportCharge = transportChargesList.stream()
                    .filter(t -> t.getCustomerID() == customer.getId())
                    .findFirst().map(TransportCharge::getTransportCharge).orElse(0);
            tempTotal = (product.getCost() * quantity) + transportCharge;
            totalPrice.setText("Total cost: Rs " + tempTotal);
        } catch (NumberFormatException e) {
            totalPrice.setText("Total cost: Rs 0");
        }
    }

    private void saveAsQuote() {
        Customer customer = (Customer) customers.getSelectedItem();
        Item product = (Item) products.getSelectedItem();
        int quantity = Integer.parseInt(quantityField.getText().trim());
        String paymentMethod = (String) paymentMethodCombo.getSelectedItem();
        String chequeNumber = "Cheque".equals(paymentMethod) ? chequeNumberField.getText().trim() : "";

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
