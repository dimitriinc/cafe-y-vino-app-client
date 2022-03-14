package com.cafeyvinowinebar.Cafe_y_Vino;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.cafeyvinowinebar.Cafe_y_Vino.POJOs.ItemCanasta;

import java.util.List;

public class CanastaViewModel extends AndroidViewModel {

    private final CanastaRepo repo;
    private final LiveData<List<ItemCanasta>> canasta;

    public CanastaViewModel(@NonNull Application application) {
        super(application);
        repo = new CanastaRepo(application);
        canasta = repo.getAllItems();
    }

    public LiveData<List<ItemCanasta>> getAllItems() {
        return canasta;
    }

    public void insert(ItemCanasta item) {
        repo.insert(item);
    }

    public void deleteItem(ItemCanasta item) {
        repo.deleteItem(item);
    }

    public void deleteItemsByName(String name) {
        repo.deleteItemsByName(name);
    }

    public List<ItemCanasta> getItemsByName(String name) {
        return repo.getItemsByName(name);
    }
}
