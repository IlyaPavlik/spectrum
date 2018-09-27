package ru.magflayer.spectrum.domain.interactor;

import android.database.Cursor;
import android.graphics.Color;
import android.support.annotation.VisibleForTesting;
import android.support.v4.util.Pair;
import android.text.TextUtils;

import com.google.gson.Gson;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import lombok.extern.slf4j.Slf4j;
import ru.magflayer.spectrum.data.android.ResourceManager;
import ru.magflayer.spectrum.domain.entity.ColorInfoEntity;
import ru.magflayer.spectrum.domain.entity.ColorInfoState;
import ru.magflayer.spectrum.domain.entity.ListType;
import ru.magflayer.spectrum.domain.entity.NcsColorEntity;
import ru.magflayer.spectrum.domain.repository.ColorInfoRepository;
import rx.Observable;
import rx.functions.Func0;
import rx.functions.Func1;
import rx.observables.SyncOnSubscribe;

@Slf4j
@Singleton
public class ColorInfoInteractor {

    @VisibleForTesting
    public static final String COLOR_NAMES_ASSET_NAME = "colors.json";
    @VisibleForTesting
    public static final String NCS_COLORS_ASSET_NAME = "ncs.json";

    private final ColorInfoRepository colorInfoRepository;
    private final ResourceManager resourceManager;
    private final Gson gson;

    @Inject
    ColorInfoInteractor(final ColorInfoRepository colorInfoRepository,
                        final ResourceManager resourceManager,
                        final Gson gson) {
        this.colorInfoRepository = colorInfoRepository;
        this.resourceManager = resourceManager;
        this.gson = gson;
    }

    public Observable<ColorInfoState> uploadColorInfo() {
        List<Observable<ColorInfoState>> observables = Arrays.asList(
                uploadColorNamesObservable(),
                uploadNcsColorsObservable()
        );
        return Observable.merge(observables);
    }

    public Observable<String> findColorNameByHex(final String hex) {
        final int sourceColor = Color.parseColor(hex);
        final int[] sourceColorRgb = new int[]{
                Color.red(sourceColor),
                Color.green(sourceColor),
                Color.blue(sourceColor)
        };

        return createCursorObservable(colorInfoRepository::loadColorNames, "hex")
                .reduce(Pair.create("", Double.MAX_VALUE), (currentMin, s) -> {
                    if (TextUtils.isEmpty(s)) s = "#000000";

                    int ncsColor = Color.parseColor(s);
                    int[] ncsColorRgb = new int[]{
                            Color.red(ncsColor),
                            Color.green(ncsColor),
                            Color.blue(ncsColor)
                    };

                    double error = calculateColorDifference(sourceColorRgb, ncsColorRgb);
                    double result = currentMin.second > error ? error : currentMin.second;
                    String colorHex = currentMin.second > error ? s : currentMin.first;
                    return Pair.create(colorHex, result);
                })
                .filter(currentMin -> currentMin.second != Integer.MAX_VALUE)
                .map(stringIntegerPair -> stringIntegerPair.first)
                .flatMap(h -> Observable.fromCallable(() -> colorInfoRepository.loadColorNameByHex(h)));
    }

    public Observable<String> findNcsColorByHex(final String hex) {
        final int sourceColor = Color.parseColor(hex);
        final int[] sourceColorRgb = new int[]{
                Color.red(sourceColor),
                Color.green(sourceColor),
                Color.blue(sourceColor)
        };

        return createCursorObservable(colorInfoRepository::loadNcsColors, "hex")
                .reduce(Pair.create("", Double.MAX_VALUE), (currentMin, s) -> {
                    int ncsColor = Color.parseColor(s);
                    int[] ncsColorRgb = new int[]{
                            Color.red(ncsColor),
                            Color.green(ncsColor),
                            Color.blue(ncsColor)
                    };

                    double error = calculateColorDifference(sourceColorRgb, ncsColorRgb);
                    Double result = currentMin.second > error ? error : currentMin.second;
                    String colorHex = currentMin.second > error ? s : currentMin.first;
                    return new Pair<>(colorHex, result);
                })
                .filter(currentMin -> currentMin.second != Integer.MAX_VALUE)
                .map(stringIntegerPair -> stringIntegerPair.first)
                .flatMap(h -> Observable.fromCallable(() -> colorInfoRepository.loadNcsColorByHex(h)));
    }

    private Observable<ColorInfoState> uploadColorNamesObservable() {
        return Observable.fromCallable(colorInfoRepository::isColorNamesUploaded)
                .flatMap(colorNamesUploaded -> {
                    if (colorNamesUploaded) {
                        return Observable.just(ColorInfoState.SUCCESS);
                    }
                    return loadColorNames()
                            .flatMap(Observable::from)
                            .toMap(ColorInfoEntity::getId, ColorInfoEntity::getName)
                            .map(hexName -> colorInfoRepository.uploadColorNames(hexName)
                                    ? ColorInfoState.SUCCESS
                                    : ColorInfoState.ERROR);
                })
                .onErrorReturn(error -> {
                    log.warn("Error while uploading color names: ", error);
                    return ColorInfoState.ERROR;
                });
    }

    private Observable<ColorInfoState> uploadNcsColorsObservable() {
        return Observable.fromCallable(colorInfoRepository::isNcsColorUploaded)
                .flatMap(ncsColorUploaded -> {
                    if (ncsColorUploaded) {
                        return Observable.just(ColorInfoState.SUCCESS);
                    }
                    return loadNscColors()
                            .flatMap(Observable::from)
                            .toMap(NcsColorEntity::getValue, NcsColorEntity::getName)
                            .map(hexName -> colorInfoRepository.uploadNcsColors(hexName)
                                    ? ColorInfoState.SUCCESS
                                    : ColorInfoState.ERROR);
                })
                .onErrorReturn(error -> {
                    log.warn("Error while uploading ncs colors: ", error);
                    return ColorInfoState.ERROR;
                });
    }

    private Observable<List<ColorInfoEntity>> loadColorNames() {
        return Observable.fromCallable(() -> resourceManager.getAsset(COLOR_NAMES_ASSET_NAME))
                .flatMap(convertInputStreamToString())
                .map(fromJsonToList(ColorInfoEntity.class));
    }

    private Observable<List<NcsColorEntity>> loadNscColors() {
        return Observable.fromCallable(() -> resourceManager.getAsset(NCS_COLORS_ASSET_NAME))
                .flatMap(convertInputStreamToString())
                .map(fromJsonToList(NcsColorEntity.class));
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

    private Observable<String> createCursorObservable(final Func0<Cursor> cursorFunc, final String columnName) {
        return Observable.create(SyncOnSubscribe.createStateful(
                cursorFunc,
                (cursor, observer) -> {
                    try {
                        if (cursor != null && cursor.moveToNext()) {
                            observer.onNext(cursor.getString(cursor.getColumnIndex(columnName)));
                        } else {
                            observer.onCompleted();
                        }
                    } catch (Exception e) {
                        observer.onError(e);
                    }
                    return cursor;
                },
                Cursor::close));
    }

    private double calculateColorDifference(final int[] colorOneRgb, final int[] colorTwoRgb) {
        Double error = Math.pow(colorTwoRgb[0] - colorOneRgb[0], 2)
                + Math.pow(colorTwoRgb[1] - colorOneRgb[1], 2)
                + Math.pow(colorTwoRgb[2] - colorOneRgb[2], 2);
        return Math.sqrt(error);
    }

}