package com.example.smiletogether_dentalapp.Patient;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.smiletogether_dentalapp.Adapter.AvailableHourAdapter;
import com.example.smiletogether_dentalapp.Adapter.ScheduleAdapter;
import com.example.smiletogether_dentalapp.Model.Appointment;
import com.example.smiletogether_dentalapp.Model.Doctor;
import com.example.smiletogether_dentalapp.Model.Investigation;
import com.example.smiletogether_dentalapp.Model.Notification;
import com.example.smiletogether_dentalapp.Model.Speciality;
import com.example.smiletogether_dentalapp.Model.WorkDay;
import com.example.smiletogether_dentalapp.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
@RequiresApi(api = Build.VERSION_CODES.O)
public class AvailableHoursActivity extends AppCompatActivity implements View.OnClickListener, AvailableHourAdapter.OnOraClickListener {
        private final DateFormat format = new SimpleDateFormat("HH:mm", Locale.US); //parsare ora din string
        //private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        //private final String dataCurenta = formatter.format(LocalDate.now());
         Date currentDate = new Date();
         SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
         String formattedDate = formatter.format(currentDate);
        private List<Appointment> appointments = new ArrayList<>();
        private TextView tvDoctor;
        private TextInputEditText tietDateOfAppointment;
        private List<String> availableHours=new ArrayList<>();
        private List<String> unavailableHour=new ArrayList<>();




    FirebaseAuth auth = FirebaseAuth.getInstance();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://smiletogetherdentalapp-default-rtdb.firebaseio.com/");
    private String idPatient=auth.getCurrentUser().getUid();

        private AvailableHourAdapter adapter;
        private RecyclerView rwAvailableHours;

        private LinearLayout llSelectDate;

        private AutoCompleteTextView actvAppointmentPurpose;
        private List<Investigation> investigations;
        private List<String> investigationName = new ArrayList<>();

        private Toolbar toolbar;
        Doctor doctor;


        private ProgressBar progressBar;

        private RelativeLayout rlNoHour;
        private TextView tvNoHour;


        private ListView lvSchedule;
        private List<String> appointmentPurpose;
        private ScheduleAdapter scheduleAdapter;

