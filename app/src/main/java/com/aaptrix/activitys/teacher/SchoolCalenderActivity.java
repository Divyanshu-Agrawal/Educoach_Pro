package com.aaptrix.activitys.teacher;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import com.aaptrix.activitys.student.OnlineExam;
import com.aaptrix.activitys.student.StudentTimeTableActivity;
import com.google.android.material.appbar.AppBarLayout;

import com.aaptrix.activitys.admin.IntermidiateScreenActivityPublication;
import com.aaptrix.activitys.student.AchievmentDetailsActivity;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.CountDownTimer;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import com.aaptrix.databeans.DatabeanEvents;
import com.aaptrix.adaptor.UpcommingListAdaptor;
import com.aaptrix.R;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.format.WeekDayFormatter;

import static com.aaptrix.tools.HttpUrl.ALL_EVENTS;
import static com.aaptrix.tools.HttpUrl.ALL_UPCOMMING_EVENTS_UNIQUE;
import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREFS_RW;
import static com.aaptrix.tools.SPClass.PREF_COLOR;

/**
 * Created by Administrator on 11/29/2017.
 */

public class SchoolCalenderActivity extends AppCompatActivity {

    private SharedPreferences settings, sp;
    SharedPreferences.Editor editor;
    String userId, userSchoolId, userSchoolLogo, userRoleId, userSection, userrType;
    AppBarLayout appBarLayout;
    SharedPreferences.Editor editorColor;
    String selToolColor, selDrawerColor, selStatusColor, selTextColor1, selTextColor2;
    TextView tool_title;
    LinearLayout add_layout;
    ImageView iv_add_more_event;

    UpcommingListAdaptor upcommingListAdaptor;
    //calender
    LinearLayout eventLayout, mainLayoutEvent;
    TextView tv_detaails, tv_date, todayTv, noEventTv;

    //eventDetails
    String eventId, eventTitle, eventDesc, eventDate, eventEndDate, eventImg;
    DatabeanEvents dbe;
    ArrayList<DatabeanEvents> eventsArray = new ArrayList<>();
    ArrayList<DatabeanEvents> eventsArray1 = new ArrayList<>();
    //offline
    private SharedPreferences sp_event;
    SharedPreferences.Editor se_event;
    public static final String PREFS_EVENT = "json_event";
    public static final String PREFS_EVENT1 = "json_event_upcomming";
    private SwipeRefreshLayout mSwipeRefreshLayout;

    ListView upcoming_listView;
    TextView ev_date, ev_today, tv_today1;
    MaterialCalendarView calendarView;
    TextView eventText;
    TextView snack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.school_calender_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        appBarLayout = findViewById(R.id.appBarLayout);
        tool_title = findViewById(R.id.tool_title);
        tv_detaails = findViewById(R.id.tv_detaails);
        eventText = findViewById(R.id.event_text);
        tv_date = findViewById(R.id.tv_date);
        todayTv = findViewById(R.id.todayTv);
        snack = findViewById(R.id.snack);
        noEventTv = findViewById(R.id.noEventTv);
        eventLayout = findViewById(R.id.eventLayout);
        mainLayoutEvent = findViewById(R.id.mainLayoutEvent);
        add_layout = findViewById(R.id.add_layout);
        iv_add_more_event = findViewById(R.id.iv_add_more_event);
        sp = this.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        ev_date = findViewById(R.id.ev_date);
        ev_today = findViewById(R.id.ev_today);
        upcoming_listView = findViewById(R.id.upcoming_listView);
        tv_today1 = findViewById(R.id.tv_today1);
        calendarView = findViewById(R.id.calendar);

        mSwipeRefreshLayout = findViewById(R.id.activity_main_swipe_refresh_layout);
        noEventTv.setVisibility(View.GONE);
        eventLayout.setVisibility(View.GONE);
        settings = getSharedPreferences(PREFS_NAME, 0);
        editor = settings.edit();
        userId = settings.getString("userID", "");
        userSchoolId = settings.getString("str_school_id", "");
        userSchoolLogo = settings.getString("userSchoolLogo", "");
        userRoleId = settings.getString("str_role_id", "");
        userSection = settings.getString("userSection", "");
        userrType = settings.getString("userrType", "");


