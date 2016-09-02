package ru.magflayer.colorpointer.presentation.main.history;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.magflayer.colorpointer.R;
import ru.magflayer.colorpointer.domain.model.ColorPicture;
import ru.magflayer.colorpointer.utils.Base64Utils;
import ru.magflayer.colorpointer.utils.BitmapUtils;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.HistoryViewHolder> {

    private List<ColorPicture> history = new ArrayList<>();

    public void setHistory(List<ColorPicture> history) {
        if (history != null) {
            this.history = history;
            notifyDataSetChanged();
        }
    }

    @Override
    public HistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new HistoryViewHolder(inflater.inflate(R.layout.item_history, parent, false));
    }

    @Override
    public void onBindViewHolder(final HistoryViewHolder holder, int position) {
        final Context context = holder.itemView.getContext();
        final ColorPicture colorPicture = history.get(position);

        Glide.with(context)
                .load(Base64Utils.base46ToBytes(colorPicture.getPictureBase64()))
                .asBitmap()
                .fitCenter()
                .into(holder.pictureView);

        holder.colorContainer.post(new Runnable() {
            @Override
            public void run() {
                int width = holder.colorContainer.getMeasuredWidth();
                int height = holder.colorContainer.getMeasuredHeight();

                Bitmap colorsBitmap = BitmapUtils.createMultiColorHorizontalBitmap(width, height, colorPicture.getSwatches());
                holder.colorContainer.setImageBitmap(colorsBitmap);
            }
        });
    }

    @Override
    public int getItemCount() {
        return history.size();
    }

    class HistoryViewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.picture)
        ImageView pictureView;
        @BindView(R.id.color_container)
        ImageView colorContainer;

        public HistoryViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
}
