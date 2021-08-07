package ru.magflayer.spectrum.presentation.pages.splash

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import moxy.presenter.InjectPresenter

import ru.magflayer.spectrum.domain.injection.InjectorManager
import ru.magflayer.spectrum.presentation.common.android.BaseActivity

class SplashActivity : BaseActivity(), SplashView {

    companion object {
        private const val CAMERA_PERMISSION_REQUEST = 111

        fun newIntent(context: Context): Intent {
            return Intent(context, SplashActivity::class.java)
        }

    }

    @InjectPresenter
    lateinit var presenter: SplashPresenter

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

    override fun inject() {
        InjectorManager.appComponent?.inject(this)
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
