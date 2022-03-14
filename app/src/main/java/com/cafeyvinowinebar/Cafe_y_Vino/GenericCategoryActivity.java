package com.cafeyvinowinebar.Cafe_y_Vino;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.cafeyvinowinebar.Cafe_y_Vino.Adapters.AdapterCategory;
import com.cafeyvinowinebar.Cafe_y_Vino.POJOs.ItemMenu;
import com.cafeyvinowinebar.Cafe_y_Vino.POJOs.ItemCanasta;
import com.cafeyvinowinebar.Cafe_y_Vino.Runnables.ToGenericItemSender;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.Objects;

/**
 * Displays the content of the menu category passed with the intent
 */

public class GenericCategoryActivity extends AppCompatActivity {

    private final FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore fStore = FirebaseFirestore.getInstance();

    private RecyclerView recGenCat;
    private AdapterCategory adapter;
    String catPath;
    String userId;
    String userNombre;
    String userMesa;
    String currentDate;
    CanastaViewModel viewModel;
    private FloatingActionButton fabParent, fabInfo, fabCanasta, fabHome;
    private TextView txtCanasta, txtInfo;
    private boolean isAllVisible;
    Handler mainHandler;

    public static Intent newIntent(Context context, String collectionPath) {
        Intent intent = new Intent(context, GenericCategoryActivity.class);
        intent.putExtra(Utils.CAT_PATH, collectionPath);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generic_category);

        init();

        // with the intent arrives the data about the category to display
        Intent intent = getIntent();
        if (intent != null) {
            catPath = intent.getStringExtra(Utils.CAT_PATH);
            if (!catPath.equals("")) {
                Query query = fStore.collection(catPath)
                        .whereEqualTo(Utils.IS_PRESENT, true);
                FirestoreRecyclerOptions<ItemMenu> options = new FirestoreRecyclerOptions.Builder<ItemMenu>()
                        .setQuery(query, ItemMenu.class)
                        .build();
                adapter = new AdapterCategory(options, mainHandler, GenericCategoryActivity.this);
                recGenCat.setAdapter(adapter);
                recGenCat.setLayoutManager(new LinearLayoutManager(this));
                adapter.setOnItemClickListener((documentSnapshot, position) ->

                        // on click we go to the GenericItemActivity via a runnable that retrieves data about the clicked product
                        // to pass it to the GenericItemActivity where it gets displayed
                        App.executor.submit(new ToGenericItemSender(documentSnapshot, GenericCategoryActivity.this, getBaseContext())));
            }
        }

        fabHome.setOnClickListener(v -> startActivity(new Intent(GenericCategoryActivity.this, MainActivity.class)));
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
        userId = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();

        // we listen to the presence status changes
        // and also retrieve some personal data while we're at it
        fStore.collection("usuarios")
                .document(userId)
                .addSnapshotListener(this, (value, error) -> {
                    if (error != null) {
                        return;
                    }
                    if (value != null && value.exists()) {
                        userNombre = value.getString(Utils.KEY_NOMBRE);
                        userMesa = value.getString(Utils.KEY_MESA);
                        if (value.getBoolean(Utils.IS_PRESENT)) {

                            // the user is present, he can go to the CanastaActivity
                            fabHome.setVisibility(View.GONE);
                            fabParent.setVisibility(View.VISIBLE);
                            fabParent.setOnClickListener(v -> {
                                if (!isAllVisible) {
                                    fabCanasta.show();
                                    fabInfo.show();
                                    txtInfo.setVisibility(View.VISIBLE);
                                    txtCanasta.setVisibility(View.VISIBLE);
                                    fabParent.setImageResource(R.drawable.ic_collapse);
                                    isAllVisible = true;
                                } else {
                                    fabCanasta.hide();
                                    fabInfo.hide();
                                    txtCanasta.setVisibility(View.GONE);
                                    txtInfo.setVisibility(View.GONE);
                                    fabParent.setImageResource(R.drawable.ic_expand);
                                    isAllVisible = false;
                                }
                            });
                            fabCanasta.setOnClickListener(v -> startActivity(CanastaActivity.newIntent(getBaseContext())));

                            fabInfo.setOnClickListener(v -> {
                                final AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
                                View view = getLayoutInflater().inflate(R.layout.alert_generic_cat_info, null);
                                builder.setView(view);
                                builder.setCancelable(true);
                                builder.create().show();
                                fabCanasta.hide();
                                fabInfo.hide();
                                txtCanasta.setVisibility(View.GONE);
                                txtInfo.setVisibility(View.GONE);
                                fabParent.setImageResource(R.drawable.ic_expand);
                                isAllVisible = false;

                            });

                            // in the 'present' status the user can add products to canasta by long clicking the item
                            adapter.setOnItemLongClickListener(((snapshot, position) -> {
                                App.executor.submit(new CanastaAdder(viewModel, snapshot));
                                Toast.makeText(getBaseContext(), getString(R.string.on_adding_item), Toast.LENGTH_SHORT).show();
                            }));
                        } else {

                            // in the 'not present' status the user sees only the Home button
                            Utils.setCanSendPedidos(getBaseContext(), true);
                            fabParent.setVisibility(View.GONE);
                            fabHome.setVisibility(View.VISIBLE);
                        }
                    }

                });
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
        fabCanasta.hide();
        fabInfo.hide();
        txtCanasta.setVisibility(View.GONE);
        txtInfo.setVisibility(View.GONE);
        fabParent.setImageResource(R.drawable.ic_expand);
        isAllVisible = false;
    }


    @SuppressLint("SimpleDateFormat")
    public void init() {
        recGenCat = findViewById(R.id.recGenCat);
        isAllVisible = false;
        fabCanasta = findViewById(R.id.fabGcCanasta);
        fabInfo = findViewById(R.id.fabGcInfo);
        fabParent = findViewById(R.id.fabGcParent);
        fabHome = findViewById(R.id.fabGcHome);
        txtCanasta = findViewById(R.id.txtGcCanasta);
        txtInfo = findViewById(R.id.txtGcInfo);
        mainHandler = new Handler();
        viewModel = new ViewModelProvider(this).get(CanastaViewModel.class);

        currentDate = Utils.getCurrentDate();
    }

    private static class CanastaAdder implements Runnable {

        private final CanastaViewModel viewModel;
        private final DocumentSnapshot snapshot;

        public CanastaAdder(CanastaViewModel viewModel, DocumentSnapshot snapshot) {
            this.viewModel = viewModel;
            this.snapshot = snapshot;
        }

        @Override
        public void run() {
            viewModel.insert(new ItemCanasta(snapshot.getString(Utils.KEY_NOMBRE),
                    snapshot.getString(Utils.KEY_CATEGORIA),
                    snapshot.getString(Utils.KEY_ICON),
                    Long.parseLong(snapshot.getString(Utils.PRECIO))));
        }
    }
}