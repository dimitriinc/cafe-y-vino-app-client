package com.cafeyvinowinebar.Cafe_y_Vino;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;
import java.util.TimeZone;

/**
 * The main screen that directs the user to all the main parts of the app
 */

public class MainActivity extends AppCompatActivity {

    private final FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private final FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private final FirebaseMessaging fMessaging = FirebaseMessaging.getInstance();

    private String userToken;

    private FloatingActionButton fabPuerta, fabCanasta, fabCuenta, fabUserData;
    private ImageView imgMenu, imgReserv, imgGiftshop;
    private String userId, nombre;
    private DocumentReference userDoc;
    private FirebaseUser user;
    private Handler handler;

    /**
     * This method is used to open the MainActivity by the activities that we don't want the user to return to with the back button
     * So we clear the stack and make the MainActivity the only instance there
     * The method is called by the CanastaActivity and the CuentaActivity when the user gets thrown from there due to the presence status change
     * And also by the Login and Registration activities
     */
    public static Intent newIntent(Context context) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        user = fAuth.getCurrentUser();

        // makes sure that the user is logged in, if they're not, sends them to the walk through
        if (user == null) {
            startActivity(WalkthroughActivity.newIntent(this));
            finish();
            return;
        }

        init();

        imgMenu.setOnClickListener(v -> startActivity(MainMenuActivity.newIntent(getBaseContext())));

        imgGiftshop.setOnClickListener(v -> startActivity(GiftShopActivity.newIntent(getBaseContext())));

        imgReserv.setOnClickListener(v -> startActivity(ReservaFechaActivity.newIntent(getBaseContext())));

        fabPuerta.setOnClickListener(v -> {

            // Shows an alert dialog explaining the functionality of the button to the user
            View view = getLayoutInflater().inflate(R.layout.alert_entry_request, null);
            TextView msg = view.findViewById(R.id.txtRequestMsg);
            Button btnPositive = view.findViewById(R.id.btnRequestPositive);
            Button btnNegative = view.findViewById(R.id.btnRequestNegative);
            ProgressBar progressBar = view.findViewById(R.id.pbEntry);

            AlertDialog dialog = new AlertDialog.Builder(MainActivity.this)
                    .setView(view)
                    .create();

            btnPositive.setOnClickListener(btnV -> {
                msg.setVisibility(View.INVISIBLE);
                btnPositive.setVisibility(View.INVISIBLE);
                btnNegative.setVisibility(View.INVISIBLE);
                progressBar.setVisibility(View.VISIBLE);
                sendEntryRequest(dialog);
            });
            btnNegative.setOnClickListener(btnV -> dialog.dismiss());

            dialog.show();

        });

        fabCanasta.setOnClickListener(v -> startActivity(CanastaActivity.newIntent(getBaseContext())));

        fabCuenta.setOnClickListener(v -> startActivity(CuentaActivity.newIntent(getBaseContext())));

