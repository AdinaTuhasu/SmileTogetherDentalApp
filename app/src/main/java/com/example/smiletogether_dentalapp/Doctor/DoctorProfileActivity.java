package com.example.smiletogether_dentalapp.Doctor;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TimePicker;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.example.smiletogether_dentalapp.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.example.smiletogether_dentalapp.Model.Doctor;
import com.example.smiletogether_dentalapp.Model.Speciality;
import com.example.smiletogether_dentalapp.Model.WorkDay;
import com.example.smiletogether_dentalapp.R;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;


public class DoctorProfileActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int REQUEST_CODE = 200;


    private TextInputEditText DoctorLastname;
    private TextInputEditText DoctorFirstname;
    private TextInputEditText DoctorNrPhone;
    private TextInputEditText DoctorEmail;
    private TextInputEditText DoctorSchedule;

    private AutoCompleteTextView DoctorProfessionalDegrees;
    private AutoCompleteTextView DoctorSpecialization;

    private CircleImageView ciwProfilePhoto;
    private Uri uri;

    private Button btnModifyData;
    private Button btnSaveData;
    private Button btnCancelData;
    private LinearLayout llButtons;



    private Toolbar toolbar;
    private FirebaseUser doctorConnected;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://smiletogetherdentalapp-default-rtdb.firebaseio.com/");
    private String idUserConnected;
    private StorageReference storage = FirebaseStorage.getInstance().getReference();




    private List<Speciality> speciality;
    private List<String> speciality_name;
    private List<WorkDay> schedule=new ArrayList<>();

    private Doctor doctor;




    private TextInputEditText StartTime;
    private TextInputEditText EndTime;
    private AppCompatButton btnAddSchedule;
    private AppCompatButton btnCancel;
    private AutoCompleteTextView WorkDays;
    private AppCompatButton btnRemove;
    private AppCompatButton btnDeleteAccount;

    private AlertDialog dialogDeleteAccount;
    private AlertDialog dialogSchedule;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_profile);

        //initialization
        toolbar = findViewById(R.id.toolbar);


        DoctorLastname = findViewById(R.id.tietDoctorLastname);
        DoctorFirstname = findViewById(R.id.tietDoctorFirstname);
        DoctorNrPhone = findViewById(R.id.tietDoctorNrPhone);
        DoctorEmail = findViewById(R.id.tietDoctorEmail);
        DoctorSchedule = findViewById(R.id.tietWorkScheduleDoctor);
        ciwProfilePhoto=findViewById(R.id.ciwProfilePhotoDoctor);




        DoctorProfessionalDegrees = findViewById(R.id.actvDoctorProfessionalDegrees);
        DoctorSpecialization = findViewById(R.id.actvSpecializations);
        speciality = new ArrayList<>();
        schedule = new ArrayList<>();

        btnModifyData = findViewById(R.id.btnModifyDataDoctor);
        btnSaveData = findViewById(R.id.btnSaveDataDoctor);
        btnCancelData = findViewById(R.id.btnCancelDoctor);
        llButtons = findViewById(R.id.llButtonsDoctor);
        btnDeleteAccount=findViewById(R.id.btnDeleteAccount);

        doctorConnected = auth.getCurrentUser();
        idUserConnected = doctorConnected.getUid();


        setToolbar();

        setAdapterProfessionalDegrees();
        //setAdapterSpecialization();
        //reference.child("specialitati").addListenerForSingleValueEvent(takeSpeciality());


        setScheduleDialog();
        setDialogDeleteAccount();

        btnModifyData.setOnClickListener(this);
        btnSaveData.setOnClickListener(this);
        btnCancelData.setOnClickListener(this);
        btnDeleteAccount.setOnClickListener(this);
        DoctorSchedule.setOnClickListener(this);
        WorkDays.setOnClickListener(this);


        WorkDays.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (schedule.isEmpty()) {
                    btnAddSchedule.setEnabled(true);
                } else {
                    for (WorkDay day : schedule) {
                        if (day.getDay().equals(WorkDays.getText().toString())) {
                            btnAddSchedule.setVisibility(View.GONE);
                            btnRemove.setVisibility(View.VISIBLE);
                            break;
                        }
                        btnRemove.setVisibility(View.GONE);
                        btnAddSchedule.setVisibility(View.VISIBLE);
                        btnAddSchedule.setEnabled(true);
                    }
                }
            }
        });

        reference.child("user").child(idUserConnected).addListenerForSingleValueEvent(takeDoctor());
        System.out.println("USER: " + reference.child("user").child(idUserConnected));

    }


    private void setScheduleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Modifică program");

        View view = getLayoutInflater().inflate(R.layout.dialog_schedule_doctor, null);
        WorkDays = view.findViewById(R.id.WorkDays);
        StartTime = view.findViewById(R.id.StartTime);
        EndTime = view.findViewById(R.id.EndTime);
        btnAddSchedule = view.findViewById(R.id.btnAddSchedule);
        btnRemove = view.findViewById(R.id.btnRemoveSchedule);
        btnCancel = view.findViewById(R.id.btnCancelSchedule);

        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.dropdown_item,
                getResources().getStringArray(R.array.days_of_the_week));
        WorkDays.setAdapter(adapter);
        WorkDays.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                btnAddSchedule.setEnabled(true);
            }
        });
        StartTime.setOnClickListener(this);
        EndTime.setOnClickListener(this);
        btnAddSchedule.setOnClickListener(this);
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogSchedule.dismiss();
                clearInput();
            }
        });
        btnRemove.setOnClickListener(this);

        builder.setView(view);
        dialogSchedule = builder.create();
        dialogSchedule.setCanceledOnTouchOutside(false);
    }

    private void setDialogDeleteAccount() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Șterge contul");

        View view = getLayoutInflater().inflate(R.layout.dialog_delete_account, null);
        TextInputEditText tietParola = view.findViewById(R.id.tietParola);

        AppCompatButton btnDa = view.findViewById(R.id.btnDa);
        AppCompatButton btnNu = view.findViewById(R.id.btnNu);

        progressBar = view.findViewById(R.id.progressBar);

        btnNu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogDeleteAccount.dismiss();
            }
        });

        btnDa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String parola = tietParola.getText().toString().trim();
                if (parola.isEmpty()) {
                    tietParola.setError(getString(R.string.err_empty_parola));
                    tietParola.requestFocus();
                    return;
                }
                if (parola.length() < 6) {
                    tietParola.setError(getString(R.string.err_not_valid_parola));
                    tietParola.requestFocus();
                    return;
                }

                AuthCredential credential = EmailAuthProvider.getCredential(doctorConnected.getEmail(), parola);
                loading(true);
                doctorConnected.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d("reautentificareUser", "User re-authenticated.");
                                    doctorConnected.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            loading(false);
                                            if (task.isSuccessful()) {
                                                //todo nush cum sa fac cand dau start sa nu mai apara credentialele si cand ies din ap si intru iar sa nu ma mai duca la profil...
                                                SharedPreferences preferinteConectare = getSharedPreferences("salveazaDateConectare",
                                                        MODE_PRIVATE);
                                                SharedPreferences.Editor preferinteConectareEditor = preferinteConectare.edit();
                                                preferinteConectareEditor.clear();
                                                preferinteConectareEditor.commit();

                                                Toast.makeText(getApplicationContext(), "Contul a fost șters cu succes!",
                                                        Toast.LENGTH_SHORT).show();
                                                Log.d("stergereCont", "Contul a fost sters.");
                                                reference.child("user").child(idUserConnected).child("contSters").setValue(true);
                                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                                finish();
                                            } else {
                                                Log.e("stergereCont", task.getException().getMessage());
                                                Toast.makeText(getApplicationContext(),
                                                        "A intervenit o eroare. Contul nu a putut fi șters!", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });

                                    tietParola.setText("");
                                    dialogDeleteAccount.dismiss();
                                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                    finish();
                                } else {
                                    loading(false);
                                    Log.e("reautentificareUser", task.getException().getMessage());
                                    tietParola.setError("Parola nu este corectă!");
                                    tietParola.requestFocus();
                                }
                            }
                        });
            }
        });

        builder.setView(view);
        dialogDeleteAccount = builder.create();
        dialogDeleteAccount.setCanceledOnTouchOutside(false);
    }




    private void loading(@NonNull Boolean load) {
        if (load) {
            progressBar.setVisibility(View.VISIBLE);
        } else progressBar.setVisibility(View.GONE);
    }

    private void setAdapterSpecialization() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.dropdown_item,speciality_name);
        DoctorSpecialization.setAdapter(adapter);
    }

    private void setAdapterProfessionalDegrees() {
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.dropdown_item,
                getResources().getStringArray(R.array.professional_degree));
        DoctorProfessionalDegrees.setAdapter(adapter);
    }

    private ValueEventListener takeDoctor() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                doctor = snapshot.getValue(Doctor.class);

                //System.out.println("Doc: "+snapshot);


                String lastname = doctor.getlastname();
                String firstname = doctor.getFirstname();
                String nrPhone =doctor.getNrPhone();
                String email =doctor.getEmail();
                String professional_degree=doctor.getprofessionalDegree();
                String urlPhotoPofile=doctor.getUrlProfilePhoto();
                //System.out.println("Doc: "+doctor.getprofessionalDegree());

                if (professional_degree == null || professional_degree.isEmpty()) {
                    DoctorProfessionalDegrees.setText("Nespecificat");
                }

                    schedule = new ArrayList<>();
                    schedule.addAll(doctor.getSchedule());
                    StringBuilder scheduleString = new StringBuilder();
                    for (int i = 0; i < schedule.size(); i++) {
                        scheduleString.append(schedule.get(i).toString());
                        if (i != schedule.size() - 1) {
                            scheduleString.append("\n");
                        }

                    }
                    DoctorSchedule.setText(scheduleString.toString());
                    DoctorLastname.setText(lastname);
                    DoctorFirstname.setText(firstname);
                    DoctorNrPhone.setText(nrPhone);
                    DoctorEmail.setText(email);


                    ArrayAdapter<String> adapter_pd = (ArrayAdapter<String>) DoctorProfessionalDegrees.getAdapter();
                    for (int i = 0; i < adapter_pd.getCount(); i++) {
                        if (adapter_pd.getItem(i).equals(professional_degree)) {
                            DoctorProfessionalDegrees.setText(adapter_pd.getItem(i));
                            break;
                        }
                    }
                if (urlPhotoPofile!=null && !urlPhotoPofile.equals("")) {
                    Glide.with(getApplicationContext()).load(urlPhotoPofile).into(ciwProfilePhoto);
                } else {
                Glide.with(getApplicationContext()).load(R.drawable.profile_photo).into(ciwProfilePhoto);
            }


                    reference.child("specialitati").addListenerForSingleValueEvent(takeSpeciality());


                }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("preluareMedic", error.getMessage());
            }
        };
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

    private ValueEventListener takeSpeciality() {
        return new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot snapshot) {
            speciality.clear();
            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                Speciality s = dataSnapshot.getValue(Speciality.class);
                speciality.add(s);
            }

                speciality_name = new ArrayList<>();
                int poz = -1;
                for (int i = 0; i < speciality.size(); i++) {
                    if (speciality.get(i).getidSpeciality().equals(doctor.getidSpeciality())) {
                        poz = i;
                    }
                    speciality_name.add(speciality.get(i).getname());
                }
                if(poz == (-1)){
                    DoctorSpecialization.setText("Nespecificată");
                }
                else{
                setAdapterSpecialization();
                DoctorSpecialization.setText(speciality_name.get(poz));
                }
            }



        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Log.e("preluareSpecialitati", error.getMessage());
        }
    };
}



    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnModifyDataDoctor:
                setAdapterProfessionalDegrees();
                setAdapterSpecialization();
                ciwProfilePhoto.setOnClickListener(this);
                setAccessibility(true);
                setVisibilityButtons(View.GONE, View.VISIBLE);
                break;
            case R.id.btnSaveDataDoctor:
                updateData();
                break;
            case R.id.btnCancelDoctor:
                reference.child("user").child(idUserConnected).addListenerForSingleValueEvent(takeDoctor());
                setAccessibility(false);
                setVisibilityButtons(View.VISIBLE, View.GONE);
                break;

            case R.id.btnDeleteAccount:
                dialogDeleteAccount.show();
                break;

            case R.id.tietWorkScheduleDoctor:
                DoctorSchedule.setError(null);
                dialogSchedule.show();
                break;
            case R.id.WorkDays:
                WorkDays.setError(null);
                break;

            case R.id.ciwProfilePhotoDoctor:
                chooseTheProfilePhoto();
                break;
            case R.id.btnRemoveSchedule:
                AlertDialog dialog = new AlertDialog.Builder(DoctorProfileActivity.this)
                        .setTitle("Confirmare ștergere zi din program")
                        .setMessage("Ștergeți ziua de " + WorkDays.getText().toString() + " din program?")
                        .setNegativeButton("Nu", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        })
                        .setPositiveButton("Da", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                schedule.removeIf(day -> day.getDay().equals(WorkDays.getText().toString()));
                                StringBuilder scheduleString = new StringBuilder();
                                for (int j = 0; j < schedule.size(); j++) {
                                    scheduleString.append(schedule.get(j).toString());
                                    if (j != schedule.size() - 1) {
                                        scheduleString.append("\n");
                                    }
                                }
                                DoctorSchedule.setText(scheduleString.toString());
                                dialogInterface.cancel();
                                dialogSchedule.dismiss();
                                clearInput();
                            }
                        }).create();
                dialog.show();
                break;
            case R.id.StartTime:
                StartTime.setError(null);
                displayTimePicker(StartTime, getString(R.string.start_time));
                break;
            case R.id.EndTime:
                EndTime.setError(null);
                displayTimePicker(EndTime, getString(R.string.end_time));
                break;
            case R.id.btnAddSchedule:
               /* if (schedule.stream().anyMatch(day -> day.getDay().equals(WorkDays.getText().toString()))) {
                    WorkDays.setError("Ziua de " + WorkDays.getText().toString() + " există deja în program!");
                    WorkDays.requestFocus();
                    return;
                }*/

                if (StartTime.getText().toString().isEmpty()) {
                    //ca sa apara si textul trb sa-i pun din xml focusable pe true
                    //dar asa imi apare tastatura la primuk click pe tiet uof
                    StartTime.setError("Selectați ora de început!");
                    StartTime.requestFocus();
                    return;
                }

                if (EndTime.getText().toString().isEmpty()) {
                    EndTime.setError("Selectați ora de sfârșit!");
                    EndTime.requestFocus();
                    return;
                }

                WorkDay workDay = new WorkDay(WorkDays.getText().toString(),StartTime.getText().toString(),
                        EndTime.getText().toString());

                schedule.add(workDay); //todo sa sortez lista dupa zilele saptamanii

                StringBuilder programString = new StringBuilder();
                for (int i = 0; i < schedule.size(); i++) {
                    programString.append(schedule.get(i).toString());
                    if (i != schedule.size() - 1) {
                        programString.append("\n");
                    }
                }
                DoctorSchedule.setText(programString.toString());
                dialogSchedule.dismiss();
                clearInput();
                break;
        }
    }

    private void clearInput() {
        WorkDays.setText("");
        WorkDays.clearFocus();
        WorkDays.setError(null);

        StartTime.setText("");
        StartTime.setError(null);

        EndTime.setText("");
        EndTime.setError(null);

        btnAddSchedule.setVisibility(View.VISIBLE);
        btnAddSchedule.setEnabled(false);
        btnRemove.setVisibility(View.GONE);
    }


    private void displayTimePicker(TextInputEditText tiet, String title) {
        Calendar calendar = Calendar.getInstance();
        Date dataDefault = null;
        try {
            dataDefault = new SimpleDateFormat("HH:mm", Locale.US).parse("08:00");
        } catch (ParseException e) {
            e.printStackTrace();
        }
        calendar.setTime(dataDefault);
        int hour = calendar.get(Calendar.HOUR);
        int minute= calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(DoctorProfileActivity.this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int ora, int minut) {
                String timp = convertInt(ora) + ":" + convertInt(minut);
                tiet.setText(timp);
            }
        }, hour, minute, true);
        timePickerDialog.setTitle(title);
        timePickerDialog.show();
    }

    private String convertInt(int i) {
        if (i >= 10) {
            return String.valueOf(i);
        }
        return "0" + i;
    }

    private void chooseTheProfilePhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent date) {
        super.onActivityResult(requestCode, resultCode, date);
        // in data am poza aleasa
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && date != null) {
            uri = date.getData();
            ciwProfilePhoto.setImageURI(uri); // setez poza pe care a ales-o
        }
    }



    private void updateData() {
        if (validInput()) {
            String lastname = DoctorLastname.getText().toString().trim();
            String firstname =DoctorFirstname.getText().toString().trim();
            String NrPhone=DoctorNrPhone.getText().toString().trim();
            String professionalDegree = DoctorProfessionalDegrees.getText().toString();

            String Speciality = DoctorSpecialization.getText().toString();
            String idSpeciality = "";
            for (Speciality s : speciality) {
                if (s.getname().equals(Speciality)) {
                    idSpeciality = s.getidSpeciality();
                    break;
                }
            }


            if (lastname.equals(doctor.getlastname()) && firstname.equals(doctor.getFirstname())
                    && NrPhone== doctor.getNrPhone() && schedule== doctor.getSchedule() && professionalDegree==doctor.getprofessionalDegree() && idSpeciality==doctor.getidSpeciality() && uri==null) {

                btnCancelData.callOnClick();
                ciwProfilePhoto.setOnClickListener(null);
                Toast.makeText(getApplicationContext(), "Informațiile nu au fost modificate!", Toast.LENGTH_SHORT).show();
                return;
            }

            reference.child("user").child(idUserConnected).child("lastname").setValue(lastname);
            reference.child("user").child(idUserConnected).child("firstname").setValue(firstname);
            reference.child("user").child(idUserConnected).child("nrPhone").setValue(NrPhone);
            reference.child("user").child(idUserConnected).child("professionalDegree").setValue(professionalDegree);
            reference.child("user").child(idUserConnected).child("idSpeciality").setValue(idSpeciality);
            reference.child("user").child(idUserConnected).child("schedule").setValue(schedule);


            if (uri != null) {
                uploadTheProfilePhoto();
            }
            setAccessibility(false);
            setVisibilityButtons(View.VISIBLE, View.GONE);
            ciwProfilePhoto.setOnClickListener(null);
            Toast.makeText(getApplicationContext(), "Datele au fost actualizate!", Toast.LENGTH_SHORT).show();

        }

    }
    private void uploadTheProfilePhoto() {
        Bitmap bmp = null;
        try {
            bmp = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 20, out);
        byte[] bytes = out.toByteArray();
        UploadTask upload =storage.child("profile photo").child(idUserConnected).putBytes(bytes);

        upload.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(getApplicationContext(), "Nu s-a putut încărca poza de profil!", Toast.LENGTH_SHORT).show();
            }
        });

        upload.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                if (taskSnapshot.getMetadata() != null && taskSnapshot.getMetadata().getReference() != null) {
                    Task<Uri> result = taskSnapshot.getStorage().getDownloadUrl();
                    result.addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Map mapPhoto = new HashMap();
                            mapPhoto.put("urlProfilePhoto", uri.toString());

                            reference.child("user").child(idUserConnected).updateChildren(mapPhoto).addOnCompleteListener(new OnCompleteListener() {
                                @Override
                                public void onComplete(@NonNull Task task) {
                                    if (task.isSuccessful()) {
                                        Log.i("incarcarePoza", "Poza a fost incarcata cu succes!");
                                    } else {
                                        Toast.makeText(getApplicationContext(), "Poza de profil nu a putut fi incarcata!", Toast.LENGTH_SHORT).show();
                                        Log.e("incarcarePoza", task.getException().getMessage());
                                    }
                                }
                            });
                        }
                    });
                }
            }
        });
    }



    private boolean validInput() {
       /* if (DoctorLastname.getText().toString().isEmpty()) {
            DoctorLastname.setError(getString(R.string.err_empty_nume));
            DoctorLastname.requestFocus();
            return false;
        }

        if (DoctorFirstname.getText().toString().isEmpty()) {
            DoctorFirstname.setError(getString(R.string.err_empty_prenume));
            DoctorFirstname.requestFocus();
            return false;
        }*/

        Pattern pattern = Pattern.compile(getString(R.string.pattern_numar_telefon));
        Matcher matcher = pattern.matcher(DoctorNrPhone.getText().toString());
        if (!DoctorNrPhone.getText().toString().isEmpty() && !matcher.matches()) {
            DoctorNrPhone.setError(getString(R.string.err_not_valid_telefon));
            DoctorNrPhone.requestFocus();
            return false;
        }

        return true;
    }

    private void setVisibilityButtons(int v1, int v2) {
        btnModifyData.setVisibility(v1);
        llButtons.setVisibility(v2);


    }

    private void setAccessibility(boolean b) {
        DoctorNrPhone.setEnabled(b);
        DoctorSchedule.setEnabled(b);
        DoctorProfessionalDegrees.setEnabled(b);
        DoctorSpecialization.setEnabled(b);

        btnDeleteAccount.setEnabled(!b);


    }
}