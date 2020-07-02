package com.aaptrix.activitys.teacher;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

import com.aaptrix.R;
import com.google.android.material.appbar.AppBarLayout;
import com.squareup.picasso.Picasso;

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
import java.util.Objects;

import static com.aaptrix.tools.HttpUrl.GET_SUBJECT;
import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREF_COLOR;

public class StudentAttenInter extends AppCompatActivity {

    SharedPreferences.Editor editor;
    String userId, roleId, schoolId, userSection, userRollNumber, userClassTeacher;

    SharedPreferences.Editor editorColor;
    String selToolColor, selDrawerColor, selStatusColor, selTextColor1, selTextColor2, numberOfUser;
    AppBarLayout appBarLayout;
    TextView tool_title;
    String userID, userLoginId, userName, userImg, userrType, userPassword, userSchoolLogo;
    String userSchoolId;
    LinearLayout logo_layout;
    //
    TextView view_attendance;
    LinearLayout attendance_action;
    ImageView school_logo;
    MediaPlayer mp;
    //
    Spinner spin_subject;
    ProgressBar loader_subject;

    String[] subject_array = {"Select Subject"};
    String selSubject;
    TextView cube1, cube2;
    TextView view1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_atten_inter);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        setTitle("");
        setResult(RESULT_OK);
        appBarLayout = findViewById(R.id.appBarLayout);
        tool_title = findViewById(R.id.tool_title);
        //color
        mp = MediaPlayer.create(this, R.raw.button_click);
        SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
        editorColor = settingsColor.edit();
        selToolColor = settingsColor.getString("tool", "");
        selDrawerColor = settingsColor.getString("drawer", "");
        selStatusColor = settingsColor.getString("status", "");
        selTextColor1 = settingsColor.getString("text1", "");
        selTextColor2 = settingsColor.getString("text2", "");

        logo_layout = findViewById(R.id.logo_layout);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        editor = settings.edit();
        userId = settings.getString("userID", "");
        userLoginId = settings.getString("userLoginId", "");
        userName = settings.getString("userName", "");
        userImg = settings.getString("userImg", "");
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
        view1 = findViewById(R.id.view1);
        //
        view_attendance = findViewById(R.id.view_attendance);
        attendance_action = findViewById(R.id.attendance_action);

        school_logo = findViewById(R.id.school_logo);

        spin_subject = findViewById(R.id.spin_subject);
        loader_subject = findViewById(R.id.loader_subject);

        Picasso.with(this).load(R.drawable.large_logo).into(school_logo);
        view_attendance.setClickable(false);

        if (isInternetOn()) {
            GetSubjects getSubjects = new GetSubjects(this);
            getSubjects.execute(schoolId, userSection);
        } else {
            Toast.makeText(this, "No internet", Toast.LENGTH_SHORT).show();

        }


        appBarLayout.setBackgroundColor(Color.parseColor(selToolColor));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(selStatusColor));
        }
        tool_title.setTextColor(Color.parseColor(selTextColor1));
        view_attendance.setTextColor(Color.parseColor(selTextColor1));
        attendance_action.setBackgroundColor(Color.parseColor(selToolColor));
        GradientDrawable drawable = (GradientDrawable) cube1.getBackground();
        drawable.setStroke(2, Color.parseColor(selToolColor));
        GradientDrawable drawable1 = (GradientDrawable) cube2.getBackground();
        drawable1.setStroke(2, Color.parseColor(selToolColor));
        view1.setBackgroundColor(Color.parseColor(selToolColor));
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
            if (result != null && !result.equals("{\"result\":null}")) {
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
                ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<>(StudentAttenInter.this, R.layout.spinner_list_item1, subject_array);
                dataAdapter1.setDropDownViewResource(R.layout.spinner_list_item1);
                spin_subject.setAdapter(dataAdapter1);
                Toast.makeText(ctx, "No Subject", Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(result);
        }
    }

    private void setSubject() {
        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<>(StudentAttenInter.this, R.layout.spinner_list_item1, subject_array);
        dataAdapter1.setDropDownViewResource(R.layout.spinner_list_item1);
        spin_subject.setAdapter(dataAdapter1);

        spin_subject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) adapterView.getChildAt(0)).setTextColor(getResources().getColor(R.color.text_gray));
                selSubject = subject_array[i];
                if (isInternetOn()) {
                    view_attendance.setOnClickListener(view22 -> {
                        mp.start();
                        if (!selSubject.equals("Select Subject")) {
                            Intent i12 = new Intent(StudentAttenInter.this, StudentAttendanceActivity.class);
                            i12.putExtra("subject", selSubject);
                            startActivity(i12);
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        } else {
                            Toast.makeText(StudentAttenInter.this, "Select Subject", Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {
                    Toast.makeText(StudentAttenInter.this, "No internet", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mp.release();
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

    public final boolean isInternetOn() {

        // get Connectivity Manager object to check connection
        ConnectivityManager connec =
                (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

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
