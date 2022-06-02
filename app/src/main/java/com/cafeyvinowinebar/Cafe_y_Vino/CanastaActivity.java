package com.cafeyvinowinebar.Cafe_y_Vino;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cafeyvinowinebar.Cafe_y_Vino.Adapters.AdapterCanasta;
import com.cafeyvinowinebar.Cafe_y_Vino.POJOs.ItemCanasta;
import com.cafeyvinowinebar.Cafe_y_Vino.Runnables.CanastaSender;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Objects;

/**
 * Displays the current order as a RecyclerView
 * Order is represented by a LiveData of a list of ItemCanasta
 * User can send the order to the Administrator App, which will submit a new runnable to the executor
 */

public class CanastaActivity extends AppCompatActivity {

    private final FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private final FirebaseAuth fAuth = FirebaseAuth.getInstance();

    private FloatingActionButton fabUpload, fabCuenta, fabMenu, fabParent, fabInfo, fabInicio;
    private TextView txtPedido, txtBack, txtCuenta, txtInfo, txtInicio;
    String userId, userNombre, userMesa, currentDate;
    private RecyclerView recCanasta;
    public CanastaViewModel viewModel;
    private boolean isAllVisible;
    public Handler mainHandler;
    private DocumentReference userDoc;

    public static Intent newIntent(Context context) {
        return new Intent(context, CanastaActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_canasta);

        init();

        setUpAdapter();

        fabParent.setOnClickListener(v -> {
            if (!isAllVisible) {
                fabMenu.show();
                fabCuenta.show();
                fabUpload.show();
                fabInfo.show();
                fabInicio.show();
                txtBack.setVisibility(View.VISIBLE);
                txtCuenta.setVisibility(View.VISIBLE);
                txtPedido.setVisibility(View.VISIBLE);
                txtInicio.setVisibility(View.VISIBLE);
                txtInfo.setVisibility(View.VISIBLE);
                isAllVisible = true;
                fabParent.setImageResource(R.drawable.ic_collapse);
            } else {
                hideEverything();
            }
        });

        fabUpload.setOnClickListener(view -> {

            if (Utils.isConnected(getBaseContext())) {

                // get the list of all the items from the SQLiteDB
                List<ItemCanasta> canasta = viewModel.getAllItems().getValue();
                assert canasta != null;
                if (canasta.isEmpty()) {
                    Toast.makeText(CanastaActivity.this,
                            getString(R.string.vacia_canasta), Toast.LENGTH_SHORT).show();
                } else {

                    // after the user sends a bill request and before the bill is canceled, he can't send more orders
                    // so we make this check
                    if (Utils.getCanSendPedidos(getBaseContext())) {

                        // we send the order on a background thread
                        App.executor.submit(new CanastaSender(viewModel, getBaseContext(), mainHandler,
                                currentDate, userMesa, userNombre, userId, canasta));
                        hideEverything();
                    } else {
                        Toast.makeText(CanastaActivity.this,
                                getString(R.string.canasta_no_sending), Toast.LENGTH_SHORT).show();
                    }
                }

            } else {
                Toast.makeText(getBaseContext(), R.string.no_connection, Toast.LENGTH_LONG).show();
            }
        });

        fabMenu.setOnClickListener(v -> startActivity(MainMenuActivity.newIntent(getBaseContext())));

        fabCuenta.setOnClickListener(v -> startActivity(CuentaActivity.newIntent(getBaseContext())));

        fabInicio.setOnClickListener(v -> startActivity(new Intent(getBaseContext(), MainActivity.class)));

        fabInfo.setOnClickListener(v -> {

            View view = getLayoutInflater().inflate(R.layout.alert_info, null);
            TextView msg = view.findViewById(R.id.txtInfoMsg);
            msg.setText(R.string.message_canasta_info);
            new AlertDialog.Builder(CanastaActivity.this)
                    .setView(view)
                    .create().show();
            hideEverything();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        userDoc.addSnapshotListener(this, (value, error) -> {
            if (error != null) {
                return;
            }
            assert value != null;

            // when the user's status changes to 'not present', this means that his bill is canceled
            // so we throw him to the MainActivity and clean the stack to prevent the back navigation
            // and set the CanSendPedidos back to true in the SharedPreferences
            if (!value.getBoolean(Utils.IS_PRESENT)) {
                Utils.setCanSendPedidos(getBaseContext(), true);
                startActivity(MainActivity.newIntent(getBaseContext()));
            }

        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        hideEverything();
    }

    private void hideEverything() {
        fabMenu.hide();
        fabCuenta.hide();
        fabUpload.hide();
        fabInfo.hide();
        fabInicio.hide();
        txtBack.setVisibility(View.GONE);
        txtCuenta.setVisibility(View.GONE);
        txtPedido.setVisibility(View.GONE);
        txtInfo.setVisibility(View.GONE);
        txtInicio.setVisibility(View.GONE);
        fabParent.setImageResource(R.drawable.ic_expand);
        isAllVisible = false;
    }

    public void setUpAdapter() {

        recCanasta.setLayoutManager(new GridLayoutManager(this, 2));
        final AdapterCanasta adapterCanasta = new AdapterCanasta(viewModel, mainHandler, CanastaActivity.this);
        recCanasta.setAdapter(adapterCanasta);
        viewModel.getAllItems().observe(this, adapterCanasta::submitList);

    }

    private void init() {

        recCanasta = findViewById(R.id.recCanasta);
        viewModel = new ViewModelProvider(this).get(CanastaViewModel.class);
        mainHandler = new Handler();
        currentDate = Utils.getCurrentDate();
        fabUpload = findViewById(R.id.fabUpload);
        fabInicio = findViewById(R.id.fabInicio);
        fabCuenta = findViewById(R.id.fabCuenta);
        fabMenu = findViewById(R.id.fabMenu);
        fabParent = findViewById(R.id.fabParent);
        txtBack = findViewById(R.id.txtBack);
        txtInicio = findViewById(R.id.txtInicio);
        txtCuenta = findViewById(R.id.txtCuenta);
        txtPedido = findViewById(R.id.txtPedido);
        fabInfo = findViewById(R.id.fabInfo);
        txtInfo = findViewById(R.id.txtInfo);
        isAllVisible = false;
        userId = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
        userDoc = fStore.collection("usuarios").document(userId);
        userDoc.get().addOnSuccessListener(App.executor, snapshot -> {

            // we will need those values when we send the order to admins
            userNombre = snapshot.getString(Utils.KEY_NOMBRE);
            userMesa = snapshot.getString(Utils.KEY_MESA);

        });
    }

}