        //color
        SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
        editorColor = settingsColor.edit();
        selToolColor = settingsColor.getString("tool", "");
        selDrawerColor = settingsColor.getString("drawer", "");
        selStatusColor = settingsColor.getString("status", "");
        selTextColor1 = settingsColor.getString("text1", "");
        selTextColor2 = settingsColor.getString("text2", "");

        sp_event = getSharedPreferences(PREFS_EVENT, 0);
        se_event = sp_event.edit();
        String events = sp_event.getString("json_event", "");

        sp_event = getSharedPreferences(PREFS_EVENT1, 0);
        se_event = sp_event.edit();
        String events1 = sp_event.getString("json_event_upcomming", "");

        if (!events.equals("null") && !events.isEmpty() && events.length() > 5 &&
                !events1.equals("null") && !events1.isEmpty() && events1.length() > 5 &&
                !events.equals("{\"result\":null}") && !events1.equals("{\"result\":null}")) {
            getAllEvents(events);
            getAllEvents1(events1);
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
            GetAllEvents b1 = new GetAllEvents(SchoolCalenderActivity.this);
            b1.execute(userSchoolId, userSection, userrType);
            GetAllEvents1 b2 = new GetAllEvents1(SchoolCalenderActivity.this);
            b2.execute(userSchoolId, userSection, userrType);
        }

