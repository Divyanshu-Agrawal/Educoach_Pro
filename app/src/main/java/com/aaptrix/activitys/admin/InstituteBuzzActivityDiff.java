package com.aaptrix.activitys.admin;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.provider.Settings;

import com.aaptrix.BuildConfig;
import com.aaptrix.activitys.AddUserForm;
import com.aaptrix.activitys.AppInfo;
import com.aaptrix.activitys.AppLogin;
import com.aaptrix.activitys.CoursesActivity;
import com.aaptrix.activitys.FullScreenImageActivity;
import com.aaptrix.activitys.GetHelp;
import com.aaptrix.activitys.InactiveInstitute;
import com.aaptrix.activitys.ReferralActivity;
import com.aaptrix.activitys.UserProfile;
import com.aaptrix.activitys.UserSelfRegistration;
import com.aaptrix.activitys.WelcomeActivity;
import com.aaptrix.activitys.guest.GuestExam;
import com.aaptrix.activitys.student.AboutUsActivity;
import com.aaptrix.activitys.student.ActivitiesActivity;
import com.aaptrix.activitys.AllUsersChatActivity;
import com.aaptrix.activitys.student.FeedbackActivity;
import com.aaptrix.activitys.student.GalleryActivity;
import com.aaptrix.activitys.MobileNumberActivity;
import com.aaptrix.activitys.guest.GuestMaterial;
import com.aaptrix.activitys.guest.GuestVideo;
import com.aaptrix.activitys.student.HomeworkActivity;
import com.aaptrix.activitys.student.LiveStreaming;
import com.aaptrix.activitys.student.OnlineExam;
import com.aaptrix.activitys.student.PublicationActivity;
import com.aaptrix.activitys.student.StaffActivity;
import com.aaptrix.activitys.student.VideoLibrary;
import com.aaptrix.activitys.teacher.SchoolCalenderActivity;
import com.aaptrix.activitys.teacher.StaffAttendance;
import com.aaptrix.activitys.teacher.StaffLeaveList;
import com.aaptrix.activitys.teacher.StaffMyAttendance;
import com.aaptrix.activitys.student.StudyMaterial;
import com.aaptrix.activitys.teacher.TeacherAttendanceActivity;
import com.aaptrix.activitys.teacher.TeacherDairyActivity;

import androidx.annotation.NonNull;

import com.aaptrix.adaptor.CustomSliderView;
import com.aaptrix.databeans.PermissionData;
import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.navigation.NavigationView;

import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ShareCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.iid.FirebaseInstanceId;
import com.kobakei.ratethisapp.RateThisApp;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.conn.ssl.SSLConnectionSocketFactory;
import cz.msebera.android.httpclient.conn.ssl.SSLContexts;
import cz.msebera.android.httpclient.entity.mime.HttpMultipartMode;
import cz.msebera.android.httpclient.entity.mime.MultipartEntityBuilder;
import cz.msebera.android.httpclient.entity.mime.content.FileBody;
import cz.msebera.android.httpclient.impl.client.HttpClients;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import cz.msebera.android.httpclient.util.EntityUtils;
import de.hdodenhof.circleimageview.CircleImageView;

import com.aaptrix.databeans.DataBeanInstitueBuzz;
import com.aaptrix.databeans.DataBeanStudent;
import com.aaptrix.adaptor.InstitueBuzzAdaptor;
import com.aaptrix.adaptor.UserListAdaptor;
import com.aaptrix.R;
import com.aaptrix.tools.FileUtil;

import javax.net.ssl.SSLContext;

import id.zelory.compressor.Compressor;

import static com.aaptrix.activitys.SplashScreen.SCHOOL_ID;
import static com.aaptrix.activitys.SplashScreen.SCHOOL_NAME;
import static com.aaptrix.tools.HttpUrl.ABOUT_SCHOOL_INFO;
import static com.aaptrix.tools.HttpUrl.ALL_INSTITUTE_BUZZ_CATE;
import static com.aaptrix.tools.HttpUrl.GET_PERMISSION;
import static com.aaptrix.tools.HttpUrl.SWITCH_USERS;
import static com.aaptrix.tools.HttpUrl.UPDATE_USER_PRO_IMAGE;
import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREFS_RW;
import static com.aaptrix.tools.SPClass.PREFS_USER;
import static com.aaptrix.tools.SPClass.PREF_COLOR;
import static com.aaptrix.tools.SPClass.PREF_ROLE;
import static cz.msebera.android.httpclient.conn.ssl.SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER;

