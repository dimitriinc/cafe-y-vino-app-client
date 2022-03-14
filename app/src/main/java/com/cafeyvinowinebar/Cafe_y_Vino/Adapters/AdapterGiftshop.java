package com.cafeyvinowinebar.Cafe_y_Vino.Adapters;

import android.content.Context;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cafeyvinowinebar.Cafe_y_Vino.App;
import com.cafeyvinowinebar.Cafe_y_Vino.POJOs.Gift;
import com.cafeyvinowinebar.Cafe_y_Vino.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class AdapterGiftshop extends FirestoreRecyclerAdapter<Gift, AdapterGiftshop.ViewHolder> {

    private final FirebaseStorage fStorage = FirebaseStorage.getInstance();

    private final Context context;
    private AdapterCategory.OnItemClickListener listener;
    private final Handler mainHandler;

    public AdapterGiftshop(@NonNull FirestoreRecyclerOptions<Gift> options, Context context, Handler mainHandler) {
        super(options);
        this.context = context;
        this.mainHandler = mainHandler;
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull Gift model) {
        holder.bind(model, context, mainHandler);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_gift, parent, false);
        return new ViewHolder(view);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgGift;
        TextView txtGift, txtGiftPrecio;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgGift = itemView.findViewById(R.id.imgGift);
            txtGift = itemView.findViewById(R.id.txtGift);
            txtGiftPrecio = itemView.findViewById(R.id.txtGiftPrecio);

            itemView.setOnClickListener(v -> {
                int position = getAbsoluteAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(getSnapshots().getSnapshot(position), position);
                }
            });
        }

        public void bind(Gift gift, Context context, Handler mainHandler) {
            txtGift.setText(gift.getNombre());
            txtGiftPrecio.setText(context.getString(R.string.gift_list_item_price, gift.getPrecio()));
            App.executor.submit(() -> {
                StorageReference reference = fStorage.getReference();
                StorageReference child = reference.child(gift.getImagen());
                mainHandler.post(() -> Glide.with(context).load(child).into(imgGift));
            });
        }
    }

    public void setOnItemClickListener(AdapterCategory.OnItemClickListener listener) {
        this.listener = listener;
    }
}
