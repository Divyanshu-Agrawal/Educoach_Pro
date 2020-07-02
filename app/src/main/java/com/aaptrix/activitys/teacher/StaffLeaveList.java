package com.aaptrix.activitys.teacher;

import com.aaptrix.adaptor.StaffLeaveAdapter;
import com.aaptrix.databeans.DataBeanLeaves;
import com.aaptrix.R;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.AppBarLayout;

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

import static com.aaptrix.tools.HttpUrl.STAFF_OWN_LEAVES;
import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREF_COLOR;

public class StaffLeaveList extends AppCompatActivity {
	
	String userId, userName, userSchoolId, userrType;
	AppBarLayout appBarLayout;
	String selToolColor, selDrawerColor, selStatusColor, selTextColor1, selTextColor2;
	TextView tool_title;
	ListView leave_list;
	TextView no_leave;
	private SwipeRefreshLayout mSwipeRefreshLayout;
	ArrayList<DataBeanLeaves> leavesArray = new ArrayList<>();
	DataBeanLeaves dbl;
	String leavesId, studentName, leaveUserId, leaveSubject, leaveDetails, leaveStartDate, leaveEndDate, leaveStatus, leaveImg;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_staff_leave_list);
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
		
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		userId = settings.getString("userID", "");
		userName = settings.getString("userName", "");
		userrType = settings.getString("userrType", "");
		userSchoolId = settings.getString("userSchoolId", "");

		SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
		selToolColor = settingsColor.getString("tool", "");
		selDrawerColor = settingsColor.getString("drawer", "");
		selStatusColor = settingsColor.getString("status", "");
		selTextColor1 = settingsColor.getString("text1", "");
		selTextColor2 = settingsColor.getString("text2", "");
		
		if (isInternetOn()) {
			AllleaveList b1 = new AllleaveList(this);
			b1.execute(userId);
		} else {
			Toast.makeText(this, "No Internet", Toast.LENGTH_SHORT).show();
		}
		
		appBarLayout.setBackgroundColor(Color.parseColor(selToolColor));
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			Window window = getWindow();
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.setStatusBarColor(Color.parseColor(selStatusColor));
		}
		tool_title.setTextColor(Color.parseColor(selTextColor1));
	}
	
	@SuppressLint("StaticFieldLeak")
	public class AllleaveList extends AsyncTask<String, String, String> {
		Context ctx;
		
		AllleaveList(Context ctx) {
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
			String data;
			
			try {
				
				URL url = new URL(STAFF_OWN_LEAVES);
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
			mSwipeRefreshLayout.setRefreshing(false);
			if (result.equals("{\"result\":null}")) {
				if (leavesArray.size() > 0) {
					no_leave.setVisibility(View.GONE);
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
				listItms();
				super.onPostExecute(result);
			}
		}
		
	}
	
	private void listItms() {
		StaffLeaveAdapter leaveListAdaptor = new StaffLeaveAdapter(this, R.layout.list_staff_leave, leavesArray, this);
		leave_list.setAdapter(leaveListAdaptor);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		if (item.getItemId() == android.R.id.home) {
			onBackPressed();
		}
		return true;
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
	}
}
