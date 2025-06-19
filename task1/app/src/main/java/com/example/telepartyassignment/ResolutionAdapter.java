package com.example.telepartyassignment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class ResolutionAdapter extends RecyclerView.Adapter<ResolutionAdapter.ViewHolder> {

    // Listener to notify when a resolution is selected
    public interface OnResolutionSelectedListener {
        void onSelected(int position);
    }

    private final List<String> resolutionList;
    private final OnResolutionSelectedListener listener;
    private int selectedPosition;

    public ResolutionAdapter(List<String> resolutionList, int selectedPosition, OnResolutionSelectedListener listener) {
        this.resolutionList = resolutionList;
        this.selectedPosition = selectedPosition;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Inflate the resolution item layout
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_resolution, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        String label = resolutionList.get(position);
        holder.label.setText(label);

        // Show check icon if this item is selected
        holder.checkIcon.setVisibility(position == selectedPosition ? View.VISIBLE : View.GONE);

        // Handle click event to update selection
        holder.itemView.setOnClickListener(v -> {
            if (position != selectedPosition) {
                int previousPosition = selectedPosition;
                selectedPosition = position;
                notifyItemChanged(previousPosition);
                notifyItemChanged(selectedPosition);
                listener.onSelected(position);
            }
        });
    }

    @Override
    public int getItemCount() {
        return resolutionList.size();
    }

    // ViewHolder class to hold item views
    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView label;
        ImageView checkIcon;

        ViewHolder(View itemView) {
            super(itemView);
            label = itemView.findViewById(R.id.label_quality);
            checkIcon = itemView.findViewById(R.id.icon_check);
        }
    }
}