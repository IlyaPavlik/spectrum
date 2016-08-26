package ru.magflayer.colorpointer.presentation.main;

import android.os.Bundle;
import android.view.WindowManager;

import ru.magflayer.colorpointer.R;
import ru.magflayer.colorpointer.presentation.common.BaseActivity;
import ru.magflayer.colorpointer.presentation.main.router.MainRouter;
import ru.magflayer.colorpointer.presentation.main.router.MainRouterImpl;

public class MainActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        MainRouter mainRouter = new MainRouterImpl(getSupportFragmentManager());

        if (savedInstanceState == null) {
            mainRouter.openColorCameraPage();
        }
    }
}
