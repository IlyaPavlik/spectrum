package ru.magflayer.spectrum.domain.interactor

import android.net.Uri
import ru.magflayer.spectrum.domain.repository.FileManagerRepository
import rx.Observable
import javax.inject.Inject

class FileManagerInteractor @Inject constructor(
    private val fileManagerRepository: FileManagerRepository
) {

    fun saveFileToExternalStorage(fileName: String, bytes: ByteArray): Observable<Uri> {
        return fileManagerRepository.saveFileToExternalStorage(fileName, bytes)
    }

    fun copyToLocalStorage(sourceFilePath: Uri): Observable<Uri> {
        return fileManagerRepository.copyFileToExternalEndpoint(sourceFilePath)
    }
}
