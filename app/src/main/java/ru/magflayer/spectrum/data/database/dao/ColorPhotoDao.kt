package ru.magflayer.spectrum.data.database.dao

import androidx.room.*
import ru.magflayer.spectrum.data.entity.ColorPhoto

@Dao
interface ColorPhotoDao {

    @Query("SELECT * FROM ColorPhoto")
    fun loadPhotos(): List<ColorPhoto>

    @Query("SELECT * FROM ColorPhoto WHERE file_path LIKE :path")
    fun loadPhoto(path: String): ColorPhoto

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun savePhoto(colorPhoto: ColorPhoto): Long

    @Delete
    fun deletePhoto(colorPhoto: ColorPhoto): Int

}
