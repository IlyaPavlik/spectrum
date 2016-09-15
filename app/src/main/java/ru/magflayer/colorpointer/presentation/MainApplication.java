package ru.magflayer.colorpointer.presentation;

import android.app.Application;

import ru.magflayer.colorpointer.injection.InjectorManager;

public class MainApplication extends Application {

//    @Inject
//    protected CameraManager cameraManager;

    @Override
    public void onCreate() {
        super.onCreate();

        InjectorManager.init(this);
//        Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
//            @Override
//            public void uncaughtException(Thread thread, Throwable ex) {
//                cameraManager.close();
//                ex.printStackTrace();
//                System.exit(1);
//            }
//        });
    }
}
