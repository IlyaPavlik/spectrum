package ru.magflayer.spectrum.presentation.pages.main.camera

import android.net.Uri
import androidx.camera.core.CameraInfo
import androidx.palette.graphics.Palette
import androidx.palette.graphics.Target
import androidx.palette.graphics.get
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import moxy.InjectViewState
import ru.magflayer.spectrum.domain.entity.AnalyticsEvent
import ru.magflayer.spectrum.domain.entity.ColorPhotoEntity
import ru.magflayer.spectrum.domain.interactor.ColorInfoInteractor
import ru.magflayer.spectrum.domain.interactor.ColorPhotoInteractor
import ru.magflayer.spectrum.domain.interactor.PageAppearanceInteractor
import ru.magflayer.spectrum.domain.interactor.ToolbarAppearanceInteractor
import ru.magflayer.spectrum.domain.repository.AnalyticsRepository
import ru.magflayer.spectrum.presentation.common.helper.ColorHelper
import ru.magflayer.spectrum.presentation.common.model.PageAppearance
import ru.magflayer.spectrum.presentation.common.model.SurfaceInfo
import ru.magflayer.spectrum.presentation.common.model.ToolbarAppearance
import ru.magflayer.spectrum.presentation.common.mvp.BasePresenter
import ru.magflayer.spectrum.presentation.pages.main.camera.holder.CenterColorResult
import ru.magflayer.spectrum.presentation.pages.main.camera.holder.ColorAnalyzerResult
import ru.magflayer.spectrum.presentation.pages.main.camera.holder.SwatchesResult
import java.util.Collections
import javax.inject.Inject
import kotlin.math.roundToInt
import kotlin.math.sign

