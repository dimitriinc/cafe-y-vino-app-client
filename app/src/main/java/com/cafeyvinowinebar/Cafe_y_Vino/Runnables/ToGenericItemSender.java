package com.cafeyvinowinebar.Cafe_y_Vino.Runnables;

import android.content.Context;
import android.content.Intent;

import com.cafeyvinowinebar.Cafe_y_Vino.GenericCategoryActivity;
import com.cafeyvinowinebar.Cafe_y_Vino.GenericItemActivity;
import com.cafeyvinowinebar.Cafe_y_Vino.Utils;
import com.google.firebase.firestore.DocumentSnapshot;

import java.lang.ref.WeakReference;

public class ToGenericItemSender implements Runnable {

    private final DocumentSnapshot snapshot;
    private final WeakReference<GenericCategoryActivity> weakReference;
    private final Context context;

    public ToGenericItemSender(DocumentSnapshot snapshot, GenericCategoryActivity activity, Context context) {
        this.snapshot = snapshot;
        weakReference = new WeakReference<>(activity);
        this.context = context;
    }

    @Override
    public void run() {
        GenericCategoryActivity activity = weakReference.get();
        if (activity == null || activity.isFinishing()) {
            return;
        }
        String itemName = snapshot.getString(Utils.KEY_NOMBRE);
        String itemCategory = snapshot.getString(Utils.KEY_CATEGORIA);
        String itemPrice = snapshot.getString(Utils.PRECIO);
        String itemDescripcion = snapshot.getString(Utils.KEY_DESCRIPCION);
        String itemImage = snapshot.getString(Utils.KEY_IMAGE);
        String itemIcon = snapshot.getString(Utils.KEY_ICON);
        Intent intent = new Intent(context, GenericItemActivity.class);
        intent.putExtra(Utils.KEY_NAME, itemName);
        intent.putExtra(Utils.KEY_CATEGORY, itemCategory);
        intent.putExtra(Utils.KEY_DESCRIPCION, itemDescripcion);
        intent.putExtra(Utils.KEY_IMAGE, itemImage);
        intent.putExtra(Utils.PRICE, itemPrice);
        intent.putExtra(Utils.KEY_ICON, itemIcon);
        activity.startActivity(intent);
    }
}
