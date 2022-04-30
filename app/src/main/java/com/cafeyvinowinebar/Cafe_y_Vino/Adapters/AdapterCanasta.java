package com.cafeyvinowinebar.Cafe_y_Vino.Adapters;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.cafeyvinowinebar.Cafe_y_Vino.App;
import com.cafeyvinowinebar.Cafe_y_Vino.CanastaViewModel;
import com.cafeyvinowinebar.Cafe_y_Vino.POJOs.ItemCanasta;
import com.cafeyvinowinebar.Cafe_y_Vino.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class AdapterCanasta extends ListAdapter<ItemCanasta, AdapterCanasta.ViewHolder> {

    private final FirebaseStorage fStorage = FirebaseStorage.getInstance();

    private final CanastaViewModel viewModel;
    private final Handler mainHandler;
    private final Context context;

    public AdapterCanasta(CanastaViewModel viewModel, Handler mainHandler, Context context) {
        super(DIFF_CALLBACK);
        this.viewModel = viewModel;
        this.mainHandler = mainHandler;
        this.context = context;
    }

    private static final DiffUtil.ItemCallback<ItemCanasta> DIFF_CALLBACK = new DiffUtil.ItemCallback<ItemCanasta>() {
        @Override
        public boolean areItemsTheSame(@NonNull ItemCanasta oldItem, @NonNull ItemCanasta newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull ItemCanasta oldItem, @NonNull ItemCanasta newItem) {
            return oldItem.getName().equals(newItem.getName()) &&
                    oldItem.getCategory().equals(newItem.getCategory()) &&
                    oldItem.getIcon().equals(newItem.getIcon()) &&
                    oldItem.getPrice() == newItem.getPrice();
        }
    };

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_canasta, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ItemCanasta item = getItem(position);
        holder.bind(item, context, mainHandler);
    }

    protected class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView txtCanastaItem;
        private final ImageView imgCanasta;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtCanastaItem = itemView.findViewById(R.id.txtCanastaItem);
            imgCanasta = itemView.findViewById(R.id.imgCanasta);

            itemView.setOnClickListener(v -> {
                ItemCanasta item = getItem(getAbsoluteAdapterPosition());
                viewModel.insert(new ItemCanasta(item.getName(), item.getCategory(), item.getIcon(), item.getPrice()));
            });

            itemView.setOnLongClickListener(v -> {
                viewModel.deleteItem(getItem(getAbsoluteAdapterPosition()));
                return true;
            });
        }

        public void bind(ItemCanasta item, Context context, Handler mainHandler) {
            txtCanastaItem.setText(item.getName());
            if (item.getIcon() != null) {
                App.executor.submit(() -> {
                    StorageReference reference = fStorage.getReference();
                    StorageReference child = reference.child(item.getIcon());
                    mainHandler.post(() -> Glide.with(context)
                            .load(child)
                            .into(imgCanasta));
                });
            } else {
                imgCanasta.setImageResource(R.drawable.logo_mini);
            }

        }
    }
}
