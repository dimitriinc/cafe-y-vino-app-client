package com.cafeyvinowinebar.Cafe_y_Vino.POJOs;

import androidx.annotation.NonNull;

import java.io.Serializable;

/**
 * Represents a menu item, as it is stored in the menu collection in the database
 * Model for the AdapterCategory
 */

public class ItemMenu implements Serializable {

    private String nombre,icon, descripcion, precio, image, categoria;
    private Boolean isPresent;

    public ItemMenu() {}

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getPrecio() {
        return precio;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getCategoria() {
        return categoria;
    }

    public void setCategoria(String categoria) {
        this.categoria = categoria;
    }

    public Boolean isPresent() {
        return isPresent;
    }

    public void setPresent(Boolean present) {
        isPresent = present;
    }

    @NonNull
    @Override
    public String toString() {
        return "ItemMenu{" +
                "nombre='" + nombre + '\'' +
                ", icon='" + icon + '\'' +
                ", descripcion='" + descripcion + '\'' +
                ", precio='" + precio + '\'' +
                ", image='" + image + '\'' +
                ", categoria='" + categoria + '\'' +
                ", isPresent=" + isPresent +
                '}';
    }
}
