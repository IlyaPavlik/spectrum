package ru.magflayer.spectrum.presentation.common.mvp.view;


import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import ru.magflayer.spectrum.presentation.common.mvp.strategy.AddToEndSingleByTagStateStrategy;

public interface PageView extends BaseView {

    String PROGRESS_TAG = "progress_tag";

    @StateStrategyType(value = AddToEndSingleByTagStateStrategy.class, tag = PROGRESS_TAG)
    void showProgressBar();

    @StateStrategyType(value = AddToEndSingleByTagStateStrategy.class, tag = PROGRESS_TAG)
    void hideProgressBar();

}
