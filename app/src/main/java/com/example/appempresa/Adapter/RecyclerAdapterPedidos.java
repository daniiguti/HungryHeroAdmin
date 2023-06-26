package com.example.appempresa.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appempresa.Modelos.Pedido;
import com.example.appempresa.R;

import java.util.ArrayList;

public class RecyclerAdapterPedidos extends RecyclerView.Adapter<RecyclerAdapterPedidos.RecyclerHolder>{
    //Atributos de nuestra clase
    private ArrayList<Pedido> listPedidos;
    //arraylistAuxiliar para buscar por el id
    private ArrayList<Pedido> listPedidosNoSeVe;
    private Context context;
    private View.OnClickListener listener;

    //Constructor
    public RecyclerAdapterPedidos(ArrayList<Pedido> listPedidos, Context context){
        this.listPedidos = listPedidos;
        this.listPedidosNoSeVe = listPedidos;
        this.context = context;
    }

    //Setter del listener
    public void setListener(View.OnClickListener listener) {
        this.listener = listener;
    }

    //Esto "infla" cada celda del recyclerView con nuestro diseño
    @NonNull
    @Override
    public RecyclerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pedido,parent, false);
        RecyclerHolder recyclerHolder = new RecyclerHolder(view);
        view.setOnClickListener(listener);
        return recyclerHolder;
    }

    //Esto junta cada Libro del arrayList con el diseño de cada celda
    @Override
    public void onBindViewHolder(@NonNull RecyclerHolder holder, int position) {
        Pedido pedido = this.listPedidos.get(position);

        holder.tvFecha.setText(pedido.getHora());
        holder.tvUsuario.setText(pedido.getUsuario());
        switch(pedido.getEstado()){
            case "En espera":
                holder.tvEstado.setText("• " + pedido.getEstado());
                holder.tvEstado.setTextColor(Color.rgb(229, 190, 1));
                break;
            case "Para recoger":
                holder.tvEstado.setText("√ " + pedido.getEstado());
                holder.tvEstado.setTextColor(Color.GREEN);
                break;
            case "Finalizado":
                holder.tvEstado.setText("× " + pedido.getEstado());
                holder.tvEstado.setTextColor(Color.RED);
                break;
        }
    }

    @Override
    public int getItemCount() {
        return listPedidos.size();
    }

    //Enlazamos los elementos del diseño en relacion a nuestra clase
    public class RecyclerHolder extends RecyclerView.ViewHolder {
        TextView tvFecha;
        TextView tvUsuario;
        TextView tvEstado;

        public RecyclerHolder(@NonNull View itemView) {
            super(itemView);
            tvFecha = (TextView) itemView.findViewById(R.id.tvFechaPedido);
            tvUsuario = (TextView) itemView.findViewById(R.id.tvUsuarioPedido);
            tvEstado = (TextView) itemView.findViewById(R.id.tvEstadoPedido);
        }
    }


    //Para insertar en nuestro arrayList
    public void insertar(Pedido pedido){
        this.listPedidos.add(pedido);
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
    public Pedido devolverPedido(int posicion){
        return this.listPedidos.get(posicion);
    }
    //Para devolver tod nuestro Arraylist
    public ArrayList<Pedido> devolverPedidos(){
        return this.listPedidos;
    }
}
