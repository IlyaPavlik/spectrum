package ru.magflayer.spectrum.presentation.widget;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.support.annotation.ColorInt;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.magflayer.spectrum.R;
import ru.magflayer.spectrum.utils.DrawableUtils;

public class TextSeekBarView extends FrameLayout {

    @BindView(R.id.seek_bar)
    protected AppCompatSeekBar seekBar;
    @BindView(R.id.text)
    protected TextView textView;

    public TextSeekBarView(Context context) {
        this(context, null);
    }

    public TextSeekBarView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public TextSeekBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setValue(int value) {
        seekBar.setProgress(value);
    }

    public void setMaxValue(int maxValue) {
        seekBar.setMax(maxValue);
    }

    public void setColor(@ColorInt int color) {
        Drawable background = seekBar.getProgressDrawable();
        LayerDrawable layerDrawable = (LayerDrawable) background;
        Drawable drawable = layerDrawable.findDrawableByLayerId(android.R.id.progress);
        DrawableUtils.setColor(drawable, color);
    }

    public void setText(String text) {
        textView.setText(text);
    }

    private void init(Context context) {
        View view = View.inflate(context, R.layout.widget_text_seek_bar, this);
        ButterKnife.bind(this, view);
        seekBar.setOnTouchListener((v, event) -> true);
    }
}
