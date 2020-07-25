package com.aaptrix.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aaptrix.BuildConfig;
import com.aaptrix.R;
import com.google.android.material.appbar.AppBarLayout;

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

import static com.aaptrix.activitys.SplashScreen.TNC;
import static com.aaptrix.tools.HttpUrl.HELP;
import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static cz.msebera.android.httpclient.conn.ssl.SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER;

public class GetHelp extends AppCompatActivity {

    MediaPlayer mp;
    CardView cardView;
    GifImageView taskStatus;
    RelativeLayout layout;
    EditText userMsg, name, phone;
    Button send;
    Toolbar toolbar;
    AppBarLayout appBarLayout;
    TextView message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_help);
        toolbar = findViewById(R.id.toolbar);
        appBarLayout = findViewById(R.id.appBarLayout);
        setSupportActionBar(toolbar);
        setTitle("Tell Us Your Problem");
        setResult(RESULT_OK);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mp = MediaPlayer.create(this, R.raw.button_click);
        cardView = findViewById(R.id.card_view);
        taskStatus = findViewById(R.id.task_status);
        layout = findViewById(R.id.layout);
        userMsg = findViewById(R.id.user_msg);
        send = findViewById(R.id.btn_send);
        message = findViewById(R.id.msg);
        name = findViewById(R.id.user_name);
        phone = findViewById(R.id.user_phone);

        userMsg.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus)
                hideKeyboard(v);
        });

        name.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus)
                hideKeyboard(v);
        });

        phone.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus)
                hideKeyboard(v);
        });

        layout.setOnClickListener(v -> {

        });

        layout.setOnTouchListener((v, event) -> false);

        SharedPreferences sp = getSharedPreferences(PREFS_NAME, 0);
        String userType = sp.getString("userrType", "");
        String username = sp.getString("userName", "");
        String userphone = sp.getString("userPhone", "");

        send.setOnClickListener(v -> {
            mp.start();
            if (userType.equals("Guest")) {
                if (TextUtils.isEmpty(name.getText().toString())) {
                    name.requestFocus();
                    name.setError("Please Enter Name");
                } else if (TextUtils.isEmpty(phone.getText().toString())) {
                    phone.requestFocus();
                    phone.setError("Please Enter Phone Number");
                } else if (phone.getText().toString().length() != 10) {
                    phone.requestFocus();
                    phone.setError("Please Enter Correct Phone Number");
                } else {
                    layout.setVisibility(View.VISIBLE);
                    layout.bringToFront();
                    SendRequest request = new SendRequest(this);
                    request.execute(userMsg.getText().toString(), name.getText().toString(), phone.getText().toString());
                }
            } else {
                if (TextUtils.isEmpty(userMsg.getText().toString())) {
                    userMsg.requestFocus();
                    userMsg.setError("Please Enter Message");
                } else {
                    layout.setVisibility(View.VISIBLE);
                    layout.bringToFront();
                    SendRequest request = new SendRequest(this);
                    request.execute(userMsg.getText().toString(), username, userphone);
                }
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    public class SendRequest extends AsyncTask<String, String, String> {
        Context ctx;

        SendRequest(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            Toast.makeText(ctx, "Please wait, sending request", Toast.LENGTH_SHORT).show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            SharedPreferences sp = ctx.getSharedPreferences(PREFS_NAME, 0);

            String userMsg = params[0];
            String username = params[1];
            String userphone = params[2];
            String instName = sp.getString("userSchoolName", "");
            String appNm = ctx.getResources().getString(R.string.app_name);
            String sdk = android.os.Build.VERSION.SDK;
            String device = android.os.Build.DEVICE;
            String model = android.os.Build.MODEL;
            String product = android.os.Build.PRODUCT;

            try {
                SSLContext sslContext = SSLContexts.custom().useTLS().build();
                SSLConnectionSocketFactory f = new SSLConnectionSocketFactory(
                        sslContext,
                        new String[]{"TLSv1.1", "TLSv1.2"},
                        null,
                        BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
                HttpClient httpclient = HttpClients.custom().setSSLSocketFactory(f).build();
                HttpPost httppost = new HttpPost(HELP);
                MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
                entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                entityBuilder.addTextBody("user_nm", username);
                entityBuilder.addTextBody("user_mob_no", userphone);
                entityBuilder.addTextBody("message", "[Help] : " +
                        userMsg + " Institute Name : " + instName);
                entityBuilder.addTextBody("institute_nm", instName);
                entityBuilder.addTextBody("app_nm", appNm);
                entityBuilder.addTextBody("app_info", "Version : " + BuildConfig.VERSION_NAME +
                        " Android : " + sdk + " Product : " + product + " Device : " + device + " Model : " + model);
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
            Log.e("res", result);
            if (result.contains("submitted")) {
                cardView.setVisibility(View.VISIBLE);
                new CountDownTimer(4000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        onBackPressed();
                    }
                }.start();
            } else {
                Toast.makeText(ctx, "Issue in server", Toast.LENGTH_SHORT).show();
                layout.setVisibility(View.GONE);
            }
            super.onPostExecute(result);
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

    @Override
    public void onBackPressed() {
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
