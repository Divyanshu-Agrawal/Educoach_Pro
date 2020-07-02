package com.aaptrix.adaptor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.aaptrix.R;
import com.aaptrix.databeans.DataBeanStudent;

import java.util.ArrayList;

public class BatchAdaptor extends ArrayAdapter<DataBeanStudent> {

    private ArrayList<DataBeanStudent> objects;
    private Context context;
    private int resource;

    public BatchAdaptor(Activity context, int resource, ArrayList<DataBeanStudent> objects) {
        super(context, resource, objects);
        this.objects = objects;
        this.context = context;
        this.resource = resource;
    }

    @SuppressLint({"InflateParams", "ViewHolder"})
    @NonNull
    public View getView(final int position, View view, @NonNull ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        view = inflater.inflate(resource, null);

        if (objects != null) {
            TextView batch = view.findViewById(R.id.batch);
            TextView subject = view.findViewById(R.id.subject);

            batch.setText(objects.get(position).getUserName());
            subject.setText(objects.get(position).getUserLoginId());
        }

        return view;
    }

    @Override
    public int getViewTypeCount() {
        return Math.max(getCount(), 1);
    }

    @Override
    public int getItemViewType(int position) {

        return position;
    }
}
