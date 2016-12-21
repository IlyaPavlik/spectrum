package ru.magflayer.spectrum.presentation.router;

import android.content.Context;

import ru.magflayer.spectrum.presentation.common.ActivityRouter;
import ru.magflayer.spectrum.presentation.pages.main.MainActivity;
import ru.magflayer.spectrum.presentation.pages.splash.SplashActivity;

public class GlobalRouterImpl extends ActivityRouter implements GlobalRouter {

    public GlobalRouterImpl(Context context) {
        super(context);
    }

    @Override
    public void openMainScreen() {
        startActivity(MainActivity.class, false);
    }

    @Override
    public void openSplashScreen() {
        startActivity(SplashActivity.class, true);
    }
}
