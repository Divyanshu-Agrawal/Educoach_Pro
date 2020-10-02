package com.aaptrix.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.aaptrix.R;
import com.aaptrix.adaptor.VideoAdapter;
import com.aaptrix.databeans.VideosData;

import org.json.JSONArray;
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

import static android.content.Context.CONNECTIVITY_SERVICE;
import static com.aaptrix.tools.HttpUrl.ALL_VIDEOS;
import static com.aaptrix.tools.SPClass.PREFS_NAME;

public class VideosFragment extends Fragment {

    ListView listView;
    VideoAdapter videoAdapter;
    ArrayList<VideosData> videosArray = new ArrayList<>(), array = new ArrayList<>();
    VideosData videosData;
    TextView noVideos;
    SharedPreferences sp;
    EditText searchBox;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    String userId, userSchoolId, userRoleId, userrType, userSection, url, userName, restricted;
    ImageButton searchBtn;
    private String selSubject = "All Subjects", disable;
    Context context;

    @Override
    public void onAttach(@NonNull Context context) {
        this.context = context;
        super.onAttach(context);
    }

    public VideosFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_videos, container, false);
        listView = view.findViewById(R.id.video_listview);
        noVideos = view.findViewById(R.id.no_videos);
        mSwipeRefreshLayout = view.findViewById(R.id.activity_main_swipe_refresh_layout);
        mSwipeRefreshLayout.setRefreshing(false);
        listView.setEnabled(true);
        searchBox = view.findViewById(R.id.search_txt);
        searchBtn = view.findViewById(R.id.search_btn);

        selSubject = getArguments().getString("sub");

        sp = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        userSchoolId = sp.getString("str_school_id", "");
        userSection = sp.getString("userSection", "");
        userId = sp.getString("userID", "");
        userRoleId = sp.getString("str_role_id", "");
        userrType = sp.getString("userrType", "");
        userName = sp.getString("userName", "");
        restricted = sp.getString("restricted", "");

        url = sp.getString("imageUrl", "") + userSchoolId + "/InstituteVideo/";

        if (isInternetOn()) {
            GetVideos getVideos = new GetVideos(context);
            getVideos.execute(userSchoolId, userSection, userrType);
        } else {
            Toast.makeText(context, "Please connect to internet", Toast.LENGTH_SHORT).show();
        }

        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            if (isInternetOn()) {
                mSwipeRefreshLayout.setRefreshing(true);
                listView.setEnabled(false);
                array.clear();
                videosArray.clear();
                GetVideos getVideos = new GetVideos(context);
                getVideos.execute(userSchoolId, userSection, userrType);
            } else {
                Toast.makeText(context, "No network Please connect with network for update", Toast.LENGTH_SHORT).show();
                mSwipeRefreshLayout.setRefreshing(false);
                listView.setEnabled(true);
            }
        });

        return view;
    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Activity.INPUT_METHOD_SERVICE);
        assert inputMethodManager != null;
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public final boolean isInternetOn() {
        ConnectivityManager connec =
                (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
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
                disable = jsonRootObject.getString("DisableSubject");
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
                for (int i = 0; i < array.size(); i++) {
                    if (!disable.contains(array.get(i).getSubject())) {
                        videosArray.add(array.get(i));
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (videosArray.size() > 0) {
                listItems(selSubject, disable);
            } else {
                noVideos.setVisibility(View.VISIBLE);
                listView.setVisibility(View.GONE);
                mSwipeRefreshLayout.setRefreshing(false);
            }
            super.onPostExecute(result);
        }
    }

    private void listItems(String subject, String disable) {

        ArrayList<VideosData> arrayList = new ArrayList<>();

        ArrayList<String> ids = new ArrayList<>();
        if (subject.equals("All Subjects")) {
            for (int i = 0; i < videosArray.size(); i++) {
                if (!ids.contains(videosArray.get(i).getId())) {
                    ids.add(videosArray.get(i).getId());
                }
            }
            for (int i = 0; i < ids.size(); i++) {
                for (int j = 0; j < videosArray.size(); j++) {
                    if (ids.get(i).equals(videosArray.get(j).getId()) && !disable.contains(videosArray.get(j).getSubject())) {
                        arrayList.add(videosArray.get(j));
                        break;
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

        searchBox.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        searchBox.setSingleLine(true);
        searchBox.setInputType(InputType.TYPE_CLASS_TEXT);
        searchBox.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                if (searchBox.getText().toString().isEmpty()) {
                    listView.setVisibility(View.VISIBLE);
                    noVideos.setVisibility(View.GONE);
                    videoAdapter = new VideoAdapter(context, R.layout.list_item_video, arrayList, "video");
                    listView.setAdapter(videoAdapter);
                    videoAdapter.notifyDataSetChanged();
                } else {
                    filterSearch(arrayList, searchBox.getText().toString());
                }
                hideKeyboard(v);
                return true;
            }
            return false;
        });

        searchBtn.setOnClickListener(v -> {
            if (searchBox.getText().toString().isEmpty()) {
                listView.setVisibility(View.VISIBLE);
                noVideos.setVisibility(View.GONE);
                videoAdapter = new VideoAdapter(context, R.layout.list_item_video, arrayList, "video");
                listView.setAdapter(videoAdapter);
                videoAdapter.notifyDataSetChanged();
            } else {
                filterSearch(arrayList, searchBox.getText().toString());
            }
            hideKeyboard(v);
        });

        listView.setEnabled(true);
        videoAdapter = new VideoAdapter(context, R.layout.list_item_video, arrayList, "video");
        listView.setAdapter(videoAdapter);
        videoAdapter.notifyDataSetChanged();
        mSwipeRefreshLayout.setRefreshing(false);
    }

    private void filterSearch(ArrayList<VideosData> array, String searchTxt) {
        ArrayList<VideosData> arrayList = new ArrayList<>();
        for (int i = 0; i < array.size(); i++) {
            if (array.get(i).getTitle().toLowerCase().contains(searchTxt.toLowerCase())) {
                arrayList.add(array.get(i));
            }
        }
        if (arrayList.size() == 0) {
            noVideos.setVisibility(View.VISIBLE);
            noVideos.setText("Nothing Found");
            listView.setVisibility(View.GONE);
        } else {
            listView.setEnabled(true);
            listView.setVisibility(View.VISIBLE);
            noVideos.setVisibility(View.GONE);
            videoAdapter = new VideoAdapter(context, R.layout.list_item_video, arrayList, "video");
            listView.setAdapter(videoAdapter);
            videoAdapter.notifyDataSetChanged();
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }
}