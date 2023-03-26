package ru.magflayer.spectrum.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import ru.magflayer.spectrum.data.entity.NcsColor

@Dao
interface NcsColorDao {

    @get:Query("SELECT COUNT(*) FROM NcsColor")
    val rowCount: Int

    @Query("SELECT * FROM NcsColor")
    suspend fun loadNcsColors(): List<NcsColor>

    @Query("SELECT * FROM NcsColor WHERE hex=:hex")
    suspend fun loadNcsColorByHex(hex: String): NcsColor

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveNcsColors(vararg ncsColors: NcsColor): LongArray
}
