package ru.magflayer.spectrum.presentation.pages.splash

import moxy.InjectViewState
import ru.magflayer.spectrum.domain.injection.InjectorManager
import ru.magflayer.spectrum.domain.interactor.ColorInfoInteractor
import ru.magflayer.spectrum.presentation.common.android.navigation.router.GlobalRouter
import ru.magflayer.spectrum.presentation.common.mvp.BasePresenter
import rx.functions.Action0
import rx.functions.Action1
import javax.inject.Inject

@InjectViewState
class SplashPresenter : BasePresenter<SplashView>() {

    @Inject
    lateinit var globalRouter: GlobalRouter

    @Inject
    lateinit var colorInfoInteractor: ColorInfoInteractor

    internal fun openMainPage() {
        execute(colorInfoInteractor.uploadColorInfo(),
            Action1 { colorInfoState -> logger.debug("Color info loaded: {}", colorInfoState) },
            Action1 { error ->
                logger.warn("Error while uploaded color info: ", error)
                globalRouter.startMain()
            },
            Action0 { globalRouter.startMain() })
    }

    override fun inject() {
        InjectorManager.appComponent?.inject(this)
    }
}
