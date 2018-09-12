package ru.magflayer.spectrum.presentation.pages.splash;

import com.arellomobile.mvp.InjectViewState;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import ru.magflayer.spectrum.domain.injection.InjectorManager;
import ru.magflayer.spectrum.presentation.common.android.navigation.router.GlobalRouter;
import ru.magflayer.spectrum.presentation.common.mvp.BasePresenter;
import rx.Observable;

@InjectViewState
public class SplashPresenter extends BasePresenter<SplashView> {

    private static final int SPLASH_DELAY = 300;

    @Inject
    GlobalRouter globalRouter;

    public void openMainPage() {
        Observable.timer(SPLASH_DELAY, TimeUnit.MILLISECONDS)
                .subscribe(aLong -> globalRouter.startMain());
    }

    @Override
    protected void inject() {
        InjectorManager.getAppComponent().inject(this);
    }
}
