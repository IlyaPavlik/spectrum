package ru.magflayer.spectrum.presentation.pages.main.camera.holder

import android.annotation.SuppressLint
import android.content.Context
import android.net.Uri
import android.util.Size
import android.view.Surface
import androidx.annotation.FloatRange
import androidx.camera.core.Camera
import androidx.camera.core.CameraInfo
import androidx.camera.core.CameraSelector
import androidx.camera.core.FocusMeteringAction
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.SurfaceOrientedMeteringPointFactory
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import ru.magflayer.spectrum.presentation.common.extension.logger
import ru.magflayer.spectrum.presentation.pages.main.camera.CameraOrientation
import java.io.File
import java.util.concurrent.Executors

class ColorCameraHolder(private val context: Context) {

    companion object {
        private const val SAVE_FILE_FORMAT = "spectre_%d.png"

        // keep aspect ratio 4:3
        private const val SAVE_IMAGE_WIDTH = 640
        private const val SAVE_IMAGE_HEIGHT = 480
    }

    private val log by logger("ColorCameraHolder")

    private var camera: Camera? = null

    val isTorchAvailable: Boolean = camera?.cameraInfo?.hasFlashUnit() ?: false

    val minZoomRatio: Float = camera?.cameraInfo?.zoomState?.value?.minZoomRatio ?: 0.0F

    val maxZoomRatio: Float = camera?.cameraInfo?.zoomState?.value?.maxZoomRatio ?: 0.0F

    private val mainExecutor by lazy { ContextCompat.getMainExecutor(context) }
    private val analyzerExecutor by lazy { Executors.newSingleThreadExecutor() }
    private val imageCaptureExecutor by lazy { Executors.newSingleThreadExecutor() }

    private val imageCapture: ImageCapture by lazy {
        ImageCapture.Builder()
            .setTargetResolution(Size(SAVE_IMAGE_WIDTH, SAVE_IMAGE_HEIGHT))
            .build()
    }

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

    fun setTargetRotation(orientation: CameraOrientation) {
        val rotation = when (orientation) {
            CameraOrientation.PORTRAIT -> Surface.ROTATION_0
            CameraOrientation.LANDSCAPE -> Surface.ROTATION_90
        }
        imageCapture.targetRotation = rotation
    }

    fun takePicture(onSuccess: (Uri) -> Unit, onError: (Exception) -> Unit) {
        val fileName = String.format(SAVE_FILE_FORMAT, System.currentTimeMillis())
        val externalFile = File(context.getExternalFilesDir(null), fileName)
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(externalFile)
            .build()

        imageCapture.takePicture(
            outputOptions,
            imageCaptureExecutor,
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    outputFileResults.savedUri?.let {
                        onSuccess(it)
                    }
                }

                override fun onError(exception: ImageCaptureException) {
                    onError(exception)
                }
            },
        )
    }
}
