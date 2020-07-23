package com.aaptrix.activitys;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.aaptrix.R;
import com.aaptrix.activitys.student.InstituteBuzzActivity;
import com.aaptrix.tools.FileUtil;
import com.google.android.material.appbar.AppBarLayout;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
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
import id.zelory.compressor.Compressor;
import pl.droidsonroids.gif.GifImageView;

import static com.aaptrix.activitys.SplashScreen.SCHOOL_ID;
import static com.aaptrix.tools.HttpUrl.UPDATE_PROFILE;
import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREF_COLOR;
import static cz.msebera.android.httpclient.conn.ssl.SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER;

public class UpdateProfile extends AppCompatActivity {

    MediaPlayer mp;
    CardView cardView;
    GifImageView taskStatus;
    RelativeLayout layout;
    EditText userName, userEmail, userDob;
    Spinner userGender;
    Button save;
    Toolbar toolbar;
    AppBarLayout appBarLayout;
    String strDob = "0", strGender = "0";
    String strName, strEmail, userId;
    CircleImageView profile;
    String userrType, userImg, imgUrl;
    File image;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        toolbar = findViewById(R.id.toolbar);
        appBarLayout = findViewById(R.id.appBarLayout);
        setSupportActionBar(toolbar);
        setTitle("Update Profile");
        setResult(RESULT_OK);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mp = MediaPlayer.create(this, R.raw.button_click);
        cardView = findViewById(R.id.card_view);
        taskStatus = findViewById(R.id.task_status);
        layout = findViewById(R.id.layout);
        userName = findViewById(R.id.user_name);
        userDob = findViewById(R.id.user_dob);
        userEmail = findViewById(R.id.user_email);
        userGender = findViewById(R.id.user_gender);
        save = findViewById(R.id.btn_verify);
        profile = findViewById(R.id.user_profile);

