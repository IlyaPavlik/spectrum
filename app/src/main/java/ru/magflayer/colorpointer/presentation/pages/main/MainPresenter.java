package ru.magflayer.colorpointer.presentation.pages.main;

import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import ru.magflayer.colorpointer.domain.event.PageAppearanceEvent;
import ru.magflayer.colorpointer.domain.model.PageAppearance;
import ru.magflayer.colorpointer.presentation.common.BasePresenter;
import ru.magflayer.colorpointer.presentation.pages.main.router.MainRouter;

public class MainPresenter extends BasePresenter<MainView, MainRouter> {

    @Inject
    public MainPresenter() {
        super();
    }

    public void openSplashScreen() {
        getRouter().openSplash();
    }

    public void openMainScreen() {
        getRouter().openColorCameraPage();
    }

    @Subscribe
    public void onPageAppearanceChanged(PageAppearanceEvent event) {
        PageAppearance pageAppearance = event.getPageAppearance();

        getView().showToolbar(pageAppearance.isShowToolbar());
        getView().showFloatingButton(pageAppearance.isShowFloatingButton());
    }

}
