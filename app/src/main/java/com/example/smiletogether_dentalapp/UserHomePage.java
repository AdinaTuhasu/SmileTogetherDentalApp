package com.example.smiletogether_dentalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserHomePage extends AppCompatActivity {
    /*private DrawerLayout Drawerloyout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private ActionBarDrawerToggle Toggle;
    private TextView lastname_tv,firstname_tv,email_tv;
    private User user;


    FirebaseAuth auth= FirebaseAuth.getInstance();
    DatabaseReference reference=FirebaseDatabase.getInstance().getReferenceFromUrl("https://smiletogetherdentalapp-default-rtdb.firebaseio.com/");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home_page);
        Drawerloyout=findViewById(R.id.drawer_layout);
        toolbar=findViewById(R.id.app_bar);
        navigationView=findViewById(R.id.navigation);

        setSupportActionBar(toolbar);
        getSupportActionBar();

        Toggle =new ActionBarDrawerToggle(this,Drawerloyout,toolbar,R.string.open,R.string.close);
        Drawerloyout.addDrawerListener(Toggle);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        Toggle.syncState();

        View view=navigationView.inflateHeaderView(R.layout.header);


        user=new User();

        lastname_tv=findViewById(R.id.show_lastname);
        email_tv=findViewById(R.id.show_email);



       navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
           @Override
           public boolean onNavigationItemSelected(@NonNull MenuItem item) {
               UserMenuSelector(item);
               return false;
           }
       });



    }

    private void UserMenuSelector(MenuItem menuItem){
       switch(menuItem.getItemId()){

       }
    }



    @Override
    protected void onStart() {
        super.onStart();

        reference.child("Pacienti").child(auth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {


                user=snapshot.getValue(User.class);
                lastname_tv.setText(user.getLast_name());
                //tufirstname_tv.setText(user.getFirst_name());
                email_tv.setText(user.getEmail());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }*/
}