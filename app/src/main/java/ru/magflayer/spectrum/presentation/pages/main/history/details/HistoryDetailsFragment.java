package ru.magflayer.spectrum.presentation.pages.main.history.details;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import ru.magflayer.spectrum.R;
import ru.magflayer.spectrum.domain.model.ColorPicture;
import ru.magflayer.spectrum.domain.model.ToolbarAppearance;
import ru.magflayer.spectrum.injection.InjectorManager;
import ru.magflayer.spectrum.presentation.common.BaseFragment;
import ru.magflayer.spectrum.presentation.common.BasePresenter;
import ru.magflayer.spectrum.presentation.common.BaseRecyclerAdapter;
import ru.magflayer.spectrum.presentation.common.BaseViewHolder;
import ru.magflayer.spectrum.presentation.common.Layout;
import ru.magflayer.spectrum.presentation.widget.TextSeekBarView;
import ru.magflayer.spectrum.utils.AppUtils;
import ru.magflayer.spectrum.utils.Base64Utils;
import ru.magflayer.spectrum.utils.ColorUtils;

@Layout(R.layout.fragment_history_details)
public class HistoryDetailsFragment extends BaseFragment implements HistoryDetailsView {

    private static final String COLOR_PICTURE = "COLOR_PICTURE";

    @BindView(R.id.picture)
    ImageView pictureView;

    @BindView(R.id.colors)
    RecyclerView colorsRecycler;

    @BindView(R.id.title)
    TextView titleView;

    @BindView(R.id.red)
    TextSeekBarView redView;
    @BindView(R.id.green)
    TextSeekBarView greenView;
    @BindView(R.id.blue)
    TextSeekBarView blueView;

    @BindView(R.id.cyan)
    TextSeekBarView cyanView;
    @BindView(R.id.magenta)
    TextSeekBarView magentaView;
    @BindView(R.id.yellow)
    TextSeekBarView yellowView;
    @BindView(R.id.key)
    TextSeekBarView keyView;

    @BindView(R.id.hue)
    TextView hueView;
    @BindView(R.id.saturation)
    TextView saturationView;
    @BindView(R.id.value)
    TextView valueView;

    @BindView(R.id.color_x)
    TextView xView;
    @BindView(R.id.color_y)
    TextView yView;
    @BindView(R.id.color_z)
    TextView zView;

    @Inject
    HistoryDetailsPresenter presenter;

    private ColorPicture colorPicture;
    private ColorAdapter adapter;

    public static HistoryDetailsFragment newInstance(final ColorPicture colorPicture) {

        Bundle args = new Bundle();
        args.putParcelable(COLOR_PICTURE, colorPicture);

        HistoryDetailsFragment fragment = new HistoryDetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void handleArguments(@NonNull Bundle arguments) {
        colorPicture = arguments.getParcelable(COLOR_PICTURE);
    }

    @NonNull
    @Override
    protected BasePresenter getPresenter() {
        return presenter;
    }

    @Override
    protected void inject() {
        InjectorManager.getAppComponent().inject(this);
    }

    @Override
    public ToolbarAppearance getToolbarAppearance() {
        final List<Integer> colors = colorPicture.getRgbColors();
        return ToolbarAppearance.builder()
                .visible(ToolbarAppearance.Visibility.VISIBLE)
                .title(getResources().getQuantityString(R.plurals.history_details_title, colors.size()))
                .build();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Glide.with(this).load(Base64Utils.base46ToBytes(colorPicture.getPictureBase64()))
                .asBitmap()
                .fitCenter()
                .into(pictureView);

        adapter = new ColorAdapter(getContext());
        adapter.setData(colorPicture.getRgbColors());
        adapter.setItemSelectListener(position -> selectColor(adapter.getItem(position)));
        colorsRecycler.setAdapter(adapter);
        colorsRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        String colorsJson = AppUtils.loadJSONFromAsset(getResources().getAssets(), "colors.json");
        presenter.loadColors(colorsJson);
    }

    @Override
    public void colorLoaded() {
        adapter.select(0);
    }

    @Override
    public void showColorName(String name) {
        titleView.setText(name);
    }

    @SuppressLint("DefaultLocale")
    private void selectColor(final int color) {
        initRGB(color);
        initCMYK(color);
        initHSV(color);
        initXYZ(color);
        presenter.handleColorDetails(color);
    }

    private void initRGB(final int color) {
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);

        int rgbMaxValue = 255;

        redView.setColor(ContextCompat.getColor(getContext(), R.color.red));
        redView.setValue(red);
        redView.setMaxValue(rgbMaxValue);
        redView.setText(String.valueOf(red));

        greenView.setColor(ContextCompat.getColor(getContext(), R.color.green));
        greenView.setValue(green);
        greenView.setMaxValue(rgbMaxValue);
        greenView.setText(String.valueOf(green));

        blueView.setColor(ContextCompat.getColor(getContext(), R.color.blue));
        blueView.setValue(blue);
        blueView.setMaxValue(rgbMaxValue);
        blueView.setText(String.valueOf(blue));
    }

