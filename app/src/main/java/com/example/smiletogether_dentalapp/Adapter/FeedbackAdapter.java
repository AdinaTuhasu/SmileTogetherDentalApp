package com.example.smiletogether_dentalapp.Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.smiletogether_dentalapp.R;

import com.example.smiletogether_dentalapp.Model.Appointment;
import com.example.smiletogether_dentalapp.Model.Feedback;

import java.util.List;

public class FeedbackAdapter extends RecyclerView.Adapter<FeedbackAdapter.FeedbackViewHolder> {
    private final List<Appointment> appointments;

    public FeedbackAdapter(List<Appointment> appointments) {
        this.appointments = appointments;
    }

    @NonNull
    @Override
    public FeedbackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_of_feedback, parent, false);
        return new FeedbackViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FeedbackViewHolder holder, int position) {
        Appointment appointment = appointments.get(position);
        if (appointment != null) {
            Feedback feedback = appointment.getFeedback();
            holder.tvAppointmentsDate_Feedback.setText(appointment.getdate());
            String grade = feedback.getGrade() + " / 10";
            holder.tvGrade_Feedback.setText(grade);

        }
    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }

    public class FeedbackViewHolder extends RecyclerView.ViewHolder {
        TextView tvAppointmentsDate_Feedback;
        TextView tvGrade_Feedback;

        public FeedbackViewHolder(@NonNull View itemView) {
            super(itemView);
            tvAppointmentsDate_Feedback = itemView.findViewById(R.id.tvAppointmentsDate_Feedback);
            tvGrade_Feedback = itemView.findViewById(R.id.tvGrade_Feedback);

        }
    }
}
