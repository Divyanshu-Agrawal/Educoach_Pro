package com.aaptrix.activitys.student;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;

import com.aaptrix.BuildConfig;
import com.aaptrix.activitys.AppInfo;
import com.aaptrix.activitys.GetHelp;
import com.aaptrix.activitys.InactiveInstitute;
import com.aaptrix.activitys.ReferralActivity;
import com.aaptrix.activitys.UserProfile;
import com.aaptrix.activitys.WelcomeActivity;
import com.aaptrix.activitys.MobileNumberActivity;
import com.aaptrix.activitys.admin.InstituteBuzzActivityDiff;
import com.aaptrix.activitys.teacher.SchoolCalenderActivity;
import com.aaptrix.activitys.teacher.StudentAttenInter;

import androidx.annotation.NonNull;

import com.aaptrix.adaptor.ActivityAdapter;
import com.aaptrix.databeans.DataBeanActivities;
import com.aaptrix.databeans.PermissionData;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.navigation.NavigationView;

import androidx.cardview.widget.CardView;
import androidx.core.app.ShareCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.kobakei.ratethisapp.RateThisApp;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import com.aaptrix.tools.ShareInstitute;

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
import de.hdodenhof.circleimageview.CircleImageView;

import com.aaptrix.databeans.DataBeanInstitueBuzz;
import com.aaptrix.databeans.DataBeanStudent;
import com.aaptrix.adaptor.InstitueBuzzAdaptor;
import com.aaptrix.adaptor.UserListAdaptor;
import com.aaptrix.R;

import javax.net.ssl.SSLContext;

import static com.aaptrix.activitys.SplashScreen.SCHOOL_ID;
import static com.aaptrix.activitys.SplashScreen.SCHOOL_NAME;
import static com.aaptrix.tools.HttpUrl.ALL_ACTIVITIES;
import static com.aaptrix.tools.HttpUrl.ALL_INSTITUTE_BUZZ_CATE;
import static com.aaptrix.tools.HttpUrl.ALL_ONLINE_EXAM;
import static com.aaptrix.tools.HttpUrl.GET_PERMISSION;
import static com.aaptrix.tools.HttpUrl.LOGOUT;
import static com.aaptrix.tools.HttpUrl.SWITCH_USERS;
import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREFS_RW;
import static com.aaptrix.tools.SPClass.PREFS_USER;
import static com.aaptrix.tools.SPClass.PREF_COLOR;
import static com.aaptrix.tools.SPClass.PREF_ROLE;
import static cz.msebera.android.httpclient.conn.ssl.SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER;

