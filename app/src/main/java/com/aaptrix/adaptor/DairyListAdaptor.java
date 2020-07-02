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

import com.aaptrix.databeans.DataBeanDairy;
import com.aaptrix.R;
import androidx.annotation.NonNull;

/**
 * Created by googledeveloper on 24/2/17.
 */
public class DairyListAdaptor extends ArrayAdapter<DataBeanDairy> {
	
	private ArrayList<DataBeanDairy> objects;
	String position;
	final String PREF_COLOR = "COLOR";
	
	public DairyListAdaptor(Context context, int resource, ArrayList<DataBeanDairy> objects) {
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
			v = inflater.inflate(R.layout.dairy_list_item, null);
		}
		final DataBeanDairy dbItemsDist = objects.get(position);
		if (dbItemsDist != null) {
			TextView reportedBy = v.findViewById(R.id.reportedBy);
			TextView dairyTitle = v.findViewById(R.id.dairyTitle);
			TextView monthYear = v.findViewById(R.id.monthYear);
			TextView examDateOnly = v.findViewById(R.id.examDateOnly);
			ImageView iv_next = v.findViewById(R.id.iv_next);
//color
			SharedPreferences settingsColor = v.getContext().getSharedPreferences(PREF_COLOR, 0);
			String selToolColor = settingsColor.getString("tool", "");
			try {
				SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
				Date newDate = null;
				try {
					newDate = format.parse(dbItemsDist.getDairyDate());
				} catch (ParseException e) {
					e.printStackTrace();
				}
				format = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
				String[] seprate = format.format(newDate).split("-");
				dairyTitle.setText(dbItemsDist.getDairyTitle());
				reportedBy.setText(dbItemsDist.getDairyReportedBy());
				examDateOnly.setText(seprate[2]);
				int m = Integer.parseInt(seprate[1]);
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
				monthYear.setText(monthName + ", " + seprate[0]);
				iv_next.setColorFilter(Color.parseColor(selToolColor));
				examDateOnly.setTextColor(Color.parseColor(selToolColor));
				monthYear.setTextColor(Color.parseColor(selToolColor));
			} catch (Exception e) {
				e.printStackTrace();
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
