package com.cafeyvinowinebar.Cafe_y_Vino;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.cafeyvinowinebar.Cafe_y_Vino.POJOs.ItemCanasta;

import java.util.List;

/**
 * Available operations on the Canasta table
 */

@Dao
public interface CanastaDao {

    @Insert
    void insert(ItemCanasta item);

    @Delete
    void deleteItem(ItemCanasta item);

    // deletes all the items of the specified name
    @Query("DELETE FROM canasta WHERE name = :name")
    void deleteItemsByName(String name);

    @Query("SELECT * FROM canasta ORDER BY name ASC")
    LiveData<List<ItemCanasta>> getAllItems();

    // returns a list of all the items of the specified name
    @Query("SELECT * FROM canasta WHERE name = :name")
    List<ItemCanasta> getItemsByName(String name);

}
