package com.aaptrix.activitys.student;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aaptrix.R;
import com.aaptrix.databeans.VideosData;
import com.google.android.material.appbar.AppBarLayout;
import com.squareup.picasso.Picasso;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import static com.aaptrix.tools.HttpUrl.ALL_VIDEOS;
import static com.aaptrix.tools.HttpUrl.GET_SUBS;
import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREFS_RW;
import static com.aaptrix.tools.SPClass.PREF_COLOR;

public class StudentVideo extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<VideosData> videosArray = new ArrayList<>(), array = new ArrayList<>();
    VideosData videosData;
    AppBarLayout appBarLayout;
    String selToolColor, selStatusColor, selTextColor1;
    TextView tool_title, noVideos;
    SharedPreferences sp;
    LinearLayout add_layout;
    String[] subjects;
    ArrayList<String> subject_array = new ArrayList<>();
    private SwipeRefreshLayout mSwipeRefreshLayout;
    String userId, userSchoolId, userRoleId, userrType, userSection, url, userName, restricted;
    LinearLayout liveVideo;
    GridView subjectGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_video);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        appBarLayout = findViewById(R.id.appBarLayout);
        tool_title = findViewById(R.id.tool_title);
        recyclerView = findViewById(R.id.video_list);
        noVideos = findViewById(R.id.no_videos);
        mSwipeRefreshLayout = findViewById(R.id.activity_main_swipe_refresh_layout);
        add_layout = findViewById(R.id.add_layout);
        liveVideo = findViewById(R.id.live_video);
        subjectGrid = findViewById(R.id.subject_grid);
        mSwipeRefreshLayout.setRefreshing(false);
        recyclerView.setEnabled(true);

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
        GradientDrawable bgShape = (GradientDrawable) add_layout.getBackground();
        bgShape.setColor(Color.parseColor(selToolColor));
        tool_title.setTextColor(Color.parseColor(selTextColor1));

        SharedPreferences preferences = getSharedPreferences(PREFS_RW, 0);
        String json = preferences.getString("result", "");

        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("result");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                if (object.getString("tbl_insti_buzz_cate_name").equals("Video Live Streaming")) {
                    if (object.getString("tbl_scl_inst_buzz_detl_status").equals("Active")) {
                        liveVideo.setVisibility(View.VISIBLE);
                    } else {
                        liveVideo.setVisibility(View.GONE);
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        liveVideo.setOnClickListener(v -> {
            Intent intent = new Intent(this, LiveStreaming.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        String section = "[{\"userName\":\"" + userSection + "\"}]";
        GetSubject subject = new GetSubject(this);
        subject.execute(userSchoolId, section);

        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            if (isInternetOn()) {
                mSwipeRefreshLayout.setRefreshing(true);
                recyclerView.setEnabled(false);
                array.clear();
                videosArray.clear();
                GetVideos getVideos = new GetVideos(this);
                getVideos.execute(userSchoolId, userSection, userrType);
            } else {
                Toast.makeText(this, "No network Please connect with network for update", Toast.LENGTH_SHORT).show();
                mSwipeRefreshLayout.setRefreshing(false);
                recyclerView.setEnabled(true);
            }
        });

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutFrozen(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
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
            recyclerView.setEnabled(false);
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
            recyclerView.setEnabled(true);
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
                        videosData.setId(jsonObject.getString("tbl_school_studyvideo_id"));
                        videosData.setTitle(jsonObject.getString("tbl_school_studyvideo_title"));
                        videosData.setUrl(jsonObject.getString("tbl_school_studyvideo_video"));
                        videosData.setSubject(jsonObject.getString("subject_name"));
                        videosData.setDesc(jsonObject.getString("tbl_school_studyvideo_desc"));
                        videosData.setBatch(jsonObject.getString("tbl_stnt_prsnl_data_section"));
                        videosData.setDate(jsonObject.getString("tbl_school_studyvideo_date"));
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
                if (!result.contains("\"instituteVideos\":null")) {
                    JSONArray jsonArray = jsonRootObject.getJSONArray("instituteVideos");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String end = jsonObject.getString("visible_till") + " " + jsonObject.getString("visible_till_time");
                        Date enddate = sdf.parse(end);
                        videosData = new VideosData();
                        videosData.setId(jsonObject.getString("tbl_school_institutevideo_id"));
                        videosData.setTitle(jsonObject.getString("tbl_school_institutevideo_title"));
                        videosData.setUrl(url + jsonObject.getString("tbl_school_institutevideo_video"));
                        videosData.setDesc(jsonObject.getString("tbl_school_institutevideo_desc"));
                        videosData.setSubject(jsonObject.getString("subject_name"));
                        videosData.setDate(jsonObject.getString("tbl_school_institutevideo_date"));
                        videosData.setBatch(jsonObject.getString("tbl_stnt_prsnl_data_section"));
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
                String disable = jsonRootObject.getString("DisableSubject");
                if (!result.contains("\"studyVideosStudent\":null")) {
                    JSONArray jsonArray = jsonRootObject.getJSONArray("studyVideosStudent");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        String end = jsonObject.getString("visible_till") + " " + jsonObject.getString("visible_till_time");
                        Date enddate = sdf.parse(end);
                        videosData = new VideosData();
                        videosData.setId(jsonObject.getString("tbl_school_studyvideo_id"));
                        videosData.setTitle(jsonObject.getString("tbl_school_studyvideo_title"));
                        videosData.setUrl(jsonObject.getString("tbl_school_studyvideo_video"));
                        videosData.setSubject(jsonObject.getString("subject_name"));
                        videosData.setDesc(jsonObject.getString("tbl_school_studyvideo_desc"));
                        videosData.setBatch(jsonObject.getString("tbl_stnt_prsnl_data_section"));
                        videosData.setDate(jsonObject.getString("tbl_school_studyvideo_date"));
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
                    for (int i = 0; i < array.size(); i++) {
                        if (!disable.contains(array.get(i).getSubject())) {
                            videosArray.add(array.get(i));
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (videosArray.size() == 0) {
                noVideos.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                mSwipeRefreshLayout.setRefreshing(false);
            } else {
                listItems();
                recyclerView.setVisibility(View.VISIBLE);
                noVideos.setVisibility(View.GONE);
            }
            super.onPostExecute(result);
        }
    }

    private void listItems() {

        ArrayList<VideosData> arrayList = new ArrayList<>();

        ArrayList<String> ids = new ArrayList<>();
        for (int i = 0; i < videosArray.size(); i++) {
            if (!ids.contains(videosArray.get(i).getId())) {
                ids.add(videosArray.get(i).getId());
            }
        }
        for (int i = 0; i < ids.size(); i++) {
            for (int j = 0; j < videosArray.size(); j++) {
                if (ids.get(i).equals(videosArray.get(j).getId())) {
                    if (arrayList.size() <= 5)
                        arrayList.add(videosArray.get(j));
                    break;
                }
            }
        }

        if (arrayList.size() == 0) {
            noVideos.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            subjectGrid.setVisibility(View.GONE);
        } else {
            noVideos.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            subjectGrid.setVisibility(View.VISIBLE);
        }

        Collections.sort(arrayList, (o1, o2) -> {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            try {
                return Objects.requireNonNull(sdf.parse(o1.getDate())).compareTo(sdf.parse(o2.getDate()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return 0;
        });
        Collections.reverse(arrayList);

        StudentVideoAdapter adapter = new StudentVideoAdapter(this, R.layout.list_video, arrayList);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        mSwipeRefreshLayout.setRefreshing(false);
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
                    String object = jsonRootObject.getString("DisableSubject");
                    subject_array.add("All Subjects");
                    for (String subject : subjects) {
                        if (!object.contains(subject)) {
                            subject_array.add(subject);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                super.onPostExecute(result);
            }
        }
    }

    static class StudentVideoAdapter extends RecyclerView.Adapter<StudentVideoAdapter.ViewHolder> {

        private Context context;
        private int resource;
        private ArrayList<VideosData> objects;

        public StudentVideoAdapter(Context context, int resource, ArrayList<VideosData> objects) {
            this.context = context;
            this.resource = resource;
            this.objects = objects;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        static class ViewHolder extends RecyclerView.ViewHolder {

            ImageView imageView;
            TextView title, startAt;
            CardView start;

            ViewHolder(@NonNull View view) {
                super(view);
                title = view.findViewById(R.id.video_title);
                imageView = view.findViewById(R.id.videoImage);
                startAt = view.findViewById(R.id.start_at);
                start = view.findViewById(R.id.start_time);
            }
        }

        @NonNull
        @Override
        public StudentVideo.StudentVideoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(context).inflate(resource, parent, false);
            return new ViewHolder(itemView);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull StudentVideo.StudentVideoAdapter.ViewHolder holder, int position) {
            holder.title.setText(objects.get(position).getTitle());
            if (objects.get(position).getUrl().contains("youtube") || objects.get(position).getUrl().contains("youtu.be")) {
                holder.imageView.setVisibility(View.VISIBLE);
                String thumbUrl = "http://img.youtube.com/vi/" + videoId(objects.get(position).getUrl())
                        + "/mqdefault.jpg";
                Picasso.with(context).load(thumbUrl).placeholder(R.drawable.youtube).error(R.drawable.youtube).into(holder.imageView);
            } else {
                holder.imageView.setVisibility(View.VISIBLE);
                Picasso.with(context).load(R.drawable.app_logo).into(holder.imageView);
                holder.imageView.setBackgroundColor(Color.WHITE);
            }

            try {
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
                String start = objects.get(position).getStart();
                Date startdate = sdf.parse(start);
                assert startdate != null;
                sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date d = sdf.parse(start);
                String cal = sdf.format(calendar.getTime());
                assert d != null;
                if (!start.equals("0000-00-00 00:00:00")) {
                    if (cal.equals(sdf.format(d))) {
                        if (calendar.getTime().before(startdate)) {
                            sdf = new SimpleDateFormat("hh:mm aa", Locale.getDefault());
                            String date = sdf.format(startdate);
                            holder.start.setVisibility(View.VISIBLE);
                            holder.startAt.setText("Starts At : " + date);
                        }
                    } else {
                        if (calendar.getTime().before(startdate)) {
                            sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm aa", Locale.getDefault());
                            String date = sdf.format(startdate);
                            holder.start.setVisibility(View.VISIBLE);
                            holder.startAt.setText("Starts At : " + date);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            holder.imageView.setOnClickListener(v -> {
                try {
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
                    String start = objects.get(position).getStart();
                    Date startdate = sdf.parse(start);
                    sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    Date date = sdf.parse(start);
                    Intent intent = new Intent(context, VideoDetails.class);
                    intent.putExtra("title", objects.get(position).getTitle());
                    intent.putExtra("url", objects.get(position).getUrl());
                    intent.putExtra("id", objects.get(position).getId());
                    intent.putExtra("desc", objects.get(position).getDesc());
                    intent.putExtra("endDate", objects.get(position).getEnd());
                    assert startdate != null;
                    if (!start.equals("0000-00-00 00:00:00")) {
                        if (calendar.getTime().equals(startdate) || (calendar.getTime().after(startdate))) {
                            context.startActivity(intent);
                        } else if (calendar.getTime().before(date)) {
                            sdf = new SimpleDateFormat("dd-MM-yyyy hh:mm aa", Locale.getDefault());
                            Toast.makeText(context, "Starts at " + sdf.format(startdate), Toast.LENGTH_SHORT).show();
                        } else {
                            sdf = new SimpleDateFormat("hh:mm aa", Locale.getDefault());
                            Toast.makeText(context, "Starts at " + sdf.format(startdate), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        context.startActivity(intent);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        private String videoId(String url) {
            int index = url.indexOf("v=");
            String id = url.substring(index + 2, index + 13);
            if (id.equals("ttps://yout")) {
                id = url.split("/")[3];
            }
            return id;
        }

        @Override
        public int getItemCount() {
            return objects.size();
        }
    }

    public final boolean isInternetOn() {
        ConnectivityManager connec =
                (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        assert connec != null;
        if (Objects.requireNonNull(connec.getNetworkInfo(0)).getState() == android.net.NetworkInfo.State.CONNECTED ||
                Objects.requireNonNull(connec.getNetworkInfo(0)).getState() == android.net.NetworkInfo.State.CONNECTING ||
                Objects.requireNonNull(connec.getNetworkInfo(1)).getState() == android.net.NetworkInfo.State.CONNECTING ||
                Objects.requireNonNull(connec.getNetworkInfo(1)).getState() == android.net.NetworkInfo.State.CONNECTED) {
            return true;
        } else if (
                Objects.requireNonNull(connec.getNetworkInfo(0)).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                        Objects.requireNonNull(connec.getNetworkInfo(1)).getState() == android.net.NetworkInfo.State.DISCONNECTED) {
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