package ru.magflayer.spectrum.presentation.pages.main.history.details

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

import ru.magflayer.spectrum.domain.entity.ColorPhotoEntity
import ru.magflayer.spectrum.presentation.common.mvp.view.BaseView

interface HistoryDetailsView : BaseView {

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showPhoto(entity: ColorPhotoEntity)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showColorName(name: String)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showRgb(color: Int)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showRyb(color: Int)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showCmyk(color: Int)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showHsv(color: Int)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showXyz(color: Int)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showLab(color: Int)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showNcs(color: Int, ncsName: String)

}
