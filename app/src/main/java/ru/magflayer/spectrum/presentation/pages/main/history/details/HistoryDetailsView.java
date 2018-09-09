package ru.magflayer.spectrum.presentation.pages.main.history.details;

import ru.magflayer.spectrum.domain.model.ColorPicture;
import ru.magflayer.spectrum.presentation.common.BaseView;

interface HistoryDetailsView extends BaseView {

    void showPicture(ColorPicture colorPicture);

    void colorLoaded();

    void showColorName(String name);

    void showRgb(int color);

    void showRyb(int color);

    void showCmyk(int color);

    void showHsv(int color);

    void showXyz(int color);

    void showLab(int color);

    void showNcs(int color, String ncsName);

}
