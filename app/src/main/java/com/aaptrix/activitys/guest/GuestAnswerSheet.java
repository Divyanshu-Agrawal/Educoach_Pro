package com.aaptrix.activitys.guest;

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
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.aaptrix.R;
import com.aaptrix.databeans.QuestionData;
import com.google.android.material.appbar.AppBarLayout;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREF_COLOR;

public class GuestAnswerSheet extends AppCompatActivity {

    AppBarLayout appBarLayout;
    TextView tool_title;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    String selToolColor;
    String ques, ans;
    TextView noResult;
    ArrayList<QuestionData> quesarray = new ArrayList<>(), userArray = new ArrayList<>();
    RecyclerView.LayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_answer_sheet);
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

        tool_title.setText(getIntent().getStringExtra("examName"));
        ques = getIntent().getStringExtra("ques_array");
        ans = getIntent().getStringExtra("ans_array");

        setAnswer();

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
    }

    private void setAnswer() {
        try {
            JSONArray jsonArray = new JSONArray(ques);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject object = jsonArray.getJSONObject(i);
                QuestionData data = new QuestionData();
                data.setQues(object.getString("ques"));
                data.setQuesImg(object.getString("quesImg"));
                data.setQuesId(object.getString("quesId"));
                data.setOptionA(object.getString("optionA"));
                data.setOptionAImg(object.getString("optionAImg"));
                data.setOptionB(object.getString("optionB"));
                data.setOptionBImg(object.getString("optionBImg"));
                data.setOptionC(object.getString("optionC"));
                data.setOptionCImg(object.getString("optionCImg"));
                data.setOptionD(object.getString("optionD"));
                data.setOptionDImg(object.getString("optionDImg"));
                data.setCorrectOption(object.getString("correctOption"));
                quesarray.add(data);
            }
            JSONArray array = new JSONArray(ans);
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
    }

    private void listItems() {
        noResult.setVisibility(View.GONE);
        RecyclerResultAdapter adapter = new RecyclerResultAdapter(this, R.layout.list_online_result, quesarray, userArray);
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
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