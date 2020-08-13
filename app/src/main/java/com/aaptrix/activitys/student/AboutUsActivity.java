package com.aaptrix.activitys.student;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import com.aaptrix.tools.ShareInstitute;
import com.google.android.material.appbar.AppBarLayout;

import com.aaptrix.activitys.FullScreenImageActivity;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.Html;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.slider.library.Animations.DescriptionAnimation;
import com.daimajia.slider.library.Indicators.PagerIndicator;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.DefaultSliderView;
import com.daimajia.slider.library.Tricks.ViewPagerEx;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import com.aaptrix.adaptor.AboutUsMoreInfoListAdaptor;
import com.aaptrix.adaptor.CustomSliderView;
import com.aaptrix.databeans.DataBeanAboutUs;
import com.aaptrix.R;

import static com.aaptrix.tools.HttpUrl.ABOUT_SCHOOL_INFO;
import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREF_COLOR;

/**
 * Created by Administrator on 11/29/2017.
 */

public class AboutUsActivity extends AppCompatActivity implements BaseSliderView.OnSliderClickListener, ViewPagerEx.OnPageChangeListener, OnMapReadyCallback {

    String userId, userSchoolId, userSchoolLogo;
    AppBarLayout appBarLayout;
    String selToolColor, selDrawerColor, selStatusColor, selTextColor1, selTextColor2;
    TextView tool_title;
    SliderLayout sliderLayout;
    HashMap<String, String> Hash_file_maps;
    TextView school_name, school_area, school_description, school_contact_number, school_cuisines, school_type, school_adress, school_website;
    ListView lv_more_info;
    String[] image;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    //offline
    ArrayList<DataBeanAboutUs> aboutArray = new ArrayList<>();
    DataBeanAboutUs dbau;
    AboutUsMoreInfoListAdaptor aboutUsMoreInfoListAdaptor;
    private String aboutName, aboutArea, aboutDesc, aboutContact, aboutCuisiness, aboutType, aboutMapLatitude, aboutMapLongitude, aboutAddr, aboutMoreImages, aboutSchoolWebsite;
    ImageView iv_right, iv_left;
    TextView tv_gtm, seperater;
    LinearLayout mapLayout, webLayout;
    SharedPreferences sp;
    LinearLayout share;
    TextView cusineTitle, facilities, connect;
    ImageView whatsapp, instagram, facebook, youtube, twitter;
    String strWA, strInsta, strfb, strYt, strTwitter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.about_us_layout);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        appBarLayout = findViewById(R.id.appBarLayout);
        tool_title = findViewById(R.id.tool_title);
        share = findViewById(R.id.share_layout);
        facilities = findViewById(R.id.facilities);
        cusineTitle = findViewById(R.id.cuisine_title);
        seperater = findViewById(R.id.seperater);
        sp = this.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        whatsapp = findViewById(R.id.view_whatsapp);
        facebook = findViewById(R.id.view_facebook);
        instagram = findViewById(R.id.view_instagram);
        twitter = findViewById(R.id.view_twitter);
        youtube = findViewById(R.id.view_youtube);
        connect = findViewById(R.id.connect);

        facilities.setText("Courses Offered");

        spInit();
        mSwipeRefreseInit();
        init();
        btnCallInit();
        valueSetInit();
    }

    private void valueSetInit() {
        appBarLayout.setBackgroundColor(Color.parseColor(selToolColor));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(selStatusColor));
        }
        tool_title.setTextColor(Color.parseColor(selTextColor1));
        mSwipeRefreshLayout.setColorSchemeResources(R.color.text_gray);
        school_contact_number.setTextColor(Color.parseColor(selToolColor));
        school_website.setTextColor(Color.parseColor(selToolColor));
        tv_gtm.setTextColor(Color.parseColor(selToolColor));
        seperater.setBackgroundColor(Color.parseColor(selToolColor));
        iv_left.setColorFilter(getResources().getColor(R.color.black));
        iv_right.setColorFilter(getResources().getColor(R.color.black));
        GradientDrawable bgShape = (GradientDrawable) share.getBackground();
        bgShape.setColor(Color.parseColor(selToolColor));
    }

    @SuppressLint("ClickableViewAccessibility")
    private void btnCallInit() {
        mapLayout.setOnTouchListener((v, event) -> {
            mapLayout.setEnabled(false);
            mapLayout.setClickable(false);
            return true;
        });

        if (isInternetOn()) {
            GetSchoolDeatails b1 = new GetSchoolDeatails(AboutUsActivity.this);
            b1.execute(userSchoolId);
        } else {
            Toast.makeText(this, "No Internet", Toast.LENGTH_SHORT).show();
            mSwipeRefreshLayout.setRefreshing(false);
        }

        iv_left.setOnClickListener(view -> sliderLayout.addOnPageChangeListener(AboutUsActivity.this));
        iv_right.setOnClickListener(view -> sliderLayout.addOnPageChangeListener(AboutUsActivity.this));

        school_contact_number.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:0" + school_contact_number.getText().toString().trim()));
            startActivity(intent);
        });
    }

    private void init() {
        school_name = findViewById(R.id.school_name);
        school_area = findViewById(R.id.school_area);
        school_contact_number = findViewById(R.id.school_contact_number);
        school_cuisines = findViewById(R.id.school_cuisines);
        school_type = findViewById(R.id.school_type);
        school_adress = findViewById(R.id.school_adress);
        tv_gtm = findViewById(R.id.tv_gtm);
        school_website = findViewById(R.id.school_website);
        school_description = findViewById(R.id.school_description);
        iv_right = findViewById(R.id.iv_right);
        iv_left = findViewById(R.id.iv_left);
        lv_more_info = findViewById(R.id.lv_more_info);
        sliderLayout = findViewById(R.id.slider);

        mapLayout = findViewById(R.id.mapLayout);
        webLayout = findViewById(R.id.webLayout);
        mapLayout.setEnabled(false);
        mapLayout.setClickable(false);
    }

    private void mSwipeRefreseInit() {
        mSwipeRefreshLayout = findViewById(R.id.activity_main_swipe_refresh_layout);
        mSwipeRefreshLayout.setRefreshing(false);
        mSwipeRefreshLayout.setEnabled(false);

        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            if (isInternetOn()) {
                GetSchoolDeatails b1 = new GetSchoolDeatails(AboutUsActivity.this);
                b1.execute(userSchoolId);
            } else {
                Toast.makeText(AboutUsActivity.this, "No network Please connect with network for update", Toast.LENGTH_SHORT).show();
                mSwipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void spInit() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        userId = settings.getString("userID", "");
        userSchoolId = settings.getString("str_school_id", "");
        userSchoolLogo = settings.getString("userSchoolLogo", "");
        //color
        SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
        selToolColor = settingsColor.getString("tool", "");
        selDrawerColor = settingsColor.getString("drawer", "");
        selStatusColor = settingsColor.getString("status", "");
        selTextColor1 = settingsColor.getString("text1", "");
        selTextColor2 = settingsColor.getString("text2", "");
    }

    @SuppressLint("StaticFieldLeak")
    public class GetSchoolDeatails extends AsyncTask<String, String, String> {
        Context ctx;

        GetSchoolDeatails(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            //loader.setVisibility(View.VISIBLE);
            mSwipeRefreshLayout.setRefreshing(true);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String userSchoolId = params[0];
            String data;
            try {
                URL url = new URL(ABOUT_SCHOOL_INFO);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                data = URLEncoder.encode("schoolId", "UTF-8") + "=" + URLEncoder.encode(userSchoolId, "UTF-8");
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

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String result) {
            mSwipeRefreshLayout.setRefreshing(false);
            if (result.contains("{\"result\":null}")) {
                school_name.setText("Not Available");
                school_area.setText("Not Available");
                school_description.setText("Not Available");
                school_contact_number.setText("Not Available");
                school_cuisines.setText("Not Available");
                school_type.setText("Not Available");
                school_adress.setText("Not Available");
                Toast.makeText(ctx, "No Data", Toast.LENGTH_SHORT).show();
            } else {
                try {
                    JSONObject jsonRootObject = new JSONObject(result);
                    JSONArray jsonArray = jsonRootObject.getJSONArray("result");
                    aboutArray.clear();
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    aboutName = jsonObject.getString("tbl_abt_schl_details_name");
                    aboutArea = jsonObject.getString("tbl_abt_schl_details_area");
                    aboutDesc = jsonObject.getString("tbl_abt_schl_details_desc");
                    aboutContact = jsonObject.getString("tbl_abt_schl_details_contact");
                    aboutCuisiness = jsonObject.getString("tbl_abt_schl_details_cuisines");
                    aboutType = jsonObject.getString("tbl_abt_schl_details_type");
                    aboutAddr = jsonObject.getString("tbl_abt_schl_details_addr");
                    aboutSchoolWebsite = jsonObject.getString("tbl_abt_schl_details_website");
                    aboutMapLatitude = jsonObject.getString("tbl_abt_schl_details_map_latitude");
                    aboutMapLongitude = jsonObject.getString("tbl_abt_schl_details_map_longitutde");
                    aboutMoreImages = jsonObject.getString("tbl_school_slider_imgs");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        dbau = new DataBeanAboutUs();
                        JSONObject object = jsonArray.getJSONObject(i);
                        dbau.setAboutMoreInfoName(object.getString("tbl_abt_schl_more_info_name"));
                        aboutArray.add(dbau);
                    }
                    JSONArray social = jsonRootObject.getJSONArray("SocialLinks");
                    JSONObject object = social.getJSONObject(0);
                    strWA = object.getString("whatsapp_no");
                    strfb = object.getString("facebook_url").split("\\?")[0];
                    strInsta = object.getString("instagram_url").split("\\?")[0];
                    strTwitter = object.getString("twitter_url").split("\\?")[0];
                    strYt = object.getString("youtube_url");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if (aboutArray.size() != 0) {
                    listItms();
                    slider();
                }
            }
            super.onPostExecute(result);
        }

    }

    private void listItms() {

        if (strYt.equals("null"))
            youtube.setVisibility(View.GONE);
        if (strTwitter.equals("null"))
            twitter.setVisibility(View.GONE);
        if (strInsta.equals("null"))
            instagram.setVisibility(View.GONE);
        if (strfb.equals("null"))
            facebook.setVisibility(View.GONE);
        if (strWA.equals("null"))
            whatsapp.setVisibility(View.GONE);

        if (strWA.equals("null") && strfb.equals("null") && strInsta.equals("null") && strTwitter.equals("null") && strYt.equals("null"))
            connect.setVisibility(View.GONE);

        facebook.setOnClickListener(v -> {
            Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
            String facebookUrl = getFacebookPageURL(strfb);
            facebookIntent.setData(Uri.parse(facebookUrl));
            startActivity(facebookIntent);
        });

        instagram.setOnClickListener(v -> {
            Intent intent = newInstagramProfileIntent(strInsta);
            startActivity(intent);
        });

        twitter.setOnClickListener(v -> {
            String[] name = strTwitter.split("/");
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("twitter://user?screen_name=" + name[name.length-1]));
                startActivity(intent);
            } catch (Exception e) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://twitter.com/#!/" + name[name.length-1])));
            }
        });

        youtube.setOnClickListener(v -> {
            Intent intent = new Intent(
                    Intent.ACTION_VIEW ,
                    Uri.parse(strYt));
            startActivity(intent);
        });

        whatsapp.setOnClickListener(v -> {
            String url = "https://api.whatsapp.com/send?phone=" + strWA;
            try {
                PackageManager pm = getPackageManager();
                pm.getPackageInfo("com.whatsapp", PackageManager.GET_ACTIVITIES);
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            } catch (PackageManager.NameNotFoundException e) {
                Toast.makeText(this, "WhatsApp not installed in your phone", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        });

        try {
            school_name.setText(aboutName);
            school_area.setText(aboutArea);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                school_description.setText(Html.fromHtml(aboutDesc, Html.FROM_HTML_MODE_COMPACT));
            } else {
                school_description.setText(Html.fromHtml(aboutDesc));
            }
            school_contact_number.setText(aboutContact);
            school_cuisines.setText(aboutCuisiness);
            school_type.setText(aboutType);
            school_adress.setText(aboutAddr);
            if (aboutType.equals("School")) {
                school_cuisines.setVisibility(View.VISIBLE);
                cusineTitle.setVisibility(View.VISIBLE);
            }
            if (aboutSchoolWebsite == null || aboutSchoolWebsite.equals("0") || aboutSchoolWebsite.equals("null")) {
                webLayout.setVisibility(View.GONE);
            } else {
                school_website.setText((aboutSchoolWebsite));
                webLayout.setVisibility(View.VISIBLE);
            }

            share.setOnClickListener(v -> {
                ShareInstitute institute = new ShareInstitute(this, this);
                institute.execute(userSchoolId);
            });

            school_website.setOnClickListener(view -> startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse(aboutSchoolWebsite))));
            aboutUsMoreInfoListAdaptor = new AboutUsMoreInfoListAdaptor(AboutUsActivity.this, R.layout.hobbies_list_item, aboutArray);
            lv_more_info.setAdapter(aboutUsMoreInfoListAdaptor);
            lv_more_info.setOnItemClickListener((parent, view, position, id) -> {
                String moreInfoName = aboutArray.get(position).getAboutMoreInfoName();
                Toast.makeText(AboutUsActivity.this, moreInfoName, Toast.LENGTH_SHORT).show();
            });
            //map
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            assert mapFragment != null;
            mapFragment.getMapAsync(AboutUsActivity.this);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Something missing", Toast.LENGTH_SHORT).show();
        }

    }

    public Intent newInstagramProfileIntent(String url) {
        final Intent intent = new Intent(Intent.ACTION_VIEW);
        PackageManager packageManager = getPackageManager();
        try {
            if (packageManager.getPackageInfo("com.instagram.android", 0) != null) {
                if (url.endsWith("/")) {
                    url = url.substring(0, url.length() - 1);
                }
                final String username = url.substring(url.lastIndexOf("/") + 1);
                intent.setData(Uri.parse("http://instagram.com/_u/" + username));
                intent.setPackage("com.instagram.android");
                return intent;
            }
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        intent.setData(Uri.parse(url));
        return intent;
    }

    public String getFacebookPageURL(String url) {
        PackageManager packageManager = getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                return "fb://facewebmodal/f?href=" + url;
            } else { //older versions of fb app
                String[] pageId = url.split("/");
                return "fb://page/" + pageId[pageId.length-1];
            }
        } catch (PackageManager.NameNotFoundException e) {
            return url; //normal web url
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Add a marker in Sydney and move the camera
        if (aboutMapLatitude != null && aboutMapLongitude != null && !aboutMapLatitude.equals("null") && !aboutMapLongitude.equals("null")) {
            final double latitude = Double.parseDouble(aboutMapLatitude);
            final double longitude = Double.parseDouble(aboutMapLongitude);
            LatLng kps = new LatLng(latitude, longitude);
            googleMap.addMarker(new MarkerOptions().position(kps).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)).title(aboutName));
            float zoomLevel = 16.0f; //This goes up to 21
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(kps, zoomLevel));
            googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            googleMap.setOnMapClickListener(latLng -> {
                String uri = String.format(Locale.ENGLISH, "geo:%f,%f", latitude, longitude);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(intent);
            });

            tv_gtm.setOnClickListener(view -> {
                String geoUri = "http://maps.google.com/maps?q=loc:" + latitude + "," + longitude + " (" + aboutName + ")";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(geoUri));
                startActivity(intent);
            });

            googleMap.getUiSettings().setScrollGesturesEnabled(false);
            googleMap.getUiSettings().setRotateGesturesEnabled(false);
            googleMap.getUiSettings().setScrollGesturesEnabled(false);
            googleMap.getUiSettings().setRotateGesturesEnabled(false);
            googleMap.getUiSettings().setZoomControlsEnabled(false);
            googleMap.getUiSettings().setAllGesturesEnabled(false);

            googleMap.setOnMapClickListener(null);
            googleMap.setOnMapClickListener(null);
        } else {
            mapLayout.setVisibility(View.GONE);
        }

    }

    public final boolean isInternetOn() {
        ConnectivityManager connec =
                (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        assert connec != null;
        if (Objects.requireNonNull(connec.getNetworkInfo(0)).getState() == android.net.NetworkInfo.State.CONNECTED ||
                Objects.requireNonNull(connec.getNetworkInfo(0)).getState() == android.net.NetworkInfo.State.CONNECTING ||
                Objects.requireNonNull(connec.getNetworkInfo(1)).getState() == android.net.NetworkInfo.State.CONNECTING ||
                Objects.requireNonNull(connec.getNetworkInfo(1)).getState() == android.net.NetworkInfo.State.CONNECTED) {
            return true;
        } else if (
                Objects.requireNonNull(connec.getNetworkInfo(0)).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                        Objects.requireNonNull(connec.getNetworkInfo(1)).getState() == android.net.NetworkInfo.State.DISCONNECTED) {
            return false;
        }
        return false;
    }

    public void slider() {
        Hash_file_maps = new HashMap<>();
        image = aboutMoreImages.split(",");
        for (int i = 0; i < image.length; i++) {
            Hash_file_maps.put(1 + i + "" + "/" + image.length, sp.getString("imageUrl", "") + sp.getString("userSchoolId", "") + "/other/" + image[i].
                    replace("\"", "").
                    replace("[", "").
                    replace("]", ""));
        }

        for (String name : Hash_file_maps.keySet()) {
            DefaultSliderView textSliderView = new DefaultSliderView(AboutUsActivity.this);
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
        sliderLayout.startAutoCycle(1000, 5000, true);
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
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onSliderClick(BaseSliderView slider) {
        String aa = (String) slider.getBundle().get("extra");
        assert aa != null;
        String[] bb = aa.split("/");
        String cc = bb[0];
        int val = Integer.parseInt(cc);
        Intent i = new Intent(AboutUsActivity.this, FullScreenImageActivity.class);
        i.putExtra("leaveImg", sp.getString("imageUrl", "") + sp.getString("userSchoolId", "") + "/other/" + image[val - 1].
                replace("\"", "").
                replace("[", "").
                replace("]", ""));
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
}
