package ru.magflayer.spectrum.presentation.pages.main.history;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.magflayer.spectrum.R;
import ru.magflayer.spectrum.domain.model.ColorPicture;
import ru.magflayer.spectrum.domain.model.ToolbarAppearance;
import ru.magflayer.spectrum.injection.InjectorManager;
import ru.magflayer.spectrum.presentation.common.BaseFragment;
import ru.magflayer.spectrum.presentation.common.BasePresenter;
import ru.magflayer.spectrum.presentation.common.BaseRecyclerView;
import ru.magflayer.spectrum.presentation.common.Layout;
import ru.magflayer.spectrum.presentation.widget.TextSeekBarView;
import ru.magflayer.spectrum.utils.ColorUtils;
import ru.magflayer.spectrum.utils.DialogUtils;

@Layout(id = R.layout.fragment_history)
public class HistoryFragment extends BaseFragment implements HistoryView {

    @BindView(R.id.history_recycler)
    protected RecyclerView historyRecycler;

    @Inject
    protected HistoryPresenter presenter;

    private HistoryAdapter adapter;

    public static HistoryFragment newInstance() {
        return new HistoryFragment();
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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        adapter = new HistoryAdapter();
        historyRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        historyRecycler.setAdapter(adapter);
        adapter.setItemClickListener(position -> {
            ColorPicture colorPicture = adapter.getItem(position);
            openHistoryDetailsDialog(colorPicture.getSwatches().get(0).getRgb());
        });
    }

    @Override
    public void onResume() {
        super.onResume();

        presenter.loadHistory();
    }

    @Override
    public void showHistory(List<ColorPicture> history) {
        adapter.setData(history);
    }

    @Override
    public ToolbarAppearance getToolbarAppearance() {
        return ToolbarAppearance.builder()
                .visible(ToolbarAppearance.Visibility.VISIBLE)
                .title(getString(R.string.history_toolbar_title))
                .build();
    }

    private void openHistoryDetailsDialog(int color) {
        View view = View.inflate(getContext(), R.layout.dialog_history_details, null);
        HistoryDetailsViewHolder viewHolder = new HistoryDetailsViewHolder(view);

        viewHolder.colorView.setBackgroundColor(color);
        viewHolder.hexView.setText(ColorUtils.colorToHex(color));

        int red = Color.red(color);
        int green = Color.green(color);
        int blue = Color.blue(color);

        viewHolder.redView.setText(getString(R.string.red_format, red));
        viewHolder.greenView.setText(getString(R.string.green_format, green));
        viewHolder.blueView.setText(getString(R.string.blue_format, blue));

        Context context = getContext();
        float[] hsv = new float[3];
        Color.colorToHSV(color, hsv);
        int hue = (int) hsv[0];
        int saturation = (int) (hsv[1] * 100);
        int value = (int) (hsv[2] * 100);

        viewHolder.hueView.setText(context.getString(R.string.hue_format, hue));
        viewHolder.saturationView.setText(context.getString(R.string.saturation_format, saturation));
        viewHolder.valueView.setText(context.getString(R.string.value_format, value));

        float[] cmyk = ColorUtils.rgbToCmyk(new float[]{red, green, blue});
        int c = (int) (cmyk[0] * 100);
        int m = (int) (cmyk[1] * 100);
        int y = (int) (cmyk[2] * 100);
        int k = (int) (cmyk[3] * 100);

        viewHolder.cyanView.setText(getString(R.string.cyan_format, c));
        viewHolder.magentaView.setText(getString(R.string.magenta_format, m));
        viewHolder.yellowView.setText(getString(R.string.yellow_format, y));
        viewHolder.keyView.setText(getString(R.string.key_color_format, k));

        DialogUtils.buildViewDialog(getContext(), "Цвет", view, (dialogInterface, i) -> dialogInterface.dismiss()).show();
    }

    static class HistoryDetailsViewHolder {

        @BindView(R.id.color)
        ImageView colorView;
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

        HistoryDetailsViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
