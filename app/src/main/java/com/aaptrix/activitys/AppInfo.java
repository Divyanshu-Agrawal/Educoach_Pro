package com.aaptrix.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ShareCompat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.aaptrix.BuildConfig;
import com.aaptrix.R;

import static com.aaptrix.activitys.SplashScreen.SCHOOL_NAME;
import static com.aaptrix.tools.HttpUrl.PV;
import static com.aaptrix.tools.HttpUrl.TC;
import static com.aaptrix.tools.SPClass.PREF_COLOR;

public class AppInfo extends AppCompatActivity {

    String versionName;
    TextView version, webUrl, contactNumber, contactEmail, privacyPolicy, terms;
    Toolbar toolbar;
    TextView rate, share;
    String selToolColor, selTextColor, selStatusColor;
    ImageView shareImg, rateImg, appLogo;
    TextView appName, developedBy, webText, contactText;
    RelativeLayout relativeLayout;
    ScrollView view;

    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_info);
        toolbar = findViewById(R.id.app_info_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        setTitle("");
        versionName = "Version " + BuildConfig.VERSION_NAME;
        version = findViewById(R.id.version);
        privacyPolicy = findViewById(R.id.privacy_policy);
        terms = findViewById(R.id.terms);

        version.setText(versionName);
        rate = findViewById(R.id.rate_app);
        share = findViewById(R.id.share_app);
        shareImg = findViewById(R.id.share_img);
        rateImg = findViewById(R.id.rate_img);
        appName = findViewById(R.id.app_name);
        developedBy = findViewById(R.id.developed_by);
        webText = findViewById(R.id.website_text);
        contactText = findViewById(R.id.contact_detail_text);
        contactEmail = findViewById(R.id.contact_email);
        contactNumber = findViewById(R.id.contact_number);
        appLogo = findViewById(R.id.aaptrix_logo);
        relativeLayout = findViewById(R.id.relative_layout);
        webUrl = findViewById(R.id.web_url);

        SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
        selToolColor = settingsColor.getString("tool", "");
        selTextColor = settingsColor.getString("text1", "");
        selStatusColor = settingsColor.getString("status", "");

        GradientDrawable drawable = new GradientDrawable();
        drawable.setColors(new int[]{Color.parseColor(selToolColor), Color.parseColor(selStatusColor)});
        drawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
        relativeLayout.setBackground(drawable);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(selStatusColor));
        }
        version.setTextColor(Color.parseColor(selTextColor));
        privacyPolicy.setTextColor(Color.parseColor(selTextColor));
        rate.setTextColor(Color.parseColor(selTextColor));
        share.setTextColor(Color.parseColor(selTextColor));
        appName.setTextColor(Color.parseColor(selTextColor));
        developedBy.setTextColor(Color.parseColor(selTextColor));
        webText.setTextColor(Color.parseColor(selTextColor));
        contactText.setTextColor(Color.parseColor(selTextColor));
        contactEmail.setTextColor(Color.parseColor(selTextColor));
        contactNumber.setTextColor(Color.parseColor(selTextColor));
        webUrl.setTextColor(Color.parseColor(selTextColor));
        shareImg.setImageTintList(ColorStateList.valueOf(Color.parseColor(selTextColor)));
        rateImg.setImageTintList(ColorStateList.valueOf(Color.parseColor(selTextColor)));
        appLogo.setImageTintList(ColorStateList.valueOf(Color.parseColor(selTextColor)));

        webUrl.setOnClickListener(v -> {
            Intent browserIntent = new Intent(Intent.ACTION_VIEW);
            browserIntent.setData(Uri.parse(webUrl.getText().toString()));
            startActivity(browserIntent);
        });

        contactNumber.setOnClickListener(v -> {
            Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
            String phone = "tel:" + contactNumber.getText().toString().trim();
            phoneIntent.setData(Uri.parse(phone));
            startActivity(phoneIntent);
        });

        contactEmail.setOnClickListener(v -> {
            Intent mailIntent = new Intent(Intent.ACTION_VIEW);
            Uri data = Uri.parse("mailto:?to=hello@apptrix.com");
            mailIntent.setData(data);
            startActivity(mailIntent);
        });

        privacyPolicy.setOnClickListener(v -> {
            Intent i = new Intent(this, TermsConditionsActivity.class);
            i.putExtra("url", PV);
            i.putExtra("tool_title", "Privacy Policy");
            startActivity(i);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        terms.setOnClickListener(v -> {
            Intent i = new Intent(this, TermsConditionsActivity.class);
            i.putExtra("url", TC);
            i.putExtra("tool_title", "Terms & Conditions");
            startActivity(i);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        rate.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setData(Uri.parse("market://details?id=" + getPackageName()));
            startActivity(i);
        });

        share.setOnClickListener(v -> {
            String msg = "Install "+ SCHOOL_NAME + ":" + " " + "\nhttp://play.google.com/store/apps/details?id=" + getPackageName();
            ShareCompat.IntentBuilder.from(this)
                    .setType("text/plain")
                    .setChooserTitle("Share via...")
                    .setText(msg)
                    .startChooser();
        });
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
