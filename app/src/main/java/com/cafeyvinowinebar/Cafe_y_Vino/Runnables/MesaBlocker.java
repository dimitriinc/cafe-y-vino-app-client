package com.cafeyvinowinebar.Cafe_y_Vino.Runnables;

import android.view.Menu;

import com.cafeyvinowinebar.Cafe_y_Vino.App;
import com.cafeyvinowinebar.Cafe_y_Vino.R;
import com.cafeyvinowinebar.Cafe_y_Vino.Utils;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Objects;

/**
 * Goes through the stored reservations for the chosen date and part of day
 * Disables the reserved tables in the popup menu
 */

public class MesaBlocker implements Runnable{

    private final FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private final String date, part;
    private final Menu popupMenu;

    public MesaBlocker(String date, String part, Menu popupMenu) {
        this.date = date;
        this.part = part;
        this.popupMenu = popupMenu;
    }

    @Override
    public void run() {

        if (part.equals(Utils.DIA)) {

            fStore.collection("reservas")
                    .document(date)
                    .collection(Utils.DIA)
                    .get()
                    .addOnSuccessListener(App.executor, queryDocumentSnapshots -> {

                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {

                            if (document.exists()) {

                                switch (document.getId()) {
                                    case "01":
                                        popupMenu.findItem(R.id.m01).setEnabled(false);
                                        break;
                                    case "02":
                                        popupMenu.findItem(R.id.m02).setEnabled(false);
                                        break;
                                    case "03":
                                        popupMenu.findItem(R.id.m03).setEnabled(false);
                                        break;
                                    case "04":
                                        popupMenu.findItem(R.id.m04).setEnabled(false);
                                        break;
                                    case "05":
                                        popupMenu.findItem(R.id.m05).setEnabled(false);
                                        break;
                                    case "06":
                                        popupMenu.findItem(R.id.m06).setEnabled(false);
                                        break;
                                    case "07":
                                        popupMenu.findItem(R.id.m07).setEnabled(false);
                                        break;
                                    case "08":
                                        popupMenu.findItem(R.id.m08).setEnabled(false);
                                        break;
                                    case "09":
                                        popupMenu.findItem(R.id.m09).setEnabled(false);
                                        break;
                                    case "10":
                                        popupMenu.findItem(R.id.m10).setEnabled(false);
                                        break;
                                    case "11":
                                        popupMenu.findItem(R.id.m11).setEnabled(false);
                                        break;
                                    case "12":
                                        popupMenu.findItem(R.id.m12).setEnabled(false);
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }
                    });

        }
        if (part.equals(Utils.NOCHE)) {
            fStore.collection("reservas")
                    .document(date)
                    .collection(Utils.NOCHE)
                    .get()
                    .addOnSuccessListener(App.executor, queryDocumentSnapshots -> {

                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {

                            if (document.exists()) {

                                switch (document.getId()) {
                                    case "01":
                                        popupMenu.findItem(R.id.m01).setEnabled(false);
                                        break;
                                    case "02":
                                        popupMenu.findItem(R.id.m02).setEnabled(false);
                                        break;
                                    case "03":
                                        popupMenu.findItem(R.id.m03).setEnabled(false);
                                        break;
                                    case "04":
                                        popupMenu.findItem(R.id.m04).setEnabled(false);
                                        break;
                                    case "05":
                                        popupMenu.findItem(R.id.m05).setEnabled(false);
                                        break;
                                    case "06":
                                        popupMenu.findItem(R.id.m06).setEnabled(false);
                                        break;
                                    case "07":
                                        popupMenu.findItem(R.id.m07).setEnabled(false);
                                        break;
                                    case "08":
                                        popupMenu.findItem(R.id.m08).setEnabled(false);
                                        break;
                                    case "09":
                                        popupMenu.findItem(R.id.m09).setEnabled(false);
                                        break;
                                    case "10":
                                        popupMenu.findItem(R.id.m10).setEnabled(false);
                                        break;
                                    case "11":
                                        popupMenu.findItem(R.id.m11).setEnabled(false);
                                        break;
                                    case "12":
                                        popupMenu.findItem(R.id.m12).setEnabled(false);
                                        break;
                                    default:
                                        break;
                                }
                            }
                        }
                    });
        }
    }
}