public class InstituteBuzzActivityDiff extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    ImageView iv_edit;
    CircleImageView header_img, prof_logo1;
    CircleImageView iv_user_img;
    TextView tv_name_id, tv_number, tv_today_day_date;
    SharedPreferences.Editor editor;
    String userId, roleId, schoolId, userName, userImg, userrType, userPhone;
    GridView institue_lv;
    DataBeanInstitueBuzz dbib;
    ArrayList<DataBeanInstitueBuzz> instiBuzzArray = new ArrayList<>();
    InstitueBuzzAdaptor institueBuzzAdaptor;
    String instBuzzId, instBuzzName, instBuzzimg;
    LinearLayout iv_edit_layout;
    ProgressBar loader;
    Bitmap bitmap;
    DrawerLayout mDrawerLayout;
    ActionBarDrawerToggle mToggle;
    NavigationView mNavigationView;
    TextView header_name, header_role;
    LinearLayout headerlayout, logo_layout;
    //offline
    AlertDialog.Builder alert;
    boolean doubleBackToExitPressedOnce = false;

    String selToolColor, selDrawerColor, selStatusColor, selTextColor1, selTextColor2, userJson, numberOfUser, userType, userSchoolSchoolLogo3;
    AppBarLayout appBarLayout;
    TextView tvID, tvCLSTEACH, tool_title;

    ArrayList<DataBeanStudent> studentArray = new ArrayList<>();
    DataBeanStudent dbs;
    ListView user_list;
    UserListAdaptor userListAdaptor;
    String userID1, userLoginId1, userName1, userPhone1, userEmailId1, userDob1, userGender1, userImg1, userPhoneStatus1, userrType1;
    String userID, userLoginId, userEmailId, userDob, userGender, userPhoneStatus;
    String userSchoolId, userSchoolName, userSchoolRoleId, userSchoolRoleName, userSchoolSchoolLogo, userSchoolSchoolLogo2;
    String userSchoolId1, userSchoolName1, userSchoolRoleId1, userSchoolRoleName1, userSchoolSchoolLogo11, userSchoolSchoolLogo12, userSchoolSchoolLogo13;
    AlertDialog alertDialog;

    String selToolColor1, selDrawerColor1, selStatusColor1, selTextColor11, selTextColor22;
    String str_class1, str_section1, str_roll_number1, str_teacher_name1;
    String str_class, str_section, str_roll_number, str_teacher_name;
    String user_token_id, android_id;
    View viewId;
    private FirebaseAuth mAuth;
    MediaPlayer mp;
    LinearLayout profileLayout;
    Button callUsNow;
    SliderLayout sliderLayout;
    CardView getMoreInfo, sliderCard;
    private String[] image;
    String instPhone;

    @SuppressLint({"SetTextI18n", "HardwareIds"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.institute_buzz_layout_diff);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("");
        setResult(RESULT_OK);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.cancelAll();

        appBarLayout = findViewById(R.id.appBarLayout);
        tool_title = findViewById(R.id.tool_title);
        mp = MediaPlayer.create(this, R.raw.button_click);

        SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
        selToolColor = settingsColor.getString("tool", "");
        selDrawerColor = settingsColor.getString("drawer", "");
        selStatusColor = settingsColor.getString("status", "");
        selTextColor1 = settingsColor.getString("text1", "");
        selTextColor2 = settingsColor.getString("text2", "");
        FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(this);
        mDrawerLayout = findViewById(R.id.drawer);
        viewId = findViewById(R.id.viewId);

        mToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.string.open, R.string.close);
        mDrawerLayout.addDrawerListener(mToggle);
        mToggle.syncState();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        mNavigationView = findViewById(R.id.navigation_drawer);
        mNavigationView.setNavigationItemSelectedListener(this);

        View v = mNavigationView.inflateHeaderView(R.layout.navi_header);
        header_img = v.findViewById(R.id.header_img);
        header_name = v.findViewById(R.id.header_name);
        header_role = v.findViewById(R.id.header_role);
        headerlayout = v.findViewById(R.id.headerlayout);
        getMoreInfo = v.findViewById(R.id.get_more_info);
        sliderCard = findViewById(R.id.slider_card);

        iv_user_img = findViewById(R.id.iv_user_img);
        prof_logo1 = findViewById(R.id.prof_logo1);
        logo_layout = findViewById(R.id.logo_layout);

        iv_edit = findViewById(R.id.iv_edit1);
        profileLayout = findViewById(R.id.profile_layout);
        sliderLayout = findViewById(R.id.slider);
        callUsNow = findViewById(R.id.call_us);

        tv_name_id = findViewById(R.id.tv_std_id);
        tvID = findViewById(R.id.tvID);
        tvCLSTEACH = findViewById(R.id.tvCLSTEACH);
        tv_number = findViewById(R.id.tv_std_cls_teacher);
        tv_today_day_date = findViewById(R.id.tv_today_day_date);
        loader = findViewById(R.id.loader);
        iv_edit_layout = findViewById(R.id.iv_edit_layout);

        institue_lv = findViewById(R.id.institue_lv);

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.getDefault());
        String formattedDate = df.format(c.getTime());
        SimpleDateFormat sdf = new SimpleDateFormat("EEEE", Locale.getDefault());
        Date d = new Date();
        String dayOfTheWeek = sdf.format(d);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        editor = settings.edit();
        userId = settings.getString("userID", "");
        userName = settings.getString("userName", "");
        userImg = settings.getString("userImg", "");
        userPhone = settings.getString("userPhone", "");
        userrType = settings.getString("userrType", "");
        roleId = settings.getString("str_role_id", "");
        schoolId = settings.getString("str_school_id", "");
        userSchoolId = settings.getString("str_school_id", "");
        numberOfUser = settings.getString("numberOfUser", "");
        userType = settings.getString("userrType", "");
        userSchoolSchoolLogo3 = settings.getString("userSchoolLogo1", "");
        userEmailId = settings.getString("userEmailId", "");

        if (!userrType.equals("Guest")) {
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
        }

        if (!userrType.equals("Guest"))
            headerlayout.setOnClickListener(view -> {
                startActivity(new Intent(this, UserProfile.class));
                mDrawerLayout.closeDrawer(GravityCompat.START);
            });

        GetPermission getPermission = new GetPermission(this);
        if (getSharedPreferences(PREF_ROLE, 0).getString("userRole", "").equals("Parent")) {
            getPermission.execute(schoolId, getSharedPreferences(PREF_ROLE, 0).getString("userRoleID", ""));
        } else {
            getPermission.execute(schoolId, roleId);
        }

        Menu menu = mNavigationView.getMenu();
        MenuItem enq = menu.findItem(R.id.enquiry);
        if (userrType.equals("Admin")) {
            enq.setTitle("Enquiries");
        }
        MenuItem login = menu.findItem(R.id.login);
        if (userrType.equals("Guest")) {
            login.setVisible(true);
            menu.findItem(R.id.institute_buzz).setVisible(false);
            headerlayout.setOnClickListener(v1 -> mDrawerLayout.closeDrawer(GravityCompat.START));
            getMoreInfo.setOnClickListener(v1 -> startActivity(new Intent(this, AddUserForm.class)));
        } else {
            login.setVisible(false);
        }

        SharedPreferences settingsUser = getSharedPreferences(PREFS_USER, 0);
        userJson = settingsUser.getString("user", "");

        user_token_id = FirebaseInstanceId.getInstance().getToken();
        editor.putString("token", user_token_id);
        editor.apply();
        editor = settings.edit();
        android_id = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        String firebase_userID = "educoach" + userId + "@educoach.co.in";
        String firebase_password = "educoach" + userId;
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(firebase_userID, firebase_password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String current_user_id = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                        DatabaseReference storeUserDefaultDataReference = FirebaseDatabase.getInstance().getReference().child("Users").child(current_user_id);
                        storeUserDefaultDataReference.child("user_token_id").setValue(user_token_id);
                    }
                });

        try {
            File directory = this.getFilesDir();
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File(directory, "instituteBuzz")));
            String json = in.readObject().toString();
            in.close();
            JSONObject jsonRootObject = new JSONObject(json);
            JSONArray jsonArray = jsonRootObject.getJSONArray("result");
            getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().
                    putString("anonymous_feedback", jsonRootObject.getString("feedback_anonymous_permission")).apply();
            instiBuzzArray.clear();

            ArrayList<String> name = new ArrayList<>();
            name.add("About Us");
            name.add("Institute Calendar");
            name.add("Activities");
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
            name.add("Guest Exam");

            for (int i = 0; i < jsonArray.length(); i++) {
                dbib = new DataBeanInstitueBuzz();
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                if (userrType.equals("Guest") && i == 2) {
                    DataBeanInstitueBuzz db = new DataBeanInstitueBuzz();
                    if (!json.contains("Guest Exam")) {
                        db.setInstBuzzName("Courses");
                        instiBuzzArray.add(db);
                    }
                    if (name.contains(jsonObject.getString("tbl_insti_buzz_cate_name"))) {
                        db = new DataBeanInstitueBuzz();
                        db.setInstBuzzName(jsonObject.getString("tbl_insti_buzz_cate_name"));
                        instiBuzzArray.add(db);
                    }
                } else if (name.contains(jsonObject.getString("tbl_insti_buzz_cate_name"))) {
                    instBuzzId = jsonObject.getString("tbl_insti_buzz_cate_id");
                    instBuzzName = jsonObject.getString("tbl_insti_buzz_cate_name");
                    instBuzzimg = jsonObject.getString("tbl_insti_buzz_cate_img");
                    dbib.setInstBuzzCateId(instBuzzId);
                    dbib.setInstBuzzName(instBuzzName);
                    dbib.setInstBuzzImg(instBuzzimg);
                    instiBuzzArray.add(dbib);
                }
            }
            if (userrType.equals("Guest")) {
                DataBeanInstitueBuzz db = new DataBeanInstitueBuzz();
                db.setInstBuzzName("Get More Info");
                instiBuzzArray.add(db);
                db = new DataBeanInstitueBuzz();
                db.setInstBuzzName("Login");
                instiBuzzArray.add(db);
            }
            listItms();
        } catch (Exception e) {
            e.printStackTrace();
            GetAllInstituteBuzz b1 = new GetAllInstituteBuzz(this);
            String id;
            if (user_token_id == null) {
                user_token_id = settings.getString("token", "");
            }
            if (settings.getString("userRole", "").equals("Parent")) {
                id = settings.getString("userRoleID", "");
            } else {
                id = roleId;
            }
            b1.execute(id, schoolId, user_token_id, android_id, userId);
        }


        if (numberOfUser.equals("multiple")) {
            prof_logo1.setVisibility(View.VISIBLE);
            if (userImg.equals("0")) {
                prof_logo1.setImageDrawable(getResources().getDrawable(R.drawable.user_place_hoder));
            } else if (!TextUtils.isEmpty(userImg)) {
                String url;
                switch (userrType) {
                    case "Parent":
                    case "Student":
                        url = settings.getString("imageUrl", "") + userSchoolId + "/users/students/profile/" + userImg;
                        Picasso.with(this).load(url).error(R.drawable.dummy).placeholder(R.drawable.dummy).into(prof_logo1);
                        break;
                    case "Admin":
                        url = settings.getString("imageUrl", "") + userSchoolId + "/users/admin/profile/" + userImg;
                        Picasso.with(this).load(url).error(R.drawable.dummy).placeholder(R.drawable.dummy).into(prof_logo1);
                        break;
                    case "Staff":
                        url = settings.getString("imageUrl", "") + userSchoolId + "/users/staff/profile/" + userImg;
                        Picasso.with(this).load(url).error(R.drawable.dummy).placeholder(R.drawable.dummy).into(prof_logo1);
                        break;
                    case "Teacher":
                        url = settings.getString("imageUrl", "") + userSchoolId + "/users/teachers/profile/" + userImg;
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

        logo_layout.setOnClickListener(view -> {
            if (isInternetOn()) {
                GetUserLoginCheck b1 = new GetUserLoginCheck(InstituteBuzzActivityDiff.this);
                b1.execute(userPhone, "");
            } else {
                Toast.makeText(InstituteBuzzActivityDiff.this, "No internet available", Toast.LENGTH_SHORT).show();
            }
        });

        tv_name_id.setText(userName);
        tv_number.setText(userPhone);
        tv_today_day_date.setText(dayOfTheWeek + ", " + formattedDate);
        header_name.setText(userName);
        if (userrType.equals("Parent")) {
            header_role.setText("Student");
        } else {
            header_role.setText(userrType);
        }

        if (userrType.equals("Guest")) {
            header_name.setText(R.string.app_name);
            header_role.setVisibility(View.GONE);
            getMoreInfo.setVisibility(View.VISIBLE);
            headerlayout.findViewById(R.id.seperater).setVisibility(View.GONE);
            headerlayout.findViewById(R.id.more).setVisibility(View.GONE);
            Picasso.with(this).load(R.drawable.small_logo).into(header_img);
            header_img.setCircleBackgroundColor(Color.WHITE);
            profileLayout.setVisibility(View.GONE);
            sliderCard.setVisibility(View.VISIBLE);
            setImages();
            callUsNow.setVisibility(View.VISIBLE);
            tv_today_day_date.setVisibility(View.GONE);
        } else {
            getMoreInfo.setVisibility(View.GONE);
            if (userImg.equals("0")) {
                iv_user_img.setImageDrawable(getResources().getDrawable(R.drawable.user_place_hoder));
                header_img.setImageDrawable(getResources().getDrawable(R.drawable.user_place_hoder));
            } else if (!TextUtils.isEmpty(userImg)) {
                String url;
                switch (userrType) {
                    case "Parent":
                        url = settings.getString("imageUrl", "") + userSchoolId + "/users/parents/profile/" + userImg;
                        Picasso.with(this).load(url).error(R.drawable.dummy).placeholder(R.drawable.dummy).into(iv_user_img);
                        Picasso.with(this).load(url).error(R.drawable.dummy).placeholder(R.drawable.dummy).into(header_img);
                        break;
                    case "Admin":
                        url = settings.getString("imageUrl", "") + userSchoolId + "/users/admin/profile/" + userImg;
                        Log.e("url", url);
                        Picasso.with(this).load(url).error(R.drawable.dummy).placeholder(R.drawable.dummy).into(iv_user_img);
                        Picasso.with(this).load(url).error(R.drawable.dummy).placeholder(R.drawable.dummy).into(header_img);
                        break;
                    case "Staff":
                        url = settings.getString("imageUrl", "") + userSchoolId + "/users/staff/profile/" + userImg;
                        Picasso.with(this).load(url).error(R.drawable.dummy).placeholder(R.drawable.dummy).into(iv_user_img);
                        Picasso.with(this).load(url).error(R.drawable.dummy).placeholder(R.drawable.dummy).into(header_img);
                        break;
                    case "Teacher":
                        url = settings.getString("imageUrl", "") + userSchoolId + "/users/teachers/profile/" + userImg;
                        Picasso.with(this).load(url).error(R.drawable.dummy).placeholder(R.drawable.dummy).into(iv_user_img);
                        Picasso.with(this).load(url).error(R.drawable.dummy).placeholder(R.drawable.dummy).into(header_img);
                        break;
                    case "Student":
                        url = settings.getString("imageUrl", "") + userSchoolId + "/users/students/profile/" + userImg;
                        Picasso.with(this).load(url).error(R.drawable.dummy).placeholder(R.drawable.dummy).into(iv_user_img);
                        Picasso.with(this).load(url).error(R.drawable.dummy).placeholder(R.drawable.dummy).into(header_img);
                        break;
                }
            } else {
                iv_user_img.setImageDrawable(getResources().getDrawable(R.drawable.user_place_hoder));
                header_img.setImageDrawable(getResources().getDrawable(R.drawable.user_place_hoder));
            }
        }

        iv_edit.setOnClickListener(view -> {
            if (isInternetOn()) {
                if (ContextCompat.checkSelfPermission(view.getContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(InstituteBuzzActivityDiff.this, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
                }
                Intent gallery = new Intent();
                gallery.setAction(Intent.ACTION_GET_CONTENT);
                gallery.setType("image/*");
                startActivityForResult(gallery, 1);
            } else {
                Toast.makeText(InstituteBuzzActivityDiff.this, "No network Please connect with network for update", Toast.LENGTH_SHORT).show();
            }
        });


        appBarLayout.setBackgroundColor(Color.parseColor(selToolColor));
        headerlayout.setBackgroundColor(Color.parseColor(selDrawerColor));
        tv_today_day_date.setBackgroundColor(Color.parseColor(selToolColor));
        tvID.setTextColor(Color.parseColor(selToolColor));
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

    private void setImages() {
        new Thread(() -> {
            try {
                SSLContext sslContext = SSLContexts.custom().useTLS().build();
                SSLConnectionSocketFactory f = new SSLConnectionSocketFactory(
                        sslContext,
                        new String[]{"TLSv1.1", "TLSv1.2"},
                        null,
                        BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
                HttpClient httpclient = HttpClients.custom().setSSLSocketFactory(f).build();
                HttpPost httppost = new HttpPost(ABOUT_SCHOOL_INFO);
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<>(2);
                nameValuePairs.add(new BasicNameValuePair("schoolId", schoolId));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity httpEntity = response.getEntity();
                final String result = EntityUtils.toString(httpEntity);

                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(() -> {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        JSONArray jsonArray = jsonObject.getJSONArray("result");
                        JSONObject object = jsonArray.getJSONObject(0);
                        instPhone = object.getString("tbl_abt_schl_details_contact");
                        slider(object.getString("tbl_school_slider_imgs"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void slider(String images) {

        callUsNow.setOnClickListener(v -> {
            startActivity(new Intent(this, UserSelfRegistration.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        SharedPreferences sp = getSharedPreferences(PREFS_NAME, 0);
        HashMap<String, String> hash_file_maps = new HashMap<>();
        image = images.split(",");
        for (int i = 0; i < image.length; i++) {
            hash_file_maps.put(1 + i + "" + "/" + image.length, sp.getString("imageUrl", "") + sp.getString("userSchoolId", "") + "/other/" + image[i].
                    replace("\"", "").
                    replace("[", "").
                    replace("]", ""));
        }

        for (String name : hash_file_maps.keySet()) {
            DefaultSliderView textSliderView = new DefaultSliderView(this);
            textSliderView
                    .description(name)
                    .image(hash_file_maps.get(name))
                    .setScaleType(CustomSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(slider -> {
                        String aa = (String) slider.getBundle().get("extra");
                        assert aa != null;
                        String[] bb = aa.split("/");
                        String cc = bb[0];
                        int val = Integer.parseInt(cc);
                        Intent i = new Intent(this, FullScreenImageActivity.class);
                        i.putExtra("leaveImg", sp.getString("imageUrl", "") + sp.getString("userSchoolId", "") + "/other/" + image[val - 1].
                                replace("\"", "").
                                replace("[", "").
                                replace("]", ""));
                        startActivity(i);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    });
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle().putString("extra", name);
            sliderLayout.addSlider(textSliderView);
        }
        sliderLayout.setPresetTransformer(SliderLayout.Transformer.Default);
        sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        sliderLayout.setIndicatorVisibility(PagerIndicator.IndicatorVisibility.Invisible);

        sliderLayout.setCustomAnimation(new DescriptionAnimation());
        sliderLayout.setPresetTransformer(4);
        sliderLayout.startAutoCycle(1000, 5000, true);
        sliderLayout.addOnPageChangeListener(new ViewPagerEx.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Uri filePath;
        if (requestCode == 1 && resultCode == RESULT_OK) {
            filePath = data.getData();
            assert filePath != null;
            CropImage.activity(filePath)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(150, 150)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setCropShape(CropImageView.CropShape.OVAL)
                    .start(this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                filePath = result.getUri();
                try {
                    File actualImage = FileUtil.from(InstituteBuzzActivityDiff.this, filePath);
                    File compressedImage = new Compressor(InstituteBuzzActivityDiff.this)
                            .setMaxWidth(640)
                            .setMaxHeight(480)
                            .setQuality(75)
                            .setCompressFormat(Bitmap.CompressFormat.WEBP)
                            .compressToFile(actualImage);
                    bitmap = MediaStore.Images.Media.getBitmap(InstituteBuzzActivityDiff.this.getContentResolver(), Uri.fromFile(compressedImage));
                    iv_user_img.setImageBitmap(bitmap);
                    prof_logo1.setImageBitmap(bitmap);
                    header_img.setImageBitmap(bitmap);

                    UpdateProfileImage updateProfileImage = new UpdateProfileImage(InstituteBuzzActivityDiff.this, compressedImage);
                    updateProfileImage.execute(userId);

                } catch (IOException e) {
                    e.printStackTrace();
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
                Toast.makeText(InstituteBuzzActivityDiff.this, "" + error, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class UpdateProfileImage extends AsyncTask<String, String, String> {
        Context ctx;
        File image;

        UpdateProfileImage(Context ctx, File image) {
            this.ctx = ctx;
            this.image = image;
        }

        @Override
        protected void onPreExecute() {
            Toast.makeText(ctx, "Please wait we are updating your profile", Toast.LENGTH_SHORT).show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String userId = params[0];

            try {

                SSLContext sslContext = SSLContexts.custom().useTLS().build();
                SSLConnectionSocketFactory f = new SSLConnectionSocketFactory(
                        sslContext,
                        new String[]{"TLSv1.1", "TLSv1.2"},
                        null,
                        BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
                HttpClient httpclient = HttpClients.custom().setSSLSocketFactory(f).build();
                HttpPost httppost = new HttpPost(UPDATE_USER_PRO_IMAGE);
                MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
                entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                FileBody newImage = new FileBody(image);
                entityBuilder.addPart("image", newImage);
                entityBuilder.addTextBody("userId", userId);
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
            try {
                JSONObject jsonObject = new JSONObject(result);
                if (jsonObject.getString("success").equals("true")) {
                    editor.putString("userImg", jsonObject.getString("imageNm"));
                    editor.commit();
                    String firebase_userID = "educoach" + userId + "@educoach.co.in";
                    String firebase_password = "educoach" + userId;
                    FirebaseAuth mAuth = FirebaseAuth.getInstance();
                    mAuth.signInWithEmailAndPassword(firebase_userID, firebase_password)
                            .addOnCompleteListener(task -> {
                                String current_user_id = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                                DatabaseReference storeUserDefaultDataReference = FirebaseDatabase.getInstance().getReference().child("Users").child(current_user_id);
                                try {
                                    storeUserDefaultDataReference.child("userImg").setValue(jsonObject.getString("imageNm"));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            });
                    Toast.makeText(InstituteBuzzActivityDiff.this, "Your Image is Updated", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(InstituteBuzzActivityDiff.this, "Not uploaded image is too large", Toast.LENGTH_SHORT).show();
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            super.onPostExecute(result);
        }

    }

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
            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert notificationManager != null;
            notificationManager.cancelAll();
            Intent i = new Intent(InstituteBuzzActivityDiff.this, InstituteBuzzActivityDiff.class);
            startActivity(i);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        } else if (id == R.id.message) {
            signintoFirebase();
        } else if (id == R.id.attendance) {
            startActivity(new Intent(InstituteBuzzActivityDiff.this, StaffAttendance.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        } else if (id == R.id.contacts) {
            startActivity(new Intent(InstituteBuzzActivityDiff.this, AdminContacts.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        } else if (id == R.id.my_attendance) {
            startActivity(new Intent(InstituteBuzzActivityDiff.this, StaffMyAttendance.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        } else if (id == R.id.staff_leave) {
            if (userrType.equals("Admin")) {
                startActivity(new Intent(InstituteBuzzActivityDiff.this, AdminStaffLeaveIntermediate.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            } else {
                if (isInternetOn()) {
                    Intent i = new Intent(this, StaffLeaveList.class);
                    startActivity(i);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                } else {
                    Toast.makeText(InstituteBuzzActivityDiff.this, "No network Please connect with network for update", Toast.LENGTH_SHORT).show();
                }
            }
        } else if (id == R.id.see_performance) {
            Intent i = new Intent(InstituteBuzzActivityDiff.this, IntermidiateScreenActivity.class);
            i.putExtra("str_tool_title", "Student Performance");
            startActivity(i);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        } else if (id == R.id.student_attendance) {
            Intent i = new Intent(InstituteBuzzActivityDiff.this, IntermidiateScreenActivity.class);
            i.putExtra("str_tool_title", "Student Attendance");
            startActivity(i);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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
        } else if (id == R.id.add_exam) {
            Intent i = new Intent(this, IntermidiateScreenActivity.class);
            i.putExtra("str_tool_title", "Add Exam");
            startActivity(i);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        } else if (id == R.id.enquiry) {
            if (userType.equals("Admin")) {
                startActivity(new Intent(this, Enquiries.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            } else {
                startActivity(new Intent(this, AddUserForm.class));
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        } else if (id == R.id.login) {
            if (isInternetOn()) {
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                Intent i = new Intent(this, AppLogin.class);
                i.putExtra("status", "Online");
                startActivity(i);
            } else {
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                Intent i = new Intent(this, AppLogin.class);
                i.putExtra("status", "Offline");
                startActivity(i);
                Toast.makeText(this, "Please Connect Internet for better experience", Toast.LENGTH_SHORT).show();
            }
        } else if (id == R.id.help) {
            startActivity(new Intent(this, GetHelp.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        } else if (id == R.id.live_stream) {
            startActivity(new Intent(this, LiveStreaming.class));
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        } else if (id == R.id.online_exam) {
            Intent i = new Intent(this, OnlineExam.class);
            startActivity(i);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
        mDrawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    private void signintoFirebase() {
        String firebase_userID = "educoach" + userId + "@educoach.co.in";
        String firebase_password = "educoach" + userId;
        loader.setVisibility(View.VISIBLE);
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(firebase_userID, firebase_password)
                .addOnCompleteListener(task -> {
                    loader.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        Intent i = new Intent(InstituteBuzzActivityDiff.this, AllUsersChatActivity.class);
                        startActivity(i);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    } else {
                        createAccountWithFirebase();
                    }
                });
    }

    private void createAccountWithFirebase() {
        final String user_token_id = FirebaseInstanceId.getInstance().getToken();
        String firebase_userID = "educoach" + userId + "@educoach.co.in";
        String firebase_password = "educoach" + userId;
        final ProgressDialog loadingBar = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);
        loadingBar.setTitle("Creating New Account");
        loadingBar.setMessage("Please Wait,while we are creating account for you.");
        loadingBar.show();
        mAuth.createUserWithEmailAndPassword(firebase_userID, firebase_password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String current_user_id = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                        DatabaseReference storeUserDefaultDataReference = FirebaseDatabase.getInstance().getReference().child("Users").child(current_user_id);
                        storeUserDefaultDataReference.child("userID").setValue(userID);
                        storeUserDefaultDataReference.child("userrType").setValue(userrType);
                        storeUserDefaultDataReference.child("userName").setValue(userName);
                        storeUserDefaultDataReference.child("userImg").setValue(userImg);
                        storeUserDefaultDataReference.child("online").setValue(ServerValue.TIMESTAMP);
                        storeUserDefaultDataReference.child("user_token_id").setValue(user_token_id);
                        storeUserDefaultDataReference.child("userSchoolId").setValue(schoolId);
                        storeUserDefaultDataReference.child("user_thumb_image").setValue("default image")
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        signintoFirebase();
                                    }
                                });
                    }
                    loadingBar.dismiss();
                });
    }


    @SuppressLint("StaticFieldLeak")
    public class GetAllInstituteBuzz extends AsyncTask<String, String, String> {
        Context ctx;

        GetAllInstituteBuzz(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            //loader.setVisibility(View.VISIBLE);
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
            String android_version = String.valueOf(BuildConfig.VERSION_CODE);
            String data;

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
            loader.setVisibility(View.GONE);
            if (!result.equals("{\"result\":null}")) {
                try {
                    JSONObject jsonRootObject = new JSONObject(result);
                    getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit().
                            putString("anonymous_feedback", jsonRootObject.getString("feedback_anonymous_permission")).apply();
                    JSONArray jsonArray = jsonRootObject.getJSONArray("result");
                    instiBuzzArray.clear();
                    String url = jsonRootObject.getString("url");
                    cacheJson(jsonRootObject);

                    ArrayList<String> name = new ArrayList<>();
                    name.add("About Us");
                    name.add("Institute Calendar");
                    name.add("Activities");
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
                    name.add("Guest Exam");

                    for (int i = 0; i < jsonArray.length(); i++) {
                        dbib = new DataBeanInstitueBuzz();
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        if (userrType.equals("Guest") && i == 2) {
                            DataBeanInstitueBuzz db = new DataBeanInstitueBuzz();
                            if (!result.contains("Guest Exam")) {
                                db.setInstBuzzName("Courses");
                                instiBuzzArray.add(db);
                            }
                            if (name.contains(jsonObject.getString("tbl_insti_buzz_cate_name"))) {
                                db = new DataBeanInstitueBuzz();
                                db.setInstBuzzName(jsonObject.getString("tbl_insti_buzz_cate_name"));
                                instiBuzzArray.add(db);
                            }
                        } else if (name.contains(jsonObject.getString("tbl_insti_buzz_cate_name"))) {
                            instBuzzId = jsonObject.getString("tbl_insti_buzz_cate_id");
                            instBuzzName = jsonObject.getString("tbl_insti_buzz_cate_name");
                            instBuzzimg = url + jsonObject.getString("tbl_insti_buzz_cate_img");
                            dbib.setInstBuzzCateId(instBuzzId);
                            dbib.setInstBuzzName(instBuzzName);
                            dbib.setInstBuzzImg(instBuzzimg);
                            instiBuzzArray.add(dbib);
                        }
                    }
                    if (userrType.equals("Guest")) {
                        DataBeanInstitueBuzz db = new DataBeanInstitueBuzz();
                        db.setInstBuzzName("Get More Info");
                        instiBuzzArray.add(db);
                        db = new DataBeanInstitueBuzz();
                        db.setInstBuzzName("Login");
                        instiBuzzArray.add(db);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                listItms();
            } else {
                Toast.makeText(ctx, "No Institute Buzz", Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(result);
        }

    }

    private void cacheJson(final JSONObject jsonObject) {
        new Thread(() -> {
            ObjectOutput out;
            String data = jsonObject.toString();
            try {
                File directory = this.getFilesDir();
                directory.mkdir();
                out = new ObjectOutputStream(new FileOutputStream(new File(directory, "instituteBuzz")));
                out.writeObject(data);
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        mp.release();
        super.onDestroy();
    }

    private void listItms() {
        institueBuzzAdaptor = new InstitueBuzzAdaptor(InstituteBuzzActivityDiff.this, R.layout.insti_buzz_list_item, instiBuzzArray);
        institue_lv.setAdapter(institueBuzzAdaptor);

        institue_lv.setOnItemClickListener((parent, view, position, id) -> {
            String hobbiesName = instiBuzzArray.get(position).getInstBuzzName();
            if (isInternetOn()) {
                switch (hobbiesName) {
                    case "About Us": {
                        mp.start();
                        Intent i = new Intent(InstituteBuzzActivityDiff.this, AboutUsActivity.class);
                        startActivity(i);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        break;
                    }
                    case "Institute Calendar": {
                        mp.start();
                        Intent i = new Intent(InstituteBuzzActivityDiff.this, SchoolCalenderActivity.class);
                        startActivity(i);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        break;
                    }
                    case "Activities": {
                        mp.start();
                        if (userType.equals("Teacher") || userType.equals("Admin")) {
                            Intent i = new Intent(InstituteBuzzActivityDiff.this, ActivitiesActivity.class);
                            startActivity(i);
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        } else {
                            Toast.makeText(InstituteBuzzActivityDiff.this, "You don't have permission for " + hobbiesName, Toast.LENGTH_SHORT).show();
                        }
                        break;
                    }
                    case "What's New!": {
                        mp.start();
                        Intent i = new Intent(InstituteBuzzActivityDiff.this, PublicationActivity.class);
                        startActivity(i);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        break;
                    }
                    case "Attendance":
                        mp.start();
                        if (userType.equals("Teacher") || userType.equals("Admin")) {
                            Intent i = new Intent(InstituteBuzzActivityDiff.this, TeacherAttendanceActivity.class);
                            startActivity(i);
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        } else {
                            Toast.makeText(InstituteBuzzActivityDiff.this, "" + hobbiesName, Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "Remarks":
                        mp.start();
                        if (userType.equals("Teacher") || userType.equals("Admin")) {
                            Intent i = new Intent(InstituteBuzzActivityDiff.this, TeacherDairyActivity.class);
                            startActivity(i);
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        } else {
                            Toast.makeText(InstituteBuzzActivityDiff.this, "" + hobbiesName, Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "Assignments":
                        mp.start();
                        if (userType.equals("Teacher") || userType.equals("Admin")) {
                            Intent i = new Intent(InstituteBuzzActivityDiff.this, HomeworkActivity.class);
                            startActivity(i);
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        } else {
                            Toast.makeText(InstituteBuzzActivityDiff.this, "" + hobbiesName, Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "Results":
                        mp.start();
                        if (userType.equals("Teacher") || userType.equals("Admin")) {
                            Intent i = new Intent(InstituteBuzzActivityDiff.this, ResultIntermidiateScreenActivity.class);
                            i.putExtra("str_tool_title", "Results");
                            startActivity(i);
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        } else {
                            Toast.makeText(InstituteBuzzActivityDiff.this, "" + hobbiesName, Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "Time Table":
                        mp.start();
                        if (userType.equals("Teacher") || userType.equals("Admin")) {
                            Intent i = new Intent(InstituteBuzzActivityDiff.this, IntermidiateScreenActivity.class);
                            i.putExtra("str_tool_title", "Time Table");
                            startActivity(i);
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        } else {
                            Toast.makeText(InstituteBuzzActivityDiff.this, "You don't have permission for " + hobbiesName, Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case "Study Materials": {
                        mp.start();
                        if (userrType.equals("Guest")) {
                            Intent i = new Intent(InstituteBuzzActivityDiff.this, GuestMaterial.class);
                            startActivity(i);
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        } else {
                            Intent i = new Intent(InstituteBuzzActivityDiff.this, StudyMaterial.class);
                            startActivity(i);
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        }
                    }
                    break;
                    case "Study Videos": {
                        mp.start();
                        if (userrType.equals("Guest")) {
                            Intent i = new Intent(InstituteBuzzActivityDiff.this, GuestVideo.class);
                            startActivity(i);
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        } else {
                            Intent i = new Intent(InstituteBuzzActivityDiff.this, VideoLibrary.class);
                            startActivity(i);
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        }
                    }
                    break;
                    case "Gallery": {
                        mp.start();
                        Intent intent = new Intent(InstituteBuzzActivityDiff.this, GalleryActivity.class);
                        startActivity(intent);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                    break;
                    case "Login": {
                        mp.start();
                        if (isInternetOn()) {
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            Intent i = new Intent(this, AppLogin.class);
                            i.putExtra("status", "Online");
                            startActivity(i);
                        } else {
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            Intent i = new Intent(this, AppLogin.class);
                            i.putExtra("status", "Offline");
                            startActivity(i);
                            Toast.makeText(this, "Please Connect Internet for better experience", Toast.LENGTH_SHORT).show();
                        }
                    }
                    break;
                    case "Courses": {
                        mp.start();
                        startActivity(new Intent(this, CoursesActivity.class));
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                    break;
                    case "Get More Info": {
                        mp.start();
                        startActivity(new Intent(this, AddUserForm.class));
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    }
                    break;
                    case "Teaching Staff": {
                        mp.start();
                        Intent i = new Intent(this, StaffActivity.class);
                        startActivity(i);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        break;
                    }
                    case "Feedback": {
                        mp.start();
                        Intent i = new Intent(this, FeedbackActivity.class);
                        startActivity(i);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        break;
                    }
                    case "Refer a Friend": {
                        mp.start();
                        Intent i = new Intent(this, ReferralActivity.class);
                        startActivity(i);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        break;
                    }
                    case "Guest Exam": {
                        mp.start();
                        Intent i = new Intent(this, GuestExam.class);
                        startActivity(i);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        break;
                    }
                }
            } else {
                Toast.makeText(InstituteBuzzActivityDiff.this, "No internet available", Toast.LENGTH_SHORT).show();
                switch (hobbiesName) {
                    case "About Us": {
                        mp.start();
                        Intent i = new Intent(InstituteBuzzActivityDiff.this, AboutUsActivity.class);
                        startActivity(i);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        break;
                    }
                    case "Institute Calendar": {
                        mp.start();
                        Intent i = new Intent(InstituteBuzzActivityDiff.this, SchoolCalenderActivity.class);
                        startActivity(i);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        break;
                    }
                    case "Activities": {
                        mp.start();
                        Intent i = new Intent(InstituteBuzzActivityDiff.this, ActivitiesActivity.class);
                        startActivity(i);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        break;
                    }
                    case "What's New!": {
                        mp.start();
                        Intent i = new Intent(InstituteBuzzActivityDiff.this, PublicationActivity.class);
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

    @SuppressWarnings("deprecation")
    public final boolean isInternetOn() {
        ConnectivityManager connec;
        connec = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        assert connec != null;
        return connec.getActiveNetworkInfo() != null && connec.getActiveNetworkInfo().isAvailable() && connec.getActiveNetworkInfo().isConnectedOrConnecting();
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
                                str_class = jsonObject.getString("tbl_stnt_prsnl_data_class");
                                str_section = jsonObject.getString("tbl_stnt_prsnl_data_section");
                                str_roll_number = jsonObject.getString("tbl_stnt_prsnl_data_rollno");
                                str_teacher_name = jsonObject.getString("tbl_stnt_prsnl_data_cls_teach");
                            }
                            userSchoolId = jsonObject.getString("tbl_school_id");
                            userSchoolRoleId = jsonObject.getString("tbl_role_id");
                            userSchoolSchoolLogo = jsonObject.getString("tbl_school_logo");
                            userSchoolSchoolLogo2 = jsonObject.getString("tbl_school_logo2");
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
                            dbs.setUserSchoolSchoolLogo1(userSchoolSchoolLogo2);
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
        LayoutInflater factory = LayoutInflater.from(InstituteBuzzActivityDiff.this);
        @SuppressLint("InflateParams") final View textEntryView = factory.inflate(R.layout.user_select_dialog, null);

        user_list = textEntryView.findViewById(R.id.user_list);
        userListAdaptor = new UserListAdaptor(InstituteBuzzActivityDiff.this, R.layout.user_select_dialog, studentArray, userId);
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
                alertDialog.dismiss();
                switch (userrType1) {
                    case "Student":
                        if (status.equalsIgnoreCase("1")) {
                            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                            SharedPreferences.Editor editor = settings.edit();
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
                            editor.putString("unique_id", studentArray.get(position).getUniqueId());

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

                            Intent i1 = new Intent(InstituteBuzzActivityDiff.this, WelcomeActivity.class);
                            i1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i1);
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            finish();
                        } else {
                            Intent i = new Intent(InstituteBuzzActivityDiff.this, MobileNumberActivity.class);
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
                        differentValidLogin1(userID1, studentArray.get(position).getUniqueId());
                        break;
                }

                //	view.setSelected(true);
                for (int j = 0; j < parent.getChildCount(); j++)
                    parent.getChildAt(j).setBackgroundColor(Color.TRANSPARENT);
                // change the background color of the selected element
                view.setBackgroundColor(getResources().getColor(R.color.light_gray1));

            } else {
                startActivity(new Intent(InstituteBuzzActivityDiff.this, InactiveInstitute.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        alert = new AlertDialog.Builder(InstituteBuzzActivityDiff.this, R.style.DialogTheme);
        alert.setTitle("Select User").setView(textEntryView).setNegativeButton("Cancel",
                (dialog, whichButton) -> {

                });
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
                case "Student":
                    if (status.equalsIgnoreCase("1")) {

                        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                        SharedPreferences.Editor editor = settings.edit();
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
                        editor.putString("userSchoolLogo1", userSchoolSchoolLogo2);
                        editor.putString("numberOfUser", "single");

                        //
                        editor.putString("userSchoolName", userSchoolName);
                        editor.putString("userSchoolRoleName", userSchoolRoleName);
                        editor.putString("str_school_id", userSchoolId);
                        editor.putString("str_role_id", userSchoolRoleId);
                        editor.putString("unique_id", studentArray.get(0).getUniqueId());
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


                        Intent i = new Intent(InstituteBuzzActivityDiff.this, WelcomeActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish();
                    } else {
                        Intent i = new Intent(InstituteBuzzActivityDiff.this, MobileNumberActivity.class);
                        i.putExtra("userID", this.userID);
                        i.putExtra("userPhone", userPhone);
                        startActivity(i);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        //Toast.makeText(InstituteBuzzActivityDiff.this, "Otp verification needed", Toast.LENGTH_SHORT).show();

                    }
                    break;
                case "Teacher":
                case "Admin":
                case "Staff":
                case "Others":
                    differentValidLogin(userID, studentArray.get(0).getUniqueId());
                    break;
            }
        } else {
            startActivity(new Intent(this, InactiveInstitute.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        }
    }

    private void differentValidLogin(String userID, String uniqueId) {
        if (userPhoneStatus.equalsIgnoreCase("1")) {
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("logged", "logged");
            editor.putString("userID", userID);
            editor.putString("userLoginId", userLoginId);
            editor.putString("userName", userName);
            editor.putString("userPhone", userPhone);
            editor.putString("unique_id", uniqueId);
            editor.putString("userEmailId", userEmailId);
            editor.putString("userDob", userDob);
            editor.putString("userGender", userGender);
            editor.putString("userImg", userImg);
            editor.putString("userPhoneStatus", userPhoneStatus);
            editor.putString("userrType", userrType);
            editor.putString("userSchoolId", userSchoolId);
            editor.putString("userSchoolLogo", userSchoolSchoolLogo);
            editor.putString("userSchoolLogo1", userSchoolSchoolLogo2);
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

            Intent i = new Intent(InstituteBuzzActivityDiff.this, WelcomeActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        } else {
            Intent i = new Intent(InstituteBuzzActivityDiff.this, MobileNumberActivity.class);
            i.putExtra("userID", this.userID);
            i.putExtra("userPhone", userPhone);
            startActivity(i);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }

    private void differentValidLogin1(String userID1, String uniqueId) {
        if (userPhoneStatus1.equalsIgnoreCase("1")) {
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
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
            editor.putString("unique_id", uniqueId);
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

            Intent i = new Intent(InstituteBuzzActivityDiff.this, WelcomeActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        } else {
            Intent i = new Intent(InstituteBuzzActivityDiff.this, MobileNumberActivity.class);
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
            if (result != null) {
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
            super.onPostExecute(result);
        }

    }

    private void setPermission(ArrayList<PermissionData> arrayList) {
        Menu menu = mNavigationView.getMenu();
        MenuItem attendance = menu.findItem(R.id.attendance);
        MenuItem stuAtten = menu.findItem(R.id.student_attendance);
        MenuItem contact = menu.findItem(R.id.contacts);
        MenuItem myAttendance = menu.findItem(R.id.my_attendance);
        MenuItem staffLeave = menu.findItem(R.id.staff_leave);
        MenuItem stuPerformance = menu.findItem(R.id.see_performance);
        MenuItem addexam = menu.findItem(R.id.add_exam);
        MenuItem enq = menu.findItem(R.id.enquiry);
        MenuItem chat = menu.findItem(R.id.message);
        MenuItem live = menu.findItem(R.id.live_stream);
        MenuItem exam = menu.findItem(R.id.online_exam);

        attendance.setVisible(false);
        stuAtten.setVisible(false);
        contact.setVisible(false);
        myAttendance.setVisible(false);
        staffLeave.setVisible(false);
        stuPerformance.setVisible(false);
        addexam.setVisible(false);
        enq.setVisible(false);
        chat.setVisible(false);
        live.setVisible(false);
        exam.setVisible(false);

        for (int i = 0; i < arrayList.size(); i++) {
            PermissionData data = arrayList.get(i);
            switch (data.getName()) {
                case "Add Exam":
                    if (data.getRead().equals("Active"))
                        addexam.setVisible(true);
                    break;
                case "Contacts Book":
                    if (data.getRead().equals("Active"))
                        contact.setVisible(true);
                    break;
                case "Leave Requests":
                    if (data.getRead().equals("Active"))
                        staffLeave.setVisible(true);
                    break;
                case "Staff Chat":
                    if (data.getRead().equals("Active"))
                        chat.setVisible(true);
                    break;
                case "Attendances":
                    if (data.getRead().equals("Active"))
                        stuAtten.setVisible(true);
                    break;
                case "Student Performance":
                    if (data.getRead().equals("Active"))
                        stuPerformance.setVisible(true);
                    break;
                case "Staff Attendance":
                    if (data.getRead().equals("Active"))
                        attendance.setVisible(true);
                    break;
                case "My Attendance":
                    if (data.getRead().equals("Active"))
                        myAttendance.setVisible(true);
                    break;
                case "Enquiries":
                    if (data.getRead().equals("Active"))
                        enq.setVisible(true);
                    break;
                case "Video Live Streaming":
                    if (data.getRead().equals("Active"))
                        live.setVisible(true);
                    break;
                case "Online Exam":
                    if (data.getRead().equals("Active"))
                        exam.setVisible(true);
            }
        }
    }
}
