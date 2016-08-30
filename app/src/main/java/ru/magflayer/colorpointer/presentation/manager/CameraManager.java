package ru.magflayer.colorpointer.presentation.manager;

import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Build;
import android.support.annotation.Nullable;
import android.view.TextureView;

import java.io.IOException;

import javax.inject.Inject;

@SuppressWarnings("deprecation")
public class CameraManager {

    private static final int CAMERA_WIDTH = 640;
    private static final int CAMERA_HEIGHT = 480;

    @Nullable
    private Camera camera;
    private Bitmap cameraBitmap;

    @Inject
    public CameraManager() {
    }

    public void open() {
        camera = Camera.open();

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

    public Bitmap loadCameraBitmap(TextureView textureView) {
        return textureView.getBitmap(cameraBitmap);
    }

    public void close() {
        if (camera != null) {
            camera.stopPreview();
            camera.release();
            camera = null;
        }
    }

}
