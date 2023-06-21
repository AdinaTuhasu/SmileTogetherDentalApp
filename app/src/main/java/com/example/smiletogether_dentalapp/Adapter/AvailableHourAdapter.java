package com.example.smiletogether_dentalapp.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.smiletogether_dentalapp.R;

import java.util.List;


public class AvailableHourAdapter extends RecyclerView.Adapter<AvailableHourAdapter.AvailableHourViewHolder> {
    private  List<String> availableHours;
    private  OnOraClickListener onOraClickListener;

    public AvailableHourAdapter(List<String> availableHours, OnOraClickListener onOraClickListener) {
        this.availableHours = availableHours;
        this.onOraClickListener = onOraClickListener;
    }

    @NonNull
    @Override
    public AvailableHourViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_available_hours, parent, false);
        return new AvailableHourViewHolder(view, onOraClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull AvailableHourViewHolder holder, int position) {
        String ora = availableHours.get(position);
        if (ora != null) {
            holder.tvHour.setText(ora);
        }
    }

    @Override
    public int getItemCount() {
        return availableHours.size();
    }

    public interface OnOraClickListener {
        void onOraClick(int position);
    }

    public class AvailableHourViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tvHour;
        CardView cwHour;

        OnOraClickListener onOraClickListener;

        public AvailableHourViewHolder(@NonNull View itemView, OnOraClickListener onOraClickListener) {
            super(itemView);
            tvHour = itemView.findViewById(R.id.tvHourAppointment);
            cwHour = itemView.findViewById(R.id.cwHourAppointment);

            this.onOraClickListener = onOraClickListener;
            cwHour.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onOraClickListener.onOraClick(getAdapterPosition());
        }
    }
}