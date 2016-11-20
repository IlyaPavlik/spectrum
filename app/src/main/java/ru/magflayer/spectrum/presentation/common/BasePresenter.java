package ru.magflayer.spectrum.presentation.common;

import com.squareup.otto.Bus;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import ru.magflayer.spectrum.data.database.AppRealm;
import ru.magflayer.spectrum.domain.event.PageAppearanceEvent;
import ru.magflayer.spectrum.domain.model.PageAppearance;

public abstract class BasePresenter<View, Router> {

    protected Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());

    private View view;
    private Router router;

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

    public void setupPageAppearance(PageAppearance pageAppearance) {
        bus.post(new PageAppearanceEvent(pageAppearance));
    }
}