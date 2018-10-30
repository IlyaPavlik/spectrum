package ru.magflayer.spectrum.presentation.common.mvp.view


import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

import ru.magflayer.spectrum.presentation.common.mvp.strategy.AddToEndSingleByTagStateStrategy

interface PageView : BaseView {

    companion object {
        const val PROGRESS_TAG = "progress_tag"
    }

    @StateStrategyType(value = AddToEndSingleByTagStateStrategy::class, tag = PROGRESS_TAG)
    fun showProgressBar()

    @StateStrategyType(value = AddToEndSingleByTagStateStrategy::class, tag = PROGRESS_TAG)
    fun hideProgressBar()

}
