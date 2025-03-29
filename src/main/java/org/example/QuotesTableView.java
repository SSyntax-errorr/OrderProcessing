package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class QuotesTableView extends JFrame {
    private String[] columns = {"Quote ID", "Customer Name", "Item", "Quantity", "Total Price", "Action"};
    private DefaultTableModel quotesTableModel;
    private JTable quotesTable;
    private int customerId;
    String item;
    int itemID;

    public QuotesTableView() {
        this.setTitle("Quotes");
        this.setSize(600, 400);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        quotesTableModel = new DefaultTableModel(columns, 0);
        quotesTable = new JTable(quotesTableModel);
        quotesTable.setRowHeight(40);
        // Set custom renderer and editor for buttons
        quotesTable.getColumn("Action").setCellRenderer(new ButtonRenderer());
        quotesTable.getColumn("Action").setCellEditor(new ButtonEditor());
        customerId = -1;
        itemID = -1;
        item = "";
        loadPendingQuotes();

        this.add(new JScrollPane(quotesTable));
        this.setVisible(true);
    }

    private void loadPendingQuotes() {
        String sql = "SELECT * FROM quotes q JOIN customers c ON q.customerID = c.customer_id JOIN items i ON q.itemID = i.item_id WHERE q.status = 'pending'";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int quoteId = rs.getInt("QuoteDetailID");
                customerId = rs.getInt("customer_id");
                String customerName = rs.getString("name");
                String item = "(" + rs.getInt("itemID") + ") " + rs.getString("i.name");
                itemID = rs.getInt("itemID");
                int quantity = rs.getInt("quantity");
                double totalPrice = rs.getDouble("unitPrice");

                // Add row with button placeholder
                quotesTableModel.addRow(new Object[]{quoteId, customerName, item, quantity, totalPrice, "Buttons"});
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void acceptQuote(int quoteId, int customerId, int itemId, int quantity, double totalPrice) {
        String insertOrderSql = "INSERT INTO orders (customer_id, item_id, quantity, total_price, status) VALUES (?, ?, ?, ?, 'paid')";
        String updateQuoteStatusSql = "UPDATE quotes SET status = 'accepted' WHERE QuoteDetailID = ?";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement orderStmt = conn.prepareStatement(insertOrderSql);
             PreparedStatement updateStmt = conn.prepareStatement(updateQuoteStatusSql)) {

            orderStmt.setInt(1, customerId);
            orderStmt.setInt(2, itemId);
            orderStmt.setInt(3, quantity);
            orderStmt.setDouble(4, totalPrice);
            orderStmt.executeUpdate();

            updateStmt.setInt(1, quoteId);
            updateStmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Quote accepted and order placed!");
            refreshTable();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void rejectQuote(int quoteId) {
        String updateQuoteStatusSql = "UPDATE quotes SET status = 'cancelled' WHERE QuoteDetailID = ?";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(updateQuoteStatusSql)) {

            stmt.setInt(1, quoteId);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Quote rejected!");
            refreshTable();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void refreshTable() {
        quotesTableModel.setRowCount(0); // Clear table
        loadPendingQuotes(); // Reload pending quotes
    }

    // --- Custom Renderer for Buttons ---
    class ButtonRenderer extends JPanel implements TableCellRenderer {
        private JButton acceptButton = new JButton("Accept");
        private JButton rejectButton = new JButton("Reject");

        public ButtonRenderer() {
            setLayout(new FlowLayout());
            add(acceptButton);
            add(rejectButton);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }

    // --- Custom Editor for Buttons ---
    class ButtonEditor extends AbstractCellEditor implements TableCellEditor {
        private JPanel panel = new JPanel();
        private JButton acceptButton = new JButton("Paid");
        private JButton rejectButton = new JButton("Cancelled");
        private int selectedRow;

        public ButtonEditor() {
            panel.setLayout(new FlowLayout());
            panel.add(acceptButton);
            panel.add(rejectButton);

            acceptButton.addActionListener(e -> {
                int quoteId = (int) quotesTable.getValueAt(selectedRow, 0);
                //int customerId = (int) quotesTable.getValueAt(selectedRow, 1);
//                int itemId = (int) quotesTable.getValueAt(selectedRow, 2);
                int quantity = Integer.parseInt(quotesTable.getValueAt(selectedRow, 3).toString());
                double totalPrice = (double) quotesTable.getValueAt(selectedRow, 4);
                acceptQuote(quoteId, customerId, itemID, quantity, totalPrice);
                stopCellEditing();
            });

            rejectButton.addActionListener(e -> {
                int quoteId = (int) quotesTable.getValueAt(selectedRow, 0);
                rejectQuote(quoteId);
                stopCellEditing();
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            this.selectedRow = row;
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "Buttons";
        }
    }
}
