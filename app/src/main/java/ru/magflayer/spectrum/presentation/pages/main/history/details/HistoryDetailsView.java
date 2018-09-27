package ru.magflayer.spectrum.presentation.pages.main.history.details;

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import ru.magflayer.spectrum.domain.entity.ColorPhotoEntity;
import ru.magflayer.spectrum.presentation.common.mvp.view.BaseView;

interface HistoryDetailsView extends BaseView {

    @StateStrategyType(AddToEndSingleStrategy.class)
    void showPhoto(ColorPhotoEntity entity);

    @StateStrategyType(AddToEndSingleStrategy.class)
    void showColorName(String name);

    @StateStrategyType(AddToEndSingleStrategy.class)
    void showRgb(int color);

    @StateStrategyType(AddToEndSingleStrategy.class)
    void showRyb(int color);

    @StateStrategyType(AddToEndSingleStrategy.class)
    void showCmyk(int color);

    @StateStrategyType(AddToEndSingleStrategy.class)
    void showHsv(int color);

    @StateStrategyType(AddToEndSingleStrategy.class)
    void showXyz(int color);

    @StateStrategyType(AddToEndSingleStrategy.class)
    void showLab(int color);

    @StateStrategyType(AddToEndSingleStrategy.class)
    void showNcs(int color, String ncsName);

}
