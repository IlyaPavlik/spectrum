package ru.magflayer.colorpointer.presentation.pages.main.splash;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import ru.magflayer.colorpointer.presentation.common.BasePresenter;
import ru.magflayer.colorpointer.presentation.pages.main.router.MainRouter;
import ru.magflayer.colorpointer.utils.RxUtils;
import rx.Observable;
import rx.functions.Action1;

public class SplashPresenter extends BasePresenter<SplashView, MainRouter> {

    private static final int OPEN_PAGE_DELAY_SECOND = 2;

    @Inject
    public SplashPresenter() {
        super();
    }

    public void openMainPageWithDelay() {
        Observable.timer(OPEN_PAGE_DELAY_SECOND, TimeUnit.SECONDS)
                .compose(RxUtils.<Long>applySchedulers())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        getRouter().openColorCameraPage();
                    }
                });
    }

}
