package com.aaptrix.activitys.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.aaptrix.R;
import com.google.android.material.appbar.AppBarLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import static com.aaptrix.tools.SPClass.PREF_COLOR;

public class EnquiryDetails extends AppCompatActivity {

    AppBarLayout appBarLayout;
    TextView tool_title;
    String selToolColor, selStatusColor, selTextColor1, selDrawerColor;
    TextView name, phone, email, course, howto, details, date;
    String strName, strPhone, strEmail, strCourse, strHowto, strDetails, strDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enquiry_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        appBarLayout = findViewById(R.id.appBarLayout);
        tool_title = findViewById(R.id.tool_title);

        name = findViewById(R.id.name);
        phone = findViewById(R.id.phone);
        email = findViewById(R.id.email);
        course = findViewById(R.id.course);
        howto = findViewById(R.id.howto);
        details = findViewById(R.id.details);
        date = findViewById(R.id.date);

        SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
        selToolColor = settingsColor.getString("tool", "");
        selStatusColor = settingsColor.getString("status", "");
        selTextColor1 = settingsColor.getString("text1", "");
        selDrawerColor = settingsColor.getString("drawer", "");

        appBarLayout.setBackgroundColor(Color.parseColor(selToolColor));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(selStatusColor));
        }
        tool_title.setTextColor(Color.parseColor(selTextColor1));
        date.setBackgroundColor(Color.parseColor(selDrawerColor));
        date.setTextColor(Color.parseColor(selTextColor1));

        strCourse = getIntent().getStringExtra("course");
        strName = getIntent().getStringExtra("name");
        strDate = getIntent().getStringExtra("date");
        strDetails = getIntent().getStringExtra("details");
        strEmail = getIntent().getStringExtra("email");
        strHowto = getIntent().getStringExtra("howto");
        strPhone = getIntent().getStringExtra("phone");

        name.setText(strName);
        course.setText(strCourse);
        details.setText(strDetails);
        email.setText(strEmail);
        howto.setText(strHowto);
        phone.setText(strPhone);

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date formatDate = sdf.parse(strDate);
            sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            date.setText(sdf.format(formatDate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
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
