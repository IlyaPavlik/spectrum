package ru.pavlik.colorlive.presentation.injection;

import android.content.Context;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
class AppModule {

    private Context context;

    public AppModule(Context context) {
        this.context = context;
    }

    @Singleton
    @Provides
    public Bus provideBus() {
        return new Bus(ThreadEnforcer.ANY);
    }
}
