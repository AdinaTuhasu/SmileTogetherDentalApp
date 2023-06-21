package com.example.smiletogether_dentalapp;

import static com.example.smiletogether_dentalapp.Patient.PatientHomePageActivity.PATIENT;
import static com.example.smiletogether_dentalapp.Patient.ListOfDoctorsActivity.DOCTOR;

import com.example.smiletogether_dentalapp.Doctor.DoctorHomePageActivity;
import com.example.smiletogether_dentalapp.Model.Feedback;
import com.example.smiletogether_dentalapp.Model.Patient;
import  com.example.smiletogether_dentalapp.Patient.ListOfDoctorsActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.AppCompatRadioButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smiletogether_dentalapp.Adapter.AppointmentAdapter;
import com.example.smiletogether_dentalapp.Model.Appointment;
import com.example.smiletogether_dentalapp.Model.Doctor;
import com.example.smiletogether_dentalapp.Model.Notification;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;


public class AppointmentsActivity extends AppCompatActivity implements View.OnClickListener, AppointmentAdapter.OnAppointmentClickListener, AppointmentAdapter.OnAppointmentLongClickListener,AppointmentAdapter.OnBtnFeedbackClickListener,
        AppointmentAdapter.OnBtnRetetaClickListener
{
        private static final int PERMISSION_REQUEST_SEND_SMS=1;

public static final String ADD_APPOINTMENT = "adaugaProgramare";
public static final int REQUEST_CODE_INTENT = 200;
public static final int REQUEST_CODE_READ_EXTERNAL_STORAGE = 100;

/*private static final DateTimeFormatter FORMAT_DATA = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
private final String dataNotification = formatter.format(LocalDate.now());*/

private static final DateFormat FORMAT_DATE = new SimpleDateFormat("dd/MM/yyyy HH:mm");
private final DateFormat formatter = DateFormat.getDateInstance(DateFormat.SHORT);
private final String dataNotification = formatter.format(new Date());
private final SimpleDateFormat FORMAT_DATA = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.US);
private final StorageReference storageReference = FirebaseStorage.getInstance().getReference();

public String type_user;
private FloatingActionButton fabAddAppointment;
private AppCompatRadioButton rbHistory;
private AppCompatRadioButton rbFuture;
private List<Appointment> appointments;
private String idReceptor;
private String  patient_phoneNr;

private Date dataCurenta;

private RecyclerView rwAppointments;
private AppointmentAdapter adapter;
private RecyclerView.LayoutManager layoutManager;

private ProgressBar progressBar;
private RelativeLayout ryNoAppointment;

private Toolbar toolbar;

private String statusSelected;


private Appointment appointment;


private ProgressDialog progressDialog;

private Uri uriPDF;
private Uri urlPrescription;

private AlertDialog dialogFeedback;
private TextView tvGrade;
private RadioGroup rgGrade;
private int grade;



FirebaseAuth auth = FirebaseAuth.getInstance();
DatabaseReference reference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://smiletogetherdentalapp-default-rtdb.firebaseio.com/");
private final String idUser = auth.getCurrentUser().getUid();

@Override
protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appointmets);

        toolbar = findViewById(R.id.toolbar);
        fabAddAppointment = findViewById(R.id.fabAddAppointment);
        rbHistory = findViewById(R.id.rbHistoryAppointments);
        rbFuture = findViewById(R.id.rbFutureAppointments);

        try {
                dataCurenta = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.US)
                        .parse(FORMAT_DATA.format(new Date()));
        } catch (ParseException e) {
                e.printStackTrace();
        }

        progressBar = findViewById(R.id.progressBar);

        ryNoAppointment = findViewById(R.id.ryNoAppointments);
        rwAppointments = findViewById(R.id.rwAppointments);

        setToolbar();

        fabAddAppointment.setOnClickListener(this);
        rbHistory.setOnClickListener(this);
        rbFuture.setOnClickListener(this);

        setUserType();

        setRecyclerView();
        setDialogFeedback();

        ActivityCompat.requestPermissions(AppointmentsActivity.this,new String[]{Manifest.permission.SEND_SMS}, PackageManager.PERMISSION_GRANTED);

                reference.child("programari").addValueEventListener(takesAppointments());
}


