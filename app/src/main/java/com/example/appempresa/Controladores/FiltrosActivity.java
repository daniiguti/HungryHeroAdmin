package com.example.appempresa.Controladores;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.preference.PreferenceManager;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.Toast;

import com.example.appempresa.R;

import java.util.Calendar;
import java.util.Date;


public class FiltrosActivity extends AppCompatActivity {

    private CalendarView calendarInicio;
    private Date fechaInicio;
    private CalendarView calendarFin;
    private Date fechaFin;

    private Button btAceptar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_filtros);

        Intent i = getIntent();

        calendarInicio = (CalendarView) findViewById(R.id.calendarInicio);
        calendarFin = (CalendarView) findViewById(R.id.calendarFin);
        btAceptar = (Button) findViewById(R.id.btAceptarFiltros);

        calendarInicio.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int year, int month, int dayOfMonth) {
                // Se crea una instancia de la fecha seleccionada
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month); // El mes en Calendar es indexado en base 0
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                fechaInicio = calendar.getTime();
            }
        });

        calendarFin.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                // Se crea una instancia de la fecha seleccionada
                Calendar calendar = Calendar.getInstance();
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month); // El mes en Calendar es indexado en base 0
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                fechaFin = calendar.getTime();
            }
        });

        btAceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fechaFin != null && fechaInicio != null){
                    Intent returnIntent = new Intent();
                    returnIntent.putExtra("fechaInicio", fechaInicio);
                    returnIntent.putExtra("fechaFin", fechaFin);
                    setResult(Activity.RESULT_OK,returnIntent);
                    finish();
                }else{
                    showToast("Seleccione los rangos de fechas");
                }
            }
        });

        loadPreferences();
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