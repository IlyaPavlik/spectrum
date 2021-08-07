package ru.magflayer.spectrum.domain.interactor

import ru.magflayer.spectrum.domain.entity.ColorPhotoEntity
import ru.magflayer.spectrum.domain.repository.FileManagerRepository
import ru.magflayer.spectrum.domain.repository.PhotoRepository
import rx.Observable
import java.io.File
import javax.inject.Inject

class ColorPhotoInteractor @Inject constructor(
    private val photoRepository: PhotoRepository,
    private val fileManagerRepository: FileManagerRepository
) {

    fun saveColorPhoto(entity: ColorPhotoEntity): Observable<Boolean> {
        return photoRepository.savePhoto(entity)
    }

    fun loadColorPhotos(): Observable<List<ColorPhotoEntity>> {
        return photoRepository.loadPhotos()
            .flatMap { photos -> Observable.from(photos) }
            .flatMap { photo -> // filter whether a file is exists
                fileManagerRepository.isFileExists(photo.filePath)
                    .filter { it }
                    .flatMap { exists ->
                        if (!exists) {
                            photoRepository.removePhoto(photo)
                        } else Observable.just(true)
                    }
                    .map { photo }
            }
            .toSortedList { photo1, photo2 -> photo2.millis.compareTo(photo1.millis) }
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
