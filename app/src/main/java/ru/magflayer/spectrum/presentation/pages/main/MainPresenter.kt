package ru.magflayer.spectrum.presentation.pages.main

import com.squareup.otto.Subscribe
import moxy.InjectViewState
import ru.magflayer.spectrum.domain.entity.event.FabClickEvent
import ru.magflayer.spectrum.domain.injection.InjectorManager
import ru.magflayer.spectrum.domain.manager.CameraManager
import ru.magflayer.spectrum.presentation.common.android.navigation.router.MainRouter
import ru.magflayer.spectrum.presentation.common.model.PageAppearance
import ru.magflayer.spectrum.presentation.common.mvp.BasePresenter
import javax.inject.Inject

@InjectViewState
class MainPresenter : BasePresenter<MainView>() {

    @Inject
    lateinit var cameraManager: CameraManager
    @Inject
    lateinit var mainRouter: MainRouter

    override fun inject() {
        InjectorManager.appComponent?.inject(this)
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

    internal fun openMainScreen() {
        mainRouter.openCameraScreen()
    }

    internal fun handleFabClick() {
        bus.post(FabClickEvent())
    }

    @Subscribe
    fun onPageAppearanceChanged(pageAppearance: PageAppearance) {
        when (pageAppearance.floatingButtonState) {
            PageAppearance.FloatingButtonState.VISIBLE -> {
                viewState.showFloatingButton(true)
            }
            PageAppearance.FloatingButtonState.INVISIBLE -> {
                viewState.showFloatingButton(false)
            }
            else -> {
                //no influence
            }
        }
    }
}
