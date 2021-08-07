package ru.magflayer.spectrum.presentation.common.android.navigation

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import com.github.terrakok.cicerone.androidx.FragmentScreen
import ru.magflayer.spectrum.presentation.pages.main.camera.ColorCameraFragment
import ru.magflayer.spectrum.presentation.pages.main.history.HistoryFragment
import ru.magflayer.spectrum.presentation.pages.main.history.details.HistoryDetailsFragment

object MainScreens {

    object Camera : FragmentScreen {
        override fun createFragment(factory: FragmentFactory): Fragment {
            return ColorCameraFragment.newInstance()
        }
    }

    object HistoryList : FragmentScreen {
        override fun createFragment(factory: FragmentFactory): Fragment {
            return HistoryFragment.newInstance()
        }
    }

    class HistoryDetails(val filePath: String) : FragmentScreen {
        override fun createFragment(factory: FragmentFactory): Fragment {
            return HistoryDetailsFragment.newInstance(filePath)
        }
    }
}
