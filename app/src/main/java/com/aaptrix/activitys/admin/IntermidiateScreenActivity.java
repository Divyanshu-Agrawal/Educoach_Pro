package com.aaptrix.activitys.admin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import com.aaptrix.activitys.student.ActivitiesActivity;
import com.google.android.material.appbar.AppBarLayout;
import com.aaptrix.activitys.student.ResultActivity;
import com.aaptrix.activitys.teacher.StudentPerformance;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import com.aaptrix.R;

import static com.aaptrix.tools.HttpUrl.ALL_BATCHS;
import static com.aaptrix.tools.HttpUrl.ALL_STUDENTS_LIST;
import static com.aaptrix.tools.HttpUrl.GET_SUBJECT;
import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREF_COLOR;

public class IntermidiateScreenActivity extends AppCompatActivity {

    String userId, roleId, schoolId, userClass, userSection, userRollNumber, userClassTeacher;
    String selToolColor, selDrawerColor, selStatusColor, selTextColor1, selTextColor2, numberOfUser;
    AppBarLayout appBarLayout;
    TextView tool_title;
    String userID, userLoginId, userName, userImg, userrType, userPassword, userSchoolLogo;
    String userSchoolId;
    //
    TextView view1;
    LinearLayout attendance_diary;
    ImageView school_logo;
    //
    View v;
    Spinner spin_section;
    Spinner spin_student;
    ProgressBar loader_section, loader_subject, loader_student;
    String[] subject_array = {"Select Subject"};
    String selSubject;
    //
    String batch_array[] = {"Select Batch"}, batch_id[] = {"0"};
    String student_array[] = {"Select Student"}, student_id[] = {"0"};
    Spinner spin_subject;
    String selBatch;
    String selStudent;
    String str_tool_title;
    TextView cube1, cube2;
    TextView view2;
    MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ntermidiate_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        setTitle("");
        setResult(RESULT_OK);
        appBarLayout = findViewById(R.id.appBarLayout);
        tool_title = findViewById(R.id.tool_title);
        SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
        selToolColor = settingsColor.getString("tool", "");
        selDrawerColor = settingsColor.getString("drawer", "");
        selStatusColor = settingsColor.getString("status", "");
        selTextColor1 = settingsColor.getString("text1", "");
        selTextColor2 = settingsColor.getString("text2", "");
        mp = MediaPlayer.create(this, R.raw.button_click);

        str_tool_title = getIntent().getStringExtra("str_tool_title");
        tool_title.setText(str_tool_title);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        userId = settings.getString("userID", "");
        userLoginId = settings.getString("userLoginId", "");
        userName = settings.getString("userName", "");
        userImg = settings.getString("userImg", "");
        userClass = settings.getString("userClass", "");
        userSection = settings.getString("userSection", "");
        userRollNumber = settings.getString("userRollNumber", "");
        userClassTeacher = settings.getString("userTeacherName", "");
        userrType = settings.getString("userrType", "");
        userPassword = settings.getString("userPassword", "");
        roleId = settings.getString("str_role_id", "");
        schoolId = settings.getString("str_school_id", "");
        numberOfUser = settings.getString("numberOfUser", "");
        userSchoolLogo = settings.getString("userSchoolLogo", "");
        cube1 = findViewById(R.id.cube1);
        cube2 = findViewById(R.id.cube2);
        view2 = findViewById(R.id.view1);
        view1 = findViewById(R.id.view);
        attendance_diary = findViewById(R.id.attendance_action);
        school_logo = findViewById(R.id.school_logo);
        spin_section = findViewById(R.id.spin_section);
        spin_student = findViewById(R.id.spin_student);
        loader_student = findViewById(R.id.loader_student);
        loader_section = findViewById(R.id.loader_section);
        spin_subject = findViewById(R.id.spin_subject);
        loader_subject = findViewById(R.id.loader_subject);

        Picasso.with(this).load(R.drawable.large_logo).into(school_logo);
        setBatch();
        setBatch1();

