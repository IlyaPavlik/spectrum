package ru.magflayer.spectrum.data.system

import android.content.Context
import android.net.Uri
import ru.magflayer.spectrum.domain.repository.FileManagerRepository
import rx.Observable
import java.io.Closeable
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocalFileManager @Inject
internal constructor() : FileManagerRepository {

    @Inject
    lateinit var context: Context

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
        return Observable.fromCallable { File(context.getExternalFilesDir(null), fileName).absolutePath }
                .flatMap { saveFile(it, bytes) }
    }

    private fun closeQuietly(closeable: Closeable?) {
        try {
            closeable!!.close()
        } catch (ignore: Exception) {
        }
    }

}
