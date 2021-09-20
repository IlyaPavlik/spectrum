package ru.magflayer.spectrum.presentation.common.android

import android.os.Bundle
import android.view.View
import androidx.annotation.LayoutRes
import moxy.MvpAppCompatFragment
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ru.magflayer.spectrum.R
import ru.magflayer.spectrum.presentation.common.mvp.view.PageView

abstract class BaseFragment(
    @LayoutRes contentLayoutId: Int
) : MvpAppCompatFragment(contentLayoutId), PageView {

    protected val logger: Logger by lazy { LoggerFactory.getLogger(javaClass.simpleName) }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        logger.debug("onViewCreated")
        val arguments = arguments
        if (arguments != null) {
            handleArguments(arguments)
        }
    }

    override fun showProgressBar() {
        view?.findViewById<View>(R.id.progress_bar)?.visibility = View.VISIBLE
    }

    override fun hideProgressBar() {
        view?.findViewById<View>(R.id.progress_bar)?.visibility = View.GONE
    }

    protected fun handleArguments(arguments: Bundle) {
        //do nothing
    }
}
