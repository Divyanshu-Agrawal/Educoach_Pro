package com.aaptrix.adaptor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;

import com.aaptrix.activitys.admin.AdminLeaveRequest;
import com.aaptrix.databeans.DataBeanLeaves;
import com.aaptrix.R;
import androidx.annotation.NonNull;

public class StaffLeaveAdapter extends ArrayAdapter<DataBeanLeaves> {
	
	private ArrayList<DataBeanLeaves> objects;
	String position;
	public static final String PREF_COLOR = "COLOR";
	private Activity activity;
	
	public StaffLeaveAdapter(Context context, int resource, ArrayList<DataBeanLeaves> objects, Activity activity) {
		super(context, resource, objects);
		this.objects = objects;
		this.activity = activity ;
	}
	
	@SuppressLint({"InflateParams", "SetTextI18n"})
	@NonNull
	public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
		
		View v = convertView;
		
		if (v == null) {
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			assert inflater != null;
			v = inflater.inflate(R.layout.list_staff_leave, null);
		}
		
		
		final DataBeanLeaves dbItemsDist = objects.get(position);
		
		if (dbItemsDist != null) {
			
			TextView leaveDate = v.findViewById(R.id.leaveDate);
			TextView leaveMonthYear = v.findViewById(R.id.leaveMonthYear);
			TextView leaveTitle = v.findViewById(R.id.leaveTitle);
			TextView leaveDetails = v.findViewById(R.id.leaveDetails);
			TextView accept = v.findViewById(R.id.accept);
			TextView reject = v.findViewById(R.id.reject);
			TextView status = v.findViewById(R.id.status);
			ImageView iv_next = v.findViewById(R.id.iv_next);
			LinearLayout leaveLayout = v.findViewById(R.id.leaveLayout);
			
			
			SharedPreferences settingsColor = v.getContext().getSharedPreferences(PREF_COLOR, 0);
			String selToolColor = settingsColor.getString("tool", "");
			String selDrawerColor = settingsColor.getString("drawer", "");
			
			String strStatus = dbItemsDist.getLeaveStatus();
			switch (strStatus) {
				case "Pending":
					status.setVisibility(View.VISIBLE);
					accept.setVisibility(View.GONE);
					reject.setVisibility(View.GONE);
					break;
				case "Approved":
					status.setVisibility(View.GONE);
					accept.setVisibility(View.VISIBLE);
					reject.setVisibility(View.GONE);
					accept.setText("Accepted");
					break;
				case "Rejected":
					status.setVisibility(View.GONE);
					accept.setVisibility(View.GONE);
					reject.setVisibility(View.VISIBLE);
					reject.setText("Rejected");
					break;
			}
			leaveTitle.setText(dbItemsDist.getLeaveSubject());
			leaveDetails.setText(dbItemsDist.getLeaveDetails());
			
			iv_next.setOnClickListener(view -> {
				Intent i = new Intent(getContext(), AdminLeaveRequest.class);
				i.putExtra("userId", dbItemsDist.getUserId());
				i.putExtra("leaveId", dbItemsDist.getLeavesId());
				i.putExtra("leaveSubject", dbItemsDist.getLeaveSubject());
				i.putExtra("leaveStartDate", dbItemsDist.getLeaveStartDate());
				i.putExtra("leaveEndDate", dbItemsDist.getLeaveEndDate());
				i.putExtra("leaveDetails", dbItemsDist.getLeaveDetails());
				i.putExtra("strStatus", dbItemsDist.getLeaveStatus());
				i.putExtra("leaveImg", dbItemsDist.getLeaveImg());
				getContext().startActivity(i);
				activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
			});
			
			leaveLayout.setOnClickListener(view -> {
				Intent i = new Intent(getContext(), AdminLeaveRequest.class);
				i.putExtra("userId", dbItemsDist.getUserId());
				i.putExtra("leaveId", dbItemsDist.getLeavesId());
				i.putExtra("leaveSubject", dbItemsDist.getLeaveSubject());
				i.putExtra("leaveStartDate", dbItemsDist.getLeaveStartDate());
				i.putExtra("leaveEndDate", dbItemsDist.getLeaveEndDate());
				i.putExtra("leaveDetails", dbItemsDist.getLeaveDetails());
				i.putExtra("strStatus", dbItemsDist.getLeaveStatus());
				i.putExtra("leaveImg", dbItemsDist.getLeaveImg());
				getContext().startActivity(i);
				activity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
			});
			
			leaveTitle.setText(dbItemsDist.getStudentName());
			leaveDetails.setText(dbItemsDist.getLeaveSubject());
			
			String date1 = dbItemsDist.getLeaveStartDate();
			String endDate = dbItemsDist.getLeaveEndDate();
			String[] separated = date1.split("-");
			String[] separated1 = endDate.split("-");
			String day = separated[2];
			String endDay = separated1[2];
			
			Log.d("day", day);
			Log.d("endDay", endDay);
			
			int a = Integer.parseInt(day);
			int b = Integer.parseInt(endDay);
			
			if (a == b) {
				leaveDate.setText("" + a);
				
			} else {
				leaveDate.setText("" + a + "-" + b);
			}
			
			String[] seprate = dbItemsDist.getLeaveStartDate().split("-");
			//   leaveDate.setText(seprate[2]);
			
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
			leaveMonthYear.setText(monthName + ", " + seprate[0]);
			leaveDate.setTextColor(Color.parseColor(selToolColor));
			leaveMonthYear.setTextColor(Color.parseColor(selToolColor));
			iv_next.setColorFilter(Color.parseColor(selDrawerColor));
			
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

