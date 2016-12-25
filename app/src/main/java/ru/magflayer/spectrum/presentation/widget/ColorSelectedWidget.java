package ru.magflayer.spectrum.presentation.widget;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ru.magflayer.spectrum.R;
import ru.magflayer.spectrum.presentation.common.BaseRecyclerView;
import ru.magflayer.spectrum.presentation.common.BaseViewHolder;
import ru.magflayer.spectrum.utils.ColorUtils;
import ru.magflayer.spectrum.utils.DialogUtils;

@SuppressWarnings("WeakerAccess")
public class ColorSelectedWidget extends RelativeLayout {

    @BindView(R.id.color_recycler)
    RecyclerView colorRecyclerView;
    @BindView(R.id.color_hex)
    TextView hexView;

    @BindView(R.id.red)
    TextView redView;
    @BindView(R.id.green)
    TextView greenView;
    @BindView(R.id.blue)
    TextView blueView;

    @BindView(R.id.hue)
    TextView hueView;
    @BindView(R.id.saturation)
    TextView saturationView;
    @BindView(R.id.value)
    TextView valueView;

    @BindView(R.id.cyan)
    TextView cyanView;
    @BindView(R.id.magenta)
    TextView magentaView;
    @BindView(R.id.yellow)
    TextView yellowView;
    @BindView(R.id.key)
    TextView keyView;

    private ColorAdapter colorAdapter;

    public ColorSelectedWidget(Context context) {
        this(context, null);
    }

    public ColorSelectedWidget(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorSelectedWidget(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    public void showDialog(List<Palette.Swatch> swatches) {
        colorAdapter.setData(swatches);
        colorAdapter.select(0);

        String title = getContext().getString(R.string.history_dialog_title);
        DialogUtils.buildViewDialog(getContext(), title, this, (dialogInterface, i) -> dialogInterface.dismiss()).show();
    }

    @OnClick(R.id.color_hex_container)
    public void onCopyClick() {
        ClipboardManager clipboard = (ClipboardManager) getContext().getSystemService(Context.CLIPBOARD_SERVICE);
        String label = getContext().getString(R.string.history_dialog_copy_label);
        ClipData clip = ClipData.newPlainText(label, hexView.getText());
        clipboard.setPrimaryClip(clip);

        Toast.makeText(getContext(), R.string.history_dialog_copy, Toast.LENGTH_SHORT).show();
    }

    private void init(Context context) {
        View view = inflate(context, R.layout.dialog_history_details, this);
        ButterKnife.bind(this, view);

        colorRecyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        colorAdapter = new ColorAdapter(getContext());
        colorAdapter.setItemSelectListener(position -> {
            Palette.Swatch swatch = colorAdapter.getItem(position);
            selectColor(swatch.getRgb());
        });
        colorRecyclerView.setAdapter(colorAdapter);
    }

    private void selectColor(int color) {
        Context context = getContext();
        hexView.setText(ColorUtils.colorToHex(color));

        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);

        redView.setText(context.getString(R.string.red_format, red));
        greenView.setText(context.getString(R.string.green_format, green));
        blueView.setText(context.getString(R.string.blue_format, blue));

        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        int hue = (int) hsv[0];
        int saturation = (int) (hsv[1] * 100);
        int value = (int) (hsv[2] * 100);

        hueView.setText(context.getString(R.string.hue_format, hue));
        saturationView.setText(context.getString(R.string.saturation_format, saturation));
        valueView.setText(context.getString(R.string.value_format, value));

        float[] cmyk = ColorUtils.rgbToCmyk(red, green, blue);
        int c = (int) (cmyk[0] * 100);
        int m = (int) (cmyk[1] * 100);
        int y = (int) (cmyk[2] * 100);
        int k = (int) (cmyk[3] * 100);

        cyanView.setText(context.getString(R.string.cyan_format, c));
        magentaView.setText(context.getString(R.string.magenta_format, m));
        yellowView.setText(context.getString(R.string.yellow_format, y));
        keyView.setText(context.getString(R.string.key_color_format, k));
    }

    static class ColorAdapter extends BaseRecyclerView<ColorAdapter.ColorViewHolder, Palette.Swatch> {

        ColorAdapter(Context context) {
            super(context);
        }

        @Override
        public ColorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            return new ColorViewHolder(inflater.inflate(R.layout.item_history_color, parent, false));
        }

        @Override
        public void onBindViewHolder(ColorViewHolder holder, int position) {
            Palette.Swatch swatch = getItem(position);
            holder.colorView.setBackgroundColor(swatch.getRgb());

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
