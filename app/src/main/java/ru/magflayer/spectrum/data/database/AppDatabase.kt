package ru.magflayer.spectrum.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import ru.magflayer.spectrum.data.database.dao.ColorNameDao
import ru.magflayer.spectrum.data.database.dao.ColorPhotoDao
import ru.magflayer.spectrum.data.database.dao.NcsColorDao
import ru.magflayer.spectrum.data.entity.ColorName
import ru.magflayer.spectrum.data.entity.ColorPhoto
import ru.magflayer.spectrum.data.entity.NcsColor

@Database(
    entities = [ColorPhoto::class, ColorName::class, NcsColor::class],
    version = 3,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    companion object {
        const val DATABASE_NAME = "spectre-database"
    }

    abstract fun colorPhotoDao(): ColorPhotoDao

    abstract fun colorNameDao(): ColorNameDao

    abstract fun ncsColorDao(): NcsColorDao


}
