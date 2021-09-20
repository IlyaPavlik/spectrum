package ru.magflayer.spectrum.presentation.pages.main.history.details

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import moxy.InjectViewState
import ru.magflayer.spectrum.R
import ru.magflayer.spectrum.data.android.ResourceManager
import ru.magflayer.spectrum.domain.entity.AnalyticsEvent
import ru.magflayer.spectrum.domain.interactor.ColorInfoInteractor
import ru.magflayer.spectrum.domain.interactor.ColorPhotoInteractor
import ru.magflayer.spectrum.domain.interactor.PageAppearanceInteractor
import ru.magflayer.spectrum.domain.interactor.ToolbarAppearanceInteractor
import ru.magflayer.spectrum.domain.manager.AnalyticsManager
import ru.magflayer.spectrum.presentation.common.helper.ColorHelper
import ru.magflayer.spectrum.presentation.common.model.PageAppearance
import ru.magflayer.spectrum.presentation.common.model.ToolbarAppearance
import ru.magflayer.spectrum.presentation.common.mvp.BasePresenter
import javax.inject.Inject

@InjectViewState
class HistoryDetailsPresenter @Inject constructor(
    private val analyticsManager: AnalyticsManager,
    private val colorInfoInteractor: ColorInfoInteractor,
    private val resourceManager: ResourceManager,
    private val colorPhotoInteractor: ColorPhotoInteractor,
    private val toolbarAppearanceInteractor: ToolbarAppearanceInteractor,
    private val pageAppearanceInteractor: PageAppearanceInteractor
) : BasePresenter<HistoryDetailsView>() {

    lateinit var filePath: String
    override val toolbarAppearance: ToolbarAppearance
        get() = ToolbarAppearance(
            ToolbarAppearance.Visibility.VISIBLE,
            resourceManager.getString(R.string.history_details_title)
        )

    override val pageAppearance: PageAppearance
        get() = PageAppearance.builder()
            .showFloatingButton(PageAppearance.FloatingButtonState.INVISIBLE)
            .build()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        loadPicture(filePath)
    }

    override fun attachView(view: HistoryDetailsView) {
        super.attachView(view)
        analyticsManager.logEvent(AnalyticsEvent.OPEN_HISTORY_DETAILS)
        toolbarAppearanceInteractor.setToolbarAppearance(toolbarAppearance)
        pageAppearanceInteractor.setPageAppearance(pageAppearance)
    }

    private fun loadPicture(filePath: String) {
        val errorHandler = CoroutineExceptionHandler { _, exception ->
            logger.warn("Error while loading photo: ", exception)
        }
        presenterScope.launch(errorHandler) {
            val colorPhoto = colorPhotoInteractor.loadColorPhoto(filePath)
            viewState.showPhoto(colorPhoto)
        }
    }

    internal fun handleSelectedColor(color: Int) {
        viewState.showRgb(color)
        viewState.showRyb(color)
        viewState.showCmyk(color)
        viewState.showHsv(color)
        viewState.showXyz(color)
        viewState.showLab(color)

        val hex = ColorHelper.dec2Hex(color)
        val errorHandler = CoroutineExceptionHandler { _, exception ->
            logger.warn("Error while finding ncs color: ", exception)
        }
        presenterScope.launch(errorHandler) {
            val ncsName = colorInfoInteractor.findNcsColorByHex(hex)
            viewState.showNcs(color, ncsName)
        }

        handleColorDetails(color)
    }

    private fun handleColorDetails(color: Int) {
        val hexColor = ColorHelper.dec2Hex(color)
        val errorHandler = CoroutineExceptionHandler { _, exception ->
            logger.warn("Error while finding hex color: ", exception)
        }
        presenterScope.launch(errorHandler) {
            val colorNameHex = colorInfoInteractor.findColorNameByHex(hexColor)
            viewState.showColorName(colorNameHex)
        }
    }
}
