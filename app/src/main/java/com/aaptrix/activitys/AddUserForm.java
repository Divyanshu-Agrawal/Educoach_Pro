package com.aaptrix.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
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
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.aaptrix.R;
import com.aaptrix.activitys.admin.InstituteBuzzActivityDiff;
import com.google.android.material.appbar.AppBarLayout;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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

import static com.aaptrix.tools.HttpUrl.ABOUT_SCHOOL_INFO;
import static com.aaptrix.tools.HttpUrl.ADD_LEAD;
import static com.aaptrix.tools.HttpUrl.ALL_BATCHS;
import static com.aaptrix.tools.HttpUrl.GET_COURSES;
import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREF_COLOR;
import static cz.msebera.android.httpclient.conn.ssl.SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER;

public class AddUserForm extends AppCompatActivity {

    AppBarLayout appBarLayout;
    Toolbar toolbar;
    EditText name, primaryPhone, email, infoFromOther, desc, followupDate;
    Spinner infoFrom, course;
    Button save;
    RelativeLayout layout;
    CardView cardView;
    MediaPlayer mp;
    String strFrom, strCourse = "Select Course", strDate;
    ArrayList<String> fromArray = new ArrayList<>(), courseArray = new ArrayList<>();
    String selToolColor, selStatusColor, selTextColor1, userSchoolId;
    String[] batch_array = {"Select Batch"};
    Spinner spin_section;
    String selBatch = "Select Batch";

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user_form);
        toolbar = findViewById(R.id.toolbar);
        appBarLayout = findViewById(R.id.appBarLayout);
        setSupportActionBar(toolbar);
        setTitle("Enquiry Form");
        setResult(RESULT_OK);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mp = MediaPlayer.create(this, R.raw.button_click);
        cardView = findViewById(R.id.card_view);
        layout = findViewById(R.id.layout);
        name = findViewById(R.id.user_name);
        infoFrom = findViewById(R.id.info_from);
        followupDate = findViewById(R.id.followup_date);
        primaryPhone = findViewById(R.id.primary_phone);
        email = findViewById(R.id.user_email);
        course = findViewById(R.id.user_course);
        infoFromOther = findViewById(R.id.info_from_other);
        save = findViewById(R.id.save_btn);
        desc = findViewById(R.id.description);
        spin_section = findViewById(R.id.spin_section);

        SharedPreferences sp = getSharedPreferences(PREFS_NAME, 0);
        String userType = sp.getString("userrType", "");
        userSchoolId = sp.getString("str_school_id", "");
        String userId = sp.getString("userID", "");

        if (userType.equals("Admin") || userType.equals("Teacher")) {
            spin_section.setVisibility(View.VISIBLE);
        } else {
            spin_section.setVisibility(View.GONE);
        }

        if (userType.equals("Guest")) {
            followupDate.setVisibility(View.GONE);
        }

        if (userType.equals("Admin") || userType.equals("Teacher")) {
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
                        batch_array[0] = "Select Batch";
                        for (int i = 0; i < ja.length(); i++) {
                            jo = ja.getJSONObject(i);
                            batch_array[i + 1] = jo.getString("tbl_batch_name");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    setBatch();
                } else {
                    String[] batch_array = {"Select Batch"};
                    ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<>(this, R.layout.spinner_list_item1, batch_array);
                    dataAdapter1.setDropDownViewResource(R.layout.spinner_list_item1);
                    spin_section.setAdapter(dataAdapter1);
                    Toast.makeText(this, "No Batch", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                GetAllBatches b1 = new GetAllBatches(this);
                b1.execute(userSchoolId, userId);
            }
        }

        if (userType.equals("Guest")) {
            try {
                File directory = this.getFilesDir();
                ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File(directory, "courses")));
                String json = in.readObject().toString();
                in.close();
                JSONObject jsonRootObject = new JSONObject(json);
                JSONArray ja = jsonRootObject.getJSONArray("result");
                for (int i = 0; i < ja.length(); i++) {
                    JSONObject jo = ja.getJSONObject(i);
                    courseArray.add(jo.getString("tbl_abt_schl_more_info_name"));
                }
                setCourse();
            } catch (Exception e) {
                e.printStackTrace();
                GetCourseGuest getCourseGuest = new GetCourseGuest(this);
                getCourseGuest.execute(userSchoolId);
            }
        } else {
            GetCourse getCourse = new GetCourse(this);
            getCourse.execute(userSchoolId);
        }

        followupDate.setOnClickListener(v -> {
            final Calendar mcurrentDate = Calendar.getInstance();
            int mYear = mcurrentDate.get(Calendar.YEAR);
            int mMonth = mcurrentDate.get(Calendar.MONTH);
            int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog mDatePicker = new DatePickerDialog(
                    this, R.style.AlertDialogCustom1, (datepicker, selectedyear, selectedmonth, selectedday) -> {
                mcurrentDate.set(Calendar.YEAR, selectedyear);
                mcurrentDate.set(Calendar.MONTH, selectedmonth);
                mcurrentDate.set(Calendar.DAY_OF_MONTH, selectedday);
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                followupDate.setText(sdf.format(mcurrentDate.getTime()));
                sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                strDate = sdf.format(mcurrentDate.getTime());
            }, mYear, mMonth, mDay);
            mDatePicker.show();
        });

        name.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus)
                hideKeyboard(v);
        });

        desc.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus)
                hideKeyboard(v);
        });

        primaryPhone.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus)
                hideKeyboard(v);
        });

        email.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus)
                hideKeyboard(v);
        });

        layout.setOnClickListener(v -> {

        });

        layout.setOnTouchListener((v, event) -> false);

        fromArray.add("How you come to know about us?");
        fromArray.add("Internet");
        fromArray.add("Newspaper/Magazine");
        fromArray.add("Alumni/Student");
        fromArray.add("Other");

        ArrayAdapter<String> fromadapter = new ArrayAdapter<>(this, R.layout.spinner_list_item1, fromArray);
        fromadapter.setDropDownViewResource(R.layout.spinner_list_item1);
        infoFrom.setAdapter(fromadapter);

        infoFrom.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (fromArray.get(position).equals("Other")) {
                    strFrom = fromArray.get(position);
                    infoFromOther.setVisibility(View.VISIBLE);
                } else {
                    strFrom = fromArray.get(position);
                    infoFromOther.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        save.setOnClickListener(v -> {
            if (TextUtils.isEmpty(name.getText().toString())) {
                name.setError("Please Enter Name");
                name.requestFocus();
            } else if (TextUtils.isEmpty(primaryPhone.getText().toString())) {
                primaryPhone.setError("Please Enter Contact Number");
                primaryPhone.requestFocus();
            } else if (strCourse.equals("Select Course")) {
                Toast.makeText(this, "Please select course", Toast.LENGTH_SHORT).show();
            }  else if (!userType.equals("Guest")) {
                if (selBatch.equals("Select Batch")) {
                    Toast.makeText(this, "Please Select Batch", Toast.LENGTH_SHORT).show();
                }
            } else if (strFrom.equals("How you come to know about us?")) {
                Toast.makeText(this, "Please select how you come to know about us", Toast.LENGTH_SHORT).show();
            } else if (strFrom.equals("Other")) {
                if (TextUtils.isEmpty(infoFromOther.getText().toString())) {
                    infoFromOther.requestFocus();
                    infoFromOther.setError("Please fill all the details");
                } else {
                    SendData sendData = new SendData(this);
                    sendData.execute(name.getText().toString(), primaryPhone.getText().toString(),
                            email.getText().toString(), strCourse, userSchoolId, strFrom, infoFromOther.getText().toString(),
                            desc.getText().toString(), strDate, selBatch);
                }
            } else {
                SendData sendData = new SendData(this);
                sendData.execute(name.getText().toString(), primaryPhone.getText().toString(),
                        email.getText().toString(), strCourse, userSchoolId, strFrom, "Other",
                        desc.getText().toString(), strDate, selBatch);
            }
        });

        SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
        selToolColor = settingsColor.getString("tool", "");
        selStatusColor = settingsColor.getString("status", "");
        selTextColor1 = settingsColor.getString("text1", "");

        appBarLayout.setBackgroundColor(Color.parseColor(selToolColor));
        save.setBackgroundColor(Color.parseColor(selToolColor));
        save.setTextColor(Color.parseColor(selTextColor1));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(selStatusColor));
        }
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
            if (!result.equals("{\"result\":null}")) {
                try {
                    JSONObject jo = new JSONObject(result);
                    JSONArray ja = jo.getJSONArray("result");
                    courseArray.add("Select Course");
                    for (int i = 0; i < ja.length(); i++) {
                        jo = ja.getJSONObject(i);
                        courseArray.add(jo.getString("tbl_course_name"));
                    }
                    setCourse();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                courseArray.clear();
                courseArray.add("Select Course");
                ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<>(ctx, R.layout.spinner_list_item1, courseArray);
                dataAdapter1.setDropDownViewResource(R.layout.spinner_list_item1);
                course.setAdapter(dataAdapter1);
                Toast.makeText(ctx, "No Course", Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(result);
        }

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

            if (!result.equals("\"course_list\":null")) {
                try {
                    JSONObject jo = new JSONObject(result);
                    JSONArray ja = jo.getJSONArray("result");
                    courseArray.add("Select Course");
                    for (int i = 0; i < ja.length(); i++) {
                        jo = ja.getJSONObject(i);
                        courseArray.add(jo.getString("tbl_abt_schl_more_info_name"));
                    }
                    setCourse();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                courseArray.clear();
                courseArray.add("Select Course");
                ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<>(ctx, R.layout.spinner_list_item1, courseArray);
                dataAdapter1.setDropDownViewResource(R.layout.spinner_list_item1);
                course.setAdapter(dataAdapter1);
                Toast.makeText(ctx, "No Course", Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(result);
        }

    }

    private void setCourse() {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, R.layout.spinner_list_item1, courseArray);
        dataAdapter.setDropDownViewResource(R.layout.spinner_list_item1);
        course.setAdapter(dataAdapter);

        course.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) adapterView.getChildAt(0)).setTextColor(getResources().getColor(R.color.text_gray));
                strCourse = courseArray.get(i);
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
            String school_id = params[0];
            String userId = params[1];
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
            Log.d("result", result);
            if (!result.equals("{\"result\":null}")) {
                try {
                    JSONObject jo = new JSONObject(result);
                    JSONArray ja = jo.getJSONArray("result");
                    batch_array = new String[ja.length() + 1];
                    batch_array[0] = "Select Batch";
                    for (int i = 0; i < ja.length(); i++) {
                        jo = ja.getJSONObject(i);
                        batch_array[i + 1] = jo.getString("tbl_batch_name");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setBatch();
            } else {
                String[] batch_array = {"Select Batch"};
                ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<>(ctx, R.layout.spinner_list_item1, batch_array);
                dataAdapter1.setDropDownViewResource(R.layout.spinner_list_item1);
                spin_section.setAdapter(dataAdapter1);
                Toast.makeText(ctx, "No Batch", Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(result);
        }

    }

    private void setBatch() {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, R.layout.spinner_list_item1, batch_array);
        dataAdapter.setDropDownViewResource(R.layout.spinner_list_item1);
        spin_section.setAdapter(dataAdapter);

        spin_section.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) adapterView.getChildAt(0)).setTextColor(getResources().getColor(R.color.text_gray));
                selBatch = batch_array[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    public class SendData extends AsyncTask<String, String, String> {
        Context ctx;

        SendData(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            layout.setVisibility(View.VISIBLE);
            layout.bringToFront();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            String userName = params[0];
            String primaryPhone = params[1];
            String email = params[2];
            String course = params[3];
            String schoolId = params[4];
            String from = params[5];
            String fromOther = params[6];
            String desc = params[7];
            String date = params[8];
            String batch = params[9];

            try {
                SSLContext sslContext = SSLContexts.custom().useTLS().build();
                SSLConnectionSocketFactory f = new SSLConnectionSocketFactory(
                        sslContext,
                        new String[]{"TLSv1.1", "TLSv1.2"},
                        null,
                        BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
                HttpClient httpclient = HttpClients.custom().setSSLSocketFactory(f).build();
                HttpPost httppost = new HttpPost(ADD_LEAD);
                MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
                entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                entityBuilder.addTextBody("tbl_enquiry_name", userName);
                entityBuilder.addTextBody("tbl_enquiry_phone", primaryPhone);
                entityBuilder.addTextBody("tbl_enquiry_email", email);
                entityBuilder.addTextBody("tbl_enquiry_course", course);
                entityBuilder.addTextBody("tbl_school_id", schoolId);
                entityBuilder.addTextBody("tbl_enquiry_howtoknow", from);
                entityBuilder.addTextBody("tbl_enquiry_howtoknow_other", fromOther);
                entityBuilder.addTextBody("tbl_enquiry_desc", desc);
                entityBuilder.addTextBody("tbl_enquiry_followup", date);
                entityBuilder.addTextBody("tbl_enquiry_batch", batch);
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
            Log.e("result", result);
            if (result.contains("\"submitted\"")) {
                cardView.setVisibility(View.VISIBLE);
                new CountDownTimer(4000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        Intent i = new Intent(ctx, InstituteBuzzActivityDiff.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish();
                    }
                }.start();
            } else {
                Toast.makeText(ctx, "Server issues", Toast.LENGTH_SHORT).show();
                layout.setVisibility(View.GONE);
            }
            super.onPostExecute(result);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mp.release();
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
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
