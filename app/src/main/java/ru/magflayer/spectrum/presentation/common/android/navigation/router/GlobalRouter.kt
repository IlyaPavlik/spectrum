package ru.magflayer.spectrum.presentation.common.android.navigation.router

import ru.magflayer.spectrum.presentation.common.android.navigation.Screens
import ru.terrakok.cicerone.Router

class GlobalRouter : Router() {

    fun startSplash() = replaceScreen(Screens.SPLASH_ACTIVITY_SCREEN)

    fun startMain() = replaceScreen(Screens.MAIN_ACTIVITY_SCREEN)

}
