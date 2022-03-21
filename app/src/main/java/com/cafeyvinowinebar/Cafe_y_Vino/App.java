package com.cafeyvinowinebar.Cafe_y_Vino;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Upon the creation of the app's process we create the notification channels for different types of messages
 * And we initialize an ExecutorService with a thread pool matching the ammount of cores available on the device
 * This executor is responsible for all the background work of the app; gets shutdown when the app's process is terminated
 */

public class App extends Application {

    public static final String PUERTA = "channelPuerta";
    public static final String RESERVA = "channelReserva";
    public static final String PEDIDO = "channelPedido";
    public static final String CUENTA = "channelCuenta";
    public static final String DATOS = "channelDatos";
    public static final String SENDER_ID = "check the firebase account";
    private static final int NUMBER_OF_CORES = Runtime.getRuntime().availableProcessors();
    public static final ExecutorService executor = Executors.newFixedThreadPool(NUMBER_OF_CORES);

    @Override
    public void onCreate() {
        super.onCreate();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channelPuerta = new NotificationChannel(
                    PUERTA,
                    "Puerta",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channelPuerta.setDescription("Solicitudes y confirmaciones de entrada al restaurante");

            NotificationChannel channelReserva = new NotificationChannel(
                    RESERVA,
                    "Reservas",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channelReserva.setDescription("Solicitudes y confirmaciones de las reservaciones");

            NotificationChannel channelPedido = new NotificationChannel(
                    PEDIDO,
                    "Pedidos",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channelPedido.setDescription("Enviar y recibir confirmaciones de los pedidos");

            NotificationChannel channelCuenta = new NotificationChannel(
                    CUENTA,
                    "Cuentas",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channelCuenta.setDescription("Solicitudes y confirmaciones de las cuentas");

            NotificationChannel channelDatos = new NotificationChannel(
                    DATOS,
                    "Promociones y mensajes personalizados",
                    NotificationManager.IMPORTANCE_HIGH
            );
            channelDatos.setDescription("Recibir promociones y otros mensajes de la administración del restaurante");

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channelPuerta);
            manager.createNotificationChannel(channelReserva);
            manager.createNotificationChannel(channelPedido);
            manager.createNotificationChannel(channelCuenta);
            manager.createNotificationChannel(channelDatos);
        }

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        executor.shutdown();
    }
}
