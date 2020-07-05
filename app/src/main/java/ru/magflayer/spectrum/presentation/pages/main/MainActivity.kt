package ru.magflayer.spectrum.presentation.pages.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import androidx.appcompat.widget.Toolbar
import butterknife.BindView
import butterknife.OnClick
import com.google.android.material.floatingactionbutton.FloatingActionButton
import moxy.presenter.InjectPresenter
import ru.magflayer.spectrum.R
import ru.magflayer.spectrum.domain.injection.InjectorManager
import ru.magflayer.spectrum.presentation.common.android.BaseActivity
import ru.magflayer.spectrum.presentation.common.android.layout.Layout
import ru.magflayer.spectrum.presentation.common.android.navigation.holder.MainRouterHolder
import ru.magflayer.spectrum.presentation.common.android.navigation.navigator.MainNavigator
import ru.magflayer.spectrum.presentation.pages.main.toolbar.ToolbarViewHolder
import javax.inject.Inject

@Layout(R.layout.activity_main)
class MainActivity : BaseActivity(), MainView {

    companion object {
        @JvmStatic
        fun newIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }

    @InjectPresenter
    lateinit var presenter: MainPresenter

    @Inject
    lateinit var mainRouterHolder: MainRouterHolder

    @BindView(R.id.toolbar)
    lateinit var toolbar: Toolbar

    @BindView(R.id.fab)
    lateinit var floatingActionButton: FloatingActionButton

    private lateinit var mainNavigator: MainNavigator
    private lateinit var toolbarViewHolder: ToolbarViewHolder

    override fun inject() {
        InjectorManager.appComponent?.inject(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN)

        mainNavigator = MainNavigator(this, R.id.container)
        toolbarViewHolder = ToolbarViewHolder(this, toolbar)
        if (savedInstanceState == null) {
            presenter.openMainScreen()
        }
    }

    override fun onResume() {
        super.onResume()
        mainRouterHolder.setNavigator(mainNavigator)
    }

    override fun onPause() {
        mainRouterHolder.removeNavigator()
        super.onPause()
    }

    override fun onDestroy() {
        toolbarViewHolder.onDestroy()
        super.onDestroy()
    }

    override fun showToolbar(showToolbar: Boolean) {
        toolbar.visibility = if (showToolbar) View.VISIBLE else View.GONE
    }

    override fun showFloatingButton(showFloatingButton: Boolean) {
        floatingActionButton.visibility = if (showFloatingButton) View.VISIBLE else View.GONE
    }

    @OnClick(R.id.fab)
    fun onFabClick() {
        presenter.handleFabClick()
    }
}
