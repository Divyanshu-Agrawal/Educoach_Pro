package com.aaptrix.activitys.student;

import com.aaptrix.activitys.admin.AddNewMaterial;
import com.aaptrix.adaptor.StudyMaterialAdaptor;
import com.aaptrix.adaptor.VideoAdapter;
import com.aaptrix.databeans.StudyMaterialData;
import com.aaptrix.R;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.aaptrix.databeans.VideosData;
import com.google.android.material.appbar.AppBarLayout;
import com.squareup.picasso.Picasso;

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
import java.util.Arrays;
import java.util.Objects;

import static com.aaptrix.tools.HttpUrl.ALL_BATCHS;
import static com.aaptrix.tools.HttpUrl.ALL_STUDY_MATERIAL;
import static com.aaptrix.tools.HttpUrl.GET_SUBS;
import static com.aaptrix.tools.HttpUrl.REMOVE_STUDY_MATERIAL;
import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREFS_RW;
import static com.aaptrix.tools.SPClass.PREF_COLOR;

public class StudyMaterial extends AppCompatActivity {

    AppBarLayout appBarLayout;
    String selToolColor, selStatusColor, selTextColor1;
    TextView tool_title;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    TextView no_material;
    ListView listView;
    LinearLayout add_layout;
    ImageView addMaterial;
    String userId, userSchoolId, userRoleId, userrType, userSection, userName, restricted;
    String skip;
    StudyMaterialAdaptor studyMaterialAdaptor;
    String[] batch_array = {"All Batches"};
    Spinner batch_spinner;
    String selBatch = "All";
    String[] subjects;
    ArrayList<String> subject_array = new ArrayList<>();
    ArrayList<StudyMaterialData> studyMaterialArray = new ArrayList<>(), array = new ArrayList<>();
    StudyMaterialData data;
    ImageButton filter;
    private String selSubject = "All Subjects", disable;
    LinearLayout search_layout;
    ImageButton search, searchBtn;
    EditText searchBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_material);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        appBarLayout = findViewById(R.id.appBarLayout);
        tool_title = findViewById(R.id.tool_title);
        no_material = findViewById(R.id.no_material);
        listView = findViewById(R.id.material_list);
        mSwipeRefreshLayout = findViewById(R.id.activity_main_swipe_refresh_layout);
        add_layout = findViewById(R.id.add_layout);
        addMaterial = findViewById(R.id.add_material);
        filter = findViewById(R.id.filter);
        batch_spinner = findViewById(R.id.batch_spinner);
        mSwipeRefreshLayout.setRefreshing(false);
        listView.setEnabled(true);
        search = findViewById(R.id.search);
        search_layout = findViewById(R.id.search_layout);
        searchBox = findViewById(R.id.search_txt);
        searchBtn = findViewById(R.id.search_btn);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        userId = settings.getString("userID", "");
        userSchoolId = settings.getString("str_school_id", "");
        userRoleId = settings.getString("str_role_id", "");
        userSection = settings.getString("userSection", "");
        userrType = settings.getString("userrType", "");
        userName = settings.getString("userName", "");
        restricted = settings.getString("restricted", "");

        selSubject = getIntent().getStringExtra("sub");

        if (userrType.equals("Student")) {
            tool_title.setText(selSubject);
        }

        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<>(this, R.layout.spinner_list_item1, new String[]{"All Batches"});
        dataAdapter1.setDropDownViewResource(R.layout.spinner_list_item1);
        batch_spinner.setAdapter(dataAdapter1);

        if (userrType.equals("Admin") || userrType.equals("Teacher")) {
            GetAllBatches b1 = new GetAllBatches(this);
            b1.execute(userSchoolId);
        }

        SharedPreferences sp = getSharedPreferences(PREFS_RW, 0);
        String result = sp.getString("result", "");

        try {
            JSONObject jsonObject = new JSONObject(result);
            JSONArray jsonArray = jsonObject.getJSONArray("result");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                if (object.getString("tbl_insti_buzz_cate_name").equals("Study Materials")) {
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

        addMaterial.setOnClickListener(view -> {
            if (isInternetOn()) {
                Intent i = new Intent(this, AddNewMaterial.class);
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            } else {
                Toast.makeText(this, "No network Please connect with network", Toast.LENGTH_SHORT).show();
            }
        });

        if (userrType.equals("Guest")) {
            batch_spinner.setVisibility(View.GONE);
            filter.setVisibility(View.GONE);
        }

        if (!userrType.equals("Student")) {
            GetSubject subject = new GetSubject(this);
            subject.execute(userSchoolId, "All");
        } else {
            filter.setVisibility(View.GONE);
        }

        if (isInternetOn()) {
            if (userrType.equals("Student")) {
                selBatch = userSection;
                GetMaterial getMaterial = new GetMaterial(this);
                getMaterial.execute(userSchoolId, userSection, userrType, "0");
                batch_spinner.setVisibility(View.GONE);
            } else {
                GetMaterial getMaterial = new GetMaterial(this);
                getMaterial.execute(userSchoolId, "All", userrType, "0");
            }
        } else {
            Toast.makeText(this, "Please connect to internet", Toast.LENGTH_SHORT).show();
        }

        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            if (isInternetOn()) {
                listView.setEnabled(false);
                array.clear();
                studyMaterialArray.clear();
                if (userrType.equals("Student")) {
                    selBatch = userSection;
                    GetMaterial getMaterial = new GetMaterial(this);
                    getMaterial.execute(userSchoolId, userSection, userrType, "0");
                    batch_spinner.setVisibility(View.GONE);
                } else {
                    GetMaterial getMaterial = new GetMaterial(this);
                    getMaterial.execute(userSchoolId, selBatch, userrType, "0");
                }
            } else {
                Toast.makeText(this, "No network Please connect with network for update", Toast.LENGTH_SHORT).show();
                mSwipeRefreshLayout.setRefreshing(false);
                listView.setEnabled(true);
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            filter.setImageTintList(ColorStateList.valueOf(Color.parseColor(selTextColor1)));
        }
        tool_title.setTextColor(Color.parseColor(selTextColor1));
        mSwipeRefreshLayout.setColorScheme(R.color.text_gray);
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
                    selBatch = "All";
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
                if (batch_array[i].equals("All Batches")) {
                    selBatch = "All";
                    GetMaterial getMaterial = new GetMaterial(StudyMaterial.this);
                    getMaterial.execute(userSchoolId, "All", userrType, "0");
                    GetSubject subject = new GetSubject(StudyMaterial.this);
                    subject.execute(userSchoolId, "All");
                } else {
                    selBatch = batch_array[i];
                    GetMaterial getMaterial = new GetMaterial(StudyMaterial.this);
                    getMaterial.execute(userSchoolId, selBatch, userrType, "0");
                    String section = "[{\"userName\":\"" + selBatch + "\"}]";
                    GetSubject subject = new GetSubject(StudyMaterial.this);
                    subject.execute(userSchoolId, section);
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
            array.clear();
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
                        URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8") + "&" +
                        URLEncoder.encode("tbl_users_nm", "UTF-8") + "=" + URLEncoder.encode(userName, "UTF-8") + "&" +
                        URLEncoder.encode("restricted_access", "UTF-8") + "=" + URLEncoder.encode(restricted, "UTF-8");
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
            listView.setEnabled(true);
            try {
                array.clear();
                studyMaterialArray.clear();
                JSONObject jsonRootObject = new JSONObject(result);
                if (!result.contains("\"result\":null")) {
                    JSONArray jsonArray = jsonRootObject.getJSONArray("result");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        data = new StudyMaterialData();
                        JSONObject jObject = jsonArray.getJSONObject(i);
                        data.setTitle(jObject.getString("tbl_school_studymaterial_title"));
                        data.setId(jObject.getString("tbl_school_studymaterial_id"));
                        data.setDescription(jObject.getString("tbl_school_studymaterial_desc"));
                        data.setUrl(jObject.getString("tbl_school_studymaterial_docfile").split(","));
                        data.setSubject(jObject.getString("subject_name"));
                        data.setBatch(jObject.getString("tbl_stnt_prsnl_data_section"));
                        data.setPermission(jObject.getString("download_permission"));
                        data.setTags(jObject.getString("tbl_school_studymaterial_tag"));
                        array.add(data);
                    }
                }
                if (userrType.equals("Student")) {
                    if (!result.contains("\"studyMaterialsStudent\":null")) {
                        JSONArray jsonArray = jsonRootObject.getJSONArray("studyMaterialsStudent");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            data = new StudyMaterialData();
                            JSONObject jObject = jsonArray.getJSONObject(i);
                            data.setTitle(jObject.getString("tbl_school_studymaterial_title"));
                            data.setId(jObject.getString("tbl_school_studymaterial_id"));
                            data.setDescription(jObject.getString("tbl_school_studymaterial_desc"));
                            data.setUrl(jObject.getString("tbl_school_studymaterial_docfile").split(","));
                            data.setSubject(jObject.getString("subject_name"));
                            data.setBatch(jObject.getString("tbl_stnt_prsnl_data_section"));
                            data.setPermission(jObject.getString("download_permission"));
                            data.setTags(jObject.getString("tbl_school_studymaterial_tag"));
                            array.add(data);
                        }
                    }
                    disable = jsonRootObject.getString("DisableSubject");
                    for (int i = 0; i < array.size(); i++) {
                        if (!disable.contains(array.get(i).getSubject())) {
                            studyMaterialArray.add(array.get(i));
                        }
                    }
                }
                if (userrType.equals("Teacher")) {
                    String restricted = jsonRootObject.getString("RistrictedSubject");
                    if (restricted.equals("null")) {
                        studyMaterialArray.addAll(array);
                    } else {
                        for (int i = 0; i < array.size(); i++) {
                            if (restricted.contains(array.get(i).getSubject())) {
                                studyMaterialArray.add(array.get(i));
                            }
                        }
                    }
                } else {
                    studyMaterialArray.addAll(array);
                }
                if (studyMaterialArray.size() > 0) {
                    no_material.setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);
                    listItms(selSubject, disable);
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

    private void listItms(String subject, String disable) {
        ArrayList<StudyMaterialData> arrayList = new ArrayList<>();
        listView.setVisibility(View.VISIBLE);
        ArrayList<String> ids = new ArrayList<>();
        if (!userrType.equals("Student")) {
            if (subject.equals("All Subjects")) {
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
                            if (studyMaterialArray.get(j).getSubject().equals(subject) || studyMaterialArray.get(j).getSubject().equals("0")) {
                                arrayList.add(studyMaterialArray.get(j));
                                break;
                            }
                        }
                    }
                }
            }
        } else {
            if (subject.equals("All Subjects")) {
                for (int i = 0; i < studyMaterialArray.size(); i++) {
                    if (!ids.contains(studyMaterialArray.get(i).getId())) {
                        ids.add(studyMaterialArray.get(i).getId());
                    }
                }
                for (int i = 0; i < ids.size(); i++) {
                    for (int j = 0; j < studyMaterialArray.size(); j++) {
                        if (ids.get(i).equals(studyMaterialArray.get(j).getId()) && !disable.contains(studyMaterialArray.get(j).getSubject())) {
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
                            if (studyMaterialArray.get(j).getSubject().equals(subject) || studyMaterialArray.get(j).getSubject().equals("0")) {
                                arrayList.add(studyMaterialArray.get(j));
                                break;
                            }
                        }
                    }
                }
            }
        }

        if (arrayList.size() == 0) {
            no_material.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        } else {
            no_material.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        }

        listView.setEnabled(true);
        studyMaterialAdaptor = new StudyMaterialAdaptor(this, R.layout.list_study_material, arrayList);
        listView.setAdapter(studyMaterialAdaptor);
        studyMaterialAdaptor.notifyDataSetChanged();

        searchBox.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        searchBox.setSingleLine(true);
        searchBox.setInputType(InputType.TYPE_CLASS_TEXT);
        searchBox.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (searchBox.getText().toString().isEmpty()) {
                    studyMaterialAdaptor = new StudyMaterialAdaptor(StudyMaterial.this, R.layout.list_study_material, arrayList);
                    listView.setAdapter(studyMaterialAdaptor);
                    studyMaterialAdaptor.notifyDataSetChanged();
                } else {
                    filterSearch(arrayList, searchBox.getText().toString());
                }
                hideKeyboard(v);
                return true;
            }
            return false;
        });

        search.setOnClickListener(v -> {
            if (search_layout.getVisibility() == View.VISIBLE) {
                search_layout.setVisibility(View.GONE);
                studyMaterialAdaptor = new StudyMaterialAdaptor(this, R.layout.list_study_material, arrayList);
                listView.setAdapter(studyMaterialAdaptor);
                studyMaterialAdaptor.notifyDataSetChanged();
                search.setImageResource(R.drawable.search_icon);
            } else {
                search_layout.setVisibility(View.VISIBLE);
                search.setImageResource(R.drawable.back_icon);
            }
        });

        searchBtn.setOnClickListener(v -> {
            if (searchBox.getText().toString().isEmpty()) {
                studyMaterialAdaptor = new StudyMaterialAdaptor(this, R.layout.list_study_material, arrayList);
                listView.setAdapter(studyMaterialAdaptor);
                studyMaterialAdaptor.notifyDataSetChanged();
            } else {
                filterSearch(arrayList, searchBox.getText().toString());
            }
            hideKeyboard(v);
        });

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(this, StudyMaterialDetail.class);
            intent.putExtra("title", arrayList.get(position).getTitle());
            intent.putExtra("description", arrayList.get(position).getDescription());
            intent.putExtra("id", arrayList.get(position).getId());
            intent.putExtra("url", arrayList.get(position).getUrl());
            intent.putExtra("permission", arrayList.get(position).getPermission());
            intent.putExtra("tags", arrayList.get(position).getTags());
            intent.putExtra("subject", arrayList.get(position).getSubject());
            startActivity(intent);
        });

        SharedPreferences sp = getSharedPreferences(PREFS_RW, 0);
        String json = sp.getString("result", "");

        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("result");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                if (object.getString("tbl_insti_buzz_cate_name").equals("Study Materials")) {
                    if (object.getString("tbl_scl_inst_buzz_detl_write_status").equals("Active")) {
                        listView.setOnItemLongClickListener((arg0, arg1, pos, id) -> {
                            if (isInternetOn()) {
                                AlertDialog.Builder alert = new AlertDialog.Builder(this, R.style.DialogTheme);
                                alert.setTitle("Are you sure you want to delete").setPositiveButton("Yes", (dialog, which) -> {
                                    RemoveStudyMaterial removeStudyMaterial = new RemoveStudyMaterial(this);
                                    removeStudyMaterial.execute(userSchoolId, arrayList.get(pos).getId());
                                }).setNegativeButton("No", null);
                                AlertDialog alertDialog = alert.create();
                                alertDialog.setCancelable(false);
                                alertDialog.show();
                                Button theButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                                Button theButton1 = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                                theButton.setTextColor(Color.parseColor(selToolColor));
                                theButton1.setTextColor(Color.parseColor(selToolColor));
                            } else {
                                Toast.makeText(this, "No network Please connect with network for update", Toast.LENGTH_SHORT).show();
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

    private void filterSearch(ArrayList<StudyMaterialData> array, String searchTxt) {
        ArrayList<StudyMaterialData> arrayList = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            if (array.get(i).getTitle().toLowerCase().contains(searchTxt.toLowerCase())) {
                arrayList.add(array.get(i));
            }
        }
        if (arrayList.size() == 0) {
            no_material.setVisibility(View.VISIBLE);
            no_material.setText("Nothing Found");
        } else {
            listView.setEnabled(true);
            studyMaterialAdaptor = new StudyMaterialAdaptor(this, R.layout.list_study_material, arrayList);
            listView.setAdapter(studyMaterialAdaptor);
            studyMaterialAdaptor.notifyDataSetChanged();
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        assert inputMethodManager != null;
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @SuppressLint("StaticFieldLeak")
    public class GetSubject extends AsyncTask<String, String, String> {
        Context ctx;

        GetSubject(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            String school_id = params[0];
            String batchArray = params[1];
            String data;

            try {

                URL url = new URL(GET_SUBS);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                data = URLEncoder.encode("school_id", "UTF-8") + "=" + URLEncoder.encode(school_id, "UTF-8") + "&" +
                        URLEncoder.encode("batchArray", "UTF-8") + "=" + URLEncoder.encode(batchArray, "UTF-8") + "&" +
                        URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8") + "&" +
                        URLEncoder.encode("tbl_users_nm", "UTF-8") + "=" + URLEncoder.encode(userName, "UTF-8") + "&" +
                        URLEncoder.encode("restricted_access", "UTF-8") + "=" + URLEncoder.encode(restricted, "UTF-8");
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
            if (result.equals("{\"SubjectList\":null}")) {
                subject_array.add("All Subjects");
            } else {
                try {
                    subject_array.clear();
                    JSONObject jsonRootObject = new JSONObject(result);
                    JSONArray jsonArray = jsonRootObject.getJSONArray("SubjectList");
                    subjects = new String[jsonArray.length()];
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        subjects[i] = jsonObject.getString("tbl_batch_subjct_name");
                    }
                    if (userrType.equals("Student")) {
                        String object = jsonRootObject.getString("DisableSubject");
                        subject_array.add("All Subjects");
                        for (String subject : subjects) {
                            if (!object.contains(subject)) {
                                subject_array.add(subject);
                            }
                        }
                    } else if (userrType.equals("Teacher")) {
                        String object = jsonRootObject.getString("RistrictedSubject");
                        subject_array.add("All Subjects");
                        if (object.equals("null")) {
                            subject_array.addAll(Arrays.asList(subjects));
                        } else {
                            for (String subject : subjects) {
                                if (object.contains(subject)) {
                                    subject_array.add(subject);
                                }
                            }
                        }
                    } else {
                        subject_array.add("All Subjects");
                        subject_array.addAll(Arrays.asList(subjects));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setSubject();
                super.onPostExecute(result);
            }
        }
    }

    private void setSubject() {
        filter.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(this, v);
            popupMenu.getMenuInflater().inflate(R.menu.filter_menu, popupMenu.getMenu());
            if (subject_array.size() != 0) {
                for (int i = 0; i < subject_array.size(); i++) {
                    popupMenu.getMenu().add(1, i, i, subject_array.get(i));
                }
                popupMenu.show();
            } else {
                Toast.makeText(this, "Nothing to show", Toast.LENGTH_SHORT).show();
            }

            popupMenu.setOnMenuItemClickListener(item1 -> {
                selSubject = item1.getTitle().toString();
                listItms(selSubject, disable);
                return true;
            });
        });
    }

    @SuppressLint("StaticFieldLeak")
    public class RemoveStudyMaterial extends AsyncTask<String, String, String> {
        Context ctx;

        RemoveStudyMaterial(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String schoolId = params[0];
            String materialID = params[1];
            String data;

            try {
                URL url = new URL(REMOVE_STUDY_MATERIAL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                data = URLEncoder.encode("schoolId", "UTF-8") + "=" + URLEncoder.encode(schoolId, "UTF-8") + "&" +
                        URLEncoder.encode("studyMaterialId", "UTF-8") + "=" + URLEncoder.encode(materialID, "UTF-8");
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
            if (!result.isEmpty()) {
                startActivity(new Intent(ctx, StudyMaterial.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP).putExtra("sub", "All Subjects"));
            } else {
                Toast.makeText(ctx, "Some Error", Toast.LENGTH_SHORT).show();
            }
        }
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
