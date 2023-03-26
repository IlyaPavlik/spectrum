package ru.magflayer.spectrum.presentation.pages.main

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.WindowManager
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
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
import ru.magflayer.spectrum.presentation.common.extension.visible
import ru.magflayer.spectrum.presentation.pages.main.toolbar.ToolbarViewHolder

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

    private lateinit var toolbarViewHolder: ToolbarViewHolder
    private lateinit var appBarConfiguration: AppBarConfiguration

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

        val host: NavHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment_container) as NavHostFragment? ?: return
        val navController = host.navController

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
        )

        toolbarViewHolder = ToolbarViewHolder(this, viewBinding.toolbar)
        viewBinding.fab.setOnClickListener { presenter.handleFabClick() }

        appBarConfiguration = AppBarConfiguration(navController.graph)

        setupActionBar(navController, appBarConfiguration)
    }

    override fun onDestroy() {
        toolbarViewHolder.onDestroy()
        super.onDestroy()
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.nav_host_fragment_container).navigateUp(appBarConfiguration)
    }

    override fun showToolbar(showToolbar: Boolean) {
        viewBinding.toolbar.visible(showToolbar)
    }

    override fun showFloatingButton(showFloatingButton: Boolean) {
        viewBinding.fab.visible(showFloatingButton)
    }

    private fun setupActionBar(navController: NavController, appBarConfig: AppBarConfiguration) {
        setupActionBarWithNavController(navController, appBarConfig)
    }
}
