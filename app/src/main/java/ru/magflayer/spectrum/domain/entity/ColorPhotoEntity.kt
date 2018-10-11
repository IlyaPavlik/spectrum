package ru.magflayer.spectrum.domain.entity

data class ColorPhotoEntity(
        val type: Type,
        val filePath: String,
        val rgbColors: List<Int>,
        val millis: Long = System.currentTimeMillis()) {

    constructor(type: Type,
                filePath: String,
                rgbColors: List<Int>) : this(type, filePath, rgbColors, System.currentTimeMillis())

    enum class Type {
        INTERNAL, EXTERNAL
    }
}
