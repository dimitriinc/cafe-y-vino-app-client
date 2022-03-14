package com.cafeyvinowinebar.Cafe_y_Vino.POJOs;

/**
 * Represents a gift shop item as it's stored in the FirestoreDB
 * A customer spends his bonus points to acquire the gift in the fidelity program
 * Model for the AdapterGiftshop
 */

public class Gift {

    String imagen;
    String precio;
    String nombre;
    boolean isPresent;

    public Gift() {}

    public String getImagen() {
        return imagen;
    }

    public String getPrecio() {
        return precio;
    }

    public String getNombre() {
        return nombre;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public void setPrecio(String precio) {
        this.precio = precio;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public boolean isPresent() {
        return isPresent;
    }

    public void setPresent(boolean present) {
        isPresent = present;
    }
}