private void setUserType() {
        Intent intent = getIntent();
        if (intent.hasExtra(PATIENT)) {
        type_user= intent.getStringExtra(PATIENT);
        } else {
        fabAddAppointment.setVisibility(View.GONE);
        type_user = intent.getStringExtra(DoctorHomePageActivity.DOCTOR);
        }
        }

private void setRecyclerView() {
        rwAppointments.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        rwAppointments.setLayoutManager(layoutManager);
        setAdapter();
        }

private void setAdapter() {
        adapter = new AppointmentAdapter(appointments, type_user,
        AppointmentsActivity.this, AppointmentsActivity.this,AppointmentsActivity.this, AppointmentsActivity.this,
                getApplicationContext());
        rwAppointments.setAdapter(adapter);
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
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
        }

@SuppressLint("NonConstantResourceId")
@Override
public void onClick(View view) {
        switch (view.getId()) {
        case R.id.fabAddAppointment:
         startActivity(new Intent(getApplicationContext(), ListOfDoctorsActivity.class).putExtra(ADD_APPOINTMENT, ""));
        break;

        case R.id.rbFutureAppointments:
          if (((AppCompatRadioButton) view).isChecked()) {
          rbFuture.setTextColor(Color.WHITE);
          rbHistory.setTextColor(ContextCompat.getColor(this, R.color.unread_notifications));
        }
        reference.child("programari").addValueEventListener(takesAppointments());
        break;
        case R.id.rbHistoryAppointments:
        if (((AppCompatRadioButton) view).isChecked()) {
                rbHistory.setTextColor(Color.WHITE);
                rbFuture.setTextColor(ContextCompat.getColor(this, R.color.unread_notifications));
        }
        reference.child("programari").addValueEventListener(takesAppointments());
        break;
        }
        }

private ValueEventListener takesAppointments() {
        loading(true);
        return new ValueEventListener() {
@Override
public void onDataChange(@NonNull DataSnapshot snapshot) {
        appointments = new ArrayList<>();
        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                Appointment p = dataSnapshot.getValue(Appointment.class);
                if (p.getPatientsId().equals(idUser)) {
                        try {
                                String dateHour = p.getdate() + " " + p.gethour();
                                Date appointmentsDate = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.US).parse(dateHour);
                                if (rbFuture.isChecked()) {
                                        if (appointmentsDate.after(dataCurenta) && p.getStatus().equals(getString(R.string.new_status))) {
                                                appointments.add(p);
                                                patient_phoneNr="";
                                                reference.child("user").child(p.getPatientsId()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                        @Override
                                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                Patient pat=dataSnapshot.getValue(Patient.class);
                                                                if(pat!=null){
                                                                     patient_phoneNr=pat.getNrPhone();
                                                                }
                                                        }

                                                        @Override
                                                        public void onCancelled(@NonNull DatabaseError error) {

                                                        }
                                                });

                                                Calendar c=Calendar.getInstance();
                                                c.setTime(appointmentsDate);
                                                c.add(Calendar.DAY_OF_MONTH,-1);
                                                Date oneDayBeforeAppointmentDate=c.getTime();

                                                Date currentDate=new Date();
                                                if(currentDate.equals(oneDayBeforeAppointmentDate) && Build.VERSION.SDK_INT>=Build.VERSION_CODES.M) {
                                                        if (checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                                                                sendSMSReminder(patient_phoneNr, p);
                                                        }
                                                        else {
                                                                // Request SMS permission
                                                                requestPermissions(new String[]{Manifest.permission.SEND_SMS}, PERMISSION_REQUEST_SEND_SMS);
                                                        }
                                                }

                                        }
                                }
                              else if (rbHistory.isChecked()) {
                                        if (appointmentsDate.before(dataCurenta) && p.getStatus().equals(getString(R.string.new_status))) {
                                                reference.child("programari")
                                                        .child(p.getIdAppointment())
                                                        .child("status")
                                                        .setValue(getString(R.string.empty_status));
                                        }
                                        if (appointmentsDate.before(dataCurenta) || p.getStatus().equals(getString(R.string.canceled_status))) {
                                                appointments.add(p);
                                        }
                                }
                        } catch (ParseException e) {
                                e.printStackTrace();
                        }
                }
                else if(p.getDoctorsId().equals(idUser)){
                        try {
                                String dateHour = p.getdate() + " " + p.gethour();
                                Date appointmentsDate = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.US).parse(dateHour);
                                if (rbFuture.isChecked()) {
                                        if (appointmentsDate.after(dataCurenta) && p.getStatus().equals(getString(R.string.new_status))) {
                                                appointments.add(p);


                                        }
                                } else if (rbHistory.isChecked()) {
                                        if (appointmentsDate.before(dataCurenta) && p.getStatus().equals(getString(R.string.new_status))) {
                                                reference.child("programari")
                                                        .child(p.getIdAppointment())
                                                        .child("status")
                                                        .setValue(getString(R.string.empty_status));
                                        }
                                        if (appointmentsDate.before(dataCurenta) || p.getStatus().equals(getString(R.string.canceled_status))) {
                                                appointments.add(p);
                                        }
                                }
                        } catch (ParseException e) {
                                e.printStackTrace();
                        }

                }
        }


        if (!appointments.isEmpty()) {
        Collections.reverse(appointments);
        ryNoAppointment.setVisibility(View.GONE);
        setAdapter();
        rwAppointments.setVisibility(View.VISIBLE);
        } else {
        rwAppointments.setVisibility(View.GONE);
        ryNoAppointment.setVisibility(View.VISIBLE);
        }

        loading(false);
        }

