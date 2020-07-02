package com.aaptrix.activitys.student;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aaptrix.R;
import com.aaptrix.adaptor.StaffAdapter;
import com.aaptrix.databeans.StaffRateData;
import com.google.android.material.appbar.AppBarLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
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

import static com.aaptrix.tools.HttpUrl.STAFF_DETAILS;
import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREF_COLOR;
import static cz.msebera.android.httpclient.conn.ssl.SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER;

public class StaffActivity extends AppCompatActivity {

    AppBarLayout appBarLayout;
    TextView tool_title;
    ProgressBar progressBar;
    String selToolColor, selDrawerColor, selStatusColor, selTextColor1, selTextColor2;
    String userId, userSection, schoolId;
    SwipeRefreshLayout swipeRefreshLayout;
    ListView listView;
    TextView noStaff;
    ArrayList<StaffRateData> staffArray = new ArrayList<>();
    SharedPreferences settings;
    String rateEnabled;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        setTitle("");
        setResult(RESULT_OK);
        appBarLayout = findViewById(R.id.appBarLayout);
        tool_title = findViewById(R.id.tool_title);
        progressBar = findViewById(R.id.progress_bar);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        listView = findViewById(R.id.stafflist);
        noStaff = findViewById(R.id.no_staff);

        SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
        selToolColor = settingsColor.getString("tool", "");
        selDrawerColor = settingsColor.getString("drawer", "");
        selStatusColor = settingsColor.getString("status", "");
        selTextColor1 = settingsColor.getString("text1", "");
        selTextColor2 = settingsColor.getString("text2", "");

        settings = getSharedPreferences(PREFS_NAME, 0);
        userId = settings.getString("userID", "");
        userSection = settings.getString("userSection", "");
        schoolId = settings.getString("str_school_id", "");
        String userType = settings.getString("userrType", "");

        appBarLayout.setBackgroundColor(Color.parseColor(selToolColor));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(selStatusColor));
        }
        tool_title.setTextColor(Color.parseColor(selTextColor1));

        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (isInternetOn()) {
                staffArray.clear();
                swipeRefreshLayout.setRefreshing(true);
                GetStaff staff = new GetStaff(this);
                staff.execute(schoolId, userId, userSection, userType);
            } else {
                Toast.makeText(this, "No Internet", Toast.LENGTH_SHORT).show();
            }
        });

        if (isInternetOn()) {
            GetStaff staff = new GetStaff(this);
            staff.execute(schoolId, userId, userSection, userType);
        } else {
            String json = settings.getString("staff", null);
            if (json != null) {
                try {
                    JSONObject jsonObject = new JSONObject(json);
                    rateEnabled = jsonObject.getString("rating_permission");
                    JSONArray jsonArray = jsonObject.getJSONArray("result");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jObject = jsonArray.getJSONObject(i);
                        if (!jObject.getString("tbl_users_name").equals("null")) {
                            StaffRateData data = new StaffRateData();
                            data.setId(jObject.getString("tbl_users_id"));
                            data.setName(jObject.getString("tbl_users_name"));
                            data.setImage(jObject.getString("tbl_users_img"));
                            data.setBio(jObject.getString("tbl_users_bio"));
                            data.setRating(jObject.getString("rating"));
                            data.setType(jObject.getString("tbl_users_type"));
                            data.setSubjects(jObject.getString("tbl_batch_subjct_name"));
                            data.setRated(jObject.getString("rated"));
                            data.setComment(jObject.getString("comment"));
                            staffArray.add(data);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (staffArray.size() != 0) {
                    listItem();
                }
            } else {
                noStaff.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class GetStaff extends AsyncTask<String, String, String> {
        Context ctx;

        GetStaff(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            String schoolId = params[0];
            String userId = params[1];
            String section = params[2];
            String userType = params[3];

            try {
                SSLContext sslContext = SSLContexts.custom().useTLS().build();
                SSLConnectionSocketFactory f = new SSLConnectionSocketFactory(
                        sslContext,
                        new String[]{"TLSv1.1", "TLSv1.2"},
                        null,
                        BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
                HttpClient httpclient = HttpClients.custom().setSSLSocketFactory(f).build();
                HttpPost httppost = new HttpPost(STAFF_DETAILS);
                MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
                entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                entityBuilder.addTextBody("school_id", schoolId);
                entityBuilder.addTextBody("tbl_users_id", userId);
                entityBuilder.addTextBody("batch_name", section);
                entityBuilder.addTextBody("userType", userType);
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
            swipeRefreshLayout.setRefreshing(false);
            if (result != null && !result.contains("{\"result\":null}")) {
                settings.edit().putString("staff", result).apply();
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    rateEnabled = jsonObject.getString("rating_permission");
                    JSONArray jsonArray = jsonObject.getJSONArray("result");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jObject = jsonArray.getJSONObject(i);
                        if (!jObject.getString("tbl_users_name").equals("null")) {
                            StaffRateData data = new StaffRateData();
                            data.setId(jObject.getString("tbl_users_id"));
                            data.setName(jObject.getString("tbl_users_name"));
                            data.setImage(jObject.getString("tbl_users_img"));
                            data.setBio(jObject.getString("tbl_users_bio"));
                            data.setRating(jObject.getString("rating"));
                            data.setType(jObject.getString("tbl_users_type"));
                            data.setSubjects(jObject.getString("tbl_batch_subjct_name"));
                            data.setRated(jObject.getString("rated"));
                            data.setComment(jObject.getString("comment"));
                            staffArray.add(data);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (staffArray.size() != 0) {
                    listItem();
                } else {
                    noStaff.setVisibility(View.VISIBLE);
                }
            }
            super.onPostExecute(result);
        }
    }

    private void listItem() {
        StaffAdapter adapter = new StaffAdapter(this, R.layout.staff_list, staffArray, rateEnabled);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public final boolean isInternetOn() {
        ConnectivityManager connec =
                (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        assert connec != null;
        if (connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED) {
            return true;
        } else if (
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED) {
            return false;
        }
        return false;
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
}
