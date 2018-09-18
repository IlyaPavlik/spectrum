package ru.magflayer.spectrum.presentation.pages.main.history;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.request.RequestOptions;

import butterknife.BindView;
import ru.magflayer.spectrum.R;
import ru.magflayer.spectrum.domain.entity.ColorPhotoEntity;
import ru.magflayer.spectrum.presentation.common.android.BaseRecyclerAdapter;
import ru.magflayer.spectrum.presentation.common.android.BaseViewHolder;
import ru.magflayer.spectrum.presentation.common.utils.BitmapUtils;

class HistoryAdapter extends BaseRecyclerAdapter<HistoryAdapter.HistoryViewHolder, ColorPhotoEntity> {

    HistoryAdapter(final Context context) {
        super(context);
    }

    @NonNull
    @Override
    public HistoryViewHolder onCreateViewHolder(@NonNull final ViewGroup parent, final int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        return new HistoryViewHolder(inflater.inflate(R.layout.item_history, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull final HistoryViewHolder holder, int position) {
        final ColorPhotoEntity entity = getItem(position);
        if (entity == null) return;

        glide.load(entity.getFilePath())
                .apply(RequestOptions.fitCenterTransform())
                .into(holder.pictureView);

        holder.colorContainer.post(() -> {
            int width = holder.colorContainer.getMeasuredWidth();
            int height = holder.colorContainer.getMeasuredHeight();

            if (width > 0 && height > 0) {
                Bitmap colorsBitmap = BitmapUtils.createMultiColorHorizontalBitmap(width, height, entity.getRgbColors());
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
