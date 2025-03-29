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
    private JTextField customerField;
    private JTextField productField;
    private JTextField quantityField;
    private JComboBox customers;
    private JComboBox products;
    private JLabel totalPrice;
    private int tempTotal;
    private int transportCost;


    public AddNewOrderView() {

        transportCost = 0;

        this.setTitle("Add New Order");
        this.setSize(400, 200);
        this.setLayout(new GridLayout(5, 2));
        tempTotal = 0;
        this.customers = new JComboBox<>();
        ArrayList<TransportCharge> transportChargesList = new ArrayList<>();
        String sql_get_customers = "SELECT * FROM customers INNER JOIN transportcosts ON transportcosts.customerID = customers.customer_id";
        try (
                Connection conn = DatabaseConnector.connect();
                PreparedStatement stmt = conn.prepareStatement(sql_get_customers);
                ResultSet rs = stmt.executeQuery();

        ) {
//            transportCost = rs.getInt("transportCharge");
            while(rs.next()) {
                //stockInfo.append(rs.getString("item_id")).append(": ").append(rs.getInt("quantity")).append(" units\n");

                customers.addItem(new Customer(rs.getInt("customer_id"), rs.getString("name"), rs.getString("address"), rs.getString("contact_details")));
                transportChargesList.add(new TransportCharge(rs.getInt("transportID"), rs.getInt("customer_id"), rs.getString("transportMethod"), rs.getInt("transportCharge")));
            }

//            System.out.println(transportCost);
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
                changePrice(transportChargesList);
            }
        });
        JButton submitButton = new JButton("Place Order");
        submitButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                AddNewOrderView.this.placeOrder();
            }
        });
        JButton saveQuote = new JButton("Save as quote");
        saveQuote.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveAsQuote();
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
        this.add(saveQuote);
        this.add(submitButton);
        this.setVisible(true);
    }

    private void saveAsQuote() {
        Customer customer = (Customer) this.customers.getSelectedItem();
        Item product = (Item) this.products.getSelectedItem();
        int quantity = Integer.parseInt(this.quantityField.getText());

        String sql = "INSERT INTO quotes ( itemID, customerID, quantity,unitPrice, status) VALUES (?, ?, ?, ?, ?)";

        try (
                Connection conn = DatabaseConnector.connect();
                PreparedStatement stmt = conn.prepareStatement(sql);
        ) {




            stmt.setInt(2, customer.getId());
//            stmt.setInt(2,);
            stmt.setInt(1, product.getId());
            stmt.setInt(3, quantity);
            stmt.setString(5, "pending");
            stmt.setInt(4, tempTotal);
            stmt.executeUpdate();
            JOptionPane.showMessageDialog(this, "Quote added succesfully successfully!");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void changePrice(ArrayList<TransportCharge> transportCharges) {
        Item product = (Item) this.products.getSelectedItem();
        Customer customer = (Customer) this.customers.getSelectedItem();
        int transportCharge = 0;
        for (TransportCharge t : transportCharges){
            if (t.getCustomerID() == customer.getId()){
                transportCharge = t.getTransportCharge();
            }
        }
        tempTotal = (product.getCost() * Integer.parseInt( quantityField.getText())) + transportCharge;

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
