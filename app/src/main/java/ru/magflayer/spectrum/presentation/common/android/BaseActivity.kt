package ru.magflayer.spectrum.presentation.common.android

import android.view.View
import androidx.annotation.LayoutRes
import moxy.MvpAppCompatActivity
import org.slf4j.Logger
import ru.magflayer.spectrum.R
import ru.magflayer.spectrum.presentation.common.extension.hide
import ru.magflayer.spectrum.presentation.common.extension.logger
import ru.magflayer.spectrum.presentation.common.extension.show
import ru.magflayer.spectrum.presentation.common.mvp.view.PageView

abstract class BaseActivity : MvpAppCompatActivity, PageView {

    protected val logger: Logger by logger()

    constructor() : super()

    constructor(@LayoutRes contentLayoutId: Int) : super(contentLayoutId)

    override fun onResume() {
        super.onResume()
        logger.debug("onResume")
    }

    override fun onPause() {
        super.onPause()
        logger.debug("onPause")
    }

    override fun showProgressBar() {
        findViewById<View>(R.id.progress_bar)?.show()
    }

    override fun hideProgressBar() {
        findViewById<View>(R.id.progress_bar)?.hide()
    }
}
