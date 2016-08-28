package ru.magflayer.colorpointer.presentation.main.camera;

import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import ru.magflayer.colorpointer.R;
import ru.magflayer.colorpointer.presentation.common.BaseFragment;
import ru.magflayer.colorpointer.presentation.common.BasePresenter;
import ru.magflayer.colorpointer.presentation.common.Layout;
import ru.magflayer.colorpointer.presentation.injection.InjectorManager;
import ru.magflayer.colorpointer.presentation.manager.CameraManager;
import ru.magflayer.colorpointer.presentation.widget.ToggleWidget;

@Layout(id = R.layout.fragment_color_camera)
public class ColorCameraFragment extends BaseFragment implements TextureView.SurfaceTextureListener, ColorCameraView {

    @BindView(R.id.camera)
    protected TextureView cameraView;
    @BindView(R.id.color_recycler)
    protected RecyclerView colorRecycler;
    @BindView(R.id.color_details)
    protected ViewGroup colorDetailsContainer;

    @BindView(R.id.toggle_mode)
    protected ToggleWidget toggleView;

    @BindView(R.id.color)
    protected View colorView;
    @BindView(R.id.color_id)
    protected TextView colorIdView;
    @BindView(R.id.color_name)
    protected TextView colorNameView;

    @Inject
    protected ColorCameraPresenter presenter;

    @Inject
    protected CameraManager cameraManager;

    private ColorCameraAdapter adapter;

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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        adapter = new ColorCameraAdapter();
        colorRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        colorRecycler.setAdapter(adapter);
        cameraView.setSurfaceTextureListener(this);
        colorRecycler.setVisibility(toggleView.isSingle() ? View.GONE : View.VISIBLE);
        toggleView.setOnCheckChangedListener(new ToggleWidget.OnCheckChangedListener() {
            @Override
            public void checkChanged(boolean isSingle) {
                logger.info("Toggle changed (isSingle): " + isSingle);
                if (isSingle) {
                    colorRecycler.setVisibility(View.GONE);
                    colorDetailsContainer.setVisibility(View.VISIBLE);
                } else {
                    colorRecycler.setVisibility(View.VISIBLE);
                    colorDetailsContainer.setVisibility(View.GONE);
                }
            }
        });
        cameraView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (toggleView.isSingle()) {
                    Bitmap bitmap = cameraView.getBitmap();
                    int pixel = bitmap.getPixel((int) event.getX(), (int) event.getY());

                    colorView.setBackgroundColor(pixel);
                    colorIdView.setText(String.format("#%06X", (0xFFFFFF & pixel)));
                    return false;
                }

                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        cameraManager.open();
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
        cameraManager.close();
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        if (colorRecycler.getVisibility() == View.VISIBLE) {
            presenter.handleCameraSurface(cameraView);
        }
    }

    @Override
    public void showColors(List<Palette.Swatch> colors) {
        adapter.setColors(colors);
    }

    @OnClick(R.id.camera)
    public void onViewClick() {
        cameraManager.autoFocus();
    }
}
