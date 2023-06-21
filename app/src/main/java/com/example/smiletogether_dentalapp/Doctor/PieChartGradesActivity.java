package com.example.smiletogether_dentalapp.Doctor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;

import com.example.smiletogether_dentalapp.Adapter.DoctorAdapter;
import com.example.smiletogether_dentalapp.Model.Doctor;
import com.example.smiletogether_dentalapp.R;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.ArrayList;
import java.util.List;


public class PieChartGradesActivity extends AppCompatActivity {

    FirebaseAuth auth = FirebaseAuth.getInstance();
    DatabaseReference reference = FirebaseDatabase.getInstance().getReferenceFromUrl("https://smiletogetherdentalapp-default-rtdb.firebaseio.com/");
    String idDoctorConnected=auth.getCurrentUser().getUid();

    private final int[] totalGrades = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0}; // vector de frecventa a notelor
    private Toolbar toolbar;
    private PieChart pieChart;
    private List<Integer> gradesFeedback = new ArrayList<>();
    private double gradPointAverage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pie_chart_grades);

        //initializeaza atribute
        toolbar = findViewById(R.id.toolbar);
        pieChart = findViewById(R.id.pieChart);

        setToolbar();

        reference.child("user").child(idDoctorConnected).addListenerForSingleValueEvent(takesDoctor());

    }
    private void setToolbar() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    private ValueEventListener takesDoctor() {
        return new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Doctor doctor = snapshot.getValue(Doctor.class);
                gradesFeedback = doctor.getGradesFeedback();
                gradPointAverage = doctor.getGradeFeedback();
                if (gradesFeedback != null) {
                    for (Integer grade : gradesFeedback) {
                        totalGrades[grade - 1]++;
                    }
                }
                graphicDrawing();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };
    }

    private void graphicDrawing() {
        ArrayList<PieEntry> grades = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            if (totalGrades[i] != 0) {
                grades.add(new PieEntry(totalGrades[i], String.valueOf(i + 1)));
            }
        }

        PieDataSet pieDataSet = new PieDataSet(grades, "Note acordate");
        pieDataSet.setColors(ColorTemplate.PASTEL_COLORS);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(16f);

        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("Medie: " + DoctorAdapter.NUMBER_FORMAT.format(gradPointAverage));
        pieData.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }

        });
        pieChart.invalidate();
        pieChart.animateY(1000, Easing.EaseInQuad);
    }

   /* private void generatePieChart() {
        ArrayList<PieEntry> grades = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            if (totalGrades[i] != 0) {
                grades.add(new PieEntry(totalGrades[i], String.valueOf(i + 1)));
            }
        }

        PieDataSet pieDataSet = new PieDataSet(grades, "Note acordate");
        pieDataSet.setColors(ColorTemplate.PASTEL_COLORS);
        pieDataSet.setValueTextColor(Color.BLACK);
        pieDataSet.setValueTextSize(16f);

        PieData pieData = new PieData(pieDataSet);
        pieData.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return String.valueOf((int) value);
            }
        });

        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.setDrawEntryLabels(false);
        pieChart.setUsePercentValues(true);
        pieChart.setHoleRadius(50f);
        pieChart.setTransparentCircleRadius(60f);
        pieChart.setCenterText("Medie: " + DoctorAdapter.NUMBER_FORMAT.format(gradPointAverage));
        pieChart.animateY(1000, Easing.EaseInQuad);
    }*/




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
}