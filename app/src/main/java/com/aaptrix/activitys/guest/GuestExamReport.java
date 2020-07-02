package com.aaptrix.activitys.guest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aaptrix.R;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.android.material.appbar.AppBarLayout;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;

import static com.aaptrix.tools.SPClass.PREF_COLOR;

public class GuestExamReport extends AppCompatActivity {

    AppBarLayout appBarLayout;
    TextView tool_title;
    ProgressBar progressBar;
    TextView noResult, totalMrks, correct, wrong, notAttempt;
    String examName;
    Button viewAnswer;
    PieChart pieChart;
    String totalQues, correctAns, unattemptedAns, wrongAns, quesArray, ansArray;
    int[] color = {Color.parseColor("#B8860B"),
            Color.parseColor("#8B0000"),
            Color.parseColor("#228B22"),
            Color.parseColor("#4B0082")};

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_exam_report);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("");
        setResult(RESULT_OK);
        appBarLayout = findViewById(R.id.appBarLayout);
        tool_title = findViewById(R.id.tool_title);
        progressBar = findViewById(R.id.progress_bar);
        noResult = findViewById(R.id.no_result);
        pieChart = findViewById(R.id.pieChart);
        totalMrks = findViewById(R.id.total_marks);
        correct = findViewById(R.id.correct);
        wrong = findViewById(R.id.wrong);
        notAttempt = findViewById(R.id.not_attempt);
        viewAnswer = findViewById(R.id.view_answer);

        examName = getIntent().getStringExtra("examName");
        totalQues = getIntent().getStringExtra("total_ques");
        correctAns = getIntent().getStringExtra("correct");
        wrongAns = getIntent().getStringExtra("wrong");
        unattemptedAns = getIntent().getStringExtra("not_attempted");
        ansArray = getIntent().getStringExtra("ans_array");
        quesArray = getIntent().getStringExtra("ques_array");

        tool_title.setText(examName);
        totalMrks.setText("Total : " + totalQues);
        correct.setText("Correct : " + correctAns);
        wrong.setText("Wrong : " + wrongAns);
        notAttempt.setText("Not Attempted : " + unattemptedAns);

        setData();

        viewAnswer.setOnClickListener(v -> {
            Intent intent = new Intent(this, GuestAnswerSheet.class);
            intent.putExtra("ans_array", ansArray);
            intent.putExtra("ques_array", quesArray);
            intent.putExtra("examName", examName);
            startActivity(intent);
        });

        SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
        String selToolColor = settingsColor.getString("tool", "");
        String selStatusColor = settingsColor.getString("status", "");
        String selTextColor1 = settingsColor.getString("text1", "");

        appBarLayout.setBackgroundColor(Color.parseColor(selToolColor));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(selStatusColor));
        }
        tool_title.setTextColor(Color.parseColor(selTextColor1));
        viewAnswer.setBackgroundColor(Color.parseColor(selToolColor));
        viewAnswer.setTextColor(Color.parseColor(selTextColor1));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(this, GuestExam.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        finish();
    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void setData() {

        pieChart.setDrawHoleEnabled(false);
        pieChart.getDescription().setEnabled(false);
        pieChart.setRotationEnabled(false);
        pieChart.setNoDataText("Not Attempted");

        Legend l = pieChart.getLegend();
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(8f);
        l.setTextColor(Color.BLACK);
        l.setTextSize(12f);

        ArrayList<PieEntry> entries = new ArrayList<>();

        entries.add(new PieEntry(Float.parseFloat(unattemptedAns), "Unattempted"));
        entries.add(new PieEntry(Float.parseFloat(wrongAns), "Wrong Answer"));
        entries.add(new PieEntry(Float.parseFloat(correctAns), "Correct Answer"));

        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setDrawValues(true);
        dataSet.setSliceSpace(3f);
        dataSet.setSelectionShift(5f);
        dataSet.setColors(color);

        dataSet.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                DecimalFormat format = new DecimalFormat("###,###,###.0");
                return format.format(value / Float.parseFloat(totalQues) * 100) + "%";
            }
        });

        PieData data = new PieData(dataSet);
        data.setValueTextSize(14f);
        data.setValueTextColor(Color.WHITE);
        pieChart.setData(data);
        pieChart.invalidate();
    }
}