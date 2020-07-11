package com.aaptrix.adaptor;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import com.aaptrix.activitys.teacher.StudentAttendanceActivity;
import com.aaptrix.databeans.DataBeanStudent;
import androidx.annotation.NonNull;
import de.hdodenhof.circleimageview.CircleImageView;
import com.aaptrix.R;

import static com.aaptrix.tools.SPClass.PREFS_NAME;

/**
 * Created by googledeveloper on 24/2/17.
 */
public class StudentAttendanceListAdaptor extends ArrayAdapter<DataBeanStudent> {
	
	private ArrayList<DataBeanStudent> objects;
	private ArrayList<DataBeanStudent> studentArray = new ArrayList<>();
	private String value, subject;
	private DataBeanStudent dbs;
	private String attendanceStatus = "", selectAll;
	
	private static final String PREFS_ATTENDANCE = "attendance";
	
	
	public StudentAttendanceListAdaptor(Context context, int resource, ArrayList<DataBeanStudent> objects, String value, String subject, String selectAll) {
		super(context, resource, objects);
		this.objects = objects;
		this.value = value;
		this.subject = subject;
		this.selectAll = selectAll;
	}
	
	
	@NonNull
	@SuppressLint("InflateParams")
	public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
		
		View v = convertView;
		
		if (v == null) {
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			assert inflater != null;
			v = inflater.inflate(R.layout.user_attendance_list_item1, null);
		}
		
		final DataBeanStudent dbItemsDist = objects.get(position);
		
