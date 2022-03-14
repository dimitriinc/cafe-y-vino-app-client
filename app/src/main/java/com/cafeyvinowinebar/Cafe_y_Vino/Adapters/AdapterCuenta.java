package com.cafeyvinowinebar.Cafe_y_Vino.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.cafeyvinowinebar.Cafe_y_Vino.POJOs.ItemCanasta;
import com.cafeyvinowinebar.Cafe_y_Vino.POJOs.ItemCuenta;
import com.cafeyvinowinebar.Cafe_y_Vino.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class AdapterCuenta extends FirestoreRecyclerAdapter<ItemCuenta, AdapterCuenta.CuentaViewHolder> {

    public AdapterCuenta(@NonNull FirestoreRecyclerOptions<ItemCuenta> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull AdapterCuenta.CuentaViewHolder holder, int position, @NonNull ItemCuenta model) {
        holder.bind(model);
    }

    @NonNull
    @Override
    public CuentaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_cuenta, parent, false);
        return new CuentaViewHolder(view);
    }

    static class CuentaViewHolder extends RecyclerView.ViewHolder {

        private final TextView cuentaCount, cuentaName, cuentaPrice, cuentaTotal;

        public CuentaViewHolder(@NonNull View itemView) {
            super(itemView);
            cuentaCount = itemView.findViewById(R.id.cuentaCount);
            cuentaName = itemView.findViewById(R.id.cuentaName);
            cuentaPrice = itemView.findViewById(R.id.cuentaPrice);
            cuentaTotal = itemView.findViewById(R.id.cuentaTotal);
        }

        public void bind(ItemCuenta item) {
            cuentaPrice.setText(String.valueOf(item.getPrice()));
            cuentaName.setText(item.getName());
            cuentaCount.setText(String.valueOf(item.getCount()));
            cuentaTotal.setText(String.valueOf(item.getTotal()));
        }


    }
}
