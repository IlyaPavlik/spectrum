package ru.magflayer.spectrum.domain.interactor;

import android.net.Uri;

import javax.inject.Inject;

import ru.magflayer.spectrum.domain.repository.FileManagerRepository;
import rx.Observable;

public class FileManagerInteractor {

    private final FileManagerRepository fileManagerRepository;

    @Inject
    public FileManagerInteractor(final FileManagerRepository fileManagerRepository) {
        this.fileManagerRepository = fileManagerRepository;
    }

    public Observable<Uri> saveFileToExternalStorage(final String fileName, final byte[] bytes) {
        return fileManagerRepository.saveFileToExternalStorage(fileName, bytes);
    }
}
