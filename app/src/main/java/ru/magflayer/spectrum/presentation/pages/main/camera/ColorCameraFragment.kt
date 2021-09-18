package ru.magflayer.spectrum.presentation.pages.main.camera

import android.annotation.SuppressLint
import android.graphics.SurfaceTexture
import android.os.Bundle
import android.view.*
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.Toast
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.GridLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import moxy.presenter.InjectPresenter
import ru.magflayer.spectrum.R
import ru.magflayer.spectrum.databinding.FragmentColorCameraBinding
import ru.magflayer.spectrum.domain.injection.InjectorManager
import ru.magflayer.spectrum.presentation.common.android.BaseFragment
import ru.magflayer.spectrum.presentation.common.extension.rotate
import ru.magflayer.spectrum.presentation.common.extension.visible

class ColorCameraFragment : BaseFragment(R.layout.fragment_color_camera),
    TextureView.SurfaceTextureListener, ColorCameraView {

    companion object {

        private const val ROTATION_INTERVAL = 5
        private const val ZOOM_VISIBLE_DELAY = 1000L
        private const val ZOOM_TOUCH_SPAN = 30

        fun newInstance(): ColorCameraFragment {
            return ColorCameraFragment()
        }
    }

    private val viewBinding by viewBinding(FragmentColorCameraBinding::bind)

    @InjectPresenter
    lateinit var presenter: ColorCameraPresenter

    private lateinit var adapter: ColorCameraAdapter
    private var orientationEventListener: OrientationEventListener? = null
    private var currentOrientation = Orientation.PORTRAIT
    private var prevZoomValue = 0.toFloat()

    override fun inject() {
        InjectorManager.appComponent?.inject(this)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = ColorCameraAdapter(requireContext())
        orientationEventListener = object : OrientationEventListener(context) {
            override fun onOrientationChanged(orientation: Int) {
                if (isLandscape(orientation) && currentOrientation != Orientation.LANDSCAPE) {
                    currentOrientation = Orientation.LANDSCAPE
                    setOrientation(currentOrientation)
                    viewBinding.colorRecycler.layoutManager =
                        GridLayoutManager(context, 2, GridLayoutManager.HORIZONTAL, true)
                } else if (isPortrait(orientation) && currentOrientation != Orientation.PORTRAIT) {
                    currentOrientation = Orientation.PORTRAIT
                    setOrientation(currentOrientation)
                    viewBinding.colorRecycler.layoutManager =
                        GridLayoutManager(context, 2, GridLayoutManager.HORIZONTAL, false)
                }
            }
        }

        if (orientationEventListener!!.canDetectOrientation()) {
            orientationEventListener?.enable()
        } else {
            orientationEventListener?.disable()
        }

        setOrientation(currentOrientation)

        viewBinding.toggleMode.setOnCheckedChangeListener { _, checked ->
            presenter.handleColorModeChanged(checked)
        }
        viewBinding.camera.setOnClickListener { presenter.handleFocusClicked() }
        viewBinding.menu.setOnClickListener { presenter.handleMenuClicked() }
        viewBinding.save.setOnClickListener { presenter.handleSaveClicked(if (currentOrientation == Orientation.LANDSCAPE) 0 else 90) }
        viewBinding.flash.setOnClickListener { presenter.handleFlashClick(viewBinding.flash.isChecked) }
    }

    override fun onDestroyView() {
        orientationEventListener?.disable()
        super.onDestroyView()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        val layoutManager = GridLayoutManager(context, 2, GridLayoutManager.HORIZONTAL, false)
        viewBinding.colorRecycler.layoutManager = layoutManager
        viewBinding.colorRecycler.adapter = adapter
        viewBinding.camera.surfaceTextureListener = this
        viewBinding.camera.setOnTouchListener { _, event ->
            when (event.actionMasked) {
                MotionEvent.ACTION_MOVE -> {
                    val value = if (currentOrientation == Orientation.PORTRAIT) {
                        event.y
                    } else {
                        event.x
                    }
                    val direction = if (currentOrientation == Orientation.PORTRAIT) {
                        (prevZoomValue - value).toInt()
                    } else {
                        (value - prevZoomValue).toInt()
                    }
                    if (prevZoomValue > 0 && (value / ZOOM_TOUCH_SPAN).toInt() != (prevZoomValue / ZOOM_TOUCH_SPAN).toInt()) {
                        presenter.handleCameraZoom(direction)
                    }
                    prevZoomValue = value
                    true
                }
                MotionEvent.ACTION_UP -> {
                    prevZoomValue = -1F
                    false
                }
                else -> {
                    false
                }
            }
        }
    }

    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
        logger.debug("onSurfaceTextureAvailable")
        presenter.handleSurfaceAvailable(surface)
    }

    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {
        //do nothing
    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
        logger.debug("onSurfaceTextureDestroyed")
        presenter.handleSurfaceDestroyed()
        return true
    }

    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {
        presenter.handleSurfaceUpdated()
    }

    override fun showPictureSavedToast() {
        Toast.makeText(context, R.string.camera_image_saved, Toast.LENGTH_SHORT).show()
    }

    override fun showColors(colors: List<Palette.Swatch>) {
        adapter.setData(colors)
    }

    override fun showColorDetails(mainColor: Int, titleColor: Int) {
        viewBinding.colorDetails.setColor(mainColor)
        viewBinding.pointDetector.setAimColor(titleColor)
    }

    override fun showColorName(name: String) {
        viewBinding.colorDetails.setColorName(name)
    }

    override fun showErrorMessage() {
        viewBinding.message.visibility = View.VISIBLE
    }

    override fun hideErrorMessage() {
        viewBinding.message.visibility = View.GONE
    }

    override fun showCrosshair() {
        viewBinding.pointDetector.visibility = View.VISIBLE
    }

    override fun hideCrosshair() {
        viewBinding.pointDetector.visibility = View.GONE
    }

    override fun showPanels() {
        showTopMenu()
        showBottomMenu()
    }

    override fun showFlash() {
        viewBinding.flash.visible(true)
    }

    override fun hideFlash() {
        viewBinding.flash.visible(false)
    }

    override fun changeMaxZoom(max: Int) {
        viewBinding.zoomSeek.max = max
    }

    override fun changeZoomProgress(progress: Int) = with(viewBinding) {
        zoomSeek.progress = progress

        if (zoomSeek.visibility != View.VISIBLE) {
            zoomSeek.visibility = View.VISIBLE
            zoomSeek.postDelayed({ zoomSeek.visibility = View.GONE }, ZOOM_VISIBLE_DELAY)
        }
    }

    override fun showSingleColorMode() = with(viewBinding) {
        colorDetails.visible(true)
        pointDetector.visible(true)
        colorRecycler.visible(false)
    }

    override fun showMultipleColorMode() = with(viewBinding) {
        colorDetails.visible(false)
        pointDetector.visible(false)
        colorRecycler.visible(true)
    }

    private fun showTopMenu() = with(viewBinding) {
        if (rightMenu.visibility != View.VISIBLE) {
            rightMenu.visibility = View.VISIBLE
            rightMenu.startAnimation(inFromTopAnimation())
        }
    }

    private fun showBottomMenu() = with(viewBinding) {
        if (leftMenu.visibility != View.VISIBLE) {
            leftMenu.visibility = View.VISIBLE
            leftMenu.startAnimation(inFromBottomAnimation())
        }
    }

    private fun inFromTopAnimation(): Animation {
        val inFromTop = TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, 0.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f,
            Animation.RELATIVE_TO_PARENT, +1.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f
        )
        inFromTop.duration = 800
        inFromTop.interpolator = AccelerateInterpolator()
        return inFromTop
    }

    private fun inFromBottomAnimation(): Animation {
        val inFromBottom = TranslateAnimation(
            Animation.RELATIVE_TO_PARENT, 0.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f,
            Animation.RELATIVE_TO_PARENT, -1.0f,
            Animation.RELATIVE_TO_PARENT, 0.0f
        )
        inFromBottom.duration = 800
        inFromBottom.interpolator = AccelerateInterpolator()
        return inFromBottom
    }

    private fun setOrientation(orientation: Orientation) = with(viewBinding) {
        logger.debug("Orientation changed: {}", orientation)
        menu.rotate(orientation.degree)
        toggleMode.rotate(orientation.degree)
        flash.rotate(orientation.degree)
        colorDetails.rotate(orientation.degree)
        zoomContainer.rotate((orientation.degree - 90).rem(360))
    }

    private fun isPortrait(orientation: Int): Boolean {
        return (orientation <= ROTATION_INTERVAL || orientation >= 360 - ROTATION_INTERVAL // [350 : 10]
                || orientation <= 180 + ROTATION_INTERVAL && orientation >= 180 - ROTATION_INTERVAL) //[170 : 190]
    }

    private fun isLandscape(orientation: Int): Boolean {
        return (orientation <= 90 + ROTATION_INTERVAL && orientation >= 90 - ROTATION_INTERVAL // [100 : 80]
                || orientation <= 270 + ROTATION_INTERVAL && orientation >= 270 - ROTATION_INTERVAL) // [280 : 260]
    }

    private enum class Orientation(val degree: Int) {
        PORTRAIT(0),
        LANDSCAPE(90)
    }
}
