package ru.magflayer.spectrum.presentation.pages.main

import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.magflayer.spectrum.presentation.common.mvp.view.BaseView

interface MainView : BaseView {

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showToolbar(showToolbar: Boolean)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showFloatingButton(showFloatingButton: Boolean)

}
