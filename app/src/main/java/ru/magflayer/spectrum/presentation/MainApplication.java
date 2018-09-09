package ru.magflayer.spectrum.presentation;

import android.app.Application;
import android.support.v7.app.AppCompatDelegate;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;

import io.fabric.sdk.android.Fabric;
import ru.magflayer.spectrum.BuildConfig;
import ru.magflayer.spectrum.domain.injection.InjectorManager;

public class MainApplication extends Application {

//    @Inject
//    protected CameraManager cameraManager;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        InjectorManager.init(this);

        CrashlyticsCore core = new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build();
        Fabric.with(this, new Crashlytics.Builder().core(core).build());
    }

//    private class UnexpectedTerminationHelper {
//        private Thread mThread;
//        private Thread.UncaughtExceptionHandler mOldUncaughtExceptionHandler = null;
//        private Thread.UncaughtExceptionHandler mUncaughtExceptionHandler = new Thread.UncaughtExceptionHandler() {
//            @Override
//            public void uncaughtException(Thread thread, Throwable ex) { // gets called on the same (main) thread
//                XXXX.closeCamera(); // TODO: write appropriate code here
//                if(mOldUncaughtExceptionHandler != null) {
//                    // it displays the "force close" dialog
//                    mOldUncaughtExceptionHandler.uncaughtException(thread, ex);
//                }
//            }
//        };
//        void init() {
//            mThread = Thread.currentThread();
//            mOldUncaughtExceptionHandler = mThread.getUncaughtExceptionHandler();
//            mThread.setUncaughtExceptionHandler(mUncaughtExceptionHandler);
//        }
//        void fini() {
//            mThread.setUncaughtExceptionHandler(mOldUncaughtExceptionHandler);
//            mOldUncaughtExceptionHandler = null;
//            mThread = null;
//        }
//    }
}
