package com.aaptrix.activitys.student;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aaptrix.R;
import com.aaptrix.databeans.QuestionData;
import com.google.android.material.appbar.AppBarLayout;
import com.squareup.picasso.Picasso;

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
import java.util.ArrayList;
import java.util.Objects;

import static com.aaptrix.tools.HttpUrl.ONLINE_EXAM_QUES;
import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREF_COLOR;

public class OnlineExamResult extends AppCompatActivity {

    AppBarLayout appBarLayout;
    TextView tool_title;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    String selToolColor;
    TextView noResult;
    ArrayList<QuestionData> quesarray = new ArrayList<>(), userArray = new ArrayList<>();
    RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_online_exam_result);
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
        progressBar = findViewById(R.id.progress_bar);
        noResult = findViewById(R.id.no_result);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        String schoolId = settings.getString("str_school_id", "");
        String userId = getIntent().getStringExtra("userId");
        String examId = getIntent().getStringExtra("examId");
        tool_title.setText(getIntent().getStringExtra("examName"));

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

        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        if (isInternetOn()) {
            GetQuestion getQuestion = new GetQuestion(this);
            getQuestion.execute(schoolId, examId, userId);
        }
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
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {

            String school_id = params[0];
            String examId = params[1];
            String userId = params[2];
            String data;

            try {

                URL url = new URL(ONLINE_EXAM_QUES);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                OutputStream outputStream = httpURLConnection.getOutputStream();

                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
                data = URLEncoder.encode("school_id", "UTF-8") + "=" + URLEncoder.encode(school_id, "UTF-8") + "&" +
                        URLEncoder.encode("exam_id", "UTF-8") + "=" + URLEncoder.encode(examId, "UTF-8") + "&" +
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
            Log.e("result", result);
            progressBar.setVisibility(View.GONE);
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
                        quesarray.add(data);
                    }
                    JSONArray array = jsonObject.getJSONArray("UserResult");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject object = array.getJSONObject(i);
                        QuestionData data = new QuestionData();
                        data.setTbl_que_id(object.getString("tbl_que_id"));
                        data.setTbl_user_answer(object.getString("tbl_user_answer"));
                        userArray.add(data);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (quesarray.size() > 0)
                    listItems();
                else
                    noResult.setVisibility(View.VISIBLE);
            } else {
                Toast.makeText(ctx, "No Result", Toast.LENGTH_SHORT).show();
                noResult.setVisibility(View.VISIBLE);
            }

            super.onPostExecute(result);
        }

    }

    private void listItems() {
        RecyclerResultAdapter adapter = new RecyclerResultAdapter(this, R.layout.list_online_result, quesarray, userArray);
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
        super.onBackPressed();
    }

    private class RecyclerResultAdapter extends RecyclerView.Adapter<RecyclerResultAdapter.ViewHolder> {

        private Context context;
        private int resource;
        private ArrayList<QuestionData> objects, users;

        RecyclerResultAdapter(Context context, int resource, ArrayList<QuestionData> objects, ArrayList<QuestionData> users) {
            this.context = context;
            this.resource = resource;
            this.objects = objects;
            this.users = users;
        }

        @Override
        public int getItemViewType(int position) {
            return position;
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            TextView ques, optionA, optionB, optionC, optionD, quesNo, marked;
            ImageView quesImg, optionAImg, optionBImg, optionCImg, optionDImg;
            CardView optionACard, optionBCard, optionCCard, optionDCard;

            ViewHolder(@NonNull View view) {
                super(view);
                ques = view.findViewById(R.id.ques);
                quesNo = view.findViewById(R.id.ques_no);
                marked = view.findViewById(R.id.marked);
                quesImg = view.findViewById(R.id.quesImg);
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
            }
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(context).inflate(resource, parent, false);
            return new ViewHolder(itemView);
        }

        @SuppressLint("SetTextI18n")
        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            SharedPreferences sp = context.getSharedPreferences(PREFS_NAME, 0);
            String schoolId = sp.getString("str_school_id", "");
            QuestionData data = objects.get(position);

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

            String correctOption = data.getCorrectOption().toUpperCase();

            switch (correctOption) {
                case "A":
                    holder.optionACard.setCardBackgroundColor(Color.GREEN);
                    break;
                case "B":
                    holder.optionBCard.setCardBackgroundColor(Color.GREEN);
                    break;
                case "C":
                    holder.optionCCard.setCardBackgroundColor(Color.GREEN);
                    break;
                case "D":
                    holder.optionDCard.setCardBackgroundColor(Color.GREEN);
                    break;
            }

            for (int i = 0; i < users.size(); i++) {
                if (users.get(i).getTbl_que_id().equals(data.getQuesId())) {
                    if (!users.get(i).getTbl_user_answer().toUpperCase().equals(correctOption)) {
                        switch (users.get(i).getTbl_user_answer().toUpperCase()) {
                            case "A":
                                holder.optionACard.setCardBackgroundColor(Color.RED);
                                holder.marked.setText("Incorrect");
                                holder.marked.setTextColor(Color.RED);
                                break;
                            case "B":
                                holder.optionBCard.setCardBackgroundColor(Color.RED);
                                holder.marked.setText("Incorrect");
                                holder.marked.setTextColor(Color.RED);
                                break;
                            case "C":
                                holder.optionCCard.setCardBackgroundColor(Color.RED);
                                holder.marked.setText("Incorrect");
                                holder.marked.setTextColor(Color.RED);
                                break;
                            case "D":
                                holder.optionDCard.setCardBackgroundColor(Color.RED);
                                holder.marked.setText("Incorrect");
                                holder.marked.setTextColor(Color.RED);
                                break;
                        }
                        break;
                    } else {
                        holder.marked.setText("Correct");
                        holder.marked.setTextColor(Color.GREEN);
                    }
                }
            }
        }

        @Override
        public int getItemCount() {
            return objects.size();
        }

    }
}
