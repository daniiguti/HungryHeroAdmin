package com.example.appempresa.Servicios;

import com.example.appempresa.Modelos.LineasPedido;
import com.example.appempresa.Modelos.Pedido;

import java.util.ArrayList;

public interface OnDetalleActivityListener {
    void onInfoReceive(Pedido pedido, ArrayList<LineasPedido> lineas);
    void onTotalCalculated(double total);
    void onEliminatePedido();
    void onConfirmarPedido();
    void onNumeroRecibido(String numero);
}
