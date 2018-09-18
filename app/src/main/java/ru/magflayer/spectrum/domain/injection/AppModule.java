package ru.magflayer.spectrum.domain.injection;

import android.arch.persistence.room.Room;
import android.content.Context;

import com.google.gson.Gson;
import com.squareup.otto.Bus;
import com.squareup.otto.ThreadEnforcer;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.magflayer.spectrum.data.database.AppDatabase;
import ru.magflayer.spectrum.data.system.LocalFileManager;
import ru.magflayer.spectrum.domain.repository.FileManagerRepository;
import ru.magflayer.spectrum.domain.repository.PhotoRepository;

@Module
class AppModule {

    private Context context;

    AppModule(Context context) {
        this.context = context;
    }

    @Provides
    Context provideContext() {
        return context;
    }

    @Singleton
    @Provides
    Bus provideBus() {
        return new Bus(ThreadEnforcer.ANY);
    }

    @Singleton
    @Provides
    Gson provideGson() {
        return new Gson();
    }

    @Singleton
    @Provides
    AppDatabase provideAppDatabase() {
        return Room.databaseBuilder(context, AppDatabase.class, AppDatabase.DATABASE_NAME)
                .build();
    }

    @Singleton
    @Provides
    PhotoRepository providePhotoRepository(final AppDatabase appDatabase) {
        return appDatabase;
    }

    @Singleton
    @Provides
    FileManagerRepository provideFileManagerRepository(final LocalFileManager localFileManager) {
        return localFileManager;
    }
}
