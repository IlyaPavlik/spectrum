package ru.magflayer.spectrum.presentation.pages.main.camera

import androidx.palette.graphics.Palette
import moxy.viewstate.strategy.AddToEndSingleStrategy
import moxy.viewstate.strategy.AddToEndSingleTagStrategy
import moxy.viewstate.strategy.OneExecutionStateStrategy
import moxy.viewstate.strategy.StateStrategyType
import ru.magflayer.spectrum.presentation.common.mvp.view.PageView

interface ColorCameraView : PageView {

    companion object {
        private const val ERROR_MESSAGE_TAG = "error_message"
        private const val CROSSHAIR_TAG = "crosshair"
        private const val PANELS_TAG = "panels"
        private const val FLASH_VISIBILITY_TAG = "flash_visibility"
        private const val FLASH_ENABLED_TAG = "flash_enabled"
        private const val COLOR_MODE = "color_mode"
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

    @StateStrategyType(value = AddToEndSingleTagStrategy::class, tag = FLASH_ENABLED_TAG)
    fun enableFlash()

    @StateStrategyType(value = AddToEndSingleTagStrategy::class, tag = FLASH_ENABLED_TAG)
    fun disableFlash()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun showZoom(zoom: Float, maxZoom: Int)

    @StateStrategyType(AddToEndSingleTagStrategy::class, tag = COLOR_MODE)
    fun showSingleColorMode()

    @StateStrategyType(AddToEndSingleTagStrategy::class, tag = COLOR_MODE)
    fun showMultipleColorMode()

    @StateStrategyType(AddToEndSingleStrategy::class)
    fun updateViewOrientation(orientation: CameraOrientation)

    @StateStrategyType(OneExecutionStateStrategy::class)
    fun autoFocus()

}
