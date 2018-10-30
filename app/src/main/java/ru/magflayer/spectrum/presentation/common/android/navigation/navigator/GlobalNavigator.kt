package ru.magflayer.spectrum.presentation.common.android.navigation.navigator

import android.app.Activity
import android.content.Intent
import org.slf4j.LoggerFactory
import ru.magflayer.spectrum.presentation.common.android.navigation.Screens
import ru.magflayer.spectrum.presentation.pages.main.MainActivity
import ru.magflayer.spectrum.presentation.pages.splash.SplashActivity
import ru.terrakok.cicerone.Navigator
import ru.terrakok.cicerone.commands.Back
import ru.terrakok.cicerone.commands.Command
import ru.terrakok.cicerone.commands.Forward
import ru.terrakok.cicerone.commands.Replace

class GlobalNavigator(private val activity: Activity) : Navigator {

    private val log = LoggerFactory.getLogger(javaClass)

    override fun applyCommands(commands: Array<Command>) {
        for (command in commands) applyCommand(command)
    }

    private fun applyCommand(command: Command) {
        when (command) {
            is Forward -> forward(command)
            is Replace -> replace(command)
            is Back -> back()
            else -> log.warn("Cannot handle command: {}", command)
        }
    }

    private fun forward(command: Forward) {
        when (command.screenKey) {
            Screens.MAIN_ACTIVITY_SCREEN -> startActivity(Intent(activity, MainActivity::class.java))
            Screens.SPLASH_ACTIVITY_SCREEN -> startActivity(Intent(activity, SplashActivity::class.java))
            else -> log.warn("Unknown screen: {}", command.screenKey)
        }
    }

    private fun replace(command: Replace) {
        when (command.screenKey) {
            Screens.MAIN_ACTIVITY_SCREEN, Screens.SPLASH_ACTIVITY_SCREEN -> {
                forward(Forward(command.screenKey, command.transitionData))
                activity.finish()
            }
            else -> log.warn("Unknown screen: {}", command.screenKey)
        }
    }

    private fun back() {
        activity.finish()
    }

    private fun startActivity(intent: Intent) {
        activity.startActivity(intent)
    }
}
