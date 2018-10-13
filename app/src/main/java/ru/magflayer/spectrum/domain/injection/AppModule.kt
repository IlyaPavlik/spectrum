package ru.magflayer.spectrum.domain.injection

import android.arch.persistence.room.Room
import android.content.Context
import com.google.gson.Gson
import com.squareup.otto.Bus
import com.squareup.otto.ThreadEnforcer
import dagger.Module
import dagger.Provides
import ru.magflayer.spectrum.data.database.AppDatabase
import ru.magflayer.spectrum.data.database.ColorInfoRepositoryImpl
import ru.magflayer.spectrum.data.system.LocalFileManager
import ru.magflayer.spectrum.domain.repository.ColorInfoRepository
import ru.magflayer.spectrum.domain.repository.FileManagerRepository
import ru.magflayer.spectrum.domain.repository.PhotoRepository
import javax.inject.Singleton

@Module
internal class AppModule(private val context: Context) {

    @Provides
    fun provideContext(): Context {
        return context
    }

    @Singleton
    @Provides
    fun provideBus(): Bus {
        return Bus(ThreadEnforcer.ANY)
    }

    @Singleton
    @Provides
    fun provideGson(): Gson {
        return Gson()
    }

    @Singleton
    @Provides
    fun provideAppDatabase(): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, AppDatabase.DATABASE_NAME)
                .fallbackToDestructiveMigration()
                .build()
    }

    @Singleton
    @Provides
    fun providePhotoRepository(appDatabase: AppDatabase): PhotoRepository {
        return appDatabase
    }

    @Singleton
    @Provides
    fun provideFileManagerRepository(localFileManager: LocalFileManager): FileManagerRepository {
        return localFileManager
    }

    @Singleton
    @Provides
    fun provideColorInfoRepository(appDatabase: AppDatabase): ColorInfoRepository {
        return ColorInfoRepositoryImpl(appDatabase)
    }
}
