package ru.magflayer.spectrum.presentation

import androidx.appcompat.app.AppCompatDelegate
import androidx.multidex.MultiDexApplication
import ru.magflayer.spectrum.domain.injection.InjectorManager

class MainApplication : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()

        InjectorManager.init(this)
    }

    companion object {

        init {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        }
    }
}
