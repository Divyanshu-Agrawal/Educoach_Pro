package com.aaptrix.activitys.admin;

import com.aaptrix.R;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.AppBarLayout;
import com.squareup.picasso.Picasso;

import java.util.Objects;

import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREF_COLOR;

public class AdminStaffLeaveIntermediate extends AppCompatActivity {

    AppBarLayout appBarLayout;
    String selToolColor, selStatusColor, selTextColor1;
    TextView tool_title;
    Spinner type;
    String[] category = {"Select Type", "Teacher", "Staff", "Other"};
    String userType = "0";
    ImageView school_logo;
    ProgressBar loader_section;
    TextView cube1, cube2;
    TextView view1;
    TextView view_leave;
    String userId, roleId, schoolId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_staff_leave_intermediate);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        appBarLayout = findViewById(R.id.appBarLayout);
        tool_title = findViewById(R.id.tool_title);
        cube1 = findViewById(R.id.cube1);
        cube2 = findViewById(R.id.cube2);
        view1 = findViewById(R.id.view1);
        type = findViewById(R.id.spin_section);
        view_leave = findViewById(R.id.view_attendance);
        school_logo = findViewById(R.id.school_logo);
        loader_section = findViewById(R.id.loader);

        SharedPreferences settings = this.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        roleId = settings.getString("str_role_id", "");
        schoolId = settings.getString("str_school_id", "");
        userId = settings.getString("userID", "");

        Picasso.with(this).load(R.drawable.large_logo).into(school_logo);
        view_leave.setClickable(false);

        SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
        selToolColor = settingsColor.getString("tool", "");
        selStatusColor = settingsColor.getString("status", "");
        selTextColor1 = settingsColor.getString("text1", "");

        appBarLayout.setBackgroundColor(Color.parseColor(selToolColor));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(selStatusColor));
        }
        tool_title.setTextColor(Color.parseColor(selTextColor1));
        view_leave.setTextColor(Color.parseColor(selTextColor1));
        view_leave.setBackgroundColor(Color.parseColor(selToolColor));
        GradientDrawable drawable = (GradientDrawable) cube1.getBackground();
        drawable.setStroke(2, Color.parseColor(selToolColor));
        GradientDrawable drawable1 = (GradientDrawable) cube2.getBackground();
        drawable1.setStroke(2, Color.parseColor(selToolColor));
        view1.setBackgroundColor(Color.parseColor(selToolColor));

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, R.layout.spinner_list_item1, category);
        dataAdapter.setDropDownViewResource(R.layout.spinner_list_item1);
        type.setAdapter(dataAdapter);
        type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.text_gray));
                switch (category[position]) {
                    case "Teacher":
                        userType = "2";
                        break;
                    case "Staff":
                        userType = "4";
                        break;
                    case "Other":
                        userType = "5";
                        break;
                    default:
                        userType = "0";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (isInternetOn()) {
            view_leave.setOnClickListener(view22 -> {
                if (!userType.equals("0")) {
                    Intent i12 = new Intent(this, AdminStaffLeave.class);
                    i12.putExtra("type", userType);
                    startActivity(i12);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                } else {
                    Toast.makeText(this, "Select Type", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(this, "No internet", Toast.LENGTH_SHORT).show();
        }
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
