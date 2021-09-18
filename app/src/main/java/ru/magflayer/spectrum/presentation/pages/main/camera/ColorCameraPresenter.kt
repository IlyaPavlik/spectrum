package ru.magflayer.spectrum.presentation.pages.main.camera

import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.SurfaceTexture
import android.os.Bundle
import androidx.palette.graphics.Palette
import kotlinx.coroutines.*
import moxy.InjectViewState
import ru.magflayer.spectrum.domain.entity.AnalyticsEvent
import ru.magflayer.spectrum.domain.entity.ColorPhotoEntity
import ru.magflayer.spectrum.domain.injection.InjectorManager
import ru.magflayer.spectrum.domain.interactor.*
import ru.magflayer.spectrum.domain.manager.AnalyticsManager
import ru.magflayer.spectrum.domain.manager.CameraManager
import ru.magflayer.spectrum.presentation.common.android.navigation.router.MainRouter
import ru.magflayer.spectrum.presentation.common.extension.convertBitmapToBytes
import ru.magflayer.spectrum.presentation.common.helper.ColorHelper
import ru.magflayer.spectrum.presentation.common.model.PageAppearance
import ru.magflayer.spectrum.presentation.common.model.SurfaceInfo
import ru.magflayer.spectrum.presentation.common.model.ToolbarAppearance
import ru.magflayer.spectrum.presentation.common.mvp.BasePresenter
import java.util.*
import javax.inject.Inject

@InjectViewState
class ColorCameraPresenter : BasePresenter<ColorCameraView>() {

    companion object {
        private const val SURFACE_UPDATE_DELAY_MILLIS = 300L

        private const val SAVE_IMAGE_WIDTH = 640
        private const val SAVE_IMAGE_HEIGHT = 360

        private const val SAVE_FILE_FORMAT = "spectre_%d.png"
        private const val ZOOM_STEP_FACTOR = 20
    }

    @Inject
    lateinit var analyticsManager: AnalyticsManager

    @Inject
    lateinit var colorInfoInteractor: ColorInfoInteractor

    @Inject
    lateinit var cameraManager: CameraManager

    @Inject
    lateinit var mainRouter: MainRouter

    @Inject
    lateinit var colorPhotoInteractor: ColorPhotoInteractor

    @Inject
    lateinit var fileManagerInteractor: FileManagerInteractor

    @Inject
    lateinit var toolbarAppearanceInteractor: ToolbarAppearanceInteractor

    @Inject
    lateinit var pageAppearanceInteractor: PageAppearanceInteractor

    private val swatches = Collections.synchronizedList(ArrayList<Palette.Swatch>())
    private var previousColor = -1
    private var currentDetailsColor: Int = 0
    private var zoomStep = 1
    private var colorMode: SurfaceInfo.Type = SurfaceInfo.Type.SINGLE

    override val pageAppearance: PageAppearance
        get() = PageAppearance.builder()
            .showFloatingButton(PageAppearance.FloatingButtonState.INVISIBLE)
            .build()

    override val toolbarAppearance: ToolbarAppearance
        get() = ToolbarAppearance(
            ToolbarAppearance.Visibility.INVISIBLE,
            ""
        )

    override fun inject() {
        InjectorManager.appComponent?.inject(this)
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        if (cameraManager.isFlashAvailable()) {
            viewState.showFlash()
        } else {
            viewState.hideFlash()
        }
        val maxZoom = cameraManager.getMaxZoom()
        viewState.changeMaxZoom(maxZoom)
        zoomStep = maxZoom / ZOOM_STEP_FACTOR
    }

    override fun attachView(view: ColorCameraView) {
        super.attachView(view)
        toolbarAppearanceInteractor.setToolbarAppearance(toolbarAppearance)
        pageAppearanceInteractor.setPageAppearance(pageAppearance)
    }

    fun handleSurfaceUpdated() {
        presenterScope.launch {
            // add some delay to reduce CPU load
            delay(SURFACE_UPDATE_DELAY_MILLIS)

            cameraManager.cameraBitmap?.let { bitmap ->
                when (colorMode) {
                    SurfaceInfo.Type.SINGLE -> handleSingleColorImage(bitmap)
                    SurfaceInfo.Type.MULTIPLE -> handleMultipleColorImage(bitmap)
                }
            }
        }
    }

    fun handleSurfaceAvailable(surface: SurfaceTexture) {
        try {
            cameraManager.startCamera(surface)
            cameraManager.updateCameraDisplayOrientation()
            viewState.hideErrorMessage()
            viewState.showCrosshair()
            viewState.showPanels()
        } catch (e: Exception) {
            logger.error("Error occurred while starting camera ", e)
            viewState.showErrorMessage()
            viewState.hideCrosshair()
        } finally {
            viewState.hideProgressBar()
        }
    }

    fun handleSurfaceDestroyed() {
        cameraManager.stopPreview()
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
        cameraManager.autoFocus()
    }

    fun handleSaveClicked(rotationDegree: Int) {
        val errorHandler = CoroutineExceptionHandler { _, exception ->
            logger.warn("Error while saving photo: ", exception)
        }
        presenterScope.launch(errorHandler) {
            cameraManager.cameraBitmap?.let { bitmap ->
                saveColorPicture(bitmap, swatches, rotationDegree)
            }
        }
    }

    fun handleFlashClick(checked: Boolean) {
        if (checked) {
            cameraManager.enabledFlash()
        } else {
            cameraManager.disableFlash()
        }
    }

    fun handleCameraZoom(direction: Int) {
        if (!cameraManager.isZoomSupported()) return

        val maxZoom = cameraManager.getMaxZoom()
        var zoom = cameraManager.getZoom()

        if (direction > 0 && zoom < maxZoom) {
            zoom += if ((zoom + zoomStep) > maxZoom) {
                maxZoom - zoom
            } else {
                zoomStep
            }
        } else if (direction < 0 && zoom > 0) {
            zoom -= if ((zoom - zoomStep) < 0) {
                zoom
            } else {
                zoomStep
            }
        }
        logger.debug("Zoom: {}", zoom)
        cameraManager.setZoom(zoom)
        viewState.changeZoomProgress(zoom)
    }

    private suspend fun handleSingleColorImage(bitmap: Bitmap) = withContext(Dispatchers.Default) {
        val centerX = bitmap.width / 2
        val centerY = bitmap.height / 2

        val pixel = bitmap.getPixel(centerX, centerY)
        val colorSwatch = Palette.Swatch(pixel, 1)
        if (colorSwatch.rgb != 0) {
            val hexColor = ColorHelper.dec2Hex(colorSwatch.rgb)
            val errorHandler = CoroutineExceptionHandler { _, exception ->
                logger.error("Error while finding color name: ", exception)
            }
            presenterScope.launch(errorHandler) {
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
    }

    private suspend fun handleMultipleColorImage(bitmap: Bitmap) =
        withContext(Dispatchers.Default) {
            val palette = Palette.from(bitmap).generate()

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

    internal fun handleMenuClicked() {
        mainRouter.openHistoryScreen()
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
        bundle.putString(AnalyticsEvent.TAKE_PHOTO_MODE, mode)
        bundle.putBoolean(AnalyticsEvent.TAKE_PHOTO_FLASHLIGHT, cameraManager.isFlashlightEnabled())
        bundle.putString(AnalyticsEvent.TAKE_PHOTO_ZOOM, "${cameraManager.getZoomRatio()}%")

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
}
