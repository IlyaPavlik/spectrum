package ru.magflayer.spectrum.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.magflayer.spectrum.data.entity.ColorName

@Dao
interface ColorNameDao {

    @get:Query("SELECT COUNT(*) FROM ColorName")
    val rowCount: Int

    @Query("SELECT * FROM ColorName")
    fun loadColorNames(): List<ColorName>

    @Query("SELECT * FROM ColorName WHERE hex=:hex")
    fun loadColorNameByHex(hex: String): ColorName

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun saveColorNames(vararg colorNames: ColorName): LongArray

}
