package ru.magflayer.spectrum.domain.interactor

import kotlinx.coroutines.flow.Flow
import ru.magflayer.spectrum.domain.entity.event.FabClickEvent
import ru.magflayer.spectrum.domain.repository.PageAppearanceRepository
import ru.magflayer.spectrum.presentation.common.model.PageAppearance
import javax.inject.Inject

class PageAppearanceInteractor @Inject constructor(
    private val pageAppearanceRepository: PageAppearanceRepository
) {

    fun setPageAppearance(pageAppearance: PageAppearance) {
        pageAppearanceRepository.setPageAppearance(pageAppearance)
    }

    fun observePageAppearance(): Flow<PageAppearance> {
        return pageAppearanceRepository.observePageAppearance()
    }

    fun publishFabEvent() {
        pageAppearanceRepository.publishFabEvent()
    }

    fun observeFabEvent(): Flow<FabClickEvent> {
        return pageAppearanceRepository.observeFabEvent()
    }
}