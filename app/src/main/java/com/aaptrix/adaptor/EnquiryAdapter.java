package com.aaptrix.adaptor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.aaptrix.R;
import com.aaptrix.databeans.EnquiryData;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import static com.aaptrix.tools.SPClass.PREF_COLOR;

public class EnquiryAdapter extends ArrayAdapter<EnquiryData> {

    private Context context;
    private int resources;
    private ArrayList<EnquiryData> objects;

    public EnquiryAdapter(Context context, int resources, ArrayList<EnquiryData> objects) {
        super(context, resources, objects);
        this.context = context;
        this.resources = resources;
        this.objects = objects;
    }

    @SuppressLint("ViewHolder")
    @NonNull
    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        view = inflater.inflate(resources, null);

        EnquiryData data = objects.get(position);

        SharedPreferences sp = context.getSharedPreferences(PREF_COLOR, Context.MODE_PRIVATE);
        String drawer = sp.getString("drawer", "");
        String text = sp.getString("text1", "");

        TextView name = view.findViewById(R.id.name);
        TextView course = view.findViewById(R.id.course);
        TextView date = view.findViewById(R.id.date);
        ImageView call = view.findViewById(R.id.call);

        date.setBackgroundColor(Color.parseColor(drawer));
        date.setTextColor(Color.parseColor(text));

        name.setText(data.getName());
        course.setText(data.getCourse());

        call.setOnClickListener(view1 -> {
            Intent phoneIntent = new Intent(Intent.ACTION_DIAL);
            String phone = "tel:" + data.getPhone().trim();
            phoneIntent.setData(Uri.parse(phone));
            context.startActivity(phoneIntent);
        });


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        try {
            Date formatDate = sdf.parse(data.getDate());
            sdf = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
            date.setText(sdf.format(formatDate));
        } catch (ParseException e) {
            e.printStackTrace();
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
