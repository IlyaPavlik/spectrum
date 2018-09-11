package ru.magflayer.spectrum.presentation.pages.main.camera;

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
import android.widget.ToggleButton;

import com.arellomobile.mvp.presenter.InjectPresenter;

import java.util.List;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.OnClick;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.magflayer.spectrum.R;
import ru.magflayer.spectrum.domain.injection.InjectorManager;
import ru.magflayer.spectrum.presentation.common.android.BaseFragment;
import ru.magflayer.spectrum.presentation.common.android.layout.Layout;
import ru.magflayer.spectrum.presentation.common.android.widget.ColorDetailsWidget;
import ru.magflayer.spectrum.presentation.common.android.widget.PointView;
import ru.magflayer.spectrum.presentation.common.model.SurfaceInfo;
import ru.magflayer.spectrum.presentation.common.utils.ViewUtils;

@Layout(R.layout.fragment_color_camera)
public class ColorCameraFragment extends BaseFragment implements TextureView.SurfaceTextureListener, ColorCameraView {

    private final static int ROTATION_INTERVAL = 5;

    @InjectPresenter
    ColorCameraPresenter presenter;

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
    ToggleButton toggleView;
    @BindView(R.id.point_detector)
    PointView pointView;
    @BindView(R.id.color_details)
    ColorDetailsWidget colorDetailsWidget;
    @BindView(R.id.message)
    TextView messageView;

    @BindDimen(R.dimen.color_list_width)
    protected int menuSize;

    private ColorCameraAdapter adapter;
    private OrientationEventListener orientationEventListener;
    private Orientation currentOrientation;

    public static ColorCameraFragment newInstance() {
        return new ColorCameraFragment();
    }

    @Override
    protected void inject() {
        InjectorManager.getAppComponent().inject(this);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        presenter.setRouter(getRouter());
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

        updateMode(toggleView.isChecked());
    }

    @Override
    public void onDestroyView() {
        orientationEventListener.disable();
        super.onDestroyView();
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        colorRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        adapter = new ColorCameraAdapter(getContext());
        colorRecycler.setAdapter(adapter);
        cameraView.setSurfaceTextureListener(this);
        colorRecycler.setVisibility(toggleView.isChecked() ? View.GONE : View.VISIBLE);
        toggleView.setOnCheckedChangeListener((buttonView, isChecked) -> updateMode(isChecked));
    }

    @Override
    public void onSurfaceTextureAvailable(final SurfaceTexture surface, final int width, final int height) {
        logger.debug("onSurfaceTextureAvailable");
        presenter.handleSurfaceAvailable(surface);
    }

    @Override
    public void onSurfaceTextureSizeChanged(final SurfaceTexture surface, final int width, final int height) {
        //do nothing
    }

    @Override
    public boolean onSurfaceTextureDestroyed(final SurfaceTexture surface) {
        logger.debug("onSurfaceTextureDestroyed");
        presenter.handleSurfaceDestroyed();
        return true;
    }

    @Override
    public void onSurfaceTextureUpdated(final SurfaceTexture surface) {
        presenter.updateSurface(toggleView.isChecked() ? SurfaceInfo.Type.SINGLE : SurfaceInfo.Type.MULTIPLE);
    }

    @Override
    public void showPictureSavedToast() {
        Toast.makeText(getContext(), R.string.camera_image_saved, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showColors(final List<Palette.Swatch> colors) {
        adapter.setData(colors);
    }

    @Override
    public void showColorDetails(final int mainColor, final int titleColor) {
        colorDetailsWidget.setColor(mainColor);
        pointView.setAimColor(titleColor);
    }

    @Override
    public void showColorName(final String name) {
        colorDetailsWidget.setColorName(name);
    }

    @Override
    public void showErrorMessage() {
        messageView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideErrorMessage() {
        messageView.setVisibility(View.GONE);
    }

    @Override
    public void showCrosshair() {
        pointView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideCrosshair() {
        pointView.setVisibility(View.GONE);
    }

    @Override
    public void showPanels() {
        showTopMenu();
        showBottomMenu();
    }

    @OnClick(R.id.camera)
    protected void onFocusClick() {
        presenter.handleFocusClicked();
    }

    @OnClick(R.id.menu)
    protected void onMenuClick() {
        presenter.openHistory();
    }

    @OnClick(R.id.save)
    protected void onSaveClick() {
        presenter.handleSaveClicked();
    }

    private void showTopMenu() {
        if (buttonsMenuView.getVisibility() != View.VISIBLE) {
            buttonsMenuView.setVisibility(View.VISIBLE);
            buttonsMenuView.startAnimation(inFromTopAnimation());
        }
    }

    private void showBottomMenu() {
        if (infoMenuView.getVisibility() != View.VISIBLE) {
            infoMenuView.setVisibility(View.VISIBLE);
            infoMenuView.startAnimation(inFromBottomAnimation());
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
        ViewUtils.rotateView(toggleView, orientation.getDegree());
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
            ViewUtils.changeViewVisibility(true, colorDetailsWidget, pointView);
            ViewUtils.changeViewVisibility(false, colorRecycler);
        } else {
            ViewUtils.changeViewVisibility(false, colorDetailsWidget, pointView);
            ViewUtils.changeViewVisibility(true, colorRecycler);
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
