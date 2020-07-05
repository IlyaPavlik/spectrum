package ru.magflayer.spectrum.presentation.common.mvp

import com.squareup.otto.Bus
import moxy.MvpPresenter
import moxy.MvpView
import org.slf4j.LoggerFactory
import ru.magflayer.spectrum.common.utils.RxUtils
import ru.magflayer.spectrum.presentation.common.model.PageAppearance
import ru.magflayer.spectrum.presentation.common.model.ToolbarAppearance
import rx.Observable
import rx.Subscription
import rx.functions.Action0
import rx.functions.Action1
import java.util.*
import javax.inject.Inject

abstract class BasePresenter<View : MvpView> : MvpPresenter<View>() {

    protected var logger = LoggerFactory.getLogger(javaClass.simpleName)

    private val subscriptionMap = HashMap<String, Subscription>()

    @Inject
    lateinit var bus: Bus

    open val toolbarAppearance: ToolbarAppearance
        get() = ToolbarAppearance(
                ToolbarAppearance.Visibility.NO_INFLUENCE,
                ""
        )

    open val pageAppearance: PageAppearance
        get() = PageAppearance.builder()
                .showFloatingButton(PageAppearance.FloatingButtonState.NO_INFLUENCE)
                .build()

    init {
        inject()
        bus.register(this)
    }

    override fun attachView(view: View) {
        super.attachView(view)

        setupPageAppearance(pageAppearance)
        setupToolbarAppearance(toolbarAppearance)
    }

    override fun onDestroy() {
        super.onDestroy()
        bus.unregister(this)
        unsubscribe()
    }

    protected abstract fun inject()

    protected fun <T> execute(observable: Observable<T>, action1: Action1<T>) {
        execute(observable.hashCode().toString(), observable, action1,
                Action1 { throwable -> logger.error("Error occurred: ", throwable) }, Action0 { })
    }

    protected fun <T> execute(observable: Observable<T>, action1: Action1<T>,
                              errorAction: Action1<Throwable>) {
        execute(observable.hashCode().toString(), observable, action1, errorAction, Action0 { })
    }

    protected fun <T> execute(observable: Observable<T>, action1: Action1<T>,
                              errorAction: Action1<Throwable>, completeAction: Action0) {
        execute(observable.hashCode().toString(), observable, action1, errorAction, completeAction)
    }

    protected fun <T> execute(tag: String, observable: Observable<T>, action1: Action1<T>) {
        execute(tag, observable, action1, Action1 { throwable -> logger.error("Error occurred: ", throwable) }, Action0 { })
    }

    protected fun <T> execute(tag: String, observable: Observable<T>, action1: Action1<T>,
                              errorAction: Action1<Throwable>) {
        execute(tag, observable, action1, errorAction, Action0 { })
    }

    protected fun <T> execute(tag: String, observable: Observable<T>, action1: Action1<T>,
                              errorAction: Action1<Throwable>, completeAction: Action0) {
        if (subscriptionMap.containsKey(tag)) {
            val subscription = subscriptionMap[tag]
            if (subscription != null && !subscription.isUnsubscribed) {
                subscription.unsubscribe()
            }
        }

        subscriptionMap[tag] = observable
                .compose(RxUtils.applySchedulers())
                .subscribe(action1, errorAction, completeAction)
    }

    private fun unsubscribe() {
        for ((_, value) in subscriptionMap) {
            if (!value.isUnsubscribed) {
                value.unsubscribe()
            }
        }

        subscriptionMap.clear()
    }

    protected fun setupToolbarAppearance(toolbarAppearance: ToolbarAppearance) {
        bus.post(toolbarAppearance)
    }

    protected fun setupPageAppearance(pageAppearance: PageAppearance) {
        bus.post(pageAppearance)
    }
}