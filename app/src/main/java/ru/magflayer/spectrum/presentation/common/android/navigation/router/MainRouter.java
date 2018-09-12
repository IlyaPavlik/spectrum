package ru.magflayer.spectrum.presentation.common.android.navigation.router;

import ru.magflayer.spectrum.presentation.common.android.navigation.Screens;
import ru.magflayer.spectrum.presentation.common.model.HistoryItem;
import ru.terrakok.cicerone.Router;

public class MainRouter extends Router {

    public void openCameraScreen() {
        newRootScreen(Screens.CAMERA_SCREEN);
    }

    public void openHistoryScreen() {
        navigateTo(Screens.HISTORY_SCREEN);
    }

    public void openHistoryDetailsScreen(final long id, final int colorQuantity) {
        navigateTo(Screens.HISTORY_DETAILS_SCREEN, new HistoryItem(id, colorQuantity));
    }

}
