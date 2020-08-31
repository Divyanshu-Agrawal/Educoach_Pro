package com.aaptrix.adaptor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;

import com.aaptrix.databeans.DataBeanActivities;

import androidx.annotation.NonNull;

import android.os.Build;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import com.aaptrix.R;

import static com.aaptrix.tools.SPClass.PREFS_NAME;

/**
 * Created by googledeveloper on 24/2/17.
 */
public class ActivitiesListAdaptor extends ArrayAdapter<DataBeanActivities> {

    private ArrayList<DataBeanActivities> objects;
    Activity context;
    final String PREF_COLOR = "COLOR";
    private String type;
    DataBeanActivities dbItemsDist;

    public ActivitiesListAdaptor(Activity context, int resource, ArrayList<DataBeanActivities> objects, String type) {
        super(context, resource, objects);
        this.objects = objects;
        this.context = context;
        this.type = type;
    }


    @SuppressLint({"InflateParams", "SetTextI18n"})
    @NonNull
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            assert inflater != null;
            v = inflater.inflate(R.layout.activities_list_item1, null);
        }


        dbItemsDist = objects.get(position);

        if (dbItemsDist != null) {
            SharedPreferences sp = v.getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
            TextView tv_achive_date = v.findViewById(R.id.tv_achive_date);
            TextView tv_achive_title = v.findViewById(R.id.tv_achive_title);
            TextView tv_achive_desc = v.findViewById(R.id.tv_achive_desc);
            ImageView iv_achive_img = v.findViewById(R.id.iv_achive_img);
            LinearLayout dateLayout = v.findViewById(R.id.dateLayout);

            SharedPreferences settingsColor = v.getContext().getSharedPreferences(PREF_COLOR, 0);
            String selDrawerColor = settingsColor.getString("drawer", "");
            tv_achive_title.setText(dbItemsDist.getActiviTitle());
            if (type.equals("publication")) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    tv_achive_desc.setText(Html.fromHtml(dbItemsDist.getActiviDesc(), Html.FROM_HTML_MODE_COMPACT));
                } else {
                    tv_achive_desc.setText(Html.fromHtml(dbItemsDist.getActiviDesc()));
                }
                dateLayout.setBackgroundColor(Color.parseColor(selDrawerColor));
                String img = objects.get(position).getActiviImg();
                String[] image;
                String url;
                if (!img.equals("[]") && !img.equals("0")) {
                    if (type.equals("publication")) {
                        image = img.split(",");
                        url = sp.getString("imageUrl", "") + sp.getString("userSchoolId", "") + "/publication/" + image[0].replace("\"", "").replace("[", "").replace("]", "");
                    } else {
                        image = img.split(",");
                        url = sp.getString("imageUrl", "") + sp.getString("userSchoolId", "") + "/activities/" + image[0].replace("\"", "").replace("[", "").replace("]", "");
                    }
                    Picasso.with(context).load(url).error(R.drawable.dummy).placeholder(R.drawable.dummy).into(iv_achive_img);
                    iv_achive_img.setVisibility(View.VISIBLE);
                } else {
                    iv_achive_img.setVisibility(View.GONE);
                }

                String[] seprate = dbItemsDist.getActiviDate().split("-");

                int m = Integer.parseInt(seprate[1]);
                String monthName = "";
                if (m == 1) {
                    monthName = "January";
                } else if (m == 2) {
                    monthName = "February";

                } else if (m == 3) {
                    monthName = "March";

                } else if (m == 4) {
                    monthName = "April";

                } else if (m == 5) {
                    monthName = "May";

                } else if (m == 6) {
                    monthName = "June";

                } else if (m == 7) {
                    monthName = "July";
                } else if (m == 8) {
                    monthName = "August";
                } else if (m == 9) {
                    monthName = "September";
                } else if (m == 10) {
                    monthName = "October";
                } else if (m == 11) {
                    monthName = "November";
                } else if (m == 12) {
                    monthName = "December";
                }
                tv_achive_date.setText(seprate[2] + "-" + monthName + "-" + seprate[0]);
            } else {
            	dateLayout.setVisibility(View.GONE);
            	tv_achive_desc.setVisibility(View.GONE);
            	tv_achive_date.setVisibility(View.GONE);
            	iv_achive_img.setVisibility(View.GONE);
            	tv_achive_title.setPadding(10, 10, 10, 10);
			}
        }
        return v;
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

