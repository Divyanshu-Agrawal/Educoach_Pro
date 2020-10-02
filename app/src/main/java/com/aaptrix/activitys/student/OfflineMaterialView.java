package com.aaptrix.activitys.student;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aaptrix.R;
import com.github.barteksc.pdfviewer.PDFView;
import com.github.chrisbanes.photoview.PhotoView;
import com.google.android.material.appbar.AppBarLayout;
import com.squareup.picasso.Picasso;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import static com.aaptrix.activitys.SplashScreen.SCHOOL_NAME;
import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREF_COLOR;

public class OfflineMaterialView extends AppCompatActivity {

    AppBarLayout appBarLayout;
    String fileName, strSubject;
    String selToolColor, selStatusColor, selTextColor1, userrType, userSchoolId, rollNo, userId;
    TextView tool_title;
    File outPutFile;
    private TextView watermark;
    private LinearLayout notice;
    private PDFView pdfView;
    PhotoView photoView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_material_view);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        appBarLayout = findViewById(R.id.appBarLayout);
        tool_title = findViewById(R.id.tool_title);

        fileName = getIntent().getStringExtra("file");
        strSubject = getIntent().getStringExtra("sub");

        photoView = findViewById(R.id.fullscr_image);
        pdfView = findViewById(R.id.fullscr_pdf);
        watermark = findViewById(R.id.watermark);
        notice = findViewById(R.id.notice);
        ImageView dismiss = findViewById(R.id.dismiss);
        notice.bringToFront();

        dismiss.setOnClickListener(v -> notice.setVisibility(View.GONE));

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        userrType = settings.getString("userrType", "");
        userSchoolId = settings.getString("userSchoolId", "");
        userId = settings.getString("userID", "");

        if (getResources().getString(R.string.watermark).equals("full")) {
            rollNo = SCHOOL_NAME + "\n" + settings.getString("userName", "") + ", " + settings.getString("userPhone", "");
        } else {
            rollNo = settings.getString("unique_id", "");
        }
        SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
        selToolColor = settingsColor.getString("tool", "");
        selStatusColor = settingsColor.getString("status", "");
        selTextColor1 = settingsColor.getString("text1", "");

        watermark.setText(rollNo);
        watermark.bringToFront();

        appBarLayout.setBackgroundColor(Color.parseColor(selToolColor));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(selStatusColor));
        }
        tool_title.setTextColor(Color.parseColor(selTextColor1));
        tool_title.setText(fileName);

        try {
            fileDecrypt();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void fileDecrypt() throws Exception {
        String key = getSharedPreferences(PREFS_NAME, 0).getString("video_key", "aaptrixtechnopvt");
        File file = new File(getExternalFilesDir("Study Material/" + strSubject), fileName);
        int size = (int) file.length();
        byte[] bytes = new byte[size];

        BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
        buf.read(bytes, 0, bytes.length);
        buf.close();

        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        byte[] bKey = key.getBytes(StandardCharsets.UTF_8);
        SecretKeySpec keySpec = new SecretKeySpec(bKey, "AES");
        IvParameterSpec ivSpec = new IvParameterSpec(bKey);
        cipher.init(Cipher.DECRYPT_MODE, keySpec, ivSpec);

        byte[] decrypted = cipher.doFinal(bytes);

        String ext = fileName.substring(fileName.lastIndexOf(".") + 1);
        outPutFile = File.createTempFile(fileName.replace(ext, "").replace(".", ""), "." + ext, getCacheDir());

        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(outPutFile));
        bos.write(decrypted);
        bos.flush();
        bos.close();
        setMaterial();
    }

    private void setMaterial() {
        setTimer();
        String fileExt = fileName.substring(fileName.lastIndexOf(".") + 1);
        if ("pdf".equals(fileExt)) {
            photoView.setVisibility(View.GONE);
            pdfView.setVisibility(View.VISIBLE);
            pdfView.fromFile(outPutFile).load();
        } else {
            Picasso.with(this).load(outPutFile).into(photoView);
            photoView.setVisibility(View.VISIBLE);
            pdfView.setVisibility(View.GONE);
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
                int color = Color.argb(80, random.nextInt(256), random.nextInt(256), random.nextInt(256));
                watermark.setTextColor(color);
                watermark.bringToFront();
                start();
            }
        }.start();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (outPutFile != null && outPutFile.exists()) {
            outPutFile.delete();
        }
        super.onBackPressed();
    }
}