package ru.magflayer.spectrum.domain.repository

import kotlinx.coroutines.flow.Flow
import ru.magflayer.spectrum.presentation.common.model.ToolbarAppearance

interface ToolbarAppearanceRepository {

    fun setToolbarAppearance(toolbarAppearance: ToolbarAppearance)

    fun observeToolbarAppearance(): Flow<ToolbarAppearance>
}
