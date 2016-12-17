package ru.magflayer.spectrum.presentation.common;


import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseRecyclerView<VH extends BaseViewHolder, T> extends RecyclerView.Adapter<VH> {

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    private List<T> data = new ArrayList<>();
    private OnItemClickListener itemClickListener;

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

    public T getItem(int position) {
        return data.get(position);
    }

    public void setItemClickListener(OnItemClickListener itemClickListener) {
        this.itemClickListener = itemClickListener;
    }

    @Override
    public void onBindViewHolder(VH holder, int position, List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
        holder.itemView.setOnClickListener(view -> {
            if (itemClickListener != null) {
                itemClickListener.onItemClick(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }
}
