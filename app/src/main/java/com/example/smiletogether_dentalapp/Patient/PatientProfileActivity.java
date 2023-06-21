package com.example.smiletogether_dentalapp.Patient;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.smiletogether_dentalapp.MainActivity;
import com.example.smiletogether_dentalapp.Model.Patient;
import com.example.smiletogether_dentalapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
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
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;

public class PatientProfileActivity extends AppCompatActivity implements View.OnClickListener {
    public static final int REQUEST_CODE = 200;


    private TextInputEditText tietLastname;
    private TextInputEditText tietFirstname;
    private TextInputEditText tietDateOfBirth;
    private TextInputEditText tietPhoneNr;
    private TextInputEditText tietAddress;
    private TextInputEditText tietSex;
    private TextInputEditText tietEmail;


    private Button btnModifyData;
    private Button btnSaveData;
    private Button btnCancel;
    private LinearLayout llButtons;

    private AppCompatButton btnDeleteAccount;

    private CircleImageView ciwProfilePhoto;
    private Uri uri;

    private FirebaseUser patientConnected;
    private String idUserConnected;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://smiletogetherdentalapp-default-rtdb.firebaseio.com/");
    private Patient patient;

    StorageReference storage = FirebaseStorage.getInstance().getReference();

    private AlertDialog dialogDeleteAccount;

