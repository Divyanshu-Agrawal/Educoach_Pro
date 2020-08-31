package com.aaptrix.activitys.student;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aaptrix.R;
import com.google.android.material.appbar.AppBarLayout;
import com.squareup.picasso.Picasso;

import org.apache.commons.lang.StringEscapeUtils;

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

import static com.aaptrix.tools.HttpUrl.SEND_FEEDBACK;
import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREF_COLOR;
import static cz.msebera.android.httpclient.conn.ssl.SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER;

public class FeedbackActivity extends AppCompatActivity {

    AppBarLayout appBarLayout;
    TextView tool_title, cube1, cube2, view2, anonText;
    ImageView school_logo;
    String selToolColor, selDrawerColor, selStatusColor, selTextColor1, selTextColor2;
    String userId, userSection, userSchoolLogo, schoolId, strAnon;
    MediaPlayer mp;
    CheckBox anonymous;
    Button submit;
    ProgressBar progressBar;
    EditText feedback;
    RatingBar ratingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_feedback);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        setTitle("");
        setResult(RESULT_OK);
        appBarLayout = findViewById(R.id.appBarLayout);
        tool_title = findViewById(R.id.tool_title);
        anonymous = findViewById(R.id.anon_checkbox);
        feedback = findViewById(R.id.feedback);
        submit = findViewById(R.id.submit_feedback);
        progressBar = findViewById(R.id.progress_bar);
        anonText = findViewById(R.id.anon_text);
        ratingBar = findViewById(R.id.rating);

        SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
        selToolColor = settingsColor.getString("tool", "");
        selDrawerColor = settingsColor.getString("drawer", "");
        selStatusColor = settingsColor.getString("status", "");
        selTextColor1 = settingsColor.getString("text1", "");
        selTextColor2 = settingsColor.getString("text2", "");
        mp = MediaPlayer.create(this, R.raw.button_click);

        ratingBar.setStepSize(1);
        LayerDrawable draw = (LayerDrawable) ratingBar.getProgressDrawable();
        draw.getDrawable(2).setColorFilter(Color.parseColor("#FFD700"), PorterDuff.Mode.SRC_ATOP);

        cube1 = findViewById(R.id.cube1);
        cube2 = findViewById(R.id.cube2);
        view2 = findViewById(R.id.view1);
        school_logo = findViewById(R.id.school_logo);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        userId = settings.getString("userID", "");
        userSection = settings.getString("userSection", "");
        schoolId = settings.getString("str_school_id", "");
        userSchoolLogo = settings.getString("userSchoolLogo", "");
        strAnon = settings.getString("anonymous_feedback", "0");

        if (strAnon.equals("0")) {
            anonymous.setVisibility(View.GONE);
            anonText.setVisibility(View.GONE);
        }

        String url = settings.getString("imageUrl", "") + schoolId + "/other/" + userSchoolLogo;
        Picasso.with(this).load(url).error(R.drawable.dummy).placeholder(R.drawable.dummy).into(school_logo);

        appBarLayout.setBackgroundColor(Color.parseColor(selToolColor));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(selStatusColor));
        }
        tool_title.setTextColor(Color.parseColor(selTextColor1));
        GradientDrawable drawable = (GradientDrawable) cube1.getBackground();
        drawable.setStroke(2, Color.parseColor(selToolColor));
        GradientDrawable drawable1 = (GradientDrawable) cube2.getBackground();
        drawable1.setStroke(2, Color.parseColor(selToolColor));
        view2.setBackgroundColor(Color.parseColor(selToolColor));
        anonymous.setHighlightColor(Color.parseColor(selToolColor));
        submit.setBackgroundColor(Color.parseColor(selToolColor));
        submit.setTextColor(Color.parseColor(selTextColor1));
        GradientDrawable drawable2 = (GradientDrawable) feedback.getBackground();
        drawable2.setStroke(5, Color.parseColor(selToolColor));

        feedback.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                hideKeyboard(v);
            }
        });

        submit.setOnClickListener(v -> {
            if (TextUtils.isEmpty(feedback.getText().toString())) {
                feedback.setError("Please Enter Feedback");
                feedback.requestFocus();
            } else if (ratingBar.getRating() < 1) {
                Toast.makeText(this, "Please select rating", Toast.LENGTH_SHORT).show();
            } else {
                submit.setClickable(false);
                if (anonymous.isChecked()) {
                    SendFeedback sendFeedback = new SendFeedback(this);
                    sendFeedback.execute(schoolId, userId, feedback.getText().toString(), "1", String.valueOf(ratingBar.getRating()));
                } else {
                    SendFeedback sendFeedback = new SendFeedback(this);
                    sendFeedback.execute(schoolId, userId, feedback.getText().toString(), "0", String.valueOf(ratingBar.getRating()));
                }
            }
        });
    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        assert inputMethodManager != null;
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("StaticFieldLeak")
    public class SendFeedback extends AsyncTask<String, String, String> {
        Context ctx;

        SendFeedback(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.bringToFront();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            String schoolId = params[0];
            String userId = params[1];
            String feedback = params[2];
            String anon = params[3];
            String rating = params[4];

            try {
                SSLContext sslContext = SSLContexts.custom().useTLS().build();
                SSLConnectionSocketFactory f = new SSLConnectionSocketFactory(
                        sslContext,
                        new String[]{"TLSv1.1", "TLSv1.2"},
                        null,
                        BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
                HttpClient httpclient = HttpClients.custom().setSSLSocketFactory(f).build();
                HttpPost httppost = new HttpPost(SEND_FEEDBACK);
                MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
                entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                entityBuilder.addTextBody("tbl_school_id", schoolId);
                entityBuilder.addTextBody("tbl_users_id", userId);
                entityBuilder.addTextBody("comment", StringEscapeUtils.escapeHtml(feedback));
                entityBuilder.addTextBody("anonymous", anon);
                entityBuilder.addTextBody("rating", rating);
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
            progressBar.setVisibility(View.GONE);
            submit.setClickable(true);
            if (result.contains("\"submitted\"")) {
                Toast.makeText(FeedbackActivity.this, "Sent Successfully", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(FeedbackActivity.this, "Server issues", Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(result);
        }
    }
}
