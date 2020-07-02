package com.aaptrix.activitys.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.aaptrix.R;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import javax.net.ssl.SSLContext;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.conn.ssl.SSLConnectionSocketFactory;
import cz.msebera.android.httpclient.conn.ssl.SSLContexts;
import cz.msebera.android.httpclient.entity.mime.HttpMultipartMode;
import cz.msebera.android.httpclient.entity.mime.MultipartEntityBuilder;
import cz.msebera.android.httpclient.impl.client.HttpClients;
import cz.msebera.android.httpclient.util.EntityUtils;
import pl.droidsonroids.gif.GifImageView;

import static com.aaptrix.tools.HttpUrl.GET_SUBJECT;
import static com.aaptrix.tools.HttpUrl.UPDATE_CLASS_TT;
import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREF_COLOR;
import static cz.msebera.android.httpclient.conn.ssl.SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER;

public class UpdateClassTimeTable extends AppCompatActivity {

    Toolbar toolbar;
    AppBarLayout appBarLayout;
    EditText startTime, endTime;
    Spinner teacher, subject;
    String selToolColor, selStatusColor, selTextColor1, userSchoolId;
    CardView cardView;
    String[] subject_array = {"Select Subject"};
    String[] teacher_array = {"Select Teacher"};
    Button update;
    RelativeLayout layout;
    String strStartTime, strEndTime, strDay, strBatch, strId, strSubject, strTeacher, strPeriodId;
    GifImageView taskStatus;
    MediaPlayer mp;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_class_time_table);
        toolbar = findViewById(R.id.toolbar);
        appBarLayout = findViewById(R.id.appBarLayout);
        setSupportActionBar(toolbar);
        setTitle("Update Class Time Table");
        setResult(RESULT_OK);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mp = MediaPlayer.create(this, R.raw.button_click);
        cardView = findViewById(R.id.card_view);
        taskStatus = findViewById(R.id.task_status);
        update = findViewById(R.id.update_btn);
        subject = findViewById(R.id.subject_name);
        teacher = findViewById(R.id.subject_teacher);

        startTime = findViewById(R.id.start_time);
        endTime = findViewById(R.id.end_time);
        startTime.setFocusable(false);
        endTime.setFocusable(false);
        layout = findViewById(R.id.layout);

        strStartTime = getIntent().getStringExtra("start_time");
        strEndTime = getIntent().getStringExtra("end_time");
        strBatch = getIntent().getStringExtra("batch");
        strDay = getIntent().getStringExtra("day");
        strId = getIntent().getStringExtra("id");
        strSubject = getIntent().getStringExtra("subject");
        strTeacher = getIntent().getStringExtra("teacher");
        strPeriodId = getIntent().getStringExtra("period");

        SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
        selToolColor = settingsColor.getString("tool", "");
        selStatusColor = settingsColor.getString("status", "");
        selTextColor1 = settingsColor.getString("text1", "");

        SharedPreferences sp_user = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        userSchoolId = sp_user.getString("str_school_id", "");

        GetSubjects getSubjects = new GetSubjects(this);
        getSubjects.execute(userSchoolId, strBatch);

        appBarLayout.setBackgroundColor(Color.parseColor(selToolColor));
        update.setBackgroundColor(Color.parseColor(selToolColor));
        update.setTextColor(Color.parseColor(selTextColor1));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(selStatusColor));
        }

        layout.setOnClickListener(v -> {

        });

        layout.setOnTouchListener((v, event) -> false);

        Calendar startCalendar = Calendar.getInstance();
        Calendar endCalendar = Calendar.getInstance();

        SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mm:ss", Locale.getDefault());
        try {
            Date date = dateFormat.parse(strStartTime);
            Date date1 = dateFormat.parse(strEndTime);
            dateFormat = new SimpleDateFormat("hh:mm aa", Locale.getDefault());
            startTime.setText(dateFormat.format(date));
            endTime.setText(dateFormat.format(date1));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String[] st = strStartTime.split(":");
        String[] en = strEndTime.split(":");

        startCalendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(st[0]));
        startCalendar.set(Calendar.MINUTE, Integer.parseInt(st[1]));

        endCalendar.set(Calendar.HOUR_OF_DAY, Integer.parseInt(en[0]));
        endCalendar.set(Calendar.MINUTE, Integer.parseInt(en[1]));

        TimePickerDialog.OnTimeSetListener start = (view, hourOfDay, minute) -> {
            startCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            startCalendar.set(Calendar.MINUTE, minute);
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa", Locale.getDefault());
            startTime.setText(sdf.format(startCalendar.getTime()));
            sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            strStartTime = sdf.format(startCalendar.getTime());
        };

        TimePickerDialog.OnTimeSetListener end = (view, hourOfDay, minute) -> {
            endCalendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            endCalendar.set(Calendar.MINUTE, minute);
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa", Locale.getDefault());
            endTime.setText(sdf.format(endCalendar.getTime()));
            sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
            strEndTime = sdf.format(endCalendar.getTime());
        };

        startTime.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(this, start, startCalendar
                    .get(Calendar.HOUR), startCalendar.get(Calendar.MINUTE), false);
            timePickerDialog.show();
        });

        endTime.setOnClickListener(v -> {
            TimePickerDialog timePickerDialog = new TimePickerDialog(this, end, endCalendar
                    .get(Calendar.HOUR), endCalendar.get(Calendar.MINUTE), false);
            timePickerDialog.show();
        });

        subject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                strSubject = subject_array[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        try {
            JSONArray jsonArray = new JSONArray(sp_user.getString("teacherArray", ""));
            teacher_array = new String[jsonArray.length() + 1];
            teacher_array[0] = "Select Teacher";
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                teacher_array[i + 1] = jsonObject.getString("tbl_users_name");
            }
            setTeacher();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        teacher.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                strTeacher = teacher_array[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        update.setOnClickListener(v -> {
            if (TextUtils.isEmpty(strSubject)) {
                Toast.makeText(this, "Please select subject", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(strTeacher)) {
                Toast.makeText(this, "Please select Teacher", Toast.LENGTH_SHORT).show();
            } else if (TextUtils.isEmpty(strStartTime)) {
                startTime.requestFocus();
                startTime.setError("Please Select Start Time");
            } else if (TextUtils.isEmpty(strEndTime)) {
                endTime.setError("Please Select End Time");
                endTime.requestFocus();
            } else {
                layout.setVisibility(View.VISIBLE);
                layout.bringToFront();
                UploadClassTT uploadClassTT = new UploadClassTT(this);
                uploadClassTT.execute(strSubject, strTeacher, strStartTime, strEndTime, strBatch, strId, strDay, userSchoolId, strPeriodId);
            }
        });
    }

    private void setTeacher() {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, R.layout.spinner_list_item1, teacher_array);
        dataAdapter.setDropDownViewResource(R.layout.spinner_list_item1);
        teacher.setAdapter(dataAdapter);

        for (int i = 0; i < teacher_array.length; i++) {
            if (teacher_array[i].equals(strTeacher)) {
                teacher.setSelection(i);
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mp.release();
    }

    @SuppressLint("StaticFieldLeak")
    public class GetSubjects extends AsyncTask<String, String, String> {
        Context ctx;

        GetSubjects(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
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
                subject.setAdapter(dataAdapter1);
                Toast.makeText(ctx, "No Subject", Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(result);
        }
    }

    private void setSubject() {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, R.layout.spinner_list_item1, subject_array);
        dataAdapter.setDropDownViewResource(R.layout.spinner_list_item1);
        subject.setAdapter(dataAdapter);

        for (int i = 0; i < subject_array.length; i++) {
            if (subject_array[i].equals(strSubject)) {
                subject.setSelection(i);
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class UploadClassTT extends AsyncTask<String, String, String> {
        Context ctx;

        UploadClassTT(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            Toast.makeText(ctx, "Please wait, updating time table", Toast.LENGTH_SHORT).show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            String subject = params[0];
            String teacher = params[1];
            String start = params[2];
            String end = params[3];
            String batch = params[4];
            String id = params[5];
            String period_id = params[8];
            String day = params[6];
            String schoolId = params[7];

            try {

                SSLContext sslContext = SSLContexts.custom().useTLS().build();
                SSLConnectionSocketFactory f = new SSLConnectionSocketFactory(
                        sslContext,
                        new String[]{"TLSv1.1", "TLSv1.2"},
                        null,
                        BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
                HttpClient httpclient = HttpClients.custom().setSSLSocketFactory(f).build();
                HttpPost httppost = new HttpPost(UPDATE_CLASS_TT);
                MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
                entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                entityBuilder.addTextBody("tbl_class_tt_subject_name", subject);
                entityBuilder.addTextBody("tbl_class_tt_teacher_name", teacher);
                entityBuilder.addTextBody("tbl_class_start_time", start);
                entityBuilder.addTextBody("tbl_class_end_time", end);
                entityBuilder.addTextBody("tbl_stnt_prsnl_data_section", batch);
                entityBuilder.addTextBody("tbl_class_tt_id", id);
                entityBuilder.addTextBody("tbl_class_tt_current_date", day);
                entityBuilder.addTextBody("tbl_school_id", schoolId);
                entityBuilder.addTextBody("tbl_class_tt_period_id", period_id);
                HttpEntity entity = entityBuilder.build();
                httppost.setEntity(entity);
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity httpEntity = response.getEntity();
                return EntityUtils.toString(httpEntity);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.e("ADDED", "result "+result);
            if (result.contains("submitted")) {
                cardView.setVisibility(View.VISIBLE);
                new CountDownTimer(4000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        finish();
                    }
                }.start();
            } else {
                layout.setVisibility(View.GONE);
                Toast.makeText(ctx, "Server issues", Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(result);
        }
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
