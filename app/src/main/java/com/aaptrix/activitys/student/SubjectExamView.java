package com.aaptrix.activitys.student;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;

import android.Manifest;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

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

public class SubjectExamView extends AppCompatActivity {

    AppBarLayout appBarLayout;
    String selToolColor, selStatusColor, selTextColor1;
    TextView tool_title, watermark;
    ImageButton download;
    String type, url, permission, rollNo;
    PDFView pdfView;
    long downloadID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_exam_view);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        appBarLayout = findViewById(R.id.appBarLayout);
        tool_title = findViewById(R.id.tool_title);
        download = findViewById(R.id.download);
        pdfView = findViewById(R.id.fullscr_pdf);
        watermark = findViewById(R.id.watermark);

        type = getIntent().getStringExtra("type");
        url = getIntent().getStringExtra("url");
        permission = getIntent().getStringExtra("download");

        tool_title.setText(type);

        if (permission.equals("0")) {
            download.setVisibility(View.GONE);
        } else {
            download.setVisibility(View.VISIBLE);
        }

        setPdf(url);

        download.setOnClickListener(v -> {
            if (PermissionChecker.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_GRANTED) {
                downloadFile(url);
            } else {
                isPermissionGranted();
            }
        });

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);

        if (getResources().getString(R.string.watermark).equals("full")) {
            rollNo = SCHOOL_NAME + "\n" + settings.getString("userName", "") + ", " + settings.getString("userPhone", "");
        } else {
            rollNo = settings.getString("unique_id", "");
        }

        watermark.setText(rollNo);
        watermark.bringToFront();

        registerReceiver(onDownloadComplete,new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
        selToolColor = settingsColor.getString("tool", "");
        selStatusColor = settingsColor.getString("status", "");
        selTextColor1 = settingsColor.getString("text1", "");

        appBarLayout.setBackgroundColor(Color.parseColor(selToolColor));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(selStatusColor));
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            download.setImageTintList(ColorStateList.valueOf(Color.parseColor(selTextColor1)));
        }
        tool_title.setTextColor(Color.parseColor(selTextColor1));
        setTimer();
    }

    private void setPdf(String url) {
        new Thread(() -> {
            try {
                URL u = new URL(url);
                u.openConnection();
                DataInputStream stream = new DataInputStream(u.openStream());
                pdfView.fromStream(stream).load();
            } catch(IOException e) {
                // swallow a 404
            }
        }).start();
    }

    private void setTimer() {
        new CountDownTimer(15000, 15000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                Random random = new Random();
                int color = Color.argb(80, random.nextInt(256), random.nextInt(256), random.nextInt(256));
                watermark.setTextColor(color);
                watermark.bringToFront();
                start();
            }
        }.start();
    }

    public void isPermissionGranted() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
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

    private BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (downloadID == id) {
                Toast.makeText(SubjectExamView.this, "Download Completed", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void downloadFile(String url) {
        String path = Environment.DIRECTORY_DOWNLOADS;
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url))
                .setTitle(url)
                .setDescription("Downloading")
                .setMimeType("application/octet-stream")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(path, url);
        request.allowScanningByMediaScanner();
        DownloadManager downloadManager= (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        assert downloadManager != null;
        downloadID = downloadManager.enqueue(request);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(onDownloadComplete);
    }

    @Override
    public void onBackPressed() {
        finish();
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}