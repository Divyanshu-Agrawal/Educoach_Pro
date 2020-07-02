package com.aaptrix.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aaptrix.R;
import com.aaptrix.adaptor.CourseAdapter;
import com.google.android.material.appbar.AppBarLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;

import static com.aaptrix.tools.HttpUrl.ABOUT_SCHOOL_INFO;
import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREF_COLOR;

public class CoursesActivity extends AppCompatActivity {

    ArrayList<String> courseArray = new ArrayList<>();
    AppBarLayout appBarLayout;
    Toolbar toolbar;
    String selToolColor, selStatusColor, selTextColor1, instPhone;
    SwipeRefreshLayout swipeRefreshLayout;
    ListView listView;
    TextView noCourse;
    Button getMoreInfo;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_courses);
        toolbar = findViewById(R.id.toolbar);
        appBarLayout = findViewById(R.id.appBarLayout);
        setSupportActionBar(toolbar);
        setTitle("Courses Offered");
        setResult(RESULT_OK);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        listView = findViewById(R.id.listview);
        progressBar = findViewById(R.id.progress_bar);
        noCourse = findViewById(R.id.no_course);
        getMoreInfo = findViewById(R.id.get_more_info);

        getMoreInfo.setOnClickListener(v1 -> {});

        SharedPreferences sp = getSharedPreferences(PREFS_NAME, 0);
        String schoolId = sp.getString("str_school_id", "");

        SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
        selToolColor = settingsColor.getString("tool", "");
        selStatusColor = settingsColor.getString("status", "");
        selTextColor1 = settingsColor.getString("text1", "");

        appBarLayout.setBackgroundColor(Color.parseColor(selToolColor));
        swipeRefreshLayout.setColorSchemeResources(R.color.text_gray);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(selStatusColor));
        }

        try {
            File directory = this.getFilesDir();
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File(directory, "courses")));
            String json = in.readObject().toString();
            in.close();
            JSONObject jsonRootObject = new JSONObject(json);
            JSONArray ja = jsonRootObject.getJSONArray("result");
            instPhone = ja.getJSONObject(0).getString("tbl_abt_schl_details_contact");
            for (int i = 0; i < ja.length(); i++) {
                JSONObject jo = ja.getJSONObject(i);
                courseArray.add(jo.getString("tbl_abt_schl_more_info_name"));
            }
            listItems();
        } catch (Exception e) {
            e.printStackTrace();
            GetCourseGuest getCourseGuest = new GetCourseGuest(this);
            getCourseGuest.execute(schoolId);
        }

        swipeRefreshLayout.setOnRefreshListener(() -> {
            swipeRefreshLayout.setRefreshing(true);
            courseArray.clear();
            GetCourseGuest getCourseGuest = new GetCourseGuest(this);
            getCourseGuest.execute(schoolId);
        });
    }

    @SuppressLint("StaticFieldLeak")
    public class GetCourseGuest extends AsyncTask<String, String, String> {
        Context ctx;

        GetCourseGuest(Context ctx) {
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
                    cacheJson(jo);
                    JSONArray ja = jo.getJSONArray("result");
                    instPhone = ja.getJSONObject(0).getString("tbl_abt_schl_details_contact");
                    for (int i = 0; i < ja.length(); i++) {
                        jo = ja.getJSONObject(i);
                        courseArray.add(jo.getString("tbl_abt_schl_more_info_name"));
                    }
                    listItems();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                noCourse.setVisibility(View.VISIBLE);
            }
            super.onPostExecute(result);
        }

    }

    private void listItems() {
        CourseAdapter adapter = new CourseAdapter(this, R.layout.course_list, courseArray);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        getMoreInfo.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:" + instPhone.trim()));
            startActivity(intent);
        });

        listView.setOnItemClickListener((parent, view, position, id) -> startActivity(new Intent(this, AddUserForm.class)));
    }

    private void cacheJson(final JSONObject jsonObject) {
        new Thread(() -> {
            ObjectOutput out;
            String data = jsonObject.toString();
            try {
                File directory = this.getFilesDir();
                directory.mkdir();
                out = new ObjectOutputStream(new FileOutputStream(new File(directory, "courses")));
                out.writeObject(data);
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
