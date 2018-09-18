package ru.magflayer.spectrum.data.database.dao;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import java.util.List;

import ru.magflayer.spectrum.data.entity.ColorPhoto;

@Dao
public interface ColorPhotoDao {

    @Query("SELECT * FROM ColorPhoto")
    List<ColorPhoto> loadPhotos();

    @Query("SELECT * FROM ColorPhoto WHERE file_path LIKE :path")
    ColorPhoto loadPhoto(String path);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long savePhoto(ColorPhoto colorPhoto);

    @Delete
    int deletePhoto(ColorPhoto colorPhoto);

}
