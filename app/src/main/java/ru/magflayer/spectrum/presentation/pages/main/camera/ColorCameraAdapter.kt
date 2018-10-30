package ru.magflayer.spectrum.presentation.pages.main.camera

import android.content.Context
import android.support.v7.graphics.Palette
import android.view.View
import android.view.ViewGroup

import butterknife.BindView
import ru.magflayer.spectrum.R
import ru.magflayer.spectrum.presentation.common.android.BaseRecyclerAdapter
import ru.magflayer.spectrum.presentation.common.android.BaseViewHolder

class ColorCameraAdapter internal constructor(context: Context?) : BaseRecyclerAdapter<ColorCameraAdapter.ColorViewHolder, Palette.Swatch>(context) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        val view = inflater.inflate(R.layout.item_color_camera, parent, false)
        return ColorViewHolder(view)
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        val swatch = getItem(position)
        if (swatch != null) {
            val color = swatch.rgb
            holder.colorContainer.setBackgroundColor(color)
        }
    }

    class ColorViewHolder(itemView: View) : BaseViewHolder(itemView) {
        @BindView(R.id.color_container)
        lateinit var colorContainer: ViewGroup
    }
}
