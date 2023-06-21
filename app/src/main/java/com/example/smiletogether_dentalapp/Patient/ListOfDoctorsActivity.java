package com.example.smiletogether_dentalapp.Patient;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.smiletogether_dentalapp.Adapter.DoctorAdapter;
import com.example.smiletogether_dentalapp.Adapter.ScheduleAdapter;
import com.example.smiletogether_dentalapp.AppointmentsActivity;
import com.example.smiletogether_dentalapp.ChatActivity;
import com.example.smiletogether_dentalapp.ConversationsActivity;
import com.example.smiletogether_dentalapp.MainActivity;
import com.example.smiletogether_dentalapp.Model.Doctor;
import com.example.smiletogether_dentalapp.Model.Speciality;
import com.example.smiletogether_dentalapp.Model.WorkDay;
import com.example.smiletogether_dentalapp.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import de.hdodenhof.circleimageview.CircleImageView;

public class ListOfDoctorsActivity extends AppCompatActivity implements DoctorAdapter.OnDoctorClickListener, AdapterView.OnItemClickListener, TextWatcher {

    DatabaseReference reference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://smiletogetherdentalapp-default-rtdb.firebaseio.com/");

    private Intent intent;
    private Button btnAppointment;

    private Toolbar toolbar;
    private ProgressBar progressBar;

    private List<Doctor> Doctors;
    private RecyclerView rwListOfDoctors;

    private DoctorAdapter adapter;

    private RecyclerView.LayoutManager layoutManager;

    private AutoCompleteTextView actvSpeciality;

    private TextView tvTitle;

    private EditText etSearchDoctor;


    private List<Speciality> specialities;
    private int  specialitySelected= 0;
    private List<Doctor> doctorsFiltered = new ArrayList<>();

    private RelativeLayout rlNoDoctor;


