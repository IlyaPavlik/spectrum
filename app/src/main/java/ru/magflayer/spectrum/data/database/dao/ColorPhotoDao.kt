package ru.magflayer.spectrum.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.magflayer.spectrum.data.entity.ColorPhoto

@Dao
interface ColorPhotoDao {

    @Query("SELECT * FROM ColorPhoto")
    suspend fun loadPhotos(): List<ColorPhoto>

    @Query("SELECT * FROM ColorPhoto WHERE file_path LIKE :path")
    suspend fun loadPhoto(path: String): ColorPhoto

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun savePhoto(colorPhoto: ColorPhoto): Long

    @Delete
    suspend fun deletePhoto(colorPhoto: ColorPhoto): Int
}
