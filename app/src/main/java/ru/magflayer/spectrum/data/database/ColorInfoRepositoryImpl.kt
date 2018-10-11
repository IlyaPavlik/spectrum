package ru.magflayer.spectrum.data.database

import ru.magflayer.spectrum.data.entity.ColorName
import ru.magflayer.spectrum.data.entity.NcsColor
import ru.magflayer.spectrum.data.entity.converter.ColorNameConverter
import ru.magflayer.spectrum.data.entity.converter.NcsColorConverter
import ru.magflayer.spectrum.domain.entity.ColorInfoEntity
import ru.magflayer.spectrum.domain.repository.ColorInfoRepository

class ColorInfoRepositoryImpl(private val appDatabase: AppDatabase) : ColorInfoRepository {
    private val colorNameConverter = ColorNameConverter()
    private val ncsColorConverter = NcsColorConverter()

    @Synchronized
    override fun uploadColorNames(hexName: Map<String, String>): Boolean {
        val colorNames = hexName.map { ColorName(it.key, it.value) }.toTypedArray()
        return appDatabase.colorNameDao().saveColorNames(*colorNames).isNotEmpty()
    }

    @Synchronized
    override fun isColorNamesUploaded(): Boolean {
        return appDatabase.colorNameDao().rowCount > 0
    }

    @Synchronized
    override fun loadColorNames(): List<ColorInfoEntity> {
        val colorNames = appDatabase.colorNameDao().loadColorNames()
        return colorNameConverter.convertToEntities(colorNames)
    }

    @Synchronized
    override fun loadColorNameByHex(hex: String): String {
        return appDatabase.colorNameDao().loadColorNameByHex(hex).name
    }

    @Synchronized
    override fun uploadNcsColors(hexName: Map<String, String>): Boolean {
        val ncsColors = hexName.map { NcsColor(it.key, it.value) }.toTypedArray()
        return appDatabase.ncsColorDao().saveNcsColors(*ncsColors).isNotEmpty()
    }

    @Synchronized
    override fun isNcsColorUploaded(): Boolean {
        return appDatabase.ncsColorDao().rowCount > 0
    }

    @Synchronized
    override fun loadNcsColors(): List<ColorInfoEntity> {
        val ncsColors = appDatabase.ncsColorDao().loadNcsColors()
        return ncsColorConverter.convertToEntities(ncsColors)
    }

    @Synchronized
    override fun loadNcsColorByHex(hex: String): String {
        val (_, name) = appDatabase.ncsColorDao().loadNcsColorByHex(hex)
        return name
    }
}
