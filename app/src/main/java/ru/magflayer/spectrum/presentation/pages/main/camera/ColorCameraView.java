package ru.magflayer.spectrum.presentation.pages.main.camera;

import android.support.v7.graphics.Palette;

import java.util.List;

interface ColorCameraView {

    void showPictureSaved();

    void showColors(List<Palette.Swatch> colors);

    void showColorDetails(int mainColor, int titleColor);

    void showColorName(String name);

}
