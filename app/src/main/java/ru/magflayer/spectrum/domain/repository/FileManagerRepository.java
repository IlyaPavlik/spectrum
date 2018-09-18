package ru.magflayer.spectrum.domain.repository;

import android.net.Uri;

import rx.Observable;

public interface FileManagerRepository {

    Observable<Uri> saveFile(String filePath, byte[] bytes);

    Observable<Uri> saveFileToExternalStorage(String fileName, byte[] bytes);

}
