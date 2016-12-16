package ru.magflayer.spectrum.presentation.pages.main.camera;

import android.graphics.Bitmap;
import android.support.v7.graphics.Palette;

import java.util.List;

import ru.magflayer.spectrum.presentation.common.BaseView;
import ru.magflayer.spectrum.presentation.common.PageView;

interface ColorCameraView extends PageView {

    void showPictureSaved();

    void showColors(List<Palette.Swatch> colors);

    void showColorDetails(int mainColor, int titleColor);

    void showColorName(String name);

    Bitmap getSurfaceBitmap();

}
