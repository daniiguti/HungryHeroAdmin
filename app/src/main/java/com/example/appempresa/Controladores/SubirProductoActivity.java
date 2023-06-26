package com.example.appempresa.Controladores;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.preference.PreferenceManager;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appempresa.R;
import com.example.appempresa.Servicios.FirestorePeticiones;

import com.example.appempresa.Servicios.OnSubirProductoActivityListener;

import java.io.IOException;

public class SubirProductoActivity extends AppCompatActivity {
    private static final int REQUEST_PERMISSION_GALERIA = 100;
    private static final int CODE_PICTURE = 1;

    private EditText nombre;
    private EditText precio;
    private ImageView imgProducto;
    private Button btAbrirGaleria;
    private Button btAceptar;
    private Button btCancelar;
    private TextView txCantidadLetras;

    private Bitmap bitmap;
    private Uri imagen;

    private int posPulsada;

    private String idFamilia;

    private FirestorePeticiones firestorePeticiones;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subir_producto);

        Intent i = getIntent();
        idFamilia = i.getStringExtra("familia");

        nombre = (EditText) findViewById(R.id.txtNombre);
        precio = (EditText) findViewById(R.id.txtPrecio);
        imgProducto = (ImageView) findViewById(R.id.imagenProducto);
        btAbrirGaleria = (Button) findViewById(R.id.btImagen);
        btAceptar = (Button) findViewById(R.id.btAceptar);
        btCancelar = (Button) findViewById(R.id.btCancelar);
        txCantidadLetras = (TextView) findViewById(R.id.tvCantidadLetras);

        //firestore peticiones
        firestorePeticiones = new FirestorePeticiones();

        nombre.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                txCantidadLetras.setText(nombre.getText().toString().length()+"/13");
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 13) {
                    nombre.setText(s.subSequence(0, 13)); // Limita el texto a la cantidad máxima de caracteres
                    nombre.setSelection(13); // Coloca el cursor al final del texto
                }
            }
        });

        //Boton para abrir la galeria y seleccionar la imagen
        btAbrirGaleria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkPermissionGaleria();
            }
        });

        //Boton para cancelar
        btCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //Boton para aceptar
        btAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nom = nombre.getText().toString();
                double precioDou = 0;
                String precioStr = precio.getText().toString();
                try {
                    if(precioStr != null && precioStr.length() > 0 && precioStr.isEmpty() == false){
                        precioDou = Double.valueOf(precioStr);
                    }
                }catch(NumberFormatException nf){
                    showToast("No introduzca letras en un precio!!");
                }
                catch(RuntimeException re){
                    showToast("No introduzca letras en un precio!!");
                }

                if (nom != null && nom.length() > 0 && precioDou > 0) {
                    firestorePeticiones.subirProducto(idFamilia, nom, precioDou);
                } else {
                    showToast("No ponga campos incorrectos!!");
                }
            }
        });

        //listener peticiones
        firestorePeticiones.setListenerSubirProductoActivity(new OnSubirProductoActivityListener() {
            @Override
            public void onProductoSubido(String idProducto){
                if(imagen != null && bitmap != null ){
                    firestorePeticiones.subirImagenProducto(idProducto, imagen);
                }else{
                    firestorePeticiones.subirImagenProducto(idProducto, null);
                }
            }
            @Override
            public void onImageSubida(String idProducto){
                Intent returnIntent = new Intent();
                returnIntent.putExtra("id", idProducto);
                setResult(Activity.RESULT_OK,returnIntent);
                finish();
                showToast("Registro insertado correctamente");
            }
        });

        loadPreferences();
    }

    //GALERIA
    //Para pedir los permisos para abrir la galeria
    private void checkPermissionGaleria() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                abrirGaleria();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_PERMISSION_GALERIA);
            }
        } else {
            abrirGaleria();
        }
    }
    //metodo para gestionar cuando se han pedido los permisos
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case REQUEST_PERMISSION_GALERIA:
                if (permissions.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    abrirGaleria();
                }else{
                    showToast("Has denegado los permisos para que la app acceda a la galería");
                }
                break;
        }

    }
    //metodo para abrir la galeria
    public void abrirGaleria(){
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");

        if(intent.resolveActivity(getPackageManager()) != null){
            startActivityForResult(intent, CODE_PICTURE);
        }
    }
    //metodo para cuando se venga de la galeria
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CODE_PICTURE:
                if (data != null && resultCode == RESULT_OK) {
                    imagen = data.getData();
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imagen);
                        imgProducto.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }
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
