package ru.magflayer.spectrum.presentation.manager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Build;
import android.support.annotation.Nullable;
import android.view.Display;
import android.view.Surface;
import android.view.TextureView;
import android.view.WindowManager;

import com.squareup.otto.Bus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

import javax.inject.Inject;
import javax.inject.Singleton;

import ru.magflayer.spectrum.utils.BitmapUtils;

@SuppressWarnings("deprecation")
@Singleton
public class CameraManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(CameraManager.class);

    private static final int CAMERA_WIDTH = 1280;
    private static final int CAMERA_HEIGHT = 720;

    interface OnTakePictureListener {
        void onTakePicture(Bitmap bitmap);
    }

    @Nullable
    private Camera camera;
    private Bitmap cameraBitmap;

    @Inject
    protected Bus bus;

    @Inject
    CameraManager() {
    }

    public void open() {
        camera = Camera.open(getBackFacingCameraId());

        Camera.Parameters parameters = camera.getParameters();
        parameters.setPreviewSize(CAMERA_WIDTH, CAMERA_HEIGHT);
        camera.setParameters(parameters);
    }

    public void startCamera(@Nullable SurfaceTexture texture) throws IOException {
        if (texture == null) {
            throw new NullPointerException("You cannot start preview without a preview texture");
        }

        if (camera == null) {
            throw new IllegalStateException("You should call open() method before start camera");
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            camera.setPreviewTexture(texture);
        } else {
            throw new IllegalStateException("Your Android version does not support this method.");
        }
        camera.startPreview();

        cameraBitmap = Bitmap.createBitmap(CAMERA_WIDTH, CAMERA_HEIGHT, Bitmap.Config.ARGB_8888);
    }

    public void autoFocus() {
        if (camera != null) {
            camera.autoFocus(new Camera.AutoFocusCallback() {
                @Override
                public void onAutoFocus(boolean success, Camera camera) {
                    camera.cancelAutoFocus();
                }
            });
        }
    }

    public void takePicture(final OnTakePictureListener pictureCallback) {
        if (camera != null) {
            camera.takePicture(shutterCallback, rawCallback, new Camera.PictureCallback() {
                @Override
                public void onPictureTaken(byte[] data, Camera camera) {
                    LOGGER.debug("onPictureTaken - jpeg");

                    Bitmap bitmap = BitmapUtils.bytesToBitmap(data);
                    pictureCallback.onTakePicture(bitmap);

                    camera.startPreview();
                }
            });
        }
    }

    public Bitmap loadCameraBitmap(final TextureView textureView) {
        return textureView.getBitmap(cameraBitmap);
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
    }

    public void setCameraDisplayOrientation(Context context) {
        android.hardware.Camera.CameraInfo camInfo =
                new android.hardware.Camera.CameraInfo();
        android.hardware.Camera.getCameraInfo(getBackFacingCameraId(), camInfo);

        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
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

    private Camera.ShutterCallback shutterCallback = new Camera.ShutterCallback() {
        public void onShutter() {
            LOGGER.debug("onShutter");
        }
    };

    private Camera.PictureCallback rawCallback = new Camera.PictureCallback() {
        public void onPictureTaken(byte[] data, Camera camera) {
            LOGGER.debug("onPictureTaken - raw");
        }
    };
}
