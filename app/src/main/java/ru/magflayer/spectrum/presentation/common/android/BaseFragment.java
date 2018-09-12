package ru.magflayer.spectrum.presentation.common.android;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arellomobile.mvp.MvpAppCompatFragment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ru.magflayer.spectrum.R;
import ru.magflayer.spectrum.presentation.common.mvp.view.PageView;
import ru.magflayer.spectrum.presentation.common.utils.AppUtils;

@SuppressWarnings("unchecked")
public abstract class BaseFragment extends MvpAppCompatFragment implements PageView {

    protected final Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());

    @BindView(R.id.progress_bar)
    @Nullable
    protected View progressBar;

    private Unbinder unbinder;

    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {
        Integer layoutId = AppUtils.getLayoutId(this);
        if (layoutId != null) {
            View view = inflater.inflate(layoutId, null);
            unbinder = ButterKnife.bind(this, view);
            return view;
        }
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        logger.debug("onViewCreated");
        final Bundle arguments = getArguments();
        if (arguments != null) {
            handleArguments(arguments);
        }
    }

    @Override
    public void onActivityCreated(final Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        inject();
    }

    @Override
    public void onDestroyView() {
        logger.debug("onDestroyView");
        unbinder.unbind();
        super.onDestroyView();
    }

    protected abstract void inject();

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

    protected void handleArguments(@NonNull final Bundle arguments) {
        //do nothing
    }
}
