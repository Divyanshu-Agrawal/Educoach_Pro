package com.aaptrix.activitys;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.aaptrix.R;
import com.aaptrix.adaptor.UserListAdaptor;
import com.aaptrix.databeans.DataBeanStudent;
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

import static com.aaptrix.activitys.SplashScreen.SCHOOL_ID;
import static com.aaptrix.tools.HttpUrl.SWITCH_USERS;
import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREFS_USER;
import static com.aaptrix.tools.SPClass.PREF_COLOR;

public class InactiveInstitute extends AppCompatActivity {

    ImageView school_logo;
    boolean doubleBackToExitPressedOnce = false;
    ArrayList<DataBeanStudent> studentArray = new ArrayList<>();
    DataBeanStudent dbs;
    ListView user_list;
    UserListAdaptor userListAdaptor;
    String userID1, userLoginId1, userName1, userPhone1, userEmailId1, userDob1, userGender1, userImg1, userPhoneStatus1, userrType1;
    String userID, userLoginId, userName, userPhone, userEmailId, userDob, userGender, userImg, userPhoneStatus, userrType;
    String userSchoolId, userSchoolName, userSchoolRoleId, userSchoolRoleName, userSchoolSchoolLogo, userSchoolSchoolLogo1;
    String userSchoolId1, userSchoolName1, userSchoolRoleId1, userSchoolRoleName1, userSchoolSchoolLogo11, userSchoolSchoolLogo12, userSchoolSchoolLogo13;
    AlertDialog alertDialog;
    String selToolColor1, selDrawerColor1, selStatusColor1, selTextColor11, selTextColor22, numberOfUser;
    String str_class1, str_section1, str_roll_number1, str_teacher_name1;
    String str_class, str_section, str_roll_number, str_teacher_name;
    AlertDialog.Builder alert;
    String selToolColor, selDrawerColor, selStatusColor, selTextColor1, selTextColor2, userId, userSchoolSchoolLogo3;
    Button switchUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inactive_institute);
        school_logo = findViewById(R.id.school_logo);
        switchUser = findViewById(R.id.switch_account);
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        String schoolId = settings.getString("str_school_id", "");
        userId = settings.getString("userID", "");
        numberOfUser = settings.getString("numberOfUser", "");
        userPhone = settings.getString("userPhone", "");

        if (numberOfUser.equals("multiple")) {
            switchUser.setVisibility(View.VISIBLE);
        } else {
            switchUser.setVisibility(View.GONE);
        }

        switchUser.setOnClickListener(view -> {
            if (isInternetOn()) {
                GetUserLoginCheck b1 = new GetUserLoginCheck(this);
                b1.execute(userPhone, "");
            } else {
                Toast.makeText(this, "No internet available", Toast.LENGTH_SHORT).show();
            }
        });

        Picasso.with(this).load(R.drawable.large_logo).error(R.drawable.app_logo).placeholder(R.drawable.app_logo).into(school_logo);
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
        if (doubleBackToExitPressedOnce) {
            Intent a = new Intent(Intent.ACTION_MAIN);
            a.addCategory(Intent.CATEGORY_HOME);
            a.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(a);
            finish();
            System.exit(0);
        }
        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
    }

    @SuppressLint("StaticFieldLeak")
    public class GetUserLoginCheck extends AsyncTask<String, String, String> {
        Context ctx;

        GetUserLoginCheck(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            switchUser.setClickable(false);
            Toast.makeText(ctx, "Please wait...", Toast.LENGTH_SHORT).show();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String str_user_phone = params[0];
            String str_user_password = params[1];
            String data;
            try {
                URL url = new URL(SWITCH_USERS);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                data = URLEncoder.encode("str_user_phone", "UTF-8") + "=" + URLEncoder.encode(str_user_phone, "UTF-8") + "&" +
                        URLEncoder.encode("str_user_password", "UTF-8") + "=" + URLEncoder.encode(str_user_password, "UTF-8");
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
            switchUser.setClickable(true);

            {
                try {
                    SharedPreferences settingsUser = getSharedPreferences(PREFS_USER, 0);
                    SharedPreferences.Editor editorUser = settingsUser.edit();
                    editorUser.putString("user", result);
                    editorUser.apply();


                    JSONObject jsonRootObject = new JSONObject(result);
                    JSONArray jsonArray = jsonRootObject.getJSONArray("result");
                    studentArray.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        dbs = new DataBeanStudent();

                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        if (jsonObject.getString("tbl_school_id").equals(SCHOOL_ID)) {
                            userrType = jsonObject.getString("tbl_users_type");

                            userID = jsonObject.getString("0");
                            userLoginId = jsonObject.getString("tbl_users_login_id");
                            userName = jsonObject.getString("tbl_users_name");
                            userPhone = jsonObject.getString("tbl_users_phone");
                            userEmailId = jsonObject.getString("tbl_users_email");
                            userDob = jsonObject.getString("tbl_users_dob");
                            userGender = jsonObject.getString("tbl_users_gender");
                            userImg = jsonObject.getString("tbl_users_img");
                            userPhoneStatus = jsonObject.getString("tbl_users_phone_status");

                            if (userrType.equals("Student")) {
                                //Toast.makeText(ctx, ""+userrType, Toast.LENGTH_SHORT).show();
                                str_class = jsonObject.getString("tbl_stnt_prsnl_data_class");
                                str_section = jsonObject.getString("tbl_stnt_prsnl_data_section");
                                str_roll_number = jsonObject.getString("tbl_stnt_prsnl_data_rollno");
                                str_teacher_name = jsonObject.getString("tbl_stnt_prsnl_data_cls_teach");
                            }
                            //	Toast.makeText(ctx, ""+userrType, Toast.LENGTH_SHORT).show();


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
                            dbs.setInstStatus(jsonObject.getString("tbl_school_status"));
                            dbs.setUniqueId(jsonObject.getString("sch_unique_id"));
                            dbs.setRestricted(jsonObject.getString("restricted_access"));
                            dbs.setUserSchoolRoleName(userSchoolRoleName);
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
                //Toast.makeText(ctx, "Valid", Toast.LENGTH_SHORT).show();

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
        LayoutInflater factory = LayoutInflater.from(this);
//text_entry is an Layout XML file containing two text field to display in alert dialog
        final View textEntryView = factory.inflate(R.layout.user_select_dialog, null);

        user_list = textEntryView.findViewById(R.id.user_list);
        Log.e("user id", String.valueOf(userId));
        userListAdaptor = new UserListAdaptor(this, R.layout.user_select_dialog, studentArray, "0");
        user_list.setAdapter(userListAdaptor);
        user_list.setOnItemClickListener((parent, view, position, id) -> {
            if (studentArray.get(position).getInstStatus().equals("1")) {
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
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(true);


                switch (userrType1) {
                    case "Student":
                        if (userPhoneStatus1.equalsIgnoreCase("1")) {
                            SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.remove("refer_code");
                            editor.remove("refer_offer");
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
                            editor.putString("userClass", str_class1);
                            editor.putString("userSection", str_section1);
                            editor.putString("userRollNumber", str_roll_number1);
                            editor.putString("userTeacherName", str_teacher_name1);
                            editor.putString("unique_id", studentArray.get(position).getUniqueId());
                            editor.putString("restricted", studentArray.get(position).getRestricted());
                            editor.putString("userSchoolId", userSchoolId1);
                            editor.putString("userSchoolLogo", userSchoolSchoolLogo11);
                            editor.putString("userSchoolLogo1", userSchoolSchoolLogo12);
                            editor.putString("userSchoolLogo3", userSchoolSchoolLogo13);
                            editor.putString("numberOfUser", "multiple");

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

                            Intent i1 = new Intent(this, WelcomeActivity.class);
                            i1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(i1);
                            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                            finish();
                        } else {
                            Intent i = new Intent(this, MobileNumberActivity.class);
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


                //	view.setSelected(true);
                for (int j = 0; j < parent.getChildCount(); j++)
                    parent.getChildAt(j).setBackgroundColor(Color.TRANSPARENT);
                // change the background color of the selected element
                view.setBackgroundColor(getResources().getColor(R.color.light_gray1));

            } else {
                startActivity(new Intent(this, InactiveInstitute.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }
        });

        alert = new AlertDialog.Builder(this, R.style.DialogTheme);
        alert.setTitle("Select User").setView(textEntryView).setNegativeButton("Cancel", null);
        //alert.show();
        alertDialog = alert.create();
        alertDialog.show();
        Button theButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        Button theButton1 = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        theButton.setTextColor(getResources().getColor(R.color.text_gray));
        theButton1.setTextColor(getResources().getColor(R.color.text_gray));
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setEnabled(false);
    }

    private void studentValidLogin(String userID) {
        if (studentArray.get(0).getInstStatus().equals("1")) {
            switch (userrType) {
                case "Student":
                    if (userPhoneStatus.equalsIgnoreCase("1")) {
                        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.remove("refer_code");
                        editor.remove("refer_offer");
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
                        editor.putString("userClass", str_class);
                        editor.putString("userSection", str_section);
                        editor.putString("userRollNumber", str_roll_number);
                        editor.putString("userTeacherName", str_teacher_name);
                        editor.putString("userSchoolId", userSchoolId);
                        editor.putString("userSchoolLogo", userSchoolSchoolLogo);
                        editor.putString("userSchoolLogo1", userSchoolSchoolLogo1);
                        editor.putString("userSchoolLogo3", userSchoolSchoolLogo3);
                        editor.putString("numberOfUser", "single");

                        //
                        editor.putString("userSchoolName", userSchoolName);
                        editor.putString("userSchoolRoleName", userSchoolRoleName);
                        editor.putString("str_school_id", userSchoolId);
                        editor.putString("str_role_id", userSchoolRoleId);
                        editor.putString("unique_id", studentArray.get(0).getUniqueId());
                        editor.putString("restricted", studentArray.get(0).getRestricted());
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


                        Intent i = new Intent(this, WelcomeActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(i);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        finish();
                    } else {
                        Intent i = new Intent(this, MobileNumberActivity.class);
                        i.putExtra("userID", this.userID);
                        i.putExtra("userPhone", userPhone);
                        startActivity(i);
                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                        //Toast.makeText(InstituteBuzzActivity.this, "Otp verification needed", Toast.LENGTH_SHORT).show();

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
            editor.remove("refer_code");
            editor.remove("refer_offer");
            editor.putString("restricted", restricted);
            editor.putString("logged", "logged");
            editor.putString("userID", userID);
            editor.putString("userLoginId", userLoginId);
            editor.putString("unique_id", uniqueId);
            editor.putString("userName", userName);
            editor.putString("userPhone", userPhone);
            editor.putString("userEmailId", userEmailId);
            editor.putString("userDob", userDob);
            editor.putString("userGender", userGender);
            editor.putString("userImg", userImg);
            editor.putString("userPhoneStatus", userPhoneStatus);
            editor.putString("userrType", userrType);
            editor.putString("userSchoolId", userSchoolId);
            editor.putString("userSchoolLogo", userSchoolSchoolLogo);
            editor.putString("userSchoolLogo1", userSchoolSchoolLogo1);
            editor.putString("userSchoolLogo3", userSchoolSchoolLogo3);
            editor.putString("numberOfUser", "single");

            //
            editor.putString("userSchoolName", userSchoolName);
            editor.putString("userSchoolRoleName", userSchoolRoleName);
            editor.putString("str_school_id", userSchoolId);
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

            Intent i = new Intent(this, WelcomeActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        } else {
            Intent i = new Intent(this, MobileNumberActivity.class);
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
            editor.remove("refer_code");
            editor.remove("refer_offer");
            editor.putString("logged", "logged");
            editor.putString("restricted", restricted);
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
            editor.putString("userSchoolId", userSchoolId1);
            editor.putString("unique_id", uniqueId);
            editor.putString("userSchoolLogo", userSchoolSchoolLogo11);
            editor.putString("userSchoolLogo1", userSchoolSchoolLogo12);
            editor.putString("userSchoolLogo3", userSchoolSchoolLogo13);
            editor.putString("numberOfUser", "multiple");

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

            Intent i = new Intent(this, WelcomeActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
            finish();
        } else {
            Intent i = new Intent(this, MobileNumberActivity.class);
            i.putExtra("userID", userID1);
            i.putExtra("userPhone", userPhone1);
            startActivity(i);
            overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        }
    }

}
