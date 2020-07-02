package com.aaptrix.activitys.student;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aaptrix.R;
import com.google.android.material.appbar.AppBarLayout;
import com.squareup.picasso.Picasso;

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
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import static com.aaptrix.tools.HttpUrl.SUBMIT_STUDENT_FEES;
import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREF_COLOR;

public class UpiPayment extends AppCompatActivity {

    AppBarLayout appBarLayout;
    String selToolColor, selStatusColor, selTextColor1;
    TextView tool_title;
    EditText amount;
    Button proceed;
    String upiId, upiUri, strAmount;
    String schoolName, schoolId, userId, userName, txnRef, userSchoolLogo;
    TextView view1;
    ImageView school_logo;
    TextView cube1, cube2;
    ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upi_payment);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        appBarLayout = findViewById(R.id.appBarLayout);
        tool_title = findViewById(R.id.tool_title);
        amount = findViewById(R.id.payment_amount);
        proceed = findViewById(R.id.btn_proceed);
        cube1 = findViewById(R.id.cube1);
        cube2 = findViewById(R.id.cube2);
        view1 = findViewById(R.id.view1);
        school_logo = findViewById(R.id.school_logo);
        progressBar = findViewById(R.id.progress_bar);

        upiId = getIntent().getStringExtra("upi_id");
        amount.requestFocus();

        SharedPreferences sp = getSharedPreferences(PREFS_NAME, 0);
        schoolId = sp.getString("str_school_id", "");
        userId = sp.getString("userID", "");
        userName = sp.getString("userName", "");
        schoolName = sp.getString("userSchoolName", "");
        userSchoolLogo = sp.getString("userSchoolLogo", "");

        String url = sp.getString("imageUrl", "") + schoolId + "/other/" + userSchoolLogo;
        Picasso.with(this).load(url).error(R.drawable.dummy).placeholder(R.drawable.dummy).into(school_logo);

        txnRef = userName.substring(0, 2).toUpperCase() + schoolName.substring(0, 2).toUpperCase() + userId
                + schoolId + System.currentTimeMillis();

        proceed.setOnClickListener(v -> {
            strAmount = amount.getText().toString();
            if (TextUtils.isEmpty(strAmount)) {
                amount.setError("Please Enter Amount");
                amount.requestFocus();
            } else {
                upiUri = "upi://pay?pa=" + upiId + "&pn=" + schoolName + "&tr=" + txnRef + "&am=" +
                        amount.getText().toString() + "&mam=" + amount.getText().toString() +
                        "&cu=INR" + "&tn=Pay to " + schoolName;

                upiUri = upiUri.replace(" ", "%20");
                Intent upiIntent = new Intent();
                upiIntent.setAction(Intent.ACTION_VIEW);
                upiIntent.setData(Uri.parse(upiUri));
                startActivityForResult(Intent.createChooser(upiIntent, "Pay via...."), 1);
            }
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
        proceed.setBackgroundColor(Color.parseColor(selToolColor));
        proceed.setTextColor(Color.parseColor(selTextColor1));
//        GradientDrawable drawable = (GradientDrawable) cube1.getBackground();
//        drawable.setStroke(2, Color.parseColor(selToolColor));
//        GradientDrawable drawable1 = (GradientDrawable) cube2.getBackground();
//        drawable1.setStroke(2, Color.parseColor(selToolColor));
//        view1.setBackgroundColor(Color.parseColor(selToolColor));
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1)
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    if (!data.getStringExtra("txnId").isEmpty()) {
                        if (!data.getStringExtra("Status").toUpperCase().equals("SUBMITTED")) {
                            SubmitDetails submitDetails = new SubmitDetails(this);
                            submitDetails.execute(data.getStringExtra("txnId"), data.getStringExtra("Status")
                                    , data.getStringExtra("responseCode"), data.getStringExtra("txnRef"));
                        } else {
                            SubmitDetails submitDetails = new SubmitDetails(this);
                            submitDetails.execute(data.getStringExtra("txnId"), data.getStringExtra("Status")
                                    , data.getStringExtra("responseCode"), data.getStringExtra("txnRef"));
                            new AlertDialog.Builder(this).setMessage("The transaction is still in processing state." +
                                    "\n\n Please inform your institute once the status gets updated.\n\n Thank You")
                                    .setPositiveButton("Ok", null).show();
                        }
                    }
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                new AlertDialog.Builder(this).setMessage("Payment Cancelled")
                        .setPositiveButton("Ok", null).show();
            }
    }

    @SuppressLint("StaticFieldLeak")
    public class SubmitDetails extends AsyncTask<String, String, String> {
        Context ctx;
        String txnId, status, resCode, txnRef;

        SubmitDetails(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {

            SharedPreferences sp = ctx.getSharedPreferences(PREFS_NAME, 0);

            txnId = params[0];
            status = params[1].toUpperCase();
            resCode = params[2];
            txnRef = params[3];
            String schoolId = sp.getString("str_school_id", "");
            String userId = sp.getString("userID", "");
            String userName = sp.getString("userName", "");
            String batch = sp.getString("userSection", "");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
            String date = sdf.format(Calendar.getInstance().getTimeInMillis());
            String data;

            try {

                URL url = new URL(SUBMIT_STUDENT_FEES);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                data = URLEncoder.encode("tbl_school_id", "UTF-8") + "=" + URLEncoder.encode(schoolId, "UTF-8") + "&" +
                        URLEncoder.encode("tbl_users_id", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8") + "&" +
                        URLEncoder.encode("tbl_users_name", "UTF-8") + "=" + URLEncoder.encode(userName, "UTF-8") + "&" +
                        URLEncoder.encode("tbl_fees_collection_amount", "UTF-8") + "=" + URLEncoder.encode(strAmount, "UTF-8") + "&" +
                        URLEncoder.encode("tbl_fees_collection_entrydt", "UTF-8") + "=" + URLEncoder.encode(date, "UTF-8") + "&" +
                        URLEncoder.encode("trans_id", "UTF-8") + "=" + URLEncoder.encode(txnId, "UTF-8") + "&" +
                        URLEncoder.encode("trans_status", "UTF-8") + "=" + URLEncoder.encode(status, "UTF-8") + "&" +
                        URLEncoder.encode("response_code", "UTF-8") + "=" + URLEncoder.encode(resCode, "UTF-8") + "&" +
                        URLEncoder.encode("trans_reference_no", "UTF-8") + "=" + URLEncoder.encode(txnRef, "UTF-8") + "&" +
                        URLEncoder.encode("tbl_batch_nm", "UTF-8") + "=" + URLEncoder.encode(batch, "UTF-8");
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
            super.onPostExecute(result);
            progressBar.setVisibility(View.GONE);
            Log.e("result", result);
            if (result.contains("\"success\":true")) {
                Intent intent = new Intent(ctx, PaymentSuccess.class);
                intent.putExtra("status", status.toUpperCase());
                intent.putExtra("txnRef", txnRef);
                intent.putExtra("txnId", txnId);
                intent.putExtra("amount", strAmount);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(ctx, "Error Occurred", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
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
