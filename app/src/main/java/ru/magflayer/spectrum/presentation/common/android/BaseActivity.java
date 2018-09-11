package ru.magflayer.spectrum.presentation.common.android;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.arellomobile.mvp.MvpAppCompatActivity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ru.magflayer.spectrum.R;
import ru.magflayer.spectrum.presentation.common.mvp.view.PageView;
import ru.magflayer.spectrum.presentation.common.utils.AppUtils;

@SuppressWarnings("unchecked")
public abstract class BaseActivity extends MvpAppCompatActivity implements PageView {

    protected final Logger logger = LoggerFactory.getLogger(getClass());

    @BindView(R.id.progress_bar)
    @Nullable
    protected View progressBar;

    private Unbinder unbinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Integer layoutId = AppUtils.getLayoutId(this);
        if (layoutId != null) {
            setContentView(layoutId);
        }
        unbinder = ButterKnife.bind(this);

        inject();
    }

    protected abstract void inject();

    @Override
    protected void onDestroy() {
        unbinder.unbind();
        super.onDestroy();
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
