package com.aaptrix.activitys.guest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.aaptrix.R;
import com.aaptrix.activitys.student.StudyMaterialDetail;
import com.aaptrix.adaptor.StudyMaterialAdaptor;
import com.aaptrix.databeans.StudyMaterialData;
import com.google.android.material.appbar.AppBarLayout;

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
import java.util.ArrayList;
import java.util.Objects;

import static com.aaptrix.tools.HttpUrl.ABOUT_SCHOOL_INFO;
import static com.aaptrix.tools.HttpUrl.ALL_STUDY_MATERIAL;
import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREF_COLOR;

public class GuestMaterial extends AppCompatActivity {

    ListView listView;
    ArrayList<StudyMaterialData> studyMaterialArray = new ArrayList<>(), array = new ArrayList<>();
    AppBarLayout appBarLayout;
    String selToolColor, selStatusColor, selTextColor1;
    TextView tool_title, no_material;
    SharedPreferences sp;
    String[] batch_array = {"All Courses"};
    Spinner batch_spinner;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    String userSchoolId, userrType, url;
    StudyMaterialData data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_material);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        appBarLayout = findViewById(R.id.appBarLayout);
        tool_title = findViewById(R.id.tool_title);
        listView = findViewById(R.id.material_list);
        no_material = findViewById(R.id.no_material);
        mSwipeRefreshLayout = findViewById(R.id.activity_main_swipe_refresh_layout);
        batch_spinner = findViewById(R.id.batch_spinner);
        mSwipeRefreshLayout.setRefreshing(false);
        listView.setEnabled(true);

        SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
        selToolColor = settingsColor.getString("tool", "");
        selStatusColor = settingsColor.getString("status", "");
        selTextColor1 = settingsColor.getString("text1", "");

        sp = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        userSchoolId = sp.getString("str_school_id", "");
        userrType = sp.getString("userrType", "");
        url = sp.getString("imageUrl", "") + userSchoolId + "/InstituteVideo/";

        appBarLayout.setBackgroundColor(Color.parseColor(selToolColor));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(selStatusColor));
        }
        tool_title.setTextColor(Color.parseColor(selTextColor1));

        GetAllBatches b1 = new GetAllBatches(this);
        b1.execute(userSchoolId);

        if (isInternetOn()) {
            GetMaterial getMaterial = new GetMaterial(this);
            getMaterial.execute(userSchoolId, "All", userrType, "0");
        } else {
            String json = sp.getString("studymaterial", "");
            try {
                JSONObject jsonRootObject = new JSONObject(json);
                if (!json.contains("\"guestMaterials\":null")) {
                    JSONArray jsonArray = jsonRootObject.getJSONArray("guestMaterials");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        data = new StudyMaterialData();
                        JSONObject jObject = jsonArray.getJSONObject(i);
                        data.setTitle(jObject.getString("tbl_school_studymaterial_title"));
                        data.setId(jObject.getString("tbl_school_studymaterial_id"));
                        data.setDescription(jObject.getString("tbl_school_studymaterial_desc"));
                        data.setUrl(jObject.getString("tbl_school_studymaterial_docfile").split(","));
                        data.setSubject(jObject.getString("subject_name"));
                        data.setBatch(jObject.getString("tbl_course_name"));
                        data.setPermission(jObject.getString("download_permission"));
                        studyMaterialArray.add(data);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (studyMaterialArray.size() > 0) {
                no_material.setVisibility(View.GONE);
                listItems("All Courses");
            } else {
                no_material.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);
            }
        }

        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            if (isInternetOn()) {
                mSwipeRefreshLayout.setRefreshing(true);
                listView.setEnabled(false);
                array.clear();
                studyMaterialArray.clear();
                GetMaterial getMaterial = new GetMaterial(this);
                getMaterial.execute(userSchoolId, "All", userrType, "0");
            } else {
                Toast.makeText(this, "No network Please connect with network for update", Toast.LENGTH_SHORT).show();
                mSwipeRefreshLayout.setRefreshing(false);
                listView.setEnabled(true);
            }
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

                URL url = new URL(ABOUT_SCHOOL_INFO);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                data = URLEncoder.encode("schoolId", "UTF-8") + "=" + URLEncoder.encode(school_id, "UTF-8");
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
            if (!result.equals("{\"result\":null}")) {
                try {
                    JSONObject jsonRootObject = new JSONObject(result);
                    JSONArray jsonArray = jsonRootObject.getJSONArray("result");
                    batch_array = new String[jsonArray.length() + 1];
                    batch_array[0] = "All Courses";
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        batch_array[i + 1] = jsonObject.getString("tbl_abt_schl_more_info_name");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setBatch();
            } else {
                String batch_array[] = {"All Courses"};
                ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<>(ctx, R.layout.spinner_list_item1, batch_array);
                dataAdapter1.setDropDownViewResource(R.layout.spinner_list_item1);
                batch_spinner.setAdapter(dataAdapter1);
                Toast.makeText(ctx, "No Course", Toast.LENGTH_SHORT).show();
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
                if (batch_array[i].equals("All Courses")) {
                    listItems("All Courses");
                } else {
                    listItems(batch_array[i]);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    public class GetMaterial extends AsyncTask<String, String, String> {
        Context ctx;

        GetMaterial(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            mSwipeRefreshLayout.setRefreshing(true);
            listView.setEnabled(false);
            studyMaterialArray.clear();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String schoolId = params[0];
            String userSection = params[1];
            String userrType = params[2];
            String data;

            try {
                URL url = new URL(ALL_STUDY_MATERIAL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                data = URLEncoder.encode("schoolId", "UTF-8") + "=" + URLEncoder.encode(schoolId, "UTF-8") + "&" +
                        URLEncoder.encode("userSection", "UTF-8") + "=" + URLEncoder.encode(userSection, "UTF-8") + "&" +
                        URLEncoder.encode("userrType", "UTF-8") + "=" + URLEncoder.encode(userrType, "UTF-8") + "&" +
                        URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode("0", "UTF-8");
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
            Log.e("study material", result);
            mSwipeRefreshLayout.setRefreshing(false);
            try {
                SharedPreferences sp = ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                sp.edit().putString("studymaterial", result).apply();
                JSONObject jsonRootObject = new JSONObject(result);
                if (!result.contains("\"guestMaterials\":null")) {
                    JSONArray jsonArray = jsonRootObject.getJSONArray("guestMaterials");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        data = new StudyMaterialData();
                        JSONObject jObject = jsonArray.getJSONObject(i);
                        data.setTitle(jObject.getString("tbl_school_studymaterial_title"));
                        data.setId(jObject.getString("tbl_school_studymaterial_id"));
                        data.setDescription(jObject.getString("tbl_school_studymaterial_desc"));
                        data.setUrl(jObject.getString("tbl_school_studymaterial_docfile").split(","));
                        data.setSubject(jObject.getString("subject_name"));
                        data.setBatch(jObject.getString("tbl_course_name"));
                        data.setPermission(jObject.getString("download_permission"));
                        studyMaterialArray.add(data);
                    }
                }
                if (studyMaterialArray.size() > 0) {
                    no_material.setVisibility(View.GONE);
                    listItems("All Courses");
                } else {
                    no_material.setVisibility(View.VISIBLE);
                    listView.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            super.onPostExecute(result);
        }

    }

    private void listItems(String course) {

        ArrayList<StudyMaterialData> arrayList = new ArrayList<>();
        ArrayList<String> ids = new ArrayList<>();

        if (course.equals("All Courses")) {
            for (int i = 0; i < studyMaterialArray.size(); i++) {
                if (!ids.contains(studyMaterialArray.get(i).getId())) {
                    ids.add(studyMaterialArray.get(i).getId());
                }
            }
            for (int i = 0; i < ids.size(); i++) {
                for (int j = 0; j < studyMaterialArray.size(); j++) {
                    if (ids.get(i).equals(studyMaterialArray.get(j).getId())) {
                        arrayList.add(studyMaterialArray.get(j));
                        break;
                    }
                }
            }
        } else {
            for (int i = 0; i < studyMaterialArray.size(); i++) {
                if (!ids.contains(studyMaterialArray.get(i).getId())) {
                    ids.add(studyMaterialArray.get(i).getId());
                }
            }
            for (int i = 0; i < ids.size(); i++) {
                for (int j = 0; j < studyMaterialArray.size(); j++) {
                    if (ids.get(i).equals(studyMaterialArray.get(j).getId())) {
                        if (studyMaterialArray.get(j).getBatch().contentEquals(course) || studyMaterialArray.get(j).getBatch().equals("0")) {
                            arrayList.add(studyMaterialArray.get(j));
                            break;
                        }
                    }
                }
            }
        }

        if (arrayList.size() == 0) {
            no_material.setVisibility(View.VISIBLE);
        } else {
            no_material.setVisibility(View.GONE);
        }

        listView.setEnabled(true);
        StudyMaterialAdaptor studyMaterialAdaptor = new StudyMaterialAdaptor(this, R.layout.list_study_material, arrayList);
        listView.setAdapter(studyMaterialAdaptor);
        studyMaterialAdaptor.notifyDataSetChanged();

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(this, StudyMaterialDetail.class);
            intent.putExtra("title", arrayList.get(position).getTitle());
            intent.putExtra("description", arrayList.get(position).getDescription());
            intent.putExtra("id", arrayList.get(position).getId());
            intent.putExtra("url", arrayList.get(position).getUrl());
            intent.putExtra("permission", arrayList.get(position).getPermission());
            startActivity(intent);
        });
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