        SharedPreferences sp = getSharedPreferences(PREFS_RW, 0);
        String json = sp.getString("result", "");

        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("result");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                if (object.getString("tbl_insti_buzz_cate_name").equals("Institute Calendar")) {
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

        iv_add_more_event.setOnClickListener(view -> {
            if (isInternetOn()) {
                Intent i = new Intent(SchoolCalenderActivity.this, IntermidiateScreenActivityPublication.class);
                i.putExtra("str_tool_title", "Add Event");
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            } else {
                Toast.makeText(SchoolCalenderActivity.this, "No network Please connect with network for update", Toast.LENGTH_SHORT).show();

            }
        });

        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            if (isInternetOn()) {
                eventsArray1.clear();
                eventsArray.clear();
                mSwipeRefreshLayout.setRefreshing(true);
                GetAllEvents b1 = new GetAllEvents(SchoolCalenderActivity.this);
                b1.execute(userSchoolId, userSection, userrType);
                GetAllEvents1 b2 = new GetAllEvents1(SchoolCalenderActivity.this);
                b2.execute(userSchoolId, userSection, userrType);
            }
        });

        appBarLayout.setBackgroundColor(Color.parseColor(selToolColor));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(selStatusColor));
        }
        tool_title.setTextColor(Color.parseColor(selTextColor1));
        todayTv.setBackgroundColor(Color.parseColor(selToolColor));
        mSwipeRefreshLayout.setColorScheme(R.color.text_gray);
        ev_date.setTextColor(Color.parseColor(selToolColor));
        ev_today.setTextColor(Color.parseColor(selToolColor));

        tv_today1.setBackground(getResources().getDrawable(R.drawable.cube));
        GradientDrawable drawable = (GradientDrawable) tv_today1.getBackground();
        drawable.setStroke(2, Color.parseColor(selToolColor)); // set stroke width and stroke color
        GradientDrawable bgShape = (GradientDrawable) add_layout.getBackground();
        bgShape.setColor(Color.parseColor(selToolColor));

    }

    private void getAllEvents(String events) {
        try {
            JSONObject jsonRootObject = new JSONObject(events);
            if (!events.contains("\"result\": null")) {
                JSONArray jsonArray = jsonRootObject.getJSONArray("result");
                eventsArray.clear();
                for (int i = 0; i < jsonArray.length(); i++) {
                    dbe = new DatabeanEvents();
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    eventImg = jsonObject.getString("tbl_school_events_imgs");
                    String[] image = eventImg.split(",");
                    String url = settings.getString("imageUrl", "") + userSchoolId + "/event/" + image[0].replace("\"", "").replace("[", "").replace("]", "");
                    dbe.setEventId(jsonObject.getString("tbl_school_events_id"));
                    dbe.setEventTitle(jsonObject.getString("tbl_school_events_title"));
                    dbe.setEventDesc(jsonObject.getString("tbl_school_events_desc"));
                    dbe.setEventDate(jsonObject.getString("tbl_school_events_start_date"));
                    dbe.setEventEndDate(jsonObject.getString("tbl_school_events_end_date"));
                    dbe.setEventImg(url);
                    dbe.setType("event");
                    eventsArray.add(dbe);
                }
            }
            if (!events.contains("\"offlineExam\": null")) {
                JSONArray offlineExamArray = jsonRootObject.getJSONArray("offlineExam");
                for (int i = 0; i < offlineExamArray.length(); i++) {
                    dbe = new DatabeanEvents();
                    JSONObject jsonObject = offlineExamArray.getJSONObject(i);
                    dbe.setEventId(jsonObject.getString("tbl_exam_id"));
                    dbe.setEventTitle(jsonObject.getString("tbl_exam_name"));
                    dbe.setEventDesc(jsonObject.getString("exam_type"));
                    dbe.setEventDate(jsonObject.getString("tbl_exam_start_date"));
                    dbe.setEventEndDate(jsonObject.getString("tbl_exam_start_date"));
                    dbe.setEventImg("");
                    dbe.setType("exam");
                    eventsArray.add(dbe);
                }
            }
            if (!events.contains("\"onlineExam\": null")) {
                JSONArray onlineExamArray = jsonRootObject.getJSONArray("onlineExam");
                for (int i = 0; i < onlineExamArray.length(); i++) {
                    dbe = new DatabeanEvents();
                    JSONObject jsonObject = onlineExamArray.getJSONObject(i);
                    dbe.setEventId(jsonObject.getString("tbl_online_exams_id"));
                    dbe.setEventTitle(jsonObject.getString("tbl_online_exam_nm"));
                    dbe.setEventDesc(jsonObject.getString("tbl_stnt_prsnl_data_section"));
                    dbe.setEventDate(jsonObject.getString("tbl_online_exam_date"));
                    dbe.setEventEndDate(jsonObject.getString("tbl_online_exam_end_date"));
                    dbe.setEventImg("");
                    dbe.setType("exam");
                    eventsArray.add(dbe);
                }
            }
            if (!events.contains("\"activities\": null")) {
                JSONArray activityArray = jsonRootObject.getJSONArray("activities");
                for (int i = 0; i < activityArray.length(); i++) {
                    dbe = new DatabeanEvents();
                    JSONObject jsonObject = activityArray.getJSONObject(i);
                    dbe.setEventId(jsonObject.getString("tbl_school_activities_id"));
                    dbe.setEventTitle(jsonObject.getString("tbl_school_activities_title"));
                    dbe.setEventDesc(jsonObject.getString("tbl_school_activities_desc"));
                    dbe.setEventDate(jsonObject.getString("tbl_school_activities_date"));
                    dbe.setEventEndDate(jsonObject.getString("tbl_school_activities_date"));
                    dbe.setEventImg(jsonObject.getString("tbl_school_activities_img"));
                    dbe.setType("activity");
                    eventsArray.add(dbe);
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (eventsArray.size() != 0) {
            eventDetails();
        }
    }

    private void getAllEvents1(String result) {
            try {
                JSONObject jsonRootObject = new JSONObject(result);
                if (!result.contains("\"result\": null")) {
                    JSONArray jsonArray = jsonRootObject.getJSONArray("result");
                    eventsArray1.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        dbe = new DatabeanEvents();
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        eventImg = jsonObject.getString("tbl_school_events_imgs");
                        String[] image = eventImg.split(",");
                        String url = settings.getString("imageUrl", "") + userSchoolId + "/event/" + image[0].replace("\"", "").replace("[", "").replace("]", "");
                        dbe.setEventId(jsonObject.getString("tbl_school_events_id"));
                        dbe.setEventTitle(jsonObject.getString("tbl_school_events_title"));
                        dbe.setEventDesc(jsonObject.getString("tbl_school_events_desc"));
                        dbe.setEventDate(jsonObject.getString("tbl_school_events_start_date"));
                        dbe.setEventEndDate(jsonObject.getString("tbl_school_events_end_date"));
                        dbe.setEventImg(url);
                        dbe.setType("event");
                        eventsArray1.add(dbe);
                    }
                }
                if (!result.contains("\"offlineExam\": null")) {
                    JSONArray offlineExamArray = jsonRootObject.getJSONArray("offlineExam");
                    for (int i = 0; i < offlineExamArray.length(); i++) {
                        dbe = new DatabeanEvents();
                        JSONObject jsonObject = offlineExamArray.getJSONObject(i);
                        dbe.setEventId(jsonObject.getString("tbl_exam_id"));
                        dbe.setEventTitle(jsonObject.getString("tbl_exam_name"));
                        dbe.setEventDesc(jsonObject.getString("exam_type"));
                        dbe.setEventDate(jsonObject.getString("tbl_exam_start_date"));
                        dbe.setEventEndDate(jsonObject.getString("tbl_exam_start_date"));
                        dbe.setEventImg("");
                        dbe.setType("exam");
                        eventsArray1.add(dbe);
                    }
                }
                if (!result.contains("\"onlineExam\": null")) {
                    JSONArray onlineExamArray = jsonRootObject.getJSONArray("onlineExam");
                    for (int i = 0; i < onlineExamArray.length(); i++) {
                        dbe = new DatabeanEvents();
                        JSONObject jsonObject = onlineExamArray.getJSONObject(i);
                        dbe.setEventId(jsonObject.getString("tbl_online_exams_id"));
                        dbe.setEventTitle(jsonObject.getString("tbl_online_exam_nm"));
                        dbe.setEventDesc(jsonObject.getString("tbl_stnt_prsnl_data_section"));
                        dbe.setEventDate(jsonObject.getString("tbl_online_exam_date"));
                        dbe.setEventEndDate(jsonObject.getString("tbl_online_exam_end_date"));
                        dbe.setEventImg("");
                        dbe.setType("exam");
                        eventsArray1.add(dbe);
                    }
                }
                if (!result.contains("\"activities\": null")) {
                    JSONArray activityArray = jsonRootObject.getJSONArray("activities");
                    for (int i = 0; i < activityArray.length(); i++) {
                        dbe = new DatabeanEvents();
                        JSONObject jsonObject = activityArray.getJSONObject(i);
                        dbe.setEventId(jsonObject.getString("tbl_school_activities_id"));
                        dbe.setEventTitle(jsonObject.getString("tbl_school_activities_title"));
                        dbe.setEventDesc(jsonObject.getString("tbl_school_activities_desc"));
                        dbe.setEventDate(jsonObject.getString("tbl_school_activities_date"));
                        dbe.setEventEndDate(jsonObject.getString("tbl_school_activities_date"));
                        dbe.setEventImg(jsonObject.getString("tbl_school_activities_img"));
                        dbe.setType("activity");
                        eventsArray1.add(dbe);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (eventsArray.size() != 0) {
                eventDetails();
            } else {
                noEventTv.setVisibility(View.VISIBLE);
                upcoming_listView.setVisibility(View.GONE);
            }
    }

    public final boolean isInternetOn() {

        ConnectivityManager connec = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

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

    @SuppressLint("StaticFieldLeak")
    public class GetAllEvents extends AsyncTask<String, String, String> {
        Context ctx;

        GetAllEvents(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            mSwipeRefreshLayout.setRefreshing(true);
            add_layout.setVisibility(View.GONE);
            mainLayoutEvent.setVisibility(View.GONE);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            String schoolId = params[0];
            String userSection = params[1];
            String userrType = params[2];
            String data;

            try {

                URL url = new URL(ALL_EVENTS);
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
            Log.d("Events", "" + result);
            sp_event = getSharedPreferences(PREFS_EVENT, 0);
            se_event = sp_event.edit();
            se_event.clear();
            se_event.putString("json_event", result);
            se_event.apply();
            mSwipeRefreshLayout.setRefreshing(false);
            mainLayoutEvent.setVisibility(View.VISIBLE);

            if (userRoleId.equals("1")) {
                add_layout.setVisibility(View.VISIBLE);
            } else {
                add_layout.setVisibility(View.GONE);

            }
            if (result.equals("{\"result\":null}")) {
                noEventTv.setVisibility(View.VISIBLE);
                upcoming_listView.setVisibility(View.GONE);
                //   Toast.makeText(ctx, "No events", Toast.LENGTH_SHORT).show();
            } else {
                noEventTv.setVisibility(View.GONE);
                upcoming_listView.setVisibility(View.VISIBLE);

                try {
                    JSONObject jsonRootObject = new JSONObject(result);
                    if (!result.contains("\"result\":null")) {
                        JSONArray jsonArray = jsonRootObject.getJSONArray("result");
                        eventsArray.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            dbe = new DatabeanEvents();
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            eventImg = jsonObject.getString("tbl_school_events_imgs");
                            String[] image = eventImg.split(",");
                            String url = settings.getString("imageUrl", "") + userSchoolId + "/event/" + image[0].replace("\"", "").replace("[", "").replace("]", "");
                            dbe.setEventId(jsonObject.getString("tbl_school_events_id"));
                            dbe.setEventTitle(jsonObject.getString("tbl_school_events_title"));
                            dbe.setEventDesc(jsonObject.getString("tbl_school_events_desc"));
                            dbe.setEventDate(jsonObject.getString("tbl_school_events_start_date"));
                            dbe.setEventEndDate(jsonObject.getString("tbl_school_events_end_date"));
                            dbe.setEventImg(url);
                            dbe.setType("event");
                            eventsArray.add(dbe);
                        }
                    }
                    if (!result.contains("\"offlineExam\":null")) {
                        JSONArray offlineExamArray = jsonRootObject.getJSONArray("offlineExam");
                        for (int i = 0; i < offlineExamArray.length(); i++) {
                            dbe = new DatabeanEvents();
                            JSONObject jsonObject = offlineExamArray.getJSONObject(i);
                            dbe.setEventId(jsonObject.getString("tbl_exam_id"));
                            dbe.setEventTitle(jsonObject.getString("tbl_exam_name"));
                            dbe.setEventDesc(jsonObject.getString("exam_type"));
                            dbe.setEventDate(jsonObject.getString("tbl_exam_start_date"));
                            dbe.setEventEndDate(jsonObject.getString("tbl_exam_start_date"));
                            dbe.setEventImg("");
                            dbe.setType("offline exam");
                            eventsArray.add(dbe);
                        }
                    }
                    if (!result.contains("\"onlineExam\":null")) {
                        JSONArray onlineExamArray = jsonRootObject.getJSONArray("onlineExam");
                        for (int i = 0; i < onlineExamArray.length(); i++) {
                            dbe = new DatabeanEvents();
                            JSONObject jsonObject = onlineExamArray.getJSONObject(i);
                            dbe.setEventId(jsonObject.getString("tbl_online_exams_id"));
                            dbe.setEventTitle(jsonObject.getString("tbl_online_exam_nm"));
                            dbe.setEventDesc(jsonObject.getString("tbl_stnt_prsnl_data_section"));
                            dbe.setEventDate(jsonObject.getString("tbl_online_exam_date"));
                            dbe.setEventEndDate(jsonObject.getString("tbl_online_exam_end_date"));
                            dbe.setEventImg("");
                            dbe.setType("online exam");
                            eventsArray.add(dbe);
                        }
                    }
                    if (!result.contains("\"activities\":null")) {
                        JSONArray activityArray = jsonRootObject.getJSONArray("activities");
                        for (int i = 0; i < activityArray.length(); i++) {
                            dbe = new DatabeanEvents();
                            JSONObject jsonObject = activityArray.getJSONObject(i);
                            dbe.setEventId(jsonObject.getString("tbl_school_activities_id"));
                            dbe.setEventTitle(jsonObject.getString("tbl_school_activities_title"));
                            dbe.setEventDesc(jsonObject.getString("tbl_school_activities_desc"));
                            dbe.setEventDate(jsonObject.getString("tbl_school_activities_date"));
                            dbe.setEventEndDate(jsonObject.getString("tbl_school_activities_date"));
                            dbe.setEventImg(jsonObject.getString("tbl_school_activities_img"));
                            dbe.setType("activity");
                            eventsArray.add(dbe);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (eventsArray.size() != 0) {
                    eventDetails();
                }
                super.onPostExecute(result);
            }
        }

    }

    @SuppressLint("StaticFieldLeak")
    public class GetAllEvents1 extends AsyncTask<String, String, String> {
        Context ctx;

        GetAllEvents1(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            mSwipeRefreshLayout.setRefreshing(true);
            add_layout.setVisibility(View.GONE);
            mainLayoutEvent.setVisibility(View.GONE);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            String schoolId = params[0];
            String userSection = params[1];
            String userrType = params[2];
            String data;

            try {

                URL url = new URL(ALL_UPCOMMING_EVENTS_UNIQUE);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                data = URLEncoder.encode("schoolId", "UTF-8") + "=" + URLEncoder.encode(schoolId, "UTF-8") + "&" +
                        URLEncoder.encode("userSection", "UTF-8") + "=" + URLEncoder.encode(userSection, "UTF-8") + "&" +
                        URLEncoder.encode("userType", "UTF-8") + "=" + URLEncoder.encode(userrType, "UTF-8");
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
            Log.d("Upcomming", "" + result);
            sp_event = getSharedPreferences(PREFS_EVENT1, 0);
            se_event = sp_event.edit();
            se_event.clear();
            se_event.putString("json_event_upcomming", result);
            se_event.commit();
            mSwipeRefreshLayout.setRefreshing(false);
            mainLayoutEvent.setVisibility(View.VISIBLE);

            if (userRoleId.equals("1")) {
                add_layout.setVisibility(View.VISIBLE);
            } else {
                add_layout.setVisibility(View.GONE);

            }

            if (result.equals("{\"result\":null}")) {
                noEventTv.setVisibility(View.VISIBLE);
                upcoming_listView.setVisibility(View.GONE);
            } else {
                noEventTv.setVisibility(View.GONE);
                upcoming_listView.setVisibility(View.VISIBLE);

                try {
                    JSONObject jsonRootObject = new JSONObject(result);
                    if (!result.contains("\"result\":null")) {
                        JSONArray jsonArray = jsonRootObject.getJSONArray("result");
                        eventsArray1.clear();
                        for (int i = 0; i < jsonArray.length(); i++) {
                            dbe = new DatabeanEvents();
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            eventImg = jsonObject.getString("tbl_school_events_imgs");
                            String[] image = eventImg.split(",");
                            String url = settings.getString("imageUrl", "") + userSchoolId + "/event/" + image[0].replace("\"", "").replace("[", "").replace("]", "");
                            dbe.setEventId(jsonObject.getString("tbl_school_events_id"));
                            dbe.setEventTitle(jsonObject.getString("tbl_school_events_title"));
                            dbe.setEventDesc(jsonObject.getString("tbl_school_events_desc"));
                            dbe.setEventDate(jsonObject.getString("tbl_school_events_start_date"));
                            dbe.setEventEndDate(jsonObject.getString("tbl_school_events_end_date"));
                            dbe.setEventImg(url);
                            dbe.setType("event");
                            eventsArray1.add(dbe);
                        }
                    }
                    if (!result.contains("\"offlineExam\":null")) {
                        JSONArray offlineExamArray = jsonRootObject.getJSONArray("offlineExam");
                        for (int i = 0; i < offlineExamArray.length(); i++) {
                            dbe = new DatabeanEvents();
                            JSONObject jsonObject = offlineExamArray.getJSONObject(i);
                            dbe.setEventId(jsonObject.getString("tbl_exam_id"));
                            dbe.setEventTitle(jsonObject.getString("tbl_exam_name"));
                            dbe.setEventDesc(jsonObject.getString("exam_type"));
                            dbe.setEventDate(jsonObject.getString("tbl_exam_start_date"));
                            dbe.setEventEndDate(jsonObject.getString("tbl_exam_start_date"));
                            dbe.setEventImg("");
                            dbe.setType("offline exam");
                            eventsArray1.add(dbe);
                        }
                    }
                    if (!result.contains("\"onlineExam\":null")) {
                        JSONArray onlineExamArray = jsonRootObject.getJSONArray("onlineExam");
                        for (int i = 0; i < onlineExamArray.length(); i++) {
                            dbe = new DatabeanEvents();
                            JSONObject jsonObject = onlineExamArray.getJSONObject(i);
                            dbe.setEventId(jsonObject.getString("tbl_online_exams_id"));
                            dbe.setEventTitle(jsonObject.getString("tbl_online_exam_nm"));
                            dbe.setEventDesc(jsonObject.getString("tbl_stnt_prsnl_data_section"));
                            dbe.setEventDate(jsonObject.getString("tbl_online_exam_date"));
                            dbe.setEventEndDate(jsonObject.getString("tbl_online_exam_end_date"));
                            dbe.setEventImg("");
                            dbe.setType("online exam");
                            eventsArray1.add(dbe);
                        }
                    }
                    if (!result.contains("\"activities\":null")) {
                        JSONArray activityArray = jsonRootObject.getJSONArray("activities");
                        for (int i = 0; i < activityArray.length(); i++) {
                            dbe = new DatabeanEvents();
                            JSONObject jsonObject = activityArray.getJSONObject(i);
                            dbe.setEventId(jsonObject.getString("tbl_school_activities_id"));
                            dbe.setEventTitle(jsonObject.getString("tbl_school_activities_title"));
                            dbe.setEventDesc(jsonObject.getString("tbl_school_activities_desc"));
                            dbe.setEventDate(jsonObject.getString("tbl_school_activities_date"));
                            dbe.setEventEndDate(jsonObject.getString("tbl_school_activities_date"));
                            dbe.setEventImg(jsonObject.getString("tbl_school_activities_img"));
                            dbe.setType("activity");
                            eventsArray1.add(dbe);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (eventsArray1.size() != 0) {
                    eventDetails();
                } else {
                    noEventTv.setVisibility(View.VISIBLE);
                    upcoming_listView.setVisibility(View.GONE);
                }
                super.onPostExecute(result);
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private void eventDetails() {
        noEventTv.setVisibility(View.GONE);
        upcoming_listView.setVisibility(View.VISIBLE);
        calendarView.setSelectionMode(MaterialCalendarView.SELECTION_MODE_NONE);
        calendarView.setAllowClickDaysOutsideCurrentMonth(false);
        calendarView.setWeekDayFormatter(new WeekDayFormatter() {
            @Override
            public CharSequence format(int dayOfWeek) {
                switch (dayOfWeek) {
                    case 2:
                        return "Mon";
                    case 3:
                        return "Tues";
                    case 4:
                        return "Wed";
                    case 5:
                        return "Thur";
                    case 6:
                        return "Fri";
                    case 7:
                        return "Sat";
                    case 1:
                        return "Sun";
                }
                return null;
            }
        });
        calendarView.setShowOtherDates(MaterialCalendarView.SHOW_NONE);
        calendarView.setDateSelected(CalendarDay.today(), true);
        calendarView.setSelectionColor(Color.WHITE);
        calendarView.addDecorators(new EventDecor(), new TodayDecor(CalendarDay.today()));
        for (int i = 0; i < eventsArray.size(); i++) {
            try {
                if (!eventsArray.get(i).getEventDate().equals("null")) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                    Date date = sdf.parse(eventsArray.get(i).getEventDate());
                    calendarView.setDateSelected(date, true);
                    calendarView.setSelectionColor(Color.parseColor(selToolColor));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        ArrayList<DatabeanEvents> arrayList = new ArrayList<>();

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM", Locale.getDefault());
        try {
            String today = sdf.format(CalendarDay.today().getDate());
            SimpleDateFormat dateFormat = new SimpleDateFormat("MM", Locale.getDefault());
            eventText.setText(getMonth(dateFormat.format(CalendarDay.today().getDate())) + " Events");
            for (int i = 0; i < eventsArray1.size(); i++) {
                if (!eventsArray1.get(i).getEventDate().equals("null")) {
                    String eventMonth = sdf.format(sdf.parse(eventsArray1.get(i).getEventDate()));
                    if (today.equals(eventMonth)) {
                        arrayList.add(eventsArray1.get(i));
                    }
                }
            }
            if (arrayList.size() != 0) {
                eventDetails1(arrayList);
                noEventTv.setVisibility(View.GONE);
                upcoming_listView.setVisibility(View.VISIBLE);
            } else {
                noEventTv.setVisibility(View.VISIBLE);
                upcoming_listView.setVisibility(View.GONE);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        calendarView.setOnMonthChangedListener((widget, date) -> {
            arrayList.clear();
            try {
                String month = sdf.format(date.getDate());
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM", Locale.getDefault());
                eventText.setText(getMonth(dateFormat.format(date.getDate())) + " Events");
                for (int i = 0; i < eventsArray1.size(); i++) {
                    String eventMonth = sdf.format(sdf.parse(eventsArray1.get(i).getEventDate()));
                    String[] month1 = month.split("-");
                    String m1 = month1[0] + "-" + month1[1];
                    if (m1.equals(eventMonth)) {
                        arrayList.add(eventsArray1.get(i));
                    }
                }
                if (arrayList.size() != 0) {
                    eventDetails1(arrayList);
                    noEventTv.setVisibility(View.GONE);
                    upcoming_listView.setVisibility(View.VISIBLE);
                } else {
                    noEventTv.setVisibility(View.VISIBLE);
                    upcoming_listView.setVisibility(View.GONE);
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        });

    }

    private void eventDetails1(ArrayList<DatabeanEvents> eventsArray1) {

        upcommingListAdaptor = new UpcommingListAdaptor(SchoolCalenderActivity.this, R.layout.upcomming_list_item, eventsArray1);
        upcoming_listView.setAdapter(upcommingListAdaptor);
        upcoming_listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent i11 = new Intent(SchoolCalenderActivity.this, AchievmentDetailsActivity.class);
            switch (eventsArray1.get(position).getType()) {
                case "event":
                    i11.putExtra("activiId", eventsArray1.get(position).getEventId());
                    i11.putExtra("achImg", eventImg);
                    i11.putExtra("achCate", "event");
                    i11.putExtra("achTitle", eventsArray1.get(position).getEventTitle());
                    i11.putExtra("achDesc", eventsArray1.get(position).getEventDesc());
                    i11.putExtra("acgDate", eventsArray1.get(position).getEventDate());
                    i11.putExtra("acgEndDate", eventsArray1.get(position).getEventEndDate());
                    startActivity(i11);
                    break;
                case "activity":
                    i11.putExtra("activiId", eventsArray1.get(position).getEventId());
                    i11.putExtra("achImg", eventImg);
                    i11.putExtra("achCate", "activities");
                    i11.putExtra("achTitle", eventsArray1.get(position).getEventTitle());
                    i11.putExtra("achDesc", eventsArray1.get(position).getEventDesc());
                    i11.putExtra("acgDate", eventsArray1.get(position).getEventDate());
                    startActivity(i11);
                    break;
                case "offline exam": {
                    Intent i = new Intent(this, StudentTimeTableActivity.class);
                    i.putExtra("loc", "sidebarexam");
                    startActivity(i);
                    break;
                }
                case "online exam": {
                    Intent i = new Intent(this, OnlineExam.class);
                    startActivity(i);
                    break;
                }
            }
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
    }

    private String getMonth(String month) {
        switch (month) {
            case "01":
                return "January";
            case "02":
                return "February";
            case "03":
                return "March";
            case "04":
                return "April";
            case "05":
                return "May";
            case "06":
                return "June";
            case "07":
                return "July";
            case "08":
                return "August";
            case "09":
                return "September";
            case "10":
                return "October";
            case "11":
                return "November";
            case "12":
                return "December";
        }
        return null;
    }

    public class EventDecor implements DayViewDecorator {

        EventDecor() {
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            List<CalendarDay> days = calendarView.getSelectedDates();
            return days.contains(day) && !day.equals(CalendarDay.today());
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new ForegroundColorSpan(Color.parseColor(selToolColor)));
            Drawable drawable = getResources().getDrawable(R.drawable.day_item_background);
            drawable.setColorFilter(Color.WHITE, PorterDuff.Mode.MULTIPLY);
            view.setSelectionDrawable(drawable);
        }
    }

    public class TodayDecor implements DayViewDecorator {

        private CalendarDay day;

        TodayDecor(CalendarDay day) {
            this.day = day;
        }

        @Override
        public boolean shouldDecorate(CalendarDay day) {
            return day.equals(this.day);
        }

        @Override
        public void decorate(DayViewFacade view) {
            view.addSpan(new ForegroundColorSpan(Color.parseColor(selDrawerColor)));
            Drawable drawable = getResources().getDrawable(R.drawable.cube);
            drawable.setColorFilter(Color.parseColor(selToolColor), PorterDuff.Mode.ADD);
            view.setSelectionDrawable(drawable);
        }
    }
}
