package ru.magflayer.spectrum.data.system

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import ru.magflayer.spectrum.BuildConfig
import ru.magflayer.spectrum.domain.repository.FileManagerRepository
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalFileManager @Inject constructor(private val context: Context) : FileManagerRepository {

    companion object {
        private const val CONTENT_PREFIX = "content://"
        private const val URI_AUTHORITY = BuildConfig.APPLICATION_ID + ".provider"
    }

    override suspend fun saveFile(filePath: String, bytes: ByteArray): Uri {
        return withContext(Dispatchers.IO) {
            FileOutputStream(filePath).use { outputStream ->
                outputStream.write(bytes)
            }
            Uri.parse(filePath)
        }
    }

    override suspend fun saveFileToExternalStorage(fileName: String, bytes: ByteArray): Uri {
        return withContext(Dispatchers.IO) {
            val externalFile = File(context.getExternalFilesDir(null), fileName).absolutePath
            saveFile(externalFile, bytes)
        }
    }

    override suspend fun isFileExists(filePath: String): Boolean {
        return if (filePath.startsWith(CONTENT_PREFIX)) {
            withContext(Dispatchers.IO) {
                try {
                    val uri = Uri.parse(filePath)
                    context.contentResolver.openInputStream(uri)
                    true
                } catch (e: Throwable) {
                    false
                }
            }
        } else {
            File(filePath).exists()
        }
    }

    override suspend fun copyFileToExternalEndpoint(sourceFilePath: Uri): Uri {
        return withContext(Dispatchers.IO) {
            context.contentResolver.openInputStream(sourceFilePath)?.use { inputStream ->
                val fileName = File(sourceFilePath.toString()).name
                val outputFile = File(
                    context.getExternalFilesDir(null),
                    fileName
                )
                FileOutputStream(outputFile).use { outputStream ->
                    inputStream.copyTo(outputStream)
                }
                getUriForFile(outputFile)
            } ?: throw FileNotFoundException("File '$sourceFilePath' is not found")
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
