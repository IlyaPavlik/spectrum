package ru.magflayer.spectrum.presentation.pages.main;

import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import ru.magflayer.spectrum.domain.injection.InjectorManager;
import ru.magflayer.spectrum.presentation.common.model.PageAppearance;
import ru.magflayer.spectrum.presentation.common.BasePresenter;
import ru.magflayer.spectrum.presentation.pages.main.router.MainRouter;

public class MainPresenter extends BasePresenter<MainView, MainRouter> {

    @Inject
    MainPresenter() {
        super();
    }

    @Override
    protected void inject() {
        InjectorManager.getAppComponent().inject(this);
    }

    public void openSplashScreen() {
        getRouter().openSplash();
    }

    void openMainScreen() {
        getRouter().openColorCameraPage();
    }

    @Subscribe
    public void onPageAppearanceChanged(PageAppearance pageAppearance) {
        getView().showFloatingButton(pageAppearance.isShowFloatingButton());
    }
}
