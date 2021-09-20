package ru.magflayer.spectrum.presentation.pages.splash

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.components.ActivityComponent
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.magflayer.spectrum.presentation.common.android.BaseActivity

@AndroidEntryPoint
class SplashActivity : BaseActivity(), SplashView {

    companion object {
        private const val CAMERA_PERMISSION_REQUEST = 111

        fun newIntent(context: Context): Intent {
            return Intent(context, SplashActivity::class.java)
        }
    }

    @EntryPoint
    @InstallIn(ActivityComponent::class)
    interface SplashEntryPoint {
        fun splashPresenter(): SplashPresenter
    }

    @InjectPresenter
    lateinit var presenter: SplashPresenter

    @ProvidePresenter
    fun providePresenter(): SplashPresenter {
        return EntryPointAccessors.fromActivity(this, SplashEntryPoint::class.java)
            .splashPresenter()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val permissionCheck = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        if (permissionCheck != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                CAMERA_PERMISSION_REQUEST
            )
            return
        }
        presenter.openMainPage()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            CAMERA_PERMISSION_REQUEST -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    presenter.openMainPage()
                } else {
                    finish()
                }
                return
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun closeScreen() {
        finish()
    }
}
