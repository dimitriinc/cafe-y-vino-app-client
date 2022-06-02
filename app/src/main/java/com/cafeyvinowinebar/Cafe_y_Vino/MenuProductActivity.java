package com.cafeyvinowinebar.Cafe_y_Vino;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.view.View;

import com.cafeyvinowinebar.Cafe_y_Vino.POJOs.ItemMenu;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MenuProductActivity extends AppCompatActivity {

    public static final String COLLECTION = "collection";
    public static final String POSITION = "position";

    private final FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private final FirebaseAuth fAuth = FirebaseAuth.getInstance();

    CanastaViewModel viewModel;
    private String userId;
    int initialPosition;

    public static Intent newIntent(Context context, Bundle bundle, String initialNombre) {
        Intent intent = new Intent(context, MenuProductActivity.class);
        intent.putExtra(COLLECTION, bundle);
        intent.putExtra(POSITION, initialNombre);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_product);

        ViewPager2 viewPager = findViewById(R.id.menuProductPager);
        userId = fAuth.getUid();
        viewModel = new ViewModelProvider(this).get(CanastaViewModel.class);

        List<ItemMenu> products = (ArrayList<ItemMenu>) (getIntent().getBundleExtra(COLLECTION).getSerializable(Utils.KEY_CATEGORIA));
        String initialNombre = getIntent().getStringExtra(POSITION);
        for (ItemMenu product : products) {
            if (product.getNombre().equals(initialNombre)) {
                initialPosition = products.indexOf(product);
                break;
            }
        }

        viewPager.setAdapter(new FragmentStateAdapter(MenuProductActivity.this) {
            @NonNull
            @Override
            public Fragment createFragment(int position) {
                return new MenuProductFragment(products.get(position), viewModel, position, products.size());
            }

            @Override
            public int getItemCount() {
                return products.size();
            }

        });
        viewPager.setCurrentItem(initialPosition);
        viewPager.setPageTransformer(new ZoomOutPageTransformer());


    }

    @Override
    protected void onStart() {
        super.onStart();
        fStore.collection("usuarios").document(userId).addSnapshotListener(this, (value, error) -> {
            if (error != null) {
                return;
            }
            if (value != null && value.exists()) {

                Utils.setIsUserPresent(MenuProductActivity.this, Boolean.TRUE.equals(value.getBoolean(Utils.IS_PRESENT)));

            }
        });
    }

    private static class ZoomOutPageTransformer implements ViewPager2.PageTransformer {
        private static final float MIN_SCALE = 0.85f;
        private static final float MIN_ALPHA = 0.5f;

        @Override
        public void transformPage(@NonNull View page, float position) {
            int pageWidth = page.getWidth();
            int pageHeigth = page.getHeight();

            if (position < -1) {
                page.setAlpha(0f);
            } else if (position <= 1) {
                float scaleFactor = Math.max(MIN_SCALE, 1 - Math.abs(position));
                float vertMargin = pageHeigth * (1 - scaleFactor) / 2;
                float horzMargin = pageWidth * (1 - scaleFactor) / 2;
                if (position < 0) {
                    page.setTranslationX(horzMargin - vertMargin / 2);
                } else {
                    page.setTranslationX(-horzMargin + vertMargin / 2);
                }

                page.setScaleX(scaleFactor);
                page.setScaleY(scaleFactor);

                page.setAlpha(MIN_ALPHA +
                        (scaleFactor - MIN_SCALE) /
                                (1 - MIN_SCALE) * (1 - MIN_ALPHA));
            } else {
                page.setAlpha(0f);
            }
        }
    }

}