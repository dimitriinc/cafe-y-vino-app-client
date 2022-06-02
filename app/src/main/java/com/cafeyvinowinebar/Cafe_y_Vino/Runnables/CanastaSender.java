package com.cafeyvinowinebar.Cafe_y_Vino.Runnables;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.cafeyvinowinebar.Cafe_y_Vino.App;
import com.cafeyvinowinebar.Cafe_y_Vino.CanastaViewModel;
import com.cafeyvinowinebar.Cafe_y_Vino.POJOs.ItemCanasta;
import com.cafeyvinowinebar.Cafe_y_Vino.POJOs.ItemPedido;
import com.cafeyvinowinebar.Cafe_y_Vino.R;
import com.cafeyvinowinebar.Cafe_y_Vino.Utils;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * Creates an order document and a collection of its products in the FirestoreDB
 * To do that, we convert the list of ItemCanasta to a list of ItemPedido
 * We iterate through that list, adding each object to the pedido collection in the FirestoreDB
 * And then we fill the document containing the collection with some metadata
 */

public class CanastaSender implements Runnable {

    private static final String TAG = "CanastaSender";

    private final FirebaseFirestore fStore = FirebaseFirestore.getInstance();
    private final FirebaseMessaging fMessaging = FirebaseMessaging.getInstance();

    // this int value represents the id of the meta document
    private int count;

    private final CanastaViewModel viewModel;
    private final Context context;
    private final Handler mainHandler;
    private final String currentDate;
    private final String mesa;
    private final String userName;
    private final String userId;
    private final List<ItemCanasta> canasta;

    public CanastaSender(CanastaViewModel viewModel, Context context, Handler mainHandler,
                         String currentDate, String mesa, String userName,
                         String userId, List<ItemCanasta> canasta) {
        this.viewModel = viewModel;
        this.context = context;
        this.mainHandler = mainHandler;
        this.currentDate = currentDate;
        this.mesa = mesa;
        this.userName = userName;
        this.userId = userId;
        this.canasta = canasta;
    }

    @Override
    public void run() {

        // create and store a random id for the meta doc
        String metaDocId = String.valueOf(new Random().nextLong());

        // the destination collection that will contain the products of the order
        CollectionReference pedido = fStore.collection("pedidos")
                .document(currentDate)
                .collection("pedidos enviados")
                .document(metaDocId)
                .collection("pedido");

        // the document that hosts the pedido collection and stores the metadata about the order
        DocumentReference metaDoc = fStore.collection("pedidos")
                .document(currentDate)
                .collection("pedidos enviados")
                .document(metaDocId);

        Set<ItemPedido> pedidoToSend = new HashSet<>();

        // we need to know if the order contains only products of kitchen, or only products of bar, or both
        // we need to know that because of how the Administrator App manages the display of orders
        // for that we deploy boolean values
        boolean servidoBarra = true;
        boolean servidoCocina = true;

        // in the ItemCanasta list all products have their own object, including the same products
        // in the ItemPedido list one product will have one object with an itemCount field for quantity
        for (ItemCanasta item : canasta) {

            // to know the quantity of the product we query for how many products of the same name are there in the canasta
            long itemCount = viewModel.getItemsByName(item.getName()).size();

            // we delete all the objects of that name from the SQLiteDB
            viewModel.deleteItemsByName(item.getName());
            if (itemCount != 0) {

                // we create a new ItemPedido and add it to the pedido list
                pedidoToSend.add(new ItemPedido(item.getName(), itemCount, item.getPrice(), item.getCategory()));
            }
        }

        for (ItemPedido pedidoItem : pedidoToSend) {

            // we add the object to the destination collection
            pedido.add(pedidoItem);

            // if at least one product in the order is of bar or kitchen category
            // we mark the set of that category as not served
            // those boolean values will go into the meta doc data
            if (pedidoItem.getCategory().equals(Utils.BARRA)) {
                servidoBarra = false;
            } else {
                servidoCocina = false;
            }
        }

        // at this point the destination collection is set, the order disappears from the canasta screen
        // and we thank the user for sending the order
        mainHandler.post(() -> Toast.makeText(context, context.getString(R.string.canasta_on_sending_pedido), Toast.LENGTH_SHORT).show());

        // create a meta object and set it to the meta doc
        Map<String, Object> pedidoMetaDoc = new HashMap<>();
        pedidoMetaDoc.put(Utils.KEY_IS_EXPANDED, false);
        pedidoMetaDoc.put(Utils.KEY_MESA, mesa);
        pedidoMetaDoc.put(Utils.SERVIDO, false);
        pedidoMetaDoc.put(Utils.SERVIDO_BARRA, servidoBarra);
        pedidoMetaDoc.put(Utils.SERVIDO_COCINA, servidoCocina);
        pedidoMetaDoc.put(Utils.KEY_USER, userName);
        pedidoMetaDoc.put(Utils.KEY_USER_ID, userId);
        pedidoMetaDoc.put(Utils.KEY_TIMESTAMP, new Timestamp(new Date()));

        metaDoc.set(pedidoMetaDoc);

        // notify the admins about the new order
        fStore.collection("administradores").get()
                .addOnSuccessListener(App.executor, queryDocumentSnapshots -> {

                    for (QueryDocumentSnapshot admin : queryDocumentSnapshots) {

                        String adminToken = admin.getString(Utils.KEY_TOKEN);
                        fMessaging.getToken().addOnSuccessListener(App.executor, s ->
                                fMessaging.send(new RemoteMessage.Builder(App.SENDER_ID + "@fcm.googleapis.com")
                                        .setMessageId(Utils.getMessageId())
                                        .addData(Utils.KEY_MESA, mesa)
                                        .addData(Utils.KEY_TOKEN, s)
                                        .addData(Utils.KEY_ADMIN_TOKEN, adminToken)
                                        .addData(Utils.KEY_NOMBRE, userName)
                                        .addData(Utils.KEY_FECHA, currentDate)
                                        .addData(Utils.KEY_META_ID, metaDocId)
                                        .addData(Utils.KEY_ACTION, Utils.ACTION_PEDIDO)
                                        .addData(Utils.KEY_TYPE, Utils.TO_ADMIN_NEW)
                                        .build()));
                    }
                });
    }
}