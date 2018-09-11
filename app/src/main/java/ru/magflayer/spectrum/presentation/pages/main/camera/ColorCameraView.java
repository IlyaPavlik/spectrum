package ru.magflayer.spectrum.presentation.pages.main.camera;

import android.support.v7.graphics.Palette;

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import java.util.List;

import ru.magflayer.spectrum.presentation.common.mvp.strategy.AddToEndSingleByTagStateStrategy;
import ru.magflayer.spectrum.presentation.common.mvp.view.PageView;

interface ColorCameraView extends PageView {

    String ERROR_MESSAGE_TAG = "error_message";
    String CROSSHAIR_TAG = "crosshair";
    String PANELS_TAG = "panels";

    @StateStrategyType(OneExecutionStateStrategy.class)
    void showPictureSavedToast();

    @StateStrategyType(AddToEndSingleStrategy.class)
    void showColors(List<Palette.Swatch> colors);

    @StateStrategyType(AddToEndSingleStrategy.class)
    void showColorDetails(int mainColor, int titleColor);

    @StateStrategyType(AddToEndSingleStrategy.class)
    void showColorName(String name);

    @StateStrategyType(value = AddToEndSingleByTagStateStrategy.class, tag = ERROR_MESSAGE_TAG)
    void showErrorMessage();

    @StateStrategyType(value = AddToEndSingleByTagStateStrategy.class, tag = ERROR_MESSAGE_TAG)
    void hideErrorMessage();

    @StateStrategyType(value = AddToEndSingleByTagStateStrategy.class, tag = CROSSHAIR_TAG)
    void showCrosshair();

    @StateStrategyType(value = AddToEndSingleByTagStateStrategy.class, tag = CROSSHAIR_TAG)
    void hideCrosshair();

    @StateStrategyType(value = AddToEndSingleByTagStateStrategy.class, tag = PANELS_TAG)
    void showPanels();

}
