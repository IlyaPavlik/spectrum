package ru.magflayer.spectrum.presentation.pages.main.toolbar;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import javax.inject.Inject;

import ru.magflayer.spectrum.domain.model.ToolbarAppearance;
import ru.magflayer.spectrum.injection.InjectorManager;
import ru.magflayer.spectrum.presentation.pages.main.router.MainRouter;
import ru.magflayer.spectrum.presentation.pages.main.router.MainRouterImpl;

public class ToolbarViewHolder implements ToolbarView {

    @Inject
    protected ToolbarPresenter presenter;

    private Toolbar toolbar;
    private MainRouter mainRouter;

    public ToolbarViewHolder(AppCompatActivity activity, Toolbar toolbar) {
        this.toolbar = toolbar;
        InjectorManager.getAppComponent().inject(this);

        mainRouter = new MainRouterImpl(activity);
        presenter.setRouter(mainRouter);
        presenter.setView(this);

        activity.setSupportActionBar(toolbar);
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(view -> presenter.handleBack());
        }
    }

    public void onRegisterBus(){
        presenter.registerBus();
    }

    public void onUnregisterBus(){
        presenter.unregisterBus();
    }

    @Override
    public void setupToolbarAppearance(ToolbarAppearance toolbarAppearance) {

        ToolbarAppearance.Visibility visibility = toolbarAppearance.getVisibility();
        switch (visibility) {
            case VISIBLE:
                toolbar.setVisibility(View.VISIBLE);
                break;
            case INVISIBLE:
                toolbar.setVisibility(View.GONE);
                break;
            default:
                //no influence
        }

        String title = toolbarAppearance.getTitle();
        if (title != null) {
            toolbar.setTitle(title);
        }
    }
}
