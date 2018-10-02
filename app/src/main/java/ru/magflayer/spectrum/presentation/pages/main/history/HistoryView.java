package ru.magflayer.spectrum.presentation.pages.main.history;

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import java.util.List;

import ru.magflayer.spectrum.domain.entity.ColorPhotoEntity;
import ru.magflayer.spectrum.presentation.common.mvp.view.PageView;

interface HistoryView extends PageView {

    @StateStrategyType(AddToEndSingleStrategy.class)
    void showHistory(List<ColorPhotoEntity> history);

    @StateStrategyType(OneExecutionStateStrategy.class)
    void openPickPhoto();

}
