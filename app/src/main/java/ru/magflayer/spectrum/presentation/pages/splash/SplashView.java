package ru.magflayer.spectrum.presentation.pages.splash;

import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import ru.magflayer.spectrum.presentation.common.mvp.view.BaseView;

interface SplashView extends BaseView {

    @StateStrategyType(OneExecutionStateStrategy.class)
    void closeScreen();

}
