package org.example;

public class Order {
    private int order_id;
    private int customer_id;
    private int item_id;
    private int quantity;
    private String status;

    public Order(int order_id, int customer_id, int item_id, int quantity, String status){
        this.order_id = order_id;
        this.customer_id = customer_id;
        this.item_id = item_id;
        this.quantity = quantity;
        this.status = status;
    }
}
