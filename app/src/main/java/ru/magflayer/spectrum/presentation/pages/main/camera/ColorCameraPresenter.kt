package ru.magflayer.spectrum.presentation.pages.main.camera

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.SurfaceTexture
import android.net.Uri
import android.os.Bundle
import android.support.v7.graphics.Palette
import com.arellomobile.mvp.InjectViewState
import com.squareup.otto.Subscribe
import ru.magflayer.spectrum.domain.entity.AnalyticsEvent
import ru.magflayer.spectrum.domain.entity.ColorPhotoEntity
import ru.magflayer.spectrum.domain.entity.event.PictureSavedEvent
import ru.magflayer.spectrum.domain.injection.InjectorManager
import ru.magflayer.spectrum.domain.interactor.ColorInfoInteractor
import ru.magflayer.spectrum.domain.interactor.ColorPhotoInteractor
import ru.magflayer.spectrum.domain.interactor.FileManagerInteractor
import ru.magflayer.spectrum.domain.manager.AnalyticsManager
import ru.magflayer.spectrum.domain.manager.CameraManager
import ru.magflayer.spectrum.presentation.common.android.navigation.router.MainRouter
import ru.magflayer.spectrum.presentation.common.extension.convertBitmapToBytes
import ru.magflayer.spectrum.presentation.common.helper.ColorHelper
import ru.magflayer.spectrum.presentation.common.model.PageAppearance
import ru.magflayer.spectrum.presentation.common.model.SurfaceInfo
import ru.magflayer.spectrum.presentation.common.model.ToolbarAppearance
import ru.magflayer.spectrum.presentation.common.mvp.BasePresenter
import rx.Observable
import rx.functions.Action1
import rx.functions.Func1
import rx.subjects.BehaviorSubject
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@InjectViewState
class ColorCameraPresenter internal constructor() : BasePresenter<ColorCameraView>() {