@Override
public void onCancelled(@NonNull DatabaseError error) {
        Log.e("preluareProgramari", error.getMessage());
        }
        };
        }

private void loading(Boolean seIncarca) {
       if (seIncarca) {
           progressBar.setVisibility(View.VISIBLE);
       } else progressBar.setVisibility(View.GONE);
}

private void sendSMSReminder(String phoneNumber, Appointment appointment) {
        try{
                String message = "Reminder: You have an appointment on " + appointment.getdate() + " at " + appointment.gethour();
                SmsManager smsManager = SmsManager.getDefault();
                smsManager.sendTextMessage(phoneNumber, null, message, null, null);
                Toast.makeText(this,"mesajul e trimis",Toast.LENGTH_SHORT).show();
        } catch(Exception e){
                e.printStackTrace();
                Toast.makeText(this,"Eroare la trimitere",Toast.LENGTH_SHORT).show();
        }

        }


@Override
public void onAppointmentClick(int position) {
       Appointment appointment = appointments.get(position);
        if (appointment.getStatus().equals(getString(R.string.new_status))) {
        Toast.makeText(getApplicationContext(), "Apăsați lung pentru a anula programarea.", Toast.LENGTH_SHORT).show();
        } else if (type_user.equals(DoctorHomePageActivity.DOCTOR) && appointment.getStatus().equals(getString(R.string.empty_status))) {
final String[] status = new String[]{getString(R.string.honored_status), getString(R.string.unhonored_status)};
        statusSelected = status[0];
        AlertDialog.Builder builder = new AlertDialog.Builder(AppointmentsActivity.this)
        .setTitle("Setează status programare")
        .setSingleChoiceItems(status, 0, new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
        statusSelected = status[i];
        }
        })
        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
           @Override
        public void onClick(DialogInterface dialogInterface, int i) {
        reference.child("programari")
        .child(appointment.getIdAppointment())
        .child("status")
        .setValue(statusSelected);

      /*  if (statusSelected.equals(status[1])) {
                reference.child("programari")
        .child(appointment.getIdAppointment())
        .child("status")
        .setValue(getString(R.string.canceled_status));
        }*/
        dialogInterface.dismiss();
        }
        });
        builder.show();
        }
        }

