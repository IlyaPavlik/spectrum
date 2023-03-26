package ru.magflayer.spectrum.domain.interactor

import android.net.Uri
import ru.magflayer.spectrum.domain.repository.FileManagerRepository
import javax.inject.Inject

class FileManagerInteractor @Inject constructor(
    private val fileManagerRepository: FileManagerRepository,
) {

    suspend fun saveFileToExternalStorage(fileName: String, bytes: ByteArray): Uri {
        return fileManagerRepository.saveFileToExternalStorage(fileName, bytes)
    }

    suspend fun copyToLocalStorage(sourceFilePath: Uri): Uri {
        return fileManagerRepository.copyFileToExternalEndpoint(sourceFilePath)
    }
}
