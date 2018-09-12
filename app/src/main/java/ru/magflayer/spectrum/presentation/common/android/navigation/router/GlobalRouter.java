package ru.magflayer.spectrum.presentation.common.android.navigation.router;

import ru.magflayer.spectrum.presentation.common.android.navigation.Screens;
import ru.terrakok.cicerone.Router;

public class GlobalRouter extends Router {

    public void startSplash() {
        replaceScreen(Screens.SPLASH_ACTIVITY_SCREEN);
    }

    public void startMain() {
        replaceScreen(Screens.MAIN_ACTIVITY_SCREEN);
    }

}
