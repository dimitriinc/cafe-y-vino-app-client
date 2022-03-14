package com.cafeyvinowinebar.Cafe_y_Vino;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cafeyvinowinebar.Cafe_y_Vino.Adapters.AdapterCuenta;
import com.cafeyvinowinebar.Cafe_y_Vino.POJOs.ItemCuenta;
import com.cafeyvinowinebar.Cafe_y_Vino.Runnables.CuentaTotalPriceSetter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Objects;

/**
 * Displays the bill as RecyclerView
 * Sends bill requests to the Administrator App
 */

public class CuentaActivity extends AppCompatActivity {

    private final FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private final FirebaseMessaging fMessaging = FirebaseMessaging.getInstance();

    private String userNombre;
    private String userMesa;
    public TextView txtMontoTotal;
    private RecyclerView recCuenta;
    private AdapterCuenta adapter;
    CollectionReference cuenta;
    private DocumentReference userDoc;
    private FloatingActionButton fabExpand, fabHome, fabMenu, fabCrypto, fabPedirCuenta, fabCash, fabPos, fabYape;
    private TextView txtCrypto, txtPedirCuenta, txtCash, txtPos, txtYape;
    private boolean areRootsVisible, areChildrenVisible;

    public static Intent newIntent(Context context) {
        return new Intent(context, CuentaActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuenta);

        init();

        setupAdapter();

        fabHome.setOnClickListener(v -> startActivity(new Intent(getBaseContext(), MainActivity.class)));

        fabMenu.setOnClickListener(v -> startActivity(MainMenuActivity.newIntent(getBaseContext())));

        fabExpand.setOnClickListener(v -> {

            if (Utils.getCanSendPedidos(getBaseContext())) {

                if (!areRootsVisible) {
                    fabPedirCuenta.show();
                    txtPedirCuenta.setVisibility(View.VISIBLE);
                    areRootsVisible = true;
                    fabExpand.setImageResource(R.drawable.ic_collapse);
                } else {
                    hideEverything();
                }

            } else {
                Toast.makeText(CuentaActivity.this, getString(R.string.cuenta_no_expand), Toast.LENGTH_SHORT).show();
            }
        });

        fabPedirCuenta.setOnClickListener(v -> {
            if (!areChildrenVisible) {
                fabCash.show();
                fabPos.show();
                fabYape.show();
                fabCrypto.show();
                txtCash.setVisibility(View.VISIBLE);
                txtPos.setVisibility(View.VISIBLE);
                txtYape.setVisibility(View.VISIBLE);
                txtCrypto.setVisibility(View.VISIBLE);
                txtPedirCuenta.setVisibility(View.GONE);
                areChildrenVisible = true;
                fabPedirCuenta.setImageResource(R.drawable.ic_x);
            } else {
                fabCash.hide();
                fabPos.hide();
                fabYape.hide();
                fabCrypto.hide();
                txtCash.setVisibility(View.GONE);
                txtPos.setVisibility(View.GONE);
                txtYape.setVisibility(View.GONE);
                txtCrypto.setVisibility(View.GONE);
                txtPedirCuenta.setVisibility(View.VISIBLE);
                fabPedirCuenta.setImageResource(R.drawable.ic_pedir_cuenta);
                areChildrenVisible = false;
            }
        });

        fabCash.setOnClickListener(v -> sendCuentaRequest(Utils.CASH));

        fabYape.setOnClickListener(v -> sendCuentaRequest(Utils.YAPE));

        fabCrypto.setOnClickListener(v -> sendCuentaRequest(Utils.CRIPTO));

        fabPos.setOnClickListener(v -> sendCuentaRequest(Utils.POS));

    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();

        // on a background thread we get the total price of the bill
        App.executor.submit(new CuentaTotalPriceSetter(cuenta, this));

        // we listen for the presence status changes of the user
        userDoc.addSnapshotListener(this, (value, error) -> {
            if (error != null) {
                return;
            }
            assert value != null;
            userNombre = value.getString(Utils.KEY_NOMBRE);
            userMesa = value.getString(Utils.KEY_MESA);
            if (!value.getBoolean(Utils.IS_PRESENT)) {

                // when the user turns 'not present', we set CanSendPedidos back to true
                // throw the user to the MainActivity, clearing the stack
                Utils.setCanSendPedidos(getBaseContext(), true);
                startActivity(MainActivity.newIntent(getBaseContext()));
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
        hideEverything();
    }

    public void setupAdapter() {
        Query query = cuenta;
        FirestoreRecyclerOptions<ItemCuenta> options = new FirestoreRecyclerOptions.Builder<ItemCuenta>()
                .setQuery(query, ItemCuenta.class)
                .build();
        adapter = new AdapterCuenta(options);
        recCuenta.setHasFixedSize(false);
        recCuenta.setAdapter(adapter);
        recCuenta.setLayoutManager(new LinearLayoutManager(this));

    }

    private void sendCuentaRequest(String modo) {

        if (Utils.isConnected(getBaseContext())) {

            if (CuentaTotalPriceSetter.total > 0) {

                fMessaging.getToken().addOnSuccessListener(App.executor, s -> fMessaging.send(new RemoteMessage.Builder(App.SENDER_ID + "@fcm.googleapis.com")
                        .setMessageId(Utils.getMessageId())
                        .addData(Utils.KEY_TOKEN, s)
                        .addData(Utils.KEY_NOMBRE, userNombre)
                        .addData(Utils.KEY_MESA, userMesa)
                        .addData(Utils.KEY_MODO, modo)
                        .addData(Utils.KEY_ACTION, Utils.ACTION_CUENTA)
                        .addData(Utils.KEY_TYPE, Utils.TO_ADMIN)
                        .build()))
                        .addOnSuccessListener(s ->
                                {
                                    // after the message is sent, we set CanSendPedidos in the SharedPreference to false
                                    // which means the user can't send any other bill requests, nor can he send orders from the canasta
                                    Toast.makeText(CuentaActivity.this, getString(R.string.cuenta_enviada), Toast.LENGTH_SHORT).show();
                                    Utils.setCanSendPedidos(getBaseContext(), false);
                                }
                        );
                hideEverything();

            } else {
                Toast.makeText(CuentaActivity.this, getString(R.string.cuenta_vacia), Toast.LENGTH_SHORT).show();
            }

        } else {
            Toast.makeText(getBaseContext(), R.string.no_connection, Toast.LENGTH_LONG).show();
        }
    }

    private void hideEverything() {
        fabPedirCuenta.setImageResource(R.drawable.ic_pedir_cuenta);
        fabCrypto.hide();
        fabPedirCuenta.hide();
        fabCash.hide();
        fabPos.hide();
        fabYape.hide();
        txtPos.setVisibility(View.GONE);
        txtCash.setVisibility(View.GONE);
        txtCrypto.setVisibility(View.GONE);
        txtPedirCuenta.setVisibility(View.GONE);
        txtYape.setVisibility(View.GONE);
        areRootsVisible = false;
        areChildrenVisible = false;
        fabExpand.setImageResource(R.drawable.ic_expand);
    }

    private void init() {
        areRootsVisible = false;
        areChildrenVisible = false;
        String userId = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();

        String currentDate = Utils.getCurrentDate();
        txtMontoTotal = findViewById(R.id.txtMontoTotal);
        recCuenta = findViewById(R.id.recCuenta);
        fabMenu = findViewById(R.id.fabCuentaMenu);
        fabHome = findViewById(R.id.fabCuentaHome);
        fabExpand = findViewById(R.id.fabParentCuenta);
        fabCrypto = findViewById(R.id.fabCrypto);
        fabPedirCuenta = findViewById(R.id.fabPedirCuenta);
        fabCash = findViewById(R.id.fabCash);
        fabPos = findViewById(R.id.fabPos);
        txtCrypto = findViewById(R.id.txtCrypto);
        txtPedirCuenta = findViewById(R.id.txtPedirCuenta);
        txtCash = findViewById(R.id.txtCash);
        txtPos = findViewById(R.id.txtPos);
        txtYape = findViewById(R.id.txtYape);
        fabYape = findViewById(R.id.fabYape);
        userDoc = fStore.collection("usuarios")
                .document(userId);
        cuenta = fStore.collection("cuentas")
                .document(currentDate)
                .collection("cuentas corrientes")
                .document(userId)
                .collection("cuenta");

    }


}