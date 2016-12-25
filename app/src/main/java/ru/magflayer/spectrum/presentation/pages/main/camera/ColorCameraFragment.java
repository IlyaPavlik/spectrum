package ru.magflayer.spectrum.presentation.pages.main.camera;

import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import ru.magflayer.spectrum.R;
import ru.magflayer.spectrum.data.local.SurfaceInfo;
import ru.magflayer.spectrum.domain.model.PageAppearance;
import ru.magflayer.spectrum.domain.model.ToolbarAppearance;
import ru.magflayer.spectrum.injection.InjectorManager;
import ru.magflayer.spectrum.presentation.common.BaseFragment;
import ru.magflayer.spectrum.presentation.common.BasePresenter;
import ru.magflayer.spectrum.presentation.common.Layout;
import ru.magflayer.spectrum.presentation.manager.CameraManager;
import ru.magflayer.spectrum.presentation.widget.ColorDetailsWidget;
import ru.magflayer.spectrum.presentation.widget.PointView;
import ru.magflayer.spectrum.presentation.widget.ToggleWidget;
import ru.magflayer.spectrum.utils.AppUtils;

@Layout(id = R.layout.fragment_color_camera)
public class ColorCameraFragment extends BaseFragment implements TextureView.SurfaceTextureListener, ColorCameraView {

    @BindView(R.id.camera)
    protected TextureView cameraView;
    @BindView(R.id.left_menu)
    ViewGroup leftMenuView;
    @BindView(R.id.right_menu)
    ViewGroup rightMenuView;
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

        String colorsJson = AppUtils.loadJSONFromAsset(getResources().getAssets(), "colors.json");
        presenter.handleColorInfo(colorsJson);

        adapter = new ColorCameraAdapter();
        colorRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        colorRecycler.setAdapter(adapter);
        cameraView.setSurfaceTextureListener(this);
        colorRecycler.setVisibility(toggleView.isSingle() ? View.GONE : View.VISIBLE);
        toggleView.setOnCheckChangedListener(isSingle -> {
            if (isSingle) {
                AppUtils.changeViewVisibility(true, colorDetailsWidget, pointView);
                AppUtils.changeViewVisibility(false, colorRecycler);
            } else {
                AppUtils.changeViewVisibility(false, colorDetailsWidget, pointView);
                AppUtils.changeViewVisibility(true, colorRecycler);
            }
        });
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        try {
            cameraManager.startCamera(surface);
            cameraManager.setCameraDisplayOrientation(getContext());
            hideProgressBar();
            showAnimation();
        } catch (IOException e) {
            logger.error("Error occurred while starting camera ", e);
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        //do nothing
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        cameraManager.stopPreview();
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        boolean isMultiColor = colorRecycler.getVisibility() == View.VISIBLE;
        presenter.updateSurface(isMultiColor ? SurfaceInfo.Type.FULL : SurfaceInfo.Type.SINGLE);
    }

    @Override
    public void showPictureSaved() {
        Toast.makeText(getContext(), R.string.camera_image_saved, Toast.LENGTH_SHORT).show();
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
    public void showColorName(String name) {
        colorDetailsWidget.setColorName(name);
    }

    @Override
    public Bitmap getSurfaceBitmap() {
        return cameraManager.loadCameraBitmap(cameraView);
    }

    @Override
    public PageAppearance getPageAppearance() {
        return PageAppearance.builder()
                .showFloatingButton(false)
                .build();
    }

    @Override
    public ToolbarAppearance getToolbarAppearance() {
        return ToolbarAppearance.builder()
                .visible(ToolbarAppearance.Visibility.INVISIBLE)
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

    @OnClick(R.id.save)
    protected void onSaveClick() {
        Bitmap bitmap = cameraManager.loadCameraBitmap(cameraView);
        presenter.saveColorPicture(bitmap, swatches);
    }

    private void showAnimation(){
        showRightMenu();
        showLeftMenu();
    }

    private void showRightMenu() {
        if (rightMenuView.getVisibility() != View.VISIBLE) {
            rightMenuView.setVisibility(View.VISIBLE);

            rightMenuView.startAnimation(inFromRightAnimation());
        }
    }

    private void showLeftMenu() {
        if (leftMenuView.getVisibility() != View.VISIBLE) {
            leftMenuView.setVisibility(View.VISIBLE);

            leftMenuView.startAnimation(inFromLeftAnimation());
        }
    }

    private Animation inFromLeftAnimation() {
        Animation inFromLeft = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromLeft.setDuration(800);
        inFromLeft.setInterpolator(new AccelerateInterpolator());
        return inFromLeft;
    }

    private Animation inFromRightAnimation() {
        Animation inFromLeft = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, +1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromLeft.setDuration(800);
        inFromLeft.setInterpolator(new AccelerateInterpolator());
        return inFromLeft;
    }
}
