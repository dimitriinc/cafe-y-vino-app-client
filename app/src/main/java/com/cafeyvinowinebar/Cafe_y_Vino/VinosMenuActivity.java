package com.cafeyvinowinebar.Cafe_y_Vino;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class VinosMenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vinos_menu);

        TextView txtTintos = findViewById(R.id.txtTintos);
        TextView txtBlancos = findViewById(R.id.txtBlancos);
        TextView txtRose = findViewById(R.id.txtRose);
        TextView txtCopa = findViewById(R.id.txtCopa);
        TextView txtPostre = findViewById(R.id.txtPostre);

        txtTintos.setOnClickListener(v -> startActivity(GenericCategoryActivity.newIntent(getBaseContext(),
                "menu/03.vinos/vinos/Vinos tintos/vinos")));

        txtBlancos.setOnClickListener(v -> startActivity(GenericCategoryActivity.newIntent(getBaseContext(),
                "menu/03.vinos/vinos/Vinos blancos/vinos")));

        txtRose.setOnClickListener(v -> startActivity(GenericCategoryActivity.newIntent(getBaseContext(),
                "menu/03.vinos/vinos/Vinos rose/vinos")));

        txtPostre.setOnClickListener(v -> startActivity(GenericCategoryActivity.newIntent(getBaseContext(),
                "menu/03.vinos/vinos/Vinos de postre/vinos")));

        txtCopa.setOnClickListener(v -> startActivity(GenericCategoryActivity.newIntent(getBaseContext(),
                "menu/03.vinos/vinos/Vinos por copa/vinos")));

    }

}