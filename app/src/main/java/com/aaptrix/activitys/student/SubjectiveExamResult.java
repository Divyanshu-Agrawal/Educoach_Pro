package com.aaptrix.activitys.student;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.PermissionChecker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aaptrix.R;
import com.google.android.material.appbar.AppBarLayout;

import org.json.JSONArray;
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
import java.util.Objects;

import static com.aaptrix.tools.HttpUrl.SUBJECTIVE_RESULT;
import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREF_COLOR;

public class SubjectiveExamResult extends AppCompatActivity {

    AppBarLayout appBarLayout;
    TextView tool_title;
    LinearLayout layout;
    long downloadID;
    TextView marks, noAnswer;
    String strpdf, id, strAnsPdf, strCorrectPdf, strUserAnsPdf, strDownloadQues, strDownloadAns;
    CardView quesLayout, ansLayout, userAnsLayout, correctAnsLayout;
    WebView quesPreview, ansPreview, userAnsPreview, correctAnsPreview;
    ImageView viewQues, viewAns, viewUserAns, viewCorrectAns, downloadQues, downloadAns, downloadUserAns, downloadCorrectAns;
    TextView quesTitle, ansTitle, userAnsTitle, correctAnsTitle;
    RelativeLayout layout1, layout2, layout3, layout4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subjective_exam_result);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("");
        setResult(RESULT_OK);
        appBarLayout = findViewById(R.id.appBarLayout);
        tool_title = findViewById(R.id.tool_title);
        marks = findViewById(R.id.marks);
        layout = findViewById(R.id.layout);
        noAnswer = findViewById(R.id.no_answer);
        quesTitle = findViewById(R.id.ques_title);
        ansTitle = findViewById(R.id.ans_title);
        userAnsTitle = findViewById(R.id.user_ans_title);
        correctAnsTitle = findViewById(R.id.correct_ans_title);
        layout1 = findViewById(R.id.layout1);
        layout2 = findViewById(R.id.layout2);
        layout3 = findViewById(R.id.layout3);
        layout4 = findViewById(R.id.layout4);

        quesLayout = findViewById(R.id.ques_paper_layout);
        ansLayout = findViewById(R.id.ans_layout);
        userAnsLayout = findViewById(R.id.user_ans_layout);
        correctAnsLayout = findViewById(R.id.correct_ans_layout);

        quesPreview = findViewById(R.id.ques_preview);
        ansPreview = findViewById(R.id.ans_preview);
        userAnsPreview = findViewById(R.id.user_ans_preview);
        correctAnsPreview = findViewById(R.id.correct_ans_preview);

        viewAns = findViewById(R.id.view_ans);
        viewCorrectAns = findViewById(R.id.view_correct_ans);
        viewQues = findViewById(R.id.view_ques);
        viewUserAns = findViewById(R.id.view_user_ans);

        downloadAns = findViewById(R.id.download_ans);
        downloadCorrectAns = findViewById(R.id.download_correct_ans);
        downloadQues = findViewById(R.id.download_ques);
        downloadUserAns = findViewById(R.id.download_user_ans);

        tool_title.setText(getIntent().getStringExtra("examName"));
        registerReceiver(onDownloadComplete, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));

        id = getIntent().getStringExtra("id");

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        String schoolid = settings.getString("str_school_id", "");
        String userId = settings.getString("userID", "");

        GetExam exam = new GetExam(this);
        exam.execute(schoolid, id, userId);

        SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
        String selToolColor = settingsColor.getString("tool", "");
        String selStatusColor = settingsColor.getString("status", "");
        String selTextColor1 = settingsColor.getString("text1", "");

        appBarLayout.setBackgroundColor(Color.parseColor(selToolColor));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(selStatusColor));
        }
        tool_title.setTextColor(Color.parseColor(selTextColor1));
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
        }

        @Override
        protected String doInBackground(String... params) {

            String school_id = params[0];
            String exam_id = params[1];
            String userId = params[2];
            String data;

            Log.e("exam", exam_id);
            Log.e("user", userId);
            Log.e("sch", school_id);

            try {

                URL url = new URL(SUBJECTIVE_RESULT);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                data = URLEncoder.encode("school_id", "UTF-8") + "=" + URLEncoder.encode(school_id, "UTF-8") + "&" +
                        URLEncoder.encode("exam_id", "UTF-8") + "=" + URLEncoder.encode(exam_id, "UTF-8") + "&" +
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

        @SuppressLint("SetTextI18n")
        @Override
        protected void onPostExecute(String result) {
            Log.e("res", result);
            if (!result.contains("{\"UserResult\":null}")) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("UserResult");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        strAnsPdf = object.getString("tbl_exam_answer_sheet");
                        strpdf = object.getString("tbl_exam_question_pdf");
                        strCorrectPdf = object.getString("tbl_user_checked_answer_sheet");
                        strUserAnsPdf = object.getString("tbl_user_answer_sheet");
                        strDownloadAns = object.getString("tbl_download_answer_sheet");
                        strDownloadQues = object.getString("tbl_download_question_sheet");
                        marks.setText("Marks Obtained : " + object.getString("tbl_user_marks"));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                setPdf();
            } else {
                noAnswer.setVisibility(View.VISIBLE);
                layout.setVisibility(View.GONE);
            }
            super.onPostExecute(result);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void setPdf() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        String url = settings.getString("imageUrl", "") + settings.getString("userSchoolId", "") + "/subjectiveExam/exam_" + id + "/";

        if (strpdf.equals("0"))
            quesLayout.setVisibility(View.GONE);
        else {
            quesLayout.setVisibility(View.VISIBLE);
            quesPreview.setVisibility(View.VISIBLE);
            quesPreview.loadUrl("https://docs.google.com/viewerng/viewer?url=" + url + strpdf.replace(" ", "%20"));
        }

        if (strAnsPdf.equals("0"))
            ansLayout.setVisibility(View.GONE);
        else {
            ansLayout.setVisibility(View.VISIBLE);
            ansPreview.setVisibility(View.VISIBLE);
            ansPreview.loadUrl("https://docs.google.com/viewerng/viewer?url=" + url + strAnsPdf);
        }

        if (strUserAnsPdf.equals("0"))
            userAnsLayout.setVisibility(View.GONE);
        else {
            userAnsLayout.setVisibility(View.VISIBLE);
            userAnsPreview.setVisibility(View.VISIBLE);
            userAnsPreview.loadUrl("https://docs.google.com/viewerng/viewer?url=" + url + "answerSheet/" + strUserAnsPdf);
        }

        if (strCorrectPdf.equals("0"))
            correctAnsLayout.setVisibility(View.GONE);
        else {
            correctAnsLayout.setVisibility(View.VISIBLE);
            correctAnsPreview.setVisibility(View.VISIBLE);
            correctAnsPreview.loadUrl("https://docs.google.com/viewerng/viewer?url=" + url + "checkedAnswerSheet/" + strCorrectPdf);
        }

        if (strDownloadAns.equals("0"))
            downloadAns.setVisibility(View.GONE);
        else
            downloadAns.setVisibility(View.VISIBLE);

        if (strDownloadQues.equals("0"))
            downloadQues.setVisibility(View.GONE);
        else
            downloadQues.setVisibility(View.VISIBLE);

        viewQues.setOnClickListener(v -> {
            Intent intent = new Intent(this, SubjectExamView.class);
            intent.putExtra("url", url + strpdf);
            intent.putExtra("download", strDownloadQues);
            intent.putExtra("type", "Question Paper");
            startActivity(intent);
        });

        viewUserAns.setOnClickListener(v -> {
            Intent intent = new Intent(this, SubjectExamView.class);
            intent.putExtra("url", url + "answerSheet/" + strUserAnsPdf);
            intent.putExtra("download", "1");
            intent.putExtra("type", "Uploaded Answer Sheet");
            startActivity(intent);
        });

        viewAns.setOnClickListener(v -> {
            Intent intent = new Intent(this, SubjectExamView.class);
            intent.putExtra("url", url + strAnsPdf);
            intent.putExtra("download", strDownloadAns);
            intent.putExtra("type", "Ideal Answer Sheet");
            startActivity(intent);
        });

        viewCorrectAns.setOnClickListener(v -> {
            Intent intent = new Intent(this, SubjectExamView.class);
            intent.putExtra("url", url + "checkedAnswerSheet/" + strCorrectPdf);
            intent.putExtra("download", "1");
            intent.putExtra("type", "Checked Answer Sheet");
            startActivity(intent);
        });

        layout1.setOnClickListener((v) -> {
            Intent intent = new Intent(this, SubjectExamView.class);
            intent.putExtra("url", url + strpdf);
            intent.putExtra("download", strDownloadQues);
            intent.putExtra("type", "Question Paper");
            startActivity(intent);
        });

        layout3.setOnClickListener(v -> {
            Intent intent = new Intent(this, SubjectExamView.class);
            intent.putExtra("url", url + "answerSheet/" + strUserAnsPdf);
            intent.putExtra("download", "1");
            intent.putExtra("type", "Uploaded Answer Sheet");
            startActivity(intent);
        });

        layout2.setOnClickListener((v) -> {
            Intent intent = new Intent(this, SubjectExamView.class);
            intent.putExtra("url", url + strAnsPdf);
            intent.putExtra("download", strDownloadAns);
            intent.putExtra("type", "Ideal Answer Sheet");
            startActivity(intent);
        });

        layout4.setOnClickListener((v) -> {
            Intent intent = new Intent(this, SubjectExamView.class);
            intent.putExtra("url", url + "checkedAnswerSheet/" + strCorrectPdf);
            intent.putExtra("download", "1");
            intent.putExtra("type", "Checked Answer Sheet");
            startActivity(intent);
        });

        downloadQues.setOnClickListener(v -> {
            if (isInternetOn()) {
                if (PermissionChecker.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_GRANTED) {
                    downloadFile(url + strpdf);
                } else {
                    isPermissionGranted();
                }
            } else {
                Toast.makeText(this, "No Internet", Toast.LENGTH_SHORT).show();
            }
        });

        downloadAns.setOnClickListener(v -> {
            if (isInternetOn()) {
                if (PermissionChecker.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_GRANTED) {
                    downloadFile(url + strAnsPdf);
                } else {
                    isPermissionGranted();
                }
            } else {
                Toast.makeText(this, "No Internet", Toast.LENGTH_SHORT).show();
            }
        });

        downloadUserAns.setOnClickListener(v -> {
            if (isInternetOn()) {
                if (PermissionChecker.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_GRANTED) {
                    downloadFile(url + strUserAnsPdf);
                } else {
                    isPermissionGranted();
                }
            } else {
                Toast.makeText(this, "No Internet", Toast.LENGTH_SHORT).show();
            }
        });

        downloadCorrectAns.setOnClickListener(v -> {
            if (isInternetOn()) {
                if (PermissionChecker.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PermissionChecker.PERMISSION_GRANTED) {
                    downloadFile(url + strCorrectPdf);
                } else {
                    isPermissionGranted();
                }
            } else {
                Toast.makeText(this, "No Internet", Toast.LENGTH_SHORT).show();
            }
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

    public void isPermissionGranted() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
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

    private BroadcastReceiver onDownloadComplete = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            if (downloadID == id) {
                Toast.makeText(SubjectiveExamResult.this, "Download Completed", Toast.LENGTH_SHORT).show();
            }
        }
    };

    private void downloadFile(String url) {
        String path = Environment.DIRECTORY_DOWNLOADS;
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url))
                .setTitle(url)
                .setDescription("Downloading")
                .setMimeType("application/octet-stream")
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalPublicDir(path, url);
        request.allowScanningByMediaScanner();
        DownloadManager downloadManager = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
        assert downloadManager != null;
        downloadID = downloadManager.enqueue(request);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(onDownloadComplete);
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
}