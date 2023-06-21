package com.example.smiletogether_dentalapp.Doctor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.smiletogether_dentalapp.Adapter.PatientAdapter;
import com.example.smiletogether_dentalapp.ChatActivity;
import com.example.smiletogether_dentalapp.ConversationsActivity;
import com.example.smiletogether_dentalapp.Model.Appointment;
import com.example.smiletogether_dentalapp.Model.Doctor;
import com.example.smiletogether_dentalapp.Model.Patient;
import com.example.smiletogether_dentalapp.R;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;


public class ListOfPatientsActivity extends AppCompatActivity implements PatientAdapter.OnPatientClickListener, View.OnClickListener {
    FirebaseAuth auth = FirebaseAuth.getInstance();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://smiletogetherdentalapp-default-rtdb.firebaseio.com/");
    String idUserConnected = auth.getCurrentUser().getUid();

    private Toolbar toolbar;

    private HashSet<String> PatientIDs;
    private List<Patient> patients;

    private RecyclerView rwPatientList;
    private PatientAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    private ProgressBar progressBar;

    private RelativeLayout rlNoPatient;

    private TextView tvTitle;

    private Intent intent;


    private EditText etSearchPatient;

    private List<Patient> patientsFiltered = new ArrayList<>();

    private AppCompatButton btnGradePointAverage;


    public static final String PATIENT = "Pacient";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_of_patients);

        //initialization

        toolbar = findViewById(R.id.toolbar);
        rwPatientList = findViewById(R.id.rwListOfPatient);
        progressBar = findViewById(R.id.progressBar);
        rlNoPatient = findViewById(R.id.rlNoPatient);
        patients = new ArrayList<>();
        tvTitle = findViewById(R.id.tvTitle);
        btnGradePointAverage = findViewById(R.id.btnGradePointAverage);

        intent = getIntent();
        etSearchPatient = findViewById(R.id.etSearchPatient);


        //set Toolbar
        setToolbar();

        if (intent.hasExtra(DoctorHomePageActivity.SHOW_FEEDBACK)) {
            btnGradePointAverage.setVisibility(View.VISIBLE);
            tvTitle.setText(getString(R.string.feedback_pacienti));
        } else if (intent.hasExtra(ConversationsActivity.NEW_CONVERSATION)) {
            tvTitle.setText("Conversatie noua");
        }


        setRecyclerView();

        btnGradePointAverage.setOnClickListener(this);
        reference.child("programari").addValueEventListener(takePatientsId());

    }

    private void setAdapterPatient(List<Patient> patient) {
        List<Patient> toRemove = new ArrayList<>();
        for (Patient p : patients) {
            if (p.isDeletedAccount()) {
                toRemove.add(p);
            }
        }
        patients.removeAll(toRemove);
        adapter = new PatientAdapter(patient, this, this);
        rwPatientList.setAdapter(adapter);
    }


    private void setRecyclerView() {
        rwPatientList.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        rwPatientList.setLayoutManager(layoutManager);
        setAdapterPatient(patients);
    }




    private void loading(Boolean loading) {
        if (loading) {
            progressBar.setVisibility(View.VISIBLE);
        } else progressBar.setVisibility(View.GONE);
    }

    private ValueEventListener takePatientsId() {
        loading(true);
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                PatientIDs = new HashSet<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Appointment a = dataSnapshot.getValue(Appointment.class);
                    if (a != null && a.getDoctorsId().equals(idUserConnected)) {
                        PatientIDs.add(a.getPatientsId());
                    }
                }

                if (PatientIDs.isEmpty()) {
                    loading(false);
                    rlNoPatient.setVisibility(View.VISIBLE);
                    btnGradePointAverage.setEnabled(false);
                    btnGradePointAverage.setTextColor(getResources().getColor(R.color.light_blue));
                    btnGradePointAverage.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_pie_chart_light_blue_24, 0);

                } else {
                    reference.child("user").addValueEventListener(takesPatients());
                    btnGradePointAverage.setEnabled(true);
                    btnGradePointAverage.setTextColor(getResources().getColor(R.color.primary_color));
                    btnGradePointAverage.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_baseline_pie_chart_blue_24, 0);
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
                patients = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Patient p = dataSnapshot.getValue(Patient.class);
                    if (p != null) {
                        if (PatientIDs != null && PatientIDs.contains(p.getId())) {
                            patients.add(p);
                        }

                    }

                }

                setAdapterPatient(patients);
                rwPatientList.setVisibility(View.VISIBLE);
                loading(false);

                etSearchPatient.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }


                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        String stringCurrent = charSequence.toString().toLowerCase();
                        if (!stringCurrent.isEmpty()) {
                            ArrayList<Patient> filteredList = new ArrayList<>();
                            for (Patient patient : patients) {
                                if (patient.getlastname().toLowerCase().contains(stringCurrent)
                                        || patient.getFirstname().toLowerCase().contains(stringCurrent)
                                        || String.valueOf(patient.getNrPhone()).contains(stringCurrent)) {
                                    filteredList.add(patient);
                                }
                            }
                            displaysPatients(filteredList);
                        } else {
                            patientsFiltered.clear();
                            setAdapterPatient(patients);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
    }

    private void displaysPatients(List<Patient> patientsFiltered) {
        if (patientsFiltered.isEmpty()) {
            rlNoPatient.setVisibility(View.VISIBLE);
            rwPatientList.setVisibility(View.GONE);
        } else {
            rlNoPatient.setVisibility(View.GONE);
            rwPatientList.setVisibility(View.VISIBLE);
            setAdapterPatient(patientsFiltered);
        }
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

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {

            case R.id.btnGradePointAverage:
                startActivity(new Intent(getApplicationContext(), PieChartGradesActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
        }

    }

    @Override
    public void onPatientClick(int position) {
        Patient patient;
        if (patientsFiltered.isEmpty()) {
            patient = patients.get(position);
        } else {
            patient = patientsFiltered.get(position);
        }

        if (intent.hasExtra(DoctorHomePageActivity.SHOW_FEEDBACK)) {
            startActivity(new Intent(getApplicationContext(), PatientsFeedbackActivity.class).putExtra(PATIENT, patient));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }  else if (intent.hasExtra(ConversationsActivity.NEW_CONVERSATION)) {
            startActivity(new Intent(getApplicationContext(), ChatActivity.class).putExtra(PATIENT, patient));
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        }


    }


}