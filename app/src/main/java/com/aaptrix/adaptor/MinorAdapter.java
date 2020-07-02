package com.aaptrix.adaptor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.aaptrix.R;
import com.aaptrix.databeans.MinorData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MinorAdapter extends ArrayAdapter<MinorData> {

    private Context context;
    private int resource;
    private ArrayList<MinorData> objects;

    public MinorAdapter(@NonNull Context context, int resource, @NonNull ArrayList<MinorData> objects) {
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

        MinorData data = objects.get(position);

        if (data != null) {
            TextView marks = view.findViewById(R.id.marks);
            TextView exam_name = view.findViewById(R.id.exam_name);
            TextView subject = view.findViewById(R.id.subject);
            TextView date = view.findViewById(R.id.date);

            marks.setText(data.getMarks());
            exam_name.setText(data.getExamName());
            subject.setText(data.getSubject());

            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
            Date newDate = null;
            try {
                newDate = format.parse(data.getDate());
            } catch (ParseException e) {
                e.printStackTrace();
            }
            format = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            date.setText(format.format(newDate));
        }

        return view;
    }

    @Override
    public int getViewTypeCount() {
        if (getCount() < 1) {
            return 1;
        } else {
            return getCount();
        }
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
