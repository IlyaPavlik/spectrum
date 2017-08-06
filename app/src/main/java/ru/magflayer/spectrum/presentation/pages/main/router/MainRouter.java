package ru.magflayer.spectrum.presentation.pages.main.router;

import ru.magflayer.spectrum.domain.model.ColorPicture;

public interface MainRouter {

    void openSplash();

    void openColorCameraPage();

    void openHistory();

    void openHistoryDetails(ColorPicture colorPicture);

    void handleBack();

}
