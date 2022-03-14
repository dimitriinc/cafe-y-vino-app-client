package com.cafeyvinowinebar.Cafe_y_Vino.Runnables;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.os.Handler;
import android.widget.Button;
import android.widget.Toast;

import com.cafeyvinowinebar.Cafe_y_Vino.App;
import com.cafeyvinowinebar.Cafe_y_Vino.R;
import com.cafeyvinowinebar.Cafe_y_Vino.Utils;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class GiftSender implements Runnable {

    final FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    final FirebaseMessaging fMessaging = FirebaseMessaging.getInstance();

    private final DocumentSnapshot snapshot;
    private final Context context;
    private final String userId, fecha;
    private final Handler mainHandler;

    public GiftSender(DocumentSnapshot snapshot, Context context, String userId, Handler mainHandler, String fecha) {
        this.snapshot = snapshot;
        this.context = context;
        this.userId = userId;
        this.mainHandler = mainHandler;
        this.fecha = fecha;
    }

    @Override
    public void run() {

        boolean isDarkThemeOn = (context.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) == Configuration.UI_MODE_NIGHT_YES;

        // we get a reference to the user's document in the FirestoreDB to get access to their bonus points data
        DocumentReference userReference = fStore.collection("usuarios").document(userId);
        userReference.get().addOnSuccessListener(App.executor, userSnap -> {

            if (userSnap != null && userSnap.exists()) {

                // since we subtract the points right after sending the message to the administration
                // we ask for confirmation with an alert dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(context)
                        .setMessage(context.getString(R.string.gift_msg, snapshot.getId()))
                        .setPositiveButton(context.getString(R.string.confirmar), (dialog, which) -> {

                            // first we make sure the user has enough points to order the chosen gift
                            // for that we compare the user's bonus points and the price of the gift
                            long bonos = userSnap.getLong(Utils.KEY_BONOS);
                            long precio = Long.parseLong(snapshot.getString(Utils.PRECIO));
                            String nombre = userSnap.getString(Utils.KEY_NOMBRE);
                            String mesa = userSnap.getString(Utils.KEY_MESA);
                            if ((bonos - precio) < 0) {
                                mainHandler.post(() -> Toast.makeText(context, context.getString(R.string.gift_saldo), Toast.LENGTH_SHORT).show());
                            } else {

                                // if the user's points are good, we create an object to store it as a gift order in the FirestoreDB
                                Map<String, Object> gift = new HashMap<>();
                                gift.put(Utils.KEY_NOMBRE, snapshot.getId());
                                gift.put(Utils.USER, nombre);
                                gift.put(Utils.KEY_USER_ID, userSnap.getId());
                                gift.put(Utils.KEY_MESA, mesa);
                                gift.put(Utils.PRECIO, precio);
                                gift.put(Utils.SERVIDO, false);
                                gift.put(Utils.KEY_TIMESTAMP, new Timestamp(new Date()));
                                fStore.collection("pedidos").document(fecha).collection("regalos")
                                        .add(gift)
                                        .addOnSuccessListener(App.executor, documentReference -> {

                                            // when the object is stored, we notify the Administrator App
                                            // and subtract the price of the gift from the user's bonus points
                                            fMessaging.send(new RemoteMessage.Builder(App.SENDER_ID + "@fcm.googleapis.com")
                                                    .setMessageId(Utils.getMessageId())
                                                    .addData(Utils.KEY_REGALO, snapshot.getId())
                                                    .addData(Utils.KEY_ACTION, Utils.ACTION_REGALO)
                                                    .addData(Utils.KEY_MESA, mesa)
                                                    .addData(Utils.KEY_TYPE, Utils.TO_ADMIN)
                                                    .addData(Utils.KEY_NOMBRE, nombre)
                                                    .build());
                                            userReference.update(Utils.KEY_BONOS, bonos - precio);
                                        })
                                        .addOnFailureListener(e -> Toast.makeText(context, context.getString(R.string.error), Toast.LENGTH_SHORT).show());

                            }
                        })
                        .setNegativeButton("No", (dialog, which) -> {
                        })
                        .setCancelable(true);

                mainHandler.post(() -> {
                    AlertDialog dialog = builder.create();
                    dialog.show();

                    // with the dark theme on we change the color of the dialog buttons for a better readability
                    if (isDarkThemeOn) {
                        Button btnPositive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                        btnPositive.setTextColor(context.getColor(R.color.cream));
                        Button btnNegative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                        btnNegative.setTextColor(context.getColor(R.color.cream));
                    }
                });
            }
        });
    }
}
