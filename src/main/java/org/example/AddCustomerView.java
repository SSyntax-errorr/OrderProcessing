package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;

public class AddCustomerView extends JFrame {

    private JLabel nameLabel;
    private JLabel addressLabel;
    private JLabel contactLabel;
    private JLabel transportMethodLabel;
    private JLabel transportLabel;

    private JTextField customerName;
    private JTextField customerAddress;
    private JTextField contactDetails;
    private JTextField transportMethod;
    private JTextField transportCharge;
    private JButton addCustomer;

    public AddCustomerView(){
        this.setTitle("Add new Customer");
        this.setSize(400, 300);
        this.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        this.setLayout(new GridLayout(6, 2));
        nameLabel = new JLabel("Customer name:");
        addressLabel = new JLabel("Address:");
        contactLabel = new JLabel("Contact:");
        transportMethodLabel = new JLabel("Transport Method");
        transportLabel = new JLabel("Transport Cost:");
        customerName = new JTextField();
        customerAddress = new JTextField();
        contactDetails = new JTextField();
        transportMethod = new JTextField();
        transportCharge = new JTextField();
        addCustomer = new JButton("Add");
        addCustomer.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addNewCustomer();
            }
        });


        this.add(nameLabel);
        this.add(customerName);
        this.add(addressLabel);
        this.add(customerAddress);
        this.add(contactLabel);
        this.add(contactDetails);
        this.add(transportMethodLabel);
        this.add(transportMethod);
        this.add(transportLabel);
        this.add(transportCharge);
        this.add(addCustomer);
        this.setVisible(true);
    }

    private void addNewCustomer() {
        String sql_add_customer = "INSERT INTO customers (name, address, contact_details) VALUES (?, ?, ?)";


        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql_add_customer,  Statement.RETURN_GENERATED_KEYS) ) {

            stmt.setString(1, customerName.getText());
            stmt.setString(2,  customerAddress.getText());
            stmt.setString(3, contactDetails.getText());
//            stmt2.setInt(1, Integer.parseInt( quantity.getText()));

            int rowsAffected = stmt.executeUpdate();
//            int rowsAffected2 = stmt2.executeUpdate();
            if (rowsAffected > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        int generatedId = rs.getInt(1);
                        String sql_add_transport = "INSERT INTO transportcosts (customerID, transportMethod, transportCharge) VALUES (?, ?, ?)";
                        PreparedStatement stmt2 = conn.prepareStatement(sql_add_transport);
                        stmt2.setInt(1, generatedId);
                        stmt2.setString(2,  transportMethod.getText());
                        stmt2.setInt(3, Integer.parseInt( transportCharge.getText()));
                        stmt2.executeUpdate();

                    }
                }
                JOptionPane.showMessageDialog(null, "Customer Added successfully!");
            } else {
                JOptionPane.showMessageDialog(null, "Customer could not be added!", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
