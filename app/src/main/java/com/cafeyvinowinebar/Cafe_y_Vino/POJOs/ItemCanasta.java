package com.cafeyvinowinebar.Cafe_y_Vino.POJOs;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 * Represents a building block of orders as seen by customers
 * Stored in a SQLite database
 * Model for the AdapterCanasta
 */

@Entity(tableName = "canasta")
public class ItemCanasta {

    @PrimaryKey (autoGenerate = true)
    private int id;

    private String category, icon, name;
    private long price;

    public ItemCanasta(String name, String category, String icon, long price) {
        this.name = name;
        this.category = category;
        this.icon = icon;
        this.price = price;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }
}
