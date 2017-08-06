package ru.magflayer.spectrum.presentation.common;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.concurrent.atomic.AtomicInteger;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ru.magflayer.spectrum.R;
import ru.magflayer.spectrum.domain.model.PageAppearance;
import ru.magflayer.spectrum.domain.model.ToolbarAppearance;
import ru.magflayer.spectrum.presentation.pages.main.MainActivity;
import ru.magflayer.spectrum.presentation.pages.main.router.MainRouter;

@SuppressWarnings("unchecked")
public abstract class BaseFragment extends Fragment implements PageView {

    protected final Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());

    @BindView(R.id.progress_bar)
    @Nullable
    protected View progressBar;

    private static final AtomicInteger lastFragmentId = new AtomicInteger(0);
    private final int fragmentId;
    private Unbinder unbinder;

    public BaseFragment() {
        fragmentId = lastFragmentId.incrementAndGet();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Class cls = getClass();
        if (!cls.isAnnotationPresent(Layout.class)) return null;
        Annotation annotation = cls.getAnnotation(Layout.class);
        Layout layout = (Layout) annotation;
        View view = inflater.inflate(layout.value(), null);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        logger.debug("onViewCreated");
        final Bundle arguments = getArguments();
        if (arguments != null) {
            handleArguments(arguments);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        inject();
        //noinspection unchecked
        getPresenter().setView(this);
        getPresenter().setRouter(getRouter());
    }

    @Override
    public void onResume() {
        super.onResume();
        getPresenter().openRealm();
        getPresenter().registerBus();

        getPresenter().setupPageAppearance(getPageAppearance());
        getPresenter().setupToolbarAppearance(getToolbarAppearance());
    }

    @Override
    public void onPause() {
        super.onPause();
        getPresenter().closeRealm();
        getPresenter().unregisterBus();
    }

    @Override
    public void onDestroyView() {
        logger.debug("onDestroyView");
        unbinder.unbind();
        getPresenter().unsubscribe();
        super.onDestroyView();
    }

    public String getFragmentName() {
        return Long.toString(fragmentId);
    }

    @NonNull
    protected abstract BasePresenter getPresenter();

    protected abstract void inject();

    public FloatingActionButton getFloatingActionButton() {
        if (getActivity() instanceof MainActivity) {
            return ((MainActivity) getActivity()).getFloatingActionButton();
        }

        return null;
    }

    @Override
    public ToolbarAppearance getToolbarAppearance() {
        return ToolbarAppearance.builder()
                .visible(ToolbarAppearance.Visibility.NO_INFLUENCE)
                .build();
    }

    @Override
    public PageAppearance getPageAppearance() {
        return PageAppearance.builder()
                .showFloatingButton(false)
                .build();
    }

    protected MainRouter getRouter() {
        if (getActivity() instanceof MainActivity) {
            return ((MainActivity) getActivity()).getRouter();
        }

        return null;
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

    protected void handleArguments(@NonNull final Bundle arguments) {
        //do nothing
    }
}
