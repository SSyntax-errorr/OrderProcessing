package org.example;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Objects;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class Dashboard extends JFrame {
    public Dashboard(String role) {
        this.setTitle("Dashboard - " + role);
        this.setSize(400, 300);
        this.setDefaultCloseOperation(3);
        this.setLayout(new FlowLayout());
        JLabel welcomeLabel = new JLabel("Welcome, " + role);
        this.add(welcomeLabel);
        JButton orderButton = new JButton("Manage Orders");
        JButton stockButton = new JButton("Manage Stock");
        JButton addOrderButton = new JButton("Add Orders");
        JButton newCustomerButton = new JButton("Add Customer");
        orderButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new OrderManagement();
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

        if(Objects.equals(role, "Sales Person")|| Objects.equals(role, "IS Manager")){
            this.add(addOrderButton);
            this.add(orderButton);
        }



        if (Objects.equals(role, "Inventory Officer") || Objects.equals(role, "IS Manager")) {
            this.add(stockButton);
        }

        if (Objects.equals(role, "IS Manager")) {
            this.add(newCustomerButton);
        }

        this.setVisible(true);
    }
}
