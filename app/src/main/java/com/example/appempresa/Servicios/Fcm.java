package com.example.appempresa.Servicios;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.graphics.drawable.IconCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.appempresa.Controladores.PedidosActivity;
import com.example.appempresa.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Fcm extends FirebaseMessagingService {

    private FirebaseFirestore mibase;

    /**
     * Cada vez que se inicie sesión se actualizará el token del admin, ya que este puede cambiar,
     * si cambia de dispositivo, por ejemplo
     */
    public void guardarToken(){
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }

                        // Obtenemos el token
                        String token = task.getResult();

                        mibase = FirebaseFirestore.getInstance();
                        mibase.collection("usuarios")
                                .document("admin")
                                .update("token", token)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        System.out.println("TOKEN ACTUALIZADO");
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

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
    }


    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        String from =remoteMessage.getFrom();

        if (remoteMessage.getData().size()>0){
            Map<String, String> data = remoteMessage.getData();
            String title = data.get("title");
            String body = data.get("body");

            procesarNotificacion(title, body);

        }
    }

    //Métodos auxiliares, para procesar, etc la gestión de las notificaciones:

    /**
     * con este metodo obtenemos el token del usuario y sabemos a quien tenemos que enviarle las notificaciones
     * @param context
     * @param usuario -> usuario a quien va dirigido la notificación, hacemos una consulta a la bdd, obteniendo
     *                   el token asociado al usuario, y enviamos la notificacion a ese usuario.
     */
    public void enviarNotificacionPedido(String usuario, Context context) {
        mibase = FirebaseFirestore.getInstance();
        //Hacemos una consulta a la bdd para traernos el token del admin
        mibase.collection("usuarios")
                .document(usuario)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            Map<String, Object> data = documentSnapshot.getData();
                            String token = data.get("token").toString();

                            RequestQueue myrequest= Volley.newRequestQueue(context);
                            JSONObject json = new JSONObject();

                            try {
                                json.put("to",token);
                                JSONObject notificacion=new JSONObject();
                                notificacion.put("title", "Pedido listo");
                                notificacion.put("body", "Hola, " + usuario + ", ya está disponible su pedido para recoger");

                                json.put("data", notificacion);
                                String URL="https://fcm.googleapis.com/fcm/send";

                                JsonObjectRequest request=new JsonObjectRequest(Request.Method.POST,URL,json,null,null){
                                    @Override
                                    public Map<String, String> getHeaders() {
                                        Map<String,String>header=new HashMap<>();
                                        header.put("content-type","application/json");
                                        header.put("authorization","key=AAAA3TEoJtw:APA91bHnqoZbrmUPkefQA2caJ8KBHCzOt8KAMEjVZKMTsCrKbDogM6HYBsbXjy4J2IYAQ4etRkLl9f1FZ1GvUhbu8hKVXywdPPxIKDz1InXr0x9hjXYV0hIdNXR5n6_Y63FF1oGfkEe3");
                                        return  header;

                                    }
                                };
                                myrequest.add(request);

                            }catch (JSONException e){
                                e.printStackTrace();
                            }

                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                    }
                });
    }

    /**
     * metodo para procesar las notificaciones
     * @param titulo -> titulo del mensaje obtenido desde onMessageReceive
     * @param detalle -> detalle del mensaje obtenido desde onMessageReceive
     */
    private void procesarNotificacion(String titulo, String detalle) {
        String id="mensaje";
        NotificationManager nm=(NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder=new NotificationCompat.Builder(this,id);

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel nc=new NotificationChannel(id,"nuevo", NotificationManager.IMPORTANCE_HIGH);
            nc.setShowBadge(true);
            assert nm != null;
            nm.createNotificationChannel(nc);
        }

        Bitmap imagen = BitmapFactory.decodeResource(getResources(), R.drawable.logo_final);

        builder.setAutoCancel(true)
                .setWhen(System.currentTimeMillis())
                .setContentTitle(titulo)
                .setLargeIcon(imagen)
                .setSmallIcon(IconCompat.createWithBitmap(imagen))
                .setContentText(detalle)
                .setContentIntent(clickNotificacion())
                .setContentInfo("nuevo");

        Random random=new Random();
        int idNotity =random.nextInt(8000);

        assert nm !=null;
        nm.notify(idNotity,builder.build());

    }

    /**
     * para gestionar cuando se pulsa la notificacion
     * en este caso, llamamos a pedidos activity (puesto que al dueño SOLO se le notifica cuando se ha realizaso
     * un pedido) y le mostramos todos los pedidos que tiene pendientes, llevandolo a esta actividad
     * @return -> PendinIntent que nos llevará a PedidosActivity(los que están en espera)
     */
    public PendingIntent clickNotificacion(){
        Intent nf = new Intent(getApplicationContext(), PedidosActivity.class);
        nf.putExtra("consulta", "En espera");
        nf.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        return PendingIntent.getActivity(this,0,nf,0);
    }
}
