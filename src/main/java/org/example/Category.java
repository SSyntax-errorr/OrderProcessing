package org.example;

public class Category {


    private int categoryID;
    private String categoryName;

    public Category(int categoryID, String categoryName){
        this.categoryID = categoryID;
        this.categoryName = categoryName;
    }

    public int getCategoryID(){
        return categoryID;
    }

    @Override
    public String toString(){
        return categoryName;
    }

}
