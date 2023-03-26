package ru.magflayer.spectrum.domain.interactor

import kotlinx.coroutines.flow.Flow
import ru.magflayer.spectrum.domain.repository.ToolbarAppearanceRepository
import ru.magflayer.spectrum.presentation.common.model.ToolbarAppearance
import javax.inject.Inject

class ToolbarAppearanceInteractor @Inject constructor(
    private val toolbarAppearanceRepository: ToolbarAppearanceRepository,
) {

    fun setToolbarAppearance(toolbarAppearance: ToolbarAppearance) {
        toolbarAppearanceRepository.setToolbarAppearance(toolbarAppearance)
    }

    fun observeToolbarAppearance(): Flow<ToolbarAppearance> {
        return toolbarAppearanceRepository.observeToolbarAppearance()
    }
}
