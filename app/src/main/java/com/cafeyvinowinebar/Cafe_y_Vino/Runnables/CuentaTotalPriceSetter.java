package com.cafeyvinowinebar.Cafe_y_Vino.Runnables;

import com.cafeyvinowinebar.Cafe_y_Vino.CuentaActivity;
import com.cafeyvinowinebar.Cafe_y_Vino.Utils;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.lang.ref.WeakReference;

/**
 * Computes the bill total price
 * Sets it as a text on the screen
 */

public class CuentaTotalPriceSetter implements Runnable {

    private final CollectionReference cuenta;
    private final WeakReference<CuentaActivity> weakReference;
    public static long total;

    public CuentaTotalPriceSetter(CollectionReference cuenta, CuentaActivity activity) {
        this.cuenta = cuenta;
        weakReference = new WeakReference<>(activity);
    }

    @Override
    public void run() {
        CuentaActivity activity = weakReference.get();
        if (activity == null || activity.isFinishing()) {
            return;
        }
        cuenta.addSnapshotListener(activity, (value, error) -> {
            if (error != null) {
                return;
            }
            total = 0;
            assert value != null;
            for (QueryDocumentSnapshot doc : value) {
                long itemTotal = doc.getLong(Utils.TOTAL);
                total += itemTotal;
            }
            activity.runOnUiThread(() -> activity.txtMontoTotal.setText(String.valueOf(total)));
        });
    }
}
