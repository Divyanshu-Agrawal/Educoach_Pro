package com.aaptrix.activitys.student;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.appbar.AppBarLayout;

import com.aaptrix.activitys.admin.AddNewActivity;
import com.aaptrix.activitys.admin.IntermidiateScreenActivityPublication;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import com.aaptrix.adaptor.ActivitiesListAdaptor;
import com.aaptrix.databeans.DataBeanActivities;
import com.aaptrix.R;

import static com.aaptrix.tools.HttpUrl.ALL_ACTIVITIES;
import static com.aaptrix.tools.HttpUrl.ALL_BATCHS;
import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREFS_RW;
import static com.aaptrix.tools.SPClass.PREF_COLOR;

/**
 * Created by sjain on 11/29/2017.
 */

public class ActivitiesActivity extends AppCompatActivity {
    SharedPreferences.Editor editor;
    String userId, userSchoolId, userSchoolLogo, userRoleId, userSection, userrType;
    AppBarLayout appBarLayout;
    SharedPreferences.Editor editorColor;
    String selToolColor, selDrawerColor, selStatusColor, selTextColor1, selTextColor2;
    TextView tool_title;
    private SwipeRefreshLayout mSwipeRefreshLayout;

    ArrayList<DataBeanActivities> activitiesArray = new ArrayList<>();
    DataBeanActivities dbact;
    String activiId, activiTitle, activiDesc, activiDate, activiImg;
    ListView activities_list;
    ActivitiesListAdaptor activitiesListAdaptor;
    LinearLayout add_layout;
    ImageView iv_add_more_activities;

    //offline
    private SharedPreferences sp_acti;
    SharedPreferences.Editor se_acti;
    public static final String PREFS_ACTI = "json_acti11";
    TextView no_aciev;
    String skip;
    String[] batch_array = {"All Batches"};
    Spinner batch_spinner;
    int a = 10;
    String selBatch = "All Batches";
    TextView snack;
    View footerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activities_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        appBarLayout = findViewById(R.id.appBarLayout);
        tool_title = findViewById(R.id.tool_title);
        no_aciev = findViewById(R.id.no_aciev);
        activities_list = findViewById(R.id.activities_list);
        mSwipeRefreshLayout = findViewById(R.id.activity_main_swipe_refresh_layout);
        add_layout = findViewById(R.id.add_layout);
        batch_spinner = findViewById(R.id.batch_spinner);
        snack = findViewById(R.id.snack);
        iv_add_more_activities = findViewById(R.id.iv_add_more_activities);
        mSwipeRefreshLayout.setRefreshing(false);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        userId = settings.getString("userID", "");
        userSchoolId = settings.getString("str_school_id", "");
        userSchoolLogo = settings.getString("userSchoolLogo", "");
        userRoleId = settings.getString("str_role_id", "");
        userSection = settings.getString("userSection", "");
        userrType = settings.getString("userrType", "");

        sp_acti = getSharedPreferences(PREFS_ACTI, 0);
        String acti = sp_acti.getString("json_acti", "");

