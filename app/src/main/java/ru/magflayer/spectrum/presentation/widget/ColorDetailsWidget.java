package ru.magflayer.spectrum.presentation.widget;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.ColorInt;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.magflayer.spectrum.R;
import ru.magflayer.spectrum.presentation.common.utils.ColorUtils;
import ru.magflayer.spectrum.presentation.common.utils.ViewUtils;

public class ColorDetailsWidget extends LinearLayout {

    @BindView(R.id.color_container)
    protected View colorContainer;
    @BindView(R.id.color)
    protected View colorView;
    @BindView(R.id.color_id)
    protected TextView colorHexView;
    @BindView(R.id.color_name)
    protected TextView colorNameView;

    @BindView(R.id.color_rgb)
    protected View colorRGB;
    @BindView(R.id.red)
    protected TextSeekBarView redView;
    @BindView(R.id.green)
    protected TextSeekBarView greenView;
    @BindView(R.id.blue)
    protected TextSeekBarView blueView;

    @BindView(R.id.color_hsv)
    protected View colorHSV;
    @BindView(R.id.hue)
    protected TextView hueView;
    @BindView(R.id.saturation)
    protected TextView saturationView;
    @BindView(R.id.value)
    protected TextView valueView;

    public ColorDetailsWidget(Context context) {
        this(context, null);
    }

    public ColorDetailsWidget(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public ColorDetailsWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void setColor(@ColorInt int color) {
        colorView.setBackgroundColor(color);
        colorHexView.setText(ColorUtils.dec2Hex(color));

        initRgbColor(color);
        initHsvColor(color);
    }

    public void setColorName(String colorName) {
        colorNameView.setText(colorName);
    }

    public void rotate(final int toDegree) {
        ViewUtils.rotateView(colorContainer, toDegree);
        ViewUtils.rotateView(colorNameView, toDegree);
        ViewUtils.rotateView(colorRGB, toDegree);
        ViewUtils.rotateView(colorHSV, toDegree);
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
        redView.setColor(ContextCompat.getColor(getContext(), R.color.red));

        greenView.setText(String.valueOf(green));
        greenView.setMaxValue(maxValue);
        greenView.setValue(green);
        greenView.setColor(ContextCompat.getColor(getContext(), R.color.green));

        blueView.setText(String.valueOf(blue));
        blueView.setMaxValue(maxValue);
        blueView.setValue(blue);
        blueView.setColor(ContextCompat.getColor(getContext(), R.color.blue));
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
