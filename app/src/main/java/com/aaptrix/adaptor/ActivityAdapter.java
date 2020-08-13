package com.aaptrix.adaptor;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aaptrix.R;
import com.aaptrix.activitys.student.ActivitiesActivity;
import com.aaptrix.databeans.DataBeanActivities;
import java.util.ArrayList;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ViewHolder> {

    private Context context;
    private int resource;
    private ArrayList<DataBeanActivities> objects;
    private String type;

    public ActivityAdapter(Context context, int resource, ArrayList<DataBeanActivities> objects, String type) {
        this.context = context;
        this.resource = resource;
        this.objects = objects;
        this.type = type;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView title;

        ViewHolder(@NonNull View view) {
            super(view);
            title = view.findViewById(R.id.title);
        }
    }

    @NonNull
    @Override
    public ActivityAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(resource, parent, false);
        return new ViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ActivityAdapter.ViewHolder holder, int position) {
        holder.title.setText(objects.get(position).getActiviTitle());
        if (type.equals("announcements")) {
            holder.itemView.setOnClickListener(v -> {
                Intent i = new Intent(context, ActivitiesActivity.class);
                context.startActivity(i);
            });
        } else {
            holder.itemView.setOnClickListener(v -> {
                LayoutInflater factory = LayoutInflater.from(context);
                @SuppressLint("InflateParams") final View textEntryView = factory.inflate(R.layout.list_testimonial, null);

                TextView detail = textEntryView.findViewById(R.id.title);
                detail.setText("\" " + objects.get(position).getActiviTitle() + " \"");

                AlertDialog.Builder alert = new AlertDialog.Builder(context, R.style.DialogTheme);
                alert.setView(textEntryView).setNegativeButton("Close",
                        (dialog, whichButton) -> {

                        });
                AlertDialog alertDialog = alert.create();
                alertDialog.show();
            });
        }
    }

    @Override
    public int getItemCount() {
        return objects.size();
    }
}
