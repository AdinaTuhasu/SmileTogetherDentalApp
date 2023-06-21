package com.example.smiletogether_dentalapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.smiletogether_dentalapp.R;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smiletogether_dentalapp.Model.Investigation;

import java.util.List;

public class InvestigationAdapter extends RecyclerView.Adapter<InvestigationAdapter.InvestigationViewHolder> {
    private final List<Investigation> investigations;
    private final Context context;

    public InvestigationAdapter(List<Investigation> investigations, Context context) {
        this.investigations = investigations;
        this.context = context;
    }

    @NonNull
    @Override
    public InvestigationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_investigation_list, parent, false);
        return new InvestigationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull InvestigationViewHolder holder, int position) {
        Investigation i = investigations.get(position);
        if (i != null) {
            holder.tvName.setText(i.getname());
            String price = String.valueOf(i.getprice()) + " RON";
            holder.tvPrice.setText(price);
        }
    }

    @Override
    public int getItemCount() {
        return investigations.size();
    }

    public class InvestigationViewHolder extends RecyclerView.ViewHolder {
        TextView tvName;
        TextView tvPrice;

        public InvestigationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvNameInvestigation);
            tvPrice = itemView.findViewById(R.id.tvPriceInvestigation);
        }
    }
}