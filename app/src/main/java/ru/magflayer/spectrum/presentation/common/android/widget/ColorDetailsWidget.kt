package ru.magflayer.spectrum.presentation.common.android.widget

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.LinearLayout
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import ru.magflayer.spectrum.R
import ru.magflayer.spectrum.databinding.WidgetColorDetailsBinding
import ru.magflayer.spectrum.presentation.common.extension.rotate
import ru.magflayer.spectrum.presentation.common.helper.ColorHelper

class ColorDetailsWidget @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = -1
) : LinearLayout(context, attrs, defStyleAttr) {

    private val viewBinding = WidgetColorDetailsBinding.inflate(
        LayoutInflater.from(context),
        this,
        true
    )

    fun setColor(@ColorInt color: Int) = with(viewBinding) {
        this.color.setBackgroundColor(color)
        this.colorId.text = ColorHelper.dec2Hex(color)

        initRgbColor(color)
        initHsvColor(color)
    }

    fun setColorName(colorName: String) = with(viewBinding) {
        this.colorName.text = colorName
    }

    fun rotate(toDegree: Int) = with(viewBinding) {
        colorContainer.rotate(toDegree)
        colorName.rotate(toDegree)
        colorRgb.root.rotate(toDegree)
        colorHsv.root.rotate(toDegree)
    }

    private fun initRgbColor(color: Int) = with(viewBinding) {
        val maxValue = 255
        val red = Color.red(color)
        val green = Color.green(color)
        val blue = Color.blue(color)

        colorRgb.red.apply {
            setText(red.toString())
            setMaxValue(maxValue)
            setValue(red)
            setColor(ContextCompat.getColor(context, R.color.red))
        }

        colorRgb.green.apply {
            setText(green.toString())
            setMaxValue(maxValue)
            setValue(green)
            setColor(ContextCompat.getColor(context, R.color.green))
        }

        colorRgb.blue.apply {
            setText(blue.toString())
            setMaxValue(maxValue)
            setValue(blue)
            setColor(ContextCompat.getColor(context, R.color.blue))
        }
    }

    private fun initHsvColor(color: Int) = with(viewBinding) {
        val context = context

        val hsv = FloatArray(3)
        Color.colorToHSV(color, hsv)
        val hue = hsv[0].toInt()
        val saturation = (hsv[1] * 100).toInt()
        val value = (hsv[2] * 100).toInt()

        colorHsv.hue.text = context.getString(R.string.hue_format, hue)
        colorHsv.saturation.text = context.getString(R.string.saturation_format, saturation)
        colorHsv.value.text = context.getString(R.string.value_format, value)
    }
}