@InjectViewState
class ColorCameraPresenter @Inject constructor(
    private val analyticsRepository: AnalyticsRepository,
    private val colorInfoInteractor: ColorInfoInteractor,
    private val colorPhotoInteractor: ColorPhotoInteractor,
    private val toolbarAppearanceInteractor: ToolbarAppearanceInteractor,
    private val pageAppearanceInteractor: PageAppearanceInteractor,
) : BasePresenter<ColorCameraView>() {

    companion object {
        private const val ROTATION_INTERVAL = 5
        private const val ZOOM_STEP_FACTOR = 50
    }

    private val targetColors = listOf(
        Target.MUTED,
        Target.DARK_MUTED,
        Target.LIGHT_MUTED,
        Target.VIBRANT,
        Target.DARK_VIBRANT,
        Target.LIGHT_VIBRANT,
    )
    private val swatches = Collections.synchronizedList(ArrayList<Palette.Swatch>())
    private var previousColor = -1
    private var currentDetailsColor: Int = 0
    private var zoomState: ZoomState = ZoomState()
    private var colorMode: SurfaceInfo.Type = SurfaceInfo.Type.SINGLE
    private var orientation: CameraOrientation = CameraOrientation.PORTRAIT
    private var flashEnabled: Boolean = false

    override val pageAppearance: PageAppearance
        get() = PageAppearance.builder()
            .showFloatingButton(PageAppearance.FloatingButtonState.INVISIBLE)
            .build()

    override val toolbarAppearance: ToolbarAppearance
        get() = ToolbarAppearance(
            ToolbarAppearance.Visibility.INVISIBLE,
            "",
        )

    override fun attachView(view: ColorCameraView) {
        super.attachView(view)
        toolbarAppearanceInteractor.setToolbarAppearance(toolbarAppearance)
        pageAppearanceInteractor.setPageAppearance(pageAppearance)
    }

    fun handleCameraInitialized(cameraInfo: CameraInfo) {
        if (cameraInfo.hasFlashUnit()) {
            viewState.showFlash()
        } else {
            viewState.hideFlash()
        }

        zoomState = cameraInfo.zoomState.value?.let {
            ZoomState(it.minZoomRatio, it.maxZoomRatio, it.zoomRatio)
        } ?: zoomState

        viewState.hideErrorMessage()
        viewState.showCrosshair()
        viewState.showPanels()
        viewState.hideProgressBar()
    }

    fun handleAnalyzeImage(analyzerResult: ColorAnalyzerResult) {
        val errorHandler = CoroutineExceptionHandler { _, exception ->
            logger.error("Error while handle image: ", exception)
        }
        presenterScope.launch(errorHandler) {
            when (analyzerResult) {
                is CenterColorResult -> handleSingleColorImage(analyzerResult)
                is SwatchesResult -> handleMultipleColorImage(analyzerResult)
            }
        }
    }

    fun handleOrientationChanged(orientationDegree: Int) {
        if (isLandscape(orientationDegree) && orientation != CameraOrientation.LANDSCAPE) {
            orientation = CameraOrientation.LANDSCAPE
        } else if (isPortrait(orientationDegree) && orientation != CameraOrientation.PORTRAIT) {
            orientation = CameraOrientation.PORTRAIT
        }
        viewState.updateViewOrientation(orientation)
    }

    fun handleColorModeChanged(checked: Boolean) {
        colorMode = when (checked) {
            true -> {
                viewState.showSingleColorMode()
                SurfaceInfo.Type.SINGLE
            }
            false -> {
                viewState.showMultipleColorMode()
                SurfaceInfo.Type.MULTIPLE
            }
        }
    }

    fun handleFocusClicked() {
        viewState.autoFocus()
    }

    fun handlePictureCaptureSucceed(uri: Uri) {
        val errorHandler = CoroutineExceptionHandler { _, exception ->
            logger.warn("Error while saving photo: ", exception)
        }
        presenterScope.launch(errorHandler) {
            saveColorPicture(uri, swatches)
        }
    }

    fun handlePictureCaptureFailed(exception: Exception) {
        logger.warn("Error while capturing a picture: ", exception)
    }

    fun handleFlashClick(checked: Boolean) {
        flashEnabled = checked
        if (checked) {
            viewState.enableFlash()
        } else {
            viewState.disableFlash()
        }
    }

    fun handleCameraZoom(distanceX: Float, distanceY: Float) {
        val distance = if (orientation == CameraOrientation.PORTRAIT) {
            distanceY
        } else {
            -distanceX
        }

        val step = ((zoomState.maxZoom - zoomState.minZoom) / ZOOM_STEP_FACTOR) * distance.sign
        val zoom = (zoomState.zoom + step).coerceIn(zoomState.minZoom, zoomState.maxZoom)
        zoomState = zoomState.copy(zoom = zoom)

        showZoom()
    }

    private fun showZoom() {
        val zoom = zoomState.zoom - zoomState.minZoom
        val maxZoom = (zoomState.maxZoom - zoomState.minZoom).roundToInt()
        viewState.showZoom(zoom, maxZoom)
    }

    private suspend fun handleSingleColorImage(centerColorResult: CenterColorResult) =
        withContext(Dispatchers.Default) {
            val colorSwatch = Palette.Swatch(centerColorResult.color, 1)
            if (colorSwatch.rgb != 0) {
                val hexColor = ColorHelper.dec2Hex(colorSwatch.rgb)
                val colorName = colorInfoInteractor.findColorNameByHex(hexColor)
                currentDetailsColor = colorSwatch.rgb

                swatches.clear()
                swatches.addAll(listOf(Palette.Swatch(colorSwatch.rgb, Integer.MAX_VALUE)))

                presenterScope.launch {
                    viewState.showColorDetails(colorSwatch.rgb, colorSwatch.titleTextColor)
                    viewState.showColorName(colorName)
                }
            }
        }

    private suspend fun handleMultipleColorImage(swatchesResult: SwatchesResult) =
        withContext(Dispatchers.Default) {
            val paletteSwatches = swatchesResult.swatches.map { Palette.Swatch(it.color, it.population) }
            val palette = paletteSwatches.takeIf { it.isNotEmpty() }
                ?.let {
                    Palette.Builder(it)
                        .apply {
                            targetColors.forEach { target ->
                                addTarget(target)
                            }
                        }
                        .generate()
                }

            if (palette == null || palette.swatches.isEmpty()) {
                return@withContext
            }

            // to reduce times of updating
            val dominantSwatch = palette.dominantSwatch
            if (dominantSwatch != null) {
                val currentColor = dominantSwatch.rgb
                val needRefresh = currentColor != previousColor
                if (currentColor != previousColor) {
                    previousColor = currentColor
                }
                if (!needRefresh) {
                    return@withContext
                }
            }

            val colors = targetColors.mapNotNull { target ->
                palette[target]
            }

            swatches.clear()
            swatches.addAll(colors)

            presenterScope.launch {
                viewState.showColors(colors)
            }
        }

    private suspend fun saveColorPicture(
        imageUri: Uri,
        swatches: List<Palette.Swatch>,
    ) = withContext(Dispatchers.Default) {
        presenterScope.launch { viewState.showProgressBar() }

        sendTakePhotoAnalytics()

        val rgbColors = convertSwatches(swatches)
        val entity = ColorPhotoEntity(
            ColorPhotoEntity.Type.INTERNAL,
            imageUri.path ?: "",
            rgbColors,
        )

        colorPhotoInteractor.saveColorPhoto(entity)

        presenterScope.launch {
            viewState.hideProgressBar()
            viewState.showPictureSavedToast()
        }
    }

    private fun sendTakePhotoAnalytics() {
        val mode = when (colorMode) {
            SurfaceInfo.Type.SINGLE -> AnalyticsEvent.TAKE_PHOTO_MODE_SINGLE
            SurfaceInfo.Type.MULTIPLE -> AnalyticsEvent.TAKE_PHOTO_MODE_MULTIPLE
        }
        val zoom = zoomState.zoom - zoomState.minZoom
        val maxZoom = (zoomState.maxZoom - zoomState.minZoom).roundToInt()
            .takeIf { it > 0 } ?: 1
        val zoomRatio = (zoom * 100 / maxZoom).roundToInt()
        val params = mapOf(
            AnalyticsEvent.TAKE_PHOTO_MODE to mode,
            AnalyticsEvent.TAKE_PHOTO_FLASHLIGHT to flashEnabled,
            AnalyticsEvent.TAKE_PHOTO_ZOOM to "$zoomRatio%",
        )

        analyticsRepository.logEvent(AnalyticsEvent.TAKE_PHOTO, params)
    }

    private fun convertSwatches(swatches: List<Palette.Swatch>): List<Int> {
        return swatches.map { it.rgb }
    }

    private fun isPortrait(orientation: Int): Boolean {
        return (
            orientation <= ROTATION_INTERVAL || orientation >= 360 - ROTATION_INTERVAL || // [350 : 10]
                orientation <= 180 + ROTATION_INTERVAL && orientation >= 180 - ROTATION_INTERVAL
            ) // [170 : 190]
    }

    private fun isLandscape(orientation: Int): Boolean {
        return (
            orientation <= 90 + ROTATION_INTERVAL && orientation >= 90 - ROTATION_INTERVAL || // [100 : 80]
                orientation <= 270 + ROTATION_INTERVAL && orientation >= 270 - ROTATION_INTERVAL
            ) // [280 : 260]
    }

    private data class ZoomState(
        val minZoom: Float = 0F,
        val maxZoom: Float = 0F,
        val zoom: Float = 0F,
    )
}
