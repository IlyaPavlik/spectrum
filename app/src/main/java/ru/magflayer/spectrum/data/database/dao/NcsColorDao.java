package ru.magflayer.spectrum.data.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

import ru.magflayer.spectrum.data.entity.NcsColor;

import static android.arch.persistence.room.OnConflictStrategy.REPLACE;

@Dao
public interface NcsColorDao {

    @Query("SELECT * FROM NcsColor")
    List<NcsColor> loadNcsColors();

    @Query("SELECT * FROM NcsColor WHERE hex=:hex")
    NcsColor loadNcsColorByHex(String hex);

    @Query("SELECT COUNT(*) FROM NcsColor")
    int getRowCount();

    @Insert(onConflict = REPLACE)
    long[] saveNcsColors(NcsColor... ncsColors);

}