@Override
public void onAppointmentLongClick(int position) {
        Appointment appointment = appointments.get(position);
        if (appointment.getStatus().equals(getString(R.string.new_status))) {
        AlertDialog dialog = new AlertDialog.Builder(AppointmentsActivity.this)
        .setTitle("Confirmare anulare")
        .setMessage("Anulați programarea din " + appointment.getdate() + " de la ora " + appointment.gethour() + "?")
        .setNegativeButton("Nu", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {
        dialogInterface.cancel();
        }
        })
        .setPositiveButton("Da", new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialogInterface, int i) {

        appointment.setStatus(getString(R.string.canceled_status));
        reference.child("programari")
        .child(appointment.getIdAppointment())
        .child("status")
        .setValue(getString(R.string.canceled_status));


        idReceptor = "";
        if (type_user.equals(DoctorHomePageActivity.DOCTOR)) {
        idReceptor = appointment.getPatientsId();
        } else if (type_user.equals(PATIENT)) {
        idReceptor = appointment.getDoctorsId();


        }

        Notification not = new Notification(null,
        getString(R.string.programare_anulata),
        idUser,
        idReceptor,
        appointment.getdate(),
        appointment.gethour(),
        dataNotification,
        false);
        String idNotification = reference.child("Notificari").push().getKey();
        not.setIdNotification(idNotification);
        reference.child("Notificari").child(idNotification).setValue(not);

        dialogInterface.cancel();
        Toast.makeText(getApplicationContext(), "Programarea a fost anulată!", Toast.LENGTH_SHORT).show();
        }
        })
        .create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        }

        }
 private void setDialogFeedback() {
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setTitle("Ce notă acordați ultimei programări?");

                View viewDialog = getLayoutInflater().inflate(R.layout.dialog_feedback, null);
                rgGrade = viewDialog.findViewById(R.id.rgGrade);
                AppCompatButton btnSend = viewDialog.findViewById(R.id.btnSend_Feedback);
                AppCompatButton btnCancel= viewDialog.findViewById(R.id.btnCancel_Feedback);
                tvGrade = viewDialog.findViewById(R.id.tvGrade);

                rgGrade.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(RadioGroup radioGroup, int i) {
                                tvGrade.setError(null);
                        }
                });

                btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                                dialogFeedback.dismiss();
                        }
                });


                btnSend.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                                int idSelectedGrade = rgGrade.getCheckedRadioButtonId();
                                if (idSelectedGrade == -1) {
                                        tvGrade.setError("Alegeți o notă!");
                                        tvGrade.requestFocus();
                                        return;
                                }

                                RadioButton rbGrade = viewDialog.findViewById(idSelectedGrade);
                                grade = Integer.parseInt(rbGrade.getText().toString());


                                Feedback feedback = new Feedback(grade);
                                reference.child("programari").child(appointment.getIdAppointment())
                                        .child("feedback")
                                        .setValue(feedback);
                                reference.child("user").child(appointment.getDoctorsId()).addListenerForSingleValueEvent(takesDoctor());

