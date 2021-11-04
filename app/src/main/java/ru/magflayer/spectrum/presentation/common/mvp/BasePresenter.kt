package ru.magflayer.spectrum.presentation.common.mvp

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import moxy.MvpPresenter
import moxy.MvpView
import org.slf4j.Logger
import ru.magflayer.spectrum.presentation.common.extension.logger
import ru.magflayer.spectrum.presentation.common.model.PageAppearance
import ru.magflayer.spectrum.presentation.common.model.ToolbarAppearance

abstract class BasePresenter<View : MvpView> : MvpPresenter<View>() {

    protected val logger: Logger by logger()
    protected val presenterScope: CoroutineScope by lazy { MainScope() }

    private val defaultErrorHandler: (Throwable) -> Unit = { logger.warn("Error: ", it) }

    open val toolbarAppearance: ToolbarAppearance
        get() = ToolbarAppearance(
            ToolbarAppearance.Visibility.NO_INFLUENCE,
            ""
        )

    open val pageAppearance: PageAppearance
        get() = PageAppearance.builder()
            .showFloatingButton(PageAppearance.FloatingButtonState.NO_INFLUENCE)
            .build()

    override fun onDestroy() {
        super.onDestroy()
        presenterScope.cancel()
    }

    protected fun <T> execute(
        flow: Flow<T>,
        onSuccess: (T) -> Unit,
        onError: (Throwable) -> Unit = defaultErrorHandler
    ) {
        presenterScope.launch {
            flow
                .catch { throwable -> onError.invoke(throwable) }
                .collect { value -> onSuccess.invoke(value) }
        }
    }
}