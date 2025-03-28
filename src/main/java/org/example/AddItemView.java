package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class AddItemView extends JFrame {

    private JTextField itemName;
    private JTextField itemPrice;
    private JTextField quantity;
    private JButton confirmBtn;


    public AddItemView(){

        this.setTitle("Add New Item to Inventory");
        this.setSize(400, 300);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setLayout(new GridLayout(4, 2));
        itemName = new JTextField();
        itemPrice = new JTextField();
        quantity = new JTextField();
        confirmBtn = new JButton();

        confirmBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                addItem();
            }
        });

        this.add(itemName);
        this.add(itemPrice);
        this.add(quantity);
        this.add(confirmBtn);
        this.setVisible(true);
    }


    public void addItem() {
        String sql_add_item = "INSERT INTO items (name, cost_price, stock_level) VALUES (?, ?, ?)";


        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql_add_item) ) {

            stmt.setString(1, itemName.getText());
            stmt.setInt(2, Integer.parseInt( itemPrice.getText()));
            stmt.setInt(3, Integer.parseInt( quantity.getText()));
//            stmt2.setInt(1, Integer.parseInt( quantity.getText()));

            int rowsAffected = stmt.executeUpdate();
//            int rowsAffected2 = stmt2.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(null, "Item Added successfully!");
            } else {
                JOptionPane.showMessageDialog(null, "Item could not be added!", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



}
