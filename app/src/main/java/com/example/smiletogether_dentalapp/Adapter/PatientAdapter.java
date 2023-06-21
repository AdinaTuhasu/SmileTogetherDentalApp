package com.example.smiletogether_dentalapp.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.smiletogether_dentalapp.Model.Appointment;
import com.example.smiletogether_dentalapp.Model.Patient;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TreeSet;

import com.example.smiletogether_dentalapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class PatientAdapter extends RecyclerView.Adapter<PatientAdapter.PatientViewHolder> {
    private final List<Patient> patients;
    private final Context context;
    private final OnPatientClickListener onPatientClickListener;
    private TreeSet<Date> appointmentDate= new TreeSet<>();//TreeSet pune in ordine


    FirebaseAuth auth = FirebaseAuth.getInstance();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://smiletogetherdentalapp-default-rtdb.firebaseio.com/");
    private final String idDoctor = auth.getCurrentUser().getUid();

    public PatientAdapter(List<Patient> patients, Context context, OnPatientClickListener onPatientClickListener) {
        this.patients = patients;
        this.context = context;
        this.onPatientClickListener = onPatientClickListener;

    }

    @NonNull
    @Override
    public PatientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_of_patients, parent, false);
        return new PatientViewHolder(view, onPatientClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull PatientViewHolder holder, int position) {
        Patient patient = patients.get(position);
        if (patient != null) {
           if (patient.getUrlProfilePhoto()!=null && !patient.getUrlProfilePhoto().equals("") ) {
                Glide.with(context).load(patient.getUrlProfilePhoto()).into(holder.ciwProfilePhoto);
            } else {
                Glide.with(context).load(R.drawable.profile_photo).into(holder.ciwProfilePhoto);
            }

            String name = patient.getlastname() + " " + patient.getFirstname();
            holder.tvNamePatient.setText(name);

            holder.tvPhoneNr.setText(String.valueOf(patient.getNrPhone()));
            reference.child("programari").addValueEventListener(takeAppointment(patient, holder));
        }
    }

    private ValueEventListener takeAppointment(Patient patient, PatientViewHolder holder) {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                appointmentDate.clear(); // ca sa nu cumuleze programarile de la toti pacientii
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Appointment appointment = dataSnapshot.getValue(Appointment.class);
                    if (appointment.getPatientsId().equals(patient.getId()) && appointment.getDoctorsId().equals(idDoctor) &&
                            (appointment.getStatus().equals(context.getString(R.string.honored_status)) )) {
                        try {
                            Date date = new SimpleDateFormat("dd/MM/yyyy", Locale.US).parse(appointment.getdate());
                            appointmentDate.add(date);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if (!appointmentDate.isEmpty()) {
                    holder.tvLastAppointmentDate.setText(new SimpleDateFormat("dd/MM/yyyy", Locale.US).format(appointmentDate.last()));
                } else {
                    holder.tvLastAppointmentDate.setText(" - ");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("preluareDateProgramari", error.getMessage());
            }
        };
    }



    @Override
    public int getItemCount() {
        return patients.size();
    }

    public interface OnPatientClickListener {
        void onPatientClick(int position);
    }


    public class PatientViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvNamePatient;
        TextView tvPhoneNr;
        TextView tvLastAppointmentDate;
        CircleImageView ciwProfilePhoto;


        OnPatientClickListener onPatientClickListener;

        public PatientViewHolder(@NonNull View itemView, OnPatientClickListener onPatientClickListener) {
            super(itemView);
            tvNamePatient = itemView.findViewById(R.id.tvName_ListOfPatients);
            tvPhoneNr=itemView.findViewById(R.id.tvPhoneNr_ListOfPatients);
            tvLastAppointmentDate=itemView.findViewById(R.id.tvLastAppointment);
            ciwProfilePhoto=itemView.findViewById(R.id.ciwProfilePhoto_ListOfPatients);


            this.onPatientClickListener = onPatientClickListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            onPatientClickListener.onPatientClick(getAdapterPosition());
        }
    }
}
