package com.aaptrix.activitys.student;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.Html;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aaptrix.R;
import com.aaptrix.adaptor.CommentsAdapter;
import com.aaptrix.databeans.CommentsData;
import com.aaptrix.youtubeview.core.player.YouTubePlayer;
import com.aaptrix.youtubeview.core.player.listeners.AbstractYouTubePlayerListener;
import com.aaptrix.youtubeview.core.player.listeners.YouTubePlayerFullScreenListener;
import com.aaptrix.youtubeview.core.player.views.YouTubePlayerView;
import com.google.android.material.appbar.AppBarLayout;

import org.apache.commons.lang.StringEscapeUtils;
import org.json.JSONArray;
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
import java.util.ArrayList;
import java.util.Collections;
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
import cz.msebera.android.httpclient.impl.client.HttpClients;
import cz.msebera.android.httpclient.util.EntityUtils;

import static com.aaptrix.tools.HttpUrl.ADD_COMMENTS;
import static com.aaptrix.tools.HttpUrl.GET_COMMENTS;
import static com.aaptrix.tools.HttpUrl.REMOVE_LIVE;
import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREFS_RW;
import static com.aaptrix.tools.SPClass.PREF_COLOR;
import static cz.msebera.android.httpclient.conn.ssl.SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER;

public class PlayLiveStream extends AppCompatActivity {

