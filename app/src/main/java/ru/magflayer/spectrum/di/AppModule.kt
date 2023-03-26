package ru.magflayer.spectrum.di

import android.content.Context
import androidx.room.Room
import com.google.gson.Gson
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import ru.magflayer.spectrum.data.database.AppDatabase
import ru.magflayer.spectrum.data.repository.ColorInfoRepositoryImpl
import ru.magflayer.spectrum.data.repository.PageAppearanceRepositoryImpl
import ru.magflayer.spectrum.data.repository.PhotoRepositoryImpl
import ru.magflayer.spectrum.data.repository.ToolbarAppearanceRepositoryImpl
import ru.magflayer.spectrum.data.system.LocalFileManager
import ru.magflayer.spectrum.domain.repository.ColorInfoRepository
import ru.magflayer.spectrum.domain.repository.FileManagerRepository
import ru.magflayer.spectrum.domain.repository.PageAppearanceRepository
import ru.magflayer.spectrum.domain.repository.PhotoRepository
import ru.magflayer.spectrum.domain.repository.ToolbarAppearanceRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideGson(): Gson = Gson()

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(context, AppDatabase::class.java, AppDatabase.DATABASE_NAME)
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    @Singleton
    fun providePhotoRepository(appDatabase: AppDatabase): PhotoRepository {
        return PhotoRepositoryImpl(appDatabase)
    }

    @Provides
    @Singleton
    fun provideFileManagerRepository(localFileManager: LocalFileManager): FileManagerRepository {
        return localFileManager
    }

    @Provides
    @Singleton
    fun provideColorInfoRepository(appDatabase: AppDatabase): ColorInfoRepository {
        return ColorInfoRepositoryImpl(appDatabase)
    }

    @Provides
    @Singleton
    fun provideToolbarAppearanceRepository(): ToolbarAppearanceRepository {
        return ToolbarAppearanceRepositoryImpl()
    }

    @Provides
    @Singleton
    fun providePageAppearanceRepository(): PageAppearanceRepository {
        return PageAppearanceRepositoryImpl()
    }
}
