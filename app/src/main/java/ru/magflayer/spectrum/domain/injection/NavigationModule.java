package ru.magflayer.spectrum.domain.injection;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import ru.magflayer.spectrum.presentation.common.android.navigation.holder.GlobalRouterHolder;
import ru.magflayer.spectrum.presentation.common.android.navigation.holder.MainRouterHolder;
import ru.magflayer.spectrum.presentation.common.android.navigation.router.GlobalRouter;
import ru.magflayer.spectrum.presentation.common.android.navigation.router.MainRouter;

@Module
class NavigationModule {

    private GlobalRouterHolder globalRouterHolder;
    private MainRouterHolder mainRouterHolder;

    NavigationModule() {
        this.globalRouterHolder = new GlobalRouterHolder();
        this.mainRouterHolder = new MainRouterHolder();
    }

    @Provides
    @Singleton
    GlobalRouterHolder provideGlobalRouterHolder() {
        return globalRouterHolder;
    }

    @Provides
    @Singleton
    MainRouterHolder provideMainRouterHolder() {
        return mainRouterHolder;
    }

    @Provides
    @Singleton
    GlobalRouter provideGlobalRouter() {
        return globalRouterHolder.getRouter();
    }

    @Provides
    @Singleton
    MainRouter provideMainRouter() {
        return mainRouterHolder.getRouter();
    }


}
