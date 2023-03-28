package ru.magflayer.spectrum.presentation.pages.main.camera.holder

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.annotation.FloatRange
import androidx.camera.core.Camera
import androidx.camera.core.CameraInfo
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceOrientedMeteringPointFactory
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import ru.magflayer.spectrum.presentation.common.extension.logger
import java.util.concurrent.Executors

class ColorCameraHolder(val context: Context) {

    private val log by logger("ColorCameraHolder")

    private var camera: Camera? = null

    val isTorchAvailable: Boolean = camera?.cameraInfo?.hasFlashUnit() ?: false

    val minZoomRatio: Float = camera?.cameraInfo?.zoomState?.value?.minZoomRatio ?: 0.0F

    val maxZoomRatio: Float = camera?.cameraInfo?.zoomState?.value?.maxZoomRatio ?: 0.0F

    private val mainExecutor by lazy { ContextCompat.getMainExecutor(context) }
    private val analyzerExecutor by lazy { Executors.newSingleThreadExecutor() }
    private val imageCaptureExecutor by lazy { Executors.newSingleThreadExecutor() }
    private val imageCapture by lazy { ImageCapture.Builder().build() }

    @FloatRange(from = 0.0, to = 1.0)
    val zoom: Float = camera?.cameraInfo?.zoomState?.value?.linearZoom ?: 0.0F

    @SuppressLint("UnsafeOptInUsageError")
    fun startCamera(
        lifecycleOwner: LifecycleOwner,
        previewView: PreviewView,
        analyzer: ImageAnalysis.Analyzer,
        initialized: (CameraInfo) -> Unit = {},
    ) {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            val preview = Preview.Builder().build()
            val analysis = ImageAnalysis.Builder()
                .setOutputImageFormat(ImageAnalysis.OUTPUT_IMAGE_FORMAT_RGBA_8888)
                .build()

            preview.setSurfaceProvider(previewView.surfaceProvider)

            analysis.setAnalyzer(analyzerExecutor, analyzer)

            try {
                cameraProvider.unbindAll()
                camera = cameraProvider.bindToLifecycle(
                    lifecycleOwner,
                    cameraSelector,
                    preview,
                    analysis,
                    imageCapture,
                ).also {
                    initialized.invoke(it.cameraInfo)
                }
            } catch (e: Exception) {
                log.warn("Error while starting of the camera: ", e)
            }
        }, mainExecutor)
    }

    fun autoFocus(previewView: PreviewView): Unit = camera?.run {
        val factory = SurfaceOrientedMeteringPointFactory(
            previewView.width.toFloat(),
            previewView.height.toFloat(),
        )
        val point = factory.createPoint(
            previewView.width / 2f,
            previewView.height / 2f,
        )
        val focusAction = FocusMeteringAction.Builder(point, FocusMeteringAction.FLAG_AF).build()

        val future = cameraControl.startFocusAndMetering(focusAction)
        future.addListener({
            log.debug("Camera has been autofocused")
        }, mainExecutor)
    } ?: Unit

    fun enabledFlash(): Unit = camera?.run {
        val future = cameraControl.enableTorch(true)
        future.addListener({
            log.debug("Flash has been enabled")
        }, mainExecutor)
    } ?: Unit

    fun disableFlash(): Unit = camera?.run {
        val future = cameraControl.enableTorch(false)
        future.addListener({
            log.debug("Flash has been disabled")
        }, mainExecutor)
    } ?: Unit

    fun setZoom(@FloatRange(from = 0.0, to = 1.0) zoom: Float): Unit = camera?.run {
        val future = cameraControl.setLinearZoom(zoom)
        future.addListener({
            log.debug("Zoom is set: $zoom")
        }, mainExecutor)
    } ?: Unit

    fun setZoomRatio(zoomRatio: Float): Unit = camera?.run {
        val future = cameraControl.setZoomRatio(zoomRatio)
        future.addListener({
            log.debug("Ratio zoom is set: $zoomRatio")
        }, mainExecutor)
    } ?: Unit

    fun takePicture(onSuccess: (Bitmap) -> Unit, onError: (Exception) -> Unit) {
        imageCapture.takePicture(
            imageCaptureExecutor,
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(image: ImageProxy) {
                    image.toBitmap()?.let { onSuccess(it) }
                        ?: onError(IllegalStateException("Cannot create bitmap"))
                }

                override fun onError(exception: ImageCaptureException) {
                    onError(exception)
                }
            },
        )
    }

    fun ImageProxy.toBitmap(): Bitmap? {
        val buffer = planes[0].buffer
        buffer.rewind()
        val bytes = ByteArray(buffer.capacity())
        buffer.get(bytes)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
    }
}
