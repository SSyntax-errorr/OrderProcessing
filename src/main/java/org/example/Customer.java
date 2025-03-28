package org.example;

public class Customer {
    private int customer_id;
    private String name;
    private String address;
    private String contact;

    public Customer(int id, String name, String address, String contact) {
        this.customer_id = id;
        this.name = name;
        this.address = address;
        this.contact = contact;
    }

    public int getId() {
        return customer_id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;  // This makes only the name visible in JComboBox
    }
}
