package ru.magflayer.spectrum.domain.manager

import android.content.Context
import android.content.pm.PackageManager
import android.graphics.*
import android.hardware.Camera
import android.view.Surface
import android.view.WindowManager
import com.squareup.otto.Bus
import org.slf4j.LoggerFactory
import ru.magflayer.spectrum.common.utils.RxUtils
import rx.Observable
import rx.Subscription
import java.io.ByteArrayOutputStream
import java.io.IOException
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.experimental.and

@Suppress("DEPRECATION")
@Singleton
class CameraManager @Inject
internal constructor(val context: Context) {

    private val log = LoggerFactory.getLogger(javaClass)

    @Inject
    lateinit var bus: Bus

    private var camera: Camera? = null
    var cameraBitmap: Bitmap? = null
        private set
    private var generateBitmapSubscription: Subscription? = null
    private val generateBitmapStream = ByteArrayOutputStream()
    private val windowManager: WindowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    private val backFacingCameraId: Int
        get() {
            var cameraId = -1

            val numberOfCameras = Camera.getNumberOfCameras()
            for (i in 0 until numberOfCameras) {
                val info = Camera.CameraInfo()
                Camera.getCameraInfo(i, info)
                if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                    cameraId = i
                    break
                }
            }
            return cameraId
        }

    private val shutterCallback = Camera.ShutterCallback { log.debug("onShutter") }
    private val rawCallback = Camera.PictureCallback { _, _ -> log.debug("onPictureTaken - raw") }

    interface OnTakePictureListener {
        fun onTakePicture(bitmap: Bitmap)
    }

    fun isFlashAvailable() = context.packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH)

    fun open() {
        camera = Camera.open(backFacingCameraId)

        if (camera == null) {
            throw RuntimeException("Camera not available")
        }

        camera?.apply {
            val params = parameters
            params.setPreviewSize(CAMERA_WIDTH, CAMERA_HEIGHT)
            params.previewFormat = ImageFormat.NV21
            parameters = params
        }

        camera?.setPreviewCallback { data, _ ->
            if (generateBitmapSubscription == null || generateBitmapSubscription!!.isUnsubscribed) {
                val size = camera?.parameters?.previewSize
                generateBitmapSubscription = generateBitmapObservable(data, size!!, generateBitmapStream)
                        .subscribe({ bitmap -> cameraBitmap = bitmap },
                                { throwable -> log.error("Error while generating bitmap: ", throwable) })
            }
        }
    }

    @Throws(IOException::class)
    fun startCamera(texture: SurfaceTexture?) {
        if (texture == null) {
            throw NullPointerException("You cannot start preview without a preview texture")
        }

        if (camera == null) {
            throw IllegalStateException("You should call open() method before start camera")
        }

        camera?.setPreviewTexture(texture)
        camera?.startPreview()

        cameraBitmap = Bitmap.createBitmap(CAMERA_WIDTH, CAMERA_HEIGHT, Bitmap.Config.ARGB_8888)
    }

    fun autoFocus() {
        try {
            camera?.autoFocus { _, camera1 -> camera1.cancelAutoFocus() }
        } catch (e: RuntimeException) {
            log.warn("Cannot auto focus camera: ", e)
        }
    }

    fun enabledFlash() {
        camera?.let {
            val params = it.parameters
            params.flashMode = Camera.Parameters.FLASH_MODE_TORCH
            it.parameters = params
        }
    }

    fun disableFlash() {
        camera?.let {
            val params = it.parameters
            params.flashMode = Camera.Parameters.FLASH_MODE_OFF
            it.parameters = params
        }
    }

    fun takePicture(pictureCallback: OnTakePictureListener) {
        camera?.takePicture(shutterCallback, rawCallback, Camera.PictureCallback { data, camera1 ->
            log.debug("onPictureTaken - jpeg")

            val bitmap = BitmapFactory.decodeByteArray(data, 0, data.size)
            pictureCallback.onTakePicture(bitmap)

            camera1.startPreview()
        })
    }

    fun stopPreview() {
        camera?.stopPreview()
    }

    fun close() {
        camera?.apply {
            stopPreview()
            setPreviewCallback(null)
            unlock()
            release()
        }
        camera = null

        generateBitmapSubscription?.apply { unsubscribe() }
        generateBitmapSubscription = null

        generateBitmapStream.reset()
    }

    fun updateCameraDisplayOrientation() {
        val camInfo = android.hardware.Camera.CameraInfo()
        android.hardware.Camera.getCameraInfo(backFacingCameraId, camInfo)

        val display = windowManager.defaultDisplay
        val rotation = display.rotation
        var degrees = 0
        when (rotation) {
            Surface.ROTATION_0 -> degrees = 0
            Surface.ROTATION_90 -> degrees = 90
            Surface.ROTATION_180 -> degrees = 180
            Surface.ROTATION_270 -> degrees = 270
        }

        updateCameraDisplayOrientation(degrees)
    }

    private fun updateCameraDisplayOrientation(degrees: Int) {
        val camInfo = android.hardware.Camera.CameraInfo()
        android.hardware.Camera.getCameraInfo(backFacingCameraId, camInfo)

        var result: Int
        if (camInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (camInfo.orientation + degrees) % 360
            result = (360 - result) % 360  // compensate the mirror
        } else {  // back-facing
            result = (camInfo.orientation - degrees + 360) % 360
        }

        camera?.setDisplayOrientation(result)
    }

    private fun generateBitmapObservable(data: ByteArray, previewSize: Camera.Size,
                                         stream: ByteArrayOutputStream): Observable<Bitmap> {
        return Observable.just(data)
                .compose(RxUtils.applySchedulers(RxUtils.ANDROID_THREAD_POOL_EXECUTOR, RxUtils.ANDROID_THREAD_POOL_EXECUTOR))
                .flatMap { bytes ->
                    //Convert YUV to RGB
                    val yuvImage = YuvImage(bytes, ImageFormat.NV21, previewSize.width, previewSize.height, null)
                    yuvImage.compressToJpeg(Rect(0, 0, previewSize.width, previewSize.height), 50, stream)
                    val jdata = stream.toByteArray()
                    stream.reset()
                    Observable.fromCallable { BitmapFactory.decodeByteArray(jdata, 0, jdata.size) }

                    //TODO research decode Yuv data to RGB, below method very slow
                    // int[] imagePixels = convertYUV420_NV21toRGB8888(yuvimage.getYuvData(), previewSize.width, previewSize.height);
                    // return Observable.just(Bitmap.createBitmap(imagePixels, previewSize.width, previewSize.height, Bitmap.Config.ARGB_8888));
                }
    }

    companion object {

        // Min allowed camera image size, if it is less setParameters will throw exception
        private const val CAMERA_WIDTH = 1280
        private const val CAMERA_HEIGHT = 720

        /**
         * Converts YUV420 NV21 to RGB8888
         *
         * @param data   byte array on YUV420 NV21 format.
         * @param width  pixels width
         * @param height pixels height
         * @return a RGB8888 pixels int array. Where each int is a pixels ARGB.
         */
        fun convertYUV420_NV21toRGB8888(data: ByteArray, width: Int, height: Int): IntArray {
            val size = width * height
            val pixels = IntArray(size)
            var u: Int
            var v: Int
            var y1: Int
            var y2: Int
            var y3: Int
            var y4: Int

            // i percorre os Y and the final pixels
            // k percorre os pixles U e V
            var i = 0
            var k = 0
            while (i < size) {
                y1 = (data[i] and 0xff.toByte()).toInt()
                y2 = (data[i + 1] and 0xff.toByte()).toInt()
                y3 = (data[width + i] and 0xff.toByte()).toInt()
                y4 = (data[width + i + 1] and 0xff.toByte()).toInt()

                u = (data[size + k] and 0xff.toByte()).toInt()
                v = (data[size + k + 1] and 0xff.toByte()).toInt()
                u -= 128
                v -= 128

                pixels[i] = convertYUVtoRGB(y1, u, v)
                pixels[i + 1] = convertYUVtoRGB(y2, u, v)
                pixels[width + i] = convertYUVtoRGB(y3, u, v)
                pixels[width + i + 1] = convertYUVtoRGB(y4, u, v)

                if (i != 0 && (i + 2) % width == 0)
                    i += width
                i += 2
                k += 2
            }

            return pixels
        }

        private fun convertYUVtoRGB(y: Int, u: Int, v: Int): Int {
            var r = y + (1.402f * v).toInt()
            var g = y - (0.344f * u + 0.714f * v).toInt()
            var b = y + (1.772f * u).toInt()

            r = if (r > 255) 255 else if (r < 0) 0 else r
            g = if (g > 255) 255 else if (g < 0) 0 else g
            b = if (b > 255) 255 else if (b < 0) 0 else b
            return -0x1000000 or (b shl 16) or (g shl 8) or r
        }
    }
}
