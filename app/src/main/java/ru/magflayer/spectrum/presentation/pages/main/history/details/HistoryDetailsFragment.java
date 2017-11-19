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
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import ru.magflayer.spectrum.R;
import ru.magflayer.spectrum.domain.injection.InjectorManager;
import ru.magflayer.spectrum.domain.model.ColorPicture;
import ru.magflayer.spectrum.domain.model.ToolbarAppearance;
import ru.magflayer.spectrum.presentation.common.BaseFragment;
import ru.magflayer.spectrum.presentation.common.BasePresenter;
import ru.magflayer.spectrum.presentation.common.BaseRecyclerAdapter;
import ru.magflayer.spectrum.presentation.common.BaseViewHolder;
import ru.magflayer.spectrum.presentation.common.Layout;
import ru.magflayer.spectrum.presentation.widget.ColorInfoWidget;
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

    @BindView(R.id.color_info_ryb)
    ColorInfoWidget rybInfo;
    @BindView(R.id.color_info_hsv)
    ColorInfoWidget hsvInfo;
    @BindView(R.id.color_info_xyz)
    ColorInfoWidget xyzInfo;
    @BindView(R.id.color_info_lab)
    ColorInfoWidget labInfo;
    @BindView(R.id.color_info_ncs)
    ColorInfoWidget ncsInfo;

    @Inject
    HistoryDetailsPresenter presenter;

    private ColorPicture colorPicture;
    private ColorAdapter adapter;
    private List<ColorUtils.NcsColor> ncsColors;

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
    public void onResume() {
        super.onResume();
        presenter.sendAnalytics();
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
        initRgb(color);
        initRyb(color);
        initCmyk(color);
        initHsv(color);
        initXyz(color);
        initLab(color);
        initNcs(color);
        presenter.handleColorDetails(color);
    }

    private void initRgb(final int color) {
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
    private void initCmyk(final int color) {
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);

        float[] cmyk = ColorUtils.rgb2Cmyk(red, green, blue);
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

    private void initRyb(final int color) {
        final int[] ryb = ColorUtils.dec2Ryb(color);

        rybInfo.setTitle(getString(R.string.history_details_ryb));
        rybInfo.setParams(Arrays.asList(
                getString(R.string.ryb_r_format, ryb[0]),
                getString(R.string.ryb_y_format, ryb[1]),
                getString(R.string.ryb_b_format, ryb[2])
        ));
    }

    private void initHsv(final int color) {
        final float[] hsv = new float[3];

        Color.colorToHSV(color, hsv);

        int hue = (int) hsv[0];
        int saturation = (int) (hsv[1] * 100);
        int value = (int) (hsv[2] * 100);

        hsvInfo.setTitle(getString(R.string.history_details_hsv));
        hsvInfo.setParams(Arrays.asList(
                getString(R.string.hue_long_format, hue),
                getString(R.string.saturation_long_format, saturation),
                getString(R.string.value_long_format, value)
        ));
    }

    private void initXyz(final int color) {
        final double[] xyz = ColorUtils.dec2Xyz(color);

        xyzInfo.setTitle(getString(R.string.history_details_xyz));
        xyzInfo.setParams(Arrays.asList(
                getString(R.string.x_format, xyz[0]),
                getString(R.string.y_format, xyz[1]),
                getString(R.string.z_format, xyz[2])
        ));
    }

    private void initLab(final int color) {
        final double[] lab = ColorUtils.dec2Lab(color);

        labInfo.setTitle(getString(R.string.history_details_lab));
        labInfo.setParams(Arrays.asList(
                getString(R.string.l_format, lab[0]),
                getString(R.string.a_format, lab[1]),
                getString(R.string.b_format, lab[2])
        ));
    }

    private void initNcs(final int color) {
        if (ncsColors == null) {
            loadNcsColors();
        }

        final String ncsColor = ColorUtils.dec2Ncs(ncsColors, color);
        ncsInfo.setTitle(getString(R.string.history_details_ncs));
        ncsInfo.setParams(Collections.singletonList(ncsColor));
    }

    private void loadNcsColors() {
        String ncsJson = AppUtils.loadJSONFromAsset(getResources().getAssets(), "ncs.json");
        ncsColors = new Gson().fromJson(ncsJson, new TypeToken<List<ColorUtils.NcsColor>>() {
        }.getType());
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
