package com.example.appempresa.Controladores;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.appempresa.Modelos.Producto;
import com.example.appempresa.R;
import com.example.appempresa.Adapter.RecyclerAdapterProductos;
import com.example.appempresa.Servicios.FirestorePeticiones;
import com.example.appempresa.Servicios.OnProductosActivity;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class ProductosActivity extends AppCompatActivity {
    public static final int CODE_PRODUCTO = 1;

    private FirestorePeticiones firestorePeticiones;

    private ArrayList<Producto> productos;
    private RecyclerView recyclerView;
    private RecyclerAdapterProductos recAdapter;

    private ActionMode actionMode;

    private int posicionPulsada;

    private String familia;

    private int numProductosSeleccionados = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productos);

        Intent i = getIntent();
        familia = i.getStringExtra("familia");
        String nombreFamilia = i.getStringExtra("nombreFamilia");
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(nombreFamilia);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        productos = new ArrayList<>();
        recAdapter = new RecyclerAdapterProductos(productos, getApplicationContext());

        recyclerView.setAdapter(recAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);

        posicionPulsada = 0;

        //Listener del recAdapterm para cuando dejemos pulsado se nos habra un menú de acción
        recAdapter.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                numProductosSeleccionados++;
                if(numProductosSeleccionados <= 1){
                    posicionPulsada = recyclerView.getChildAdapterPosition(view);

                    Producto productoSeleccionado = productos.get(posicionPulsada);

                    //Comprobamos si el producto estaba seleccionado, si estaba seleccionado lo eliminamos
                    //del arraylist y lo deseleccionamos visiblemente
                    if(productoSeleccionado.isSelected() == true){
                        productoSeleccionado.setSelected(false);
                        Producto pAEliminar = recAdapter.devolverProducto(posicionPulsada);
                        view.setSelected(false);
                    }
                    else{
                        productoSeleccionado.setSelected(true);
                        view.setSelected(true);
                    }

                    recAdapter.notifyDataSetChanged();
                    //Hay que pasarle la interfaz implementada mas abajo
                    actionMode = startActionMode(actionModeCallback);
                    view.setSelected(true);
                }

                return true;
            }
        });

        //listeners firestore peticiones
        firestorePeticiones = new FirestorePeticiones();
        firestorePeticiones.setListenerProductosActivity(new OnProductosActivity() {
            @Override
            public void onProductosReceive(ArrayList<Producto> productos) {
                for(Producto p: productos){
                    recAdapter.insertar(p);
                }
            }

            @Override
            public void onProductoInsertado(Producto producto){
                recAdapter.insertar(producto);
            }

            @Override
            public void onProductoEliminado(){
                recAdapter.eliminar(posicionPulsada);
                showToast("PRODUCTO ELIMINADO");
            }
        });

        firestorePeticiones.cargarProductos(familia);
        loadPreferences();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    //MENÚ SIMPLE
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_simple,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId){
            case R.id.btAdd:
                Intent i = new Intent(getApplicationContext(), SubirProductoActivity.class);
                i.putExtra("familia", familia);
                startActivityForResult(i, CODE_PRODUCTO);
                break;
        }
        return true;
    }

    //cuando venga de la otra actividad solo cargamos el último, puesto solo nos interesa mostrarle ese ultimo que añadio
    //esto se hace para evitar problemas de rendimiento, ya que no es lo mismo que cada vez que añada un producto
    //y vuelva aquí, se carguen de nuevo TODOS los productos, a que solo se cargue el nuevo que ha añadido
    //para esto ordenamos por un campo que genera automaticamente firebase(timestamp) que es por la fecha, y obtenemos solo uno
    //esto es mas eficiente que cargarlos todos de nuevo
    //cargamos ese a través de su id documento que pasaremos a través del startActivityForResult
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CODE_PRODUCTO:
                if (resultCode == RESULT_OK) {
                    String idProducto = data.getStringExtra("id");
                    firestorePeticiones.cargarProducto(idProducto);
                }
                break;
        }
    }

    //ELIMINAR UN PRODUCTO
    //Interfaz para el MENU DE ACCION
    private ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_accion, menu);
            return true;
        }
        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {
                case R.id.btEliminar:

                    AlertDialog alertDialog = createAlertDialogEliminar();
                    alertDialog.show();
                    mode.finish();
                    break;
            }
            return true;
        }
        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }
        @Override
        public void onDestroyActionMode(ActionMode mode) {
            recAdapter.deseleccionar();
            numProductosSeleccionados = 0;
            actionMode = null;
        }
    };
    public AlertDialog createAlertDialogEliminar(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        String msg = "¿Seguro que desea eliminar este producto?";
        builder.setMessage(msg);
        builder.setTitle("Eliminar producto");

        builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Producto productoEliminar = recAdapter.devolverProducto(posicionPulsada);
                String id = productoEliminar.getId();
                firestorePeticiones.eliminarProducto(id);
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                showToast("PRODUCTO NO ELIMINADO");
            }
        });

        return builder.create();
    }

    //Toast
    private void showToast(String mensaje) {
        Toast.makeText(getApplicationContext(), mensaje, Toast.LENGTH_SHORT).show();
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