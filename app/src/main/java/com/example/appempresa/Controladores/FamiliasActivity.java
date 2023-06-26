package com.example.appempresa.Controladores;

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

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appempresa.Adapter.RecyclerAdapterFamilias;
import com.example.appempresa.Modelos.Familia;
import com.example.appempresa.R;
import com.example.appempresa.Servicios.FirestorePeticiones;
import com.example.appempresa.Servicios.OnFamiliasActivityListener;

import java.util.ArrayList;

public class FamiliasActivity extends AppCompatActivity {

    private RecyclerView recyclerViewFamilias;
    private RecyclerAdapterFamilias recAdapterFamilias;
    private ArrayList<Familia> familias;

    private FirestorePeticiones firestorePeticiones;

    private ActionMode actionMode;

    private int posicionPulsada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_familias);

        familias = new ArrayList<>();
        recAdapterFamilias = new RecyclerAdapterFamilias(familias, getApplicationContext());
        LinearLayoutManager layoutManager1 = new LinearLayoutManager(getApplicationContext());
        recyclerViewFamilias = (RecyclerView) findViewById(R.id.recyclerViewFamilias);
        recyclerViewFamilias.setAdapter(recAdapterFamilias);
        recyclerViewFamilias.setLayoutManager(layoutManager1);

        recAdapterFamilias.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int posicionPulsada = recyclerViewFamilias.getChildAdapterPosition(v);
                Familia familiaSeleccionada = recAdapterFamilias.devolverFamilia(posicionPulsada);
                Intent i = new Intent(getApplicationContext(), ProductosActivity.class);
                i.putExtra("familia", familiaSeleccionada.getId());
                i.putExtra("nombreFamilia", familiaSeleccionada.getNombre());
                startActivity(i);
            }
        });
        recAdapterFamilias.setLongListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                posicionPulsada = recyclerViewFamilias.getChildAdapterPosition(v);
                //Hay que pasarle la interfaz implementada mas abajo
                actionMode = startActionMode(actionModeCallback);
                v.setSelected(true);
                return true;
            }
        });

        firestorePeticiones = new FirestorePeticiones();
        firestorePeticiones.setListenerFamiliasActivity(new OnFamiliasActivityListener() {
            @Override
            public void onFamiliasReceive(ArrayList<Familia> familias) {
                for(Familia f: familias){
                    recAdapterFamilias.insertar(f);
                }
            }
        });

        firestorePeticiones.cargarFamilias();
        loadPreferences();
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

    private ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater inflater = mode.getMenuInflater();
            inflater.inflate(R.menu.menu_accion_eliminar, menu);
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
            actionMode = null;
        }
    };

    //AlertDialog para eliminar
    public AlertDialog createAlertDialogEliminar(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        String msg = "¿Seguro que desea eliminar esta familia y sus productos asociados?";
        builder.setMessage(msg);
        builder.setTitle("Eliminar Familia");

        builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Familia fElim = recAdapterFamilias.devolverFamilia(posicionPulsada);
                firestorePeticiones.eliminarFamiliaYProductos(fElim.getId());
                recAdapterFamilias.eliminar(posicionPulsada);
                showToast("FAMILIA Y PRODUCTOS ELIMINADOS");
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                showToast("FAMILIA NO ELIMINADA");
            }
        });

        return builder.create();
    }

    //Método para mostrar los Toasts
    public void showToast(String texto) {
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
    }
}