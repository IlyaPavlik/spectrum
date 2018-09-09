package ru.magflayer.spectrum.presentation.pages.main.camera;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.util.Pair;
import android.support.v7.graphics.Palette;
import android.text.TextUtils;

import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import ru.magflayer.spectrum.presentation.common.model.SurfaceInfo;
import ru.magflayer.spectrum.domain.injection.InjectorManager;
import ru.magflayer.spectrum.domain.interactor.ColorsInteractor;
import ru.magflayer.spectrum.domain.manager.AnalyticsManager;
import ru.magflayer.spectrum.domain.model.AnalyticsEvent;
import ru.magflayer.spectrum.domain.model.ColorPicture;
import ru.magflayer.spectrum.domain.model.event.PictureSavedEvent;
import ru.magflayer.spectrum.presentation.common.BasePresenter;
import ru.magflayer.spectrum.presentation.pages.main.router.MainRouter;
import ru.magflayer.spectrum.presentation.common.utils.Base64Utils;
import ru.magflayer.spectrum.presentation.common.utils.ColorUtils;
import rx.Observable;
import rx.subjects.BehaviorSubject;

public class ColorCameraPresenter extends BasePresenter<ColorCameraView, MainRouter> {

    private static final int SURFACE_UPDATE_DELAY_MILLIS = 300;

    private static final int SAVE_IMAGE_WIDTH = 640;
    private static final int SAVE_IMAGE_HEIGHT = 360;

    private static final String TAG_SINGLE_COLOR = "TAG_SINGLE_COLOR";
    private static final String TAG_MULTIPLE_COLOR = "TAG_MULTIPLE_COLOR";

    @Inject
    AnalyticsManager analyticsManager;
    @Inject
    ColorsInteractor colorsInteractor;

    private int previousColor = -1;
    private Map<String, String> colorInfoMap = new HashMap<>();
    private BehaviorSubject<SurfaceInfo.Type> changeObservable = BehaviorSubject.create();

    private int currentDetailsColor;

    @Inject
    ColorCameraPresenter() {
        super();

        execute(changeObservable
                        .sample(SURFACE_UPDATE_DELAY_MILLIS, TimeUnit.MILLISECONDS)
                        .flatMap(type -> {
                            Bitmap bitmap = getView().getSurfaceBitmap();
                            return Observable.just(new SurfaceInfo(type, bitmap));
                        }),
                surfaceInfo -> {
                    Bitmap bitmap = surfaceInfo.getBitmap();
                    if (surfaceInfo.getType() == SurfaceInfo.Type.MULTIPLE) {
                        handleCameraSurface(bitmap);
                    } else {
                        handleColorDetails(bitmap);
                    }
                },
                throwable -> logger.error("Error occurred while listening changing", throwable));

        loadColorNames();
    }

    @Override
    protected void inject() {
        InjectorManager.getAppComponent().inject(this);
    }

    void updateSurface(final SurfaceInfo.Type type) {
        changeObservable.onNext(type);
    }

    private void loadColorNames() {
        execute(colorsInteractor.loadColorNames()
                        .flatMap(Observable::from),
                colorInfo -> colorInfoMap.put(colorInfo.getId(), colorInfo.getName()));
    }

    private void handleCameraSurface(final Bitmap bitmap) {
        execute(TAG_MULTIPLE_COLOR, Observable.just(bitmap)
                        .flatMap(bitmap1 -> Observable.just(Palette.from(bitmap1).generate()))
                        .filter(palette -> {
                            //to reduce times of updating
                            Palette.Swatch dominantSwatch = palette.getDominantSwatch();
                            if (dominantSwatch == null) return true;
                            int currentColor = dominantSwatch.getRgb();

                            boolean needRefresh = currentColor != previousColor;
                            if (currentColor != previousColor) {
                                previousColor = currentColor;
                            }
                            return needRefresh;
                        })
                        .map(palette -> {
                            List<Palette.Swatch> colors = new ArrayList<>(palette.getSwatches());
                            //filtered by brightness
                            Collections.sort(colors,
                                    (lhs, rhs) -> Float.compare(lhs.getHsl()[2], rhs.getHsl()[2]));
                            return colors;
                        }),
                colors -> getView().showColors(colors));
    }

    private void handleColorDetails(final Bitmap bmp) {
        int centerX = bmp.getWidth() / 2;
        int centerY = bmp.getHeight() / 2;

        Palette.Swatch color = new Palette.Swatch(bmp.getPixel(centerX, centerY), 1);

        final String hexColor = ColorUtils.dec2Hex(color.getRgb());
        int newColor = Color.parseColor(hexColor);
        final int red = Color.red(newColor);
        final int green = Color.green(newColor);
        final int blue = Color.blue(newColor);

        execute(TAG_SINGLE_COLOR, Observable.from(colorInfoMap.keySet())
                        .reduce(Pair.create("", Integer.MAX_VALUE), (currentMin, s) -> {
                            if (TextUtils.isEmpty(s)) s = "#000000";
                            int otherColor = Color.parseColor(s);

                            int otherRed = Color.red(otherColor);
                            int otherGreen = Color.green(otherColor);
                            int otherBlue = Color.blue(otherColor);
                            int newFi = (int) (Math.pow(otherRed - red, 2)
                                    + Math.pow(otherGreen - green, 2)
                                    + Math.pow(otherBlue - blue, 2));

                            int result = currentMin.second > newFi ? newFi : currentMin.second;
                            String color1 = currentMin.second > newFi ? s : currentMin.first;
                            return Pair.create(color1, result);
                        })
                        .filter(aDouble -> aDouble.second != Integer.MAX_VALUE
                                && ColorUtils.isSameColor(currentDetailsColor, newColor)),
                result -> {
                    currentDetailsColor = color.getRgb();
                    getView().showColorDetails(color.getRgb(), color.getTitleTextColor());
                    getView().showColorName(colorInfoMap.get(result.first));
                },
                throwable -> logger.error("Error occurred: ", throwable));
    }

    void saveColorPicture(final Bitmap bitmap, final List<Palette.Swatch> swatches) {
        getView().showProgressBar();

        sendTakePhotoAnalytics();

        execute(Observable.just(bitmap)
                        .flatMap(bitmap1 ->
                                Observable.just(Bitmap.createScaledBitmap(bitmap1, SAVE_IMAGE_WIDTH, SAVE_IMAGE_HEIGHT, false)))
                        .flatMap((bitmap1 ->
                                Observable.just(Base64Utils.bitmapToBase64(bitmap1))))
                        .map(pictureBase64 ->
                                ColorPicture.fromBase64(pictureBase64, swatches)),
                colorPicture -> {
                    getView().hideProgressBar();
                    appRealm.savePicture(colorPicture);
                });
    }

    void openHistory() {
        getRouter().openHistory();
    }

    @Subscribe
    public void onPictureSaved(PictureSavedEvent event) {
        getView().showPictureSaved();
    }

    private void sendTakePhotoAnalytics() {
        Bundle bundle = new Bundle();
        String mode = changeObservable.hasValue() && changeObservable.getValue() == SurfaceInfo.Type.MULTIPLE
                ? AnalyticsEvent.TAKE_PHOTO_MODE_MULTIPLE : AnalyticsEvent.TAKE_PHOTO_MODE_SINGLE;
        bundle.putString(AnalyticsEvent.TAKE_PHOTO_MODE, mode);
        analyticsManager.logEvent(AnalyticsEvent.TAKE_PHOTO, bundle);
    }
}
