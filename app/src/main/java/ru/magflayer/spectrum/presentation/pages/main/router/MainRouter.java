package ru.magflayer.spectrum.presentation.pages.main.router;

public interface MainRouter {

    void openColorCameraPage();

    void openHistory();

    void openHistoryDetails(long colorPictureId, int colorQuantity);

    void handleBack();

}
