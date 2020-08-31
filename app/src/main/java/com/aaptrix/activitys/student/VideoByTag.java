package com.aaptrix.activitys.student;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
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
import android.widget.Toast;

import com.aaptrix.R;
import com.aaptrix.activitys.admin.AddNewVideo;
import com.aaptrix.adaptor.VideoAdapter;
import com.aaptrix.databeans.VideosData;
import com.google.android.material.appbar.AppBarLayout;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import static com.aaptrix.tools.HttpUrl.ALL_BATCHS;
import static com.aaptrix.tools.HttpUrl.ALL_VIDEOS;
import static com.aaptrix.tools.HttpUrl.GET_SUBS;
import static com.aaptrix.tools.HttpUrl.REMOVE_VIDEOS;
import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREFS_RW;
import static com.aaptrix.tools.SPClass.PREF_COLOR;

public class VideoByTag extends AppCompatActivity {

    ListView listView;
    VideoAdapter videoAdapter;
    ArrayList<VideosData> videosArray = new ArrayList<>(), array = new ArrayList<>();
    VideosData videosData;
    AppBarLayout appBarLayout;
    String selToolColor, selStatusColor, selTextColor1;
    TextView tool_title, noVideos;
    SharedPreferences sp;
    LinearLayout search_layout;
    EditText searchBox;
    ImageButton search, searchBtn;
    String[] batch_array = {"All Batches"};
    Spinner batch_spinner;
    String selBatch = "All";
    private SwipeRefreshLayout mSwipeRefreshLayout;
    String userId, userSchoolId, userRoleId, userrType, userSection, url, userName, restricted;
    private String selSubject, strTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_by_tag);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        appBarLayout = findViewById(R.id.appBarLayout);
        tool_title = findViewById(R.id.tool_title);
        listView = findViewById(R.id.video_listview);
        noVideos = findViewById(R.id.no_videos);
        mSwipeRefreshLayout = findViewById(R.id.activity_main_swipe_refresh_layout);
        batch_spinner = findViewById(R.id.batch_spinner);
        mSwipeRefreshLayout.setRefreshing(false);
        listView.setEnabled(true);
        search = findViewById(R.id.search);
        search_layout = findViewById(R.id.search_layout);
        searchBox = findViewById(R.id.search_txt);
        searchBtn = findViewById(R.id.search_btn);

        selSubject = getIntent().getStringExtra("subject");
        strTag = getIntent().getStringExtra("tag");

        tool_title.setText(strTag);

        SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
        selToolColor = settingsColor.getString("tool", "");
        selStatusColor = settingsColor.getString("status", "");
        selTextColor1 = settingsColor.getString("text1", "");

        sp = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        userSchoolId = sp.getString("str_school_id", "");
        userSection = sp.getString("userSection", "");
        userId = sp.getString("userID", "");
        userRoleId = sp.getString("str_role_id", "");
        userrType = sp.getString("userrType", "");
        userName = sp.getString("userName", "");
        restricted = sp.getString("restricted", "");

        url = sp.getString("imageUrl", "") + userSchoolId + "/InstituteVideo/";

        appBarLayout.setBackgroundColor(Color.parseColor(selToolColor));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(selStatusColor));
        }
        tool_title.setTextColor(Color.parseColor(selTextColor1));

        if (userrType.equals("Guest")) {
            batch_spinner.setVisibility(View.GONE);
        }

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
                    String[] batch_array = {"All Batches"};
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
        }

        if (isInternetOn()) {
            GetVideos getVideos = new GetVideos(this);
            if (userrType.equals("Student")) {
                getVideos.execute(userSchoolId, userSection, userrType);
                batch_spinner.setVisibility(View.GONE);
            } else {
                getVideos.execute(userSchoolId, "All", userrType);
            }
        } else {
            Toast.makeText(this, "Please connect to internet", Toast.LENGTH_SHORT).show();
        }

        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            if (isInternetOn()) {
                mSwipeRefreshLayout.setRefreshing(true);
                listView.setEnabled(false);
                array.clear();
                videosArray.clear();
                GetVideos getVideos = new GetVideos(this);
                if (userrType.equals("Student")) {
                    getVideos.execute(userSchoolId, userSection, userrType);
                    selBatch = userSection;
                    batch_spinner.setVisibility(View.GONE);
                } else {
                    getVideos.execute(userSchoolId, selBatch, userrType);
                }
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
                String[] batch_array = {"All Batches"};
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
                    GetVideos getVideos = new GetVideos(VideoByTag.this);
                    getVideos.execute(userSchoolId, "All", userrType);
                } else {
                    selBatch = batch_array[i];
                    GetVideos getVideos = new GetVideos(VideoByTag.this);
                    getVideos.execute(userSchoolId, selBatch, userrType);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    public class GetVideos extends AsyncTask<String, String, String> {
        Context ctx;

        GetVideos(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            videosArray.clear();
            mSwipeRefreshLayout.setRefreshing(true);
            listView.setEnabled(false);
            array.clear();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            String schoolId = params[0];
            String sectionName = params[1];
            String userType = params[2];
            String data;

            try {

                URL url = new URL(ALL_VIDEOS);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                data = URLEncoder.encode("schoolId", "UTF-8") + "=" + URLEncoder.encode(schoolId, "UTF-8") + "&" +
                        URLEncoder.encode("userSection", "UTF-8") + "=" + URLEncoder.encode(sectionName, "UTF-8") + "&" +
                        URLEncoder.encode("userType", "UTF-8") + "=" + URLEncoder.encode(userType, "UTF-8") + "&" +
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
            Log.e("res", result);
            mSwipeRefreshLayout.setRefreshing(false);
            listView.setEnabled(true);
            try {
                array.clear();
                videosArray.clear();
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
                JSONObject jsonRootObject = new JSONObject(result);
                if (!result.contains("\"result\":null")) {
                    JSONArray jsonArray = jsonRootObject.getJSONArray("result");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String end = jsonObject.getString("visible_till") + " " + jsonObject.getString("visible_till_time");
                        Date enddate = sdf.parse(end);
                        videosData = new VideosData();
                        if (jsonObject.getString("tbl_school_studyvideo_tag").contains(strTag)) {
                            videosData.setId(jsonObject.getString("tbl_school_studyvideo_id"));
                            videosData.setTitle(jsonObject.getString("tbl_school_studyvideo_title"));
                            videosData.setUrl(jsonObject.getString("tbl_school_studyvideo_video"));
                            videosData.setSubject(jsonObject.getString("subject_name"));
                            videosData.setTotalTime(jsonObject.getString("video_total_time"));
                            videosData.setDesc(jsonObject.getString("tbl_school_studyvideo_desc"));
                            videosData.setBatch(jsonObject.getString("tbl_stnt_prsnl_data_section"));
                            videosData.setDate(jsonObject.getString("tbl_school_studyvideo_date"));
                            videosData.setTags(jsonObject.getString("tbl_school_studyvideo_tag"));
                            videosData.setStart(jsonObject.getString("visible_start_date") + " " + jsonObject.getString("visible_start_time"));
                            videosData.setEnd(jsonObject.getString("visible_till") + " " + jsonObject.getString("visible_till_time"));
                            if (!end.equals("0000-00-00 00:00:00")) {
                                if (calendar.getTime().before(enddate)) {
                                    array.add(videosData);
                                }
                            } else {
                                array.add(videosData);
                            }
                        }
                    }
                }
                if (!result.contains("\"instituteVideos\":null")) {
                    JSONArray jsonArray = jsonRootObject.getJSONArray("instituteVideos");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String end = jsonObject.getString("visible_till") + " " + jsonObject.getString("visible_till_time");
                        Date enddate = sdf.parse(end);
                        videosData = new VideosData();
                        if (jsonObject.getString("tbl_school_studyvideo_tag").contains(strTag)) {
                            videosData.setId(jsonObject.getString("tbl_school_institutevideo_id"));
                            videosData.setTitle(jsonObject.getString("tbl_school_institutevideo_title"));
                            videosData.setUrl(url + jsonObject.getString("tbl_school_institutevideo_video"));
                            videosData.setDesc(jsonObject.getString("tbl_school_institutevideo_desc"));
                            videosData.setSubject(jsonObject.getString("subject_name"));
                            videosData.setTotalTime(jsonObject.getString("video_total_time"));
                            videosData.setDate(jsonObject.getString("tbl_school_institutevideo_date"));
                            videosData.setBatch(jsonObject.getString("tbl_stnt_prsnl_data_section"));
                            videosData.setTags(jsonObject.getString("tbl_school_studyvideo_tag"));
                            videosData.setStart(jsonObject.getString("visible_start_date") + " " + jsonObject.getString("visible_start_time"));
                            videosData.setEnd(jsonObject.getString("visible_till") + " " + jsonObject.getString("visible_till_time"));
                            if (!end.equals("0000-00-00 00:00:00")) {
                                if (calendar.getTime().before(enddate)) {
                                    array.add(videosData);
                                }
                            } else {
                                array.add(videosData);
                            }
                        }
                    }
                }
                if (userrType.equals("Student")) {
                    String disable = jsonRootObject.getString("DisableSubject");
                    if (!result.contains("\"studyVideosStudent\":null")) {
                        JSONArray jsonArray = jsonRootObject.getJSONArray("studyVideosStudent");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            String end = jsonObject.getString("visible_till") + " " + jsonObject.getString("visible_till_time");
                            Date enddate = sdf.parse(end);
                            videosData = new VideosData();
                            if (jsonObject.getString("tbl_school_studyvideo_tag").contains(strTag)) {
                                videosData.setId(jsonObject.getString("tbl_school_studyvideo_id"));
                                videosData.setTitle(jsonObject.getString("tbl_school_studyvideo_title"));
                                videosData.setUrl(jsonObject.getString("tbl_school_studyvideo_video"));
                                videosData.setSubject(jsonObject.getString("subject_name"));
                                videosData.setTotalTime(jsonObject.getString("video_total_time"));
                                videosData.setDesc(jsonObject.getString("tbl_school_studyvideo_desc"));
                                videosData.setBatch(jsonObject.getString("tbl_stnt_prsnl_data_section"));
                                videosData.setDate(jsonObject.getString("tbl_school_studyvideo_date"));
                                videosData.setTags(jsonObject.getString("tbl_school_studyvideo_tag"));
                                videosData.setStart(jsonObject.getString("visible_start_date") + " " + jsonObject.getString("visible_start_time"));
                                videosData.setEnd(jsonObject.getString("visible_till") + " " + jsonObject.getString("visible_till_time"));
                                if (!end.equals("0000-00-00 00:00:00")) {
                                    if (calendar.getTime().before(enddate)) {
                                        array.add(videosData);
                                    }
                                } else {
                                    array.add(videosData);
                                }
                            }
                        }
                    }
                    for (int i = 0; i < array.size(); i++) {
                        if (!disable.contains(array.get(i).getSubject())) {
                            videosArray.add(array.get(i));
                        }
                    }
                }
                if (userrType.equals("Teacher")) {
                    String restricted = jsonRootObject.getString("RistrictedSubject");
                    if (restricted.equals("null")) {
                        videosArray.addAll(array);
                    } else {
                        for (int i = 0; i < array.size(); i++) {
                            if (restricted.contains(array.get(i).getSubject())) {
                                videosArray.add(array.get(i));
                            }
                        }
                    }
                } else {
                    videosArray.addAll(array);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (videosArray.size() > 0) {
                listItems(selSubject);
            } else {
                noVideos.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);
                mSwipeRefreshLayout.setRefreshing(false);
            }
            super.onPostExecute(result);
        }
    }

    private void listItems(String subject) {

        ArrayList<VideosData> arrayList = new ArrayList<>();

        ArrayList<String> ids = new ArrayList<>();
        if (!userrType.equals("Student")) {
            for (int i = 0; i < videosArray.size(); i++) {
                if (!ids.contains(videosArray.get(i).getId())) {
                    ids.add(videosArray.get(i).getId());
                }
            }
            for (int i = 0; i < ids.size(); i++) {
                for (int j = 0; j < videosArray.size(); j++) {
                    if (ids.get(i).equals(videosArray.get(j).getId())) {
                        if (videosArray.get(j).getSubject().contentEquals(subject) || videosArray.get(j).getSubject().equals("0")) {
                            arrayList.add(videosArray.get(j));
                            break;
                        }
                    }
                }
            }
        } else {
            for (int i = 0; i < videosArray.size(); i++) {
                if (!ids.contains(videosArray.get(i).getId())) {
                    ids.add(videosArray.get(i).getId());
                }
            }
            for (int i = 0; i < ids.size(); i++) {
                for (int j = 0; j < videosArray.size(); j++) {
                    if (ids.get(i).equals(videosArray.get(j).getId())) {
                        if (videosArray.get(j).getSubject().contentEquals(subject) || videosArray.get(j).getSubject().equals("0")) {
                            arrayList.add(videosArray.get(j));
                            break;
                        }
                    }
                }
            }
        }

        if (arrayList.size() > 0) {
            noVideos.setVisibility(View.GONE);
            listView.setVisibility(View.VISIBLE);
        } else {
            noVideos.setVisibility(View.VISIBLE);
            listView.setVisibility(View.GONE);
        }

        Collections.sort(arrayList, (o1, o2) -> {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            try {
                return sdf.parse(o1.getDate()).compareTo(sdf.parse(o2.getDate()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return 0;
        });
        Collections.reverse(arrayList);

        listView.setEnabled(true);
        videoAdapter = new VideoAdapter(this, R.layout.list_item_video, arrayList, "video");
        listView.setAdapter(videoAdapter);
        videoAdapter.notifyDataSetChanged();
        mSwipeRefreshLayout.setRefreshing(false);

        search.setOnClickListener(v -> {
            if (search_layout.getVisibility() == View.VISIBLE) {
                search_layout.setVisibility(View.GONE);
                videoAdapter = new VideoAdapter(this, R.layout.list_item_video, arrayList, "video");
                listView.setAdapter(videoAdapter);
                videoAdapter.notifyDataSetChanged();
            } else {
                search_layout.setVisibility(View.VISIBLE);
            }
        });

        searchBtn.setOnClickListener(v -> {
            if (searchBox.getText().toString().isEmpty()) {
                videoAdapter = new VideoAdapter(this, R.layout.list_item_video, arrayList, "video");
                listView.setAdapter(videoAdapter);
                videoAdapter.notifyDataSetChanged();
            } else {
                filterSearch(arrayList, searchBox.getText().toString());
            }
        });

        listView.setOnItemClickListener((parent, view, position, id) -> {
            if (isInternetOn()) {
                try {
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
                    String start = arrayList.get(position).getStart();
                    Date startdate = sdf.parse(start);
                    sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    Date date = sdf.parse(start);
                    Intent intent = new Intent(this, VideoDetails.class);
                    intent.putExtra("title", arrayList.get(position).getTitle());
                    intent.putExtra("url", arrayList.get(position).getUrl());
                    intent.putExtra("id", arrayList.get(position).getId());
                    intent.putExtra("desc", arrayList.get(position).getDesc());
                    intent.putExtra("endDate", arrayList.get(position).getEnd());
                    intent.putExtra("tags", arrayList.get(position).getTags());
                    intent.putExtra("subject", arrayList.get(position).getSubject());
                    if (!start.equals("0000-00-00 00:00:00")) {
                        if (calendar.getTime().equals(startdate) || (calendar.getTime().after(startdate))) {
                            startActivity(intent);
                        } else if (calendar.getTime().before(date)) {
                            sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm aa", Locale.getDefault());
                            Toast.makeText(this, "Starts at " + sdf.format(startdate), Toast.LENGTH_SHORT).show();
                        } else {
                            sdf = new SimpleDateFormat("hh:mm aa", Locale.getDefault());
                            Toast.makeText(this, "Starts at " + sdf.format(startdate), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        startActivity(intent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                Toast.makeText(this, "No Internet", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void filterSearch(ArrayList<VideosData> array, String searchTxt) {
        ArrayList<VideosData> arrayList = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            if (array.get(i).getTitle().toLowerCase().contains(searchTxt.toLowerCase())) {
                arrayList.add(array.get(i));
            }
        }
        listView.setEnabled(true);
        videoAdapter = new VideoAdapter(this, R.layout.list_item_video, arrayList, "video");
        listView.setAdapter(videoAdapter);
        videoAdapter.notifyDataSetChanged();
        mSwipeRefreshLayout.setRefreshing(false);
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