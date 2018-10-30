package ru.magflayer.spectrum.presentation.common.android.navigation.router

import ru.magflayer.spectrum.presentation.common.android.navigation.Screens
import ru.terrakok.cicerone.Router

class MainRouter : Router() {

    fun openCameraScreen() = newRootScreen(Screens.CAMERA_SCREEN)

    fun openHistoryScreen() = navigateTo(Screens.HISTORY_SCREEN)

    fun openHistoryDetailsScreen(filePath: String) = navigateTo(Screens.HISTORY_DETAILS_SCREEN, filePath)

}
