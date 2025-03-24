CREATE TABLE customers (
    customer_id INT PRIMARY KEY,
    name VARCHAR(255),
    address VARCHAR(255),
    contact_details VARCHAR(255)
);

CREATE TABLE items (
    item_id INT PRIMARY KEY,
    name VARCHAR(255),
    cost_price DECIMAL(10, 2),
    stock_level INT
);

CREATE TABLE orders (
    order_id INT PRIMARY KEY,
    customer_id INT,
    item_id INT,
    quantity INT,
    status VARCHAR(255),
    FOREIGN KEY (customer_id) REFERENCES customers(customer_id),
    FOREIGN KEY (item_id) REFERENCES items(item_id)
);

CREATE TABLE payments (
    payment_id INT PRIMARY KEY,
    order_id INT,
    amount DECIMAL(10, 2),
    status VARCHAR(255),
    FOREIGN KEY (order_id) REFERENCES orders(order_id)
);

CREATE TABLE stock (
    item_id INT PRIMARY KEY,
    quantity INT,
    FOREIGN KEY (item_id) REFERENCES items(item_id)
);




-- Create a trigger to update the stock level when an order is placed
CREATE TRIGGER update_stock_level
AFTER INSERT ON orders
FOR EACH ROW
BEGIN
    UPDATE stock
    SET quantity = quantity - NEW.quantity
    WHERE item_id = NEW.item_id;
END;

-- Create a trigger to update the stock level when an order is cancelled
CREATE TRIGGER update_stock_level_on_cancel
AFTER UPDATE ON orders
FOR EACH ROW
BEGIN
    IF OLD.status = 'cancelled' THEN
        UPDATE stock
        SET quantity = quantity + OLD.quantity
        WHERE item_id = OLD.item_id;
    END IF;
END;

-- Create a trigger to update the order status when a payment is made
CREATE TRIGGER update_order_status_on_payment
AFTER INSERT ON payments
FOR EACH ROW
BEGIN
    UPDATE orders
    SET status = 'paid'
    WHERE order_id = NEW.order_id;
END;

-- Create a trigger to update the order status when a payment is cancelled
CREATE TRIGGER update_order_status_on_payment_cancel
AFTER UPDATE ON payments
FOR EACH ROW
BEGIN
    IF OLD.status = 'cancelled' THEN
        UPDATE orders
        SET status = 'pending'
        WHERE order_id = OLD.order_id;
    END IF;
END;