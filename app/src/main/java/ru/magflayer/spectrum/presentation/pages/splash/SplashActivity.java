package ru.magflayer.spectrum.presentation.pages.splash;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import javax.inject.Inject;

import ru.magflayer.spectrum.injection.InjectorManager;
import ru.magflayer.spectrum.presentation.common.BaseActivity;
import ru.magflayer.spectrum.presentation.router.GlobalRouterImpl;

public class SplashActivity extends BaseActivity<SplashPresenter> {

    private static final int CAMERA_PERMISSION_REQUEST = 111;
    private static final int SPLASH_DELAY = 300;

    @Inject
    protected SplashPresenter presenter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getPresenter().setRouter(new GlobalRouterImpl(this));

        int permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA);
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_REQUEST);
            return;
        }
        startMainPage();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case CAMERA_PERMISSION_REQUEST: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    startMainPage();
                } else {
                    finish();
                }
                return;
            }
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void startMainPage() {
        new Handler().postDelayed(() -> {
            getPresenter().openMainPage();
            finish();
        }, SPLASH_DELAY);
    }
}
