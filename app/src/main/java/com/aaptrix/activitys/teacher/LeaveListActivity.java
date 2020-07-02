package com.aaptrix.activitys.teacher;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.appbar.AppBarLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import java.util.Objects;

import com.aaptrix.activitys.student.LeaveApplicationActivity;
import com.aaptrix.databeans.DataBeanLeaves;
import com.aaptrix.adaptor.LeaveListAdaptor;
import com.aaptrix.R;

import static com.aaptrix.tools.HttpUrl.ALL_FILTER_LEAVES;
import static com.aaptrix.tools.HttpUrl.ALL_LEAVES;
import static com.aaptrix.tools.HttpUrl.ALL_STUDENT_LEAVES;
import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREF_COLOR;

/**
 * Created by Administrator on 11/29/2017.
 */

public class LeaveListActivity extends AppCompatActivity {
	
	private SharedPreferences settings;
	SharedPreferences.Editor editor;
	String userId, userName, userPassword, userSchoolLogo, numberOfUser, userSchoolId, userrType;
	AppBarLayout appBarLayout;
	SharedPreferences.Editor editorColor;
	String selToolColor, selDrawerColor, selStatusColor, selTextColor1, selTextColor2;
	TextView tool_title;
	ListView leave_list;
	TextView no_leave;
	private SwipeRefreshLayout mSwipeRefreshLayout;
	String selBatch = "";
	ArrayList<DataBeanLeaves> leavesArray = new ArrayList<>();
	DataBeanLeaves dbl;
	String leavesId, studentName, leaveUserId, leaveSubject, leaveDetails, leaveStartDate, leaveEndDate, leaveStatus, leaveImg;
	LeaveListAdaptor leaveListAdaptor;
	int a = 10;
	View footerView;
	String skip;
	LinearLayout add_layout;
	ImageView iv_add_more;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.leave_list_layout);
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		appBarLayout = findViewById(R.id.appBarLayout);
		tool_title = findViewById(R.id.tool_title);
		
		no_leave = findViewById(R.id.no_leave);
		leave_list = findViewById(R.id.leave_list);
		mSwipeRefreshLayout = findViewById(R.id.activity_main_swipe_refresh_layout);
		mSwipeRefreshLayout.setRefreshing(false);
		mSwipeRefreshLayout.setEnabled(false);
		add_layout = findViewById(R.id.add_layout);
		iv_add_more = findViewById(R.id.iv_add_more);
		
		settings = getSharedPreferences(PREFS_NAME, 0);
		editor = settings.edit();
		userId = settings.getString("userID", "");
		userName = settings.getString("userName", "");
		userrType = settings.getString("userrType", "");
		userSchoolId = settings.getString("userSchoolId", "");
		userPassword = settings.getString("userPassword", "");
		userSchoolLogo = settings.getString("userSchoolLogo", "");
		numberOfUser = settings.getString("numberOfUser", "");

		if (userrType.equals("Student")) {
			add_layout.setVisibility(View.VISIBLE);
		} else {
			add_layout.setVisibility(View.GONE);
		}

		SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
		editorColor = settingsColor.edit();
		selToolColor = settingsColor.getString("tool", "");
		selDrawerColor = settingsColor.getString("drawer", "");
		selStatusColor = settingsColor.getString("status", "");
		selTextColor1 = settingsColor.getString("text1", "");
		selTextColor2 = settingsColor.getString("text2", "");

		callFunction("0");

		iv_add_more.setOnClickListener(v -> {
			if (isInternetOn()) {
				Intent i = new Intent(this, LeaveApplicationActivity.class);
				startActivity(i);
				overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
			} else {
				Toast.makeText(this, "No internet", Toast.LENGTH_SHORT).show();
			}
		});
		
		appBarLayout.setBackgroundColor(Color.parseColor(selToolColor));
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			Window window = getWindow();
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.setStatusBarColor(Color.parseColor(selStatusColor));
		}
		tool_title.setTextColor(Color.parseColor(selTextColor1));
		iv_add_more.setBackgroundColor(Color.parseColor(selToolColor));
		GradientDrawable bgShape = (GradientDrawable) add_layout.getBackground();
		bgShape.setColor(Color.parseColor(selToolColor));
		
	}
	
	private void callFunction(String s) {
		if (isInternetOn()) {
			if (userrType.equals("Student")) {
				StudentleaveList b1 = new StudentleaveList(LeaveListActivity.this);
				b1.execute(userId, s);
				selBatch = settings.getString("userSection", "");
			} else {
				selBatch = getIntent().getStringExtra("selBatch");
				GetAllHobbies b1 = new GetAllHobbies(LeaveListActivity.this);
				b1.execute(userSchoolId, userName, userrType, selBatch, s);
			}
		} else {
			Toast.makeText(this, "No Internet", Toast.LENGTH_SHORT).show();
		}
	}
	
