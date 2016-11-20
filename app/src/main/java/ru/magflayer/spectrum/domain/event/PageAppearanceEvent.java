package ru.magflayer.spectrum.domain.event;

import ru.magflayer.spectrum.domain.model.PageAppearance;

public class PageAppearanceEvent {

    private PageAppearance pageAppearance;

    public PageAppearanceEvent(PageAppearance pageAppearance) {
        this.pageAppearance = pageAppearance;
    }

    public PageAppearance getPageAppearance() {
        return pageAppearance;
    }
}
