package ru.pavlik.colorlive.presentation.main.camera;

import android.support.annotation.NonNull;

import javax.inject.Inject;

import ru.pavlik.colorlive.presentation.common.BaseFragment;
import ru.pavlik.colorlive.presentation.common.BasePresenter;
import ru.pavlik.colorlive.presentation.injection.InjectorManager;

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
