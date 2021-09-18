package ru.magflayer.spectrum.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.magflayer.spectrum.domain.entity.event.FabClickEvent
import ru.magflayer.spectrum.presentation.common.model.PageAppearance

interface PageAppearanceRepository {

    fun setPageAppearance(pageAppearance: PageAppearance)

    fun observePageAppearance(): Flow<PageAppearance>

    fun publishFabEvent()

    fun observeFabEvent(): Flow<FabClickEvent>

}