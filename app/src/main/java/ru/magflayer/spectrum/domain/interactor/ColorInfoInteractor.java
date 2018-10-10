package ru.magflayer.spectrum.domain.interactor;

import com.google.gson.Gson;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.magflayer.spectrum.data.android.ResourceManager;
import ru.magflayer.spectrum.domain.common.utils.Utils;
import ru.magflayer.spectrum.domain.entity.ColorInfoEntity;
import ru.magflayer.spectrum.domain.entity.ColorInfoState;
import ru.magflayer.spectrum.domain.entity.ListType;
import ru.magflayer.spectrum.domain.entity.NcsColorEntity;
import ru.magflayer.spectrum.domain.repository.ColorInfoRepository;
import ru.magflayer.spectrum.presentation.common.utils.ColorUtils;
import rx.Observable;
import rx.functions.Func1;

@Slf4j
@Singleton
public class ColorInfoInteractor {

    private static final String COLOR_NAMES_ASSET_NAME = "colors.json";
    private static final String NCS_COLORS_ASSET_NAME = "ncs.json";

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
        final int[] sourceColorRgb;
        try {
            final int sourceColor = ColorUtils.parseHex2Dec(hex);
            sourceColorRgb = ColorUtils.dec2Rgb(sourceColor);
        } catch (Exception e) {
            return Observable.error(e);
        }

        return Observable.fromCallable(colorInfoRepository::loadColorNames)
                .flatMap(Observable::from)
                .map(ColorInfoEntity::getId)
                .reduce(new ColorError("", Double.MAX_VALUE), (currentMin, s) -> {
                    if (Utils.isEmpty(s)) s = "#000000";

                    int ncsColor = ColorUtils.parseHex2Dec(s);
                    int[] ncsColorRgb = ColorUtils.dec2Rgb(ncsColor);

                    double error = calculateColorDifference(sourceColorRgb, ncsColorRgb);
                    double result = currentMin.error > error ? error : currentMin.error;
                    String colorHex = currentMin.error > error ? s : currentMin.id;
                    return new ColorError(colorHex, result);
                })
                .filter(currentMin -> currentMin.error != Integer.MAX_VALUE)
                .map(stringIntegerPair -> stringIntegerPair.id)
                .flatMap(h -> Observable.fromCallable(() -> colorInfoRepository.loadColorNameByHex(h)));
    }

    public Observable<String> findNcsColorByHex(final String hex) {
        final int[] sourceColorRgb;
        try {
            final int sourceColor = ColorUtils.parseHex2Dec(hex);
            sourceColorRgb = ColorUtils.dec2Rgb(sourceColor);
        } catch (Exception e) {
            return Observable.error(e);
        }

        return Observable.fromCallable(colorInfoRepository::loadNcsColors)
                .flatMap(Observable::from)
                .map(ColorInfoEntity::getId)
                .reduce(new ColorError("", Double.MAX_VALUE), (currentMin, s) -> {
                    int ncsColor = ColorUtils.parseHex2Dec(s);
                    int[] ncsColorRgb = ColorUtils.dec2Rgb(ncsColor);

                    double error = calculateColorDifference(sourceColorRgb, ncsColorRgb);
                    Double result = currentMin.error > error ? error : currentMin.error;
                    String colorHex = currentMin.error > error ? s : currentMin.id;
                    return new ColorError(colorHex, result);
                })
                .filter(currentMin -> currentMin.error != Integer.MAX_VALUE)
                .map(stringIntegerPair -> stringIntegerPair.id)
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

    private double calculateColorDifference(final int[] colorOneRgb, final int[] colorTwoRgb) {
        Double error = Math.pow(colorTwoRgb[0] - colorOneRgb[0], 2)
                + Math.pow(colorTwoRgb[1] - colorOneRgb[1], 2)
                + Math.pow(colorTwoRgb[2] - colorOneRgb[2], 2);
        return Math.sqrt(error);
    }

    @AllArgsConstructor
    private static class ColorError {
        private String id;
        private double error;
    }

}