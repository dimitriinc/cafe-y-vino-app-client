package com.cafeyvinowinebar.Cafe_y_Vino.POJOs;

/**
 * Represents a building block of orders as seen by the administration
 * ItemCanasta becomes ItemPedido when stored to the FirestoreDB
 * Multiple ItemCanasta with the same name are merged in one ItemPedido with the appropriate 'count' value
 */

public class ItemPedido {

    private String name, category;
    private long price, count;

    public ItemPedido(String name, long count, long price, String category) {
        this.name = name;
        this.price = price;
        this.category = category;
        this.count = count;
    }

    public ItemPedido() {}

    public void setCount(long count) {
        this.count = count;
    }

    public long getCount() {
        return count;
    }

    public String getName() {
        return name;
    }

    public String getCategory() {
        return category;
    }

    public long getPrice() {
        return price;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setPrice(long price) {
        this.price = price;
    }

}
