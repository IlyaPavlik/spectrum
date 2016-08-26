package ru.magflayer.colorpointer.presentation;

import android.app.Application;

import ru.magflayer.colorpointer.presentation.injection.InjectorManager;

public class MainApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        InjectorManager.init(this);

    }
}
