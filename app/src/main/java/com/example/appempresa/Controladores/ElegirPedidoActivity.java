package com.example.appempresa.Controladores;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.appempresa.R;

public class ElegirPedidoActivity extends AppCompatActivity {

    private Button btPedidosEnEspera;
    private Button btPedidosParaRecoger;
    private Button btPedidosFinalizados;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_elegir_pedido);

        btPedidosEnEspera = (Button) findViewById(R.id.btPedidosEnEspera);
        btPedidosParaRecoger = (Button) findViewById(R.id.btPedidosRecoger);
        btPedidosFinalizados = (Button) findViewById(R.id.btPedidosFinalizados);

        //si se pulsa un botón lo mandaremos al activity de los pedidos, con un extra, para saber
        //que consulta utilizar
        btPedidosEnEspera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), PedidosActivity.class);
                i.putExtra("consulta", "En espera");
                startActivity(i);
            }
        });
        btPedidosParaRecoger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), PedidosActivity.class);
                i.putExtra("consulta", "Para recoger");
                startActivity(i);
            }
        });
        btPedidosFinalizados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), PedidosActivity.class);
                i.putExtra("consulta", "Finalizado");
                startActivity(i);
            }
        });

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
}