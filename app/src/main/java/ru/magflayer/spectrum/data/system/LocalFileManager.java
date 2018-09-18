package ru.magflayer.spectrum.data.system;

import android.content.Context;
import android.net.Uri;

import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;

import javax.inject.Inject;
import javax.inject.Singleton;

import ru.magflayer.spectrum.domain.repository.FileManagerRepository;
import rx.Observable;

@Singleton
public class LocalFileManager implements FileManagerRepository {

    @Inject
    Context context;

    @Inject
    LocalFileManager() {
    }

    @Override
    public Observable<Uri> saveFile(final String filePath, final byte[] bytes) {
        return Observable.fromCallable(() -> {
            FileOutputStream out = null;
            try {
                out = new FileOutputStream(filePath);
                out.write(bytes);
            } finally {
                closeQuietly(out);
            }
            return Uri.parse(filePath);
        });
    }

    @Override
    public Observable<Uri> saveFileToExternalStorage(final String fileName, final byte[] bytes) {
        String filePath = new File(context.getExternalFilesDir(null), fileName).getAbsolutePath();
        return saveFile(filePath, bytes);
    }

    private void closeQuietly(final Closeable closeable) {
        try {
            closeable.close();
        } catch (Exception ignore) {
        }
    }

}
