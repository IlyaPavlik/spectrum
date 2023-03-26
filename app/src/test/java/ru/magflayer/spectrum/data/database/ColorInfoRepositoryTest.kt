package ru.magflayer.spectrum.data.database

import ru.magflayer.spectrum.domain.entity.ColorInfoEntity
import ru.magflayer.spectrum.domain.repository.ColorInfoRepository

class ColorInfoRepositoryTest : ColorInfoRepository {

    private val testColorNames = listOf(
        ColorInfoEntity("#FBCEB1", "Абрикосовый"),
        ColorInfoEntity("#FDD9B5", "Абрикосовый Крайола"),
        ColorInfoEntity("#B5B8B1", "Агатовый серый"),
        ColorInfoEntity("#7FFFD4", "Аквамариновый"),
        ColorInfoEntity("#78DBE2", "Аквамариновый Крайола"),
        ColorInfoEntity("#E32636", "Ализариновый красный"),
        ColorInfoEntity("#FF2400", "Алый"),
        ColorInfoEntity("#ED3CCA", "Амарантовый маджента"),
        ColorInfoEntity("#CD9575", "Античная латунь"),
    )

    private val testNcsColors = listOf(
        ColorInfoEntity("#FFFFFF", "NCS S 0300-N"),
        ColorInfoEntity("#FCFCFB", "NCS S 0500-N"),
        ColorInfoEntity("#F2F2F0", "NCS S 1000-N"),
        ColorInfoEntity("#E0DFDE", "NCS S 1500-N"),
        ColorInfoEntity("#D8D8D7", "NCS S 2000-N"),
        ColorInfoEntity("#CECDCC", "NCS S 2500-N"),
    )

    override suspend fun uploadColorNames(hexName: Map<String, String>): Boolean {
        return false
    }

    override suspend fun isColorNamesUploaded(): Boolean {
        return false
    }

    override suspend fun loadColorNames(): List<ColorInfoEntity> {
        return testColorNames
    }

    override suspend fun loadColorNameByHex(hex: String): String {
        return testColorNames.find { hex == it.id }?.name.orEmpty()
    }

    override suspend fun uploadNcsColors(hexName: Map<String, String>): Boolean {
        return false
    }

    override suspend fun isNcsColorUploaded(): Boolean {
        return false
    }

    override suspend fun loadNcsColors(): List<ColorInfoEntity> {
        return testNcsColors
    }

    override suspend fun loadNcsColorByHex(hex: String): String {
        return testNcsColors.find { hex == it.id }?.name.orEmpty()
    }
}
