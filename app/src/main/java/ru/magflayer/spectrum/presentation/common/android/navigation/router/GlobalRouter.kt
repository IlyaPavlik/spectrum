package ru.magflayer.spectrum.presentation.common.android.navigation.router

import ru.magflayer.spectrum.presentation.common.android.navigation.GlobalScreens
import ru.terrakok.cicerone.Router

class GlobalRouter : Router() {

    fun startSplash() = replaceScreen(GlobalScreens.Splash)

    fun startMain() = replaceScreen(GlobalScreens.Main)

}
