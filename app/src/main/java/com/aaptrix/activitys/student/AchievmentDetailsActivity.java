package com.aaptrix.activitys.student;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.PendingIntent;
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
import android.os.SystemClock;
import android.provider.Settings;

import com.aaptrix.activitys.AppLogin;
import com.aaptrix.activitys.FullScreenImageActivity;

import androidx.annotation.RequiresApi;

import com.aaptrix.activitys.admin.AddNewActivity;
import com.aaptrix.activitys.admin.AddNewPublication;
import com.aaptrix.activitys.admin.InstituteBuzzActivityDiff;
import com.aaptrix.activitys.teacher.SchoolCalenderActivity;
import com.aaptrix.activitys.teacher.TeacherDairyActivity;
import com.google.android.material.appbar.AppBarLayout;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.squareup.picasso.Picasso;

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
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import com.aaptrix.adaptor.CustomSliderView;
import com.aaptrix.databeans.DataBeanAboutUs;
import com.aaptrix.R;
import com.aaptrix.notifications.NotificationPublisher;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.aaptrix.tools.HttpUrl.REMOVE_ACTIVITY;
import static com.aaptrix.tools.HttpUrl.REMOVE_DIARY;
import static com.aaptrix.tools.HttpUrl.REMOVE_EVENT;
import static com.aaptrix.tools.HttpUrl.REMOVE_HW;
import static com.aaptrix.tools.HttpUrl.REMOVE_PUBLICATION;
import static com.aaptrix.tools.HttpUrl.UPDAATE_EXAM_TT;
import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREFS_RW;
import static com.aaptrix.tools.SPClass.PREF_COLOR;

/**
 * Created by Administrator on 12/3/2017.
 */

public class AchievmentDetailsActivity extends AppCompatActivity implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener {
    String achImg, achCate, achTitle, achDesc, acgDate, aboutMoreImages, activiId, acgEndDate = "";
    ImageView image_main;
    TextView titleMain, details, date, tool_title, Enddate;
    CircleImageView iv_edit;
    AppBarLayout appBarLayout;

    String selToolColor, selDrawerColor, selStatusColor, selTextColor1, selTextColor2;
    LinearLayout date_layout;

    SliderLayout sliderLayout;
    HashMap<String, String> Hash_file_maps;
    ArrayList<DataBeanAboutUs> activiArraySlider = new ArrayList<>();
    DataBeanAboutUs dbau;

