package ru.magflayer.spectrum.domain.repository

import ru.magflayer.spectrum.domain.entity.event.FabClickEvent
import ru.magflayer.spectrum.presentation.common.model.PageAppearance
import rx.Observable

interface PageAppearanceRepository {

    fun setPageAppearance(pageAppearance: PageAppearance)

    fun getPageAppearance(): PageAppearance?

    fun observePageAppearance(): Observable<PageAppearance>

    fun publishFabEvent()

    fun observeFabEvent(): Observable<FabClickEvent>

}