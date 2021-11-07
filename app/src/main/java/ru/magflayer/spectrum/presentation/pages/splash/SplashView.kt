package ru.magflayer.spectrum.presentation.pages.splash

import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.magflayer.spectrum.presentation.common.mvp.view.BaseView

interface SplashView : BaseView {

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun openMainScreen()

}
