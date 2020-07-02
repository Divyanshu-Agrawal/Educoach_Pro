package com.aaptrix.adaptor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

import com.aaptrix.databeans.DataBeanExamTt;
import com.aaptrix.R;
import androidx.annotation.NonNull;

import static com.aaptrix.tools.SPClass.PREFS_NAME;

public class ClassTimeTableListAdaptor extends ArrayAdapter<DataBeanExamTt> {
	
	private ArrayList<DataBeanExamTt> objects;
	String position;
	public static final String PREF_COLOR = "COLOR";
	
	public ClassTimeTableListAdaptor(Context context, int resource, ArrayList<DataBeanExamTt> objects) {
		super(context, resource, objects);
		this.objects = objects;
	}
	
	
	@SuppressLint({"InflateParams", "SetTextI18n"})
	@NonNull
	public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
		
		View v = convertView;
		
		if (v == null) {
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			assert inflater != null;
			v = inflater.inflate(R.layout.class_tt_list_item, null);
		}
		
		
		final DataBeanExamTt dbItemsDist = objects.get(position);
		
		if (dbItemsDist != null) {
			
			TextView subjectName = v.findViewById(R.id.subjectName);
			TextView examDateOnly = v.findViewById(R.id.examDateOnly);
			TextView monthYear = v.findViewById(R.id.monthYear);
			TextView teacherName = v.findViewById(R.id.teacherName);
			ImageView iv_next = v.findViewById(R.id.iv_next);
			TextView time = v.findViewById(R.id.time);

			SharedPreferences settingsColor = v.getContext().getSharedPreferences(PREF_COLOR, 0);
			String selToolColor = settingsColor.getString("tool", "");
			String drawerColor = settingsColor.getString("drawer", "");
			String textColor = settingsColor.getString("text1", "");

			time.setBackgroundColor(Color.parseColor(drawerColor));
			time.setTextColor(Color.parseColor(textColor));

			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.getDefault());

			try {
				Date start = sdf.parse(dbItemsDist.getStartDate());
				Date end = sdf.parse(dbItemsDist.getEndDate());
				sdf = new SimpleDateFormat("hh:mm aa", Locale.getDefault());
				time.setText(sdf.format(start) + " - " + sdf.format(end));
			} catch (ParseException e) {
				e.printStackTrace();
			}

			subjectName.setText(dbItemsDist.getSubjectName());
			examDateOnly.setText(dbItemsDist.getExamId());
			teacherName.setText(dbItemsDist.getExamDate());
			examDateOnly.setTextColor(Color.parseColor(selToolColor));
			monthYear.setTextColor(Color.parseColor(selToolColor));
			iv_next.setColorFilter(v.getContext().getResources().getColor(R.color.text_gray));

			SharedPreferences settings = getContext().getSharedPreferences(PREFS_NAME, 0);
			String userType = settings.getString("userrType", "");
			if (userType.equals("Admin")) {
				iv_next.setVisibility(View.VISIBLE);
			} else {
				iv_next.setVisibility(View.GONE);
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

