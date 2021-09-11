package ru.magflayer.spectrum.domain.interactor

import ru.magflayer.spectrum.domain.entity.event.FabClickEvent
import ru.magflayer.spectrum.domain.repository.PageAppearanceRepository
import ru.magflayer.spectrum.presentation.common.model.PageAppearance
import rx.Observable
import javax.inject.Inject

class PageAppearanceInteractor @Inject constructor(
    private val pageAppearanceRepository: PageAppearanceRepository
) {

    fun setPageAppearance(pageAppearance: PageAppearance) {
        pageAppearanceRepository.setPageAppearance(pageAppearance)
    }

    fun getPageAppearance(): PageAppearance? {
        return pageAppearanceRepository.getPageAppearance()
    }

    fun observePageAppearance(): Observable<PageAppearance> {
        return pageAppearanceRepository.observePageAppearance()
    }

    fun publishFabEvent() {
        pageAppearanceRepository.publishFabEvent()
    }

    fun observeFabEvent(): Observable<FabClickEvent> {
        return pageAppearanceRepository.observeFabEvent()
    }
}