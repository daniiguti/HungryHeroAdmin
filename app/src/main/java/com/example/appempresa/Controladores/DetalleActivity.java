package com.example.appempresa.Controladores;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appempresa.Adapter.RecyclerAdapterLineasPedidos;
import com.example.appempresa.Modelos.LineasPedido;
import com.example.appempresa.Modelos.Pedido;
import com.example.appempresa.R;
import com.example.appempresa.Servicios.Fcm;
import com.example.appempresa.Servicios.FirestorePeticiones;
import com.example.appempresa.Servicios.OnDetalleActivityListener;

import java.text.DecimalFormat;
import java.util.ArrayList;

public class DetalleActivity extends AppCompatActivity {

    private Button btAceptar;
    private Button btCancelar;

    private RecyclerView recyclerView;
    private RecyclerAdapterLineasPedidos recyclerAdapterLineasPedidos;
    private ArrayList<LineasPedido> lineasPedidos;

    private TextView tvId;
    private TextView tvFecha;
    private TextView tvEstado;
    private TextView tvUsuario;
    private TextView tvTotal;
    private TextView tvNumero;

    private String idPedido;

    private FirestorePeticiones firestorePeticiones;

    private double total;

    private Pedido aux;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle);

        Intent i = getIntent();
        idPedido = i.getStringExtra("idPedido");
        total = 0;

        tvId = (TextView) findViewById(R.id.tvId);
        tvFecha = (TextView) findViewById(R.id.tvFecha);
        tvEstado = (TextView) findViewById(R.id.tvEstado);
        tvUsuario = (TextView) findViewById(R.id.tvUsuario);
        tvTotal = (TextView) findViewById(R.id.tvTotal);
        tvNumero = (TextView) findViewById(R.id.tvTelefono);
        btCancelar = (Button) findViewById(R.id.btCancelarPedido);
        btAceptar = (Button) findViewById(R.id.btConfirmarPedido);

        lineasPedidos = new ArrayList<>();
        recyclerAdapterLineasPedidos = new RecyclerAdapterLineasPedidos(lineasPedidos, getApplicationContext());
        recyclerView = (RecyclerView) findViewById(R.id.recyclerViewDetalle);
        recyclerView.setAdapter(recyclerAdapterLineasPedidos);
        LinearLayoutManager layoutManager2 = new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager2);

        btAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = createAlertDialogInsertar();
                alertDialog.show();
            }
        });

        btCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog alertDialog = createAlertDialogEliminar();
                alertDialog.show();
            }
        });

        //listeners firestore peticiones
        firestorePeticiones = new FirestorePeticiones();
        firestorePeticiones.setListenerDetalleActivity(new OnDetalleActivityListener() {
            @Override
            public void onInfoReceive(Pedido pedido, ArrayList<LineasPedido> lineas) {
                aux = pedido;
                //Inserccion de datos
                tvId.setText(pedido.getIdPedido());
                tvFecha.setText(pedido.getHora());
                tvUsuario.setText(pedido.getUsuario());
                switch(pedido.getEstado()){
                    case "En espera":
                        tvEstado.setText("• " + pedido.getEstado());
                        tvEstado.setTextColor(Color.rgb(229, 190, 1));
                        break;
                    //si el pedido esta para recoger ya no se puede cancelar
                    case "Para recoger":
                        tvEstado.setText("√ " + pedido.getEstado());
                        tvEstado.setTextColor(Color.GREEN);
                        btCancelar.setEnabled(false);
                        break;
                    case "Finalizado":
                        tvEstado.setText("× " + pedido.getEstado());
                        tvEstado.setTextColor(Color.RED);
                        btCancelar.setEnabled(false);
                        btAceptar.setEnabled(false);
                }

                for(LineasPedido l: lineas){
                    recyclerAdapterLineasPedidos.insertar(l);
                }

                //una vez obtenidas las lineas consultamos cuanto vale cada producto de estas
                firestorePeticiones.calcularTotal(lineas);
                //tambien, una vez obtenido el pedido, obtenemos también el telefono del usuario
                firestorePeticiones.obtenerTelefono(pedido.getUsuario());
            }

            @Override
            public void onTotalCalculated(double totalAux){
                //introducimos el total
                total = total + totalAux;
                double redondeado = Math.round(total * 100) / 100.0;
                tvTotal.setText(String.valueOf(redondeado) + "€");
            }

            @Override
            public void onEliminatePedido(){
                showToast("PEDIDO CANCELADO");
                finish();
            }

            @Override
            public void onConfirmarPedido(){
                showToast("PEDIDO CONFIRMADO");
                finish();
            }

            @Override
            public void onNumeroRecibido(String numero){
                tvNumero.setText(numero);
            }
        });

        firestorePeticiones.cargarInfo(idPedido);
        loadPreferences();
    }

    //AlertDialog para eliminar
    public AlertDialog createAlertDialogEliminar(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        String msg = "¿Seguro que desea cancelar este pedido?";
        builder.setMessage(msg);
        builder.setTitle("Cancelar pedido");

        builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                firestorePeticiones.eliminarPedido(idPedido);
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                showToast("PEDIDO NO CANCELADO");
            }
        });

        return builder.create();
    }

    //AlertDialog para aceptar
    public AlertDialog createAlertDialogInsertar(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        String msg = "¿Seguro que desea confirmar este pedido?";
        builder.setMessage(msg);
        builder.setTitle("Confirmar pedido");

        builder.setPositiveButton("SI", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                firestorePeticiones.confirmarPedido(aux.getEstado(), idPedido);
                if(aux.getEstado().equals("En espera")){
                    Fcm fcm = new Fcm();
                    fcm.enviarNotificacionPedido(tvUsuario.getText().toString(), getApplicationContext());
                }
                showToast("PEDIDO CONFIRMADO");
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                showToast("PEDIDO NO REALIZADO");
            }
        });

        return builder.create();
    }

    //Método para mostrar los Toasts
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