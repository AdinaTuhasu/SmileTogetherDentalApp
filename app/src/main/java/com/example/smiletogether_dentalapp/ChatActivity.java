package com.example.smiletogether_dentalapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.smiletogether_dentalapp.Adapter.ChatAdapter;
import com.example.smiletogether_dentalapp.Doctor.DoctorHomePageActivity;
import com.example.smiletogether_dentalapp.Doctor.ListOfPatientsActivity;
import com.example.smiletogether_dentalapp.Model.Chat;
import com.example.smiletogether_dentalapp.Model.Doctor;
import com.example.smiletogether_dentalapp.Model.Message;
import com.example.smiletogether_dentalapp.Model.Patient;
import com.example.smiletogether_dentalapp.Patient.ListOfDoctorsActivity;
import com.example.smiletogether_dentalapp.Patient.PatientHomePageActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatActivity extends AppCompatActivity {
    FirebaseAuth auth = FirebaseAuth.getInstance();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://smiletogetherdentalapp-default-rtdb.firebaseio.com/");
    private final String idUserConnected = auth.getCurrentUser().getUid();

    private Toolbar toolbar;
    private CircleImageView ciwProfilePhotoUser;
    private TextView tvNameUser;
    private FloatingActionButton fabSendMessage;
    private EditText etMessage;

    private Intent intent;
    private Doctor doctor;
    private Patient patient;
    private String idDoctor;
    private String idPatient;

    private RecyclerView rwMessage;
    private ChatAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private List<Message> messages = new ArrayList<>();
    private String idConversation = "";

    private String typeUser;

    private ValueEventListener readMessageListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        toolbar = findViewById(R.id.toolbar);
        ciwProfilePhotoUser = findViewById(R.id.ciwProfilePhotoUser);
        tvNameUser = findViewById(R.id.tvNameUser);
        rwMessage = findViewById(R.id.rwMessages);
        fabSendMessage= findViewById(R.id.fabSendMessage);
        etMessage = findViewById(R.id.etMessage);

        etMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!messages.isEmpty()) {
                    rwMessage.smoothScrollToPosition(messages.size() - 1);
                }
            }
        });

        etMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String message = charSequence.toString();
                fabSendMessage.setEnabled(!message.isEmpty());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        intent = getIntent();
       if (intent.hasExtra(ListOfDoctorsActivity.DOCTOR)) {
            doctor = (Doctor) intent.getSerializableExtra(ListOfDoctorsActivity.DOCTOR);
            idDoctor = doctor.getId();
            typeUser =PatientHomePageActivity.PATIENT;
        } else if (intent.hasExtra(ListOfDoctorsActivity.INFO_DOCTOR)) {
            doctor = (Doctor) intent.getSerializableExtra(ListOfDoctorsActivity.INFO_DOCTOR);
            idDoctor = doctor.getId();
            typeUser = PatientHomePageActivity.PATIENT;
        } else {
            patient = (Patient) intent.getSerializableExtra(ListOfPatientsActivity.PATIENT);
            idPatient = patient.getId();
            typeUser = DoctorHomePageActivity.DOCTOR;
        }

        setToolbar();

        setRecycleView();

        String name;
        if (typeUser.equals(PatientHomePageActivity.PATIENT)) {
            name = "Dr. " + doctor.getlastname() + " " + doctor.getFirstname();
            if (doctor.getUrlProfilePhoto()!=null && !doctor.getUrlProfilePhoto().equals("") ) {
                Glide.with(getApplicationContext()).load(doctor.getUrlProfilePhoto()).into(ciwProfilePhotoUser);
            } else {
                Glide.with(getApplicationContext()).load(R.drawable.profile_photo).into(ciwProfilePhotoUser);
            }
        } else {
            name = patient.getlastname() + " " + patient.getFirstname();
            if (patient.getUrlProfilePhoto()!=null && !patient.getUrlProfilePhoto().equals("") ) {
                Glide.with(getApplicationContext()).load(patient.getUrlProfilePhoto()).into(ciwProfilePhotoUser);
            } else {
                Glide.with(getApplicationContext()).load(R.drawable.profile_photo).into(ciwProfilePhotoUser);
            }
        }

        tvNameUser.setText(name);

        reference.child("Conversatii").addValueEventListener(takesMessages());

        fabSendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String message = etMessage.getText().toString().trim();
                if (typeUser.equals(PatientHomePageActivity.PATIENT)) {
                   sendMessage(idUserConnected, doctor.getId(), message);
                } else {
                   sendMessage(idUserConnected, patient.getId(), message);
                }

                etMessage.setText("");
            }
        });

        if (typeUser.equals(PatientHomePageActivity.PATIENT)) {
            markMessagesReadByPatient();
        } else if (typeUser.equals(DoctorHomePageActivity.DOCTOR)) {
            markMessagesReadByDoctor();
        }
    }


    private void setRecycleView() {
        rwMessage.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        rwMessage.setLayoutManager(layoutManager);
        setAdaptor();
    }

    private void setAdaptor() {
        adapter = new ChatAdapter(messages, this);
        if (!messages.isEmpty()) {
            rwMessage.smoothScrollToPosition(messages.size() - 1);
        }
        rwMessage.setAdapter(adapter);

    }

    private void markMessagesReadByDoctor() {
       readMessageListener = reference.child("Conversatii").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    List<Message> messages = chat.getMessages();
                    if ((messages.get(0).getIdTransmitter().equals(idUserConnected) && messages.get(0).getIdReceiver().equals(idPatient))
                            || (messages.get(0).getIdReceiver().equals(idUserConnected) && messages.get(0).getIdTransmitter().equals(idPatient))) {
                        for (int i = 0; i < chat.getMessages().size(); i++) {
                            if (messages.get(i).getIdTransmitter().equals(idPatient) && !messages.get(i).isMessageRead()) {
                               reference.child("Conversatii")
                                        .child(chat.getIdConversation())
                                        .child("messages")
                                        .child(String.valueOf(i))
                                        .child("messageRead")
                                        .setValue(true);
                                messages.get(i).setMessageRead(true);
                            }
                        }
                    }

                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void markMessagesReadByPatient() {
        readMessageListener =reference.child("Conversatii").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    List<Message> message = chat.getMessages();
                    if ((message.get(0).getIdTransmitter().equals(idUserConnected) && message.get(0).getIdReceiver().equals(idDoctor))
                            || (message.get(0).getIdReceiver().equals(idUserConnected) && message.get(0).getIdTransmitter().equals(idDoctor))) {
                        for (int i = 0; i < chat.getMessages().size(); i++) {
                            if (message.get(i).getIdTransmitter().equals(idDoctor) && !message.get(i).isMessageRead()) {
                                reference.child("Conversatii")
                                        .child(chat.getIdConversation())
                                        .child("messages")
                                        .child(String.valueOf(i))
                                        .child("messageRead")
                                        .setValue(true);
                                message.get(i).setMessageRead(true);
                            }
                        }
                    }

                }

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private ValueEventListener takesMessages() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Chat chat = dataSnapshot.getValue(Chat.class);
                    messages = chat.getMessages();
                    if ((typeUser.equals(PatientHomePageActivity.PATIENT) &&
                            ((messages.get(0).getIdTransmitter().equals(idUserConnected) && messages.get(0).getIdReceiver().equals(idDoctor))
                                    || (messages.get(0).getIdReceiver().equals(idUserConnected) && messages.get(0).getIdTransmitter().equals(idDoctor))))
                            || (typeUser.equals(DoctorHomePageActivity.DOCTOR) &&
                            ((messages.get(0).getIdTransmitter().equals(idUserConnected) && messages.get(0).getIdReceiver().equals(idPatient))
                                    || (messages.get(0).getIdReceiver().equals(idUserConnected) && messages.get(0).getIdTransmitter().equals(idPatient))))) {
                        idConversation = chat.getIdConversation();
//                        seteazaRecycleView();
                        setAdaptor();
                        break;
                    } else {
                        messages = new ArrayList<>();
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
    }

    private void sendMessage(String idTransmitter, String idReceiver, String text) {
        Message message = new Message(idTransmitter, idReceiver, text, false);
        if (!idConversation.equals("")) {
            messages.add(message);
            reference.child("Conversatii").child(idConversation)
                    .child("messages").setValue(messages);
        } else {
            messages.add(message);
            Chat chat= new Chat(null, messages);
            idConversation = reference.child("Conversatii").push().getKey();
            chat.setIdConversation(idConversation);
            reference.child("Conversatii").child(idConversation).setValue(chat);
        }
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
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        super.finish();
        reference.child("Conversatii").removeEventListener(readMessageListener);
        if (intent.hasExtra(ListOfDoctorsActivity.DOCTOR) || intent.hasExtra(ListOfPatientsActivity.PATIENT)) {
            overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }
    }
}