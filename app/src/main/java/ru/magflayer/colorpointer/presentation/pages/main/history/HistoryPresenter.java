package ru.magflayer.colorpointer.presentation.pages.main.history;

import java.util.List;

import javax.inject.Inject;

import ru.magflayer.colorpointer.domain.model.ColorPicture;
import ru.magflayer.colorpointer.presentation.common.BasePresenter;
import ru.magflayer.colorpointer.presentation.pages.main.router.MainRouter;

public class HistoryPresenter extends BasePresenter<HistoryView, MainRouter> {

    @Inject
    public HistoryPresenter() {
        super();
    }

    public void loadHistory() {
        List<ColorPicture> colorPictures = appRealm.loadPictures();
        getView().showHistory(colorPictures);
    }
}
