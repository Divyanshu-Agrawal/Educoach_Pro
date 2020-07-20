package com.aaptrix.activitys.admin;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import com.google.android.material.appbar.AppBarLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;

import com.aaptrix.adaptor.BatchListAdaptor;
import com.aaptrix.databeans.DataBeanStudent;
import com.aaptrix.R;

import static com.aaptrix.tools.HttpUrl.ALL_BATCHS;
import static com.aaptrix.tools.SPClass.PREFS_DAIRY;
import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREF_COLOR;

public class IntermidiateScreenActivityPublication extends AppCompatActivity {

    SharedPreferences.Editor editor;
    String userId, roleId, schoolId, userClass, userSection, userRollNumber, userClassTeacher;
    SharedPreferences.Editor editorColor;
    String selToolColor, selDrawerColor, selStatusColor, selTextColor1, selTextColor2, numberOfUser;
    AppBarLayout appBarLayout;
    TextView tool_title;
    String userLoginId, userName, userImg, userrType, userPassword, userSchoolLogo;
    //
    TextView view1;
    LinearLayout attendance_diary;
    ImageView school_logo;
    ProgressBar loader_student;
    //
    String str_tool_title;

    ArrayList<DataBeanStudent> studentArray = new ArrayList<>();
    DataBeanStudent dbs;
    String studentId, studentName;
    ListView user_list;
    CheckBox cb_selectAll;
    BatchListAdaptor batchListAdaptor;
    AlertDialog alertDialog;
    boolean IsVisibleMain = false;
    TextView tv_student;
    private SharedPreferences sp_dairy;
    SharedPreferences.Editor se_dairy;
    AlertDialog.Builder alert;
    TextView cube1, cube2;
    TextView view2;
    MediaPlayer mp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ntermidiate_layout1);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        setTitle("");
        setResult(RESULT_OK);
        appBarLayout = findViewById(R.id.appBarLayout);
        tool_title = findViewById(R.id.tool_title);
        //color
        SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
        editorColor = settingsColor.edit();
        selToolColor = settingsColor.getString("tool", "");
        selDrawerColor = settingsColor.getString("drawer", "");
        selStatusColor = settingsColor.getString("status", "");
        selTextColor1 = settingsColor.getString("text1", "");
        selTextColor2 = settingsColor.getString("text2", "");
        mp = MediaPlayer.create(this, R.raw.button_click);

        str_tool_title = getIntent().getStringExtra("str_tool_title");
        tool_title.setText(str_tool_title);
        //header
        cube1 = findViewById(R.id.cube1);
        cube2 = findViewById(R.id.cube2);
        view2 = findViewById(R.id.view1);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        editor = settings.edit();
        userId = settings.getString("userID", "");
        userLoginId = settings.getString("userLoginId", "");
        userName = settings.getString("userName", "");
        userImg = settings.getString("userImg", "");
        userClass = settings.getString("userClass", "");
        userSection = settings.getString("userSection", "");
        userRollNumber = settings.getString("userRollNumber", "");
        userClassTeacher = settings.getString("userTeacherName", "");
        userrType = settings.getString("userrType", "");
        userPassword = settings.getString("userPassword", "");
        roleId = settings.getString("str_role_id", "");
        schoolId = settings.getString("str_school_id", "");
        numberOfUser = settings.getString("numberOfUser", "");
        userSchoolLogo = settings.getString("imageUrl", "") + schoolId + "/other/" + settings.getString("userSchoolLogo", "");

        //
        view1 = findViewById(R.id.view);
        attendance_diary = findViewById(R.id.attendance_action);

        school_logo = findViewById(R.id.school_logo);
        tv_student = findViewById(R.id.tv_student);
        loader_student = findViewById(R.id.loader_student);

        Picasso.with(this).load(R.drawable.large_logo).into(school_logo);

        tv_student.setOnClickListener(view -> setNumberOfUsers(studentArray));
        try {
            File directory = this.getFilesDir();
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File(directory, "batches")));
            String json = in.readObject().toString();
            in.close();
            if (!json.equals("{\"result\":null}")) {
                try {
                    JSONObject jsonRootObject = new JSONObject(json);
                    JSONArray jsonArray = jsonRootObject.getJSONArray("result");
                    studentArray.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        dbs = new DataBeanStudent();
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        studentId = jsonObject.getString("tbl_batch_detail_id");
                        studentName = jsonObject.getString("tbl_batch_name");
                        dbs.setUserID(studentId);
                        dbs.setUserName(studentName);
                        studentArray.add(dbs);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            GetAllBatches b1 = new GetAllBatches(this);
            b1.execute(schoolId);
        }

        sp_dairy = getSharedPreferences(PREFS_DAIRY, 0);
        se_dairy = sp_dairy.edit();
        se_dairy.clear();
        se_dairy.apply();
        view1.setOnClickListener(view -> {
            mp.start();
            String studentArray1 = sp_dairy.getString("studentArray", "");
            if ("Add Activity".equals(str_tool_title)) {
                if (!studentArray1.isEmpty()) {
                    Intent intent = new Intent(this, AddNewActivity.class);
                    intent.putExtra("type", "add");
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Select Batch", Toast.LENGTH_SHORT).show();
                }
            }
        });


        appBarLayout.setBackgroundColor(Color.parseColor(selToolColor));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(selStatusColor));
        }
        tool_title.setTextColor(Color.parseColor(selTextColor1));
        view1.setTextColor(Color.parseColor(selTextColor1));
        attendance_diary.setBackgroundColor(Color.parseColor(selToolColor));
        GradientDrawable drawable = (GradientDrawable) cube1.getBackground();
        drawable.setStroke(2, Color.parseColor(selToolColor));
        GradientDrawable drawable1 = (GradientDrawable) cube2.getBackground();
        drawable1.setStroke(2, Color.parseColor(selToolColor));
        view2.setBackgroundColor(Color.parseColor(selToolColor));
    }

    @SuppressLint("StaticFieldLeak")
    public class GetAllBatches extends AsyncTask<String, String, String> {
        Context ctx;

        GetAllBatches(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            loader_student.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            String school_id = params[0];
            String data;

            try {

                URL url = new URL(ALL_BATCHS);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                data = URLEncoder.encode("school_id", "UTF-8") + "=" + URLEncoder.encode(school_id, "UTF-8") + "&" +
                        URLEncoder.encode("tbl_users_id", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8");
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

            loader_student.setVisibility(View.GONE);
            Log.d("result", result);
            //save state for offline
            if (result.equals("{\"result\":null}")) {
                studentArray.clear();
                Toast.makeText(ctx, "No Value", Toast.LENGTH_SHORT).show();

            } else {

                try {
                    JSONObject jsonRootObject = new JSONObject(result);
                    JSONArray jsonArray = jsonRootObject.getJSONArray("result");
                    studentArray.clear();
                    for (int i = 0; i < jsonArray.length(); i++) {
                        dbs = new DataBeanStudent();
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        studentId = jsonObject.getString("tbl_batch_detail_id");
                        studentName = jsonObject.getString("tbl_batch_name");
                        //studentResultStatus = jsonObject.getString("tbl_attandance_status").toString();
                        dbs.setUserID(studentId);
                        dbs.setUserName(studentName);
                        //attendance
                        //dbs.setUserLoginId(studentResultStatus);
                        studentArray.add(dbs);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                super.onPostExecute(result);
                //	setNumberOfUsers(studentArray);
            }
        }
    }

    private void setNumberOfUsers(final ArrayList<DataBeanStudent> studentArray) {
        LayoutInflater factory = LayoutInflater.from(IntermidiateScreenActivityPublication.this);
        final View textEntryView = factory.inflate(R.layout.tudent_select_dialog, null);

        user_list = textEntryView.findViewById(R.id.user_list);
        cb_selectAll = textEntryView.findViewById(R.id.cb_selectAll);
        batchListAdaptor = new BatchListAdaptor(IntermidiateScreenActivityPublication.this, R.layout.user_select_dialog, studentArray, IsVisibleMain);
        user_list.setAdapter(batchListAdaptor);

        cb_selectAll.setOnCheckedChangeListener((compoundButton, b) -> {
            if (b) {
                batchListAdaptor = new BatchListAdaptor(IntermidiateScreenActivityPublication.this, R.layout.user_select_dialog, studentArray, b);
                user_list.setAdapter(batchListAdaptor);
                batchListAdaptor.notifyDataSetChanged();

                Gson gson = new GsonBuilder().create();
                JsonArray myCustomArray = gson.toJsonTree(studentArray).getAsJsonArray();
                JsonArray studentArray1 = gson.toJsonTree(studentArray).getAsJsonArray();

                sp_dairy = getSharedPreferences(PREFS_DAIRY, 0);
                se_dairy = sp_dairy.edit();
                se_dairy.clear();
                se_dairy.apply();
                se_dairy.putString("dairy", "" + myCustomArray);
                se_dairy.putString("studentArray", "" + studentArray1);
                se_dairy.commit();

            } else {
                batchListAdaptor = new BatchListAdaptor(IntermidiateScreenActivityPublication.this, R.layout.user_select_dialog, studentArray, b);
                user_list.setAdapter(batchListAdaptor);
                batchListAdaptor.notifyDataSetChanged();
                //studentListAdaptor.unselectAll(b);
                sp_dairy = getSharedPreferences(PREFS_DAIRY, 0);
                se_dairy = sp_dairy.edit();
                se_dairy.clear();
                se_dairy.commit();
            }
        });

        user_list.setOnItemClickListener((parent, view, position, id) -> Toast.makeText(IntermidiateScreenActivityPublication.this, "" + position, Toast.LENGTH_SHORT).show());

        alert = new AlertDialog.Builder(IntermidiateScreenActivityPublication.this, R.style.DialogTheme);

        alert.setTitle("Select Batch").setView(textEntryView).setPositiveButton("Ok",
                (dialog, whichButton) -> {
                    alertDialog.dismiss();
                }).setNegativeButton("Cancel",
                (dialog, whichButton) -> {
                });
        //alert.show();
        alertDialog = alert.create();
        alertDialog.show();
        Button theButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
        Button theButton1 = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        theButton.setTextColor(getResources().getColor(R.color.text_gray));
        theButton1.setTextColor(getResources().getColor(R.color.text_gray));
        alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                .setEnabled(true);
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
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mp.release();
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
