package ru.magflayer.spectrum.presentation.pages.main.history;

import com.arellomobile.mvp.InjectViewState;

import java.util.List;

import javax.inject.Inject;

import ru.magflayer.spectrum.R;
import ru.magflayer.spectrum.data.android.ResourceManager;
import ru.magflayer.spectrum.domain.injection.InjectorManager;
import ru.magflayer.spectrum.domain.manager.AnalyticsManager;
import ru.magflayer.spectrum.domain.model.AnalyticsEvent;
import ru.magflayer.spectrum.domain.model.ColorPicture;
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

    @Override
    protected void inject() {
        InjectorManager.getAppComponent().inject(this);
    }

    @Override
    protected void onFirstViewAttach() {
        super.onFirstViewAttach();
        loadHistory();
    }

    @Override
    public ToolbarAppearance getToolbarAppearance() {
        return ToolbarAppearance.builder()
                .visible(ToolbarAppearance.Visibility.VISIBLE)
                .title(resourceManager.getString(R.string.history_toolbar_title))
                .build();
    }

    void removeColor(ColorPicture colorPicture) {
        appRealm.removePicture(colorPicture);
    }

    void handleColorSelected(final ColorPicture colorPicture) {
        final List<Integer> colors = colorPicture.getRgbColors();
        int quantity = colors != null ? colors.size() : 0;
        openHistoryDetails(colorPicture.getDateInMillis(), quantity);
    }

    private void openHistoryDetails(final long id, final int quantity) {
        mainRouter.openHistoryDetailsScreen(id, quantity);
    }

    private void loadHistory() {
        analyticsManager.logEvent(AnalyticsEvent.OPEN_HISTORY);
        List<ColorPicture> colorPictures = appRealm.loadPictures();
        getViewState().showHistory(colorPictures);
    }
}
