package ru.magflayer.spectrum.utils;

import android.os.AsyncTask;

import rx.Observable;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class RxUtils {

    public static final Scheduler ANDROID_THREAD_POOL_EXECUTOR = Schedulers.from(AsyncTask.THREAD_POOL_EXECUTOR);

    public static <T> Observable.Transformer<T, T> applySchedulers(final Scheduler worker, final Scheduler main) {
        return observable -> observable
                .subscribeOn(worker == null ? ANDROID_THREAD_POOL_EXECUTOR : worker)
                .observeOn(main == null ? AndroidSchedulers.mainThread() : main);
    }

    public static <T> Observable.Transformer<T, T> applySchedulers() {
        return applySchedulers(null, null);
    }
}
