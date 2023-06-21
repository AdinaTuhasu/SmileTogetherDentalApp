package com.example.smiletogether_dentalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smiletogether_dentalapp.Admin.AdminHomePageActivity;
import com.example.smiletogether_dentalapp.Authentification.RegisterActivity;
import com.example.smiletogether_dentalapp.Doctor.DoctorHomePageActivity;
import com.example.smiletogether_dentalapp.Patient.PatientHomePageActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextInputEditText tietLoginEmail;
    private TextInputEditText tietLoginPassword;

    private TextView tv_create_account;

    private AppCompatButton btnLogin;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://smiletogetherdentalapp-default-rtdb.firebaseio.com/");

    private ProgressDialog progressDialog;

    private Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tietLoginEmail= findViewById(R.id.tietEmail);
        tietLoginPassword = findViewById(R.id.tietPassword);
        tv_create_account = findViewById(R.id.tvCreateAccount);
        btnLogin = findViewById(R.id.btnLogin);


        intent = getIntent();


        tv_create_account.setOnClickListener(this);
        btnLogin.setOnClickListener(this);

    }


    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tvCreateAccount:
                startActivity(new Intent(getApplicationContext(), RegisterActivity.class));
                break;

            case R.id.btnLogin:
               connectUser();
                break;

        }
    }

    private void connectUser() {
        String email = tietLoginEmail.getText().toString().trim();
        String password = tietLoginPassword.getText().toString().trim();

        if (email.isEmpty()) {
            tietLoginEmail.setError(getString(R.string.err_empty_email));
            tietLoginEmail.requestFocus();
            return;
        }



        if (password.isEmpty()) {
            tietLoginPassword.setError(getString(R.string.err_empty_parola));
            tietLoginPassword.requestFocus();
            return;
        }


        progressDialog = new ProgressDialog(MainActivity.this, R.style.ProgressDialogStyle);
        progressDialog.setMessage("Se verifică credețialele...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {

                    if(email.equals("admin@gmail.com") && password.equals("Admin1234")) {
                        Toast.makeText(MainActivity.this, "Admin logat cu succes", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this, AdminHomePageActivity.class));
                        finish();
                    }
                    else {
                        Toast.makeText(MainActivity.this, "Utilizator logat cu succes", Toast.LENGTH_SHORT).show();
                        String userID = auth.getCurrentUser().getUid();
                        reference.child("user").child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                String role = snapshot.child("role").getValue(String.class);
                                if ("Doctor".equals(role)) {
                                    startActivity(new Intent(MainActivity.this, DoctorHomePageActivity.class));
                                    finish();
                                } else if ("Pacient".equals(role)) {
                                    startActivity(new Intent(MainActivity.this, PatientHomePageActivity.class));
                                    finish();

                                }

                            }


                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                                Log.e("preluareUser", error.getMessage());
                            }
                        });

                    }
                }
                else{
                    Toast.makeText(MainActivity.this, "Credențiale invalide!", Toast.LENGTH_SHORT).show();
                }
                progressDialog.dismiss();

            }
        });



    }
}
