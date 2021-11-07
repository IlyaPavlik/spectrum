package ru.magflayer.spectrum.presentation.pages.main.toolbar

import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.components.ActivityComponent
import moxy.MvpDelegate
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter

class ToolbarViewHolder(
    private val activity: AppCompatActivity,
    private val toolbar: Toolbar
) : ToolbarView {

    @InjectPresenter
    lateinit var presenter: ToolbarPresenter

    private var mvpDelegate = MvpDelegate(this)

    init {
        getMvpDelegate().onCreate()
        getMvpDelegate().onAttach()

        activity.setSupportActionBar(toolbar)
    }

    @EntryPoint
    @InstallIn(ActivityComponent::class)
    interface ToolbarEntryPoint {
        fun presenter(): ToolbarPresenter
    }

    @ProvidePresenter
    fun providePresenter(): ToolbarPresenter {
        return EntryPointAccessors.fromActivity(
            activity,
            ToolbarEntryPoint::class.java
        ).presenter()
    }

    override fun showToolbar() {
        toolbar.visibility = View.VISIBLE
    }

    override fun hideToolbar() {
        toolbar.visibility = View.GONE
    }

    override fun showTitle(title: String) {
        toolbar.title = title
    }

    fun onDestroy() {
        getMvpDelegate().onDetach()
        getMvpDelegate().onDestroy()
    }

    private fun getMvpDelegate(): MvpDelegate<ToolbarViewHolder> {
        return mvpDelegate
    }
}
