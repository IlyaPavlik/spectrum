package ru.magflayer.spectrum.injection;

import android.app.Application;

public class InjectorManager {

    private static AppComponent appComponent;

    public static void init(Application application) {
        synchronized (InjectorManager.class) {
            appComponent = DaggerAppComponent.builder()
                    .appModule(new AppModule(application))
                    .build();
        }
    }

    public static AppComponent getAppComponent() {
        return appComponent;
    }
}
