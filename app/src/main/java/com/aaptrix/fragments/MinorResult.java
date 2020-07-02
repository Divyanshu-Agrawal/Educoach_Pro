package com.aaptrix.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.aaptrix.R;
import com.aaptrix.adaptor.MinorAdapter;
import com.aaptrix.databeans.MinorData;

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

import static android.content.Context.CONNECTIVITY_SERVICE;
import static com.aaptrix.tools.HttpUrl.MINOR_RESULT;
import static com.aaptrix.tools.SPClass.PREFS_NAME;

public class MinorResult extends Fragment {

    TextView noResult;
    String userId, userSchoolId, userType, str_section;
    ProgressBar progressBar;
    SwipeRefreshLayout swipeRefreshLayout;
    ListView listView;
    ArrayList<MinorData> minorArray = new ArrayList<>();
    String studentImg, userImg, userName, studentName, id, section;
    Context context;

    public MinorResult() {

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_minor_result, container, false);
        noResult = view.findViewById(R.id.no_result);
        listView = view.findViewById(R.id.listview);
        progressBar = view.findViewById(R.id.progress_bar);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);

        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        userId = settings.getString("userID", "");
        userSchoolId = settings.getString("str_school_id", "");
        str_section = settings.getString("userSection", "");
        userImg = settings.getString("userImg", "");
        userName = settings.getString("userName", "");
        userType = getArguments().getString("userType");

        if (isInternetOn()) {
            if (userType.equals("student")) {
                GetAllResult result = new GetAllResult(context);
                result.execute(userSchoolId, userId, str_section);
            } else {
                studentName = getArguments().getString("studentName");
                studentImg = getArguments().getString("studentImage");
                id = getArguments().getString("studentId");
                section = getArguments().getString("userSection");

                GetAllResult result = new GetAllResult(context);
                result.execute(userSchoolId, id, section);
            }
        } else {
            Toast.makeText(context, "No Internet", Toast.LENGTH_SHORT).show();
        }

        swipeRefreshLayout.setOnRefreshListener(() -> {
            minorArray.clear();
            if (isInternetOn()) {
                if (userType.equals("student")) {
                    GetAllResult result = new GetAllResult(context);
                    result.execute(userSchoolId, userId, str_section);
                } else {
                    GetAllResult result = new GetAllResult(context);
                    result.execute(userSchoolId, id, section);
                }
            } else {
                Toast.makeText(context, "No Internet", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    @SuppressLint("StaticFieldLeak")
    public class GetAllResult extends AsyncTask<String, String, String> {
        Context ctx;

        GetAllResult(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            String schoolId = params[0];
            String userId = params[1];
            String userSection = params[2];
            String data;

            try {

                URL url = new URL(MINOR_RESULT);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                data = URLEncoder.encode("schoolId", "UTF-8") + "=" + URLEncoder.encode(schoolId, "UTF-8") + "&" +
                        URLEncoder.encode("userId", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8") + "&" +
                        URLEncoder.encode("batchNm", "UTF-8") + "=" + URLEncoder.encode(userSection, "UTF-8");
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
            super.onPostExecute(result);
            progressBar.setVisibility(View.GONE);
            swipeRefreshLayout.setRefreshing(false);
            if (result != null)
                if (result.contains("\"result\":null")) {
                    noResult.setVisibility(View.VISIBLE);
                } else {
                    noResult.setVisibility(View.GONE);
                    try {
                        minorArray.clear();
                        JSONObject jsonObject = new JSONObject(result);
                        JSONArray jsonArray = jsonObject.getJSONArray("result");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jObject = jsonArray.getJSONObject(i);
                            MinorData data = new MinorData();
                            data.setId(jObject.getString("tbl_result_id"));
                            data.setExamName(jObject.getString("tbl_result_exam_name"));
                            data.setMarks(jObject.getString("tbl_result_details_marks"));
                            data.setSubject(jObject.getString("tbl_result_details_subject_name"));
                            data.setDate(jObject.getString("tbl_exam_details_date"));
                            minorArray.add(data);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    if (minorArray.size() != 0) {
                        listItems();
                    } else {
                        noResult.setVisibility(View.VISIBLE);
                    }
                }
        }
    }

    private void listItems() {
        MinorAdapter adapter = new MinorAdapter(context, R.layout.minor_result_list, minorArray);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public final boolean isInternetOn() {
        ConnectivityManager connec =
                (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
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
