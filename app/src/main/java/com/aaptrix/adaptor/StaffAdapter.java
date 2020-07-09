package com.aaptrix.adaptor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.aaptrix.R;
import com.aaptrix.activitys.student.StaffDetails;
import com.aaptrix.databeans.StaffRateData;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.aaptrix.tools.SPClass.PREFS_NAME;

public class StaffAdapter extends ArrayAdapter<StaffRateData> {

    private Context context;
    private int resource;
    private ArrayList<StaffRateData> objects;
    private String rateEnabled;

    public StaffAdapter(@NonNull Context context, int resource, @NonNull ArrayList<StaffRateData> objects, String rateEnabled) {
        super(context, resource, objects);
        this.context = context;
        this.resource = resource;
        this.objects = objects;
        this.rateEnabled = rateEnabled;
    }

    @SuppressLint({"ViewHolder", "InflateParams"})
    @NonNull
    @Override
    public View getView(int position, @Nullable View view, @NonNull ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(resource, null);
        StaffRateData data = objects.get(position);

        if (data != null) {
            CircleImageView profile = view.findViewById(R.id.staff_profile);
            TextView name = view.findViewById(R.id.staff_name);
            TextView subjects = view.findViewById(R.id.staff_subject);

            name.setText(data.getName());
            String[] subs;

            if (data.getShortBio().equals("") || data.getShortBio().equals("null")) {
                try {
                    JSONArray jsonArray = new JSONArray(data.getSubjects());
                    subs = new String[jsonArray.length()];
                    for (int i = 0; i < jsonArray.length(); i++) {
                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                        subs[i] = jsonObject.getString("tbl_batch_subjct_name");
                    }
                    subjects.setText(Arrays.toString(subs).replace("[", "").replace("]", ""));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                subjects.setText(data.getShortBio());
            }

            SharedPreferences sp = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            String schoolId = sp.getString("str_school_id", "");
            String imageUrl = sp.getString("imageUrl", "");

            String url;
            switch (data.getType()) {
                case "Admin":
                    url = imageUrl + schoolId + "/users/admin/profile/" + data.getImage();
                    Picasso.with(context).load(url).error(R.drawable.user_place_hoder).placeholder(R.drawable.user_place_hoder).into(profile);
                    break;
                case "Teacher":
                    url = imageUrl + schoolId + "/users/teachers/profile/" + data.getImage();
                    Picasso.with(context).load(url).error(R.drawable.user_place_hoder).placeholder(R.drawable.user_place_hoder).into(profile);
                    break;
            }

            view.setOnClickListener(v -> {
                Intent intent = new Intent(context, StaffDetails.class);
                intent.putExtra("id", data.getId());
                intent.putExtra("name", data.getName());
                intent.putExtra("image", data.getImage());
                intent.putExtra("type", data.getType());
                intent.putExtra("subjects", data.getSubjects());
                intent.putExtra("bio", data.getBio());
                intent.putExtra("rating", data.getRating());
                intent.putExtra("rateEnable", rateEnabled);
                intent.putExtra("rated", data.getRated());
                intent.putExtra("comment", data.getComment());
                intent.putExtra("shortBio", data.getShortBio());
                context.startActivity(intent);
            });
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
