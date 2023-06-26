package com.example.appempresa.Modelos;

public class Pedido {
    //Atributos
    private String hora;
    private String usuario;
    private String estado;
    private String idPedido;

    //Constructores
    public Pedido(String hora, String usuario, String estado, String idPedido) {
        this.hora = hora;
        this.usuario = usuario;
        this.estado = estado;
        this.idPedido = idPedido;
    }

    //Getters y Setters
    public String getHora() {
        return hora;
    }
    public void setHora(String hora) {
        this.hora = hora;
    }
    public String getUsuario() {
        return usuario;
    }
    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }
    public String getEstado() {
        return estado;
    }
    public void setEstado(String estado) {
        this.estado = estado;
    }
    public String getIdPedido() {
        return idPedido;
    }
    public void setIdPedido(String idPedido) {
        this.idPedido = idPedido;
    }
}
