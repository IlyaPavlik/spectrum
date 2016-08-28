package ru.magflayer.colorpointer.presentation.main.camera;

import android.support.v7.graphics.Palette;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.magflayer.colorpointer.R;

public class ColorCameraAdapter extends RecyclerView.Adapter<ColorCameraAdapter.ColorViewHolder> {

    private List<Palette.Swatch> colors = new ArrayList<>();

    @Override
    public ColorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ColorViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_color_camera, parent, false));
    }

    @SuppressWarnings("SuspiciousMethodCalls")
    public void setColors(List<Palette.Swatch> colors) {
        if (colors != null) {
            this.colors = new ArrayList<>(colors);
            this.colors.removeAll(Collections.singleton(null));
            notifyDataSetChanged();
        }
    }

    @Override
    public void onBindViewHolder(ColorViewHolder holder, int position) {
        Palette.Swatch swatch = colors.get(position);
        int color = swatch.getRgb();

        holder.colorContainer.setBackgroundColor(color);
        holder.colorTextView.setTextColor(swatch.getTitleTextColor());

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
