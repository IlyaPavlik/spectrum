package ru.magflayer.colorpointer.presentation.common;

import ru.magflayer.colorpointer.domain.model.PageAppearance;

public interface BaseView {

    void changePageAppearance(PageAppearance pageAppearance);

    PageAppearance getPageAppearance();

}
