package ru.magflayer.spectrum.data.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import ru.magflayer.spectrum.data.entity.ColorName;

@Dao
public interface ColorNameDao {

    @Query("SELECT * FROM ColorName")
    List<ColorName> loadColorNames();

    @Query("SELECT * FROM ColorName WHERE hex=:hex")
    ColorName loadColorNameByHex(String hex);

    @Query("SELECT COUNT(*) FROM ColorName")
    int getRowCount();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long[] saveColorNames(ColorName... colorNames);

}
