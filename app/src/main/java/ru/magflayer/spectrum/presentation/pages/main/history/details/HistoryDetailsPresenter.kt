package ru.magflayer.spectrum.presentation.pages.main.history.details

import moxy.InjectViewState
import ru.magflayer.spectrum.R
import ru.magflayer.spectrum.data.android.ResourceManager
import ru.magflayer.spectrum.domain.entity.AnalyticsEvent
import ru.magflayer.spectrum.domain.entity.ColorPhotoEntity
import ru.magflayer.spectrum.domain.injection.InjectorManager
import ru.magflayer.spectrum.domain.interactor.ColorInfoInteractor
import ru.magflayer.spectrum.domain.interactor.ColorPhotoInteractor
import ru.magflayer.spectrum.domain.manager.AnalyticsManager
import ru.magflayer.spectrum.presentation.common.helper.ColorHelper
import ru.magflayer.spectrum.presentation.common.model.PageAppearance
import ru.magflayer.spectrum.presentation.common.model.ToolbarAppearance
import ru.magflayer.spectrum.presentation.common.mvp.BasePresenter
import rx.functions.Action1
import javax.inject.Inject

@InjectViewState
class HistoryDetailsPresenter internal constructor(filePath: String) :
    BasePresenter<HistoryDetailsView>() {

    @Inject
    lateinit var analyticsManager: AnalyticsManager

    @Inject
    lateinit var colorInfoInteractor: ColorInfoInteractor

    @Inject
    lateinit var resourceManager: ResourceManager

    @Inject
    lateinit var colorPhotoInteractor: ColorPhotoInteractor

    override val toolbarAppearance: ToolbarAppearance
        get() = ToolbarAppearance(
            ToolbarAppearance.Visibility.VISIBLE,
            resourceManager.getString(R.string.history_details_title)
        )

    override val pageAppearance: PageAppearance
        get() = PageAppearance.builder()
            .showFloatingButton(PageAppearance.FloatingButtonState.INVISIBLE)
            .build()

    init {
        loadPicture(filePath)
    }

    override fun inject() {
        InjectorManager.appComponent?.inject(this)
    }

    override fun attachView(view: HistoryDetailsView) {
        super.attachView(view)
        analyticsManager.logEvent(AnalyticsEvent.OPEN_HISTORY_DETAILS)
    }

    private fun loadPicture(filePath: String) {
        execute<ColorPhotoEntity>(colorPhotoInteractor.loadColorPhoto(filePath)
            .filter { entity -> entity != null },
            Action1 { entity -> viewState.showPhoto(entity) },
            Action1 { error -> logger.warn("Error while loading photo: ", error) })
    }

    internal fun handleSelectedColor(color: Int) {
        viewState.showRgb(color)
        viewState.showRyb(color)
        viewState.showCmyk(color)
        viewState.showHsv(color)
        viewState.showXyz(color)
        viewState.showLab(color)

        val hex = ColorHelper.dec2Hex(color)
        execute(colorInfoInteractor.findNcsColorByHex(hex),
            Action1 { ncsName -> viewState.showNcs(color, ncsName) })

        handleColorDetails(color)
    }

    private fun handleColorDetails(color: Int) {
        val hexColor = ColorHelper.dec2Hex(color)
        execute(colorInfoInteractor.findColorNameByHex(hexColor),
            Action1 { viewState.showColorName(it) },
            Action1 { error -> logger.error("Error while finding color name: ", error) })
    }
}
