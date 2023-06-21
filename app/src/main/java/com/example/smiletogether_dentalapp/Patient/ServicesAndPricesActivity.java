package com.example.smiletogether_dentalapp.Patient;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.example.smiletogether_dentalapp.Adapter.InvestigationAdapter;
import com.example.smiletogether_dentalapp.Model.Investigation;
import com.example.smiletogether_dentalapp.Model.Speciality;
import com.example.smiletogether_dentalapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


public class ServicesAndPricesActivity extends AppCompatActivity {
    private AutoCompleteTextView actvSpecialities;
    private ProgressBar progressBar;

    private Toolbar toolbar;

    private LinearLayout llSelectSpeciality;

    private List<Investigation> investigations;
    private RecyclerView rwInvestigationList;
    private InvestigationAdapter AdapterInvestigation;
    private RecyclerView.LayoutManager layoutManager;

    private AdapterView.OnItemSelectedListener onItemSelectedListener;
    private FirebaseUser patientConnected;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://smiletogetherdentalapp-default-rtdb.firebaseio.com/");

    private EditText etSearchInvestigation;
    private RelativeLayout rlNoInvestigation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_services_and_prices);

        //initialization
        progressBar = findViewById(R.id.progressBar);
        toolbar = findViewById(R.id.toolbar);
        actvSpecialities = findViewById(R.id.actvSpecialities);
        llSelectSpeciality = findViewById(R.id.llSelectSpeciality);
        rwInvestigationList = findViewById(R.id.rwInvestigationsList);
        investigations = new ArrayList<>();
        etSearchInvestigation = findViewById(R.id.etSearchInvestigation);
        rlNoInvestigation = findViewById(R.id.rlNoInvestigation);

        seteazaToolbar();

        reference.child("specialitati").addListenerForSingleValueEvent(takeSpeciality());


        seteazaRecyclerView();

    }




    private void seteazaRecyclerView() {
        layoutManager = new LinearLayoutManager(this);
        rwInvestigationList.setLayoutManager(layoutManager);
        AdapterInvestigation = new InvestigationAdapter(investigations, this);
        rwInvestigationList.setAdapter(AdapterInvestigation);
    }

    private void seteazaToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }


    private ValueEventListener takeSpeciality() {
        loading(true);
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Speciality> specialities = new ArrayList<>();
                List<String> nameSpeciality = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Speciality s = dataSnapshot.getValue(Speciality.class);
                    specialities.add(s);
                    nameSpeciality.add(s.getname());
                }

                ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(),
                        R.layout.dropdown_item, nameSpeciality);
                actvSpecialities.setAdapter(adapter);
                loading(false);

                actvSpecialities.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                        etSearchInvestigation.setFocusable(true);
                        etSearchInvestigation.setFocusableInTouchMode(true);
                        etSearchInvestigation.setText("");
                        rlNoInvestigation.setVisibility(View.GONE);
                        for (Speciality s : specialities)
                            if (actvSpecialities.getText().toString().equals(s.getname())) {
                                investigations = s.getInvestigations();
                                setAdapter(investigations);

                                rwInvestigationList.setVisibility(View.VISIBLE);
                                llSelectSpeciality.setVisibility(View.GONE);
                                break;
                            }
                    }
                });

                etSearchInvestigation.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        String textSearch = charSequence.toString().toLowerCase();
                        List<Investigation> investigationFiltered = new ArrayList<>();
                        for (Investigation inv : investigations) {
                            if (inv != null && inv.getname() != null && inv.getname().toLowerCase().contains(textSearch)) {
                                investigationFiltered.add(inv);
                            }
                        }

                        if (investigationFiltered.isEmpty()) {
                            rlNoInvestigation.setVisibility(View.VISIBLE);
                            rwInvestigationList.setVisibility(View.GONE);
                        } else {
                            rlNoInvestigation.setVisibility(View.GONE);
                            rwInvestigationList.setVisibility(View.VISIBLE);
                            setAdapter(investigationFiltered);
                        }
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("preluareSpecialitati", error.getMessage());
            }
        };
        }

    private void setAdapter(List<Investigation> investigations) {
        AdapterInvestigation = new InvestigationAdapter(investigations, getApplicationContext());
        rwInvestigationList.setAdapter(AdapterInvestigation);
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

    private void loading(Boolean seIncarca) {
        if (seIncarca) {
            progressBar.setVisibility(View.VISIBLE);
        } else progressBar.setVisibility(View.GONE);
    }

}