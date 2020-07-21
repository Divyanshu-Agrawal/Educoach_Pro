package com.aaptrix.activitys;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.provider.Settings;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

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

import com.aaptrix.databeans.DataBeanStudent;
import com.aaptrix.adaptor.UserListAdaptor;
import com.aaptrix.R;

import static com.aaptrix.activitys.SplashScreen.SCHOOL_ID;
import static com.aaptrix.tools.HttpUrl.UPDATE_USER_PRO_PASSWORD;
import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREFS_USER;
import static com.aaptrix.tools.SPClass.PREF_COLOR;
import static com.aaptrix.tools.SPClass.PREF_ROLE;

/**
 * Created by Administrator on 11/29/2017.
 */

public class ChangePasswordInitialActivity extends AppCompatActivity {

    Button btn_change_password;
    EditText et_new_password2, et_con_password3;
    ImageView school_logo;
    LinearLayout mainLayoutRegister;
    String str_uid, str_mob, str_password, str_con_password;
    String userID, userLoginId, userName, userPhone, userEmailId, userDob, userGender, userImg, userPhoneStatus, userrType, userPassword;
    String userID1, userLoginId1, userName1, userPhone1, userEmailId1, userDob1, userGender1, userImg1, userPhoneStatus1, userrType1, userPassword1;
    String userSchoolId, userSchoolName, userSchoolRoleId, userSchoolRoleName, userSchoolSchoolLogo, userSchoolSchoolLogo1, userSchoolSchoolLogo3;
    String userSchoolId1, userSchoolName1, userSchoolRoleId1, userSchoolRoleName1, userSchoolSchoolLogo11, userSchoolSchoolLogo12, userSchoolSchoolLogo13;

