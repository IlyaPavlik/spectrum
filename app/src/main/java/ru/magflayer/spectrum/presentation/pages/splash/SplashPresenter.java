package ru.magflayer.spectrum.presentation.pages.splash;

import com.arellomobile.mvp.InjectViewState;

import java.util.concurrent.TimeUnit;

import ru.magflayer.spectrum.domain.injection.InjectorManager;
import ru.magflayer.spectrum.presentation.common.mvp.BasePresenter;
import ru.magflayer.spectrum.presentation.router.GlobalRouter;
import rx.Observable;

@InjectViewState
public class SplashPresenter extends BasePresenter<SplashView, GlobalRouter> {

    private static final int SPLASH_DELAY = 300;

    public void openMainPage() {
        Observable.timer(SPLASH_DELAY, TimeUnit.MILLISECONDS)
                .subscribe(aLong -> {
                    getRouter().openMainScreen();
                    getViewState().closeScreen();
                });
    }

    @Override
    protected void inject() {
        InjectorManager.getAppComponent().inject(this);
    }
}
