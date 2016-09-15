package ru.magflayer.colorpointer.presentation.pages.router;

import android.content.Context;

import ru.magflayer.colorpointer.presentation.common.ActivityRouter;
import ru.magflayer.colorpointer.presentation.pages.main.MainActivity;
import ru.magflayer.colorpointer.presentation.pages.splash.SplashActivity;

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
