package com.cafeyvinowinebar.Cafe_y_Vino.POJOs;

/**
 * Represents a category of the menu
 * Model for the AdapterMainMenu
 */

public class MenuCategory {

    private String name;
    private String catPath;
    private String image;

    public MenuCategory() {}

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCatPath() {
        return catPath;
    }

    public void setCatPath(String catPath) {
        this.catPath = catPath;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
