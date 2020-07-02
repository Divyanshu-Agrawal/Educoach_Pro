package com.aaptrix.adaptor;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import com.aaptrix.activitys.teacher.LeaveListActivity;
import com.aaptrix.activitys.teacher.TeacherRequestLeaveActivity;
import com.aaptrix.databeans.DataBeanLeaves;
import com.aaptrix.R;
import androidx.annotation.NonNull;

import static com.aaptrix.tools.HttpUrl.SUBMIT_STATUS;

/**
 * Created by googledeveloper on 24/2/17.
 */
public class LeaveListAdaptor extends ArrayAdapter<DataBeanLeaves> {
	
	private ArrayList<DataBeanLeaves> objects;
	String position;
	private String userrType;
	private AlertDialog.Builder alert;
	private String selBatch;
	public static final String PREF_COLOR = "COLOR";
	private LeaveListActivity leaveListActivity;
	private String selToolColor;
	
	public LeaveListAdaptor(Context context, int resource, ArrayList<DataBeanLeaves> objects, String userrType, String selBatch, LeaveListActivity leaveListActivity) {
		super(context, resource, objects);
		this.objects = objects;
		this.userrType = userrType;
		this.selBatch = selBatch;
		this.leaveListActivity = leaveListActivity;
	}
	
	
	@SuppressLint({"InflateParams", "SetTextI18n"})
	@NonNull
	public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
		
		View v = convertView;
		