        if (userrType.equals("Admin") || userrType.equals("Teacher")) {
            setBatch();
            try {
                File directory = getFilesDir();
                ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File(directory, "batches")));
                String json = in.readObject().toString();
                in.close();
                if (!json.equals("{\"result\":null}")) {
                    try {
                        JSONObject jo = new JSONObject(json);
                        JSONArray ja = jo.getJSONArray("result");
                        batch_array = new String[ja.length() + 1];
                        selBatch = "All Batches";
                        batch_array[0] = "All Batches";
                        for (int i = 0; i < ja.length(); i++) {
                            jo = ja.getJSONObject(i);
                            batch_array[i + 1] = jo.getString("tbl_batch_name");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    setBatch();
                } else {
                    String batch_array[] = {"All Batches"};
                    ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<>(this, R.layout.spinner_list_item1, batch_array);
                    dataAdapter1.setDropDownViewResource(R.layout.spinner_list_item1);
                    batch_spinner.setAdapter(dataAdapter1);
                    Toast.makeText(this, "No Batch", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                GetAllBatches b1 = new GetAllBatches(this);
                b1.execute(userSchoolId);
            }

            GetAllActivities b1 = new GetAllActivities(ActivitiesActivity.this);
            b1.execute(userSchoolId, selBatch, userrType, "0");

            mSwipeRefreshLayout.setOnRefreshListener(() -> {
                if (isInternetOn()) {
                    activitiesArray.clear();
                    a = 10;
                    GetAllActivities b = new GetAllActivities(ActivitiesActivity.this);
                    b.execute(userSchoolId, selBatch, userrType, "0");
                } else {
                    Toast.makeText(ActivitiesActivity.this, "No network Please connect with network for update", Toast.LENGTH_SHORT).show();
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            });
        } else {
            batch_spinner.setVisibility(View.GONE);
            if (acti != null && !acti.equals("null") && !acti.isEmpty() && acti.length() > 5) {
                getAllActivities(acti);
                snack.setVisibility(View.VISIBLE);
                new CountDownTimer(3000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        snack.setVisibility(View.GONE);
                    }
                }.start();
            } else {
                GetAllActivities b1 = new GetAllActivities(ActivitiesActivity.this);
                b1.execute(userSchoolId, userSection, userrType, "0");
            }

            mSwipeRefreshLayout.setOnRefreshListener(() -> {
                if (isInternetOn()) {
                    activitiesArray.clear();
                    a = 10;
                    GetAllActivities b1 = new GetAllActivities(ActivitiesActivity.this);
                    b1.execute(userSchoolId, userSection, userrType, "0");
                } else {
                    Toast.makeText(ActivitiesActivity.this, "No network Please connect with network for update", Toast.LENGTH_SHORT).show();
                    mSwipeRefreshLayout.setRefreshing(false);
                }
            });
        }

        SharedPreferences sp = getSharedPreferences(PREFS_RW, 0);
        String json = sp.getString("result", "");

        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("result");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                if (object.getString("tbl_insti_buzz_cate_name").equals("Activities")) {
                    if (object.getString("tbl_scl_inst_buzz_detl_write_status").equals("Active")) {
                        add_layout.setVisibility(View.VISIBLE);
                    } else {
                        add_layout.setVisibility(View.GONE);
                    }
                    break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        iv_add_more_activities.setOnClickListener(view -> {
            if (isInternetOn()) {
                Intent i = new Intent(ActivitiesActivity.this, IntermidiateScreenActivityPublication.class);
                i.putExtra("str_tool_title", "Add Activity");
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            } else {
                Toast.makeText(ActivitiesActivity.this, "No network Please connect with network for update", Toast.LENGTH_SHORT).show();
            }
        });

        SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
        selToolColor = settingsColor.getString("tool", "");
        selDrawerColor = settingsColor.getString("drawer", "");
        selStatusColor = settingsColor.getString("status", "");
        selTextColor1 = settingsColor.getString("text1", "");
        selTextColor2 = settingsColor.getString("text2", "");

        appBarLayout.setBackgroundColor(Color.parseColor(selToolColor));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(selStatusColor));
        }
        tool_title.setTextColor(Color.parseColor(selTextColor1));
        mSwipeRefreshLayout.setColorScheme(R.color.text_gray);
        iv_add_more_activities.setBackgroundColor(Color.parseColor(selToolColor));
        GradientDrawable bgShape = (GradientDrawable) add_layout.getBackground();
        bgShape.setColor(Color.parseColor(selToolColor));
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
            Log.d("result", result);
            if (!result.equals("{\"result\":null}")) {
                try {
                    JSONObject jo = new JSONObject(result);
                    JSONArray ja = jo.getJSONArray("result");
                    batch_array = new String[ja.length() + 1];
                    selBatch = "All Batches";
                    batch_array[0] = "All Batches";
                    for (int i = 0; i < ja.length(); i++) {
                        jo = ja.getJSONObject(i);
                        batch_array[i + 1] = jo.getString("tbl_batch_name");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setBatch();
            } else {
                String batch_array[] = {"All Batches"};
                ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<>(ctx, R.layout.spinner_list_item1, batch_array);
                dataAdapter1.setDropDownViewResource(R.layout.spinner_list_item1);
                batch_spinner.setAdapter(dataAdapter1);
                Toast.makeText(ctx, "No Batch", Toast.LENGTH_SHORT).show();
            }

            super.onPostExecute(result);
        }

    }

    private void setBatch() {
        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<>(this, R.layout.spinner_list_item1, batch_array);
        dataAdapter1.setDropDownViewResource(R.layout.spinner_list_item1);
        batch_spinner.setAdapter(dataAdapter1);
        batch_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) adapterView.getChildAt(0)).setTextColor(getResources().getColor(R.color.text_gray));
                if (batch_array[i].equals("All Batches")) {
                    selBatch = "All Batches";
                    listItms(selBatch);
                } else {
                    selBatch = batch_array[i];
                    listItms(selBatch);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    private void getAllActivities(String acti) {
        if (acti.equals("{\"result\":null}")) {
            no_aciev.setVisibility(View.VISIBLE);
            activitiesArray.clear();
        } else {
            try {
                JSONObject jsonRootObject = new JSONObject(acti);
                JSONArray jsonArray = jsonRootObject.getJSONArray("result");
                activitiesArray.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    dbact = new DataBeanActivities();
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    activiId = jsonObject.getString("tbl_school_activities_id");
                    activiTitle = jsonObject.getString("tbl_school_activities_title");
                    activiDesc = jsonObject.getString("tbl_school_activities_desc");
                    activiDate = jsonObject.getString("tbl_school_activities_date");
                    activiImg = jsonObject.getString("tbl_school_activities_img");
                    dbact.setBatch(jsonObject.getString("tbl_stnt_prsnl_data_section"));
                    dbact.setActiviId(activiId);
                    dbact.setActiviTitle(activiTitle);
                    dbact.setActiviDesc(activiDesc);
                    dbact.setActiviDate(activiDate);
                    dbact.setActiviImg(activiImg);
                    activitiesArray.add(dbact);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (activitiesArray.size() != 0) {
                listItms("All Batches");
            }
        }
    }


    @SuppressLint("StaticFieldLeak")
    public class GetAllActivities extends AsyncTask<String, String, String> {
        Context ctx;

        GetAllActivities(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            mSwipeRefreshLayout.setRefreshing(true);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            String schoolId = params[0];
            String userSection = params[1];
            String userrType = params[2];
            skip = params[3];
            String data;

            try {
                URL url = new URL(ALL_ACTIVITIES);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                data = URLEncoder.encode("schoolId", "UTF-8") + "=" + URLEncoder.encode(schoolId, "UTF-8") + "&" +
                        URLEncoder.encode("userSection", "UTF-8") + "=" + URLEncoder.encode(userSection, "UTF-8") + "&" +
                        URLEncoder.encode("userrType", "UTF-8") + "=" + URLEncoder.encode(userrType, "UTF-8");
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
            Log.e("ACHIVE", "" + result);
            mSwipeRefreshLayout.setRefreshing(false);
            sp_acti = getSharedPreferences(PREFS_ACTI, 0);
            se_acti = sp_acti.edit();
            se_acti.clear();
            se_acti.putString("json_acti", result);
            se_acti.apply();

            if (result.equals("{\"result\":null}")) {
                if (activitiesArray.size() > 0) {
                    no_aciev.setVisibility(View.GONE);
                    activities_list.removeFooterView(footerView);
                } else {
                    no_aciev.setVisibility(View.VISIBLE);
                    activities_list.setVisibility(View.GONE);
                }
            } else {
                try {
                    activitiesArray.clear();
                    no_aciev.setVisibility(View.GONE);
                    activities_list.setVisibility(View.VISIBLE);
                    JSONObject jsonRootObject = new JSONObject(result);
                    JSONArray jsonArray = jsonRootObject.getJSONArray("result");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        dbact = new DataBeanActivities();
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        activiId = jsonObject.getString("tbl_school_activities_id");
                        activiTitle = jsonObject.getString("tbl_school_activities_title");
                        activiDesc = jsonObject.getString("tbl_school_activities_desc");
                        activiDate = jsonObject.getString("tbl_school_activities_date");
                        activiImg = jsonObject.getString("tbl_school_activities_img");
                        dbact.setBatch(jsonObject.getString("tbl_stnt_prsnl_data_section"));
                        dbact.setActiviId(activiId);
                        dbact.setActiviTitle(activiTitle);
                        dbact.setActiviDesc(activiDesc);
                        dbact.setActiviDate(activiDate);
                        dbact.setActiviImg(activiImg);
                        activitiesArray.add(dbact);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (activitiesArray.size() != 0) {
                    listItms(selBatch);
                }
            }
            super.onPostExecute(result);
        }
    }

    private void listItms(String batch) {

        ArrayList<DataBeanActivities> arrayList = new ArrayList<>();

        if (batch.equals("All Batches")) {
            ArrayList<String> ids = new ArrayList<>();
            for (int i = 0; i < activitiesArray.size(); i++) {
                if (!ids.contains(activitiesArray.get(i).getActiviId())) {
                    ids.add(activitiesArray.get(i).getActiviId());
                }
            }
            for (int i = 0; i < ids.size(); i++) {
                for (int j = 0; j < activitiesArray.size(); j++) {
                    if (ids.get(i).equals(activitiesArray.get(j).getActiviId())) {
                        arrayList.add(activitiesArray.get(j));
                        break;
                    }
                }
            }
            Collections.reverse(arrayList);
        } else {
            for (int i = 0; i < activitiesArray.size(); i++) {
                if (activitiesArray.get(i).getBatch().equals(batch)) {
                    arrayList.add(activitiesArray.get(i));
                }
            }
        }

        activitiesListAdaptor = new ActivitiesListAdaptor(ActivitiesActivity.this, R.layout.achivement_list_item1, arrayList, "activities");
        activities_list.setAdapter(activitiesListAdaptor);
        activities_list.removeFooterView(footerView);//Add view to list view as footer view
        activities_list.setOnItemClickListener((adapterView, view, i, l) -> {
            String achId = arrayList.get(i).getActiviId();
            String achImg = arrayList.get(i).getActiviImg();
            String achCate = "activities";
            String achTitle = arrayList.get(i).getActiviTitle();
            String achDesc = arrayList.get(i).getActiviDesc();
            String acgDate = arrayList.get(i).getActiviDate();
            Intent i11 = new Intent(ActivitiesActivity.this, AchievmentDetailsActivity.class);
            i11.putExtra("activiId", achId);
            i11.putExtra("achImg", achImg);
            i11.putExtra("achCate", achCate);
            i11.putExtra("achTitle", achTitle);
            i11.putExtra("achDesc", achDesc);
            i11.putExtra("acgDate", acgDate);
            startActivity(i11);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        if (arrayList.size() < 9) {
            activities_list.removeFooterView(footerView);//Add view to list view as footer view
        }

        SharedPreferences sp = getSharedPreferences(PREFS_RW, 0);
        String json = sp.getString("result", "");

        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("result");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                if (object.getString("tbl_insti_buzz_cate_name").equals("Activities")) {
                    if (object.getString("tbl_scl_inst_buzz_detl_write_status").equals("Active")) {
                        activities_list.setOnItemLongClickListener((arg0, arg1, pos, id) -> {
                            if (isInternetOn()) {
                                Intent intent = new Intent(ActivitiesActivity.this, AddNewActivity.class);
                                intent.putExtra("type", "update");
                                intent.putExtra("title", arrayList.get(pos).getActiviTitle());
                                intent.putExtra("description", arrayList.get(pos).getActiviDesc());
                                intent.putExtra("date", arrayList.get(pos).getActiviDate());
                                intent.putExtra("image", arrayList.get(pos).getActiviImg());
                                intent.putExtra("id", arrayList.get(pos).getActiviId());
                                startActivity(intent);
                            } else {
                                Toast.makeText(ActivitiesActivity.this, "No network Please connect with network for update", Toast.LENGTH_SHORT).show();
                            }

                            return true;
                        });
                    }
                    break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public final boolean isInternetOn() {
        ConnectivityManager connec = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

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
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override

    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
