package ru.magflayer.spectrum.presentation.common.android.navigation.holder;

import ru.terrakok.cicerone.BaseRouter;
import ru.terrakok.cicerone.Cicerone;
import ru.terrakok.cicerone.Navigator;
import ru.terrakok.cicerone.NavigatorHolder;

public abstract class BaseRouterHolder<T extends BaseRouter> {

    private Cicerone<T> cicerone;

    BaseRouterHolder(T router) {
        cicerone = Cicerone.create(router);
    }

    public T getRouter() {
        return cicerone.getRouter();
    }

    public NavigatorHolder getNavigatorHolder() {
        return cicerone.getNavigatorHolder();
    }

    public void setNavigator(final Navigator navigator) {
        getNavigatorHolder().setNavigator(navigator);
    }

    public void removeNavigator() {
        getNavigatorHolder().removeNavigator();
    }

}
