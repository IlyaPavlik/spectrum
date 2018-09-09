package ru.magflayer.spectrum.presentation.pages.splash;

import javax.inject.Inject;

import ru.magflayer.spectrum.domain.injection.InjectorManager;
import ru.magflayer.spectrum.presentation.common.BasePresenter;
import ru.magflayer.spectrum.presentation.router.GlobalRouter;

public class SplashPresenter extends BasePresenter<SplashView, GlobalRouter> {

    @Inject
    public SplashPresenter() {
        super();
    }

    public void openMainPage() {
        getRouter().openMainScreen();
    }

    @Override
    protected void inject() {
        InjectorManager.getAppComponent().inject(this);
    }
}
