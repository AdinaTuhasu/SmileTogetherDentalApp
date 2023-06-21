package com.example.smiletogether_dentalapp.Admin;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.smiletogether_dentalapp.Model.Investigation;
import com.example.smiletogether_dentalapp.Model.Speciality;
import com.example.smiletogether_dentalapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AdminAddServicesActivity extends AppCompatActivity {
    EditText addSpecialityName,addInvestigationName,addPrice;
    Button button_addSpeciality, button_addInvestigation,button_modifyprice;
    List<Investigation> investigations = new ArrayList<>();
    private Speciality currentSpeciality;




    DatabaseReference reference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://smiletogetherdentalapp-default-rtdb.firebaseio.com/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_add_services);

        //initialization

        addSpecialityName = findViewById(R.id.tietAddSpecialityName);
        addInvestigationName = findViewById(R.id.tietAddInvestigationName);
        addPrice = findViewById(R.id.tietPrice);
        button_addSpeciality = findViewById(R.id.buttonAddSpeciality);
        button_addInvestigation = findViewById(R.id.buttonAddInvestigation);
        button_modifyprice=findViewById(R.id.buttonModifyPrice);



        button_addSpeciality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String specialityName = addSpecialityName.getText().toString().trim();


                DatabaseReference specialityRef = reference.child("specialitati");
                Query query = specialityRef.orderByChild("name").equalTo(specialityName);
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Toast.makeText(getApplicationContext(), "Specialitatea deja există", Toast.LENGTH_SHORT).show();
                        } else {
                            Speciality speciality = new Speciality(null, specialityName, investigations);
                            String idSpeciality = reference.push().getKey();
                            speciality.setidSpeciality(idSpeciality);
                            reference.child("specialitati").child(speciality.getidSpeciality()).setValue(speciality);
                            addSpecialityName.setText("");


                            currentSpeciality = speciality;
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext(), "Eroare la interogare: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });


        button_addInvestigation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String investigationName = addInvestigationName.getText().toString().trim();
                double price = Double.parseDouble(addPrice.getText().toString().trim());

                Query query = reference.child("specialitati").orderByChild("name").equalTo(addSpecialityName.getText().toString().trim());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Speciality speciality = snapshot.getValue(Speciality.class);
                                if (speciality != null) {
                                    Investigation i = new Investigation(investigationName, price);
                                    speciality.getInvestigations().add(i);


                                    reference.child("specialitati").child(snapshot.getKey()).setValue(speciality);


                                    addInvestigationName.setText("");
                                    addPrice.setText("");
                                    break;
                                }
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Specialitatea nu există în baza de date", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext(), "Eroare la interogare: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        button_modifyprice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String investigationName = addInvestigationName.getText().toString().trim();
                double newPrice = Double.parseDouble(addPrice.getText().toString().trim());


                Query query = reference.child("specialitati").orderByChild("name").equalTo(addSpecialityName.getText().toString().trim());
                query.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {

                            for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                                Speciality speciality = snapshot.getValue(Speciality.class);
                                if (speciality != null) {

                                    for (Investigation investigation : speciality.getInvestigations()) {
                                        if (investigation != null && investigation.getname() != null && investigation.getname().equals(investigationName)) {

                                            investigation.setprice(newPrice);


                                            reference.child("specialitati").child(snapshot.getKey()).setValue(speciality);


                                            addInvestigationName.setText("");
                                            addPrice.setText("");
                                            return;
                                        }
                                    }

                                    Toast.makeText(getApplicationContext(), "Investigația nu există în specialitate", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }
                        } else {
                            Toast.makeText(getApplicationContext(), "Specialitatea nu există în baza de date", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(getApplicationContext(), "Eroare la interogare: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });





    }}


