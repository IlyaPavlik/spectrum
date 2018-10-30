package ru.magflayer.spectrum.presentation.common.android

import android.os.Bundle
import android.view.View
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.Unbinder
import com.arellomobile.mvp.MvpAppCompatActivity
import org.slf4j.LoggerFactory
import ru.magflayer.spectrum.R
import ru.magflayer.spectrum.presentation.common.android.navigation.holder.GlobalRouterHolder
import ru.magflayer.spectrum.presentation.common.android.navigation.navigator.GlobalNavigator
import ru.magflayer.spectrum.presentation.common.helper.AppHelper
import ru.magflayer.spectrum.presentation.common.mvp.view.PageView
import javax.inject.Inject

abstract class BaseActivity : MvpAppCompatActivity(), PageView {

    protected val logger = LoggerFactory.getLogger(javaClass)

    @Inject
    lateinit var globalRouterHolder: GlobalRouterHolder

    @BindView(R.id.progress_bar)
    @JvmField
    var progressBar: View? = null

    private val globalNavigator = GlobalNavigator(this)

    private var unbinder: Unbinder? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val layoutId = AppHelper.getLayoutId(this)
        if (layoutId != null) {
            setContentView(layoutId)
        }
        unbinder = ButterKnife.bind(this)

        inject()
    }

    protected abstract fun inject()

    override fun onResume() {
        super.onResume()
        globalRouterHolder.setNavigator(globalNavigator)
    }

    override fun onPause() {
        globalRouterHolder.removeNavigator()
        super.onPause()
    }

    override fun onDestroy() {
        unbinder?.unbind()
        super.onDestroy()
    }

    override fun showProgressBar() {
        progressBar?.visibility = View.VISIBLE
    }

    override fun hideProgressBar() {
        progressBar?.visibility = View.GONE
    }
}
