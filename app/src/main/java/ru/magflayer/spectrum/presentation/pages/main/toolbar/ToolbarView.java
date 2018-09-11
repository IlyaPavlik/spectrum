package ru.magflayer.spectrum.presentation.pages.main.toolbar;

import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import ru.magflayer.spectrum.presentation.common.mvp.strategy.AddToEndSingleByTagStateStrategy;
import ru.magflayer.spectrum.presentation.common.mvp.view.BaseView;

interface ToolbarView extends BaseView {

    String TOOLBAR_VISIBLE_TAG = "toolbar_visible";

    @StateStrategyType(value = AddToEndSingleByTagStateStrategy.class, tag = TOOLBAR_VISIBLE_TAG)
    void showToolbar();

    @StateStrategyType(value = AddToEndSingleByTagStateStrategy.class, tag = TOOLBAR_VISIBLE_TAG)
    void hideToolbar();

    @StateStrategyType(AddToEndSingleStrategy.class)
    void showTitle(String title);

}