		if (v == null) {
			LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			assert inflater != null;
			v = inflater.inflate(R.layout.leave_list_item, null);
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
			selToolColor = settingsColor.getString("tool", "");
			String selDrawerColor = settingsColor.getString("drawer", "");
			
			if (userrType.equals("Student")) {
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
					Intent i = new Intent(getContext(), TeacherRequestLeaveActivity.class);
					i.putExtra("userId", dbItemsDist.getUserId());
					i.putExtra("leaveId", dbItemsDist.getLeavesId());
					i.putExtra("leaveSubject", dbItemsDist.getLeaveSubject());
					i.putExtra("leaveStartDate", dbItemsDist.getLeaveStartDate());
					i.putExtra("leaveEndDate", dbItemsDist.getLeaveEndDate());
					i.putExtra("leaveDetails", dbItemsDist.getLeaveDetails());
					i.putExtra("strStatus", dbItemsDist.getLeaveStatus());
					i.putExtra("leaveImg", dbItemsDist.getLeaveImg());
					getContext().startActivity(i);
					leaveListActivity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
					
				});
				leaveLayout.setOnClickListener(view -> {
					Intent i = new Intent(getContext(), TeacherRequestLeaveActivity.class);
					i.putExtra("userId", dbItemsDist.getUserId());
					i.putExtra("leaveId", dbItemsDist.getLeavesId());
					i.putExtra("leaveSubject", dbItemsDist.getLeaveSubject());
					i.putExtra("leaveStartDate", dbItemsDist.getLeaveStartDate());
					i.putExtra("leaveEndDate", dbItemsDist.getLeaveEndDate());
					i.putExtra("leaveDetails", dbItemsDist.getLeaveDetails());
					i.putExtra("strStatus", dbItemsDist.getLeaveStatus());
					i.putExtra("leaveImg", dbItemsDist.getLeaveImg());
					getContext().startActivity(i);
					leaveListActivity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
				});
				
				
			} else {
				String strStatus = dbItemsDist.getLeaveStatus();
				
				switch (strStatus) {
					case "Pending":
						status.setVisibility(View.GONE);
						accept.setVisibility(View.VISIBLE);
						reject.setVisibility(View.VISIBLE);
						accept.setOnClickListener(view -> {
							LayoutInflater factory = LayoutInflater.from(getContext());
							View textEntryView = factory.inflate(R.layout.leave_confirm_layout, null);
							
							TextView tv_alert = textEntryView.findViewById(R.id.tv_alert);
							TextView tv_alert_body = textEntryView.findViewById(R.id.tv_alert_body);
							tv_alert.setText("Accept Leave");
							tv_alert_body.setText("Are you sure you want to accept this leave request?");
							alert = new AlertDialog.Builder(getContext(), R.style.DialogTheme);
							//.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
							alert.setView(textEntryView).setPositiveButton("Yes",
									(dialog, whichButton) -> {
										String status1 = "Approved";
										SubmitStatus submitStatus = new SubmitStatus(getContext());
										submitStatus.execute(status1, dbItemsDist.getLeavesId());
									}).setNegativeButton("Cancel", null);
							AlertDialog alertDialog = alert.create();
							alertDialog.show();
							Button theButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
							Button theButton1 = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
							theButton.setTextColor(Color.parseColor(selToolColor));
							theButton1.setTextColor(Color.parseColor(selToolColor));
							
						});
						
						reject.setOnClickListener(view -> {
							
							LayoutInflater factory = LayoutInflater.from(getContext());
							View textEntryView = factory.inflate(R.layout.leave_confirm_layout, null);
							TextView tv_alert = textEntryView.findViewById(R.id.tv_alert);
							TextView tv_alert_body = textEntryView.findViewById(R.id.tv_alert_body);
							
							tv_alert.setText("Reject Leave");
							tv_alert_body.setText("Are you sure you want to reject this leave request?");
							
							alert = new AlertDialog.Builder(getContext(), R.style.DialogTheme);
							//.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
							alert.setView(textEntryView).setPositiveButton("Yes",
									(dialog, whichButton) -> {
										String status12 = "Rejected";
										SubmitStatus submitStatus = new SubmitStatus(getContext());
										submitStatus.execute(status12, dbItemsDist.getLeavesId());
									}).setNegativeButton("Cancel", null);
							AlertDialog alertDialog = alert.create();
							alertDialog.show();
							Button theButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
							Button theButton1 = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
							theButton.setTextColor(Color.parseColor(selToolColor));
							theButton1.setTextColor(Color.parseColor(selToolColor));
							
						});
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
				
				
				iv_next.setOnClickListener(view -> {
					Intent i = new Intent(getContext(), TeacherRequestLeaveActivity.class);
					i.putExtra("userId", dbItemsDist.getUserId());
					i.putExtra("leaveId", dbItemsDist.getLeavesId());
					i.putExtra("leaveSubject", dbItemsDist.getLeaveSubject());
					i.putExtra("leaveStartDate", dbItemsDist.getLeaveStartDate());
					i.putExtra("leaveEndDate", dbItemsDist.getLeaveEndDate());
					i.putExtra("leaveDetails", dbItemsDist.getLeaveDetails());
					i.putExtra("strStatus", dbItemsDist.getLeaveStatus());
					i.putExtra("leaveImg", dbItemsDist.getLeaveImg());
					i.putExtra("selBatch", selBatch);
					getContext().startActivity(i);
					leaveListActivity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
					
					
				});
				
				leaveLayout.setOnClickListener(view -> {
					Intent i = new Intent(getContext(), TeacherRequestLeaveActivity.class);
					i.putExtra("userId", dbItemsDist.getUserId());
					i.putExtra("leaveId", dbItemsDist.getLeavesId());
					i.putExtra("leaveSubject", dbItemsDist.getLeaveSubject());
					i.putExtra("leaveStartDate", dbItemsDist.getLeaveStartDate());
					i.putExtra("leaveEndDate", dbItemsDist.getLeaveEndDate());
					i.putExtra("leaveDetails", dbItemsDist.getLeaveDetails());
					i.putExtra("strStatus", dbItemsDist.getLeaveStatus());
					i.putExtra("leaveImg", dbItemsDist.getLeaveImg());
					i.putExtra("selBatch", selBatch);
					getContext().startActivity(i);
					leaveListActivity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
					
					
				});
				
				leaveTitle.setText(dbItemsDist.getStudentName());
				leaveDetails.setText(dbItemsDist.getLeaveSubject());
			}
			
			
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
	
	@SuppressLint("StaticFieldLeak")
	public class SubmitStatus extends AsyncTask<String, String, String> {
		Context ctx;
		
		SubmitStatus(Context ctx) {
			this.ctx = ctx;
		}
		
		@Override
		protected void onPreExecute() {
			Toast.makeText(ctx, "Please wait", Toast.LENGTH_SHORT).show();
			super.onPreExecute();
			
		}
		
		@Override
		protected String doInBackground(String... params) {
			
			String status = params[0];
			String leaveId = params[1];
			String data;
			
			try {
				
				URL url = new URL(SUBMIT_STATUS);
				HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
				httpURLConnection.setRequestMethod("POST");
				httpURLConnection.setDoOutput(true);
				httpURLConnection.setDoInput(true);
				
				OutputStream outputStream = httpURLConnection.getOutputStream();
				
				BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
				data = URLEncoder.encode("status", "UTF-8") + "=" + URLEncoder.encode(status, "UTF-8") + "&" +
						URLEncoder.encode("leaveId", "UTF-8") + "=" + URLEncoder.encode(leaveId, "UTF-8");
				outputStream.write(data.getBytes());
				
				bufferedWriter.write(data);
				bufferedWriter.flush();
				bufferedWriter.close();
				outputStream.flush();
				outputStream.close();
				
				InputStream inputStream = httpURLConnection.getInputStream();
				BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1));
				StringBuilder response = new StringBuilder();
				String line;
				
				while ((line = bufferedReader.readLine()) != null) {
					response.append(line);
				}
				bufferedReader.close();
				inputStream.close();
				httpURLConnection.disconnect();
				return response.toString();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			Log.d("Json", "" + result);
			
			if (result.equals("{\"result\":null}")) {
				Toast.makeText(ctx, "Server Issue", Toast.LENGTH_SHORT).show();
			} else {
				Intent i = new Intent(ctx, LeaveListActivity.class);
				i.putExtra("str_tool_title", "Leave Requests");
				i.putExtra("selBatch", selBatch);
				ctx.startActivity(i);
				leaveListActivity.overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
				Toast.makeText(ctx, "" + result, Toast.LENGTH_SHORT).show();
				super.onPostExecute(result);
			}
		}
		
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

