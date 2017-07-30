package ru.magflayer.spectrum.presentation.pages.main.camera;

import android.content.Context;
import android.support.v7.graphics.Palette;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import butterknife.BindView;
import ru.magflayer.spectrum.R;
import ru.magflayer.spectrum.presentation.common.BaseRecyclerAdapter;
import ru.magflayer.spectrum.presentation.common.BaseViewHolder;
import ru.magflayer.spectrum.utils.ViewUtils;

public class ColorCameraAdapter extends BaseRecyclerAdapter<ColorCameraAdapter.ColorViewHolder, Palette.Swatch> {

    private int rotateDegree;

    public ColorCameraAdapter(Context context) {
        super(context);
    }

    @Override
    public ColorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = inflater.inflate(R.layout.item_color_camera, parent, false);
        return new ColorViewHolder(view, rotateDegree);
    }

    @Override
    public void onBindViewHolder(ColorViewHolder holder, int position) {
        Palette.Swatch swatch = getItem(position);
        int color = swatch.getRgb();

        holder.colorContainer.setBackgroundColor(color);
        holder.colorTextView.setTextColor(swatch.getTitleTextColor());

        holder.colorTextView.setText(String.format("#%06X", (0xFFFFFF & color)));

        ViewUtils.rotateView(holder.itemView, rotateDegree);
    }

    public void updateRotateDegree(final int rotateDegree) {
        this.rotateDegree = rotateDegree;
        notifyDataSetChanged();
    }

    static class ColorViewHolder extends BaseViewHolder {

        @BindView(R.id.color_container)
        ViewGroup colorContainer;
        @BindView(R.id.color_text)
        TextView colorTextView;

        ColorViewHolder(View itemView, int rotateDegree) {
            super(itemView);
            itemView.setRotation(rotateDegree);
        }
    }
}
