package com.example.smiletogether_dentalapp;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.example.smiletogether_dentalapp.Adapter.NotificationAdapter;
import com.example.smiletogether_dentalapp.Doctor.DoctorHomePageActivity;
import com.example.smiletogether_dentalapp.Model.Notification;
import com.example.smiletogether_dentalapp.Patient.PatientHomePageActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class NotificationsActivity extends AppCompatActivity{
        public static final String NOTIFICARI = "Notificari";

        private String idUserConnected;
        FirebaseAuth auth = FirebaseAuth.getInstance();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://smiletogetherdentalapp-default-rtdb.firebaseio.com/");

        private Toolbar toolbar;

        private ProgressBar progressBar;
        private RelativeLayout rlNoNotification;

        private RecyclerView rwNotification;
        private NotificationAdapter adapter;
        private RecyclerView.LayoutManager layoutManager;
        private List<Notification> notifications;

        private String user_type;

        private ValueEventListener readNotificationsListener;

//    private CardView cwNotificare;

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_notifications);

            //initializations
            toolbar = findViewById(R.id.toolbar);
            progressBar = findViewById(R.id.progressBar);
            rlNoNotification = findViewById(R.id.rlNoNotifications);
            rwNotification = findViewById(R.id.rwNotifications);
            notifications = new ArrayList<>();


            idUserConnected= auth.getCurrentUser().getUid();

            setToolbar();

            setUserType();

            setRecyclerView();

            reference.child("Notificari").addValueEventListener(takesNotifications());
        }



        private void marksReadNotifications() {
            readNotificationsListener = reference.child("Notificari").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Notification not= dataSnapshot.getValue(Notification.class);
                        if (not.getIdReceiver().equals(idUserConnected) && !not.isNoticeRead()) {
                            reference.child("Notificari")
                                    .child(not.getIdNotification())
                                    .child("noticeRead")
                                    .setValue(true);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }

        private void setUserType() {
            Intent intent = getIntent();
            if (intent.hasExtra(PatientHomePageActivity.PATIENT)) {
                user_type = intent.getStringExtra(PatientHomePageActivity.PATIENT);
            } else {
                user_type = intent.getStringExtra(DoctorHomePageActivity.DOCTOR);
            }
        }

        private ValueEventListener takesNotifications() {
            loading(true);
            return new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    notifications = new ArrayList<>();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Notification not = dataSnapshot.getValue(Notification.class);
                        if (not.getIdReceiver().equals(idUserConnected)) {
                            notifications.add(not);
                        }
                    }

                    if (!notifications.isEmpty()) {
                        Collections.reverse(notifications);
                        rlNoNotification.setVisibility(View.GONE);
                        setAdapter();
                        rwNotification.setVisibility(View.VISIBLE);
                    } else {
                        rwNotification.setVisibility(View.GONE);
                        rlNoNotification.setVisibility(View.VISIBLE);
                    }
                    loading(false);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            };
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        private void setRecyclerView() {
            rwNotification.setHasFixedSize(true);
            layoutManager = new LinearLayoutManager(this);
            rwNotification.setLayoutManager(layoutManager);
            setAdapter();
        }

        @RequiresApi(api = Build.VERSION_CODES.O)
        private void setAdapter() {
            adapter = new NotificationAdapter(notifications, user_type, this);
            rwNotification.setAdapter(adapter);
        }

        private void setToolbar() {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }


        private void loading(Boolean seIncarca) {
            if (seIncarca) {
                progressBar.setVisibility(View.VISIBLE);
            } else progressBar.setVisibility(View.GONE);
        }

        @Override
        public boolean onOptionsItemSelected(@NonNull MenuItem item) {
            if (item.getItemId() == android.R.id.home) {
                finish();
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        @Override
        public void finish() {
            super.finish();
            marksReadNotifications();
        }
}