package ru.magflayer.spectrum.presentation.pages.main.toolbar

import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.AddToEndSingleTagStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.magflayer.spectrum.presentation.common.mvp.view.BaseView

interface ToolbarView : BaseView {

    companion object {
        const val TOOLBAR_VISIBLE_TAG = "toolbar_visible"
    }

    @StateStrategyType(value = AddToEndSingleTagStrategy::class, tag = TOOLBAR_VISIBLE_TAG)
    fun showToolbar()

    @StateStrategyType(value = AddToEndSingleTagStrategy::class, tag = TOOLBAR_VISIBLE_TAG)
    fun hideToolbar()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showTitle(title: String)
}
