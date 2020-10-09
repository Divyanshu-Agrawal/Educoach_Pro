package com.aaptrix.activitys.student;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
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
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
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

import static com.aaptrix.tools.HttpUrl.GET_SUBS;
import static com.aaptrix.tools.HttpUrl.STUDENT_VIDEO;
import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREFS_RW;
import static com.aaptrix.tools.SPClass.PREF_COLOR;

public class StudentVideo extends AppCompatActivity {

    RecyclerView recyclerView, liveList;
    ArrayList<VideosData> videosArray = new ArrayList<>(), array = new ArrayList<>(), liveArray = new ArrayList<>(), dataArray = new ArrayList<>();
    VideosData videosData;
    AppBarLayout appBarLayout;
    String selToolColor, selStatusColor, selTextColor1;
    TextView tool_title, noVideos;
    SharedPreferences sp;
    String[] subjects;
    ArrayList<String> subject_array = new ArrayList<>();
    String userId, userSchoolId, userRoleId, userrType, userSection, url, userName, restricted;
    GridView subjectGrid;
    ProgressBar progressBar;
    CardView offlineVideos;
    LinearLayout viewAll, viewAllLive, viewAllSubject;
    boolean permission = false;

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
        subjectGrid = findViewById(R.id.subject_grid);
        progressBar = findViewById(R.id.progress);
        viewAll = findViewById(R.id.view_all);
        viewAllLive = findViewById(R.id.view_all_live);
        viewAllSubject = findViewById(R.id.view_all_subject);
        liveList = findViewById(R.id.live_list);
        offlineVideos = findViewById(R.id.offline_videos);
        recyclerView.setEnabled(true);

        offlineVideos.setOnClickListener(view -> {
            Intent intent = new Intent(this, OfflineSubjects.class);
            intent.putExtra("type", "video");
            startActivity(intent);
        });

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

        GetVideos getVideos = new GetVideos(this);
        getVideos.execute(userSchoolId, userSection, userrType);

        url = sp.getString("imageUrl", "") + userSchoolId + "/InstituteVideo/";

