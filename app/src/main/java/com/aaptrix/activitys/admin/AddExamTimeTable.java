package com.aaptrix.activitys.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.aaptrix.R;
import com.aaptrix.adaptor.SubjectAdapter;
import com.aaptrix.databeans.ExamData;
import com.google.android.material.appbar.AppBarLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import pl.droidsonroids.gif.GifImageView;

import static com.aaptrix.tools.HttpUrl.ADD_EXAM_TIMETABLE;
import static com.aaptrix.tools.HttpUrl.LIST_OF_SUBJECTS;
import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREF_COLOR;
import static cz.msebera.android.httpclient.conn.ssl.SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER;

public class AddExamTimeTable extends AppCompatActivity {

    ArrayList<ExamData> arrayList = new ArrayList<>();
    Toolbar toolbar;
    AppBarLayout appBarLayout;
    EditText marks, details, date;
    Spinner subjects;
    Button save;
    Button add;
    ListView listView;
    String examType = "1";
    RadioButton major, minor;
    EditText examName;
    String selToolColor, selStatusColor, selTextColor1, userSchoolId;
    CardView cardView;
    GifImageView taskStatus;
    MediaPlayer mp;
    TextView no_subject;
    String[] subject_array = {"Select Subject"};
    String selSubject, selBatch, userClass, selDate;
    RelativeLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_exam_time_table);
        toolbar = findViewById(R.id.toolbar);
        appBarLayout = findViewById(R.id.appBarLayout);
        setSupportActionBar(toolbar);
        setResult(RESULT_OK);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        selBatch = getIntent().getStringExtra("section");

        mp = MediaPlayer.create(this, R.raw.button_click);
        cardView = findViewById(R.id.card_view);
        taskStatus = findViewById(R.id.task_status);

        layout = findViewById(R.id.layout);
        save = findViewById(R.id.save_btn);
        add = findViewById(R.id.add_subject);
        listView = findViewById(R.id.subject_list);
        no_subject = findViewById(R.id.no_subjects);

        examName = findViewById(R.id.exam_name);
        major = findViewById(R.id.major);
        minor = findViewById(R.id.minor);
        major.setChecked(true);

        examName.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus)
                hideKeyboard(v);
        });

        major.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                examType = "1";
            }
        });

        minor.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                examType = "0";
            }
        });

        layout.setOnClickListener(v -> {

        });

        layout.setOnTouchListener((v, event) -> false);

        add.setOnClickListener(v -> addSubject());

        save.setOnClickListener(v -> {
            if (TextUtils.isEmpty(examName.getText().toString())) {
                examName.setError("Please Enter Exam Name");
                examName.requestFocus();
            } else if (arrayList.size() == 0) {
                Toast.makeText(this, "Please add subjects", Toast.LENGTH_SHORT).show();
            } else {
                layout.setVisibility(View.VISIBLE);
                layout.bringToFront();
                saveDataInSP(arrayList);
            }
        });

        if (arrayList.size() == 0) {
            no_subject.setVisibility(View.VISIBLE);
        }

        SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
        selToolColor = settingsColor.getString("tool", "");
        selStatusColor = settingsColor.getString("status", "");
        selTextColor1 = settingsColor.getString("text1", "");

        SharedPreferences sp_user = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        userSchoolId = sp_user.getString("str_school_id", "");
        userClass = sp_user.getString("userClass", "");

        ListOfSubjects listOfSubjects = new ListOfSubjects(this);
        listOfSubjects.execute(userSchoolId, userClass, selBatch);

        appBarLayout.setBackgroundColor(Color.parseColor(selToolColor));
        save.setBackgroundColor(Color.parseColor(selToolColor));
        save.setTextColor(Color.parseColor(selTextColor1));
        add.setBackgroundColor(Color.parseColor(selToolColor));
        add.setTextColor(Color.parseColor(selTextColor1));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(selStatusColor));
        }
    }

    private void addSubject() {
        LayoutInflater factory = LayoutInflater.from(this);
        final View view = factory.inflate(R.layout.add_subject_dialog, null);

        marks = view.findViewById(R.id.subject_marks);
        details = view.findViewById(R.id.subject_detail);
        date = view.findViewById(R.id.exam_date);
        date.setFocusable(false);
        subjects = view.findViewById(R.id.select_subject);

        AllAdaptors();

        date.setOnClickListener(v -> {
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
                date.setText(sdf.format(mcurrentDate.getTime()));
                sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                selDate = sdf.format(mcurrentDate.getTime());
            }, mYear, mMonth, mDay);
            mDatePicker.getDatePicker().setMinDate(System.currentTimeMillis());
            mDatePicker.show();
        });

        AlertDialog.Builder alert = new AlertDialog.Builder(this, R.style.DialogTheme);

        alert.setTitle("Add Subject").setView(view).setPositiveButton("Ok",
                (dialog, whichButton) -> {
                    if (!selSubject.equals("Select Subject")) {
                        if (TextUtils.isEmpty(selDate)) {
                            date.setError("Please Select Date");
                            date.requestFocus();
                        } else if (TextUtils.isEmpty(details.getText().toString())) {
                            details.setError("Please Enter Details");
                            details.requestFocus();
                        } else if (TextUtils.isEmpty(marks.getText().toString())) {
                            marks.setError("Please Enter Marks");
                            marks.requestFocus();
                        } else {
                            for (int i = 0; i < arrayList.size(); i++) {
                                if (arrayList.get(i).getSubjectNm().equals(selSubject)) {
                                    Toast.makeText(this, "Subject Already Added", Toast.LENGTH_SHORT).show();
                                    return;
                                }
                            }

                            ExamData data = new ExamData();
                            data.setSubjectNm(selSubject);
                            data.setExamDate(selDate);
                            data.setDetails(details.getText().toString());
                            data.setMarks(marks.getText().toString());
                            arrayList.add(data);
                            showSubjectList(arrayList);
                        }
                    } else {
                        Toast.makeText(this, "Please Select Subject", Toast.LENGTH_SHORT).show();
                    }
                }).setNegativeButton("Cancel",
                (dialog, whichButton) -> {
                });

        AlertDialog alertDialog = alert.create();
        alertDialog.show();
        Button theButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        Button theButton1 = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        theButton.setTextColor(getResources().getColor(R.color.text_gray));
        theButton1.setTextColor(getResources().getColor(R.color.text_gray));
    }

    private void showSubjectList(ArrayList<ExamData> arrayList) {
        SubjectAdapter adapter = new SubjectAdapter(this, R.layout.subject_list, arrayList);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        if (arrayList.size() != 0) {
            no_subject.setVisibility(View.GONE);
        } else {
            no_subject.setVisibility(View.VISIBLE);
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

    @SuppressLint("StaticFieldLeak")
    public class ListOfSubjects extends AsyncTask<String, String, String> {
        Context ctx;

        ListOfSubjects(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            String schoolId = params[0];
            String userClass = params[1];
            String userSection = params[2];

            try {

                SSLContext sslContext = SSLContexts.custom().useTLS().build();
                SSLConnectionSocketFactory f = new SSLConnectionSocketFactory(
                        sslContext,
                        new String[]{"TLSv1.1", "TLSv1.2"},
                        null,
                        BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
                HttpClient httpclient = HttpClients.custom().setSSLSocketFactory(f).build();
                HttpPost httppost = new HttpPost(LIST_OF_SUBJECTS);
                MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
                entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                entityBuilder.addTextBody("schoolId", schoolId);
                entityBuilder.addTextBody("class", userClass);
                entityBuilder.addTextBody("section", userSection);
                HttpEntity entity = entityBuilder.build();
                httppost.setEntity(entity);
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity httpEntity = response.getEntity();
                String res = EntityUtils.toString(httpEntity);
                Log.e("res", res);
                return res;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            try {
                JSONObject jo = new JSONObject(result);
                JSONArray ja = jo.getJSONArray("result");
                subject_array = new String[ja.length() + 1];
                subject_array[0] = "Select Subject";
                for (int i = 0; i < ja.length(); i++) {
                    jo = ja.getJSONObject(i);
                    subject_array[i + 1] = jo.getString("tbl_batch_subjct_name");
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            super.onPostExecute(result);
        }
    }

    private void AllAdaptors() {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, R.layout.spinner_list_item1, subject_array);
        dataAdapter.setDropDownViewResource(R.layout.spinner_list_item1);
        subjects.setAdapter(dataAdapter);

        subjects.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                selSubject = subject_array[i];
                ((TextView) view).setTextColor(getResources().getColor(R.color.text_gray));
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void saveDataInSP(ArrayList<ExamData> arrayList) {
        Gson gson = new GsonBuilder().create();
        JsonArray subjectArray = gson.toJsonTree(arrayList).getAsJsonArray();
        UploadTT tt = new UploadTT(this);
        tt.execute(userSchoolId, subjectArray.toString(), examType, examName.getText().toString(), selBatch);
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
        finish();
        super.onBackPressed();
    }

    @SuppressLint("StaticFieldLeak")
    public class UploadTT extends AsyncTask<String, String, String> {
        Context ctx;

        UploadTT(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            Toast.makeText(ctx, "Please wait, adding exam", Toast.LENGTH_SHORT).show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            String schoolId = params[0];
            String ttArray = params[1];
            String examType = params[2];
            String examName = params[3];
            String batch = params[4];

            try {
                SSLContext sslContext = SSLContexts.custom().useTLS().build();
                SSLConnectionSocketFactory f = new SSLConnectionSocketFactory(
                        sslContext,
                        new String[]{"TLSv1.1", "TLSv1.2"},
                        null,
                        BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
                HttpClient httpclient = HttpClients.custom().setSSLSocketFactory(f).build();
                HttpPost httppost = new HttpPost(ADD_EXAM_TIMETABLE);
                MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
                entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                entityBuilder.addTextBody("school_id", schoolId);
                entityBuilder.addTextBody("timetableArray", ttArray);
                entityBuilder.addTextBody("tbl_exam_name", examName);
                entityBuilder.addTextBody("exam_major_minor", examType);
                entityBuilder.addTextBody("tbl_stnt_prsnl_data_section", batch);
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
            Log.e("ADDED", result);
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
                Toast.makeText(ctx, result, Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(result);
        }
    }
}

