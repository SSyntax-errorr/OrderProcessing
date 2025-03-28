package org.example;

public class Item {
    private int item_id;
    private String name;
    private int cost;
    private int stock;

    public Item(int id, String name, int cost, int stock) {
        this.item_id = id;
        this.name = name;
        this.cost = cost;
        this.stock = stock;
    }

    public int getId() {
        return item_id;
    }

    public String getName() {
        return name;
    }

    public int getCost(){
        return cost;
    }

    public int getStock() {
        return stock;
    }

    @Override
    public String toString() {
        return name;  // This makes only the name visible in JComboBox
    }
}
