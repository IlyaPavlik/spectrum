package ru.magflayer.spectrum.presentation.pages.main.camera;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.support.v7.graphics.Palette;

import com.arellomobile.mvp.InjectViewState;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import ru.magflayer.spectrum.domain.entity.AnalyticsEvent;
import ru.magflayer.spectrum.domain.entity.ColorPhotoEntity;
import ru.magflayer.spectrum.domain.entity.event.PictureSavedEvent;
import ru.magflayer.spectrum.domain.injection.InjectorManager;
import ru.magflayer.spectrum.domain.interactor.ColorInfoInteractor;
import ru.magflayer.spectrum.domain.interactor.ColorPhotoInteractor;
import ru.magflayer.spectrum.domain.interactor.FileManagerInteractor;
import ru.magflayer.spectrum.domain.manager.AnalyticsManager;
import ru.magflayer.spectrum.domain.manager.CameraManager;
import ru.magflayer.spectrum.presentation.common.android.navigation.router.MainRouter;
import ru.magflayer.spectrum.presentation.common.model.PageAppearance;
import ru.magflayer.spectrum.presentation.common.model.SurfaceInfo;
import ru.magflayer.spectrum.presentation.common.model.ToolbarAppearance;
import ru.magflayer.spectrum.presentation.common.mvp.BasePresenter;
import ru.magflayer.spectrum.presentation.common.utils.BitmapUtils;
import ru.magflayer.spectrum.presentation.common.utils.ColorUtils;
import rx.Observable;
import rx.subjects.BehaviorSubject;

@InjectViewState
public class ColorCameraPresenter extends BasePresenter<ColorCameraView> {

    private static final int SURFACE_UPDATE_DELAY_MILLIS = 300;

    private static final int SAVE_IMAGE_WIDTH = 640;
    private static final int SAVE_IMAGE_HEIGHT = 360;

    private static final String TAG_SINGLE_COLOR = "TAG_SINGLE_COLOR";
    private static final String TAG_MULTIPLE_COLOR = "TAG_MULTIPLE_COLOR";

    private static final String SAVE_FILE_FORMAT = "spectre_%d.png";

    @Inject
    AnalyticsManager analyticsManager;
    @Inject
    ColorInfoInteractor colorInfoInteractor;
    @Inject
    CameraManager cameraManager;
    @Inject
    MainRouter mainRouter;
    @Inject
    ColorPhotoInteractor colorPhotoInteractor;
    @Inject
    FileManagerInteractor fileManagerInteractor;

    private int previousColor = -1;
    private BehaviorSubject<SurfaceInfo.Type> changeObservable = BehaviorSubject.create();
    private int currentDetailsColor;
    private List<Palette.Swatch> swatches = new ArrayList<>();

    ColorCameraPresenter() {
        super();

        execute(changeObservable
                        .sample(SURFACE_UPDATE_DELAY_MILLIS, TimeUnit.MILLISECONDS)
                        .flatMap(type -> {
                            Bitmap bitmap = cameraManager.getCameraBitmap();
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
    }

    @Override
    protected void inject() {
        InjectorManager.getAppComponent().inject(this);
    }

    @Override
    public PageAppearance getPageAppearance() {
        return PageAppearance.builder()
                .showFloatingButton(false)
                .build();
    }

    @Override
    public ToolbarAppearance getToolbarAppearance() {
        return ToolbarAppearance.builder()
                .visible(ToolbarAppearance.Visibility.INVISIBLE)
                .build();
    }

    void updateSurface(final SurfaceInfo.Type type) {
        changeObservable.onNext(type);
    }

    void handleSurfaceAvailable(final SurfaceTexture surface) {
        try {
            cameraManager.startCamera(surface);
            cameraManager.updateCameraDisplayOrientation();
            getViewState().hideErrorMessage();
            getViewState().showCrosshair();
            getViewState().showPanels();
        } catch (Exception e) {
            logger.error("Error occurred while starting camera ", e);
            getViewState().showErrorMessage();
            getViewState().hideCrosshair();
        } finally {
            getViewState().hideProgressBar();
        }
    }

    void handleSurfaceDestroyed() {
        cameraManager.stopPreview();
    }

    void handleFocusClicked() {
        cameraManager.autoFocus();
    }

    void handleSaveClicked() {
        Bitmap bitmap = cameraManager.getCameraBitmap();
        saveColorPicture(bitmap, swatches);
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
                colors -> {
                    getViewState().showColors(colors);
                    swatches = new ArrayList<>(colors);
                });
    }

    private void handleColorDetails(final Bitmap bmp) {
        int centerX = bmp.getWidth() / 2;
        int centerY = bmp.getHeight() / 2;

        Palette.Swatch color = new Palette.Swatch(bmp.getPixel(centerX, centerY), 1);

        final String hexColor = ColorUtils.dec2Hex(color.getRgb());
        execute(TAG_SINGLE_COLOR, colorInfoInteractor.findColorNameByHex(hexColor),
                colorName -> {
                    currentDetailsColor = color.getRgb();
                    getViewState().showColorDetails(color.getRgb(), color.getTitleTextColor());
                    getViewState().showColorName(colorName);

                    swatches = Collections.singletonList(new Palette.Swatch(color.getRgb(), Integer.MAX_VALUE));
                },
                error -> logger.error("Error while finding color name: ", error));
    }

    void openHistory() {
        mainRouter.openHistoryScreen();
    }

    @Subscribe
    public void onPictureSaved(final PictureSavedEvent event) {
        getViewState().showPictureSavedToast();
    }

    @SuppressLint("DefaultLocale")
    private void saveColorPicture(final Bitmap bitmap, final List<Palette.Swatch> swatches) {
        getViewState().showProgressBar();

        sendTakePhotoAnalytics();

        execute(Observable.just(bitmap)
                        .map(bitmap1 -> Bitmap.createScaledBitmap(bitmap1, SAVE_IMAGE_WIDTH, SAVE_IMAGE_HEIGHT, false))
                        .flatMap(bitmap1 -> Observable.fromCallable(() -> BitmapUtils.convertBitmapToBytes(bitmap1)))
                        .flatMap(bytes -> {
                            String fileName = String.format(SAVE_FILE_FORMAT, System.currentTimeMillis());
                            return fileManagerInteractor.saveFileToExternalStorage(fileName, bytes);
                        })
                        .flatMap(uri -> {
                            List<Integer> rgbColors = convertSwatches(swatches);
                            return colorPhotoInteractor.saveColorPhoto(new ColorPhotoEntity(uri.getPath(), rgbColors));
                        }),
                success -> {
                    getViewState().hideProgressBar();
                    getViewState().showPictureSavedToast();
                },
                error -> logger.warn("Error while saving photo: ", error));
    }

    private void sendTakePhotoAnalytics() {
        Bundle bundle = new Bundle();
        String mode = changeObservable.hasValue() && changeObservable.getValue() == SurfaceInfo.Type.MULTIPLE
                ? AnalyticsEvent.TAKE_PHOTO_MODE_MULTIPLE : AnalyticsEvent.TAKE_PHOTO_MODE_SINGLE;
        bundle.putString(AnalyticsEvent.TAKE_PHOTO_MODE, mode);
        analyticsManager.logEvent(AnalyticsEvent.TAKE_PHOTO, bundle);
    }

    private List<Integer> convertSwatches(final List<Palette.Swatch> swatches) {
        final List<Integer> colors = new ArrayList<>();
        for (Palette.Swatch swatch : swatches) {
            colors.add(swatch.getRgb());
        }
        return colors;
    }
}
