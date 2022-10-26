package com.example.smiletogether_dentalapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    RadioButton doctor_radiobtn,user_radiobtn;
    Button apply_btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        doctor_radiobtn=findViewById(R.id.doctor_radioButton);
        user_radiobtn=findViewById(R.id.user_radioButton);
        apply_btn = findViewById(R.id.selectrole_button);

        apply_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(doctor_radiobtn.isChecked()){
                    startActivity(new Intent(MainActivity.this,LoginActivity.class));
                    finish();

                }

                if(user_radiobtn.isChecked()){
                    startActivity(new Intent(MainActivity.this,LoginActivity.class));
                    finish();

                }

            }
        });
    }
}