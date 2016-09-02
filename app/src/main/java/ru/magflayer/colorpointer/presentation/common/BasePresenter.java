package ru.magflayer.colorpointer.presentation.common;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;

import ru.magflayer.colorpointer.data.database.AppRealm;

public abstract class BasePresenter<View, Router> {

    protected Logger logger = LoggerFactory.getLogger(getClass().getSimpleName());

    private View view;
    private Router router;

    @Inject
    protected AppRealm appRealm;

    public abstract void onStart();

    public abstract void onStop();

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
}