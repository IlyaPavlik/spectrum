package ru.magflayer.spectrum.presentation.pages.main.toolbar;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.arellomobile.mvp.MvpDelegate;
import com.arellomobile.mvp.presenter.InjectPresenter;

import ru.magflayer.spectrum.presentation.pages.main.router.MainRouter;
import ru.magflayer.spectrum.presentation.pages.main.router.MainRouterImpl;

public class ToolbarViewHolder implements ToolbarView {

    @InjectPresenter
    ToolbarPresenter presenter;

    private Toolbar toolbar;
    private MvpDelegate<ToolbarViewHolder> mvpDelegate;

    public ToolbarViewHolder(final AppCompatActivity activity, final Toolbar toolbar) {
        this.toolbar = toolbar;
        getMvpDelegate().onCreate();
        getMvpDelegate().onAttach();

        MainRouter mainRouter = new MainRouterImpl(activity);
        presenter.setRouter(mainRouter);

        activity.setSupportActionBar(toolbar);
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(view -> presenter.handleBack());
        }
    }

    @Override
    public void showToolbar() {
        toolbar.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideToolbar() {
        toolbar.setVisibility(View.GONE);
    }

    @Override
    public void showTitle(final String title) {
        toolbar.setTitle(title);
    }

    public void onDestroy() {
        getMvpDelegate().onDetach();
        getMvpDelegate().onDestroy();
    }

    private MvpDelegate getMvpDelegate() {
        if (mvpDelegate == null) {
            mvpDelegate = new MvpDelegate<>(this);
        }
        return mvpDelegate;
    }
}
