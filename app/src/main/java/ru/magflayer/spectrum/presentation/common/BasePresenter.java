package ru.magflayer.spectrum.presentation.common;

import com.squareup.otto.Bus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import ru.magflayer.spectrum.data.database.AppRealm;
import ru.magflayer.spectrum.domain.model.PageAppearance;
import ru.magflayer.spectrum.domain.model.ToolbarAppearance;
import ru.magflayer.spectrum.utils.RxUtils;
import rx.Observable;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

public abstract class BasePresenter<View, Router> {

    protected Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());

    private View view;
    private Router router;
    private CompositeSubscription subscription = new CompositeSubscription();

    @Inject
    protected Bus bus;

    @Inject
    protected AppRealm appRealm;

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

    public <T> void execute(Observable<T> observable, Action1<T> action1) {
        execute(observable, action1, throwable -> logger.error("Error occurred: ", throwable));
    }

    @SuppressWarnings("unchecked")
    public <T> void execute(Observable<T> observable, Action1<T> action1, Action1<Throwable> errorAction) {
        subscription.add(observable
                .compose(RxUtils.applySchedulers())
                .subscribe(action1, errorAction));
    }

    public void unsubscribe() {
        subscription.clear();
    }
}