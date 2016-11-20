package ru.magflayer.spectrum.presentation.common;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.util.concurrent.atomic.AtomicInteger;

import butterknife.ButterKnife;
import butterknife.Unbinder;
import ru.magflayer.spectrum.domain.model.PageAppearance;
import ru.magflayer.spectrum.presentation.pages.main.MainActivity;
import ru.magflayer.spectrum.presentation.pages.main.router.MainRouter;

@SuppressWarnings("unchecked")
public abstract class BaseFragment extends Fragment implements BaseView {

    protected final Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());

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
        View view = inflater.inflate(layout.id(), null);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        inject();
        //noinspection unchecked
        getPresenter().setView(this);
        getPresenter().openRealm();
        getPresenter().setRouter(getRouter());
        getPresenter().registerBus();
        changePageAppearance(getPageAppearance());
    }


    @Override
    public void onDestroyView() {
        unbinder.unbind();
        getPresenter().closeRealm();
        getPresenter().unregisterBus();
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

    protected MainRouter getRouter() {
        if (getActivity() instanceof MainActivity) {
            return ((MainActivity) getActivity()).getRouter();
        }

        return null;
    }
}
