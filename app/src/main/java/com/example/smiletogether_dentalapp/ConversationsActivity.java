package com.example.smiletogether_dentalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.example.smiletogether_dentalapp.Adapter.DoctorAdapter;
import com.example.smiletogether_dentalapp.Adapter.DoctorsChatAdapter;
import com.example.smiletogether_dentalapp.Adapter.PatientAdapter;
import com.example.smiletogether_dentalapp.Adapter.PatientsChatAdapter;
import com.example.smiletogether_dentalapp.Doctor.DoctorHomePageActivity;
import com.example.smiletogether_dentalapp.Doctor.ListOfPatientsActivity;
import com.example.smiletogether_dentalapp.Model.Chat;
import com.example.smiletogether_dentalapp.Model.Doctor;
import com.example.smiletogether_dentalapp.Model.Patient;
import com.example.smiletogether_dentalapp.Patient.ListOfDoctorsActivity;
import com.example.smiletogether_dentalapp.Patient.PatientHomePageActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ConversationsActivity extends AppCompatActivity implements View.OnClickListener, DoctorAdapter.OnDoctorClickListener, PatientAdapter.OnPatientClickListener {
    public static final String NEW_CONVERSATION = "conversatieNoua";

    FirebaseAuth auth = FirebaseAuth.getInstance();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://smiletogetherdentalapp-default-rtdb.firebaseio.com/");
    private final String idUserConnected = auth.getCurrentUser().getUid();

    private Toolbar toolbar;
    private FloatingActionButton fabNewConversation;

    private RelativeLayout rlNoConversation;
    private ProgressBar progressBar;

    private List<String> doctorsId = new ArrayList<>();
    private List<String>  patientsId= new ArrayList<>();

    private RecyclerView rwConversations;
    private List<Doctor> doctors = new ArrayList<>();
    private List<Patient> patients = new ArrayList<>();
    private RecyclerView.LayoutManager layoutManager;
    private DoctorsChatAdapter doctorsChatAdapter;
    private PatientsChatAdapter patientsChatAdapter;

    private String user_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversations);

        toolbar = findViewById(R.id.toolbar);
        fabNewConversation = findViewById(R.id.fabNewConversation);
        rlNoConversation = findViewById(R.id.rlNoConversations);
        rwConversations = findViewById(R.id.rwConversations);
        progressBar = findViewById(R.id.progressBar);

        setToolbar();

        setUserType();

        setRecyclerView();

        if (user_type.equals(PatientHomePageActivity.PATIENT)) {
            reference.child("Conversatii").addValueEventListener(takeDoctorsID());
        } else {
            reference.child("Conversatii").addValueEventListener(takePatinetsID());
        }

        fabNewConversation.setOnClickListener(this);
    }


    private void setUserType() {
        Intent intent = getIntent();
        if (intent.hasExtra(PatientHomePageActivity.PATIENT)) {
            user_type = intent.getStringExtra(PatientHomePageActivity.PATIENT);
        } else {
            user_type = intent.getStringExtra(DoctorHomePageActivity.DOCTOR);
        }
    }

    private ValueEventListener takePatinetsID() {
        loading(true);
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    if (chat.getMessages().get(0).getIdTransmitter().equals(idUserConnected)) {
                        patientsId.add(chat.getMessages().get(0).getIdReceiver());
                    } else if (chat.getMessages().get(0).getIdReceiver().equals(idUserConnected)) {
                        patientsId.add(chat.getMessages().get(0).getIdTransmitter());
                    }
                }

                if (patientsId.isEmpty()) {
                    loading(false);
                    rlNoConversation.setVisibility(View.VISIBLE);
                } else {
                    reference.child("user").addValueEventListener(takesPatients());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
    }

    private ValueEventListener takeDoctorsID() {
        loading(true);
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    if (chat.getMessages().get(0).getIdTransmitter().equals(idUserConnected)) {
                        doctorsId.add(chat.getMessages().get(0).getIdReceiver());
                    } else if (chat.getMessages().get(0).getIdReceiver().equals(idUserConnected)) {
                        doctorsId.add(chat.getMessages().get(0).getIdTransmitter());
                    }
                }

                if (doctorsId.isEmpty()) {
                    loading(false);
                    rlNoConversation.setVisibility(View.VISIBLE);
                } else {
                    reference.child("user").addValueEventListener(takesDoctors());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
    }

    private ValueEventListener takesPatients() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                patients.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Patient patient = dataSnapshot.getValue(Patient.class);
                    if (patientsId.contains(patient.getId())) {
                        patients.add(patient);
                    }
                }

                rwConversations.setVisibility(View.VISIBLE);

                loading(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
    }

    private ValueEventListener takesDoctors() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                doctors.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Doctor doctor = dataSnapshot.getValue(Doctor.class);
                    if (doctorsId.contains(doctor.getId())) {
                        doctors.add(doctor);
                    }
                }

                rwConversations.setVisibility(View.VISIBLE);

                loading(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
    }

    private void setRecyclerView() {
        rwConversations.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        rwConversations.setLayoutManager(layoutManager);

        if (user_type.equals(PatientHomePageActivity.PATIENT)) {
            setAdapterDoctors();
        } else {
            setAdapterPatients();
        }
    }

    private void setAdapterPatients() {
        patientsChatAdapter = new PatientsChatAdapter(patients, this, this);
        rwConversations.setAdapter(patientsChatAdapter);
    }

    private void setAdapterDoctors() {


        doctorsChatAdapter = new DoctorsChatAdapter(doctors, this, this);
        rwConversations.setAdapter(doctorsChatAdapter);
    }

    private void setToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fabNewConversation:
                if (user_type.equals(PatientHomePageActivity.PATIENT)) {
                    startActivity(new Intent(getApplicationContext(), ListOfDoctorsActivity.class).putExtra(NEW_CONVERSATION, ""));
                } else {
                    startActivity(new Intent(getApplicationContext(), ListOfPatientsActivity.class).putExtra(NEW_CONVERSATION, ""));
                }
                break;
        }
    }

    @Override
    public void onPatientClick(int position) {
        Patient patient = patients.get(position);
        startActivity(new Intent(getApplicationContext(), ChatActivity.class).putExtra(ListOfPatientsActivity.PATIENT, patient));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    @Override
    public void onDoctorClick(int position) {
        Doctor doctor = doctors.get(position);
        startActivity(new Intent(getApplicationContext(), ChatActivity.class).putExtra(ListOfDoctorsActivity.DOCTOR,doctor));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }

    private void loading(Boolean seIncarca) {
        if (seIncarca) {
            progressBar.setVisibility(View.VISIBLE);
        } else progressBar.setVisibility(View.GONE);
    }
}