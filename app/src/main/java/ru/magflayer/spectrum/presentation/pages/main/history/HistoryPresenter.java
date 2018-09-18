package ru.magflayer.spectrum.presentation.pages.main.history;

import com.arellomobile.mvp.InjectViewState;

import javax.inject.Inject;

import ru.magflayer.spectrum.R;
import ru.magflayer.spectrum.data.android.ResourceManager;
import ru.magflayer.spectrum.domain.entity.AnalyticsEvent;
import ru.magflayer.spectrum.domain.entity.ColorPhotoEntity;
import ru.magflayer.spectrum.domain.injection.InjectorManager;
import ru.magflayer.spectrum.domain.interactor.ColorPhotoInteractor;
import ru.magflayer.spectrum.domain.manager.AnalyticsManager;
import ru.magflayer.spectrum.presentation.common.android.navigation.router.MainRouter;
import ru.magflayer.spectrum.presentation.common.model.ToolbarAppearance;
import ru.magflayer.spectrum.presentation.common.mvp.BasePresenter;

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

    void removeColor(final ColorPhotoEntity entity) {
        execute(colorPhotoInteractor.removeColorPhoto(entity),
                success -> logger.debug("Photo removed {}", success ? "successfully" : "unsuccessfully"),
                error -> logger.warn("Error while removing photo: ", error));
    }

    void handleColorSelected(final ColorPhotoEntity entity) {
        mainRouter.openHistoryDetailsScreen(entity.getFilePath());
    }

    private void loadHistory() {
        execute(colorPhotoInteractor.loadColorPhotos(),
                entities -> getViewState().showHistory(entities),
                error -> logger.warn("Error while loading color photos: ", error));
    }
}
