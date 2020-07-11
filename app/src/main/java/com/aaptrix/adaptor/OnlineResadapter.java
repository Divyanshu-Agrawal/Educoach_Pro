package com.aaptrix.adaptor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.aaptrix.R;
import com.aaptrix.databeans.OnlineExamData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static com.aaptrix.tools.SPClass.PREFS_NAME;

public class OnlineResadapter extends ArrayAdapter<OnlineExamData> {

    private Context context;
    private int resource;
    private ArrayList<OnlineExamData> objects;

    public OnlineResadapter(@NonNull Context context, int resource, @NonNull ArrayList<OnlineExamData> objects) {
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
        TextView status = view.findViewById(R.id.status);
        TextView type = view.findViewById(R.id.type);
        TextView examDuration = view.findViewById(R.id.exam_duration);

        SharedPreferences sp = context.getSharedPreferences(PREFS_NAME, 0);
        String userType = sp.getString("userrType", "");

        if (objects != null) {
            OnlineExamData data = objects.get(position);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                examName.setText(Html.fromHtml(data.getName(), Html.FROM_HTML_MODE_COMPACT));
            } else {
                examName.setText(Html.fromHtml(data.getName()));
            }

            if (data.getType().equals("MCQ")) {
                examDuration.setText("Duration : " + data.getDuration() + " Hours");
            } else {
                examDuration.setText("Duration : None");
            }

            type.setText("Exam Type : " + data.getType());

            if (userType.equals("Admin") || userType.equals("Teacher")) {
                status.setVisibility(View.GONE);
            }

            if (data.getStatus().equals("1")) {
                status.setText("Status : Attempted");
            } else {
                status.setText("Status : Not Attempted");
            }

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            try {
                Date date = sdf.parse(data.getDate());
                Date endDate = sdf.parse(data.getEndDate());
                sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                assert date != null;
                assert endDate != null;
                examDate.setText(sdf.format(date));

                String end = sdf.format(endDate);
                sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());
                Date time = sdf.parse(data.getEndTime());
                assert time != null;
                sdf = new SimpleDateFormat("hh:mm aa", Locale.getDefault());
                examTime.setText("Ended On : " + end + " At " + sdf.format(time));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return view;
    }
}
