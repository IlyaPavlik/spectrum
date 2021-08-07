package ru.magflayer.spectrum.presentation.common.android.navigation.holder

import com.github.terrakok.cicerone.BaseRouter
import com.github.terrakok.cicerone.Cicerone
import com.github.terrakok.cicerone.Navigator
import com.github.terrakok.cicerone.NavigatorHolder

abstract class BaseRouterHolder<T : BaseRouter> internal constructor(router: T) {

    private val cicerone: Cicerone<T> = Cicerone.create(router)

    val router: T
        get() = cicerone.router

    private val navigatorHolder: NavigatorHolder
        get() = cicerone.getNavigatorHolder()

    fun setNavigator(navigator: Navigator) {
        navigatorHolder.setNavigator(navigator)
    }

    fun removeNavigator() {
        navigatorHolder.removeNavigator()
    }

}
