package com.example.appempresa.Controladores;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.appempresa.R;
import com.example.appempresa.Servicios.Fcm;

import com.google.firebase.messaging.FirebaseMessaging;

public class MainActivity extends AppCompatActivity {

    private Button btProductos;
    private Button btPedidos;
    private Button btSugerencias;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FirebaseMessaging.getInstance().setAutoInitEnabled(true);
        Fcm fcm = new Fcm();
        fcm.guardarToken();

        btProductos = (Button) findViewById(R.id.btProductos);
        btPedidos = (Button) findViewById(R.id.btPedidos);
        btSugerencias = (Button) findViewById(R.id.btSugerencias);

        btProductos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), FamiliasActivity.class);
                startActivity(i);
            }
        });
        btPedidos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ElegirPedidoActivity.class);
                startActivity(i);
            }
        });
        btSugerencias.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), SugerenciasActivity.class);
                startActivity(i);
            }
        });
    }

    //utilizamos el onResume para cargar las preferencias por si las cambia
    @Override
    protected void onResume() {
        super.onResume();
        loadPreferences();
    }

    //MENÚ SIMPLE
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_simple_ajustes,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        switch (itemId){
            case R.id.btAjustes:
                Intent i2 = new Intent(getApplicationContext(), PreferencesActivity.class);
                startActivity(i2);
                break;
        }
        return true;
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