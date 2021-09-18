package ru.magflayer.spectrum.presentation.pages.splash

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import moxy.InjectViewState
import ru.magflayer.spectrum.domain.injection.InjectorManager
import ru.magflayer.spectrum.domain.interactor.ColorInfoInteractor
import ru.magflayer.spectrum.presentation.common.android.navigation.router.GlobalRouter
import ru.magflayer.spectrum.presentation.common.mvp.BasePresenter
import javax.inject.Inject

@InjectViewState
class SplashPresenter : BasePresenter<SplashView>() {

    @Inject
    lateinit var globalRouter: GlobalRouter

    @Inject
    lateinit var colorInfoInteractor: ColorInfoInteractor

    internal fun openMainPage() {
        val errorHandler = CoroutineExceptionHandler { _, exception ->
            logger.warn("Error while uploaded color info: ", exception)
            globalRouter.startMain()
        }
        presenterScope.launch(errorHandler) {
            val colorState = colorInfoInteractor.uploadColorInfo()
            logger.debug("Color info loaded: {}", colorState)
            globalRouter.startMain()
        }
    }

    override fun inject() {
        InjectorManager.appComponent?.inject(this)
    }
}
