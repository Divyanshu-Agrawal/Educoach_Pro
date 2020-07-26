package com.aaptrix.activitys.admin;

import com.aaptrix.activitys.student.StudyMaterial;
import com.aaptrix.R;
import com.aaptrix.adaptor.BatchListAdaptor;
import com.aaptrix.databeans.DataBeanStudent;
import com.aaptrix.tools.FileUtil;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.conn.ssl.SSLConnectionSocketFactory;
import cz.msebera.android.httpclient.conn.ssl.SSLContexts;
import cz.msebera.android.httpclient.entity.mime.HttpMultipartMode;
import cz.msebera.android.httpclient.entity.mime.MultipartEntityBuilder;
import cz.msebera.android.httpclient.entity.mime.content.FileBody;
import cz.msebera.android.httpclient.impl.client.HttpClients;
import cz.msebera.android.httpclient.util.EntityUtils;
import id.zelory.compressor.Compressor;
import pl.droidsonroids.gif.GifImageView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.AppBarLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
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
import java.util.Arrays;
import java.util.Objects;

import javax.net.ssl.SSLContext;

import static com.aaptrix.tools.HttpUrl.ADD_STUDY_MATERIAL;
import static com.aaptrix.tools.HttpUrl.BATCH_BY_COURSE;
import static com.aaptrix.tools.HttpUrl.GET_COURSES;
import static com.aaptrix.tools.HttpUrl.GET_SUBS;
import static com.aaptrix.tools.SPClass.PREFS_DAIRY;
import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREF_COLOR;
import static cz.msebera.android.httpclient.conn.ssl.SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER;

public class AddNewMaterial extends AppCompatActivity {

