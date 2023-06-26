package com.example.appempresa.Controladores;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appempresa.Adapter.RecyclerAdapterPedidos;
import com.example.appempresa.Modelos.Pedido;
import com.example.appempresa.R;
import com.example.appempresa.Servicios.FirestorePeticiones;
import com.example.appempresa.Servicios.OnPedidosActivityListener;

import java.sql.SQLOutput;
import java.util.ArrayList;
import java.util.Date;

public class PedidosActivity extends AppCompatActivity {
    public static final int CODE_FILTROS = 1;


    private RecyclerView recyclerViewPedidos;
    private RecyclerAdapterPedidos recyclerAdapterPedidos;
    private ArrayList<Pedido> pedidos;

    private String consulta;

    private EditText txtConsultaID;

    private ArrayList<Pedido> aux;

    private FirestorePeticiones firestorePeticiones;

    private boolean peticionNueva;

    private String usuario;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pedidos);

        pedidos = new ArrayList<>();
        recyclerAdapterPedidos = new RecyclerAdapterPedidos(pedidos, getApplicationContext());
        recyclerViewPedidos = (RecyclerView) findViewById(R.id.recyclerViewPedidos);
        recyclerViewPedidos.setAdapter(recyclerAdapterPedidos);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerViewPedidos.setLayoutManager(layoutManager);

        txtConsultaID = (EditText) findViewById(R.id.txtBuscar);
        peticionNueva = true;

        aux = new ArrayList<>();

        Intent i = getIntent();
        consulta = i.getStringExtra("consulta");

        recyclerAdapterPedidos.setListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int posicionPulsada = recyclerViewPedidos.getChildAdapterPosition(v);
                Pedido aux = recyclerAdapterPedidos.devolverPedido(posicionPulsada);
                Intent i = new Intent(getApplicationContext(), DetalleActivity.class);
                i.putExtra("idPedido", aux.getIdPedido());
                startActivity(i);
            }
        });

        txtConsultaID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                usuario = txtConsultaID.getText().toString();
                if(usuario.length() > 0){
                    consultar(usuario);
                }
                else{
                    insertarTodos();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        //listeners firestore peticiones
        firestorePeticiones = new FirestorePeticiones();
        firestorePeticiones.setListenerPedidosActivity(new OnPedidosActivityListener() {
            @Override
            public void onPedidosReceive(ArrayList<Pedido> pedidos, boolean filtros) {
                recyclerAdapterPedidos.clear();
                aux.clear();
                for(Pedido p: pedidos){
                    recyclerAdapterPedidos.insertar(p);
                    aux.add(p);
                }
            }
        });

        loadPreferences();
    }

    //se hace en el on resume la consulta puesto que en esta actividad puede entrar varias veces
    @Override
    protected void onResume() {
        super.onResume();
        txtConsultaID.setText("");
        if(peticionNueva == false){

        }else{
            //limpiamos el adapter
            recyclerAdapterPedidos.clear();

            //hacemos la consulta
            firestorePeticiones.cargarPedidos(consulta);
        }
    }

    //Método para mostrar los Toasts
    public void showToast(String texto) {
        Toast.makeText(this, texto, Toast.LENGTH_SHORT).show();
    }

    //cada vez que hagamos una consulta:
    public void consultar(String msg){
        //vaciamos el arraylist del adapter
        this.recyclerAdapterPedidos.clear();
        //recorremos el auxiliar
        for(Pedido aux: this.aux){
            //insertamos en el adapter
            if(aux.getUsuario().contains(msg) == true){
                this.recyclerAdapterPedidos.insertar(aux);
            }
        }
    }
    public void insertarTodos(){
        this.recyclerAdapterPedidos.clear();
        for(Pedido aux: this.aux){
            //insertamos en el adapter
            this.recyclerAdapterPedidos.insertar(aux);
        }
    }

    //MENÚ SIMPLE
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_simple2,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId){
            case R.id.btFiltros:
                Intent i = new Intent(getApplicationContext(), FiltrosActivity.class);
                startActivityForResult(i, CODE_FILTROS);
                break;
        }
        return true;
    }

    //Para obtener los filtros cuando venga de la actividad
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CODE_FILTROS:
                if (resultCode == RESULT_OK) {
                    Date fechaInicio = (Date) data.getSerializableExtra("fechaInicio");
                    Date fechaFin = (Date) data.getSerializableExtra("fechaFin");
                    //Hacer la consulta
                    firestorePeticiones.filtrosPedidos(fechaInicio, fechaFin, consulta);
                    peticionNueva = false;
                }
                break;
        }
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