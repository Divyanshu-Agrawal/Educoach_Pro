package com.aaptrix.adaptor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;

import java.util.ArrayList;

import com.aaptrix.databeans.StaffData;
import com.aaptrix.R;
import androidx.annotation.NonNull;
import de.hdodenhof.circleimageview.CircleImageView;

import static com.aaptrix.activitys.admin.TeacherAttendance.ATTEN_JSON;
import static com.aaptrix.activitys.admin.TeacherAttendance.TEACHER_ARRAY;
import static com.aaptrix.activitys.admin.TeacherAttendance.TEACHER_ATTEN_PREFS;
import static com.aaptrix.tools.SPClass.PREFS_NAME;

public class TeacherAttendanceAdapter extends ArrayAdapter<StaffData> {
	
	Context context;
	int resource;
	private ArrayList<StaffData> objects, staffArray = new ArrayList<>();
	private String value;
	
	public TeacherAttendanceAdapter(Context context, int resource, ArrayList<StaffData> objects, String value) {
		super(context, resource, objects);
		this.context = context;
		this.resource = resource;
		this.objects = objects;
		this.value = value;
	}
	
	@NonNull
	@SuppressLint("InflateParams")
	public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			assert inflater != null;
			v = inflater.inflate(resource, null);
		}
		
		StaffData staffData = objects.get(position);
		if (staffData != null) {
			TextView clgname = v.findViewById(R.id.clgname);
			TextView tv_leave_status = v.findViewById(R.id.tv_leave_status);
			CircleImageView userLogo = v.findViewById(R.id.userLogo);
			RadioGroup rg = v.findViewById(R.id.rg);
			RadioButton rb_present = v.findViewById(R.id.rb_present);
			RadioButton rb_absent = v.findViewById(R.id.rb_absent);
			RadioButton rb_leave = v.findViewById(R.id.rb_leave);
			
			SharedPreferences settings = context.getSharedPreferences(PREFS_NAME, 0);
			clgname.setText(staffData.getName());
			
			if (staffData.getImage().equals("0")) {
				userLogo.setImageDrawable(v.getContext().getResources().getDrawable(R.drawable.user_place_hoder));
			} else if (!TextUtils.isEmpty(staffData.getImage())) {
				String url;
				switch (staffData.getType()) {
					case "4":
						url = settings.getString("imageUrl", "") + settings.getString("userSchoolId", "") + "/users/staff/profile/" + staffData.getImage();
						Log.e("url", url);
						Glide.with(context).load(url).into(userLogo);
//						Picasso.with(context).load(url).error(R.drawable.dummy).placeholder(R.drawable.dummy).into(userLogo);
						break;
					case "2":
						url = settings.getString("imageUrl", "") + settings.getString("userSchoolId", "") + "/users/teachers/profile/" + staffData.getImage();
						Log.e("url", url);
						Glide.with(context).load(url).into(userLogo);
//						Picasso.with(context).load(url).error(R.drawable.dummy).placeholder(R.drawable.dummy).into(userLogo);
						break;
				}
			} else {
				userLogo.setImageDrawable(v.getContext().getResources().getDrawable(R.drawable.user_place_hoder));
			}
			
			if (value.equals("take")) {
				switch (staffData.getAttenStatus()) {
					case "Present":
						rb_present.setChecked(true);
						rb_absent.setEnabled(false);
						rb_leave.setEnabled(false);
						break;
					case "Absent":
						rb_absent.setChecked(true);
						rb_present.setEnabled(false);
						rb_leave.setEnabled(false);
						break;
					case "Leave":
						rb_leave.setChecked(true);
						rb_absent.setEnabled(false);
						rb_present.setEnabled(false);
						tv_leave_status.setVisibility(View.VISIBLE);
						rg.setVisibility(View.GONE);
						break;
					default:
						if (staffData.getId().equals("1")) {
							tv_leave_status.setVisibility(View.VISIBLE);
							rg.setVisibility(View.GONE);
							rb_absent.setEnabled(false);
							rb_leave.setEnabled(false);
							rb_present.setEnabled(false);
							final View finalV = v;
							
							rg.setOnClickListener(view -> Toast.makeText(finalV.getContext(), "Staff is in leave", Toast.LENGTH_SHORT).show());
							clgname.setOnClickListener(view -> Toast.makeText(finalV.getContext(), "Staff is in leave", Toast.LENGTH_SHORT).show());
							StaffData data = new StaffData();
							data.setId(staffData.getId());
							data.setAttenStatus("Leave");
							staffArray.add(data);
							saveDataInSP(staffArray, v);
						}
						
						View finalV1 = v;
						rb_absent.setOnClickListener(view -> {
							StaffData data = new StaffData();
							data.setId(staffData.getId());
							data.setAttenStatus("Absent");
							staffArray.add(data);
							saveDataInSP(staffArray, finalV1);
						});
						
						rb_leave.setOnClickListener(view -> {
							StaffData data = new StaffData();
							data.setId(staffData.getId());
							data.setAttenStatus("Leave");
							staffArray.add(data);
							saveDataInSP(staffArray, finalV1);
						});
						
						rb_present.setOnClickListener(view -> {
							StaffData data = new StaffData();
							data.setId(staffData.getId());
							data.setAttenStatus("Present");
							staffArray.add(data);
							saveDataInSP(staffArray, finalV1);
						});
				}
			} else if (value.equals("view")) {
				switch (staffData.getAttenStatus()) {
					case "Present":
						rb_present.setChecked(true);
						rb_absent.setEnabled(false);
						rb_leave.setEnabled(false);
						break;
					case "Absent":
						rb_absent.setChecked(true);
						rb_present.setEnabled(false);
						rb_leave.setEnabled(false);
						break;
					case "Leave":
						rb_leave.setChecked(true);
						rb_absent.setEnabled(false);
						rb_present.setEnabled(false);
						tv_leave_status.setVisibility(View.VISIBLE);
						rg.setVisibility(View.GONE);
						break;
				}
			}
		}
		return v;
	}
	
	private void saveDataInSP(ArrayList<StaffData> staffArray, View view) {
		Gson gson = new GsonBuilder().create();
		JsonArray myCustomArray = gson.toJsonTree(staffArray).getAsJsonArray();
		SharedPreferences sp_attend = view.getContext().getSharedPreferences(TEACHER_ATTEN_PREFS, 0);
		SharedPreferences.Editor se_attend = sp_attend.edit();
		se_attend.clear();
		se_attend.putString(ATTEN_JSON, "" + myCustomArray);
		se_attend.putString(TEACHER_ARRAY, "" + staffArray.size());
		se_attend.apply();
		
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
