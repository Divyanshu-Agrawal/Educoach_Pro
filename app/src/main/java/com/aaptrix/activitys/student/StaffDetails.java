package com.aaptrix.activitys.student;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aaptrix.R;
import com.google.android.material.appbar.AppBarLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.Objects;

import javax.net.ssl.SSLContext;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.conn.ssl.SSLConnectionSocketFactory;
import cz.msebera.android.httpclient.conn.ssl.SSLContexts;
import cz.msebera.android.httpclient.entity.mime.HttpMultipartMode;
import cz.msebera.android.httpclient.entity.mime.MultipartEntityBuilder;
import cz.msebera.android.httpclient.impl.client.HttpClients;
import cz.msebera.android.httpclient.util.EntityUtils;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.aaptrix.tools.HttpUrl.SEND_RATING;
import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREF_COLOR;
import static cz.msebera.android.httpclient.conn.ssl.SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER;

public class StaffDetails extends AppCompatActivity {

    Toolbar toolbar;
    CircleImageView userProfile;
    AppBarLayout appBarLayout;
    SharedPreferences sp;
    TextView name, bio, subjects, tool_title, bio_title, sub_title;
    Button rateBtn;
    String strName, strId, strBio, strRateEnabled, strSubjects, strImage, strType, strRated, strComment, strRating, strShort;
    String url = "", schoolId, userId, imageUrl;
    String selToolColor, selDrawerColor, selStatusColor, selTextColor1, selTextColor2;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_staff_details);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        setTitle("");
        setResult(RESULT_OK);
        appBarLayout = findViewById(R.id.appBarLayout);
        tool_title = findViewById(R.id.tool_title);
        bio_title = findViewById(R.id.bio_title);
        sub_title = findViewById(R.id.sub_title);

        sp = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        schoolId = sp.getString("str_school_id","");
        userId = sp.getString("userID", "");
        imageUrl = sp.getString("imageUrl", "");
        String userType = sp.getString("userrType", "");

        SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
        selToolColor = settingsColor.getString("tool", "");
        selDrawerColor = settingsColor.getString("drawer", "");
        selStatusColor = settingsColor.getString("status", "");
        selTextColor1 = settingsColor.getString("text1", "");
        selTextColor2 = settingsColor.getString("text2", "");

        userProfile = findViewById(R.id.profile_image);
        name = findViewById(R.id.user_name);
        bio = findViewById(R.id.user_bio);
        subjects = findViewById(R.id.subjects);
        rateBtn = findViewById(R.id.rate_btn);

        strName = getIntent().getStringExtra("name");
        strId = getIntent().getStringExtra("id");
        strBio = getIntent().getStringExtra("bio");
        strRateEnabled = getIntent().getStringExtra("rateEnable");
        strSubjects = getIntent().getStringExtra("subjects");
        strImage = getIntent().getStringExtra("image");
        strType = getIntent().getStringExtra("type");
        strRated = getIntent().getStringExtra("rated");
        strComment = getIntent().getStringExtra("comment");
        strRating = getIntent().getStringExtra("rating");
        strShort = getIntent().getStringExtra("shortBio");

        if (strRated.equals("1")) {
            rateBtn.setText("Show Rating");
        }

        if (userType.equals("Guest")) {
            rateBtn.setVisibility(View.GONE);
        } else if (strRateEnabled.equals("0")) {
            rateBtn.setVisibility(View.GONE);
        }

        rateBtn.setOnClickListener(v -> {
            if (strRated.equals("0")) {
                rateStaff();
            } else {
                showRate();
            }
        });

        name.setText(strName);
        bio_title.setText("About " + strName);
        bio.setText(strBio);

        String[] subs;

        if (strShort.equals("") || strShort.equals("null")) {
            try {
                JSONArray jsonArray = new JSONArray(strSubjects);
                subs = new String[jsonArray.length()];
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    subs[i] = jsonObject.getString("tbl_batch_subjct_name");
                }
                if (subs.length < 2) {
                    sub_title.setText("Subject");
                }
                subjects.setText(Arrays.toString(subs).replace("[", "").replace("]", ""));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        } else {
            subjects.setText(strShort);
        }

        if (!strImage.isEmpty() && !strImage.equals("0")) {
            switch (strType) {
                case "Admin":
                    url = imageUrl + schoolId + "/users/admin/profile/" + strImage;
                    Picasso.with(this).load(url).error(R.drawable.user_place_hoder).placeholder(R.drawable.user_place_hoder).into(userProfile);
                    break;
                case "Teacher":
                    url = imageUrl + schoolId + "/users/teachers/profile/" + strImage;
                    Picasso.with(this).load(url).error(R.drawable.user_place_hoder).placeholder(R.drawable.user_place_hoder).into(userProfile);
                    break;
            }
        } else {
            Picasso.with(this).load(R.drawable.user_place_hoder).into(userProfile);
        }

        appBarLayout.setBackgroundColor(Color.parseColor(selToolColor));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(selStatusColor));
        }
        tool_title.setTextColor(Color.parseColor(selTextColor1));
        rateBtn.setBackgroundColor(Color.parseColor(selToolColor));
        rateBtn.setTextColor(Color.parseColor(selTextColor1));

    }

    private void rateStaff() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.rate_staff, null);

        CircleImageView profile = view.findViewById(R.id.staff_profile);
        RatingBar ratingBar = view.findViewById(R.id.rate_bar);
        EditText review = view.findViewById(R.id.staff_review);
        TextView name = view.findViewById(R.id.staff_name);

        name.setText(strName);

        switch (strType) {
            case "Admin":
                url = imageUrl + schoolId + "/users/admin/profile/" + strImage;
                Picasso.with(this).load(url).error(R.drawable.dummy).placeholder(R.drawable.dummy).into(profile);
                break;
            case "Teacher":
                url = imageUrl + schoolId + "/users/teachers/profile/" + strImage;
                Picasso.with(this).load(url).error(R.drawable.dummy).placeholder(R.drawable.dummy).into(profile);
                break;
        }

        ratingBar.setStepSize(1);
        LayerDrawable drawable = (LayerDrawable) ratingBar.getProgressDrawable();
        drawable.getDrawable(2).setColorFilter(Color.parseColor("#FFD700"), PorterDuff.Mode.SRC_ATOP);

        GradientDrawable drawable2 = (GradientDrawable) review.getBackground();
        drawable2.setStroke(5, Color.parseColor(selToolColor));

        AlertDialog.Builder alert = new AlertDialog.Builder(this, R.style.DialogTheme);
        alert.setView(view).setPositiveButton("Submit",
                (dialog, whichButton) -> {

                }).setNegativeButton("Cancel",
                (dialog, whichButton) -> {
                });

        AlertDialog alertDialog = alert.create();
        alertDialog.show();
        Button theButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        Button theButton1 = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        theButton.setBackgroundColor(Color.TRANSPARENT);
        theButton1.setBackgroundColor(Color.TRANSPARENT);
        theButton.setTextColor(getResources().getColor(R.color.text_gray));
        theButton1.setTextColor(getResources().getColor(R.color.text_gray));

        theButton.setOnClickListener(v -> {
            if (ratingBar.getRating() < 1) {
                Toast.makeText(this, "Please select rating", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Sending", Toast.LENGTH_SHORT).show();
                if (review.getText().toString().isEmpty()) {
                    sendRating(String.valueOf(Math.round(ratingBar.getRating())), "No Comment");
                } else {
                    sendRating(String.valueOf(Math.round(ratingBar.getRating())), review.getText().toString());
                }
            }
        });
    }

    private void showRate() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.show_rate, null);

        CircleImageView profile = view.findViewById(R.id.staff_profile);
        RatingBar ratingBar = view.findViewById(R.id.rate_bar);
        TextView review = view.findViewById(R.id.staff_review);
        TextView name = view.findViewById(R.id.staff_name);

        LayerDrawable drawable = (LayerDrawable) ratingBar.getProgressDrawable();
        drawable.getDrawable(2).setColorFilter(Color.parseColor("#FFD700"), PorterDuff.Mode.SRC_ATOP);
        drawable.getDrawable(1).setColorFilter(Color.parseColor("#00FFD700"), PorterDuff.Mode.SRC_ATOP);
        ratingBar.setIsIndicator(true);
        name.setText(strName);
        review.setText(strComment);

        switch (strType) {
            case "Admin":
                url = imageUrl + schoolId + "/users/admin/profile/" + strImage;
                Picasso.with(this).load(url).error(R.drawable.dummy).placeholder(R.drawable.dummy).into(profile);
                break;
            case "Teacher":
                url = imageUrl + schoolId + "/users/teachers/profile/" + strImage;
                Picasso.with(this).load(url).error(R.drawable.dummy).placeholder(R.drawable.dummy).into(profile);
                break;
        }

        if (strRating != null && !strRating.equals("null")) {
            ratingBar.setRating(Float.valueOf(strRating));
        }

        AlertDialog.Builder alert = new AlertDialog.Builder(this, R.style.DialogTheme);
        alert.setView(view).setPositiveButton("OK", null);
        AlertDialog alertDialog = alert.create();
        alertDialog.show();
        Button theButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        theButton.setBackgroundColor(Color.TRANSPARENT);
        theButton.setTextColor(getResources().getColor(R.color.text_gray));
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

    private void sendRating(String rating, String comment) {
            new Thread(() -> {
                try {
                    SSLContext sslContext = SSLContexts.custom().useTLS().build();
                    SSLConnectionSocketFactory f = new SSLConnectionSocketFactory(
                            sslContext,
                            new String[]{"TLSv1.1", "TLSv1.2"},
                            null,
                            BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
                    HttpClient httpclient = HttpClients.custom().setSSLSocketFactory(f).build();
                    HttpPost httppost = new HttpPost(SEND_RATING);
                    MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
                    entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                    entityBuilder.addTextBody("tbl_school_id", schoolId);
                    entityBuilder.addTextBody("tbl_users_id", userId);
                    entityBuilder.addTextBody("tbl_staff_id", strId);
                    entityBuilder.addTextBody("tbl_staff_name", strName);
                    entityBuilder.addTextBody("rating", rating);
                    entityBuilder.addTextBody("comment", comment);
                    HttpEntity entity = entityBuilder.build();
                    httppost.setEntity(entity);
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity httpEntity = response.getEntity();
                    String res = EntityUtils.toString(httpEntity);
                    Log.e("result", res);
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(() -> {
                        if (res.contains("submitted")) {
                            Toast.makeText(this, "Submitted", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(this, StaffActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
    }
}
