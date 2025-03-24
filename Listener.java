// OrderListener.java
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class OrderListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        // Handle order-related events
        if (e.getActionCommand().equals("placeOrder")) {
            // Place an order
            Order order = new Order();
            order.setCustomerId(Integer.parseInt(e.getActionCommand()));
            order.setItemId(Integer.parseInt(e.getActionCommand()));
            order.setQuantity(Integer.parseInt(e.getActionCommand()));
            order.setStatus("pending");
            // Save the order to the database
            OrderDAO orderDAO = new OrderDAO();
            orderDAO.saveOrder(order);
        } else if (e.getActionCommand().equals("cancelOrder")) {
            // Cancel an order
            Order order = new Order();
            order.setOrderId(Integer.parseInt(e.getActionCommand()));
            order.setStatus("cancelled");
            // Update the order status in the database
            OrderDAO orderDAO = new OrderDAO();
            orderDAO.updateOrder(order);
        }
    }
}

// PaymentListener.java
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class PaymentListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        // Handle payment-related events
        if (e.getActionCommand().equals("makePayment")) {
            // Make a payment
            Payment payment = new Payment();
            payment.setOrderId(Integer.parseInt(e.getActionCommand()));
            payment.setAmount(Double.parseDouble(e.getActionCommand()));
            payment.setStatus("paid");
            // Save the payment to the database
            PaymentDAO paymentDAO = new PaymentDAO();
            paymentDAO.savePayment(payment);
        } else if (e.getActionCommand().equals("cancelPayment")) {
            // Cancel a payment
            Payment payment = new Payment();
            payment.setPaymentId(Integer.parseInt(e.getActionCommand()));
            payment.setStatus("cancelled");
            // Update the payment status in the database
            PaymentDAO paymentDAO = new PaymentDAO();
            paymentDAO.updatePayment(payment);
        }
    }
}

// StockListener.java
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class StockListener implements ActionListener {
    @Override
    public void actionPerformed(ActionEvent e) {
        // Handle stock-related events
        if (e.getActionCommand().equals("updateStock")) {
            // Update the stock level
            Stock stock = new Stock();
            stock.setItemId(Integer.parseInt(e.getActionCommand()));
            stock.setQuantity(Integer.parseInt(e.getActionCommand()));
            // Update the stock level in the database
            StockDAO stockDAO = new StockDAO();
            stockDAO.updateStock(stock);
        }
    }
}