package ru.magflayer.spectrum.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy.REPLACE
import androidx.room.Query
import ru.magflayer.spectrum.data.entity.NcsColor

@Dao
interface NcsColorDao {

    @get:Query("SELECT COUNT(*) FROM NcsColor")
    val rowCount: Int

    @Query("SELECT * FROM NcsColor")
    fun loadNcsColors(): List<NcsColor>

    @Query("SELECT * FROM NcsColor WHERE hex=:hex")
    fun loadNcsColorByHex(hex: String): NcsColor

    @Insert(onConflict = REPLACE)
    fun saveNcsColors(vararg ncsColors: NcsColor): LongArray

}
