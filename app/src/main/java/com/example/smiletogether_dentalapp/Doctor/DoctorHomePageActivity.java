package com.example.smiletogether_dentalapp.Doctor;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.smiletogether_dentalapp.AboutUsActivity;
import com.example.smiletogether_dentalapp.AppointmentsActivity;
import com.example.smiletogether_dentalapp.ChatActivity;
import com.example.smiletogether_dentalapp.ConversationsActivity;
import com.example.smiletogether_dentalapp.MainActivity;
import com.example.smiletogether_dentalapp.Model.Chat;
import com.example.smiletogether_dentalapp.Model.Doctor;
import com.example.smiletogether_dentalapp.Model.Message;
import com.example.smiletogether_dentalapp.Model.Notification;
import com.example.smiletogether_dentalapp.Model.User;
import com.example.smiletogether_dentalapp.NotificationsActivity;
import com.example.smiletogether_dentalapp.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class DoctorHomePageActivity extends AppCompatActivity implements View.OnClickListener, NavigationView.OnNavigationItemSelectedListener {

    private TextView tvNameUserConnected;
    private TextView tvEmailUserConnected;
    private CircleImageView ciwProfilePhoto_nav_menu;

    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;

    private RelativeLayout rlLogout;

    Context context;
    private String idDoctor;

    private CardView cwAppointments;
    private CardView cwPatients;
    private CardView cwFeedback;

    private ImageView ivNotifications;
    private List<Notification> notifications = new ArrayList<>();

    private FirebaseUser doctorConnected;
    FirebaseAuth auth = FirebaseAuth.getInstance();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://smiletogetherdentalapp-default-rtdb.firebaseio.com/");

    private FloatingActionButton fabChat;
    private List<Message> messages = new ArrayList<>();


    private String name;

    public static final String SHOW_PATIENTS = "vizualizarePacienti";
    public static final String SHOW_FEEDBACK = "vizualizareFeedback";
    public static final String DOCTOR = "Medic";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_home_page);
        //initialization
        toolbar = findViewById(R.id.toolbar);
        drawerLayout = findViewById(R.id.drawerLayout);

        navigationView = findViewById(R.id.navigationView);

        tvNameUserConnected = navigationView.getHeaderView(0).findViewById(R.id.tvNameUserConnected);
        tvEmailUserConnected = navigationView.getHeaderView(0).findViewById(R.id.tvEmailUserConnected);
        ciwProfilePhoto_nav_menu=navigationView.getHeaderView(0).findViewById(R.id.ciwProfilePhoto_nav_header);

        rlLogout = findViewById(R.id.rlLogout);


        doctorConnected = auth.getCurrentUser();
        idDoctor = doctorConnected.getUid();

        cwAppointments = findViewById(R.id.cwAppointment);
        cwPatients = findViewById(R.id.cwPatient);
        cwFeedback = findViewById(R.id.cwFeedback);


        ivNotifications = findViewById(R.id.ivNotification_doctor);

        fabChat = findViewById(R.id.fabChat);

        setToolbar();

        setToggle();

        checksUnreadNotifications();
        checksUnreadMessages();


        navigationView.setNavigationItemSelectedListener(this);
        rlLogout.setOnClickListener(this);
        cwAppointments.setOnClickListener(this);
        cwPatients.setOnClickListener(this);
        cwFeedback.setOnClickListener(this);
        ivNotifications.setOnClickListener(this);
        fabChat.setOnClickListener(this);


        uploadInfoNavMenu();


    }

    private void setToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
    }

    private void setToggle() {
        toggle = new ActionBarDrawerToggle(this, drawerLayout,toolbar, R.string.open_toggle, R.string.close_toggle);
        toggle.getDrawerArrowDrawable().setColor(getResources().getColor(R.color.white));
        drawerLayout.addDrawerListener(toggle); // atasam toggle-ul la drawerLayout
        toggle.syncState(); // sa se roteasca atunci cand inchid/deschid meniul
    }

    private void checksUnreadMessages() {
        reference.child("Conversatii").addValueEventListener(takesMessages());
    }

    private ValueEventListener takesMessages() {
        messages.clear();
        return new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    List<Message> messages = chat.getMessages();
                    Message lastMessage = messages.get(messages.size() - 1);
                    if (lastMessage.getIdReceiver().equals(idDoctor) && !lastMessage.isMessageRead()) {
                        fabChat.setImageResource(R.drawable.chat_notification);
                        fabChat.setImageTintList(null);
                        break;
                    } else {
                        fabChat.setImageResource(R.drawable.chat);
                        fabChat.setImageTintList(null);

                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
    }



    private void checksUnreadNotifications() {
        reference.child("Notificari").addValueEventListener(retrieveNotification());
    }

    private ValueEventListener retrieveNotification() {
        notifications.clear();
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Notification notification = dataSnapshot.getValue(Notification.class);
                    if (notification.getIdReceiver().equals(idDoctor)) {
                        notifications.add(notification);
                    }
                }
                Drawable drawable = null;

                if (!notifications.isEmpty()) {
                    boolean hasUnreadNotification = false;
                    for (Notification notification : notifications) {
                        if (!notification.isNoticeRead()) {
                            hasUnreadNotification = true;
                            break;
                        }
                    }
                    if (hasUnreadNotification) {
                        drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.notificari_necitite, null);
                    }else {
                        drawable =ResourcesCompat.getDrawable(getResources(),R.drawable.notificari,null);
                    }

                    int drawableSize = (int) (ivNotifications.getHeight() * 1.3); // set size proportional to ImageView height

                    if (drawableSize <= 0 || drawable == null) {
                        // ImageView height is zero or negative or drawable is null, exit the method
                        return;
                    }

                    // Create a new BitmapDrawable with the desired size
                    Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                    Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, drawableSize, drawableSize, false);
                    Drawable scaledDrawable = new BitmapDrawable(getResources(), scaledBitmap);

                    // Set the scaled drawable as the image drawable for the ImageView
                    ivNotifications.setImageDrawable(scaledDrawable);
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
    }



    @Override
    protected void onResume() {
        super.onResume();
        uploadInfoNavMenu();
        retrieveNotification();
    }

    private void uploadInfoNavMenu() {
        reference.child("user").child(idDoctor).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user = snapshot.getValue(User.class);
                Doctor doctor = snapshot.getValue(Doctor.class);


                if (user != null) {
                    String lastname = user.getlastname();
                    String firstname = user.getFirstname();
                    name = lastname + " " + firstname;
                    String email = user.getEmail();
                    String photo=doctor.getUrlProfilePhoto();


                    tvNameUserConnected.setText(name);
                    tvEmailUserConnected.setText(email);

                    if ((photo!=null) && (!photo.equals(""))) {
                        Glide.with(getApplicationContext()).load(photo).into(ciwProfilePhoto_nav_menu);
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("preluareMedic", error.getMessage());
            }
        });
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.cwAppointment:
                startActivity(new Intent(getApplicationContext(), AppointmentsActivity.class).putExtra(DOCTOR,"Medic"));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            case R.id.cwPatient:
                startActivity(new Intent(getApplicationContext(), ListOfPatientsActivity.class).putExtra(SHOW_PATIENTS,""));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;

            case R.id.cwFeedback:
                startActivity(new Intent(getApplicationContext(), ListOfPatientsActivity.class).putExtra(SHOW_FEEDBACK, ""));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;


            case R.id.ivNotification_doctor:
                /*Drawable drawable = null;
                drawable =ResourcesCompat.getDrawable(getResources(),R.drawable.notificari,null);
                int drawableSize = (int) (ivNotifications.getHeight() * 1.3); // set size proportional to ImageView height

                    if (drawableSize <= 0 || drawable == null) {
                    // ImageView height is zero or negative or drawable is null, exit the method
                    return;
                    }

                    // Create a new BitmapDrawable with the desired size
                Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
                Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, drawableSize, drawableSize, false);
                Drawable scaledDrawable = new BitmapDrawable(getResources(), scaledBitmap);

                // Set the scaled drawable as the image drawable for the ImageView
                 ivNotifications.setImageDrawable(scaledDrawable);
                //ivNotifications.setImageResource(R.drawable.notificari);*/
                checksUnreadNotifications();
                startActivity(new Intent(getApplicationContext(), NotificationsActivity.class).putExtra(DOCTOR, "Medic"));
                break;
            case R.id.fabChat:
                startActivity(new Intent(getApplicationContext(), ConversationsActivity.class).putExtra(DOCTOR, "Medic"));
                break;
            case R.id.rlLogout:
                auth.signOut();
                startActivity(new Intent(getApplicationContext(), MainActivity.class));
                finish();
                break;
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.item_about_us:
                startActivity(new Intent(getApplicationContext(), AboutUsActivity.class));
                drawerLayout.closeDrawer(GravityCompat.START);
                break;

            case R.id.item_profil:
                startActivity(new Intent(getApplicationContext(), DoctorProfileActivity.class));
                drawerLayout.closeDrawer(GravityCompat.START);
                break;


        }
        return true;
    }
}