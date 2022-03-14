package com.cafeyvinowinebar.Cafe_y_Vino.Runnables;

import android.content.Context;
import android.os.Handler;
import android.widget.Toast;

import com.cafeyvinowinebar.Cafe_y_Vino.App;
import com.cafeyvinowinebar.Cafe_y_Vino.R;
import com.cafeyvinowinebar.Cafe_y_Vino.Utils;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.util.HashMap;
import java.util.Map;

/**
 * Creates a reservation object
 * Sets it as a document in the FirestoreDB
 * Sends a message to the Administrator App with all the data
 */
public class ReservaSender implements Runnable {

    private final FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private final FirebaseMessaging fMessaging = FirebaseMessaging.getInstance();

    private final String part, date, hora, pax, mesa, userNombre, userTelefono, userId, comment;
    private final Context context;
    private final Handler mainHandler;

    public ReservaSender(String part, String date, String hora, String pax, String mesa,
                         String userNombre, String userTelefono, String userId, String comment,
                         Context context, Handler mainHandler) {
        this.part = part;
        this.date = date;
        this.hora = hora;
        this.pax = pax;
        this.mesa = mesa;
        this.userNombre = userNombre;
        this.userTelefono = userTelefono;
        this.userId = userId;
        this.comment = comment;
        this.context = context;
        this.mainHandler = mainHandler;
    }

    @Override
    public void run() {

        DocumentReference document = fStore.collection("reservas")
                .document(date)
                .collection(part)
                .document(mesa);

        // create an object for the reservation
        Map<String, Object> reserva = new HashMap<>();
        reserva.put(Utils.KEY_NOMBRE, userNombre);
        reserva.put(Utils.TELEFONO, userTelefono);
        reserva.put(Utils.KEY_PAX, pax);
        reserva.put(Utils.KEY_HORA, hora);
        reserva.put(Utils.KEY_CONFIRMADO, false);
        reserva.put(Utils.KEY_MESA, mesa);
        reserva.put(Utils.KEY_USER_ID, userId);
        reserva.put(Utils.KEY_COMENTARIO, comment);
        reserva.put(Utils.KEY_FECHA, date);
        reserva.put(Utils.KEY_LLEGADO, false);

        // set it on the document
        document.set(reserva)
                .addOnSuccessListener(App.executor, aVoid -> mainHandler.post(() -> {

                    mainHandler.post(() -> Toast.makeText(context, context.getString(R.string.solicitud_reserva, userNombre), Toast.LENGTH_LONG).show());

                    // once the document is set, send the message to administrators
                    fMessaging.getToken()
                            .addOnSuccessListener(App.executor, s ->
                                    fMessaging.send(new RemoteMessage.Builder(App.SENDER_ID + "@fcm.googleapis.com")
                                            .setMessageId(Utils.getMessageId())
                                            .addData(Utils.KEY_TOKEN, s)
                                            .addData(Utils.KEY_NOMBRE, userNombre)
                                            .addData(Utils.KEY_ACTION, Utils.ACTION_RESERVA)
                                            .addData(Utils.KEY_FECHA, date)
                                            .addData(Utils.KEY_HORA, hora)
                                            .addData(Utils.KEY_PAX, pax)
                                            .addData(Utils.KEY_MESA, mesa)
                                            .addData(Utils.KEY_PARTE, part)
                                            .addData(Utils.KEY_TYPE, Utils.TO_ADMIN)
                                            .addData(Utils.KEY_COMENTARIO, comment)
                                            .build()));
                }))
                .addOnFailureListener(App.executor, e -> mainHandler.post(() ->
                        Toast.makeText(context, context.getString(R.string.error), Toast.LENGTH_SHORT).show()));
    }
}