    String strTitle, url, id, cmntStatus, strDesc;
    TextView title, desc, noComment;
    AppBarLayout appBarLayout;
    String selToolColor, selStatusColor, selTextColor1, userrType, userSchoolId, rollNo, userId;
    TextView tool_title;
    ImageButton delete, disableComment;
    boolean fullscr = false;
    TextView watermark_yt;
    CountDownTimer timer = null;
    LinearLayout notice;
    ImageView dismiss;
    YouTubePlayerView youTubeView;
    ListView listView;
    EditText comments;
    TextView disable;
    ImageView sendComment;
    String strStartTime;
    ArrayList<CommentsData> commentArray = new ArrayList<>();
    ArrayList<String> cmnt_id = new ArrayList<>();
    long time = 10000;
    CommentsAdapter adapter;
    FrameLayout yt_frame;
    YouTubePlayer ytPlayer;
    RelativeLayout yt_layout;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.activity_play_live_stream);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        appBarLayout = findViewById(R.id.appBarLayout);
        tool_title = findViewById(R.id.tool_title);
        notice = findViewById(R.id.notice);
        dismiss = findViewById(R.id.dismiss);
        watermark_yt = findViewById(R.id.watermark_yt);
        listView = findViewById(R.id.comments);
        comments = findViewById(R.id.input_comment);
        sendComment = findViewById(R.id.send_comment);
        title = findViewById(R.id.video);
        noComment = findViewById(R.id.no_comment);
        desc = findViewById(R.id.video_desc);
        disable = findViewById(R.id.disable);
        yt_frame = findViewById(R.id.frame_layout);
        disableComment = findViewById(R.id.disable_comment);
        yt_layout = findViewById(R.id.relative);
        watermark_yt.bringToFront();
        notice.bringToFront();

        dismiss.setOnClickListener(v -> notice.setVisibility(View.GONE));

        youTubeView = findViewById(R.id.youtube_view);
        getLifecycle().addObserver(youTubeView);
        delete = findViewById(R.id.delete_video);

        strTitle = getIntent().getStringExtra("title");
        url = getIntent().getStringExtra("url");
        id = getIntent().getStringExtra("id");
        cmntStatus = getIntent().getStringExtra("comments");
        strDesc = getIntent().getStringExtra("desc");
        strStartTime = getIntent().getStringExtra("date");

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        userrType = settings.getString("userrType", "");
        userSchoolId = settings.getString("userSchoolId", "");
        rollNo = settings.getString("unique_id", "");
        userId = settings.getString("userID", "");

        comments.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus)
                hideKeyboard(v);
        });

        adapter = new CommentsAdapter(this, R.layout.messages_layout_of_user, commentArray);
        listView.setAdapter(adapter);

        if (comments.equals("0")) {
            disableComment.setImageResource(R.drawable.comment_off);
            title.setVisibility(View.VISIBLE);
            desc.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
            comments.setVisibility(View.GONE);
            sendComment.setVisibility(View.GONE);
            if (!strDesc.equals("null")) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    desc.setText(Html.fromHtml(strDesc, Html.FROM_HTML_MODE_COMPACT));
                } else {
                    desc.setText(Html.fromHtml(strDesc));
                }
            } else {
                desc.setVisibility(View.GONE);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                title.setText(Html.fromHtml(strTitle, Html.FROM_HTML_MODE_COMPACT));
            } else {
                title.setText(Html.fromHtml(strTitle));
            }
            time = 60000;
            commentTimer();
        } else {
            GetComments comments = new GetComments(this);
            comments.execute(userSchoolId, id, strStartTime, "1");
        }

        if (userrType.equals("Guest")) {
            rollNo = getResources().getString(R.string.app_name);
        }

        disableComment.setOnClickListener(v -> {
            if (cmntStatus.equals("1")) {
                disableComment.setImageResource(R.drawable.comment_off);
                RemoveVideo removeVideo = new RemoveVideo(this, "1", "0");
                removeVideo.execute(userId, id);
            } else {
                disableComment.setImageResource(R.drawable.comment);
                RemoveVideo removeVideo = new RemoveVideo(this, "1", "1");
                removeVideo.execute(userId, id);
            }
        });

        if (userrType.equals("Admin")) {
            delete.setVisibility(View.VISIBLE);
            disableComment.setVisibility(View.VISIBLE);
        } else if (userrType.equals("Teacher")) {
            disableComment.setVisibility(View.GONE);
            SharedPreferences sp = getSharedPreferences(PREFS_RW, 0);
            if (sp.getString("Study Videos", "").equals("")) {
                delete.setVisibility(View.VISIBLE);
            }
        } else {
            disableComment.setVisibility(View.GONE);
            delete.setVisibility(View.GONE);
        }

        sendComment.setOnClickListener(v -> {
            if (TextUtils.isEmpty(comments.getText().toString())) {
                Toast.makeText(this, "Please enter comment", Toast.LENGTH_SHORT).show();
            } else {
                hideKeyboard(v);
                sendComment.setEnabled(false);
                SendComments sendComments = new SendComments(this);
                sendComments.execute(id, comments.getText().toString(), userSchoolId, userId, strStartTime);
            }
        });

        delete.setOnClickListener(v -> {
            AlertDialog.Builder alert = new AlertDialog.Builder(this, R.style.DialogTheme);
            alert.setTitle("Are you sure you want to stop streaming").setPositiveButton("Yes", (dialog, which) -> {
                RemoveVideo removeVideo = new RemoveVideo(this, "0", "0");
                removeVideo.execute(userSchoolId, id);
            }).setNegativeButton("No", null);
            AlertDialog alertDialog = alert.create();
            alertDialog.setCancelable(false);
            alertDialog.show();
            Button theButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
            Button theButton1 = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            theButton.setTextColor(Color.parseColor(selToolColor));
            theButton1.setTextColor(Color.parseColor(selToolColor));
        });

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
        tool_title.setText(strTitle);

        watermark_yt.setText(rollNo);
        setTimerYT();
        String videoKey =videoId(url);
        youTubeView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                super.onReady(youTubePlayer);
                ytPlayer = youTubePlayer;
                youTubePlayer.loadVideo(videoKey, 0);
                youTubeView.addFullScreenListener(new YouTubePlayerFullScreenListener() {
                    @Override
                    public void onYouTubePlayerEnterFullScreen() {
                        fullscr = true;
                        View decorView = getWindow().getDecorView();
                        hideSystemUi(decorView);
                    }

                    @Override
                    public void onYouTubePlayerExitFullScreen() {
                        fullscr = false;
                        View decorView = getWindow().getDecorView();
                        showSystemUi(decorView);
                    }
                });


            }
        });
    }

    private String videoId(String url) {
        int index = url.indexOf("v=");
        String id = url.substring(index+2, index+13);
        if (id.equals("ttps://yout")) {
            id = url.split("/")[3];
        }
        return id;
    }

    private void hideSystemUi(View mDecorView) {
        getSupportActionBar().hide();
        mDecorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        appBarLayout.getRootView().setFitsSystemWindows(false);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        yt_frame.setLayoutParams(params);
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private void showSystemUi(View mDecorView) {
        getSupportActionBar().show();
        mDecorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        appBarLayout.getRootView().setFitsSystemWindows(true);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, -1);
        params.height = (int) getResources().getDimension(R.dimen._200sdp);
        yt_frame.setLayoutParams(params);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(0,40,0,0);
        layoutParams.height = (int) getResources().getDimension(R.dimen._200sdp);
        youTubeView.setLayoutParams(layoutParams);
    }

    @Override
    public void onDestroy() {
        if (timer != null) {
            timer.cancel();
        }
        youTubeView.release();
        super.onDestroy();
    }

    private void setTimerYT() {
        timer = new CountDownTimer(15000, 15000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                int width = yt_layout.getWidth();
                int height = yt_layout.getHeight();
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-2, -2);
                Random random = new Random();
                int top = random.nextInt(height - 70);
                int left = random.nextInt(width - 150);
                params.setMargins(left, top, 0, 0);
                watermark_yt.setLayoutParams(params);
                int color = Color.argb(200, random.nextInt(256), random.nextInt(256), random.nextInt(256));
                watermark_yt.setTextColor(color);
                watermark_yt.bringToFront();
                start();
            }
        }.start();
    }

    @SuppressLint("StaticFieldLeak")
    public class GetComments extends AsyncTask<String, String, String> {
        Context ctx;
        String status;

        GetComments(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            sendComment.setEnabled(false);
        }

        @Override
        protected String doInBackground(String... params) {

            String schoolId = params[0];
            String video_id = params[1];
            String startTime = params[2];
            status = params[3];

            String data;

            try {

                URL url = new URL(GET_COMMENTS);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                data = URLEncoder.encode("tbl_school_id", "UTF-8") + "=" + URLEncoder.encode(schoolId, "UTF-8") + "&" +
                        URLEncoder.encode("video_id", "UTF-8") + "=" + URLEncoder.encode(video_id, "UTF-8") + "&" +
                        URLEncoder.encode("start_time", "UTF-8") + "=" + URLEncoder.encode(startTime, "UTF-8") + "&" +
                        URLEncoder.encode("userStreamingStatus", "UTF-8") + "=" + URLEncoder.encode(status, "UTF-8");
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
            try {
                JSONObject jsonRootObject = new JSONObject(result);
                strStartTime = jsonRootObject.getString("lastCommentTime");
                if (jsonRootObject.getString("streaming_on_off").equals("0")) {
                    new AlertDialog.Builder(ctx)
                            .setMessage("Live streaming has ended for this video.")
                            .setPositiveButton("Ok", (dialog, which) -> onBackPressed())
                            .setCancelable(false)
                            .show();
                } else if (jsonRootObject.getString("comments_enable_disable").equals("0")) {
                    cmntStatus = "0";
                    time = 60000;
                    title.setVisibility(View.VISIBLE);
                    desc.setVisibility(View.VISIBLE);
                    disable.setVisibility(View.VISIBLE);
                    listView.setVisibility(View.GONE);
                    comments.setVisibility(View.GONE);
                    sendComment.setVisibility(View.GONE);
                    if (!strDesc.equals("null")) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            desc.setText(Html.fromHtml(strDesc, Html.FROM_HTML_MODE_COMPACT));
                        } else {
                            desc.setText(Html.fromHtml(strDesc));
                        }
                    } else {
                        desc.setVisibility(View.GONE);
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        title.setText(Html.fromHtml(strTitle, Html.FROM_HTML_MODE_COMPACT));
                    } else {
                        title.setText(Html.fromHtml(strTitle));
                    }
                } else if (jsonRootObject.getString("comments_enable_disable").equals("1")) {
                    cmntStatus = "1";
                    time = 10000;
                    title.setVisibility(View.GONE);
                    desc.setVisibility(View.GONE);
                    disable.setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);
                    comments.setVisibility(View.VISIBLE);
                    sendComment.setVisibility(View.VISIBLE);
                }
                if (!result.contains("\"result\":null")) {
                    noComment.setVisibility(View.GONE);
                    JSONArray jsonArray = jsonRootObject.getJSONArray("result");
                    for (int j = 0; j < jsonArray.length(); j++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(j);
                        if (!cmnt_id.contains(jsonObject.getString("id"))) {
                            CommentsData data = new CommentsData();
                            data.setComment(jsonObject.getString("comments"));
                            Log.e("comment", jsonObject.getString("comments"));
                            data.setDate(jsonObject.getString("entry_dt"));
                            data.setName(jsonObject.getString("tbl_users_name"));
                            cmnt_id.add(jsonObject.getString("id"));
                            commentArray.add(data);
                        }
                    }
                    if (status.equals("1")) {
                        Collections.reverse(commentArray);
                    }
                }
                listItems();
            } catch (Exception e) {
                e.printStackTrace();
            }

            super.onPostExecute(result);
        }
    }

    private void listItems() {
        sendComment.setEnabled(true);
        if (commentArray.size() == 0) {
            noComment.setVisibility(View.VISIBLE);
        }
        adapter.notifyDataSetChanged();
        commentTimer();
    }

    private void commentTimer() {
        new Handler().postDelayed(() -> {
            GetComments comments = new GetComments(PlayLiveStream.this);
            comments.execute(userSchoolId, id, strStartTime, "0");
        }, 10000);
    }

    @SuppressLint("StaticFieldLeak")
    class SendComments extends AsyncTask<String, String, String> {

        Context context;

        SendComments(Context context) {
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            sendComment.setEnabled(false);
            Toast.makeText(context, "Please wait", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected String doInBackground(String... params) {
            String video_id = params[0];
            String comment = params[1];
            String school_id = params[2];
            String userId = params[3];
            String startTime = params[4];

            try {
                SSLContext sslContext = SSLContexts.custom().useTLS().build();
                SSLConnectionSocketFactory f = new SSLConnectionSocketFactory(
                        sslContext,
                        new String[]{"TLSv1.1", "TLSv1.2"},
                        null,
                        BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
                HttpClient httpclient = HttpClients.custom().setSSLSocketFactory(f).build();
                HttpPost httppost = new HttpPost(ADD_COMMENTS);
                MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
                entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                entityBuilder.addTextBody("video_id", video_id);
                entityBuilder.addTextBody("tbl_users_id", userId);
                entityBuilder.addTextBody("tbl_school_id", school_id);
                String cmnt = StringEscapeUtils.escapeHtml(comment);
                entityBuilder.addTextBody("comments", cmnt);
                entityBuilder.addTextBody("start_time", startTime);
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
            try {
                comments.getText().clear();
                sendComment.setEnabled(true);
                JSONObject jsonRootObject = new JSONObject(result);
                if (jsonRootObject.getString("msg").contains("submitted")) {
                    strStartTime = jsonRootObject.getString("lastCommentTime");
                    GetComments comments = new GetComments(PlayLiveStream.this);
                    comments.execute(userSchoolId, id, strStartTime, "0");
                } else {
                    Toast.makeText(context, "Error", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
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
    protected void onPause() {
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
        if (fullscr) {
            fullscr = false;
            youTubeView.exitFullScreen();
        } else {
            finish();
            super.onBackPressed();
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class RemoveVideo extends AsyncTask<String, String, String> {
        Context ctx;
        String stream, comment;

        RemoveVideo(Context ctx, String stream, String comment) {
            this.ctx = ctx;
            this.stream = stream;
            this.comment = comment;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String userId = params[0];
            String videoId = params[1];
            String data;

            try {
                URL url = new URL(REMOVE_LIVE);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                data = URLEncoder.encode("tbl_school_id", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8") + "&" +
                        URLEncoder.encode("video_id", "UTF-8") + "=" + URLEncoder.encode(videoId, "UTF-8") + "&" +
                        URLEncoder.encode("streaming_on_off", "UTF-8") + "=" + URLEncoder.encode(stream, "UTF-8") + "&" +
                        URLEncoder.encode("comments_enable_disable", "UTF-8") + "=" + URLEncoder.encode(comment, "UTF-8");
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
            if (!result.isEmpty()) {
                if (stream.equals("0")) {
                    finish();
                    Toast.makeText(ctx, "Stream Ended", Toast.LENGTH_SHORT).show();
                } else if (comment.equals("0")) {
                    Toast.makeText(ctx, "Comments disabled", Toast.LENGTH_SHORT).show();
                    disableComment.setImageResource(R.drawable.comment_off);
                    cmntStatus = "0";
                    time = 60000;
                    title.setVisibility(View.VISIBLE);
                    desc.setVisibility(View.VISIBLE);
                    disable.setVisibility(View.VISIBLE);
                    listView.setVisibility(View.GONE);
                    comments.setVisibility(View.GONE);
                    sendComment.setVisibility(View.GONE);
                    if (!strDesc.equals("null")) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            desc.setText(Html.fromHtml(strDesc, Html.FROM_HTML_MODE_COMPACT));
                        } else {
                            desc.setText(Html.fromHtml(strDesc));
                        }
                    } else {
                        desc.setVisibility(View.GONE);
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        title.setText(Html.fromHtml(strTitle, Html.FROM_HTML_MODE_COMPACT));
                    } else {
                        title.setText(Html.fromHtml(strTitle));
                    }
                } else if (comment.equals("1")) {
                    Toast.makeText(ctx, "Comments enabled", Toast.LENGTH_SHORT).show();
                    disableComment.setImageResource(R.drawable.comment);
                    cmntStatus = "1";
                    time = 10000;
                    title.setVisibility(View.GONE);
                    desc.setVisibility(View.GONE);
                    disable.setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);
                    comments.setVisibility(View.VISIBLE);
                    sendComment.setVisibility(View.VISIBLE);
                }
            } else {
                if (comment.equals("0")) {
                    disableComment.setImageResource(R.drawable.comment);
                    cmntStatus = "1";
                    time = 10000;
                    title.setVisibility(View.GONE);
                    desc.setVisibility(View.GONE);
                    disable.setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);
                    comments.setVisibility(View.VISIBLE);
                    sendComment.setVisibility(View.VISIBLE);
                } else if (comment.equals("1")) {
                    disableComment.setImageResource(R.drawable.comment_off);
                    cmntStatus = "0";
                    time = 60000;
                    title.setVisibility(View.VISIBLE);
                    desc.setVisibility(View.VISIBLE);
                    disable.setVisibility(View.VISIBLE);
                    listView.setVisibility(View.GONE);
                    comments.setVisibility(View.GONE);
                    sendComment.setVisibility(View.GONE);
                    if (!strDesc.equals("null")) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            desc.setText(Html.fromHtml(strDesc, Html.FROM_HTML_MODE_COMPACT));
                        } else {
                            desc.setText(Html.fromHtml(strDesc));
                        }
                    } else {
                        desc.setVisibility(View.GONE);
                    }

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        title.setText(Html.fromHtml(strTitle, Html.FROM_HTML_MODE_COMPACT));
                    } else {
                        title.setText(Html.fromHtml(strTitle));
                    }
                }
                Toast.makeText(ctx, "Some Error", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
