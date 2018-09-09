package ru.magflayer.spectrum.presentation.common;

import com.squareup.otto.Bus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import ru.magflayer.spectrum.data.database.AppRealm;
import ru.magflayer.spectrum.domain.model.PageAppearance;
import ru.magflayer.spectrum.domain.model.ToolbarAppearance;
import ru.magflayer.spectrum.utils.RxUtils;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action0;
import rx.functions.Action1;

@SuppressWarnings("WeakerAccess")
public abstract class BasePresenter<View, Router> {

    protected Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());

    private View view;
    private Router router;
    private Map<String, Subscription> subscriptionMap = new HashMap<>();

    @Inject
    protected Bus bus;

    @Inject
    protected AppRealm appRealm;

    public BasePresenter() {
        inject();
    }

    protected abstract void inject();

    public View getView() {
        return view;
    }

    public void setView(View view) {
        this.view = view;
    }

    public Router getRouter() {
        return router;
    }

    public void setRouter(Router router) {
        this.router = router;
    }

    public void openRealm() {
        appRealm.open();
    }

    public void closeRealm() {
        appRealm.close();
    }

    public void registerBus() {
        bus.register(this);
    }

    public void unregisterBus() {
        bus.unregister(this);
    }

    public void setupToolbarAppearance(ToolbarAppearance toolbarAppearance) {
        bus.post(toolbarAppearance);
    }

    public void setupPageAppearance(PageAppearance pageAppearance) {
        bus.post(pageAppearance);
    }

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

    public void unsubscribe() {
        for (Map.Entry<String, Subscription> entry : subscriptionMap.entrySet()) {
            if (!entry.getValue().isUnsubscribed()) {
                entry.getValue().unsubscribe();
            }
        }

        subscriptionMap.clear();
    }
}