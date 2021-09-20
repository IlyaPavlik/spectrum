package ru.magflayer.spectrum.presentation.pages.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.components.ActivityComponent
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.magflayer.spectrum.R
import ru.magflayer.spectrum.databinding.ActivityMainBinding
import ru.magflayer.spectrum.presentation.common.android.BaseActivity
import ru.magflayer.spectrum.presentation.common.android.navigation.holder.MainRouterHolder
import ru.magflayer.spectrum.presentation.common.android.navigation.navigator.MainNavigator
import ru.magflayer.spectrum.presentation.pages.main.toolbar.ToolbarViewHolder
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : BaseActivity(R.layout.activity_main), MainView {

    companion object {

        fun newIntent(context: Context): Intent {
            return Intent(context, MainActivity::class.java)
        }
    }

    private val viewBinding by viewBinding(ActivityMainBinding::bind)

    @InjectPresenter
    lateinit var presenter: MainPresenter

    @Inject
    lateinit var mainRouterHolder: MainRouterHolder

    private lateinit var mainNavigator: MainNavigator
    private lateinit var toolbarViewHolder: ToolbarViewHolder

    @EntryPoint
    @InstallIn(ActivityComponent::class)
    interface MainEntryPoint {
        fun mainPresenter(): MainPresenter
    }

    @ProvidePresenter
    fun providePresenter(): MainPresenter {
        return EntryPointAccessors.fromActivity(this, MainEntryPoint::class.java)
            .mainPresenter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        mainNavigator = MainNavigator(this, R.id.container)
        toolbarViewHolder = ToolbarViewHolder(this, viewBinding.toolbar)
        viewBinding.fab.setOnClickListener { presenter.handleFabClick() }
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
        viewBinding.toolbar.visibility = if (showToolbar) View.VISIBLE else View.GONE
    }

    override fun showFloatingButton(showFloatingButton: Boolean) {
        viewBinding.fab.visibility = if (showFloatingButton) View.VISIBLE else View.GONE
    }
}
