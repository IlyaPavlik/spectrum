package ru.magflayer.spectrum.domain.repository

import ru.magflayer.spectrum.domain.entity.ColorPhotoEntity
import rx.Observable

interface PhotoRepository {

    fun savePhoto(colorPhoto: ColorPhotoEntity): Observable<Boolean>

    fun loadPhotos(): Observable<List<ColorPhotoEntity>>

    fun loadPhoto(filePath: String): Observable<ColorPhotoEntity>

    fun removePhoto(entity: ColorPhotoEntity): Observable<Boolean>

}
