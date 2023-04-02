package ru.magflayer.spectrum.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.magflayer.spectrum.data.repository.AnalyticsRepositoryImpl
import ru.magflayer.spectrum.data.repository.ColorInfoRepositoryImpl
import ru.magflayer.spectrum.data.repository.PageAppearanceRepositoryImpl
import ru.magflayer.spectrum.data.repository.PhotoRepositoryImpl
import ru.magflayer.spectrum.data.repository.ToolbarAppearanceRepositoryImpl
import ru.magflayer.spectrum.data.system.LocalFileManager
import ru.magflayer.spectrum.domain.repository.AnalyticsRepository
import ru.magflayer.spectrum.domain.repository.ColorInfoRepository
import ru.magflayer.spectrum.domain.repository.FileManagerRepository
import ru.magflayer.spectrum.domain.repository.PageAppearanceRepository
import ru.magflayer.spectrum.domain.repository.PhotoRepository
import ru.magflayer.spectrum.domain.repository.ToolbarAppearanceRepository
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindPhotoRepository(repository: PhotoRepositoryImpl): PhotoRepository

    @Binds
    @Singleton
    abstract fun bindFileManagerRepository(localFileManager: LocalFileManager): FileManagerRepository

    @Binds
    @Singleton
    abstract fun bindColorInfoRepository(repository: ColorInfoRepositoryImpl): ColorInfoRepository

    @Binds
    @Singleton
    abstract fun bindToolbarAppearanceRepository(repository: ToolbarAppearanceRepositoryImpl): ToolbarAppearanceRepository

    @Binds
    @Singleton
    abstract fun bindPageAppearanceRepository(repository: PageAppearanceRepositoryImpl): PageAppearanceRepository

    @Binds
    @Singleton
    abstract fun bindAnalyticsRepository(repository: AnalyticsRepositoryImpl): AnalyticsRepository
}
