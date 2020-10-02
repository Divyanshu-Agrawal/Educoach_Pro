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

import java.util.ArrayList;

import com.aaptrix.activitys.student.StudyMaterialDetail;
import com.aaptrix.databeans.StudyMaterialData;
import com.aaptrix.R;

import androidx.annotation.NonNull;

import static com.aaptrix.tools.SPClass.PREF_COLOR;

public class StudyMaterialAdaptor extends ArrayAdapter<StudyMaterialData> {

    Context context;
    private int resources;
    ArrayList<StudyMaterialData> objects;

    public StudyMaterialAdaptor(Context context, int resources, ArrayList<StudyMaterialData> objects) {
        super(context, resources, objects);
        this.context = context;
        this.resources = resources;
        this.objects = objects;
    }

    private static class ViewHolder {
        TextView title, description, subject;
    }

    @SuppressLint("ViewHolder")
    @NonNull
    @Override
    public View getView(int position, View view, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        assert inflater != null;
        view = inflater.inflate(resources, null);
        ViewHolder holder = new ViewHolder();
        view.setTag(holder);

        SharedPreferences sp = context.getSharedPreferences(PREF_COLOR, Context.MODE_PRIVATE);
        String drawer = sp.getString("drawer", "");
        String text = sp.getString("text1", "");

        if (objects != null) {
            StudyMaterialData data = objects.get(position);
            holder.title = view.findViewById(R.id.title);
            holder.description = view.findViewById(R.id.description);
            holder.subject = view.findViewById(R.id.subject);

            holder.subject.setBackgroundColor(Color.parseColor(drawer));
            holder.subject.setTextColor(Color.parseColor(text));

            if (data.getSubject().equals("0")) {
                holder.subject.setText("All Subjects");
            } else {
                if (!data.getSubject().equals("Null"))
                    holder.subject.setText(data.getSubject());
                else
                	holder.subject.setVisibility(View.GONE);
            }
            holder.title.setText(data.getTitle());

            if (!data.getDescription().equals("null")) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    holder.description.setText(Html.fromHtml(data.getDescription(), Html.FROM_HTML_MODE_COMPACT));
                } else {
                    holder.description.setText(Html.fromHtml(data.getDescription()));
                }
            } else {
                holder.description.setText("");
            }

            view.setOnClickListener(v -> {
                Intent intent = new Intent(context, StudyMaterialDetail.class);
                intent.putExtra("title", objects.get(position).getTitle());
                intent.putExtra("description", objects.get(position).getDescription());
                intent.putExtra("id", objects.get(position).getId());
                intent.putExtra("url", objects.get(position).getUrl());
                intent.putExtra("permission", objects.get(position).getPermission());
                intent.putExtra("tags", objects.get(position).getTags());
                intent.putExtra("subject", objects.get(position).getSubject());
                context.startActivity(intent);
            });
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
