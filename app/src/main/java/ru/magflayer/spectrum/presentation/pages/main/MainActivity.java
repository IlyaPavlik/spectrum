package ru.magflayer.spectrum.presentation.pages.main;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;

import javax.inject.Inject;

import butterknife.BindView;
import ru.magflayer.spectrum.R;
import ru.magflayer.spectrum.domain.injection.InjectorManager;
import ru.magflayer.spectrum.presentation.common.BaseActivity;
import ru.magflayer.spectrum.presentation.common.Layout;
import ru.magflayer.spectrum.domain.manager.CameraManager;
import ru.magflayer.spectrum.presentation.pages.main.router.MainRouter;
import ru.magflayer.spectrum.presentation.pages.main.router.MainRouterImpl;
import ru.magflayer.spectrum.presentation.pages.main.toolbar.ToolbarPresenter;
import ru.magflayer.spectrum.presentation.pages.main.toolbar.ToolbarViewHolder;

@Layout(R.layout.activity_main)
public class MainActivity extends BaseActivity<MainPresenter> implements MainView {

    @BindView(R.id.toolbar)
    protected Toolbar toolbar;

    @BindView(R.id.fab)
    protected FloatingActionButton floatingActionButton;

    @Inject
    protected MainPresenter presenter;
    @Inject
    protected ToolbarPresenter toolbarPresenter;

    @Inject
    protected CameraManager cameraManager;

    private MainRouter mainRouter;
    private ToolbarViewHolder toolbarViewHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mainRouter = new MainRouterImpl(this);
        getPresenter().setRouter(mainRouter);

        toolbarViewHolder = new ToolbarViewHolder(this, toolbar);

        if (savedInstanceState == null) {
            getPresenter().openMainScreen();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            cameraManager.open();
        } catch (RuntimeException e) {
            logger.error("Camera not available: ", e);
        }
        toolbarViewHolder.onRegisterBus();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraManager.close();
        toolbarViewHolder.onUnregisterBus();
    }

    public MainRouter getRouter() {
        return mainRouter;
    }

    @Override
    protected void inject() {
        InjectorManager.getAppComponent().inject(this);
    }

    @NonNull
    @Override
    protected MainPresenter getPresenter() {
        return presenter;
    }

    public FloatingActionButton getFloatingActionButton() {
        return floatingActionButton;
    }

    @Override
    public void showToolbar(boolean showToolbar) {
        if (toolbar != null) {
            toolbar.setVisibility(showToolbar ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void showFloatingButton(boolean showFloatingButton) {
        if (floatingActionButton != null) {
            floatingActionButton.setVisibility(showFloatingButton ? View.VISIBLE : View.GONE);
        }
    }
}
