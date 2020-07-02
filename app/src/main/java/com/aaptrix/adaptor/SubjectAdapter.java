package com.aaptrix.adaptor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.aaptrix.R;
import com.aaptrix.databeans.ExamData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static com.aaptrix.tools.SPClass.PREF_COLOR;

public class SubjectAdapter extends ArrayAdapter<ExamData> {

    private Context context;
    private int resource;
    private ArrayList<ExamData> objects;
    String selDrawerColor, selTextColor;

    public SubjectAdapter(@NonNull Context context, int resource, ArrayList<ExamData> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.objects = objects;
    }

    @SuppressLint("ViewHolder")
    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(resource, null);
        ExamData data = objects.get(position);

        if (data != null) {
            TextView subjectName = view.findViewById(R.id.subjectName);
            TextView marks = view.findViewById(R.id.marks);
            TextView date = view.findViewById(R.id.date);
            TextView details = view.findViewById(R.id.details);

            SharedPreferences settingsColor = context.getSharedPreferences(PREF_COLOR, 0);
            selDrawerColor = settingsColor.getString("drawer", "");
            selTextColor = settingsColor.getString("text1", "");

            date.setBackgroundColor(Color.parseColor(selDrawerColor));
            date.setTextColor(Color.parseColor(selTextColor));

            subjectName.setText(data.getSubjectNm());
            marks.setText(data.getMarks() + " Marks");
            details.setText(data.getDetails());

            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            try {
                Date formatDate = sdf.parse(data.getExamDate());
                sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                date.setText(sdf.format(formatDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
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
