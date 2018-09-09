package ru.magflayer.spectrum.domain.interactor;

import android.support.annotation.VisibleForTesting;

import com.google.gson.Gson;

import java.io.InputStream;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import ru.magflayer.spectrum.data.android.ResourceManager;
import ru.magflayer.spectrum.data.local.ColorInfo;
import ru.magflayer.spectrum.data.local.NcsColor;
import ru.magflayer.spectrum.domain.model.ListType;
import rx.Observable;
import rx.functions.Func1;

@Singleton
public class ColorsInteractor {

    @VisibleForTesting
    public static final String COLOR_NAMES_ASSET_NAME = "colors.json";
    @VisibleForTesting
    public static final String NCS_COLORS_ASSET_NAME = "ncs.json";

    private final ResourceManager resourceManager;
    private final Gson gson;

    @Inject
    ColorsInteractor(final ResourceManager resourceManager, final Gson gson) {
        this.resourceManager = resourceManager;
        this.gson = gson;
    }

    public Observable<List<ColorInfo>> loadColorNames() {
        return Observable.fromCallable(() -> resourceManager.getAsset(COLOR_NAMES_ASSET_NAME))
                .flatMap(convertInputStreamToString())
                .map(fromJsonToList(ColorInfo.class));
    }

    public Observable<List<NcsColor>> loadNscColors() {
        return Observable.fromCallable(() -> resourceManager.getAsset(NCS_COLORS_ASSET_NAME))
                .flatMap(convertInputStreamToString())
                .map(fromJsonToList(NcsColor.class));
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private Func1<InputStream, Observable<String>> convertInputStreamToString() {
        return inputStream -> Observable.fromCallable(() -> {
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            return new String(buffer, "UTF-8");
        });
    }

    private <T> Func1<String, List<T>> fromJsonToList(final Class<T> clazz) {
        return jsonText -> gson.fromJson(jsonText, new ListType<T>(clazz));
    }

}