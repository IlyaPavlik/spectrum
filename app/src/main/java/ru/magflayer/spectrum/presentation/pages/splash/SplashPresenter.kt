package ru.magflayer.spectrum.presentation.pages.splash

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import moxy.InjectViewState
import ru.magflayer.spectrum.domain.interactor.ColorInfoInteractor
import ru.magflayer.spectrum.presentation.common.mvp.BasePresenter
import javax.inject.Inject

@InjectViewState
class SplashPresenter @Inject constructor(
    private val colorInfoInteractor: ColorInfoInteractor,
) : BasePresenter<SplashView>() {

    internal fun handlePermissionsGranted() {
        val errorHandler = CoroutineExceptionHandler { _, exception ->
            logger.warn("Error while uploaded color info: ", exception)
            viewState.openMainScreen()
        }
        presenterScope.launch(errorHandler) {
            val colorState = colorInfoInteractor.uploadColorInfo()
            logger.debug("Color info loaded: {}", colorState)
            viewState.openMainScreen()
        }
    }
}
