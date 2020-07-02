package com.aaptrix.activitys;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.widget.ImageView;

import com.aaptrix.BuildConfig;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;

import com.aaptrix.R;

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
import java.util.ArrayList;
import java.util.List;

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
import static com.aaptrix.tools.HttpUrl.REFER_DATA;
import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static cz.msebera.android.httpclient.conn.ssl.SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER;


public class WelcomeActivity extends Activity {

    String schoolId, roleId, user_token_id, android_id, userId, userType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.screen_popup);

        final ImageView school_logo = findViewById(R.id.school_logo);
        SharedPreferences sp = this.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        Picasso.with(this).load(R.drawable.small_logo).into(school_logo);

        roleId = sp.getString("str_role_id", "");
        schoolId = sp.getString("str_school_id", "");
        userId = sp.getString("userID", "");
        userType = sp.getString("userrType", "");

        if (sp.getString("token", "") != null) {
            user_token_id = sp.getString("token", "");
        } else {
            user_token_id = FirebaseInstanceId.getInstance().getToken();
        }
        android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        GetAllInstituteBuzz b = new GetAllInstituteBuzz(this);
        String id;
        if (sp.getString("userRole", "").equals("Parent")) {
            id = sp.getString("userRoleID", "");
        } else {
            id = roleId;
        }
        b.execute(id, schoolId, user_token_id, android_id, userId);

        if (!userType.equals("Student")) {
            GetAllBatches b1 = new GetAllBatches(this);
            b1.execute(schoolId);
        }

        fetchData(schoolId, userId);

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            Intent i = new Intent(WelcomeActivity.this, AppLogin.class);
            i.putExtra("status", "Online");
            startActivity(i);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        }, 3000);

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
                        URLEncoder.encode("android_build_no", "UTF-8") + "=" + URLEncoder.encode(android_version, "UTF-8");
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
            if (!result.equals("{\"result\":null}")) {
                try {
                    JSONObject jsonRootObject = new JSONObject(result);
                    getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().
                            putString("anonymous_feedback", jsonRootObject.getString("feedback_anonymous_permission")).apply();
                    cacheJson(jsonRootObject, "instituteBuzz");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            super.onPostExecute(result);
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
            try {
                JSONObject jo = new JSONObject(result);
                cacheJson(jo, "batches");
            } catch (JSONException e) {
                e.printStackTrace();
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
}
