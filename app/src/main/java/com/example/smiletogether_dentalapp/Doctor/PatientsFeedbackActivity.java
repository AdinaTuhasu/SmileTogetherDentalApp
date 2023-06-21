package com.example.smiletogether_dentalapp.Doctor;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.smiletogether_dentalapp.Adapter.FeedbackAdapter;
import com.example.smiletogether_dentalapp.Model.Appointment;
import com.example.smiletogether_dentalapp.Model.Patient;
import com.example.smiletogether_dentalapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class PatientsFeedbackActivity extends AppCompatActivity {

    private String idPatient;
    private Toolbar toolbar;
    private Intent intent;
    private TextView tvTitle;
    private Patient patient;
    private RecyclerView rwListOfFeedback;
    private FeedbackAdapter adapter;
    private GridLayoutManager gridLayoutManager;
    private List<Appointment> appointmentsWithFeedback;
    private RelativeLayout rlNoFeedback;
    private ProgressBar progressBar;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://smiletogetherdentalapp-default-rtdb.firebaseio.com/");
    String idDoctorConnected=auth.getCurrentUser().getUid();


    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patients_feedback);

        //initializeaza atribute
        toolbar = findViewById(R.id.toolbar);
        tvTitle = findViewById(R.id.tvTitle);
        rwListOfFeedback = findViewById(R.id.rwListOfFeedback);
        rlNoFeedback = findViewById(R.id.rlNoFeedback);
        progressBar = findViewById(R.id.progressBar);
        intent = getIntent();
        patient = (Patient) intent.getSerializableExtra(ListOfPatientsActivity.PATIENT);
        idPatient = patient.getId();
        appointmentsWithFeedback = new ArrayList<>();

        setToolbar();
        setRecyclerView();

        reference.child("programari").addValueEventListener(takesAppointmentsWithFeedback());
    }

    private ValueEventListener takesAppointmentsWithFeedback() {
        loading(true);
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                appointmentsWithFeedback.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Appointment appointment = dataSnapshot.getValue(Appointment.class);
                    if (appointment.getDoctorsId().equals(idDoctorConnected) && appointment.getPatientsId().equals(idPatient)
                            && appointment.getFeedback() != null) {
                        appointmentsWithFeedback.add(appointment);
                    }
                }

                if (appointmentsWithFeedback.isEmpty()) {
                    rlNoFeedback.setVisibility(View.VISIBLE);
                    rwListOfFeedback.setVisibility(View.GONE);
                } else {
                    rlNoFeedback.setVisibility(View.GONE);
                    setAdapter();
                    rwListOfFeedback.setVisibility(View.VISIBLE);
                }

                loading(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
    }

    private void loading(Boolean seIncarca) {
        if (seIncarca) {
            progressBar.setVisibility(View.VISIBLE);
        } else progressBar.setVisibility(View.GONE);
    }

    private void setRecyclerView() {
        rwListOfFeedback.setHasFixedSize(true);
        gridLayoutManager = new GridLayoutManager(this, 2, GridLayoutManager.VERTICAL, false);
        rwListOfFeedback.setLayoutManager(gridLayoutManager);
        setAdapter();
    }

    private void setAdapter() {
        adapter = new FeedbackAdapter(appointmentsWithFeedback);
        rwListOfFeedback.setAdapter(adapter);
    }

    private void setToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        String title = "Feedback " + patient.getlastname() + " " + patient.getFirstname();
        tvTitle.setText(title);
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
}