        appBarLayout.setBackgroundColor(Color.parseColor(selToolColor));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(selStatusColor));
        }
        tool_title.setTextColor(Color.parseColor(selTextColor1));

        SharedPreferences preferences = getSharedPreferences(PREFS_RW, 0);
        String json = preferences.getString("result", "");

        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("result");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                if (object.getString("tbl_insti_buzz_cate_name").equals("Downloadable Videos")) {
                    permission = object.getString("tbl_scl_inst_buzz_detl_status").equals("Active");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        try {
            File directory = this.getFilesDir();
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File(directory, "subjects")));
            String res = in.readObject().toString();
            in.close();
            JSONObject jsonRootObject = new JSONObject(res);
            JSONArray jsonArray = jsonRootObject.getJSONArray("SubjectList");
            subjects = new String[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                subjects[i] = jsonObject.getString("tbl_batch_subjct_name");
            }
            String object = jsonRootObject.getString("DisableSubject");
            Log.e("sub", object);
            for (String subject : subjects) {
                if (!object.contains(subject)) {
                    subject_array.add(subject);
                }
            }
            setSubject();
        } catch (Exception e) {
            e.printStackTrace();
            if (isInternetOn()) {
                String section = "[{\"userName\":\"" + userSection + "\"}]";
                GetSubject subject = new GetSubject(this);
                subject.execute(userSchoolId, section);
            } else {
                Toast.makeText(this, "Please connect to internet to refresh subjects", Toast.LENGTH_SHORT).show();
                noVideos.setVisibility(View.VISIBLE);
            }
        }

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutFrozen(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        liveList.setLayoutManager(layoutManager);
        liveList.setNestedScrollingEnabled(false);
        liveList.setHasFixedSize(true);
        liveList.setLayoutFrozen(true);
        liveList.setItemAnimator(new DefaultItemAnimator());

        viewAllLive.setOnClickListener(v -> startActivity(new Intent(this, LiveStreaming.class)));

        viewAll.setOnClickListener(v -> {
            if (permission) {
                if (PermissionChecker.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PermissionChecker.PERMISSION_GRANTED &&
                        PermissionChecker.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PermissionChecker.PERMISSION_GRANTED) {
                    isPermissionGranted();
                } else {
                    Intent intent = new Intent(this, StudyVideo.class);
                    intent.putExtra("sub", "All Subjects");
                    startActivity(intent);
                }
            } else {
                Intent intent = new Intent(this, VideoLibrary.class);
                intent.putExtra("sub", "All Subjects");
                startActivity(intent);
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
            recyclerView.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
            array.clear();
            dataArray.clear();
            liveArray.clear();
            liveList.setEnabled(false);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            String schoolId = params[0];
            String sectionName = params[1];
            String userType = params[2];
            String data;

            try {

                URL url = new URL(STUDENT_VIDEO);
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
            recyclerView.setEnabled(true);
            liveList.setEnabled(true);
            try {
                array.clear();
                videosArray.clear();
                dataArray.clear();
                liveArray.clear();
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
                        videosData.setTotalTime(jsonObject.getString("video_total_time"));
                        videosData.setDesc(jsonObject.getString("tbl_school_studyvideo_desc"));
                        videosData.setBatch(jsonObject.getString("tbl_stnt_prsnl_data_section"));
                        videosData.setDate(jsonObject.getString("tbl_school_studyvideo_date"));
                        videosData.setTags(jsonObject.getString("tbl_school_studyvideo_tag"));
                        videosData.setStart(jsonObject.getString("visible_start_date") + " " + jsonObject.getString("visible_start_time"));
                        videosData.setEnd(end);
                        if (!end.equals("0000-00-00 00:00:00")) {
                            if (calendar.getTime().before(enddate)) {
                                array.add(videosData);
                            }
                        } else {
                            array.add(videosData);
                        }
                    }
                }
                if (!result.contains("\"liveStreamingvideo\":null")) {
                    JSONArray jsonArray = jsonRootObject.getJSONArray("liveStreamingvideo");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        videosData = new VideosData();
                        videosData.setId(jsonObject.getString("tbl_school_live_streamingvideo_id"));
                        videosData.setTitle(jsonObject.getString("tbl_school_live_streamingvideo_title"));
                        videosData.setUrl(jsonObject.getString("tbl_school_live_streamingvideo_video"));
                        videosData.setSubject(jsonObject.getString("subject_name"));
                        videosData.setDesc(jsonObject.getString("tbl_school_live_streamingvideo_desc"));
                        videosData.setBatch(jsonObject.getString("tbl_stnt_prsnl_data_section"));
                        videosData.setDate(jsonObject.getString("tbl_school_live_streamingvideo_date"));
                        videosData.setComments(jsonObject.getString("comments_enable_disable"));
                        videosData.setStream(jsonObject.getString("streaming_on_off"));
                        dataArray.add(videosData);
                    }
                }
                String disable = jsonRootObject.getString("DisableSubject");
                for (int i = 0; i < array.size(); i++) {
                    if (!disable.contains(array.get(i).getSubject())) {
                        videosArray.add(array.get(i));
                    }
                }
                for (int i = 0; i < dataArray.size(); i++) {
                    if (!disable.contains(dataArray.get(i).getSubject())) {
                        liveArray.add(dataArray.get(i));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            progressBar.setVisibility(View.GONE);

            if (videosArray.size() == 0 && liveArray.size() == 0) {
                viewAllLive.setVisibility(View.GONE);
                viewAll.setVisibility(View.GONE);
                liveList.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
            } else if (videosArray.size() > 0 && liveArray.size() > 0) {
                listItems();
                viewAllLive.setVisibility(View.VISIBLE);
                viewAll.setVisibility(View.VISIBLE);
                liveList.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
            } else {
                if (videosArray.size() > 0) {
                    listItems();
                    viewAll.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);
                } else {
                    viewAll.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.GONE);
                }

                if (liveArray.size() > 0) {
                    listItems();
                    viewAllLive.setVisibility(View.VISIBLE);
                    liveList.setVisibility(View.VISIBLE);
                } else {
                    viewAllLive.setVisibility(View.GONE);
                    liveList.setVisibility(View.GONE);
                }
            }
            super.onPostExecute(result);
        }
    }

    private void listItems() {

        ArrayList<VideosData> arrayList = new ArrayList<>();
        ArrayList<VideosData> live = new ArrayList<>();

        ArrayList<String> ids = new ArrayList<>();
        for (int i = 0; i < videosArray.size(); i++) {
            if (!ids.contains(videosArray.get(i).getId())) {
                ids.add(videosArray.get(i).getId());
            }
        }
        for (int i = 0; i < ids.size(); i++) {
            for (int j = 0; j < videosArray.size(); j++) {
                if (ids.get(i).equals(videosArray.get(j).getId())) {
                    arrayList.add(videosArray.get(j));
                    break;
                }
            }
        }

        ArrayList<String> id = new ArrayList<>();
        for (int i = 0; i < liveArray.size(); i++) {
            if (!id.contains(liveArray.get(i).getId())) {
                id.add(liveArray.get(i).getId());
            }
        }
        for (int i = 0; i < id.size(); i++) {
            for (int j = 0; j < liveArray.size(); j++) {
                if (id.get(i).equals(liveArray.get(j).getId())) {
                    live.add(liveArray.get(j));
                    break;
                }
            }
        }

        progressBar.setVisibility(View.GONE);

        if (arrayList.size() == 0 && live.size() == 0) {
            viewAllLive.setVisibility(View.GONE);
            viewAll.setVisibility(View.GONE);
            liveList.setVisibility(View.GONE);
            recyclerView.setVisibility(View.GONE);
        } else if (arrayList.size() > 0 && live.size() > 0) {
            viewAllLive.setVisibility(View.VISIBLE);
            viewAll.setVisibility(View.VISIBLE);
            liveList.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
        } else {
            viewAllLive.setVisibility(View.VISIBLE);
            viewAll.setVisibility(View.VISIBLE);
            liveList.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            if (arrayList.size() == 0) {
                viewAll.setVisibility(View.GONE);
                recyclerView.setVisibility(View.GONE);
            } else {
                viewAllLive.setVisibility(View.GONE);
                liveList.setVisibility(View.GONE);
            }
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

        StudentVideoAdapter adapter = new StudentVideoAdapter(this, R.layout.list_video, arrayList, "video");
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        StudentVideoAdapter liveAdapter = new StudentVideoAdapter(this, R.layout.list_video, live, "live");
        liveList.setAdapter(liveAdapter);
        liveAdapter.notifyDataSetChanged();

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
            Log.e("res", result);
            if (!result.equals("{\"SubjectList\":null}")) {
                try {
                    subject_array.clear();
                    JSONObject jsonRootObject = new JSONObject(result);
                    cacheJson(jsonRootObject, "subjects");
                    JSONArray jsonArray = jsonRootObject.getJSONArray("SubjectList");
                    subjects = new String[jsonArray.length()];
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        subjects[i] = jsonObject.getString("tbl_batch_subjct_name");
                    }
                    String object = jsonRootObject.getString("DisableSubject");
                    for (String subject : subjects) {
                        if (!object.contains(subject)) {
                            subject_array.add(subject);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setSubject();
                super.onPostExecute(result);
            }
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

    private void setSubject() {
        SubjectAdapter adapter = new SubjectAdapter(this, R.layout.list_subject, subject_array);
        subjectGrid.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        if (subject_array.size() == 0)
            noVideos.setVisibility(View.VISIBLE);
        else
            noVideos.setVisibility(View.GONE);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (subject_array.size() > 1)
            params.height = (int) getResources().getDimension(R.dimen._90sdp) * (subject_array.size() / 2);
        else
            params.height = (int) getResources().getDimension(R.dimen._90sdp) * (subject_array.size());
        subjectGrid.setLayoutParams(params);
    }

    class SubjectAdapter extends ArrayAdapter<String> {

        private ArrayList<String> objects;
        private Activity context;
        private int resource;

        public SubjectAdapter(Activity context, int resource, ArrayList<String> objects) {
            super(context, resource, objects);
            this.objects = objects;
            this.context = context;
            this.resource = resource;
        }

        private class ViewHolder {
            TextView subject;
        }

        @SuppressLint("ViewHolder")
        @NonNull
        @Override
        public View getView(int position, View view, @NonNull ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            view = inflater.inflate(resource, null);
            ViewHolder holder = new ViewHolder();
            holder.subject = view.findViewById(R.id.subject);
            view.setTag(holder);

            view.setOnClickListener(v -> {
                if (permission) {
                    if (PermissionChecker.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) != PermissionChecker.PERMISSION_GRANTED &&
                            PermissionChecker.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PermissionChecker.PERMISSION_GRANTED) {
                        isPermissionGranted();
                    } else {
                        Intent intent = new Intent(context, StudyVideo.class);
                        intent.putExtra("sub", objects.get(position));
                        startActivity(intent);
                    }
                } else {
                    Intent intent = new Intent(context, VideoLibrary.class);
                    intent.putExtra("sub", objects.get(position));
                    startActivity(intent);
                }
            });

            if (objects != null) {
                holder.subject.setText(objects.get(position));
            }
            return view;
        }

        @Override
        public int getViewTypeCount() {
            return Math.max(getCount(), 1);
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }
    }

    public void isPermissionGranted() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    class StudentVideoAdapter extends RecyclerView.Adapter<StudentVideoAdapter.ViewHolder> {

        private Context context;
        private int resource;
        private ArrayList<VideosData> objects;
        private String type;

        public StudentVideoAdapter(Context context, int resource, ArrayList<VideosData> objects, String type) {
            this.context = context;
            this.resource = resource;
            this.objects = objects;
            this.type = type;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView title, startAt;
            ImageView imageView;
            CardView live, start;

            ViewHolder(@NonNull View view) {
                super(view);
                title = view.findViewById(R.id.video_title);
                imageView = view.findViewById(R.id.videoImage);
                startAt = view.findViewById(R.id.start_at);
                start = view.findViewById(R.id.start_time);
                live = view.findViewById(R.id.live_video_indicator);
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

            if (type.equals("live")) {
                if (objects.get(position).getStream().equals("1")) {
                    holder.live.setVisibility(View.VISIBLE);
                    holder.live.bringToFront();
                }
            }

            if (type.equals("video")) {
                try {
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
                    String start = objects.get(position).getStart();
                    Date startdate = sdf.parse(start);
                    assert startdate != null;
                    sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    Date d = sdf.parse(start);
                    String cal = sdf.format(calendar.getTime());
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
            }

            holder.imageView.setOnClickListener(v -> {
                if (type.equals("video")) {
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
                        intent.putExtra("tags", objects.get(position).getTags());
                        intent.putExtra("subject", objects.get(position).getSubject());
                        intent.putExtra("time", objects.get(position).getTotalTime());
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
                } else {
                    if (objects.get(position).getStream().equals("1")) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                        if (isInternetOn()) {
                            Intent intent = new Intent(context, PlayLiveStream.class);
                            intent.putExtra("title", objects.get(position).getTitle());
                            intent.putExtra("url", objects.get(position).getUrl());
                            intent.putExtra("id", objects.get(position).getId());
                            intent.putExtra("desc", objects.get(position).getDesc());
                            intent.putExtra("comments", objects.get(position).getComments());
                            intent.putExtra("sub", objects.get(position).getSubject());
                            intent.putExtra("date", sdf.format(Calendar.getInstance().getTime()));
                            startActivity(intent);
                        } else {
                            Toast.makeText(context, "No Internet", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, "Streaming is ended", Toast.LENGTH_SHORT).show();
                    }
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
            return Math.min(objects.size(), 5);
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