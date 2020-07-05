package ru.magflayer.spectrum.presentation.common.android

import android.view.View
import androidx.recyclerview.widget.RecyclerView

import butterknife.ButterKnife

abstract class BaseViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    init {
        ButterKnife.bind(this, itemView)
    }
}
