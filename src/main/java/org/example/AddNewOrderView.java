package org.example;




import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.*;

public class AddNewOrderView extends JFrame {
    private JTextField customerField;
    private JTextField productField;
    private JTextField quantityField;
    private JComboBox customers;
    private JComboBox products;
    private JLabel totalPrice;
    private int tempTotal;


    public AddNewOrderView() {



        this.setTitle("Add New Order");
        this.setSize(300, 200);
        this.setLayout(new GridLayout(4, 2));
        tempTotal = 0;
        this.customers = new JComboBox<>();
        String sql_get_customers = "SELECT * FROM customers";
        try (
                Connection conn = DatabaseConnector.connect();
                PreparedStatement stmt = conn.prepareStatement(sql_get_customers);
                ResultSet rs = stmt.executeQuery();
        ) {
            while(rs.next()) {
                //stockInfo.append(rs.getString("item_id")).append(": ").append(rs.getInt("quantity")).append(" units\n");

                customers.addItem(new Customer(rs.getInt("customer_id"), rs.getString("name"), rs.getString("address"), rs.getString("contact_details")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        JLabel customerLabel = new JLabel("Customer Name:");

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

                products.addItem(new Item(rs.getInt("item_id"), rs.getString("name"), rs.getInt("cost_price"), rs.getInt("stock_level")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        totalPrice = new JLabel();
        JLabel quantityLabel = new JLabel("Quantity:");
        this.quantityField = new JTextField();
        quantityField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                changePrice();
            }
        });
        JButton submitButton = new JButton("Place Order");
        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                AddNewOrderView.this.placeOrder();
            }
        });


        this.add(customerLabel);
        this.add(this.customers);
        this.add(productLabel);
//        this.add(this.productField);
        this.add(products);
        this.add(quantityLabel);
        this.add(this.quantityField);
        this.add(totalPrice);
        this.add(submitButton);
        this.setVisible(true);
    }

    private void changePrice() {
        Item product = (Item) this.products.getSelectedItem();
        tempTotal = product.getCost() * Integer.parseInt( quantityField.getText());

        totalPrice.setText("Total cost: Rs " + tempTotal);
    }

    private void placeOrder() {
        Customer customer = (Customer) this.customers.getSelectedItem();
        Item product = (Item) this.products.getSelectedItem();
        int quantity = Integer.parseInt(this.quantityField.getText());

        String sql = "INSERT INTO orders ( customer_id, item_id, quantity, status, total_price) VALUES (?, ?, ?, ?, ?)";

        try (
                Connection conn = DatabaseConnector.connect();
                PreparedStatement stmt = conn.prepareStatement(sql);
        ) {


            if(quantity > product.getStock()){
//                addBackOrder();
            }

            stmt.setInt(1, customer.getId());
//            stmt.setInt(2,);
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

    private void addBackOrder() {

        String sql_add_back_order = "INSERT INTO backOrders ( OrderID, ItemID, Quantity, ExpectedDate) VALUES (?, ?, ?, ?)";
        try (
                Connection conn = DatabaseConnector.connect();
                PreparedStatement stmt = conn.prepareStatement(sql_add_back_order);
        ) {





        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
