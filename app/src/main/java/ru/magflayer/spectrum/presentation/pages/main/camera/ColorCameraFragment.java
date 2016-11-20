package ru.magflayer.spectrum.presentation.pages.main.camera;

import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.TextureView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import ru.magflayer.spectrum.R;
import ru.magflayer.spectrum.domain.model.PageAppearance;
import ru.magflayer.spectrum.injection.InjectorManager;
import ru.magflayer.spectrum.presentation.common.BaseFragment;
import ru.magflayer.spectrum.presentation.common.BasePresenter;
import ru.magflayer.spectrum.presentation.common.Layout;
import ru.magflayer.spectrum.presentation.manager.CameraManager;
import ru.magflayer.spectrum.presentation.widget.ColorDetailsWidget;
import ru.magflayer.spectrum.presentation.widget.PointView;
import ru.magflayer.spectrum.presentation.widget.ToggleWidget;

@Layout(id = R.layout.fragment_color_camera)
public class ColorCameraFragment extends BaseFragment implements TextureView.SurfaceTextureListener, ColorCameraView {

    private static final int PERIOD = 3;

    @BindView(R.id.camera)
    protected TextureView cameraView;
    @BindView(R.id.color_recycler)
    protected RecyclerView colorRecycler;
    @BindView(R.id.toggle_mode)
    protected ToggleWidget toggleView;
    @BindView(R.id.point_detector)
    protected PointView pointView;
    @BindView(R.id.color_details)
    protected ColorDetailsWidget colorDetailsWidget;

    @Inject
    protected ColorCameraPresenter presenter;

    @Inject
    protected CameraManager cameraManager;

    private ColorCameraAdapter adapter;
    private List<Palette.Swatch> swatches = new ArrayList<>();

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
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        adapter = new ColorCameraAdapter();
        colorRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        colorRecycler.setAdapter(adapter);
        cameraView.setSurfaceTextureListener(this);
        colorRecycler.setVisibility(toggleView.isSingle() ? View.GONE : View.VISIBLE);
        toggleView.setOnCheckChangedListener(new ToggleWidget.OnCheckChangedListener() {
            @Override
            public void checkChanged(boolean isSingle) {
                if (isSingle) {
                    colorRecycler.setVisibility(View.GONE);
                    colorDetailsWidget.setVisibility(View.VISIBLE);
                    pointView.setVisibility(View.VISIBLE);
                } else {
                    colorRecycler.setVisibility(View.VISIBLE);
                    colorDetailsWidget.setVisibility(View.GONE);
                    pointView.setVisibility(View.GONE);
                }
            }
        });

        int fabColor = ContextCompat.getColor(getContext(), R.color.gray);

        FloatingActionButton fab = getFloatingActionButton();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = cameraManager.loadCameraBitmap(cameraView);
                presenter.saveColorPicture(bitmap, swatches);
            }
        });
        fab.setBackgroundTintList(ColorStateList.valueOf(fabColor));
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        try {
            cameraManager.startCamera(surface);
            cameraManager.setCameraDisplayOrientation(getContext());
        } catch (IOException e) {
            logger.error("Error while start camera ", e);
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {

    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        cameraManager.stopPreview();
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        if (System.currentTimeMillis() % PERIOD == 0) {
            if (colorRecycler.getVisibility() == View.VISIBLE) {
                presenter.handleCameraSurface(cameraManager.loadCameraBitmap(cameraView));
            } else {
                presenter.handleColorDetails(cameraManager.loadCameraBitmap(cameraView));
            }
        }
    }

    @Override
    public void showPictureSaved() {
        Toast.makeText(getContext(), "Picture saved!", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showColors(List<Palette.Swatch> colors) {
        adapter.setColors(colors);
        swatches = new ArrayList<>(colors);
    }

    @Override
    public void showColorDetails(int mainColor, int titleColor) {
        colorDetailsWidget.setColor(mainColor);
        pointView.setAimColor(titleColor);

        swatches = Collections.singletonList(new Palette.Swatch(mainColor, Integer.MAX_VALUE));
    }

    @Override
    public PageAppearance getPageAppearance() {
        return PageAppearance.builder()
                .showFloatingButton(true)
                .build();
    }

    @OnClick(R.id.camera)
    protected void onFocusClick() {
        cameraManager.autoFocus();
    }

    @OnClick(R.id.menu)
    protected void onMenuClick() {
        presenter.openHistory();
    }
}
