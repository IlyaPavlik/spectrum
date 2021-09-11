package ru.magflayer.spectrum.domain.injection

import android.content.Context
import androidx.room.Room
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import ru.magflayer.spectrum.data.database.AppDatabase
import ru.magflayer.spectrum.data.database.ColorInfoRepositoryImpl
import ru.magflayer.spectrum.data.repository.PageAppearanceRepositoryImpl
import ru.magflayer.spectrum.data.repository.ToolbarAppearanceRepositoryImpl
import ru.magflayer.spectrum.data.system.LocalFileManager
import ru.magflayer.spectrum.domain.repository.*
import javax.inject.Singleton

@Module
internal class AppModule(private val context: Context) {

    @Provides
    fun provideContext(): Context {
        return context
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

    @Singleton
    @Provides
    fun provideToolbarAppearanceRepository(): ToolbarAppearanceRepository {
        return ToolbarAppearanceRepositoryImpl()
    }

    @Singleton
    @Provides
    fun providePageAppearanceRepository(): PageAppearanceRepository {
        return PageAppearanceRepositoryImpl()
    }
}
