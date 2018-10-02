package ru.magflayer.spectrum.presentation.pages.main.history;

import android.graphics.Bitmap;
import android.support.v7.graphics.Palette;

import com.arellomobile.mvp.InjectViewState;
import com.squareup.otto.Subscribe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import ru.magflayer.spectrum.R;
import ru.magflayer.spectrum.data.android.ResourceManager;
import ru.magflayer.spectrum.domain.entity.AnalyticsEvent;
import ru.magflayer.spectrum.domain.entity.ColorPhotoEntity;
import ru.magflayer.spectrum.domain.entity.event.FabClickEvent;
import ru.magflayer.spectrum.domain.injection.InjectorManager;
import ru.magflayer.spectrum.domain.interactor.ColorPhotoInteractor;
import ru.magflayer.spectrum.domain.manager.AnalyticsManager;
import ru.magflayer.spectrum.presentation.common.android.navigation.router.MainRouter;
import ru.magflayer.spectrum.presentation.common.model.PageAppearance;
import ru.magflayer.spectrum.presentation.common.model.ToolbarAppearance;
import ru.magflayer.spectrum.presentation.common.mvp.BasePresenter;
import rx.Observable;

@InjectViewState
public class HistoryPresenter extends BasePresenter<HistoryView> {

    @Inject
    AnalyticsManager analyticsManager;
    @Inject
    ResourceManager resourceManager;
    @Inject
    MainRouter mainRouter;
    @Inject
    ColorPhotoInteractor colorPhotoInteractor;

    @Override
    protected void inject() {
        InjectorManager.getAppComponent().inject(this);
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        loadHistory();
        analyticsManager.logEvent(AnalyticsEvent.OPEN_HISTORY);
    }

    @Override
    public ToolbarAppearance getToolbarAppearance() {
        return ToolbarAppearance.builder()
                .visible(ToolbarAppearance.Visibility.VISIBLE)
                .title(resourceManager.getString(R.string.history_toolbar_title))
                .build();
    }

    @Override
    protected PageAppearance getPageAppearance() {
        return PageAppearance.builder()
                .showFloatingButton(true)
                .build();
    }

    @Subscribe
    public void onFabClicked(final FabClickEvent event) {
        getViewState().openPickPhoto();
        analyticsManager.logEvent(AnalyticsEvent.CHOOSE_IMAGE);
    }

    void removeColor(final ColorPhotoEntity entity) {
        execute(colorPhotoInteractor.removeColorPhoto(entity),
                success -> logger.debug("Photo removed {}", success ? "successfully" : "unsuccessfully"),
                error -> logger.warn("Error while removing photo: ", error));
    }

    void handleColorSelected(final ColorPhotoEntity entity) {
        mainRouter.openHistoryDetailsScreen(entity.getFilePath());
    }

    void handleSelectedImage(final String path, final Bitmap bitmap) {
        logger.debug("Image selected: {}", path);
        getViewState().showProgressBar();
        execute(Observable.just(bitmap)
                        .flatMap(b -> Observable.just(Palette.from(b).generate()))
                        .map(palette -> {
                            List<Palette.Swatch> colors = new ArrayList<>(palette.getSwatches());
                            //filtered by brightness
                            Collections.sort(colors,
                                    (lhs, rhs) -> Float.compare(lhs.getHsl()[2], rhs.getHsl()[2]));
                            return colors;
                        })
                        .flatMap(swatches -> {
                            List<Integer> rgbColors = convertSwatches(swatches);
                            return colorPhotoInteractor.saveColorPhoto(new ColorPhotoEntity(path, rgbColors));
                        }),
                success -> {
                    logger.debug("Image saved: {}", success);
                    getViewState().hideProgressBar();
                    if (success) loadHistory();
                },
                error -> {
                    logger.warn("Error while saving image: ", error);
                    getViewState().hideProgressBar();
                });
    }

    private void loadHistory() {
        execute(colorPhotoInteractor.loadColorPhotos(),
                entities -> getViewState().showHistory(entities),
                error -> logger.warn("Error while loading color photos: ", error));
    }

    private List<Integer> convertSwatches(final List<Palette.Swatch> swatches) {
        final List<Integer> colors = new ArrayList<>();
        for (Palette.Swatch swatch : swatches) {
            colors.add(swatch.getRgb());
        }
        return colors;
    }
}
