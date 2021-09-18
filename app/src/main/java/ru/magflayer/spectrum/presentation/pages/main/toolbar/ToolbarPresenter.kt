package ru.magflayer.spectrum.presentation.pages.main.toolbar

import moxy.InjectViewState
import ru.magflayer.spectrum.domain.injection.InjectorManager
import ru.magflayer.spectrum.domain.interactor.ToolbarAppearanceInteractor
import ru.magflayer.spectrum.presentation.common.android.navigation.router.MainRouter
import ru.magflayer.spectrum.presentation.common.model.ToolbarAppearance
import ru.magflayer.spectrum.presentation.common.mvp.BasePresenter
import javax.inject.Inject

@InjectViewState
class ToolbarPresenter : BasePresenter<ToolbarView>() {

    @Inject
    lateinit var mainRouter: MainRouter

    @Inject
    lateinit var toolbarAppearanceInteractor: ToolbarAppearanceInteractor

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        execute(
            toolbarAppearanceInteractor.observeToolbarAppearance(),
            onSuccess = { toolbarAppearance -> handleToolbarAppearance(toolbarAppearance) }
        )
    }

    override fun inject() {
        InjectorManager.appComponent?.inject(this)
    }

    internal fun handleBack() {
        mainRouter.exit()
    }

    private fun handleToolbarAppearance(toolbarAppearance: ToolbarAppearance) {
        when (toolbarAppearance.visible) {
            ToolbarAppearance.Visibility.VISIBLE -> viewState.showToolbar()
            ToolbarAppearance.Visibility.INVISIBLE -> viewState.hideToolbar()
            else -> {
                /* do nothing */
            }
        }

        if (toolbarAppearance.title.isNotEmpty()) {
            viewState.showTitle(toolbarAppearance.title)
        }
    }
}
