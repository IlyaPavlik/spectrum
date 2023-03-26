package ru.magflayer.spectrum.domain.repository

import ru.magflayer.spectrum.domain.entity.ColorInfoEntity

interface ColorInfoRepository {

    suspend fun isColorNamesUploaded(): Boolean

    suspend fun isNcsColorUploaded(): Boolean

    suspend fun uploadColorNames(hexName: Map<String, String>): Boolean

    suspend fun loadColorNames(): List<ColorInfoEntity>

    suspend fun loadColorNameByHex(hex: String): String

    suspend fun uploadNcsColors(hexName: Map<String, String>): Boolean

    suspend fun loadNcsColors(): List<ColorInfoEntity>

    suspend fun loadNcsColorByHex(hex: String): String
}
