package ru.magflayer.spectrum.presentation.common;

import ru.magflayer.spectrum.domain.model.PageAppearance;

public interface BaseView {

    void changePageAppearance(PageAppearance pageAppearance);

    PageAppearance getPageAppearance();

}
