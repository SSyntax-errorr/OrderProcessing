package org.example;//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class OrderTableView extends JFrame {

    String[] columns = {"Order ID", "Customer ID", "Item ID", "Quantity", "Status"};
    private JTable orderTable;
    DefaultTableModel orderTableModel;

    public OrderTableView() {
        this.setTitle("Order Management");
        this.setSize(400, 300);
        orderTableModel = new DefaultTableModel(columns, 0);
        orderTable = new JTable(orderTableModel);
        StringBuilder stockInfo = new StringBuilder();
        String sql = "SELECT * FROM orders";

        try (
                Connection conn = DatabaseConnector.connect();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery();
        ) {
            while(rs.next()) {
                //stockInfo.append(rs.getString("item_id")).append(": ").append(rs.getInt("quantity")).append(" units\n");
                orderTableModel.addRow(new Object[]{rs.getInt("order_id"),rs.getInt("customer_id"), rs.getInt("item_id"), rs.getInt("quantity"), rs.getString("status")  });
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        this.add(new JScrollPane(orderTable));

        this.setVisible(true);
    }


}
