package com.aaptrix.activitys.student;

import com.aaptrix.databeans.ResultData;
import com.aaptrix.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

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

import java.util.ArrayList;
import java.util.Objects;

import static com.aaptrix.tools.SPClass.PREF_COLOR;

public class ViewReports extends AppCompatActivity {

    ArrayList<ResultData> examArray = new ArrayList<>();
    String resultJson;
    AppBarLayout appBarLayout;
    String selToolColor, selStatusColor, selTextColor1;
    TextView tool_title;
    LinearLayout layout;
    ProgressBar progressbar;
    String[] color = {"#6495ED", "#B8860B", "#8B0000", "#228B22", "#4B0082", "#32CD32", "#000080", "#ff0000", "#FFA500", "#9ACD32",
            "#6495ED", "#B8860B", "#8B0000", "#228B22", "#4B0082", "#32CD32", "#000080", "#ff0000", "#FFA500", "#9ACD32",
            "#6495ED", "#B8860B", "#8B0000", "#228B22", "#4B0082", "#32CD32", "#000080", "#ff0000", "#FFA500", "#9ACD32",
            "#6495ED", "#B8860B", "#8B0000", "#228B22", "#4B0082", "#32CD32", "#000080", "#ff0000", "#FFA500", "#9ACD32"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_reports);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        appBarLayout = findViewById(R.id.appBarLayout);
        tool_title = findViewById(R.id.tool_title);
        resultJson = getIntent().getStringExtra("resultArray");
        progressbar = findViewById(R.id.loader);
        layout = findViewById(R.id.linear_layout);
        SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
        selToolColor = settingsColor.getString("tool", "");
        selStatusColor = settingsColor.getString("status", "");
        selTextColor1 = settingsColor.getString("text1", "");

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
            setGraph();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void setGraph() throws JSONException {

        for (int i = 0; i < examArray.size(); i++) {

            BarChart chart = new BarChart(this);
            ArrayList<ResultData> subjectArray = new ArrayList<>();
            chart.invalidate();

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -1);
            params.height = (int) getResources().getDimension(R.dimen._200sdp);

            JSONObject jsonObject = new JSONObject(resultJson);
            JSONObject marks = jsonObject.getJSONObject("marksSubjects");

            progressbar.setVisibility(View.GONE);
            chart.setDrawGridBackground(false);

            XAxis xAxis = chart.getXAxis();
            xAxis.setDrawLabels(false);
            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setCenterAxisLabels(true);
            xAxis.setAxisMinimum(1f);
            xAxis.setDrawAxisLine(false);

            YAxis leftAxis = chart.getAxisLeft();
            leftAxis.setValueFormatter(new LargeValueFormatter());
            leftAxis.setDrawGridLines(false);
            leftAxis.setAxisMinimum(0f);
            leftAxis.setAxisMaximum(110f);
            leftAxis.setDrawAxisLine(false);

            chart.getAxisRight().setEnabled(false);
            chart.getDescription().setEnabled(false);
            chart.setDragEnabled(true);
            chart.setHighlightPerTapEnabled(false);
            chart.setHighlightPerDragEnabled(false);

            ArrayList<BarDataSet> dataSets = new ArrayList<>();
            JSONArray jsonArray = marks.getJSONArray(examArray.get(i).getExamName());

            for (int j = 0; j < jsonArray.length(); j++) {
                ResultData data = new ResultData();
                JSONObject object = jsonArray.getJSONObject(j);
                data.setSubjectName(object.getString("subjectNm"));
                data.setMarks(object.getString("Marks"));
                subjectArray.add(data);
            }

            params.width = (int) getResources().getDimension(R.dimen._90sdp) * subjectArray.size();
            chart.setLayoutParams(params);

            for (int j = 0; j < subjectArray.size(); j++) {
                ArrayList<BarEntry> values = new ArrayList<>();
                values.add(new BarEntry(j, Float.valueOf(subjectArray.get(j).getMarks())));
                BarDataSet d = new BarDataSet(values, subjectArray.get(j).getSubjectName());
                d.setColor(Color.parseColor(color[j]));
                dataSets.add(d);
            }

            BarData barData = new BarData();
            for (int j = 0; j < dataSets.size(); j++) {
                barData.addDataSet(dataSets.get(j));
            }

            float barWidth;
            float gran;
            float barSpace; // x4 DataSet

            barWidth = 0.5f;
            barSpace = 0.010f * subjectArray.size();
            gran = (barSpace + barWidth) * (subjectArray.size()*2);
            float groupSpace = 0.06f;
            barData.setBarWidth(barWidth);

            barData.setDrawValues(true);
            barData.setValueTextSize(12);
            chart.setData(barData);
            xAxis.setAxisMinimum(0f);
            xAxis.setTextSize(12);
            xAxis.setDrawLabels(false);
            xAxis.setGranularityEnabled(true);
            xAxis.setGranularity(gran);
            if (dataSets.size() > 1)
                chart.groupBars(0, groupSpace, barSpace);
            chart.setDrawBorders(false);
            chart.setFitBars(true);
            chart.setPinchZoom(false);
            chart.setDoubleTapToZoomEnabled(false);
            chart.setVisibleXRangeMinimum(dataSets.size());
            chart.getLegend().setTextSize(16);

            TextView textView = new TextView(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -1);
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            layoutParams.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            textView.setPadding(100, 10, 50, 10);
            textView.setTextSize(18);
            textView.setTextColor(Color.BLACK);
            textView.setText(examArray.get(i).getExamName());

            layout.addView(chart);
            layout.addView(textView);
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

}
