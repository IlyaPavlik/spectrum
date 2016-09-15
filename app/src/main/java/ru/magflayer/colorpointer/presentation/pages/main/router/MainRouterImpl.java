package ru.magflayer.colorpointer.presentation.pages.main.router;

import android.support.v4.app.FragmentManager;

import ru.magflayer.colorpointer.presentation.common.FragmentRouter;
import ru.magflayer.colorpointer.presentation.pages.main.camera.ColorCameraFragment;
import ru.magflayer.colorpointer.presentation.pages.main.history.HistoryFragment;
import ru.magflayer.colorpointer.presentation.pages.main.splash.SplashFragment;

public class MainRouterImpl extends FragmentRouter implements MainRouter {

    public MainRouterImpl(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public void openSplash() {
        replaceFragment(SplashFragment.newInstance(), false);
    }

    @Override
    public void openColorCameraPage() {
        replaceFragment(ColorCameraFragment.newInstance(), false);
    }

    @Override
    public void openHistory() {
        replaceFragment(HistoryFragment.newInstance(), true);
    }
}
