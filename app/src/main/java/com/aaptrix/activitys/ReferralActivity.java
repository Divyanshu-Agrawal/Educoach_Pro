package com.aaptrix.activitys;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.AppBarLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
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
import de.hdodenhof.circleimageview.CircleImageView;

import static com.aaptrix.tools.HttpUrl.REFER_DATA;
import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREF_COLOR;
import static cz.msebera.android.httpclient.conn.ssl.SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER;

public class ReferralActivity extends AppCompatActivity {

    Toolbar toolbar;
    AppBarLayout appBarLayout;
    String selToolColor, selStatusColor, selTextColor1;
    TextView referCode, referBenefit;
    CircleImageView schoolLogo, userImg;
    String schoolId, userId, schoolName;
    String FACEBOOK = "com.facebook.katana";
    String TWITTER = "com.twitter.android";
    String MESSENGER = "com.facebook.orca";
    String WHATSAPP = "com.whatsapp";
    ImageView whatsapp, messenger, sms, copy, fb, twitter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_referral);
        toolbar = findViewById(R.id.toolbar);
        appBarLayout = findViewById(R.id.appBarLayout);
        setSupportActionBar(toolbar);
        setTitle("Refer Friends");
        setResult(RESULT_OK);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        referBenefit = findViewById(R.id.referral_benefit);
        referCode = findViewById(R.id.referral_code);
        schoolLogo = findViewById(R.id.school_logo);
        userImg = findViewById(R.id.user_image);
        whatsapp = findViewById(R.id.whatsapp_share);
        messenger = findViewById(R.id.messenger_share);
        sms = findViewById(R.id.message_share);
        copy = findViewById(R.id.copy_share);
        fb = findViewById(R.id.facebook_share);
        twitter = findViewById(R.id.twitter_share);

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

        SharedPreferences sp = this.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        userId = sp.getString("userID", "");
        schoolId = sp.getString("userSchoolId", "");
        schoolName = sp.getString("userSchoolName", "");

        String url = sp.getString("imageUrl", "") + sp.getString("userSchoolId", "")
                + "/other/" + sp.getString("userSchoolLogo1", "");
        Picasso.with(this).load(url).error(R.drawable.app_logo).placeholder(R.drawable.app_logo).into(schoolLogo);
        schoolLogo.setBorderWidth(2);
        schoolLogo.setBorderColor(Color.parseColor(selToolColor));

        if (!sp.getString("userImg", "").equals("0")) {
            String profUrl = sp.getString("imageUrl", "") + sp.getString("userSchoolId", "")
                    + "/users/students/profile/" + sp.getString("userImg", "");
            Picasso.with(this).load(profUrl).error(R.drawable.user_place_hoder).placeholder(R.drawable.user_place_hoder).into(userImg);
        } else {
            Picasso.with(this).load(url).error(R.drawable.app_logo).placeholder(R.drawable.app_logo).into(userImg);
            schoolLogo.setVisibility(View.GONE);
        }

        String code = sp.getString("refer_code", "");
        String offer = sp.getString("refer_offer", "");

        if (code != null && !code.equals("null") && offer != null && !offer.equals("null")) {
            referCode.setText(code);
            referBenefit.setText(offer);
            share();
        } else {
            fetchData();
        }
    }

    private void fetchData() {
        new Thread(() -> {
            try {
                SSLContext sslContext = SSLContexts.custom().useTLS().build();
                SSLConnectionSocketFactory f = new SSLConnectionSocketFactory(
                        sslContext,
                        new String[]{"TLSv1.1", "TLSv1.2"},
                        null,
                        BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
                HttpClient httpclient = HttpClients.custom().setSSLSocketFactory(f).build();
                HttpPost httppost = new HttpPost(REFER_DATA);
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<>(2);
                nameValuePairs.add(new BasicNameValuePair("schoolId", schoolId));
                nameValuePairs.add(new BasicNameValuePair("userId", userId));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity httpEntity = response.getEntity();
                final String result = EntityUtils.toString(httpEntity);

                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(() -> {
                    try {
                        Log.e("result", result);
                        JSONObject jsonObject = new JSONObject(result);
                        referCode.setText(jsonObject.getString("referral_code"));
                        referBenefit.setText(jsonObject.getString("referral_offer"));
                        SharedPreferences sp = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sp.edit();
                        editor.putString("refer_code", jsonObject.getString("referral_code"));
                        editor.putString("refer_offer", jsonObject.getString("referral_offer"));
                        editor.apply();
                        share();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void share() {
        String msg = "Join " + schoolName + ",\n \n" + "With my referral code : "
                + referCode.getText() + "\n \n Benefits : \n" + referBenefit.getText();

        whatsapp.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.setPackage(WHATSAPP);
            intent.putExtra(Intent.EXTRA_TEXT, msg);
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException ex) {
                Toast.makeText(this, "WhatsApp not installed on this device", Toast.LENGTH_SHORT).show();
            }
        });

        messenger.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.setPackage(MESSENGER);
            intent.putExtra(Intent.EXTRA_TEXT, msg);
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException ex) {
                Toast.makeText(this, "Messenger not installed on this device", Toast.LENGTH_SHORT).show();
            }
        });

        sms.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setType("vnd.android-dir/mms-sms");
            intent.putExtra("sms_body",msg);
            startActivity(intent);
        });

        copy.setOnClickListener(v -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Referral", msg);
            clipboard.setPrimaryClip(clip);
            Toast.makeText(this, "Copied to clipboard", Toast.LENGTH_SHORT).show();
        });

        fb.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.setPackage(FACEBOOK);
            intent.putExtra(Intent.EXTRA_TEXT, msg);
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException ex) {
                Toast.makeText(this, "Facebook not installed on this device", Toast.LENGTH_SHORT).show();
            }
        });

        twitter.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_SEND);
            intent.setType("text/plain");
            intent.setClassName(TWITTER, "com.twitter.android.composer.ComposerActivity");
            intent.putExtra(Intent.EXTRA_TEXT, msg);
            try {
                startActivity(intent);
            } catch (ActivityNotFoundException ex) {
                Toast.makeText(this, "Twitter not installed on this device", Toast.LENGTH_SHORT).show();
            }
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
}
