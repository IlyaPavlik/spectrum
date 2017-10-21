package ru.magflayer.spectrum.presentation.pages.main.splash;

import android.os.Bundle;
import android.support.annotation.NonNull;

import javax.inject.Inject;

import ru.magflayer.spectrum.R;
import ru.magflayer.spectrum.domain.injection.InjectorManager;
import ru.magflayer.spectrum.presentation.common.BaseFragment;
import ru.magflayer.spectrum.presentation.common.BasePresenter;
import ru.magflayer.spectrum.presentation.common.Layout;

@Layout(R.layout.fragment_splash)
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
