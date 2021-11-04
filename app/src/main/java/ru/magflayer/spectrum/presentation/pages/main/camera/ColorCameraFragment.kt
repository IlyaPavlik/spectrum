package ru.magflayer.spectrum.presentation.pages.main.camera

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.Toast
import androidx.core.view.GestureDetectorCompat
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.GridLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.android.components.ActivityComponent
import moxy.presenter.InjectPresenter
import moxy.presenter.ProvidePresenter
import ru.magflayer.spectrum.R
import ru.magflayer.spectrum.databinding.FragmentColorCameraBinding
import ru.magflayer.spectrum.presentation.common.android.BaseFragment
import ru.magflayer.spectrum.presentation.common.extension.hide
import ru.magflayer.spectrum.presentation.common.extension.rotate
import ru.magflayer.spectrum.presentation.common.extension.show
import ru.magflayer.spectrum.presentation.pages.main.camera.holder.ColorAnalyzer
import ru.magflayer.spectrum.presentation.pages.main.camera.holder.ColorCameraHolder

class ColorCameraFragment : BaseFragment(R.layout.fragment_color_camera), ColorCameraView {

    companion object {

        private const val ZOOM_VISIBLE_DELAY = 1000L //ms
        private const val COLOR_SPAN_COUNT = 2
        private const val MENU_ANIMATION_DURATION = 800L

        fun newInstance(): ColorCameraFragment {
            return ColorCameraFragment()
        }
    }

    @InjectPresenter
    lateinit var presenter: ColorCameraPresenter

    private val viewBinding by viewBinding(FragmentColorCameraBinding::bind)
    private val cameraHolder by lazy { ColorCameraHolder(requireContext()) }
    private val gestureDetector by lazy {
        GestureDetectorCompat(
            requireContext(),
            gestureDetectorListener
        )
    }
    private val gestureDetectorListener = object : GestureDetector.SimpleOnGestureListener() {

        override fun onScroll(
            e1: MotionEvent?,
            e2: MotionEvent?,
            distanceX: Float,
            distanceY: Float
        ): Boolean {
            presenter.handleCameraZoom(distanceX, distanceY)
            return super.onScroll(e1, e2, distanceX, distanceY)
        }
    }
    private val orientationEventListener by lazy {
        object : OrientationEventListener(requireContext()) {
            override fun onOrientationChanged(orientationDegree: Int) {
                presenter.handleOrientationChanged(orientationDegree)
            }
        }
    }
    private val colorAnalyzer by lazy {
        ColorAnalyzer().apply {
            analyzeResultListener = { presenter.handleAnalyzeImage(it) }
        }
    }

    private lateinit var adapter: ColorCameraAdapter

    @EntryPoint
    @InstallIn(ActivityComponent::class)
    interface ColorCameraEntryPoint {
        fun colorCameraPresenter(): ColorCameraPresenter
    }

    @ProvidePresenter
    fun providePresenter(): ColorCameraPresenter {
        return EntryPointAccessors.fromActivity(
            requireActivity(),
            ColorCameraEntryPoint::class.java
        ).colorCameraPresenter()
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        if (orientationEventListener.canDetectOrientation()) {
            orientationEventListener.enable()
        } else {
            orientationEventListener.disable()
        }

        viewBinding.toggleMode.setOnCheckedChangeListener { _, checked ->
            presenter.handleColorModeChanged(checked)
        }
        viewBinding.menu.setOnClickListener { presenter.handleMenuClicked() }
        viewBinding.save.setOnClickListener {
            cameraHolder.takePicture(
                onSuccess = { presenter.handlePictureCaptureSucceed(it) },
                onError = { presenter.handlePictureCaptureFailed(it) }
            )
        }
        viewBinding.flash.setOnClickListener { presenter.handleFlashClick(viewBinding.flash.isChecked) }

        adapter = ColorCameraAdapter(requireContext())
        val layoutManager = GridLayoutManager(
            requireContext(),
            COLOR_SPAN_COUNT,
            GridLayoutManager.HORIZONTAL,
            false
        )
        viewBinding.colorRecycler.layoutManager = layoutManager
        viewBinding.colorRecycler.adapter = adapter

        viewBinding.cameraPreview.setOnClickListener { presenter.handleFocusClicked() }
        viewBinding.cameraPreview.setOnTouchListener { _, motionEvent ->
            gestureDetector.onTouchEvent(motionEvent)
        }

        cameraHolder.startCamera(
            viewLifecycleOwner,
            viewBinding.cameraPreview,
            colorAnalyzer
        ) { cameraInfo ->
            presenter.handleCameraInitialized(cameraInfo)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        orientationEventListener.disable()
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
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
        viewBinding.message.show()
    }

    override fun hideErrorMessage() {
        viewBinding.message.hide()
    }

    override fun showCrosshair() {
        viewBinding.pointDetector.show()
    }

    override fun hideCrosshair() {
        viewBinding.pointDetector.hide()
    }

    override fun showPanels() {
        showTopMenu()
        showBottomMenu()
    }

    override fun showFlash() {
        viewBinding.flash.show()
    }

    override fun hideFlash() {
        viewBinding.flash.hide()
    }

    override fun enableFlash() {
        cameraHolder.enabledFlash()
    }

    override fun disableFlash() {
        cameraHolder.disableFlash()
    }

    override fun showZoom(zoom: Float, maxZoom: Int) = with(viewBinding) {
        zoomSeek.max = maxZoom
        zoomSeek.progress = zoom.toInt()

        if (zoomSeek.visibility != View.VISIBLE) {
            zoomSeek.show()
            zoomSeek.postDelayed({ zoomSeek.hide() }, ZOOM_VISIBLE_DELAY)
        }

        cameraHolder.setZoomRatio(zoom)
    }

    override fun showSingleColorMode() = with(viewBinding) {
        colorDetails.show()
        pointDetector.show()
        colorRecycler.hide()
        colorAnalyzer.analyzerType = ColorAnalyzer.Type.CENTER
    }

    override fun showMultipleColorMode() = with(viewBinding) {
        colorDetails.hide()
        pointDetector.hide()
        colorRecycler.show()
        colorAnalyzer.analyzerType = ColorAnalyzer.Type.SWATCHES
    }

    override fun updateViewOrientation(orientation: CameraOrientation) = with(viewBinding) {
        menu.rotate(orientation.degree)
        toggleMode.rotate(orientation.degree)
        flash.rotate(orientation.degree)
        colorDetails.rotate(orientation.degree)
        zoomContainer.rotate((orientation.degree - 90).rem(360))
        viewBinding.colorRecycler.layoutManager =
            GridLayoutManager(
                context,
                COLOR_SPAN_COUNT,
                GridLayoutManager.HORIZONTAL,
                orientation == CameraOrientation.LANDSCAPE
            )
    }

    override fun autoFocus() {
        cameraHolder.autoFocus(viewBinding.cameraPreview)
    }

    private fun showTopMenu() = with(viewBinding) {
        if (rightMenu.visibility != View.VISIBLE) {
            rightMenu.visibility = View.VISIBLE
            rightMenu.startAnimation(inFromTopAnimation())
        }
    }

    private fun showBottomMenu() = with(viewBinding) {
        if (leftMenu.visibility != View.VISIBLE) {
            leftMenu.show()
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
        inFromTop.duration = MENU_ANIMATION_DURATION
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
        inFromBottom.duration = MENU_ANIMATION_DURATION
        inFromBottom.interpolator = AccelerateInterpolator()
        return inFromBottom
    }
}