        @RequiresApi(api = Build.VERSION_CODES.O)
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_available_hours);

            //initialization
            toolbar = findViewById(R.id.toolbar);
            llSelectDate = findViewById(R.id.llSelectDate);
            tvDoctor = findViewById(R.id.tvDoctor);
            tietDateOfAppointment = findViewById(R.id.tietDateOfAppointment);

            actvAppointmentPurpose = findViewById(R.id.actvInvestigationAppointment);

            availableHours = new ArrayList<>();
            rwAvailableHours = findViewById(R.id.rwAvailableHours);

            progressBar = findViewById(R.id.progressBar);

            rlNoHour = findViewById(R.id.rlNoHour);
            tvNoHour = findViewById(R.id.tvNoHour);



            lvSchedule = findViewById(R.id.lvScheduleDoctor);


            setToolbar();


          Intent intent=getIntent();
            if(intent.getExtras()!=null){
                doctor=(Doctor) intent.getSerializableExtra(ListOfDoctorsActivity.AVAILABLE_HOURS);
            }



            actvAppointmentPurpose.setOnClickListener(this);

            setInfoDoctor();

            tietDateOfAppointment.setOnClickListener(this);

            appointmentPurpose = new ArrayList<>();

            //sex
            ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.dropdown_item,
                    getResources().getStringArray(R.array.appointment_purpose));
            actvAppointmentPurpose.setAdapter(adapter);

            //reference.child("specialitati").child(doctor.getidSpeciality()).addListenerForSingleValueEvent(takeInvestigation());

            setRecyclerView();
        }

        private void setInfoDoctor() {

            String name = "Dr. " +doctor.getlastname()+" "+doctor.getFirstname();
            tvDoctor.setText(name);

            scheduleAdapter = new ScheduleAdapter(getApplicationContext(),R.layout.item_schedule_doctor,doctor.getSchedule(), getLayoutInflater());
            lvSchedule.setAdapter(scheduleAdapter);
        }

        private void setRecyclerView() {
            rwAvailableHours.setHasFixedSize(true);
            GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 4, GridLayoutManager.VERTICAL, false);
            rwAvailableHours.setLayoutManager(gridLayoutManager);
            setAdapter();
        }

        private void setAdapter() {
            adapter = new AvailableHourAdapter(availableHours, AvailableHoursActivity.this);
            rwAvailableHours.setAdapter(adapter);
        }

        private void setToolbar() {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }



       /* private ValueEventListener takeInvestigation() {
            return new ValueEventListener() {
                @RequiresApi(api = Build.VERSION_CODES.O)
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    investigations = new ArrayList<>();
                    Speciality speciality = snapshot.getValue(Speciality.class);
                    investigations.addAll(speciality.getInvestigations());
                    for (Investigation inv : investigations) {
                        if (inv != null && inv.getname() != null) {
                            investigationName.add(inv.getname());
                        }
                    }


                    ArrayAdapter<String> adapter = new ArrayAdapter(getApplicationContext(), R.layout.dropdown_item, investigationName);
                    actvInvestigation.setAdapter(adapter);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("preluareInvestigatii", error.getMessage());
                }
            };
        }*/

        @Override
        public boolean onOptionsItemSelected(@NonNull MenuItem item) {
            if (item.getItemId() == android.R.id.home) {
                finish();
                return true;
            }
            return super.onOptionsItemSelected(item);
        }

        @SuppressLint("NonConstantResourceId")
        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.tietDateOfAppointment:
                    displayCalendar();
                    break;
                case R.id.actvInvestigationAppointment:
                    actvAppointmentPurpose.setError(null);
                    break;
            }

        }
    private void displayCalendar() {
        final Calendar calendar = Calendar.getInstance();

        if (!tietDateOfAppointment.getText().toString().equals(getString(R.string.selectati_data))) {
            try {
                Date selectedDate = new SimpleDateFormat("dd/MM/yyyy", Locale.US).parse(tietDateOfAppointment.getText().toString());
                calendar.setTime(selectedDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        int day = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        DatePickerDialog datePickerDialog = new DatePickerDialog(AvailableHoursActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        month += 1;
                        String dateString = day + "/" + month + "/" + year;
                        tietDateOfAppointment.setText(dateString);

                        displayAvailableHour(dateString);
                    }
                }, year, month, day) {

            // Override the onDateChanged method to disable weekends
            @Override
            public void onDateChanged(DatePicker view, int year, int month, int day) {
                // Get the selected date
                Calendar selectedDate = Calendar.getInstance();
                selectedDate.set(year, month, day);

                // Check if it's a weekend (Saturday or Sunday)
                if (selectedDate.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY ||
                        selectedDate.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                    // Disable the OK button and set the date to the previous valid date (Friday or Monday)
                    view.updateDate(year, month, day - 1);
                } else {
                    // Enable the OK button and set the selected date
                    super.onDateChanged(view, year, month, day);
                }
            }
        };

        //setare data minima
        Calendar dateMin = Calendar.getInstance();
        dateMin.add(Calendar.DAY_OF_MONTH, 1); //adaug o zi la data curenta
        datePickerDialog.getDatePicker().setMinDate(dateMin.getTimeInMillis()); //si o setez ca valoare minima

        //setare data maxima
        Calendar dateMax = Calendar.getInstance();
        dateMax.add(Calendar.DAY_OF_MONTH, 90);
        datePickerDialog.getDatePicker().setMaxDate(dateMax.getTimeInMillis());

        datePickerDialog.show();
        datePickerDialog.setCanceledOnTouchOutside(false);
    }












    /*private void displayCalendar() {
        final Calendar calendar = Calendar.getInstance();

        if (!tietDateOfAppointment.getText().toString().equals(getString(R.string.selectati_data))) {
            try {
                Date selectedDate = new SimpleDateFormat("dd/MM/yyyy", Locale.US).parse(tietDateOfAppointment.getText().toString());
                calendar.setTime(selectedDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        int day = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH);
        int year = calendar.get(Calendar.YEAR);

        DatePickerDialog datePickerDialog = new DatePickerDialog(AvailableHoursActivity.this,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        month += 1;
                        String dateString = day + "/" + month + "/" + year;
                        tietDateOfAppointment.setText(dateString);

                        displayAvailableHour(dateString);
                    }
                }, year, month, day);

        // set minimum and maximum dates
        Calendar dateMin = Calendar.getInstance();
        dateMin.add(Calendar.DAY_OF_MONTH, 1); // add one day to current date
        datePickerDialog.getDatePicker().setMinDate(dateMin.getTimeInMillis()); // and set it as minimum

        Calendar dateMax = Calendar.getInstance();
        dateMax.add(Calendar.DAY_OF_MONTH, 90);
        datePickerDialog.getDatePicker().setMaxDate(dateMax.getTimeInMillis()); // set as maximum

        // disable weekends
        datePickerDialog.getDatePicker().init(year, month, day, new DatePicker.OnDateChangedListener() {
            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                Calendar selectedDate = Calendar.getInstance();
                selectedDate.set(year, monthOfYear, dayOfMonth);
                int dayOfWeek = selectedDate.get(Calendar.DAY_OF_WEEK);
                if (dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY) {
                    // if the selected day is a weekend day, reset to the nearest weekday
                    if (dayOfWeek == Calendar.SATURDAY) {
                        selectedDate.add(Calendar.DAY_OF_MONTH, -1);
                    } else if (dayOfWeek == Calendar.SUNDAY) {
                        selectedDate.add(Calendar.DAY_OF_MONTH, 1);
                    }
                    // update the DatePicker with the new date
                    view.updateDate(selectedDate.get(Calendar.YEAR), selectedDate.get(Calendar.MONTH), selectedDate.get(Calendar.DAY_OF_MONTH));
                }
            }
        });

        datePickerDialog.show();
        datePickerDialog.setCanceledOnTouchOutside(false);
    }*/







       /* private void displayCalendar() {
            final Calendar calendar = Calendar.getInstance();

            if (!tietDateOfAppointment.getText().toString().equals(getString(R.string.selectati_data))) {
                try {
                    Date selectedDate = new SimpleDateFormat("dd/MM/yyyy", Locale.US).parse(tietDateOfAppointment.getText().toString());
                    calendar.setTime(selectedDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            int day = calendar.get(Calendar.DATE);
            int month = calendar.get(Calendar.MONTH);
            int year = calendar.get(Calendar.YEAR);

            DatePickerDialog datePickerDialog = new DatePickerDialog(AvailableHoursActivity.this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                            month += 1;
                            String dateString = day + "/" + month + "/" + year;
                            tietDateOfAppointment.setText(dateString);

                            displayAvailableHour(dateString);
                        }
                    }, year, month, day);

            //setare data minima
            Calendar dateMin = Calendar.getInstance();
            dateMin.add(Calendar.DAY_OF_MONTH, 1); //adaug o zi la data curenta
            datePickerDialog.getDatePicker().setMinDate(dateMin.getTimeInMillis()); //si o setez ca valoare minima

            //setare data maxima
            Calendar dateMax = Calendar.getInstance();
            dateMax.add(Calendar.DAY_OF_MONTH, 90);
            datePickerDialog.getDatePicker().setMaxDate(dateMax.getTimeInMillis());

            datePickerDialog.show();
            datePickerDialog.setCanceledOnTouchOutside(false);
        }*/

        private void displayAvailableHour(String dateString) {
            llSelectDate.setVisibility(View.GONE);
            Date date = null;
            try {
                date = new SimpleDateFormat("dd/MM/yyyy", Locale.US).parse(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String appointmentDay = new SimpleDateFormat("EEEE", new Locale("ro", "RO")).format(date);

            for (WorkDay z : doctor.getSchedule()) {
                if (z.getDay().equals(appointmentDay)) {
                    availableHours = new ArrayList<>();
                    try {
                        Date dStartTime= format.parse(z.getStartTime());
                        Calendar cStartTime = Calendar.getInstance();
                        cStartTime.setTime(dStartTime);

                        Date dEndTime = format.parse(z.getEndTime());
                        Calendar cEndTime = Calendar.getInstance();
                        cEndTime.setTime(dEndTime);
                        cEndTime.add(Calendar.MINUTE, -40);

                    /*cat timp ora de inceput e mai mica decat ora de sfarsit - 40 min
                    pt ca ultima programare poate fi de la orasf - 40 min
                    ca ora de sfasit inseamna ca atunci se termina programul medicului*/
                        while (cStartTime.getTime().compareTo(cEndTime.getTime()) <= 0) {
                            availableHours.add(format.format(cStartTime.getTime()));
                            cStartTime.add(Calendar.MINUTE, 40);
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    break;
                } else availableHours = null;
            }

            if (availableHours == null) {
                rwAvailableHours.setVisibility(View.GONE);
                String message = "Medicul nu are program in zilele de " + appointmentDay + "!";
                tvNoHour.setText(message);
                rlNoHour.setVisibility(View.VISIBLE);
            } else if (!availableHours.isEmpty()) {
                reference.child("programari").addValueEventListener(takeAvailableHour());
            }

        }

        private ValueEventListener takeAvailableHour() {
            loading(true);
            return new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    unavailableHour = new ArrayList<>();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Appointment p = dataSnapshot.getValue(Appointment.class);
                            if (p.getDoctorsId().equals(doctor.getId()) &&
                                    p.getdate().equals(tietDateOfAppointment.getText().toString())
                                    && p.getStatus().equals("noua")) {
                                // daca programarea a fost anulata (statusul nu e "noua")
                                // atunci trec ora ca fiind disponibila
                                unavailableHour.add(p.gethour());
                            }
                        }


                    availableHours.removeAll(unavailableHour);

                    // cand toate orele din program sunt indisponibile
                    if (availableHours.isEmpty()) {
                        rwAvailableHours.setVisibility(View.GONE);
                        tvNoHour.setText(getString(R.string.no_hour));
                        rlNoHour.setVisibility(View.VISIBLE);
                    } else {
                        rlNoHour.setVisibility(View.GONE);
                        rwAvailableHours.setVisibility(View.VISIBLE);
                        setAdapter();
                    }

                    loading(false);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("preluareProgramari", error.getMessage());
                }
            };
        }

        @Override
        public void onOraClick(int position) {
            if (!actvAppointmentPurpose.getText().toString().equals(getString(R.string.select_appointment_purpose))) {
                AlertDialog dialog = new AlertDialog.Builder(AvailableHoursActivity.this)
                        .setTitle("Confirmare programare")
                        .setMessage("Trimiteți programarea pentru data " + tietDateOfAppointment.getText().toString() + " la ora " + availableHours.get(position) + "?")
                        .setNegativeButton("Nu", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        })
                        .setPositiveButton("Da", new DialogInterface.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.O)
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                Appointment appointment = new Appointment(null,
                                        doctor.getId(),idPatient,tietDateOfAppointment.getText().toString(), availableHours.get(position),"noua",null,"");
                                String idAppointment = reference.child("programari").push().getKey();
                                appointment.setIdAppointment(idAppointment);
                                reference.child("programari").child(idAppointment).setValue(appointment);

                                Notification not = new Notification(null,
                                         getString(R.string.programare_noua),
                                         idPatient,
                                         appointment.getDoctorsId(),
                                         appointment.getdate(),
                                         appointment.gethour(),
                                         formattedDate,
                                         false);

                                         String idNotification = reference.child("Notificari").push().getKey();
                                         not.setIdNotification(idNotification);
                                         reference.child("Notificari").child(idNotification).setValue(not);


                                Toast.makeText(getApplicationContext(), "Programarea a fost trimisă!", Toast.LENGTH_SHORT).show();
                                dialogInterface.cancel();

                                finish();

                            }
                        }).create();
                dialog.show();
                dialog.setCanceledOnTouchOutside(false);
            } else {
                actvAppointmentPurpose.setError(getString(R.string.err_empty_appointment_purpose));
                actvAppointmentPurpose.requestFocus();
            }
        }

    private void loading(Boolean seIncarca) {
        if (seIncarca) {
            progressBar.setVisibility(View.VISIBLE);
        } else progressBar.setVisibility(View.GONE);
    }
}


