package ru.magflayer.spectrum.presentation.pages.main.history.details;

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import ru.magflayer.spectrum.domain.model.ColorPicture;
import ru.magflayer.spectrum.presentation.common.mvp.view.BaseView;

interface HistoryDetailsView extends BaseView {

    @StateStrategyType(AddToEndSingleStrategy.class)
    void showPicture(ColorPicture colorPicture);

    @StateStrategyType(OneExecutionStateStrategy.class)
    void selectFirstItem();

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