    private Toolbar toolbar;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_profile);

        //initialization
        toolbar = findViewById(R.id.toolbar);
        tietLastname = findViewById(R.id.tietLastnamePatient);
        tietFirstname = findViewById(R.id.tietFirstnamePatient);
        tietDateOfBirth = findViewById(R.id.tietBirthDatePatient);
        tietPhoneNr = findViewById(R.id.tietNrPhonePatient);
        tietAddress = findViewById(R.id.tietAddress);
        tietSex = findViewById(R.id.tietSex);
        tietEmail=findViewById(R.id.tietPatientEmail);
        ciwProfilePhoto=findViewById(R.id.ciwProfilePhotoPatient);
        btnDeleteAccount=findViewById(R.id.btnDeleteAccount_patient);




        btnModifyData = findViewById(R.id.btnModifyDataPatient);
        btnSaveData = findViewById(R.id.btnSaveDataPatient);
        btnCancel = findViewById(R.id.btnCancelPatient);
        llButtons= findViewById(R.id.llButtonsPatient);

        patientConnected= auth.getCurrentUser();
        idUserConnected= patientConnected.getUid();


        setToolbar();


        btnDeleteAccount.setOnClickListener(this);
        btnModifyData.setOnClickListener(this);
        btnSaveData.setOnClickListener(this);
        btnCancel.setOnClickListener(this);


        setDialogDeleteAccount();


        reference.child("user").child(idUserConnected).addListenerForSingleValueEvent(takePatient());
    }


    private ValueEventListener takePatient() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                patient = snapshot.getValue(Patient.class);

                if (patient != null) {
                    String lastname = patient.getlastname();
                    String firstname = patient.getFirstname();
                    String sex = patient.getSex();
                    String date_of_birth = patient.getDateOfBirth();
                    String phoneNr = patient.getNrPhone();
                    String address = patient.getAddress();
                    String email=patient.getEmail();
                    String urlPhotoPofile=patient.getUrlProfilePhoto();

                    if (address == null || address.isEmpty()) {
                        tietAddress.setText("Nespecificat");
                    }
                    else{
                        tietAddress.setText(address);
                    }
                    tietLastname.setText(lastname);
                    tietFirstname.setText(firstname);
                    tietDateOfBirth.setText(date_of_birth);
                    tietPhoneNr.setText(phoneNr);
                    tietSex.setText(sex);
                    tietEmail.setText(email);

                    if (urlPhotoPofile!=null && !urlPhotoPofile.equals("")) {
                        Glide.with(getApplicationContext()).load(urlPhotoPofile).into(ciwProfilePhoto);
                    }else {
                        Glide.with(getApplicationContext()).load(R.drawable.profile_photo).into(ciwProfilePhoto);
                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("preluarePacient", error.getMessage());
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
//            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnModifyDataPatient:
                ciwProfilePhoto.setOnClickListener(this);
                setAccessibility(true);
                setVisibilityButtons(View.GONE, View.VISIBLE);
                break;
            case R.id.btnSaveDataPatient:
                updateData();
                break;

            case R.id.btnDeleteAccount_patient:
                dialogDeleteAccount.show();
                break;

            case R.id.btnCancelPatient:
                setAccessibility(false);
                setVisibilityButtons(View.VISIBLE, View.GONE);
                break;

            case R.id.ciwProfilePhotoPatient:
                chooseTheProfilePhoto();
                break;

        }
    }





    private void loading(@NonNull Boolean seIncarca) {
        if (seIncarca) {
            progressBar.setVisibility(View.VISIBLE);
        } else progressBar.setVisibility(View.GONE);
    }

    private void chooseTheProfilePhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent date) {
        super.onActivityResult(requestCode, resultCode, date);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && date != null) {
            uri = date.getData();
            ciwProfilePhoto.setImageURI(uri);
        }
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

                AuthCredential credential = EmailAuthProvider.getCredential(patientConnected.getEmail(), parola);
                loading(true);
                patientConnected.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Log.d("reautentificareUser", "User re-authenticated.");
                                    patientConnected.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            loading(false);
                                            if (task.isSuccessful()) {

                                                Toast.makeText(getApplicationContext(), "Contul a fost șters cu succes!",
                                                        Toast.LENGTH_SHORT).show();
                                                Log.d("stergereCont", "Contul a fost sters.");
                                                reference.child("user").child(idUserConnected).child("contSters").setValue(true);
                                                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                                                finish();
                                            } else {
                                                Log.e("stergereCont", task.getException().getMessage());
                                                Toast.makeText(getApplicationContext(),
                                                        "A intervenit o eroare. Contul nu a putut fi sters!", Toast.LENGTH_SHORT).show();
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

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void updateData() {
        if (inputValid()) {
            String lastname = tietLastname.getText().toString().trim();
            String firstname = tietFirstname.getText().toString().trim();
            String sex = tietSex.getText().toString().trim();
            String date_of_birth = tietDateOfBirth.getText().toString().trim();
            String phoneNr =tietPhoneNr.getText().toString().trim();
            String address =tietAddress.getText().toString().trim();



            if (lastname.equals(patient.getlastname()) && firstname.equals(patient.getFirstname()) && phoneNr.equals(patient.getNrPhone()) && sex.equals(patient.getSex()) && date_of_birth.equals(patient.getDateOfBirth()) &&address.equals(patient.getAddress()) && uri==null)  {
                setAccessibility(false);
                setVisibilityButtons(View.VISIBLE, View.GONE);
                ciwProfilePhoto.setOnClickListener(null);
                Toast.makeText(getApplicationContext(), "Informatiile nu au fost modificate!", Toast.LENGTH_SHORT).show();
                return;
            }

            reference.child("user").child(idUserConnected).child("lastname").setValue(lastname);
            reference.child("user").child(idUserConnected).child("firstname").setValue(firstname);
            reference.child("user").child(idUserConnected).child("nrPhone").setValue(phoneNr);
            reference.child("user").child(idUserConnected).child("dateOfBirth").setValue(date_of_birth);
            reference.child("user").child(idUserConnected).child("sex").setValue(sex);
            reference.child("user").child(idUserConnected).child("address").setValue(address);



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


    private boolean inputValid() {
        Pattern pattern;
        Matcher matcher;


        if (tietAddress.getText().toString().trim().isEmpty()) {
            tietAddress.setError("Introduceți adresa!");
            tietAddress.requestFocus();
            return false;
        }


        if (tietPhoneNr.getText().toString().trim().isEmpty()) {
            tietPhoneNr.setError(getString(R.string.err_empty_nrTelefon));
            tietPhoneNr.requestFocus();
            return false;
        }

        pattern = Pattern.compile(getString(R.string.pattern_numar_telefon));
        matcher = pattern.matcher(tietPhoneNr.getText().toString().trim());
        if (!matcher.matches()) {
            tietPhoneNr.setError(getString(R.string.err_not_valid_telefon));
            tietPhoneNr.requestFocus();
            return false;
        }


        return true;
    }



    private void setVisibilityButtons(int v1, int v2) {
        btnModifyData.setVisibility(v1);
        llButtons.setVisibility(v2);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void setAccessibility(boolean b) {
        tietPhoneNr.setEnabled(b);
        tietAddress.setEnabled(b);
        btnDeleteAccount.setEnabled(!b);

    }
}