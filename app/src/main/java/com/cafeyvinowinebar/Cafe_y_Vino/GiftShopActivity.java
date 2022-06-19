package com.cafeyvinowinebar.Cafe_y_Vino;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Locale;
import java.util.Objects;

/**
 * The screen displays the amount of bonus points in the fidelity program
 * And the dialog fragment that shows what gifts are available
 */

public class GiftShopActivity extends AppCompatActivity {

    private final FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private final FirebaseAuth fAuth = FirebaseAuth.getInstance();

    private TextView txtMensaje, txtBonos;
    String userId;
    Handler mainHandler;
    private DocumentReference userDoc;
    private ImageView imgGastar;
    private GiftshopDialogFragment fragment;
    public static Intent newIntent(Context context) {
        return new Intent(context, GiftShopActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gift_shop);

        mainHandler = new Handler();
        txtBonos = findViewById(R.id.txtBonos);
        txtMensaje = findViewById(R.id.txtMensaje);
        userId = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
        userDoc = fStore.collection("usuarios").document(userId);
        imgGastar = findViewById(R.id.imgGastar);



    }

    @Override
    protected void onStart() {
        super.onStart();
        userDoc.addSnapshotListener(this, (value, error) -> {
            if (error != null) {
                return;
            }
            if (value != null && value.exists()) {
                txtBonos.setText(String.valueOf(value.getLong(Utils.KEY_BONOS)));

                // we want only the first name of the user on display
                String firstName = Objects.requireNonNull(value.getString(Utils.KEY_NOMBRE)).split(" ")[0].toLowerCase();
                String firstNameCapitalized = firstName.substring(0, 1).toUpperCase() + firstName.substring(1);
                txtMensaje.setText(getString(R.string.gift_user_msg, firstNameCapitalized));
                imgGastar.setOnClickListener(v -> {

                    // the user is able to open the dialog fragment only in the 'present' status
                    if (Boolean.TRUE.equals(value.getBoolean(Utils.IS_PRESENT))) {

                        Utils.setIsUserPresent(GiftShopActivity.this, true);

                        FragmentManager manager = getSupportFragmentManager();
                        fragment = new GiftshopDialogFragment(userId, mainHandler);
                        fragment.show(manager, Utils.TAG);
                    } else {
                        Utils.setCanSendPedidos(GiftShopActivity.this, false);
                        Toast.makeText(GiftShopActivity.this, getString(R.string.regalo_not_present_rejection), Toast.LENGTH_SHORT).show();
                    }

                });
            }
        });
    }
    
}