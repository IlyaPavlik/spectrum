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

    private final int colorContainerSize;

    HistoryAdapter(final Context context) {
        super(context);
        colorContainerSize = context.getResources().getDimensionPixelSize(R.dimen.history_item_color_size);
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

        RequestOptions requestOptions = new RequestOptions()
                .fitCenter()
                .dontTransform()
                .dontAnimate();

        glide.load(entity.getFilePath())
                .apply(requestOptions)
                .into(holder.pictureView);

        if (entity.getRgbColors().size() == 1) {
            holder.colorContainer.setBackgroundColor(entity.getRgbColors().get(0));
        } else {
            Bitmap colorsBitmap = BitmapUtils.createMultiColorHorizontalBitmap(colorContainerSize, colorContainerSize,
                    entity.getRgbColors());
            holder.colorContainer.setImageBitmap(colorsBitmap);
        }
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
