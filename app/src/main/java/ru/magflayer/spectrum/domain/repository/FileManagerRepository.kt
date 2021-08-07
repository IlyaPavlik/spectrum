package ru.magflayer.spectrum.domain.repository

import android.net.Uri

import rx.Observable

interface FileManagerRepository {

    fun saveFile(filePath: String, bytes: ByteArray): Observable<Uri>

    fun saveFileToExternalStorage(fileName: String, bytes: ByteArray): Observable<Uri>

    fun isFileExists(filePath: String): Observable<Boolean>

    fun copyFileToExternalEndpoint(sourceFilePath: Uri): Observable<Uri>

}
