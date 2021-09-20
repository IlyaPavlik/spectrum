package ru.magflayer.spectrum.presentation.pages.main

import moxy.InjectViewState
import ru.magflayer.spectrum.domain.interactor.PageAppearanceInteractor
import ru.magflayer.spectrum.domain.manager.CameraManager
import ru.magflayer.spectrum.presentation.common.android.navigation.router.MainRouter
import ru.magflayer.spectrum.presentation.common.model.PageAppearance
import ru.magflayer.spectrum.presentation.common.mvp.BasePresenter
import javax.inject.Inject

@InjectViewState
class MainPresenter @Inject constructor(
    private val cameraManager: CameraManager,
    private val mainRouter: MainRouter,
    private val pageAppearanceInteractor: PageAppearanceInteractor
) : BasePresenter<MainView>() {

    override fun onFirstViewAttach() {
        mainRouter.openCameraScreen()
        execute(
            pageAppearanceInteractor.observePageAppearance(),
            onSuccess = { pageAppearance -> handlePageAppearanceChanged(pageAppearance) }
        )
    }

    override fun attachView(view: MainView) {
        super.attachView(view)
        try {
            cameraManager.open()
        } catch (e: Exception) {
            logger.error("Camera not available: ", e)
        }
    }

    override fun detachView(view: MainView) {
        try {
            cameraManager.close()
        } catch (e: Exception) {
            logger.error("Cannot close stop camera")
        }
        super.detachView(view)
    }

    fun handleFabClick() {
        pageAppearanceInteractor.publishFabEvent()
    }

    private fun handlePageAppearanceChanged(pageAppearance: PageAppearance) {
        when (pageAppearance.floatingButtonState) {
            PageAppearance.FloatingButtonState.VISIBLE -> {
                viewState.showFloatingButton(true)
            }
            PageAppearance.FloatingButtonState.INVISIBLE -> {
                viewState.showFloatingButton(false)
            }
            else -> {
                /* do nothing */
            }
        }
    }
}
