package ru.magflayer.spectrum.presentation.pages.main;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.WindowManager;

import com.arellomobile.mvp.presenter.InjectPresenter;

import butterknife.BindView;
import ru.magflayer.spectrum.R;
import ru.magflayer.spectrum.domain.injection.InjectorManager;
import ru.magflayer.spectrum.presentation.common.android.BaseActivity;
import ru.magflayer.spectrum.presentation.common.android.layout.Layout;
import ru.magflayer.spectrum.presentation.pages.main.router.MainRouter;
import ru.magflayer.spectrum.presentation.pages.main.router.MainRouterImpl;
import ru.magflayer.spectrum.presentation.pages.main.toolbar.ToolbarViewHolder;

@Layout(R.layout.activity_main)
public class MainActivity extends BaseActivity implements MainView {

    @InjectPresenter
    MainPresenter presenter;

    @BindView(R.id.toolbar)
    protected Toolbar toolbar;
    @BindView(R.id.fab)
    protected FloatingActionButton floatingActionButton;

    private MainRouter mainRouter;
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

        mainRouter = new MainRouterImpl(this);
        presenter.setRouter(mainRouter);

        toolbarViewHolder = new ToolbarViewHolder(this, toolbar);

        if (savedInstanceState == null) {
            presenter.openMainScreen();
        }
    }

    @Override
    protected void onDestroy() {
        toolbarViewHolder.onDestroy();
        super.onDestroy();
    }

    public MainRouter getRouter() {
        return mainRouter;
    }

    public FloatingActionButton getFloatingActionButton() {
        return floatingActionButton;
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
}
