package ru.magflayer.spectrum.presentation.pages.main.toolbar;

import com.arellomobile.mvp.InjectViewState;
import com.squareup.otto.Subscribe;

import ru.magflayer.spectrum.domain.injection.InjectorManager;
import ru.magflayer.spectrum.presentation.common.model.ToolbarAppearance;
import ru.magflayer.spectrum.presentation.common.mvp.BasePresenter;
import ru.magflayer.spectrum.presentation.pages.main.router.MainRouter;

@InjectViewState
public class ToolbarPresenter extends BasePresenter<ToolbarView, MainRouter> {

    @Override
    protected void inject() {
        InjectorManager.getAppComponent().inject(this);
    }

    void handleBack() {
        getRouter().handleBack();
    }

    @Subscribe
    public void onToolbarAppearance(final ToolbarAppearance toolbarAppearance) {
        ToolbarAppearance.Visibility visibility = toolbarAppearance.getVisible();
        switch (visibility) {
            case VISIBLE:
                getViewState().showToolbar();
                break;
            case INVISIBLE:
                getViewState().hideToolbar();
                break;
            default:
                //no influence
        }

        String title = toolbarAppearance.getTitle();
        if (title != null) {
            getViewState().showTitle(title);
        }
    }
}
