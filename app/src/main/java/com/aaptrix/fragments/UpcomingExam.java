package com.aaptrix.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.aaptrix.R;
import com.aaptrix.adaptor.OnlineExamAdapter;
import com.aaptrix.databeans.OnlineExamData;

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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import static com.aaptrix.tools.HttpUrl.ALL_BATCHS;
import static com.aaptrix.tools.HttpUrl.ALL_ONLINE_EXAM;
import static com.aaptrix.tools.SPClass.PREFS_NAME;

public class UpcomingExam extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;
    private ProgressBar progressBar;
    private ArrayList<OnlineExamData> examArray = new ArrayList<>();
    private TextView noExam;
    private Context context;
    String[] batch_array = {"All Batches"};
    Spinner batch_spinner;
    String selBatch = "All", schoolId, userSection, userId, userType;

    public UpcomingExam() {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        this.context = context;
        super.onAttach(context);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_upcoming_exam, container, false);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        listView = view.findViewById(R.id.listview);
        progressBar = view.findViewById(R.id.progress_bar);
        noExam = view.findViewById(R.id.no_exam);
        batch_spinner = view.findViewById(R.id.batch_spinner);

        SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
        schoolId = settings.getString("str_school_id", "");
        userSection = settings.getString("userSection", "");
        userId = settings.getString("userID", "");
        userType = settings.getString("userrType", "");

        if (userType.equals("Admin") || userType.equals("Teacher")) {
            setBatch();
            try {
                File directory = getContext().getFilesDir();
                ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File(directory, "batches")));
                String json = in.readObject().toString();
                in.close();
                if (!json.equals("{\"result\":null}")) {
                    try {
                        JSONObject jo = new JSONObject(json);
                        JSONArray ja = jo.getJSONArray("result");
                        batch_array = new String[ja.length() + 1];
                        selBatch = "All";
                        batch_array[0] = "All Batches";
                        for (int i = 0; i < ja.length(); i++) {
                            jo = ja.getJSONObject(i);
                            batch_array[i + 1] = jo.getString("tbl_batch_name");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    setBatch();
                } else {
                    String batch_array[] = {"All Batches"};
                    ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<>(getContext(), R.layout.spinner_list_item1, batch_array);
                    dataAdapter1.setDropDownViewResource(R.layout.spinner_list_item1);
                    batch_spinner.setAdapter(dataAdapter1);
                    Toast.makeText(getContext(), "No Batch", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                e.printStackTrace();
                GetAllBatches b1 = new GetAllBatches(getContext());
                b1.execute(schoolId);
            }
        } else {
            batch_spinner.setVisibility(View.GONE);
        }

        if (isInternetOn()) {
            GetExam getExam = new GetExam(getContext());
            if (userType.equals("Student")) {
                getExam.execute(schoolId, userSection);
            } else {
                getExam.execute(schoolId, selBatch);
            }
        } else {
            noExam.setVisibility(View.VISIBLE);
            noExam.setText("No Internet");
        }

        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (isInternetOn()) {
                examArray.clear();
                swipeRefreshLayout.setRefreshing(true);
                progressBar.setVisibility(View.VISIBLE);
                GetExam getExam = new GetExam(getContext());
                if (userType.equals("Student")) {
                    getExam.execute(schoolId, userSection);
                } else {
                    getExam.execute(schoolId, selBatch);
                }
            } else {
                Toast.makeText(getContext(), "No Internet", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
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
            if (!result.equals("{\"result\":null}")) {
                try {
                    JSONObject jo = new JSONObject(result);
                    JSONArray ja = jo.getJSONArray("result");
                    batch_array = new String[ja.length() + 1];
                    selBatch = "All Batches";
                    batch_array[0] = "All Batches";
                    for (int i = 0; i < ja.length(); i++) {
                        jo = ja.getJSONObject(i);
                        batch_array[i + 1] = jo.getString("tbl_batch_name");
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                setBatch();
            } else {
                String batch_array[] = {"All Batches"};
                ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<>(ctx, R.layout.spinner_list_item1, batch_array);
                dataAdapter1.setDropDownViewResource(R.layout.spinner_list_item1);
                batch_spinner.setAdapter(dataAdapter1);
                Toast.makeText(ctx, "No Batch", Toast.LENGTH_SHORT).show();
            }

            super.onPostExecute(result);
        }

    }

    private void setBatch() {
        ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<>(getContext(), R.layout.spinner_list_item1, batch_array);
        dataAdapter1.setDropDownViewResource(R.layout.spinner_list_item1);
        batch_spinner.setAdapter(dataAdapter1);
        batch_spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (batch_array[i].equals("All Batches")) {
                    selBatch = "All";
                    GetExam getExam = new GetExam(getContext());
                    getExam.execute(schoolId, selBatch);
                } else {
                    selBatch = batch_array[i];
                    GetExam getExam = new GetExam(getContext());
                    getExam.execute(schoolId, selBatch);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    public class GetExam extends AsyncTask<String, String, String> {
        Context ctx;

        GetExam(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {

            String school_id = params[0];
            String batch = params[1];

            Log.e("user", userId);
            Log.e("batch", batch);

            String data;

            try {

                URL url = new URL(ALL_ONLINE_EXAM);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                data = URLEncoder.encode("school_id", "UTF-8") + "=" + URLEncoder.encode(school_id, "UTF-8") + "&" +
                        URLEncoder.encode("batch_nm", "UTF-8") + "=" + URLEncoder.encode(batch, "UTF-8") + "&" +
                        URLEncoder.encode("tbl_users_id", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8") + "&" +
                        URLEncoder.encode("userType", "UTF-8") + "=" + URLEncoder.encode(userType, "UTF-8");
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
            swipeRefreshLayout.setRefreshing(false);
            examArray.clear();
            if (!result.contains("{\"OnlineExamList\":null}")) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("OnlineExamList");
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.DATE, -1);
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        OnlineExamData data = new OnlineExamData();
                        String start = object.getString("tbl_online_exam_date");
                        String end = object.getString("tbl_online_exam_end_date");
                        Date startdate = sdf.parse(start);
                        Date enddate = sdf.parse(end);
                        if (userType.equals("Student")) {
                            String sub = jsonObject.getString("DisableSubject");
                            if (!sub.contains(object.getString("subject_name"))) {
                                if (calendar.getTime().equals(startdate) || calendar.getTime().before(startdate) || (calendar.getTime().after(startdate) && calendar.getTime().before(enddate))) {
                                    data.setStatus(object.getString("user_exam_taken_status"));
                                    data.setId(object.getString("tbl_online_exams_id"));
                                    data.setName(object.getString("tbl_online_exam_nm"));
                                    data.setDate(object.getString("tbl_online_exam_date"));
                                    data.setStartTime(object.getString("tbl_online_exam_start_time"));
                                    data.setEndTime(object.getString("tbl_online_exam_end_time"));
                                    data.setSubject(object.getString("subject_name"));
                                    data.setMarks(object.getString("tbl_online_exam_marks"));
                                    data.setNegMarks(object.getString("tbl_online_exam_neg_marks"));
                                    data.setResPublish(object.getString("tbl_online_exam_result_publish"));
                                    data.setEndDate(object.getString("tbl_online_exam_end_date"));
                                    data.setDuration(object.getString("tbl_online_exam_duration"));
                                    data.setType("MCQ");
                                    examArray.add(data);
                                }
                            }
                        } else {
                            if (calendar.getTime().before(startdate) || (calendar.getTime().after(startdate) && calendar.getTime().before(enddate))) {
                                data.setStatus(object.getString("user_exam_taken_status"));
                                data.setId(object.getString("tbl_online_exams_id"));
                                data.setName(object.getString("tbl_online_exam_nm"));
                                data.setDate(object.getString("tbl_online_exam_date"));
                                data.setStartTime(object.getString("tbl_online_exam_start_time"));
                                data.setEndTime(object.getString("tbl_online_exam_end_time"));
                                data.setSubject(object.getString("subject_name"));
                                data.setMarks(object.getString("tbl_online_exam_marks"));
                                data.setNegMarks(object.getString("tbl_online_exam_neg_marks"));
                                data.setResPublish(object.getString("tbl_online_exam_result_publish"));
                                data.setEndDate(object.getString("tbl_online_exam_end_date"));
                                data.setDuration(object.getString("tbl_online_exam_duration"));
                                data.setType("MCQ");
                                examArray.add(data);
                            }
                        }
                    }
                    if (!result.contains("{\"subjectiveExamList\":null}")) {
                        JSONArray array = jsonObject.getJSONArray("subjectiveExamList");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject object = array.getJSONObject(i);
                            OnlineExamData data = new OnlineExamData();
                            String start = object.getString("tbl_online_exam_date");
                            String end = object.getString("tbl_online_exam_end_date");
                            Date startdate = sdf.parse(start);
                            Date enddate = sdf.parse(end);
                            if (userType.equals("Student")) {
                                String sub = jsonObject.getString("DisableSubject");
                                if (!sub.contains(object.getString("subject_name"))) {
                                    if (calendar.getTime().equals(startdate) || calendar.getTime().before(startdate) || (calendar.getTime().after(startdate) && calendar.getTime().before(enddate))) {
                                        data.setId(object.getString("tbl_subjective_online_exams_id"));
                                        data.setName(object.getString("tbl_online_exam_nm"));
                                        data.setDate(object.getString("tbl_online_exam_date"));
                                        data.setStartTime(object.getString("tbl_online_exam_start_time"));
                                        data.setEndTime(object.getString("tbl_online_exam_end_time"));
                                        data.setSubject(object.getString("subject_name"));
                                        data.setEndDate(object.getString("tbl_online_exam_end_date"));
                                        data.setStatus(object.getString("user_exam_taken_status"));
                                        data.setResPublish(object.getString("tbl_online_exam_result_publish"));
                                        data.setQuesPdf(object.getString("tbl_exam_question_pdf"));
                                        data.setAnsPdf(object.getString("tbl_exam_answer_sheat"));
                                        data.setType("Subjective");
                                        Log.e("start", start);
                                        Log.e("end", end);
                                        examArray.add(data);
                                    }
                                }
                            } else {
                                if (calendar.getTime().before(startdate) || (calendar.getTime().after(startdate) && calendar.getTime().before(enddate))) {
                                    data.setId(object.getString("tbl_subjective_online_exams_id"));
                                    data.setName(object.getString("tbl_online_exam_nm"));
                                    data.setDate(object.getString("tbl_online_exam_date"));
                                    data.setStartTime(object.getString("tbl_online_exam_start_time"));
                                    data.setEndTime(object.getString("tbl_online_exam_end_time"));
                                    data.setSubject(object.getString("subject_name"));
                                    data.setEndDate(object.getString("tbl_online_exam_end_date"));
                                    data.setStatus(object.getString("user_exam_taken_status"));
                                    data.setResPublish(object.getString("tbl_online_exam_result_publish"));
                                    data.setQuesPdf(object.getString("tbl_exam_question_pdf"));
                                    data.setAnsPdf(object.getString("tbl_exam_answer_sheat"));
                                    data.setType("Subjective");
                                    examArray.add(data);
                                }
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (examArray.size() > 0)
                    listItems();
                else {
                    listView.setVisibility(View.GONE);
                    noExam.setVisibility(View.VISIBLE);
                }
            } else {
                Toast.makeText(ctx, "No Exam", Toast.LENGTH_SHORT).show();
                noExam.setVisibility(View.VISIBLE);
            }

            super.onPostExecute(result);
        }

    }

    private void listItems() {
        noExam.setVisibility(View.GONE);
        listView.setVisibility(View.VISIBLE);
        Collections.sort(examArray, (o1, o2) -> {
            SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            try {
                return sdf.parse(o1.getDate()).compareTo(sdf.parse(o2.getDate()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return 0;
        });

        OnlineExamAdapter adapter = new OnlineExamAdapter(context, R.layout.list_online_exam, examArray);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public final boolean isInternetOn() {

        ConnectivityManager connec = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

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
