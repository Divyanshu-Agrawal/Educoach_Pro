package com.aaptrix.activitys;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aaptrix.BuildConfig;

import com.aaptrix.activitys.admin.InstituteBuzzActivityDiff;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.security.ProviderInstaller;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.aaptrix.R;

import javax.net.ssl.SSLContext;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.conn.ssl.SSLConnectionSocketFactory;
import cz.msebera.android.httpclient.conn.ssl.SSLContexts;
import cz.msebera.android.httpclient.impl.client.HttpClients;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import cz.msebera.android.httpclient.util.EntityUtils;

import static com.aaptrix.tools.HttpUrl.ALL_BATCHS;
import static com.aaptrix.tools.HttpUrl.ALL_INSTITUTE_BUZZ_CATE;
import static com.aaptrix.tools.HttpUrl.LOGOUT;
import static com.aaptrix.tools.HttpUrl.REFER_DATA;
import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREF_COLOR;
import static com.aaptrix.tools.SPClass.PREF_ROLE;
import static cz.msebera.android.httpclient.conn.ssl.SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER;

public class SplashScreen extends Activity {

    LinearLayout school_logo_layout, official_logo_layout;
    ImageView school_logo;
    RelativeLayout layout;
    TextView mainTxt, version;
    String currentVersion, schoolId, roleId, user_token_id, android_id, userId, userType, priority, userSection, userPhone;
    public static String HTTP_HOST = "https://dashboard.educoachapp.com/android_services/";
//    public static String HTTP_HOST = "https://stage.educoachapp.com/android_services/";
    public static String SENDER_ID = BuildConfig.SENDER_ID;
    public static final String SCHOOL_ID = BuildConfig.SCHOOL_ID;
    public static final String SCHOOL_NAME = BuildConfig.SCHOOL_NAME;
    public static final String TNC = BuildConfig.TNC;
    CountDownTimer mTimer;

    @SuppressLint("HardwareIds")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.cancelAll();

        school_logo_layout = findViewById(R.id.school_logo_layout);
        official_logo_layout = findViewById(R.id.official_logo_layout);
        layout = findViewById(R.id.maintenance_layout);
        mainTxt = findViewById(R.id.maintenance_text);
        school_logo = findViewById(R.id.school_logo);
        version = findViewById(R.id.version);
        version.setText("Version " + BuildConfig.VERSION_NAME);

