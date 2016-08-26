package ru.magflayer.colorpointer.presentation.main.camera;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import ru.magflayer.colorpointer.presentation.common.BaseFragment;
import ru.magflayer.colorpointer.presentation.common.BasePresenter;
import ru.magflayer.colorpointer.presentation.injection.InjectorManager;

public class ColorCameraFragment extends BaseFragment {

    @Inject
    protected ColorCameraPresenter presenter;

    @NonNull
    @Override
    protected BasePresenter getPresenter() {
        return presenter;
    }

    @Override
    protected void inject() {
        InjectorManager.getAppComponent().inject(this);
    }
}
