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
import com.aaptrix.databeans.ExamData;

import java.util.ArrayList;

public class ExamListAdapter extends ArrayAdapter<ExamData> {

    private Context context;
    private int resource;
    private ArrayList<ExamData> objects;

    public ExamListAdapter(Context context, int resource, ArrayList<ExamData> objects) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.objects = objects;
    }

    @SuppressLint({"InflateParams", "SetTextI18n", "ViewHolder"})
    @NonNull
    public View getView(final int position, View view, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        view = inflater.inflate(resource, null);

        ExamData data = objects.get(position);

        TextView name = view.findViewById(R.id.exam_name);
        TextView type = view.findViewById(R.id.exam_type);

        name.setText(data.getSubjectNm());

        if (data.getDetails().equals("1")) {
            type.setText("Major Exam");
        } else {
            type.setText("Minor Exam");
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
