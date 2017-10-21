package ru.magflayer.spectrum.presentation.pages.main.camera;

import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.OrientationEventListener;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.OnClick;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.magflayer.spectrum.R;
import ru.magflayer.spectrum.data.local.SurfaceInfo;
import ru.magflayer.spectrum.domain.injection.InjectorManager;
import ru.magflayer.spectrum.domain.model.PageAppearance;
import ru.magflayer.spectrum.domain.model.ToolbarAppearance;
import ru.magflayer.spectrum.presentation.common.BaseFragment;
import ru.magflayer.spectrum.presentation.common.BasePresenter;
import ru.magflayer.spectrum.presentation.common.Layout;
import ru.magflayer.spectrum.presentation.manager.CameraManager;
import ru.magflayer.spectrum.presentation.widget.ColorDetailsWidget;
import ru.magflayer.spectrum.presentation.widget.PointView;
import ru.magflayer.spectrum.presentation.widget.ToggleWidget;
import ru.magflayer.spectrum.utils.AppUtils;
import ru.magflayer.spectrum.utils.ViewUtils;

@Layout(R.layout.fragment_color_camera)
public class ColorCameraFragment extends BaseFragment implements TextureView.SurfaceTextureListener, ColorCameraView {

    private final static int ROTATION_INTERVAL = 5;

    @BindView(R.id.camera)
    TextureView cameraView;
    @BindView(R.id.right_menu)
    ViewGroup buttonsMenuView;
    @BindView(R.id.left_menu)
    ViewGroup infoMenuView;

    @BindView(R.id.menu)
    View menuButton;
    @BindView(R.id.color_recycler)
    RecyclerView colorRecycler;
    @BindView(R.id.toggle_mode)
    ToggleWidget toggleView;
    @BindView(R.id.point_detector)
    PointView pointView;
    @BindView(R.id.color_details)
    ColorDetailsWidget colorDetailsWidget;
    @BindView(R.id.message)
    TextView messageView;

    @Inject
    protected ColorCameraPresenter presenter;
    @Inject
    protected CameraManager cameraManager;

    @BindDimen(R.dimen.color_list_width)
    protected int menuSize;

    private ColorCameraAdapter adapter;
    private List<Palette.Swatch> swatches = new ArrayList<>();
    private OrientationEventListener orientationEventListener;

    private Orientation currentOrientation;

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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        orientationEventListener = new OrientationEventListener(getContext()) {
            @Override
            public void onOrientationChanged(int orientation) {
                if (isLandscape(orientation) && currentOrientation != Orientation.LANDSCAPE) {
                    currentOrientation = Orientation.LANDSCAPE;
                    setOrientation(currentOrientation);
                } else if (isPortrait(orientation) && currentOrientation != Orientation.PORTRAIT) {
                    currentOrientation = Orientation.PORTRAIT;
                    setOrientation(currentOrientation);
                }
            }
        };

        if (orientationEventListener.canDetectOrientation()) {
            orientationEventListener.enable();
        } else {
            orientationEventListener.disable();
        }

