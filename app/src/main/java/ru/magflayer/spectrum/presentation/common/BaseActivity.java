package ru.magflayer.spectrum.presentation.common;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ru.magflayer.spectrum.R;
import ru.magflayer.spectrum.domain.model.PageAppearance;
import ru.magflayer.spectrum.utils.AppUtils;

@SuppressWarnings("unchecked")
public abstract class BaseActivity<Presenter extends BasePresenter> extends AppCompatActivity implements BaseView {

    @BindView(R.id.progress_bar)
    @Nullable
    protected View progressBar;

    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        inject();
        getPresenter().registerBus();

        AppUtils.applyLayout(this);

        unbinder = ButterKnife.bind(this);
        getPresenter().setView(this);
    }

    protected abstract void inject();

    @NonNull
    protected abstract Presenter getPresenter();

    @Override
    protected void onDestroy() {
        getPresenter().unregisterBus();
        getPresenter().unsubscribe();
        unbinder.unbind();
        super.onDestroy();
    }

    @Override
    public void changePageAppearance(PageAppearance pageAppearance) {
        getPresenter().setupPageAppearance(pageAppearance);
    }

    @Override
    public PageAppearance getPageAppearance() {
        return PageAppearance.builder()
                .showFloatingButton(false)
                .showToolbar(false)
                .build();
    }

    @Override
    public void showProgressBar() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void hideProgressBar() {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }
}
