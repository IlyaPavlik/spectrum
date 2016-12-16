package ru.magflayer.spectrum.presentation.pages.main.router;

import android.support.v7.app.AppCompatActivity;

import ru.magflayer.spectrum.presentation.common.FragmentRouter;
import ru.magflayer.spectrum.presentation.pages.main.camera.ColorCameraFragment;
import ru.magflayer.spectrum.presentation.pages.main.history.HistoryFragment;
import ru.magflayer.spectrum.presentation.pages.main.splash.SplashFragment;

public class MainRouterImpl extends FragmentRouter implements MainRouter {

    public MainRouterImpl(AppCompatActivity activity) {
        super(activity);
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

    @Override
    public void handleBack() {
        int count = fragmentManager.getBackStackEntryCount();
        for (int i = 0; i < count; i++) {
            if (fragmentManager.popBackStackImmediate()) {
                return;
            }
        }

        activity.onBackPressed();
    }
}
