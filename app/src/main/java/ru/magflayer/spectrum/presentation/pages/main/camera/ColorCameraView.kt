package ru.magflayer.spectrum.presentation.pages.main.camera

import androidx.palette.graphics.Palette
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.AddToEndSingleTagStrategy
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType
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

    @StateStrategyType(value = AddToEndSingleTagStrategy::class, tag = ERROR_MESSAGE_TAG)
    fun showErrorMessage()

    @StateStrategyType(value = AddToEndSingleTagStrategy::class, tag = ERROR_MESSAGE_TAG)
    fun hideErrorMessage()

    @StateStrategyType(value = AddToEndSingleTagStrategy::class, tag = CROSSHAIR_TAG)
    fun showCrosshair()

    @StateStrategyType(value = AddToEndSingleTagStrategy::class, tag = CROSSHAIR_TAG)
    fun hideCrosshair()

    @StateStrategyType(value = AddToEndSingleTagStrategy::class, tag = PANELS_TAG)
    fun showPanels()

    @StateStrategyType(value = AddToEndSingleTagStrategy::class, tag = FLASH_VISIBILITY_TAG)
    fun showFlash()

    @StateStrategyType(value = AddToEndSingleTagStrategy::class, tag = FLASH_VISIBILITY_TAG)
    fun hideFlash()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun changeMaxZoom(max: Int)

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun changeZoomProgress(progress: Int)

}
