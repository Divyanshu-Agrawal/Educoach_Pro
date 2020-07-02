package com.aaptrix.activitys.student;

import com.aaptrix.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.aaptrix.fragments.MajorResult;
import com.aaptrix.fragments.MinorResult;
import com.aaptrix.fragments.PreviousExam;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import static com.aaptrix.tools.SPClass.PREFS_RW;
import static com.aaptrix.tools.SPClass.PREF_COLOR;

public class StudentResult extends AppCompatActivity {

    AppBarLayout appBarLayout;
    String selToolColor, selStatusColor, selTextColor1, selDrawerColor;
    TextView tool_title;
    String stdSection, studentName, studentId, studentImg, userType;
    ViewPager viewPager;
    TabLayout tabLayout;
    String readPermission = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_result);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        appBarLayout = findViewById(R.id.appBarLayout);
        tool_title = findViewById(R.id.tool_title);
        viewPager = findViewById(R.id.contacts_viewpager);
        tabLayout = findViewById(R.id.tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.addTab(tabLayout.newTab().setText("Major Exams"));
        tabLayout.addTab(tabLayout.newTab().setText("Minor Exams"));

        userType = getIntent().getStringExtra("userType");

        if (userType.equals("teacher")) {
            studentId = getIntent().getStringExtra("studentId");
            studentName = getIntent().getStringExtra("studentName");
            studentImg = getIntent().getStringExtra("studentImage");
            stdSection = getIntent().getStringExtra("userSection");
        }

        SharedPreferences sp = getSharedPreferences(PREFS_RW, Context.MODE_PRIVATE);
        try {
            JSONObject jsonObject = new JSONObject(sp.getString("result", ""));
            JSONArray jsonArray = jsonObject.getJSONArray("result");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                if (object.getString("tbl_insti_buzz_cate_name").equals("Online Exam")) {
                    readPermission = object.getString("tbl_scl_inst_buzz_detl_status");
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        if (readPermission.equals("Active")) {
            tabLayout.addTab(tabLayout.newTab().setText("Online Exams"));
        }

        SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
        selToolColor = settingsColor.getString("tool", "");
        selStatusColor = settingsColor.getString("status", "");
        selTextColor1 = settingsColor.getString("text1", "");
        selDrawerColor = settingsColor.getString("drawer", "");

        tabLayout.setBackgroundColor(Color.parseColor(selToolColor));
        tabLayout.setSelectedTabIndicatorColor(Color.parseColor(selToolColor));
        tabLayout.setTabTextColors(Color.parseColor("#ffffff"), Color.parseColor(selToolColor));

        appBarLayout.setBackgroundColor(Color.parseColor(selToolColor));
        tabLayout.setBackgroundColor(Color.parseColor("#ffffff"));
        tabLayout.setTabTextColors(Color.parseColor(selToolColor), Color.parseColor(selStatusColor));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(selStatusColor));
        }
        tool_title.setTextColor(Color.parseColor(selTextColor1));

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition(), true);
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    private class ViewPagerAdapter extends FragmentStatePagerAdapter {

        ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @NonNull
        @Override
        public Fragment getItem(int position) {
            Bundle bundle = new Bundle();
            bundle.putString("userType", userType);
            if (userType.equals("teacher")) {
                bundle.putString("studentId", studentId);
                bundle.putString("studentName", studentName);
                bundle.putString("studentImage", studentImg);
                bundle.putString("userSection", stdSection);
            }
            switch (position) {
                case 0:
                    Fragment major = new MajorResult();
                    major.setArguments(bundle);
                    return major;
                case 1:
                    Fragment minor = new MinorResult();
                    minor.setArguments(bundle);
                    return minor;
                case 2:
                    Fragment online = new PreviousExam();
                    online.setArguments(bundle);
                    return online;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            if (readPermission.equals("Active"))
                return 3;
            else
                return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            String title = null;
            if (position == 0) {
                title = "Major Exams";
            } else if (position == 1) {
                title = "Minor Exams";
            } else if (readPermission.equals("Active"))
                if (position == 2) {
                    title = "Online Exams";
                }
            return title;
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return true;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
