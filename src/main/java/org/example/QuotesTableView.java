package org.example;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableCellEditor;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.sql.*;

public class QuotesTableView extends JFrame {
    private String[] columns = {"Quote ID", "Customer Name", "Item", "Quantity", "Total Price", "Action"};
    private DefaultTableModel quotesTableModel;
    private JTable quotesTable;
    private int customerId;
    private int itemID;

    public QuotesTableView() {
        setTitle("Quotes Management");
        setSize(1000, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        // Header
        JLabel titleLabel = new JLabel("Quotes Management", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setOpaque(true);
        titleLabel.setBackground(new Color(64, 128, 128));
        titleLabel.setPreferredSize(new Dimension(getWidth(), 50));
        add(titleLabel, BorderLayout.NORTH);

        // Table
        quotesTableModel = new DefaultTableModel(columns, 0);
        quotesTable = new JTable(quotesTableModel);
        quotesTable.setRowHeight(40);
        quotesTable.setFont(new Font("Arial", Font.PLAIN, 14));
        quotesTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        quotesTable.getTableHeader().setBackground(new Color(64, 128, 128));
        quotesTable.getTableHeader().setForeground(Color.WHITE);
        quotesTable.setGridColor(new Color(200, 200, 200));
        quotesTable.getColumn("Action").setCellRenderer(new ButtonRenderer());
        quotesTable.getColumn("Action").setCellEditor(new ButtonEditor());
        customerId = -1;
        itemID = -1;
        loadPendingQuotes();

        JScrollPane scrollPane = new JScrollPane(quotesTable);
        scrollPane.getViewport().setBackground(Color.WHITE);
        add(scrollPane, BorderLayout.CENTER);

        // Centering window
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void loadPendingQuotes() {
        String sql = "SELECT q.*, c.name, i.name AS itemName " +
                "FROM quotes q " +
                "JOIN customers c ON q.customerID = c.customer_id " +
                "JOIN items i ON q.itemID = i.item_id " +
                "WHERE q.status = 'pending'";

        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                int quoteId = rs.getInt("QuoteDetailID");
                customerId = rs.getInt("customerID");
                String customerName = rs.getString("name");
                String item = "(" + rs.getInt("itemID") + ") " + rs.getString("itemName");
                itemID = rs.getInt("itemID");
                int quantity = rs.getInt("quantity");
                double totalPrice = rs.getDouble("unitPrice");

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

    // New rejectQuote method that takes a reject reason.
    private void rejectQuote(int quoteId, String rejectReason) {
        String updateQuoteStatusSql = "UPDATE quotes SET status = 'cancelled', reject_reason = ? WHERE QuoteDetailID = ?";
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(updateQuoteStatusSql)) {

            stmt.setString(1, rejectReason);
            stmt.setInt(2, quoteId);
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "Quote rejected!");
            refreshTable();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

     // Helper method: query DB to get payment method for a given quote.
     private String getPaymentMethodForQuote(int quoteId) {
        String sql = "SELECT payment_method FROM quotes WHERE QuoteDetailID = ?";
        try (Connection conn = DatabaseConnector.connect();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, quoteId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("payment_method");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }

    // Helper method: Show a dialog for entering a reject reason.
    private String getRejectReasonFromUser(String paymentMethod) {
        JPanel panel = new JPanel(new GridLayout(2, 2, 5, 5));
        panel.add(new JLabel("Quick Reason:"));
        String[] options;
        if ("credit card".equalsIgnoreCase(paymentMethod)) {
            options = new String[]{"Card Declined", "Other"};
        } else if ("cheque".equalsIgnoreCase(paymentMethod)) {
            options = new String[]{"Cheque Returned", "Other"};
        } else {
            options = new String[]{"Other"};
        }
        JComboBox<String> reasonCombo = new JComboBox<>(options);
        panel.add(reasonCombo);
        panel.add(new JLabel("Other Reason:"));
        JTextField otherField = new JTextField();
        panel.add(otherField);

        int result = JOptionPane.showConfirmDialog(null, panel, "Enter Reject Reason", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            String selected = (String) reasonCombo.getSelectedItem();
            if ("Other".equals(selected)) {
                return otherField.getText().trim();
            } else {
                return selected;
            }
        }
        return null;
    }

    private void refreshTable() {
        quotesTableModel.setRowCount(0);
        loadPendingQuotes();
    }

    class ButtonRenderer extends JPanel implements TableCellRenderer {
        private JButton acceptButton = createStyledButton("Accept", new Color(34, 139, 34));
        private JButton rejectButton = createStyledButton("Reject", new Color(220, 20, 60));

        public ButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
            setBackground(Color.WHITE);
            add(acceptButton);
            add(rejectButton);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                                                       boolean hasFocus, int row, int column) {
            return this;
        }
    }

    class ButtonEditor extends AbstractCellEditor implements TableCellEditor {
        private JPanel panel = new JPanel();
        private JButton acceptButton = createStyledButton("Accept", new Color(34, 139, 34));
        private JButton rejectButton = createStyledButton("Reject", new Color(220, 20, 60));
        private int selectedRow;

        public ButtonEditor() {
            panel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
            panel.setBackground(Color.WHITE);
            panel.add(acceptButton);
            panel.add(rejectButton);

            acceptButton.addActionListener(e -> {
                int quoteId = (int) quotesTable.getValueAt(selectedRow, 0);
                int quantity = Integer.parseInt(quotesTable.getValueAt(selectedRow, 3).toString());
                double totalPrice = (double) quotesTable.getValueAt(selectedRow, 4);
                acceptQuote(quoteId, customerId, itemID, quantity, totalPrice);
                stopCellEditing();
            });

            rejectButton.addActionListener(e -> {
                int quoteId = (int) quotesTable.getValueAt(selectedRow, 0);
                String paymentMethod = getPaymentMethodForQuote(quoteId);
                String rejectReason = getRejectReasonFromUser(paymentMethod);
                if (rejectReason != null && !rejectReason.trim().isEmpty()) {
                    rejectQuote(quoteId, rejectReason);
                    stopCellEditing();
                }
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

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        button.setFocusPainted(false);
        return button;
    }
}
