package ru.magflayer.spectrum.presentation.pages.main;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;

import com.arellomobile.mvp.presenter.InjectPresenter;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.OnClick;
import ru.magflayer.spectrum.R;
import ru.magflayer.spectrum.domain.injection.InjectorManager;
import ru.magflayer.spectrum.presentation.common.android.BaseActivity;
import ru.magflayer.spectrum.presentation.common.android.layout.Layout;
import ru.magflayer.spectrum.presentation.common.android.navigation.holder.MainRouterHolder;
import ru.magflayer.spectrum.presentation.common.android.navigation.navigator.MainNavigator;
import ru.magflayer.spectrum.presentation.pages.main.toolbar.ToolbarViewHolder;

@Layout(R.layout.activity_main)
public class MainActivity extends BaseActivity implements MainView {

    @InjectPresenter
    MainPresenter presenter;

    @Inject
    MainRouterHolder mainRouterHolder;

    @BindView(R.id.toolbar)
    protected Toolbar toolbar;
    @BindView(R.id.fab)
    protected FloatingActionButton floatingActionButton;

    private final MainNavigator mainNavigator = new MainNavigator(this, R.id.container);

    private ToolbarViewHolder toolbarViewHolder;

    @Override
    protected void inject() {
        InjectorManager.getAppComponent().inject(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        toolbarViewHolder = new ToolbarViewHolder(this, toolbar);

        if (savedInstanceState == null) {
            presenter.openMainScreen();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mainRouterHolder.setNavigator(mainNavigator);
    }

    @Override
    protected void onPause() {
        mainRouterHolder.removeNavigator();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        toolbarViewHolder.onDestroy();
        super.onDestroy();
    }

    @Override
    public void showToolbar(final boolean showToolbar) {
        if (toolbar != null) {
            toolbar.setVisibility(showToolbar ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    public void showFloatingButton(final boolean showFloatingButton) {
        if (floatingActionButton != null) {
            floatingActionButton.setVisibility(showFloatingButton ? View.VISIBLE : View.GONE);
        }
    }

    @OnClick(R.id.fab)
    public void onFabClick() {
        presenter.handleFabClick();
    }
}
