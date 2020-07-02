package com.aaptrix.activitys.student;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Html;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.aaptrix.R;
import com.google.android.material.appbar.AppBarLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREF_COLOR;

public class StartExam extends AppCompatActivity {

    AppBarLayout appBarLayout;
    TextView tool_title;
    TextView startDate, startTime, endDate, endTime, duration;
    TextView timer, instructions, timerTitle;
    Button startExam;
    TextView startD, endD, dur, startT, endT;
    CountDownTimer count;
    String userType;
    String examId, examName, examStartTime, examEndTime, resPublish, strEndDate, strStartDate, strDuration;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_exam);
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
        timer = findViewById(R.id.timer);
        instructions = findViewById(R.id.instructions);
        startExam = findViewById(R.id.start_exam);
        timerTitle = findViewById(R.id.title);
        startDate = findViewById(R.id.exam_start_date);
        endDate = findViewById(R.id.exam_end_date);
        startTime = findViewById(R.id.exam_start_time);
        endTime = findViewById(R.id.exam_end_time);
        duration = findViewById(R.id.exam_duration);
        startD = findViewById(R.id.examstartdate);
        startT = findViewById(R.id.examstarttime);
        endD = findViewById(R.id.examenddate);
        endT = findViewById(R.id.examendtime);
        dur = findViewById(R.id.examduration);

        SharedPreferences sp = getSharedPreferences(PREFS_NAME, 0);
        userType = sp.getString("userrType", "");

        examId = getIntent().getStringExtra("examId");
        examName = getIntent().getStringExtra("examName");
        examStartTime = getIntent().getStringExtra("examStart");
        examEndTime = getIntent().getStringExtra("examEnd");
        resPublish = getIntent().getStringExtra("resPublish");
        String negMarks = getIntent().getStringExtra("negMarks");
        strDuration = getIntent().getStringExtra("duration");
        strEndDate = getIntent().getStringExtra("endDate");
        strStartDate = getIntent().getStringExtra("startDate");

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
        try {
            Date end = sdf.parse(examEndTime);
            Date start = sdf.parse(examStartTime);
            sdf = new SimpleDateFormat("hh:mm aa", Locale.getDefault());
            startTime.setText(sdf.format(start));
            endTime.setText(sdf.format(end));
            sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date startdate = sdf.parse(strStartDate);
            Date enddate = sdf.parse(strEndDate);
            sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            startDate.setText(sdf.format(startdate));
            endDate.setText(sdf.format(enddate));

            String[] dur = strDuration.split(":");
            duration.setText(dur[0] + " Hours " + dur[1] + " Mins");

            StringBuilder ins = new StringBuilder();

            assert negMarks != null;
            if (negMarks.equals("0.00")) {
                ins.append("<b>&#8226;</b> You can use \"<b>Next</b>\" and \"<b>Previous</b>\" buttons to navigate the questions.<br/><br/>" +
                        "<b>&#8226;</b> \"<b>Mark As Review</b>\" button can be used to mark the answer for reviewing later.<br/><br/>" +
                        "<b>&#8226;</b> It is to be noted that the question marked for review will not be counted in marking until submitted.<br/><br/>" +
                        "<b>&#8226;</b> There is no negative marking in this exam.<br/><br/>");
            } else {
                ins.append("<b>&#8226;</b> You can use \"Next\" and \"Previous\" buttons to navigate the questions.<br/><br/>" +
                        "<b>&#8226;</b> \"Mark As Review\" button can be used to mark the answer for reviewing later.<br/><br/>" +
                        "<b>&#8226;</b> It is to be noted that the question marked for review will not be counted in marking until submitted.<br/><br/>" +
                        "<b>&#8226;</b> Negative marking is there, for every wrong question ").append(negMarks).append(" will be deducted.<br/><br/>");
            }

            if (resPublish.equals("1")) {
                ins.append("<b>&#8226;</b> You will be shown result once exam is completed.");
            } else {
                ins.append("<b>&#8226;</b> You will be notified once result is published.");
            }

            instructions.setText(Html.fromHtml(ins.toString()));
        } catch (Exception e) {
            e.printStackTrace();
        }

        tool_title.setText(examName);

        if (userType.equals("Student")) {
            count = new CountDownTimer(1000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    try {
                        Date end = sdf.parse(strEndDate + " " + examEndTime);
                        Date start = sdf.parse(strStartDate + " " + examStartTime);
                        String time = sdf.format(Calendar.getInstance().getTimeInMillis());
                        Date cur = sdf.parse(time);
                        assert end != null;
                        assert start != null;
                        if (end.before(cur)) {
                            startExam.setOnClickListener(v -> Toast.makeText(StartExam.this, "Exam ended.", Toast.LENGTH_SHORT).show());
                        } else if (start.after(cur)) {
                            startExam.setOnClickListener(v -> Toast.makeText(StartExam.this, "Exam not started yet.", Toast.LENGTH_SHORT).show());
                        } else {
                            startActivity();
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFinish() {
                    start();
                }
            }.start();
        } else {
            startActivity();
        }

        SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
        String selToolColor = settingsColor.getString("tool", "");
        String selStatusColor = settingsColor.getString("status", "");
        String selTextColor1 = settingsColor.getString("text1", "");

        appBarLayout.setBackgroundColor(Color.parseColor(selToolColor));
        startExam.setBackgroundColor(Color.parseColor(selToolColor));
        startExam.setTextColor(Color.parseColor(selTextColor1));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(selStatusColor));
        }
        tool_title.setTextColor(Color.parseColor(selTextColor1));
        endT.setTextColor(Color.parseColor(selToolColor));
        endD.setTextColor(Color.parseColor(selToolColor));
        startD.setTextColor(Color.parseColor(selToolColor));
        startT.setTextColor(Color.parseColor(selToolColor));
        dur.setTextColor(Color.parseColor(selToolColor));
    }

    private void startActivity() {
        startExam.setOnClickListener(v -> {
            if (isInternetOn()) {
                Intent intent = new Intent(this, StartOnlineExam.class);
                intent.putExtra("examId", examId);
                intent.putExtra("startTime", examStartTime);
                intent.putExtra("endTime", examEndTime);
                intent.putExtra("examName", examName);
                intent.putExtra("resPublish", resPublish);
                intent.putExtra("duration", strDuration);
                startActivity(intent);
                if (count != null)
                    count.cancel();
            } else {
                Toast.makeText(this, "Please Connect to Internet", Toast.LENGTH_SHORT).show();
            }
        });
    }

    public final boolean isInternetOn() {

        ConnectivityManager connec = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

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
        if (count != null)
            count.cancel();
    }
}