//	private void addHeaderFooterView() {
//		LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//		assert inflater != null;
//		footerView = inflater.inflate(R.layout.footer_view, null, false);
//		Button loadmorelayout = footerView.findViewById(R.id.header_footer_title);
//
//		GradientDrawable bgShape = (GradientDrawable) loadmorelayout.getBackground();
//		bgShape.setColor(Color.parseColor(selToolColor));
//		loadmorelayout.setTextColor(Color.parseColor(selTextColor1));
//		loadmorelayout.setOnClickListener(view -> {
//			if (isInternetOn()) {
//				callFunction("" + a);
//				a = a + 10;
//			} else {
//				Toast.makeText(LeaveListActivity.this, "No internet", Toast.LENGTH_SHORT).show();
//			}
//		});
//		leave_list.addFooterView(footerView);//Add view to list view as footer view
//
//	}
	
	@SuppressLint("StaticFieldLeak")
	public class StudentleaveList extends AsyncTask<String, String, String> {
		Context ctx;
		
		StudentleaveList(Context ctx) {
			this.ctx = ctx;
		}
		
		@Override
		protected void onPreExecute() {
			//loader.setVisibility(View.VISIBLE);
			mSwipeRefreshLayout.setRefreshing(true);
			super.onPreExecute();
			
		}
		
		@Override
		protected String doInBackground(String... params) {
			
			String userId = params[0];
			skip = params[1];
			String data;
			
			try {
				
				URL url = new URL(ALL_STUDENT_LEAVES);
				HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
				httpURLConnection.setRequestMethod("POST");
				httpURLConnection.setDoOutput(true);
				httpURLConnection.setDoInput(true);
				
				OutputStream outputStream = httpURLConnection.getOutputStream();
				
				BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
				data = URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8");
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
			Log.e("Leave_Request", "" + result);
			//loader.setVisibility(View.GONE);
			mSwipeRefreshLayout.setRefreshing(false);
			
			//	pDialog.dismiss();
			if (result.equals("{\"result\":null}")) {
				if (leavesArray.size() > 0) {
					no_leave.setVisibility(View.GONE);
					leave_list.removeFooterView(footerView);
				} else {
					no_leave.setVisibility(View.VISIBLE);
					leave_list.setVisibility(View.GONE);
				}
			} else {
				no_leave.setVisibility(View.GONE);
				leave_list.setVisibility(View.VISIBLE);
				
				try {
					leavesArray.clear();
					JSONObject jsonRootObject = new JSONObject(result);
					JSONArray jsonArray = jsonRootObject.getJSONArray("result");
					for (int i = 0; i < jsonArray.length(); i++) {
						dbl = new DataBeanLeaves();
						
						JSONObject jsonObject = jsonArray.getJSONObject(i);
						studentName = jsonObject.getString("tbl_users_name");
						leavesId = jsonObject.getString("tbl_leave_id");
						leaveUserId = jsonObject.getString("tbl_users_id");
						leaveSubject = jsonObject.getString("tbl_leave_subject");
						leaveDetails = jsonObject.getString("tbl_leave_compose");
						leaveStartDate = jsonObject.getString("tbl_leave_startdate");
						leaveEndDate = jsonObject.getString("tbl_leave_enddate");
						leaveStatus = jsonObject.getString("tbl_leave_status");
						leaveImg = jsonObject.getString("tbl_leave_img");
						dbl.setStudentName(studentName);
						dbl.setLeavesId(leavesId);
						dbl.setUserId(leaveUserId);
						dbl.setLeaveSubject(leaveSubject);
						dbl.setLeaveDetails(leaveDetails);
						dbl.setLeaveStartDate(leaveStartDate);
						dbl.setLeaveEndDate(leaveEndDate);
						dbl.setLeaveStatus(leaveStatus);
						dbl.setLeaveImg(leaveImg);
						leavesArray.add(dbl);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				listItms(skip);
				super.onPostExecute(result);
			}
		}
		
	}
	
	@SuppressLint("StaticFieldLeak")
	public class GetAllHobbies extends AsyncTask<String, String, String> {
		Context ctx;
		
		GetAllHobbies(Context ctx) {
			this.ctx = ctx;
		}
		
		@Override
		protected void onPreExecute() {
			//loader.setVisibility(View.VISIBLE);
			mSwipeRefreshLayout.setRefreshing(true);
			super.onPreExecute();
		}
		
		@Override
		protected String doInBackground(String... params) {
			
			String userId = params[0];
			String userName = params[1];
			String userType = params[2];
			String selBatch = params[3];
			skip = params[4];
			String data;
			try {
				URL url = new URL(ALL_LEAVES);
				HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
				httpURLConnection.setRequestMethod("POST");
				httpURLConnection.setDoOutput(true);
				httpURLConnection.setDoInput(true);
				
				OutputStream outputStream = httpURLConnection.getOutputStream();
				
				BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
				data = URLEncoder.encode("school_id", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8") + "&" +
						URLEncoder.encode("userName", "UTF-8") + "=" + URLEncoder.encode(userName, "UTF-8") + "&" +
						URLEncoder.encode("userType", "UTF-8") + "=" + URLEncoder.encode(userType, "UTF-8") + "&" +
						URLEncoder.encode("selBatch", "UTF-8") + "=" + URLEncoder.encode(selBatch, "UTF-8");
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
			Log.e("Leave_Request", "" + result);
			//loader.setVisibility(View.GONE);
			mSwipeRefreshLayout.setRefreshing(false);
			
			//	pDialog.dismiss();
			if (result.equals("{\"result\":null}")) {
				if (leavesArray.size() > 0) {
					no_leave.setVisibility(View.GONE);
					leave_list.removeFooterView(footerView);
				} else {
					no_leave.setVisibility(View.VISIBLE);
					leave_list.setVisibility(View.GONE);
				}
			} else {
				no_leave.setVisibility(View.GONE);
				leave_list.setVisibility(View.VISIBLE);
				
				try {
					JSONObject jsonRootObject = new JSONObject(result);
					JSONArray jsonArray = jsonRootObject.getJSONArray("result");
					
					for (int i = 0; i < jsonArray.length(); i++) {
						dbl = new DataBeanLeaves();
						
						JSONObject jsonObject = jsonArray.getJSONObject(i);
						studentName = jsonObject.getString("tbl_users_name");
						leavesId = jsonObject.getString("tbl_leave_id");
						leaveUserId = jsonObject.getString("tbl_users_id");
						leaveSubject = jsonObject.getString("tbl_leave_subject");
						leaveDetails = jsonObject.getString("tbl_leave_compose");
						leaveStartDate = jsonObject.getString("tbl_leave_startdate");
						leaveEndDate = jsonObject.getString("tbl_leave_enddate");
						leaveStatus = jsonObject.getString("tbl_leave_status");
						leaveImg = jsonObject.getString("tbl_leave_img");
						
						dbl.setStudentName(studentName);
						dbl.setLeavesId(leavesId);
						dbl.setUserId(leaveUserId);
						dbl.setLeaveSubject(leaveSubject);
						dbl.setLeaveDetails(leaveDetails);
						dbl.setLeaveStartDate(leaveStartDate);
						dbl.setLeaveEndDate(leaveEndDate);
						dbl.setLeaveStatus(leaveStatus);
						dbl.setLeaveImg(leaveImg);
						leavesArray.add(dbl);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				listItms(skip);
				super.onPostExecute(result);
			}
		}
		
	}
	
	@SuppressLint("StaticFieldLeak")
	public class GetFilterLeave extends AsyncTask<String, String, String> {
		Context ctx;
		
		GetFilterLeave(Context ctx) {
			this.ctx = ctx;
		}
		
		@Override
		protected void onPreExecute() {
			//loader.setVisibility(View.VISIBLE);
			mSwipeRefreshLayout.setRefreshing(true);
			
			super.onPreExecute();
			
		}
		
		@Override
		protected String doInBackground(String... params) {
			
			String userId = params[0];
			String type = params[1];
			String userStudentId = params[2];
			String userType = params[3];
			String selBatch = params[4];
			skip = params[5];
			
			String data;
			
			try {
				URL url = new URL(ALL_FILTER_LEAVES);
				HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
				httpURLConnection.setRequestMethod("POST");
				httpURLConnection.setDoOutput(true);
				httpURLConnection.setDoInput(true);
				
				OutputStream outputStream = httpURLConnection.getOutputStream();
				
				BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
				data = URLEncoder.encode("school_id", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8") + "&" +
						URLEncoder.encode("type", "UTF-8") + "=" + URLEncoder.encode(type, "UTF-8") + "&" +
						URLEncoder.encode("userStudentId", "UTF-8") + "=" + URLEncoder.encode(userStudentId, "UTF-8") + "&" +
						URLEncoder.encode("userType", "UTF-8") + "=" + URLEncoder.encode(userType, "UTF-8") + "&" +
						URLEncoder.encode("selBatch", "UTF-8") + "=" + URLEncoder.encode(selBatch, "UTF-8");
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
			//loader.setVisibility(View.GONE);
			mSwipeRefreshLayout.setRefreshing(false);
			
			//	pDialog.dismiss();
			if (result.equals("{\"result\":null}")) {
				leavesArray.clear();
				if (leavesArray.size() > 0) {
					no_leave.setVisibility(View.GONE);
					leave_list.removeFooterView(footerView);
				} else {
					no_leave.setVisibility(View.VISIBLE);
					leave_list.setVisibility(View.GONE);
				}
			} else {
				no_leave.setVisibility(View.GONE);
				leave_list.setVisibility(View.VISIBLE);
				leavesArray.clear();
				try {
					JSONObject jsonRootObject = new JSONObject(result);
					JSONArray jsonArray = jsonRootObject.getJSONArray("result");
					for (int i = 0; i < jsonArray.length(); i++) {
						dbl = new DataBeanLeaves();
						
						JSONObject jsonObject = jsonArray.getJSONObject(i);
						studentName = jsonObject.getString("tbl_users_name");
						leavesId = jsonObject.getString("tbl_leave_id");
						leaveUserId = jsonObject.getString("tbl_users_id");
						leaveSubject = jsonObject.getString("tbl_leave_subject");
						leaveDetails = jsonObject.getString("tbl_leave_compose");
						leaveStartDate = jsonObject.getString("tbl_leave_startdate");
						leaveEndDate = jsonObject.getString("tbl_leave_enddate");
						leaveStatus = jsonObject.getString("tbl_leave_status");
						leaveImg = jsonObject.getString("tbl_leave_img");
						
						dbl.setStudentName(studentName);
						dbl.setLeavesId(leavesId);
						dbl.setUserId(leaveUserId);
						dbl.setLeaveSubject(leaveSubject);
						dbl.setLeaveDetails(leaveDetails);
						dbl.setLeaveStartDate(leaveStartDate);
						dbl.setLeaveEndDate(leaveEndDate);
						dbl.setLeaveStatus(leaveStatus);
						dbl.setLeaveImg(leaveImg);
						leavesArray.add(dbl);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				listItms(skip);
				super.onPostExecute(result);
			}
		}
		
	}
	
	private void listItms(String skip) {
		leaveListAdaptor = new LeaveListAdaptor(LeaveListActivity.this, R.layout.leave_list_item, leavesArray, userrType, selBatch, LeaveListActivity.this);
		leave_list.setAdapter(leaveListAdaptor);
		leave_list.setSelectionFromTop(Integer.parseInt(skip), 0);
		leave_list.removeFooterView(footerView);//Add view to list view as footer view
//		addHeaderFooterView();
		if (leavesArray.size() < 9) {
			leave_list.removeFooterView(footerView);//Add view to list view as footer view
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.leave_status_menu, menu);//Menu Resource, Menu
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		if (item.getItemId() == android.R.id.home) {
			onBackPressed();
		}
		switch (item.getItemId()) {
			case R.id.pending:
				
				GetFilterLeave b1 = new GetFilterLeave(LeaveListActivity.this);
				b1.execute(userSchoolId, "Pending", userId, userrType, selBatch, "0");
				return true;
			case R.id.approved:
				
				GetFilterLeave b2 = new GetFilterLeave(LeaveListActivity.this);
				b2.execute(userSchoolId, "Approved", userId, userrType, selBatch, "0");
				return true;
			case R.id.rejected:
				
				GetFilterLeave b3 = new GetFilterLeave(LeaveListActivity.this);
				b3.execute(userSchoolId, "Rejected", userId, userrType, selBatch, "0");
				return true;
			
			case R.id.all:
				
				GetFilterLeave b4 = new GetFilterLeave(LeaveListActivity.this);
				b4.execute(userSchoolId, "All", userId, userrType, selBatch, "0");
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}
	
	public final boolean isInternetOn() {
		ConnectivityManager connec = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		// Check for network connections
		assert connec != null;
		if (connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
				connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
				connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
				connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED) {
			
			// if connected with internet
			return true;
		} else if (
				connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
						connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED) {
			return false;
		}
		return false;
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//		if (userrType.equals("Student")) {
//			Intent i = new Intent(LeaveListActivity.this, StudentAttendanceActivity.class);
//			startActivity(i);
//			finish();
//			overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//		} else {
//			Intent i = new Intent(LeaveListActivity.this, IntermidiateScreenActivity.class);
//			i.putExtra("str_tool_title", "Leave Requests");
//			startActivity(i);
//			finish();
//			overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
//		}
	}
}
