package com.aaptrix.activitys;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;

import com.aaptrix.BuildConfig;
import com.aaptrix.activitys.admin.InstituteBuzzActivityDiff;

import androidx.annotation.Nullable;

import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ServerValue;
import com.google.firebase.iid.FirebaseInstanceId;

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
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

import com.aaptrix.databeans.DataBeanStudent;
import com.aaptrix.adaptor.UserListAdaptor;
import com.aaptrix.R;

import static com.aaptrix.activitys.SplashScreen.SCHOOL_ID;
import static com.aaptrix.activitys.SplashScreen.SCHOOL_NAME;
import static com.aaptrix.activitys.SplashScreen.SENDER_ID;
import static com.aaptrix.tools.HttpUrl.UPDATE_OTP;
import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREFS_USER;
import static com.aaptrix.tools.SPClass.PREF_COLOR;
import static com.aaptrix.tools.SPClass.PREF_ROLE;

public class OtpScreenActivity extends Activity {

    String mob, otp, uid, type;
    private EditText otpNumber;
    Button btn_submitOtp;
    TextView resend, timer;
    ImageView school_logo;
    String userID, userLoginId, userName, userPhone, userEmailId, userDob, userGender, userImg, userPhoneStatus, userrType, userPassword;
    String userID1, userLoginId1, userName1, userPhone1, userEmailId1, userDob1, userGender1, userImg1, userPhoneStatus1, userrType1, userPassword1;
    String userSchoolId, userSchoolName, userSchoolRoleId, userSchoolRoleName, userSchoolSchoolLogo, userSchoolSchoolLogo1, userSchoolSchoolLogo3;
    String userSchoolId1, userSchoolName1, userSchoolRoleId1, userSchoolRoleName1, userSchoolSchoolLogo11, userSchoolSchoolLogo12, userSchoolSchoolLogo13;

    String str_class, str_section, str_roll_number, str_teacher_name;
    String str_section1, str_roll_number1, str_teacher_name1;
    LinearLayout loader;
    RelativeLayout mainLayoutRegister;

    AlertDialog.Builder alert;
    ArrayList<DataBeanStudent> studentArray = new ArrayList<>();
    DataBeanStudent dbs;
    ListView user_list;
    MediaPlayer mp;
    UserListAdaptor userListAdaptor;
    //color
    String selToolColor, selDrawerColor, selStatusColor, selTextColor1, selTextColor2;
    String selToolColor1, selDrawerColor1, selStatusColor1, selTextColor11, selTextColor22;
    AlertDialog alertDialog;

    //message
    private FirebaseAuth mAuth;
    private DatabaseReference storeUserDefaultDataReference;
    private ProgressDialog loadingBar;
    PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallback;

