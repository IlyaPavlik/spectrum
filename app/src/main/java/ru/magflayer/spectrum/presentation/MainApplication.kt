package ru.magflayer.spectrum.presentation

import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDexApplication
import ru.magflayer.spectrum.domain.injection.InjectorManager

class MainApplication : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()

        InjectorManager.init(this)
    }

    companion object {

        //    @Inject
        //    protected CameraManager cameraManager;

        init {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        }
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