    SharedPreferences settings1;
    SharedPreferences.Editor editor1;
    LinearLayout edit_layout;
    private SharedPreferences settings;
    SharedPreferences.Editor editor;
    String userrType, userId, schoolid, batch;
    EditText tv_exam_detail;
    AlertDialog.Builder alert;
    LinearLayout mainLayoutDialog;
    String[] image;
    ImageButton delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.achievement_details_layout);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        appBarLayout = findViewById(R.id.appBarLayout);
        date_layout = findViewById(R.id.date_layout);
        tool_title = findViewById(R.id.tool_title);
        SwipeRefreshLayout mSwipeRefreshLayout = findViewById(R.id.activity_main_swipe_refresh_layout);
        mSwipeRefreshLayout.setRefreshing(false);
        mSwipeRefreshLayout.setEnabled(false);
        delete = findViewById(R.id.delete);

        //color
        SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
        selToolColor = settingsColor.getString("tool", "");
        selDrawerColor = settingsColor.getString("drawer", "");
        selStatusColor = settingsColor.getString("status", "");
        selTextColor1 = settingsColor.getString("text1", "");
        selTextColor2 = settingsColor.getString("text2", "");

        settings = getSharedPreferences(PREFS_NAME, 0);
        userrType = settings.getString("userrType", "");
        userId = settings.getString("userID", "");
        schoolid = settings.getString("userSchoolId", "");


        image_main = findViewById(R.id.image_main);

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-1, -1);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = width * 9 / 16;
        params.width = width;
        params.height = height;

        image_main.setLayoutParams(params);

        iv_edit = findViewById(R.id.iv_edit);
        titleMain = findViewById(R.id.titleMain);
        details = findViewById(R.id.details);
        date = findViewById(R.id.date);
        Enddate = findViewById(R.id.Enddate);
        sliderLayout = findViewById(R.id.slider);
        sliderLayout.setLayoutParams(params);
        edit_layout = findViewById(R.id.edit_layout);


        //Log.e( "imgurl",achImg );
        achCate = getIntent().getStringExtra("achCate");
        achTitle = getIntent().getStringExtra("achTitle");
        if (!achCate.equals("activities")) {
            achImg = getIntent().getStringExtra("achImg");
            achDesc = getIntent().getStringExtra("achDesc");
            acgDate = getIntent().getStringExtra("acgDate");
        } else {
            achDesc = "";
            details.setVisibility(View.GONE);
            sliderLayout.setVisibility(View.GONE);
            image_main.setVisibility(View.GONE);
        }

        switch (achCate) {
            case "event": {
                tool_title.setText(achTitle);
                String date1 = acgDate;
                activiId = getIntent().getStringExtra("activiId");
                Log.d("activiId", "" + activiId);
                acgEndDate = getIntent().getStringExtra("acgEndDate");

                if (acgDate.equals(acgEndDate)) {
                    setDate(date1);
                    Enddate.setText("");
                } else {
                    setDate(date1);
                    setDate1(acgEndDate);
                }

                sliderLayout.setVisibility(View.GONE);
                image_main.setVisibility(View.GONE);

                SharedPreferences preferences = getSharedPreferences(PREFS_RW, 0);
                String json = preferences.getString("result", "");

                try {
                    JSONObject jsonObject = new JSONObject(json);
                    JSONArray jsonArray = jsonObject.getJSONArray("result");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        if (object.getString("tbl_insti_buzz_cate_name").equals("Institute Calendar")) {
                            if (object.getString("tbl_scl_inst_buzz_detl_write_status").equals("Active")) {
                                delete.setVisibility(View.VISIBLE);
                            } else {
                                delete.setVisibility(View.GONE);
                            }
                            break;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    delete.setImageTintList(ColorStateList.valueOf(Color.parseColor(selTextColor1)));
                }

                delete.setOnClickListener(v -> new AlertDialog.Builder(this).setTitle("Are you sure you want to delete?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            if (activiId != null) {
                                RemoveEvent event = new RemoveEvent(this);
                                event.execute(schoolid, activiId);
                            }
                        }).setNegativeButton("No", null).show());

                if (achImg != null && !achImg.equals("null") && !achImg.equals("0")) {
                    image = achImg.split(",");

                    if (achImg.equals("[]") || image.length == 0) {
                        sliderLayout.setVisibility(View.GONE);
                        image_main.setVisibility(View.GONE);
                    } else {
                        activiArraySlider.clear();
                        if (image.length == 1) {
                            image_main.setVisibility(View.VISIBLE);
                            sliderLayout.setVisibility(View.GONE);

                            for (String anImage : image) {
                                SharedPreferences sp = this.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                                String url = sp.getString("imageUrl", "") + sp.getString("userSchoolId", "");
                                dbau = new DataBeanAboutUs();
                                aboutMoreImages = url + "/events/" + anImage.replace("\"", "").
                                        replace("[", "").replace("]", "").replace(" ", "");
                                dbau.setAboutMoreImages(aboutMoreImages);
                                activiArraySlider.add(dbau);
                            }
                            if (aboutMoreImages.equals("[]")) {
                                image_main.setVisibility(View.GONE);
                            } else {
                                Picasso.with(this).load(aboutMoreImages).error(R.drawable.dummy).placeholder(R.drawable.dummy).into(image_main);
                            }

                        } else {
                            sliderLayout.setVisibility(View.VISIBLE);
                            image_main.setVisibility(View.GONE);
                            for (String anImage : image) {
                                SharedPreferences sp = this.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                                String url = sp.getString("imageUrl", "") + sp.getString("userSchoolId", "");
                                dbau = new DataBeanAboutUs();
                                aboutMoreImages = url + "/events/" + anImage.replace("\"", "").
                                        replace("[", "").replace("]", "").replace(" ", "");
                                dbau.setAboutMoreImages(aboutMoreImages);
                                activiArraySlider.add(dbau);
                            }
                            slider();
                        }
                    }
                } else {
                    image_main.setVisibility(View.GONE);
                    sliderLayout.setVisibility(View.GONE);
                }


                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Date date11 = null;
                try {
                    date11 = dateFormat.parse(acgDate);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                Calendar calendar5 = Calendar.getInstance();
                calendar5.setTime(date11);
                calendar5.add(Calendar.DATE, -5);
                final String eventBeforeDays5 = dateFormat.format(calendar5.getTime());


                Calendar calendar3 = Calendar.getInstance();
                calendar3.setTime(date11);
                calendar3.add(Calendar.DATE, -3);
                final String eventBeforeDays3 = dateFormat.format(calendar3.getTime());

                Calendar calendar1 = Calendar.getInstance();
                calendar1.setTime(date11);
                calendar1.add(Calendar.DATE, -1);
                final String eventBeforeDays1 = dateFormat.format(calendar1.getTime());

                Date c = Calendar.getInstance().getTime();
                SimpleDateFormat df = new SimpleDateFormat("yyyyy-MM-dd");
                String Today = df.format(c);
                String a5 = getCountOfDays(Today, eventBeforeDays5);
                String a3 = getCountOfDays(Today, eventBeforeDays3);
                String a1 = getCountOfDays(Today, eventBeforeDays1);

                int hrs5 = ((Integer.parseInt((a5))) * 24 * 60 * 60);
                int hrs3 = ((Integer.parseInt((a3))) * 24 * 60 * 60);
                int hrs1 = ((Integer.parseInt((a1))) * 24 * 60 * 60);

                settings1 = getSharedPreferences(achTitle, 0);
                String val = settings1.getString(achTitle, "");


                if (!achTitle.equals(val)) {
                    settings1 = getSharedPreferences(achTitle, 0);
                    editor1 = settings1.edit();
                    editor1.putString(achTitle, achTitle);
                    editor1.apply();
                    int unique5 = (Integer.parseInt(activiId)) + 5;
                    int unique3 = (Integer.parseInt(activiId)) + 3;
                    int unique1 = (Integer.parseInt(activiId)) + 1;
                    scheduleNotification(getNotification(achTitle, AchievmentDetailsActivity.this), hrs5, AchievmentDetailsActivity.this, unique5);
                    scheduleNotification(getNotification(achTitle, AchievmentDetailsActivity.this), hrs3, AchievmentDetailsActivity.this, unique3);
                    scheduleNotification(getNotification(achTitle, AchievmentDetailsActivity.this), hrs1, AchievmentDetailsActivity.this, unique1);
                }
                break;
            }
            case "activities": {
                activiId = getIntent().getStringExtra("activiId");
                tool_title.setText(achTitle);
                sliderLayout.setVisibility(View.GONE);
                image_main.setVisibility(View.GONE);
                date_layout.setVisibility(View.GONE);

                SharedPreferences preferences = getSharedPreferences(PREFS_RW, 0);
                String json = preferences.getString("result", "");

                try {
                    JSONObject jsonObject = new JSONObject(json);
                    JSONArray jsonArray = jsonObject.getJSONArray("result");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        if (object.getString("tbl_insti_buzz_cate_name").equals("Activities")) {
                            if (object.getString("tbl_scl_inst_buzz_detl_write_status").equals("Active")) {
                                delete.setVisibility(View.VISIBLE);
                            } else {
                                delete.setVisibility(View.GONE);
                            }
                            break;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    delete.setImageTintList(ColorStateList.valueOf(Color.parseColor(selTextColor1)));
                }

                delete.setOnClickListener(v -> new AlertDialog.Builder(this).setTitle("Are you sure you want to delete?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            if (activiId != null) {
                                RemoveActivity event = new RemoveActivity(this);
                                event.execute(schoolid, activiId);
                            }
                        }).setNegativeButton("No", null).show());

                break;
            }
            case "publication": {
                tool_title.setText(achTitle);
                activiId = getIntent().getStringExtra("achId");

                String date1 = acgDate;
                setDate(date1);
                sliderLayout.setVisibility(View.GONE);
                image_main.setVisibility(View.GONE);

                SharedPreferences preferences = getSharedPreferences(PREFS_RW, 0);
                String json = preferences.getString("result", "");

                try {
                    JSONObject jsonObject = new JSONObject(json);
                    JSONArray jsonArray = jsonObject.getJSONArray("result");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        if (object.getString("tbl_insti_buzz_cate_name").equals("What's New!")) {
                            if (object.getString("tbl_scl_inst_buzz_detl_write_status").equals("Active")) {
                                delete.setVisibility(View.VISIBLE);
                            } else {
                                delete.setVisibility(View.GONE);
                            }
                            break;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    delete.setImageTintList(ColorStateList.valueOf(Color.parseColor(selTextColor1)));
                }

                delete.setOnClickListener(v -> new AlertDialog.Builder(this).setTitle("Are you sure you want to delete?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            if (activiId != null) {
                                RemovePublication event = new RemovePublication(this);
                                event.execute(schoolid, activiId);
                            }
                        }).setNegativeButton("No", null).show());

                if (achImg != null && !achImg.equals("null") && !achImg.equals("0")) {
                    image = achImg.split(",");

                    if (achImg.equals("[]") || image.length == 0) {
                        sliderLayout.setVisibility(View.GONE);
                        image_main.setVisibility(View.GONE);
                    } else {
                        activiArraySlider.clear();
                        if (image.length == 1) {
                            image_main.setVisibility(View.VISIBLE);
                            sliderLayout.setVisibility(View.GONE);

                            for (String anImage : image) {
                                SharedPreferences sp = this.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                                String url = sp.getString("imageUrl", "") + sp.getString("userSchoolId", "");
                                dbau = new DataBeanAboutUs();
                                aboutMoreImages = url + "/publication/" + anImage.replace("\"", "").
                                        replace("[", "").replace(" ", "").replace(" ", "").replace("]", "").replace(" ", "");
                                Log.e("image", aboutMoreImages);
                                Log.e("image", aboutMoreImages);
                                dbau.setAboutMoreImages(aboutMoreImages);
                                activiArraySlider.add(dbau);
                            }
                            if (aboutMoreImages.equals("0")) {
                                image_main.setVisibility(View.GONE);
                            } else {
                                Picasso.with(this).load(aboutMoreImages).error(R.drawable.dummy).placeholder(R.drawable.dummy).into(image_main);
                            }

                        } else {
                            sliderLayout.setVisibility(View.VISIBLE);
                            image_main.setVisibility(View.GONE);
                            for (String anImage : image) {
                                SharedPreferences sp = this.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                                String url = sp.getString("imageUrl", "") + sp.getString("userSchoolId", "");
                                dbau = new DataBeanAboutUs();
                                aboutMoreImages = url + "/publication/" + anImage.replace("\"", "").
                                        replace("[", "").replace(" ", "").replace(" ", "").replace("]", "").replace(" ", "");
                                Log.e("image", aboutMoreImages);
                                dbau.setAboutMoreImages(aboutMoreImages);
                                activiArraySlider.add(dbau);
                            }
                            slider();
                        }
                    }
                } else {
                    image_main.setVisibility(View.GONE);
                    sliderLayout.setVisibility(View.GONE);
                }
                break;
            }
            case "dairy": {
                tool_title.setText(achTitle);
                activiId = getIntent().getStringExtra("dairyId");
                String date1 = acgDate;
                setDate(date1);
                titleMain.setText(achTitle);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    details.setText(Html.fromHtml(achDesc, Html.FROM_HTML_MODE_COMPACT));
                } else {
                    details.setText(Html.fromHtml(achDesc));
                }

                SharedPreferences preferences = getSharedPreferences(PREFS_RW, 0);
                String json = preferences.getString("result", "");

                try {
                    JSONObject jsonObject = new JSONObject(json);
                    JSONArray jsonArray = jsonObject.getJSONArray("result");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        if (object.getString("tbl_insti_buzz_cate_name").equals("Remarks")) {
                            if (object.getString("tbl_scl_inst_buzz_detl_write_status").equals("Active")) {
                                delete.setVisibility(View.VISIBLE);
                            } else {
                                delete.setVisibility(View.GONE);
                            }
                            break;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    delete.setImageTintList(ColorStateList.valueOf(Color.parseColor(selTextColor1)));
                }

                delete.setOnClickListener(v -> new AlertDialog.Builder(this).setTitle("Are you sure you want to delete?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            if (activiId != null) {
                                RemoveDiary diary = new RemoveDiary(this);
                                diary.execute(schoolid, activiId);
                            } else {
                                Toast.makeText(this, "Error 101", Toast.LENGTH_SHORT).show();
                            }
                        }).setNegativeButton("No", null).show());


                Log.e("image", String.valueOf(achImg));
                if (achImg != null && !achImg.equals("null") && !achImg.equals("0")) {
                    image = achImg.split(",");

                    if (achImg.equals("[]") || image.length == 0) {
                        sliderLayout.setVisibility(View.GONE);
                        image_main.setVisibility(View.GONE);
                    } else {
                        activiArraySlider.clear();
                        if (image.length == 1) {
                            image_main.setVisibility(View.VISIBLE);
                            sliderLayout.setVisibility(View.GONE);

                            for (String anImage : image) {
                                SharedPreferences sp = this.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                                String url = sp.getString("imageUrl", "") + sp.getString("userSchoolId", "");
                                dbau = new DataBeanAboutUs();
                                aboutMoreImages = url + "/diary/" + anImage.replace("\"", "").
                                        replace("[", "").replace(" ", "").replace(" ", "").replace("]", "").replace(" ", "");
                                Log.e("url", aboutMoreImages);
                                dbau.setAboutMoreImages(aboutMoreImages);
                                activiArraySlider.add(dbau);
                            }
                            if (aboutMoreImages.equals("0")) {
                                image_main.setVisibility(View.GONE);
                            } else {
                                Picasso.with(this).load(aboutMoreImages).error(R.drawable.dummy).placeholder(R.drawable.dummy).into(image_main);
                            }

                        } else {
                            sliderLayout.setVisibility(View.VISIBLE);
                            image_main.setVisibility(View.GONE);
                            for (String anImage : image) {
                                SharedPreferences sp = this.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                                String url = sp.getString("imageUrl", "") + sp.getString("userSchoolId", "");
                                dbau = new DataBeanAboutUs();
                                aboutMoreImages = url + "/diary/" + anImage.replace("\"", "").
                                        replace("[", "").replace(" ", "").replace(" ", "").replace("]", "");
                                Log.e("url", aboutMoreImages);
                                dbau.setAboutMoreImages(aboutMoreImages);
                                activiArraySlider.add(dbau);
                            }
                            slider();
                        }
                    }
                } else {
                    image_main.setVisibility(View.GONE);
                    sliderLayout.setVisibility(View.GONE);
                }

                break;
            }
            case "homework": {
                tool_title.setText(achTitle);
                activiId = getIntent().getStringExtra("dairyId");
                batch = getIntent().getStringExtra("batch");
                String date1 = acgDate;
                setDate(date1);
                titleMain.setText(achTitle);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    details.setText(Html.fromHtml(achDesc, Html.FROM_HTML_MODE_COMPACT));
                } else {
                    details.setText(Html.fromHtml(achDesc));
                }

                SharedPreferences preferences = getSharedPreferences(PREFS_RW, 0);
                String json = preferences.getString("result", "");

                try {
                    JSONObject jsonObject = new JSONObject(json);
                    JSONArray jsonArray = jsonObject.getJSONArray("result");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        if (object.getString("tbl_insti_buzz_cate_name").equals("Assignments")) {
                            if (object.getString("tbl_scl_inst_buzz_detl_write_status").equals("Active")) {
                                delete.setVisibility(View.VISIBLE);
                            } else {
                                delete.setVisibility(View.GONE);
                            }
                            break;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    delete.setImageTintList(ColorStateList.valueOf(Color.parseColor(selTextColor1)));
                }

                delete.setOnClickListener(v -> new AlertDialog.Builder(this).setTitle("Are you sure you want to delete?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            if (activiId != null) {
                                RemoveHomework homework = new RemoveHomework(this);
                                homework.execute(schoolid, activiId);
                            }
                        }).setNegativeButton("No", null).show());

                if (achImg != null && !achImg.equals("null") && !achImg.equals("0")) {
                    image = achImg.split(",");

                    if (achImg.equals("[]") || image.length == 0) {
                        sliderLayout.setVisibility(View.GONE);
                        image_main.setVisibility(View.GONE);
                    } else {
                        activiArraySlider.clear();
                        if (image.length == 1) {
                            image_main.setVisibility(View.VISIBLE);
                            sliderLayout.setVisibility(View.GONE);

                            for (String anImage : image) {
                                SharedPreferences sp = this.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                                String url = sp.getString("imageUrl", "") + sp.getString("userSchoolId", "");
                                dbau = new DataBeanAboutUs();
                                aboutMoreImages = url + "/homework/" + anImage.replace("\"", "").
                                        replace("[", "").replace(" ", "").replace(" ", "").replace("]", "");
                                Log.e("hw url", aboutMoreImages);
                                dbau.setAboutMoreImages(aboutMoreImages);
                                activiArraySlider.add(dbau);
                            }
                            if (aboutMoreImages.equals("0")) {
                                image_main.setVisibility(View.GONE);
                            } else {
                                Picasso.with(this).load(aboutMoreImages).error(R.drawable.dummy).placeholder(R.drawable.dummy).into(image_main);
                            }

                        } else {
                            sliderLayout.setVisibility(View.VISIBLE);
                            image_main.setVisibility(View.GONE);
                            for (String anImage : image) {
                                SharedPreferences sp = this.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                                String url = sp.getString("imageUrl", "") + sp.getString("userSchoolId", "");
                                dbau = new DataBeanAboutUs();
                                aboutMoreImages = url + "/homework/" + anImage.replace("\"", "").
                                        replace("[", "").replace(" ", "").replace(" ", "").replace("]", "");
                                Log.e("hw url", aboutMoreImages);
                                dbau.setAboutMoreImages(aboutMoreImages);
                                activiArraySlider.add(dbau);
                            }
                            slider();
                        }
                    }
                } else {
                    image_main.setVisibility(View.GONE);
                    sliderLayout.setVisibility(View.GONE);
                }
                break;
            }
            case "classTT": {
                tool_title.setText(achTitle);
                activiId = getIntent().getStringExtra("dairyId");

                String date1 = acgDate;
                setDate(date1);
                Log.d("acgDate", acgDate);
                titleMain.setText(achTitle);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    details.setText(Html.fromHtml(achDesc, Html.FROM_HTML_MODE_COMPACT));
                } else {
                    details.setText(Html.fromHtml(achDesc));
                }
                sliderLayout.setVisibility(View.GONE);
                image_main.setVisibility(View.GONE);
                break;
            }
            case "examTT":
                tool_title.setText(achTitle);
                activiId = getIntent().getStringExtra("examId");
                // date.setText(acgDate);
                String examDate = acgDate;
                setDate(examDate);
                titleMain.setText(achTitle);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    details.setText(Html.fromHtml(achDesc, Html.FROM_HTML_MODE_COMPACT));
                } else {
                    details.setText(Html.fromHtml(achDesc));
                }
                sliderLayout.setVisibility(View.GONE);
                image_main.setVisibility(View.GONE);
                if (userrType.equals("Teacher") || userrType.equals("Admin")) {
                    edit_layout.setVisibility(View.VISIBLE);
                    edit_layout.setOnClickListener(view -> updateTimeTable(activiId, achDesc, achTitle));
                } else {
                    edit_layout.setVisibility(View.GONE);
                }
                break;
            default:
                tool_title.setText(achTitle);
                date.setText(acgDate);
                sliderLayout.setVisibility(View.GONE);
                image_main.setVisibility(View.VISIBLE);
                if (!achImg.equals("0") && !achImg.equals("[]")) {
                    String[] image = achImg.split(",");
                    String url = settings.getString("imageUrl", "") + settings.getString("userSchoolId", "") + "/users/students/achivments/" + image[0].replace(" ", "").replace(" ", "").replace("\"", "").replace("[", "").replace("]", "");
                    Picasso.with(this).load(url).error(R.drawable.dummy).placeholder(R.drawable.dummy).into(image_main);
                } else {
                    image_main.getLayoutParams().height = 0;
                }

                break;
        }

        image_main.setOnClickListener(view -> {
            String url;
            if (achImg != null && !achImg.equals("null")) {
                String[] image = achImg.split(",");
                switch (achCate) {
                    case "event":
                        url = settings.getString("imageUrl", "") + settings.getString("userSchoolId", "")
                                + "/events/" + image[0].replace("\"", "").replace(" ", "").replace(" ", "").replace(" ", "").replace("[", "").replace("]", "");
                        break;
                    case "activities":
                        url = settings.getString("imageUrl", "") + settings.getString("userSchoolId", "")
                                + "/activities/" + image[0].replace("\"", "").replace(" ", "").replace(" ", "").replace(" ", "").replace("[", "").replace("]", "");
                        break;
                    case "dairy":
                        url = settings.getString("imageUrl", "") + settings.getString("userSchoolId", "")
                                + "/diary/" + image[0].replace("\"", "").replace(" ", "").replace(" ", "").replace(" ", "").replace("[", "").replace("]", "").replace(" ", "");
                        break;
                    case "homework":
                        url = settings.getString("imageUrl", "") + settings.getString("userSchoolId", "")
                                + "/homework/" + image[0].replace("\"", "").replace(" ", "").replace(" ", "").replace(" ", "").replace("[", "").replace("]", "");
                        break;
                    case "publication":
                        url = settings.getString("imageUrl", "") + settings.getString("userSchoolId", "")
                                + "/publication/" + image[0].replace("\"", "").replace(" ", "").replace(" ", "").replace("[", "").replace("]", "").replace(" ", "");
                        break;
                    default:
                        url = settings.getString("imageUrl", "") + settings.getString("userSchoolId", "")
                                + "/users/students/achivments/" + image[0].replace(" ", "").replace(" ", "").replace(" ", "").replace("\"", "").replace("[", "").replace("]", "");
                        break;
                }
                Intent i = new Intent(AchievmentDetailsActivity.this, FullScreenImageActivity.class);
                i.putExtra("leaveImg", url);
                startActivity(i);
                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            }
        });

        titleMain.setText(achTitle);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            details.setText(Html.fromHtml(achDesc, Html.FROM_HTML_MODE_COMPACT));
        } else {
            details.setText(Html.fromHtml(achDesc));
        }

        appBarLayout.setBackgroundColor(Color.parseColor(selToolColor));
        date_layout.setBackgroundColor(Color.parseColor(selDrawerColor));
        tool_title.setTextColor(Color.parseColor(selTextColor1));
        GradientDrawable bgShape = (GradientDrawable) edit_layout.getBackground();
        bgShape.setColor(Color.parseColor(selToolColor));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(selStatusColor));
        }

    }


    private void updateTimeTable(final String examId, String achDesc, final String titleMain) {
        if (isInternetOn()) {
            LayoutInflater factory = LayoutInflater.from(AchievmentDetailsActivity.this);
            View textEntryView = factory.inflate(R.layout.add_exam_description_layout, null);
            mainLayoutDialog = textEntryView.findViewById(R.id.mainLayoutDialog);

            tv_exam_detail = textEntryView.findViewById(R.id.tv_hobby_name);
            tv_exam_detail.setText(achDesc);
            alert = new AlertDialog.Builder(AchievmentDetailsActivity.this, R.style.DialogTheme);
            //.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
            alert.setTitle(" Update Exam ").setView(textEntryView).setPositiveButton("Save",
                    (dialog, whichButton) -> {
                        String val = tv_exam_detail.getText().toString().trim();
                        details.setText(val);
                        updateExamTT b1 = new updateExamTT(AchievmentDetailsActivity.this);
                        b1.execute(userId, examId, val, titleMain);
                    });
            alert.setNegativeButton("Cancel",
                    (dialog, whichButton) -> {
                    });

            //	alert.show();
            AlertDialog alertDialog = alert.create();
            alertDialog.setCancelable(false);
            alertDialog.show();
            Button theButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
            Button theButton1 = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
            Button theButton2 = alertDialog.getButton(DialogInterface.BUTTON_NEUTRAL);
            //theButton.setOnClickListener(new CustomListener(alertDialog));
            theButton.setTextColor(Color.parseColor(selToolColor));
            theButton1.setTextColor(Color.parseColor(selToolColor));
            theButton2.setTextColor(Color.parseColor(selToolColor));
        } else {
            Toast.makeText(AchievmentDetailsActivity.this, "No network Please connect with network for update", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class RemovePublication extends AsyncTask<String, String, String> {
        Context ctx;

        RemovePublication(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String userId=params[0];
            String publicationId=params[1];
            String data;

            try {
                URL url = new URL(REMOVE_PUBLICATION);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                data = URLEncoder.encode("userId", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8")+ "&" +
                        URLEncoder.encode("publicationId", "UTF-8") + "=" + URLEncoder.encode(publicationId, "UTF-8");
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
            Log.e("Json", result);
            if (!result.isEmpty()) {
                startActivity(new Intent(ctx, PublicationActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            } else {
                Toast.makeText(ctx, "Some Error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class RemoveActivity extends AsyncTask<String, String, String> {
        Context ctx;

        RemoveActivity(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            String userId = params[0];
            String achievId = params[1];
            String data;

            try {

                URL url = new URL(REMOVE_ACTIVITY);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                data = URLEncoder.encode("userId", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8") + "&" +
                        URLEncoder.encode("achievId", "UTF-8") + "=" + URLEncoder.encode(achievId, "UTF-8");
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
            if (!result.isEmpty()) {
                startActivity(new Intent(ctx, ActivitiesActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            } else {
                Toast.makeText(ctx, "Some Error", Toast.LENGTH_SHORT).show();
            }
        }

    }

    @SuppressLint("StaticFieldLeak")
    public class RemoveEvent extends AsyncTask<String, String, String> {
        Context ctx;

        RemoveEvent(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String school_id = params[0];
            String event_id = params[1];
            String data;

            try {
                URL url = new URL(REMOVE_EVENT);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                data = URLEncoder.encode("tbl_school_id", "UTF-8") + "=" + URLEncoder.encode(school_id, "UTF-8") + "&" +
                        URLEncoder.encode("tbl_event_id", "UTF-8") + "=" + URLEncoder.encode(event_id, "UTF-8");
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
                startActivity(new Intent(ctx, SchoolCalenderActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            } else {
                Toast.makeText(ctx, "Some Error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class updateExamTT extends AsyncTask<String, String, String> {
        Context ctx;

        updateExamTT(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            Toast.makeText(ctx, "Please wait we are updating", Toast.LENGTH_SHORT).show();
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {

            String userId = params[0];
            String examId = params[1];
            String subjectDetails = params[2];
            String examName = params[3];

            String data;

            try {

                URL url = new URL(UPDAATE_EXAM_TT);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                data = URLEncoder.encode("userId", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8") + "&" +
                        URLEncoder.encode("examId", "UTF-8") + "=" + URLEncoder.encode(examId, "UTF-8") + "&" +
                        URLEncoder.encode("subjectDetail", "UTF-8") + "=" + URLEncoder.encode(subjectDetails, "UTF-8") + "&" +
                        URLEncoder.encode("examName", "UTF-8") + "=" + URLEncoder.encode(examName, "UTF-8");
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
            Log.d("HOBBYS", "" + result);
            //	pDialog.dismiss();
            if (result.equals("\"submitted\"")) {
                Toast.makeText(ctx, "Updated", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ctx, "Server issues", Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(result);
        }

    }

    private void setDate(String date1) {
        String[] separated = date1.split("-");
        String day = separated[2];
        int m = Integer.parseInt(separated[1]);
        String year = separated[0];
        String monthName = "";
        if (m == 1) {
            monthName = "January";
        } else if (m == 2) {
            monthName = "February";

        } else if (m == 3) {
            monthName = "March";

        } else if (m == 4) {
            monthName = "April";

        } else if (m == 5) {
            monthName = "May";

        } else if (m == 6) {
            monthName = "June";

        } else if (m == 7) {
            monthName = "July";

        } else if (m == 8) {
            monthName = "August";

        } else if (m == 9) {
            monthName = "September";

        } else if (m == 10) {
            monthName = "October";

        } else if (m == 11) {
            monthName = "November";

        } else if (m == 12) {
            monthName = "December";

        }
        switch (achCate) {
            case "dairy":
                date.setText(day + "-" + monthName + "-" + year);
                break;
            case "homework":
                date.setText("Due Date : " + day + "-" + monthName + "-" + year);
                break;
            default:
                date.setText(day + "-" + monthName + "-" + year);
                break;
        }
    }

    private void setDate1(String acgEndDate) {
        String[] separated = acgEndDate.split("-");
        String day = separated[2];
        int m = Integer.parseInt(separated[1]);
        String year = separated[0];
        String monthName = "";
        if (m == 1) {
            monthName = "January";
        } else if (m == 2) {
            monthName = "February";
        } else if (m == 3) {
            monthName = "March";
        } else if (m == 4) {
            monthName = "April";
        } else if (m == 5) {
            monthName = "May";
        } else if (m == 6) {
            monthName = "June";
        } else if (m == 7) {
            monthName = "July";
        } else if (m == 8) {
            monthName = "August";
        } else if (m == 9) {
            monthName = "September";
        } else if (m == 10) {
            monthName = "October";
        } else if (m == 11) {
            monthName = "November";
        } else if (m == 12) {
            monthName = "December";
        }
        Enddate.setText(" to " + day + "-" + monthName + "-" + year);
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

    public void slider() {
        Hash_file_maps = new HashMap<>();
        for (int i = 0; i < activiArraySlider.size(); i++) {
            Hash_file_maps.put(1 + i + "" + "/" + activiArraySlider.size(), activiArraySlider.get(i).getAboutMoreImages());
        }
        for (String name : Hash_file_maps.keySet()) {
            DefaultSliderView textSliderView = new DefaultSliderView(AchievmentDetailsActivity.this);
            textSliderView
                    .description(name)
                    .image(Hash_file_maps.get(name))
                    .setScaleType(CustomSliderView.ScaleType.Fit)
                    .setOnSliderClickListener(this);
            textSliderView.bundle(new Bundle());
            textSliderView.getBundle()
                    .putString("extra", name);
            sliderLayout.addSlider(textSliderView);
        }
        sliderLayout.setPresetTransformer(SliderLayout.Transformer.Accordion);
        sliderLayout.setPresetIndicator(SliderLayout.PresetIndicators.Center_Bottom);
        sliderLayout.setIndicatorVisibility(PagerIndicator.IndicatorVisibility.Invisible);

        sliderLayout.setCustomAnimation(new DescriptionAnimation());
        sliderLayout.setPresetTransformer(4);
        sliderLayout.setDuration(5000);
        sliderLayout.addOnPageChangeListener(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == android.R.id.home) {
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

    }

    @SuppressLint("StaticFieldLeak")
    public class RemoveHomework extends AsyncTask<String, String, String> {
        Context ctx;

        RemoveHomework(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String school_id = params[0];
            String homework_id = params[1];
            String data;

            try {
                URL url = new URL(REMOVE_HW);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                data = URLEncoder.encode("tbl_school_id", "UTF-8") + "=" + URLEncoder.encode(school_id, "UTF-8") + "&" +
                        URLEncoder.encode("tbl_homework_id", "UTF-8") + "=" + URLEncoder.encode(homework_id, "UTF-8");
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
                startActivity(new Intent(ctx, HomeworkActivity.class).putExtra("batch", batch).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            } else {
                Toast.makeText(ctx, "Some Error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class RemoveDiary extends AsyncTask<String, String, String> {
        Context ctx;

        RemoveDiary(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String school_id = params[0];
            String homework_id = params[1];
            Log.e("schoolid", school_id);
            Log.e("diary id", homework_id);
            String data;

            try {
                URL url = new URL(REMOVE_DIARY);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                data = URLEncoder.encode("tbl_school_id", "UTF-8") + "=" + URLEncoder.encode(school_id, "UTF-8") + "&" +
                        URLEncoder.encode("tbl_diary_id", "UTF-8") + "=" + URLEncoder.encode(homework_id, "UTF-8");
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
                startActivity(new Intent(ctx, TeacherDairyActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            } else {
                Toast.makeText(ctx, "Some Error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        String aa = (String) slider.getBundle().get("extra");
        assert aa != null;
        String[] bb = aa.split("/");
        String cc = bb[0];
        int val = Integer.parseInt(cc);
        String url;
        String[] image = achImg.split(",");
        switch (achCate) {
            case "event":
                url = settings.getString("imageUrl", "") + settings.getString("userSchoolId", "")
                        + "/events/" + image[val - 1].replace("\"", "").replace(" ", "").replace(" ", "").replace(" ", "").replace("[", "").replace("]", "");
                break;
            case "activities":
                url = settings.getString("imageUrl", "") + settings.getString("userSchoolId", "")
                        + "/activities/" + image[val - 1].replace("\"", "").replace(" ", "").replace(" ", "").replace(" ", "").replace("[", "").replace("]", "");
                break;
            case "dairy":
                url = settings.getString("imageUrl", "") + settings.getString("userSchoolId", "")
                        + "/diary/" + image[val - 1].replace("\"", "").replace(" ", "").replace(" ", "").replace(" ", "").replace("[", "").replace("]", "").replace(" ", "");
                break;
            case "homework":
                url = settings.getString("imageUrl", "") + settings.getString("userSchoolId", "")
                        + "/homework/" + image[val - 1].replace("\"", "").replace(" ", "").replace(" ", "").replace(" ", "").replace("[", "").replace("]", "");
                break;
            case "publication":
                url = settings.getString("imageUrl", "") + settings.getString("userSchoolId", "")
                        + "/publication/" + image[val - 1].replace("\"", "").replace(" ", "").replace(" ", "").replace("[", "").replace("]", "").replace(" ", "");
                break;
            default:
                url = settings.getString("imageUrl", "") + settings.getString("userSchoolId", "")
                        + "/users/students/achivments/" + image[val - 1].replace(" ", "").replace(" ", "").replace(" ", "").replace("\"", "").replace("[", "").replace("]", "");
                break;
        }
        Intent i = new Intent(this, FullScreenImageActivity.class);
        i.putExtra("leaveImg", url);
        startActivity(i);
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }


    private void scheduleNotification(Notification notification, int delay, Activity act, int unique) {
        Intent notificationIntent = new Intent(act, NotificationPublisher.class);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION_ID, 1);
        notificationIntent.putExtra(NotificationPublisher.NOTIFICATION, notification);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(act, unique, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_ONE_SHOT);
        long futureInMillis = SystemClock.elapsedRealtime() + delay;
        AlarmManager alarmManager = (AlarmManager) act.getSystemService(Context.ALARM_SERVICE);
        assert alarmManager != null;
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, futureInMillis, pendingIntent);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private Notification getNotification(String content, Activity act) {
        Notification.Builder builder = new Notification.Builder(act);
        builder.setContentTitle("Event Notification");
        builder.setContentText(content);
        builder.setSmallIcon(R.drawable.notification_icon);
        builder.setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND);
        Intent notificationIntent = new Intent(AchievmentDetailsActivity.this, AppLogin.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent intent1 = PendingIntent.getActivity(AchievmentDetailsActivity.this, 0, notificationIntent, 0);
        builder.setAutoCancel(true);
        builder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
        //LED
        builder.setLights(Color.RED, 3000, 3000);
        builder.setContentIntent(intent1);
        return builder.build();
    }

    public String getCountOfDays(String createdDateString, String expireDateString) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date createdConvertedDate = null, expireCovertedDate = null, todayWithZeroTime = null;
        try {
            createdConvertedDate = dateFormat.parse(createdDateString);
            expireCovertedDate = dateFormat.parse(expireDateString);
            Date today = new Date();
            todayWithZeroTime = dateFormat.parse(dateFormat.format(today));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        int cYear, cMonth, cDay;

        assert createdConvertedDate != null;
        if (createdConvertedDate.after(todayWithZeroTime)) {
            Calendar cCal = Calendar.getInstance();
            cCal.setTime(createdConvertedDate);
            cYear = cCal.get(Calendar.YEAR);
            cMonth = cCal.get(Calendar.MONTH);
            cDay = cCal.get(Calendar.DAY_OF_MONTH);
        } else {
            Calendar cCal = Calendar.getInstance();
            cCal.setTime(todayWithZeroTime);
            cYear = cCal.get(Calendar.YEAR);
            cMonth = cCal.get(Calendar.MONTH);
            cDay = cCal.get(Calendar.DAY_OF_MONTH);
        }
        Calendar eCal = Calendar.getInstance();
        eCal.setTime(expireCovertedDate);

        int eYear = eCal.get(Calendar.YEAR);
        int eMonth = eCal.get(Calendar.MONTH);
        int eDay = eCal.get(Calendar.DAY_OF_MONTH);

        Calendar date1 = Calendar.getInstance();
        Calendar date2 = Calendar.getInstance();

        date1.clear();
        date1.set(cYear, cMonth, cDay);
        date2.clear();
        date2.set(eYear, eMonth, eDay);

        long diff = date2.getTimeInMillis() - date1.getTimeInMillis();

        float dayCount = (float) diff / (24 * 60 * 60 * 1000);

        return ("" + (int) dayCount);
    }


}
