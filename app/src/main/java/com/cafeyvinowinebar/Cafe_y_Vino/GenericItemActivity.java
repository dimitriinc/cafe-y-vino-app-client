package com.cafeyvinowinebar.Cafe_y_Vino;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import com.cafeyvinowinebar.Cafe_y_Vino.POJOs.ItemCanasta;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

/**
 * Displays the data about the product
 * In the 'present' status the user can add the product to their order
 */

public class GenericItemActivity extends AppCompatActivity {

    private final FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private final FirebaseAuth fAuth = FirebaseAuth.getInstance();
    private final FirebaseStorage fStorage = FirebaseStorage.getInstance();

    private CanastaViewModel viewModel;
    private FloatingActionButton fabCanasta, fabAdd, fabMainMenu, fabExpand, fabHome;
    private ImageView imgItem;
    private TextView txtDesc, txtPrecio, txtPrecioInt, txtItemName, txtAgregar, txtMenu, txtCanasta;
    private String userId;
    private String itemName;
    private String itemPrice;
    private String itemCategory;
    private String itemIcon;
    private boolean isAllVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generic_item);

        init();

        Intent intent = getIntent();
        if (intent != null) {

            // initialize the member vars with the data from the intent
            itemName = intent.getStringExtra(Utils.KEY_NAME);
            String itemDescripcion = intent.getStringExtra(Utils.KEY_DESCRIPCION);
            String itemImage = intent.getStringExtra(Utils.KEY_IMAGE);
            itemPrice = intent.getStringExtra(Utils.PRICE);
            itemCategory = intent.getStringExtra(Utils.KEY_CATEGORY);
            itemIcon = intent.getStringExtra(Utils.KEY_ICON);

            // set the data on the screen's views
            StorageReference reference = fStorage.getReference();
            StorageReference imgRef = reference.child(itemImage);
            Glide.with(this).load(imgRef).into(imgItem);
            txtItemName.setText(itemName);
            txtDesc.setText(itemDescripcion);
            txtPrecioInt.setText(itemPrice);
            txtPrecio.setVisibility(View.VISIBLE);

        }

        fabExpand.setOnClickListener(v -> {
            if (!isAllVisible) {
                fabAdd.show();
                fabCanasta.show();
                fabMainMenu.show();
                txtAgregar.setVisibility(View.VISIBLE);
                txtMenu.setVisibility(View.VISIBLE);
                txtCanasta.setVisibility(View.VISIBLE);
                fabExpand.setImageResource(R.drawable.ic_collapse);
                isAllVisible = true;
            } else {
                hideEverything();
            }
        });

        // insert a new object into the SQLiteDB
        fabAdd.setOnClickListener(v -> {
            viewModel.insert(new ItemCanasta(itemName, itemCategory, itemIcon, Long.parseLong(itemPrice)));
            Toast.makeText(this, getString(R.string.on_adding_item), Toast.LENGTH_SHORT).show();
        });

        fabCanasta.setOnClickListener(v -> startActivity(CanastaActivity.newIntent(getBaseContext())));

        fabMainMenu.setOnClickListener(v -> startActivity(MainMenuActivity.newIntent(getBaseContext())));

        // don't use the static newIntent(), 'cause we want the user to be able to navigate back
        fabHome.setOnClickListener(v -> startActivity(new Intent(getBaseContext(), MainActivity.class)));

    }

    @Override
    protected void onStart() {
        super.onStart();

        // listens to the boolean value of presence from the current user doc
        // based on the value decides the visibility of the floatExpand
        final DocumentReference document = fStore.collection("usuarios").document(userId);
        document.addSnapshotListener(this, (value, error) -> {
            if (error != null) {
                return;
            }
            if (value != null && value.exists()) {
                if (value.getBoolean(Utils.IS_PRESENT)) {
                    fabHome.setVisibility(View.GONE);
                    fabExpand.setVisibility(View.VISIBLE);
                } else {
                    Utils.setCanSendPedidos(getBaseContext(), true);
                    fabExpand.setVisibility(View.GONE);
                    fabHome.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        hideEverything();
    }

    private void init() {
        viewModel = new ViewModelProvider(this).get(CanastaViewModel.class);
        fabCanasta = findViewById(R.id.fabCanasta);
        fabAdd = findViewById(R.id.fabAdd);
        fabExpand = findViewById(R.id.fabExpand);
        fabHome = findViewById(R.id.fabGiHome);
        fabMainMenu = findViewById(R.id.fabMainMenu);
        imgItem = findViewById(R.id.imgItem);
        txtAgregar = findViewById(R.id.txtAgregar);
        txtMenu = findViewById(R.id.txtMenu);
        txtCanasta = findViewById(R.id.txtCanasta);
        txtDesc = findViewById(R.id.txtDesc);
        txtPrecio = findViewById(R.id.txtPrecio);
        txtPrecioInt = findViewById(R.id.txtPrecioInt);
        txtItemName = findViewById(R.id.txtItemName);
        userId = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
        isAllVisible = false;
    }

    private void hideEverything() {
        fabAdd.hide();
        fabCanasta.hide();
        fabMainMenu.hide();
        txtAgregar.setVisibility(View.GONE);
        txtMenu.setVisibility(View.GONE);
        txtCanasta.setVisibility(View.GONE);
        fabExpand.setImageResource(R.drawable.ic_expand);
        isAllVisible = false;
    }
}