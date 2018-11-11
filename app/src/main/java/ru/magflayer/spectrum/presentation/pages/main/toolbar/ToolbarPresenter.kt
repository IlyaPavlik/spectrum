package ru.magflayer.spectrum.presentation.pages.main.toolbar

import com.arellomobile.mvp.InjectViewState
import com.squareup.otto.Subscribe
import ru.magflayer.spectrum.domain.injection.InjectorManager
import ru.magflayer.spectrum.presentation.common.android.navigation.router.MainRouter
import ru.magflayer.spectrum.presentation.common.model.ToolbarAppearance
import ru.magflayer.spectrum.presentation.common.mvp.BasePresenter
import javax.inject.Inject

@InjectViewState
class ToolbarPresenter : BasePresenter<ToolbarView>() {

    @Inject
    lateinit var mainRouter: MainRouter

    override fun inject() {
        InjectorManager.appComponent?.inject(this)
    }

    internal fun handleBack() {
        mainRouter.exit()
    }

    @Subscribe
    fun onToolbarAppearance(toolbarAppearance: ToolbarAppearance) {
        val visibility = toolbarAppearance.visible
        when (visibility) {
            ToolbarAppearance.Visibility.VISIBLE -> viewState.showToolbar()
            ToolbarAppearance.Visibility.INVISIBLE -> viewState.hideToolbar()
            else -> {
                //no influence
            }
        }

        if (!toolbarAppearance.title.isEmpty()) {
            viewState.showTitle(toolbarAppearance.title)
        }
    }
}