    @SuppressLint("DefaultLocale")
    private void initCMYK(final int color) {
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);

        float[] cmyk = ColorUtils.rgbToCmyk(red, green, blue);
        int cmykMaxValue = 100;

        cyanView.setColor(ContextCompat.getColor(getContext(), R.color.cyan));
        cyanView.setValue((int) (cmyk[0] * 100));
        cyanView.setMaxValue(cmykMaxValue);
        cyanView.setText(String.format("%.2f", cmyk[0]));

        magentaView.setColor(ContextCompat.getColor(getContext(), R.color.magenta));
        magentaView.setValue((int) (cmyk[1] * 100));
        magentaView.setMaxValue(cmykMaxValue);
        magentaView.setText(String.format("%.2f", cmyk[1]));

        yellowView.setColor(ContextCompat.getColor(getContext(), R.color.yellow));
        yellowView.setValue((int) (cmyk[2] * 100));
        yellowView.setMaxValue(cmykMaxValue);
        yellowView.setText(String.format("%.2f", cmyk[2]));

        keyView.setColor(ContextCompat.getColor(getContext(), R.color.key));
        keyView.setValue((int) (cmyk[3] * 100));
        keyView.setMaxValue(cmykMaxValue);
        keyView.setText(String.format("%.2f", cmyk[3]));
    }

    private void initHSV(final int color) {
        final float[] hsv = new float[3];

        Color.colorToHSV(color, hsv);

        int hue = (int) hsv[0];
        int saturation = (int) (hsv[1] * 100);
        int value = (int) (hsv[2] * 100);

        hueView.setText(getString(R.string.hue_long_format, hue));
        saturationView.setText(getString(R.string.saturation_long_format, saturation));
        valueView.setText(getString(R.string.value_long_format, value));
    }

    private void initXYZ(final int color) {
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);

        final double[] xyz = ColorUtils.rgbToXYZ(red, green, blue);

        xView.setText(getString(R.string.x_format, xyz[0]));
        yView.setText(getString(R.string.y_format, xyz[1]));
        zView.setText(getString(R.string.z_format, xyz[2]));
    }

    static class ColorAdapter extends BaseRecyclerAdapter<ColorAdapter.ColorViewHolder, Integer> {

        ColorAdapter(Context context) {
            super(context);
        }

        @Override
        public ColorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ColorViewHolder(inflater.inflate(R.layout.item_history_color, parent, false));
        }

        @Override
        public void onBindViewHolder(ColorViewHolder holder, int position) {
            Integer color = getItem(position);
            holder.colorView.setBackgroundColor(color);

            if (position == getSelectedPosition()) {
                holder.colorView.setImageResource(R.drawable.rectangle_color_selected);
            } else {
                holder.colorView.setImageBitmap(null);
            }
        }

        @Override
        public void select(int position) {
            super.select(position);
            notifyDataSetChanged();
        }

        static class ColorViewHolder extends BaseViewHolder {

            @BindView(R.id.color)
            ImageView colorView;

            ColorViewHolder(View itemView) {
                super(itemView);
            }
        }
    }
}
