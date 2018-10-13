package ru.magflayer.spectrum.domain.repository

import ru.magflayer.spectrum.domain.entity.ColorInfoEntity

interface ColorInfoRepository {

    fun isColorNamesUploaded(): Boolean

    fun isNcsColorUploaded(): Boolean

    fun uploadColorNames(hexName: Map<String, String>): Boolean

    fun loadColorNames(): List<ColorInfoEntity>

    fun loadColorNameByHex(hex: String): String

    fun uploadNcsColors(hexName: Map<String, String>): Boolean

    fun loadNcsColors(): List<ColorInfoEntity>

    fun loadNcsColorByHex(hex: String): String

}
