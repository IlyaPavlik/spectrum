package ru.magflayer.colorpointer.presentation.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.v7.widget.AppCompatSeekBar;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.magflayer.colorpointer.R;
import ru.magflayer.colorpointer.utils.DrawableUtils;

public class TextSeekBarView extends FrameLayout {

    @BindView(R.id.seek_bar)
    protected AppCompatSeekBar seekBar;
    @BindView(R.id.text)
    protected TextView textView;

    public TextSeekBarView(Context context) {
        super(context);
        init(context);
    }

    public TextSeekBarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public TextSeekBarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public TextSeekBarView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
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
    }
}
