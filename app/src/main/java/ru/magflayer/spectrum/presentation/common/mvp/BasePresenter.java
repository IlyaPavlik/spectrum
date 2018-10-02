package ru.magflayer.spectrum.presentation.common.mvp;

import com.arellomobile.mvp.MvpPresenter;
import com.arellomobile.mvp.MvpView;
import com.squareup.otto.Bus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import ru.magflayer.spectrum.common.utils.RxUtils;
import ru.magflayer.spectrum.presentation.common.model.PageAppearance;
import ru.magflayer.spectrum.presentation.common.model.ToolbarAppearance;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;

@SuppressWarnings("WeakerAccess")
public abstract class BasePresenter<View extends MvpView> extends MvpPresenter<View> {

    protected Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());

    private Map<String, Subscription> subscriptionMap = new HashMap<>();

    @Inject
    protected Bus bus;

    public BasePresenter() {
        inject();
        bus.register(this);
    }

    @Override
    public void attachView(View view) {
        super.attachView(view);

        setupPageAppearance(getPageAppearance());
        setupToolbarAppearance(getToolbarAppearance());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        bus.unregister(this);
        unsubscribe();
    }

    protected abstract void inject();

    protected <T> void execute(Observable<T> observable, Action1<T> action1) {
        execute(String.valueOf(observable.hashCode()), observable, action1, throwable -> logger.error("Error occurred: ", throwable), () -> {
        });
    }

    protected <T> void execute(Observable<T> observable, Action1<T> action1, Action1<Throwable> errorAction) {
        execute(String.valueOf(observable.hashCode()), observable, action1, errorAction, () -> {
        });
    }

    protected <T> void execute(Observable<T> observable, Action1<T> action1, Action1<Throwable> errorAction, Action0 completeAction) {
        execute(String.valueOf(observable.hashCode()), observable, action1, errorAction, completeAction);
    }

    protected <T> void execute(String tag, Observable<T> observable, Action1<T> action1) {
        execute(tag, observable, action1, throwable -> logger.error("Error occurred: ", throwable), () -> {
        });
    }

    protected <T> void execute(String tag, Observable<T> observable, Action1<T> action1, Action1<Throwable> errorAction) {
        execute(tag, observable, action1, errorAction, () -> {
        });
    }

    @SuppressWarnings("unchecked")
    protected <T> void execute(String tag, Observable<T> observable, Action1<T> action1, Action1<Throwable> errorAction, Action0 completeAction) {
        if (subscriptionMap.containsKey(tag)) {
            Subscription subscription = subscriptionMap.get(tag);
            if (!subscription.isUnsubscribed()) {
                subscription.unsubscribe();
            }
        }

        subscriptionMap.put(tag, observable
                .compose(RxUtils.applySchedulers())
                .subscribe(action1, errorAction, completeAction));
    }

    private void unsubscribe() {
        for (Map.Entry<String, Subscription> entry : subscriptionMap.entrySet()) {
            if (!entry.getValue().isUnsubscribed()) {
                entry.getValue().unsubscribe();
            }
        }

        subscriptionMap.clear();
    }

    protected void setupToolbarAppearance(final ToolbarAppearance toolbarAppearance) {
        bus.post(toolbarAppearance);
    }

    protected void setupPageAppearance(final PageAppearance pageAppearance) {
        bus.post(pageAppearance);
    }

    protected ToolbarAppearance getToolbarAppearance() {
        return ToolbarAppearance.builder()
                .visible(ToolbarAppearance.Visibility.NO_INFLUENCE)
                .build();
    }

    protected PageAppearance getPageAppearance() {
        return PageAppearance.builder()
                .showFloatingButton(null)
                .build();
    }
}