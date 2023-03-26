package ru.magflayer.spectrum.domain.repository

import android.net.Uri

interface FileManagerRepository {

    suspend fun saveFile(filePath: String, bytes: ByteArray): Uri

    suspend fun saveFileToExternalStorage(fileName: String, bytes: ByteArray): Uri

    suspend fun isFileExists(filePath: String): Boolean

    suspend fun copyFileToExternalEndpoint(sourceFilePath: Uri): Uri
}
