package com.aaptrix.adaptor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;

import com.aaptrix.databeans.DataBeanAchivment;
import androidx.annotation.NonNull;

import android.os.Build;
import android.text.Html;
import android.util.Log;
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
public class AchivmentListAdaptor1 extends ArrayAdapter<DataBeanAchivment> {
	
	private ArrayList<DataBeanAchivment> objects;
	Activity context;
	final String PREF_COLOR = "COLOR";
	String selDrawerColor;
	DataBeanAchivment dbItemsDist;
	
	public AchivmentListAdaptor1(Activity context, int resource, ArrayList<DataBeanAchivment> objects) {
		super(context, resource, objects);
		this.objects = objects;
		this.context = context;
	}
	
	
	@NonNull
	@SuppressLint("InflateParams")
	public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
		
		View v = convertView;
		
		if (v == null) {
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			assert inflater != null;
			v = inflater.inflate(R.layout.achivement_list_item1, null);
		}
		
		
		dbItemsDist = objects.get(position);
		
		if (dbItemsDist != null) {
			SharedPreferences sp = getContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
			TextView tv_achive_date = v.findViewById(R.id.tv_achive_date);
			TextView tv_achive_title = v.findViewById(R.id.tv_achive_title);
			TextView tv_achive_desc = v.findViewById(R.id.tv_achive_desc);
			ImageView iv_achive_img = v.findViewById(R.id.iv_achive_img);
			ImageView iv_achiv_cate = v.findViewById(R.id.iv_achiv_cate);
			LinearLayout dateLayout = v.findViewById(R.id.dateLayout);
			SharedPreferences settingsColor = v.getContext().getSharedPreferences(PREF_COLOR, 0);
			selDrawerColor = settingsColor.getString("drawer", "");
			tv_achive_date.setText(dbItemsDist.getAchivDate());
			tv_achive_title.setText(dbItemsDist.getAchivTitle());
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
				tv_achive_desc.setText(Html.fromHtml(dbItemsDist.getAchivDesc(), Html.FROM_HTML_MODE_COMPACT));
			} else {
				tv_achive_desc.setText(Html.fromHtml(dbItemsDist.getAchivDesc()));
			}
			dateLayout.setBackgroundColor(Color.parseColor(selDrawerColor));
			
			Log.d("getAchiveImg", dbItemsDist.getAchiveImg());
			
			String img = dbItemsDist.getAchiveImg();
			if (!img.equals("0")) {
				String[] image = dbItemsDist.getAchiveImg().split(",");
				String img1 = image[0].replace("\"", "").replace("[", "").replace("]", "");
				String url = sp.getString("imageUrl", "") + sp.getString("userSchoolId", "") + "/users/students/achivments/" + img1;
				Picasso.with(context).load(url).error(R.drawable.dummy).placeholder(R.drawable.dummy).into(iv_achive_img);
				Picasso.with(context).load(dbItemsDist.getAchivCate()).error(R.drawable.dummy).placeholder(R.drawable.dummy).into(iv_achiv_cate);
				iv_achive_img.setVisibility(View.VISIBLE);
				
			} else {
				Picasso.with(context).load(dbItemsDist.getAchivCate()).error(R.drawable.dummy).placeholder(R.drawable.dummy).into(iv_achiv_cate);
				//  Picasso.with(v.getContext()).load("http://demo4.sjainventures.com/edugauge/android/uploads/achievment/no_achive_image.png").error(R.drawable.dummy).placeholder(R.drawable.dummy).into(iv_achive_img);
				iv_achive_img.getLayoutParams().height = 0;
				iv_achive_img.setVisibility(View.GONE);
			}
		}
		return v;
		
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

