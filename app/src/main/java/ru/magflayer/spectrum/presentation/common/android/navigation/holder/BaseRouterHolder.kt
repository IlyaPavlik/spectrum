package ru.magflayer.spectrum.presentation.common.android.navigation.holder

import ru.terrakok.cicerone.BaseRouter
import ru.terrakok.cicerone.Cicerone
import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.NavigatorHolder

abstract class BaseRouterHolder<T : BaseRouter> internal constructor(router: T) {

    private val cicerone: Cicerone<T> = Cicerone.create(router)

    val router: T
        get() = cicerone.router

    private val navigatorHolder: NavigatorHolder
        get() = cicerone.navigatorHolder

    fun setNavigator(navigator: Navigator) {
        navigatorHolder.setNavigator(navigator)
    }

    fun removeNavigator() {
        navigatorHolder.removeNavigator()
    }

}
