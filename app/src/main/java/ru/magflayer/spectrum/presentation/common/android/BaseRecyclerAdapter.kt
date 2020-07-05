package ru.magflayer.spectrum.presentation.common.android

import android.content.Context
import android.view.LayoutInflater
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.RequestManager
import java.util.*

abstract class BaseRecyclerAdapter<VH : BaseViewHolder, T>(
        val context: Context?,
        val inflater: LayoutInflater = LayoutInflater.from(context)
) : RecyclerView.Adapter<VH>() {

    val data = ArrayList<T>()
    var itemLongClickListener: OnItemLongClickListener? = null
    var itemSelectListener: OnItemSelectListener? = null
    var selectedPosition: Int = 0

    protected var glide: RequestManager? = context?.let { Glide.with(it) }

    interface OnItemLongClickListener {
        fun onItemLongClick(position: Int)
    }

    interface OnItemSelectListener {
        fun onItemSelect(position: Int)
    }

    fun addAll(data: List<T>) {
        this.data.addAll(data)
        notifyDataSetChanged()
    }

    fun setData(data: List<T>) {
        this.data.clear()
        this.data.addAll(data)

        notifyDataSetChanged()
    }

    fun remove(position: Int) {
        this.data.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, itemCount)
    }

    fun getItem(position: Int): T? {
        return if (position >= 0 && position < data.size)
            data[position]
        else
            null
    }

    override fun onBindViewHolder(holder: VH, position: Int, payloads: List<Any>) {
        super.onBindViewHolder(holder, position, payloads)
        holder.itemView.setOnClickListener { select(holder.adapterPosition) }
        holder.itemView.setOnLongClickListener {
            itemLongClickListener?.onItemLongClick(position)
            true
        }
    }

    override fun getItemCount(): Int {
        return data.size
    }

    open fun select(position: Int) {
        selectedPosition = position
        itemSelectListener?.onItemSelect(position)
    }
}
