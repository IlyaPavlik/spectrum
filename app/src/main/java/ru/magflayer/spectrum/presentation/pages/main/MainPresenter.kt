package ru.magflayer.spectrum.presentation.pages.main

import moxy.InjectViewState
import ru.magflayer.spectrum.domain.interactor.PageAppearanceInteractor
import ru.magflayer.spectrum.presentation.common.model.PageAppearance
import ru.magflayer.spectrum.presentation.common.mvp.BasePresenter
import javax.inject.Inject

@InjectViewState
class MainPresenter @Inject constructor(
    private val pageAppearanceInteractor: PageAppearanceInteractor
) : BasePresenter<MainView>() {

    override fun onFirstViewAttach() {
        execute(
            pageAppearanceInteractor.observePageAppearance(),
            onSuccess = { pageAppearance -> handlePageAppearanceChanged(pageAppearance) }
        )
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
