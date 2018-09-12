package ru.magflayer.spectrum.presentation.pages.main.toolbar;

import com.arellomobile.mvp.InjectViewState;
import com.squareup.otto.Subscribe;

import javax.inject.Inject;

import ru.magflayer.spectrum.domain.injection.InjectorManager;
import ru.magflayer.spectrum.presentation.common.android.navigation.router.MainRouter;
import ru.magflayer.spectrum.presentation.common.model.ToolbarAppearance;
import ru.magflayer.spectrum.presentation.common.mvp.BasePresenter;

@InjectViewState
public class ToolbarPresenter extends BasePresenter<ToolbarView> {

    @Inject
    MainRouter mainRouter;

    @Override
    protected void inject() {
        InjectorManager.getAppComponent().inject(this);
    }

    void handleBack() {
        mainRouter.exit();
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
