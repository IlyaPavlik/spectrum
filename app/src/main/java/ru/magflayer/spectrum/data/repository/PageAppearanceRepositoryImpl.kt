package ru.magflayer.spectrum.data.repository

import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import ru.magflayer.spectrum.domain.entity.event.FabClickEvent
import ru.magflayer.spectrum.domain.repository.PageAppearanceRepository
import ru.magflayer.spectrum.presentation.common.model.PageAppearance
import javax.inject.Inject

class PageAppearanceRepositoryImpl @Inject constructor() :
    BaseSingleModelRepository<PageAppearance>(),
    PageAppearanceRepository {

    private val fabSharedFlow by lazy {
        MutableSharedFlow<FabClickEvent>(
            replay = 0,
            extraBufferCapacity = 1,
            onBufferOverflow = BufferOverflow.DROP_OLDEST,
        )
    }

    override fun setPageAppearance(pageAppearance: PageAppearance) {
        emitModel(pageAppearance)
    }

    override fun observePageAppearance(): Flow<PageAppearance> {
        return modelNotNullStateFlow
    }

    override fun publishFabEvent() {
        fabSharedFlow.tryEmit(FabClickEvent())
    }

    override fun observeFabEvent(): Flow<FabClickEvent> {
        return fabSharedFlow
    }
}
