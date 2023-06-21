package com.example.smiletogether_dentalapp.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.smiletogether_dentalapp.Model.Doctor;
import com.example.smiletogether_dentalapp.Model.Patient;
import com.example.smiletogether_dentalapp.Model.Speciality;
import com.example.smiletogether_dentalapp.R;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smiletogether_dentalapp.Model.Appointment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class AppointmentAdapter extends RecyclerView.Adapter<AppointmentAdapter.AppointmentAdapterViewHolder> {
    public static final String PATIENT = "Pacient";
    public static final String DOCTOR = "Medic";

    FirebaseAuth auth = FirebaseAuth.getInstance();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://smiletogetherdentalapp-default-rtdb.firebaseio.com/");
    private final String idUser = auth.getCurrentUser().getUid();

    private final List<Appointment> appointments;
    private final OnAppointmentClickListener onAppointmentClickListener;
    private final OnAppointmentLongClickListener onAppointmentLongClickListener;
    private final OnBtnFeedbackClickListener onBtnFeedbackClickListener;
    private final OnBtnRetetaClickListener onBtnRetetaClickListener;

    private final String typeUser;

    private final Context context;

    public AppointmentAdapter(List<Appointment> appointments, String typeUser, OnAppointmentClickListener onAppointmentClickListener,
                             OnAppointmentLongClickListener onAppointmentLongClickListener,
                              OnBtnFeedbackClickListener onBtnFeedbackClickListener,
                              OnBtnRetetaClickListener onBtnRetetaClickListener,Context context
                            ) {
        this.appointments = appointments;
        this.typeUser = typeUser;
        this.onAppointmentClickListener = onAppointmentClickListener;
        this.onAppointmentLongClickListener = onAppointmentLongClickListener;
        this.onBtnFeedbackClickListener = onBtnFeedbackClickListener;
        this.onBtnRetetaClickListener = onBtnRetetaClickListener;
        this.context = context;
    }

    @NonNull
    @Override
    public AppointmentAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_of_appointments, parent, false);
        return new AppointmentAdapterViewHolder(view, onAppointmentClickListener, onAppointmentLongClickListener,onBtnFeedbackClickListener, onBtnRetetaClickListener);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onBindViewHolder(@NonNull AppointmentAdapterViewHolder holder, int position) {

        Appointment p = appointments.get(position);
        if (p != null) {
            holder.tvAppointmentsDate.setText(p.getdate());
            holder.tvAppointmentsHour.setText(p.gethour());
            holder.tvStatus.setText(p.getStatus());

            holder.tvStatus.setTextColor(ContextCompat.getColor(context, R.color.black));
            if (p.getStatus().equals(context.getString(R.string.canceled_status))
                    || p.getStatus().equals(context.getString(R.string.unhonored_status))) {
               // holder.tvStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.cancel, 0, 0, 0);
                Drawable drawable = ContextCompat.getDrawable(context, R.drawable.cancel);
                int drawableSize = (int) (holder.tvStatus.getLineHeight() * 0.8); // set size proportional to text size
                drawable.setBounds(0, 0, drawableSize, drawableSize);
                holder.tvStatus.setCompoundDrawables(drawable, null, null, null);
                holder.btnFeedback.setEnabled(false);
                holder.btnFeedback.setTextColor(ContextCompat.getColor(context, R.color.light_blue));
                holder.btnFeedback.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_star_rate_off_24, 0);

            } else if (p.getStatus().equals(context.getString(R.string.empty_status))) {
                Drawable drawable = ContextCompat.getDrawable(context, R.drawable.important);
                int drawableSize = (int) (holder.tvStatus.getLineHeight() * 1.3); // set size proportional to text size
                drawable.setBounds(0, 0, drawableSize, drawableSize);
                holder.tvStatus.setCompoundDrawables(drawable, null, null, null);
                if (typeUser.equals(DOCTOR)) {
                    holder.tvStatus.setTextColor(Color.parseColor("#FF9100"));
                } else {
                    holder.tvStatus.setText("");
                    holder.btnFeedback.setEnabled(false);
                    holder.btnFeedback.setTextColor(ContextCompat.getColor(context, R.color.light_blue));
                    holder.btnFeedback.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_star_rate_off_24, 0);

                }
            } else if (p.getStatus().equals(context.getString(R.string.honored_status))) {
                Drawable drawable = ContextCompat.getDrawable(context, R.drawable.ok);
                int drawableSize = (int) (holder.tvStatus.getLineHeight() * 1.3); // set size proportional to text size
                drawable.setBounds(0, 0, drawableSize, drawableSize);
                holder.tvStatus.setCompoundDrawables(drawable, null, null, null);
                holder.btnFeedback.setEnabled(true);
                holder.btnFeedback.setTextColor(ContextCompat.getColor(context, R.color.primary_color));
                holder.btnFeedback.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_star_rate_on_24, 0);

            } else if (p.getStatus().equals(context.getString(R.string.new_status))) {
               // holder.tvStatus.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ok, 0, 0, 0);
                Drawable drawable = ContextCompat.getDrawable(context, R.drawable.ok);
                int drawableSize = (int) (holder.tvStatus.getLineHeight() * 1.3); // set size proportional to text size
                drawable.setBounds(0, 0, drawableSize, drawableSize);
                holder.tvStatus.setCompoundDrawables(drawable, null, null, null);
                holder.btnFeedback.setEnabled(false);
                holder.btnFeedback.setTextColor(ContextCompat.getColor(context, R.color.light_blue));
                holder.btnFeedback.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_star_rate_off_24, 0);
            }
            if (p.getFeedback() != null) {
                holder.btnFeedback.setEnabled(false);
                holder.btnFeedback.setTextColor(ContextCompat.getColor(context, R.color.light_blue));
                holder.btnFeedback.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_star_rate_off_24, 0);
            }



            if (typeUser.equals(PATIENT)) {
                if (p.getUrlPrescription() == null || p.getUrlPrescription().equals("")) {
                    holder.btnPrescription.setEnabled(false);
                    holder.btnPrescription.setTextColor(ContextCompat.getColor(context, R.color.light_blue));
                    holder.btnPrescription.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_file_download_off_24, 0);
                } else {
                    holder.btnPrescription.setEnabled(true);
                    holder.btnPrescription.setTextColor(ContextCompat.getColor(context, R.color.primary_color));
                    holder.btnPrescription.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_file_download_on_24, 0);
                }

                setDataDoctor(p, holder);

            } else {
                holder.tvSpeciality.setVisibility(View.GONE);
                holder.btnFeedback.setVisibility(View.GONE);

                if (p.getUrlPrescription() == null || p.getUrlPrescription().equals("")) { //daca nu a atasat inca nicio reteta
                    holder.btnPrescription.setText("Atașează rețetă");
                    holder.btnPrescription.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_attach_file_on_24, 0);
                }

                if (!p.getStatus().equals(context.getString(R.string.honored_status))) {
                    holder.btnPrescription.setEnabled(false);
                    holder.btnPrescription.setTextColor(ContextCompat.getColor(context, R.color.light_blue));
                    holder.btnPrescription.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_attach_file_off_24, 0);
                }
                setPatientName(p, holder);
            }
        }

    }

    private void setPatientName(Appointment p, AppointmentAdapterViewHolder holder) {
        holder.progressBar.setVisibility(View.VISIBLE);
        reference.child("user").child(p.getPatientsId()).addListenerForSingleValueEvent(takePatient(holder));
    }

    private ValueEventListener takePatient(AppointmentAdapterViewHolder holder) {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Patient patient = snapshot.getValue(Patient.class);
                if (patient != null) {
                    String full_name = patient.getlastname() + " " + patient.getFirstname();
                    holder.tvName.setText(full_name);
                    holder.progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("preluarePacientProg", error.getMessage());
            }
        };
    }

    private void setDataDoctor(Appointment p, AppointmentAdapterViewHolder holder) {
        holder.progressBar.setVisibility(View.VISIBLE);
        reference.child("user").child(p.getDoctorsId()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Doctor d = snapshot.getValue(Doctor.class);
                if (d != null) {

                    String full_name = "Dr. " + d.getlastname() + " " + d.getFirstname();
                    holder.tvName.setText(full_name);
                    reference.child("specialitati").child(d.getidSpeciality()).addListenerForSingleValueEvent(takeSpeciality(holder));
                }

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("preluareDoctorProg", error.getMessage());
            }
        });
    }

    private ValueEventListener takeSpeciality(AppointmentAdapterViewHolder holder) {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Speciality s = snapshot.getValue(Speciality.class);
                holder.tvSpeciality.setText(s.getname());
                holder.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("preluareSpecProg", error.getMessage());
            }
        };
    }

    @Override
    public int getItemCount() {
        return appointments.size();
    }

    public interface OnAppointmentClickListener {
        void onAppointmentClick(int position);
    }

    public interface OnAppointmentLongClickListener {
        void onAppointmentLongClick(int position);
    }

    public interface OnBtnFeedbackClickListener {
        void onBtnFeedbackClick(int position);
    }

    public interface OnBtnRetetaClickListener {
        void onBtnRetetaClickListener(int position);
    }



    public class AppointmentAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener, View.OnLongClickListener {
        TextView tvName;
        TextView tvSpeciality;
        TextView tvAppointmentsDate;
        TextView tvAppointmentsHour;
        TextView tvStatus;
        ProgressBar progressBar;
        AppCompatButton btnFeedback;
        AppCompatButton btnPrescription;

        CardView cwAppointments;
        OnAppointmentClickListener onAppointmentClickListener;
        OnAppointmentLongClickListener onAppointmentLongClickListener;
        OnBtnFeedbackClickListener onBtnFeedbackClickListener;
        OnBtnRetetaClickListener onBtnRetetaClickListener;

        public AppointmentAdapterViewHolder(@NonNull View itemView, OnAppointmentClickListener onAppointmentClickListener,
                                            OnAppointmentLongClickListener onAppointmentLongClickListener,OnBtnFeedbackClickListener onBtnFeedbackClickListener,
                                            OnBtnRetetaClickListener onBtnRetetaClickListener) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvName_appointment_item);
            tvSpeciality = itemView.findViewById(R.id.tvSpeciality_appointment_item);
            tvAppointmentsDate = itemView.findViewById(R.id.tvAppointmentsDate);
            tvAppointmentsHour = itemView.findViewById(R.id.tvAppointmentsHour);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            btnFeedback = itemView.findViewById(R.id.btnFeedback);
            btnPrescription = itemView.findViewById(R.id.btnPrescription);
            progressBar = itemView.findViewById(R.id.progressBar);
            cwAppointments = itemView.findViewById(R.id.cwAppointment_item);


            cwAppointments.setOnClickListener(this);
            cwAppointments.setOnLongClickListener(this);
            btnFeedback.setOnClickListener(this);
            btnPrescription.setOnClickListener(this);

            this.onAppointmentClickListener = onAppointmentClickListener;
            this.onAppointmentLongClickListener = onAppointmentLongClickListener;
            this.onBtnFeedbackClickListener = onBtnFeedbackClickListener;
            this.onBtnRetetaClickListener = onBtnRetetaClickListener;

        }


        @SuppressLint("NonConstantResourceId")
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.cwAppointment_item:
                    onAppointmentClickListener.onAppointmentClick(getAdapterPosition());
                    break;

                case R.id.btnFeedback:
                    onBtnFeedbackClickListener.onBtnFeedbackClick(getAdapterPosition());
                    break;

                case R.id.btnPrescription:
                    onBtnRetetaClickListener.onBtnRetetaClickListener(getAdapterPosition());
                    break;

            }
        }

        @Override
        public boolean onLongClick(View view) {
            onAppointmentLongClickListener.onAppointmentLongClick(getAdapterPosition());
            return true;
        }
    }
}