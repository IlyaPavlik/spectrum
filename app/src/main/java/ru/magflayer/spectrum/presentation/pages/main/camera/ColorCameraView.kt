package ru.magflayer.spectrum.presentation.pages.main.camera

import android.support.v7.graphics.Palette

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType

import ru.magflayer.spectrum.presentation.common.mvp.strategy.AddToEndSingleByTagStateStrategy
import ru.magflayer.spectrum.presentation.common.mvp.view.PageView

interface ColorCameraView : PageView {

    companion object {
        const val ERROR_MESSAGE_TAG = "error_message"
        const val CROSSHAIR_TAG = "crosshair"
        const val PANELS_TAG = "panels"
        const val FLASH_VISIBILITY_TAG = "flash_visibility"
    }

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun showPictureSavedToast()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showColors(colors: List<Palette.Swatch>)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showColorDetails(mainColor: Int, titleColor: Int)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showColorName(name: String)

    @StateStrategyType(value = AddToEndSingleByTagStateStrategy::class, tag = ERROR_MESSAGE_TAG)
    fun showErrorMessage()

    @StateStrategyType(value = AddToEndSingleByTagStateStrategy::class, tag = ERROR_MESSAGE_TAG)
    fun hideErrorMessage()

    @StateStrategyType(value = AddToEndSingleByTagStateStrategy::class, tag = CROSSHAIR_TAG)
    fun showCrosshair()

    @StateStrategyType(value = AddToEndSingleByTagStateStrategy::class, tag = CROSSHAIR_TAG)
    fun hideCrosshair()

    @StateStrategyType(value = AddToEndSingleByTagStateStrategy::class, tag = PANELS_TAG)
    fun showPanels()

    @StateStrategyType(value = AddToEndSingleByTagStateStrategy::class, tag = FLASH_VISIBILITY_TAG)
    fun showFlash()

    @StateStrategyType(value = AddToEndSingleByTagStateStrategy::class, tag = FLASH_VISIBILITY_TAG)
    fun hideFlash()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun changeMaxZoom(max: Int);

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun changeZoomProgress(progress: Int);

}
