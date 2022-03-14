package com.cafeyvinowinebar.Cafe_y_Vino;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Displays the arrangement of tables in the restaurant
 */
public class PlanActivity extends AppCompatActivity {

    public static Intent newIntent(Context context) {
        return new Intent(context, PlanActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan);
    }
}