package ru.magflayer.spectrum.presentation.common.android

import android.view.View
import androidx.annotation.LayoutRes
import moxy.MvpAppCompatActivity
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import ru.magflayer.spectrum.R
import ru.magflayer.spectrum.presentation.common.android.navigation.holder.GlobalRouterHolder
import ru.magflayer.spectrum.presentation.common.android.navigation.navigator.GlobalNavigator
import ru.magflayer.spectrum.presentation.common.mvp.view.PageView
import javax.inject.Inject

abstract class BaseActivity : MvpAppCompatActivity, PageView {

    @Inject
    lateinit var globalRouterHolder: GlobalRouterHolder

    protected val logger: Logger by lazy { LoggerFactory.getLogger(javaClass) }

    private val globalNavigator by lazy { GlobalNavigator(this) }

    constructor() : super()

    constructor(@LayoutRes contentLayoutId: Int) : super(contentLayoutId)

    override fun onResume() {
        super.onResume()
        globalRouterHolder.setNavigator(globalNavigator)
    }

    override fun onPause() {
        globalRouterHolder.removeNavigator()
        super.onPause()
    }

    override fun showProgressBar() {
        findViewById<View>(R.id.progress_bar)?.visibility = View.VISIBLE
    }

    override fun hideProgressBar() {
        findViewById<View>(R.id.progress_bar)?.visibility = View.GONE
    }
}
