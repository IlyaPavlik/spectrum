package ru.magflayer.spectrum.domain.injection

import android.app.Application

object InjectorManager {

    var appComponent: AppComponent? = null
        private set

    fun init(application: Application) {
        synchronized(InjectorManager::class.java) {
            appComponent = DaggerAppComponent.builder()
                .appModule(AppModule(application))
                .build()
        }
    }
}