public class InstituteBuzzActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ImageView iv_edit;
    CircleImageView header_img;
    CircleImageView iv_user_img, prof_logo1;
    TextView tv_std_id, tv_std_class, tv_std_rollnumber, tv_std_cls_teacher, tv_today_day_date;
    SharedPreferences.Editor editor;
    String userId, roleId, schoolId, userSection, userRollNumber, userClassTeacher;
    GridView institue_lv;
    DataBeanInstitueBuzz dbib;
    ArrayList<DataBeanInstitueBuzz> instiBuzzArray = new ArrayList<>();
    InstitueBuzzAdaptor institueBuzzAdaptor;
    String instBuzzId, instBuzzName, instBuzzimg;
    LinearLayout iv_edit_layout;
    ProgressBar loader;
    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mToggle;
    NavigationView mNavigationView;
    TextView header_name, header_role;
    LinearLayout headerlayout;
    SharedPreferences.Editor se_institue_buzz;
    public static final String PREFS_INSTI_BUZZ = "json_institue_buzz";
    AlertDialog.Builder alert;
    RecyclerView announcements;
    LinearLayout announce_layout;

    SharedPreferences.Editor editorColor;
    String selToolColor, selDrawerColor, selStatusColor, selTextColor1, selTextColor2, userJson, numberOfUser, userSchoolSchoolLogo3;
    AppBarLayout appBarLayout;
    TextView tvID, tvCLASS, tvROLLNO, tvCLSTEACH, tool_title;
    SharedPreferences.Editor editorUser;

    ArrayList<DataBeanStudent> studentArray = new ArrayList<>();
    DataBeanStudent dbs;
    ListView user_list;
    UserListAdaptor userListAdaptor;
    String userID1, userLoginId1, userName1, userPhone1, userEmailId1, userDob1, userGender1, userImg1, userPhoneStatus1, userrType1;
    String userID, userLoginId, userName, userPhone, userEmailId, userDob, userGender, userImg, userPhoneStatus, userrType;
    String userSchoolId, userSchoolName, userSchoolRoleId, userSchoolRoleName, userSchoolSchoolLogo, userSchoolSchoolLogo1;
    String userSchoolId1, userSchoolName1, userSchoolRoleId1, userSchoolRoleName1, userSchoolSchoolLogo11, userSchoolSchoolLogo12, userSchoolSchoolLogo13;
    AlertDialog alertDialog;
    String selToolColor1, selDrawerColor1, selStatusColor1, selTextColor11, selTextColor22;
    String str_class1, str_section1, str_roll_number1, str_teacher_name1;
    String str_class, str_section, str_roll_number, str_teacher_name;
    LinearLayout logo_layout;
    String user_token_id, android_id;
    MediaPlayer mp;
    boolean doubleBackToExitPressedOnce = false;
    ArrayList<DataBeanActivities> activitiesArray = new ArrayList<>();
    RecyclerView.LayoutManager mLayoutManager;
    int pos = 0;

    @SuppressLint({"CommitPrefEdits", "SetTextI18n", "HardwareIds"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.institute_buzz_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("");
        setResult(RESULT_OK);
        appBarLayout = findViewById(R.id.appBarLayout);
        tool_title = findViewById(R.id.tool_title);
        announce_layout = findViewById(R.id.announce_layout);
        announcements = findViewById(R.id.announcement);

        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        announcements.setLayoutManager(mLayoutManager);
        announcements.setNestedScrollingEnabled(false);
        announcements.setHasFixedSize(true);
        announcements.setLayoutFrozen(true);
        announcements.setItemAnimator(new DefaultItemAnimator());

        SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
        editorColor = settingsColor.edit();
        selToolColor = settingsColor.getString("tool", "");
        selDrawerColor = settingsColor.getString("drawer", "");
        selStatusColor = settingsColor.getString("status", "");
        selTextColor1 = settingsColor.getString("text1", "");
        selTextColor2 = settingsColor.getString("text2", "");

        mDrawerLayout = findViewById(R.id.drawer);
        mp = MediaPlayer.create(this, R.raw.button_click);

        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        mNavigationView = findViewById(R.id.navigation_drawer);
        mNavigationView.setNavigationItemSelectedListener(this);

        //header
        View v = mNavigationView.inflateHeaderView(R.layout.navi_header);
        v.findViewById(R.id.get_more_info).setVisibility(View.GONE);
        header_img = v.findViewById(R.id.header_img);
        header_name = v.findViewById(R.id.header_name);
        header_role = v.findViewById(R.id.header_role);
        headerlayout = v.findViewById(R.id.headerlayout);
        headerlayout.setOnClickListener(view -> {
            startActivity(new Intent(this, UserProfile.class));
            mDrawerLayout.closeDrawer(GravityCompat.START);
        });

        RateThisApp.onStart(this);
        RateThisApp.Config config = new RateThisApp.Config(3, 15);
        RateThisApp.init(config);
        if (RateThisApp.shouldShowRateDialog()) {
            new AlertDialog.Builder(this)
                    .setMessage("Enjoying this app!")
                    .setPositiveButton("Yes", (dialog, which) -> RateThisApp.showRateDialogIfNeeded(this))
                    .setNegativeButton("No", null)
                    .show();
        }

        iv_user_img = findViewById(R.id.iv_user_img);
        prof_logo1 = findViewById(R.id.prof_logo1);

        iv_edit = findViewById(R.id.iv_edit1);
        logo_layout = findViewById(R.id.logo_layout);

        tv_std_id = findViewById(R.id.tv_std_id);
        tvID = findViewById(R.id.tvID);
        tvCLASS = findViewById(R.id.tvCLASS);
        tv_std_class = findViewById(R.id.tv_std_class);
        tvROLLNO = findViewById(R.id.tvROLLNO);
        tv_std_rollnumber = findViewById(R.id.tv_std_rollnumber);
        tvCLSTEACH = findViewById(R.id.tvCLSTEACH);
        tv_std_cls_teacher = findViewById(R.id.tv_std_cls_teacher);
        tv_today_day_date = findViewById(R.id.tv_today_day_date);
        loader = findViewById(R.id.loader);
        iv_edit_layout = findViewById(R.id.iv_edit_layout);

        institue_lv = findViewById(R.id.institue_lv);

        Calendar c = Calendar.getInstance();
        System.out.println("Current time => " + c.getTime());

        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c.getTime());
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE", Locale.getDefault());
        Date d = new Date();
        String dayOfTheWeek = sdf.format(d);
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        editor = settings.edit();
        userId = settings.getString("userID", "");
        userLoginId = settings.getString("userLoginId", "");
        userName = settings.getString("userName", "");
        userImg = settings.getString("userImg", "");
        userPhone = settings.getString("userPhone", "");
        userSection = settings.getString("userSection", "");
        userRollNumber = settings.getString("userRollNumber", "");
        userClassTeacher = settings.getString("userTeacherName", "");
        userrType = settings.getString("userrType", "");
        roleId = settings.getString("str_role_id", "");
        schoolId = settings.getString("str_school_id", "");
        numberOfUser = settings.getString("numberOfUser", "");

        GetAllActivities b = new GetAllActivities(this);
        b.execute(schoolId, userSection, userrType);

        GetExam getExam = new GetExam(this);
        getExam.execute(schoolId, userSection, userId);

        GetPermission getPermission = new GetPermission(this);
        if (getSharedPreferences(PREF_ROLE, 0).getString("userRole", "").equals("Parent")) {
            getPermission.execute(schoolId, getSharedPreferences(PREF_ROLE, 0).getString("userRoleID", ""));
        } else {
            getPermission.execute(schoolId, roleId);
        }

        userSchoolSchoolLogo3 = settings.getString("userSchoolLogo1", "");
        //popup
        tv_std_id.setText(userName);
        tv_std_class.setText(userSection);
        tv_std_rollnumber.setText(userRollNumber);
        tv_std_cls_teacher.setText(userPhone);
        tv_today_day_date.setText(dayOfTheWeek + ", " + formattedDate);

        SharedPreferences settingsUser = getSharedPreferences(PREFS_USER, 0);
        editorUser = settingsUser.edit();
        userJson = settingsUser.getString("user", "");

        user_token_id = FirebaseInstanceId.getInstance().getToken();
        editor.putString("token", user_token_id);
        editor.apply();
        editor = settings.edit();
        android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        if (numberOfUser.equals("multiple")) {
            prof_logo1.setVisibility(View.VISIBLE);

            if (userImg.equals("0")) {
                prof_logo1.setImageDrawable(getResources().getDrawable(R.drawable.user_place_hoder));
            } else if (!TextUtils.isEmpty(userImg)) {
                String url;
                switch (userrType) {
                    case "Parent":
                    case "Student":
                        url = settings.getString("imageUrl", "") + settings.getString("userSchoolId", "") + "/users/students/profile/" + userImg;
                        Picasso.with(this).load(url).error(R.drawable.dummy).placeholder(R.drawable.dummy).into(prof_logo1);
                        break;
                    case "Admin":
                        url = settings.getString("imageUrl", "") + settings.getString("userSchoolId", "") + "/users/admin/profile/" + userImg;
                        Picasso.with(this).load(url).error(R.drawable.dummy).placeholder(R.drawable.dummy).into(prof_logo1);
                        break;
                    case "Staff":
                        url = settings.getString("imageUrl", "") + settings.getString("userSchoolId", "") + "/users/staff/profile/" + userImg;
                        Picasso.with(this).load(url).error(R.drawable.dummy).placeholder(R.drawable.dummy).into(prof_logo1);
                        break;
                    case "Teacher":
                        url = settings.getString("imageUrl", "") + settings.getString("userSchoolId", "") + "/users/teachers/profile/" + userImg;
                        Picasso.with(this).load(url).error(R.drawable.dummy).placeholder(R.drawable.dummy).into(prof_logo1);
                        break;
                }
            } else {
                prof_logo1.setImageDrawable(getResources().getDrawable(R.drawable.user_place_hoder));
            }
        } else if (numberOfUser.equals("single")) {
            prof_logo1.setVisibility(View.GONE);
            logo_layout.setVisibility(View.GONE);
        }


        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.cancelAll();

        header_name.setText(userName);
        if (userrType.equals("Parent")) {
            header_role.setText("Student");
        } else {
            header_role.setText(userrType);
        }

        if (userImg.equals("0")) {
            iv_user_img.setImageDrawable(getResources().getDrawable(R.drawable.user_place_hoder));
            header_img.setImageDrawable(getResources().getDrawable(R.drawable.user_place_hoder));
        } else if (!TextUtils.isEmpty(userImg)) {
            String url;
            switch (userrType) {
                case "Parent":
                case "Student":
                    url = settings.getString("imageUrl", "") + settings.getString("userSchoolId", "") + "/users/students/profile/" + userImg;
                    Picasso.with(this).load(url).error(R.drawable.dummy).placeholder(R.drawable.dummy).into(iv_user_img);
                    Picasso.with(this).load(url).error(R.drawable.dummy).placeholder(R.drawable.dummy).into(header_img);
                    break;
                case "Admin":
                    url = settings.getString("imageUrl", "") + settings.getString("userSchoolId", "") + "/users/admin/profile/" + userImg;
                    Picasso.with(this).load(url).error(R.drawable.dummy).placeholder(R.drawable.dummy).into(iv_user_img);
                    Picasso.with(this).load(url).error(R.drawable.dummy).placeholder(R.drawable.dummy).into(header_img);
                    break;
                case "Staff":
                    url = settings.getString("imageUrl", "") + settings.getString("userSchoolId", "") + "/users/staff/profile/" + userImg;
                    Picasso.with(this).load(url).error(R.drawable.dummy).placeholder(R.drawable.dummy).into(iv_user_img);
                    Picasso.with(this).load(url).error(R.drawable.dummy).placeholder(R.drawable.dummy).into(header_img);
                    break;
                case "Teacher":
                    url = settings.getString("imageUrl", "") + settings.getString("userSchoolId", "") + "/users/teachers/profile/" + userImg;
                    Picasso.with(this).load(url).error(R.drawable.dummy).placeholder(R.drawable.dummy).into(iv_user_img);
                    Picasso.with(this).load(url).error(R.drawable.dummy).placeholder(R.drawable.dummy).into(header_img);
                    break;
            }
        } else {
            iv_user_img.setImageDrawable(getResources().getDrawable(R.drawable.user_place_hoder));
            header_img.setImageDrawable(getResources().getDrawable(R.drawable.user_place_hoder));
        }

        try {
            File directory = this.getFilesDir();
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File(directory, "instituteBuzz")));
            String json = in.readObject().toString();
            Log.e("Json", "" + json);
            in.close();
            JSONObject jsonRootObject = new JSONObject(json);
            JSONArray jsonArray = jsonRootObject.getJSONArray("result");
            SharedPreferences.Editor sp = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
            sp.putString("anonymous_feedback", jsonRootObject.getString("feedback_anonymous_permission")).apply();
            if (userrType.equals("Student")) {
                JSONObject object = jsonRootObject.getJSONObject("Batch_details");
                if (!userSection.equals(object.getString("tbl_stnt_prsnl_data_section"))) {
                    logout(userPhone);
                }
            }

            instiBuzzArray.clear();

            ArrayList<String> name = new ArrayList<>();
            name.add("About Us");
            name.add("Institute Calendar");
            name.add("What's New!");
            name.add("Attendance");
            name.add("Remarks");
            name.add("Assignments");
            name.add("Results");
            name.add("Time Table");
            name.add("Study Materials");
            name.add("Study Videos");
            name.add("Gallery");
            name.add("Teaching Staff");
            name.add("Refer a Friend");
            name.add("Feedback");
            name.add("Online Exam");

            for (int i = 0; i < jsonArray.length(); i++) {
                dbib = new DataBeanInstitueBuzz();
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (name.contains(jsonObject.getString("tbl_insti_buzz_cate_name"))) {
                    instBuzzId = jsonObject.getString("tbl_insti_buzz_cate_id");
                    instBuzzName = jsonObject.getString("tbl_insti_buzz_cate_name");
                    dbib.setInstBuzzCateId(instBuzzId);
                    dbib.setInstBuzzName(instBuzzName);
                    instiBuzzArray.add(dbib);
                }
            }
            listItms();
        } catch (Exception e) {
            e.printStackTrace();
            GetAllInstituteBuzz b1 = new GetAllInstituteBuzz(InstituteBuzzActivity.this);
            String id;
            if (settings.getString("userRole", "").equals("Parent")) {
                id = settings.getString("userRoleID", "");
            } else {
                id = roleId;
            }
            b1.execute(id, schoolId, user_token_id, android_id, userId);
        }

        logo_layout.setOnClickListener(view -> {
            if (isInternetOn()) {
                GetUserLoginCheck b1 = new GetUserLoginCheck(InstituteBuzzActivity.this);
                b1.execute(userPhone, "");
            } else {
                Toast.makeText(InstituteBuzzActivity.this, "No internet available", Toast.LENGTH_SHORT).show();
            }
        });

        iv_edit.setOnClickListener(view -> startActivity(new Intent(this, UserProfile.class)));

        appBarLayout.setBackgroundColor(Color.parseColor(selToolColor));
        headerlayout.setBackgroundColor(Color.parseColor(selDrawerColor));
        tv_today_day_date.setBackgroundColor(Color.parseColor(selToolColor));
        tvID.setTextColor(Color.parseColor(selToolColor));
        tvCLASS.setTextColor(Color.parseColor(selToolColor));
        tvROLLNO.setTextColor(Color.parseColor(selToolColor));
        tvCLSTEACH.setTextColor(Color.parseColor(selToolColor));
        mNavigationView.setBackgroundColor(Color.parseColor(selDrawerColor));
        header_role.setTextColor(Color.parseColor(selToolColor));
        GradientDrawable bgShape = (GradientDrawable) iv_edit_layout.getBackground();
        bgShape.setColor(Color.parseColor(selDrawerColor));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(selStatusColor));
        }
        tool_title.setTextColor(Color.parseColor(selTextColor1));
        header_name.setTextColor(Color.parseColor(selTextColor1));
        mNavigationView.setItemTextColor(ColorStateList.valueOf(Color.parseColor(selTextColor1)));
        tv_today_day_date.setTextColor(Color.parseColor(selTextColor1));

    }

    @SuppressLint("StaticFieldLeak")
    public class GetAllActivities extends AsyncTask<String, String, String> {
        Context ctx;

        GetAllActivities(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            String schoolId = params[0];
            String userSection = params[1];
            String userrType = params[2];
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
            if (result.equals("{\"result\":null}")) {
                announce_layout.setVisibility(View.GONE);
                tv_today_day_date.setVisibility(View.VISIBLE);
            } else {
                try {
                    JSONObject jsonRootObject = new JSONObject(result);
                    JSONArray jsonArray = jsonRootObject.getJSONArray("result");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        DataBeanActivities dbact = new DataBeanActivities();
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        dbact.setActiviTitle(jsonObject.getString("tbl_school_activities_title"));
                        activitiesArray.add(dbact);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (activitiesArray.size() != 0) {
                    setAnnouncements();
                } else {
                    announce_layout.setVisibility(View.GONE);
                    tv_today_day_date.setVisibility(View.VISIBLE);
                }
            }
            super.onPostExecute(result);
        }
    }

    private void setAnnouncements() {
        ActivityAdapter adapter = new ActivityAdapter(this, R.layout.list_announcement, activitiesArray, "announcements");
        announcements.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        new CountDownTimer(4000, 4000) {

            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                if (pos == activitiesArray.size())
                    pos = 0;
                announcements.smoothScrollToPosition(pos);
                pos++;
                start();
            }
        }.start();

        announce_layout.setOnClickListener(v -> {
            mp.start();
            Intent i = new Intent(InstituteBuzzActivity.this, ActivitiesActivity.class);
            startActivity(i);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });
    }

    private void logout(String mobileno) {
        Toast.makeText(this, "Your session has expired. Please login again.", Toast.LENGTH_SHORT).show();
        File directory = this.getFilesDir();
        File file = new File(directory, "instituteBuzz");
        file.delete();
        file = new File(directory, "batches");
        file.delete();
        getSharedPreferences(PREF_COLOR, 0).edit().clear().apply();
        getSharedPreferences(PREF_ROLE, 0).edit().clear().apply();
        getSharedPreferences(PREFS_NAME, 0).edit().clear().apply();
        getSharedPreferences("date", 0).edit().clear().apply();
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString("logged", "notlogged");
        editor.putString("userID", "0");
        editor.putString("userLoginId", " ");
        editor.putString("userName", "Guest");
        editor.putString("userPhone", " ");
        editor.putString("userEmailId", " ");
        editor.putString("userDob", " ");
        editor.putString("userGender", "Guest");
        editor.putString("userPhoneStatus", "1");
        editor.putString("userrType", "Guest");
        editor.putString("userSchoolId", SCHOOL_ID);
        editor.putString("numberOfUser", "single");
        editor.putString("userPassword", null);
        editor.putString("userSection", " ");
        editor.putString("userRollNumber", " ");
        editor.putString("userTeacherName", " ");
        editor.putString("userSchoolLogo", " ");
        editor.putString("userSchoolLogo1", " ");
        editor.putString("userSchoolLogo3", " ");
        editor.putString("imageUrl", "https://dashboard.educoachapp.com//uploads/institute/institute_");

        //
        editor.putString("userSchoolName", SCHOOL_NAME);
        editor.putString("str_school_id", SCHOOL_ID);
        editor.putString("str_role_id", "7");
        editor.apply();

        //color set
        SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
        SharedPreferences.Editor editorColor = settingsColor.edit();

        editorColor.putString("tool", getResources().getString(R.string.tool));
        editorColor.putString("drawer", getResources().getString(R.string.drawer));
        editorColor.putString("status", getResources().getString(R.string.status));
        editorColor.putString("text1", getResources().getString(R.string.text1));
        editorColor.putString("text2", getResources().getString(R.string.text2));
        editorColor.apply();


        Intent i = new Intent(this, InstituteBuzzActivityDiff.class);
        startActivity(i);
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        new Thread(() -> {
            try {
                SSLContext sslContext = SSLContexts.custom().useTLS().build();
                SSLConnectionSocketFactory f = new SSLConnectionSocketFactory(
                        sslContext,
                        new String[]{"TLSv1.1", "TLSv1.2"},
                        null,
                        BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
                HttpClient httpclient = HttpClients.custom().setSSLSocketFactory(f).build();
                HttpPost httppost = new HttpPost(LOGOUT);
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<>(1);
                nameValuePairs.add(new BasicNameValuePair("mobno", mobileno));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity httpEntity = response.getEntity();
                final String result = EntityUtils.toString(httpEntity);

                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(() -> Log.e("result", result));

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == 1 && resultCode == RESULT_OK) {
//            Uri filePath = data.getData();
//
//            assert filePath != null;
//            CropImage.activity(filePath)
//                    .setGuidelines(CropImageView.Guidelines.ON)
//                    .setAspectRatio(150, 150)
//                    .setGuidelines(CropImageView.Guidelines.ON)
//                    .setCropShape(CropImageView.CropShape.OVAL)
//                    .start(this);
//        }
//
//        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
//            CropImage.ActivityResult result = CropImage.getActivityResult(data);
//            if (resultCode == RESULT_OK) {
//                Uri filePath = result.getUri();
//                try {
//                    // bitmap = MediaStore.Images.Media.getBitmap(ChildProfileActivity.this.getContentResolver(), filePath);
//                    File actualImage = FileUtil.from(InstituteBuzzActivity.this, filePath);
//                    File compressedImage = new Compressor(InstituteBuzzActivity.this)
//                            .setMaxWidth(640)
//                            .setMaxHeight(480)
//                            .setQuality(75)
//                            .setCompressFormat(Bitmap.CompressFormat.WEBP)
//                            .setDestinationDirectoryPath(Environment.getExternalStoragePublicDirectory(
//                                    Environment.DIRECTORY_PICTURES).getAbsolutePath())
//                            .compressToFile(actualImage);
//                    bitmap = MediaStore.Images.Media.getBitmap(InstituteBuzzActivity.this.getContentResolver(), Uri.fromFile(compressedImage));
//                    iv_user_img.setImageBitmap(bitmap);
//                    prof_logo1.setImageBitmap(bitmap);
//                    header_img.setImageBitmap(bitmap);
//
//                    UpdateProfileImage updateProfileImage = new UpdateProfileImage(InstituteBuzzActivity.this, compressedImage);
//                    updateProfileImage.execute(userId);
//
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//
//            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
//                Exception error = result.getError();
//                Toast.makeText(InstituteBuzzActivity.this, "" + error, Toast.LENGTH_SHORT).show();
//            }
//        }
//    }
//
//
//    @SuppressLint("StaticFieldLeak")
//    public class UpdateProfileImage extends AsyncTask<String, String, String> {
//        Context ctx;
//        File image;
//
//        UpdateProfileImage(Context ctx, File image) {
//            this.ctx = ctx;
//            this.image = image;
//        }
//
//        @Override
//        protected void onPreExecute() {
//            Toast.makeText(ctx, "Please wait we are updating your profile", Toast.LENGTH_SHORT).show();
//            super.onPreExecute();
//
//        }
//
//        @Override
//        protected String doInBackground(String... params) {
//
//            String userId = params[0];
//
//            try {
//
//                SSLContext sslContext = SSLContexts.custom().useTLS().build();
//                SSLConnectionSocketFactory f = new SSLConnectionSocketFactory(
//                        sslContext,
//                        new String[]{"TLSv1.1", "TLSv1.2"},
//                        null,
//                        BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
//                HttpClient httpclient = HttpClients.custom().setSSLSocketFactory(f).build();
//                HttpPost httppost = new HttpPost(UPDATE_USER_PRO_IMAGE);
//                MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
//                entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
//                FileBody newImage = new FileBody(image);
//                entityBuilder.addPart("image", newImage);
//                entityBuilder.addTextBody("userId", userId);
//                HttpEntity entity = entityBuilder.build();
//                httppost.setEntity(entity);
//                HttpResponse response = httpclient.execute(httppost);
//                HttpEntity httpEntity = response.getEntity();
//                String result = EntityUtils.toString(httpEntity);
//                Log.e("result", result);
//                return result;
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//            return null;
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            Log.d("Json", "" + result);
//            try {
//                JSONObject jsonObject = new JSONObject(result);
//                if (jsonObject.getString("success").equals("true")) {
//                    editor.putString("userImg", jsonObject.getString("imageNm"));
//                    editor.commit();
//                    Toast.makeText(InstituteBuzzActivity.this, "Your Image is Updated", Toast.LENGTH_SHORT).show();
//                } else {
//                    Toast.makeText(InstituteBuzzActivity.this, "Not uploaded image is too large", Toast.LENGTH_SHORT).show();
//                }
//            } catch (JSONException e) {
//                e.printStackTrace();
//            }
//            super.onPostExecute(result);
//        }
//
//    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (mToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.institute_buzz) {
            Intent i = new Intent(InstituteBuzzActivity.this, InstituteBuzzActivity.class);
            startActivity(i);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        } else if (id == R.id.share_ins) {
            ShareInstitute institute = new ShareInstitute(this, this);
            institute.execute(schoolId);
        } else if (id == R.id.share_app) {
            String msg = "Install " + SCHOOL_NAME + " " + "\nhttp://play.google.com/store/apps/details?id=" + getPackageName();
            ShareCompat.IntentBuilder.from(this)
                    .setType("text/plain")
                    .setChooserTitle("Share via...")
                    .setText(msg)
                    .startChooser();
        } else if (id == R.id.rate) {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse("market://details?id=" + getPackageName()));
            startActivity(i);
        } else if (id == R.id.app_info) {
            startActivity(new Intent(this, AppInfo.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        } else if (id == R.id.refer) {
            startActivity(new Intent(this, ReferralActivity.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        } else if (id == R.id.performance) {
            startActivity(new Intent(this, StudentResult.class).putExtra("userType", "student"));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        } else if (id == R.id.time_table) {
            Intent i = new Intent(InstituteBuzzActivity.this, StudentTimeTableActivity.class);
            i.putExtra("loc", "sidebarclass");
            startActivity(i);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        } else if (id == R.id.exam_time_table) {
            Intent i = new Intent(InstituteBuzzActivity.this, StudentTimeTableActivity.class);
            i.putExtra("loc", "sidebarexam");
            startActivity(i);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        } else if (id == R.id.feedback) {
            Intent i = new Intent(InstituteBuzzActivity.this, FeedbackActivity.class);
            startActivity(i);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        } else if (id == R.id.online_exam) {
            Intent i = new Intent(InstituteBuzzActivity.this, OnlineExam.class);
            startActivity(i);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        } else if (id == R.id.pay_fee) {
            startActivity(new Intent(this, FeePayment.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        } else if (id == R.id.help) {
            startActivity(new Intent(this, GetHelp.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        } else if (id == R.id.live_stream) {
            startActivity(new Intent(this, LiveStreaming.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @SuppressLint("StaticFieldLeak")
    public class GetExam extends AsyncTask<String, String, String> {
        Context ctx;

        GetExam(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            String school_id = params[0];
            String batch = params[1];
            String userId = params[2];
            String data;

            try {

                URL url = new URL(ALL_ONLINE_EXAM);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                data = URLEncoder.encode("school_id", "UTF-8") + "=" + URLEncoder.encode(school_id, "UTF-8") + "&" +
                        URLEncoder.encode("batch_nm", "UTF-8") + "=" + URLEncoder.encode(batch, "UTF-8") + "&" +
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
            if (!result.contains("{\"OnlineExamList\":null}")) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("OnlineExamList");
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.DATE, 0);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
                    SharedPreferences sp = getSharedPreferences("date", 0);
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        String start = object.getString("tbl_online_exam_date");
                        String end = object.getString("tbl_online_exam_end_date");
                        String startTime = object.getString("tbl_online_exam_start_time");
                        String endTime = object.getString("tbl_online_exam_end_time");
                        Date startdate = sdf.parse(start + " " + startTime);
                        Date enddate = sdf.parse(end + " " + endTime);
                        if (calendar.getTime().equals(startdate) || calendar.getTime().before(startdate) || (calendar.getTime().after(startdate) && calendar.getTime().before(enddate))) {
                            @SuppressLint("SimpleDateFormat")
                            String date = new SimpleDateFormat("dd-MM-yyyy").format(Calendar.getInstance().getTime());
                            if (!sp.getString("date", "").equals(date)) {
                                LayoutInflater factory = LayoutInflater.from(ctx);
                                @SuppressLint("InflateParams") final View view = factory.inflate(R.layout.online_exam_dialog, null);

                                TextView time = view.findViewById(R.id.start_time);

                                sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                                StringBuilder sb = new StringBuilder();
                                sb.append("On ").append(sdf.format(startdate)).append(" at ");
                                sdf = new SimpleDateFormat("hh:mm:ss", Locale.getDefault());
                                Date sttime = sdf.parse(startTime);
                                sdf = new SimpleDateFormat("hh:mm aa", Locale.getDefault());
                                sb.append(sdf.format(sttime));
                                time.setText(sb.toString());

                                AlertDialog.Builder alert = new AlertDialog.Builder(ctx, R.style.DialogTheme);

                                alert.setView(view).setPositiveButton("Show Exam",
                                        (dialog, whichButton) -> {
                                            sp.edit().putString("date", date).apply();
                                            Intent intent = new Intent(InstituteBuzzActivity.this, OnlineExam.class);
                                            startActivity(intent);
                                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                        }).setNegativeButton("Ok",
                                        (dialog, whichButton) -> {
                                            sp.edit().putString("date", date).apply();
                                        });
                                AlertDialog alertDialog = alert.create();
                                alertDialog.show();
                                Button theButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                                Button theButton1 = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                                theButton.setTextColor(getResources().getColor(R.color.text_gray));
                                theButton1.setTextColor(getResources().getColor(R.color.text_gray));
                                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                                        .setEnabled(true);
                                break;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            super.onPostExecute(result);
        }

    }

    @SuppressLint("StaticFieldLeak")
    public class GetAllInstituteBuzz extends AsyncTask<String, String, String> {
        Context ctx;

        GetAllInstituteBuzz(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            loader.setVisibility(View.VISIBLE);
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {

            String roleId = params[0];
            String schoolId = params[1];
            String tokenId = params[2];
            String androidId = params[3];
            String userId = params[4];

            String data;
            String android_version = String.valueOf(BuildConfig.VERSION_CODE);
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
            Log.e("Json", "" + result);
            //	pDialog.dismiss();
            loader.setVisibility(View.GONE);

            if (!result.contains("\"result\":null")) {
                try {
                    JSONObject jsonRootObject = new JSONObject(result);
                    JSONArray jsonArray = jsonRootObject.getJSONArray("result");
                    SharedPreferences sp_institue_buzz = getSharedPreferences(PREFS_INSTI_BUZZ, 0);
                    se_institue_buzz = sp_institue_buzz.edit();
                    se_institue_buzz.clear();
                    se_institue_buzz.putString("json_institue_buzz", result);
                    se_institue_buzz.putString("length", "" + jsonArray.length());
                    se_institue_buzz.apply();
                    SharedPreferences.Editor sp = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit();
                    sp.putString("anonymous_feedback", jsonRootObject.getString("feedback_anonymous_permission")).apply();
                    if (userrType.equals("Student")) {
                        JSONObject object = jsonRootObject.getJSONObject("Batch_details");
                        if (!userSection.equals(object.getString("tbl_stnt_prsnl_data_section"))) {
                            logout(userPhone);
                        }
                    }
                    String url = jsonRootObject.getString("url");
                    instiBuzzArray.clear();
                    //offline

                    ArrayList<String> name = new ArrayList<>();
                    name.add("About Us");
                    name.add("Institute Calendar");
                    name.add("What's New!");
                    name.add("Attendance");
                    name.add("Remarks");
                    name.add("Assignments");
                    name.add("Results");
                    name.add("Time Table");
                    name.add("Study Materials");
                    name.add("Study Videos");
                    name.add("Gallery");
                    name.add("Teaching Staff");
                    name.add("Refer a Friend");
                    name.add("Feedback");
                    name.add("Online Exam");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        dbib = new DataBeanInstitueBuzz();
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        if (name.contains(jsonObject.getString("tbl_insti_buzz_cate_name"))) {
                            instBuzzId = jsonObject.getString("tbl_insti_buzz_cate_id");
                            instBuzzName = jsonObject.getString("tbl_insti_buzz_cate_name");
                            instBuzzimg = url + jsonObject.getString("tbl_insti_buzz_cate_img");
                            dbib.setInstBuzzCateId(instBuzzId);
                            dbib.setInstBuzzName(instBuzzName);
                            dbib.setInstBuzzImg(instBuzzimg);
                            instiBuzzArray.add(dbib);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                listItms();
            } else {
                Toast.makeText(ctx, "No instituebuzz", Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(result);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mp.release();
    }

    private void listItms() {

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        params.height = (int) getResources().getDimension(R.dimen._110sdp) * (instiBuzzArray.size()/3);
        institue_lv.setLayoutParams(params);

        institueBuzzAdaptor = new InstitueBuzzAdaptor(InstituteBuzzActivity.this, R.layout.insti_buzz_list_item, instiBuzzArray);
        institue_lv.setAdapter(institueBuzzAdaptor);

        institue_lv.setOnItemClickListener((parent, view, position, id) -> {
            String hobbiesName = instiBuzzArray.get(position).getInstBuzzName();
            if (isInternetOn()) {
                Log.d("hobbiesName", hobbiesName);
                switch (hobbiesName) {
                    case "About Us": {
                        mp.start();
                        Intent i = new Intent(InstituteBuzzActivity.this, AboutUsActivity.class);
                        startActivity(i);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        break;
                    }
                    case "Institute Calendar": {
                        mp.start();
                        Intent i = new Intent(InstituteBuzzActivity.this, SchoolCalenderActivity.class);
                        startActivity(i);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        break;
                    }
                    case "Activities": {
                        mp.start();
                        Intent i = new Intent(InstituteBuzzActivity.this, ActivitiesActivity.class);
                        startActivity(i);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        break;
                    }
                    case "What's New!": {
                        mp.start();
                        Intent i = new Intent(InstituteBuzzActivity.this, PublicationActivity.class);
                        startActivity(i);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        break;
                    }
                    case "Attendance": {
                        mp.start();
                        Intent i = new Intent(InstituteBuzzActivity.this, StudentAttenInter.class);
                        startActivity(i);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        break;
                    }
                    case "Time Table": {
                        mp.start();
                        Intent i = new Intent(InstituteBuzzActivity.this, StudentTimeTableActivity.class);
                        i.putExtra("loc", "dashboard");
                        startActivity(i);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        break;
                    }
                    case "Remarks": {
                        mp.start();
                        Intent i = new Intent(InstituteBuzzActivity.this, DairyActivity.class);
                        startActivity(i);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        break;
                    }
                    case "Assignments": {
                        mp.start();
                        Intent i = new Intent(InstituteBuzzActivity.this, HomeworkActivity.class);
                        startActivity(i);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        break;
                    }
                    case "Results": {
                        mp.start();
                        Intent i = new Intent(InstituteBuzzActivity.this, StudentResult.class);
                        i.putExtra("userType", "student");
                        startActivity(i);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        break;
                    }
                    case "Study Materials": {
                        mp.start();
                        Intent i = new Intent(InstituteBuzzActivity.this, StudentMaterial.class);
                        startActivity(i);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                    break;
                    case "Study Videos": {
                        mp.start();
                        Intent i = new Intent(InstituteBuzzActivity.this, StudentVideo.class);
                        startActivity(i);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                    break;
                    case "Gallery": {
                        mp.start();
                        Intent intent = new Intent(InstituteBuzzActivity.this, GalleryActivity.class);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                    break;
                    case "Teaching Staff": {
                        mp.start();
                        Intent i = new Intent(InstituteBuzzActivity.this, StaffActivity.class);
                        startActivity(i);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        break;
                    }
                    case "Feedback": {
                        mp.start();
                        Intent i = new Intent(InstituteBuzzActivity.this, FeedbackActivity.class);
                        startActivity(i);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        break;
                    }
                    case "Refer a Friend": {
                        mp.start();
                        Intent i = new Intent(InstituteBuzzActivity.this, ReferralActivity.class);
                        startActivity(i);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        break;
                    }
                    case "Online Exam": {
                        mp.start();
                        Intent i = new Intent(this, OnlineExam.class);
                        startActivity(i);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        break;
                    }
                }
            } else {
                Toast.makeText(InstituteBuzzActivity.this, "No internet available", Toast.LENGTH_SHORT).show();
                switch (hobbiesName) {
                    case "About Us": {
                        mp.start();
                        Intent i = new Intent(InstituteBuzzActivity.this, AboutUsActivity.class);
                        startActivity(i);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        break;
                    }
                    case "Institute Calendar": {
                        mp.start();
                        Intent i = new Intent(InstituteBuzzActivity.this, SchoolCalenderActivity.class);
                        startActivity(i);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        break;
                    }
                    case "Activities": {
                        mp.start();
                        Intent i = new Intent(InstituteBuzzActivity.this, ActivitiesActivity.class);
                        startActivity(i);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        break;
                    }
                    case "What's New!": {
                        mp.start();
                        Intent i = new Intent(InstituteBuzzActivity.this, PublicationActivity.class);
                        startActivity(i);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        break;
                    }
                    case "Attendance": {
                        mp.start();
                        Intent i = new Intent(InstituteBuzzActivity.this, StudentAttenInter.class);
                        startActivity(i);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        Toast.makeText(InstituteBuzzActivity.this, "Not available without internet", Toast.LENGTH_SHORT).show();

                        break;
                    }
                    case "Time Table": {
                        mp.start();
                        Intent i = new Intent(InstituteBuzzActivity.this, StudentTimeTableActivity.class);
                        i.putExtra("loc", "dashboard");
                        startActivity(i);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        break;
                    }
                    case "Remarks": {
                        mp.start();
                        Intent i = new Intent(InstituteBuzzActivity.this, DairyActivity.class);
                        startActivity(i);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        break;
                    }
                    case "Assignment": {
                        mp.start();
                        Intent i = new Intent(InstituteBuzzActivity.this, HomeworkActivity.class);
                        startActivity(i);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        break;
                    }
                    case "Results": {
                        mp.start();
                        Intent i = new Intent(InstituteBuzzActivity.this, StudentResult.class);
                        i.putExtra("userType", "student");
                        startActivity(i);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        break;
                    }
                    case "Teaching Staff": {
                        mp.start();
                        Intent i = new Intent(InstituteBuzzActivity.this, StaffActivity.class);
                        startActivity(i);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        break;
                    }
                    case "Feedback": {
                        mp.start();
                        Intent i = new Intent(InstituteBuzzActivity.this, FeedbackActivity.class);
                        startActivity(i);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        break;
                    }
                    case "Refer Friend": {
                        mp.start();
                        Intent i = new Intent(InstituteBuzzActivity.this, ReferralActivity.class);
                        startActivity(i);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        break;
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);
            finish();
            System.exit(0);
        }
        this.doubleBackToExitPressedOnce = true;
        mDrawerLayout.closeDrawer(GravityCompat.START);
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);

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


    @SuppressLint("StaticFieldLeak")
    public class GetUserLoginCheck extends AsyncTask<String, String, String> {
        Context ctx;

        GetUserLoginCheck(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            logo_layout.setClickable(false);
            Toast.makeText(ctx, "Please wait...", Toast.LENGTH_SHORT).show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            String str_user_phone = params[0];
            String str_user_password = params[1];
            String data;
            try {
                URL url = new URL(SWITCH_USERS);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                data = URLEncoder.encode("str_user_phone", "UTF-8") + "=" + URLEncoder.encode(str_user_phone, "UTF-8") + "&" +
                        URLEncoder.encode("str_user_password", "UTF-8") + "=" + URLEncoder.encode(str_user_password, "UTF-8") + "&" +
                        URLEncoder.encode("user_type", "UTF-8") + "=" + URLEncoder.encode(getSharedPreferences(PREF_ROLE, 0).getString("userRole", ""), "UTF-8");
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

            //Log.d("USERS", result);
            logo_layout.setClickable(true);

            {
                try {
                    SharedPreferences settingsUser = getSharedPreferences(PREFS_USER, 0);
                    SharedPreferences.Editor editorUser = settingsUser.edit();
                    editorUser.putString("user", result);
                    editorUser.apply();

                    JSONObject jsonRootObject = new JSONObject(result);
                    JSONArray jsonArray = jsonRootObject.getJSONArray("result");
                    studentArray.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        dbs = new DataBeanStudent();

                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        if (jsonObject.getString("tbl_school_id").equals(SCHOOL_ID)) {
                            userrType = jsonObject.getString("tbl_users_type");

                            userID = jsonObject.getString("tbl_users_id");
                            userLoginId = jsonObject.getString("tbl_users_login_id");
                            userName = jsonObject.getString("tbl_users_name");
                            userPhone = jsonObject.getString("tbl_users_phone");
                            userEmailId = jsonObject.getString("tbl_users_email");
                            userDob = jsonObject.getString("tbl_users_dob");
                            userGender = jsonObject.getString("tbl_users_gender");
                            userImg = jsonObject.getString("tbl_users_img");
                            userPhoneStatus = jsonObject.getString("tbl_users_phone_status");

                            if (userrType.equals("Student")) {
                                //Toast.makeText(ctx, ""+userrType, Toast.LENGTH_SHORT).show();
                                str_class = jsonObject.getString("tbl_stnt_prsnl_data_class");
                                str_section = jsonObject.getString("tbl_stnt_prsnl_data_section");
                                str_roll_number = jsonObject.getString("tbl_stnt_prsnl_data_rollno");
                                str_teacher_name = jsonObject.getString("tbl_stnt_prsnl_data_cls_teach");
                            }
                            //	Toast.makeText(ctx, ""+userrType, Toast.LENGTH_SHORT).show();


                            //school details


                            userSchoolId = jsonObject.getString("tbl_school_id");
                            userSchoolRoleId = jsonObject.getString("tbl_role_id");
                            userSchoolSchoolLogo = jsonObject.getString("tbl_school_logo");
                            userSchoolSchoolLogo1 = jsonObject.getString("tbl_school_logo2");
                            userSchoolSchoolLogo3 = jsonObject.getString("tbl_school_logo3");


                            //color
                            selToolColor = getResources().getString(R.string.tool);
                            selDrawerColor = getResources().getString(R.string.drawer);
                            selStatusColor = getResources().getString(R.string.status);
                            selTextColor1 = getResources().getString(R.string.text1);
                            selTextColor2 = getResources().getString(R.string.text2);

                            userSchoolName = jsonObject.getString("tbl_school_name");
                            userSchoolRoleName = jsonObject.getString("tbl_role_name");
                            dbs.setUserSchoolName(userSchoolName);
                            dbs.setParentStatus(jsonObject.getString("tbl_users_parents_phone_status"));
                            dbs.setParentPhone(jsonObject.getString("tbl_users_parents_no"));
                            dbs.setParentPassword(jsonObject.getString("tbl_users_parents_password"));
                            dbs.setInstStatus(jsonObject.getString("tbl_school_status"));
                            dbs.setUniqueId(jsonObject.getString("sch_unique_id"));
                            dbs.setRestricted(jsonObject.getString("restricted_access"));
                            dbs.setUserSchoolRoleName(userSchoolRoleName);
                            dbs.setUserID(userID);
                            dbs.setUserrType(userrType);
                            dbs.setUserLoginId(userLoginId);
                            dbs.setUserName(userName);
                            dbs.setUserPhone(userPhone);
                            dbs.setUserEmailId(userEmailId);
                            dbs.setUserDob(userDob);
                            dbs.setUserGender(userGender);
                            dbs.setUserImg(userImg);
                            dbs.setUserPhoneStatus(userPhoneStatus);
                            dbs.setUserClass(str_class);
                            dbs.setUserSection(str_section);
                            dbs.setUserRollNumber(str_roll_number);
                            dbs.setUserTeacherName(str_teacher_name);

                            dbs.setUserSchoolId(userSchoolId);
                            dbs.setUserSchoolRoleId(userSchoolRoleId);
                            dbs.setUserSchoolSchoolLogo(userSchoolSchoolLogo);
                            dbs.setUserSchoolSchoolLogo1(userSchoolSchoolLogo1);
                            dbs.setUserSchoolSchoolLogo3(userSchoolSchoolLogo3);

                            //color
                            dbs.setSelToolColor(selToolColor);
                            dbs.setSelDrawerColor(selDrawerColor);
                            dbs.setSelStatusColor(selStatusColor);
                            dbs.setSelTextColor1(selTextColor1);
                            dbs.setSelTextColor2(selTextColor2);

                            studentArray.add(dbs);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                userDialogCheck(studentArray);
            }
            super.onPostExecute(result);
        }

    }

    private void userDialogCheck(ArrayList<DataBeanStudent> studentArray) {
        if (studentArray.size() > 1) {
            setNumberOfUsers(studentArray);
        } else if (studentArray.size() == 1) {
            studentValidLogin(userID);
        } else {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }
    }

    private void setNumberOfUsers(final ArrayList<DataBeanStudent> studentArray) {
        LayoutInflater factory = LayoutInflater.from(InstituteBuzzActivity.this);
//text_entry is an Layout XML file containing two text field to display in alert dialog
        @SuppressLint("InflateParams") final View textEntryView = factory.inflate(R.layout.user_select_dialog, null);

        user_list = textEntryView.findViewById(R.id.user_list);
        userListAdaptor = new UserListAdaptor(InstituteBuzzActivity.this, R.layout.user_select_dialog, studentArray, userId);
        user_list.setAdapter(userListAdaptor);
        user_list.setOnItemClickListener((parent, view, position, id) -> {
            String status;
            if (getSharedPreferences(PREF_ROLE, 0).getString("userRole", "").equals("Parent")) {
                status = studentArray.get(position).getParentStatus();
            } else {
                status = studentArray.get(position).getInstStatus();
            }
            if (status.equals("1")) {
                userID1 = studentArray.get(position).getUserID();
                userLoginId1 = studentArray.get(position).getUserLoginId();
                userName1 = studentArray.get(position).getUserName();
                userPhone1 = studentArray.get(position).getUserPhone();
                userEmailId1 = studentArray.get(position).getUserEmailId();
                userDob1 = studentArray.get(position).getUserDob();
                userGender1 = studentArray.get(position).getUserGender();
                userImg1 = studentArray.get(position).getUserImg();
                userPhoneStatus1 = studentArray.get(position).getUserPhoneStatus();
                userrType1 = studentArray.get(position).getUserrType();

                str_class1 = studentArray.get(position).getUserClass();
                str_section1 = studentArray.get(position).getUserSection();
                str_roll_number1 = studentArray.get(position).getUserRollNumber();
                str_teacher_name1 = studentArray.get(position).getUserTeacherName();


                selToolColor1 = studentArray.get(position).getSelToolColor();
                selDrawerColor1 = studentArray.get(position).getSelDrawerColor();
                selStatusColor1 = studentArray.get(position).getSelStatusColor();
                selTextColor11 = studentArray.get(position).getSelTextColor1();
                selTextColor22 = studentArray.get(position).getSelTextColor2();


                userSchoolId1 = studentArray.get(position).getUserSchoolId();
                userSchoolRoleId1 = studentArray.get(position).getUserSchoolRoleId();
                userSchoolSchoolLogo11 = studentArray.get(position).getUserSchoolSchoolLogo();
                userSchoolSchoolLogo12 = studentArray.get(position).getUserSchoolSchoolLogo1();
                userSchoolSchoolLogo13 = studentArray.get(position).getUserSchoolSchoolLogo3();

                userSchoolName1 = studentArray.get(position).getUserSchoolName();
                userSchoolRoleName1 = studentArray.get(position).getUserSchoolRoleName();
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);


                switch (userrType1) {
                    case "Student":
                        if (status.equalsIgnoreCase("1")) {
                            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.remove("refer_code");
                            editor.remove("refer_offer");
                            editor.putString("logged", "logged");
                            editor.putString("userID", userID1);
                            editor.putString("userLoginId", userLoginId1);
                            editor.putString("userName", userName1);
                            editor.putString("userPhone", userPhone1);
                            editor.putString("userEmailId", userEmailId1);
                            editor.putString("userDob", userDob1);
                            editor.putString("userGender", userGender1);
                            editor.putString("userImg", userImg1);
                            editor.putString("userPhoneStatus", userPhoneStatus1);
                            editor.putString("userrType", userrType1);
                            editor.putString("userClass", str_class1);
                            editor.putString("userSection", str_section1);
                            editor.putString("userRollNumber", str_roll_number1);
                            editor.putString("userTeacherName", str_teacher_name1);
                            editor.putString("userSchoolId", userSchoolId1);
                            editor.putString("userSchoolLogo", userSchoolSchoolLogo11);
                            editor.putString("userSchoolLogo1", userSchoolSchoolLogo12);
                            editor.putString("userSchoolLogo3", userSchoolSchoolLogo13);
                            editor.putString("numberOfUser", "multiple");
                            editor.putString("unique_id", studentArray.get(position).getUniqueId());
                            editor.putString("restricted", studentArray.get(position).getRestricted());

                            //
                            editor.putString("userSchoolName", userSchoolName1);
                            editor.putString("userSchoolRoleName", userSchoolRoleName1);
                            editor.putString("str_school_id", userSchoolId1);
                            editor.putString("str_role_id", userSchoolRoleId1);
                            editor.apply();

                            //color set
                            SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
                            SharedPreferences.Editor editorColor = settingsColor.edit();
                            editorColor.putString("tool", selToolColor1);
                            editorColor.putString("drawer", selDrawerColor1);
                            editorColor.putString("status", selStatusColor1);
                            editorColor.putString("text1", selTextColor11);
                            editorColor.putString("text2", selTextColor22);

                            editorColor.apply();

                            Intent i1 = new Intent(InstituteBuzzActivity.this, WelcomeActivity.class);
                            i1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i1);
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            finish();
                        } else {
                            Intent i = new Intent(InstituteBuzzActivity.this, MobileNumberActivity.class);
                            i.putExtra("userID", userID1);
                            i.putExtra("userPhone", userPhone1);
                            startActivity(i);
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        }
                        break;
                    case "Teacher":
                    case "Admin":
                    case "Staff":
                    case "Others":
                        differentValidLogin1(userID1, studentArray.get(position).getUniqueId(), studentArray.get(position).getRestricted());
                        break;
                }


                //	view.setSelected(true);
                for (int j = 0; j < parent.getChildCount(); j++)
                    parent.getChildAt(j).setBackgroundColor(Color.TRANSPARENT);
                // change the background color of the selected element
                view.setBackgroundColor(getResources().getColor(R.color.light_gray1));

            } else {
                startActivity(new Intent(InstituteBuzzActivity.this, InactiveInstitute.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        alert = new AlertDialog.Builder(InstituteBuzzActivity.this, R.style.DialogTheme);
        alert.setTitle("Select User").setView(textEntryView).setNegativeButton("Cancel", null);
        //alert.show();
        alertDialog = alert.create();
        alertDialog.show();
        Button theButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        Button theButton1 = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        theButton.setTextColor(getResources().getColor(R.color.text_gray));
        theButton1.setTextColor(getResources().getColor(R.color.text_gray));
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setEnabled(false);
    }

    private void studentValidLogin(String userID) {
        String status;
        if (getSharedPreferences(PREF_ROLE, 0).getString("userRole", "").equals("Parent")) {
            status = studentArray.get(0).getParentStatus();
        } else {
            status = studentArray.get(0).getInstStatus();
        }
        if (status.equals("1")) {
            switch (userrType) {
                case "Student": {
                    SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.remove("refer_code");
                    editor.remove("refer_offer");
                    editor.putString("logged", "logged");
                    editor.putString("userID", userID);
                    editor.putString("userLoginId", userLoginId);
                    editor.putString("userName", userName);
                    editor.putString("userPhone", userPhone);
                    editor.putString("userEmailId", userEmailId);
                    editor.putString("userDob", userDob);
                    editor.putString("userGender", userGender);
                    editor.putString("userImg", userImg);
                    editor.putString("userPhoneStatus", userPhoneStatus);
                    editor.putString("userrType", userrType);
                    editor.putString("userClass", str_class);
                    editor.putString("userSection", str_section);
                    editor.putString("userRollNumber", str_roll_number);
                    editor.putString("userTeacherName", str_teacher_name);
                    editor.putString("userSchoolId", userSchoolId);
                    editor.putString("userSchoolLogo", userSchoolSchoolLogo);
                    editor.putString("userSchoolLogo1", userSchoolSchoolLogo1);
                    editor.putString("userSchoolLogo3", userSchoolSchoolLogo3);
                    editor.putString("numberOfUser", "single");

                    //
                    editor.putString("userSchoolName", userSchoolName);
                    editor.putString("userSchoolRoleName", userSchoolRoleName);
                    editor.putString("str_school_id", userSchoolId);
                    editor.putString("unique_id", studentArray.get(0).getUniqueId());
                    editor.putString("restricted", studentArray.get(0).getRestricted());
                    editor.putString("str_role_id", userSchoolRoleId);
                    editor.apply();

                    //color set
                    SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
                    SharedPreferences.Editor editorColor = settingsColor.edit();
                    editorColor.putString("tool", selToolColor);
                    editorColor.putString("drawer", selDrawerColor);
                    editorColor.putString("status", selStatusColor);
                    editorColor.putString("text1", selTextColor1);
                    editorColor.putString("text2", selTextColor2);
                    editorColor.apply();


                    Intent i = new Intent(InstituteBuzzActivity.this, WelcomeActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                }
                break;
                case "Teacher":
                case "Admin":
                case "Staff":
                case "Others":
                    differentValidLogin(userID, studentArray.get(0).getUniqueId(), studentArray.get(0).getRestricted());
                    break;
            }
        } else {
            startActivity(new Intent(this, InactiveInstitute.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        }
    }

    private void differentValidLogin(String userID, String uniqueId, String restricted) {
        if (userPhoneStatus.equalsIgnoreCase("1")) {
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.remove("refer_code");
            editor.remove("refer_offer");
            editor.putString("logged", "logged");
            editor.putString("userID", userID);
            editor.putString("restricted", restricted);
            editor.putString("userLoginId", userLoginId);
            editor.putString("userName", userName);
            editor.putString("userPhone", userPhone);
            editor.putString("userEmailId", userEmailId);
            editor.putString("userDob", userDob);
            editor.putString("userGender", userGender);
            editor.putString("userImg", userImg);
            editor.putString("userPhoneStatus", userPhoneStatus);
            editor.putString("unique_id", uniqueId);
            editor.putString("userrType", userrType);
            editor.putString("userSchoolId", userSchoolId);
            editor.putString("userSchoolLogo", userSchoolSchoolLogo);
            editor.putString("userSchoolLogo1", userSchoolSchoolLogo1);
            editor.putString("userSchoolLogo3", userSchoolSchoolLogo3);
            editor.putString("numberOfUser", "single");

            //
            editor.putString("userSchoolName", userSchoolName);
            editor.putString("userSchoolRoleName", userSchoolRoleName);
            editor.putString("str_school_id", userSchoolId);
            editor.putString("str_role_id", userSchoolRoleId);
            editor.apply();

            //color set
            SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
            SharedPreferences.Editor editorColor = settingsColor.edit();
            editorColor.putString("tool", selToolColor);
            editorColor.putString("drawer", selDrawerColor);
            editorColor.putString("status", selStatusColor);
            editorColor.putString("text1", selTextColor1);
            editorColor.putString("text2", selTextColor2);
            editorColor.apply();

            Intent i = new Intent(InstituteBuzzActivity.this, WelcomeActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        } else {
            Intent i = new Intent(InstituteBuzzActivity.this, MobileNumberActivity.class);
            i.putExtra("userID", this.userID);
            i.putExtra("userPhone", userPhone);
            startActivity(i);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }

    private void differentValidLogin1(String userID1, String uniqueId, String restricted) {
        if (userPhoneStatus1.equalsIgnoreCase("1")) {
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.remove("refer_code");
            editor.remove("refer_offer");
            editor.putString("logged", "logged");
            editor.putString("restricted", restricted);
            editor.putString("userID", userID1);
            editor.putString("userLoginId", userLoginId1);
            editor.putString("userName", userName1);
            editor.putString("userPhone", userPhone1);
            editor.putString("userEmailId", userEmailId1);
            editor.putString("unique_id", uniqueId);
            editor.putString("userDob", userDob1);
            editor.putString("userGender", userGender1);
            editor.putString("userImg", userImg1);
            editor.putString("userPhoneStatus", userPhoneStatus1);
            editor.putString("userrType", userrType1);
            editor.putString("userSchoolId", userSchoolId1);
            editor.putString("userSchoolLogo", userSchoolSchoolLogo11);
            editor.putString("userSchoolLogo1", userSchoolSchoolLogo12);
            editor.putString("userSchoolLogo3", userSchoolSchoolLogo13);
            editor.putString("numberOfUser", "multiple");

            //
            editor.putString("userSchoolName", userSchoolName1);
            editor.putString("userSchoolRoleName", userSchoolRoleName1);
            editor.putString("str_school_id", userSchoolId1);
            editor.putString("str_role_id", userSchoolRoleId1);
            editor.apply();

            //color set
            SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
            SharedPreferences.Editor editorColor = settingsColor.edit();
            editorColor.putString("tool", selToolColor1);
            editorColor.putString("drawer", selDrawerColor1);
            editorColor.putString("status", selStatusColor1);
            editorColor.putString("text1", selTextColor11);
            editorColor.putString("text2", selTextColor22);
            editorColor.apply();

            Intent i = new Intent(InstituteBuzzActivity.this, WelcomeActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        } else {
            Intent i = new Intent(InstituteBuzzActivity.this, MobileNumberActivity.class);
            i.putExtra("userID", userID1);
            i.putExtra("userPhone", userPhone1);
            startActivity(i);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class GetPermission extends AsyncTask<String, String, String> {
        Context ctx;

        GetPermission(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String school_id = params[0];
            String roleId = params[1];
            String data;

            try {
                URL url = new URL(GET_PERMISSION);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                data = URLEncoder.encode("tbl_school_id", "UTF-8") + "=" + URLEncoder.encode(school_id, "UTF-8") + "&" +
                        URLEncoder.encode("role_id", "UTF-8") + "=" + URLEncoder.encode(roleId, "UTF-8");
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
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("result");
                SharedPreferences sp = ctx.getSharedPreferences(PREFS_RW, Context.MODE_PRIVATE);
                SharedPreferences.Editor se = sp.edit();
                se.putString("result", result);
                se.apply();
                ArrayList<PermissionData> array = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject object = jsonArray.getJSONObject(i);
                    PermissionData data = new PermissionData();
                    data.setName(object.getString("tbl_insti_buzz_cate_name"));
                    data.setRead(object.getString("tbl_scl_inst_buzz_detl_status"));
                    data.setWrite(object.getString("tbl_scl_inst_buzz_detl_write_status"));
                    array.add(data);
                }
                setPermission(array);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }

    private void setPermission(ArrayList<PermissionData> arrayList) {
        Menu menu = mNavigationView.getMenu();
        MenuItem exam = menu.findItem(R.id.online_exam);
        MenuItem fee = menu.findItem(R.id.pay_fee);
        MenuItem live = menu.findItem(R.id.live_stream);

        exam.setVisible(false);
        fee.setVisible(false);
        live.setVisible(false);

        for (int i = 0; i < arrayList.size(); i++) {
            PermissionData data = arrayList.get(i);
            if ("Online Exam".equals(data.getName())) {
                if (data.getWrite().equals("Active"))
                    exam.setVisible(true);
            } else if ("Fees".equals(data.getName())) {
                if (data.getRead().equals("Active"))
                    fee.setVisible(true);
            } else if (data.getName().equals("Video Live Streaming")) {
                if (data.getRead().equals("Active"))
                    live.setVisible(true);
            }
        }
    }

}