    EditText title, description, batch_spinner;
    TextView fileCount;
    ImageButton addImage;
    ArrayList<File> filepath = new ArrayList<>();
    Toolbar toolbar;
    AppBarLayout appBarLayout;
    Button save;
    String selToolColor, selStatusColor, selTextColor1, userSchoolId, userId, userType, userName, restricted, userrType;
    MediaPlayer mp;
    CardView cardView;
    RelativeLayout layout;
    CheckBox download;
    String strDownload = "1";
    GifImageView taskStatus;
    Spinner subject_spinner, course_spinner;
    String[] subjects = {"Select Subject"};
    ArrayList<String> subject_array = new ArrayList<>();
    String[] course_array = {"Select Course"};
    String[] course_id = {"0"};
    BatchListAdaptor batchListAdaptor;
    String selsubject = "Select Subject", strCourse;
    AlertDialog.Builder alert;
    AlertDialog alertDialog;
    private ArrayList<DataBeanStudent> studentArray = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_material);
        addImage = findViewById(R.id.sel_image);
        toolbar = findViewById(R.id.toolbar);
        download = findViewById(R.id.download_permission);
        appBarLayout = findViewById(R.id.appBarLayout);
        setSupportActionBar(toolbar);
        setTitle("Add New Study Material");
        setResult(RESULT_OK);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mp = MediaPlayer.create(this, R.raw.button_click);
        cardView = findViewById(R.id.card_view);
        taskStatus = findViewById(R.id.task_status);
        title = findViewById(R.id.material_title);
        description = findViewById(R.id.material_description);
        save = findViewById(R.id.save_btn);
        layout = findViewById(R.id.layout);
        fileCount = findViewById(R.id.file_count);
        batch_spinner = findViewById(R.id.batch_spinner);
        subject_spinner = findViewById(R.id.subject_spinner);
        course_spinner = findViewById(R.id.course_spinner);

        SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
        selToolColor = settingsColor.getString("tool", "");
        selStatusColor = settingsColor.getString("status", "");
        selTextColor1 = settingsColor.getString("text1", "");

        SharedPreferences sp_user = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        userSchoolId = sp_user.getString("str_school_id", "");
        userId = sp_user.getString("userID", "");
        userType = sp_user.getString("userrType", "");
        userName = sp_user.getString("userName", "");
        restricted = sp_user.getString("restricted", "");
        userrType = sp_user.getString("userrType", "");

        GetCourse getCourse = new GetCourse(this);
        getCourse.execute(userSchoolId);

        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<>(this, R.layout.spinner_list_item1, subjects);
        dataAdapter1.setDropDownViewResource(R.layout.spinner_list_item1);
        subject_spinner.setAdapter(dataAdapter1);

        appBarLayout.setBackgroundColor(Color.parseColor(selToolColor));
        save.setBackgroundColor(Color.parseColor(selToolColor));
        save.setTextColor(Color.parseColor(selTextColor1));

        title.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                hideKeyboard(v);
            }
        });

        description.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                hideKeyboard(v);
            }
        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(selStatusColor));
        }

        download.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                strDownload = "1";
            } else {
                strDownload = "0";
            }
        });

        addImage.setOnClickListener(v -> {
            if (PermissionChecker.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_GRANTED) {
                Intent intent = new Intent();
                intent.setType("*/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, 1);
            } else {
                isPermissionGranted();
            }
        });

        layout.setOnClickListener(v -> {

        });

        layout.setOnTouchListener((v, event) -> false);

        save.setOnClickListener(v -> {
            mp.start();
            if (TextUtils.isEmpty(title.getText())) {
                title.requestFocus();
                title.setError("Please enter title");
            } else {
                layout.setVisibility(View.VISIBLE);
                layout.bringToFront();
                SharedPreferences sp = getSharedPreferences(PREFS_DAIRY, 0);
                String studentArray = sp.getString("studentArray", "");
                if (studentArray.isEmpty()) {
                    layout.setVisibility(View.VISIBLE);
                    layout.bringToFront();
                    UploadStudyMaterial uploadStudyMaterial = new UploadStudyMaterial(this);
                    uploadStudyMaterial.execute(userSchoolId, title.getText().toString(),
                            description.getText().toString(), studentArray, userId, "");
                } else {
                    if (selsubject.equals("Select Subject")) {
                        Toast.makeText(this, "Please select subject", Toast.LENGTH_SHORT).show();
                    } else {
                        layout.setVisibility(View.VISIBLE);
                        layout.bringToFront();
                        UploadStudyMaterial uploadStudyMaterial = new UploadStudyMaterial(this);
                        uploadStudyMaterial.execute(userSchoolId, title.getText().toString(),
                                description.getText().toString(), studentArray, userId, userType, selsubject);
                    }
                }
            }
        });
    }

    private void saveDataInSP(ArrayList<DataBeanStudent> studentArray) {
        Gson gson = new GsonBuilder().create();
        JsonArray studentArray1 = gson.toJsonTree(studentArray).getAsJsonArray();
        String PREFS_DAIRY = "dairy";
        SharedPreferences sp_dairy = getSharedPreferences(PREFS_DAIRY, 0);
        SharedPreferences.Editor se_dairy = sp_dairy.edit();
        se_dairy.clear();
        se_dairy.putString("studentArray", "" + studentArray1);
        se_dairy.apply();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mp.release();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                ClipData clipData = data.getClipData();
                if (clipData != null) {
                    for (int i = 0; i < clipData.getItemCount(); i++) {
                        try {
                            String file_extn = FileUtil.from(this, clipData.getItemAt(i).getUri()).toString()
                                    .substring(FileUtil.from(this, clipData.getItemAt(i).getUri()).toString().lastIndexOf(".") + 1);
                            if (file_extn.equals("jpg") || file_extn.equals("jpeg") || file_extn.equals("png")) {
                                filepath.add(new Compressor(this)
                                        .setMaxWidth(1280)
                                        .setMaxHeight(720)
                                        .setQuality(25)
                                        .setCompressFormat(Bitmap.CompressFormat.JPEG)
                                        .compressToFile(FileUtil.from(this, clipData.getItemAt(i).getUri())));
                            } else if (file_extn.equals("pdf")) {
                                filepath.add(FileUtil.from(this, clipData.getItemAt(i).getUri()));
                            } else {
                                Toast.makeText(this, "File not in supported format, you can only select pdf or image file", Toast.LENGTH_SHORT).show();
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    fileCount.setText(filepath.size() + " Files Selected");
                } else {
                    try {
                        String file_extn = FileUtil.from(this, data.getData()).toString()
                                .substring(FileUtil.from(this, data.getData()).toString().lastIndexOf(".") + 1);
                        if (file_extn.equals("jpg") || file_extn.equals("jpeg") || file_extn.equals("png")) {
                            filepath.add(new Compressor(this)
                                    .setMaxWidth(1280)
                                    .setMaxHeight(720)
                                    .setQuality(25)
                                    .setCompressFormat(Bitmap.CompressFormat.JPEG)
                                    .compressToFile(FileUtil.from(this, data.getData())));
                        } else if (file_extn.equals("pdf")) {
                            filepath.add(FileUtil.from(this, data.getData()));
                        } else {
                            Toast.makeText(this, "File not in supported format, you can only select pdf or image file", Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    fileCount.setText(filepath.size() + " File Selected");
                }
            }
        }
    }

    public void isPermissionGranted() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
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
                    GetAllBatches getAllBatches = new GetAllBatches(AddNewMaterial.this);
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
            if (result.equals("{\"SubjectList\":null}")) {
                String[] subject_array = {"Select Subject"};
                ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<>(ctx, R.layout.spinner_list_item1, subject_array);
                dataAdapter1.setDropDownViewResource(R.layout.spinner_list_item1);
                subject_spinner.setAdapter(dataAdapter1);
            } else {
                try {
                    subject_array.clear();
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

    @SuppressLint("StaticFieldLeak")
    public class UploadStudyMaterial extends AsyncTask<String, String, String> {
        Context ctx;

        UploadStudyMaterial(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            Toast.makeText(ctx, "Please wait, adding study material", Toast.LENGTH_SHORT).show();
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {

            String schoolId = params[0];
            String str_title = params[1];
            String str_desc = params[2];
            String userSection = params[3];
            String userId = params[4];
            String userType = params[5];
            String subject = params[6];

            try {
                ArrayList<String> fileNames = new ArrayList<>();
                for (int i = 0; i < filepath.size(); i++) {
                    try {
                        SSLContext sslContext = SSLContexts.custom().useTLS().build();
                        SSLConnectionSocketFactory f = new SSLConnectionSocketFactory(
                                sslContext,
                                new String[]{"TLSv1.1", "TLSv1.2"},
                                null,
                                BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
                        HttpClient httpclient = HttpClients.custom().setSSLSocketFactory(f).build();
                        HttpPost httppost = new HttpPost(ADD_STUDY_MATERIAL);
                        MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
                        entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                        FileBody image = new FileBody(filepath.get(i));
                        entityBuilder.addPart("file", image);
                        entityBuilder.addTextBody("schoolId", schoolId);
                        HttpEntity entity = entityBuilder.build();
                        httppost.setEntity(entity);
                        HttpResponse response = httpclient.execute(httppost);
                        HttpEntity httpEntity = response.getEntity();
                        String result = EntityUtils.toString(httpEntity);
                        JSONObject jsonObject = new JSONObject(result);
                        fileNames.add("\"" + jsonObject.getString("imageNm") + "\"");
                        Log.e("file", jsonObject.getString("imageNm"));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                Log.e("file name", fileNames.toString());
                try {
                    SSLContext sslContext = SSLContexts.custom().useTLS().build();
                    SSLConnectionSocketFactory f = new SSLConnectionSocketFactory(
                            sslContext,
                            new String[]{"TLSv1.1", "TLSv1.2"},
                            null,
                            BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
                    HttpClient httpclient = HttpClients.custom().setSSLSocketFactory(f).build();
                    HttpPost httppost = new HttpPost(ADD_STUDY_MATERIAL);
                    MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
                    entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                    entityBuilder.addTextBody("file_array", fileNames.toString().replace(" ", ""));
                    entityBuilder.addTextBody("schoolId", schoolId);
                    entityBuilder.addTextBody("str_title", str_title);
                    entityBuilder.addTextBody("str_desc", str_desc);
                    entityBuilder.addTextBody("userSection", userSection);
                    entityBuilder.addTextBody("userId", userId);
                    entityBuilder.addTextBody("download_permission", strDownload);
                    entityBuilder.addTextBody("userType", userType);
                    entityBuilder.addTextBody("tbl_course_group_id", strCourse);
                    entityBuilder.addTextBody("subject_name", subject);
                    HttpEntity entity = entityBuilder.build();
                    httppost.setEntity(entity);
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity httpEntity = response.getEntity();
                    return EntityUtils.toString(httpEntity);
                } catch (Exception e) {
                    e.printStackTrace();
                }

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
                        Intent i = new Intent(ctx, StudyMaterial.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                        finish();
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                }.start();
            } else {
                Toast.makeText(ctx, "Issue in server", Toast.LENGTH_SHORT).show();
                layout.setVisibility(View.GONE);
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