		if (dbItemsDist != null) {
			
			TextView clgname = v.findViewById(R.id.clgname);
			TextView tv_leave_status = v.findViewById(R.id.tv_leave_status);
			CircleImageView userLogo = v.findViewById(R.id.userLogo);
			RadioGroup rg = v.findViewById(R.id.rg);
			RadioButton rb_present = v.findViewById(R.id.rb_present);
			RadioButton rb_absent = v.findViewById(R.id.rb_absent);
			RadioButton rb_leave = v.findViewById(R.id.rb_leave);
			ImageView next = v.findViewById(R.id.next);
			
			SharedPreferences settings = getContext().getSharedPreferences(PREFS_NAME, 0);
			clgname.setText(dbItemsDist.getUserName());
			
			if (dbItemsDist.getUserImg().equals("0")) {
				userLogo.setImageDrawable(v.getContext().getResources().getDrawable(R.drawable.user_place_hoder));
			} else if (!TextUtils.isEmpty(dbItemsDist.getUserImg())) {
				String url = settings.getString("imageUrl", "") + settings.getString("userSchoolId", "") + "/users/students/profile/" + dbItemsDist.getUserImg();
				Picasso.with(getContext()).load(url).error(R.drawable.dummy).placeholder(R.drawable.dummy).into(userLogo);
			} else {
				userLogo.setImageDrawable(v.getContext().getResources().getDrawable(R.drawable.user_place_hoder));
			}

			if(value.equals("take")) {
				if (selectAll.equals("present")) {
					rb_present.setChecked(true);
					attendanceStatus = "Present";
					dbs = new DataBeanStudent();
					dbs.setUserID(dbItemsDist.getUserID());
					dbs.setUserLoginId(attendanceStatus);
					for (int i = 0; i < studentArray.size(); i++) {
						if (studentArray.get(i).getUserID().equals(dbItemsDist.getUserID())) {
							studentArray.remove(i);
							break;
						}
					}
					studentArray.add(dbs);
					saveDataInSP(studentArray);
				} else if (selectAll.equals("absent")) {
					rb_absent.setChecked(true);
					attendanceStatus = "Absent";
					dbs = new DataBeanStudent();
					dbs.setUserID(dbItemsDist.getUserID());
					dbs.setUserLoginId(attendanceStatus);
					for (int i = 0; i < studentArray.size(); i++) {
						if (studentArray.get(i).getUserID().equals(dbItemsDist.getUserID())) {
							studentArray.remove(i);
							break;
						}
					}
					studentArray.add(dbs);
					saveDataInSP(studentArray);
				}
			}
			
			if (value.equals("take")) {
				switch (objects.get(position).getUserClass()) {
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
						if (objects.get(position).getUserLoginId().equals("1")) {
							tv_leave_status.setVisibility(View.VISIBLE);
							rg.setVisibility(View.GONE);
							rb_absent.setEnabled(false);
							rb_leave.setEnabled(false);
							rb_present.setEnabled(false);
							final View finalV = v;
							
							rg.setOnClickListener(view -> Toast.makeText(finalV.getContext(), "Student is in leave", Toast.LENGTH_SHORT).show());
							clgname.setOnClickListener(view1 -> Toast.makeText(finalV.getContext(), "Student is in leave", Toast.LENGTH_SHORT).show());
							attendanceStatus = "Leave";
							dbs = new DataBeanStudent();
							dbs.setUserID(dbItemsDist.getUserID());
							dbs.setUserLoginId(attendanceStatus);
							studentArray.add(dbs);
							saveDataInSP(studentArray);
						}
						
						rb_absent.setOnClickListener(view -> {
							attendanceStatus = "Absent";
							dbs = new DataBeanStudent();
							dbs.setUserID(dbItemsDist.getUserID());
							dbs.setUserLoginId(attendanceStatus);
							for (int i = 0; i < studentArray.size(); i++) {
								if (studentArray.get(i).getUserID().equals(dbItemsDist.getUserID())) {
									studentArray.remove(i);
									break;
								}
							}
							studentArray.add(dbs);
							saveDataInSP(studentArray);
						});
						
						rb_leave.setOnClickListener(view -> {
							attendanceStatus = "Leave";
							dbs = new DataBeanStudent();
							dbs.setUserID(dbItemsDist.getUserID());
							dbs.setUserLoginId(attendanceStatus);
							for (int i = 0; i < studentArray.size(); i++) {
								if (studentArray.get(i).getUserID().equals(dbItemsDist.getUserID())) {
									studentArray.remove(i);
									break;
								}
							}
							studentArray.add(dbs);
							saveDataInSP(studentArray);
						});
						
						rb_present.setOnClickListener(view -> {
							attendanceStatus = "Present";
							dbs = new DataBeanStudent();
							dbs.setUserID(dbItemsDist.getUserID());
							dbs.setUserLoginId(attendanceStatus);
							for (int i = 0; i < studentArray.size(); i++) {
								if (studentArray.get(i).getUserID().equals(dbItemsDist.getUserID())) {
									studentArray.remove(i);
									break;
								}
							}
							studentArray.add(dbs);
							saveDataInSP(studentArray);
						});
				}
			} else if (value.equals("view")) {
				next.setVisibility(View.VISIBLE);
				clgname.setOnClickListener(v1 -> {
					Intent intent = new Intent(getContext(), StudentAttendanceActivity.class);
					intent.putExtra("studentId", dbItemsDist.getUserID());
					intent.putExtra("studentImg", dbItemsDist.getUserImg());
					intent.putExtra("subject", subject);
					getContext().startActivity(intent);
				});
				userLogo.setOnClickListener(v1 -> {
					Intent intent = new Intent(getContext(), StudentAttendanceActivity.class);
					intent.putExtra("studentId", dbItemsDist.getUserID());
					intent.putExtra("studentImg", dbItemsDist.getUserImg());
					intent.putExtra("subject", subject);
					getContext().startActivity(intent);
				});
				v.setOnClickListener(v1 -> {
					Intent intent = new Intent(getContext(), StudentAttendanceActivity.class);
					intent.putExtra("studentId", dbItemsDist.getUserID());
					intent.putExtra("studentImg", dbItemsDist.getUserImg());
					intent.putExtra("subject", subject);
					getContext().startActivity(intent);
				});
				switch (dbItemsDist.getUserLoginId()) {
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
	
	private void saveDataInSP(ArrayList<DataBeanStudent> studentArray) {
		Gson gson = new GsonBuilder().create();
		JsonArray myCustomArray = gson.toJsonTree(studentArray).getAsJsonArray();
		SharedPreferences sp_attend = getContext().getSharedPreferences(PREFS_ATTENDANCE, 0);
		SharedPreferences.Editor se_attend = sp_attend.edit();
		se_attend.clear();
		se_attend.putString("json_attend", "" + myCustomArray);
		se_attend.putString("studentArray", "" + studentArray.size());
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

