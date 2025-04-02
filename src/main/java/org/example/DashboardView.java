package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;

public class DashboardView extends JFrame {
    public DashboardView(String role) {
        setTitle("Dashboard - " + role);
        setSize(400, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center window on the screen
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // Space between components

        // Title label
        JLabel welcomeLabel = new JLabel("Welcome, " + role);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 18));
        welcomeLabel.setForeground(new Color(64, 128, 128)); // Blue color
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        add(welcomeLabel, gbc);

        // Buttons with styling
        JButton orderButton = createStyledButton("Manage Orders");
        JButton stockButton = createStyledButton("Manage Stock");
        JButton addOrderButton = createStyledButton("Add Orders");
        JButton newCustomerButton = createStyledButton("Add Customer");
        JButton viewQuotes = createStyledButton("View Quotes");
        JButton addUser = createStyledButton("Add New User");

        // Action Listeners
        orderButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new OrderTableView();
            }
        });
        addOrderButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new AddNewOrderView();
            }
        });
        stockButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new StockManagement(role);
            }
        });
        newCustomerButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AddCustomerView();
            }
        });
        viewQuotes.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new QuotesTableView();
            }
        });
        addUser.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                new AddUserView();
            }
        });

        // Layout buttons based on user role
        if (Objects.equals(role, "Sales Person") || Objects.equals(role, "IS Manager")) {
            gbc.gridx = 0;
            gbc.gridy = 1;
            gbc.gridwidth = 2;
            add(addOrderButton, gbc);

            gbc.gridy = 2;
            add(orderButton, gbc);

            gbc.gridy = 3;
            add(viewQuotes, gbc);
        }

        if (Objects.equals(role, "Inventory Officer") || Objects.equals(role, "IS Manager")) {
            gbc.gridy = 4;
            add(stockButton, gbc);
        }

        if (Objects.equals(role, "IS Manager")) {
            gbc.gridy = 5;
            add(newCustomerButton, gbc);

            gbc.gridy = 6;
            add(addUser, gbc);
        }

        setVisible(true);
    }

    // Helper method to create styled buttons
    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setBackground(new Color(64, 128, 128));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(200, 40));
        return button;
    }

    public static void main(String[] args) {
        new DashboardView("Sales Person");
    }
}
