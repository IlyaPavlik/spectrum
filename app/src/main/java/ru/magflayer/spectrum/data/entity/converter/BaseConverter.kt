package ru.magflayer.spectrum.data.entity.converter

abstract class BaseConverter<DTO, ENTITY> {

    abstract fun convertToEntity(dto: DTO): ENTITY

    abstract fun convertToDto(entity: ENTITY): DTO

    fun convertToEntities(dtos: Iterable<DTO>) = dtos.map { convertToEntity(it) }

    fun convertToDtos(entities: Iterable<ENTITY>) = entities.map { convertToDto(it) }

}