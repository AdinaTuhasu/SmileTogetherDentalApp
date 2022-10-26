package com.example.smiletogether_dentalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class RegisterActivity extends AppCompatActivity {
    private EditText Lastname, Firstname, Email, Age, Username, Password;
    private Button signup_button;

    private DatabaseReference mydb = FirebaseDatabase.getInstance().getReferenceFromUrl("https://smiletogetherdentalapp-default-rtdb.firebaseio.com/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Firstname = findViewById(R.id.firstname);
        Lastname = findViewById(R.id.lastname);
        Email = findViewById(R.id.email);
        Age = findViewById(R.id.age);
        Username = findViewById(R.id.username);
        Password = findViewById(R.id.password);
        signup_button = findViewById(R.id.register_button);

        signup_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String lastname1 = Lastname.getText().toString();
                String firstname1 = Firstname.getText().toString();
                String email1 = Email.getText().toString();
                String age1 = Age.getText().toString();
                String username1 = Username.getText().toString();
                String password1 = Password.getText().toString();

                if(lastname1.isEmpty() || firstname1.isEmpty() || email1.isEmpty() || age1.isEmpty() || username1.isEmpty() || password1.isEmpty() ){
                    Toast.makeText(RegisterActivity.this,"Va rugam completati toate campurile", Toast.LENGTH_SHORT).show();
                }
                else {
                    mydb.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.hasChild(username1)){
                                Toast.makeText(RegisterActivity.this,"Utilizatorul deja exista",Toast.LENGTH_SHORT).show();
                            }
                            else{
                                mydb.child("users").child(username1).child("Lastname").setValue(lastname1);
                                mydb.child("users").child(username1).child("Firstname").setValue(firstname1);
                                mydb.child("users").child(username1).child("Email").setValue(email1);
                                mydb.child("users").child(username1).child("Age").setValue(age1);
                                mydb.child("users").child(username1).child("Username").setValue(username1);
                                mydb.child("users").child(username1).child("Password").setValue(password1);

                                Toast.makeText(RegisterActivity.this,"Contul a fost creat cu succes!",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                                finish();

                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                }

            }
        });


    }
}