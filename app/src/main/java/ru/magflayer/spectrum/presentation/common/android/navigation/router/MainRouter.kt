package ru.magflayer.spectrum.presentation.common.android.navigation.router

import com.github.terrakok.cicerone.Router
import ru.magflayer.spectrum.presentation.common.android.navigation.MainScreens

class MainRouter : Router() {

    fun openCameraScreen() = newRootScreen(MainScreens.Camera)

    fun openHistoryScreen() = navigateTo(MainScreens.HistoryList)

    fun openHistoryDetailsScreen(filePath: String) =
        navigateTo(MainScreens.HistoryDetails(filePath))

}
