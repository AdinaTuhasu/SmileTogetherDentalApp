package com.example.smiletogether_dentalapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.smiletogether_dentalapp.R;

import com.example.smiletogether_dentalapp.Model.WorkDay;


import java.util.ArrayList;
import java.util.List;

public class ScheduleAdapter extends ArrayAdapter<WorkDay> {
    private Context context;
    private int resource;
    private List<WorkDay> schedule;
    private LayoutInflater inflater;

    public ScheduleAdapter(@NonNull Context context, int resource, @NonNull List<WorkDay> schedule, LayoutInflater inflater) {
        super(context, resource, schedule);
        this.context = context;
        this.resource = resource;
        this.schedule = schedule;
        this.inflater = inflater;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = inflater.inflate(resource, parent, false);
        WorkDay day = schedule.get(position);

        if (day != null) {
            TextView tvWorkDay = view.findViewById(R.id.tvWorkDay_DoctorSchedule);
            String WorkDay = day.getDay() + ": " + day.getStartTime() + " - " + day.getEndTime();
            tvWorkDay.setText(WorkDay);
        }

        return view;
    }
}
