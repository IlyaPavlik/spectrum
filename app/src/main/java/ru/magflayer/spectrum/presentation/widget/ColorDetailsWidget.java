package ru.magflayer.spectrum.presentation.widget;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.magflayer.spectrum.R;

public class ColorDetailsWidget extends LinearLayout {

    @BindView(R.id.color)
    protected View colorView;
    @BindView(R.id.color_id)
    protected TextView colorHexView;
    @BindView(R.id.color_name)
    protected TextView colorNameView;

    @BindView(R.id.red)
    protected TextSeekBarView redView;
    @BindView(R.id.green)
    protected TextSeekBarView greenView;
    @BindView(R.id.blue)
    protected TextSeekBarView blueView;

    @BindView(R.id.hue)
    protected TextView hueView;
    @BindView(R.id.saturation)
    protected TextView saturationView;
    @BindView(R.id.value)
    protected TextView valueView;

    public ColorDetailsWidget(Context context) {
        super(context);
        init(context);
    }

    public ColorDetailsWidget(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ColorDetailsWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public ColorDetailsWidget(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    public void setColor(@ColorInt int color) {
        colorView.setBackgroundColor(color);
        colorHexView.setText(String.format("#%06X", (0xFFFFFF & color)));

        initRgbColor(color);
        initHsvColor(color);
    }

    public void setColorName(String colorName) {
        colorNameView.setText(colorName);
    }

    private void init(Context context) {
        View view = View.inflate(context, R.layout.widget_color_details, this);
        ButterKnife.bind(this, view);
    }

    private void initRgbColor(int color) {
        int maxValue = 255;
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);

        redView.setText(String.valueOf(red));
        redView.setMaxValue(maxValue);
        redView.setValue(red);
        redView.setColor(Color.RED);

        greenView.setText(String.valueOf(green));
        greenView.setMaxValue(maxValue);
        greenView.setValue(green);
        greenView.setColor(Color.GREEN);

        blueView.setText(String.valueOf(blue));
        blueView.setMaxValue(maxValue);
        blueView.setValue(blue);
        blueView.setColor(Color.BLUE);
    }

    private void initHsvColor(int color) {
        Context context = getContext();

        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        int hue = (int) hsv[0];
        int saturation = (int) (hsv[1] * 100);
        int value = (int) (hsv[2] * 100);

        hueView.setText(context.getString(R.string.hue_format, hue));
        saturationView.setText(context.getString(R.string.saturation_format, saturation));
        valueView.setText(context.getString(R.string.value_format, value));
    }

}
