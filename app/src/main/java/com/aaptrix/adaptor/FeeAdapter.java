package com.aaptrix.adaptor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.aaptrix.R;
import com.aaptrix.databeans.FeeData;

import java.util.ArrayList;

public class FeeAdapter extends ArrayAdapter<FeeData> {

    private ArrayList<FeeData> objects;
    private Context context;
    private int resource;

    public FeeAdapter(@NonNull Context context, int resource, @NonNull ArrayList<FeeData> objects) {
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
        assert inflater != null;
        view = inflater.inflate(resource, null);

        if (objects != null) {
            FeeData data = objects.get(position);

            TextView categoryNm = view.findViewById(R.id.category_name);
            TextView categoryAmount = view.findViewById(R.id.category_amount);

            categoryAmount.setText(data.getAmount());
            categoryNm.setText(data.getType());

            if (position >= objects.size()-4) {
                categoryNm.setTypeface(Typeface.DEFAULT_BOLD);
                categoryAmount.setTypeface(Typeface.DEFAULT_BOLD);
                categoryAmount.setTextColor(Color.BLACK);
                categoryNm.setTextColor(Color.BLACK);
            }

        }

        return view;
    }
}
