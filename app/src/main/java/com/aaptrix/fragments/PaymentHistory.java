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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.aaptrix.R;
import com.aaptrix.adaptor.PaymentHistoryAdapter;
import com.aaptrix.databeans.FeeData;

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

import static android.content.Context.CONNECTIVITY_SERVICE;
import static com.aaptrix.tools.HttpUrl.STUDENT_FEE_DETAIL;
import static com.aaptrix.tools.SPClass.PREFS_NAME;

public class PaymentHistory extends Fragment {

    private Context context;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private TextView noHistory;
    private ArrayList<FeeData> feeArray = new ArrayList<>();
    private ListView listView;

    public PaymentHistory() {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        this.context = context;
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_payment_history, container, false);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        progressBar = view.findViewById(R.id.progress_bar);
        noHistory = view.findViewById(R.id.no_history);
        listView = view.findViewById(R.id.listview);

        SharedPreferences sp = context.getSharedPreferences(PREFS_NAME, 0);
        String userId = sp.getString("userID", "");
        String schoolId = sp.getString("str_school_id", "");
        String batchNm = sp.getString("userSection", "");

        if (isInternetOn()) {
            GetPaymentHistory getPaymentHistory = new GetPaymentHistory(context);
            getPaymentHistory.execute(schoolId, userId, batchNm);
        }

        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (isInternetOn()) {
                swipeRefreshLayout.setRefreshing(true);
                feeArray.clear();
                GetPaymentHistory getPaymentHistory = new GetPaymentHistory(context);
                getPaymentHistory.execute(schoolId, userId, batchNm);
            }
        });

        return view;
    }

    @SuppressLint("StaticFieldLeak")
    public class GetPaymentHistory extends AsyncTask<String, String, String> {
        Context ctx;

        GetPaymentHistory(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
            listView.setEnabled(false);
        }

        @Override
        protected String doInBackground(String... params) {

            String schoolId = params[0];
            String userId = params[1];
            String userSection = params[2];
            String data;

            try {

                URL url = new URL(STUDENT_FEE_DETAIL);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                data = URLEncoder.encode("tbl_school_id", "UTF-8") + "=" + URLEncoder.encode(schoolId, "UTF-8") + "&" +
                        URLEncoder.encode("tbl_users_id", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8") + "&" +
                        URLEncoder.encode("tbl_batch_nm", "UTF-8") + "=" + URLEncoder.encode(userSection, "UTF-8");
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
            noHistory.setVisibility(View.GONE);
            listView.setEnabled(true);
            if (!result.contains("\"PaidFeesDetails\":null")) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("PaidFeesDetails");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jObject = jsonArray.getJSONObject(i);
                        FeeData data = new FeeData();
                        data.setStatus(jObject.getString("trans_status"));
                        data.setAmount(jObject.getString("tbl_fees_collection_amount"));
                        data.setDate(jObject.getString("tbl_fees_collection_entrydt"));
                        data.setMode(jObject.getString("tbl_fees_collection_mode"));
                        feeArray.add(data);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            if (feeArray.size() == 0) {
                noHistory.setVisibility(View.VISIBLE);
            } else {
                listItems();
            }
        }
    }

    private void listItems() {

        Collections.sort(feeArray, (o1, o2) -> {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            try {
                return sdf.parse(o1.getDate()).compareTo(sdf.parse(o2.getDate()));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            return 0;
        });

        PaymentHistoryAdapter adapter = new PaymentHistoryAdapter(context, R.layout.list_payment_history, feeArray);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public final boolean isInternetOn() {
        ConnectivityManager connec;
        connec = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        assert connec != null;
        return connec.getActiveNetworkInfo() != null && connec.getActiveNetworkInfo().isAvailable() && connec.getActiveNetworkInfo().isConnectedOrConnecting();
    }
}
