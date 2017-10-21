package ru.magflayer.spectrum.domain.injection;

import android.content.Context;

import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.magflayer.spectrum.data.database.AppRealm;

@Module
class AppModule {

    private Context context;

    AppModule(Context context) {
        this.context = context;
    }

    @Singleton
    @Provides
    Bus provideBus() {
        return new Bus(ThreadEnforcer.ANY);
    }

    @Singleton
    @Provides
    AppRealm provideAppRealm(Bus bus) {
        return new AppRealm(context, bus);
    }
}
