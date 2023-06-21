package com.example.smiletogether_dentalapp.Authentification;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatTextView;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smiletogether_dentalapp.MainActivity;
import com.example.smiletogether_dentalapp.Model.User;
import com.example.smiletogether_dentalapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    private TextInputEditText lastName;
    private TextInputEditText firstName;
    private TextInputEditText nrPhone;
    private TextInputEditText email;
    private TextInputEditText dateOfBirth;
    private TextInputEditText password;
    private TextInputEditText confirmPassword;
    private AlertDialog dialog;  // declare the dialog variable

    private AutoCompleteTextView actvSex;

    private List<String> sex;

    private AppCompatButton btnRegister;
    private AppCompatTextView btnAlreadyHaveAnAccount;
    private RadioButton radio_button_patient, radio_button_doctor;

    FirebaseAuth auth = FirebaseAuth.getInstance();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://smiletogetherdentalapp-default-rtdb.firebaseio.com/");


    User user;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        //Initializare
        lastName= findViewById(R.id.Lastname);
        firstName = findViewById(R.id.Firstname);
        nrPhone= findViewById(R.id.NrPhone);
        email = findViewById(R.id.Email);
        dateOfBirth = findViewById(R.id.date_of_birth);
        actvSex = findViewById(R.id.Sex);
        password = findViewById(R.id.Password);
        confirmPassword = findViewById(R.id.confirm_password);


        btnRegister = findViewById(R.id.btnRegister);
        btnAlreadyHaveAnAccount = findViewById(R.id.btn_already_have_an_account);
        radio_button_doctor = findViewById(R.id.rbDoctor);
        radio_button_patient = findViewById(R.id.rbPatient);

        sex = new ArrayList<>();

        //sex
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.dropdown_item,
                getResources().getStringArray(R.array.sex));
        actvSex.setAdapter(adapter);

        dateOfBirth.setOnClickListener(this);
        btnRegister.setOnClickListener(this);


        btnAlreadyHaveAnAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                finish();

            }
        });




    }

   /* private void Register() {
        if (inputValid()) {
            String lastname = LastName.getText().toString();
            String firstname= FirstName.getText().toString();
            String email = Email.getText().toString();
            String date_of_birth = DateOfBirth.getText().toString();
            String sex = Sex.getText().toString();
            String nr_phone = NrPhone.getText().toString();
            String password = Password.getText().toString();
            String confirm_password = ConfirmPassword.getText().toString();
            String role1=radio_button_doctor.getText().toString();
            String role3=radio_button_patient.getText().toString();



            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        if(radio_button_doctor.isChecked()) {

                            String id = auth.getCurrentUser().getUid();

                            User user = new User(id, lastname, firstname, email, nr_phone, password,role1, sex, date_of_birth);

                            reference.child("user").child(id).setValue(user);
                            Toast.makeText(RegisterActivity.this, "Contul a fost creat cu succes!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                            finish();



                        } else if(radio_button_patient.isChecked()) {
                            String id = auth.getCurrentUser().getUid();

                            User user = new User(id, lastname, firstname, email, nr_phone, password,role3, sex, date_of_birth);

                            reference.child("user").child(id).setValue(user);
                            Toast.makeText(RegisterActivity.this, "Contul a fost creat cu succes!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                            finish();

                        }
                        else{
                            Toast.makeText(RegisterActivity.this, "Va rugam selectati rolul!", Toast.LENGTH_SHORT).show();

                        }


                    } else {
                        Toast.makeText(RegisterActivity.this, "Eroare la crearea contului!", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }*/

    private void Register() {
        if (inputValid()) {
            String lastname = lastName.getText().toString();
            String firstname = firstName.getText().toString();
            String email_user = email.getText().toString();
            String date_of_birth = dateOfBirth.getText().toString();
            String sex = actvSex.getText().toString();
            String nr_phone = nrPhone.getText().toString();
            String password_user = password.getText().toString();
            String confirm_password = confirmPassword.getText().toString();
            String role1 = radio_button_doctor.getText().toString();
            String role2 = radio_button_patient.getText().toString();

            if (radio_button_doctor.isChecked()) {

                    // Create a custom dialog box to prompt the user for a code
                    final Dialog dialog = new Dialog(RegisterActivity.this);
                    dialog.setContentView(R.layout.dialog_confirm_code_for_doctor_role);

                    // Find the EditText and Button views in the dialog box
                    EditText codeEditText = dialog.findViewById(R.id.code_edittext);
                    Button submitButton = dialog.findViewById(R.id.confirm_button_for_confirm_code);
                    Button cancelButton = dialog.findViewById(R.id.cancel_button_for_confirm_code);
                    TextView messageTextView = dialog.findViewById(R.id.message_textview);


                    cancelButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    // Set the click listener for the submit button
                    int[] triesCounter = {0};
                    submitButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Get the entered code and check if it is correct
                            String enteredCode = codeEditText.getText().toString();
                            if (enteredCode.equals("9a73h8")) {
                                // If the code is correct, create the user account
                                auth.createUserWithEmailAndPassword(email_user, password_user).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                            String id = auth.getCurrentUser().getUid();
                                            User user = new User(id, lastname, firstname, email_user, nr_phone, password_user, role1, sex, date_of_birth);
                                            reference.child("user").child(id).setValue(user);
                                            Toast.makeText(RegisterActivity.this, "Contul a fost creat cu succes!", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                            finish();
                                        } else {
                                            Toast.makeText(RegisterActivity.this, "Eroare la crearea contului!", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                                dialog.dismiss();


                            } else {
                                triesCounter[0]++;
                                if (triesCounter[0] >= 3) {
                                    Toast.makeText(RegisterActivity.this, "Ati gresit codul de 3 ori. Nu mai puteti crea cont pentru acest rol!", Toast.LENGTH_SHORT).show();
                                    dialog.dismiss();
                                    startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                                    finish();

                                } else {
                                    messageTextView.setText("Codul introdus este incorect. Mai aveti " + (3 - triesCounter[0]) + " incercari ramase.");
                                }

                            }

                        }

                    });

                    dialog.show();

                }
            else if (radio_button_patient.isChecked()) {
                // Create the user account for a patient
                auth.createUserWithEmailAndPassword(email_user, password_user).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            String id = auth.getCurrentUser().getUid();
                            User user = new User(id, lastname, firstname, email_user, nr_phone, password_user, role2, sex, date_of_birth);
                            reference.child("user").child(id).setValue(user);
                            Toast.makeText(RegisterActivity.this, "Contul a fost creat cu succes!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Eroare la crearea contului!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } else {
                Toast.makeText(RegisterActivity.this, "Va rugam selectati rolul!", Toast.LENGTH_SHORT).show();

            }


        }
    }




    private void displayCalendar() {

        dateOfBirth.setError(null);

        final Calendar calendar = Calendar.getInstance();

        if (!dateOfBirth.getText().toString().equals(getString(R.string.selectati_data))) {
            try {
                Date selectedDate = new SimpleDateFormat("dd/MM/yyyy", Locale.US).parse(dateOfBirth.getText().toString());
                calendar.setTime(selectedDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        int day = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        DatePickerDialog datePickerDialog = new DatePickerDialog(RegisterActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        month += 1;
                        String date = day + "/" + month + "/" + year;
                        dateOfBirth.setText(date);
                    }
                }, year, month, day);

        Calendar maximumDate = Calendar.getInstance();
        maximumDate.add(Calendar.YEAR, -14);
        datePickerDialog.getDatePicker().setMaxDate(maximumDate.getTimeInMillis());
        datePickerDialog.show();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btnRegister:
                Register();
                break;
            case R.id.date_of_birth:
                displayCalendar();
                break;

        }
    }

    private boolean inputValid() {

        if (lastName.getText().toString().isEmpty() || firstName.getText().toString().isEmpty() || email.getText().toString().isEmpty() || password.getText().toString().isEmpty() || confirmPassword.getText().toString().isEmpty() || nrPhone.getText().toString().isEmpty() || dateOfBirth.getText().toString().isEmpty()) {
            Toast.makeText(RegisterActivity.this, "Va rugam completati toate campurile", Toast.LENGTH_SHORT).show();
            return false;
        }

       else  if (!Pattern.matches("^[A-Z][a-z]*$",firstName.getText().toString())) {
            firstName.setError("Prenumele trebuie sa contina doar litere!");
            firstName.requestFocus();
            return false;
        }
        else  if (!Pattern.matches("^[A-Z][a-z]*$",lastName.getText().toString())) {
            lastName.setError("Numele de familie trebuie sa contina doar litere!");
            lastName.requestFocus();
            return false;
        }

       else if (!Pattern.matches("^(\\+4|)?(07[0-8]{1}[0-9]{1}|02[0-9]{2}|03[0-9]{2}){1}?(\\s|\\.|\\-)?([0-9]{3}(\\s|\\.|\\-|)){2}$",nrPhone.getText().toString())) {
            nrPhone.setError(getString(R.string.err_not_valid_telefon));
            nrPhone.requestFocus();
            return false;
        }

       else if (!Patterns.EMAIL_ADDRESS.matcher(email.getText().toString()).matches()){
            email.setError(getString(R.string.err_not_valid_email));
            email.requestFocus();
            return false;
        }

        else if (password.getText().toString().length() < 6) {
            password.setError(getString(R.string.err_not_valid_parola));
            password.requestFocus();
            return false;
        }


        else if (!password.getText().toString().equals(confirmPassword.getText().toString())) {
            confirmPassword.setError(getString(R.string.err_not_valid_confirmare_parola));
            confirmPassword.requestFocus();
            return false;
        }

        return true;
    }
}