package com.example.appempresa.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.example.appempresa.Modelos.Familia;
import com.example.appempresa.R;

import java.util.ArrayList;

public class RecyclerAdapterFamilias extends RecyclerView.Adapter<RecyclerAdapterFamilias.RecyclerHolder>{
    //Atributos de nuestra clase
    private ArrayList<Familia> listFamilias;
    private Context context;
    private View.OnClickListener listener;
    private View.OnLongClickListener longListener;
    private CircularProgressDrawable progressDrawable;

    //Constructor
    public RecyclerAdapterFamilias(ArrayList<Familia> listFamilias, Context context){
        this.listFamilias = listFamilias;
        this.context = context;
    }

    //Setter del listener
    public void setListener(View.OnClickListener listener) {
        this.listener = listener;
    }
    public void setLongListener(View.OnLongClickListener longListener) {
        this.longListener = longListener;
    }

    //Esto "infla" cada celda del recyclerView con nuestro diseño
    @NonNull
    @Override
    public RecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.familia,parent, false);
        RecyclerHolder recyclerHolder = new RecyclerHolder(view);
        view.setOnClickListener(listener);
        view.setOnLongClickListener(longListener);
        return recyclerHolder;
    }

    //Esto junta cada Libro del arrayList con el diseño de cada celda
    @Override
    public void onBindViewHolder(@NonNull RecyclerHolder holder, int position) {
        Familia familia = this.listFamilias.get(position);

        holder.tvNombre.setText(familia.getNombre());
        holder.tvInfo.setText(familia.getInfo());

        progressDrawable = new CircularProgressDrawable(context);
        progressDrawable.setStrokeWidth(10f);
        progressDrawable.setStyle(CircularProgressDrawable.LARGE);
        progressDrawable.setCenterRadius(30f);
        progressDrawable.start();

        //if(producto.getImgURL().equals("no tiene")){                 //Comprobamos que tiene imagen, sino la tiene ponemos una
        //holder.imagenProducto.setImageResource(R.drawable.producto);       //una por defecto nosotros (para cuando añada un producto)
        //}else{
        Glide.with(context)
                .load(familia.getImgUrl())
                .placeholder(progressDrawable)
                //.error(R.drawable.not_found)
                .into(holder.imgFamilia);
        //}
    }

    @Override
    public int getItemCount() {
        return listFamilias.size();
    }

    //Enlazamos los elementos del diseño en relacion a nuestra clase
    public class RecyclerHolder extends RecyclerView.ViewHolder {
        TextView tvNombre;
        TextView tvInfo;
        ImageView imgFamilia;

        public RecyclerHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = (TextView) itemView.findViewById(R.id.tvFamilia);
            tvInfo = (TextView) itemView.findViewById(R.id.tvInfoFamilia);
            imgFamilia = (ImageView) itemView.findViewById(R.id.imgFamilia);
        }
    }


    //Para insertar en nuestro arrayList
    public void insertar(Familia familia){
        this.listFamilias.add(familia);
        this.notifyDataSetChanged();
    }
    //Para eliminar un elemento de nuestro arraylist
    public void eliminar(int pos){
        this.listFamilias.remove(pos);
        this.notifyDataSetChanged();
    }
    //Para borrar nuestro arrayList
    public void clear(){
        this.listFamilias.clear();
        this.notifyDataSetChanged();
    }
    //Para devolver de nuestro arrayList
    public Familia devolverFamilia(int posicion){
        return this.listFamilias.get(posicion);
    }
    //Para devolver tod nuestro Arraylist
    public ArrayList<Familia> devolverFamilias(){
        return this.listFamilias;
    }
}
