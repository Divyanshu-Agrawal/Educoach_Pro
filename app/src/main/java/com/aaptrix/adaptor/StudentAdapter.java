package com.aaptrix.adaptor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aaptrix.R;
import com.aaptrix.databeans.DataBeanStudent;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.aaptrix.tools.SPClass.PREFS_NAME;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.ViewHolder> {

    private Context context;
    private int resources;
    private ArrayList<DataBeanStudent> objects;
    private float marks;

    public StudentAdapter(Context context, int resources, ArrayList<DataBeanStudent> objects, float marks) {
        this.context = context;
        this.resources = resources;
        this.objects = objects;
        this.marks = marks;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CircleImageView userImage;
        TextView userName, userMarks;

        public ViewHolder(@NonNull View view) {
            super(view);
            userImage = view.findViewById(R.id.user_profile);
            userName = view.findViewById(R.id.user_name);
            userMarks = view.findViewById(R.id.marks);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(resources, parent, false);
        if (objects.size() == 1) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, 0);
            params.width = LinearLayout.LayoutParams.MATCH_PARENT;
            params.height = LinearLayout.LayoutParams.WRAP_CONTENT;
            itemView.setLayoutParams(params);
        }
        return new ViewHolder(itemView);
    }


    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (objects.get(position) != null) {
            SharedPreferences sp = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            String imageUrl = sp.getString("imageUrl", "") + sp.getString("userSchoolId", "");
            String url = imageUrl + "/users/students/profile/" + objects.get(position).getUserImg();
            Picasso.with(context).load(url).error(R.drawable.user_place_hoder).placeholder(R.drawable.user_place_hoder).into(holder.userImage);
            holder.userName.setText(objects.get(position).getUserName());
            holder.userMarks.setText(String.valueOf((Math.round(marks))));
        }
    }

    @Override
    public int getItemCount() {
        return objects.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
