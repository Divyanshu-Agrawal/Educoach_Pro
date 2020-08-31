package com.aaptrix.activitys.admin;

import com.aaptrix.activitys.student.ActivitiesActivity;
import com.aaptrix.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.aaptrix.adaptor.BatchListAdaptor;
import com.aaptrix.databeans.DataBeanStudent;
import com.google.android.material.appbar.AppBarLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import javax.net.ssl.SSLContext;

import static com.aaptrix.tools.HttpUrl.ALL_BATCHS;
import static com.aaptrix.tools.HttpUrl.BATCH_BY_COURSE;
import static com.aaptrix.tools.SPClass.PREFS_DAIRY;
import static com.aaptrix.tools.HttpUrl.ADD_ACTIVITY;
import static com.aaptrix.tools.HttpUrl.REMOVE_ACTIVITY;
import static com.aaptrix.tools.HttpUrl.UPDATE_ACTIVITY;
import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREF_COLOR;
import static cz.msebera.android.httpclient.conn.ssl.SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER;

public class AddNewActivity extends AppCompatActivity {

    RelativeLayout layout;
    Toolbar toolbar;
    AppBarLayout appBarLayout;
    EditText title;
    ProgressBar progressBar;
    Button save, delete;
    String type, strTitle, strId;
    String selToolColor, selStatusColor, selTextColor1, userSchoolId, userId;
    MediaPlayer mp;
    CardView cardView;
    GifImageView taskStatus;
    BatchListAdaptor batchListAdaptor;
    EditText batch_spinner;
    AlertDialog.Builder alert;
    AlertDialog alertDialog;
    private ArrayList<DataBeanStudent> studentArray = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new);
        toolbar = findViewById(R.id.toolbar);
        appBarLayout = findViewById(R.id.appBarLayout);
        setSupportActionBar(toolbar);
        setTitle("Add Announcement");
        setResult(RESULT_OK);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        type = getIntent().getStringExtra("type");
        mp = MediaPlayer.create(this, R.raw.button_click);
        cardView = findViewById(R.id.card_view);
        taskStatus = findViewById(R.id.task_status);
        layout = findViewById(R.id.layout);
        batch_spinner = findViewById(R.id.batch_spinner);

        title = findViewById(R.id.tv_achive_title);
        progressBar = findViewById(R.id.loader);
        save = findViewById(R.id.save_btn);
        delete = findViewById(R.id.remove_btn);

        title.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus)
                hideKeyboard(v);
        });

        SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
        selToolColor = settingsColor.getString("tool", "");
        selStatusColor = settingsColor.getString("status", "");
        selTextColor1 = settingsColor.getString("text1", "");

        SharedPreferences sp_user = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        userSchoolId = sp_user.getString("str_school_id", "");
        userId = sp_user.getString("userID", "");
        appBarLayout.setBackgroundColor(Color.parseColor(selToolColor));
        save.setBackgroundColor(Color.parseColor(selToolColor));
        delete.setBackgroundColor(Color.parseColor(selToolColor));
        save.setTextColor(Color.parseColor(selTextColor1));
        delete.setTextColor(Color.parseColor(selTextColor1));

        GetAllBatches getAllBatches = new GetAllBatches(this);
        getAllBatches.execute(userSchoolId);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(selStatusColor));
        }


        layout.setOnClickListener(v -> {

        });

        layout.setOnTouchListener((v, event) -> false);

        if (type.equals("update")) {
            setTitle("Update Announcement");
            strTitle = getIntent().getStringExtra("title");
            strId = getIntent().getStringExtra("id");

            title.setText(strTitle);
            save.setText("Update");
            delete.setVisibility(View.VISIBLE);
        }

        save.setOnClickListener(v -> {
            mp.start();
            SharedPreferences sp = getSharedPreferences(PREFS_DAIRY, 0);
            String studentArray = sp.getString("studentArray", "");
            if (TextUtils.isEmpty(title.getText().toString())) {
                title.setError("Please Enter Details");
                title.requestFocus();
            } else if (studentArray.isEmpty()) {
                Toast.makeText(this, "Please select batch", Toast.LENGTH_SHORT).show();
            } else {
                layout.setVisibility(View.VISIBLE);
                layout.bringToFront();
                if (type.equals("add")) {
                    UploadActivity uploadActivity = new UploadActivity(this);
                    uploadActivity.execute(userSchoolId, title.getText().toString(),
                            studentArray, sp_user.getString("userID", ""));
                } else {
                    UpdateActivity updateActivity = new UpdateActivity(this);
                    updateActivity.execute(userId, title.getText().toString(),
                            strId, sp_user.getString("userSection", ""));
                }
            }
        });

        delete.setOnClickListener(v -> {
            mp.start();
            AlertDialog.Builder alert = new AlertDialog.Builder(this, R.style.DialogTheme);
            alert.setTitle("Are you sure you want to delete").setPositiveButton("Yes", (dialog, which) -> {
                RemoveActivity removeActivity = new RemoveActivity(this);
                removeActivity.execute(userSchoolId, strId);
            }).setNegativeButton("No", null);
            AlertDialog alertDialog = alert.create();
            alertDialog.setCancelable(false);
            alertDialog.show();
            Button theButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
            Button theButton1 = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            theButton.setTextColor(Color.parseColor(selToolColor));
            theButton1.setTextColor(Color.parseColor(selToolColor));
        });
    }

    @SuppressLint("StaticFieldLeak")
    public class GetAllBatches extends AsyncTask<String, String, String> {
        Context ctx;

        GetAllBatches(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            String school_id = params[0];
            String data;

            try {

                URL url = new URL(ALL_BATCHS);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                data = URLEncoder.encode("school_id", "UTF-8") + "=" + URLEncoder.encode(school_id, "UTF-8") + "&" +
                        URLEncoder.encode("tbl_users_id", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8");
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
            Log.e("result", result);
            if (result.equals("{\"result\":null}")) {
                studentArray.clear();
                Toast.makeText(ctx, "No Value", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    JSONObject jsonRootObject = new JSONObject(result);
                    JSONArray jsonArray = jsonRootObject.getJSONArray("result");
                    studentArray.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        DataBeanStudent dbs = new DataBeanStudent();
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        dbs.setUserID(jsonObject.getString("tbl_batch_detail_id"));
                        dbs.setUserName(jsonObject.getString("tbl_batch_name"));
                        studentArray.add(dbs);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setBatch();
                super.onPostExecute(result);
            }
        }
    }

    private void setBatch() {
        batch_spinner.setOnClickListener(v -> setNumberOfUsers(studentArray));
    }

    private void setNumberOfUsers(final ArrayList<DataBeanStudent> studentArray) {
        LayoutInflater factory = LayoutInflater.from(this);
        final View textEntryView = factory.inflate(R.layout.tudent_select_dialog, null);

        ListView user_list = textEntryView.findViewById(R.id.user_list);
        CheckBox cb_selectAll = textEntryView.findViewById(R.id.cb_selectAll);
        batchListAdaptor = new BatchListAdaptor(this, R.layout.user_select_dialog, studentArray, false);
        user_list.setAdapter(batchListAdaptor);

        cb_selectAll.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                batchListAdaptor = new BatchListAdaptor(this, R.layout.user_select_dialog, studentArray, b);
                user_list.setAdapter(batchListAdaptor);
                batchListAdaptor.notifyDataSetChanged();
                saveDataInSP(studentArray);
            } else {
                batchListAdaptor = new BatchListAdaptor(this, R.layout.user_select_dialog, studentArray, b);
                user_list.setAdapter(batchListAdaptor);
                batchListAdaptor.notifyDataSetChanged();
                SharedPreferences.Editor se_dairy = getSharedPreferences(PREFS_DAIRY, 0).edit();
                se_dairy.clear();
                se_dairy.apply();
            }
        });

        alert = new AlertDialog.Builder(this, R.style.DialogTheme);

        alert.setTitle("Select Batch").setView(textEntryView).setPositiveButton("Ok",
                (dialog, whichButton) -> {
                    alertDialog.dismiss();
                    String studentArray1 = getSharedPreferences("dairy", 0).getString("studentArray", "");
                    if (!studentArray1.isEmpty()) {
                        batch_spinner.setHint("Selected");
                    }
                }).setNegativeButton("Cancel",
                (dialog, whichButton) -> {
                });
        alertDialog = alert.create();
        alertDialog.show();
        Button theButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        Button theButton1 = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        theButton.setTextColor(getResources().getColor(R.color.text_gray));
        theButton1.setTextColor(getResources().getColor(R.color.text_gray));
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setEnabled(true);
    }

    private void saveDataInSP(ArrayList<DataBeanStudent> studentArray) {
        Gson gson = new GsonBuilder().create();
        JsonArray studentArray1 = gson.toJsonTree(studentArray).getAsJsonArray();
        SharedPreferences sp_dairy = getSharedPreferences(PREFS_DAIRY, 0);
        SharedPreferences.Editor se_dairy = sp_dairy.edit();
        se_dairy.clear();
        se_dairy.putString("studentArray", "" + studentArray1);
        se_dairy.apply();
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

    @SuppressLint("StaticFieldLeak")
    public class UploadActivity extends AsyncTask<String, String, String> {
        Context ctx;

        UploadActivity(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            Toast.makeText(ctx, "Please wait, adding announcement entry", Toast.LENGTH_SHORT).show();
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {

            String schoolId = params[0];
            String str_title = params[1];
            String userSection = params[2];
            String userId = params[4];
            String notiImage = "0";

            try {
                try {
                    SSLContext sslContext = SSLContexts.custom().useTLS().build();
                    SSLConnectionSocketFactory f = new SSLConnectionSocketFactory(
                            sslContext,
                            new String[]{"TLSv1.1", "TLSv1.2"},
                            null,
                            BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
                    HttpClient httpclient = HttpClients.custom().setSSLSocketFactory(f).build();
                    HttpPost httppost = new HttpPost(ADD_ACTIVITY);
                    MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
                    entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                    entityBuilder.addTextBody("schoolId", schoolId);
                    entityBuilder.addTextBody("str_title", str_title);
                    entityBuilder.addTextBody("userSection", userSection);
                    entityBuilder.addTextBody("userId", userId);
                    entityBuilder.addTextBody("noti_image", notiImage);
                    HttpEntity entity = entityBuilder.build();
                    httppost.setEntity(entity);
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity httpEntity = response.getEntity();
                    return EntityUtils.toString(httpEntity);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.d("ADDED", "" + result);
            if (result.contains("submitted")) {
                cardView.setVisibility(View.VISIBLE);
                new CountDownTimer(4000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        Intent i = new Intent(AddNewActivity.this, ActivitiesActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(i);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish();
                    }
                }.start();
            } else {
                Toast.makeText(ctx, "Issue in server", Toast.LENGTH_SHORT).show();
                layout.setVisibility(View.GONE);
            }
            super.onPostExecute(result);
        }

    }

    @SuppressLint("StaticFieldLeak")
    public class UpdateActivity extends AsyncTask<String, String, String> {
        Context ctx;

        UpdateActivity(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            String userId = params[0];
            String str_title = params[1];
            String achivId = params[2];
            String userSection = params[3];

            try {
                try {
                    SSLContext sslContext = SSLContexts.custom().useTLS().build();
                    SSLConnectionSocketFactory f = new SSLConnectionSocketFactory(
                            sslContext,
                            new String[]{"TLSv1.1", "TLSv1.2"},
                            null,
                            BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
                    HttpClient httpclient = HttpClients.custom().setSSLSocketFactory(f).build();
                    HttpPost httppost = new HttpPost(UPDATE_ACTIVITY);
                    MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
                    entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                    entityBuilder.addTextBody("userId", userId);
                    entityBuilder.addTextBody("str_title", str_title);
                    entityBuilder.addTextBody("userSection", userSection);
                    entityBuilder.addTextBody("actiId", achivId);
                    HttpEntity entity = entityBuilder.build();
                    httppost.setEntity(entity);
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity httpEntity = response.getEntity();
                    String res = EntityUtils.toString(httpEntity);
                    return res;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.e("Json", "" + result);
            if (result.contains("submitted")) {
                cardView.setVisibility(View.VISIBLE);
                new CountDownTimer(4000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        startActivity(new Intent(AddNewActivity.this, InstituteBuzzActivityDiff.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    }
                }.start();

            } else {
                layout.setVisibility(View.GONE);
                Toast.makeText(ctx, "Server issues", Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(result);
        }

    }

    //remove
    @SuppressLint("StaticFieldLeak")
    public class RemoveActivity extends AsyncTask<String, String, String> {
        Context ctx;

        RemoveActivity(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            String userId = params[0];
            String achievId = params[1];
            String data;

            try {

                URL url = new URL(REMOVE_ACTIVITY);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                data = URLEncoder.encode("userId", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8") + "&" +
                        URLEncoder.encode("achievId", "UTF-8") + "=" + URLEncoder.encode(achievId, "UTF-8");
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
            Log.d("Json", "" + result);
            progressBar.setVisibility(View.GONE);
            if (!result.isEmpty()) {
                startActivity(new Intent(AddNewActivity.this, InstituteBuzzActivityDiff.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            } else {
                Toast.makeText(ctx, "Some Error", Toast.LENGTH_SHORT).show();
            }
        }

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
