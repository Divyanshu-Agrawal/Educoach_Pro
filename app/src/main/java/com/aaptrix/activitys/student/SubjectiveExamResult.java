package com.aaptrix.activitys.student;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.aaptrix.R;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.material.appbar.AppBarLayout;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.Random;

import static com.aaptrix.activitys.SplashScreen.SCHOOL_NAME;
import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREF_COLOR;

public class SubjectiveExamResult extends AppCompatActivity {

    AppBarLayout appBarLayout;
    TextView tool_title;
    TextView watermark;
    PDFView pdfView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subjective_exam_result);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("");
        setResult(RESULT_OK);
        appBarLayout = findViewById(R.id.appBarLayout);
        watermark = findViewById(R.id.watermark);
        tool_title = findViewById(R.id.tool_title);
        pdfView = findViewById(R.id.ans_pdf);

        tool_title.setText(getIntent().getStringExtra("examName"));

        String strpdf = getIntent().getStringExtra("pdf");
        String id = getIntent().getStringExtra("id");

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        String rollNo = SCHOOL_NAME + "\n" + settings.getString("userName", "") + ", " + settings.getString("userPhone", "");

        if (settings.getString("userrType", "").equals("Guest")) {
            rollNo = getResources().getString(R.string.app_name);
        }

        String url = settings.getString("imageUrl", "") + settings.getString("userSchoolId", "") + "/subjectiveExam/" + strpdf;
        watermark.setText(rollNo);
        watermark.bringToFront();
        setTimer();

        new Thread(() -> {
            try {
                URL u = new URL(url);
                u.openConnection();
                DataInputStream stream = new DataInputStream(u.openStream());
                pdfView.fromStream(stream).load();
            } catch (IOException e) {
                // swallow a 404
            }
        }).start();

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