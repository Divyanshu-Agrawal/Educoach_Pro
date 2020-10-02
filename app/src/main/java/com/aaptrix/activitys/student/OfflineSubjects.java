package com.aaptrix.activitys.student;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aaptrix.R;
import com.google.android.material.appbar.AppBarLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;

import static com.aaptrix.tools.HttpUrl.GET_SUBS;
import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREF_COLOR;

public class OfflineSubjects extends AppCompatActivity {

    AppBarLayout appBarLayout;
    String selToolColor, selStatusColor, selTextColor1, type;
    TextView tool_title, noVideos;
    SharedPreferences sp;
    String[] subjects;
    ArrayList<String> subject_array = new ArrayList<>();
    String userId, userSchoolId, userRoleId, userrType, userSection, userName, restricted;
    GridView subjectGrid;
    ProgressBar progressBar;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offline_subjects);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        appBarLayout = findViewById(R.id.appBarLayout);
        tool_title = findViewById(R.id.tool_title);
        noVideos = findViewById(R.id.no_videos);
        subjectGrid = findViewById(R.id.subject_grid);
        progressBar = findViewById(R.id.progress);

        type = getIntent().getStringExtra("type");

        if (type.equals("video")) {
            tool_title.setText("Offline Videos");
        } else {
            tool_title.setText("Downloaded Material");
        }

        SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
        selToolColor = settingsColor.getString("tool", "");
        selStatusColor = settingsColor.getString("status", "");
        selTextColor1 = settingsColor.getString("text1", "");

        sp = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        userSchoolId = sp.getString("str_school_id", "");
        userSection = sp.getString("userSection", "");
        userId = sp.getString("userID", "");
        userRoleId = sp.getString("str_role_id", "");
        userrType = sp.getString("userrType", "");
        userName = sp.getString("userName", "");
        restricted = sp.getString("restricted", "");

        appBarLayout.setBackgroundColor(Color.parseColor(selToolColor));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(selStatusColor));
        }
        tool_title.setTextColor(Color.parseColor(selTextColor1));

        if (PermissionChecker.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PermissionChecker.PERMISSION_GRANTED &&
                PermissionChecker.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PermissionChecker.PERMISSION_GRANTED) {
            isPermissionGranted();
        }

        try {
            File directory = this.getFilesDir();
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File(directory, "subjects")));
            String json = in.readObject().toString();
            in.close();
            JSONObject jsonRootObject = new JSONObject(json);
            JSONArray jsonArray = jsonRootObject.getJSONArray("SubjectList");
            subjects = new String[jsonArray.length()];
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                subjects[i] = jsonObject.getString("tbl_batch_subjct_name");
            }
            String object = jsonRootObject.getString("DisableSubject");
            for (String subject : subjects) {
                if (!object.contains(subject)) {
                    subject_array.add(subject);
                }
            }
            setSubject();
        } catch (Exception e) {
            e.printStackTrace();
            if (isInternetOn()) {
                String section = "[{\"userName\":\"" + userSection + "\"}]";
                GetSubject subject = new GetSubject(this);
                subject.execute(userSchoolId, section);
            } else {
                Toast.makeText(this, "Please connect to internet to refresh subjects", Toast.LENGTH_SHORT).show();
                noVideos.setVisibility(View.VISIBLE);
                subjectGrid.setVisibility(View.GONE);
                findViewById(R.id.title).setVisibility(View.GONE);
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    public class GetSubject extends AsyncTask<String, String, String> {
        Context ctx;

        GetSubject(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            String school_id = params[0];
            String batchArray = params[1];
            String data;

            try {

                URL url = new URL(GET_SUBS);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                data = URLEncoder.encode("school_id", "UTF-8") + "=" + URLEncoder.encode(school_id, "UTF-8") + "&" +
                        URLEncoder.encode("batchArray", "UTF-8") + "=" + URLEncoder.encode(batchArray, "UTF-8") + "&" +
                        URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8") + "&" +
                        URLEncoder.encode("tbl_users_nm", "UTF-8") + "=" + URLEncoder.encode(userName, "UTF-8") + "&" +
                        URLEncoder.encode("restricted_access", "UTF-8") + "=" + URLEncoder.encode(restricted, "UTF-8");
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
            if (!result.equals("{\"SubjectList\":null}")) {
                try {
                    subject_array.clear();
                    JSONObject jsonRootObject = new JSONObject(result);
                    cacheJson(jsonRootObject, "subjects");
                    JSONArray jsonArray = jsonRootObject.getJSONArray("SubjectList");
                    subjects = new String[jsonArray.length()];
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        subjects[i] = jsonObject.getString("tbl_batch_subjct_name");
                    }
                    String object = jsonRootObject.getString("DisableSubject");
                    for (String subject : subjects) {
                        if (!object.contains(subject)) {
                            subject_array.add(subject);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setSubject();
                super.onPostExecute(result);
            }
        }
    }

    private void setSubject() {
        SubjectAdapter adapter = new SubjectAdapter(this, R.layout.list_subject, subject_array);
        subjectGrid.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    class SubjectAdapter extends ArrayAdapter<String> {

        private ArrayList<String> objects;
        private Activity context;
        private int resource;

        public SubjectAdapter(Activity context, int resource, ArrayList<String> objects) {
            super(context, resource, objects);
            this.objects = objects;
            this.context = context;
            this.resource = resource;
        }

        private class ViewHolder {
            TextView subject;
        }

        @SuppressLint("ViewHolder")
        @NonNull
        @Override
        public View getView(int position, View view, @NonNull ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            view = inflater.inflate(resource, null);
            SubjectAdapter.ViewHolder holder = new SubjectAdapter.ViewHolder();
            holder.subject = view.findViewById(R.id.subject);
            view.setTag(holder);

            view.setOnClickListener(v -> {
                if (type.equals("video")) {
                    Intent intent = new Intent(context, OfflineVideos.class);
                    intent.putExtra("sub", objects.get(position));
                    context.startActivity(intent);
                } else {
                    Intent intent = new Intent(context, OfflineMaterial.class);
                    intent.putExtra("sub", objects.get(position));
                    context.startActivity(intent);
                }
            });

            if (objects != null) {
                holder.subject.setText(objects.get(position));
            }
            return view;
        }

        @Override
        public int getViewTypeCount() {
            return Math.max(getCount(), 1);
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }
    }

    private void cacheJson(final JSONObject jsonObject, String name) {
        new Thread(() -> {
            ObjectOutput out;
            String data = jsonObject.toString();
            try {
                File directory = this.getFilesDir();
                directory.mkdir();
                out = new ObjectOutputStream(new FileOutputStream(new File(directory, name)));
                out.writeObject(data);
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
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

    public void isPermissionGranted() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
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
}