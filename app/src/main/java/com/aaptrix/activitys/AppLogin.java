package com.aaptrix.activitys;

import com.aaptrix.BuildConfig;
import com.aaptrix.activitys.admin.InstituteBuzzActivityDiff;
import com.aaptrix.activitys.student.InstituteBuzzActivity;
import com.aaptrix.databeans.DataBeanStudent;
import com.aaptrix.adaptor.UserListAdaptor;
import com.aaptrix.R;
import com.uxcam.UXCam;

import androidx.appcompat.app.AppCompatActivity;

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

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import javax.net.ssl.SSLContext;

import static com.aaptrix.activitys.SplashScreen.SCHOOL_ID;
import static com.aaptrix.tools.HttpUrl.LOGIN_URL;
import static com.aaptrix.tools.HttpUrl.PV;
import static com.aaptrix.tools.HttpUrl.REGISTER_URL;
import static com.aaptrix.tools.HttpUrl.TC;
import static com.aaptrix.tools.HttpUrl.VERIFY_PHONE;
import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREFS_USER;
import static com.aaptrix.tools.SPClass.PREF_COLOR;
import static com.aaptrix.tools.SPClass.PREF_ROLE;
import static cz.msebera.android.httpclient.conn.ssl.SSLConnectionSocketFactory.BROWSER_COMPATIBLE_HOSTNAME_VERIFIER;

public class AppLogin extends AppCompatActivity {

