package com.aaptrix.activitys.admin;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aaptrix.R;
import com.aaptrix.activitys.AddUserForm;
import com.aaptrix.adaptor.EnquiryAdapter;
import com.aaptrix.databeans.EnquiryData;
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
import java.util.ArrayList;
import java.util.Objects;

import static com.aaptrix.tools.HttpUrl.VIEW_ENQUIRY;
import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREFS_RW;
import static com.aaptrix.tools.SPClass.PREF_COLOR;

public class Enquiries extends AppCompatActivity {

    AppBarLayout appBarLayout;
    String selToolColor, selStatusColor, selTextColor1;
    TextView tool_title;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    TextView no_enquiry;
    ListView listView;
    LinearLayout add_layout;
    ProgressBar progressBar;
    ImageView addEnquiry;
    String userSchoolId;
    ArrayList<EnquiryData> enquiryArray = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enquiries);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        appBarLayout = findViewById(R.id.appBarLayout);
        tool_title = findViewById(R.id.tool_title);
        no_enquiry = findViewById(R.id.no_enquiry);
        listView = findViewById(R.id.listview);
        progressBar = findViewById(R.id.progress_bar);
        mSwipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        add_layout = findViewById(R.id.add_layout);
        addEnquiry = findViewById(R.id.add_enquiry);
        mSwipeRefreshLayout.setRefreshing(false);
        listView.setEnabled(true);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        userSchoolId = settings.getString("str_school_id", "");

        SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
        selToolColor = settingsColor.getString("tool", "");
        selStatusColor = settingsColor.getString("status", "");
        selTextColor1 = settingsColor.getString("text1", "");

        appBarLayout.setBackgroundColor(Color.parseColor(selToolColor));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(selStatusColor));
        }
        tool_title.setTextColor(Color.parseColor(selTextColor1));
        mSwipeRefreshLayout.setColorScheme(R.color.text_gray);
        GradientDrawable bgShape = (GradientDrawable) add_layout.getBackground();
        bgShape.setColor(Color.parseColor(selToolColor));

        SharedPreferences sp = getSharedPreferences(PREFS_RW, 0);
        String json = sp.getString("result", "");

        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONArray jsonArray = jsonObject.getJSONArray("result");
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                if (object.getString("tbl_insti_buzz_cate_name").equals("Enquiries")) {
                    if (object.getString("tbl_scl_inst_buzz_detl_write_status").equals("Active")) {
                        add_layout.setVisibility(View.VISIBLE);
                    } else {
                        add_layout.setVisibility(View.GONE);
                    }
                    break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        addEnquiry.setOnClickListener(v -> {
            Intent intent = new Intent(this, AddUserForm.class);
            startActivity(intent);
        });

        if (isInternetOn()) {
            GetEnquiry enquiry = new GetEnquiry(this);
            enquiry.execute(userSchoolId);
        } else {
            Toast.makeText(this, "No Internet", Toast.LENGTH_SHORT).show();
        }

        mSwipeRefreshLayout.setOnRefreshListener(() -> {
            if (isInternetOn()) {
                enquiryArray.clear();
                GetEnquiry enquiry = new GetEnquiry(this);
                enquiry.execute(userSchoolId);
                mSwipeRefreshLayout.setRefreshing(true);
            } else {
                Toast.makeText(this, "No Internet", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    public class GetEnquiry extends AsyncTask<String, String, String> {
        Context ctx;

        GetEnquiry(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            mSwipeRefreshLayout.setRefreshing(true);
            listView.setEnabled(false);
            progressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            String schoolId = params[0];
            String data;

            try {
                URL url = new URL(VIEW_ENQUIRY);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                data = URLEncoder.encode("tbl_school_id", "UTF-8") + "=" + URLEncoder.encode(schoolId, "UTF-8");
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
            Log.e("result", String.valueOf(result));
            try {
                mSwipeRefreshLayout.setRefreshing(false);
                progressBar.setVisibility(View.GONE);
                if (result != null && !result.equals("{\"result\":null}")) {
                    try {
                        enquiryArray.clear();
                        no_enquiry.setVisibility(View.GONE);
                        listView.setVisibility(View.VISIBLE);
                        JSONObject jsonRootObject = new JSONObject(result);
                        JSONArray jsonArray = jsonRootObject.getJSONArray("result");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jObject = jsonArray.getJSONObject(i);
                            EnquiryData data = new EnquiryData();
                            data.setName(jObject.getString("tbl_enquiry_name"));
                            data.setPhone(jObject.getString("tbl_enquiry_phone"));
                            data.setEmail(jObject.getString("tbl_enquiry_email"));
                            data.setDate(jObject.getString("tbl_enquiry_date"));
                            data.setDetails(jObject.getString("tbl_enquiry_desc"));
                            data.setCourse(jObject.getString("tbl_enquiry_course"));
                            data.setHowto(jObject.getString("tbl_enquiry_howtoknow"));
                            data.setHowtoOther(jObject.getString("tbl_enquiry_howtoknow_other"));
                            enquiryArray.add(data);
                        }
                        listItems();
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(ctx, "Server Issue : 201", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    no_enquiry.setVisibility(View.VISIBLE);
                }
            } catch (IndexOutOfBoundsException e) {
                e.printStackTrace();
            }
            super.onPostExecute(result);
        }

    }

    private void listItems() {
        listView.setEnabled(true);
        EnquiryAdapter adapter = new EnquiryAdapter(this, R.layout.list_enquiry, enquiryArray);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        listView.setOnItemClickListener((parent, view, position, id) -> {
            Intent intent = new Intent(this, EnquiryDetails.class);
            intent.putExtra("name", enquiryArray.get(position).getName());
            intent.putExtra("phone", enquiryArray.get(position).getPhone());
            intent.putExtra("email", enquiryArray.get(position).getEmail());
            intent.putExtra("date", enquiryArray.get(position).getDate());
            intent.putExtra("course", enquiryArray.get(position).getCourse());
            if (enquiryArray.get(position).getHowto().equals("Other")) {
                intent.putExtra("howto", enquiryArray.get(position).getHowtoOther());
            } else {
                intent.putExtra("howto", enquiryArray.get(position).getHowto());
            }
            intent.putExtra("details", enquiryArray.get(position).getDetails());
            startActivity(intent);
        });

    }

    public final boolean isInternetOn() {
        ConnectivityManager connec =
                (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
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
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
    }
}
