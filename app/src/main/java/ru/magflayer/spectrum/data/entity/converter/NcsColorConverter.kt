package ru.magflayer.spectrum.data.entity.converter

import ru.magflayer.spectrum.data.entity.NcsColor
import ru.magflayer.spectrum.domain.entity.ColorInfoEntity

class NcsColorConverter : BaseConverter<NcsColor, ColorInfoEntity>() {

    override fun convertToEntity(dto: NcsColor) = ColorInfoEntity(dto.hex, dto.name)

    override fun convertToDto(entity: ColorInfoEntity) = NcsColor(entity.id, entity.name)

}
