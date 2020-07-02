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

import com.aaptrix.R;
import com.aaptrix.databeans.ResultData;

import java.util.ArrayList;

import static com.aaptrix.tools.SPClass.PREF_COLOR;

public class ResAdapter extends ArrayAdapter<ResultData> {

    private ArrayList<ResultData> objects;
    String position;
    Context context;

    public ResAdapter(Context context, int resource, ArrayList<ResultData> objects) {
        super(context, resource, objects);
        this.objects = objects;
        this.context = context;
    }

    @NonNull
    @SuppressLint("InflateParams")
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            v = inflater.inflate(R.layout.result_list_item, null);
        }
        ResultData data = objects.get(position);
        if (data != null) {
            TextView subjectName = v.findViewById(R.id.subjectName);
            TextView examDateOnly = v.findViewById(R.id.examDateOnly);
            SharedPreferences settingsColor = v.getContext().getSharedPreferences(PREF_COLOR, 0);
            String selToolColor = settingsColor.getString("tool", "");
            subjectName.setText(data.getSubjectName());
            examDateOnly.setText(data.getMarks());
            examDateOnly.setTextColor(Color.parseColor(selToolColor));
        }
        return v;

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
