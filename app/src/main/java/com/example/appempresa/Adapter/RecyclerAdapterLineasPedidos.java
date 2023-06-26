package com.example.appempresa.Adapter;

import android.content.Context;
import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.CircularProgressDrawable;

import com.bumptech.glide.Glide;
import com.example.appempresa.Modelos.LineasPedido;
import com.example.appempresa.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Map;

public class RecyclerAdapterLineasPedidos extends RecyclerView.Adapter<RecyclerAdapterLineasPedidos.RecyclerHolder>{
    //Atributos de nuestra clase
    private ArrayList<LineasPedido> listPedidos;
    private Context context;
    private CircularProgressDrawable progressDrawable;
    private View.OnLongClickListener listener;
    private FirebaseFirestore mibase;

    //Constructor
    public RecyclerAdapterLineasPedidos(ArrayList<LineasPedido> listPedidos, Context context){
        this.listPedidos = listPedidos;
        this.context = context;
    }

    //Setter del listener
    public void setLongListener(View.OnLongClickListener listener) {
        this.listener = listener;
    }


    //Esto "infla" cada celda del recyclerView con nuestro diseño
    @NonNull
    @Override
    public RecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.linea_pedido,parent, false);
        RecyclerHolder recyclerHolder = new RecyclerHolder(view);
        view.setOnLongClickListener(listener);
        return recyclerHolder;
    }

    //Esto junta cada Producto del arrayList con el diseño de cada celda
    @Override
    public void onBindViewHolder(@NonNull RecyclerHolder holder, int position) {
        LineasPedido pedido = this.listPedidos.get(position);

        //Obtenemos el producto asociado a esa linea de pedido, con su id en una consulta a nuestra bdd
        mibase = FirebaseFirestore.getInstance();
        mibase.collection("productos")
                .document(pedido.getIdProducto())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            Map<String, Object> data = documentSnapshot.getData();
                            String nombre = data.get("name").toString();
                            String url = data.get("imgURL").toString();
                            String eliminado = data.get("eliminado").toString();

                            if(eliminado.equals("si")){
                                holder.tvNombre.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                                holder.tvCantidad.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                                holder.tvNota.setPaintFlags(Paint.STRIKE_THRU_TEXT_FLAG);
                            }
                            holder.tvNombre.setText(nombre);
                            holder.tvCantidad.setText("x" + pedido.getCantidad());
                            holder.tvNota.setText(pedido.getNota());

                            progressDrawable = new CircularProgressDrawable(context);
                            progressDrawable.setStrokeWidth(10f);
                            progressDrawable.setStyle(CircularProgressDrawable.LARGE);
                            progressDrawable.setCenterRadius(30f);
                            progressDrawable.start();

                            if(url.equals("no tiene")){                 //Comprobamos que tiene imagen, sino la tiene ponemos una
                                holder.imagenProducto.setImageResource(R.drawable.noimage);       //una por defecto nosotros (para cuando añada un producto)
                            }else {
                                Glide.with(context)
                                        .load(url)
                                        .placeholder(progressDrawable)
                                        .error(R.drawable.not_found)
                                        .into(holder.imagenProducto);
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        //showToast("No se pudo conectar a la bdd");
                    }
                });
    }

    @Override
    public int getItemCount() {
        return listPedidos.size();
    }

    //Enlazamos los elementos del diseño en relacion a nuestra clase
    public class RecyclerHolder extends RecyclerView.ViewHolder {
        TextView tvNombre;
        TextView tvCantidad;
        TextView tvNota;
        ImageView imagenProducto;

        public RecyclerHolder(@NonNull View itemView) {
            super(itemView);
            tvNombre = (TextView) itemView.findViewById(R.id.tvNombre);
            tvCantidad = (TextView) itemView.findViewById(R.id.tvCantidad);
            tvNota = (TextView) itemView.findViewById(R.id.tvNota);
            imagenProducto = (ImageView) itemView.findViewById(R.id.imgProducto);
        }
    }


    //Para insertar en nuestro arrayList
    public void insertar(LineasPedido linea){
        this.listPedidos.add(linea);
        this.notifyDataSetChanged();
    }
    //Para eliminar un elemento de nuestro arraylist
    public void eliminar(int pos){
        this.listPedidos.remove(pos);
        this.notifyDataSetChanged();
    }
    //Para borrar nuestro arrayList
    public void clear(){
        this.listPedidos.clear();
        this.notifyDataSetChanged();
    }
    //Para devolver de nuestro arrayList
    public LineasPedido devolverLinea(int posicion){
        return this.listPedidos.get(posicion);
    }
    //Para devolver tod nuestro Arraylist
    public ArrayList<LineasPedido> devolverLineas(){
        return this.listPedidos;
    }
}
