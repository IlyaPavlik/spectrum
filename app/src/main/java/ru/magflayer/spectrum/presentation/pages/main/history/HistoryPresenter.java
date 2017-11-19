package ru.magflayer.spectrum.presentation.pages.main.history;

import java.util.List;

import javax.inject.Inject;

import ru.magflayer.spectrum.domain.manager.AnalyticsManager;
import ru.magflayer.spectrum.domain.model.AnalyticsEvent;
import ru.magflayer.spectrum.domain.model.ColorPicture;
import ru.magflayer.spectrum.presentation.common.BasePresenter;
import ru.magflayer.spectrum.presentation.pages.main.router.MainRouter;

public class HistoryPresenter extends BasePresenter<HistoryView, MainRouter> {

    @Inject
    AnalyticsManager analyticsManager;

    @Inject
    HistoryPresenter() {
        super();
    }

    void loadHistory() {
        analyticsManager.logEvent(AnalyticsEvent.OPEN_HISTORY);
        List<ColorPicture> colorPictures = appRealm.loadPictures();
        getView().showHistory(colorPictures);
    }

    void removeColor(ColorPicture colorPicture) {
        appRealm.removePicture(colorPicture);
    }
}
