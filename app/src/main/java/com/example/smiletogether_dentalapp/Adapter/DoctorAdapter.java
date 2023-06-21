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
import com.example.smiletogether_dentalapp.Model.Doctor;

import java.text.DecimalFormat;
import java.util.List;

import com.example.smiletogether_dentalapp.Model.Speciality;
import com.example.smiletogether_dentalapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;

public class DoctorAdapter extends RecyclerView.Adapter<DoctorAdapter.DoctorViewHolder> {
    private final List<Doctor> doctors;
    private final Context context;
    public static final DecimalFormat NUMBER_FORMAT = new DecimalFormat("#.00");
    DatabaseReference reference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://smiletogetherdentalapp-default-rtdb.firebaseio.com/");
    private final OnDoctorClickListener onDoctorClickListener;

    public DoctorAdapter(List<Doctor> doctors, Context context, OnDoctorClickListener onDoctorClickListener) {
        this.doctors = doctors;
        this.context = context;
        this.onDoctorClickListener = onDoctorClickListener;
    }

    @NonNull
    @Override
    public DoctorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_list_of_doctors,
                parent, false);
        return new DoctorViewHolder(view, onDoctorClickListener);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorViewHolder holder, int position) {
        holder.setDataDoctor(doctors.get(position), holder);
    }

    @Override
    public int getItemCount() {
        return doctors.size();
    }

    private ValueEventListener takeSpeciality(DoctorViewHolder holder) {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Speciality s = snapshot.getValue(Speciality.class);
                holder.tvViewDoctor_Speciality.setText(s.getname());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("preluareSpecialitate", error.getMessage());
            }
        };
    }


    public interface OnDoctorClickListener {
        void onDoctorClick(int position);
    }

    public class DoctorViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView tvFullNameDoctor;
        TextView tvViewDoctor_ProfessionalDegree;
        TextView tvViewDoctor_Speciality;
        TextView tvGrade;
        CircleImageView ciwProfilePhoto;

        OnDoctorClickListener onDoctorClickListener;

        public DoctorViewHolder(@NonNull View itemView, OnDoctorClickListener onDoctorClickListener) {
            super(itemView);
            tvFullNameDoctor = itemView.findViewById(R.id.tvFullNameDoctor);
            tvViewDoctor_ProfessionalDegree= itemView.findViewById(R.id.tvViewDoctor_ProfessionalDegree);
            tvViewDoctor_Speciality = itemView.findViewById(R.id.tvViewDoctor_Speciality);
            tvGrade = itemView.findViewById(R.id.tvGradeDoctor);
            ciwProfilePhoto=itemView.findViewById(R.id.ciwProfilePhoto_ListOfDoctors);

            this.onDoctorClickListener = onDoctorClickListener;
            itemView.setOnClickListener(this);
        }

        void setDataDoctor(Doctor doctor, DoctorViewHolder holder) {

            if (doctor.getUrlProfilePhoto()!=null && !doctor.getUrlProfilePhoto().equals("") ) {
                Glide.with(context).load(doctor.getUrlProfilePhoto()).into(holder.ciwProfilePhoto);
            } else {
                Glide.with(context).load(R.drawable.profile_photo).into(holder.ciwProfilePhoto);
            }

            String name = "Dr. " + doctor.getlastname() + " " + doctor.getFirstname();
            holder.tvFullNameDoctor.setText(name);

            if (doctor.getprofessionalDegree().equals("Nespecificat")) {
                holder.tvViewDoctor_ProfessionalDegree.setText("");
            } else holder.tvViewDoctor_ProfessionalDegree.setText(doctor.getprofessionalDegree());

            reference.child("specialitati").child(doctor.getidSpeciality()).addListenerForSingleValueEvent(takeSpeciality(holder));

            if (doctor.getGradeFeedback() == 0.0) {
                holder.tvGrade.setText("");
            } else {
                holder.tvGrade.setText(NUMBER_FORMAT.format(doctor.getGradeFeedback()));
            }

        }

        @Override
        public void onClick(View view) {
            onDoctorClickListener.onDoctorClick(getAdapterPosition());
        }

    }
}