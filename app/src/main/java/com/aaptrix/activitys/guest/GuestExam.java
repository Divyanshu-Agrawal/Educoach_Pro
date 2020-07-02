package com.aaptrix.activitys.guest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.aaptrix.R;
import com.aaptrix.adaptor.GuestExamAdapter;
import com.aaptrix.databeans.OnlineExamData;
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

import static com.aaptrix.tools.HttpUrl.ABOUT_SCHOOL_INFO;
import static com.aaptrix.tools.HttpUrl.GUEST_EXAM;
import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREF_COLOR;

public class GuestExam extends AppCompatActivity {

    AppBarLayout appBarLayout;
    TextView tool_title;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;
    private ProgressBar progressBar;
    private ArrayList<OnlineExamData> examArray = new ArrayList<>();
    private TextView noExam;
    String[] batch_array;
    Spinner batch_spinner;
    String selBatch = "All", schoolId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_exam);
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
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        listView = findViewById(R.id.listview);
        progressBar = findViewById(R.id.progress_bar);
        noExam = findViewById(R.id.no_exam);
        batch_spinner = findViewById(R.id.batch_spinner);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        schoolId = settings.getString("str_school_id", "");

        GetCourse getCourse = new GetCourse(this);
        getCourse.execute(schoolId);

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
    }

    @SuppressLint("StaticFieldLeak")
    public class GetCourse extends AsyncTask<String, String, String> {
        Context ctx;

        GetCourse(Context ctx) {
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
            String data;

            try {

                URL url = new URL(ABOUT_SCHOOL_INFO);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                data = URLEncoder.encode("schoolId", "UTF-8") + "=" + URLEncoder.encode(school_id, "UTF-8");
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
            swipeRefreshLayout.setRefreshing(false);
            if (!result.equals("\"course_list\":null")) {
                try {
                    JSONObject jo = new JSONObject(result);
                    JSONArray ja = jo.getJSONArray("result");
                    batch_array = new String[ja.length() + 1];
                    batch_array[0] = "All Courses";
                    for (int i = 0; i < ja.length(); i++) {
                        jo = ja.getJSONObject(i);
                        batch_array[i+1] = jo.getString("tbl_abt_schl_more_info_name");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setBatch();
            } else {
                String[] batch_array = {"All Courses"};
                ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<>(ctx, R.layout.spinner_list_item1, batch_array);
                dataAdapter1.setDropDownViewResource(R.layout.spinner_list_item1);
                batch_spinner.setAdapter(dataAdapter1);
                Toast.makeText(ctx, "No Course", Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(result);
        }

    }

    private void setBatch() {
        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<>(this, R.layout.spinner_list_item1, batch_array);
        dataAdapter1.setDropDownViewResource(R.layout.spinner_list_item1);
        batch_spinner.setAdapter(dataAdapter1);
        batch_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                GetExam getExam = new GetExam(GuestExam.this);
                if (batch_array[i].equals("All Courses")) {
                    getExam.execute(schoolId, "All");
                } else {
                    getExam.execute(schoolId, batch_array[i]);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    public class GetExam extends AsyncTask<String, String, String> {
        Context ctx;

        GetExam(Context ctx) {
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
            String batch = params[1];

            String data;

            try {

                URL url = new URL(GUEST_EXAM);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                data = URLEncoder.encode("school_id", "UTF-8") + "=" + URLEncoder.encode(school_id, "UTF-8") + "&" +
                        URLEncoder.encode("course_nm", "UTF-8") + "=" + URLEncoder.encode(batch, "UTF-8");
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
            swipeRefreshLayout.setRefreshing(false);
            examArray.clear();
            if (!result.equals("{\"OnlineExamList\":null}")) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("OnlineExamList");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        OnlineExamData data = new OnlineExamData();
                        data.setId(object.getString("tbl_online_exams_guest_id"));
                        data.setName(object.getString("tbl_online_exam_nm"));
                        data.setSubject(object.getString("tbl_course_nm"));
                        examArray.add(data);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (examArray.size() > 0)
                    listItems();
                else {
                    listView.setVisibility(View.GONE);
                    noExam.setVisibility(View.VISIBLE);
                }
            } else {
                listView.setVisibility(View.GONE);
                noExam.setVisibility(View.VISIBLE);
            }

            super.onPostExecute(result);
        }

    }

    private void listItems() {
        noExam.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);

        GuestExamAdapter adapter = new GuestExamAdapter(this, R.layout.list_online_exam, examArray);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public final boolean isInternetOn() {

        ConnectivityManager connec = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        assert connec != null;
        if (connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED) {

            return true;
        } else if (
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED) {
            return false;
        }
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
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