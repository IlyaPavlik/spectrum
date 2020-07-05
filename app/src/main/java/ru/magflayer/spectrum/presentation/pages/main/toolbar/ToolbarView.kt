package ru.magflayer.spectrum.presentation.pages.main.toolbar

import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.magflayer.spectrum.presentation.common.mvp.strategy.AddToEndSingleByTagStateStrategy
import ru.magflayer.spectrum.presentation.common.mvp.view.BaseView

interface ToolbarView : BaseView {

    companion object {
        const val TOOLBAR_VISIBLE_TAG = "toolbar_visible"
    }

    @StateStrategyType(value = AddToEndSingleByTagStateStrategy::class, tag = TOOLBAR_VISIBLE_TAG)
    fun showToolbar()

    @StateStrategyType(value = AddToEndSingleByTagStateStrategy::class, tag = TOOLBAR_VISIBLE_TAG)
    fun hideToolbar()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showTitle(title: String)

}
