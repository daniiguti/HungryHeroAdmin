package com.example.appempresa.Modelos;

public class Familia {
    private String id;
    private String nombre;
    private String info;
    private String imgUrl;

    public Familia(String id, String nombre, String info, String imgUrl) {
        this.id = id;
        this.nombre = nombre;
        this.info = info;
        this.imgUrl = imgUrl;
    }

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
    public String getInfo() {
        return info;
    }
    public void setInfo(String info) {
        this.info = info;
    }
    public String getImgUrl() {
        return imgUrl;
    }
    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }
}
