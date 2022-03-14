package com.cafeyvinowinebar.Cafe_y_Vino;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import com.cafeyvinowinebar.Cafe_y_Vino.Adapters.AdapterMainMenu;
import com.cafeyvinowinebar.Cafe_y_Vino.POJOs.MenuCategory;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.time.LocalDate;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

/**
 * Displays the categories of the menu in the form of a RecyclerView
 */
public class MainMenuActivity extends AppCompatActivity {

    private final FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private final FirebaseAuth fAuth = FirebaseAuth.getInstance();

    private AdapterMainMenu adapter;
    String userId, name, catPath;
    Handler handler;
    private RecyclerView recCategories;
    private FloatingActionButton fabHome;


    public static Intent newIntent(Context context) {
        return new Intent(context, MainMenuActivity.class);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        init();

        setupAdapter();

        fabHome.setOnClickListener(v -> startActivity(new Intent(getBaseContext(), MainActivity.class)));
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private void init() {
        recCategories = findViewById(R.id.recCategories);
        fabHome = findViewById(R.id.fabHome);
        handler = new Handler();
    }

    private void setupAdapter() {
        Query query = fStore.collection("menu");
        FirestoreRecyclerOptions<MenuCategory> options = new FirestoreRecyclerOptions.Builder<MenuCategory>()
                .setQuery(query, MenuCategory.class)
                .build();
        adapter = new AdapterMainMenu(options, handler, this);
        recCategories.setAdapter(adapter);
        recCategories.setLayoutManager(new LinearLayoutManager(this));

        adapter.setOnItemClickListener((documentSnapshot, position) -> {

            // on click the user is directed to the GenericCategory to see the products of the chosen category
            // there are two special cases: wines have their own activity due to their subcollections of different types of wines
            // and the happy hour items (ofertas) must be available only during the time of happy hour, and if the user is in the 'not present' status
            name = documentSnapshot.getString(Utils.KEY_NAME);
            catPath = documentSnapshot.getString(Utils.CAT_PATH);

            switch (name) {

                case "Ofertas":

                    userId = Objects.requireNonNull(fAuth.getCurrentUser()).getUid();
                    fStore.collection("usuarios")
                            .document(userId)
                            .get()
                            .addOnSuccessListener(App.executor, userSnapshot -> {

                                // in the 'not present' status the user can't add an item to his canasta
                                // so he's allowed to review what's available
                                if (!userSnapshot.getBoolean("isPresent")) {
                                    startActivity(GenericCategoryActivity.newIntent(getBaseContext(), catPath));
                                } else {

                                    // in the 'present' status first we check if the current day is the day when happy hour is active
                                    String day = LocalDate.now().getDayOfWeek().name();

                                    fStore.collection("utils").document("horas").get()
                                            .addOnSuccessListener(App.executor, utilsSnapshot -> {

                                                List<String> daysOfHappyHour = (List<String>) utilsSnapshot.get("dias de happy hour");
                                                assert daysOfHappyHour != null;
                                                for (String dayOfHappyHour : daysOfHappyHour) {
                                                    if (day.equals(dayOfHappyHour)) {

                                                        // on the current day happy hour is available, we check is the current hour is a happy one
                                                        Calendar calendar = Calendar.getInstance();
                                                        calendar.setTimeZone(TimeZone.getTimeZone(Utils.GMT));
                                                        int hour = calendar.get(Calendar.HOUR_OF_DAY);

                                                        List<Long> horas = (List<Long>) utilsSnapshot.get("horas de happy hour");
                                                        assert horas != null;
                                                        for (Long hora : horas) {

                                                            // and if it is, we allow the user to enter
                                                            if (hora == hour) {
                                                                startActivity(GenericCategoryActivity.newIntent(getBaseContext(), catPath));
                                                                return;
                                                            }
                                                        }
                                                        handler.post(() -> Toast.makeText(MainMenuActivity.this,
                                                                R.string.main_menu_ofertas_404, Toast.LENGTH_LONG).show());
                                                        break;
                                                    }
                                                }

                                                // the current day is not in the array of happy hour days
                                                // the user can't enter
                                                handler.post(() -> Toast.makeText(MainMenuActivity.this,
                                                        R.string.main_menu_ofertas_404, Toast.LENGTH_LONG).show());
                                            });
                                }
                            });
                    break;

                case "Vinos":

                    startActivity(new Intent(getBaseContext(), VinosMenuActivity.class));
                    break;

                default:

                    startActivity(GenericCategoryActivity.newIntent(getBaseContext(), catPath));
                    break;

            }

        });
    }

}