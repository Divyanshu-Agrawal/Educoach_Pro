package com.aaptrix.activitys.student;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aaptrix.R;
import com.aaptrix.tools.FileUtil;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.material.appbar.AppBarLayout;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.Random;

import javax.net.ssl.SSLContext;

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
import pl.droidsonroids.gif.GifImageView;

import static com.aaptrix.activitys.SplashScreen.SCHOOL_NAME;
import static com.aaptrix.tools.HttpUrl.SUBMIT_SUBJECTIVE_EXAM;
import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREF_COLOR;
import static cz.msebera.android.httpclient.conn.ssl.SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER;

public class SubjectiveQuesPaper extends AppCompatActivity {

    String examId, examName, startTime, endTime, strPdf;
    AppBarLayout appBarLayout;
    TextView tool_title;
    CardView cardView;
    GifImageView taskStatus;
    RelativeLayout layout;
    String schoolId, userId, userSection;
    String selToolColor, userType;
    PDFView pdfView;
    TextView watermark;
    Button submit;
    File uploadFile;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subjective_ques_paper);
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
        cardView = findViewById(R.id.card_view);
        taskStatus = findViewById(R.id.task_status);
        layout = findViewById(R.id.layout);
        pdfView = findViewById(R.id.ques_paper);
        submit = findViewById(R.id.submit_exam);
        watermark = findViewById(R.id.watermark);

        layout.setOnClickListener(v -> {

        });

        layout.setOnTouchListener((v, event) -> false);

        examId = getIntent().getStringExtra("examId");
        examName = getIntent().getStringExtra("examName");
        startTime = getIntent().getStringExtra("startTime");
        endTime = getIntent().getStringExtra("endTime");
        strPdf = getIntent().getStringExtra("pdf");

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        schoolId = settings.getString("str_school_id", "");
        userId = settings.getString("userID", "");
        userSection = settings.getString("userSection", "");
        userType = settings.getString("userrType", "");
        String rollNo = SCHOOL_NAME + "\n" + settings.getString("userName", "") + ", " + settings.getString("userPhone", "");

        if (settings.getString("userrType", "").equals("Guest")) {
            rollNo = getResources().getString(R.string.app_name);
        }
        tool_title.setText(examName);

        if (!userType.equals("Student")) {
            submit.setVisibility(View.GONE);
        }

        String url = settings.getString("imageUrl", "") + settings.getString("userSchoolId", "") + "/subjectiveExam/" + strPdf;
        watermark.setText(rollNo);
        watermark.bringToFront();
        setTimer();

        try {
            URL u = new URL(url);
            u.openConnection();
            DataInputStream stream = new DataInputStream(u.openStream());
            pdfView.fromStream(stream).load();
        } catch (IOException e) {
            // swallow a 404
        }

        submit.setOnClickListener(v -> {
            if (isInternetOn()) {
                if (PermissionChecker.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_GRANTED) {
                    Intent intent = new Intent();
                    intent.setType("*/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);
                    startActivityForResult(intent, 1);
                } else {
                    isPermissionGranted();
                }
            }
        });

        SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
        selToolColor = settingsColor.getString("tool", "");
        String selStatusColor = settingsColor.getString("status", "");
        String selTextColor1 = settingsColor.getString("text1", "");

        appBarLayout.setBackgroundColor(Color.parseColor(selToolColor));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(selStatusColor));
        }
        tool_title.setTextColor(Color.parseColor(selTextColor1));
        submit.setBackgroundColor(Color.parseColor(selToolColor));
        submit.setTextColor(Color.parseColor(selTextColor1));
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                try {
                    String file_extn = FileUtil.from(this, data.getData()).toString()
                            .substring(FileUtil.from(this, data.getData()).toString().lastIndexOf(".") + 1);
                    if (file_extn.equals("pdf")) {
                        uploadFile = FileUtil.from(this, data.getData());
                        new AlertDialog.Builder(this)
                                .setMessage("Are you sure you want to upload this file. Once uploaded no changes can be made.")
                                .setPositiveButton("Upload", (dialog, which) -> {
                                    SubmitExam submitExam = new SubmitExam(this);
                                    submitExam.execute(schoolId, examId, userId);
                                })
                                .setNegativeButton("Cancel", null)
                                .show();
                    } else {
                        Toast.makeText(this, "File not in supported format, you can only select pdf or image file", Toast.LENGTH_SHORT).show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
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

    private void setTimer() {
        new CountDownTimer(15000, 15000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                Random random = new Random();
                int color = Color.argb(100, random.nextInt(256), random.nextInt(256), random.nextInt(256));
                watermark.setTextColor(color);
                watermark.bringToFront();
                start();
            }
        }.start();
    }

    public final boolean isInternetOn() {

        ConnectivityManager connec = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        assert connec != null;
        if (Objects.requireNonNull(connec.getNetworkInfo(0)).getState() == android.net.NetworkInfo.State.CONNECTED ||
                Objects.requireNonNull(connec.getNetworkInfo(0)).getState() == android.net.NetworkInfo.State.CONNECTING ||
                Objects.requireNonNull(connec.getNetworkInfo(1)).getState() == android.net.NetworkInfo.State.CONNECTING ||
                Objects.requireNonNull(connec.getNetworkInfo(1)).getState() == android.net.NetworkInfo.State.CONNECTED) {

            return true;
        } else if (
                Objects.requireNonNull(connec.getNetworkInfo(0)).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                        Objects.requireNonNull(connec.getNetworkInfo(1)).getState() == android.net.NetworkInfo.State.DISCONNECTED) {
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

    @SuppressLint("StaticFieldLeak")
    public class SubmitExam extends AsyncTask<String, String, String> {
        Context ctx;

        SubmitExam(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            layout.setVisibility(View.VISIBLE);
            layout.bringToFront();
        }

        @Override
        protected String doInBackground(String... params) {

            String school_id = params[0];
            String examId = params[1];
            String userId = params[2];

            try {
                SSLContext sslContext = SSLContexts.custom().useTLS().build();
                SSLConnectionSocketFactory f = new SSLConnectionSocketFactory(
                        sslContext,
                        new String[]{"TLSv1.1", "TLSv1.2"},
                        null,
                        BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
                HttpClient httpclient = HttpClients.custom().setSSLSocketFactory(f).build();
                HttpPost httppost = new HttpPost(SUBMIT_SUBJECTIVE_EXAM);
                MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
                entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                entityBuilder.addPart("", new FileBody(uploadFile));
                entityBuilder.addTextBody("tbl_school_id", school_id);
                entityBuilder.addTextBody("tbl_subjective_online_exams_id", examId);
                entityBuilder.addTextBody("tbl_users_id", userId);
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
            if (result.contains("\"success\":true")) {
                cardView.setVisibility(View.VISIBLE);
                new CountDownTimer(4000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        startActivity(new Intent(ctx, OnlineExam.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    }
                }.start();

            } else {
                Toast.makeText(ctx, "Error", Toast.LENGTH_SHORT).show();
                layout.setVisibility(View.GONE);
            }
            super.onPostExecute(result);
        }

    }
}