package ru.magflayer.colorpointer.domain.event;

import ru.magflayer.colorpointer.domain.model.PageAppearance;

public class PageAppearanceEvent {

    private PageAppearance pageAppearance;

    public PageAppearanceEvent(PageAppearance pageAppearance) {
        this.pageAppearance = pageAppearance;
    }

    public PageAppearance getPageAppearance() {
        return pageAppearance;
    }
}
