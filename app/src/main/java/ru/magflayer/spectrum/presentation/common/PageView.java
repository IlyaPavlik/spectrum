package ru.magflayer.spectrum.presentation.common;


import ru.magflayer.spectrum.domain.model.PageAppearance;
import ru.magflayer.spectrum.domain.model.ToolbarAppearance;

public interface PageView extends BaseView {

    PageAppearance getPageAppearance();

    ToolbarAppearance getToolbarAppearance();

    void showProgressBar();

    void hideProgressBar();

}
