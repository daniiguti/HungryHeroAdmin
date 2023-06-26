package com.example.appempresa.Servicios;

import com.example.appempresa.Modelos.Producto;

import java.util.ArrayList;

public interface OnProductosActivity {
    void onProductosReceive(ArrayList<Producto> productos);
    void onProductoInsertado(Producto producto);
    void onProductoEliminado();
}
