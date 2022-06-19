package com.cafeyvinowinebar.Cafe_y_Vino;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.cafeyvinowinebar.Cafe_y_Vino.Adapters.AdapterGiftshop;
import com.cafeyvinowinebar.Cafe_y_Vino.POJOs.Gift;
import com.cafeyvinowinebar.Cafe_y_Vino.Runnables.GiftSender;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

/**
 * Displays a list of possible gifts in the fidelity program
 * When the user chooses and confirms one of them, send a message to the Administrator App
 */
public class GiftshopDialogFragment extends DialogFragment {

    private final FirebaseFirestore fStore = FirebaseFirestore.getInstance();

    private AdapterGiftshop adapter;
    private String userId;
    private Handler mainHandler;

    public GiftshopDialogFragment(String userId, Handler mainHandler) {
        this.userId = userId;
        this.mainHandler = mainHandler;
    }

    public GiftshopDialogFragment() {}

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            userId = savedInstanceState.getString(Utils.KEY_USER_ID);
            mainHandler = new Handler(requireActivity().getMainLooper());
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        String fecha = Utils.getCurrentDate();
        Context context = requireContext();

        RecyclerView recGiftshop = new RecyclerView(context);
        Query query = fStore.collection("regalos").whereEqualTo(Utils.IS_PRESENT, true);
        FirestoreRecyclerOptions<Gift> options = new FirestoreRecyclerOptions.Builder<Gift>()
                .setQuery(query, Gift.class)
                .build();
        adapter = new AdapterGiftshop(options, context, mainHandler);
        adapter.setOnItemClickListener((documentSnapshot, position) -> {

            if (Utils.isConnected(context)) {

                App.executor.submit(new GiftSender(documentSnapshot, context, userId, mainHandler, fecha));

            } else {
                Toast.makeText(context, R.string.no_connection, Toast.LENGTH_LONG).show();
            }
        });
        recGiftshop.setAdapter(adapter);
        recGiftshop.setLayoutManager(new GridLayoutManager(context, 2));
        return recGiftshop;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(Utils.KEY_USER_ID, userId);
    }
}
