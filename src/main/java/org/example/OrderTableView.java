package org.example;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class OrderTableView extends JFrame {

    private String[] columns = {"Order ID", "Customer ID", "Item ID", "Quantity", "Status"};
    private JTable orderTable;
    private DefaultTableModel orderTableModel;

    public OrderTableView() {
        this.setTitle("Order Management");
        this.setSize(800, 400);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        this.getContentPane().setBackground(Color.decode("#F5F5F5")); // Light gray background

        orderTableModel = new DefaultTableModel(columns, 0);
        orderTable = new JTable(orderTableModel);
        orderTable.setRowHeight(40);
        orderTable.setFont(new Font("Arial", Font.PLAIN, 16));

        // Table header styling
        orderTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 18));
        orderTable.getTableHeader().setBackground(new Color(64, 128, 128)); // Teal color
        orderTable.getTableHeader().setForeground(Color.WHITE);

        // Center text in table columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        for (int i = 0; i < orderTableModel.getColumnCount(); i++) {
            orderTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        loadOrders();

        JScrollPane scrollPane = new JScrollPane(orderTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding around table
        this.add(scrollPane);

        this.setVisible(true);
    }

    private void loadOrders() {
        String sql = "SELECT * FROM orders";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                orderTableModel.addRow(new Object[]{
                        rs.getInt("order_id"),
                        rs.getInt("customer_id"),
                        rs.getInt("item_id"),
                        rs.getInt("quantity"),
                        rs.getString("status")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
