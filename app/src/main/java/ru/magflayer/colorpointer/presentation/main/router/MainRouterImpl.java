package ru.magflayer.colorpointer.presentation.main.router;

import android.support.v4.app.FragmentManager;

import ru.magflayer.colorpointer.presentation.common.FragmentRouter;
import ru.magflayer.colorpointer.presentation.main.camera.ColorCameraFragment;

public class MainRouterImpl extends FragmentRouter implements MainRouter {

    public MainRouterImpl(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public void openColorCameraPage() {
        replaceFragment(ColorCameraFragment.newInstance(), false);
    }
}
