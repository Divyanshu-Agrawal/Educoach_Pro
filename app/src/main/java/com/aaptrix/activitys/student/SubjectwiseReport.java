package com.aaptrix.activitys.student;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.aaptrix.databeans.DataBeanStudent;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
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
import java.util.ArrayList;
import java.util.Objects;

import com.aaptrix.R;
import com.aaptrix.databeans.ResultData;
import com.aaptrix.adaptor.StudentAdapter;

import static com.aaptrix.tools.HttpUrl.MIN_MAX_MARKS;
import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREF_COLOR;

public class SubjectwiseReport extends AppCompatActivity {

    ArrayList<ResultData> examArray = new ArrayList<>(), subjectArray = new ArrayList<>();
    String resultJson;
    AppBarLayout appBarLayout;
    String selToolColor, selStatusColor, selTextColor1;
    TextView tool_title, no_report;
    Spinner subjects;
    ProgressBar progressbar;
    LinearLayout layout, label;
    private String strSubject, schoolId, userId, batchNm;
    private ArrayList<String> subArray = new ArrayList<>();
    int[] color;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subjectwise_report);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        appBarLayout = findViewById(R.id.appBarLayout);
        tool_title = findViewById(R.id.tool_title);
        resultJson = getIntent().getStringExtra("resultArray");
        subjects = findViewById(R.id.type_spinner);
        label = findViewById(R.id.label);
        SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
        selToolColor = settingsColor.getString("tool", "");
        selStatusColor = settingsColor.getString("status", "");
        selTextColor1 = settingsColor.getString("text1", "");
        String usertype = getIntent().getStringExtra("usertype");
        progressbar = findViewById(R.id.loader);
        layout = findViewById(R.id.linear_layout);
        no_report = findViewById(R.id.no_report);

        color = new int[]{
                getResources().getColor(R.color.trans),
                getResources().getColor(R.color.navi_black),
                getResources().getColor(R.color.trans),
                getResources().getColor(R.color.red),
                getResources().getColor(R.color.cream_yellow),
                getResources().getColor(R.color.green)};

        SharedPreferences sp = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        schoolId = sp.getString("userSchoolId", "");
        if (usertype.equals("student")) {
            userId = sp.getString("userID", "");
            batchNm = sp.getString("userSection", "");
        } else {
            userId = getIntent().getStringExtra("studentId");
            batchNm = getIntent().getStringExtra("section");
        }

        appBarLayout.setBackgroundColor(Color.parseColor(selToolColor));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(selStatusColor));
        }
        tool_title.setTextColor(Color.parseColor(selTextColor1));

        try {
            progressbar.setVisibility(View.VISIBLE);
            JSONObject jsonRootObject = new JSONObject(resultJson);
            JSONArray jsonExamArray = jsonRootObject.getJSONArray("examNm");
            for (int i = 0; i < jsonExamArray.length(); i++) {
                JSONObject jObject = jsonExamArray.getJSONObject(i);
                ResultData resultData = new ResultData();
                resultData.setResultId(jObject.getString("tbl_result_id"));
                resultData.setExamName(jObject.getString("tbl_result_exam_name"));
                examArray.add(resultData);
            }
            JSONArray jsonSubjectArray = jsonRootObject.getJSONArray("subjectNm");
            for (int i = 0; i < jsonSubjectArray.length(); i++) {
                JSONObject jObject = jsonSubjectArray.getJSONObject(i);
                ResultData data = new ResultData();
                data.setSubjectName(jObject.getString("tbl_batch_subjct_name"));
                subArray.add(jObject.getString("tbl_batch_subjct_name"));
                subjectArray.add(data);
            }
            ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, R.layout.spinner_list_item1, subArray);
            dataAdapter.setDropDownViewResource(R.layout.spinner_list_item1);
            subjects.setAdapter(dataAdapter);
            if (subArray.size() != 0)
                strSubject = subArray.get(0);
            MinMax minMax = new MinMax(SubjectwiseReport.this, strSubject);
            minMax.execute(schoolId, userId, batchNm);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        subjects.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                strSubject = subArray.get(position);
                MinMax minMax = new MinMax(SubjectwiseReport.this, strSubject);
                minMax.execute(schoolId, userId, batchNm);
                progressbar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void setSubjectwise(String subName, String minMaxJson) throws JSONException {

        JSONObject jo = new JSONObject(minMaxJson);
        JSONObject minMax = jo.getJSONObject("SubjectsWiseMinMax");
        JSONObject maxMarks = jo.getJSONObject("MaxMarksUserDetails");
        JSONObject outOfArray = jo.getJSONObject("ExamOutOfMarks");
        layout.removeAllViews();

        for (int i = 0; i < examArray.size(); i++) {

            BarChart chart = new BarChart(this);

            ArrayList<BarDataSet> dataSets = new ArrayList<>();

            ArrayList<String> mrks = new ArrayList<>();
            mrks.add(" ");
            mrks.add("Marks");
            mrks.add(" ");
            mrks.add("Minimum");
            mrks.add("Average");
            mrks.add("Maximum");

            ArrayList<BarEntry> entry = new ArrayList<>();
            JSONArray minMaxArray = minMax.getJSONArray(examArray.get(i).getExamName());
            ArrayList<ResultData> resArray = new ArrayList<>();

            for (int j = 0; j < minMaxArray.length(); j++) {
                JSONObject object = minMaxArray.getJSONObject(j);
                ResultData data = new ResultData();
                data.setMarks(object.getString("Marks"));
                data.setSubjectName(object.getString("subjectNm"));
                data.setAvg(object.getString("Average"));
                data.setMax(object.getString("MaxMarks"));
                data.setMin(object.getString("MinMarks"));
                resArray.add(data);
            }

            for (int j = 0; j < resArray.size(); j++) {
                if (resArray.get(j).getSubjectName().equals(subName)) {
                    entry.add(new BarEntry(0, 0f));
                    entry.add(new BarEntry(1, Float.valueOf(resArray.get(j).getMarks())));
                    entry.add(new BarEntry(2, 0f));
                    entry.add(new BarEntry(3, Float.valueOf(resArray.get(j).getMin())));
                    entry.add(new BarEntry(4, Float.valueOf(resArray.get(j).getAvg())));
                    entry.add(new BarEntry(5, Float.valueOf(resArray.get(j).getMax())));
                    break;
                }
            }

            float barWidth;
            float barSpace; // x4 DataSet

            barWidth = 0.5f;
            barSpace = 0.08f;
            float groupSpace = 0.06f;

            for (int j = 0; j < entry.size(); j++) {
                ArrayList<BarEntry> ent = new ArrayList<>();
                ent.add(entry.get(j));
                BarDataSet d = new BarDataSet(ent, mrks.get(j));
                if (j == 0 || j == 2) {
                    d.setDrawValues(false);
                } else {
                    d.setValueTextSize(12);
                }
                d.setColor(color[j]);
                dataSets.add(d);
            }

            BarData barData = new BarData();
            for (int k = 0; k < dataSets.size(); k++) {
                barData.addDataSet(dataSets.get(k));
            }

            barData.setBarWidth(barWidth);
            if (barData.getDataSetCount() != 0) {
                chart.setData(barData);
                if (dataSets.size() > 1) {
                    chart.groupBars(0, groupSpace, barSpace);
                }
            }

            XAxis xAxis = chart.getXAxis();
            xAxis.setDrawLabels(false);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setCenterAxisLabels(true);
            xAxis.setAxisMinimum(0f);
            xAxis.setGranularity(10f);
            xAxis.setGranularityEnabled(true);
            xAxis.setDrawAxisLine(false);

            chart.setDrawBorders(false);
            chart.setFitBars(true);
            chart.setPinchZoom(false);
            chart.setDoubleTapToZoomEnabled(false);
            chart.setVisibleXRangeMinimum(dataSets.size());
            chart.setDoubleTapToZoomEnabled(false);
            chart.setHighlightPerTapEnabled(false);
            chart.setHighlightPerDragEnabled(false);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -1);
            params.height = (int) getResources().getDimension(R.dimen._200sdp);
            params.width = (int) getResources().getDimension(R.dimen._50sdp) * 6;
            chart.setLayoutParams(params);

            chart.setDrawGridBackground(false);

            YAxis leftAxis = chart.getAxisLeft();
            leftAxis.setValueFormatter(new LargeValueFormatter());
            leftAxis.setDrawGridLines(false);
            leftAxis.setAxisMinimum(0f);
            leftAxis.setAxisMaximum(110f);
            leftAxis.setDrawAxisLine(false);

            chart.getAxisRight().setEnabled(false);
            chart.getDescription().setEnabled(false);
            chart.getLegend().setEnabled(false);

            ArrayList<DataBeanStudent> studentArray = new ArrayList<>();
            JSONArray array = maxMarks.getJSONArray(examArray.get(i).getExamName());
            for (int j = 0; j < array.length(); j++) {
                JSONObject object = array.getJSONObject(j);
                if (object.getString("tbl_result_details_subject_name").equals(subName)) {
                    DataBeanStudent data = new DataBeanStudent();
                    data.setUserName(object.getString("tbl_users_name"));
                    data.setUserImg(object.getString("tbl_users_img"));
                    studentArray.add(data);
                }
            }

            RecyclerView listView = new RecyclerView(this);
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(-1, -1);
            p.height = (int) getResources().getDimension(R.dimen._55sdp);
            DisplayMetrics metrics = new DisplayMetrics();
            getWindowManager().getDefaultDisplay().getMetrics(metrics);
            p.width = metrics.widthPixels-50;
            listView.setLayoutParams(p);
            listView.setNestedScrollingEnabled(true);

            LinearLayoutManager manager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
            listView.setLayoutManager(manager);

            if (entry.size() == 6) {
                StudentAdapter adapter = new StudentAdapter(this, R.layout.student_list, studentArray, entry.get(5).getY());
                listView.setAdapter(adapter);
                adapter.notifyDataSetChanged();
            }

            LinearLayout layout1 = new LinearLayout(this);
            LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(-1, -1);
            params1.width = LinearLayout.LayoutParams.MATCH_PARENT;
            params1.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            layout1.setOrientation(LinearLayout.HORIZONTAL);

            TextView textView = new TextView(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -1);
            layoutParams.width = 0;
            layoutParams.weight = 1;
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            textView.setLayoutParams(layoutParams);
            textView.setPadding(50, 20, 50, 20);
            textView.setTextSize(18);
            textView.setTextColor(Color.BLACK);
            textView.setText(examArray.get(i).getExamName());

            layout1.addView(textView);
            textView = new TextView(this);
            textView.setLayoutParams(layoutParams);
            textView.setPadding(50, 20, 50, 20);
            textView.setTextSize(14);
            textView.setGravity(Gravity.END);
            textView.setTextColor(getResources().getColor(R.color.text_gray));
            JSONArray outOf = outOfArray.getJSONArray(examArray.get(i).getExamName());
            for (int j = 0; j < outOf.length(); j++) {
                JSONObject object = outOf.getJSONObject(j);
                if (object.getString("tbl_result_details_subject_name").equals(subName)) {
                    textView.setText("Out of " + object.getString("tbl_exam_subject_marks"));
                }
            }
            layout1.addView(textView);

            LayoutInflater inflater = LayoutInflater.from(this);
            View view = inflater.inflate(R.layout.high_scores, null);

            if (barData.getDataSetCount() != 0) {
                layout.addView(layout1);
                layout.addView(chart);
                layout.addView(view);
                layout.addView(listView);
            }

            progressbar.setVisibility(View.GONE);

            if (layout.getChildCount() == 0) {
                no_report.setVisibility(View.VISIBLE);
                label.setVisibility(View.GONE);
            } else {
                no_report.setVisibility(View.GONE);
                label.setVisibility(View.VISIBLE);
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @SuppressLint("StaticFieldLeak")
    public class MinMax extends AsyncTask<String, String, String> {
        Context ctx;
        String schoolId;
        String sub;

        MinMax(Context ctx, String sub) {
            this.ctx = ctx;
            this.sub = sub;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            schoolId = params[0];
            String userId = params[1];
            String batchNm = params[2];
            String data;

            try {
                URL url = new URL(MIN_MAX_MARKS);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                data = URLEncoder.encode("schoolId", "UTF-8") + "=" + URLEncoder.encode(schoolId, "UTF-8") + "&" +
                        URLEncoder.encode("userId", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8") + "&" +
                        URLEncoder.encode("batchNm", "UTF-8") + "=" + URLEncoder.encode(batchNm, "UTF-8");
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
            Log.e("result", result);
            if (!result.equals("\"SubjectsWiseMinMax\":null")) {
                try {
                    setSubjectwise(sub, result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            super.onPostExecute(result);
        }

    }

}
