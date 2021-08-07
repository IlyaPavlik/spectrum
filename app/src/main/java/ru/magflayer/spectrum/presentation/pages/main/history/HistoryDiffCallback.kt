package ru.magflayer.spectrum.presentation.pages.main.history

import androidx.recyclerview.widget.DiffUtil
import ru.magflayer.spectrum.domain.entity.ColorPhotoEntity

class HistoryDiffCallback(
    private val oldItems: List<ColorPhotoEntity>,
    private val newItems: List<ColorPhotoEntity>
) : DiffUtil.Callback() {

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldItems[oldItemPosition].filePath == newItems[newItemPosition].filePath
    }

    override fun getOldListSize() = oldItems.size

    override fun getNewListSize() = newItems.size

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldItems[oldItemPosition] == newItems[newItemPosition]
    }
}