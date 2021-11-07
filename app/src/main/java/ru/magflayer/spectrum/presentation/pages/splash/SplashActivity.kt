package ru.magflayer.spectrum.presentation.pages.splash

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.components.ActivityComponent
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.magflayer.spectrum.R
import ru.magflayer.spectrum.presentation.common.android.BaseActivity
import ru.magflayer.spectrum.presentation.pages.main.MainActivity

@AndroidEntryPoint
class SplashActivity : BaseActivity(), SplashView {

    companion object {

        private const val CAMERA_PERMISSION_REQUEST = 111
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)

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

        if (allPermissionsGranted()) {
            presenter.handlePermissionsGranted()
        } else {
            ActivityCompat.requestPermissions(
                this,
                REQUIRED_PERMISSIONS,
                CAMERA_PERMISSION_REQUEST
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            CAMERA_PERMISSION_REQUEST -> if (allPermissionsGranted()) {
                presenter.handlePermissionsGranted()
            } else {
                Toast.makeText(this, R.string.camera_required, Toast.LENGTH_SHORT).show()
                finish()
            }
            else -> super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        }
    }

    override fun openMainScreen() {
        finish()
        startActivity(MainActivity.newIntent(this))
    }

    private fun allPermissionsGranted(): Boolean = REQUIRED_PERMISSIONS.all { permission ->
        ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
    }
}
