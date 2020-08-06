package com.aaptrix.adaptor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aaptrix.R;
import com.aaptrix.activitys.student.ActivitiesActivity;
import com.aaptrix.activitys.student.InstituteBuzzActivity;
import com.aaptrix.databeans.DataBeanActivities;
import java.util.ArrayList;

public class ActivityAdapter extends RecyclerView.Adapter<ActivityAdapter.ViewHolder> {

    private Context context;
    private int resource;
    private ArrayList<DataBeanActivities> objects;

    public ActivityAdapter(Context context, int resource, ArrayList<DataBeanActivities> objects) {
        this.context = context;
        this.resource = resource;
        this.objects = objects;
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
        holder.itemView.setOnClickListener(v -> {
            Intent i = new Intent(context, ActivitiesActivity.class);
            context.startActivity(i);
        });
    }

    @Override
    public int getItemCount() {
        return objects.size();
    }
}
