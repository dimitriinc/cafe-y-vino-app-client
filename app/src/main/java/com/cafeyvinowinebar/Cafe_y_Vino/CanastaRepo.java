package com.cafeyvinowinebar.Cafe_y_Vino;

import android.app.Application;

import androidx.lifecycle.LiveData;

import com.cafeyvinowinebar.Cafe_y_Vino.POJOs.ItemCanasta;

import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

/**
 * Defines the background operations on the canasta table declared in the dao interface
 */

public class CanastaRepo {

    private final CanastaDao canastaDao;
    private final LiveData<List<ItemCanasta>> canasta;

    public CanastaRepo(Application application) {
        CanastaDatabase database = CanastaDatabase.getInstance(application);
        canastaDao = database.canastaDao();
        canasta = canastaDao.getAllItems();
    }

    public void insert(ItemCanasta item) {
        App.executor.submit(new InsertRunnable(canastaDao, item));
    }

    public void deleteItem(ItemCanasta item) {
        App.executor.submit(new DeleteItemRunnable(canastaDao, item));
    }

    public void deleteItemsByName(String name) {
        App.executor.submit(new DeleteItemsByNameRunnable(canastaDao, name));
    }

    public LiveData<List<ItemCanasta>> getAllItems() {
        return canasta;
    }

    public List<ItemCanasta> getItemsByName(String name) {
        Future<List<ItemCanasta>> future = App.executor.submit(new GetItemsByNameCallable(canastaDao, name));
        try {
            return future.get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }
    }

    private static class InsertRunnable implements Runnable {

        private final CanastaDao canastaDao;
        private final ItemCanasta item;

        public InsertRunnable(CanastaDao canastaDao, ItemCanasta item) {
            this.canastaDao = canastaDao;
            this.item = item;
        }

        @Override
        public void run() {
            canastaDao.insert(item);
        }
    }

    private static class DeleteItemRunnable implements Runnable {

        private final CanastaDao canastaDao;
        private final ItemCanasta item;

        public DeleteItemRunnable(CanastaDao canastaDao, ItemCanasta item) {
            this.canastaDao = canastaDao;
            this.item = item;
        }

        @Override
        public void run() {
            canastaDao.deleteItem(item);
        }
    }

    private static class DeleteItemsByNameRunnable implements Runnable {
        private final CanastaDao canastaDao;
        private final String name;
        private DeleteItemsByNameRunnable(CanastaDao canastaDao, String name) {
            this.canastaDao = canastaDao;
            this.name = name;
        }

        @Override
        public void run() {
            canastaDao.deleteItemsByName(name);
        }
    }

    // since we want a new object returned from the background task, we use Callable instead of Runnable
    private static class GetItemsByNameCallable implements Callable<List<ItemCanasta>> {
        private final CanastaDao canastaDao;
        private final String name;
        private GetItemsByNameCallable(CanastaDao canastaDao, String name) {
            this.canastaDao = canastaDao;
            this.name = name;
        }

        @Override
        public List<ItemCanasta> call() throws Exception {
            return canastaDao.getItemsByName(name);
        }
    }
}
