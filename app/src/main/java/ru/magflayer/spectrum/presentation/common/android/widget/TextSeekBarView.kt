package ru.magflayer.spectrum.presentation.common.android.widget

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.drawable.LayerDrawable
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.annotation.ColorInt
import androidx.appcompat.widget.AppCompatSeekBar
import butterknife.BindView
import butterknife.ButterKnife
import ru.magflayer.spectrum.R
import ru.magflayer.spectrum.presentation.common.extension.setColor

@SuppressLint("ClickableViewAccessibility")
class TextSeekBarView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = -1) : FrameLayout(context, attrs, defStyleAttr) {

    @BindView(R.id.seek_bar)
    lateinit var seekBar: AppCompatSeekBar

    @BindView(R.id.text)
    lateinit var textView: TextView

    init {
        val view = View.inflate(context, R.layout.widget_text_seek_bar, this)
        ButterKnife.bind(this, view)
        seekBar.setOnTouchListener { _, _ -> true }
    }

    fun setValue(value: Int) {
        seekBar.progress = value
    }

    fun setMaxValue(maxValue: Int) {
        seekBar.max = maxValue
    }

    fun setColor(@ColorInt color: Int) {
        val background = seekBar.progressDrawable
        val layerDrawable = background as LayerDrawable
        val drawable = layerDrawable.findDrawableByLayerId(android.R.id.progress)
        drawable.setColor(color)
    }

    fun setText(text: String) {
        textView.text = text
    }
}