        try {
            File directory = this.getFilesDir();
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File(directory, "batches")));
            String json = in.readObject().toString();
            in.close();
            if (!json.equals("{\"result\":null}")) {
                try {
                    JSONObject jo = new JSONObject(json);
                    JSONArray ja = jo.getJSONArray("result");
                    batch_array = new String[ja.length() + 1];
                    batch_id = new String[ja.length() + 1];
                    batch_array[0] = "Select Batch";
                    batch_id[0] = "0";
                    for (int i = 0; i < ja.length(); i++) {
                        jo = ja.getJSONObject(i);
                        batch_id[i + 1] = jo.getString("tbl_batch_detail_id");
                        batch_array[i + 1] = jo.getString("tbl_batch_name");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setBatch();
            } else {
                String batch_array[] = {"Select Batch"};
                ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<>(IntermidiateScreenActivity.this, R.layout.spinner_list_item1, batch_array);
                dataAdapter1.setDropDownViewResource(R.layout.spinner_list_item1);
                spin_section.setAdapter(dataAdapter1);
                Toast.makeText(this, "No Batch", Toast.LENGTH_SHORT).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
            GetAllBatches b1 = new GetAllBatches(this);
            b1.execute(schoolId);
        }

        appBarLayout.setBackgroundColor(Color.parseColor(selToolColor));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(selStatusColor));
        }
        tool_title.setTextColor(Color.parseColor(selTextColor1));
        view1.setTextColor(Color.parseColor(selTextColor1));
        attendance_diary.setBackgroundColor(Color.parseColor(selToolColor));
        GradientDrawable drawable = (GradientDrawable) cube1.getBackground();
        drawable.setStroke(2, Color.parseColor(selToolColor));
        GradientDrawable drawable1 = (GradientDrawable) cube2.getBackground();
        drawable1.setStroke(2, Color.parseColor(selToolColor));
        view2.setBackgroundColor(Color.parseColor(selToolColor));
    }

    @SuppressLint("StaticFieldLeak")
    public class GetAllBatches extends AsyncTask<String, String, String> {
        Context ctx;

        GetAllBatches(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            loader_section.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String school_id = params[0];
            String data;

            try {
                URL url = new URL(ALL_BATCHS);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                data = URLEncoder.encode("school_id", "UTF-8") + "=" + URLEncoder.encode(school_id, "UTF-8") + "&" +
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
            loader_section.setVisibility(View.GONE);
            Log.d("result", result);
            if (!result.equals("{\"result\":null}")) {
                try {
                    JSONObject jo = new JSONObject(result);
                    JSONArray ja = jo.getJSONArray("result");
                    batch_array = new String[ja.length() + 1];
                    batch_id = new String[ja.length() + 1];
                    batch_array[0] = "Select Batch";
                    batch_id[0] = "0";
                    for (int i = 0; i < ja.length(); i++) {
                        jo = ja.getJSONObject(i);
                        batch_id[i + 1] = jo.getString("tbl_batch_detail_id");
                        batch_array[i + 1] = jo.getString("tbl_batch_name");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setBatch();
            } else {
                String batch_array[] = {"Select Batch"};
                ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<>(IntermidiateScreenActivity.this, R.layout.spinner_list_item1, batch_array);
                dataAdapter1.setDropDownViewResource(R.layout.spinner_list_item1);
                spin_section.setAdapter(dataAdapter1);
                Toast.makeText(ctx, "No Batch", Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(result);
        }

    }

    private void setBatch() {
        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<>(IntermidiateScreenActivity.this, R.layout.spinner_list_item1, batch_array);
        dataAdapter1.setDropDownViewResource(R.layout.spinner_list_item1);
        spin_section.setAdapter(dataAdapter1);

        spin_section.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selBatch = batch_array[i];
                switch (str_tool_title) {
                    case "Time Table":
                        view1.setText("View");
                        spin_student.setVisibility(View.GONE);
                        view1.setOnClickListener(view3 -> {
                            mp.start();
                            if (!selBatch.equalsIgnoreCase("Select Batch")) {
                                Intent i12 = new Intent(IntermidiateScreenActivity.this, AdminTimeTableActivity.class);
                                i12.putExtra("selBatch", selBatch);
                                i12.putExtra("data", String.valueOf(spin_section.getSelectedItem()));
                                startActivity(i12);
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            } else {
                                Toast.makeText(IntermidiateScreenActivity.this, "Please Select Batch", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                    case "Student Performance":
                        view1.setOnClickListener(view32 -> {
                            mp.start();
                            if (!selBatch.equalsIgnoreCase("Select Batch")) {
                                Intent i1 = new Intent(IntermidiateScreenActivity.this, StudentPerformance.class);
                                i1.putExtra("type", "performance");
                                i1.putExtra("selBatch", selBatch);
                                startActivity(i1);
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            } else {
                                Toast.makeText(IntermidiateScreenActivity.this, "Please Select Batch", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                    case "Online Exam Result":
                        String examId = getIntent().getStringExtra("examId");
                        String examName = getIntent().getStringExtra("examName");
                        view1.setOnClickListener(view32 -> {
                            mp.start();
                            if (!selBatch.equalsIgnoreCase("Select Batch")) {
                                Intent i1 = new Intent(IntermidiateScreenActivity.this, StudentPerformance.class);
                                i1.putExtra("type", "online");
                                i1.putExtra("examId", examId);
                                i1.putExtra("examName", examName);
                                i1.putExtra("selBatch", selBatch);
                                startActivity(i1);
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            } else {
                                Toast.makeText(IntermidiateScreenActivity.this, "Please Select Batch", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                    case "Student Attendance":
                        if (!selBatch.equals("Select Batch")) {
                            GetSubjects subjects = new GetSubjects(IntermidiateScreenActivity.this);
                            subjects.execute(schoolId, selBatch);
                            spin_subject.setVisibility(View.VISIBLE);
                        }
                        break;
                    case "Activities":
                        view1.setOnClickListener(view32 -> {
                            mp.start();
                            if (!selBatch.equalsIgnoreCase("Select Batch")) {
                                Intent i1 = new Intent(IntermidiateScreenActivity.this, ActivitiesActivity.class);
                                i1.putExtra("selBatch", selBatch);
                                startActivity(i1);
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            } else {
                                Toast.makeText(IntermidiateScreenActivity.this, "Please Select Batch", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                    case "Add Exam": {
                        view1.setText("Add");
                        view1.setOnClickListener(v1 -> {
                            mp.start();
                            if (!selBatch.equalsIgnoreCase("Select Batch")) {
                                Intent intent = new Intent(IntermidiateScreenActivity.this, AddExamTimeTable.class);
                                intent.putExtra("section", selBatch);
                                startActivity(intent);
                            } else {
                                Toast.makeText(getApplicationContext(), "Select Batch", Toast.LENGTH_SHORT).show();
                            }
                        });
                        break;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    public class GetSubjects extends AsyncTask<String, String, String> {
        Context ctx;

        GetSubjects(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            loader_subject.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            String school_id = params[0];
            String batchNm = params[1];
            String data;

            try {

                URL url = new URL(GET_SUBJECT);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                data = URLEncoder.encode("school_id", "UTF-8") + "=" + URLEncoder.encode(school_id, "UTF-8") + "&" +
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
            loader_subject.setVisibility(View.GONE);
            Log.e("res", result);
            if (!result.equals("{\"result\":null}")) {
                try {
                    JSONObject jo = new JSONObject(result);
                    JSONArray ja = jo.getJSONArray("SubjectList");
                    subject_array = new String[ja.length() + 1];
                    subject_array[0] = "Select Subject";
                    for (int i = 0; i < ja.length(); i++) {
                        jo = ja.getJSONObject(i);
                        subject_array[i + 1] = jo.getString("tbl_batch_subjct_name");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setSubject();
            } else {
                String[] subject_array = {"Select Subject"};
                ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<>(ctx, R.layout.spinner_list_item1, subject_array);
                dataAdapter1.setDropDownViewResource(R.layout.spinner_list_item1);
                spin_subject.setAdapter(dataAdapter1);
                Toast.makeText(ctx, "No Subject", Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(result);
        }
    }

    private void setSubject() {
        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<>(this, R.layout.spinner_list_item1, subject_array);
        dataAdapter1.setDropDownViewResource(R.layout.spinner_list_item1);
        spin_subject.setAdapter(dataAdapter1);

        spin_subject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) adapterView.getChildAt(0)).setTextColor(getResources().getColor(R.color.text_gray));
                selSubject = subject_array[i];
                view1.setOnClickListener(view32 -> {
                    mp.start();
                    if (!selBatch.equalsIgnoreCase("Select Batch")) {
                        if (!selSubject.equals("Select Subject")) {
                            Intent i1 = new Intent(IntermidiateScreenActivity.this, StudentPerformance.class);
                            i1.putExtra("type", "attendance");
                            i1.putExtra("selBatch", selBatch);
                            i1.putExtra("subject", selSubject);
                            startActivity(i1);
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        } else {
                            Toast.makeText(IntermidiateScreenActivity.this, "Please Select Subject", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(IntermidiateScreenActivity.this, "Please Select Batch", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    public class GetAllBatches1 extends AsyncTask<String, String, String> {
        Context ctx;

        GetAllBatches1(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            loader_student.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            String school_id = params[0];
            String section_name = params[1];
            String data;

            try {

                URL url = new URL(ALL_STUDENTS_LIST);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                data = URLEncoder.encode("school_id", "UTF-8") + "=" + URLEncoder.encode(school_id, "UTF-8") + "&" +
                        URLEncoder.encode("section_name", "UTF-8") + "=" + URLEncoder.encode(section_name, "UTF-8");
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

            loader_student.setVisibility(View.GONE);
            Log.d("result", result);
            if (!result.equals("{\"result\":null}")) {
                try {
                    JSONObject jo = new JSONObject(result);
                    JSONArray ja = jo.getJSONArray("result");
                    student_array = new String[ja.length() + 1];
                    student_id = new String[ja.length() + 1];
                    student_array[0] = "Select Student";
                    student_id[0] = "0";
                    for (int i = 0; i < ja.length(); i++) {
                        jo = ja.getJSONObject(i);
                        student_id[i + 1] = jo.getString("tbl_users_id");
                        student_array[i + 1] = jo.getString("tbl_users_name");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setBatch1();
            } else {
                String student_array[] = {"Select Student"};
                ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<>(IntermidiateScreenActivity.this, R.layout.spinner_list_item1, student_array);
                dataAdapter1.setDropDownViewResource(R.layout.spinner_list_item1);
                spin_student.setAdapter(dataAdapter1);
                Toast.makeText(ctx, "No Student", Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(result);
        }

    }

    private void setBatch1() {
        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<>(IntermidiateScreenActivity.this, R.layout.spinner_list_item1, student_array);
        dataAdapter1.setDropDownViewResource(R.layout.spinner_list_item1);
        spin_student.setAdapter(dataAdapter1);

        spin_student.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) adapterView.getChildAt(0)).setTextColor(getResources().getColor(R.color.text_gray));
                selStudent = student_id[i];
                if (!student_id[i].equals("0")) {
                    view1.setOnClickListener(view3 -> {
                        mp.start();
                        Intent i1 = new Intent(IntermidiateScreenActivity.this, ResultActivity.class);
                        i1.putExtra("selBatch", selStudent);
                        startActivity(i1);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    });
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
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
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mp.release();
    }

    public final boolean isInternetOn() {
        ConnectivityManager connec = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
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
}