    CountDownTimer countDownTimer;
    boolean sendcomp = false;
    private String imageUrl;
    TextView version, troubleOtp;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.otp_screen_layout);
        mainLayoutRegister = findViewById(R.id.mainLayoutLogin);
        setupUI(mainLayoutRegister);
        otpNumber = findViewById(R.id.otpNumber);
        btn_submitOtp = findViewById(R.id.btn_submitOtp);
        resend = findViewById(R.id.resend);
        timer = findViewById(R.id.timer);
        school_logo = findViewById(R.id.school_logo);
        version = findViewById(R.id.version);
        troubleOtp = findViewById(R.id.trouble_otp);
        version.setText("Version " + BuildConfig.VERSION_NAME);
        mp = MediaPlayer.create(this, R.raw.button_click);
        mAuth = FirebaseAuth.getInstance();

        troubleOtp.setOnClickListener(v -> {
            Intent intent = new Intent(this, TroubleFAQ.class);
            startActivity(intent);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        otpNumber.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus)
                hideKeyboard(v);
        });
        type = getIntent().getStringExtra("type").trim();
        mob = getIntent().getStringExtra("contact").trim();
        if (!type.equals("register_verify")) {
            uid = getIntent().getStringExtra("uid").trim();
            otp = getIntent().getStringExtra("OTPNumber").trim();
        }

        SharedPreferences sp = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String password = sp.getString("userPassword", "");

        loadingBar = new ProgressDialog(this, R.style.AppCompatAlertDialogStyle);
        resendTimer();
        switch (type) {
            case "forgot":
                btn_submitOtp.setOnClickListener(view -> {
                    mp.start();
                    String str_otp = otpNumber.getText().toString().trim();
                    if (TextUtils.isDigitsOnly(otp)) {
                        if (otp.equals(str_otp)) {
                            Intent i = new Intent(OtpScreenActivity.this, ChangePasswordInitialActivity.class);
                            i.putExtra("uid", uid);
                            i.putExtra("mob", mob);
                            startActivity(i);
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        } else {
                            Toast.makeText(OtpScreenActivity.this, "Invalid Otp", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                resend.setOnClickListener(view -> {
                    mp.start();
                    resendTimer();
                    Toast.makeText(OtpScreenActivity.this, "Please wait...", Toast.LENGTH_SHORT).show();
                    sendOtp(mob);
                });
                break;
            case "register_code":
                btn_submitOtp.setOnClickListener(view -> {
                    mp.start();
                    String str_otp = otpNumber.getText().toString().trim();
                    if (TextUtils.isDigitsOnly(otp)) {
                        if (otp.equals(str_otp)) {
                            UpdateOtpStatus b1 = new UpdateOtpStatus(OtpScreenActivity.this);
                            b1.execute(mob, password);
                        } else {
                            Toast.makeText(OtpScreenActivity.this, "Invalid Otp", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

                resend.setVisibility(View.GONE);
                resend.setOnClickListener(view -> {
                    mp.start();
                    resendTimer();
                    Toast.makeText(OtpScreenActivity.this, "Please wait...", Toast.LENGTH_SHORT).show();
                    sendOtp(mob);
                });
                break;
            case "register_verify":
                UpdateOtpStatus b1 = new UpdateOtpStatus(OtpScreenActivity.this);
                b1.execute(mob, password);
                break;
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

    private void resendTimer() {
        timer.setVisibility(View.VISIBLE);
        resend.setVisibility(View.GONE);
        new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                timer.setText("00 : " + millisUntilFinished / 1000);
            }

            public void onFinish() {
                timer.setVisibility(View.GONE);
                resend.setVisibility(View.VISIBLE);
            }
        }.start();
    }

    @SuppressLint("ClickableViewAccessibility")
    public void setupUI(View view) {
        if (!(view instanceof EditText)) {
            view.setOnTouchListener((v, event) -> {
                hideKeyboard(v);
                return false;
            });
        }

        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }

    private int getRandomNumberInRange() {
        Random r = new Random();
        return r.nextInt((999998 - 100001) + 1) + 100001;
    }

    @SuppressLint("StaticFieldLeak")
    public class UpdateOtpStatus extends AsyncTask<String, String, String> {
        Context ctx;

        UpdateOtpStatus(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            Toast.makeText(ctx, "Please wait we are checking", Toast.LENGTH_SHORT).show();

            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {

            String mob = params[0];
            String password = params[1];
            @SuppressLint("HardwareIds") String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            String data;

            try {

                URL url = new URL(UPDATE_OTP);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                data = URLEncoder.encode("mob", "UTF-8") + "=" + URLEncoder.encode(mob, "UTF-8") + "&" +
                        URLEncoder.encode("str_user_password", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8") + "&" +
                        URLEncoder.encode("device_id", "UTF-8") + "=" + URLEncoder.encode(deviceId, "UTF-8") + "&" +
                        URLEncoder.encode("tbl_school_id", "UTF-8") + "=" + URLEncoder.encode(SCHOOL_ID, "UTF-8") + "&" +
                        URLEncoder.encode("user_type", "UTF-8") + "=" + URLEncoder.encode(getSharedPreferences(PREF_ROLE, 0).getString("userRole", ""), "UTF-8");
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
            Log.e("Json", " " + result);
            //	pDialog.dismiss();
            if (!result.isEmpty()) {

                try {
                    SharedPreferences settingsUser = getSharedPreferences(PREFS_USER, 0);
                    SharedPreferences.Editor editorUser = settingsUser.edit();
                    editorUser.putString("user", result);
                    editorUser.commit();


                    JSONObject jsonRootObject = new JSONObject(result);
                    JSONArray jsonArray = jsonRootObject.getJSONArray("result");

                    imageUrl = jsonRootObject.getString("ImgUrl");

                    SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                    SharedPreferences.Editor editor = settings.edit();
                    editor.putString("imageUrl", imageUrl);
                    editor.apply();

                    studentArray.clear();
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
                            userPassword = jsonObject.getString("tbl_users_password");

                            if (userrType.equals("Student")) {
                                //Toast.makeText(ctx, ""+userrType, Toast.LENGTH_SHORT).show();
                                str_class = jsonObject.getString("tbl_stnt_prsnl_data_class");
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
                            dbs.setParentStatus(jsonObject.getString("tbl_users_parents_phone_status"));
                            dbs.setParentPhone(jsonObject.getString("tbl_users_parents_no"));
                            dbs.setParentPassword(jsonObject.getString("tbl_users_parents_password"));
                            dbs.setInstStatus(jsonObject.getString("tbl_school_status"));
                            dbs.setUniqueId(jsonObject.getString("sch_unique_id"));
                            dbs.setRestricted(jsonObject.getString("restricted_access"));
                            dbs.setUserID(userID);
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
                            dbs.setUserClass(str_class);
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
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                userDialogCheck(studentArray);
            } else {
                Toast.makeText(ctx, "Server issues", Toast.LENGTH_SHORT).show();

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

    private void studentValidLogin(String userID) {
        String status;
        if (getSharedPreferences(PREF_ROLE, 0).getString("userRole", "").equals("Parent")) {
            status = studentArray.get(0).getParentStatus();
        } else {
            status = studentArray.get(0).getInstStatus();
        }
        if (status.equals("1")) {
            switch (userrType) {
                case "Student":
                    if (status.equalsIgnoreCase("1")) {
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
                        editor.putString("userClass", str_class);
                        editor.putString("userSection", str_section);
                        editor.putString("userRollNumber", str_roll_number);
                        editor.putString("userTeacherName", str_teacher_name);
                        editor.putString("userSchoolId", userSchoolId);
                        editor.putString("url", imageUrl + userSchoolId);
                        editor.putString("userSchoolLogo", userSchoolSchoolLogo);
                        editor.putString("userSchoolLogo1", userSchoolSchoolLogo1);
                        editor.putString("userSchoolLogo3", userSchoolSchoolLogo3);

                        editor.putString("parentStatus", studentArray.get(0).getParentStatus());
                        editor.putString("parentPhone", studentArray.get(0).getParentPhone());
                        editor.putString("parentPassword", studentArray.get(0).getParentPassword());
                        editor.putString("unique_id", studentArray.get(0).getUniqueId());
                        editor.putString("restricted", studentArray.get(0).getRestricted());

                        editor.putString("numberOfUser", "single");

                        //
                        editor.putString("str_school_id", userSchoolId);
                        editor.putString("str_role_id", userSchoolRoleId);
                        editor.putString("userSchoolName", userSchoolName);
                        editor.putString("userSchoolRoleName", userSchoolRoleName);
                        editor.commit();

                        //color set
                        SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
                        SharedPreferences.Editor editorColor = settingsColor.edit();
                        editorColor.putString("tool", selToolColor);
                        editorColor.putString("drawer", selDrawerColor);
                        editorColor.putString("status", selStatusColor);
                        editorColor.putString("text1", selTextColor1);
                        editorColor.putString("text2", selTextColor2);
                        editorColor.commit();
                        Intent i = new Intent(OtpScreenActivity.this, WelcomeActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish();


                    } else {
                        Intent i = new Intent(OtpScreenActivity.this, MobileNumberActivity.class);
                        i.putExtra("userID", this.userID);
                        i.putExtra("userPhone", userPhone);
                        startActivity(i);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        //Toast.makeText(OtpScreenActivity.this, "Otp verification needed", Toast.LENGTH_SHORT).show();

                    }
                    break;
                case "Teacher":
                case "Admin":
                case "Staff":
                case "Others":
                    differentValidLogin(userID, studentArray.get(0).getUniqueId(), studentArray.get(0).getRestricted());
                    break;

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
            editor.putString("unique_id", uniqueId);
            editor.putString("url", imageUrl + userSchoolId);
            editor.putString("userSchoolLogo", userSchoolSchoolLogo);
            editor.putString("userSchoolLogo1", userSchoolSchoolLogo1);
            editor.putString("userSchoolLogo3", userSchoolSchoolLogo3);
            editor.putString("numberOfUser", "single");
            //
            editor.putString("str_school_id", userSchoolId);
            editor.putString("str_role_id", userSchoolRoleId);
            editor.putString("userSchoolName", userSchoolName);
            editor.putString("userSchoolRoleName", userSchoolRoleName);
            editor.commit();

            //color set
            SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
            SharedPreferences.Editor editorColor = settingsColor.edit();
            editorColor.putString("tool", selToolColor);
            editorColor.putString("drawer", selDrawerColor);
            editorColor.putString("status", selStatusColor);
            editorColor.putString("text1", selTextColor1);
            editorColor.putString("text2", selTextColor2);
            editorColor.commit();

            messagingWithFirebase(userID, userrType, userName, userImg, userSchoolId);
        } else {
            Intent i = new Intent(OtpScreenActivity.this, MobileNumberActivity.class);
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
            editor.putString("restricted", restricted);
            editor.putString("userID", userID1);
            editor.putString("userLoginId", userLoginId1);
            editor.putString("unique_id", uniqueId);
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

            //
            editor.putString("str_school_id", userSchoolId1);
            editor.putString("str_role_id", userSchoolRoleId1);
            editor.putString("userSchoolName", userSchoolName1);
            editor.putString("userSchoolRoleName", userSchoolRoleName1);
            editor.commit();

            //color set
            SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
            SharedPreferences.Editor editorColor = settingsColor.edit();
            editorColor.putString("tool", selToolColor1);
            editorColor.putString("drawer", selDrawerColor1);
            editorColor.putString("status", selStatusColor1);
            editorColor.putString("text1", selTextColor11);
            editorColor.putString("text2", selTextColor22);
            editorColor.commit();

            messagingWithFirebase(userID1, userrType1, userName1, userImg1, userSchoolId);
        } else {
            Intent i = new Intent(OtpScreenActivity.this, MobileNumberActivity.class);
            i.putExtra("userID", userID1);
            i.putExtra("userPhone", userPhone1);
            startActivity(i);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }

    private void setNumberOfUsers(final ArrayList<DataBeanStudent> studentArray) {
        LayoutInflater factory = LayoutInflater.from(OtpScreenActivity.this);
//text_entry is an Layout XML file containing two text field to display in alert dialog
        final View textEntryView = factory.inflate(R.layout.user_select_dialog, null);

        user_list = textEntryView.findViewById(R.id.user_list);
        userListAdaptor = new UserListAdaptor(OtpScreenActivity.this, R.layout.user_select_dialog, studentArray, "0");
        user_list.setAdapter(userListAdaptor);
        user_list.setOnItemClickListener((parent, view, position, id) -> {
            String status;
            if (getSharedPreferences(PREF_ROLE, 0).getString("userRole", "").equals("Parent")) {
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

                switch (userrType1) {
                    case "Student":
                        if (status.equalsIgnoreCase("1")) {
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
                            editor.putString("url", imageUrl + userSchoolId1);
                            editor.putString("userSchoolLogo", userSchoolSchoolLogo11);
                            editor.putString("userSchoolLogo1", userSchoolSchoolLogo12);
                            editor.putString("userSchoolLogo3", userSchoolSchoolLogo13);
                            editor.putString("numberOfUser", "multiple");
                            editor.putString("parentStatus", studentArray.get(position).getParentStatus());
                            editor.putString("parentPhone", studentArray.get(position).getParentPhone());
                            editor.putString("parentPassword", studentArray.get(position).getParentPassword());
                            editor.putString("unique_id", studentArray.get(position).getUniqueId());
                            editor.putString("restricted", studentArray.get(position).getRestricted());
                            //
                            editor.putString("userSchoolName", userSchoolName1);
                            editor.putString("userSchoolRoleName", userSchoolRoleName1);
                            editor.putString("str_school_id", userSchoolId1);
                            editor.putString("str_role_id", userSchoolRoleId1);
                            editor.commit();

                            //color set
                            SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
                            SharedPreferences.Editor editorColor = settingsColor.edit();
                            editorColor.putString("tool", selToolColor1);
                            editorColor.putString("drawer", selDrawerColor1);
                            editorColor.putString("status", selStatusColor1);
                            editorColor.putString("text1", selTextColor11);
                            editorColor.putString("text2", selTextColor22);
                            editorColor.commit();

                            Intent i1 = new Intent(OtpScreenActivity.this, WelcomeActivity.class);
                            i1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i1);
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            finish();
                        } else {
                            Intent i = new Intent(OtpScreenActivity.this, MobileNumberActivity.class);
                            i.putExtra("userID", userID1);
                            i.putExtra("userPhone", userPhone1);
                            startActivity(i);
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        }
                        break;
                    case "Teacher":
                    case "Admin":
                    case "Staff":
                    case "Others":
                        differentValidLogin1(userID1, studentArray.get(position).getUniqueId(), studentArray.get(position).getRestricted());
                        break;
                }

                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);


                //	view.setSelected(true);
                for (int j = 0; j < parent.getChildCount(); j++)
                    parent.getChildAt(j).setBackgroundColor(Color.TRANSPARENT);
                // change the background color of the selected element
                view.setBackgroundColor(getResources().getColor(R.color.light_gray1));

            } else {
                startActivity(new Intent(OtpScreenActivity.this, InactiveInstitute.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        alert = new AlertDialog.Builder(OtpScreenActivity.this, R.style.DialogTheme);
        alert.setTitle("Select User").setView(textEntryView).setNegativeButton("Cancel",
                (dialog, whichButton) -> {
                    /*
                     * User clicked cancel so do some stuff
                     */
                });
        //alert.show();
        alertDialog = alert.create();
        alertDialog.show();
    }

    private void messagingWithFirebase(final String userID, final String userrType, final String userName, final
    String userImg, final String userSchoolId) {
        final String user_token_id = FirebaseInstanceId.getInstance().getToken();
        String firebase_userID = "educoach" + userID + "@educoach.co.in";
        String firebase_password = "educoach" + userID;
        loadingBar.setTitle("Creating New Account");
        loadingBar.setMessage("Please Wait,while we are creating account for you.");
        loadingBar.show();
        mAuth.createUserWithEmailAndPassword(firebase_userID, firebase_password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        String current_user_id = Objects.requireNonNull(mAuth.getCurrentUser()).getUid();
                        storeUserDefaultDataReference = FirebaseDatabase.getInstance().getReference().child("Users").child(current_user_id);
                        storeUserDefaultDataReference.child("userID").setValue(userID);
                        storeUserDefaultDataReference.child("userrType").setValue(userrType);
                        storeUserDefaultDataReference.child("userName").setValue(userName);
                        storeUserDefaultDataReference.child("userImg").setValue(userImg);
                        storeUserDefaultDataReference.child("online").setValue(ServerValue.TIMESTAMP);
                        storeUserDefaultDataReference.child("user_token_id").setValue(user_token_id);
                        storeUserDefaultDataReference.child("userSchoolId").setValue(userSchoolId);
                        storeUserDefaultDataReference.child("user_thumb_image").setValue("default image")
                                .addOnCompleteListener(task1 -> {
                                    if (task1.isSuccessful()) {
                                        Intent i = new Intent(OtpScreenActivity.this, InstituteBuzzActivityDiff.class);
                                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(i);
                                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                        finish();
                                    } else {
                                        Intent i = new Intent(OtpScreenActivity.this, InstituteBuzzActivityDiff.class);
                                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(i);
                                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                        finish();
                                    }
                                });
                    } else {
                        Intent i = new Intent(OtpScreenActivity.this, InstituteBuzzActivityDiff.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish();
                    }
                    loadingBar.dismiss();
                });
    }

    public void sendOtp(String contact) {
        if (!TextUtils.isEmpty(contact) && (contact.length() == 10)) {
            int randomNum = getRandomNumberInRange();
            Toast.makeText(getApplicationContext(), "Code sent to the number", Toast.LENGTH_SHORT).show();
            smsBackground mGetImagesTask = new smsBackground(this);
            mGetImagesTask.execute(contact, randomNum + "");
        } else {
            Toast.makeText(this, "Please Enter valid Number", Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class smsBackground extends AsyncTask<String, Void, String> {
        Context ctx;
        String mobileNo;
        String senderId;

        smsBackground(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected String doInBackground(String... params) {
            String mobile = params[0].replace("+91", "");
            String randomno = params[1];
            otp = randomno;
            mobileNo = mobile;
            sms_Send(mobile, Integer.parseInt(randomno));
            return null;
        }

        private void sms_Send(String mobileNo, int randomNum) {

            String authkey = getResources().getString(R.string.sms_Auth_key);
            senderId = SENDER_ID;
            String message = randomNum + " is your verification code for " + SCHOOL_NAME + " | Smart App for Smart Coaching.";
            String route = "4";

            URLConnection myURLConnection;
            URL myURL;
            BufferedReader reader;
            String encoded_message = URLEncoder.encode(message);
            String mainUrl = "http://msg.sjainventures.com/api/sendhttp.php?";

            StringBuilder sbPostData = new StringBuilder(mainUrl);
            sbPostData.append("authkey=").append(authkey);
            sbPostData.append("&mobiles=").append(mobileNo);
            sbPostData.append("&message=").append(encoded_message);
            sbPostData.append("&route=").append(route);
            sbPostData.append("&sender=").append(senderId);

            try {
                mainUrl = sbPostData.toString();
                myURL = new URL(mainUrl);
                myURLConnection = myURL.openConnection();
                myURLConnection.connect();
                reader = new BufferedReader(new InputStreamReader(myURLConnection.getInputStream()));

                String response;
                while ((response = reader.readLine()) != null)
                    Log.e("RESPONSE", "" + response);
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }
}
