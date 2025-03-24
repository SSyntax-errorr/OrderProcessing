/ OrderDAO.java
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderDAO {
    public void saveOrder(Order order) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/order_processing", "root", "password");
            statement = connection.prepareStatement("INSERT INTO orders (customer_id, item_id, quantity, status) VALUES (?, ?, ?, ?)");
            statement.setInt(1, order.getCustomerId());
            statement.setInt(2, order.getItemId());
            statement.setInt(3, order.getQuantity());
            statement.setString(4, order.getStatus());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                if (connection != null ) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void updateOrder(Order order) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/order_processing", "root", "password");
            statement = connection.prepareStatement("UPDATE orders SET status = ? WHERE order_id = ?");
            statement.setString(1, order.getStatus());
            statement.setInt(2, order.getOrderId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

// PaymentDAO.java
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class PaymentDAO {
    public void savePayment(Payment payment) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/order_processing", "root", "password");
            statement = connection.prepareStatement("INSERT INTO payments (order_id, amount, status) VALUES (?, ?, ?)");
            statement.setInt(1, payment.getOrderId());
            statement.setDouble(2, payment.getAmount());
            statement.setString(3, payment.getStatus());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public void updatePayment(Payment payment) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/order_processing", "root", "password");
            statement = connection.prepareStatement("UPDATE payments SET status = ? WHERE payment_id = ?");
            statement.setString(1, payment.getStatus());
            statement.setInt(2, payment.getPaymentId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}

// StockDAO.java
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class StockDAO {
    public void updateStock(Stock stock) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/order_processing", "root", "password");
            statement = connection.prepareStatement("UPDATE stock SET quantity = ? WHERE item_id = ?");
            statement.setInt(1, stock.getQuantity());
            statement.setInt(2, stock.getItemId());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (statement != null) {
                    statement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
``` ```java
// Order.java
public class Order {
    private int orderId;
    private int customerId;
    private int itemId;
    private int quantity;
    private String status;

    // Getters and Setters
    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

// Payment.java
public class Payment {
    private int paymentId;
    private int orderId;
    private double amount;
    private String status;

    // Getters and Setters
    public int getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(int paymentId) {
        this.paymentId = paymentId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}

// Stock.java
public class Stock {
    private int itemId;
    private int quantity;

    // Getters and Setters
    public int getItemId() {
        return itemId;
    }

    public void setItemId(int itemId) {
        this.itemId = itemId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
``` ```java
// User.java
public class User {
    private int userId;
    private String username;
    private String password;
    private String role; // e.g., Salesperson, Inventory Officer, IS Manager

    // Getters and Setters
    public int getUser Id() {
        return userId;
    }

    public void setUser Id(int userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }
}