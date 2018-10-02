package ru.magflayer.spectrum.presentation.pages.main.history.details;

import com.arellomobile.mvp.InjectViewState;

import javax.inject.Inject;

import ru.magflayer.spectrum.R;
import ru.magflayer.spectrum.data.android.ResourceManager;
import ru.magflayer.spectrum.domain.entity.AnalyticsEvent;
import ru.magflayer.spectrum.domain.injection.InjectorManager;
import ru.magflayer.spectrum.domain.interactor.ColorInfoInteractor;
import ru.magflayer.spectrum.domain.interactor.ColorPhotoInteractor;
import ru.magflayer.spectrum.domain.manager.AnalyticsManager;
import ru.magflayer.spectrum.presentation.common.model.PageAppearance;
import ru.magflayer.spectrum.presentation.common.model.ToolbarAppearance;
import ru.magflayer.spectrum.presentation.common.mvp.BasePresenter;
import ru.magflayer.spectrum.presentation.common.utils.ColorUtils;

@InjectViewState
public class HistoryDetailsPresenter extends BasePresenter<HistoryDetailsView> {

    @Inject
    AnalyticsManager analyticsManager;
    @Inject
    ColorInfoInteractor colorInfoInteractor;
    @Inject
    ResourceManager resourceManager;
    @Inject
    ColorPhotoInteractor colorPhotoInteractor;

    HistoryDetailsPresenter(final String filePath) {
        loadPicture(filePath);
    }

    @Override
    protected void inject() {
        InjectorManager.getAppComponent().inject(this);
    }

    @Override
    public void attachView(final HistoryDetailsView view) {
        super.attachView(view);
        analyticsManager.logEvent(AnalyticsEvent.OPEN_HISTORY_DETAILS);
    }

    private void loadPicture(final String filePath) {
        execute(colorPhotoInteractor.loadColorPhoto(filePath)
                        .filter(entity -> entity != null),
                entity -> getViewState().showPhoto(entity),
                error -> logger.warn("Error while loading photo: ", error));
    }

    void handleSelectedColor(final int color) {
        getViewState().showRgb(color);
        getViewState().showRyb(color);
        getViewState().showCmyk(color);
        getViewState().showHsv(color);
        getViewState().showXyz(color);
        getViewState().showLab(color);

        String hex = ColorUtils.dec2Hex(color);
        execute(colorInfoInteractor.findNcsColorByHex(hex),
                ncsName -> getViewState().showNcs(color, ncsName));

        handleColorDetails(color);
    }

    @Override
    public ToolbarAppearance getToolbarAppearance() {
        return ToolbarAppearance.builder()
                .visible(ToolbarAppearance.Visibility.VISIBLE)
                .title(resourceManager.getString(R.string.history_details_title))
                .build();
    }

    @Override
    protected PageAppearance getPageAppearance() {
        return PageAppearance.builder()
                .showFloatingButton(false)
                .build();
    }

    private void handleColorDetails(final int color) {
        final String hexColor = ColorUtils.dec2Hex(color);
        execute(colorInfoInteractor.findColorNameByHex(hexColor),
                getViewState()::showColorName,
                error -> logger.error("Error while finding color name: ", error));
    }
}
