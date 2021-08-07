package ru.magflayer.spectrum.presentation.common.android.navigation

import android.content.Context
import android.content.Intent
import com.github.terrakok.cicerone.androidx.ActivityScreen
import ru.magflayer.spectrum.presentation.pages.main.MainActivity
import ru.magflayer.spectrum.presentation.pages.splash.SplashActivity

object GlobalScreens {

    object Splash : ActivityScreen {
        override fun createIntent(context: Context): Intent {
            return SplashActivity.newIntent(context)
        }
    }

    object Main : ActivityScreen {
        override fun createIntent(context: Context): Intent {
            return MainActivity.newIntent(context)
        }
    }

}