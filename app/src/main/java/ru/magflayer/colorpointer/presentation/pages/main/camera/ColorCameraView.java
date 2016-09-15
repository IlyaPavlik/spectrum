package ru.magflayer.colorpointer.presentation.pages.main.camera;

import android.support.v7.graphics.Palette;

import java.util.List;

public interface ColorCameraView {

    void showPictureSaved();

    void showColors(List<Palette.Swatch> colors);

    void showColorDetails(int mainColor, int titleColor);

}
