package ru.magflayer.spectrum.presentation.pages.splash;

import com.arellomobile.mvp.InjectViewState;

import javax.inject.Inject;

import ru.magflayer.spectrum.domain.injection.InjectorManager;
import ru.magflayer.spectrum.domain.interactor.ColorInfoInteractor;
import ru.magflayer.spectrum.presentation.common.android.navigation.router.GlobalRouter;
import ru.magflayer.spectrum.presentation.common.mvp.BasePresenter;

@InjectViewState
public class SplashPresenter extends BasePresenter<SplashView> {

    @Inject
    GlobalRouter globalRouter;
    @Inject
    ColorInfoInteractor colorInfoInteractor;

    void openMainPage() {
        execute(colorInfoInteractor.uploadColorInfo(),
                colorInfoState -> logger.debug("Color info loaded: {}", colorInfoState),
                error -> {
                    logger.warn("Error while uploaded color info: ", error);
                    globalRouter.startMain();
                },
                () -> globalRouter.startMain());
    }

    @Override
    protected void inject() {
        InjectorManager.getAppComponent().inject(this);
    }
}
