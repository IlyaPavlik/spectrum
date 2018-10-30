package ru.magflayer.spectrum.presentation.pages.main.toolbar

import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.Toolbar
import android.view.View
import com.arellomobile.mvp.MvpDelegate
import com.arellomobile.mvp.presenter.InjectPresenter

class ToolbarViewHolder(activity: AppCompatActivity, private val toolbar: Toolbar) : ToolbarView {

    @InjectPresenter
    lateinit var presenter: ToolbarPresenter

    private var mvpDelegate = MvpDelegate(this)

    init {
        getMvpDelegate().onCreate()
        getMvpDelegate().onAttach()

        activity.setSupportActionBar(toolbar)
        val actionBar = activity.supportActionBar
        actionBar?.setDisplayHomeAsUpEnabled(true)

        toolbar.setNavigationOnClickListener { presenter.handleBack() }
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
