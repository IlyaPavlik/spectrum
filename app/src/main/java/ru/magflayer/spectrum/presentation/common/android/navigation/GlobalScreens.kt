package ru.magflayer.spectrum.presentation.common.android.navigation

import android.content.Context
import android.content.Intent
import ru.magflayer.spectrum.presentation.pages.main.MainActivity
import ru.magflayer.spectrum.presentation.pages.splash.SplashActivity
import ru.terrakok.cicerone.android.support.SupportAppScreen

object GlobalScreens {

    object Splash : SupportAppScreen() {
        override fun getActivityIntent(context: Context): Intent? {
            return SplashActivity.newIntent(context)
        }
    }

    object Main : SupportAppScreen() {
        override fun getActivityIntent(context: Context): Intent? {
            return MainActivity.newIntent(context)
        }
    }

}