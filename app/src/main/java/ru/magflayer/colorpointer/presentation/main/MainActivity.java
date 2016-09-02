package ru.magflayer.colorpointer.presentation.main;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.view.WindowManager;

import javax.inject.Inject;

import butterknife.BindView;
import ru.magflayer.colorpointer.R;
import ru.magflayer.colorpointer.injection.InjectorManager;
import ru.magflayer.colorpointer.presentation.common.BaseActivity;
import ru.magflayer.colorpointer.presentation.common.Layout;
import ru.magflayer.colorpointer.presentation.main.router.MainRouter;
import ru.magflayer.colorpointer.presentation.main.router.MainRouterImpl;
import ru.magflayer.colorpointer.presentation.manager.CameraManager;

@Layout(id = R.layout.activity_main)
public class MainActivity extends BaseActivity<MainRouter> {

    @BindView(R.id.fab)
    protected FloatingActionButton floatingActionButton;

    @Inject
    protected CameraManager cameraManager;

    private MainRouter mainRouter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mainRouter = new MainRouterImpl(getSupportFragmentManager());

        if (savedInstanceState == null) {
            mainRouter.openColorCameraPage();
        }
    }

    @Override
    public MainRouter getRouter() {
        return mainRouter;
    }

    @Override
    protected void inject() {
        InjectorManager.getAppComponent().inject(this);
    }

    @Override
    protected void onDestroy() {
        cameraManager.close();
        super.onDestroy();
    }

    public FloatingActionButton getFloatingActionButton() {
        return floatingActionButton;
    }
}
