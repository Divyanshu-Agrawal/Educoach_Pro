package com.aaptrix.adaptor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import com.aaptrix.databeans.DatabeanEvents;
import com.aaptrix.R;
import androidx.annotation.NonNull;

/**
 * Created by googledeveloper on 24/2/17.
 */
public class UpcommingListAdaptor extends ArrayAdapter<DatabeanEvents> {
	
	private ArrayList<DatabeanEvents> objects;
	final String PREF_COLOR = "COLOR";
	
	public UpcommingListAdaptor(Context context, int resource, ArrayList<DatabeanEvents> objects) {
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
			v = inflater.inflate(R.layout.upcomming_list_item, null);
		}
		
		
		final DatabeanEvents dbItemsDist = objects.get(position);
		
		if (dbItemsDist != null) {
			
			//color
			SharedPreferences settingsColor = v.getContext().getSharedPreferences(PREF_COLOR, 0);
			String selToolColor = settingsColor.getString("tool", "");
			
			TextView tv_event_name = v.findViewById(R.id.tv_event_name);
			TextView tv_date = v.findViewById(R.id.tv_date);
			TextView tv_month_year = v.findViewById(R.id.tv_month_year);
			ImageView iv_next = v.findViewById(R.id.iv_next);
			tv_event_name.setText(dbItemsDist.getEventTitle());
			
			String date1 = dbItemsDist.getEventDate();
			String endDate = dbItemsDist.getEventEndDate();
			String[] separated = date1.split("-");
			String[] separated1 = endDate.split("-");
			String day = separated[2];
			String endDay = separated1[2];
			
			Log.d("day", day);
			Log.d("endDay", endDay);
			
			int a = Integer.parseInt(day);
			int b = Integer.parseInt(endDay);
			
			if (a == b) {
				tv_date.setText("" + a);
				
			} else {
				tv_date.setText("" + a + "-" + b);
			}

			int m = Integer.parseInt(separated[1]);
			String year = separated[0];
			String monthName = "";
			if (m == 1) {
				monthName = "Jan";
			} else if (m == 2) {
				monthName = "Feb";
				
			} else if (m == 3) {
				monthName = "Mar";
				
			} else if (m == 4) {
				monthName = "Apr";
				
			} else if (m == 5) {
				monthName = "May";
				
			} else if (m == 6) {
				monthName = "Jun";
				
			} else if (m == 7) {
				monthName = "Jul";
				
			} else if (m == 8) {
				monthName = "Aug";
				
			} else if (m == 9) {
				monthName = "Sep";
				
			} else if (m == 10) {
				monthName = "Oct";
				
			} else if (m == 11) {
				monthName = "Nov";
				
			} else if (m == 12) {
				monthName = "Dec";
			}
			
			tv_month_year.setText(monthName + ", " + year);
			tv_date.setTextColor(Color.parseColor(selToolColor));
			tv_month_year.setTextColor(Color.parseColor(selToolColor));
			iv_next.setColorFilter(v.getContext().getResources().getColor(R.color.text_gray));
			
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

