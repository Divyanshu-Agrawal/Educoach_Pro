package com.aaptrix.tools;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.text.Html;
import android.widget.Toast;

import androidx.core.app.ShareCompat;

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

import static com.aaptrix.tools.HttpUrl.ABOUT_SCHOOL_INFO;

@SuppressLint("StaticFieldLeak")
public class ShareInstitute extends AsyncTask<String, String, String> {

    private Context ctx;
    private Activity activity;

    public ShareInstitute(Context ctx, Activity activity) {
        this.ctx = ctx;
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {
        String userSchoolId = params[0];
        String data;
        try {
            URL url = new URL(ABOUT_SCHOOL_INFO);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setDoInput(true);

            OutputStream outputStream = httpURLConnection.getOutputStream();

            BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
            data = URLEncoder.encode("schoolId", "UTF-8") + "=" + URLEncoder.encode(userSchoolId, "UTF-8");
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
        if (result.equals("{\"result\":null}")) {
            Toast.makeText(ctx, "No Data", Toast.LENGTH_SHORT).show();
        } else {
            try {
                ArrayList<String> aboutArray = new ArrayList<>();
                JSONObject jsonRootObject = new JSONObject(result);
                JSONArray jsonArray = jsonRootObject.getJSONArray("result");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                    aboutArray.add(jsonObject.getString("tbl_abt_schl_more_info_name"));
                }
                JSONObject jsonObject = jsonArray.getJSONObject(0);
                StringBuilder message = new StringBuilder();
                message.append(jsonObject.getString("tbl_abt_schl_details_name"));
                message.append("\n\n");
                message.append("Courses Offered : ");
                message.append("\n");
                for (int i = 0; i < aboutArray.size(); i++) {
                    message.append(Html.fromHtml(aboutArray.get(i)));
                    message.append("\n");
                }
                message.append("\n");
                message.append("Contact No. : ").append(jsonObject.getString("tbl_abt_schl_details_contact"));
                message.append("\n");
                message.append("Address : ").append(jsonObject.getString("tbl_abt_schl_details_addr"));
                message.append("\n");
                jsonObject.getString("tbl_abt_schl_details_website");
                if (!jsonObject.getString("tbl_abt_schl_details_website").isEmpty()) {
                    message.append("Website : ").append(jsonObject.getString("tbl_abt_schl_details_website"));
                    message.append("\n");
                }

                message.append("\n");
                message.append("Download our android app from here: ");
                message.append("\n");
                message.append("http://play.google.com/store/apps/details?id=" + ctx.getPackageName());

                ShareCompat.IntentBuilder.from(activity)
                        .setType("text/plain")
                        .setChooserTitle("Share via...")
                        .setText(message.toString())
                        .startChooser();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        super.onPostExecute(result);
    }

}
