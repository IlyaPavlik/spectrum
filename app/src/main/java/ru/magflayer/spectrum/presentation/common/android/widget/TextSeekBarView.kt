package ru.magflayer.spectrum.presentation.common.android.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.LayerDrawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.annotation.ColorInt
import ru.magflayer.spectrum.databinding.WidgetTextSeekBarBinding
import ru.magflayer.spectrum.presentation.common.extension.setColor

@SuppressLint("ClickableViewAccessibility")
class TextSeekBarView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = -1
) : FrameLayout(context, attrs, defStyleAttr) {

    private val viewBinding by lazy {
        WidgetTextSeekBarBinding.inflate(
            LayoutInflater.from(context),
            this,
            true
        )
    }

    init {
        viewBinding.seekBar.setOnTouchListener { _, _ -> true }
    }

    fun setValue(value: Int) {
        viewBinding.seekBar.progress = value
    }

    fun setMaxValue(maxValue: Int) {
        viewBinding.seekBar.max = maxValue
    }

    fun setColor(@ColorInt color: Int) {
        val background = viewBinding.seekBar.progressDrawable
        val layerDrawable = background as LayerDrawable
        val drawable = layerDrawable.findDrawableByLayerId(android.R.id.progress)
        drawable.setColor(color)
    }

    fun setText(text: String) {
        viewBinding.text.text = text
    }
}
