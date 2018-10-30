package ru.magflayer.spectrum.presentation.common.android

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import com.arellomobile.mvp.MvpAppCompatFragment
import org.slf4j.LoggerFactory
import ru.magflayer.spectrum.R
import ru.magflayer.spectrum.presentation.common.helper.AppHelper
import ru.magflayer.spectrum.presentation.common.mvp.view.PageView

abstract class BaseFragment : MvpAppCompatFragment(), PageView {

    protected val logger = LoggerFactory.getLogger(javaClass.simpleName)

    @BindView(R.id.progress_bar)
    @JvmField
    var progressBar: View? = null

    private var unbinder: Unbinder? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val layoutId = AppHelper.getLayoutId(this)
        if (layoutId != null) {
            val view = inflater.inflate(layoutId, null)
            unbinder = ButterKnife.bind(this, view)
            return view
        }
        return super.onCreateView(inflater, container, savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        logger.debug("onViewCreated")
        val arguments = arguments
        if (arguments != null) {
            handleArguments(arguments)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        inject()
    }

    override fun onDestroyView() {
        logger.debug("onDestroyView")
        unbinder?.unbind()
        super.onDestroyView()
    }

    protected abstract fun inject()

    override fun showProgressBar() {
        progressBar?.visibility = View.VISIBLE
    }

    override fun hideProgressBar() {
        progressBar?.visibility = View.GONE
    }

    protected fun handleArguments(arguments: Bundle) {
        //do nothing
    }
}
