package com.aaptrix.activitys.student;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.aaptrix.R;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.ui.PlayerControlView;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.material.appbar.AppBarLayout;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Objects;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import static com.aaptrix.activitys.SplashScreen.SCHOOL_NAME;
import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREF_COLOR;

public class OfflineVideosPlay extends AppCompatActivity {

    String fileName, strSubject;
    AppBarLayout appBarLayout;
    String selToolColor, selStatusColor, selTextColor1, userrType, userSchoolId, rollNo, userId, userName;
    TextView tool_title;
    PlayerView player;
    SimpleExoPlayer exoPlayer;
    private boolean playWhenReady = true;
    private int currentWindow = 0;
    private long playbackPosition = 0;
    private boolean mExoPlayerFullscreen = false;
    private ImageView mFullScreenIcon;
    private Dialog mFullScreenDialog;
    TextView watermark, title;
    CountDownTimer timer = null;
    LinearLayout notice;
    ImageView dismiss;
    RelativeLayout exo_layout;
    ProgressBar progressBar;
    File outPutFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_videos_play);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        appBarLayout = findViewById(R.id.appBarLayout);
        tool_title = findViewById(R.id.tool_title);
        title = findViewById(R.id.video);
        player = findViewById(R.id.exo_player);
        watermark = findViewById(R.id.watermark);
        watermark.bringToFront();
        notice = findViewById(R.id.notice);
        dismiss = findViewById(R.id.dismiss);
        exo_layout = findViewById(R.id.rel_layout);
        progressBar = findViewById(R.id.progress_bar);
        notice.bringToFront();

        LoadControl loadControl = new DefaultLoadControl.Builder()
                .setAllocator(new DefaultAllocator(true, 16))
                .setBufferDurationsMs(1500,
                        5000,
                        1500,
                        1500)
                .setTargetBufferBytes(-1)
                .setPrioritizeTimeOverSizeThresholds(true).createDefaultLoadControl();

        exoPlayer = ExoPlayerFactory.newSimpleInstance(this, new DefaultTrackSelector(), loadControl);
        exoPlayer.addListener(new Player.EventListener() {
            @Override
            public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
                if (playbackState == Player.STATE_IDLE || playbackState == Player.STATE_ENDED ||
                        !playWhenReady) {
                    player.setKeepScreenOn(false);
                } else {
                    player.setKeepScreenOn(true);
                }
            }
        });

        dismiss.setOnClickListener(v -> notice.setVisibility(View.GONE));

        player.setPlayer(exoPlayer);

        fileName = getIntent().getStringExtra("file");
        strSubject = getIntent().getStringExtra("sub");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            title.setText(Html.fromHtml(fileName, Html.FROM_HTML_MODE_COMPACT));
        } else {
            title.setText(Html.fromHtml(fileName));
        }

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        userrType = settings.getString("userrType", "");
        userSchoolId = settings.getString("userSchoolId", "");
        userId = settings.getString("userID", "");
        userName = settings.getString("userName", "");

        if (getResources().getString(R.string.watermark).equals("full")) {
            rollNo = SCHOOL_NAME + "\n" + settings.getString("userName", "") + ", " + settings.getString("userPhone", "");
        } else {
            rollNo = settings.getString("unique_id", "");
        }
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
        tool_title.setTextColor(Color.parseColor(selTextColor1));
        tool_title.setText(fileName);

        try {
            File directory = this.getFilesDir();
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File(directory, "offline_videos")));
            String json = in.readObject().toString();
            String hash = computeHash();
            if (json.contains(hash)) {
                fileDecrypt();
            } else {
                new AlertDialog.Builder(this)
                        .setMessage("This video can't be played")
                        .setPositiveButton("Ok", (dialog, which) -> onBackPressed())
                        .setCancelable(false)
                        .show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String computeHash() throws Exception{
        File file = new File(getExternalFilesDir(userName + "/Videos/" + strSubject), fileName);
        byte[] bytes = new byte[(int)file.length()];
        BufferedInputStream buf = new BufferedInputStream(new FileInputStream(file));
        buf.read(bytes, 0, bytes.length);
        buf.close();
        MessageDigest m = MessageDigest.getInstance("MD5");
        byte[] digest = m.digest(bytes);
        return new BigInteger(1, digest).toString(16);
    }

    private void fileDecrypt() throws Exception {
        String key = getSharedPreferences(PREFS_NAME, 0).getString("video_key", "aaptrixtechnopvt");
        File file = new File(getExternalFilesDir(userName + "/Videos/" + strSubject), fileName);
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
        initializePlayer(outPutFile);
    }

    private void initializePlayer(File videoFile) {
        watermark.setText(rollNo);
        watermark.bringToFront();
        setTimer();
        MediaSource mediaSource = buildMediaSource(Uri.fromFile(videoFile));
        exoPlayer.setPlayWhenReady(playWhenReady);
        exoPlayer.seekTo(currentWindow, playbackPosition);
        exoPlayer.prepare(mediaSource, false, false);
        initFullscreenDialog();
        initFullscreenButton();
    }

    private MediaSource buildMediaSource(Uri uri) {
        String playerInfo = Util.getUserAgent(this, getPackageName());
        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(
                this, playerInfo
        );
        return new ExtractorMediaSource.Factory(dataSourceFactory)
                .setExtractorsFactory(new DefaultExtractorsFactory())
                .createMediaSource(uri);
    }

    private void releasePlayer() {
        if (player != null) {
            playWhenReady = exoPlayer.getPlayWhenReady();
            playbackPosition = exoPlayer.getCurrentPosition();
            currentWindow = exoPlayer.getCurrentWindowIndex();
            exoPlayer.release();
            exoPlayer = null;
        }
        if (timer != null) {
            timer.cancel();
        }
    }

    private void initFullscreenDialog() {

        mFullScreenDialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen) {
            public void onBackPressed() {
                if (mExoPlayerFullscreen)
                    closeFullscreenDialog();
                super.onBackPressed();
            }
        };
    }

    @Override
    public void onDestroy() {
        if (timer != null) {
            timer.cancel();
        }
        super.onDestroy();
    }


    @SuppressLint("SourceLockedOrientationActivity")
    private void openFullscreenDialog() {
        ((ViewGroup) player.getParent()).removeView(player);
        ((ViewGroup) watermark.getParent()).removeView(watermark);
        mFullScreenDialog.addContentView(player, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mFullScreenDialog.addContentView(watermark, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        watermark.bringToFront();
        mFullScreenIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_fullscreen_shrink));
        mExoPlayerFullscreen = true;
        mFullScreenDialog.show();
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }


    @SuppressLint("SourceLockedOrientationActivity")
    private void closeFullscreenDialog() {
        ((ViewGroup) player.getParent()).removeView(player);
        ((ViewGroup) watermark.getParent()).removeView(watermark);
        ((FrameLayout) findViewById(R.id.main_media_frame)).addView(player);
        ((FrameLayout) findViewById(R.id.main_media_frame)).addView(watermark);
        watermark.bringToFront();
        mExoPlayerFullscreen = false;
        mFullScreenDialog.dismiss();
        mFullScreenIcon.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_fullscreen_expand));
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }


    private void initFullscreenButton() {
        PlayerControlView controlView = player.findViewById(R.id.exo_controller);
        mFullScreenIcon = controlView.findViewById(R.id.exo_fullscreen_icon);
        FrameLayout mFullScreenButton = controlView.findViewById(R.id.exo_fullscreen_button);
        mFullScreenButton.setOnClickListener(v -> {
            if (!mExoPlayerFullscreen)
                openFullscreenDialog();
            else
                closeFullscreenDialog();
        });
    }

    private void setTimer() {
        timer = new CountDownTimer(15000, 15000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                int width = exo_layout.getWidth();
                int height = exo_layout.getHeight();
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-2, -2);
                Random random = new Random();
                int top = random.nextInt(height - 70);
                int left = random.nextInt(width - 100);
                params.setMargins(left, top, 0, 0);
                watermark.setLayoutParams(params);
                int color = Color.argb(150, random.nextInt(256), random.nextInt(256), random.nextInt(256));
                watermark.setTextColor(color);
                watermark.bringToFront();
                start();
            }
        }.start();
    }

    @Override
    protected void onPause() {
        if (exoPlayer != null) {
            releasePlayer();
            if (mFullScreenDialog != null)
                mFullScreenDialog.dismiss();
        }
        if (timer != null) {
            timer.cancel();
        }
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle outState) {
        super.onRestoreInstanceState(outState);
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
        if (exoPlayer != null) {
            releasePlayer();
            if (mFullScreenDialog != null)
                mFullScreenDialog.dismiss();
            finish();
        }
        if (outPutFile != null && outPutFile.exists()) {
            outPutFile.delete();
        }
        super.onBackPressed();
    }
}