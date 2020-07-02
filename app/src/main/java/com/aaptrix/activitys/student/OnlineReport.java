package com.aaptrix.activitys.student;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Objects;

import static com.aaptrix.tools.HttpUrl.ONLINE_EXAM_QUES;
import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREF_COLOR;

public class OnlineReport extends AppCompatActivity {

    AppBarLayout appBarLayout;
    TextView tool_title;
    ProgressBar progressBar;
    TextView noResult, totalMrks;
    Button viewAnswer;
    String userId, schoolId, examId, examName,section;
    PieChart pieChart;
    String totalQues, correctAns, unattemptedAns, wrongAns, marks, totalMarks;
    int[] color = {Color.parseColor("#B8860B"),
            Color.parseColor("#8B0000"),
            Color.parseColor("#228B22"),
            Color.parseColor("#4B0082")};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_report);
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
        viewAnswer = findViewById(R.id.view_answer);
        pieChart = findViewById(R.id.pieChart);
        totalMrks = findViewById(R.id.total_marks);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        schoolId = settings.getString("str_school_id", "");
        userId = getIntent().getStringExtra("userId");
        examId = getIntent().getStringExtra("examId");
        examName = getIntent().getStringExtra("examName");
        section = getIntent().getStringExtra("userSection");
        tool_title.setText(examName);

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

        GetQuestion getQuestion = new GetQuestion(this);
        getQuestion.execute(schoolId, examId, userId);

        viewAnswer.setOnClickListener(v -> Toast.makeText(this, "Not Attempted", Toast.LENGTH_SHORT).show());
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
        super.onBackPressed();
    }

    @SuppressLint("StaticFieldLeak")
    public class GetQuestion extends AsyncTask<String, String, String> {
        Context ctx;

        GetQuestion(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {

            String school_id = params[0];
            String examId = params[1];
            String userId = params[2];
            String data;

            try {

                URL url = new URL(ONLINE_EXAM_QUES);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                data = URLEncoder.encode("school_id", "UTF-8") + "=" + URLEncoder.encode(school_id, "UTF-8") + "&" +
                        URLEncoder.encode("exam_id", "UTF-8") + "=" + URLEncoder.encode(examId, "UTF-8") + "&" +
                        URLEncoder.encode("tbl_users_id", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8");
                outputStream.write(data.getBytes());

                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.flush();
                outputStream.close();

                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1));
                StringBuilder response = new StringBuilder();
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    response.append(line);
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                return response.toString();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            progressBar.setVisibility(View.GONE);
            if (!result.contains("\"resultGraphDetails\":null")) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("resultGraphDetails");
                    JSONObject object = jsonArray.getJSONObject(0);
                    correctAns = object.getString("Correct");
                    unattemptedAns = object.getString("notAnswer");
                    wrongAns = object.getString("Wrong");
                    marks = object.getString("userTotalMarksObtained");
                    totalQues = object.getString("totalQueCount");
                    totalMarks = object.getString("totalQueMarks");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setData();
            } else {
                Toast.makeText(ctx, "No Result", Toast.LENGTH_SHORT).show();
                noResult.setVisibility(View.VISIBLE);
            }
            super.onPostExecute(result);
        }

    }

    @SuppressLint({"SetTextI18n", "DefaultLocale"})
    private void setData() {
        totalMrks.setText("Score " + String.format("%.2f", Float.valueOf(marks)) + "/" + totalMarks);

        viewAnswer.setOnClickListener(v -> {
            Intent intent = new Intent(this, OnlineExamResult.class);
            intent.putExtra("userId", userId);
            intent.putExtra("userSection", section);
            intent.putExtra("examId", examId);
            intent.putExtra("examName", examName);
            startActivity(intent);
        });

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