    public static final String AVAILABLE_HOURS = "oreDisponibile";
    public static final String DOCTOR = "Medic";
    public static final String INFO_DOCTOR= "infoMedic";
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_doctors);

        //initialization
        progressBar = findViewById(R.id.progressBar);
        toolbar = findViewById(R.id.toolbar);
        Doctors = new ArrayList<>();
        rwListOfDoctors = findViewById(R.id.rwListOfDoctors);
        actvSpeciality = findViewById(R.id.actvSpecialities_ListOfDoctors);
        tvTitle = findViewById(R.id.tvTitle);
        intent = getIntent();
        etSearchDoctor = findViewById(R.id.etSearchDoctor_ListOfDoctors);
        rlNoDoctor = findViewById(R.id.rlNoDoctor);

        setToolbar();

        setRecyclerView();

        reference.child("specialitati").addValueEventListener(takeSpecialities());
        reference.child("user").addValueEventListener(takeDoctor());
    }

    private void setRecyclerView() {
        rwListOfDoctors.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        rwListOfDoctors.setLayoutManager(layoutManager);
        adapter = new DoctorAdapter(Doctors, this, this);
        rwListOfDoctors.setAdapter(adapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void setToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        if (intent.hasExtra(ConversationsActivity.NEW_CONVERSATION)) {
            tvTitle.setText("Conversatie noua");
        } else if (intent.hasExtra(AppointmentsActivity.ADD_APPOINTMENT)) {
            tvTitle.setText("Selectați medicul");
        }
    }



    private void loading(Boolean loading) {
        if (loading) {
            progressBar.setVisibility(View.VISIBLE);
        } else progressBar.setVisibility(View.GONE);
    }



    private ValueEventListener takeSpecialities() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                specialities = new ArrayList<>();
                List<String> name_speciality = new ArrayList<>();

                name_speciality.add(getString(R.string.toate_specialitatile));
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Speciality s = dataSnapshot.getValue(Speciality.class);
                    specialities.add(s);
                    name_speciality.add(s.getname());
                }

                ArrayAdapter<String> adapter = new ArrayAdapter(getApplicationContext(), R.layout.dropdown_item, name_speciality);

                actvSpeciality.setAdapter(adapter);
                actvSpeciality.setOnItemClickListener(ListOfDoctorsActivity.this);
                etSearchDoctor.addTextChangedListener(ListOfDoctorsActivity.this);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("preluareSpecialitati", error.getMessage());
            }
        };
    }

    private ValueEventListener takeDoctor() {
        loading(true);
        return new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Doctors.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Doctor m = dataSnapshot.getValue(Doctor.class);
                    if(m.getRole().equals("Doctor")) {
                        Doctors.add(m);
                    }
                }

                setAdapterDoctor(Doctors);

                loading(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("preluareMedici", error.getMessage());
            }
        };
    }

    private void setAdapterDoctor(List<Doctor> doctor) {
        List<Doctor> toRemove = new ArrayList<>();
        for (Doctor d : Doctors) {
            if (d.isDeletedAccount()) {
                toRemove.add(d);
            }
        }
        Doctors.removeAll(toRemove);
        adapter = new DoctorAdapter(doctor, getApplicationContext(), ListOfDoctorsActivity.this);
        rwListOfDoctors.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onDoctorClick(int position) {
        Doctor doctor;
        if (doctorsFiltered.isEmpty()) {
            doctor = Doctors.get(position);
        } else {
            doctor =doctorsFiltered.get(position);
        }
        if (intent.hasExtra(AppointmentsActivity.ADD_APPOINTMENT)) {
            startActivity(new Intent(getApplicationContext(), AvailableHoursActivity.class).putExtra(AVAILABLE_HOURS, doctor));
        } else if (intent.hasExtra(PatientHomePageActivity.SHOW_DOCTORS)) {
            BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(
                    ListOfDoctorsActivity.this, R.style.BottomSheetDialogTheme
            );
            View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.bottom_sheet_doctor, findViewById(R.id.rlBottomSheet));

            CircleImageView ciwDoctor_ProfileImg = view.findViewById(R.id.ciwProfilePhoto_doctorClick);
            TextView tvDoctorName = view.findViewById(R.id.tvDoctorName_bottom_sheet);
            TextView tvDoctorProfessionalDegree = view.findViewById(R.id.tvProfessionalDegree_bottom_sheet);
            TextView tvDoctorSpeciality = view.findViewById(R.id.tvSpeciality_bottom_sheet);
            TextView tvDoctorEmail = view.findViewById(R.id.tvEmail_bottom_sheet);
            ListView lvSchedule = view.findViewById(R.id.lvSchedule_bottom_sheet);
            TextView tvGrade = view.findViewById(R.id.tvGrade_doctorClick);
            btnAppointment = view.findViewById(R.id.btnAppointment_bottom_sheet);

            btnAppointment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    startActivity(new Intent(getApplicationContext(),AvailableHoursActivity.class).putExtra(AVAILABLE_HOURS,doctor));

                }
            });
            Button btnContact = view.findViewById(R.id.btnContact_bottom_sheet);
            btnContact.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getApplicationContext(), ChatActivity.class).putExtra(INFO_DOCTOR, doctor));
                }
            });

            String name = "Dr. " + doctor.getlastname() + " " + doctor.getFirstname();
            tvDoctorName.setText(name);

            if (!doctor.getprofessionalDegree().equals("Nespecificat")) {
                tvDoctorProfessionalDegree.setText(doctor.getprofessionalDegree());

            } else
                tvDoctorProfessionalDegree.setVisibility(View.GONE);

            String urlProfilePhoto = doctor.getUrlProfilePhoto();
            if (urlProfilePhoto!=null && !urlProfilePhoto.equals("") ) {
                Glide.with(getApplicationContext()).load(urlProfilePhoto).into(ciwDoctor_ProfileImg);
            } else {
                Glide.with(getApplicationContext()).load(R.drawable.profile_photo).into(ciwDoctor_ProfileImg);
            }

            reference.child("specialitati").child(doctor.getidSpeciality()).addListenerForSingleValueEvent(takeSpeciality(tvDoctorSpeciality));

            tvDoctorEmail.setText(doctor.getEmail());

            String grade = "-";
            if (doctor.getGradeFeedback() != 0) {
                grade = DoctorAdapter.NUMBER_FORMAT.format(doctor.getGradeFeedback()) + " din 10";
            }
            tvGrade.setText(grade);

            ScheduleAdapter adapter = new ScheduleAdapter(getApplicationContext(), R.layout.item_schedule_doctor,
                    doctor.getSchedule(), getLayoutInflater());
            lvSchedule.setAdapter(adapter);


            bottomSheetDialog.setContentView(view);
            bottomSheetDialog.show();
        }
        else if (intent.hasExtra(ConversationsActivity.NEW_CONVERSATION)) {
            startActivity(new Intent(getApplicationContext(), ChatActivity.class).putExtra(DOCTOR, doctor));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }
        }


    private ValueEventListener takeSpeciality(TextView tvSpeciality1) {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Speciality s = snapshot.getValue(Speciality.class);
                tvSpeciality1.setText(s.getname());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("preluareSpecialitate", error.getMessage());
            }
        };
    }



    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        String search_filter = etSearchDoctor.getText().toString();
        specialitySelected = i;
        if (i != 0) {
            if (!search_filter.isEmpty()) { //dar daca am deja ceva in cautare
                filtersDoctorBySpecialityAndName(specialities.get(i - 1).getidSpeciality(), search_filter);
            } else {
                filtersDoctorsBySpeciality(specialities.get(i - 1).getidSpeciality());
            }

            displayDoctors();

        } else {
            if (search_filter.isEmpty()) {
                rlNoDoctor.setVisibility(View.GONE);
                rwListOfDoctors.setVisibility(View.VISIBLE);
                setAdapterDoctor(Doctors);
            } else {
                filtersDoctorsByName(search_filter);
                displayDoctors();
            }
        }

    }


    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        String stringCurrent = charSequence.toString().toLowerCase();
        if (!stringCurrent.isEmpty()) {


            if (specialitySelected == 0) { //daca selectia din actvSpeciality e pe primul elem (nu am nicio specialiatte selectata)
                filtersDoctorsByName(stringCurrent);
            } else { //caut medicii doar de la specialitatea selecatata
                filtersDoctorBySpecialityAndName(specialities.get(specialitySelected - 1).getidSpeciality(), stringCurrent);
            }
            displayDoctors();

        } else {
            doctorsFiltered.clear();
            if (specialitySelected == 0) {
                rlNoDoctor.setVisibility(View.GONE);
                rwListOfDoctors.setVisibility(View.VISIBLE);
                setAdapterDoctor(Doctors);
            } else {
                filtersDoctorsBySpeciality(specialities.get(specialitySelected - 1).getidSpeciality());
                displayDoctors();
            }
        }

    }

    @Override
    public void afterTextChanged(Editable editable) {

    }

    private void displayDoctors() {
        if (doctorsFiltered.isEmpty()) {
            rlNoDoctor.setVisibility(View.VISIBLE);
            rwListOfDoctors.setVisibility(View.GONE);
        } else {
            rlNoDoctor.setVisibility(View.GONE);
            rwListOfDoctors.setVisibility(View.VISIBLE);
            setAdapterDoctor(doctorsFiltered);
        }
    }

    private void filtersDoctorsBySpeciality(String idSpeciality) {
        doctorsFiltered.clear();
        for (Doctor doctor: Doctors) {
            if (doctor.getidSpeciality().equals(idSpeciality)) {
                doctorsFiltered.add(doctor);
            }
        }

    }

    private void filtersDoctorsByName(String string_search) {
        doctorsFiltered.clear();
        for (Doctor doctor : Doctors) {
            if (doctor.getlastname().toLowerCase().contains(string_search.toLowerCase()) ||
                    doctor.getFirstname().toLowerCase().contains(string_search.toLowerCase())) {
                doctorsFiltered.add(doctor);
            }
        }

    }

    private void filtersDoctorBySpecialityAndName(String idSpeciality, String stringSearch) {
        doctorsFiltered.clear();
        for (Doctor doctor : Doctors) {
            if (doctor.getidSpeciality().equals(idSpeciality) &&
                    (doctor.getlastname().toLowerCase().contains(stringSearch.toLowerCase()) ||
                            doctor.getFirstname().toLowerCase().contains(stringSearch.toLowerCase()))) {
                doctorsFiltered.add(doctor);
            }
        }
    }


}
