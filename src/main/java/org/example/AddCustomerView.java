package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class AddCustomerView extends JFrame {
    private JTextField customerName, customerAddress, contactDetails, transportMethod, transportCharge;
    private JButton addCustomer;

    public AddCustomerView() {
        setTitle("Add New Customer");
        setSize(400, 350);
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setLayout(new GridBagLayout());
        getContentPane().setBackground(Color.WHITE);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Labels
        JLabel nameLabel = createStyledLabel("Customer Name:");
        JLabel addressLabel = createStyledLabel("Address:");
        JLabel contactLabel = createStyledLabel("Contact:");
        JLabel transportMethodLabel = createStyledLabel("Transport Method:");
        JLabel transportLabel = createStyledLabel("Transport Cost:");

        // Fields
        customerName = createStyledTextField();
        customerAddress = createStyledTextField();
        contactDetails = createStyledTextField();
        transportMethod = createStyledTextField();
        transportCharge = createStyledTextField();

        // Button
        addCustomer = createStyledButton("Add Customer");
        addCustomer.addActionListener(e -> addNewCustomer());

        // Adding components to the layout
        gbc.gridx = 0;
        gbc.gridy = 0;
        add(nameLabel, gbc);

        gbc.gridx = 1;
        add(customerName, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(addressLabel, gbc);

        gbc.gridx = 1;
        add(customerAddress, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        add(contactLabel, gbc);

        gbc.gridx = 1;
        add(contactDetails, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        add(transportMethodLabel, gbc);

        gbc.gridx = 1;
        add(transportMethod, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        add(transportLabel, gbc);

        gbc.gridx = 1;
        add(transportCharge, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        add(addCustomer, gbc);

        setVisible(true);
    }

    private void addNewCustomer() {
        String sql_add_customer = "INSERT INTO customers (name, address, contact_details) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql_add_customer, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, customerName.getText());
            stmt.setString(2, customerAddress.getText());
            stmt.setString(3, contactDetails.getText());

            int rowsAffected = stmt.executeUpdate();

            if (rowsAffected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int generatedId = rs.getInt(1);
                        String sql_add_transport = "INSERT INTO transportcosts (customerID, transportMethod, transportCharge) VALUES (?, ?, ?)";
                        try (PreparedStatement stmt2 = conn.prepareStatement(sql_add_transport)) {
                            stmt2.setInt(1, generatedId);
                            stmt2.setString(2, transportMethod.getText());
                            stmt2.setInt(3, Integer.parseInt(transportCharge.getText()));
                            stmt2.executeUpdate();
                        }
                    }
                }
                JOptionPane.showMessageDialog(this, "Customer added successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Customer could not be added!", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.BOLD, 14));
        label.setForeground(Color.DARK_GRAY);
        return label;
    }

    private JTextField createStyledTextField() {
        JTextField textField = new JTextField();
        textField.setFont(new Font("Arial", Font.PLAIN, 14));
        textField.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        textField.setPreferredSize(new Dimension(200, 30));  // Ensuring proper size
        return textField;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(64, 128, 128));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        return button;
    }
}
