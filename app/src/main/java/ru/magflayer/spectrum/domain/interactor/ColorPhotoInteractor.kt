package ru.magflayer.spectrum.domain.interactor

import ru.magflayer.spectrum.domain.entity.ColorPhotoEntity
import ru.magflayer.spectrum.domain.repository.PhotoRepository
import rx.Observable
import java.io.File
import javax.inject.Inject

class ColorPhotoInteractor @Inject
internal constructor(private val photoRepository: PhotoRepository) {

    fun saveColorPhoto(entity: ColorPhotoEntity): Observable<Boolean> {
        return photoRepository.savePhoto(entity)
    }

    fun loadColorPhotos(): Observable<List<ColorPhotoEntity>> {
        return photoRepository.loadPhotos()
                .map { entities ->
                    entities.asSequence()
                            .filter {
                                val exists = File(it.filePath).exists()
                                if (!exists) {
                                    photoRepository.removePhoto(it)
                                }
                                exists
                            }
                            .sortedByDescending { it.millis }
                            .toList()
                }
    }

    fun removeColorPhoto(entity: ColorPhotoEntity): Observable<Boolean> {
        return photoRepository.removePhoto(entity)
                .map { success ->
                    if (entity.type === ColorPhotoEntity.Type.INTERNAL) {
                        File(entity.filePath).delete()
                    }
                    success
                }
    }

    fun loadColorPhoto(filePath: String): Observable<ColorPhotoEntity> {
        return photoRepository.loadPhoto(filePath)
    }

}
