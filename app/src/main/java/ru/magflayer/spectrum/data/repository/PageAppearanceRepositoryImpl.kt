package ru.magflayer.spectrum.data.repository

import ru.magflayer.spectrum.domain.entity.event.FabClickEvent
import ru.magflayer.spectrum.domain.repository.PageAppearanceRepository
import ru.magflayer.spectrum.presentation.common.model.PageAppearance
import rx.Observable
import rx.subjects.BehaviorSubject
import rx.subjects.PublishSubject

class PageAppearanceRepositoryImpl : PageAppearanceRepository {

    private val pageAppearanceBehavior = BehaviorSubject.create<PageAppearance>()
    private val fabEventPublish = PublishSubject.create<FabClickEvent>()

    override fun setPageAppearance(pageAppearance: PageAppearance) {
        pageAppearanceBehavior.onNext(pageAppearance)
    }

    override fun getPageAppearance(): PageAppearance? {
        return pageAppearanceBehavior.value
    }

    override fun observePageAppearance(): Observable<PageAppearance> {
        return pageAppearanceBehavior
    }

    override fun publishFabEvent() {
        fabEventPublish.onNext(FabClickEvent())
    }

    override fun observeFabEvent(): Observable<FabClickEvent> {
        return fabEventPublish
    }
}