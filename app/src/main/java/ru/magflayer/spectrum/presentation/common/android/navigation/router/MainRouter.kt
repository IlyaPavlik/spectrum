package ru.magflayer.spectrum.presentation.common.android.navigation.router

import ru.magflayer.spectrum.presentation.common.android.navigation.MainScreens
import ru.terrakok.cicerone.Router

class MainRouter : Router() {

    fun openCameraScreen() = newRootScreen(MainScreens.Camera)

    fun openHistoryScreen() = navigateTo(MainScreens.HistoryList)

    fun openHistoryDetailsScreen(filePath: String) = navigateTo(MainScreens.HistoryDetails(filePath))

}