        updateMode(toggleView.isSingle());
    }

    @Override
    public void onDestroyView() {
        orientationEventListener.disable();
        super.onDestroyView();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        String colorsJson = AppUtils.loadJSONFromAsset(getResources().getAssets(), "colors.json");
        presenter.handleColorInfo(colorsJson);

        colorRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        adapter = new ColorCameraAdapter(getContext());
        colorRecycler.setAdapter(adapter);
        cameraView.setSurfaceTextureListener(this);
        colorRecycler.setVisibility(toggleView.isSingle() ? View.GONE : View.VISIBLE);
        toggleView.setOnCheckChangedListener(this::updateMode);
    }

    @Override
    public void onSurfaceTextureAvailable(SurfaceTexture surface, int width, int height) {
        logger.debug("onSurfaceTextureAvailable");
        hideProgressBar();
        try {
            messageView.setVisibility(View.GONE);
            pointView.setVisibility(View.VISIBLE);
            cameraManager.startCamera(surface);
            cameraManager.setCameraDisplayOrientation(getContext());
            showAnimation();
        } catch (Exception e) {
            logger.error("Error occurred while starting camera ", e);
            messageView.setVisibility(View.VISIBLE);
            pointView.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSurfaceTextureSizeChanged(SurfaceTexture surface, int width, int height) {
        //do nothing
    }

    @Override
    public boolean onSurfaceTextureDestroyed(SurfaceTexture surface) {
        logger.debug("onSurfaceTextureDestroyed");
        cameraManager.stopPreview();
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(SurfaceTexture surface) {
        presenter.updateSurface(toggleView.isSingle() ? SurfaceInfo.Type.SINGLE : SurfaceInfo.Type.MULTIPLE);
    }

    @Override
    public void showPictureSaved() {
        Toast.makeText(getContext(), R.string.camera_image_saved, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showColors(List<Palette.Swatch> colors) {
        adapter.setData(colors);
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
        return cameraManager.getCameraBitmap();
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
        Bitmap bitmap = cameraManager.getCameraBitmap();
        presenter.saveColorPicture(bitmap, swatches);
    }

    private void showAnimation() {
        showTopMenu();
        showBottomMenu();
    }

    private void showBottomMenu() {
        if (infoMenuView.getVisibility() != View.VISIBLE) {
            infoMenuView.setVisibility(View.VISIBLE);

            infoMenuView.startAnimation(inFromBottomAnimation());
        }
    }

    private void showTopMenu() {
        if (buttonsMenuView.getVisibility() != View.VISIBLE) {
            buttonsMenuView.setVisibility(View.VISIBLE);

            buttonsMenuView.startAnimation(inFromTopAnimation());
        }
    }

    private Animation inFromTopAnimation() {
        Animation inFromTop = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, +1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromTop.setDuration(800);
        inFromTop.setInterpolator(new AccelerateInterpolator());
        return inFromTop;
    }

    private Animation inFromBottomAnimation() {
        Animation inFromBottom = new TranslateAnimation(
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f,
                Animation.RELATIVE_TO_PARENT, -1.0f,
                Animation.RELATIVE_TO_PARENT, 0.0f);
        inFromBottom.setDuration(800);
        inFromBottom.setInterpolator(new AccelerateInterpolator());
        return inFromBottom;
    }

    private void setOrientation(final Orientation orientation) {
        logger.debug("Orientation changed: {}", orientation);
        ViewUtils.rotateView(menuButton, orientation.getDegree());
        toggleView.rotateIcons(orientation.getDegree());
        colorDetailsWidget.rotate(orientation.getDegree());
    }

    private boolean isPortrait(final int orientation) {
        return orientation <= ROTATION_INTERVAL || orientation >= 360 - ROTATION_INTERVAL // [350 : 10]
                || (orientation <= 180 + ROTATION_INTERVAL && orientation >= 180 - ROTATION_INTERVAL); //[170 : 190]
    }

    private boolean isLandscape(final int orientation) {
        return (orientation <= 90 + ROTATION_INTERVAL && orientation >= 90 - ROTATION_INTERVAL) // [100 : 80]
                || (orientation <= 270 + ROTATION_INTERVAL && orientation >= 270 - ROTATION_INTERVAL); // [280 : 260]
    }

    private void updateMode(final boolean single) {
        if (single) {
            AppUtils.changeViewVisibility(true, colorDetailsWidget, pointView);
            AppUtils.changeViewVisibility(false, colorRecycler);
        } else {
            AppUtils.changeViewVisibility(false, colorDetailsWidget, pointView);
            AppUtils.changeViewVisibility(true, colorRecycler);
        }
    }

    @Getter
    @AllArgsConstructor
    private enum Orientation {
        PORTRAIT(0),
        LANDSCAPE(90);

        private int degree;
    }
}
