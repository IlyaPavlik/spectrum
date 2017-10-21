package ru.magflayer.spectrum.presentation.pages.main.history;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import butterknife.BindView;
import ru.magflayer.spectrum.R;
import ru.magflayer.spectrum.domain.model.ColorPicture;
import ru.magflayer.spectrum.presentation.common.BaseRecyclerAdapter;
import ru.magflayer.spectrum.presentation.common.BaseViewHolder;
import ru.magflayer.spectrum.utils.Base64Utils;
import ru.magflayer.spectrum.utils.BitmapUtils;

class HistoryAdapter extends BaseRecyclerAdapter<HistoryAdapter.HistoryViewHolder, ColorPicture> {

    HistoryAdapter(Context context) {
        super(context);
    }

    @Override
    public HistoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new HistoryViewHolder(inflater.inflate(R.layout.item_history, parent, false));
    }

    @Override
    public void onBindViewHolder(final HistoryViewHolder holder, int position) {
        final ColorPicture colorPicture = getItem(position);
        if (colorPicture == null) return;

        glide.load(Base64Utils.base46ToBytes(colorPicture.getPictureBase64()))
                .asBitmap()
                .fitCenter()
                .into(holder.pictureView);

        holder.colorContainer.post(() -> {
            int width = holder.colorContainer.getMeasuredWidth();
            int height = holder.colorContainer.getMeasuredHeight();

            if (width > 0 && height > 0) {
                Bitmap colorsBitmap = BitmapUtils.createMultiColorHorizontalBitmap(width, height, colorPicture.getRgbColors());
                holder.colorContainer.setImageBitmap(colorsBitmap);
            }
        });
    }

    static class HistoryViewHolder extends BaseViewHolder {

        @BindView(R.id.picture)
        ImageView pictureView;
        @BindView(R.id.color_container)
        ImageView colorContainer;

        HistoryViewHolder(View itemView) {
            super(itemView);
        }
    }
}
