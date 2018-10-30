package ru.magflayer.spectrum.presentation.pages.main.history

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

import ru.magflayer.spectrum.domain.entity.ColorPhotoEntity
import ru.magflayer.spectrum.presentation.common.mvp.view.PageView

interface HistoryView : PageView {

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showHistory(history: List<ColorPhotoEntity>)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun openPickPhoto()

}
