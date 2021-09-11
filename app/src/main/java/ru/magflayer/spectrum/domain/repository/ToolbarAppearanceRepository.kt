package ru.magflayer.spectrum.domain.repository

import ru.magflayer.spectrum.presentation.common.model.ToolbarAppearance
import rx.Observable

interface ToolbarAppearanceRepository {

    fun setToolbarAppearance(toolbarAppearance: ToolbarAppearance)

    fun getToolbarAppearance(): ToolbarAppearance?

    fun observeToolbarAppearance(): Observable<ToolbarAppearance>

}