        userEmail.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus)
                hideKeyboard(v);
        });

        userDob.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus)
                hideKeyboard(v);
        });

        userName.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus)
                hideKeyboard(v);
        });

        layout.setOnClickListener(v -> {

        });

        layout.setOnTouchListener((v, event) -> false);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        strName = settings.getString("userName", "");
        strEmail = settings.getString("userEmailId", "");
        strDob = settings.getString("userDob", "");
        strGender = settings.getString("userGender", "");
        userId = settings.getString("userID", "");
        userrType = settings.getString("userrType", "");
        userImg = settings.getString("userImg", "");
        imgUrl = settings.getString("imageUrl", "");

        userName.setText(strName);
        userEmail.setText(strEmail);

        userDob.setText(strDob);

        if (userImg.equals("0")) {
            profile.setImageDrawable(getResources().getDrawable(R.drawable.user_place_hoder));
        } else if (!TextUtils.isEmpty(userImg)) {
            String url;
            switch (userrType) {
                case "Parent":
                case "Student":
                    url = imgUrl + settings.getString("userSchoolId", "") + "/users/students/profile/" + userImg;
                    Picasso.with(this).load(url).error(R.drawable.user_place_hoder).into(profile);
                    break;
                case "Admin":
                    url = imgUrl + settings.getString("userSchoolId", "") + "/users/admin/profile/" + userImg;
                    Picasso.with(this).load(url).error(R.drawable.user_place_hoder).into(profile);
                    break;
                case "Staff":
                    url = imgUrl + settings.getString("userSchoolId", "") + "/users/staff/profile/" + userImg;
                    Picasso.with(this).load(url).error(R.drawable.user_place_hoder).into(profile);
                    break;
                case "Teacher":
                    url = imgUrl + settings.getString("userSchoolId", "") + "/users/teachers/profile/" + userImg;
                    Picasso.with(this).load(url).error(R.drawable.user_place_hoder).into(profile);
                    break;
            }
        } else {
            profile.setImageDrawable(getResources().getDrawable(R.drawable.user_place_hoder));
        }

        String[] gender = {"Select Gender", "Male", "Female", "Other"};

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, R.layout.spinner_list_item1, gender);
        dataAdapter.setDropDownViewResource(R.layout.spinner_list_item1);
        userGender.setAdapter(dataAdapter);

        switch (strGender) {
            case "male":
                userGender.setSelection(1);
                break;
            case "female":
                userGender.setSelection(2);
                break;
        }

        userGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) adapterView.getChildAt(0)).setTextColor(getResources().getColor(R.color.black));
                switch (i) {
                    case 1:
                        strGender = "male";
                        break;
                    case 2:
                        strGender = "female";
                        break;
                    default:
                        strGender = "0";
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        profile.setOnClickListener(v -> {
            if (PermissionChecker.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_GRANTED) {
                Intent photoPickerIntent = new Intent();
                photoPickerIntent.setType("image/*");
                photoPickerIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(photoPickerIntent, 1);
            } else {
                isPermissionGranted();
            }
        });

        userDob.setOnClickListener(v -> {
            final Calendar mcurrentDate = Calendar.getInstance();
            int mYear = mcurrentDate.get(Calendar.YEAR);
            int mMonth = mcurrentDate.get(Calendar.MONTH);
            int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog mDatePicker = new DatePickerDialog(
                    this, R.style.AlertDialogCustom1, (datepicker, selectedyear, selectedmonth, selectedday) -> {
                mcurrentDate.set(Calendar.YEAR, selectedyear);
                mcurrentDate.set(Calendar.MONTH, selectedmonth);
                mcurrentDate.set(Calendar.DAY_OF_MONTH, selectedday);
                SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                userDob.setText(sdf.format(mcurrentDate.getTime()));
                sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                strDob = sdf.format(mcurrentDate.getTime());
            }, mYear, mMonth, mDay);
            if (strDob != null && !strDob.equals("0") && !strDob.equals("null")) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                Calendar calendar = Calendar.getInstance();
                try {
                    calendar.setTime(Objects.requireNonNull(sdf.parse(strDob)));
                    mDatePicker.updateDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            mDatePicker.show();
        });

        save.setOnClickListener(v -> {
            mp.start();
            if (TextUtils.isEmpty(userName.getText().toString())) {
                userName.requestFocus();
                userName.setError("Please Enter Name");
            } else {
                layout.setVisibility(View.VISIBLE);
                layout.bringToFront();
                SendRequest sendRequest = new SendRequest(this);
                sendRequest.execute(userName.getText().toString(), userEmail.getText().toString());
            }
        });

        SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
        String selToolColor = settingsColor.getString("tool", "");
        String selStatusColor = settingsColor.getString("status", "");
        String selTextColor1 = settingsColor.getString("text1", "");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(selStatusColor));
        }

        appBarLayout.setBackgroundColor(Color.parseColor(selToolColor));
        save.setBackgroundColor(Color.parseColor(selToolColor));
        save.setTextColor(Color.parseColor(selTextColor1));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1)
            if (resultCode == Activity.RESULT_OK) {
                if (data != null && data.getData() != null) {
                    String fileName = FileUtil.getFileName(this, data.getData());
                    String file_extn = fileName.substring(fileName.lastIndexOf(".") + 1);
                    try {
                        if (file_extn.equals("jpg") || file_extn.equals("jpeg") || file_extn.equals("png")) {
                            image = new Compressor(this)
                                    .setQuality(75)
                                    .setCompressFormat(Bitmap.CompressFormat.JPEG)
                                    .compressToFile(FileUtil.from(this, data.getData()));
                            Picasso.with(this).load(image).into(profile);
                        } else {
                            FileNotFoundException fe = new FileNotFoundException();
                            Toast.makeText(this, "File not in required format.", Toast.LENGTH_SHORT).show();
                            throw fe;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(this, "Unknown Error Occurred", Toast.LENGTH_SHORT).show();
                }
            }
    }

    public void isPermissionGranted() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "Permission granted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class SendRequest extends AsyncTask<String, String, String> {
        Context ctx;

        String username = userName.getText().toString();
        String useremail = userEmail.getText().toString();

        SendRequest(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            Toast.makeText(ctx, "Please wait, sending request", Toast.LENGTH_SHORT).show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            try {
                SSLContext sslContext = SSLContexts.custom().useTLS().build();
                SSLConnectionSocketFactory f = new SSLConnectionSocketFactory(
                        sslContext,
                        new String[]{"TLSv1.1", "TLSv1.2"},
                        null,
                        BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
                HttpClient httpclient = HttpClients.custom().setSSLSocketFactory(f).build();
                HttpPost httppost = new HttpPost(UPDATE_PROFILE);
                MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
                entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                entityBuilder.addTextBody("tbl_school_id", SCHOOL_ID);
                entityBuilder.addTextBody("tbl_users_name", username);
                entityBuilder.addTextBody("tbl_users_email", useremail);
                entityBuilder.addTextBody("tbl_users_gender", strGender);
                entityBuilder.addTextBody("tbl_users_dob", strDob);
                entityBuilder.addTextBody("tbl_users_id", userId);
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
            Log.e("ADDED", "" + result);
            try {
                JSONObject jsonObject = new JSONObject(result);
                String msg = jsonObject.getString("msg");
                String img = jsonObject.getString("tbl_users_img");
                if (msg.contains("user information successfully updated!")) {
                    cardView.setVisibility(View.VISIBLE);
                    new CountDownTimer(4000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {

                        }

                        @Override
                        public void onFinish() {
                            SharedPreferences.Editor sp = getSharedPreferences(PREFS_NAME, 0).edit();
                            sp.putString("userName", username);
                            sp.putString("userEmailId", useremail);
                            sp.putString("userDob", strDob);
                            sp.putString("userGender", strGender);
                            if (image != null) {
                                sp.putString("userImg", img);
                            }
                            sp.apply();
                            Intent intent = new Intent(ctx, UserProfile.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(intent);
                            finish();
                        }
                    }.start();
                } else {
                    Toast.makeText(ctx, "Issue in server", Toast.LENGTH_SHORT).show();
                    layout.setVisibility(View.GONE);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            super.onPostExecute(result);
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mp.release();
    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        assert inputMethodManager != null;
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}