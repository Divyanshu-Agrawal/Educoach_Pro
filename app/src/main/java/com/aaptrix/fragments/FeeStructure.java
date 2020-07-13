package com.aaptrix.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.aaptrix.R;
import com.aaptrix.activitys.student.UpiPayment;
import com.aaptrix.adaptor.FeeAdapter;
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
import java.util.ArrayList;

import static android.content.Context.CONNECTIVITY_SERVICE;
import static com.aaptrix.tools.HttpUrl.STUDENT_FEE_DETAIL;
import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREF_COLOR;
import static com.aaptrix.tools.SPClass.PREFS_RW;

public class FeeStructure extends Fragment {

    private Context context;
    private Button payFee;
    private String upiId = "";
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private TextView noFee;
    private ArrayList<FeeData> feeArray = new ArrayList<>();
    private ListView listView;
    private int paidAmount = 0;

    public FeeStructure() {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        this.context = context;
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_fee_structure, container, false);
        payFee = view.findViewById(R.id.pay_fees);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        progressBar = view.findViewById(R.id.progress_bar);
        noFee = view.findViewById(R.id.no_fee);
        listView = view.findViewById(R.id.listview);

        SharedPreferences sp = context.getSharedPreferences(PREFS_NAME, 0);
        String userId = sp.getString("userID", "");
        String schoolId = sp.getString("str_school_id", "");
        String batchNm = sp.getString("userSection", "");

        if (isInternetOn()) {
            GetFeeStruture getFeeStruture = new GetFeeStruture(context);
            getFeeStruture.execute(schoolId, userId, batchNm);
        }

        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (isInternetOn()) {
                paidAmount = 0;
                swipeRefreshLayout.setRefreshing(true);
                feeArray.clear();
                GetFeeStruture getFeeStruture = new GetFeeStruture(context);
                getFeeStruture.execute(schoolId, userId, batchNm);
            }
        });

        SharedPreferences permission = context.getSharedPreferences(PREFS_RW, 0);
        if (!permission.getString("Online Fee Payment", "").equals("Active")) {
            payFee.setVisibility(View.GONE);
        }

        SharedPreferences settingsColor = context.getSharedPreferences(PREF_COLOR, 0);
        String selToolColor = settingsColor.getString("tool", "");
        String selTextColor1 = settingsColor.getString("text1", "");

        payFee.setTextColor(Color.parseColor(selTextColor1));
        payFee.setBackgroundColor(Color.parseColor(selToolColor));

        return view;
    }

    @SuppressLint("StaticFieldLeak")
    public class GetFeeStruture extends AsyncTask<String, String, String> {
        Context ctx;

        GetFeeStruture(Context ctx) {
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
            noFee.setVisibility(View.GONE);
            listView.setEnabled(true);
            if (!result.contains("\"AssignFeesDetails\":null")) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    upiId = jsonObject.getString("school_upi_id");
                    JSONArray jsonArray = jsonObject.getJSONArray("AssignFeesDetails");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jObject = jsonArray.getJSONObject(i);
                        FeeData data = new FeeData();
                        data.setType(jObject.getString("tbl_fees_category_nm"));
                        data.setAmount(jObject.getString("tbl_fees_category_amount"));
                        data.setAdjusted(jsonObject.getString("fees_adjustment_amount"));
                        feeArray.add(data);
                    }
                    if (!result.contains("\"PaidFeesDetails\":null")) {
                        JSONArray paidArray = jsonObject.getJSONArray("PaidFeesDetails");
                        for (int i = 0; i < paidArray.length(); i++) {
                            JSONObject object = paidArray.getJSONObject(i);
                            if (object.getString("trans_status").equals("SUCCESS"))
                                paidAmount = paidAmount + Integer.valueOf(object.getString("tbl_fees_collection_amount"));
                        }
                    }
                    listItems();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                noFee.setVisibility(View.VISIBLE);
            }
        }
    }

    private void listItems() {

        int total = 0;

        for (int i = 0; i < feeArray.size(); i++) {
            total = total + Integer.parseInt(feeArray.get(i).getAmount().trim());
        }

        FeeData data = new FeeData();
        data.setType("Total");
        data.setAmount(String.valueOf(total));
        feeArray.add(data);

        data = new FeeData();
        data.setType("Adjusted Amount");
        data.setAmount(feeArray.get(0).getAdjusted());
        feeArray.add(data);

        data = new FeeData();
        data.setType("Paid Amount");
        data.setAmount(String.valueOf(paidAmount));
        feeArray.add(data);

        data = new FeeData();
        total = total + Integer.valueOf(feeArray.get(0).getAdjusted());
        total = total - paidAmount;
        data.setType("Final Amount");
        data.setAmount(String.valueOf(total));
        feeArray.add(data);

        FeeAdapter adapter = new FeeAdapter(context, R.layout.list_fee_structure, feeArray);
        listView.setAdapter(adapter);
        adapter.notifyDataSetChanged();

        payFee.setOnClickListener(v -> {
            if (!TextUtils.isEmpty(upiId)) {
                Intent intent = new Intent(context, UpiPayment.class);
                intent.putExtra("upi_id", upiId);
                startActivity(intent);
            }
        });


    }

    public final boolean isInternetOn() {
        ConnectivityManager connec;
        connec = (ConnectivityManager) context.getSystemService(CONNECTIVITY_SERVICE);
        assert connec != null;
        return connec.getActiveNetworkInfo() != null && connec.getActiveNetworkInfo().isAvailable() && connec.getActiveNetworkInfo().isConnectedOrConnecting();

    }
}