//
                                Toast.makeText(getApplicationContext(), "Feedback-ul a fost trimis!", Toast.LENGTH_SHORT).show();

                                dialogFeedback.dismiss();
                        }
                });

                builder.setView(viewDialog);
                dialogFeedback = builder.create();
                dialogFeedback.setCanceledOnTouchOutside(false);
        }

        private ValueEventListener takesDoctor() {
                return new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                               Doctor doctor= snapshot.getValue(Doctor.class);
                                if (doctor.getGradesFeedback() == null) {
                                        doctor.setGradesFeedback(new ArrayList<>());
                                }
                                doctor.getGradesFeedback().add(grade);

                                double sum = 0.0;
                                List<Integer> gradesFeedback = doctor.getGradesFeedback();
                                for (int grade : gradesFeedback) {
                                        sum += grade;
                                }
                                double average = gradesFeedback.size() > 0 ? sum / gradesFeedback.size() : 0.0;
                                doctor.setGradeFeedback(average);

                                reference.child("user").child(doctor.getId()).setValue(doctor);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                };
        }

        @Override
        public void onBtnFeedbackClick(int position) {
                appointment= appointments.get(position);
                resetsInput();
                dialogFeedback.show();
        }

        @Override
        public void onBtnRetetaClickListener(int position) {
                Appointment appointment = appointments.get(position);
                if (type_user.equals(PATIENT)) {
                        String namePdfPrescription = "reteta" + appointment.getIdAppointment() + ".pdf";
                        Uri urlPrescription = Uri.parse(appointment.getUrlPrescription());
                        downloadPrescription(urlPrescription, Environment.DIRECTORY_DOWNLOADS, namePdfPrescription);
                } else if (type_user.equals(DOCTOR)) {
                        if (  appointment.getUrlPrescription() == null || appointment.getUrlPrescription().equals("")) {
                                if (uriPDF == null) {
                                        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                                                attachPdf();
                                        } else {
                                                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, REQUEST_CODE_READ_EXTERNAL_STORAGE);
                                        }
                                } else {
                                        uploadPdf(uriPDF, appointment);
                                }
                        } else {
                                String namePdfPrescription = "reteta" + appointment.getIdAppointment() + ".pdf";
                                Uri urlPrescription = Uri.parse(appointment.getUrlPrescription());
                                downloadPrescription(urlPrescription, Environment.DIRECTORY_DOWNLOADS, namePdfPrescription);
                        }
                }
        }

        private void downloadPrescription(Uri uri, String directorDestination, String nameFile) {
                DownloadManager downloadManager = (DownloadManager) getApplicationContext()
                        .getSystemService(Context.DOWNLOAD_SERVICE);
                DownloadManager.Request request = new DownloadManager.Request(uri);
                Toast.makeText(getApplicationContext(), "Rețeta se descarcă...",
                        Toast.LENGTH_SHORT).show();
                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalFilesDir(getApplicationContext(),
                        directorDestination, nameFile);
                downloadManager.enqueue(request);
        }

        private void uploadPdf(Uri uriPDF, Appointment appointment) {
                progressDialog = new ProgressDialog(this, R.style.ProgressDialogStyle);
                progressDialog.setMessage("Documentul se încarcă…");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.show();

                StorageReference referencePrescription = FirebaseStorage.getInstance().getReference()
                        .child("documente pacient")
                        .child(appointment.getPatientsId())
                        .child("urlPrescription")
                        .child(appointment.getIdAppointment());
                referencePrescription.putFile(uriPDF)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        referencePrescription.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                        urlPrescription = uri;
                                                        reference.child("programari").child(appointment.getIdAppointment())
                                                                .child("urlPrescription")
                                                                .setValue(urlPrescription.toString());
                                                        Toast.makeText(getApplicationContext(),
                                                                "Rețeta a fost încărcată cu succes!", Toast.LENGTH_SHORT).show();
                                                }
                                        }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                        });

                                        progressDialog.dismiss();
                                }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(getApplicationContext(),
                                                "Rețeta nu a putut fi încărcată!", Toast.LENGTH_SHORT).show();

                                        progressDialog.dismiss();
                                }
                        });

        }

        private void attachPdf() {
                Intent intent = new Intent();
                intent.setType("application/pdf");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQUEST_CODE_INTENT);
        }

        @Override
        protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
                super.onActivityResult(requestCode, resultCode, data);
                if (requestCode == REQUEST_CODE_INTENT && resultCode == RESULT_OK && data != null) {
                        uriPDF = data.getData();
                }
                Toast.makeText(getApplicationContext(), "Apăsați din nou pe ”Atașează rețetă” pentru a o încărca.", Toast.LENGTH_LONG).show();
        }

        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
                if (requestCode == REQUEST_CODE_READ_EXTERNAL_STORAGE && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                        attachPdf();
                }
        }

        private void resetsInput() {
                rgGrade.clearCheck();
                tvGrade.setError(null);

        }




}
