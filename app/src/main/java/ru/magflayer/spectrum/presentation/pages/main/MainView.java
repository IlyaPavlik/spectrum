package ru.magflayer.spectrum.presentation.pages.main;

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import ru.magflayer.spectrum.presentation.common.mvp.view.BaseView;

interface MainView extends BaseView {

    @StateStrategyType(OneExecutionStateStrategy.class)
    void showToolbar(boolean showToolbar);

    @StateStrategyType(OneExecutionStateStrategy.class)
    void showFloatingButton(boolean showFloatingButton);

}
