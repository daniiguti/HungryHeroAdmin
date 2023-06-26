package com.example.appempresa.Servicios;

import android.net.Uri;

import androidx.annotation.NonNull;

import com.example.appempresa.Modelos.Familia;
import com.example.appempresa.Modelos.LineasPedido;
import com.example.appempresa.Modelos.Pedido;
import com.example.appempresa.Modelos.Producto;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FirestorePeticiones {

    //listeners para avisar a nuestros controladores
    private OnDetalleActivityListener listenerDetalleActivity;
    private OnPedidosActivityListener listenerPedidosActivity;
    private OnSubirProductoActivityListener listenerSubirProductoActivity;
    private OnFamiliasActivityListener listenerFamiliasActivity;
    private OnProductosActivity listenerProductosActivity;
    private OnSugerenciasActivityListener listenerSugerenciasActivity;

    //firebase
    private FirebaseFirestore mibase;

    //constructor
    public FirestorePeticiones(){

    }

    //getters y setters
    public OnDetalleActivityListener getListenerDetalleActivity() {
        return listenerDetalleActivity;
    }
    public void setListenerDetalleActivity(OnDetalleActivityListener listenerDetalleActivity) {
        this.listenerDetalleActivity = listenerDetalleActivity;
    }

    public OnPedidosActivityListener getListenerPedidosActivity() {
        return listenerPedidosActivity;
    }
    public void setListenerPedidosActivity(OnPedidosActivityListener listenerPedidosActivity) {
        this.listenerPedidosActivity = listenerPedidosActivity;
    }

    public OnSubirProductoActivityListener getListenerSubirProductoActivity() {
        return listenerSubirProductoActivity;
    }
    public void setListenerSubirProductoActivity(OnSubirProductoActivityListener listenerSubirProductoActivity) {
        this.listenerSubirProductoActivity = listenerSubirProductoActivity;
    }

    public OnFamiliasActivityListener getListenerFamiliasActivity() {
        return listenerFamiliasActivity;
    }
    public void setListenerFamiliasActivity(OnFamiliasActivityListener listenerFamiliasActivity) {
        this.listenerFamiliasActivity = listenerFamiliasActivity;
    }

    public OnProductosActivity getListenerProductosActivity() {
        return listenerProductosActivity;
    }
    public void setListenerProductosActivity(OnProductosActivity listenerProductosActivity) {
        this.listenerProductosActivity = listenerProductosActivity;
    }

    public OnSugerenciasActivityListener getListenerSugerenciasActivity() {
        return listenerSugerenciasActivity;
    }
    public void setListenerSugerenciasActivity(OnSugerenciasActivityListener listenerSugerenciasActivity) {
        this.listenerSugerenciasActivity = listenerSugerenciasActivity;
    }

    //METODOS DE: DetalleActivity
    public void cargarInfo(String idPedido){
        mibase = FirebaseFirestore.getInstance();

        mibase.collection("pedidos")
                .document(idPedido)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Map<String, Object> data = documentSnapshot.getData();
                        String id = documentSnapshot.getId().toString();
                        //fecha
                        Timestamp timestamp = documentSnapshot.getTimestamp("fecha");
                        // Crear un objeto de fecha a partir del valor de timestamp
                        Date date = timestamp.toDate();
                        // Crear un objeto SimpleDateFormat para el formato deseado
                        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                        // Formatear la fecha como una cadena en el formato deseado
                        String fecha = sdf.format(date);
                        String usuario = data.get("usuario").toString();
                        String estado = data.get("estado").toString();
                        //Pedido que vamos a devolver
                        Pedido pedido = new Pedido(fecha, usuario, estado, id);

                        mibase = FirebaseFirestore.getInstance();
                        ArrayList<LineasPedido> lineas = new ArrayList<>();

                        //obtenemos las lineas asociadas a ese pedido
                        mibase.collection("pedidos")
                                .document(idPedido)
                                .collection("lineas")
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                            Map<String, Object> data = documentSnapshot.getData();
                                            int cantidad = Integer.valueOf(data.get("cantidad").toString());
                                            String idProducto = data.get("idProducto").toString();
                                            String nota = data.get("nota").toString();

                                            LineasPedido lineaPedido = new LineasPedido(idProducto, cantidad, nota);
                                            lineas.add(lineaPedido);
                                        }
                                        listenerDetalleActivity.onInfoReceive(pedido, lineas);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }
    public void obtenerTelefono(String idUsuario){
        mibase = FirebaseFirestore.getInstance();
        mibase.collection("usuarios")
                .document(idUsuario)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Map<String, Object> data = documentSnapshot.getData();
                        String telefono = data.get("telefono").toString();
                        listenerDetalleActivity.onNumeroRecibido(telefono);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }
    public void calcularTotal(ArrayList<LineasPedido> lineas){
        mibase = FirebaseFirestore.getInstance();
        for(LineasPedido linea: lineas){
            mibase.collection("productos")
                    .document(linea.getIdProducto())
                    .get()
                    .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            double total = 0;
                            if (documentSnapshot.exists()) {
                                Map<String, Object> data = documentSnapshot.getData();
                                double precio = Double.valueOf(data.get("precio").toString());
                                total = total + (precio * linea.getCantidad());
                                listenerDetalleActivity.onTotalCalculated(total);
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        }
                    });
        }

    }
    public void eliminarPedido(String idPedido){
        mibase = FirebaseFirestore.getInstance();
        //eliminamos de la tabla pedidos
        mibase.collection("pedidos")
                .document(idPedido)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        listenerDetalleActivity.onEliminatePedido();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }
    public void confirmarPedido(String auxEstado, String idPedido){
        mibase = FirebaseFirestore.getInstance();
        Map<String, Object> estado = new HashMap<>();

        switch(auxEstado){
            case "En espera":
                estado.put("estado", "Para recoger");
                break;
            case "Para recoger":
                estado.put("estado", "Finalizado");
                break;
        }

        mibase.collection("pedidos")
                .document(idPedido)
                .update(estado)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        listenerDetalleActivity.onConfirmarPedido();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }

    //METODOS DE: PedidosActivity
    public void cargarPedidos(String consulta) {
        mibase = FirebaseFirestore.getInstance();
        ArrayList<Pedido> pedidos = new ArrayList<>();

        mibase.collection("pedidos")
                .orderBy("fecha", Query.Direction.DESCENDING)
                .whereEqualTo("estado", consulta)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Map<String, Object> data = documentSnapshot.getData();
                            //fecha
                            Timestamp timestamp = documentSnapshot.getTimestamp("fecha");
                            // Crear un objeto de fecha a partir del valor de timestamp
                            Date date = timestamp.toDate();
                            // Crear un objeto SimpleDateFormat para el formato deseado
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                            // Formatear la fecha como una cadena en el formato deseado
                            String fecha = sdf.format(date);

                            String estado = data.get("estado").toString();
                            String usuario = data.get("usuario").toString();
                            String id = documentSnapshot.getId();
                            Pedido pedido = new Pedido(fecha, usuario, estado, id);
                            pedidos.add(pedido);
                        }
                        listenerPedidosActivity.onPedidosReceive(pedidos, false);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }
    public void filtrosPedidos(Date fechaInicio, Date fechaFin, String consulta){
        mibase = FirebaseFirestore.getInstance();
        ArrayList<Pedido> pedidos = new ArrayList<>();

        mibase.collection("pedidos")
                .whereEqualTo("estado", consulta)
                .whereGreaterThanOrEqualTo("fecha", fechaInicio)
                .whereLessThanOrEqualTo("fecha", fechaFin)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Map<String, Object> data = documentSnapshot.getData();
                            //fecha
                            Timestamp timestamp = documentSnapshot.getTimestamp("fecha");
                            // Crear un objeto de fecha a partir del valor de timestamp
                            Date date = timestamp.toDate();
                            // Crear un objeto SimpleDateFormat para el formato deseado
                            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm");
                            // Formatear la fecha como una cadena en el formato deseado
                            String fecha = sdf.format(date);

                            String estado = data.get("estado").toString();
                            String usuario = data.get("usuario").toString();
                            String id = documentSnapshot.getId();
                            Pedido pedido = new Pedido(fecha, usuario, estado, id);
                            pedidos.add(pedido);
                        }
                        listenerPedidosActivity.onPedidosReceive(pedidos, true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }

    //METODOS DE: SubirProductoActivity
    //1 -> Subimos el producto sin la url
    public void subirProducto(String idFamilia, String nombre, double precio){
        mibase = FirebaseFirestore.getInstance();

        Map<String, Object> datos = new HashMap<>();
        datos.put("name", nombre);
        datos.put("precio", precio);
        datos.put("imgURL", "");
        datos.put("tipo", idFamilia);
        datos.put("eliminado", "no");
        datos.put("sugerencias", "no");
        //campos auxiliares que no se verán pero nos serviran para top_ventas y novedades
        datos.put("veces_pedido", 0);

        Date fechaActual = Calendar.getInstance().getTime();
        datos.put("fecha", fechaActual);

        mibase.collection("productos")
                .add(datos)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        String id = documentReference.getId();
                        listenerSubirProductoActivity.onProductoSubido(id);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }
    //2 -> Subimos la imagen a Storage en la ruta imagenes/id
    public void subirImagenProducto(String id, Uri imagen){
        if(imagen != null){
            FirebaseStorage storage = FirebaseStorage.getInstance();
            StorageReference storageRef = storage.getReference().child("productos/" + id);
            storageRef.putFile(imagen)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Obtener la URL de descarga de la imagen subida
                            storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri downloadUrl) {
                                    //3 -> cuando tenemos la url, modificamos ese elemento
                                    String url = downloadUrl.toString();
                                    mibase = FirebaseFirestore.getInstance();
                                    //modificamos el producto, teniendo ahora la url de descarga
                                    mibase.collection("productos")
                                            .document(id)
                                            .update("imgURL", url)
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    listenerSubirProductoActivity.onImageSubida(id);

                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                }
                                            });

                                }
                            });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                        }
                    });
        }else{
            mibase = FirebaseFirestore.getInstance();
            mibase.collection("productos")
                    .document(id)
                    .update("imgURL", "no tiene")
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            listenerSubirProductoActivity.onImageSubida(id);
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                        }
                    });
        }
    }

    //METODOS DE: FamiliasActivity
    public void cargarFamilias(){
        ArrayList<Familia> familias = new ArrayList<>();

        mibase = FirebaseFirestore.getInstance();
        //obtener los productos bocadillos
        mibase.collection("familias")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Map<String, Object> data = documentSnapshot.getData();
                            String nombre = data.get("nombre").toString();
                            String info = data.get("info").toString();
                            String url = data.get("imgURL").toString();
                            String id = documentSnapshot.getId();
                            Familia familia = new Familia(id, nombre, info, url);

                            familias.add(familia);
                        }
                        listenerFamiliasActivity.onFamiliasReceive(familias);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }
    public void eliminarFamiliaYProductos(String idFamilia){
        mibase = FirebaseFirestore.getInstance();
        //eliminamos de la tabla familias
        mibase.collection("familias")
                .document(idFamilia)
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        //Eliminamos de storage
                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        StorageReference storageRef = storage.getReference().child("familias/" + idFamilia);
                        storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                            }
                        });

                        //eliminamos los productos asociados
                        mibase.collection("productos")
                                .whereEqualTo("tipo", idFamilia)
                                .get()
                                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                    @Override
                                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                        for (DocumentSnapshot document : queryDocumentSnapshots) {
                                            document.getReference().delete();
                                            FirebaseStorage storage = FirebaseStorage.getInstance();
                                            StorageReference storageRef = storage.getReference().child("productos/" + document.getId());
                                            storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception exception) {
                                                }
                                            });
                                        }
                                    }
                                });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }
    
    //METODOS DE: ProductosActivity
    public void cargarProductos(String idFamilia){
        ArrayList<Producto> productos = new ArrayList<>();
        mibase = FirebaseFirestore.getInstance();

        mibase.collection("productos")
                //para que salgan arriba los mas nuevos
                .orderBy("fecha", Query.Direction.DESCENDING)
                .whereEqualTo("tipo", idFamilia)
                .whereEqualTo("eliminado", "no")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Map<String, Object> data = documentSnapshot.getData();
                            String nombre = data.get("name").toString();
                            double precio = Double.valueOf(data.get("precio").toString());
                            String url = data.get("imgURL").toString();
                            String id = documentSnapshot.getId();
                            String tipo = data.get("tipo").toString();
                            Producto producto = new Producto(id, nombre, precio, url, tipo);
                            productos.add(producto);
                        }
                        listenerProductosActivity.onProductosReceive(productos);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }
    public void cargarProducto(String idProducto){
        mibase = FirebaseFirestore.getInstance();
        mibase.collection("productos")
                .document(idProducto)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            Map<String, Object> data = documentSnapshot.getData();
                            String nombre = data.get("name").toString();
                            double precio = Double.valueOf(data.get("precio").toString());
                            String url = data.get("imgURL").toString();
                            String id = documentSnapshot.getId();
                            String tipo = data.get("tipo").toString();
                            Producto producto = new Producto(id, nombre, precio, url, tipo);
                            listenerProductosActivity.onProductoInsertado(producto);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }
    public void eliminarProducto(String idProducto){
        mibase = FirebaseFirestore.getInstance();
        //no eliminamos el producto, eliminamos su foto, para que no colapse la bdd de imagenes y el campo de la bdd eliminado lo ponemos a si
        //para identificar alli donde nos interese ese producto eliminado
        Map<String, Object> producto = new HashMap<>();
        producto.put("imgURL", "no tiene");
        producto.put("eliminado", "si");

        //Eliminamos de firestore
        mibase.collection("productos")
                .document(idProducto)
                .update(producto)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                        //Eliminamos de storage
                        FirebaseStorage storage = FirebaseStorage.getInstance();
                        StorageReference storageRef = storage.getReference().child("productos/" + idProducto);
                        storageRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                listenerProductosActivity.onProductoEliminado();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                //puede ser que de excepcion, puesto que hay productos que no tienen imagen
                                listenerProductosActivity.onProductoEliminado();
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }

    //METODOS DE: SugerenciasActivity
    public void cargarProductos(){
        ArrayList<Producto> productos = new ArrayList<>();
        mibase = FirebaseFirestore.getInstance();

        mibase.collection("productos")
                .whereEqualTo("eliminado", "no")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                            Map<String, Object> data = documentSnapshot.getData();
                            String nombre = data.get("name").toString();
                            double precio = Double.valueOf(data.get("precio").toString());
                            String url = data.get("imgURL").toString();
                            String id = documentSnapshot.getId();
                            String tipo = data.get("tipo").toString();
                            Producto producto = new Producto(id, nombre, precio, url, tipo);
                            productos.add(producto);
                        }
                        listenerSugerenciasActivity.onProductosCargados(productos);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }
    int i = 0;
    public void actualizarSugerencias(ArrayList<String> idsProductosSeleccionados){
        mibase = FirebaseFirestore.getInstance();

        //CAMBIAMOS LAS SUGERENCIAS QUE HABIA a si a no
        mibase.collection("productos")
                .whereEqualTo("sugerencias", "si")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        //Si la tabla NO esta vacía, cambiamos las que habia a no
                        if(queryDocumentSnapshots.size() > 0){
                            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {

                                Map<String, Object> sugerencias = new HashMap<>();
                                sugerencias.put("sugerencias", "no");
                                // código para eliminar documentos individuales
                                document.getReference().update(sugerencias).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        i++;
                                        //cuando haya cambiado el ultimo, añadimos los nuevos
                                        if(i == queryDocumentSnapshots.size()){
                                            insertarRegistrosProductos(idsProductosSeleccionados);
                                        }
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                    }
                                });
                            }
                        }
                        //Si la tabla estaba vacía los añadimos directamente
                        else{
                            insertarRegistrosProductos(idsProductosSeleccionados);
                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }

    private void insertarRegistrosProductos(ArrayList<String> idsProductosSeleccionados){
        mibase = FirebaseFirestore.getInstance();
        //AÑADIMOS LOS NUEVOS
        for(String str: idsProductosSeleccionados){
            Map<String, Object> sugerencias = new HashMap<>();
            sugerencias.put("sugerencias", "si");

            mibase.collection("productos")
                    .document(str)
                    .update(sugerencias)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            System.out.println("SUGERENCIAS ACTUALIZADAS");
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
        }
    }
}
