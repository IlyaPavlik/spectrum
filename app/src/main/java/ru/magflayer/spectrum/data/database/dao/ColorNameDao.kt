package ru.magflayer.spectrum.data.database.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query

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
