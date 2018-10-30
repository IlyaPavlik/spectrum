package ru.magflayer.spectrum.presentation.pages.main.history

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import butterknife.BindView
import com.bumptech.glide.request.RequestOptions
import ru.magflayer.spectrum.R
import ru.magflayer.spectrum.domain.entity.ColorPhotoEntity
import ru.magflayer.spectrum.presentation.common.android.BaseRecyclerAdapter
import ru.magflayer.spectrum.presentation.common.android.BaseViewHolder
import ru.magflayer.spectrum.presentation.common.extension.createMultiColorHorizontalBitmap

internal class HistoryAdapter(context: Context?) : BaseRecyclerAdapter<HistoryAdapter.HistoryViewHolder, ColorPhotoEntity>(context) {

    private val colorContainerSize: Int = context?.resources?.getDimensionPixelSize(R.dimen.history_item_color_size)
            ?: 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistoryViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return HistoryViewHolder(inflater.inflate(R.layout.item_history, parent, false))
    }

    override fun onBindViewHolder(holder: HistoryViewHolder, position: Int) {
        val (_, filePath, rgbColors) = getItem(position) ?: return

        val requestOptions = RequestOptions()
                .fitCenter()
                .dontTransform()
                .dontAnimate()

        glide?.load(filePath)?.apply(requestOptions)?.into(holder.pictureView)

        if (rgbColors.size == 1) {
            holder.colorContainer.setBackgroundColor(rgbColors[0])
        } else {
            val colorsBitmap = createMultiColorHorizontalBitmap(colorContainerSize, colorContainerSize, rgbColors)
            holder.colorContainer.setImageBitmap(colorsBitmap)
        }
    }

    internal class HistoryViewHolder(itemView: View) : BaseViewHolder(itemView) {
        @BindView(R.id.picture)
        lateinit var pictureView: ImageView
        @BindView(R.id.color_container)
        lateinit var colorContainer: ImageView
    }
}
