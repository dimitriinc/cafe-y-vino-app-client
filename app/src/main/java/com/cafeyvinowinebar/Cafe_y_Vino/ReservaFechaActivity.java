package com.cafeyvinowinebar.Cafe_y_Vino;

import androidx.annotation.Keep;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

/**
 * First step of making a reservation
 * The user chooses date and part of day (each day has two sets: day and night)
 */

public class ReservaFechaActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener, DatePickerDialog.OnDateSetListener {

    private static final String TAG = "ReservaFechaActivity";

    private final FirebaseFirestore fStore = FirebaseFirestore.getInstance();

    TextView txtDate;
    ImageView imgCalendar, imgDayNight, imgPartOfDayDisplay, imgInfo;
    String date, part;
    private FloatingActionButton fabToNextScreen;
    private ContextThemeWrapper wrapper;
    FragmentManager manager;
    Handler mainHandler;

    public static Intent newIntent(Context context) {
        return new Intent(context, ReservaFechaActivity.class);
    }


    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserva_fecha);

        init();

        if (savedInstanceState != null) {

            Log.d(TAG, "onCreate: date: " + date + "\npart: " + part);

            date = savedInstanceState.getString(Utils.KEY_DATE);
            part = savedInstanceState.getString(Utils.KEY_PART);

            if (date != null && part != null) {
                txtDate.setText(date);
                txtDate.setVisibility(View.VISIBLE);
                if (part.equals(Utils.DIA)) {
                    imgPartOfDayDisplay.setBackgroundResource(R.drawable.ic_day);
                    imgPartOfDayDisplay.setVisibility(View.VISIBLE);
                } else if (part.equals(Utils.NOCHE)) {
                    imgPartOfDayDisplay.setBackgroundResource(R.drawable.ic_night);
                    imgPartOfDayDisplay.setVisibility(View.VISIBLE);
                }
            }
        }

        imgInfo.setOnClickListener(v -> showInfo());

        imgCalendar.setOnClickListener(v -> new DatePickerFragment().show(manager, Utils.TAG));

        // Takes the user to the ReservaMesaActivity, passes the mDate and mPart with the Intent
        fabToNextScreen.setOnClickListener(v -> {

            if (date == null || date.isEmpty()) {
                Toast.makeText(ReservaFechaActivity.this, getString(R.string.reserva_error_date), Toast.LENGTH_LONG).show();
                return;
            }
            if (part == null || part.isEmpty()) {
                Toast.makeText(ReservaFechaActivity.this, getString(R.string.reserva_error_part), Toast.LENGTH_SHORT).show();
                return;
            }
            startActivity(ReservaMesaActivity.newIntent(getBaseContext(), date, part));
        });
    }

    public void init() {

        manager = getSupportFragmentManager();
        txtDate = findViewById(R.id.txtDateDisplay);
        imgCalendar = findViewById(R.id.imgCalendar);
        fabToNextScreen = findViewById(R.id.fabNextFecha);
        imgPartOfDayDisplay = findViewById(R.id.imgPartOfDayDisplay);
        imgDayNight = findViewById(R.id.imgDayNight);
        imgInfo = findViewById(R.id.imgInfo);
        wrapper = new ContextThemeWrapper(this, R.style.popupMenuStyle);
        mainHandler = new Handler();
    }

    /**
     * Creates and shows a popup menu to choose a part of day
     * Is kept from minifying since it uses the Reflection API
     */
    @Keep
    public void showPopup(View v) {

        // to build a popup we first need to retrieve the data about the hours for day and night parts from the FirestoreDB
        fStore.collection("utils").document("horas")
                .get()
                .addOnSuccessListener(App.executor, documentSnapshot -> {

                    ArrayList<String> horas = (ArrayList<String>) documentSnapshot.get("horas dia_noche");
                    assert horas != null;

                    // then we start building the menu programmatically
                    // we don't use a resource menu file, so that the popup dynamically reflects changes in the hours
                    PopupMenu popup = new PopupMenu(wrapper, v);
                    popup.getMenu().add(1, R.id.day, 0, horas.get(0)).setIcon(R.drawable.ic_day);
                    popup.getMenu().add(1, R.id.night, 1, horas.get(1)).setIcon(R.drawable.ic_night);
                    popup.setOnMenuItemClickListener(ReservaFechaActivity.this);

                    // we use reflections to force show the icons beside the menu items
                    Object menuHelper;
                    Class[] argTypes;
                    try {
                        @SuppressLint("DiscouragedPrivateApi")
                        Field fMenuHelper = PopupMenu.class.getDeclaredField("mPopup");
                        fMenuHelper.setAccessible(true);
                        menuHelper = fMenuHelper.get(popup);
                        argTypes = new Class[]{boolean.class};
                        assert menuHelper != null;
                        menuHelper.getClass().getDeclaredMethod("setForceShowIcon", argTypes).invoke(menuHelper, true);
                    } catch (Exception ignored) {
                    }
                    mainHandler.post(popup::show);
                });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.day:
                part = Utils.DIA;
                imgPartOfDayDisplay.setBackgroundResource(R.drawable.ic_day);
                imgPartOfDayDisplay.setVisibility(View.VISIBLE);
                return true;
            case R.id.night:
                part = Utils.NOCHE;
                imgPartOfDayDisplay.setBackgroundResource(R.drawable.ic_night);
                imgPartOfDayDisplay.setVisibility(View.VISIBLE);
                return true;
            default:
                return false;
        }
    }

    private void showInfo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ReservaFechaActivity.this);
        View view = getLayoutInflater().inflate(R.layout.alert_reserva_fecha_info, null);
        builder.setView(view);
        builder.create().show();
    }

    @Override
    @SuppressLint("SimpleDateFormat")
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        // we check what day of week the chosen date is
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE");
        Date date = new Date(year, month, dayOfMonth - 1);
        String dayOfWeek = sdf.format(date);

        // the restaurant doesn't accept reservations on weekends, so if the user chooses those days
        // we show them an alert dialog with the message about the restrictions
        if (dayOfWeek.equals("viernes") || dayOfWeek.equals("sábado") || dayOfWeek.equals("domingo")
                || dayOfWeek.equals("friday") || dayOfWeek.equals("saturday") || dayOfWeek.equals("sunday")) {
            showInfo();
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, dayOfMonth);
            SimpleDateFormat format = new SimpleDateFormat(Utils.DATE_FORMAT);
            this.date = format.format(calendar.getTime());
            txtDate.setText(this.date);
            txtDate.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        Log.d(TAG, "onSaveInstanceState: date: " + date + "\npart: " + part);
        outState.putString(Utils.KEY_DATE, date);
        outState.putString(Utils.KEY_PART, part);
    }
}