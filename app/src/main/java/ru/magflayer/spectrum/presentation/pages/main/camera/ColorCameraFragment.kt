package ru.magflayer.spectrum.presentation.pages.main.camera

import android.annotation.SuppressLint
import android.graphics.SurfaceTexture
import android.os.Bundle
import android.view.*
import android.view.animation.AccelerateInterpolator
import android.view.animation.Animation
import android.view.animation.TranslateAnimation
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import android.widget.ToggleButton
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.OnClick
import moxy.presenter.InjectPresenter
import ru.magflayer.spectrum.R
import ru.magflayer.spectrum.domain.injection.InjectorManager
import ru.magflayer.spectrum.presentation.common.android.BaseFragment
import ru.magflayer.spectrum.presentation.common.android.layout.Layout
import ru.magflayer.spectrum.presentation.common.android.widget.ColorDetailsWidget
import ru.magflayer.spectrum.presentation.common.android.widget.PointView
import ru.magflayer.spectrum.presentation.common.extension.rotate
import ru.magflayer.spectrum.presentation.common.extension.visible
import ru.magflayer.spectrum.presentation.common.model.SurfaceInfo

@Layout(R.layout.fragment_color_camera)
class ColorCameraFragment : BaseFragment(), TextureView.SurfaceTextureListener, ColorCameraView {

    companion object {

        private const val ROTATION_INTERVAL = 5
        private const val ZOOM_VISIBLE_DELAY = 1000L
        private const val ZOOM_TOUCH_SPAN = 30

        fun newInstance(): ColorCameraFragment {
            return ColorCameraFragment()
        }
    }

    @InjectPresenter
    lateinit var presenter: ColorCameraPresenter

    @BindView(R.id.camera)
    lateinit var cameraView: TextureView

    @BindView(R.id.right_menu)
    lateinit var buttonsMenuView: ViewGroup

    @BindView(R.id.left_menu)
    lateinit var infoMenuView: ViewGroup

    @BindView(R.id.menu)
    lateinit var menuButton: View

    @BindView(R.id.color_recycler)
    lateinit var colorRecycler: RecyclerView

    @BindView(R.id.toggle_mode)
    lateinit var toggleView: ToggleButton

    @BindView(R.id.flash)
    lateinit var flashView: ToggleButton

    @BindView(R.id.point_detector)
    lateinit var pointView: PointView

    @BindView(R.id.color_details)
    lateinit var colorDetailsWidget: ColorDetailsWidget

    @BindView(R.id.message)
    lateinit var messageView: TextView

    @BindView(R.id.zoom_container)
    lateinit var zoomContainer: ViewGroup

    @BindView(R.id.zoom_seek)
    lateinit var zoomBar: SeekBar

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
                    colorRecycler.layoutManager =
                        GridLayoutManager(context, 2, GridLayoutManager.HORIZONTAL, true)
                } else if (isPortrait(orientation) && currentOrientation != Orientation.PORTRAIT) {
                    currentOrientation = Orientation.PORTRAIT
                    setOrientation(currentOrientation)
                    colorRecycler.layoutManager =
                        GridLayoutManager(context, 2, GridLayoutManager.HORIZONTAL, false)
                }
            }
        }

        if (orientationEventListener!!.canDetectOrientation()) {
            orientationEventListener?.enable()
        } else {
            orientationEventListener?.disable()
        }

        updateMode(toggleView.isChecked)
        setOrientation(currentOrientation)
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
        colorRecycler.layoutManager = layoutManager
        colorRecycler.adapter = adapter
        cameraView.surfaceTextureListener = this
        cameraView.setOnTouchListener { _, event ->
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
        colorRecycler.visibility = if (toggleView.isChecked) View.GONE else View.VISIBLE
        toggleView.setOnCheckedChangeListener { _, isChecked -> updateMode(isChecked) }
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
        presenter.updateSurface(if (toggleView.isChecked) SurfaceInfo.Type.SINGLE else SurfaceInfo.Type.MULTIPLE)
    }

    override fun showPictureSavedToast() {
        Toast.makeText(context, R.string.camera_image_saved, Toast.LENGTH_SHORT).show()
    }

    override fun showColors(colors: List<Palette.Swatch>) {
        adapter.setData(colors)
    }

    override fun showColorDetails(mainColor: Int, titleColor: Int) {
        colorDetailsWidget.setColor(mainColor)
        pointView.setAimColor(titleColor)
    }

    override fun showColorName(name: String) {
        colorDetailsWidget.setColorName(name)
    }

    override fun showErrorMessage() {
        messageView.visibility = View.VISIBLE
    }

    override fun hideErrorMessage() {
        messageView.visibility = View.GONE
    }

    override fun showCrosshair() {
        pointView.visibility = View.VISIBLE
    }

    override fun hideCrosshair() {
        pointView.visibility = View.GONE
    }

    override fun showPanels() {
        showTopMenu()
        showBottomMenu()
    }

    override fun showFlash() {
        flashView.visible(true)
    }

    override fun hideFlash() {
        flashView.visible(false)
    }

    override fun changeMaxZoom(max: Int) {
        zoomBar.max = max
    }

    override fun changeZoomProgress(progress: Int) {
        zoomBar.progress = progress

        if (zoomBar.visibility != View.VISIBLE) {
            zoomBar.visibility = View.VISIBLE
            zoomBar.postDelayed({ zoomBar.visibility = View.GONE }, ZOOM_VISIBLE_DELAY)
        }
    }

    @OnClick(R.id.camera)
    fun onFocusClick() {
        presenter.handleFocusClicked()
    }

    @OnClick(R.id.menu)
    fun onMenuClick() {
        presenter.openHistory()
    }

    @OnClick(R.id.save)
    fun onSaveClick() {
        presenter.handleSaveClicked(if (currentOrientation == Orientation.LANDSCAPE) 0 else 90)
    }

    @OnClick(R.id.flash)
    fun onFlashClick() {
        presenter.handleFlashClick(flashView.isChecked)
    }

    private fun showTopMenu() {
        if (buttonsMenuView.visibility != View.VISIBLE) {
            buttonsMenuView.visibility = View.VISIBLE
            buttonsMenuView.startAnimation(inFromTopAnimation())
        }
    }

    private fun showBottomMenu() {
        if (infoMenuView.visibility != View.VISIBLE) {
            infoMenuView.visibility = View.VISIBLE
            infoMenuView.startAnimation(inFromBottomAnimation())
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

    private fun setOrientation(orientation: Orientation) {
        logger.debug("Orientation changed: {}", orientation)
        menuButton.rotate(orientation.degree)
        toggleView.rotate(orientation.degree)
        flashView.rotate(orientation.degree)
        colorDetailsWidget.rotate(orientation.degree)
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

    private fun updateMode(single: Boolean) {
        if (single) {
            colorDetailsWidget.visible(true)
            pointView.visible(true)
            colorRecycler.visible(false)
        } else {
            colorDetailsWidget.visible(false)
            pointView.visible(false)
            colorRecycler.visible(true)
        }
    }

    private enum class Orientation(val degree: Int) {
        PORTRAIT(0),
        LANDSCAPE(90)
    }
}
