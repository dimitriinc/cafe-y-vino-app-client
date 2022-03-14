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
import com.cafeyvinowinebar.Cafe_y_Vino.POJOs.MenuCategory;
import com.cafeyvinowinebar.Cafe_y_Vino.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class AdapterMainMenu extends FirestoreRecyclerAdapter<MenuCategory, AdapterMainMenu.ViewHolder> {

    private final FirebaseStorage storage = FirebaseStorage.getInstance();
    private AdapterCategory.OnItemClickListener listener;
    private final Handler mainHandler;
    private final Context context;

    public AdapterMainMenu(@NonNull FirestoreRecyclerOptions<MenuCategory> options, Handler mainHandler, Context context) {
        super(options);
        this.mainHandler = mainHandler;
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull AdapterMainMenu.ViewHolder holder, int position, @NonNull MenuCategory model) {
        holder.bind(model, context, mainHandler);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_menu, parent, false);
        return new ViewHolder(view);
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView txtCategoryName;
        private final ImageView imgCategory;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            txtCategoryName = itemView.findViewById(R.id.txtCategoryName);
            imgCategory = itemView.findViewById(R.id.imgCategory);

            itemView.setOnClickListener(v -> {
                int position = getAbsoluteAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    listener.onItemClick(getSnapshots().getSnapshot(position), position);
                }

            });
        }

        public void bind(MenuCategory menuCategory, Context context, Handler mainHandler) {
            txtCategoryName.setText(menuCategory.getName());
            App.executor.submit(() -> {
                StorageReference reference = storage.getReference();
                StorageReference spaceRef = reference.child(menuCategory.getImage());
                mainHandler.post(() -> Glide.with(context)
                        .load(spaceRef)
                        .into(imgCategory));
            });
        }
    }

    public void setOnItemClickListener(AdapterCategory.OnItemClickListener listener) {
        this.listener = listener;
    }
}
