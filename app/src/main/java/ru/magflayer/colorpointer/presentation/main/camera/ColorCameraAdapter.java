package ru.magflayer.colorpointer.presentation.main.camera;

import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.magflayer.colorpointer.R;
import ru.magflayer.colorpointer.utils.ColorUtils;

public class ColorCameraAdapter extends RecyclerView.Adapter<ColorCameraAdapter.ColorViewHolder> {

    private List<Palette.Swatch> colors = new ArrayList<>();

    @Override
    public ColorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ColorViewHolder(View.inflate(parent.getContext(), R.layout.item_color_camera, null));
    }

    public void setColors(List<Palette.Swatch> colors) {
        if (colors != null) {
            this.colors = new ArrayList<>(colors);
            Collections.sort(this.colors, new Comparator<Palette.Swatch>() {
                @Override
                public int compare(Palette.Swatch lhs, Palette.Swatch rhs) {
                    if (lhs.getRgb() < rhs.getRgb()) {
                        return -1;
                    }

                    if (lhs.getRgb() > rhs.getRgb()) {
                        return 1;
                    }
                    return 0;
                }
            });
            notifyDataSetChanged();
        }
    }

    @Override
    public void onBindViewHolder(ColorViewHolder holder, int position) {
        Palette.Swatch swatch = colors.get(position);
        int color = swatch.getRgb();
        int inverseColor = ColorUtils.inverseColor(color);

        holder.colorContainer.setBackgroundColor(color);
        holder.colorTextView.setTextColor(inverseColor);

        holder.colorTextView.setText(String.format("#%06X", (0xFFFFFF & color)));
    }

    @Override
    public int getItemCount() {
        return colors.size();
    }

    class ColorViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.color_container)
        ViewGroup colorContainer;
        @BindView(R.id.color_text)
        TextView colorTextView;

        public ColorViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
