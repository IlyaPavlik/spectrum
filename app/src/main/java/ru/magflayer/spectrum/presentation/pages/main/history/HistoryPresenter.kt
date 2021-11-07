package ru.magflayer.spectrum.presentation.pages.main.history

import android.graphics.Bitmap
import android.net.Uri
import androidx.palette.graphics.Palette
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.launch
import moxy.InjectViewState
import ru.magflayer.spectrum.R
import ru.magflayer.spectrum.data.android.ResourceManager
import ru.magflayer.spectrum.domain.entity.AnalyticsEvent
import ru.magflayer.spectrum.domain.entity.ColorPhotoEntity
import ru.magflayer.spectrum.domain.interactor.ColorPhotoInteractor
import ru.magflayer.spectrum.domain.interactor.FileManagerInteractor
import ru.magflayer.spectrum.domain.interactor.PageAppearanceInteractor
import ru.magflayer.spectrum.domain.interactor.ToolbarAppearanceInteractor
import ru.magflayer.spectrum.domain.manager.AnalyticsManager
import ru.magflayer.spectrum.presentation.common.model.PageAppearance
import ru.magflayer.spectrum.presentation.common.model.ToolbarAppearance
import ru.magflayer.spectrum.presentation.common.mvp.BasePresenter
import javax.inject.Inject

@InjectViewState
class HistoryPresenter @Inject constructor(
    private val analyticsManager: AnalyticsManager,
    private val resourceManager: ResourceManager,
    private val colorPhotoInteractor: ColorPhotoInteractor,
    private val fileManagerInteractor: FileManagerInteractor,
    private val toolbarAppearanceInteractor: ToolbarAppearanceInteractor,
    private val pageAppearanceInteractor: PageAppearanceInteractor
) : BasePresenter<HistoryView>() {

    override val toolbarAppearance: ToolbarAppearance
        get() = ToolbarAppearance(
            ToolbarAppearance.Visibility.VISIBLE,
            resourceManager.getString(R.string.history_toolbar_title)
        )

    override val pageAppearance: PageAppearance
        get() = PageAppearance.builder()
            .showFloatingButton(PageAppearance.FloatingButtonState.VISIBLE)
            .build()

    private val entities = ArrayList<ColorPhotoEntity>()

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        loadHistory()
        analyticsManager.logEvent(AnalyticsEvent.OPEN_HISTORY)
        execute(
            pageAppearanceInteractor.observeFabEvent(),
            onSuccess = { handleFabClicked() }
        )
    }

    override fun attachView(view: HistoryView) {
        super.attachView(view)
        toolbarAppearanceInteractor.setToolbarAppearance(toolbarAppearance)
        pageAppearanceInteractor.setPageAppearance(pageAppearance)
    }

    fun removeColor(entity: ColorPhotoEntity) {
        val errorHandler = CoroutineExceptionHandler { _, exception ->
            logger.warn("Error while removing photo: ", exception)
        }

        presenterScope.launch(errorHandler) {
            val success = colorPhotoInteractor.removeColorPhoto(entity)
            logger.debug("Photo removed {}", if (success) "successfully" else "unsuccessfully")
            entities.remove(entity)
            viewState.showHistory(entities)
        }
    }

    fun handleSelectedImage(fileUri: Uri, bitmap: Bitmap) {
        logger.debug("Image selected: {}", fileUri)
        viewState.showProgressBar()

        val errorHandler = CoroutineExceptionHandler { _, exception ->
            logger.warn("Error while saving image: ", exception)
            viewState.hideProgressBar()
        }
        presenterScope.launch(errorHandler) {
            val palette = Palette.from(bitmap).generate()
            val cacheFileUri = fileManagerInteractor.copyToLocalStorage(fileUri)
            val swatches = ArrayList(palette.swatches)

            //filtered by brightness
            swatches.sortWith { lhs, rhs ->
                lhs.hsl[2].compareTo(rhs.hsl[2])
            }

            val rgbColors = convertSwatches(swatches)
            val entity = ColorPhotoEntity(
                ColorPhotoEntity.Type.EXTERNAL,
                cacheFileUri.toString(),
                rgbColors
            )
            val success = colorPhotoInteractor.saveColorPhoto(entity)

            logger.debug("Image saved: {}", success)
            viewState.hideProgressBar()
            if (success) loadHistory()
        }
    }

    private fun loadHistory() {
        val errorHandler = CoroutineExceptionHandler { _, exception ->
            logger.warn("Error while loading color photos: ", exception)
        }
        presenterScope.launch(errorHandler) {
            val loadedEntities = colorPhotoInteractor.loadColorPhotos()
            entities.clear()
            entities.addAll(loadedEntities)
            viewState.showHistory(loadedEntities)
        }
    }

    private fun convertSwatches(swatches: List<Palette.Swatch>): List<Int> {
        return swatches.map { it.rgb }
    }

    private fun handleFabClicked() {
        viewState.openPickPhoto()
        analyticsManager.logEvent(AnalyticsEvent.CHOOSE_IMAGE)
    }
}
