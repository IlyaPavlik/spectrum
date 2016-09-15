package ru.magflayer.colorpointer.presentation.pages.splash;

import javax.inject.Inject;

import ru.magflayer.colorpointer.presentation.common.BasePresenter;
import ru.magflayer.colorpointer.presentation.pages.router.GlobalRouter;

public class SplashPresenter extends BasePresenter<SplashView, GlobalRouter> {

    @Inject
    public SplashPresenter() {
        super();
    }

    public void openMainPage() {
        getRouter().openMainScreen();
    }

}
