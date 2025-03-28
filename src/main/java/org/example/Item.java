package org.example;

public class Item {
    private int item_id;
    private String name;
    private int cost;

    public Item(int id, String name, int cost) {
        this.item_id = id;
        this.name = name;
        this.cost = cost;
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

    @Override
    public String toString() {
        return name;  // This makes only the name visible in JComboBox
    }
}
