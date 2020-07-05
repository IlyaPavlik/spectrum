package ru.magflayer.spectrum.presentation.common.android.widget

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import butterknife.BindView
import butterknife.ButterKnife
import ru.magflayer.spectrum.R
import ru.magflayer.spectrum.presentation.common.extension.rotate
import ru.magflayer.spectrum.presentation.common.helper.ColorHelper

class ColorDetailsWidget @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = -1) : LinearLayout(context, attrs, defStyleAttr) {

    @BindView(R.id.color_container)
    lateinit var colorContainer: View
    @BindView(R.id.color)
    lateinit var colorView: View
    @BindView(R.id.color_id)
    lateinit var colorHexView: TextView
    @BindView(R.id.color_name)
    lateinit var colorNameView: TextView

    @BindView(R.id.color_rgb)
    lateinit var colorRGB: View
    @BindView(R.id.red)
    lateinit var redView: TextSeekBarView
    @BindView(R.id.green)
    lateinit var greenView: TextSeekBarView
    @BindView(R.id.blue)
    lateinit var blueView: TextSeekBarView

    @BindView(R.id.color_hsv)
    lateinit var colorHSV: View
    @BindView(R.id.hue)
    lateinit var hueView: TextView
    @BindView(R.id.saturation)
    lateinit var saturationView: TextView
    @BindView(R.id.value)
    lateinit var valueView: TextView

    init {
        init(context)
    }

    fun setColor(@ColorInt color: Int) {
        colorView.setBackgroundColor(color)
        colorHexView.text = ColorHelper.dec2Hex(color)

        initRgbColor(color)
        initHsvColor(color)
    }

    fun setColorName(colorName: String) {
        colorNameView.text = colorName
    }

    fun rotate(toDegree: Int) {
        colorContainer.rotate(toDegree)
        colorNameView.rotate(toDegree)
        colorRGB.rotate(toDegree)
        colorHSV.rotate(toDegree)
    }

    private fun init(context: Context) {
        val view = View.inflate(context, R.layout.widget_color_details, this)
        ButterKnife.bind(this, view)
    }

    private fun initRgbColor(color: Int) {
        val maxValue = 255
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)

        redView.setText(red.toString())
        redView.setMaxValue(maxValue)
        redView.setValue(red)
        redView.setColor(ContextCompat.getColor(context, R.color.red))

        greenView.setText(green.toString())
        greenView.setMaxValue(maxValue)
        greenView.setValue(green)
        greenView.setColor(ContextCompat.getColor(context, R.color.green))

        blueView.setText(blue.toString())
        blueView.setMaxValue(maxValue)
        blueView.setValue(blue)
        blueView.setColor(ContextCompat.getColor(context, R.color.blue))
    }

    private fun initHsvColor(color: Int) {
        val context = context

        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        val hue = hsv[0].toInt()
        val saturation = (hsv[1] * 100).toInt()
        val value = (hsv[2] * 100).toInt()

        hueView.text = context.getString(R.string.hue_format, hue)
        saturationView.text = context.getString(R.string.saturation_format, saturation)
        valueView.text = context.getString(R.string.value_format, value)
    }
}
