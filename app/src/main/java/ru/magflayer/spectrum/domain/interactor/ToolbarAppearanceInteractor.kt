package ru.magflayer.spectrum.domain.interactor

import ru.magflayer.spectrum.domain.repository.ToolbarAppearanceRepository
import ru.magflayer.spectrum.presentation.common.model.ToolbarAppearance
import rx.Observable
import javax.inject.Inject

class ToolbarAppearanceInteractor @Inject constructor(
    private val toolbarAppearanceRepository: ToolbarAppearanceRepository
) {

    fun setToolbarAppearance(toolbarAppearance: ToolbarAppearance) {
        toolbarAppearanceRepository.setToolbarAppearance(toolbarAppearance)
    }

    fun getToolbarAppearance(): ToolbarAppearance? {
        return toolbarAppearanceRepository.getToolbarAppearance()
    }

    fun observeToolbarAppearance(): Observable<ToolbarAppearance> {
        return toolbarAppearanceRepository.observeToolbarAppearance()
    }
}