        fabUserData.setOnClickListener(v -> startActivity(UserDataActivity.newIntent(getBaseContext())));

    }

    public void init() {
        imgMenu = findViewById(R.id.imgBtnMenu);
        imgReserv = findViewById(R.id.imgBtnReserv);
        imgGiftshop = findViewById(R.id.imgBtnGiftshop);
        fabPuerta = findViewById(R.id.fabPuerta);
        fabCanasta = findViewById(R.id.fabCanastaMain);
        fabCuenta = findViewById(R.id.fabCuentaMain);
        fabUserData = findViewById(R.id.fabUserData);
        userId = Objects.requireNonNull(user.getUid());
        userDoc = fStore.collection("usuarios").document(userId);
        handler = new Handler();
        fMessaging.getToken().addOnSuccessListener(App.executor, s -> userToken = s);
    }

    /**
     * User sends a message to the Administrator App requesting to change their status to 'present'
     * Before sending, the method performs a series of checks: is the device connected to a network? is the current day a working day?
     * is the current hour a working hour?
     * Only if all three checks return positive, we get tokens of the admins and send them an FCM message
     */
    private void sendEntryRequest(AlertDialog dialog) {
        if (Utils.isConnected(getBaseContext())) {

            // first we check what day it is
            String day = LocalDate.now().getDayOfWeek().name();

            // then we consult the FirestoreDB what days are stored as the off days
            fStore.collection("utils").document("horas").get().addOnSuccessListener(App.executor,
                    documentSnapshot -> {

                        ArrayList<String> daysOff = (ArrayList<String>) documentSnapshot.get("dias de descanso");
                        assert daysOff != null;
                        for (String dayOff : daysOff) {

                            if (!day.equals(dayOff)) {

                                // if the current day is not an off day, we check what hour is it
                                Calendar calendar = Calendar.getInstance();
                                calendar.setTimeZone(TimeZone.getTimeZone(Utils.GMT));
                                int hour = calendar.get(Calendar.HOUR_OF_DAY);

                                // then we consult the FirestoreDB about the working hours of the restaurant
                                ArrayList<Long> horas = (ArrayList<Long>) documentSnapshot.get("horas de atencion");
                                assert horas != null;
                                for (Long hora : horas) {

                                    // if the current hour is within the array of warking hours, we can send the user's request
                                    if (hora == hour) {

                                        // get all the registered admins' tokens, and send each one a message
                                        fStore.collection("administradores").get()
                                                .addOnSuccessListener(App.executor, queryDocumentSnapshots -> {

                                                    for (QueryDocumentSnapshot admin : queryDocumentSnapshots) {
                                                        String adminToken = admin.getString(Utils.KEY_TOKEN);
                                                        fMessaging.send(new RemoteMessage.Builder(App.SENDER_ID + "@fcm.googleapis.com")
                                                                .setMessageId(Utils.getMessageId())
                                                                .setCollapseKey(Utils.ACTION_PUERTA)
                                                                .addData(Utils.KEY_USER_ID, userId)
                                                                .addData(Utils.KEY_TOKEN, userToken)
                                                                .addData(Utils.KEY_ADMIN_TOKEN, adminToken)
                                                                .addData(Utils.KEY_TYPE, Utils.TO_ADMIN_NEW)
                                                                .addData(Utils.KEY_NOMBRE, nombre)
                                                                .addData(Utils.KEY_ACTION, Utils.ACTION_PUERTA)
                                                                .build());
                                                    }
                                                    handler.post(() -> {
                                                        Toast.makeText(getBaseContext(), getString(R.string.main_request_puerta), Toast.LENGTH_SHORT).show();
                                                        if (dialog != null) {
                                                            dialog.dismiss();
                                                        }
                                                    });
                                                });
                                        return;
                                    }
                                }
                            }

                            // if the current day is in the array of off days,
                            // or the current hour is not in the array of attendance hours,
                            // the user can't send entrance requests
                            handler.post(() -> {
                                Toast.makeText(MainActivity.this, R.string.main_we_closed, Toast.LENGTH_SHORT).show();
                                if (dialog != null) {
                                    dialog.dismiss();
                                }
                            });

                        }
                    });
        } else {
            Toast.makeText(getBaseContext(), R.string.no_connection, Toast.LENGTH_LONG).show();
            if (dialog != null) {
                dialog.dismiss();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        // we listen to the user's presence status. depending on the status, visibility of the FABs changes
        // in the 'not present' status they can review and change their personal data and send entrance requests
        // in the 'present' status they can enter the canasta and cuenta activities
        userDoc.addSnapshotListener(this, (value, error) -> {
            if (error != null) {
                return;
            }
            if (value != null && value.exists()) {
                nombre = value.getString(Utils.KEY_NOMBRE);
                if (Boolean.TRUE.equals(value.getBoolean(Utils.IS_PRESENT))) {

                    Utils.setIsUserPresent(MainActivity.this, true);
                    fabCanasta.setVisibility(View.VISIBLE);
                    fabCuenta.setVisibility(View.VISIBLE);
                    fabPuerta.setVisibility(View.GONE);
                    fabUserData.setVisibility(View.GONE);
                } else {
                    Utils.setCanSendPedidos(getBaseContext(), true);
                    Utils.setIsUserPresent(MainActivity.this, false);
                    fabCanasta.setVisibility(View.GONE);
                    fabCuenta.setVisibility(View.GONE);
                    fabPuerta.setVisibility(View.VISIBLE);
                    fabUserData.setVisibility(View.VISIBLE);
                }
            }
        });
    }
}