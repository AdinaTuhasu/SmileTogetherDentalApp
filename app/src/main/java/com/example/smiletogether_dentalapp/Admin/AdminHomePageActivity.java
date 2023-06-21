package com.example.smiletogether_dentalapp.Admin;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.example.smiletogether_dentalapp.MainActivity;
import com.example.smiletogether_dentalapp.R;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

public class AdminHomePageActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;

    private RelativeLayout rlLogout;

    private CardView cwAddSpecialitiesAndInvestigations;
    private CardView cwViewDoctors;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home_page);
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawerLayout);

        navigationView = findViewById(R.id.navigationView);


        rlLogout = findViewById(R.id.rlLogout_admin);
        cwAddSpecialitiesAndInvestigations=findViewById(R.id.cwAddServices);


        setToolbar();

        setToggle();



        navigationView.setNavigationItemSelectedListener(this);
        rlLogout.setOnClickListener(this);
        cwAddSpecialitiesAndInvestigations.setOnClickListener(this);
        cwViewDoctors.setOnClickListener(this);


    }




    private void setToggle() {
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.open_toggle, R.string.close_toggle);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void setToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
    }




    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cwAddServices:
                startActivity(new Intent(getApplicationContext(), AdminAddServicesActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;



            case R.id.rlLogout_admin:
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
                break;
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_profil:
                //startActivity(new Intent(getApplicationContext(), PatientProfileActivity.class));
                drawerLayout.closeDrawer(GravityCompat.START);
                break;


            case R.id.item_about_us:
                //startActivity(new Intent(getApplicationContext(), class));
                drawerLayout.closeDrawer(GravityCompat.START);
                break;
        }
        return true;
    }
}
