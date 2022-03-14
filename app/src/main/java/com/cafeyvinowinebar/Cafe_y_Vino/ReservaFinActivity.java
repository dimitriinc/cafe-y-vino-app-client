package com.cafeyvinowinebar.Cafe_y_Vino;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.cafeyvinowinebar.Cafe_y_Vino.Runnables.ReservaSender;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Objects;

/**
 * Final step of creating a reservation
 * Based on the chosen part of day, the user chooses the time
 * Based on chosen table, the user chooses the amount of persons
 */

public class ReservaFinActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    private final FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private final FirebaseAuth fAuth = FirebaseAuth.getInstance();

    private TextView txtClockDisplay;
    private TextView txtPaxDisplay;
    private ImageView imgClock;
    private String userId, hora, pax, mesa, userNombre, userTelefono, date;
    String part;
    private EditText edtComment;
    private ContextThemeWrapper wrapper;
    private FloatingActionButton fabUpload;
    private Handler mainHandler;
    PopupMenu clockPopup;

    public static Intent newIntent(Context context, String date, String part, String mesa) {
        Intent intent = new Intent(context, ReservaFinActivity.class);
        intent.putExtra(Utils.KEY_DATE, date);
        intent.putExtra(Utils.KEY_PART, part);
        intent.putExtra(Utils.KEY_MESA, mesa);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserva_fin);

        init();

        if (savedInstanceState != null) {
            hora = savedInstanceState.getString(Utils.KEY_HORA);
            pax = savedInstanceState.getString(Utils.KEY_PAX);
            if (hora != null && pax != null) {
                txtPaxDisplay.setText(pax);
                txtPaxDisplay.setVisibility(View.VISIBLE);
                txtClockDisplay.setText(hora);
                txtClockDisplay.setVisibility(View.VISIBLE);
            }

        }

        boolean isDarkThemeOn = (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)
                == Configuration.UI_MODE_NIGHT_YES;

        // we store three values from the previous steps in the instance vars
        Intent intent = getIntent();
        date = intent.getStringExtra(Utils.KEY_DATE);
        part = intent.getStringExtra(Utils.KEY_PART);
        mesa = intent.getStringExtra(Utils.KEY_MESA);

        // we build the clockPopup menu in the background before showing it on click
        App.executor.submit(new ClockPopupBuilder(clockPopup, part));

        clockPopup.setOnMenuItemClickListener(item -> {
            hora = item.getTitle().toString();
            txtClockDisplay.setText(hora);
            txtClockDisplay.setVisibility(View.VISIBLE);
            return true;
        });

        userId = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();

        // we get the user's name and phone number to put it into the reservation's request
        // and after we get it, we can define the OnClickListener to send the request
        DocumentReference doc = fStore.collection("usuarios").document(userId);
        doc.get().addOnCompleteListener(App.executor, task -> {

            if (task.isSuccessful()) {
                DocumentSnapshot snapshot = task.getResult();
                if (snapshot != null && snapshot.exists()) {

                    userNombre = snapshot.getString(Utils.KEY_NOMBRE);
                    userTelefono = snapshot.getString(Utils.TELEFONO_ACCENT);

                    fabUpload.setOnClickListener(v -> {

                        if (Utils.isConnected(getBaseContext())) {

                            if (hora == null || hora.isEmpty()) {
                                mainHandler.post(() -> Toast.makeText(ReservaFinActivity.this,
                                        getString(R.string.reserva_error_hora), Toast.LENGTH_SHORT).show());
                                return;
                            }
                            if (pax == null || pax.isEmpty()) {
                                mainHandler.post(() -> Toast.makeText(ReservaFinActivity.this,
                                        getString(R.string.reserva_error_pax), Toast.LENGTH_SHORT).show());
                                return;
                            }

                            // get an optional comment from the edit view
                            String comment = edtComment.getText().toString().trim();

                            // build an alert dialog to give the user an opportunity to review the data they chose
                            AlertDialog.Builder builder = new AlertDialog.Builder(ReservaFinActivity.this);
                            LayoutInflater inflater = getLayoutInflater();
                            View view = inflater.inflate(R.layout.reserva_confirmation, null);
                            builder.setView(view);
                            builder.setMessage(getString(R.string.reserva_tiempo_espera));
                            builder.setPositiveButton(R.string.confirmar, (dialog, which) -> {

                                // send the request on a background thread
                                App.executor.submit(new ReservaSender(part, date, hora, pax, mesa, userNombre, userTelefono,
                                        userId, comment, getBaseContext(), mainHandler));

                                // go to the MainActivity with a clear top flag to destroy all the instances of the reserva's activities
                                Intent intentFin = new Intent(ReservaFinActivity.this, MainActivity.class);
                                intentFin.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                startActivity(intentFin);

                            });

                            // populate the view with the data
                            TextView txtFecha = view.findViewById(R.id.txtReservaFecha);
                            TextView txtHora = view.findViewById(R.id.txtReservaHora);
                            TextView txtComentario = view.findViewById(R.id.txtReservaComentario);
                            TextView txtMesa = view.findViewById(R.id.txtReservaMesa);
                            TextView txtPax = view.findViewById(R.id.txtReservaPax);
                            txtFecha.setText(date);
                            txtHora.setText(hora);
                            if (comment.isEmpty()) {
                                txtComentario.setText(getString(R.string.no_comment));
                            } else {
                                txtComentario.setText(comment);
                            }
                            txtMesa.setText(mesa);
                            txtPax.setText(pax);
                            AlertDialog dialog = builder.create();
                            dialog.show();

                            // to make the alert dialog more readable in the dark theme
                            if (isDarkThemeOn) {
                                Button btnPositive = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
                                btnPositive.setTextColor(getColor(R.color.soft_light_teal));
                                Button btnNegative = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                                btnNegative.setTextColor(getColor(R.color.soft_light_teal));
                            }

                        } else {
                            Toast.makeText(getBaseContext(), R.string.no_connection, Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });

        imgClock.setOnClickListener(v -> clockPopup.show());


    }

    public void init() {
        fabUpload = findViewById(R.id.fabUpload);
        edtComment = findViewById(R.id.edtComment);
        imgClock = findViewById(R.id.imgClock);
        txtClockDisplay = findViewById(R.id.txtClockDisplay);
        txtPaxDisplay = findViewById(R.id.txtPaxDisplay);
        // using a custom style for the popups to increase their niceness
        wrapper = new ContextThemeWrapper(this, R.style.popupMenuStyle);
        mainHandler = new Handler();
        clockPopup = new PopupMenu(wrapper, imgClock);
    }

    public void showPopupPax(View v) {

        // those two tables are big ones, so the amount of persons available is larger, it gets its own popup
        if (mesa.equals("05") | mesa.equals("11")) {
            PopupMenu popup = new PopupMenu(wrapper, v);
            popup.setOnMenuItemClickListener(this);
            popup.inflate(R.menu.popup_pax_large);
            popup.show();
        }

        // the rest of the tables are of personal size, gets the appropriate popup
        if (mesa.equals("01") | mesa.equals("02") | mesa.equals("03") | mesa.equals("04")
                | mesa.equals("06") | mesa.equals("07") | mesa.equals("08") | mesa.equals("09")
                | mesa.equals("10") | mesa.equals("12")) {
            PopupMenu popup = new PopupMenu(wrapper, v);
            popup.setOnMenuItemClickListener(this);
            popup.inflate(R.menu.popup_pax_small);
            popup.show();
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Utils.KEY_HORA, hora);
        outState.putString(Utils.KEY_PAX, pax);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.p01:
                pax = "01";
                txtPaxDisplay.setText(pax);
                txtPaxDisplay.setVisibility(View.VISIBLE);
                return true;
            case R.id.p02:
                pax = "02";
                txtPaxDisplay.setText(pax);
                txtPaxDisplay.setVisibility(View.VISIBLE);
                return true;
            case R.id.p03:
            case R.id.g03:
                pax = "03";
                txtPaxDisplay.setText(pax);
                txtPaxDisplay.setVisibility(View.VISIBLE);
                return true;
            case R.id.g04:
                pax = "04";
                txtPaxDisplay.setText(pax);
                txtPaxDisplay.setVisibility(View.VISIBLE);
                return true;
            case R.id.g05:
                pax = "05";
                txtPaxDisplay.setText(pax);
                txtPaxDisplay.setVisibility(View.VISIBLE);
                return true;
            case R.id.g06:
                pax = "06";
                txtPaxDisplay.setText(pax);
                txtPaxDisplay.setVisibility(View.VISIBLE);
                return true;
            default:
                return false;

        }
    }

    /**
     * Based on the part of day we retrieve an array of available hours from the FirestoreDb
     * We pass this array to the getPopup() to build the popup menu's items
     * We build it programmatically to keep the available hours flexible
     */
    private static class ClockPopupBuilder implements Runnable {

        PopupMenu clockPopup;
        String part;
        private final FirebaseFirestore fStore = FirebaseFirestore.getInstance();

        public ClockPopupBuilder(PopupMenu clockPopup, String part) {
            this.clockPopup = clockPopup;
            this.part = part;
        }

        @Override
        public void run() {

            if (part.equals(Utils.DIA)) {
                fStore.collection("utils").document("horas")
                        .get()
                        .addOnSuccessListener(App.executor, documentSnapshot -> {
                            ArrayList<String> horas = (ArrayList<String>) documentSnapshot.get("horas de reserva (dia)");
                            assert horas != null;
                            getPopup(horas, clockPopup);
                        });
            } else if (part.equals(Utils.NOCHE)) {
                fStore.collection("utils").document("horas")
                        .get()
                        .addOnSuccessListener(App.executor, documentSnapshot -> {
                            ArrayList<String> horas = (ArrayList<String>) documentSnapshot.get("horas de reserva (noche)");
                            assert horas != null;
                            getPopup(horas, clockPopup);
                        });
            }
        }

        /**
         * Builds a popup menu based on the array of hours
         */
        private void getPopup(ArrayList<String> horas, PopupMenu clockPopup) {

            // we add one item to the menu for one value stored in the array
            for (int i = 0; i < horas.size(); i++) {
                clockPopup.getMenu().add(i, Menu.FIRST, i, horas.get(i));
            }

        }

    }
}