    String str_class, str_section, str_roll_number, str_teacher_name;
    String str_class1, str_section1, str_roll_number1, str_teacher_name1;
    AlertDialog.Builder alert;
    ArrayList<DataBeanStudent> studentArray = new ArrayList<>();
    DataBeanStudent dbs;
    ListView user_list;
    UserListAdaptor userListAdaptor;
    //color
    String selToolColor, selDrawerColor, selStatusColor, selTextColor1, selTextColor2;
    String selToolColor1, selDrawerColor1, selStatusColor1, selTextColor11, selTextColor22;
    private String imageUrl;
    TextView troublePass;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password_intial_password_layout_new);
        mainLayoutRegister = findViewById(R.id.mainLayoutLogin);

        school_logo = findViewById(R.id.school_logo);
        btn_change_password = findViewById(R.id.btn_change_password);
        et_new_password2 = findViewById(R.id.et_new_password2);
        et_con_password3 = findViewById(R.id.et_con_password3);
        troublePass = findViewById(R.id.trouble_chng_pss);

        troublePass.setOnClickListener(v -> {
            Intent intent = new Intent(this, TroubleLoggingIn.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        });

        et_con_password3.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus)
                hideKeyboard(v);
        });

        et_new_password2.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus)
                hideKeyboard(v);
        });

        str_uid = getIntent().getStringExtra("uid");
        str_mob = getIntent().getStringExtra("mob");
        Picasso.with(this).load(R.drawable.large_logo).into(school_logo);

        btn_change_password.setOnClickListener(view -> {
            str_password = et_new_password2.getText().toString().trim();
            str_con_password = et_con_password3.getText().toString().trim();
            if (TextUtils.isEmpty(str_password)) {
                Toast.makeText(ChangePasswordInitialActivity.this, "Please enter new password", Toast.LENGTH_SHORT).show();
            } else {
                if (TextUtils.isEmpty(str_con_password)) {
                    Toast.makeText(ChangePasswordInitialActivity.this, "Please enter confirm password", Toast.LENGTH_SHORT).show();
                } else if (!str_password.equals(str_con_password)) {
                    Toast.makeText(ChangePasswordInitialActivity.this, "Enter Password is mismatch", Toast.LENGTH_SHORT).show();
                } else if (str_password.length() < 6) {
                    Toast.makeText(ChangePasswordInitialActivity.this, "Password must have 6 characters", Toast.LENGTH_SHORT).show();
                } else {
                    UpdateProfilePassword updateProfilePassword = new UpdateProfilePassword(ChangePasswordInitialActivity.this);
                    updateProfilePassword.execute(str_mob, str_password);
                }

            }

        });
    }


    @SuppressLint("StaticFieldLeak")
    public class UpdateProfilePassword extends AsyncTask<String, String, String> {
        Context ctx;

        UpdateProfilePassword(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            Toast.makeText(ctx, "Please wait we are updating your profile password", Toast.LENGTH_SHORT).show();
            super.onPreExecute();

        }

        @Override
        protected String doInBackground(String... params) {

            String userId = params[0];
            String password = params[1];
            @SuppressLint("HardwareIds") String deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
            String data;

            try {

                URL url = new URL(UPDATE_USER_PRO_PASSWORD);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                data = URLEncoder.encode("str_mob", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8") + "&" +
                        URLEncoder.encode("newPass", "UTF-8") + "=" + URLEncoder.encode(password, "UTF-8") + "&" +
                        URLEncoder.encode("device_id", "UTF-8") + "=" + URLEncoder.encode(deviceId, "UTF-8") + "&" +
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
                                str_class = jsonObject.getString("tbl_stnt_prsnl_data_class");
                                str_section = jsonObject.getString("tbl_stnt_prsnl_data_section");
                                str_roll_number = jsonObject.getString("tbl_stnt_prsnl_data_rollno");
                                str_teacher_name = jsonObject.getString("tbl_stnt_prsnl_data_cls_teach");
                            }
                            userSchoolId = jsonObject.getString("tbl_school_id");
                            userSchoolRoleId = jsonObject.getString("tbl_role_id");
                            userSchoolSchoolLogo = jsonObject.getString("tbl_school_logo");
                            userSchoolSchoolLogo1 = jsonObject.getString("tbl_school_logo2");
                            userSchoolSchoolLogo3 = jsonObject.getString("tbl_school_logo3");

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
                            dbs.setUserSchoolId(userSchoolId);
                            dbs.setUserSchoolRoleId(userSchoolRoleId);
                            dbs.setUserSchoolSchoolLogo(userSchoolSchoolLogo);
                            dbs.setUserSchoolSchoolLogo1(userSchoolSchoolLogo1);
                            dbs.setUserSchoolSchoolLogo3(userSchoolSchoolLogo3);
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
                        editor.putString("numberOfUser", "single");

                        editor.putString("parentStatus", studentArray.get(0).getParentStatus());
                        editor.putString("parentPhone", studentArray.get(0).getParentPhone());
                        editor.putString("parentPassword", studentArray.get(0).getParentPassword());
                        editor.putString("unique_id", studentArray.get(0).getUniqueId());
                        editor.putString("restricted", studentArray.get(0).getRestricted());

                        editor.putString("str_school_id", userSchoolId);
                        editor.putString("str_role_id", userSchoolRoleId);
                        editor.putString("userSchoolName", userSchoolName);
                        editor.putString("userSchoolRoleName", userSchoolRoleName);
                        editor.commit();

                        SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
                        SharedPreferences.Editor editorColor = settingsColor.edit();
                        editorColor.putString("tool", selToolColor);
                        editorColor.putString("drawer", selDrawerColor);
                        editorColor.putString("status", selStatusColor);
                        editorColor.putString("text1", selTextColor1);
                        editorColor.putString("text2", selTextColor2);
                        editorColor.commit();

                        Intent i = new Intent(ChangePasswordInitialActivity.this, WelcomeActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish();
                    } else {
                        Intent i = new Intent(ChangePasswordInitialActivity.this, MobileNumberActivity.class);
                        i.putExtra("userID", this.userID);
                        i.putExtra("userPhone", userPhone);
                        startActivity(i);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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
            editor.putString("restricted", restricted);
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
            editor.putString("userSchoolId", userSchoolId);
            editor.putString("url", imageUrl + userSchoolId);
            editor.putString("userSchoolLogo", userSchoolSchoolLogo);
            editor.putString("unique_id", uniqueId);
            editor.putString("userSchoolLogo1", userSchoolSchoolLogo1);
            editor.putString("userSchoolLogo3", userSchoolSchoolLogo3);
            editor.putString("numberOfUser", "single");

            editor.putString("str_school_id", userSchoolId);
            editor.putString("str_role_id", userSchoolRoleId);
            editor.putString("userSchoolName", userSchoolName);
            editor.putString("userSchoolRoleName", userSchoolRoleName);
            editor.commit();

            SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
            SharedPreferences.Editor editorColor = settingsColor.edit();
            editorColor.putString("tool", selToolColor);
            editorColor.putString("drawer", selDrawerColor);
            editorColor.putString("status", selStatusColor);
            editorColor.putString("text1", selTextColor1);
            editorColor.putString("text2", selTextColor2);
            editorColor.commit();

            Intent i = new Intent(ChangePasswordInitialActivity.this, WelcomeActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        } else {
            Intent i = new Intent(ChangePasswordInitialActivity.this, MobileNumberActivity.class);
            i.putExtra("userID", this.userID);
            i.putExtra("userPhone", userPhone);
            startActivity(i);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }

    private void differentValidLogin1(String userID1, String uniqueId, String restricted) {
        if (userPhoneStatus.equalsIgnoreCase("1")) {
            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
            SharedPreferences.Editor editor = settings.edit();
            editor.putString("logged", "logged");
            editor.putString("userID", userID1);
            editor.putString("restricted", restricted);
            editor.putString("userLoginId", userLoginId1);
            editor.putString("userName", userName1);
            editor.putString("userPhone", userPhone1);
            editor.putString("userEmailId", userEmailId1);
            editor.putString("userDob", userDob1);
            editor.putString("userGender", userGender1);
            editor.putString("userImg", userImg1);
            editor.putString("userPhoneStatus", userPhoneStatus1);
            editor.putString("userrType", userrType1);
            editor.putString("unique_id", uniqueId);
            editor.putString("userPassword", userPassword1);
            editor.putString("userSchoolId", userSchoolId1);
            editor.putString("url", imageUrl + userSchoolId1);
            editor.putString("userSchoolLogo", userSchoolSchoolLogo11);
            editor.putString("userSchoolLogo1", userSchoolSchoolLogo12);
            editor.putString("userSchoolLogo3", userSchoolSchoolLogo13);

            editor.putString("numberOfUser", "multiple");

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

            Intent i = new Intent(ChangePasswordInitialActivity.this, WelcomeActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        } else {
            Intent i = new Intent(ChangePasswordInitialActivity.this, MobileNumberActivity.class);
            i.putExtra("userID", userID1);
            i.putExtra("userPhone", userPhone1);
            startActivity(i);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }


    private void setNumberOfUsers(final ArrayList<DataBeanStudent> studentArray) {
        LayoutInflater factory = LayoutInflater.from(ChangePasswordInitialActivity.this);
        final View textEntryView = factory.inflate(R.layout.user_select_dialog, null);

        user_list = textEntryView.findViewById(R.id.user_list);
        userListAdaptor = new UserListAdaptor(ChangePasswordInitialActivity.this, R.layout.user_select_dialog, studentArray, "0");
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

                str_class1 = studentArray.get(position).getUserClass();
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
                            editor.putString("userClass", str_class1);
                            editor.putString("userSection", str_section1);
                            editor.putString("userRollNumber", str_roll_number1);
                            editor.putString("userTeacherName", str_teacher_name1);
                            editor.putString("userSchoolId", userSchoolId1);
                            editor.putString("url", imageUrl + userSchoolId1);
                            editor.putString("userSchoolLogo", userSchoolSchoolLogo11);
                            editor.putString("userSchoolLogo1", userSchoolSchoolLogo12);
                            editor.putString("userSchoolLogo3", userSchoolSchoolLogo13);

                            editor.putString("numberOfUser", "multiple");
                            editor.putString("restricted", studentArray.get(position).getRestricted());
                            editor.putString("parentStatus", studentArray.get(position).getParentStatus());
                            editor.putString("parentPhone", studentArray.get(position).getParentPhone());
                            editor.putString("unique_id", studentArray.get(position).getUniqueId());
                            editor.putString("parentPassword", studentArray.get(position).getParentPassword());

                            //
                            editor.putString("userSchoolName", userSchoolName);
                            editor.putString("userSchoolRoleName", userSchoolRoleName);
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

                            Intent i1 = new Intent(ChangePasswordInitialActivity.this, WelcomeActivity.class);
                            i1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i1);
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            finish();
                        } else {
                            Intent i = new Intent(ChangePasswordInitialActivity.this, MobileNumberActivity.class);
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

                for (int j = 0; j < parent.getChildCount(); j++)
                    parent.getChildAt(j).setBackgroundColor(Color.TRANSPARENT);
                // change the background color of the selected element
                view.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));

            } else {
                startActivity(new Intent(ChangePasswordInitialActivity.this, InactiveInstitute.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        alert = new AlertDialog.Builder(ChangePasswordInitialActivity.this, R.style.DialogTheme);
        alert.setTitle("Select User").setView(textEntryView)
                .setNegativeButton("Cancel", null);
        //alert.show();
        AlertDialog alertDialog = alert.create();
        alertDialog.show();
    }

    private void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
        assert inputMethodManager != null;
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}