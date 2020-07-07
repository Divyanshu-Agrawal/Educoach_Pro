package com.aaptrix.activitys.guest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aaptrix.R;
import com.google.android.material.appbar.AppBarLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.squareup.picasso.Picasso;

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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import com.aaptrix.databeans.QuestionData;

import pl.droidsonroids.gif.GifImageView;

import static com.aaptrix.tools.HttpUrl.GUEST_EXAM_DETAILS;
import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREF_COLOR;

public class StartGuestExam extends AppCompatActivity {

    String examId, examName, course;
    AppBarLayout appBarLayout;
    TextView tool_title;
    Chronometer timer;
    RecyclerView recyclerView;
    ArrayList<QuestionData> quesarray = new ArrayList<>();
    RecyclerView.LayoutManager mLayoutManager;
    CardView cardView;
    GifImageView taskStatus;
    RelativeLayout layout;
    AlertDialog alertDialog;
    String schoolId;
    AlertDialog.Builder alert;
    ImageButton viewDialog;
    String selToolColor;
    TextView quesCount;
    int correct = 0, wrong = 0, notAttempt = 0;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start_guest_exam);
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
        recyclerView = findViewById(R.id.recyclerview);
        timer = findViewById(R.id.timer);
        cardView = findViewById(R.id.card_view);
        taskStatus = findViewById(R.id.task_status);
        layout = findViewById(R.id.layout);
        viewDialog = findViewById(R.id.view_dialog);
        quesCount = findViewById(R.id.ques_count);

        layout.setOnClickListener(v -> {

        });

        layout.setOnTouchListener((v, event) -> false);

        examId = getIntent().getStringExtra("examId");
        examName = getIntent().getStringExtra("examName");
        course = getIntent().getStringExtra("course");

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        schoolId = settings.getString("str_school_id", "");
        tool_title.setText(examName);

        timer.setOnChronometerTickListener(chronometer -> {
            long time = SystemClock.elapsedRealtime() - chronometer.getBase();
            int h = (int)(time / 3600000);
            int m = (int)(time - h * 3600000) / 60000;
            int s = (int)(time - h * 3600000 - m * 60000) / 1000 ;
            String t = (h < 10 ? "0" + h: h) + ":" + (m < 10 ? "0" + m: m) + ":" + (s < 10 ? "0" + s: s);
            chronometer.setText(t);
        });
        timer.setBase(SystemClock.elapsedRealtime());
        timer.setText("00:00:00");
        timer.start();

        GetQuestion question = new GetQuestion(this);
        question.execute(schoolId, examId);

        SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
        selToolColor = settingsColor.getString("tool", "");
        String selStatusColor = settingsColor.getString("status", "");
        String selTextColor1 = settingsColor.getString("text1", "");

        appBarLayout.setBackgroundColor(Color.parseColor(selToolColor));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor(selStatusColor));
        }
        tool_title.setTextColor(Color.parseColor(selTextColor1));
        timer.setTextColor(Color.parseColor(selToolColor));

        mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutFrozen(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        recyclerView.addOnItemTouchListener(new RecyclerView.SimpleOnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                return rv.getScrollState() == RecyclerView.SCROLL_STATE_DRAGGING;
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    public class GetQuestion extends AsyncTask<String, String, String> {
        Context ctx;

        GetQuestion(Context ctx) {
            this.ctx = ctx;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {

            String school_id = params[0];
            String examId = params[1];
            String data;

            try {

                URL url = new URL(GUEST_EXAM_DETAILS);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                data = URLEncoder.encode("school_id", "UTF-8") + "=" + URLEncoder.encode(school_id, "UTF-8") + "&" +
                        URLEncoder.encode("exam_id", "UTF-8") + "=" + URLEncoder.encode(examId, "UTF-8");
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
            if (!result.contains("\"QuestionList\":null")) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    JSONArray jsonArray = jsonObject.getJSONArray("QuestionList");
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject object = jsonArray.getJSONObject(i);
                        QuestionData data = new QuestionData();
                        data.setQues(object.getString("tbl_question"));
                        data.setQuesImg(object.getString("tbl_question_img"));
                        data.setQuesId(object.getString("tbl_que_id"));
                        data.setOptionA(object.getString("tbl_ques_options_a"));
                        data.setOptionAImg(object.getString("tbl_ques_options_a_img"));
                        data.setOptionB(object.getString("tbl_ques_options_b"));
                        data.setOptionBImg(object.getString("tbl_ques_options_b_img"));
                        data.setOptionC(object.getString("tbl_ques_options_c"));
                        data.setOptionCImg(object.getString("tbl_ques_options_c_img"));
                        data.setOptionD(object.getString("tbl_ques_options_d"));
                        data.setOptionDImg(object.getString("tbl_ques_options_d_img"));
                        data.setCorrectOption(object.getString("tbl_ques_correct_options"));
                        data.setStatus("0");
                        quesarray.add(data);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (quesarray.size() > 0) {
                    listItems();
                    answerDialog();
                }
            } else {
                Toast.makeText(ctx, "No Question", Toast.LENGTH_SHORT).show();
            }

            super.onPostExecute(result);
        }

    }

    @SuppressLint("SetTextI18n")
    private void listItems() {
        quesCount.setText("Total Questions : " + quesarray.size());
        RecyclerResultAdapter adapter = new RecyclerResultAdapter(this, R.layout.list_start_online_exam, quesarray);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    public final boolean isInternetOn() {

        ConnectivityManager connec = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        assert connec != null;
        if (Objects.requireNonNull(connec.getNetworkInfo(0)).getState() == android.net.NetworkInfo.State.CONNECTED ||
                Objects.requireNonNull(connec.getNetworkInfo(0)).getState() == android.net.NetworkInfo.State.CONNECTING ||
                Objects.requireNonNull(connec.getNetworkInfo(1)).getState() == android.net.NetworkInfo.State.CONNECTING ||
                Objects.requireNonNull(connec.getNetworkInfo(1)).getState() == android.net.NetworkInfo.State.CONNECTED) {

            return true;
        } else if (
                Objects.requireNonNull(connec.getNetworkInfo(0)).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                        Objects.requireNonNull(connec.getNetworkInfo(1)).getState() == android.net.NetworkInfo.State.DISCONNECTED) {
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
        new AlertDialog.Builder(this)
                .setMessage("Are you sure you want to leave? All your progress will be lost.")
                .setPositiveButton("Ok", (dialog, which) -> super.onBackPressed())
                .setNegativeButton("Cancel", null)
                .show();
    }

    private class RecyclerResultAdapter extends RecyclerView.Adapter<RecyclerResultAdapter.ViewHolder> {

        private Context context;
        private int resource;
        private ArrayList<QuestionData> objects;
        ArrayList<QuestionData> arrayList = new ArrayList<>();

        RecyclerResultAdapter(Context context, int resource, ArrayList<QuestionData> objects) {
            this.context = context;
            this.resource = resource;
            this.objects = objects;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView ques, quesNo;
            ImageView quesImg;
            Button next, previous, markAsReview;
            TextView optionA, optionB, optionC, optionD, marked;
            ImageView optionAImg, optionBImg, optionCImg, optionDImg;
            CardView optionACard, optionBCard, optionCCard, optionDCard;
            LinearLayout optionALayout, optionBLayout, optionCLayout, optionDLayout;

            ViewHolder(@NonNull View view) {
                super(view);
                ques = view.findViewById(R.id.ques);
                quesNo = view.findViewById(R.id.ques_no);
                quesImg = view.findViewById(R.id.quesImg);
                next = view.findViewById(R.id.next);
                previous = view.findViewById(R.id.previous);
                markAsReview = view.findViewById(R.id.mark_as_review);
                optionA = view.findViewById(R.id.optionA);
                optionB = view.findViewById(R.id.optionB);
                optionC = view.findViewById(R.id.optionC);
                optionD = view.findViewById(R.id.optionD);
                optionAImg = view.findViewById(R.id.optionAImg);
                optionBImg = view.findViewById(R.id.optionBImg);
                optionCImg = view.findViewById(R.id.optionCImg);
                optionDImg = view.findViewById(R.id.optionDImg);
                optionACard = view.findViewById(R.id.optionACard);
                optionBCard = view.findViewById(R.id.optionBCard);
                optionCCard = view.findViewById(R.id.optionCCard);
                optionDCard = view.findViewById(R.id.optionDCard);
                optionALayout = view.findViewById(R.id.optionALayout);
                optionBLayout = view.findViewById(R.id.optionBLayout);
                optionCLayout = view.findViewById(R.id.optionCLayout);
                optionDLayout = view.findViewById(R.id.optionDLayout);
                marked = view.findViewById(R.id.marked);
            }
        }

        @NonNull
        @Override
        public RecyclerResultAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(context).inflate(resource, parent, false);
            return new RecyclerResultAdapter.ViewHolder(itemView);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull RecyclerResultAdapter.ViewHolder holder, int position) {

            SharedPreferences sp = context.getSharedPreferences(PREFS_NAME, 0);
            String schoolId = sp.getString("str_school_id", "");
            QuestionData data = objects.get(position);
            holder.markAsReview.setVisibility(View.GONE);

            if (data.getQuesImg().equals("0") || data.getQuesImg().isEmpty() || data.getQuesImg().equals("null")) {
                holder.quesNo.setText(Html.fromHtml("<font color='" + selToolColor + "'>(Q:" + (position + 1) + ") </font>"));
                holder.ques.setText(Html.fromHtml(data.getQues()));
            } else {
                holder.quesNo.setText(Html.fromHtml("<font color='" + selToolColor + "'>(Q:" + (position + 1) + ") </font>"));
                holder.quesImg.setVisibility(View.VISIBLE);
                String url = sp.getString("imageUrl", "") + schoolId + "/onlineExam/" + data.getQuesImg();
                Picasso.with(context).load(url).into(holder.quesImg);
            }

            if (data.getOptionAImg().equals("0") || data.getOptionAImg().isEmpty() || data.getOptionAImg().equals("null")) {
                holder.optionA.setText("A: " + Html.fromHtml(data.getOptionA()));
            } else {
                holder.optionA.setVisibility(View.GONE);
                holder.optionAImg.setVisibility(View.VISIBLE);
                String url = sp.getString("imageUrl", "") + schoolId + "/onlineExam/" + data.getOptionAImg();
                Picasso.with(context).load(url).into(holder.optionAImg);
            }

            if (data.getOptionBImg().equals("0") || data.getOptionBImg().isEmpty() || data.getOptionBImg().equals("null")) {
                holder.optionB.setText("B: " + Html.fromHtml(data.getOptionB()));
            } else {
                holder.optionB.setVisibility(View.GONE);
                holder.optionBImg.setVisibility(View.VISIBLE);
                String url = sp.getString("imageUrl", "") + schoolId + "/onlineExam/" + data.getOptionBImg();
                Picasso.with(context).load(url).into(holder.optionBImg);
            }

            if (data.getOptionCImg().equals("0") || data.getOptionCImg().isEmpty() || data.getOptionCImg().equals("null")) {
                holder.optionC.setText("C: " + Html.fromHtml(data.getOptionC()));
            } else {
                holder.optionC.setVisibility(View.GONE);
                holder.optionCImg.setVisibility(View.VISIBLE);
                String url = sp.getString("imageUrl", "") + schoolId + "/onlineExam/" + data.getOptionCImg();
                Picasso.with(context).load(url).into(holder.optionCImg);
            }

            if (data.getOptionDImg().equals("0") || data.getOptionDImg().isEmpty() || data.getOptionDImg().equals("null")) {
                holder.optionD.setText("D: " + Html.fromHtml(data.getOptionD()));
            } else {
                holder.optionD.setVisibility(View.GONE);
                holder.optionDImg.setVisibility(View.VISIBLE);
                String url = sp.getString("imageUrl", "") + schoolId + "/onlineExam/" + data.getOptionDImg();
                Picasso.with(context).load(url).into(holder.optionDImg);
            }

            holder.optionALayout.setOnClickListener(v -> {
                QuestionData que = new QuestionData();
                que.setTbl_que_id(data.getQuesId());
                que.setTbl_user_answer("a");
                que.setCorrectOption(data.getCorrectOption());
                for (int i = 0; i < arrayList.size(); i++) {
                    if (arrayList.get(i).getTbl_que_id().equals(data.getQuesId())) {
                        arrayList.remove(i);
                        break;
                    }
                }
                arrayList.add(que);
                saveDataInSP(arrayList);
                quesarray.get(holder.getAdapterPosition()).setStatus("1");
                answerDialog();
                holder.optionACard.setCardBackgroundColor(Color.parseColor(selToolColor));
                holder.optionBCard.setCardBackgroundColor(Color.WHITE);
                holder.optionCCard.setCardBackgroundColor(Color.WHITE);
                holder.optionDCard.setCardBackgroundColor(Color.WHITE);
            });

            holder.optionBLayout.setOnClickListener(v -> {
                QuestionData que = new QuestionData();
                que.setTbl_que_id(data.getQuesId());
                que.setTbl_user_answer("b");
                que.setCorrectOption(data.getCorrectOption());
                for (int i = 0; i < arrayList.size(); i++) {
                    if (arrayList.get(i).getTbl_que_id().equals(data.getQuesId())) {
                        arrayList.remove(i);
                        break;
                    }
                }
                arrayList.add(que);
                saveDataInSP(arrayList);
                quesarray.get(holder.getAdapterPosition()).setStatus("1");
                answerDialog();
                holder.optionBCard.setCardBackgroundColor(Color.parseColor(selToolColor));
                holder.optionACard.setCardBackgroundColor(Color.WHITE);
                holder.optionCCard.setCardBackgroundColor(Color.WHITE);
                holder.optionDCard.setCardBackgroundColor(Color.WHITE);
            });

            holder.optionCLayout.setOnClickListener(v -> {
                QuestionData que = new QuestionData();
                que.setTbl_que_id(data.getQuesId());
                que.setTbl_user_answer("c");
                que.setCorrectOption(data.getCorrectOption());
                for (int i = 0; i < arrayList.size(); i++) {
                    if (arrayList.get(i).getTbl_que_id().equals(data.getQuesId())) {
                        arrayList.remove(i);
                        break;
                    }
                }
                arrayList.add(que);
                saveDataInSP(arrayList);
                quesarray.get(holder.getAdapterPosition()).setStatus("1");
                answerDialog();
                holder.optionCCard.setCardBackgroundColor(Color.parseColor(selToolColor));
                holder.optionBCard.setCardBackgroundColor(Color.WHITE);
                holder.optionACard.setCardBackgroundColor(Color.WHITE);
                holder.optionDCard.setCardBackgroundColor(Color.WHITE);
            });

            holder.optionDLayout.setOnClickListener(v -> {
                QuestionData que = new QuestionData();
                que.setTbl_que_id(data.getQuesId());
                que.setTbl_user_answer("d");
                que.setCorrectOption(data.getCorrectOption());
                for (int i = 0; i < arrayList.size(); i++) {
                    if (arrayList.get(i).getTbl_que_id().equals(data.getQuesId())) {
                        arrayList.remove(i);
                        break;
                    }
                }
                arrayList.add(que);
                saveDataInSP(arrayList);
                quesarray.get(holder.getAdapterPosition()).setStatus("1");
                answerDialog();
                holder.optionDCard.setCardBackgroundColor(Color.parseColor(selToolColor));
                holder.optionBCard.setCardBackgroundColor(Color.WHITE);
                holder.optionCCard.setCardBackgroundColor(Color.WHITE);
                holder.optionACard.setCardBackgroundColor(Color.WHITE);
            });

            if (objects.size() - 1 == position) {
                holder.next.setText("Finish");
            }

            holder.previous.setOnClickListener(v -> {
                if (position != 0) {
                    recyclerView.smoothScrollToPosition(position - 1);
                }
            });

            holder.next.setOnClickListener(v -> {
                if (position == objects.size() - 1) {
                    SharedPreferences preferences = context.getSharedPreferences("onlineArray", 0);
                    if (isInternetOn()) {
                        SharedPreferences prefs = context.getSharedPreferences("lead_prefs", 0);
                        if (!prefs.getBoolean("status", false)) {
                            new AlertDialog.Builder(context)
                                    .setMessage("Are you sure you want to submit. Once submitted no changes can be made.")
                                    .setPositiveButton("Submit", (dialog, which) -> {
                                        Gson gson = new GsonBuilder().create();
                                        long millis = SystemClock.elapsedRealtime() - timer.getBase();
                                        String hms = String.format(Locale.getDefault(), "%02d:%02d:%02d", TimeUnit.MILLISECONDS.toHours(millis),
                                                TimeUnit.MILLISECONDS.toMinutes(millis) % TimeUnit.HOURS.toMinutes(1),
                                                TimeUnit.MILLISECONDS.toSeconds(millis) % TimeUnit.MINUTES.toSeconds(1));
                                        JsonArray myCustomArray = gson.toJsonTree(quesarray).getAsJsonArray();
                                        Intent intent = new Intent(context, GenerateLead.class);
                                        intent.putExtra("exam_name", examName);
                                        intent.putExtra("exam_id", examId);
                                        intent.putExtra("course", course);
                                        intent.putExtra("time", hms);
                                        intent.putExtra("total_ques", quesarray.size() + "");
                                        intent.putExtra("array", preferences.getString("onlineExamArray", ""));
                                        intent.putExtra("ques_array", myCustomArray.toString());
                                        startActivity(intent);
                                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                    })
                                    .setNegativeButton("Cancel", null)
                                    .show();
                        } else {
                            ArrayList<QuestionData> arrayList = new ArrayList<>();
                            try {
                                JSONArray jsonArray = new JSONArray(preferences.getString("onlineExamArray", ""));
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    QuestionData questionData = new QuestionData();
                                    questionData.setCorrectOption(jsonObject.getString("correctOption"));
                                    questionData.setTbl_user_answer(jsonObject.getString("tbl_user_answer"));
                                    questionData.setTbl_que_id(jsonObject.getString("tbl_que_id"));
                                    arrayList.add(questionData);
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            for (int i = 0; i < arrayList.size(); i++) {
                                if (arrayList.get(i).getTbl_user_answer().toLowerCase().equals(arrayList.get(i).getCorrectOption().toLowerCase())) {
                                    correct++;
                                } else {
                                    wrong++;
                                }
                            }

                            notAttempt = quesarray.size() - arrayList.size();
                            new AlertDialog.Builder(context)
                                    .setMessage("Are you sure you want to submit. Once submitted no changes can be made.")
                                    .setPositiveButton("Submit", (dialog, which) -> {
                                        Gson gson = new GsonBuilder().create();
                                        JsonArray myCustomArray = gson.toJsonTree(quesarray).getAsJsonArray();
                                        Intent i = new Intent(context, GuestExamReport.class);
                                        i.putExtra("examName", examName);
                                        i.putExtra("correct", correct + "");
                                        i.putExtra("wrong", wrong + "");
                                        i.putExtra("not_attempted", notAttempt + "");
                                        i.putExtra("total_ques", quesarray.size() + "");
                                        i.putExtra("ques_array", myCustomArray.toString());
                                        i.putExtra("ans_array", preferences.getString("onlineExamArray", ""));
                                        startActivity(i);
                                        finish();
                                        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
                                    })
                                    .setNegativeButton("Cancel", null)
                                    .show();
                        }
                    } else {
                        Toast.makeText(context, "Please connect to internet", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    recyclerView.smoothScrollToPosition(position + 1);
                }
            });

        }

        private void saveDataInSP(ArrayList<QuestionData> quesArray) {
            Gson gson = new GsonBuilder().create();
            JsonArray myCustomArray = gson.toJsonTree(quesArray).getAsJsonArray();
            SharedPreferences sp_attend = context.getSharedPreferences("onlineArray", 0);
            SharedPreferences.Editor se_attend = sp_attend.edit();
            se_attend.clear();
            se_attend.putString("onlineExamArray", "" + myCustomArray);
            se_attend.putString("online_exam_array", "" + quesArray.size());
            se_attend.apply();

        }

        @Override
        public int getItemCount() {
            return objects.size();
        }

    }

    private void answerDialog() {
        LayoutInflater factory = LayoutInflater.from(this);
        @SuppressLint("InflateParams") final View view = factory.inflate(R.layout.answer_dialog, null);

        GridView gridView = view.findViewById(R.id.grid_view);

        DialogAdapter adapter = new DialogAdapter(this, R.layout.grid_item, quesarray);
        gridView.setAdapter(adapter);

        alert = new AlertDialog.Builder(this, R.style.DialogTheme);
        alert.setTitle("").setView(view).setPositiveButton("Ok",
                (dialog, whichButton) -> alertDialog.dismiss());
        alertDialog = alert.create();

        viewDialog.setOnClickListener(v -> alertDialog.show());

        gridView.setOnItemClickListener((parent, v, position, id) -> {
            recyclerView.smoothScrollToPosition(position);
            alertDialog.dismiss();
        });
    }

    public class DialogAdapter extends ArrayAdapter<QuestionData> {

        private Context context;
        private int resource;
        private ArrayList<QuestionData> objects;

        DialogAdapter(@NonNull Context context, int resource, @NonNull ArrayList<QuestionData> objects) {
            super(context, resource, objects);
            this.context = context;
            this.resource = resource;
            this.objects = objects;
        }

        @SuppressLint("ViewHolder")
        @NonNull
        @Override
        public View getView(int position, View view, @NonNull ViewGroup parent) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            view = inflater.inflate(resource, null);
            CardView cardView = view.findViewById(R.id.card_view);
            TextView quesPosition = view.findViewById(R.id.position);
            QuestionData data = objects.get(position);

            quesPosition.setText(String.valueOf(position + 1));

            switch (data.getStatus()) {
                case "0":
                    cardView.setCardBackgroundColor(Color.WHITE);
                    quesPosition.setTextColor(Color.BLACK);
                    break;
                case "1":
                    cardView.setCardBackgroundColor(getResources().getColor(android.R.color.holo_green_dark));
                    quesPosition.setTextColor(Color.WHITE);
                    break;
            }

            return view;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }
    }
}