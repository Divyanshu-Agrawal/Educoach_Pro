package com.aaptrix.activitys.admin;

import com.aaptrix.activitys.student.GalleryActivity;
import com.aaptrix.adaptor.GridImageAdapter;
import com.aaptrix.R;
import com.aaptrix.tools.FileUtil;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.conn.ssl.SSLConnectionSocketFactory;
import cz.msebera.android.httpclient.conn.ssl.SSLContexts;
import cz.msebera.android.httpclient.entity.mime.HttpMultipartMode;
import cz.msebera.android.httpclient.entity.mime.MultipartEntityBuilder;
import cz.msebera.android.httpclient.entity.mime.content.FileBody;
import cz.msebera.android.httpclient.impl.client.HttpClients;
import cz.msebera.android.httpclient.util.EntityUtils;
import id.zelory.compressor.Compressor;
import pl.droidsonroids.gif.GifImageView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.ClipData;
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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.AppBarLayout;

import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import javax.net.ssl.SSLContext;

import static com.aaptrix.tools.HttpUrl.ADD_GALLERY;
import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREF_COLOR;
import static cz.msebera.android.httpclient.conn.ssl.SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER;

public class AddNewGallery extends AppCompatActivity {

    AppBarLayout appBarLayout;
    String selToolColor, selStatusColor, selTextColor1;
    TextView tool_title;
    Button save;
    EditText title, date;
    String userId, userSchoolId, userRoleId, userSection, userrType;
    ArrayList<File> imageArray = new ArrayList<>();
    String file_extn;
    ArrayList<String> filepath = new ArrayList<>();
    ArrayList<Uri> image = new ArrayList<>();
    GridView gridView;
    ImageView imageView;
    Uri addImageUri = Uri.parse("android.resource://com.aaptrix/drawable/add_image");
    InputStream stream;
    Intent intent;
    RelativeLayout layout;
    ProgressBar progressBar;
    MediaPlayer mp;
    CardView cardView;
    GifImageView taskStatus;
    CheckBox showToGuest;
    String strShowToGuest = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_new_gallery);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        appBarLayout = findViewById(R.id.appBarLayout);
        tool_title = findViewById(R.id.tool_title);
        save = findViewById(R.id.save_btn);
        gridView = findViewById(R.id.preview_grid);
        imageView = findViewById(R.id.add_select);
        date = findViewById(R.id.add_gallery_date);
        showToGuest = findViewById(R.id.show_to_guest);
        title = findViewById(R.id.add_gallery_title);
        progressBar = findViewById(R.id.loader);
        layout = findViewById(R.id.relative_layout);
        mp = MediaPlayer.create(this, R.raw.button_click);
        taskStatus = findViewById(R.id.task_status);
        cardView = findViewById(R.id.card_view);
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        userId = settings.getString("userID", "");
        userSchoolId = settings.getString("str_school_id", "");
        userRoleId = settings.getString("str_role_id", "");
        userSection = settings.getString("userSection", "");
        userrType = settings.getString("userrType", "");

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
        save.setBackgroundColor(Color.parseColor(selToolColor));
        save.setTextColor(Color.parseColor(selTextColor1));

        try {
            stream = getContentResolver().openInputStream(addImageUri);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        showToGuest.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked)
                strShowToGuest = "1";
            else
                strShowToGuest = "0";
        });

        title.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                hideKeyboard(v);
            }
        });

        layout.setOnClickListener(v -> {

        });

        layout.setOnTouchListener((v, event) -> false);

        imageView.setOnClickListener(v -> {
            if (PermissionChecker.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_GRANTED) {
                Intent photoPickerIntent = new Intent();
                photoPickerIntent.setType("image/*");
                photoPickerIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                photoPickerIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(photoPickerIntent, 1);
            } else {
                isPermissionGranted();
            }
        });

        date.setOnClickListener(v -> {
            final Calendar mcurrentDate = Calendar.getInstance();
            int mYear = mcurrentDate.get(Calendar.YEAR);
            int mMonth = mcurrentDate.get(Calendar.MONTH);
            int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);
            DatePickerDialog mDatePicker = new DatePickerDialog(this, R.style.AlertDialogCustom1, (datepicker, selectedyear, selectedmonth, selectedday) -> {
                mcurrentDate.set(Calendar.YEAR, selectedyear);
                mcurrentDate.set(Calendar.MONTH, selectedmonth);
                mcurrentDate.set(Calendar.DAY_OF_MONTH, selectedday);
                SimpleDateFormat sdf = new SimpleDateFormat(getResources().getString(R.string.date_card_formate_dairy5), Locale.US);
                date.setText(sdf.format(mcurrentDate.getTime()));
            }, mYear, mMonth, mDay);
            mDatePicker.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            mDatePicker.show();
        });

        gridView.setOnItemClickListener((parent, view, position, id) -> {
            image.remove(addImageUri);
            if (PermissionChecker.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_GRANTED) {
                Intent photoPickerIntent = new Intent();
                photoPickerIntent.setType("image/*");
                photoPickerIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                photoPickerIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(photoPickerIntent, 1);
            } else {
                isPermissionGranted();
            }
        });

        save.setOnClickListener(v -> {
            mp.start();
            if (title.getText().toString().isEmpty()) {
                title.setError("Please Enter Title");
                title.requestFocus();
            } else if (date.getText().toString().isEmpty()) {
                date.setError("Please Select Date");
                date.requestFocus();
            } else if (imageArray.size() > 0) {
                layout.setVisibility(View.VISIBLE);
                layout.bringToFront();
                getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                        WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                UploadImage uploadImage = new UploadImage(this, imageArray, this);
                uploadImage.execute(title.getText().toString(), userSchoolId, userId, date.getText().toString(), userrType);
            } else {
                Toast.makeText(this, "Please Select Image", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mp.release();
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1)
            if (resultCode == Activity.RESULT_OK) {
                ClipData clipData = data.getClipData();
                if (clipData != null) {
                    if (filepath.size() > 12) {
                        Toast.makeText(this, "Please select upto only 10 images", Toast.LENGTH_SHORT).show();
                    } else {
                        for (int i = 0; i < clipData.getItemCount(); i++) {
                            image.add(clipData.getItemAt(i).getUri());
                        }
                        for (int i = 0; i < image.size(); i++) {
                            filepath.add(FileUtil.getFileName(this, image.get(i)));
                            file_extn = filepath.get(i).substring(filepath.get(i).lastIndexOf(".") + 1);
                            try {
                                if (file_extn.equals("jpg") || file_extn.equals("jpeg") || file_extn.equals("png")) {
                                    imageArray.add(new Compressor(this)
                                            .setMaxWidth(1280)
                                            .setMaxHeight(720)
                                            .setQuality(25)
                                            .setCompressFormat(Bitmap.CompressFormat.JPEG)
                                            .compressToFile(FileUtil.from(this, image.get(i))));
                                } else {
                                    FileNotFoundException fe = new FileNotFoundException();
                                    Toast.makeText(this, "File not in required format.", Toast.LENGTH_SHORT).show();
                                    throw fe;
                                }
                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                        if (image.size() != 12) {
                            image.add(addImageUri);
                        }
                        GridImageAdapter addAdapter = new GridImageAdapter(this, R.layout.image_add_grid, image);
                        gridView.setAdapter(addAdapter);
                        imageView.setVisibility(View.GONE);
                        addAdapter.notifyDataSetChanged();
                    }
                } else {
                    image.add(data.getData());
                    for (int i = 0; i < image.size(); i++) {
                        filepath.add(FileUtil.getFileName(this, image.get(i)));
                        file_extn = filepath.get(i).substring(filepath.get(i).lastIndexOf(".") + 1);
                        try {
                            if (file_extn.equals("jpg") || file_extn.equals("jpeg") || file_extn.equals("png")) {
                                imageArray.add(new Compressor(this)
                                        .setMaxWidth(1280)
                                        .setMaxHeight(720)
                                        .setQuality(25)
                                        .setCompressFormat(Bitmap.CompressFormat.JPEG)
                                        .compressToFile(FileUtil.from(this, image.get(i))));
                            } else {
                                FileNotFoundException fe = new FileNotFoundException();
                                Toast.makeText(this, "File not in required format.", Toast.LENGTH_SHORT).show();
                                throw fe;
                            }
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    if (image.size() != 12) {
                        image.add(addImageUri);
                    }
                    GridImageAdapter addAdapter = new GridImageAdapter(this, R.layout.image_add_grid, image);
                    gridView.setAdapter(addAdapter);
                    imageView.setVisibility(View.GONE);
                    addAdapter.notifyDataSetChanged();
                }
            }
    }

    public void isPermissionGranted() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        assert inputMethodManager != null;
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
    class UploadImage extends AsyncTask<String, String, String> {

        Context ctx;
        ArrayList<File> imagePaths;
        Activity activity;
        ArrayList<String> imageNames = new ArrayList<>();

        UploadImage(Context ctx, ArrayList<File> imagePaths, Activity activity) {
            this.ctx = ctx;
            this.imagePaths = imagePaths;
            this.activity = activity;
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            progressBar.bringToFront();
            Toast.makeText(ctx, "Please wait we are adding images", Toast.LENGTH_SHORT).show();
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {

            String title = params[0];
            String schoolId = params[1];
            String userId = params[2];
            String date = params[3];
            String userType = params[4];
            String notiImage = "0";

            for (int i = 0; i < imagePaths.size(); i++) {
                try {
                    SSLContext sslContext = SSLContexts.custom().useTLS().build();
                    SSLConnectionSocketFactory f = new SSLConnectionSocketFactory(
                            sslContext,
                            new String[]{"TLSv1.1", "TLSv1.2"},
                            null,
                            BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
                    HttpClient httpclient = HttpClients.custom().setSSLSocketFactory(f).build();
                    HttpPost httppost = new HttpPost(ADD_GALLERY);
                    MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
                    entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                    FileBody image = new FileBody(imagePaths.get(i));
                    entityBuilder.addPart("image", image);
                    entityBuilder.addTextBody("schoolId", schoolId);
                    HttpEntity entity = entityBuilder.build();
                    httppost.setEntity(entity);
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity httpEntity = response.getEntity();
                    String result = EntityUtils.toString(httpEntity);
                    JSONObject jsonObject = new JSONObject(result);
                    imageNames.add("\"" + jsonObject.getString("imageNm") + "\"");
                    if (i == 0)
                        notiImage = jsonObject.getString("imageNm");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            Log.e("result", imageNames.toString());
            try {
                SSLContext sslContext = SSLContexts.custom().useTLS().build();
                SSLConnectionSocketFactory f = new SSLConnectionSocketFactory(
                        sslContext,
                        new String[]{"TLSv1.1", "TLSv1.2"},
                        null,
                        BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
                HttpClient httpclient = HttpClients.custom().setSSLSocketFactory(f).build();
                HttpPost httppost = new HttpPost(ADD_GALLERY);
                MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
                entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                entityBuilder.addTextBody("str_img", imageNames.toString().replace(" ", ""));
                entityBuilder.addTextBody("str_title", title);
                entityBuilder.addTextBody("schoolId", schoolId);
                entityBuilder.addTextBody("str_date", date);
                entityBuilder.addTextBody("userId", userId);
                entityBuilder.addTextBody("show_to_guest", strShowToGuest);
                entityBuilder.addTextBody("userType", userType);
                entityBuilder.addTextBody("noti_image", notiImage);
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
            Log.e("response", result);
            progressBar.setVisibility(View.GONE);
            if (result.contains("submitted")) {
                cardView.setVisibility(View.VISIBLE);
                new CountDownTimer(4000, 1000) {
                    @Override
                    public void onTick(long millisUntilFinished) {

                    }

                    @Override
                    public void onFinish() {
                        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                        startActivity(new Intent(ctx, GalleryActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish();
                    }
                }.start();
            } else {
                layout.setVisibility(View.GONE);
                Toast.makeText(ctx, "Error Occured", Toast.LENGTH_SHORT).show();
            }

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
    }
}
