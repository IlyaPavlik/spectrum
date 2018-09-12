package ru.magflayer.spectrum.presentation.pages.main;

import com.arellomobile.mvp.InjectViewState;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import ru.magflayer.spectrum.domain.injection.InjectorManager;
import ru.magflayer.spectrum.domain.manager.CameraManager;
import ru.magflayer.spectrum.presentation.common.android.navigation.router.MainRouter;
import ru.magflayer.spectrum.presentation.common.model.PageAppearance;
import ru.magflayer.spectrum.presentation.common.mvp.BasePresenter;

@InjectViewState
public class MainPresenter extends BasePresenter<MainView> {

    @Inject
    CameraManager cameraManager;
    @Inject
    MainRouter mainRouter;

    @Override
    protected void inject() {
        InjectorManager.getAppComponent().inject(this);
    }

    @Override
    public void attachView(final MainView view) {
        super.attachView(view);
        try {
            cameraManager.open();
        } catch (RuntimeException e) {
            logger.error("Camera not available: ", e);
        }
    }

    @Override
    public void detachView(final MainView view) {
        super.detachView(view);
        cameraManager.close();
    }

    void openMainScreen() {
        mainRouter.openCameraScreen();
    }

    @Subscribe
    public void onPageAppearanceChanged(final PageAppearance pageAppearance) {
        getViewState().showFloatingButton(pageAppearance.isShowFloatingButton());
    }
}
