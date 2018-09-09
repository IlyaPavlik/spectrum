package ru.magflayer.spectrum.presentation.pages.main.splash;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import ru.magflayer.spectrum.domain.injection.InjectorManager;
import ru.magflayer.spectrum.presentation.common.BasePresenter;
import ru.magflayer.spectrum.presentation.pages.main.router.MainRouter;
import ru.magflayer.spectrum.common.utils.RxUtils;
import rx.Observable;

public class SplashPresenter extends BasePresenter<SplashView, MainRouter> {

    private static final int OPEN_PAGE_DELAY_SECOND = 2;

    @Inject
    public SplashPresenter() {
        super();
    }

    @Override
    protected void inject() {
        InjectorManager.getAppComponent().inject(this);
    }

    public void openMainPageWithDelay() {
        Observable.timer(OPEN_PAGE_DELAY_SECOND, TimeUnit.SECONDS)
                .compose(RxUtils.applySchedulers())
                .subscribe(aLong -> getRouter().openColorCameraPage());
    }
}
