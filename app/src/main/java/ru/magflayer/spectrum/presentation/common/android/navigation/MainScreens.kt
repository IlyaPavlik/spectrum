package ru.magflayer.spectrum.presentation.common.android.navigation

import androidx.fragment.app.Fragment
import ru.magflayer.spectrum.presentation.pages.main.camera.ColorCameraFragment
import ru.magflayer.spectrum.presentation.pages.main.history.HistoryFragment
import ru.magflayer.spectrum.presentation.pages.main.history.details.HistoryDetailsFragment
import ru.terrakok.cicerone.android.support.SupportAppScreen

object MainScreens {
    object Camera : SupportAppScreen() {
        override fun getFragment(): Fragment? {
            return ColorCameraFragment.newInstance()
        }
    }

    object HistoryList : SupportAppScreen() {
        override fun getFragment(): Fragment? {
            return HistoryFragment.newInstance()
        }
    }

    class HistoryDetails(val filePath: String) : SupportAppScreen() {
        override fun getFragment(): Fragment? {
            return HistoryDetailsFragment.newInstance(filePath)
        }
    }
}
