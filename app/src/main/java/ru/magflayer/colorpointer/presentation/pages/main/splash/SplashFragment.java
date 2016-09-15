package ru.magflayer.colorpointer.presentation.pages.main.splash;

import android.os.Bundle;
import android.support.annotation.NonNull;

import javax.inject.Inject;

import ru.magflayer.colorpointer.R;
import ru.magflayer.colorpointer.injection.InjectorManager;
import ru.magflayer.colorpointer.presentation.common.BaseFragment;
import ru.magflayer.colorpointer.presentation.common.BasePresenter;
import ru.magflayer.colorpointer.presentation.common.Layout;

@Layout(id = R.layout.fragment_splash)
public class SplashFragment extends BaseFragment implements SplashView {

    @Inject
    protected SplashPresenter presenter;

    public static SplashFragment newInstance() {
        return new SplashFragment();
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

        presenter.openMainPageWithDelay();
    }
}
