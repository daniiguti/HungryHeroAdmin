package com.example.appempresa.Modelos;

import java.util.Objects;

public class Producto {
    //Atributos
    private String id;
    private String nombre;
    private double precio;
    private String imgURL;
    private String tipo;
    //atrib auxiliar
    private boolean selected;

    //Constructor
    public Producto(String id, String nombre, double precio, String imgURL, String tipo) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.imgURL = imgURL;
        this.tipo = tipo;
        this.selected = false;
    }

    public Producto(String id, String nombre, double precio, String tipo) {
        this.id = id;
        this.nombre = nombre;
        this.precio = precio;
        this.tipo = tipo;
    }

    //Getters y Setters
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public double getPrecio() {
        return precio;
    }
    public void setPrecio(double precio) {
        this.precio = precio;
    }
    public String getImgURL() {
        return imgURL;
    }
    public void setImgURL(String imgURL) {
        this.imgURL = imgURL;
    }
    public String getTipo() {
        return tipo;
    }
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    public boolean isSelected() {
        return selected;
    }
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    //Hay que sobreescribir el metodo equals para que el remove() funcione correctamente

    @Override
    public boolean equals(Object o) {
        boolean sonIguales = false;
        if(o instanceof Producto){
            Producto aux = (Producto) o;
            if(aux.getId().equals(this.id)){
                sonIguales = true;
            }
        }
        return sonIguales;
    }

}
