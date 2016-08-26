package ru.magflayer.colorpointer.presentation.main.camera;

import android.graphics.SurfaceTexture;
import android.support.annotation.NonNull;
import android.view.TextureView;

import java.io.IOException;

import javax.inject.Inject;

import butterknife.BindView;
import ru.magflayer.colorpointer.R;
import ru.magflayer.colorpointer.presentation.common.BaseFragment;
import ru.magflayer.colorpointer.presentation.common.BasePresenter;
import ru.magflayer.colorpointer.presentation.common.Layout;
import ru.magflayer.colorpointer.presentation.injection.InjectorManager;
import ru.magflayer.colorpointer.presentation.manager.CameraManager;

@Layout(id = R.layout.fragment_color_camera)
public class ColorCameraFragment extends BaseFragment implements TextureView.SurfaceTextureListener {

    @BindView(R.id.camera)
    protected TextureView cameraView;

    @Inject
    protected ColorCameraPresenter presenter;

    @Inject
    protected CameraManager cameraManager;

    public static ColorCameraFragment newInstance() {
        return new ColorCameraFragment();
    }

    @NonNull
    @Override
    protected BasePresenter getPresenter() {
        return presenter;
    }

    @Override
    protected void inject() {
        InjectorManager.getAppComponent().inject(this);
    }

    @Override
    public void onResume() {
        super.onResume();

        cameraManager.open();
        cameraView.setSurfaceTextureListener(this);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        cameraManager.close();
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        try {
            cameraManager.startCamera(surface);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        return false;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {

    }
}
