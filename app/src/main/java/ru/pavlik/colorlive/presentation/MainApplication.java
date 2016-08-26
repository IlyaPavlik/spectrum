package ru.pavlik.colorlive.presentation;

import android.app.Application;

import ru.pavlik.colorlive.presentation.injection.InjectorManager;

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        InjectorManager.init(this);

    }
}
