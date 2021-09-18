package ru.magflayer.spectrum.data.repository

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.magflayer.spectrum.data.database.AppDatabase
import ru.magflayer.spectrum.data.entity.ColorName
import ru.magflayer.spectrum.data.entity.NcsColor
import ru.magflayer.spectrum.data.entity.converter.ColorNameConverter
import ru.magflayer.spectrum.data.entity.converter.NcsColorConverter
import ru.magflayer.spectrum.domain.entity.ColorInfoEntity
import ru.magflayer.spectrum.domain.repository.ColorInfoRepository

class ColorInfoRepositoryImpl(private val appDatabase: AppDatabase) : ColorInfoRepository {

    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
    private val colorNameConverter = ColorNameConverter()
    private val ncsColorConverter = NcsColorConverter()

    override suspend fun uploadColorNames(hexName: Map<String, String>): Boolean =
        withContext(dispatcher) {
            val colorNames = hexName.map { ColorName(it.key, it.value) }.toTypedArray()
            appDatabase.colorNameDao().saveColorNames(*colorNames).isNotEmpty()
        }

    override suspend fun isColorNamesUploaded(): Boolean = withContext(dispatcher) {
        appDatabase.colorNameDao().rowCount > 0
    }

    override suspend fun loadColorNames(): List<ColorInfoEntity> = withContext(dispatcher) {
        val colorNames = appDatabase.colorNameDao().loadColorNames()
        colorNameConverter.convertToEntities(colorNames)
    }

    override suspend fun loadColorNameByHex(hex: String): String = withContext(dispatcher) {
        appDatabase.colorNameDao().loadColorNameByHex(hex).name
    }

    override suspend fun uploadNcsColors(hexName: Map<String, String>): Boolean =
        withContext(dispatcher) {
            val ncsColors = hexName.map { NcsColor(it.key, it.value) }.toTypedArray()
            appDatabase.ncsColorDao().saveNcsColors(*ncsColors).isNotEmpty()
        }

    override suspend fun isNcsColorUploaded(): Boolean = withContext(dispatcher) {
        appDatabase.ncsColorDao().rowCount > 0
    }

    override suspend fun loadNcsColors(): List<ColorInfoEntity> = withContext(dispatcher) {
        val ncsColors = appDatabase.ncsColorDao().loadNcsColors()
        ncsColorConverter.convertToEntities(ncsColors)
    }

    override suspend fun loadNcsColorByHex(hex: String): String = withContext(dispatcher) {
        val (_, name) = appDatabase.ncsColorDao().loadNcsColorByHex(hex)
        name
    }
}
