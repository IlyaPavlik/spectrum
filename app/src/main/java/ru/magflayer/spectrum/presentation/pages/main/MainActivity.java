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
import ru.magflayer.spectrum.injection.InjectorManager;
import ru.magflayer.spectrum.presentation.common.BaseActivity;
import ru.magflayer.spectrum.presentation.common.Layout;
import ru.magflayer.spectrum.presentation.manager.CameraManager;
import ru.magflayer.spectrum.presentation.pages.main.router.MainRouter;
import ru.magflayer.spectrum.presentation.pages.main.router.MainRouterImpl;

@Layout(id = R.layout.activity_main)
public class MainActivity extends BaseActivity<MainPresenter> implements MainView {

    @BindView(R.id.toolbar)
    protected Toolbar toolbar;

    @BindView(R.id.fab)
    protected FloatingActionButton floatingActionButton;

    @Inject
    protected MainPresenter presenter;

    @Inject
    protected CameraManager cameraManager;

    private MainRouter mainRouter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mainRouter = new MainRouterImpl(getSupportFragmentManager());
        getPresenter().setRouter(mainRouter);

        if (savedInstanceState == null) {
            getPresenter().openMainScreen();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        cameraManager.open();
    }

    @Override
    protected void onPause() {
        super.onPause();
        cameraManager.close();
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
