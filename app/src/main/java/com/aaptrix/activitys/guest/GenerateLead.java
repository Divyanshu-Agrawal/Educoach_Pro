package com.aaptrix.activitys.guest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.Selection;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aaptrix.R;
import com.aaptrix.databeans.QuestionData;
import com.google.android.material.appbar.AppBarLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
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
import cz.msebera.android.httpclient.impl.client.HttpClients;
import cz.msebera.android.httpclient.util.EntityUtils;
import pl.droidsonroids.gif.GifImageView;

import static com.aaptrix.activitys.SplashScreen.SCHOOL_ID;
import static com.aaptrix.activitys.SplashScreen.SCHOOL_NAME;
import static com.aaptrix.activitys.SplashScreen.SENDER_ID;
import static com.aaptrix.tools.HttpUrl.LEAD_GENERATION;
import static com.aaptrix.tools.SPClass.PREF_COLOR;
import static cz.msebera.android.httpclient.conn.ssl.SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER;

public class GenerateLead extends AppCompatActivity {

    RelativeLayout initial_layout, otp_layout;
    Button proceed_btn, submit_btn;
    EditText user_name, user_phone, user_otp;
    ProgressBar progressBar;
    CardView cardView;
    GifImageView taskStatus;
    RelativeLayout layout;
    AppBarLayout appBarLayout;
    TextView tool_title;
    String examId, examName, course, totalQues, ansArray, quesArray;
    int correct = 0, wrong = 0, notAttempt;
    ArrayList<QuestionData> arrayList = new ArrayList<>();

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_lead);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setResult(RESULT_OK);
        appBarLayout = findViewById(R.id.appBarLayout);
        tool_title = findViewById(R.id.tool_title);
        cardView = findViewById(R.id.card_view);
        taskStatus = findViewById(R.id.task_status);
        layout = findViewById(R.id.layout);
        progressBar = findViewById(R.id.loader);
        initial_layout = findViewById(R.id.initial_layout);
        otp_layout = findViewById(R.id.otp_layout);
        proceed_btn = findViewById(R.id.btn_proceed);
        submit_btn = findViewById(R.id.btn_submit);
        user_otp = findViewById(R.id.verify_otp);
        user_name = findViewById(R.id.user_name);
        user_phone = findViewById(R.id.user_phone);

        layout.setOnClickListener(v -> {

        });

        layout.setOnTouchListener((v, event) -> false);

        user_phone.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus)
                hideKeyboard(v);
        });

        user_name.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus)
                hideKeyboard(v);
        });

        user_otp.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus)
                hideKeyboard(v);
        });

        examId = getIntent().getStringExtra("exam_id");
        examName = getIntent().getStringExtra("exam_name");
        course = getIntent().getStringExtra("course");
        totalQues = getIntent().getStringExtra("total_ques");
        ansArray = getIntent().getStringExtra("array");
        quesArray = getIntent().getStringExtra("ques_array");

        try {
            JSONArray jsonArray = new JSONArray(ansArray);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                QuestionData data = new QuestionData();
                data.setCorrectOption(jsonObject.getString("correctOption"));
                data.setTbl_user_answer(jsonObject.getString("tbl_user_answer"));
                data.setTbl_que_id(jsonObject.getString("tbl_que_id"));
                arrayList.add(data);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        for (int i = 0; i < arrayList.size(); i++) {
            if (arrayList.get(i).getTbl_user_answer().toLowerCase().equals(arrayList.get(i).getCorrectOption().toLowerCase())) {
                correct++;
            } else {
                wrong++;
            }
        }

        notAttempt = Integer.parseInt(totalQues) - arrayList.size();

        user_phone.setText("+91");
        user_phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().startsWith("+91")) {
                    user_phone.setText("+91");
                    Selection.setSelection(user_phone.getText(), user_phone.getText().length());
                }
            }
        });

        proceed_btn.setOnClickListener(v -> {
            if (TextUtils.isEmpty(user_name.getText().toString())) {
                user_name.setError("Please enter name");
                user_name.requestFocus();
            } else if (TextUtils.isEmpty(user_phone.getText().toString())) {
                user_phone.setError("Please enter phone number");
                user_phone.requestFocus();
            } else if (user_phone.getText().length() != 13) {
                user_phone.setError("Please enter correct phone number");
                user_phone.requestFocus();
            } else {
                progressBar.setVisibility(View.VISIBLE);
                sendOtp(user_phone.getText().toString().replace("+91", ""));
            }
        });

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

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        assert inputMethodManager != null;
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public void sendOtp(String contact) {
        if (!TextUtils.isEmpty(contact) && (contact.length() == 10)) {
            int randomNum = getRandomNumberInRange();
            Toast.makeText(getApplicationContext(), "Code sent to the number", Toast.LENGTH_SHORT).show();
            smsBackground mGetImagesTask = new smsBackground(this);
            mGetImagesTask.execute(contact, randomNum + "");
        } else {
            Toast.makeText(this, "Please Enter valid Number", Toast.LENGTH_SHORT).show();
        }
    }

    private int getRandomNumberInRange() {
        Random r = new Random();
        return r.nextInt((999998 - 100001) + 1) + 100001;
    }

    @SuppressLint("StaticFieldLeak")
    private class smsBackground extends AsyncTask<String, Void, String> {
        Context ctx;
        String mobileNo;
        String senderId;
        String otp;

        smsBackground(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected String doInBackground(String... params) {
            String mobile = params[0].replace("+91", "");
            String randomno = params[1];
            otp = randomno;
            mobileNo = mobile;
            sms_Send(mobile, Integer.parseInt(randomno));
            return null;
        }

        private void sms_Send(String mobileNo, int randomNum) {

            String authkey = getResources().getString(R.string.sms_Auth_key);
            senderId = SENDER_ID;
            String message = randomNum + " is your verification code for " + SCHOOL_NAME + " | Smart App for Smart Coaching.";
            String route = "4";

            URLConnection myURLConnection;
            URL myURL;
            BufferedReader reader;
            String encoded_message = URLEncoder.encode(message);
            String mainUrl = "http://msg.sjainventures.com/api/sendhttp.php?";

            StringBuilder sbPostData = new StringBuilder(mainUrl);
            sbPostData.append("authkey=").append(authkey);
            sbPostData.append("&mobiles=").append(mobileNo);
            sbPostData.append("&message=").append(encoded_message);
            sbPostData.append("&route=").append(route);
            sbPostData.append("&sender=").append(senderId);

            try {
                mainUrl = sbPostData.toString();
                myURL = new URL(mainUrl);
                myURLConnection = myURL.openConnection();
                myURLConnection.connect();
                reader = new BufferedReader(new InputStreamReader(myURLConnection.getInputStream()));

                String response;
                while ((response = reader.readLine()) != null)
                    Log.e("RESPONSE", "" + response);
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            progressBar.setVisibility(View.GONE);
            initial_layout.setVisibility(View.GONE);
            otp_layout.setVisibility(View.VISIBLE);
            verifyOtp(otp);
        }
    }

    private void verifyOtp(String otp) {
        submit_btn.setOnClickListener(v -> {
            if (TextUtils.isEmpty(user_otp.getText().toString())) {
                user_otp.requestFocus();
                user_otp.setError("Please enter otp");
            } else if (!TextUtils.isDigitsOnly(user_otp.getText().toString())) {
                user_otp.requestFocus();
                user_otp.setError("Please enter correct otp");
            } else if (!user_otp.getText().toString().equals(otp)) {
                Toast.makeText(this, "OTP does not match", Toast.LENGTH_SHORT).show();
            } else if (user_otp.getText().toString().equals(otp)) {
                layout.setVisibility(View.VISIBLE);
                layout.bringToFront();
                SendRequest sendRequest = new SendRequest(this);
                sendRequest.execute(user_name.getText().toString(), user_phone.getText().toString().replace("+91", ""));
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

            String userName = params[0];
            String phone = params[1];

            try {
                SSLContext sslContext = SSLContexts.custom().useTLS().build();
                SSLConnectionSocketFactory f = new SSLConnectionSocketFactory(
                        sslContext,
                        new String[]{"TLSv1.1", "TLSv1.2"},
                        null,
                        BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
                HttpClient httpclient = HttpClients.custom().setSSLSocketFactory(f).build();
                HttpPost httppost = new HttpPost(LEAD_GENERATION);
                MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
                entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                entityBuilder.addTextBody("tbl_school_id", SCHOOL_ID);
                entityBuilder.addTextBody("name", userName);
                entityBuilder.addTextBody("mobno", phone);
                entityBuilder.addTextBody("tbl_exam_id", examId);
                entityBuilder.addTextBody("course", course);
                entityBuilder.addTextBody("total_questions", totalQues);
                entityBuilder.addTextBody("attempt_questions", correct + "");
                entityBuilder.addTextBody("wrong_questions", wrong + "");
                entityBuilder.addTextBody("notattempt_questions", notAttempt + "");
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
            Log.e("ADDED", "" + result);
            if (result.contains("submitted")) {
                cardView.setVisibility(View.VISIBLE);
                new CountDownTimer(4000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        SharedPreferences sp = getSharedPreferences("lead_prefs", 0);
                        sp.edit().putBoolean("status", true).apply();
                        Intent i = new Intent(ctx, GuestExamReport.class);
                        i.putExtra("examName", examName);
                        i.putExtra("correct", correct + "");
                        i.putExtra("wrong", wrong + "");
                        i.putExtra("not_attempted", notAttempt + "");
                        i.putExtra("total_ques", totalQues);
                        i.putExtra("ques_array", quesArray);
                        i.putExtra("ans_array", ansArray);
                        startActivity(i);
                        finish();
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                }.start();
            } else {
                layout.setVisibility(View.GONE);
                Toast.makeText(ctx, "Server Issue", Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(result);
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}