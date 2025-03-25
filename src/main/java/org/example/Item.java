package org.example;

public class Item {
    private int item_id;
    private String name;

    public Item(int id, String name) {
        this.item_id = id;
        this.name = name;
    }

    public int getId() {
        return item_id;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return name;  // This makes only the name visible in JComboBox
    }
}
