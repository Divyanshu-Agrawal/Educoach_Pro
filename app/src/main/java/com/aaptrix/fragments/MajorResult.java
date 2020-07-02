package com.aaptrix.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.aaptrix.R;
import com.aaptrix.activitys.student.SubjectwiseReport;
import com.aaptrix.activitys.student.ViewReports;
import com.aaptrix.adaptor.ResAdapter;
import com.aaptrix.databeans.ResultData;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

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

import static android.content.Context.CONNECTIVITY_SERVICE;
import static com.aaptrix.tools.HttpUrl.STUDENT_RESULT;
import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREF_COLOR;

public class MajorResult extends Fragment {

    private Context context;
    private String userId, userType, str_section;
    private String userSchoolId;
    private String stdSection;
    private String studentId;
    private ListView exam_list;
    private LinearLayout header, header1;
    private int pos = 0, sub = 0;
    private Button viewReports, subjectReport;
    private TextView no_time_table, examMainName;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private ArrayList<ResultData> examArray = new ArrayList<>(), subjectArray = new ArrayList<>();
    private FrameLayout previousF, nextF;
    private BarChart chart;
    private String[] color = {"#6495ED", "#B8860B", "#8B0000", "#228B22", "#4B0082", "#32CD32", "#000080", "#ff0000", "#FFA500", "#9ACD32",
            "#6495ED", "#B8860B", "#8B0000", "#228B22", "#4B0082", "#32CD32", "#000080", "#ff0000", "#FFA500", "#9ACD32",
            "#6495ED", "#B8860B", "#8B0000", "#228B22", "#4B0082", "#32CD32", "#000080", "#ff0000", "#FFA500", "#9ACD32",
            "#6495ED", "#B8860B", "#8B0000", "#228B22", "#4B0082", "#32CD32", "#000080", "#ff0000", "#FFA500", "#9ACD32"};

