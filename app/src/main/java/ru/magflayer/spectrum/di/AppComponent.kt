package ru.magflayer.spectrum.di

import dagger.Component
import ru.magflayer.spectrum.presentation.pages.main.MainActivity
import ru.magflayer.spectrum.presentation.pages.main.MainPresenter
import ru.magflayer.spectrum.presentation.pages.main.camera.ColorCameraFragment
import ru.magflayer.spectrum.presentation.pages.main.camera.ColorCameraPresenter
import ru.magflayer.spectrum.presentation.pages.main.history.HistoryFragment
import ru.magflayer.spectrum.presentation.pages.main.history.HistoryPresenter
import ru.magflayer.spectrum.presentation.pages.main.history.details.HistoryDetailsFragment
import ru.magflayer.spectrum.presentation.pages.main.history.details.HistoryDetailsPresenter
import ru.magflayer.spectrum.presentation.pages.main.toolbar.ToolbarPresenter
import ru.magflayer.spectrum.presentation.pages.main.toolbar.ToolbarViewHolder
import ru.magflayer.spectrum.presentation.pages.splash.SplashActivity
import ru.magflayer.spectrum.presentation.pages.splash.SplashPresenter
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {

    // ACTIVITY

    fun inject(activity: MainActivity)

    fun inject(activity: SplashActivity)

    // FRAGMENT

    fun inject(fragment: ColorCameraFragment)

    fun inject(fragment: HistoryFragment)

    fun inject(fragment: HistoryDetailsFragment)

    // VIEW HOLDER
    fun inject(viewHolder: ToolbarViewHolder)

    // PRESENTER
    fun inject(presenter: SplashPresenter)

    fun inject(presenter: MainPresenter)

    fun inject(presenter: ToolbarPresenter)

    fun inject(presenter: ColorCameraPresenter)

    fun inject(presenter: HistoryPresenter)

    fun inject(presenter: HistoryDetailsPresenter)
}
