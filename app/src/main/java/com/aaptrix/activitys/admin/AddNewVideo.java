package com.aaptrix.activitys.admin;

import com.aaptrix.activitys.student.VideoLibrary;
import com.aaptrix.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.aaptrix.adaptor.BatchListAdaptor;
import com.aaptrix.databeans.DataBeanStudent;
import com.google.android.material.appbar.AppBarLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import javax.net.ssl.SSLContext;

import static com.aaptrix.tools.HttpUrl.ADD_VIDEOS;
import static com.aaptrix.tools.HttpUrl.BATCH_BY_COURSE;
import static com.aaptrix.tools.HttpUrl.GET_COURSES;
import static com.aaptrix.tools.HttpUrl.GET_SUBS;
import static com.aaptrix.tools.SPClass.PREFS_DAIRY;
import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREF_COLOR;
import static cz.msebera.android.httpclient.conn.ssl.SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER;

public class AddNewVideo extends AppCompatActivity {

    AppBarLayout appBarLayout;
    String selToolColor, selStatusColor, selTextColor1;
    TextView tool_title;
    Button save;
    EditText title, url, desc, batch_spinner;
    String userId, userSchoolId, userRoleId, userSection, userrType, userName, restricted;
    MediaPlayer mp;
    CardView cardView;
    RelativeLayout layout;
    GifImageView taskStatus;
    Spinner subject_spinner, course_spinner;
    String[] subjects = {"Select Subject"};
    ArrayList<String> subject_array = new ArrayList<>();
    String[] course_array = {"Select Course"};
    String[] course_id = {"0"};
    BatchListAdaptor batchListAdaptor;
    String selsubject = "Select Subject", strDate = "0000-00-00", strTime = "00:00:00", strStart = "0000-00-00", strstartTime = "00:00:00", strCourse;
    AlertDialog.Builder alert;
    EditText visibleDate, visibleTime, visibleFrom, visibleFromTime;
    AlertDialog alertDialog;
    private ArrayList<DataBeanStudent> studentArray = new ArrayList<>();

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_video);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        appBarLayout = findViewById(R.id.appBarLayout);
        tool_title = findViewById(R.id.tool_title);
        save = findViewById(R.id.save_btn);
        layout = findViewById(R.id.layout);
        title = findViewById(R.id.add_video_title);
        url = findViewById(R.id.add_video_url);
        desc = findViewById(R.id.add_video_desc);
        mp = MediaPlayer.create(this, R.raw.button_click);
        cardView = findViewById(R.id.card_view);
        taskStatus = findViewById(R.id.task_status);
        batch_spinner = findViewById(R.id.batch_spinner);
        subject_spinner = findViewById(R.id.subject_spinner);
        course_spinner = findViewById(R.id.course_spinner);
        visibleDate = findViewById(R.id.visible_till);
        visibleTime = findViewById(R.id.visible_till_time);
        visibleFrom = findViewById(R.id.visible_from);
        visibleFromTime = findViewById(R.id.visible_from_time);

        title.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                hideKeyboard(v);
            }
        });

        url.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                hideKeyboard(v);
            }
        });

        desc.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                hideKeyboard(v);
            }
        });

        layout.setOnClickListener(v -> {

        });

        layout.setOnTouchListener((v, event) -> false);

        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<>(this, R.layout.spinner_list_item1, subjects);
        dataAdapter1.setDropDownViewResource(R.layout.spinner_list_item1);
        subject_spinner.setAdapter(dataAdapter1);

        SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
        selToolColor = settingsColor.getString("tool", "");
        selStatusColor = settingsColor.getString("status", "");
        selTextColor1 = settingsColor.getString("text1", "");

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        userId = settings.getString("userID", "");
        userSchoolId = settings.getString("str_school_id", "");
        userRoleId = settings.getString("str_role_id", "");
        userSection = settings.getString("userSection", "");
        userrType = settings.getString("userrType", "");
        userName = settings.getString("userName", "");
        restricted = settings.getString("restricted", "");

        GetCourse getCourse = new GetCourse(this);
        getCourse.execute(userSchoolId);

        appBarLayout.setBackgroundColor(Color.parseColor(selToolColor));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(selStatusColor));
        }
        tool_title.setTextColor(Color.parseColor(selTextColor1));
        save.setBackgroundColor(Color.parseColor(selToolColor));
        save.setTextColor(Color.parseColor(selTextColor1));
        visibleTime.setEnabled(false);
        visibleFromTime.setEnabled(false);

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        visibleFrom.setText(format.format(calendar.getTime()));
        format = new SimpleDateFormat("HH:mm", Locale.getDefault());
        visibleFromTime.setText(format.format(calendar.getTime()));

        visibleDate.setOnClickListener(v -> {
            final Calendar mcurrentDate = Calendar.getInstance();
            int mYear = mcurrentDate.get(Calendar.YEAR);
            int mMonth = mcurrentDate.get(Calendar.MONTH);
            int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog mDatePicker = new DatePickerDialog(
                    this, R.style.AlertDialogCustom1, (datepicker, selectedyear, selectedmonth, selectedday) -> {
                mcurrentDate.set(Calendar.YEAR, selectedyear);
                mcurrentDate.set(Calendar.MONTH, selectedmonth);
                mcurrentDate.set(Calendar.DAY_OF_MONTH, selectedday);
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
                visibleDate.setText(sdf.format(mcurrentDate.getTime()));
                sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                strDate = sdf.format(mcurrentDate.getTime());
                visibleTime.setEnabled(true);
            }, mYear, mMonth, mDay);
            mDatePicker.getDatePicker().setMinDate(calendar.getTimeInMillis());
            mDatePicker.show();
        });

        visibleFrom.setOnClickListener(v -> {
            final Calendar mcurrentDate = Calendar.getInstance();
            int mYear = mcurrentDate.get(Calendar.YEAR);
            int mMonth = mcurrentDate.get(Calendar.MONTH);
            int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog mDatePicker = new DatePickerDialog(
                    this, R.style.AlertDialogCustom1, (datepicker, selectedyear, selectedmonth, selectedday) -> {
                mcurrentDate.set(Calendar.YEAR, selectedyear);
                mcurrentDate.set(Calendar.MONTH, selectedmonth);
                mcurrentDate.set(Calendar.DAY_OF_MONTH, selectedday);
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.US);
                visibleFrom.setText(sdf.format(mcurrentDate.getTime()));
                sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                strStart = sdf.format(mcurrentDate.getTime());
                visibleFromTime.setEnabled(true);
                calendar.setTime(mcurrentDate.getTime());
            }, mYear, mMonth, mDay);
            mDatePicker.updateDate(mYear, mMonth, mDay);
            mDatePicker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            mDatePicker.show();
        });

        visibleTime.setOnClickListener(v -> {
            Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);
            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(this, (timePicker, selectedHour, selectedMinute) -> {
                strTime = selectedHour + ":" + selectedMinute;
                visibleTime.setText(selectedHour + ":" + selectedMinute);
            }, hour, minute, true);
            mTimePicker.setTitle("Select Time");
            mTimePicker.show();
        });

        visibleFromTime.setOnClickListener(v -> {
            Calendar mcurrentTime = Calendar.getInstance();
            int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
            int minute = mcurrentTime.get(Calendar.MINUTE);
            TimePickerDialog mTimePicker;
            mTimePicker = new TimePickerDialog(this, (timePicker, selectedHour, selectedMinute) -> {
                strstartTime = selectedHour + ":" + selectedMinute;
                visibleFromTime.setText(selectedHour + ":" + selectedMinute);
            }, hour, minute, true);
            mTimePicker.setTitle("Select Time");
            mTimePicker.updateTime(hour, minute);
            mTimePicker.show();
        });

        save.setOnClickListener(v -> {
            mp.start();
            if (TextUtils.isEmpty(title.getText())) {
                title.requestFocus();
                title.setError("Please enter video title");
            } else if (TextUtils.isEmpty(url.getText())) {
                url.requestFocus();
                url.setError("Please enter video URL");
            } else {
                SharedPreferences sp = getSharedPreferences(PREFS_DAIRY, 0);
                String studentArray = sp.getString("studentArray", "");
                if (studentArray.isEmpty()) {
                    layout.setVisibility(View.VISIBLE);
                    layout.bringToFront();
                    UploadVideo uploadVideo = new UploadVideo(this);
                    uploadVideo.execute(title.getText().toString(), url.getText().toString(),
                            userSchoolId, userId, studentArray, userrType, desc.getText().toString(), "");
                } else {
                    if (selsubject.equals("Select Subject")) {
                        Toast.makeText(this, "Please select subject", Toast.LENGTH_SHORT).show();
                    } else {
                        layout.setVisibility(View.VISIBLE);
                        layout.bringToFront();
                        UploadVideo uploadVideo = new UploadVideo(this);
                        uploadVideo.execute(title.getText().toString(), url.getText().toString(),
                                userSchoolId, userId, studentArray, userrType, desc.getText().toString(), selsubject);
                    }
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mp.release();
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
        }

        @Override
        protected String doInBackground(String... params) {

            String school_id = params[0];
            String data;

            try {

                URL url = new URL(GET_COURSES);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                data = URLEncoder.encode("school_id", "UTF-8") + "=" + URLEncoder.encode(school_id, "UTF-8");
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
            Log.d("result", result);
            if (result.equals("{\"result\":null}")) {
                String[] course_array = {"Select Course"};
                ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<>(ctx, R.layout.spinner_list_item1, course_array);
                dataAdapter1.setDropDownViewResource(R.layout.spinner_list_item1);
                course_spinner.setAdapter(dataAdapter1);
            } else {
                try {
                    JSONObject jsonRootObject = new JSONObject(result);
                    JSONArray jsonArray = jsonRootObject.getJSONArray("result");
                    course_array = new String[jsonArray.length() + 1];
                    course_id = new String[jsonArray.length() + 1];
                    course_array[0] = "Select Course";
                    course_id[0] = "0";
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        course_array[i + 1] = jsonObject.getString("tbl_course_name");
                        course_id[i + 1] = jsonObject.getString("tbl_course_group_id");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setCourses();
                super.onPostExecute(result);
            }
            super.onPostExecute(result);
        }

    }

    private void setCourses() {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, R.layout.spinner_list_item1, course_array);
        dataAdapter.setDropDownViewResource(R.layout.spinner_list_item1);
        course_spinner.setAdapter(dataAdapter);

        course_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) adapterView.getChildAt(0)).setTextColor(getResources().getColor(R.color.text_gray));
                if (!course_id[i].equals("0")) {
                    strCourse = course_id[i];
                    GetAllBatches getAllBatches = new GetAllBatches(AddNewVideo.this);
                    getAllBatches.execute(course_id[i]);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    public class GetAllBatches extends AsyncTask<String, String, String> {
        Context ctx;

        GetAllBatches(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            String course_id = params[0];
            String data;

            try {

                URL url = new URL(BATCH_BY_COURSE);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                data = URLEncoder.encode("school_id", "UTF-8") + "=" + URLEncoder.encode(userSchoolId, "UTF-8") + "&" +
                        URLEncoder.encode("tbl_users_id", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8") + "&" +
                        URLEncoder.encode("tbl_course_group_id", "UTF-8") + "=" + URLEncoder.encode(course_id, "UTF-8");
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
            if (result.equals("{\"result\":null}")) {
                studentArray.clear();
                Toast.makeText(ctx, "No Value", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    JSONObject jsonRootObject = new JSONObject(result);
                    JSONArray jsonArray = jsonRootObject.getJSONArray("result");
                    studentArray.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        DataBeanStudent dbs = new DataBeanStudent();
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        dbs.setUserID(jsonObject.getString("tbl_batch_detail_id"));
                        dbs.setUserName(jsonObject.getString("tbl_batch_name"));
                        studentArray.add(dbs);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setBatch();
                super.onPostExecute(result);
            }
        }
    }

    private void setBatch() {
        batch_spinner.setOnClickListener(v -> setNumberOfUsers(studentArray));
    }

    private void setNumberOfUsers(final ArrayList<DataBeanStudent> studentArray) {
        LayoutInflater factory = LayoutInflater.from(this);
        final View textEntryView = factory.inflate(R.layout.tudent_select_dialog, null);

        ListView user_list = textEntryView.findViewById(R.id.user_list);
        CheckBox cb_selectAll = textEntryView.findViewById(R.id.cb_selectAll);
        batchListAdaptor = new BatchListAdaptor(this, R.layout.user_select_dialog, studentArray, false);
        user_list.setAdapter(batchListAdaptor);

        cb_selectAll.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                batchListAdaptor = new BatchListAdaptor(this, R.layout.user_select_dialog, studentArray, b);
                user_list.setAdapter(batchListAdaptor);
                batchListAdaptor.notifyDataSetChanged();
                saveDataInSP(studentArray);
            } else {
                batchListAdaptor = new BatchListAdaptor(this, R.layout.user_select_dialog, studentArray, b);
                user_list.setAdapter(batchListAdaptor);
                batchListAdaptor.notifyDataSetChanged();
                SharedPreferences.Editor se_dairy = getSharedPreferences(PREFS_DAIRY, 0).edit();
                se_dairy.clear();
                se_dairy.apply();
            }
        });

        alert = new AlertDialog.Builder(this, R.style.DialogTheme);

        alert.setTitle("Select Batch").setView(textEntryView).setPositiveButton("Ok",
                (dialog, whichButton) -> {
                    alertDialog.dismiss();
                    String studentArray1 = getSharedPreferences("dairy", 0).getString("studentArray", "");
                    if (!studentArray1.isEmpty()) {
                        batch_spinner.setHint("Selected");
                        GetSubject subject = new GetSubject(this);
                        subject.execute(userSchoolId, studentArray1);
                    }
                }).setNegativeButton("Cancel",
                (dialog, whichButton) -> {
                });
        alertDialog = alert.create();
        alertDialog.show();
        Button theButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        Button theButton1 = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        theButton.setTextColor(getResources().getColor(R.color.text_gray));
        theButton1.setTextColor(getResources().getColor(R.color.text_gray));
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setEnabled(true);
    }

    @SuppressLint("StaticFieldLeak")
    public class GetSubject extends AsyncTask<String, String, String> {
        Context ctx;

        GetSubject(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            String school_id = params[0];
            String batchArray = params[1];
            String data;

            try {

                URL url = new URL(GET_SUBS);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                data = URLEncoder.encode("school_id", "UTF-8") + "=" + URLEncoder.encode(school_id, "UTF-8") + "&" +
                        URLEncoder.encode("batchArray", "UTF-8") + "=" + URLEncoder.encode(batchArray, "UTF-8") + "&" +
                        URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8") + "&" +
                        URLEncoder.encode("tbl_users_nm", "UTF-8") + "=" + URLEncoder.encode(userName, "UTF-8") + "&" +
                        URLEncoder.encode("restricted_access", "UTF-8") + "=" + URLEncoder.encode(restricted, "UTF-8");
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
            Log.e("sub", result);
            if (result.equals("{\"SubjectList\":null}")) {
                String[] subject_array = {"Select Subject"};
                ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<>(ctx, R.layout.spinner_list_item1, subject_array);
                dataAdapter1.setDropDownViewResource(R.layout.spinner_list_item1);
                subject_spinner.setAdapter(dataAdapter1);
            } else {
                try {
                    JSONObject jsonRootObject = new JSONObject(result);
                    JSONArray jsonArray = jsonRootObject.getJSONArray("SubjectList");
                    subjects = new String[jsonArray.length() + 1];
                    subjects[0] = "Select Subject";
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        subjects[i + 1] = jsonObject.getString("tbl_batch_subjct_name");
                    }
                    if (userrType.equals("Teacher")) {
                        String object = jsonRootObject.getString("RistrictedSubject");
                        if (object.equals("null")) {
                            subject_array.addAll(Arrays.asList(subjects));
                        } else {
                            for (String subject : subjects) {
                                if (object.contains(subject)) {
                                    subject_array.add(subject);
                                }
                            }
                        }
                    } else {
                        subject_array.addAll(Arrays.asList(subjects));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setSubject();
                super.onPostExecute(result);
            }
        }
    }

    private void setSubject() {
        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<>(this, R.layout.spinner_list_item1, subject_array);
        dataAdapter1.setDropDownViewResource(R.layout.spinner_list_item1);
        subject_spinner.setAdapter(dataAdapter1);

        subject_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (view != null) {
                    ((TextView) view).setTextColor(getResources().getColor(R.color.text_gray));
                }
                selsubject = subject_array.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void saveDataInSP(ArrayList<DataBeanStudent> studentArray) {
        Gson gson = new GsonBuilder().create();
        JsonArray studentArray1 = gson.toJsonTree(studentArray).getAsJsonArray();
        SharedPreferences sp_dairy = getSharedPreferences(PREFS_DAIRY, 0);
        SharedPreferences.Editor se_dairy = sp_dairy.edit();
        se_dairy.clear();
        se_dairy.putString("studentArray", "" + studentArray1);
        se_dairy.apply();
    }

    @SuppressLint("StaticFieldLeak")
    class UploadVideo extends AsyncTask<String, String, String> {

        Context context;

        UploadVideo(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(context, "Please wait adding video", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(String... params) {
            String title = params[0];
            String video_url = params[1];
            String school_id = params[2];
            String userId = params[3];
            String studentArray1 = params[4];
            String userType = params[5];
            String desc = params[6];
            String subject = params[7];

            try {
                SSLContext sslContext = SSLContexts.custom().useTLS().build();
                SSLConnectionSocketFactory f = new SSLConnectionSocketFactory(
                        sslContext,
                        new String[]{"TLSv1.1", "TLSv1.2"},
                        null,
                        BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
                HttpClient httpclient = HttpClients.custom().setSSLSocketFactory(f).build();
                HttpPost httppost = new HttpPost(ADD_VIDEOS);
                MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
                entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                entityBuilder.addTextBody("str_title", title);
                entityBuilder.addTextBody("userSection", studentArray1);
                entityBuilder.addTextBody("str_videoLink", video_url);
                entityBuilder.addTextBody("schoolId", school_id);
                entityBuilder.addTextBody("userId", userId);
                entityBuilder.addTextBody("userType", userType);
                entityBuilder.addTextBody("description", desc);
                entityBuilder.addTextBody("subject_name", subject);
                entityBuilder.addTextBody("tbl_course_group_id", strCourse);
                entityBuilder.addTextBody("visible_till", strDate);
                entityBuilder.addTextBody("visible_till_time", strTime);
                entityBuilder.addTextBody("visible_start_date", strStart);
                entityBuilder.addTextBody("visible_start_time", strstartTime);
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
            if (result.contains("submitted")) {
                cardView.setVisibility(View.VISIBLE);
                new CountDownTimer(4000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        Intent i = new Intent(context, VideoLibrary.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                        finish();
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                }.start();
            } else {
                layout.setVisibility(View.GONE);
                Toast.makeText(context, "Server Issue", Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(result);
        }
    }


    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        assert inputMethodManager != null;
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
