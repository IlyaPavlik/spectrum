package ru.magflayer.spectrum.data.repository

import kotlinx.coroutines.flow.Flow
import ru.magflayer.spectrum.domain.repository.ToolbarAppearanceRepository
import ru.magflayer.spectrum.presentation.common.model.ToolbarAppearance

class ToolbarAppearanceRepositoryImpl : BaseSingleModelRepository<ToolbarAppearance>(),
    ToolbarAppearanceRepository {

    override fun setToolbarAppearance(toolbarAppearance: ToolbarAppearance) {
        emitModel(toolbarAppearance)
    }

    override fun observeToolbarAppearance(): Flow<ToolbarAppearance> {
        return modelNotNullStateFlow
    }
}