        try {
            ProviderInstaller.installIfNeeded(this);
            SSLContext sslContext;
            sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, null, null);
            sslContext.createSSLEngine();
        } catch (GooglePlayServicesRepairableException e) {
            GooglePlayServicesUtil.getErrorDialog(e.getConnectionStatusCode(), this, 0);
        } catch (GooglePlayServicesNotAvailableException | NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        }

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        roleId = settings.getString("str_role_id", "");
        schoolId = settings.getString("str_school_id", "0");
        userId = settings.getString("userID", "");
        userType = settings.getString("userrType", "Guest");
        userSection = settings.getString("userSection", "");
        userPhone = settings.getString("userPhone", "");

        user_token_id = settings.getString("token", "");
        android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        if (!roleId.equals("") && !schoolId.equals("0") && !userId.equals("")) {
            GetAllInstituteBuzz b = new GetAllInstituteBuzz(this);
            String id;
            if (settings.getString("userRole", "").equals("Parent")) {
                id = settings.getString("userRoleID", "");
            } else {
                id = roleId;
            }
            b.execute(id, schoolId, user_token_id, android_id, userId, userPhone);
            if (!userType.equals("Student")) {
                GetAllBatches b1 = new GetAllBatches(this);
                b1.execute(schoolId);
            }
            fetchData(schoolId, userId);
        }

        Picasso.with(this)
                .load(R.drawable.small_logo)
                .into(school_logo);

        if (isInternetOn()) {
            checkVersion c = new checkVersion(SplashScreen.this);
            c.execute();
        }

        mTimer = new CountDownTimer(4000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                startnextActivity();
            }
        };
        mTimer.start();
    }

    public void onStart() {
        super.onStart();
    }


    @SuppressLint("StaticFieldLeak")
    public class checkVersion extends AsyncTask<String, String, String> {
        Context ctx;

        checkVersion(Context context) {
            ctx = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            String branch_url = HTTP_HOST + "check_app_version.php";
            String data;

            try {
                URL url = new URL(branch_url);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                data = URLEncoder.encode("tbl_school_id", "UTF-8") + "=" + URLEncoder.encode(SCHOOL_ID, "UTF-8") + "&" +
                        URLEncoder.encode("app_type", "UTF-8") + "=" + URLEncoder.encode("pro", "UTF-8");
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
            super.onPostExecute(result);
            try {
                JSONObject jsonRootObject = new JSONObject(result);
                JSONArray jsonArray = jsonRootObject.getJSONArray("result");
                String maintenance = jsonRootObject.getString("app_maintenance");
                if (maintenance.equals("Null")) {
                    if (result.contains("\"InstituteStatus\":\"Active\"")) {
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            currentVersion = jsonObject.getString("tbl_app_current_version");
                            priority = jsonObject.getString("app_priority");
                            getVersionInfo();
                        }
                    } else {
                        startActivity(new Intent(ctx, InactiveInstitute.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    }
                    layout.setVisibility(View.GONE);
                } else {
                    mTimer.cancel();
                    layout.setVisibility(View.VISIBLE);
                    layout.bringToFront();
                    official_logo_layout.setVisibility(View.GONE);
                    school_logo_layout.setVisibility(View.GONE);
                    mainTxt.setText(maintenance);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    private void getVersionInfo() {
        mTimer.cancel();
        if (BuildConfig.VERSION_CODE >= Integer.parseInt(currentVersion)) {
            startnextActivity();
        } else {
            if (priority.equals("2")) {
                SharedPreferences sp = getSharedPreferences("dialog", 0);
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                String date = sdf.format(System.currentTimeMillis());
                if (!date.equals(sp.getString("date", ""))) {
                    updateDialog();
                } else {
                    startnextActivity();
                }
            } else {
                updateDialog();
            }
        }
    }

    @SuppressLint("SetTextI18n, InflateParams")
    private void updateDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.DialogTheme);
        View view = getLayoutInflater().inflate(R.layout.dialog_update_app, null);
        builder.setView(view);
        builder.setCancelable(false);
        final AlertDialog ad = builder.create();
        ad.show();
        Button later = view.findViewById(R.id.later);
        Button update = view.findViewById(R.id.update);

        if (priority.equals("0")) {
            later.setText("Exit");
            later.setOnClickListener(view1 -> {
                finish();
                System.exit(0);
            });
        } else if (priority.equals("1")) {
            later.setOnClickListener(view1 -> {
                ad.dismiss();
                startnextActivity();
            });
        } else {
            later.setOnClickListener(view1 -> {
                SharedPreferences sp = getSharedPreferences("dialog", 0);
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                String date = sdf.format(System.currentTimeMillis());
                sp.edit().putString("date", date).apply();
                ad.dismiss();
                startnextActivity();
            });
        }

        update.setOnClickListener(view12 -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getApplicationContext().getPackageName()));
            startActivity(intent);
        });
    }

    private void startnextActivity() {
        SharedPreferences sp = getSharedPreferences(PREFS_NAME, 0);
        if (sp.getString("userPassword", null) != null) {
            if (isInternetOn()) {
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                Intent i = new Intent(SplashScreen.this, AppLogin.class);
                i.putExtra("status", "Online");
                startActivity(i);
                finish();
            } else {
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                Intent i = new Intent(SplashScreen.this, AppLogin.class);
                i.putExtra("status", "Offline");
                startActivity(i);
                finish();
                Toast.makeText(SplashScreen.this, "Please Connect Internet for better experience", Toast.LENGTH_SHORT).show();
            }
        } else {
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("logged", "notlogged");
            editor.putString("userID", "0");
            editor.putString("userLoginId", " ");
            editor.putString("userName", "Guest");
            editor.putString("userPhone", " ");
            editor.putString("userEmailId", " ");
            editor.putString("userDob", " ");
            editor.putString("userGender", "Guest");
            editor.putString("userPhoneStatus", "1");
            editor.putString("userrType", "Guest");
            editor.putString("userSchoolId", SCHOOL_ID);
            editor.putString("numberOfUser", "single");
            editor.putString("userPassword", null);
            editor.putString("userSection", " ");
            editor.putString("userRollNumber", " ");
            editor.putString("userTeacherName", " ");
            editor.putString("userSchoolLogo", " ");
            editor.putString("userSchoolLogo1", " ");
            editor.putString("userSchoolLogo3", " ");
            editor.putString("imageUrl", "https://dashboard.educoachapp.com//uploads/institute/institute_");

            //
            editor.putString("userSchoolName", SCHOOL_NAME);
            editor.putString("str_school_id", SCHOOL_ID);
            editor.putString("str_role_id", "7");
            editor.apply();

            //color set
            SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
            SharedPreferences.Editor editorColor = settingsColor.edit();

            editorColor.putString("tool", getResources().getString(R.string.tool));
            editorColor.putString("drawer", getResources().getString(R.string.drawer));
            editorColor.putString("status", getResources().getString(R.string.status));
            editorColor.putString("text1", getResources().getString(R.string.text1));
            editorColor.putString("text2", getResources().getString(R.string.text2));
            editorColor.apply();


            Intent i = new Intent(this, InstituteBuzzActivityDiff.class);
            startActivity(i);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }

    private void fetchData(String schoolId, String userId) {
        new Thread(() -> {
            try {
                SSLContext sslContext = SSLContexts.custom().useTLS().build();
                SSLConnectionSocketFactory f = new SSLConnectionSocketFactory(
                        sslContext,
                        new String[]{"TLSv1.1", "TLSv1.2"},
                        null,
                        BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
                HttpClient httpclient = HttpClients.custom().setSSLSocketFactory(f).build();
                HttpPost httppost = new HttpPost(REFER_DATA);
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<>(2);
                nameValuePairs.add(new BasicNameValuePair("schoolId", schoolId));
                nameValuePairs.add(new BasicNameValuePair("userId", userId));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity httpEntity = response.getEntity();
                final String result = EntityUtils.toString(httpEntity);

                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(() -> {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        SharedPreferences sp = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("refer_code", jsonObject.getString("referral_code"));
                        editor.putString("refer_offer", jsonObject.getString("referral_offer"));
                        editor.apply();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }


    public final boolean isInternetOn() {

        // get Connectivity Manager object to check connection
        ConnectivityManager connec = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

        // Check for network connections
        assert connec != null;
        if (connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED) {

            // if connected with internet
            return true;
        } else if (
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED) {
            return false;
        }
        return false;
    }

    @SuppressLint("StaticFieldLeak")
    public class GetAllInstituteBuzz extends AsyncTask<String, String, String> {
        Context ctx;

        GetAllInstituteBuzz(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String roleId = params[0];
            String schoolId = params[1];
            String tokenId = params[2];
            String androidId = params[3];
            String userId = params[4];
            String phone = params[5];
            String android_version = String.valueOf(BuildConfig.VERSION_CODE);
            String data;

            try {
                URL url = new URL(ALL_INSTITUTE_BUZZ_CATE);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                data = URLEncoder.encode("roleId", "UTF-8") + "=" + URLEncoder.encode(roleId, "UTF-8") + "&" +
                        URLEncoder.encode("schoolId", "UTF-8") + "=" + URLEncoder.encode(schoolId, "UTF-8") + "&" +
                        URLEncoder.encode("tokenId", "UTF-8") + "=" + URLEncoder.encode(tokenId, "UTF-8") + "&" +
                        URLEncoder.encode("androidId", "UTF-8") + "=" + URLEncoder.encode(androidId, "UTF-8") + "&" +
                        URLEncoder.encode("userId", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8") + "&" +
                        URLEncoder.encode("android_build_no", "UTF-8") + "=" + URLEncoder.encode(android_version, "UTF-8") + "&" +
                        URLEncoder.encode("str_user_phone", "UTF-8") + "=" + URLEncoder.encode(phone, "UTF-8");
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
            if (!userType.equals("Guest"))
                try {
                    ArrayList<String> user = new ArrayList<>();
                    JSONArray array = new JSONObject(result).getJSONArray("UserList");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        user.add(object.getString("tbl_users_id"));
                    }

                    if (user.size() == 0) {
                        logout(userPhone);
                    } else if (user.size() == 1) {
                        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, 0).edit();
                        editor.putString("numberOfUser", "single");
                        editor.apply();
                    } else {
                        SharedPreferences.Editor editor = getSharedPreferences(PREFS_NAME, 0).edit();
                        editor.putString("numberOfUser", "multiple");
                        editor.apply();
                    }

                    if (!user.contains(userId)) {
                        logout(userPhone);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            if (result != null && !result.contains("\"result\":null")) {
                try {
                    JSONObject jsonRootObject = new JSONObject(result);
                    cacheJson(jsonRootObject, "instituteBuzz");
                    getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().
                            putString("anonymous_feedback", jsonRootObject.getString("feedback_anonymous_permission")).apply();
                    if (userType.equals("Student")) {
                        JSONObject object = jsonRootObject.getJSONObject("Batch_details");
                        if (!userSection.equals(object.getString("tbl_stnt_prsnl_data_section"))) {
                            logout(userPhone);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            super.onPostExecute(result);
        }

    }

    private void logout(String mobileno) {
        Toast.makeText(this, "Your session has expired. Please login again.", Toast.LENGTH_SHORT).show();
        File directory = this.getFilesDir();
        File file = new File(directory, "instituteBuzz");
        file.delete();
        file = new File(directory, "batches");
        file.delete();
        getSharedPreferences(PREF_COLOR, 0).edit().clear().apply();
        getSharedPreferences(PREF_ROLE, 0).edit().clear().apply();
        getSharedPreferences(PREFS_NAME, 0).edit().clear().apply();
        getSharedPreferences("date", 0).edit().clear().apply();
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("logged", "notlogged");
        editor.putString("userID", "0");
        editor.putString("userLoginId", " ");
        editor.putString("userName", "Guest");
        editor.putString("userPhone", " ");
        editor.putString("userEmailId", " ");
        editor.putString("userDob", " ");
        editor.putString("userGender", "Guest");
        editor.putString("userPhoneStatus", "1");
        editor.putString("userrType", "Guest");
        editor.putString("userSchoolId", SCHOOL_ID);
        editor.putString("numberOfUser", "single");
        editor.putString("userPassword", null);
        editor.putString("userSection", " ");
        editor.putString("userRollNumber", " ");
        editor.putString("userTeacherName", " ");
        editor.putString("userSchoolLogo", " ");
        editor.putString("userSchoolLogo1", " ");
        editor.putString("userSchoolLogo3", " ");
        editor.putString("imageUrl", "https://dashboard.educoachapp.com//uploads/institute/institute_");

        //
        editor.putString("userSchoolName", SCHOOL_NAME);
        editor.putString("str_school_id", SCHOOL_ID);
        editor.putString("str_role_id", "7");
        editor.apply();

        //color set
        SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
        SharedPreferences.Editor editorColor = settingsColor.edit();

        editorColor.putString("tool", getResources().getString(R.string.tool));
        editorColor.putString("drawer", getResources().getString(R.string.drawer));
        editorColor.putString("status", getResources().getString(R.string.status));
        editorColor.putString("text1", getResources().getString(R.string.text1));
        editorColor.putString("text2", getResources().getString(R.string.text2));
        editorColor.apply();


        Intent i = new Intent(this, InstituteBuzzActivityDiff.class);
        startActivity(i);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        new Thread(() -> {
            try {
                SSLContext sslContext = SSLContexts.custom().useTLS().build();
                SSLConnectionSocketFactory f = new SSLConnectionSocketFactory(
                        sslContext,
                        new String[]{"TLSv1.1", "TLSv1.2"},
                        null,
                        BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
                HttpClient httpclient = HttpClients.custom().setSSLSocketFactory(f).build();
                HttpPost httppost = new HttpPost(LOGOUT);
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<>(1);
                nameValuePairs.add(new BasicNameValuePair("mobno", mobileno));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity httpEntity = response.getEntity();
                final String result = EntityUtils.toString(httpEntity);

                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(() -> Log.e("result", result));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
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
            if (result != null) {
                try {
                    JSONObject jo = new JSONObject(result);
                    cacheJson(jo, "batches");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            super.onPostExecute(result);
        }

    }

    private void cacheJson(final JSONObject jsonObject, String name) {
        new Thread(() -> {
            ObjectOutput out;
            String data = jsonObject.toString();
            try {
                File directory = this.getFilesDir();
                directory.mkdir();
                out = new ObjectOutputStream(new FileOutputStream(new File(directory, name)));
                out.writeObject(data);
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    public void onBackPressed() {
        finish();
    }

}