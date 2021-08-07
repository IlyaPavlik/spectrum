package ru.magflayer.spectrum.data.system

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import ru.magflayer.spectrum.BuildConfig
import ru.magflayer.spectrum.domain.repository.FileManagerRepository
import rx.Observable
import java.io.Closeable
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalFileManager @Inject constructor(private val context: Context) : FileManagerRepository {

    companion object {
        private const val CONTENT_PREFIX = "content://"
        private const val URI_AUTHORITY = BuildConfig.APPLICATION_ID + ".provider"
    }

    override fun saveFile(filePath: String, bytes: ByteArray): Observable<Uri> {
        return Observable.fromCallable {
            var out: FileOutputStream? = null
            try {
                out = FileOutputStream(filePath)
                out.write(bytes)
            } finally {
                closeQuietly(out)
            }
            Uri.parse(filePath)
        }
    }

    override fun saveFileToExternalStorage(fileName: String, bytes: ByteArray): Observable<Uri> {
        return Observable.fromCallable {
            File(
                context.getExternalFilesDir(null),
                fileName
            ).absolutePath
        }
            .flatMap { saveFile(it, bytes) }
    }

    override fun isFileExists(filePath: String): Observable<Boolean> {
        return if (filePath.startsWith(CONTENT_PREFIX)) {
            try {
                val uri = Uri.parse(filePath)
                context.contentResolver.openInputStream(uri)
                Observable.just(true)
            } catch (e: Exception) {
                Observable.just(false)
            }
        } else {
            Observable.just(File(filePath).exists())
        }
    }

    override fun copyFileToExternalEndpoint(sourceFilePath: Uri): Observable<Uri> {
        return Observable.fromCallable { context.contentResolver.openInputStream(sourceFilePath) }
            .map { inputStream ->
                val fileName = File(sourceFilePath.toString()).name
                val outputFile = File(
                    context.getExternalFilesDir(null),
                    fileName
                )
                val fileOutputStream = FileOutputStream(outputFile)

                inputStream?.use {
                    fileOutputStream.use {
                        inputStream.copyTo(fileOutputStream)
                    }
                }
                getUriForFile(outputFile)
            }
    }

    private fun closeQuietly(closeable: Closeable?) {
        try {
            closeable?.close()
        } catch (ignore: Exception) {
        }
    }

    /**
     * The method is needed to passing file:// with Intent
     * Read more [android.os.FileUriExposedException]
     *
     * @throws IllegalArgumentException When the given [File] is outside
     * the paths supported by the provider.
     */
    private fun getUriForFile(file: File): Uri {
        return FileProvider.getUriForFile(context, URI_AUTHORITY, file)
    }

}
