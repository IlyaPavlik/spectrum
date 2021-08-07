package ru.magflayer.spectrum.presentation.common.android.navigation.router

import com.github.terrakok.cicerone.Router
import ru.magflayer.spectrum.presentation.common.android.navigation.GlobalScreens

class GlobalRouter : Router() {

    fun startSplash() = replaceScreen(GlobalScreens.Splash)

    fun startMain() = replaceScreen(GlobalScreens.Main)

}
