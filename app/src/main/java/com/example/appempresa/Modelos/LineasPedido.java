package com.example.appempresa.Modelos;

public class LineasPedido {
    //Atributos
    private String idPedido;
    private String idLinea;
    private String idProducto;
    private int cantidad;
    private String nota;

    //Constructores
    public LineasPedido(String idPedido, String idLinea, String idProducto, int cantidad, String nota) {
        this.idPedido = idPedido;
        this.idLinea = idLinea;
        this.idProducto = idProducto;
        this.cantidad = cantidad;
        this.nota = nota;
    }
    public LineasPedido(String idProducto, int cantidad, String nota) {
        this.idProducto = idProducto;
        this.cantidad = cantidad;
        this.nota = nota;
    }

    //Getters y setters
    public String getIdPedido() {
        return idPedido;
    }
    public void setIdPedido(String idPedido) {
        this.idPedido = idPedido;
    }
    public String getIdLinea() {
        return idLinea;
    }
    public void setIdLinea(String idLinea) {
        this.idLinea = idLinea;
    }
    public String getIdProducto() {
        return idProducto;
    }
    public void setIdProducto(String idProducto) {
        this.idProducto = idProducto;
    }
    public int getCantidad() {
        return cantidad;
    }
    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }
    public String getNota() {
        return nota;
    }
    public void setNota(String nota) {
        this.nota = nota;
    }
}
