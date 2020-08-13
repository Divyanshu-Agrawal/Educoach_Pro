package com.aaptrix.activitys.student;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aaptrix.R;
import com.aaptrix.databeans.StudyMaterialData;
import com.google.android.material.appbar.AppBarLayout;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Objects;

import static com.aaptrix.tools.HttpUrl.ALL_STUDY_MATERIAL;
import static com.aaptrix.tools.HttpUrl.GET_SUBS;
import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREF_COLOR;

public class StudentMaterial extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<StudyMaterialData> materialArray = new ArrayList<>(), array = new ArrayList<>();
    AppBarLayout appBarLayout;
    String selToolColor, selStatusColor, selTextColor1, selDrawerColor;
    TextView tool_title, noVideos;
    SharedPreferences sp;
    String[] subjects;
    ArrayList<String> subject_array = new ArrayList<>();
    String userId, userSchoolId, userRoleId, userrType, userSection, url, userName, restricted;
    GridView subjectGrid;
    ProgressBar progressBar;
    LinearLayout mainLayout, viewAll, viewAllSubjects;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_material);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        appBarLayout = findViewById(R.id.appBarLayout);
        tool_title = findViewById(R.id.tool_title);
        recyclerView = findViewById(R.id.video_list);
        noVideos = findViewById(R.id.no_videos);
        subjectGrid = findViewById(R.id.subject_grid);
        progressBar = findViewById(R.id.progress);
        mainLayout = findViewById(R.id.main_layout);
        viewAll = findViewById(R.id.view_all);
        viewAllSubjects = findViewById(R.id.view_all_subject);
        recyclerView.setEnabled(true);

        SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
        selToolColor = settingsColor.getString("tool", "");
        selStatusColor = settingsColor.getString("status", "");
        selTextColor1 = settingsColor.getString("text1", "");
        selDrawerColor = settingsColor.getString("drawer", "");

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

        String section = "[{\"userName\":\"" + userSection + "\"}]";
        GetSubject subject = new GetSubject(this);
        subject.execute(userSchoolId, section);

        GetMaterial getMaterial = new GetMaterial(this);
        getMaterial.execute(userSchoolId, userSection, userrType);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutFrozen(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        viewAll.setOnClickListener(v -> {
            Intent intent = new Intent(this, StudyMaterial.class);
            intent.putExtra("sub", "All");
            startActivity(intent);
        });

        viewAllSubjects.setOnClickListener(v -> {
            Intent intent = new Intent(this, StudyMaterial.class);
            intent.putExtra("sub", "All");
            startActivity(intent);
        });
    }

    @SuppressLint("StaticFieldLeak")
    public class GetMaterial extends AsyncTask<String, String, String> {
        Context ctx;

        GetMaterial(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            recyclerView.setEnabled(false);
            materialArray.clear();
            array.clear();
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String schoolId = params[0];
            String userSection = params[1];
            String userrType = params[2];
            String data;

            try {
                URL url = new URL(ALL_STUDY_MATERIAL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                data = URLEncoder.encode("schoolId", "UTF-8") + "=" + URLEncoder.encode(schoolId, "UTF-8") + "&" +
                        URLEncoder.encode("userSection", "UTF-8") + "=" + URLEncoder.encode(userSection, "UTF-8") + "&" +
                        URLEncoder.encode("userrType", "UTF-8") + "=" + URLEncoder.encode(userrType, "UTF-8") + "&" +
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
            Log.e("study material", result);
            recyclerView.setEnabled(true);
            try {
                array.clear();
                materialArray.clear();
                JSONObject jsonRootObject = new JSONObject(result);
                if (!result.contains("\"result\":null")) {
                    JSONArray jsonArray = jsonRootObject.getJSONArray("result");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        StudyMaterialData data = new StudyMaterialData();
                        JSONObject jObject = jsonArray.getJSONObject(i);
                        data.setTitle(jObject.getString("tbl_school_studymaterial_title"));
                        data.setId(jObject.getString("tbl_school_studymaterial_id"));
                        data.setDescription(jObject.getString("tbl_school_studymaterial_desc"));
                        data.setUrl(jObject.getString("tbl_school_studymaterial_docfile").split(","));
                        data.setSubject(jObject.getString("subject_name"));
                        data.setBatch(jObject.getString("tbl_stnt_prsnl_data_section"));
                        data.setPermission(jObject.getString("download_permission"));
                        data.setDate(jObject.getString("tbl_school_studymaterial_date"));
                        data.setTags(jObject.getString("tbl_school_studymaterial_tag"));
                        array.add(data);
                    }
                }
                if (!result.contains("\"studyMaterialsStudent\":null")) {
                    JSONArray jsonArray = jsonRootObject.getJSONArray("studyMaterialsStudent");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        StudyMaterialData data = new StudyMaterialData();
                        JSONObject jObject = jsonArray.getJSONObject(i);
                        data.setTitle(jObject.getString("tbl_school_studymaterial_title"));
                        data.setId(jObject.getString("tbl_school_studymaterial_id"));
                        data.setDescription(jObject.getString("tbl_school_studymaterial_desc"));
                        data.setUrl(jObject.getString("tbl_school_studymaterial_docfile").split(","));
                        data.setSubject(jObject.getString("subject_name"));
                        data.setBatch(jObject.getString("tbl_stnt_prsnl_data_section"));
                        data.setPermission(jObject.getString("download_permission"));
                        data.setDate(jObject.getString("tbl_school_studymaterial_date"));
                        data.setTags(jObject.getString("tbl_school_studymaterial_tag"));
                        array.add(data);
                    }
                }
                String disable = jsonRootObject.getString("DisableSubject");
                for (int i = 0; i < array.size(); i++) {
                    if (!disable.contains(array.get(i).getSubject())) {
                        materialArray.add(array.get(i));
                    }
                }
                if (materialArray.size() > 0) {
                    noVideos.setVisibility(View.GONE);
                    mainLayout.setVisibility(View.VISIBLE);
                    listItems();
                } else {
                    noVideos.setVisibility(View.VISIBLE);
                    mainLayout.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            super.onPostExecute(result);
        }

    }

    private void listItems() {

        ArrayList<StudyMaterialData> arrayList = new ArrayList<>();

        ArrayList<String> ids = new ArrayList<>();
        for (int i = 0; i < materialArray.size(); i++) {
            if (!ids.contains(materialArray.get(i).getId())) {
                ids.add(materialArray.get(i).getId());
            }
        }
        for (int i = 0; i < ids.size(); i++) {
            for (int j = 0; j < materialArray.size(); j++) {
                if (ids.get(i).equals(materialArray.get(j).getId())) {
                    arrayList.add(materialArray.get(j));
                    break;
                }
            }
        }

        progressBar.setVisibility(View.GONE);

        if (arrayList.size() == 0) {
            noVideos.setVisibility(View.VISIBLE);
            mainLayout.setVisibility(View.GONE);
        } else {
            noVideos.setVisibility(View.GONE);
            mainLayout.setVisibility(View.VISIBLE);
        }

        Collections.sort(arrayList, (o1, o2) -> {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            try {
                return Objects.requireNonNull(sdf.parse(o1.getDate())).compareTo(sdf.parse(o2.getDate()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return 0;
        });
        Collections.reverse(arrayList);

        StudentMaterialAdapter adapter = new StudentMaterialAdapter(this, R.layout.list_study_material, arrayList);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    class StudentMaterialAdapter extends RecyclerView.Adapter<StudentMaterial.StudentMaterialAdapter.ViewHolder> {

        private Context context;
        private int resource;
        private ArrayList<StudyMaterialData> objects;

        public StudentMaterialAdapter(Context context, int resource, ArrayList<StudyMaterialData> objects) {
            this.context = context;
            this.resource = resource;
            this.objects = objects;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView title, description, subject;
            CardView cardView;

            ViewHolder(@NonNull View view) {
                super(view);
                title = view.findViewById(R.id.title);
                description = view.findViewById(R.id.description);
                subject = view.findViewById(R.id.subject);
                cardView = view.findViewById(R.id.cardview);
            }
        }

        @NonNull
        @Override
        public StudentMaterial.StudentMaterialAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(context).inflate(resource, parent, false);
            return new StudentMaterial.StudentMaterialAdapter.ViewHolder(itemView);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull StudentMaterial.StudentMaterialAdapter.ViewHolder holder, int position) {
            StudyMaterialData data = objects.get(position);
            holder.subject.setBackgroundColor(Color.parseColor(selDrawerColor));
            holder.subject.setTextColor(Color.parseColor(selTextColor1));
            if (data.getSubject().equals("0")) {
                holder.subject.setText("All Subjects");
            } else {
                if (!data.getSubject().equals("Null"))
                    holder.subject.setText(data.getSubject());
                else
                    holder.subject.setVisibility(View.GONE);
            }
            holder.title.setText(data.getTitle());

            if (!data.getDescription().equals("null")) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    holder.description.setText(Html.fromHtml(data.getDescription(), Html.FROM_HTML_MODE_COMPACT));
                } else {
                    holder.description.setText(Html.fromHtml(data.getDescription()));
                }
            } else {
                holder.description.setText("");
            }

            holder.cardView.setOnClickListener(v -> {
                Intent intent = new Intent(context, StudyMaterialDetail.class);
                intent.putExtra("title", data.getTitle());
                intent.putExtra("description", data.getDescription());
                intent.putExtra("id", data.getId());
                intent.putExtra("url", data.getUrl());
                intent.putExtra("permission", data.getPermission());
                intent.putExtra("subject", data.getSubject());
                intent.putExtra("tags", data.getTags());
                startActivity(intent);
            });
        }

        @Override
        public int getItemCount() {
            return Math.min(objects.size(), 5);
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

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.height = (int) getResources().getDimension(R.dimen._90sdp) * (subject_array.size() / 2);
        subjectGrid.setLayoutParams(params);
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
                Intent intent = new Intent(context, StudyMaterial.class);
                intent.putExtra("sub", objects.get(position));
                context.startActivity(intent);
            });

            viewAllSubjects.setOnClickListener(v -> {
                Intent intent = new Intent(context, StudyMaterial.class);
                intent.putExtra("sub", "All");
                context.startActivity(intent);
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
}