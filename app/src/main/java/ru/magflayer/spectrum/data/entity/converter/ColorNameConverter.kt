package ru.magflayer.spectrum.data.entity.converter

import ru.magflayer.spectrum.data.entity.ColorName
import ru.magflayer.spectrum.domain.entity.ColorInfoEntity

class ColorNameConverter : BaseConverter<ColorName, ColorInfoEntity>() {

    override fun convertToEntity(dto: ColorName): ColorInfoEntity =
        ColorInfoEntity(dto.hex, dto.name)

    override fun convertToDto(entity: ColorInfoEntity): ColorName =
        ColorName(entity.id, entity.name)

}
