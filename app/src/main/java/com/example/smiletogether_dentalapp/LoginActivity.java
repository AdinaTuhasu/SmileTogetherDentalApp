package com.example.smiletogether_dentalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {
    private EditText Username,Password;
    private Button login_btn;
    private TextView register_now_btn;

    private DatabaseReference mydb = FirebaseDatabase.getInstance().getReferenceFromUrl("https://smiletogetherdentalapp-default-rtdb.firebaseio.com/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Username = findViewById(R.id.username);
        Password = findViewById(R.id.password);
        login_btn = findViewById(R.id.login_button);
        register_now_btn=findViewById(R.id.create_an_account);

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String usernametxt = Username.getText().toString();
                String passwordtxt = Password.getText().toString();

                if(usernametxt.isEmpty() || passwordtxt.isEmpty()){
                    Toast.makeText(LoginActivity.this,"Va rugam introduceti numele de utilizator sau parola",Toast.LENGTH_SHORT).show();

                } else{
                    mydb.child("users").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if(snapshot.hasChild(usernametxt))
                            {
                                String getPassword=snapshot.child(usernametxt).child("Password").getValue(String.class);

                                if(getPassword.equals(passwordtxt))
                                {
                                    Toast.makeText(LoginActivity.this, "Autentificare cu succes!", Toast.LENGTH_SHORT).show();

                                }
                                else
                                {
                                    Toast.makeText(LoginActivity.this, "Parola incorecta!", Toast.LENGTH_SHORT).show();
                                }


                            }
                            else
                                Toast.makeText(LoginActivity.this, "Numele de utilizator este incorect!", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });



                }
            }
        });

        register_now_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,RegisterActivity.class));
                finish();
            }
        });
    }
}