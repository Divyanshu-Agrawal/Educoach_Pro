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
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.aaptrix.R;
import com.aaptrix.tools.FileUtil;
import com.google.android.material.appbar.AppBarLayout;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
import cz.msebera.android.httpclient.entity.mime.content.FileBody;
import cz.msebera.android.httpclient.impl.client.HttpClients;
import cz.msebera.android.httpclient.util.EntityUtils;
import id.zelory.compressor.Compressor;
import pl.droidsonroids.gif.GifImageView;

import static com.aaptrix.activitys.SplashScreen.SCHOOL_ID;
import static com.aaptrix.tools.HttpUrl.ALL_BATCHS;
import static com.aaptrix.tools.HttpUrl.PV;
import static com.aaptrix.tools.HttpUrl.TC;
import static com.aaptrix.tools.HttpUrl.USER_REGISTRATION;
import static com.aaptrix.tools.SPClass.PREF_COLOR;
import static cz.msebera.android.httpclient.conn.ssl.SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER;

public class UserSelfRegistration extends AppCompatActivity {

    MediaPlayer mp;
    CardView cardView;
    GifImageView taskStatus;
    RelativeLayout layout;
    EditText userName, userPhone, userRoll, userEmail, userDob, parentName, parentNumber, parentOccupation, referral;
    Spinner userGender, userBatch;
    Button register;
    File image = null;
    Toolbar toolbar;
    AppBarLayout appBarLayout;
    String[] batch_array = {"Select Batch"};
    String selBatch = "Select Batch";
    String strDob = "0", strGender = "0";
    ImageView attachment;
    TextView ts, pv;
    CheckBox cb;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_self_registration);
        toolbar = findViewById(R.id.toolbar);
        appBarLayout = findViewById(R.id.appBarLayout);
        setSupportActionBar(toolbar);
        setTitle("Register");
        setResult(RESULT_OK);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        mp = MediaPlayer.create(this, R.raw.button_click);
        cardView = findViewById(R.id.card_view);
        taskStatus = findViewById(R.id.task_status);
        layout = findViewById(R.id.layout);
        userName = findViewById(R.id.user_name);
        userPhone = findViewById(R.id.user_phone);
        userRoll = findViewById(R.id.user_roll_no);
        register = findViewById(R.id.btn_verify);
        userDob = findViewById(R.id.user_dob);
        userEmail = findViewById(R.id.user_email);
        attachment = findViewById(R.id.attachment);
        userGender = findViewById(R.id.user_gender);
        userBatch = findViewById(R.id.user_batch);
        parentName = findViewById(R.id.parent_name);
        parentNumber = findViewById(R.id.parent_number);
        parentOccupation = findViewById(R.id.parent_occupation);
        referral = findViewById(R.id.referral_code);
        ts = findViewById(R.id.ts);
        cb = findViewById(R.id.cb);
        pv = findViewById(R.id.pv);

        userRoll.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus)
                hideKeyboard(v);
        });

        userPhone.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus)
                hideKeyboard(v);
        });

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

        ts.setOnClickListener(view -> {
            Intent i = new Intent(this, TermsConditionsActivity.class);
            i.putExtra("url", TC);
            i.putExtra("tool_title", "Terms and Conditions");
            startActivity(i);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        pv.setOnClickListener(view -> {
            Intent i = new Intent(this, TermsConditionsActivity.class);
            i.putExtra("url", PV);
            i.putExtra("tool_title", "Privacy Policy");
            startActivity(i);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        GetAllBatches b1 = new GetAllBatches(this);
        b1.execute();

        String[] gender = {"Select Gender", "Male", "Female", "Other"};

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, R.layout.spinner_list_item1, gender);
        dataAdapter.setDropDownViewResource(R.layout.spinner_list_item1);
        userGender.setAdapter(dataAdapter);

        userGender.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) adapterView.getChildAt(0)).setTextColor(getResources().getColor(R.color.black));
                switch (i) {
                    case 1:
                        strGender = "m";
                        break;
                    case 2:
                        strGender = "f";
                        break;
                    case 3:
                        strGender = "not specified";
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

        attachment.setOnClickListener(v -> {
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
            mDatePicker.getDatePicker().setMaxDate(System.currentTimeMillis());
            mDatePicker.show();
        });

        register.setOnClickListener(v -> {
            mp.start();
            if (TextUtils.isEmpty(userName.getText().toString())) {
                userName.requestFocus();
                userName.setError("Please Enter Name");
            } else if (TextUtils.isEmpty(userPhone.getText().toString())) {
                userPhone.requestFocus();
                userPhone.setError("Please Enter Phone Number");
            } else if (userPhone.getText().toString().length() != 10) {
                userPhone.requestFocus();
                userPhone.setError("Please Enter Correct Phone Number");
            } else if (!cb.isChecked()) {
                Toast.makeText(this, "Please agree to our Terms and Conditions", Toast.LENGTH_SHORT).show();
            } else {
                layout.setVisibility(View.VISIBLE);
                layout.bringToFront();
                SendRequest sendRequest = new SendRequest(this);
                sendRequest.execute();
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
        register.setBackgroundColor(Color.parseColor(selToolColor));
        register.setTextColor(Color.parseColor(selTextColor1));
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1)
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    String fileName = FileUtil.getFileName(this, data.getData());
                    String file_extn = fileName.substring(fileName.lastIndexOf(".") + 1);
                    try {
                        if (file_extn.equals("jpg") || file_extn.equals("jpeg") || file_extn.equals("png")) {
                            image = new Compressor(this)
                                    .setQuality(25)
                                    .setCompressFormat(Bitmap.CompressFormat.JPEG)
                                    .compressToFile(FileUtil.from(this, data.getData()));
                        } else {
                            FileNotFoundException fe = new FileNotFoundException();
                            Toast.makeText(this, "File not in required format.", Toast.LENGTH_SHORT).show();
                            throw fe;
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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
    public class GetAllBatches extends AsyncTask<String, String, String> {
        Context ctx;

        GetAllBatches(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String data;

            try {
                URL url = new URL(ALL_BATCHS);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                data = URLEncoder.encode("school_id", "UTF-8") + "=" + URLEncoder.encode(SCHOOL_ID, "UTF-8") + "&" +
                        URLEncoder.encode("tbl_users_id", "UTF-8") + "=" + URLEncoder.encode("0", "UTF-8");
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

        @Override
        protected void onPostExecute(String result) {
            if (!result.equals("{\"result\":null}")) {
                try {
                    JSONObject jo = new JSONObject(result);
                    JSONArray ja = jo.getJSONArray("result");
                    batch_array = new String[ja.length() + 1];
                    batch_array[0] = "Select Batch";
                    for (int i = 0; i < ja.length(); i++) {
                        jo = ja.getJSONObject(i);
                        batch_array[i + 1] = jo.getString("tbl_batch_name");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setBatch();
            } else {
                String[] batch_array = {"Select Batch"};
                ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<>(ctx, R.layout.spinner_list_item1, batch_array);
                dataAdapter1.setDropDownViewResource(R.layout.spinner_list_item1);
                userBatch.setAdapter(dataAdapter1);
                Toast.makeText(ctx, "No Batch", Toast.LENGTH_SHORT).show();
            }
            super.onPostExecute(result);
        }

    }

    private void setBatch() {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, R.layout.spinner_list_item1, batch_array);
        dataAdapter.setDropDownViewResource(R.layout.spinner_list_item1);
        userBatch.setAdapter(dataAdapter);

        userBatch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                ((TextView) adapterView.getChildAt(0)).setTextColor(getResources().getColor(R.color.text_gray));
                selBatch = batch_array[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    public class SendRequest extends AsyncTask<String, String, String> {
        Context ctx;

        String username = userName.getText().toString();
        String userphone = userPhone.getText().toString();
        String userroll = userRoll.getText().toString();
        String useremail = userEmail.getText().toString();
        String parentnum = parentNumber.getText().toString();
        String parentname = parentName.getText().toString();
        String occupation = parentOccupation.getText().toString();
        String token = FirebaseInstanceId.getInstance().getToken();
        String ref = referral.getText().toString();

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
                HttpPost httppost = new HttpPost(USER_REGISTRATION);
                MultipartEntityBuilder entityBuilder = MultipartEntityBuilder.create();
                entityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                if (image != null) {
                    FileBody fileBody = new FileBody(image);
                    entityBuilder.addPart("attachment", fileBody);
                }
                entityBuilder.addTextBody("tbl_school_id", SCHOOL_ID);
                entityBuilder.addTextBody("tbl_users_name", username);
                entityBuilder.addTextBody("tbl_users_phone", userphone);
                entityBuilder.addTextBody("sch_unique_id", userroll);
                entityBuilder.addTextBody("tbl_users_email", useremail);
                entityBuilder.addTextBody("tbl_users_gender", strGender);
                entityBuilder.addTextBody("tbl_users_dob", strDob);
                entityBuilder.addTextBody("app_token", token);
                entityBuilder.addTextBody("tbl_users_parents_no", parentnum);
                entityBuilder.addTextBody("tbl_users_parents_name", parentname);
                entityBuilder.addTextBody("tbl_users_parents_occupation", occupation);
                entityBuilder.addTextBody("invited_user_referral_code", ref);
                if (!selBatch.equals("Select Batch")) {
                    entityBuilder.addTextBody("tbl_batch_nm", selBatch);
                } else {
                    entityBuilder.addTextBody("tbl_batch_nm", "");
                }
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
                String name = jsonObject.getString("user_nm");
                switch (msg) {
                    case "submitted":
                        cardView.setVisibility(View.VISIBLE);
                        new CountDownTimer(4000, 1000) {
                            @Override
                            public void onTick(long millisUntilFinished) {

                            }

                            @Override
                            public void onFinish() {
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                onBackPressed();
                            }
                        }.start();
                        break;

                    case "Enrollment number already exist":
                    case "Mobile number already exist":
                        Toast.makeText(ctx, msg + " for " + name, Toast.LENGTH_SHORT).show();
                        layout.setVisibility(View.GONE);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        Intent i = new Intent(ctx, AppLogin.class);
                        i.putExtra("status", "Online");
                        startActivity(i);
                        finish();
                        break;
                    default:
                        Toast.makeText(ctx, "Issue in server", Toast.LENGTH_SHORT).show();
                        layout.setVisibility(View.GONE);
                        break;
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
