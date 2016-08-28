package ru.magflayer.colorpointer.presentation.manager;

import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.os.Build;
import android.support.annotation.Nullable;

import java.io.IOException;

import javax.inject.Inject;

@SuppressWarnings("deprecation")
public class CameraManager {

    @Nullable
    private Camera camera;

    @Inject
    public CameraManager() {
    }

    public void open() {
        camera = Camera.open();
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

    public void close() {
        if (camera != null) {
            camera.release();
            camera = null;
        }
    }

}