    public MajorResult() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_major_result, container, false);
        viewReports = view.findViewById(R.id.overall_reports);
        subjectReport = view.findViewById(R.id.subject_reports);

        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        userId = settings.getString("userID", "");
        userSchoolId = settings.getString("str_school_id", "");
        str_section = settings.getString("userSection", "");

        exam_list = view.findViewById(R.id.exam_list);
        no_time_table = view.findViewById(R.id.no_time_table);
        mSwipeRefreshLayout = view.findViewById(R.id.activity_main_swipe_refresh_layout);
        nextF = view.findViewById(R.id.Ib_nextF);
        previousF = view.findViewById(R.id.ib_prevF);
        header = view.findViewById(R.id.header);
        header1 = view.findViewById(R.id.header1);
        examMainName = view.findViewById(R.id.examMainName);
        chart = view.findViewById(R.id.chart);

        ImageView next = view.findViewById(R.id.Ib_next);
        ImageView previous = view.findViewById(R.id.ib_prev);

        userType = getArguments().getString("userType");

        if (userType.equals("student")) {
            GetAllResult getAllResult = new GetAllResult(context);
            getAllResult.execute(userSchoolId, userId, str_section);

            mSwipeRefreshLayout.setOnRefreshListener(() -> {
                examArray.clear();
                mSwipeRefreshLayout.setRefreshing(true);
                GetAllResult getResult = new GetAllResult(context);
                getResult.execute(userSchoolId, userId, str_section);
            });
        } else if (userType.equals("teacher")) {
            studentId = getArguments().getString("studentId");
            stdSection = getArguments().getString("userSection");
            GetAllResult getAllResult = new GetAllResult(context);
            getAllResult.execute(userSchoolId, studentId, stdSection);

            mSwipeRefreshLayout.setOnRefreshListener(() -> {
                examArray.clear();
                mSwipeRefreshLayout.setRefreshing(true);
                GetAllResult getResult = new GetAllResult(context);
                getResult.execute(userSchoolId, studentId, stdSection);
            });
        }

        if (pos == 0) {
            previousF.setVisibility(View.INVISIBLE);
        }

        SharedPreferences settingsColor = context.getSharedPreferences(PREF_COLOR, 0);
        String selToolColor = settingsColor.getString("tool", "");
        String selTextColor1 = settingsColor.getString("text1", "");
        String selDrawerColor = settingsColor.getString("drawer", "");

        viewReports.setBackgroundColor(Color.parseColor(selToolColor));
        viewReports.setTextColor(Color.parseColor(selTextColor1));
        subjectReport.setBackgroundColor(Color.parseColor(selToolColor));
        subjectReport.setTextColor(Color.parseColor(selTextColor1));
        previous.setColorFilter(Color.parseColor(selDrawerColor));
        next.setColorFilter(Color.parseColor(selDrawerColor));
        return view;
    }

    @SuppressLint("StaticFieldLeak")
    public class GetAllResult extends AsyncTask<String, String, String> {
        Context ctx;

        GetAllResult(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            String schoolId = params[0];
            String userId = params[1];
            String userSection = params[2];

            String data;

            try {

                URL url = new URL(STUDENT_RESULT);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                data = URLEncoder.encode("schoolId", "UTF-8") + "=" + URLEncoder.encode(schoolId, "UTF-8") + "&" +
                        URLEncoder.encode("userId", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8") + "&" +
                        URLEncoder.encode("batchNm", "UTF-8") + "=" + URLEncoder.encode(userSection, "UTF-8");
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
            super.onPostExecute(result);
            mSwipeRefreshLayout.setRefreshing(false);
            if (result != null && !result.contains("\"examNm\":null") && !result.contains("\"marksSubjects\":null")) {
                header.setVisibility(View.VISIBLE);
                header1.setVisibility(View.VISIBLE);
                try {
                    JSONObject jsonRootObject = new JSONObject(result);
                    JSONArray jsonExamArray = jsonRootObject.getJSONArray("examNm");
                    for (int i = 0; i < jsonExamArray.length(); i++) {
                        JSONObject jObject = jsonExamArray.getJSONObject(i);
                        ResultData resultData = new ResultData();
                        resultData.setResultId(jObject.getString("tbl_result_id"));
                        resultData.setExamName(jObject.getString("tbl_result_exam_name"));
                        examArray.add(resultData);
                    }
                    examMainName.setText(examArray.get(0).getExamName());
                    listItms(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                chart.setVisibility(View.GONE);
                exam_list.setVisibility(View.GONE);
                no_time_table.setVisibility(View.VISIBLE);
                header.setVisibility(View.GONE);
                header1.setVisibility(View.GONE);
            }
        }
    }

    private void listItms(String resultJson) {

        viewReports.setOnClickListener(view -> {
            Intent intent = new Intent(context, ViewReports.class);
            intent.putExtra("resultArray", resultJson);
            startActivity(intent);
        });

        subjectReport.setOnClickListener(view -> {
            Intent intent = new Intent(context, SubjectwiseReport.class);
            if (userType.equals("student")) {
                intent.putExtra("resultArray", resultJson);
                intent.putExtra("usertype", userType);
            } else {
                intent.putExtra("resultArray", resultJson);
                intent.putExtra("usertype", userType);
                intent.putExtra("studentId", studentId);
                intent.putExtra("section", stdSection);
            }
            startActivity(intent);
        });

        try {
            setGraph(examArray.get(0).getExamName(), resultJson);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        nextF.setOnClickListener(v -> {
            pos++;
            sub = 0;
            if (pos == examArray.size()-1)
                nextF.setVisibility(View.INVISIBLE);
            if (pos < examArray.size()) {
                previousF.setVisibility(View.VISIBLE);
                examMainName.setText(examArray.get(pos).getExamName());
                try {
                    setGraph(examArray.get(pos).getExamName(), resultJson);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                pos--;
                nextF.setVisibility(View.INVISIBLE);
            }
        });

        previousF.setOnClickListener(v -> {
            sub = 0;
            pos--;
            if(pos == 0)
                previousF.setVisibility(View.INVISIBLE);
            nextF.setVisibility(View.VISIBLE);
            if (pos >= 0) {
                examMainName.setText(examArray.get(pos).getExamName());
                try {
                    setGraph(examArray.get(pos).getExamName(), resultJson);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                pos++;
                previousF.setVisibility(View.INVISIBLE);
            }
        });
    }

    private void setGraph(String examNm, String resultJson) throws JSONException {
        subjectArray.clear();
        chart.invalidate();
        JSONObject jsonObject = new JSONObject(resultJson);
        JSONObject marks = jsonObject.getJSONObject("marksSubjects");

        chart.setDrawGridBackground(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setDrawLabels(true);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                int index = Math.round(value);
                if (index < 0 || index >= subjectArray.size())
                    return "";
                return subjectArray.get(index).getSubjectName();
            }
        });
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

        JSONArray array = marks.getJSONArray(examNm);

        for (int i = 0; i < array.length(); i++) {
            ResultData data = new ResultData();
            JSONObject object = array.getJSONObject(i);
            data.setSubjectName(object.getString("subjectNm"));
            data.setMarks(object.getString("Marks"));
            subjectArray.add(data);
        }

        ResAdapter resultListAdaptor = new ResAdapter(context, R.layout.result_list_item, subjectArray);
        exam_list.setAdapter(resultListAdaptor);
        resultListAdaptor.notifyDataSetChanged();

        for (int i = 0; i < subjectArray.size(); i++) {
            ArrayList<BarEntry> values = new ArrayList<>();
            values.add(new BarEntry(i, Float.valueOf(subjectArray.get(i).getMarks())));
            sub++;
            BarDataSet d = new BarDataSet(values, subjectArray.get(i).getSubjectName());
            d.setColor(Color.parseColor(color[i]));
            d.setValueTextSize(12);
            dataSets.add(d);
        }

        BarData barData = new BarData();
        for (int i = 0; i < dataSets.size(); i++) {
            barData.addDataSet(dataSets.get(i));
        }

        float barWidth;
        float gran;
        float barSpace; // x4 DataSet

        barWidth = 0.5f;
        barSpace = 0.09f;
        gran = ((barSpace * 2f) + barWidth) * (sub*2);
        float groupSpace = 0.03f;
        barData.setBarWidth(barWidth);

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
        chart.getLegend().setTextSize(14);
        chart.getLegend().setWordWrapEnabled(true);
    }

    public final boolean isInternetOn() {

        // get Connectivity Manager object to check connection
        ConnectivityManager connec =
                (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);

        // Check for network connections
        assert connec != null;
        if (connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED) {

            // if connected with internet
            return true;
        } else if (
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED) {
            return false;
        }
        return false;
    }
}
