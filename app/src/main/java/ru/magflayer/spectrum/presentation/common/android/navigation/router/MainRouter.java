package ru.magflayer.spectrum.presentation.common.android.navigation.router;

import ru.magflayer.spectrum.presentation.common.android.navigation.Screens;
import ru.terrakok.cicerone.Router;

public class MainRouter extends Router {

    public void openCameraScreen() {
        newRootScreen(Screens.CAMERA_SCREEN);
    }

    public void openHistoryScreen() {
        navigateTo(Screens.HISTORY_SCREEN);
    }

    public void openHistoryDetailsScreen(final String filePath) {
        navigateTo(Screens.HISTORY_DETAILS_SCREEN, filePath);
    }

}
