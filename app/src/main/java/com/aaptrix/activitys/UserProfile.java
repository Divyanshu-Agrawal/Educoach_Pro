package com.aaptrix.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.chrisbanes.photoview.PhotoView;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import com.aaptrix.R;

import javax.net.ssl.SSLContext;

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

import static com.aaptrix.tools.HttpUrl.LOGOUT;
import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREF_COLOR;
import static com.aaptrix.tools.SPClass.PREF_ROLE;
import static cz.msebera.android.httpclient.conn.ssl.SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER;

public class UserProfile extends AppCompatActivity {

    Toolbar toolbar;
    ImageView userProfile, editProfile;
    SharedPreferences sp;
    ProgressBar progressBar;
    TextView gender, dob, email, number, type, name, section, roll, sectionTitle, rollTitle, typeTitle;
    RelativeLayout fullscrLayout;
    LinearLayout edit_layout;
    ImageView edit;
    PhotoView fullscrImage;
    boolean isVisible = false;
    TextView androidId, idTitle;
    ImageView copy;
    RelativeLayout layout;
    String url = "", selToolColor, strPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);
        toolbar = findViewById(R.id.toolbar);
        userProfile = findViewById(R.id.profile_image);
        editProfile = findViewById(R.id.edit_profile);
        progressBar = findViewById(R.id.progress_bar);
        name = findViewById(R.id.user_name);
        layout = findViewById(R.id.layout);
        edit_layout = findViewById(R.id.edit_layout);
        edit = findViewById(R.id.edit_details);
        setSupportActionBar(toolbar);
        toolbar.bringToFront();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle("");
        sp = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        gender = findViewById(R.id.user_gender);
        dob = findViewById(R.id.user_dob);
        type = findViewById(R.id.user_type);
        email = findViewById(R.id.user_email);
        number = findViewById(R.id.user_contact);
        fullscrImage = findViewById(R.id.fullscr_user_profile);
        fullscrLayout = findViewById(R.id.fullscr_image);
        section = findViewById(R.id.user_section);
        roll = findViewById(R.id.user_roll);
        sectionTitle = findViewById(R.id.section_title);
        rollTitle = findViewById(R.id.roll_title);
        idTitle = findViewById(R.id.id_title);
        androidId = findViewById(R.id.android_id);
        typeTitle = findViewById(R.id.org_title);
        copy = findViewById(R.id.copy);

        String userImg = sp.getString("userImg", "");
        String userType = sp.getString("userrType", "");
        String imageUrl = sp.getString("imageUrl", "") + sp.getString("userSchoolId", "");

        if (userType.equals("Admin") || userType.equals("Teacher")) {
            idTitle.setVisibility(View.VISIBLE);
            androidId.setVisibility(View.VISIBLE);
            copy.setVisibility(View.VISIBLE);

            @SuppressLint("HardwareIds") String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            androidId.setText(deviceId);

            copy.setOnClickListener(v -> {
                ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Device ID", deviceId);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(this, "Copied to clipboard", Toast.LENGTH_SHORT).show();
            });

            type.setVisibility(View.VISIBLE);
            typeTitle.setVisibility(View.VISIBLE);
        } else {
            typeTitle.setVisibility(View.GONE);
            type.setVisibility(View.GONE);
        }

        strPhone = sp.getString("userPhone", "");
        type.setText(userType);
        name.setText(sp.getString("userName", ""));
        number.setText(strPhone);
        email.setText(sp.getString("userEmailId", ""));
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date date = sdf.parse(sp.getString("userDob", ""));
            sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            dob.setText(sdf.format(date));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        gender.setText(sp.getString("userGender", ""));
        if (userType.equals("Student")) {
            section.setText(sp.getString("userSection", ""));
            roll.setText(sp.getString("userRollNumber", ""));
        } else {
            section.setVisibility(View.GONE);
            sectionTitle.setVisibility(View.GONE);
            rollTitle.setVisibility(View.GONE);
            roll.setVisibility(View.GONE);
        }

        edit.setOnClickListener(v -> {
            Intent intent = new Intent(this, UpdateProfile.class);
            startActivity(intent);
        });

        if (userImg.equals("0")) {
            userProfile.setImageDrawable(getResources().getDrawable(R.drawable.user_place_hoder));
        } else if (!TextUtils.isEmpty(userImg)) {
            switch (userType) {
                case "Parent":
                case "Student":
                    url = imageUrl + "/users/students/profile/" + userImg;
                    Picasso.with(this).load(url).error(R.drawable.dummy).placeholder(R.drawable.dummy).into(userProfile);
                    break;
                case "Admin":
                    url = imageUrl + "/users/admin/profile/" + userImg;
                    Picasso.with(this).load(url).error(R.drawable.dummy).placeholder(R.drawable.dummy).into(userProfile);
                    break;
                case "Staff":
                    url = imageUrl + "/users/staff/profile/" + userImg;
                    Picasso.with(this).load(url).error(R.drawable.dummy).placeholder(R.drawable.dummy).into(userProfile);
                    break;
                case "Teacher":
                    url = imageUrl + "/users/teachers/profile/" + userImg;
                    Picasso.with(this).load(url).error(R.drawable.dummy).placeholder(R.drawable.dummy).into(userProfile);
                    break;
            }
            userProfile.setOnClickListener(v -> {
                if (!url.isEmpty()) {
                    fullscrLayout.setVisibility(View.VISIBLE);
                    isVisible = true;
                    Picasso.with(this).load(url).placeholder(R.drawable.user_place_hoder).into(fullscrImage);
                }
            });
        } else {
            userProfile.setImageDrawable(getResources().getDrawable(R.drawable.user_place_hoder));
        }

        SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
        selToolColor = settingsColor.getString("tool", "");

        editProfile.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(v.getContext(), v);
            popup.getMenuInflater().inflate(R.menu.profile_menu, popup.getMenu());
            popup.show();
            popup.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.change_password:
                        if (isInternetOn()) {
                            Intent i = new Intent(this, ChangePasswordActivity.class);
                            startActivity(i);
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        } else {
                            Toast.makeText(this, "No network Please connect with network for update", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.change_phone:
                        if (isInternetOn()) {
                            Intent i = new Intent(this, UpdatePhoneNumberActivity.class);
                            startActivity(i);
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        } else {
                            Toast.makeText(this, "No network Please connect with network for update", Toast.LENGTH_SHORT).show();
                        }
                        break;
                    case R.id.logout:
                        AlertDialog.Builder alert;
                        LayoutInflater factory = LayoutInflater.from(this);
                        View textEntryView = factory.inflate(R.layout.logout_layout, null);
                        alert = new AlertDialog.Builder(this, R.style.DialogTheme);
                        alert.setView(textEntryView).setPositiveButton("Yes",
                                (dialog, whichButton) -> {
                                    logout(strPhone);
                                }).setNegativeButton("Cancel", null);
                        AlertDialog alertDialog = alert.create();
                        alertDialog.show();
                        Button theButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
                        Button theButton1 = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
                        theButton.setTextColor(Color.parseColor(selToolColor));
                        theButton1.setTextColor(Color.parseColor(selToolColor));
                        break;
                }
                return true;
            });
        });

        GradientDrawable bgShape = (GradientDrawable) edit_layout.getBackground();
        bgShape.setColor(Color.parseColor(selToolColor));
        edit.setBackgroundColor(Color.parseColor(selToolColor));
    }

    private void logout(String mobileno) {
        File directory = this.getFilesDir();
        File file = new File(directory, "instituteBuzz");
        file.delete();
        file = new File(directory, "batches");
        file.delete();
        getSharedPreferences(PREF_COLOR, 0).edit().clear().apply();
        getSharedPreferences(PREF_ROLE, 0).edit().clear().apply();
        getSharedPreferences(PREFS_NAME, 0).edit().clear().apply();
        getSharedPreferences("date", 0).edit().clear().apply();
        if (isInternetOn()) {
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            Intent i = new Intent(this, AppLogin.class);
            i.putExtra("status", "Online");
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            Toast.makeText(this, "Thank You", Toast.LENGTH_SHORT).show();
        } else {
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            Intent i = new Intent(this, AppLogin.class);
            i.putExtra("status", "Offline");
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            Toast.makeText(this, "Thank You", Toast.LENGTH_SHORT).show();
        }
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
        if (isVisible) {
            isVisible = false;
            fullscrLayout.setVisibility(View.GONE);
        } else {
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            super.onBackPressed();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
