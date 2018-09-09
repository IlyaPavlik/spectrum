package ru.magflayer.spectrum.presentation.pages.main.camera;

import android.content.Context;
import android.support.v7.graphics.Palette;
import android.view.View;
import android.view.ViewGroup;

import butterknife.BindView;
import ru.magflayer.spectrum.R;
import ru.magflayer.spectrum.presentation.common.android.BaseRecyclerAdapter;
import ru.magflayer.spectrum.presentation.common.android.BaseViewHolder;

public class ColorCameraAdapter extends BaseRecyclerAdapter<ColorCameraAdapter.ColorViewHolder, Palette.Swatch> {

    public ColorCameraAdapter(Context context) {
        super(context);
    }

    @Override
    public ColorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = inflater.inflate(R.layout.item_color_camera, parent, false);
        return new ColorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ColorViewHolder holder, int position) {
        Palette.Swatch swatch = getItem(position);
        int color = swatch.getRgb();

        holder.colorContainer.setBackgroundColor(color);
    }

    static class ColorViewHolder extends BaseViewHolder {

        @BindView(R.id.color_container)
        ViewGroup colorContainer;

        ColorViewHolder(View itemView) {
            super(itemView);
        }
    }
}
