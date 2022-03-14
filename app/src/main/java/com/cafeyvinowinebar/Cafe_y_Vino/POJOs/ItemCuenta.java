package com.cafeyvinowinebar.Cafe_y_Vino.POJOs;

/**
 * Represent an item the bill consists of
 * Model for the AdapterCanasta
 */

public class ItemCuenta {
    String name;
    long price, count, total;

    public ItemCuenta(String name, long price, long count, long total) {
        this.name = name;
        this.price = price;
        this.count = count;
        this.total = total;
    }

    public ItemCuenta(long price, long count) {
        this.price = price;
        this.count = count;
    }

    public ItemCuenta() {
    }

    public String getName() {
        return name;
    }

    public long getPrice() {
        return price;
    }

    public long getCount() {
        return count;
    }

    public long getTotal() {
        return total;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public void setTotal(long total) {
        this.total = total;
    }
}
