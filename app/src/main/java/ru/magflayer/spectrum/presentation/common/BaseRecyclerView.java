package ru.magflayer.spectrum.presentation.common;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;

import java.util.ArrayList;
import java.util.List;

import lombok.Setter;

public abstract class BaseRecyclerView<VH extends BaseViewHolder, T> extends RecyclerView.Adapter<VH> {

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public interface OnItemSelectListener {
        void onItemSelect(int position);
    }

    private List<T> data = new ArrayList<>();
    private OnItemClickListener itemClickListener;
    private OnItemSelectListener itemSelectListener;
    private int selectedPosition;

    protected LayoutInflater inflater;
    protected RequestManager glide;

    public BaseRecyclerView(Context context) {
        glide = Glide.with(context);
        inflater = LayoutInflater.from(context);
    }

    public void addAll(List<T> data) {
        this.data.addAll(data);
        notifyDataSetChanged();
    }

    public void setData(List<T> data) {
        if (data != null) {
            this.data = data;
            notifyDataSetChanged();
        }
    }

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    public void setItemSelectListener(OnItemSelectListener itemSelectListener) {
        this.itemSelectListener = itemSelectListener;
    }

    public T getItem(int position) {
        return data.get(position);
    }

    @Override
    public void onBindViewHolder(VH holder, int position, List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
        holder.itemView.setOnClickListener(view -> {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(position);
            }
            select(holder.getAdapterPosition());
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public int getSelectedPosition() {
        return selectedPosition;
    }

    public void select(int position) {
        selectedPosition = position;
        if (itemSelectListener != null) {
            itemSelectListener.onItemSelect(position);
        }
        notifyDataSetChanged();
    }
}