    RelativeLayout initialLayout, loginLayout, registerLayout;
    ProgressBar progressBar;
    EditText login, password, registerPassword, confirmPassword;
    Button proceedBtn, loginBtn, forgotBtn, registerBtn;
    String str_user_phone, str_user_password;
    String userID, userLoginId, userName, userPhone, userEmailId, userDob, userGender, userImg, userPhoneStatus, userrType, userPassword;
    String userID1, userLoginId1, userName1, userPhone1, userEmailId1, userDob1, userGender1, userImg1, userPhoneStatus1, userrType1, userPassword1;
    String userSchoolId, userSchoolName, userSchoolRoleId, userSchoolRoleName, userSchoolSchoolLogo, userSchoolSchoolLogo1, userSchoolSchoolLogo3;
    String userSchoolId1, userSchoolName1, userSchoolRoleId1, userSchoolRoleName1, userSchoolSchoolLogo11, userSchoolSchoolLogo12, userSchoolSchoolLogo13;
    String str_section, str_roll_number, str_teacher_name, imageUrl;
    String str_section1, str_roll_number1, str_teacher_name1;
    AlertDialog.Builder alert;
    ArrayList<DataBeanStudent> studentArray = new ArrayList<>();
    DataBeanStudent dbs;
    AlertDialog.Builder alertdialog;
    ListView user_list;
    UserListAdaptor userListAdaptor;
    AlertDialog alertDialog;
    String userRole = "Other User";
    String selToolColor, selDrawerColor, selStatusColor, selTextColor1, selTextColor2;
    String selToolColor1, selDrawerColor1, selStatusColor1, selTextColor11, selTextColor22;
    SharedPreferences settings;
    String Networkstatus, status, userType;
    private String type;
    CheckBox cb;
    AlertDialog dialog;
    TextView tc, ts, pv, version;
    MediaPlayer mp;
    TextView troubleProceed, troubleLogin, troubleRegister;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_login);
        initialLayout = findViewById(R.id.initial_layout);
        loginLayout = findViewById(R.id.login_layout);
        registerLayout = findViewById(R.id.register_layout);
        progressBar = findViewById(R.id.loader);
        login = findViewById(R.id.et_login_phone);
        password = findViewById(R.id.et_login_password);
        tc = findViewById(R.id.tc);
        ts = findViewById(R.id.ts);
        pv = findViewById(R.id.pv);
        cb = findViewById(R.id.cb);
        version = findViewById(R.id.version);
        version.setText("Version " + BuildConfig.VERSION_NAME);
        registerPassword = findViewById(R.id.et_new_password);
        confirmPassword = findViewById(R.id.et_confirm_password);
        mp = MediaPlayer.create(this, R.raw.button_click);
        troubleLogin = findViewById(R.id.trouble_login);
        troubleProceed = findViewById(R.id.trouble_proceed);
        troubleRegister = findViewById(R.id.trouble_register);

        login.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus)
                hideKeyboard(v);
        });

        password.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus)
                hideKeyboard(v);
        });

        registerPassword.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus)
                hideKeyboard(v);
        });

        confirmPassword.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus)
                hideKeyboard(v);
        });

        proceedBtn = findViewById(R.id.btn_proceed);
        loginBtn = findViewById(R.id.btn_login);
        forgotBtn = findViewById(R.id.btn_forgot_pass);
        registerBtn = findViewById(R.id.btn_verify);
        settings = getSharedPreferences(PREFS_NAME, 0);
        status = settings.getString("logged", "");
        userType = settings.getString("userrType", "");
        checkUserLogin();
    }

    private void checkUserLogin() {
        UXCam.setUserIdentity(settings.getString("userName", ""));
        progressBar.setVisibility(View.VISIBLE);
        try {
            Networkstatus = getIntent().getStringExtra("status");
        } catch (NullPointerException e) {
            Networkstatus = "Online";
        }
        if (Networkstatus != null && Networkstatus.equals("Online")) {
            if (status.equals("logged")) {
                if ("Student".equals(userType)) {
                    Intent i = new Intent(AppLogin.this, InstituteBuzzActivity.class);
                    startActivity(i);
                    finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                } else {
                    Intent i = new Intent(AppLogin.this, InstituteBuzzActivityDiff.class);
                    startActivity(i);
                    finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            } else {
                allMethods();
            }
        } else if (Networkstatus != null && Networkstatus.equals("Offline")) {
            if (status.equals("logged")) {
                if ("Student".equals(userType)) {
                    Intent i = new Intent(AppLogin.this, InstituteBuzzActivity.class);
                    startActivity(i);
                    finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                } else {
                    Intent i = new Intent(AppLogin.this, InstituteBuzzActivityDiff.class);
                    startActivity(i);
                    finish();
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                }
            } else {
                allMethods();
            }
        }
    }

    private void allMethods() {
        UXCam.pauseScreenRecording();
        progressBar.setVisibility(View.GONE);
        proceedBtn.setOnClickListener(view -> {
            mp.start();
            str_user_phone = login.getText().toString().trim();
            hideKeyboard(view);
            if (TextUtils.isEmpty(str_user_phone)) {
                Toast.makeText(AppLogin.this, "Please enter user phone number", Toast.LENGTH_SHORT).show();
            } else {
                if (isInternetOn()) {
                    progressBar.setVisibility(View.VISIBLE);
                    verifyNumber(str_user_phone);
                } else {
                    Toast.makeText(AppLogin.this, "No internet", Toast.LENGTH_SHORT).show();
                }
            }
        });

        troubleProceed.setOnClickListener(v -> {
            Intent intent = new Intent(this, TroubleFAQ.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

    }

    private void verifyNumber(final String registerPhoneNumber) {

        new Thread(() -> {
            try {
                SSLContext sslContext = SSLContexts.custom().useTLS().build();
                SSLConnectionSocketFactory f = new SSLConnectionSocketFactory(
                        sslContext,
                        new String[]{"TLSv1.1", "TLSv1.2"},
                        null,
                        BROWSER_COMPATIBLE_HOSTNAME_VERIFIER);
                HttpClient httpclient = HttpClients.custom().setSSLSocketFactory(f).build();
                HttpPost httppost = new HttpPost(VERIFY_PHONE);
                // Add your data
                List<NameValuePair> nameValuePairs = new ArrayList<>(2);
                nameValuePairs.add(new BasicNameValuePair("mob", registerPhoneNumber));
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));

                // Execute HTTP Post Request
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity httpEntity = response.getEntity();
                final String result = EntityUtils.toString(httpEntity);

                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(() -> {
                    try {
                        JSONObject jsonObject = new JSONObject(result);
                        Log.e("res", result);
                        if (result.contains("\"Users\":null")) {
                            JSONObject parent = jsonObject.getJSONObject("Parent");
                            userRole = "Parent";
                            switch (parent.getString("VerificationStatus")) {
                                case "0":
                                    registerUser();
                                    break;
                                case "1":
                                    loginUser(registerPhoneNumber);
                                    break;
                                case "2":
                                    new AlertDialog.Builder(AppLogin.this)
                                            .setMessage("This number is not registered with us. If you are already registered with the institute you can fill the request form here.")
                                            .setCancelable(false)
                                            .setPositiveButton("Register", (dialog1, which) -> {
                                                Intent intent = new Intent(this, UserSelfRegistration.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                                finish();
                                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                            })
                                            .setNegativeButton("Cancel", null)
                                            .show();
                                    progressBar.setVisibility(View.GONE);
                            }
                        } else if (result.contains("\"Parent\":null")) {
                            userRole = "Other User";
                            JSONObject user = jsonObject.getJSONObject("Users");
                            switch (user.getString("VerificationStatus")) {
                                case "0":
                                    registerUser();
                                    break;
                                case "1":
                                    loginUser(registerPhoneNumber);
                                    break;
                                case "2":
                                    new AlertDialog.Builder(AppLogin.this)
                                            .setMessage("This number is not registered with us. If you are already registered with the institute you can fill the request form here.")
                                            .setCancelable(false)
                                            .setPositiveButton("Register", (dialog1, which) -> {
                                                Intent intent = new Intent(this, UserSelfRegistration.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                                finish();
                                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                            })
                                            .setNegativeButton("Cancel", null)
                                            .show();
                                    progressBar.setVisibility(View.GONE);
                            }
                        } else if (result.contains("\"Users\":{\"success\":false,\"VerificationStatus\":\"2\"},\"Parent\":{\"success\":false,\"VerificationStatus\":\"2\"}")) {
                            new AlertDialog.Builder(AppLogin.this)
                                    .setMessage("This number is not registered with us. If you are already registered with the institute you can fill the request form here.")
                                    .setCancelable(false)
                                    .setPositiveButton("Ok", (dialog1, which) -> {
                                        Intent intent = new Intent(this, UserSelfRegistration.class);
                                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        finish();
                                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                    })
                                    .setNegativeButton("Cancel", null)
                                    .show();
                            progressBar.setVisibility(View.GONE);
                        } else {
                            parentLogin(result, registerPhoneNumber);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        cacheJson(e.toString());
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    private void cacheJson(final String data) {
        new Thread(() -> {
            ObjectOutput out;
            try {
                File directory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                directory.mkdir();
                out = new ObjectOutputStream(new FileOutputStream(new File(directory, "error_lpg.txt")));
                out.writeObject(data);
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @SuppressLint("InflateParams")
    private void parentLogin(String resJson, String phone) {

        View view = LayoutInflater.from(this).inflate(R.layout.select_user, null);

        RadioGroup radioGroup = view.findViewById(R.id.select_user);

        alertdialog = new AlertDialog.Builder(this, R.style.DialogTheme);

        radioGroup.setOnCheckedChangeListener((radioGroup1, i) -> {
            if (radioGroup1.getCheckedRadioButtonId() == R.id.select_student) {
                userRole = "Other User";
            } else {
                userRole = "Parent";
            }
        });

        alertdialog.setView(view)
                .setPositiveButton("Ok", (dialogInterface, i) -> {
                    try {
                        JSONObject jsonObject = new JSONObject(resJson);
                        if (userRole.equals("Other User")) {
                            JSONObject user = jsonObject.getJSONObject("Users");
                            switch (user.getString("VerificationStatus")) {
                                case "0":
                                    registerUser();
                                    break;
                                case "1":
                                    loginUser(phone);
                                    break;
                                case "2":
                                    new AlertDialog.Builder(AppLogin.this)
                                            .setMessage("This number is not registered with us. If you are already registered with the institute you can fill the request form here.")
                                            .setCancelable(false)
                                            .setPositiveButton("Register", (dialog1, which) -> {
                                                Intent intent = new Intent(this, UserSelfRegistration.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                                finish();
                                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                            })
                                            .setNegativeButton("Cancel", null)
                                            .show();
                                    progressBar.setVisibility(View.GONE);
                            }
                        } else {
                            JSONObject parent = jsonObject.getJSONObject("Parent");
                            switch (parent.getString("VerificationStatus")) {
                                case "0":
                                    registerUser();
                                    break;
                                case "1":
                                    loginUser(phone);
                                    break;
                                case "2":
                                    new AlertDialog.Builder(AppLogin.this)
                                            .setMessage("This number is not registered with us. If you are already registered with the institute you can fill the request form here.")
                                            .setCancelable(false)
                                            .setPositiveButton("Register", (dialog1, which) -> {
                                                Intent intent = new Intent(this, UserSelfRegistration.class);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                                finish();
                                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                            })
                                            .setNegativeButton("Cancel", null)
                                            .show();
                                    progressBar.setVisibility(View.GONE);
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                })
                .setNegativeButton("Cancel", (dialogInterface, i) -> {
                }).setCancelable(false);

        dialog = alertdialog.create();
        dialog.show();

    }

    public void registerUser() {
        type = "register";
        progressBar.setVisibility(View.GONE);
        initialLayout.setVisibility(View.GONE);
        registerLayout.setVisibility(View.VISIBLE);
        ts.setOnClickListener(view -> {
            Intent i = new Intent(AppLogin.this, TermsConditionsActivity.class);
            i.putExtra("url", TC);
            i.putExtra("tool_title", "Terms and Conditions");
            startActivity(i);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        pv.setOnClickListener(view -> {
            Intent i = new Intent(AppLogin.this, TermsConditionsActivity.class);
            i.putExtra("url", PV);
            i.putExtra("tool_title", "Privacy Policy");
            startActivity(i);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        troubleRegister.setOnClickListener(v -> {
            Intent intent = new Intent(this, TroubleFAQ.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        registerBtn.setOnClickListener(v -> {
            mp.start();
            hideKeyboard(v);
            str_user_password = registerPassword.getText().toString();
            if (TextUtils.isEmpty(str_user_password)) {
                registerPassword.setError("Please Enter Password");
                registerPassword.requestFocus();
            } else if (TextUtils.isEmpty(confirmPassword.getText().toString())) {
                confirmPassword.setError("Please Confirm Password");
                confirmPassword.requestFocus();
            } else if (!str_user_password.equals(confirmPassword.getText().toString())) {
                Toast.makeText(AppLogin.this, "Password do not match", Toast.LENGTH_SHORT).show();
                confirmPassword.getText().clear();
                registerPassword.getText().clear();
            } else if (cb.isChecked()) {
                RegisterUser registerUser = new RegisterUser(progressBar);
                registerUser.execute(str_user_phone, str_user_password, type);
            } else {
                Toast.makeText(AppLogin.this, "Please agree to our Terms and Conditions", Toast.LENGTH_SHORT).show();
            }
        });
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

    public void loginUser(final String phone) {
        progressBar.setVisibility(View.GONE);
        initialLayout.setVisibility(View.GONE);
        loginLayout.setVisibility(View.VISIBLE);

        forgotBtn.setOnClickListener(v -> {
            mp.start();
            hideKeyboard(v);
            Intent i = new Intent(AppLogin.this, ForgotPasswordActivity.class);
            i.putExtra("number", phone);
            startActivity(i);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        loginBtn.setOnClickListener(v -> {
            mp.start();
            hideKeyboard(v);
            if (TextUtils.isEmpty(password.getText().toString())) {
                password.setError("Please Enter Password");
                password.requestFocus();
            } else {
                LoginUser loginUser = new LoginUser(AppLogin.this, progressBar);
                loginUser.execute(phone, password.getText().toString());
            }
        });

        troubleLogin.setOnClickListener(v -> {
            Intent intent = new Intent(this, TroubleFAQ.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public boolean isInternetOn() {
        ConnectivityManager connec;
        connec = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        assert connec != null;
        return connec.getActiveNetworkInfo() != null && connec.getActiveNetworkInfo().isAvailable() && connec.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    @SuppressLint("StaticFieldLeak")
    class RegisterUser extends AsyncTask<String, String, String> {

        @SuppressLint("StaticFieldLeak")
        private ProgressBar progressBar;
        @SuppressLint("StaticFieldLeak")
        String str_user_phone, str_user_password;

        RegisterUser(ProgressBar progressBar) {
            this.progressBar = progressBar;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }


        @Override
        protected String doInBackground(String... params) {

            str_user_phone = params[0];
            str_user_password = params[1];
            @SuppressLint("HardwareIds") String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            String data;

            try {

                URL url = new URL(REGISTER_URL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                data = URLEncoder.encode("str_user_phone", "UTF-8") + "=" + URLEncoder.encode(str_user_phone, "UTF-8") + "&" +
                        URLEncoder.encode("str_user_password", "UTF-8") + "=" + URLEncoder.encode(str_user_password, "UTF-8") + "&" +
                        URLEncoder.encode("user_type", "UTF-8") + "=" + URLEncoder.encode(userRole, "UTF-8") + "&" +
                        URLEncoder.encode("device_id", "UTF-8") + "=" + URLEncoder.encode(deviceId, "UTF-8") + "&" +
                        URLEncoder.encode("tbl_school_id", "UTF-8") + "=" + URLEncoder.encode(SCHOOL_ID, "UTF-8");
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
            if (result != null) {
                progressBar.setVisibility(View.GONE);
                switch (result) {
                    case "Already Registered":
                        Toast.makeText(AppLogin.this, "You are already registered", Toast.LENGTH_SHORT).show();
                        break;
                    case "Invalid School or Role":
                        Toast.makeText(AppLogin.this, "Invalid School or Role", Toast.LENGTH_SHORT).show();
                        break;
                    case "Invalid User login phone":
                        Toast.makeText(AppLogin.this, "Invalid User login phone", Toast.LENGTH_SHORT).show();
                        break;
                    default:
                        JSONObject jsonObject;
                        try {
                            jsonObject = new JSONObject(result);
                            JSONArray mJSONArray = jsonObject.getJSONArray("result");
                            JSONObject uid = mJSONArray.getJSONObject(0);
                            userID = uid.getString("tbl_users_id");
                            userLoginId = uid.getString("tbl_users_login_id");
                            userName = uid.getString("tbl_users_name");
                            userEmailId = uid.getString("tbl_users_email");
                            userDob = uid.getString("tbl_users_dob");
                            userGender = uid.getString("tbl_users_gender");
                            userImg = uid.getString("tbl_users_img");
                            userrType = uid.getString("tbl_users_type");
                            if (userRole.equals("Parent")) {
                                getSharedPreferences(PREFS_NAME, 0).edit()
                                        .putString("userPassword", uid.getString("tbl_users_parents_password")).apply();
                                userPhone = uid.getString("tbl_users_parents_no");
                                userPhoneStatus = uid.getString("tbl_users_parents_phone_status");
                            } else {
                                getSharedPreferences(PREFS_NAME, 0).edit()
                                        .putString("userPassword", uid.getString("tbl_users_password")).apply();
                                userPhone = uid.getString("tbl_users_phone");
                                userPhoneStatus = uid.getString("tbl_users_phone_status");
                            }
                            SharedPreferences sp = getSharedPreferences(PREF_ROLE, 0);
                            sp.edit().putString("userRole", userRole).apply();
                            {
                                Intent i = new Intent(AppLogin.this, MobileNumberActivity.class);
                                i.putExtra("userID", userID);
                                i.putExtra("userPhone", userPhone);
                                startActivity(i);
                                overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                Toast.makeText(AppLogin.this, "Otp verification needed", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }
            super.onPostExecute(result);
        }
    }

    @SuppressLint("StaticFieldLeak")
    class LoginUser extends AsyncTask<String, String, String> {

        @SuppressLint("StaticFieldLeak")
        private Context ctx;
        @SuppressLint("StaticFieldLeak")
        private ProgressBar progressBar;

        LoginUser(Context ctx, ProgressBar progressBar) {
            this.ctx = ctx;
            this.progressBar = progressBar;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }


        @Override
        protected String doInBackground(String... params) {

            String str_user_phone = params[0];
            String str_user_password = params[1];
            @SuppressLint("HardwareIds") String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            String data;
            try {
                URL url = new URL(LOGIN_URL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                data = URLEncoder.encode("str_user_phone", "UTF-8") + "=" + URLEncoder.encode(str_user_phone, "UTF-8") + "&" +
                        URLEncoder.encode("str_user_password", "UTF-8") + "=" + URLEncoder.encode(str_user_password, "UTF-8") + "&" +
                        URLEncoder.encode("user_type", "UTF-8") + "=" + URLEncoder.encode(userRole, "UTF-8") + "&" +
                        URLEncoder.encode("device_id", "UTF-8") + "=" + URLEncoder.encode(deviceId, "UTF-8") + "&" +
                        URLEncoder.encode("tbl_school_id", "UTF-8") + "=" + URLEncoder.encode(SCHOOL_ID, "UTF-8");
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
            progressBar.setVisibility(View.GONE);
            Log.e("res", result);
            switch (result) {
                case "Invalid User login phone":
                    Toast.makeText(ctx, "Invalid Phone", Toast.LENGTH_SHORT).show();
                    break;
                case "Invalid User login password":
                    Toast.makeText(ctx, "Invalid Password", Toast.LENGTH_SHORT).show();
                    break;
                case "Password not set":
                    Toast.makeText(AppLogin.this, "Password is not set. Please set the password to continue", Toast.LENGTH_SHORT).show();
                    registerUser();
                    registerBtn.setText("Set Password");
                    break;
                case "Invalid School or Role":
                    Toast.makeText(AppLogin.this, "Invalid School or Role", Toast.LENGTH_SHORT).show();
                    break;
                case "No active batch in this Student account":
                    Toast.makeText(AppLogin.this, "No active batch in this Student account", Toast.LENGTH_SHORT).show();
                    break;
                case "Device not registered" :
                    Toast.makeText(AppLogin.this, result, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    getSharedPreferences("noti_prefs", MODE_PRIVATE).edit().clear().apply();
                    try {
                        SharedPreferences settingsUser = getSharedPreferences(PREFS_USER, 0);
                        SharedPreferences.Editor editorUser = settingsUser.edit();
                        editorUser.putString("user", result);
                        editorUser.apply();

                        JSONObject jsonRootObject = new JSONObject(result);
                        JSONArray jsonArray = jsonRootObject.getJSONArray("result");
                        studentArray.clear();

                        imageUrl = jsonRootObject.getString("ImgUrl");

                        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("imageUrl", imageUrl);
                        editor.apply();
                        SharedPreferences.Editor ed = getSharedPreferences(PREF_ROLE, 0).edit();
                        ed.putString("userRole", jsonRootObject.getString("user_type"));
                        ed.putString("userRoleID", jsonRootObject.getString("user_roleId"));
                        ed.apply();
                        getSharedPreferences(PREFS_NAME, 0).edit()
                                .putString("userPassword", jsonRootObject.getString("tbl_users_password")).apply();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            dbs = new DataBeanStudent();

                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            if (jsonObject.getString("tbl_school_id").equals(SCHOOL_ID)) {
                                userrType = jsonObject.getString("tbl_users_type");
                                userID = jsonObject.getString("tbl_users_id");
                                userLoginId = jsonObject.getString("tbl_users_login_id");
                                userName = jsonObject.getString("tbl_users_name");
                                userPhone = jsonObject.getString("tbl_users_phone");
                                userEmailId = jsonObject.getString("tbl_users_email");
                                userDob = jsonObject.getString("tbl_users_dob");
                                userGender = jsonObject.getString("tbl_users_gender");
                                userImg = jsonObject.getString("tbl_users_img");
                                userPhoneStatus = jsonObject.getString("tbl_users_phone_status");
                                userPassword = jsonRootObject.getString("tbl_users_password");

                                if (userrType.equals("Student")) {
                                    str_section = jsonObject.getString("tbl_stnt_prsnl_data_section");
                                    str_roll_number = jsonObject.getString("tbl_stnt_prsnl_data_rollno");
                                    str_teacher_name = jsonObject.getString("tbl_stnt_prsnl_data_cls_teach");
                                }


                                //school details
                                userSchoolId = jsonObject.getString("tbl_school_id");
                                userSchoolRoleId = jsonObject.getString("tbl_role_id");
                                userSchoolSchoolLogo = jsonObject.getString("tbl_school_logo");
                                userSchoolSchoolLogo1 = jsonObject.getString("tbl_school_logo2");
                                userSchoolSchoolLogo3 = jsonObject.getString("tbl_school_logo3");

                                //color
                                selToolColor = getResources().getString(R.string.tool);
                                selDrawerColor = getResources().getString(R.string.drawer);
                                selStatusColor = getResources().getString(R.string.status);
                                selTextColor1 = getResources().getString(R.string.text1);
                                selTextColor2 = getResources().getString(R.string.text2);

                                userSchoolName = jsonObject.getString("tbl_school_name");
                                userSchoolRoleName = jsonObject.getString("tbl_role_name");
                                dbs.setUserSchoolName(userSchoolName);
                                dbs.setUserSchoolRoleName(userSchoolRoleName);
                                dbs.setUserID(userID);
                                dbs.setRestricted(jsonObject.getString("restricted_access"));
                                dbs.setParentStatus(jsonObject.getString("tbl_users_parents_phone_status"));
                                dbs.setParentPhone(jsonObject.getString("tbl_users_parents_no"));
                                dbs.setParentPassword(jsonObject.getString("tbl_users_parents_password"));
                                dbs.setInstStatus(jsonObject.getString("tbl_school_status"));
                                dbs.setUniqueId(jsonObject.getString("sch_unique_id"));
                                dbs.setUserrType(userrType);
                                dbs.setUserLoginId(userLoginId);
                                dbs.setUserName(userName);
                                dbs.setUserPhone(userPhone);
                                dbs.setUserEmailId(userEmailId);
                                dbs.setUserDob(userDob);
                                dbs.setUserGender(userGender);
                                dbs.setUserImg(userImg);
                                dbs.setUserPhoneStatus(userPhoneStatus);
                                dbs.setUserPassword(userPassword);
                                dbs.setUserSection(str_section);
                                dbs.setUserRollNumber(str_roll_number);
                                dbs.setUserTeacherName(str_teacher_name);

                                dbs.setUserSchoolId(userSchoolId);
                                dbs.setUserSchoolRoleId(userSchoolRoleId);
                                dbs.setUserSchoolSchoolLogo(userSchoolSchoolLogo);
                                dbs.setUserSchoolSchoolLogo1(userSchoolSchoolLogo1);
                                dbs.setUserSchoolSchoolLogo3(userSchoolSchoolLogo3);
                                //color
                                dbs.setSelToolColor(selToolColor);
                                dbs.setSelDrawerColor(selDrawerColor);
                                dbs.setSelStatusColor(selStatusColor);
                                dbs.setSelTextColor1(selTextColor1);
                                dbs.setSelTextColor2(selTextColor2);
                                studentArray.add(dbs);
                            }
                        }
                        if (studentArray.size() == 0) {
                            Toast.makeText(ctx, "No account for this institute", Toast.LENGTH_SHORT).show();
                            getSharedPreferences(PREFS_NAME, 0).edit().clear().apply();
                        } else {
                            userDialogCheck(studentArray);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    break;
            }
            super.onPostExecute(result);
        }
    }

    private void userDialogCheck(ArrayList<DataBeanStudent> studentArray) {
        if (studentArray.size() > 1) {
            setNumberOfUsers(studentArray);
        } else if (studentArray.size() == 1) {
            studentValidLogin(userID);
        } else {
            Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
        }
    }

    private void setNumberOfUsers(final ArrayList<DataBeanStudent> studentArray) {
        LayoutInflater factory = LayoutInflater.from(AppLogin.this);
        //text_entry is an Layout XML file containing two text field to display in alert dialog
        @SuppressLint("InflateParams") final View textEntryView = factory.inflate(R.layout.user_select_dialog, null);

        user_list = textEntryView.findViewById(R.id.user_list);
        userListAdaptor = new UserListAdaptor(AppLogin.this, R.layout.user_select_dialog, studentArray, "0");
        user_list.setAdapter(userListAdaptor);
        user_list.setOnItemClickListener((parent, view, position, id) -> {
            String status;
            if (userRole.equals("Parent")) {
                status = studentArray.get(position).getParentStatus();
            } else {
                status = studentArray.get(position).getInstStatus();
            }
            if (status.equals("1")) {
                userID1 = studentArray.get(position).getUserID();
                userLoginId1 = studentArray.get(position).getUserLoginId();
                userName1 = studentArray.get(position).getUserName();
                userPhone1 = studentArray.get(position).getUserPhone();
                userEmailId1 = studentArray.get(position).getUserEmailId();
                userDob1 = studentArray.get(position).getUserDob();
                userGender1 = studentArray.get(position).getUserGender();
                userImg1 = studentArray.get(position).getUserImg();
                userPhoneStatus1 = studentArray.get(position).getUserPhoneStatus();
                userrType1 = studentArray.get(position).getUserrType();
                userPassword1 = studentArray.get(position).getUserPassword();

                str_section1 = studentArray.get(position).getUserSection();
                str_roll_number1 = studentArray.get(position).getUserRollNumber();
                str_teacher_name1 = studentArray.get(position).getUserTeacherName();


                selToolColor1 = studentArray.get(position).getSelToolColor();
                selDrawerColor1 = studentArray.get(position).getSelDrawerColor();
                selStatusColor1 = studentArray.get(position).getSelStatusColor();
                selTextColor11 = studentArray.get(position).getSelTextColor1();
                selTextColor22 = studentArray.get(position).getSelTextColor2();


                userSchoolId1 = studentArray.get(position).getUserSchoolId();
                userSchoolRoleId1 = studentArray.get(position).getUserSchoolRoleId();
                userSchoolSchoolLogo11 = studentArray.get(position).getUserSchoolSchoolLogo();
                userSchoolSchoolLogo12 = studentArray.get(position).getUserSchoolSchoolLogo1();
                userSchoolSchoolLogo13 = studentArray.get(position).getUserSchoolSchoolLogo3();

                userSchoolName1 = studentArray.get(position).getUserSchoolName();
                userSchoolRoleName1 = studentArray.get(position).getUserSchoolRoleName();
                alertDialog.dismiss();
                if ("Student".equals(userrType1)) {
                    SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("logged", "logged");
                    editor.putString("userID", userID1);
                    editor.putString("userLoginId", userLoginId1);
                    editor.putString("userName", userName1);
                    editor.putString("userPhone", userPhone1);
                    editor.putString("userEmailId", userEmailId1);
                    editor.putString("userDob", userDob1);
                    editor.putString("userGender", userGender1);
                    editor.putString("userImg", userImg1);
                    editor.putString("userPhoneStatus", userPhoneStatus1);
                    editor.putString("userrType", userrType1);
                    editor.putString("userPassword", userPassword1);
                    editor.putString("userSection", str_section1);
                    editor.putString("userRollNumber", str_roll_number1);
                    editor.putString("userTeacherName", str_teacher_name1);
                    editor.putString("userSchoolId", userSchoolId1);
                    editor.putString("userSchoolLogo", userSchoolSchoolLogo11);
                    editor.putString("userSchoolLogo1", userSchoolSchoolLogo12);
                    editor.putString("userSchoolLogo3", userSchoolSchoolLogo13);
                    editor.putString("numberOfUser", "multiple");
                    editor.putString("restricted", studentArray.get(position).getRestricted());
                    editor.putString("parentStatus", studentArray.get(position).getParentStatus());
                    editor.putString("parentPhone", studentArray.get(position).getParentPhone());
                    editor.putString("parentPassword", studentArray.get(position).getParentPassword());
                    editor.putString("unique_id", studentArray.get(position).getUniqueId());

                    //
                    editor.putString("userSchoolName", userSchoolName1);
                    editor.putString("userSchoolRoleName", userSchoolRoleName1);
                    editor.putString("str_school_id", userSchoolId1);
                    editor.putString("url", imageUrl + userSchoolId1);
                    editor.putString("str_role_id", userSchoolRoleId1);
                    editor.apply();

                    //color set
                    SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
                    SharedPreferences.Editor editorColor = settingsColor.edit();
                    editorColor.putString("tool", selToolColor1);
                    editorColor.putString("drawer", selDrawerColor1);
                    editorColor.putString("status", selStatusColor1);
                    editorColor.putString("text1", selTextColor11);
                    editorColor.putString("text2", selTextColor22);
                    editorColor.apply();

                    Intent i1 = new Intent(AppLogin.this, WelcomeActivity.class);
                    i1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i1);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                } else {
                    differentValidLogin1(userID1, studentArray.get(position).getUniqueId(), studentArray.get(position).getRestricted());
                }

                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);


                //	view.setSelected(true);
                for (int j = 0; j < parent.getChildCount(); j++)
                    parent.getChildAt(j).setBackgroundColor(Color.TRANSPARENT);
                // change the background color of the selected element
                view.setBackgroundColor(getResources().getColor(R.color.light_gray1));

            } else {
                startActivity(new Intent(AppLogin.this, InactiveInstitute.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        alert = new AlertDialog.Builder(AppLogin.this, R.style.DialogTheme);
        alert.setTitle("Select User").setView(textEntryView).setNegativeButton("Cancel",
                (dialog, whichButton) -> {

                });
        //alert.show();
        alertDialog = alert.create();
        alertDialog.show();
    }

    private void studentValidLogin(String userID) {
        String status;
        if (userRole.equals("Parent")) {
            status = studentArray.get(0).getParentStatus();
        } else {
            status = studentArray.get(0).getInstStatus();
        }
        if (status.equals("1")) {
            if ("Student".equals(userrType)) {
                {
                    SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("logged", "logged");
                    editor.putString("userID", userID);
                    editor.putString("userLoginId", userLoginId);
                    editor.putString("userName", userName);
                    editor.putString("userPhone", userPhone);
                    editor.putString("userEmailId", userEmailId);
                    editor.putString("userDob", userDob);
                    editor.putString("userGender", userGender);
                    editor.putString("userImg", userImg);
                    editor.putString("userPhoneStatus", userPhoneStatus);
                    editor.putString("userrType", userrType);
                    editor.putString("userPassword", userPassword);
                    editor.putString("userSection", str_section);
                    editor.putString("userRollNumber", str_roll_number);
                    editor.putString("userTeacherName", str_teacher_name);
                    editor.putString("userSchoolId", userSchoolId);
                    editor.putString("userSchoolLogo", userSchoolSchoolLogo);
                    editor.putString("userSchoolLogo1", userSchoolSchoolLogo1);
                    editor.putString("userSchoolLogo3", userSchoolSchoolLogo3);
                    editor.putString("numberOfUser", "single");
                    editor.putString("restricted", studentArray.get(0).getRestricted());
                    editor.putString("parentStatus", studentArray.get(0).getParentStatus());
                    editor.putString("parentPhone", studentArray.get(0).getParentPhone());
                    editor.putString("parentPassword", studentArray.get(0).getParentPassword());
                    editor.putString("unique_id", studentArray.get(0).getUniqueId());

                    //
                    editor.putString("userSchoolName", userSchoolName);
                    editor.putString("userSchoolRoleName", userSchoolRoleName);
                    editor.putString("str_school_id", userSchoolId);
                    editor.putString("url", imageUrl + userSchoolId);
                    editor.putString("str_role_id", userSchoolRoleId);
                    editor.apply();

                    //color set
                    SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
                    SharedPreferences.Editor editorColor = settingsColor.edit();
                    editorColor.putString("tool", selToolColor);
                    editorColor.putString("drawer", selDrawerColor);
                    editorColor.putString("status", selStatusColor);
                    editorColor.putString("text1", selTextColor1);
                    editorColor.putString("text2", selTextColor2);
                    editorColor.apply();


                    Intent i = new Intent(AppLogin.this, WelcomeActivity.class);
                    i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(i);
                    overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                    finish();
                }
            } else {
                differentValidLogin(userID, studentArray.get(0).getUniqueId(), studentArray.get(0).getRestricted());
            }
        } else {
            startActivity(new Intent(this, InactiveInstitute.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        }
    }

    private void differentValidLogin(String userID, String uniqueId, String restricted) {
        if (userPhoneStatus.equalsIgnoreCase("1")) {
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("logged", "logged");
            editor.putString("userID", userID);
            editor.putString("restricted", restricted);
            editor.putString("userLoginId", userLoginId);
            editor.putString("userName", userName);
            editor.putString("userPhone", userPhone);
            editor.putString("userEmailId", userEmailId);
            editor.putString("userDob", userDob);
            editor.putString("userGender", userGender);
            editor.putString("userImg", userImg);
            editor.putString("userPhoneStatus", userPhoneStatus);
            editor.putString("userrType", userrType);
            editor.putString("userPassword", userPassword);
            editor.putString("userSchoolId", userSchoolId);
            editor.putString("userSchoolLogo", userSchoolSchoolLogo);
            editor.putString("userSchoolLogo1", userSchoolSchoolLogo1);
            editor.putString("userSchoolLogo3", userSchoolSchoolLogo3);
            editor.putString("numberOfUser", "single");
            editor.putString("unique_id", uniqueId);
            //
            editor.putString("userSchoolName", userSchoolName);
            editor.putString("userSchoolRoleName", userSchoolRoleName);
            editor.putString("str_school_id", userSchoolId);
            editor.putString("url", imageUrl + userSchoolId);
            editor.putString("str_role_id", userSchoolRoleId);
            editor.apply();

            //color set
            SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
            SharedPreferences.Editor editorColor = settingsColor.edit();
            editorColor.putString("tool", selToolColor);
            editorColor.putString("drawer", selDrawerColor);
            editorColor.putString("status", selStatusColor);
            editorColor.putString("text1", selTextColor1);
            editorColor.putString("text2", selTextColor2);
            editorColor.apply();

            Intent i = new Intent(AppLogin.this, WelcomeActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        } else {
            Intent i = new Intent(AppLogin.this, MobileNumberActivity.class);
            i.putExtra("userID", this.userID);
            i.putExtra("userPhone", userPhone);
            startActivity(i);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }

    private void differentValidLogin1(String userID1, String uniqueId, String restricted) {
        if (userPhoneStatus1.equalsIgnoreCase("1")) {
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("logged", "logged");
            editor.putString("userID", userID1);
            editor.putString("userLoginId", userLoginId1);
            editor.putString("restricted", restricted);
            editor.putString("userName", userName1);
            editor.putString("userPhone", userPhone1);
            editor.putString("userEmailId", userEmailId1);
            editor.putString("userDob", userDob1);
            editor.putString("userGender", userGender1);
            editor.putString("userImg", userImg1);
            editor.putString("userPhoneStatus", userPhoneStatus1);
            editor.putString("userrType", userrType1);
            editor.putString("userPassword", userPassword1);
            editor.putString("userSchoolId", userSchoolId1);
            editor.putString("url", imageUrl + userSchoolId1);
            editor.putString("userSchoolLogo", userSchoolSchoolLogo11);
            editor.putString("userSchoolLogo1", userSchoolSchoolLogo12);
            editor.putString("userSchoolLogo3", userSchoolSchoolLogo13);
            editor.putString("numberOfUser", "multiple");
            editor.putString("unique_id", uniqueId);

            //
            editor.putString("userSchoolName", userSchoolName1);
            editor.putString("userSchoolRoleName", userSchoolRoleName1);
            editor.putString("str_school_id", userSchoolId1);
            editor.putString("str_role_id", userSchoolRoleId1);
            editor.apply();

            //color set
            SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
            SharedPreferences.Editor editorColor = settingsColor.edit();
            editorColor.putString("tool", selToolColor1);
            editorColor.putString("drawer", selDrawerColor1);
            editorColor.putString("status", selStatusColor1);
            editorColor.putString("text1", selTextColor11);
            editorColor.putString("text2", selTextColor22);
            editorColor.apply();

            Intent i = new Intent(AppLogin.this, WelcomeActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        } else {
            Intent i = new Intent(AppLogin.this, MobileNumberActivity.class);
            i.putExtra("userID", userID1);
            i.putExtra("userPhone", userPhone1);
            startActivity(i);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }
}
