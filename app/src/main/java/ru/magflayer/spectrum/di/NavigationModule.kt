package ru.magflayer.spectrum.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import ru.magflayer.spectrum.presentation.common.android.navigation.holder.GlobalRouterHolder
import ru.magflayer.spectrum.presentation.common.android.navigation.holder.MainRouterHolder
import ru.magflayer.spectrum.presentation.common.android.navigation.router.GlobalRouter
import ru.magflayer.spectrum.presentation.common.android.navigation.router.MainRouter
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NavigationModule {

    private val globalRouterHolder: GlobalRouterHolder = GlobalRouterHolder()
    private val mainRouterHolder: MainRouterHolder = MainRouterHolder()

    @Provides
    @Singleton
    fun provideGlobalRouterHolder(): GlobalRouterHolder {
        return globalRouterHolder
    }

    @Provides
    @Singleton
    fun provideMainRouterHolder(): MainRouterHolder {
        return mainRouterHolder
    }

    @Provides
    @Singleton
    fun provideGlobalRouter(): GlobalRouter {
        return globalRouterHolder.router
    }

    @Provides
    @Singleton
    fun provideMainRouter(): MainRouter {
        return mainRouterHolder.router
    }
}