    companion object {
        private const val SURFACE_UPDATE_DELAY_MILLIS = 300

        private const val SAVE_IMAGE_WIDTH = 640
        private const val SAVE_IMAGE_HEIGHT = 360

        private const val TAG_SINGLE_COLOR = "TAG_SINGLE_COLOR"
        private const val TAG_MULTIPLE_COLOR = "TAG_MULTIPLE_COLOR"

        private const val SAVE_FILE_FORMAT = "spectre_%d.png"
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

    private var previousColor = -1
    private val changeObservable = BehaviorSubject.create<SurfaceInfo.Type>()
    private var currentDetailsColor: Int = 0
    private var swatches: List<Palette.Swatch> = ArrayList()

    override val pageAppearance: PageAppearance
        get() = PageAppearance.builder()
                .showFloatingButton(PageAppearance.FloatingButtonState.INVISIBLE)
                .build()

    override val toolbarAppearance: ToolbarAppearance
        get() = ToolbarAppearance(
                ToolbarAppearance.Visibility.INVISIBLE,
                ""
        )

    init {
        execute<SurfaceInfo>(changeObservable
                .sample(SURFACE_UPDATE_DELAY_MILLIS.toLong(), TimeUnit.MILLISECONDS)
                .flatMap { type ->
                    val bitmap = cameraManager.cameraBitmap
                    Observable.just(SurfaceInfo(type, bitmap!!))
                },
                Action1 {
                    val bitmap = it.bitmap
                    if (it.type === SurfaceInfo.Type.MULTIPLE) {
                        handleCameraSurface(bitmap)
                    } else {
                        handleColorDetails(bitmap)
                    }
                },
                Action1 {
                    logger.error("Error occurred while listening changing", it)
                })
    }

    override fun inject() {
        InjectorManager.appComponent?.inject(this)
    }

    internal fun updateSurface(type: SurfaceInfo.Type) {
        changeObservable.onNext(type)
    }

    internal fun handleSurfaceAvailable(surface: SurfaceTexture) {
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

    internal fun handleSurfaceDestroyed() {
        cameraManager.stopPreview()
    }

    internal fun handleFocusClicked() {
        cameraManager.autoFocus()
    }

    internal fun handleSaveClicked(rotationDegree: Int) {
        val bitmap = cameraManager.cameraBitmap
        saveColorPicture(bitmap, swatches, rotationDegree)
    }

    private fun handleCameraSurface(bitmap: Bitmap) {
        execute<List<Palette.Swatch>>(TAG_MULTIPLE_COLOR, Observable.just(bitmap)
                .flatMap { bitmap1 -> Observable.just(Palette.from(bitmap1).generate()) }
                .filter { palette ->
                    //to reduce times of updating
                    val dominantSwatch = palette.dominantSwatch
                    if (dominantSwatch != null) {
                        val currentColor = dominantSwatch.rgb
                        val needRefresh = currentColor != previousColor
                        if (currentColor != previousColor) {
                            previousColor = currentColor
                        }
                        needRefresh
                    } else {
                        true
                    }
                }
                .map<List<Palette.Swatch>> { palette ->
                    val colors = ArrayList(palette.swatches)
                    //filtered by brightness
                    colors.sortWith(Comparator { lhs, rhs -> java.lang.Float.compare(lhs.hsl[2], rhs.hsl[2]) })
                    colors
                },
                Action1 {
                    viewState.showColors(it)
                    swatches = ArrayList<Palette.Swatch>(it)
                })
    }

    private fun handleColorDetails(bmp: Bitmap) {
        val centerX = bmp.width / 2
        val centerY = bmp.height / 2

        val color = Palette.Swatch(bmp.getPixel(centerX, centerY), 1)

        val hexColor = ColorHelper.dec2Hex(color.rgb)
        execute(TAG_SINGLE_COLOR, colorInfoInteractor.findColorNameByHex(hexColor),
                Action1 { colorName ->
                    currentDetailsColor = color.rgb
                    viewState.showColorDetails(color.rgb, color.titleTextColor)
                    viewState.showColorName(colorName)

                    swatches = listOf(Palette.Swatch(color.rgb, Integer.MAX_VALUE))
                },
                Action1 { error -> logger.error("Error while finding color name: ", error) })
    }

    internal fun openHistory() {
        mainRouter.openHistoryScreen()
    }

    @Subscribe
    fun onPictureSaved(event: PictureSavedEvent) {
        viewState.showPictureSavedToast()
    }

    @SuppressLint("DefaultLocale")
    private fun saveColorPicture(bitmap: Bitmap?, swatches: List<Palette.Swatch>, rotationDegree: Int) {
        viewState.showProgressBar()

        sendTakePhotoAnalytics()

        execute<Boolean>(Observable.just<Bitmap>(bitmap)
                .map(scaleBitmapWithRotate(rotationDegree))
                .flatMap { bitmap1 -> Observable.fromCallable { bitmap1.convertBitmapToBytes() } }
                .flatMap<Uri> { bytes ->
                    val fileName = String.format(SAVE_FILE_FORMAT, System.currentTimeMillis())
                    fileManagerInteractor.saveFileToExternalStorage(fileName, bytes)
                }
                .flatMap { uri ->
                    val rgbColors = convertSwatches(swatches)
                    val entity = ColorPhotoEntity(ColorPhotoEntity.Type.INTERNAL, uri.path, rgbColors)
                    colorPhotoInteractor.saveColorPhoto(entity)
                },
                Action1 {
                    viewState.hideProgressBar()
                    viewState.showPictureSavedToast()
                },
                Action1 { error -> logger.warn("Error while saving photo: ", error) })
    }

    private fun sendTakePhotoAnalytics() {
        val bundle = Bundle()
        val mode = if (changeObservable.hasValue() && changeObservable.value === SurfaceInfo.Type.MULTIPLE)
            AnalyticsEvent.TAKE_PHOTO_MODE_MULTIPLE
        else
            AnalyticsEvent.TAKE_PHOTO_MODE_SINGLE
        bundle.putString(AnalyticsEvent.TAKE_PHOTO_MODE, mode)
        analyticsManager.logEvent(AnalyticsEvent.TAKE_PHOTO, bundle)
    }

    private fun convertSwatches(swatches: List<Palette.Swatch>): List<Int> {
        return swatches.map { it.rgb }
    }

    private fun scaleBitmapWithRotate(degrees: Int): Func1<Bitmap, Bitmap> {
        return Func1 { bitmap ->
            val matrix = Matrix()
            matrix.postRotate(degrees.toFloat())
            val scaledBitmap = Bitmap.createScaledBitmap(bitmap, SAVE_IMAGE_WIDTH, SAVE_IMAGE_HEIGHT, false)
            Bitmap.createBitmap(scaledBitmap, 0, 0, scaledBitmap.width, scaledBitmap.height, matrix, true)
        }
    }
}
