package com.aaptrix.adaptor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.aaptrix.R;

import java.util.ArrayList;

public class CourseAdapter extends ArrayAdapter<String> {

    private Context context;
    private int resource;
    private ArrayList<String> objects;

    public CourseAdapter(Context context, int resourse, ArrayList<String> objects) {
        super(context, resourse, objects);
        this.context = context;
        this.resource = resourse;
        this.objects = objects;
    }

    @SuppressLint("ViewHolder")
    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        view = inflater.inflate(resource, null);

        if (objects != null) {
            TextView course = view.findViewById(R.id.moreinfo);
            course.setText(objects.get(position));
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
