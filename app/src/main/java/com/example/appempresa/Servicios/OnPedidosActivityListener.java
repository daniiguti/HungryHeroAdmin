package com.example.appempresa.Servicios;

import com.example.appempresa.Modelos.Pedido;

import java.util.ArrayList;

public interface OnPedidosActivityListener {
    //tiene una condicion booleana para saber si la consulta viene con filtros o no
    void onPedidosReceive(ArrayList<Pedido> pedidos, boolean filtros);
}
