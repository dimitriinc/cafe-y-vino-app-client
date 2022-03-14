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
import com.cafeyvinowinebar.Cafe_y_Vino.POJOs.ItemMenu;
import com.cafeyvinowinebar.Cafe_y_Vino.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class AdapterCategory extends FirestoreRecyclerAdapter<ItemMenu, AdapterCategory.ViewHolder> {

    private final FirebaseStorage storage = FirebaseStorage.getInstance();

    private OnItemLongClickListener longListener;
    private OnItemClickListener listener;
    private final Handler mainHandler;
    private final Context context;

    public AdapterCategory(@NonNull FirestoreRecyclerOptions<ItemMenu> options, Handler mainHandler, Context context) {
        super(options);
        this.mainHandler = mainHandler;
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull AdapterCategory.ViewHolder holder, int position, @NonNull ItemMenu model) {
        holder.bind(model, context, mainHandler);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_menu_item, parent, false);
        return new ViewHolder(view);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView txtItem;
        private final ImageView imgItem;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtItem = itemView.findViewById(R.id.txtItem);
            imgItem = itemView.findViewById(R.id.imgItem);

            itemView.setOnLongClickListener(v -> {
                int position = getAbsoluteAdapterPosition();
                if (position != RecyclerView.NO_POSITION && longListener != null) {
                    longListener.onItemLongClick(getSnapshots().getSnapshot(position), position);
                }
                return true;
            });

            itemView.setOnClickListener(v -> {
                int position = getAbsoluteAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(getSnapshots().getSnapshot(position), position);
                }
            });
        }

        public void bind(ItemMenu itemMenu, Context context, Handler mainHandler) {
            txtItem.setText(itemMenu.getNombre());
            App.executor.submit(() -> {
                StorageReference reference = storage.getReference();
                StorageReference child = reference.child(itemMenu.getImage());
                mainHandler.post(() -> Glide.with(context)
                        .load(child)
                        .into(imgItem));
            });
        }
    }

    public interface OnItemLongClickListener {
        void onItemLongClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemLongClickListener(OnItemLongClickListener longListener) {
        this.longListener = longListener;
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}
