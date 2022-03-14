package com.cafeyvinowinebar.Cafe_y_Vino;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.cafeyvinowinebar.Cafe_y_Vino.Runnables.MesaBlocker;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

/**
 * Second step of making a reservation
 * The user chooses from the set of available tables for the chosen day and part of day
 */

public class ReservaMesaActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    String date, part, mesa;
    Menu popupMenu;
    private ImageView imgSala, imgInfo, imgMesa;
    private TextView txtMesa;
    private ContextThemeWrapper wrapper;
    private FloatingActionButton fabToNextScreen;

    public static Intent newIntent(Context context, String date, String part) {
        Intent intent = new Intent(context, ReservaMesaActivity.class);
        intent.putExtra(Utils.KEY_DATE, date);
        intent.putExtra(Utils.KEY_PART, part);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reserva_mesa);

        init();

        if (savedInstanceState != null) {
            mesa = savedInstanceState.getString(Utils.KEY_MESA);
            if (mesa != null) {
                txtMesa.setText(getString(R.string.mesa, mesa));
                txtMesa.setVisibility(View.VISIBLE);
            }
        }

        Intent intent = getIntent();
        date = intent.getStringExtra(Utils.KEY_DATE);
        part = intent.getStringExtra(Utils.KEY_PART);

        // we prepare the popup menu on a background thread with the reserved tables blocked
        PopupMenu popup = new PopupMenu(wrapper, imgMesa);
        popupMenu = popup.getMenu();

        // users shouldn't be able to choose already reserved tables
        // so we disable them from the popup menu
        App.executor.submit(new MesaBlocker(date, part, popupMenu));
        popup.setOnMenuItemClickListener(this);
        popup.inflate(R.menu.popup_mesa);

        // shows the location of tables on a plan
        imgSala.setOnClickListener(v -> startActivity(PlanActivity.newIntent(getBaseContext())));

        imgInfo.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(ReservaMesaActivity.this);
            View view = getLayoutInflater().inflate(R.layout.alert_reserva_mesa_info, null);
            builder.setView(view);
            builder.setCancelable(true);
            builder.create().show();
        });

        // to already chosen day and part we add the mesa and pass the three values to the ReservaFinActivity with the intent
        fabToNextScreen.setOnClickListener(v -> {
            if (mesa == null || mesa.isEmpty()) {
                Toast.makeText(ReservaMesaActivity.this, getString(R.string.reserva_empty_mesa), Toast.LENGTH_SHORT).show();
                return;
            }
            startActivity(ReservaFinActivity.newIntent(getBaseContext(), date, part, mesa));
        });

        imgMesa.setOnClickListener(v -> popup.show());
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.m01:
                mesa = "01";
                txtMesa.setText(getString(R.string.mesa, mesa));
                txtMesa.setVisibility(View.VISIBLE);
                return true;
            case R.id.m02:
                mesa = "02";
                txtMesa.setText(getString(R.string.mesa, mesa));
                txtMesa.setVisibility(View.VISIBLE);
                return true;
            case R.id.m03:
                mesa = "03";
                txtMesa.setText(getString(R.string.mesa, mesa));
                txtMesa.setVisibility(View.VISIBLE);
                return true;
            case R.id.m04:
                mesa = "04";
                txtMesa.setText(getString(R.string.mesa, mesa));
                txtMesa.setVisibility(View.VISIBLE);
                return true;
            case R.id.m05:
                mesa = "05";
                txtMesa.setText(getString(R.string.mesa, mesa));
                txtMesa.setVisibility(View.VISIBLE);
                return true;
            case R.id.m06:
                mesa = "06";
                txtMesa.setText(getString(R.string.mesa, mesa));
                txtMesa.setVisibility(View.VISIBLE);
                return true;
            case R.id.m07:
                mesa = "07";
                txtMesa.setText(getString(R.string.mesa, mesa));
                txtMesa.setVisibility(View.VISIBLE);
                return true;
            case R.id.m08:
                mesa = "08";
                txtMesa.setText(getString(R.string.mesa, mesa));
                txtMesa.setVisibility(View.VISIBLE);
                return true;
            case R.id.m09:
                mesa = "09";
                txtMesa.setText(getString(R.string.mesa, mesa));
                txtMesa.setVisibility(View.VISIBLE);
                return true;
            case R.id.m10:
                mesa = "10";
                txtMesa.setText(getString(R.string.mesa, mesa));
                txtMesa.setVisibility(View.VISIBLE);
                return true;
            case R.id.m11:
                mesa = "11";
                txtMesa.setText(getString(R.string.mesa, mesa));
                txtMesa.setVisibility(View.VISIBLE);
                return true;
            case R.id.m12:
                mesa = "12";
                txtMesa.setText(getString(R.string.mesa, mesa));
                txtMesa.setVisibility(View.VISIBLE);
                return true;
            default:
                return false;
        }

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Utils.KEY_MESA, mesa);
    }

    private void init() {
        imgSala = findViewById(R.id.imgSala);
        imgMesa = findViewById(R.id.imgEscogerMesa);
        fabToNextScreen = findViewById(R.id.fabNextMesa);
        imgInfo = findViewById(R.id.imgInfoMesa);
        txtMesa = findViewById(R.id.txtMesa);
        // we customize the appearance of the popup to make it look more pleasant with a custom style
        wrapper = new ContextThemeWrapper(this, R.style.popupMenuStyle);
    }
}