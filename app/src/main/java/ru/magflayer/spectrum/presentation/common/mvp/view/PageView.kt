package ru.magflayer.spectrum.presentation.common.mvp.view

import moxy.viewstate.strategy.AddToEndSingleTagStrategy
import moxy.viewstate.strategy.StateStrategyType

interface PageView : BaseView {

    companion object {
        const val PROGRESS_TAG = "progress_tag"
    }

    @StateStrategyType(value = AddToEndSingleTagStrategy::class, tag = PROGRESS_TAG)
    fun showProgressBar()

    @StateStrategyType(value = AddToEndSingleTagStrategy::class, tag = PROGRESS_TAG)
    fun hideProgressBar()
}
