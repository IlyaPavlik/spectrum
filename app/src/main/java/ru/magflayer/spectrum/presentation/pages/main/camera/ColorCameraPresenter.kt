package ru.magflayer.spectrum.presentation.pages.main.camera

import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Bundle
import androidx.camera.core.CameraInfo
import androidx.palette.graphics.Palette
import kotlinx.coroutines.*
import moxy.InjectViewState
import ru.magflayer.spectrum.domain.entity.AnalyticsEvent
import ru.magflayer.spectrum.domain.entity.ColorPhotoEntity
import ru.magflayer.spectrum.domain.interactor.*
import ru.magflayer.spectrum.domain.manager.AnalyticsManager
import ru.magflayer.spectrum.presentation.common.extension.convertBitmapToBytes
import ru.magflayer.spectrum.presentation.common.helper.ColorHelper
import ru.magflayer.spectrum.presentation.common.model.PageAppearance
import ru.magflayer.spectrum.presentation.common.model.SurfaceInfo
import ru.magflayer.spectrum.presentation.common.model.ToolbarAppearance
import ru.magflayer.spectrum.presentation.common.mvp.BasePresenter
import ru.magflayer.spectrum.presentation.pages.main.camera.holder.CenterColorResult
import ru.magflayer.spectrum.presentation.pages.main.camera.holder.ColorAnalyzerResult
import ru.magflayer.spectrum.presentation.pages.main.camera.holder.SwatchesResult
import java.util.*
import javax.inject.Inject
import kotlin.math.roundToInt
import kotlin.math.sign

@InjectViewState
class ColorCameraPresenter @Inject constructor(
    private val analyticsManager: AnalyticsManager,
    private val colorInfoInteractor: ColorInfoInteractor,
    private val colorPhotoInteractor: ColorPhotoInteractor,
    private val fileManagerInteractor: FileManagerInteractor,
    private val toolbarAppearanceInteractor: ToolbarAppearanceInteractor,
    private val pageAppearanceInteractor: PageAppearanceInteractor
) : BasePresenter<ColorCameraView>() {

    companion object {
        private const val SAVE_IMAGE_WIDTH = 640
        private const val SAVE_IMAGE_HEIGHT = 360

        private const val SAVE_FILE_FORMAT = "spectre_%d.png"
        private const val ROTATION_INTERVAL = 5
        private const val ZOOM_STEP_FACTOR = 50
    }

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
            ""
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

    fun handlePictureCaptureSucceed(bitmap: Bitmap) {
        val orientationDegree = if (orientation == CameraOrientation.LANDSCAPE) 0 else 90
        val errorHandler = CoroutineExceptionHandler { _, exception ->
            logger.warn("Error while saving photo: ", exception)
        }
        presenterScope.launch(errorHandler) {
            saveColorPicture(bitmap, swatches, orientationDegree)
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
            val paletteSwatches =
                swatchesResult.swatches.map { Palette.Swatch(it.color, it.population) }
            val palette = Palette.from(paletteSwatches)

            if (palette.swatches.isEmpty()) {
                return@withContext
            }

            //to reduce times of updating
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
            val colors = ArrayList(palette.swatches)

            //filtered by brightness
            colors.sortWith { lhs, rhs -> lhs.hsl[2].compareTo(rhs.hsl[2]) }

            swatches.clear()
            swatches.addAll(colors)

            presenterScope.launch {
                viewState.showColors(colors)
            }
        }

    private suspend fun saveColorPicture(
        bitmap: Bitmap,
        swatches: List<Palette.Swatch>,
        rotationDegree: Int
    ) = withContext(Dispatchers.Default) {
        presenterScope.launch { viewState.showProgressBar() }

        sendTakePhotoAnalytics()

        val scaledBitmap = scaleBitmapWithRotate(bitmap, rotationDegree)
        val bitmapBytes = scaledBitmap.convertBitmapToBytes()
        val fileName = String.format(SAVE_FILE_FORMAT, System.currentTimeMillis())
        val savedFileUri = fileManagerInteractor.saveFileToExternalStorage(
            fileName,
            bitmapBytes
        )
        val rgbColors = convertSwatches(swatches)
        val entity = ColorPhotoEntity(
            ColorPhotoEntity.Type.INTERNAL,
            savedFileUri.path ?: "",
            rgbColors
        )

        colorPhotoInteractor.saveColorPhoto(entity)

        presenterScope.launch {
            viewState.hideProgressBar()
            viewState.showPictureSavedToast()
        }
    }

    private fun sendTakePhotoAnalytics() {
        val bundle = Bundle()
        val mode = when (colorMode) {
            SurfaceInfo.Type.SINGLE -> AnalyticsEvent.TAKE_PHOTO_MODE_SINGLE
            SurfaceInfo.Type.MULTIPLE -> AnalyticsEvent.TAKE_PHOTO_MODE_MULTIPLE
        }
        val zoom = zoomState.zoom - zoomState.minZoom
        val maxZoom = (zoomState.maxZoom - zoomState.minZoom).roundToInt()
            .takeIf { it > 0 } ?: 1
        val zoomRatio = (zoom * 100 / maxZoom).roundToInt()
        bundle.putString(AnalyticsEvent.TAKE_PHOTO_MODE, mode)
        bundle.putBoolean(AnalyticsEvent.TAKE_PHOTO_FLASHLIGHT, flashEnabled)
        bundle.putString(AnalyticsEvent.TAKE_PHOTO_ZOOM, "$zoomRatio%")

        analyticsManager.logEvent(AnalyticsEvent.TAKE_PHOTO, bundle)
    }

    private fun convertSwatches(swatches: List<Palette.Swatch>): List<Int> {
        return swatches.map { it.rgb }
    }

    private fun scaleBitmapWithRotate(sourceBitmap: Bitmap, degrees: Int): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degrees.toFloat())
        val scaledBitmap =
            Bitmap.createScaledBitmap(sourceBitmap, SAVE_IMAGE_WIDTH, SAVE_IMAGE_HEIGHT, false)
        return Bitmap.createBitmap(
            scaledBitmap,
            0,
            0,
            scaledBitmap.width,
            scaledBitmap.height,
            matrix,
            true
        )
    }

    private fun isPortrait(orientation: Int): Boolean {
        return (orientation <= ROTATION_INTERVAL || orientation >= 360 - ROTATION_INTERVAL // [350 : 10]
                || orientation <= 180 + ROTATION_INTERVAL && orientation >= 180 - ROTATION_INTERVAL) //[170 : 190]
    }

    private fun isLandscape(orientation: Int): Boolean {
        return (orientation <= 90 + ROTATION_INTERVAL && orientation >= 90 - ROTATION_INTERVAL // [100 : 80]
                || orientation <= 270 + ROTATION_INTERVAL && orientation >= 270 - ROTATION_INTERVAL) // [280 : 260]
    }

    private data class ZoomState(
        val minZoom: Float = 0F,
        val maxZoom: Float = 0F,
        val zoom: Float = 0F
    )
}
