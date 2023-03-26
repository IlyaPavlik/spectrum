package ru.magflayer.spectrum.data.entity.converter

import ru.magflayer.spectrum.data.entity.ColorPhoto
import ru.magflayer.spectrum.domain.entity.ColorPhotoEntity

class ColorPhotoConverter : BaseConverter<ColorPhoto, ColorPhotoEntity>() {

    override fun convertToEntity(dto: ColorPhoto): ColorPhotoEntity {
        val type = ColorPhotoEntity.Type.values()[dto.type]
        return ColorPhotoEntity(type, dto.filePath, dto.rgbColors, dto.id)
    }

    override fun convertToDto(entity: ColorPhotoEntity): ColorPhoto {
        return ColorPhoto(
            entity.millis,
            entity.filePath,
            entity.rgbColors,
            entity.type.ordinal,
        )
    }
}
