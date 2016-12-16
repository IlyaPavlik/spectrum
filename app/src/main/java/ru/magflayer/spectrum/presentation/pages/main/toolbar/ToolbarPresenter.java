package ru.magflayer.spectrum.presentation.pages.main.toolbar;

import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import ru.magflayer.spectrum.domain.model.ToolbarAppearance;
import ru.magflayer.spectrum.presentation.common.BasePresenter;
import ru.magflayer.spectrum.presentation.pages.main.router.MainRouter;

public class ToolbarPresenter extends BasePresenter<ToolbarView, MainRouter> {

    @Inject
    ToolbarPresenter() {
        super();
    }

    void handleBack() {
        getRouter().handleBack();
    }

    @Subscribe
    public void onToolbarAppearance(ToolbarAppearance toolbarAppearance) {
        getView().setupToolbarAppearance(toolbarAppearance);
    }
}
