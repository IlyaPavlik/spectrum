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
import com.bumptech.glide.request.RequestOptions;

import java.util.Arrays;
import java.util.Collections;

import javax.inject.Inject;

import butterknife.BindView;
import ru.magflayer.spectrum.R;
import ru.magflayer.spectrum.domain.injection.InjectorManager;
import ru.magflayer.spectrum.domain.model.ColorPicture;
import ru.magflayer.spectrum.presentation.common.model.ToolbarAppearance;
import ru.magflayer.spectrum.presentation.common.android.BaseFragment;
import ru.magflayer.spectrum.presentation.common.BasePresenter;
import ru.magflayer.spectrum.presentation.common.android.BaseRecyclerAdapter;
import ru.magflayer.spectrum.presentation.common.android.BaseViewHolder;
import ru.magflayer.spectrum.presentation.common.Layout;
import ru.magflayer.spectrum.presentation.widget.ColorInfoWidget;
import ru.magflayer.spectrum.presentation.widget.TextSeekBarView;
import ru.magflayer.spectrum.presentation.common.utils.Base64Utils;
import ru.magflayer.spectrum.presentation.common.utils.ColorUtils;

@Layout(R.layout.fragment_history_details)
public class HistoryDetailsFragment extends BaseFragment implements HistoryDetailsView {

    private static final String COLOR_PICTURE_ID = "COLOR_PICTURE_ID";
    private static final String COLOR_QUANTITY = "COLOR_QUANTITY";

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

    private ColorAdapter adapter;
    private int colorQuantity;

    public static HistoryDetailsFragment newInstance(final long id, final int colorQuantity) {

        Bundle args = new Bundle();
        args.putLong(COLOR_PICTURE_ID, id);
        args.putInt(COLOR_QUANTITY, colorQuantity);

        HistoryDetailsFragment fragment = new HistoryDetailsFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void handleArguments(@NonNull final Bundle arguments) {
        colorQuantity = arguments.getInt(COLOR_QUANTITY, colorQuantity);
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
        return ToolbarAppearance.builder()
                .visible(ToolbarAppearance.Visibility.VISIBLE)
                .title(getResources().getQuantityString(R.plurals.history_details_title, colorQuantity))
                .build();
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        adapter = new ColorAdapter(getContext());
        adapter.setItemSelectListener(position -> {
            Integer color = adapter.getItem(position);
            if (color != null) {
                presenter.handleSelectedColor(color);
            }
        });
        colorsRecycler.setAdapter(adapter);
        colorsRecycler.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getArguments() != null) {
            presenter.loadPicture(getArguments().getLong(COLOR_PICTURE_ID));
        }
        presenter.sendAnalytics();
    }

    @Override
    public void showPicture(final ColorPicture colorPicture) {
        Glide.with(this).load(Base64Utils.base46ToBytes(colorPicture.getPictureBase64()))
                .apply(RequestOptions.fitCenterTransform())
                .into(pictureView);

        adapter.setData(colorPicture.getRgbColors());
    }

    @Override
    public void colorLoaded() {
        adapter.select(0);
    }

    @Override
    public void showColorName(String name) {
        titleView.setText(name);
    }

    @Override
    public void showRgb(final int color) {
        Context context = getContext();
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);

        int rgbMaxValue = 255;


        if (context != null) redView.setColor(ContextCompat.getColor(context, R.color.red));
        redView.setValue(red);
        redView.setMaxValue(rgbMaxValue);
        redView.setText(String.valueOf(red));

        if (context != null) greenView.setColor(ContextCompat.getColor(context, R.color.green));
        greenView.setValue(green);
        greenView.setMaxValue(rgbMaxValue);
        greenView.setText(String.valueOf(green));

        if (context != null) blueView.setColor(ContextCompat.getColor(context, R.color.blue));
        blueView.setValue(blue);
        blueView.setMaxValue(rgbMaxValue);
        blueView.setText(String.valueOf(blue));
    }

    @Override
    public void showRyb(final int color) {
        final int[] ryb = ColorUtils.dec2Ryb(color);

        rybInfo.setTitle(getString(R.string.history_details_ryb));
        rybInfo.setParams(Arrays.asList(
                getString(R.string.ryb_r_format, ryb[0]),
                getString(R.string.ryb_y_format, ryb[1]),
                getString(R.string.ryb_b_format, ryb[2])
        ));
    }

    @Override
    @SuppressLint("DefaultLocale")
    public void showCmyk(final int color) {
        Context context = getContext();
        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);

        float[] cmyk = ColorUtils.rgb2Cmyk(red, green, blue);
        int cmykMaxValue = 100;

        if (context != null) cyanView.setColor(ContextCompat.getColor(context, R.color.cyan));
        cyanView.setValue((int) (cmyk[0] * 100));
        cyanView.setMaxValue(cmykMaxValue);
        cyanView.setText(String.format("%.2f", cmyk[0]));

        if (context != null)
            magentaView.setColor(ContextCompat.getColor(context, R.color.magenta));
        magentaView.setValue((int) (cmyk[1] * 100));
        magentaView.setMaxValue(cmykMaxValue);
        magentaView.setText(String.format("%.2f", cmyk[1]));

        if (context != null)
            yellowView.setColor(ContextCompat.getColor(context, R.color.yellow));
        yellowView.setValue((int) (cmyk[2] * 100));
        yellowView.setMaxValue(cmykMaxValue);
        yellowView.setText(String.format("%.2f", cmyk[2]));

        if (context != null) keyView.setColor(ContextCompat.getColor(context, R.color.key));
        keyView.setValue((int) (cmyk[3] * 100));
        keyView.setMaxValue(cmykMaxValue);
        keyView.setText(String.format("%.2f", cmyk[3]));
    }

    @Override
    public void showHsv(final int color) {
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

    @Override
    public void showXyz(final int color) {
        final double[] xyz = ColorUtils.dec2Xyz(color);

        xyzInfo.setTitle(getString(R.string.history_details_xyz));
        xyzInfo.setParams(Arrays.asList(
                getString(R.string.x_format, xyz[0]),
                getString(R.string.y_format, xyz[1]),
                getString(R.string.z_format, xyz[2])
        ));
    }

    @Override
    public void showLab(final int color) {
        final double[] lab = ColorUtils.dec2Lab(color);

        labInfo.setTitle(getString(R.string.history_details_lab));
        labInfo.setParams(Arrays.asList(
                getString(R.string.l_format, lab[0]),
                getString(R.string.a_format, lab[1]),
                getString(R.string.b_format, lab[2])
        ));
    }

    @Override
    public void showNcs(final int color, final String ncsName) {
        ncsInfo.setTitle(getString(R.string.history_details_ncs));
        ncsInfo.setParams(Collections.singletonList(ncsName));
    }

    static class ColorAdapter extends BaseRecyclerAdapter<ColorAdapter.ColorViewHolder, Integer> {

        ColorAdapter(Context context) {
            super(context);
        }

        @NonNull
        @Override
        public ColorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new ColorViewHolder(inflater.inflate(R.layout.item_history_color, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull ColorViewHolder holder, int position) {
            Integer color = getItem(position);
            if (color != null) {
                holder.colorView.setBackgroundColor(color);
            }

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
