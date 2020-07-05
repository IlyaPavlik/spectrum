package ru.magflayer.spectrum.presentation.pages.main.history

import android.graphics.Bitmap
import androidx.palette.graphics.Palette
import com.squareup.otto.Subscribe
import moxy.InjectViewState
import ru.magflayer.spectrum.R
import ru.magflayer.spectrum.data.android.ResourceManager
import ru.magflayer.spectrum.domain.entity.AnalyticsEvent
import ru.magflayer.spectrum.domain.entity.ColorPhotoEntity
import ru.magflayer.spectrum.domain.entity.event.FabClickEvent
import ru.magflayer.spectrum.domain.injection.InjectorManager
import ru.magflayer.spectrum.domain.interactor.ColorPhotoInteractor
import ru.magflayer.spectrum.domain.manager.AnalyticsManager
import ru.magflayer.spectrum.presentation.common.android.navigation.router.MainRouter
import ru.magflayer.spectrum.presentation.common.model.PageAppearance
import ru.magflayer.spectrum.presentation.common.model.ToolbarAppearance
import ru.magflayer.spectrum.presentation.common.mvp.BasePresenter
import rx.Observable
import rx.functions.Action1
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

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

    @Subscribe
    fun onFabClicked(event: FabClickEvent) {
        viewState.openPickPhoto()
        analyticsManager.logEvent(AnalyticsEvent.CHOOSE_IMAGE)
    }

    internal fun removeColor(entity: ColorPhotoEntity) {
        execute(colorPhotoInteractor.removeColorPhoto(entity),
                Action1 { success ->
                    logger.debug("Photo removed {}", if (success) "successfully" else "unsuccessfully")
                    entities.remove(entity)
                    viewState.showHistory(entities)
                },
                Action1 { error -> logger.warn("Error while removing photo: ", error) })
    }

    internal fun handleColorSelected(entity: ColorPhotoEntity) {
        mainRouter.openHistoryDetailsScreen(entity.filePath)
    }

    internal fun handleSelectedImage(path: String, bitmap: Bitmap) {
        logger.debug("Image selected: {}", path)
        viewState.showProgressBar()
        execute<Boolean>(Observable.just(bitmap)
                .flatMap { b -> Observable.just(Palette.from(b).generate()) }
                .map<List<Palette.Swatch>> { palette ->
                    val colors = ArrayList(palette.swatches)
                    //filtered by brightness
                    colors.sortWith(Comparator { lhs, rhs -> java.lang.Float.compare(lhs.hsl[2], rhs.hsl[2]) })
                    colors
                }
                .flatMap { swatches ->
                    val rgbColors = convertSwatches(swatches)
                    val entity = ColorPhotoEntity(ColorPhotoEntity.Type.EXTERNAL, path, rgbColors)
                    colorPhotoInteractor.saveColorPhoto(entity)
                },
                Action1 { success ->
                    logger.debug("Image saved: {}", success)
                    viewState.hideProgressBar()
                    if (success) loadHistory()
                },
                Action1 { error ->
                    logger.warn("Error while saving image: ", error)
                    viewState.hideProgressBar()
                })
    }

    private fun loadHistory() {
        execute(colorPhotoInteractor.loadColorPhotos(),
                Action1 { entities ->
                    this.entities.clear()
                    this.entities.addAll(entities)
                    viewState.showHistory(entities)
                },
                Action1 { error -> logger.warn("Error while loading color photos: ", error) })
    }

    private fun convertSwatches(swatches: List<Palette.Swatch>): List<Int> {
        return swatches.map { it.rgb }
    }
}
