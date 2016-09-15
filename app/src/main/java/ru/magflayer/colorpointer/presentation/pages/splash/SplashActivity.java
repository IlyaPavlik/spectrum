package ru.magflayer.colorpointer.presentation.pages.splash;

import android.os.Bundle;
import android.support.annotation.NonNull;

import javax.inject.Inject;

import ru.magflayer.colorpointer.injection.InjectorManager;
import ru.magflayer.colorpointer.presentation.common.BaseActivity;
import ru.magflayer.colorpointer.presentation.pages.router.GlobalRouterImpl;

public class SplashActivity extends BaseActivity<SplashPresenter> {

    @Inject
    protected SplashPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getPresenter().setRouter(new GlobalRouterImpl(this));
        getPresenter().openMainPage();
        finish();
    }

    @Override
    protected void inject() {
        InjectorManager.getAppComponent().inject(this);
    }

    @NonNull
    @Override
    protected SplashPresenter getPresenter() {
        return presenter;
    }
}
