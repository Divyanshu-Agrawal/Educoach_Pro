package com.aaptrix.adaptor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;

import com.aaptrix.R;
import com.aaptrix.activitys.student.OnlineReport;
import com.aaptrix.activitys.student.StartExam;
import com.aaptrix.activitys.student.StartSubjective;
import com.aaptrix.activitys.student.SubjectiveExamResult;
import com.aaptrix.databeans.OnlineExamData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static com.aaptrix.tools.SPClass.PREFS_NAME;

public class OnlineExamAdapter extends ArrayAdapter<OnlineExamData> {

    private Context context;
    private int resource;
    private ArrayList<OnlineExamData> objects;

    public OnlineExamAdapter(@NonNull Context context, int resource, @NonNull ArrayList<OnlineExamData> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.objects = objects;
    }

    @SuppressLint({"ViewHolder", "InflateParams", "SetTextI18n"})
    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(resource, null);

        TextView examName = view.findViewById(R.id.exam_name);
        TextView examDate = view.findViewById(R.id.exam_date);
        TextView examTime = view.findViewById(R.id.exam_time);
        TextView examDuration = view.findViewById(R.id.exam_duration);
        CardView cardView = view.findViewById(R.id.card_view);
        TextView type = view.findViewById(R.id.type);
        TextView sub = view.findViewById(R.id.status);

        SharedPreferences sp = context.getSharedPreferences(PREFS_NAME, 0);
        String userId = sp.getString("userID", "");
        String userSection = sp.getString("userSection", "");
        String userType = sp.getString("userrType", "");

        if (objects != null) {
            OnlineExamData data = objects.get(position);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                examName.setText(Html.fromHtml(data.getName(), Html.FROM_HTML_MODE_COMPACT));
            } else {
                examName.setText(Html.fromHtml(data.getName()));
            }

            sub.setText("Subject : " + data.getSubject());
            type.setText("Exam Type : " + data.getType());

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault());
            try {
                Date date = sdf.parse(data.getDate() + " " + data.getStartTime());
                Date endDate = sdf.parse(data.getEndDate() + " " + data.getEndTime());
                sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                assert date != null;
                assert endDate != null;
                String strstart = sdf.format(date);
                String strEnd = sdf.format(endDate);

                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DATE, -1);

                if (userType.equals("Student")) {
                    if (calendar.getTime().before(date) || (calendar.getTime().after(date) && calendar.getTime().before(endDate))) {
                        if (data.getStatus().equals("0"))
                            cardView.setCardBackgroundColor(Color.GREEN);
                        view.setOnClickListener(v -> {
                            if (!data.getStatus().equals("1")) {
                                if (data.getType().equals("MCQ")) {
                                    Intent intent = new Intent(context, StartExam.class);
                                    intent.putExtra("examId", data.getId());
                                    intent.putExtra("examName", data.getName());
                                    intent.putExtra("examStart", data.getStartTime());
                                    intent.putExtra("examEnd", data.getEndTime());
                                    intent.putExtra("negMarks", data.getNegMarks());
                                    intent.putExtra("resPublish", data.getResPublish());
                                    intent.putExtra("endDate", data.getEndDate());
                                    intent.putExtra("startDate", data.getDate());
                                    intent.putExtra("duration", data.getDuration());
                                    context.startActivity(intent);
                                } else {
                                    Intent intent = new Intent(context, StartSubjective.class);
                                    intent.putExtra("examId", data.getId());
                                    intent.putExtra("examName", data.getName());
                                    intent.putExtra("examStart", data.getStartTime());
                                    intent.putExtra("examEnd", data.getEndTime());
                                    intent.putExtra("pdf", data.getQuesPdf());
                                    intent.putExtra("endDate", data.getEndDate());
                                    intent.putExtra("startDate", data.getDate());
                                    context.startActivity(intent);
                                }
                            } else {
                                Toast.makeText(context, "You have already taken this exam", Toast.LENGTH_SHORT).show();
                                if (data.getResPublish().equals("1")) {
                                    if (data.getType().equals("MCQ")) {
                                        Intent intent = new Intent(context, OnlineReport.class);
                                        intent.putExtra("userId", userId);
                                        intent.putExtra("examId", data.getId());
                                        intent.putExtra("examName", data.getName());
                                        intent.putExtra("userSection", userSection);
                                        context.startActivity(intent);
                                    } else {
                                        Intent intent = new Intent(context, SubjectiveExamResult.class);
                                        intent.putExtra("examName", data.getName());
                                        intent.putExtra("pdf", data.getAnsPdf());
                                        intent.putExtra("id", data.getId());
                                        context.startActivity(intent);
                                    }
                                } else {
                                    Toast.makeText(context, "Result not published yet", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    } else {
                        view.setOnClickListener(v -> Toast.makeText(context, "Exam not started yet.", Toast.LENGTH_SHORT).show());
                    }
                } else {
                    view.setOnClickListener(v -> {
                        if (data.getType().equals("MCQ")) {
                            Intent intent = new Intent(context, StartExam.class);
                            intent.putExtra("examId", data.getId());
                            intent.putExtra("examName", data.getName());
                            intent.putExtra("examStart", data.getStartTime());
                            intent.putExtra("examEnd", data.getEndTime());
                            intent.putExtra("negMarks", data.getNegMarks());
                            intent.putExtra("resPublish", data.getResPublish());
                            intent.putExtra("endDate", data.getEndDate());
                            intent.putExtra("startDate", data.getDate());
                            intent.putExtra("duration", data.getDuration());
                            context.startActivity(intent);
                        } else {
                            Intent intent = new Intent(context, StartSubjective.class);
                            intent.putExtra("examId", data.getId());
                            intent.putExtra("examName", data.getName());
                            intent.putExtra("examStart", data.getStartTime());
                            intent.putExtra("examEnd", data.getEndTime());
                            intent.putExtra("pdf", data.getQuesPdf());
                            intent.putExtra("endDate", data.getEndDate());
                            intent.putExtra("startDate", data.getDate());
                            context.startActivity(intent);
                        }
                    });
                }

                sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                Date time = sdf.parse(data.getStartTime());
                Date endTime = sdf.parse(data.getEndTime());
                assert time != null;
                assert endTime != null;
                sdf = new SimpleDateFormat("hh:mm aa", Locale.getDefault());
                examDate.setText("Starts On : " + strstart + " At : " + sdf.format(time));
                examTime.setText("Ends On : " + strEnd + " At : " + sdf.format(endTime));
                if (data.getType().equals("MCQ")) {
                    examDuration.setText("Duration : " + data.getDuration() + " Hours");
                } else {
                    examDuration.setText("Answer accepted till " + strEnd + " " + sdf.format(endTime));
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return view;
    }
}
