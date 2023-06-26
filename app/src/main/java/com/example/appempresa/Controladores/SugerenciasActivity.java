package com.example.appempresa.Controladores;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.appempresa.Adapter.RecyclerAdapterProductos;
import com.example.appempresa.Modelos.Producto;
import com.example.appempresa.R;
import com.example.appempresa.Servicios.FirestorePeticiones;
import com.example.appempresa.Servicios.OnSugerenciasActivityListener;

import java.sql.SQLOutput;
import java.util.ArrayList;

public class SugerenciasActivity extends AppCompatActivity {

    private FirestorePeticiones firestorePeticiones;

    private ArrayList<Producto> productos;
    private RecyclerView recyclerView;
    private RecyclerAdapterProductos recAdapter;

    private ActionMode actionMode;

    private int posicionPulsada;
    private ArrayList<String> productosSeleccionados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sugerencias);

        recyclerView = (RecyclerView) findViewById(R.id.recViewSugerencias);

        productosSeleccionados = new ArrayList<>();
        productos = new ArrayList<>();
        recAdapter = new RecyclerAdapterProductos(productos, getApplicationContext());

        recyclerView.setAdapter(recAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        recAdapter.setSmallListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int posicionPulsada = recyclerView.getChildAdapterPosition(v);
                Producto productoSeleccionado = productos.get(posicionPulsada);
                //Comprobamos si el producto estaba seleccionado, si estaba seleccionado lo eliminamos
                //del arraylist y lo deseleccionamos visiblemente
                if(productoSeleccionado.isSelected() == true){
                    productoSeleccionado.setSelected(false);
                    Producto pAEliminar = recAdapter.devolverProducto(posicionPulsada);
                    productosSeleccionados.remove(pAEliminar.getId());
                    v.setSelected(false);
                }
                else{
                    productoSeleccionado.setSelected(true);
                    productosSeleccionados.add(productoSeleccionado.getId());
                    v.setSelected(true);
                }

                recAdapter.notifyDataSetChanged();
            }
        });

        firestorePeticiones = new FirestorePeticiones();
        firestorePeticiones.setListenerSugerenciasActivity(new OnSugerenciasActivityListener() {
            @Override
            public void onProductosCargados(ArrayList<Producto> productos) {
                for(Producto p: productos){
                    recAdapter.insertar(p);
                }
            }
        });

        firestorePeticiones.cargarProductos();
        loadPreferences();
    }

    //MENÚ SIMPLE
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_simple3,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId){
            case R.id.btConfirmar:
                if(productosSeleccionados.size() >= 2){
                    firestorePeticiones.actualizarSugerencias(this.productosSeleccionados);
                    recAdapter.deseleccionar();
                    showToast("SUGERENCIAS ACTUALIZADAS");
                    finish();

                }
                else{
                    showToast("Seleccione al menos tres productos");
                }
                break;
        }
        return true;
    }

    public void showToast(String texto) {
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
    }

    //Para cargar las preferencias
    public void loadPreferences(){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String colorFondo = sharedPreferences.getString("preferences_tema","Light");
        switch (colorFondo){
            case "Light":
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO);
                break;
            case "Night":
                getDelegate().setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES);
                break;
        }
    }
}