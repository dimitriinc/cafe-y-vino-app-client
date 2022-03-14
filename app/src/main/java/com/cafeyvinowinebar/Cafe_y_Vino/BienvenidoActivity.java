package com.cafeyvinowinebar.Cafe_y_Vino;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

/**
 * After the walkthrough, the user can either register and create a new profile, or login into an existing one
 */

public class BienvenidoActivity extends AppCompatActivity {

    public static Intent newIntent(Context context) {
        return new Intent(context, BienvenidoActivity.class);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bienvenido);

        Button btnLog = findViewById(R.id.btnLog);
        Button btnReg = findViewById(R.id.btnReg);

        btnReg.setOnClickListener(v -> startActivity(RegistrationActivity.newIntent(getBaseContext())));
        btnLog.setOnClickListener(v -> startActivity(LoginActivity.newIntent(getBaseContext())));
    }
}