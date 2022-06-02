package com.cafeyvinowinebar.Cafe_y_Vino;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import com.cafeyvinowinebar.Cafe_y_Vino.POJOs.ItemCanasta;
import com.cafeyvinowinebar.Cafe_y_Vino.POJOs.ItemMenu;
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

public class MenuProductFragment extends Fragment {

    private final FirebaseStorage fStorage = FirebaseStorage.getInstance();

    private final CanastaViewModel viewModel;
    private FloatingActionButton fabCanasta, fabAdd, fabMainMenu, fabExpand, fabHome;
    private ImageView imgItem, imgLeft, imgRight;
    private TextView txtDesc, txtPrecio, txtPrecioInt, txtItemName, txtAgregar, txtMenu, txtCanasta;
    private final ItemMenu product;
    private boolean isAllVisible;
    private boolean isUserPresent;
    private final int position, setSize;

    public MenuProductFragment(ItemMenu product, CanastaViewModel viewModel, int position, int setSize) {
        this.product = product;
        this.viewModel = viewModel;
        this.position = position;
        this.setSize = setSize;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_menu_product, container, false);

        init(view);

        String productName = product.getNombre();
        String productDescription = product.getDescripcion();
        String productImage = product.getImage();
        String productPrice = product.getPrecio();
        String productCategory = product.getCategoria();
        String productIcon = product.getIcon();

        // set the data on the screen's views
        if (productImage != null) {
            StorageReference reference = fStorage.getReference();
            StorageReference imgRef = reference.child(productImage);
            Glide.with(this).load(imgRef).into(imgItem);
        } else {
            imgItem.setImageResource(R.drawable.logo_stand_in);
        }

        if (productDescription == null || productDescription.trim().isEmpty()) {
            txtDesc.setText(R.string.no_desc);
        } else {
            txtDesc.setText(productDescription);
        }
        txtItemName.setText(productName);
        txtPrecioInt.setText(productPrice);
        txtPrecio.setVisibility(View.VISIBLE);

        if (position == 0) {
            imgLeft.setVisibility(View.GONE);
        } else if (position == setSize - 1) {
            imgRight.setVisibility(View.GONE);
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
            viewModel.insert(new ItemCanasta(productName, productCategory, productIcon, Long.parseLong(productPrice)));
            Toast.makeText(requireContext(), getString(R.string.on_adding_item), Toast.LENGTH_SHORT).show();
        });

        fabCanasta.setOnClickListener(v -> startActivity(CanastaActivity.newIntent(requireContext())));

        fabMainMenu.setOnClickListener(v -> startActivity(MainMenuActivity.newIntent(requireContext())));

        // don't use the static newIntent(), 'cause we want the user to be able to navigate back
        fabHome.setOnClickListener(v -> startActivity(new Intent(requireContext(), MainActivity.class)));
        return view;
    }

    @Override
    public void onPause() {
        super.onPause();
        hideEverything();
    }

    private void init(View view) {

        isUserPresent = PreferenceManager.getDefaultSharedPreferences(requireContext())
                .getBoolean(Utils.IS_PRESENT, false);

        fabCanasta = view.findViewById(R.id.fabCanasta);
        fabAdd = view.findViewById(R.id.fabAdd);
        fabExpand = view.findViewById(R.id.fabExpand);
        fabHome = view.findViewById(R.id.fabGiHome);
        fabMainMenu = view.findViewById(R.id.fabMainMenu);
        imgItem = view.findViewById(R.id.imgItem);
        txtAgregar = view.findViewById(R.id.txtAgregar);
        txtMenu = view.findViewById(R.id.txtMenu);
        txtCanasta = view.findViewById(R.id.txtCanasta);
        txtDesc = view.findViewById(R.id.txtDesc);
        txtPrecio = view.findViewById(R.id.txtPrecio);
        txtPrecioInt = view.findViewById(R.id.txtPrecioInt);
        txtItemName = view.findViewById(R.id.txtItemName);
        imgLeft = view.findViewById(R.id.arrow_left);
        imgRight = view.findViewById(R.id.arrow_right);
        isAllVisible = false;

        if (isUserPresent) {
            fabHome.setVisibility(View.GONE);
            fabExpand.setVisibility(View.VISIBLE);
        } else {
            Utils.setCanSendPedidos(requireContext(), true);
            fabExpand.setVisibility(View.GONE);
            fabHome.setVisibility(View.VISIBLE);
        }
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