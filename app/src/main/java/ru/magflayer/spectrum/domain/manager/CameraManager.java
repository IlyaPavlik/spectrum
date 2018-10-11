package ru.magflayer.spectrum.domain.manager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.SurfaceTexture;
import android.graphics.YuvImage;
import android.hardware.Camera;
import android.support.annotation.Nullable;
import android.view.Display;
import android.view.Surface;
import android.view.WindowManager;

import com.squareup.otto.Bus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

import ru.magflayer.spectrum.common.utils.RxUtils;
import ru.magflayer.spectrum.presentation.common.utils.BitmapUtils;
import rx.Observable;
import rx.Subscription;

@Singleton
@SuppressWarnings({"deprecation", "WeakerAccess"})
public class CameraManager {

    private final Logger log = LoggerFactory.getLogger(getClass());

    private static final int CAMERA_WIDTH = 1280;
    private static final int CAMERA_HEIGHT = 720;

    interface OnTakePictureListener {
        void onTakePicture(Bitmap bitmap);
    }

    @Inject
    Bus bus;

    @Nullable
    private Camera camera;
    private Bitmap cameraBitmap;
    private Subscription generateBitmapSubscription;
    private ByteArrayOutputStream generateBitmapStream;
    private WindowManager windowManager;

    @Inject
    CameraManager(final Context context) {
        windowManager = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE));
    }

    public void open() {
        camera = Camera.open(getBackFacingCameraId());

        if (camera == null) {
            throw new RuntimeException("Camera not available");
        }

        if (generateBitmapStream == null) {
            generateBitmapStream = new ByteArrayOutputStream();
        }

        Camera.Parameters parameters = camera.getParameters();
        parameters.setPreviewSize(CAMERA_WIDTH, CAMERA_HEIGHT);
        parameters.setPreviewFormat(ImageFormat.NV21);
        camera.setParameters(parameters);
        camera.setPreviewCallback((data, camera1) -> {
            if (generateBitmapSubscription == null || generateBitmapSubscription.isUnsubscribed()) {
                Camera.Size size = camera.getParameters().getPreviewSize();
                generateBitmapSubscription = generateBitmapObservable(data, size, generateBitmapStream)
                        .subscribe(bitmap -> cameraBitmap = bitmap,
                                throwable -> log.error("Error while generating bitmap: ", throwable));
            }
        });
    }

    public void startCamera(@Nullable SurfaceTexture texture) throws IOException {
        if (texture == null) {
            throw new NullPointerException("You cannot start preview without a preview texture");
        }

        if (camera == null) {
            throw new IllegalStateException("You should call open() method before start camera");
        }

        camera.setPreviewTexture(texture);
        camera.startPreview();

        cameraBitmap = Bitmap.createBitmap(CAMERA_WIDTH, CAMERA_HEIGHT, Bitmap.Config.ARGB_8888);
    }

    public void autoFocus() {
        if (camera != null) {
            try {
                camera.autoFocus((success, camera1) -> camera1.cancelAutoFocus());
            } catch (RuntimeException e) {
                log.warn("Cannot auto focus camera: ", e);
            }
        }
    }

    public void takePicture(final OnTakePictureListener pictureCallback) {
        if (camera != null) {
            camera.takePicture(shutterCallback, rawCallback, (data, camera1) -> {
                log.debug("onPictureTaken - jpeg");

                Bitmap bitmap = BitmapUtils.bytesToBitmap(data);
                pictureCallback.onTakePicture(bitmap);

                camera1.startPreview();
            });
        }
    }

    public Bitmap getCameraBitmap() {
        return cameraBitmap;
    }

    public void stopPreview() {
        if (camera != null) {
            camera.stopPreview();
        }
    }

    public void close() {
        if (camera != null) {
            camera.stopPreview();
            camera.setPreviewCallback(null);
            camera.unlock();
            camera.release();
            camera = null;
        }
        if (generateBitmapSubscription != null) {
            generateBitmapSubscription.unsubscribe();
            generateBitmapSubscription = null;
        }
        if (generateBitmapStream != null) {
            try {
                generateBitmapStream.close();
            } catch (IOException e) {
                log.warn("Error occurred while closing generate bitmap stream: ", e);
            }
            generateBitmapStream = null;
        }
    }

    public void updateCameraDisplayOrientation() {
        android.hardware.Camera.CameraInfo camInfo =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(getBackFacingCameraId(), camInfo);

        Display display = windowManager.getDefaultDisplay();
        int rotation = display.getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        updateCameraDisplayOrientation(degrees);
    }

    public void updateCameraDisplayOrientation(final int degrees) {
        android.hardware.Camera.CameraInfo camInfo =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(getBackFacingCameraId(), camInfo);

        int result;
        if (camInfo.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (camInfo.orientation + degrees) % 360;
            result = (360 - result) % 360;  // compensate the mirror
        } else {  // back-facing
            result = (camInfo.orientation - degrees + 360) % 360;
        }

        if (camera != null) {
            camera.setDisplayOrientation(result);
        }
    }

    private int getBackFacingCameraId() {
        int cameraId = -1;

        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i;
                break;
            }
        }
        return cameraId;
    }

    private Camera.ShutterCallback shutterCallback = () -> log.debug("onShutter");

    private Camera.PictureCallback rawCallback = (data, camera1) -> log.debug("onPictureTaken - raw");

    private Observable<Bitmap> generateBitmapObservable(final byte[] data,
                                                        final Camera.Size previewSize,
                                                        final ByteArrayOutputStream stream) {
        return Observable.just(data)
                .compose(RxUtils.applySchedulers(RxUtils.ANDROID_THREAD_POOL_EXECUTOR, RxUtils.ANDROID_THREAD_POOL_EXECUTOR))
                .flatMap(bytes -> {
                    //Convert YUV to RGB
                    YuvImage yuvimage = new YuvImage(data, ImageFormat.NV21, previewSize.width, previewSize.height, null);
                    yuvimage.compressToJpeg(new Rect(0, 0, previewSize.width, previewSize.height), 50, stream);
                    byte[] jdata = stream.toByteArray();
                    stream.reset();
                    return Observable.just(BitmapFactory.decodeByteArray(jdata, 0, jdata.length));

                    //TODO research decode Yuv data to RGB, below method very slow
//                      int[] imagePixels = convertYUV420_NV21toRGB8888(yuvimage.getYuvData(), previewSize.width, previewSize.height);
//                      return Observable.just(Bitmap.createBitmap(imagePixels, previewSize.width, previewSize.height, Bitmap.Config.ARGB_8888));
                });
    }

    /**
     * Converts YUV420 NV21 to RGB8888
     *
     * @param data   byte array on YUV420 NV21 format.
     * @param width  pixels width
     * @param height pixels height
     * @return a RGB8888 pixels int array. Where each int is a pixels ARGB.
     */
    @SuppressWarnings("UnnecessaryLocalVariable")
    public static int[] convertYUV420_NV21toRGB8888(byte[] data, int width, int height) {
        int size = width * height;
        int offset = size;
        int[] pixels = new int[size];
        int u, v, y1, y2, y3, y4;

        // i percorre os Y and the final pixels
        // k percorre os pixles U e V
        for (int i = 0, k = 0; i < size; i += 2, k += 2) {
            y1 = data[i] & 0xff;
            y2 = data[i + 1] & 0xff;
            y3 = data[width + i] & 0xff;
            y4 = data[width + i + 1] & 0xff;

            u = data[offset + k] & 0xff;
            v = data[offset + k + 1] & 0xff;
            u = u - 128;
            v = v - 128;

            pixels[i] = convertYUVtoRGB(y1, u, v);
            pixels[i + 1] = convertYUVtoRGB(y2, u, v);
            pixels[width + i] = convertYUVtoRGB(y3, u, v);
            pixels[width + i + 1] = convertYUVtoRGB(y4, u, v);

            if (i != 0 && (i + 2) % width == 0)
                i += width;
        }

        return pixels;
    }

    private static int convertYUVtoRGB(int y, int u, int v) {
        int r, g, b;

        r = y + (int) (1.402f * v);
        g = y - (int) (0.344f * u + 0.714f * v);
        b = y + (int) (1.772f * u);
        r = r > 255 ? 255 : r < 0 ? 0 : r;
        g = g > 255 ? 255 : g < 0 ? 0 : g;
        b = b > 255 ? 255 : b < 0 ? 0 : b;
        return 0xff000000 | (b << 16) | (g << 8) | r;
    }
}
