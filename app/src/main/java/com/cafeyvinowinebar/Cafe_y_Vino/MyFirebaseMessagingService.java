package com.cafeyvinowinebar.Cafe_y_Vino;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Objects;
import java.util.Random;

public class MyFirebaseMessagingService extends FirebaseMessagingService {

    private final FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore fStore = FirebaseFirestore.getInstance();

    private NotificationManagerCompat manager;

    @Override
    public void onCreate() {
        super.onCreate();
        manager = NotificationManagerCompat.from(this);
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        FirebaseUser user = fAuth.getCurrentUser();
        if (user != null) {
            String userId = user.getUid();
            fStore.collection("usuarios").document(userId).update(Utils.KEY_TOKEN, s);
        }
    }

    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        String action = remoteMessage.getData().get(Utils.KEY_ACTION);
        switch (Objects.requireNonNull(action)) {
            case Utils.ACTION_PUERTA_ADMIN:
                processPuertaAck(remoteMessage);
                break;
            case Utils.ACTION_RESERVA_ACK:
                processReservaAck(remoteMessage);
                break;
            case Utils.ACTION_RESERVA_NACK:
                processReservaNack(remoteMessage);
                break;
            case Utils.ACTION_PEDIDO_ADMIN:
                processPedidoAck();
                break;
            case Utils.ACTION_CUENTA_ACK:
                processCuentaAck();
                break;
            case Utils.ACTION_CUENTA_ADMIN:
                processCuentaMessage(remoteMessage);
                break;
            case Utils.ACTION_REGALO_ADMIN:
                processRegaloMessage();
                break;
            case Utils.ACTION_MSG:
                processMsg(remoteMessage);
                break;
            default:
                break;
        }
    }

    private void processMsg(RemoteMessage message) {

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, App.DATOS)
                .setContentTitle(getString(R.string.noti_msg_title))
                .setSmallIcon(R.drawable.logo_mini)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(message.getData().get(Utils.ACTION_MSG)))
                .setColor(getColor(R.color.disco));
        manager.notify(new Random().nextInt(), builder.build());
    }

    private void processPuertaAck(RemoteMessage message) {
        String nombre = message.getData().get(Utils.KEY_NOMBRE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, App.PUERTA)
                .setContentTitle(getString(R.string.noti_puerta_title))
                .setSmallIcon(R.drawable.logo_mini)
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(getString(R.string.noti_puerta_text, nombre)))
                .setColor(getColor(R.color.disco))
                .setAutoCancel(true);
        manager.notify(new Random().nextInt(), builder.build());
        Utils.setCanSendPedidos(this, true);
    }

    private void processReservaAck(RemoteMessage message) {
        String fecha = message.getData().get(Utils.KEY_FECHA);
        String hora = message.getData().get(Utils.KEY_HORA);
        String pax = message.getData().get(Utils.KEY_PAX);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, App.RESERVA)
                .setContentTitle(getString(R.string.noti_reserva_ack_title))
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(getString(R.string.noti_reserva_ack_text, fecha, hora, pax)))
                .setSmallIcon(R.drawable.logo_mini)
                .setColor(getColor(R.color.disco))
                .setAutoCancel(true);
        manager.notify(new Random().nextInt(), builder.build());
    }

    private void processReservaNack(RemoteMessage message) {
        String fecha = message.getData().get(Utils.KEY_FECHA);
        String hora = message.getData().get(Utils.KEY_HORA);
        String comentario = message.getData().get(Utils.KEY_COMENTARIO);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, App.RESERVA)
                .setContentTitle(getString(R.string.noti_reserva_nack_title))
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(getString(R.string.noti_reserva_nack_text, fecha, hora, comentario)))
                .setSmallIcon(R.drawable.logo_mini)
                .setColor(getColor(R.color.disco))
                .setAutoCancel(true);
        manager.notify(new Random().nextInt(), builder.build());
    }

    private void processPedidoAck() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, App.PEDIDO)
                .setContentTitle(getString(R.string.noti_pedido_title))
                .setContentText(getString(R.string.noti_pedido_text))
                .setSmallIcon(R.drawable.logo_mini)
                .setColor(getColor(R.color.disco))
                .setAutoCancel(true);
        manager.notify(new Random().nextInt(), builder.build());
    }

    private void processCuentaAck() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, App.CUENTA)
                .setContentTitle(getString(R.string.noti_cuenta_title))
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(getString(R.string.noti_cuenta_text)))
                .setSmallIcon(R.drawable.logo_mini)
                .setColor(getColor(R.color.disco))
                .setAutoCancel(true);
        manager.notify(new Random().nextInt(), builder.build());
    }

    private void processCuentaMessage(RemoteMessage message) {
        Utils.setCanSendPedidos(this, true);
        String nombre = message.getData().get(Utils.KEY_NOMBRE);
        String bono = message.getData().get(Utils.KEY_BONO);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, App.CUENTA)
                .setSmallIcon(R.drawable.logo_mini)
                .setContentTitle(getString(R.string.noti_farewell_title))
                .setStyle(new NotificationCompat.BigTextStyle()
                        .bigText(getString(R.string.noti_farewell_text, nombre, bono)))
                .setColor(getColor(R.color.disco))
                .setAutoCancel(true);
        manager.notify(new Random().nextInt(), builder.build());
    }

    private void processRegaloMessage() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, App.PEDIDO)
                .setContentTitle(getString(R.string.noti_pedido_title))
                .setSmallIcon(R.drawable.logo_mini)
                .setContentText(getString(R.string.noti_pedido_text))
                .setColor(getColor(R.color.disco))
                .setAutoCancel(true);
        manager.notify(new Random().nextInt(), builder.build());
    }
}
