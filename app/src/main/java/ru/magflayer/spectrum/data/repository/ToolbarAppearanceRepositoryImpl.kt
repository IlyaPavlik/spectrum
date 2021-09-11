package ru.magflayer.spectrum.data.repository

import ru.magflayer.spectrum.domain.repository.ToolbarAppearanceRepository
import ru.magflayer.spectrum.presentation.common.model.ToolbarAppearance
import rx.Observable
import rx.subjects.BehaviorSubject

class ToolbarAppearanceRepositoryImpl : ToolbarAppearanceRepository {

    private val toolbarAppearanceBehavior = BehaviorSubject.create<ToolbarAppearance>()

    override fun setToolbarAppearance(toolbarAppearance: ToolbarAppearance) {
        toolbarAppearanceBehavior.onNext(toolbarAppearance)
    }

    override fun getToolbarAppearance(): ToolbarAppearance? {
        return toolbarAppearanceBehavior.value
    }

    override fun observeToolbarAppearance(): Observable<ToolbarAppearance> {
        return toolbarAppearanceBehavior
    }
}