package ru.magflayer.spectrum.presentation.pages.main.history

import android.graphics.Bitmap
import android.net.Uri
import androidx.palette.graphics.Palette
import moxy.InjectViewState
import ru.magflayer.spectrum.R
import ru.magflayer.spectrum.data.android.ResourceManager
import ru.magflayer.spectrum.domain.entity.AnalyticsEvent
import ru.magflayer.spectrum.domain.entity.ColorPhotoEntity
import ru.magflayer.spectrum.domain.injection.InjectorManager
import ru.magflayer.spectrum.domain.interactor.ColorPhotoInteractor
import ru.magflayer.spectrum.domain.interactor.FileManagerInteractor
import ru.magflayer.spectrum.domain.interactor.PageAppearanceInteractor
import ru.magflayer.spectrum.domain.interactor.ToolbarAppearanceInteractor
import ru.magflayer.spectrum.domain.manager.AnalyticsManager
import ru.magflayer.spectrum.presentation.common.android.navigation.router.MainRouter
import ru.magflayer.spectrum.presentation.common.model.PageAppearance
import ru.magflayer.spectrum.presentation.common.model.ToolbarAppearance
import ru.magflayer.spectrum.presentation.common.mvp.BasePresenter
import rx.Observable
import javax.inject.Inject

@InjectViewState
class HistoryPresenter : BasePresenter<HistoryView>() {

    @Inject
    lateinit var analyticsManager: AnalyticsManager

    @Inject
    lateinit var resourceManager: ResourceManager

    @Inject
    lateinit var mainRouter: MainRouter

    @Inject
    lateinit var colorPhotoInteractor: ColorPhotoInteractor

    @Inject
    lateinit var fileManagerInteractor: FileManagerInteractor

    @Inject
    lateinit var toolbarAppearanceInteractor: ToolbarAppearanceInteractor

    @Inject
    lateinit var pageAppearanceInteractor: PageAppearanceInteractor

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

    override fun inject() {
        InjectorManager.appComponent?.inject(this)
    }

    override fun onFirstViewAttach() {
        super.onFirstViewAttach()
        loadHistory()
        analyticsManager.logEvent(AnalyticsEvent.OPEN_HISTORY)
    }

    override fun attachView(view: HistoryView) {
        super.attachView(view)
        toolbarAppearanceInteractor.setToolbarAppearance(toolbarAppearance)
        pageAppearanceInteractor.setPageAppearance(pageAppearance)
        execute(pageAppearanceInteractor.observeFabEvent()) { handleFabClicked() }
    }

    internal fun removeColor(entity: ColorPhotoEntity) {
        execute(colorPhotoInteractor.removeColorPhoto(entity),
            { success ->
                logger.debug("Photo removed {}", if (success) "successfully" else "unsuccessfully")
                entities.remove(entity)
                viewState.showHistory(entities)
            },
            { error -> logger.warn("Error while removing photo: ", error) })
    }

    internal fun handleColorSelected(entity: ColorPhotoEntity) {
        mainRouter.openHistoryDetailsScreen(entity.filePath)
    }

    internal fun handleSelectedImage(fileUri: Uri, bitmap: Bitmap) {
        logger.debug("Image selected: {}", fileUri)
        viewState.showProgressBar()
        execute<Boolean>(Observable.just(bitmap)
            .flatMap { b -> Observable.fromCallable { Palette.from(b).generate() } }
            .flatMap { palette ->
                fileManagerInteractor.copyToLocalStorage(fileUri)
                    .map { it to palette }
            }
            .map { filePalette ->
                val colors = ArrayList(filePalette.second.swatches)
                //filtered by brightness
                colors.sortWith { lhs, rhs ->
                    lhs.hsl[2].compareTo(rhs.hsl[2])
                }
                filePalette.first to colors
            }
            .flatMap { fileSwatches ->
                val rgbColors = convertSwatches(fileSwatches.second)
                val entity = ColorPhotoEntity(
                    ColorPhotoEntity.Type.EXTERNAL,
                    fileSwatches.first.toString(),
                    rgbColors
                )
                colorPhotoInteractor.saveColorPhoto(entity)
            },
            { success ->
                logger.debug("Image saved: {}", success)
                viewState.hideProgressBar()
                if (success) loadHistory()
            },
            { error ->
                logger.warn("Error while saving image: ", error)
                viewState.hideProgressBar()
            })
    }

    private fun loadHistory() {
        execute(colorPhotoInteractor.loadColorPhotos(),
            { entities ->
                this.entities.clear()
                this.entities.addAll(entities)
                viewState.showHistory(entities)
            },
            { error -> logger.warn("Error while loading color photos: ", error) })
    }

    private fun convertSwatches(swatches: List<Palette.Swatch>): List<Int> {
        return swatches.map { it.rgb }
    }

    private fun handleFabClicked() {
        viewState.openPickPhoto()
        analyticsManager.logEvent(AnalyticsEvent.CHOOSE_IMAGE)
    }
}
