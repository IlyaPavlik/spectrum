package ru.magflayer.spectrum.presentation.common.android.navigation.navigator

import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity

import ru.magflayer.spectrum.presentation.common.android.navigation.Screens
import ru.magflayer.spectrum.presentation.pages.main.camera.ColorCameraFragment
import ru.magflayer.spectrum.presentation.pages.main.history.HistoryFragment
import ru.magflayer.spectrum.presentation.pages.main.history.details.HistoryDetailsFragment
import ru.terrakok.cicerone.android.SupportFragmentNavigator

class MainNavigator
/**
 * Creates SupportFragmentNavigator.
 *
 * @param rootActivity support fragment manager
 * @param containerId  id of the fragments container layout
 */
(private val activity: AppCompatActivity, containerId: Int) : SupportFragmentNavigator(activity.supportFragmentManager, containerId) {

    override fun createFragment(screenKey: String?, data: Any?): Fragment {
        return when (screenKey) {
            Screens.CAMERA_SCREEN -> ColorCameraFragment.newInstance()
            Screens.HISTORY_SCREEN -> HistoryFragment.newInstance()
            Screens.HISTORY_DETAILS_SCREEN -> HistoryDetailsFragment.newInstance(data as String)
            else -> throw IllegalArgumentException("Illegal screen key: $screenKey")
        }
    }

    override fun showSystemMessage(message: String) {
        //do nothing
    }

    override fun exit() {
        activity.finish()
    }
}
