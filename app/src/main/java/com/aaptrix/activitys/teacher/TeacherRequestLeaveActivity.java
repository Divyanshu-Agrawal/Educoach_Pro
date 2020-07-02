package com.aaptrix.activitys.teacher;

import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.appbar.AppBarLayout;

import com.aaptrix.activitys.FullScreenImageActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

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
import java.util.GregorianCalendar;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;
import com.aaptrix.databeans.DatabeanAttendance;
import com.aaptrix.databeans.DatabeanEvents;
import com.aaptrix.R;

import static com.aaptrix.tools.HttpUrl.ALL_ATTENDANCE;
import static com.aaptrix.tools.HttpUrl.SUBMIT_STATUS;
import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREFS_USER;
import static com.aaptrix.tools.SPClass.PREF_COLOR;

public class TeacherRequestLeaveActivity extends AppCompatActivity {
	
	CircleImageView iv_user_img;
	private SharedPreferences settings;
	SharedPreferences.Editor editor;
	String userId, roleId, schoolId, userSection, userRollNumber, userClassTeacher;
	
	//offline
	AlertDialog.Builder alert;
	SharedPreferences.Editor editorColor;
	String selToolColor, selDrawerColor, selStatusColor, selTextColor1, selTextColor2, userJson, numberOfUser;
	AppBarLayout appBarLayout;
	TextView tool_title;
	
	SharedPreferences.Editor editorUser;
	String userID;
	String userLoginId;
	String userName;
	String userPhone;
	String userImg;
	String userrType;
	String userPassword;
	AlertDialog alertDialog;
	
	//calender
	public GregorianCalendar cal_month, cal_month_copy;
	ArrayList<DatabeanEvents> eventsArray = new ArrayList<>();
	DatabeanEvents dbe;
	TextView tv_month_year, tv_present_count, tv_absent_count, tv_year, tv_prese_count, tv_absnt_count, tv_month;
	private SwipeRefreshLayout mSwipeRefreshLayout;
	
	ArrayList<DatabeanAttendance> attandanceArray = new ArrayList<>();
	DatabeanAttendance dbattand;
	String attandId, attandStatus, attandDate;
	TextView percent1, percent2;
	int presentCount, absentCount, totalLeave;
	float totalPresentYear;
	int presentCountMonth, absentCountMonth, totalLeaveMonth;
	float totalPresentMonth;
	ProgressBar progressBar, progressBar1;
	
	SharedPreferences.Editor se_attend;
	public static final String PREFS_ATTEND = "json_attend";
	LinearLayout mainLayoutAttendance, header;
	String userIdA, leaveId;
	MediaPlayer mp;
	TextView accept_leave, reject_leave;
	TextView leaveDetails, leaveSubject, leaveEndDate, leaveStartDate;
	String str_leaveDetails, str_leaveSubject, str_leaveEndDate, str_leaveStartDate, strStatus, leaveImg;
	ImageView imageViewLeave;
	String selBatch = "";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.teacher_request_leave_layout);
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		mp = MediaPlayer.create(this, R.raw.button_click);
		setTitle("");
		setResult(RESULT_OK);
		appBarLayout = findViewById(R.id.appBarLayout);
		mainLayoutAttendance = findViewById(R.id.mainLayoutAttendance);
		header = findViewById(R.id.header);
		tool_title = findViewById(R.id.tool_title);
		//color
		SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
		editorColor = settingsColor.edit();
		selToolColor = settingsColor.getString("tool", "");
		selDrawerColor = settingsColor.getString("drawer", "");
		selStatusColor = settingsColor.getString("status", "");
		selTextColor1 = settingsColor.getString("text1", "");
		selTextColor2 = settingsColor.getString("text2", "");
		
		//typecasting
		iv_user_img = findViewById(R.id.iv_user_img);
		progressBar = findViewById(R.id.progressBar);
		progressBar1 = findViewById(R.id.progressBar1);
		
		
		tv_month = findViewById(R.id.tv_month);
		tv_month_year = findViewById(R.id.tv_month_year);
		tv_present_count = findViewById(R.id.tv_present_count);
		tv_absent_count = findViewById(R.id.tv_absent_count);
		tv_year = findViewById(R.id.tv_year);
		tv_prese_count = findViewById(R.id.tv_prsnt_count);
		tv_absnt_count = findViewById(R.id.tv_absnt_count);
		percent1 = findViewById(R.id.percent1);
		percent2 = findViewById(R.id.percent2);
		
		accept_leave = findViewById(R.id.accept_leave);
		reject_leave = findViewById(R.id.reject_leave);
		
		leaveDetails = findViewById(R.id.leaveDetails);
		leaveSubject = findViewById(R.id.leaveSubject);
		leaveEndDate = findViewById(R.id.leaveEndDate);
		leaveStartDate = findViewById(R.id.leaveStartDate);
		
		tv_month_year = findViewById(R.id.tv_month_year);
		tv_year = findViewById(R.id.tv_year);
		
		TextView status = findViewById(R.id.status);
		imageViewLeave = findViewById(R.id.imageViewLeave);
		
		mSwipeRefreshLayout = findViewById(R.id.activity_main_swipe_refresh_layout);
		mSwipeRefreshLayout.setEnabled(false);
		
		settings = getSharedPreferences(PREFS_NAME, 0);
		editor = settings.edit();
		userId = settings.getString("userID", "");
		userLoginId = settings.getString("userLoginId", "");
		userSection = settings.getString("userSection", "");
		userRollNumber = settings.getString("userRollNumber", "");
		userClassTeacher = settings.getString("userTeacherName", "");
		userrType = settings.getString("userrType", "");
		userPassword = settings.getString("userPassword", "");
		roleId = settings.getString("str_role_id", "");
		schoolId = settings.getString("str_school_id", "");
		numberOfUser = settings.getString("numberOfUser", "");
		
		
		SharedPreferences settingsUser = getSharedPreferences(PREFS_USER, 0);
		editorUser = settingsUser.edit();
		userJson = settingsUser.getString("user", "");
		
		userIdA = getIntent().getStringExtra("userId");
		leaveId = getIntent().getStringExtra("leaveId");
		str_leaveSubject = getIntent().getStringExtra("leaveSubject");
		str_leaveStartDate = getIntent().getStringExtra("leaveStartDate");
		str_leaveEndDate = getIntent().getStringExtra("leaveEndDate");
		str_leaveDetails = getIntent().getStringExtra("leaveDetails");
		strStatus = getIntent().getStringExtra("strStatus");
		leaveImg = getIntent().getStringExtra("leaveImg");
		selBatch = getIntent().getStringExtra("selBatch");
		
		if (leaveImg.equals("0")) {
			imageViewLeave.setVisibility(View.GONE);
		} else {
			Picasso.with(this).load(leaveImg).error(R.drawable.dummy).placeholder(R.drawable.dummy).into(imageViewLeave);
		}
		
		imageViewLeave.setOnClickListener(view -> {
			Intent i = new Intent(TeacherRequestLeaveActivity.this, FullScreenImageActivity.class);
			i.putExtra("leaveImg", leaveImg);
			startActivity(i);
			overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
			
		});
		
		switch (strStatus) {
			case "Pending":
				if (userrType.equals("Student")) {
					status.setVisibility(View.VISIBLE);
					accept_leave.setVisibility(View.GONE);
					reject_leave.setVisibility(View.GONE);
				} else {
					status.setVisibility(View.GONE);
					accept_leave.setVisibility(View.VISIBLE);
					reject_leave.setVisibility(View.VISIBLE);
					
					accept_leave.setOnClickListener(view -> {
						mp.start();
						LayoutInflater factory = LayoutInflater.from(TeacherRequestLeaveActivity.this);
						View textEntryView = factory.inflate(R.layout.leave_confirm_layout, null);
						TextView tv_alert = textEntryView.findViewById(R.id.tv_alert);
						TextView tv_alert_body = textEntryView.findViewById(R.id.tv_alert_body);
						
						tv_alert.setText("Accept Leave");
						tv_alert_body.setText("Are you sure you want to accept this leave request?");
						
						alert = new AlertDialog.Builder(TeacherRequestLeaveActivity.this, R.style.DialogTheme);
						//.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
						alert.setView(textEntryView).setPositiveButton("Yes",
								(dialog, whichButton) -> {
									String status1 = "Approved";
									SubmitStatus submitStatus = new SubmitStatus(TeacherRequestLeaveActivity.this);
									submitStatus.execute(status1, leaveId);
								}).setNegativeButton("Cancel", null);
						AlertDialog alertDialog = alert.create();
						alertDialog.show();
						Button theButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
						Button theButton1 = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
						theButton.setTextColor(Color.parseColor(selToolColor));
						theButton1.setTextColor(Color.parseColor(selToolColor));
						
					});
					
					reject_leave.setOnClickListener(view -> {
						mp.start();
						LayoutInflater factory = LayoutInflater.from(TeacherRequestLeaveActivity.this);
						View textEntryView = factory.inflate(R.layout.leave_confirm_layout, null);
						
						TextView tv_alert = textEntryView.findViewById(R.id.tv_alert);
						TextView tv_alert_body = textEntryView.findViewById(R.id.tv_alert_body);
						
						tv_alert.setText("Reject Leave");
						tv_alert_body.setText("Are you sure you want to reject this leave request?");
						alert = new AlertDialog.Builder(TeacherRequestLeaveActivity.this, R.style.DialogTheme);
						//.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
						alert.setView(textEntryView).setPositiveButton("Yes",
								(dialog, whichButton) -> {
									String status12 = "Rejected";
									SubmitStatus submitStatus = new SubmitStatus(TeacherRequestLeaveActivity.this);
									submitStatus.execute(status12, leaveId);
								}).setNegativeButton("Cancel", null);
						AlertDialog alertDialog = alert.create();
						alertDialog.show();
						Button theButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
						Button theButton1 = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
						theButton.setTextColor(Color.parseColor(selToolColor));
						theButton1.setTextColor(Color.parseColor(selToolColor));
						
					});
				}
				
				break;
			case "Approved":
				status.setVisibility(View.GONE);
				accept_leave.setVisibility(View.VISIBLE);
				reject_leave.setVisibility(View.GONE);
				accept_leave.setText("Accepted");
				break;
			case "Rejected":
				status.setVisibility(View.GONE);
				accept_leave.setVisibility(View.GONE);
				reject_leave.setVisibility(View.VISIBLE);
				reject_leave.setText("Rejected");
				
				break;
		}
		
		if (isInternetOn()) {
			cal_month = (GregorianCalendar) GregorianCalendar.getInstance();
			cal_month_copy = (GregorianCalendar) cal_month.clone();
			eventsArray.clear();
			attandanceArray.clear();
			GetAllAttandance b1 = new GetAllAttandance(TeacherRequestLeaveActivity.this);
			b1.execute(userIdA);
		} else {
			Toast.makeText(TeacherRequestLeaveActivity.this, "No network Please connect with network for update", Toast.LENGTH_SHORT).show();
		}
		
		
		appBarLayout.setBackgroundColor(Color.parseColor(selToolColor));
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			Window window = getWindow();
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.setStatusBarColor(Color.parseColor(selStatusColor));
		}
		tool_title.setTextColor(Color.parseColor(selTextColor1));
		tv_month_year.setTextColor(Color.parseColor(selToolColor));
		tv_year.setTextColor(Color.parseColor(selToolColor));
		header.setBackgroundColor(Color.parseColor(selToolColor));
		
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mp.release();
	}
	
	private void startAnimation(int d, int b) {
		ObjectAnimator progressAnimator = ObjectAnimator.ofInt(progressBar, "progress", 100, (100 - d));
		progressAnimator.setDuration(3000);
		progressAnimator.setInterpolator(new LinearInterpolator());
		progressAnimator.start();
		
		ObjectAnimator progressAnimator1 = ObjectAnimator.ofInt(progressBar1, "progress", 100, (100 - b));
		progressAnimator1.setDuration(3000);
		progressAnimator1.setInterpolator(new LinearInterpolator());
		progressAnimator1.start();
	}
	
	
	@SuppressLint("StaticFieldLeak")
	public class GetAllAttandance extends AsyncTask<String, String, String> {
		Context ctx;
		
		GetAllAttandance(Context ctx) {
			this.ctx = ctx;
		}
		
		@Override
		protected void onPreExecute() {
			mSwipeRefreshLayout.setRefreshing(true);
			presentCount = 0;
			absentCount = 0;
			totalLeave = 0;
			totalPresentYear = 0;
			presentCountMonth = 0;
			absentCountMonth = 0;
			totalLeaveMonth = 0;
			totalPresentMonth = 0;
			mainLayoutAttendance.setVisibility(View.GONE);
			super.onPreExecute();
		}
		
		@Override
		protected String doInBackground(String... params) {
			
			String userId = params[0];
			String data;
			
			try {
				
				URL url = new URL(ALL_ATTENDANCE);
				HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
				httpURLConnection.setRequestMethod("POST");
				httpURLConnection.setDoOutput(true);
				httpURLConnection.setDoInput(true);
				OutputStream outputStream = httpURLConnection.getOutputStream();
				
				BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
				data = URLEncoder.encode("userId", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8");
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
			Log.d("Events", "" + result);
			//offline
			SharedPreferences sp_attend = getSharedPreferences(PREFS_ATTEND, 0);
			se_attend = sp_attend.edit();
			se_attend.clear();
			se_attend.putString("json_attend", result);
			se_attend.commit();
			mSwipeRefreshLayout.setRefreshing(false);
			mainLayoutAttendance.setVisibility(View.VISIBLE);
			
			if (result.equals("{\"result\":null}")) {
				tv_present_count.setText("N.A");
				tv_absent_count.setText("N.A");
				tv_prese_count.setText("N.A");
				tv_absnt_count.setText("N.A");
				percent1.setText("0%");
				percent2.setText("0%");
				Toast.makeText(ctx, "No Attendance", Toast.LENGTH_SHORT).show();
			} else {
				
				try {
					JSONObject jsonRootObject = new JSONObject(result);
					JSONArray jsonArray = jsonRootObject.getJSONArray("result");
					eventsArray.clear();
					for (int i = 0; i < jsonArray.length(); i++) {
						dbattand = new DatabeanAttendance();
						dbe = new DatabeanEvents();
						
						JSONObject jsonObject = jsonArray.getJSONObject(i);
						attandId = jsonObject.getString("tbl_attandance_id");
						attandStatus = jsonObject.getString("tbl_attandance_status");
						attandDate = jsonObject.getString("tbl_attandance_date");
						
						userName = jsonObject.getString("tbl_users_name");
						userImg = jsonObject.getString("tbl_users_img");
						
						String[] separated = attandDate.split("-");
						String month = separated[1];
						String year = separated[0];
						
						if (month.contentEquals(android.text.format.DateFormat.format("MM", cal_month)) && year.contentEquals(android.text.format.DateFormat.format("yyyy", cal_month))) {
							switch (attandStatus) {
								case "Present":
									presentCountMonth++;
									break;
								case "Absent":
									absentCountMonth++;
									break;
								case "Leave":
									totalLeaveMonth++;
									break;
							}
						}
						
						if (year.contentEquals(android.text.format.DateFormat.format("yyyy", cal_month))) {
							switch (attandStatus) {
								case "Present":
									presentCount++;
									break;
								case "Absent":
									absentCount++;
									break;
								case "Leave":
									totalLeave++;
									break;
							}
						}
						dbattand.setAttandId(attandId);
						dbattand.setAttandStatus(attandStatus);
						dbattand.setAttandDate(attandDate);
						dbe.setEventDate(attandDate);
						dbe.setEventTitle(attandStatus);
						eventsArray.add(dbe);
						attandanceArray.add(dbattand);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				setAttendance();
				super.onPostExecute(result);
			}
		}
		
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
			Log.d("RESULT", "" + result);
			
			if (result.equals("{\"result\":null}")) {
				Toast.makeText(ctx, "Server Issue", Toast.LENGTH_SHORT).show();
			} else {
				Intent i = new Intent(TeacherRequestLeaveActivity.this, LeaveListActivity.class);
				i.putExtra("str_tool_title", "Leave Requests");
				i.putExtra("selBatch", selBatch);
				startActivity(i);
				overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
				finish();
				Toast.makeText(ctx, "" + result, Toast.LENGTH_SHORT).show();
				super.onPostExecute(result);
			}
		}
		
	}
	
	private void setAttendance() {
		if (userImg.equals("0")) {
			iv_user_img.setImageDrawable(getResources().getDrawable(R.drawable.user_place_hoder));
		} else if (!TextUtils.isEmpty(userImg)) {
			String url;
			switch (userrType) {
				case "Parent":
					url = settings.getString("imageUrl", "") + schoolId + "/users/students/profile/" + userImg;
					Log.e("url", url);
					Picasso.with(this).load(url).error(R.drawable.dummy).placeholder(R.drawable.dummy).into(iv_user_img);
				case "Admin":
					url = settings.getString("imageUrl", "") + schoolId + "/users/admin/profile/" + userImg;
					Log.e("url", url);
					Picasso.with(this).load(url).error(R.drawable.dummy).placeholder(R.drawable.dummy).into(iv_user_img);
				case "Staff":
					url = settings.getString("imageUrl", "") + schoolId + "/users/staff/profile/" + userImg;
					Log.e("url", url);
					Picasso.with(this).load(url).error(R.drawable.dummy).placeholder(R.drawable.dummy).into(iv_user_img);
				case "Teacher":
					url = settings.getString("imageUrl", "") + schoolId + "/users/teachers/profile/" + userImg;
					Log.e("url", url);
					Picasso.with(this).load(url).error(R.drawable.dummy).placeholder(R.drawable.dummy).into(iv_user_img);
				case "Student":
					url = settings.getString("imageUrl", "") + schoolId + "/users/students/profile/" + userImg;
					Log.e("url", url);
					Picasso.with(this).load(url).error(R.drawable.dummy).placeholder(R.drawable.dummy).into(iv_user_img);
			}
		} else {
			iv_user_img.setImageDrawable(getResources().getDrawable(R.drawable.user_place_hoder));
		}
		if (leaveImg.equals("0")) {
			imageViewLeave.setVisibility(View.GONE);
		} else if (!leaveImg.equals("[]")) {
			Log.e("image", leaveImg);
			String url = settings.getString("imageUrl", "") + schoolId + "/usersLeave/" + userImg;
			Picasso.with(this).load(url).error(R.drawable.dummy).placeholder(R.drawable.dummy).into(imageViewLeave);
		} else {
			imageViewLeave.setVisibility(View.GONE);
		}
		
		tv_month.setText(userName);
		leaveSubject.setText(str_leaveSubject);
		setDate(str_leaveStartDate);
		setDate1(str_leaveEndDate);
		
		//	leaveStartDate.setText(str_leaveStartDate);
		//leaveEndDate.setText(str_leaveEndDate);
		leaveDetails.setText(str_leaveDetails);
		
		tv_month_year.setText(android.text.format.DateFormat.format("MMMM, yyyy", cal_month));
		tv_year.setText(android.text.format.DateFormat.format("yyyy", cal_month));
		
		tv_prese_count.setText("Present : " + presentCount);
		tv_absnt_count.setText("Absent  : " + absentCount);
		
		tv_present_count.setText("Present : " + presentCountMonth);
		tv_absent_count.setText("Absent  : " + absentCountMonth);
		//setPercent
		float a = (presentCount + absentCount);
		totalPresentYear = presentCount / a;
		int b = (int) (totalPresentYear * 100);
		percent2.setText("" + b + "%");
		
		float c = (presentCountMonth + absentCountMonth);
		totalPresentMonth = presentCountMonth / c;
		int d = (int) (totalPresentMonth * 100);
		percent1.setText("" + d + "%");
		
		startAnimation(d, b);
		progressBar.setProgress(d);
		progressBar1.setProgress(b);
		
	}
	
	private void setDate1(String str_leaveEndDate) {
		String[] separated = str_leaveEndDate.split("-");
		String day = separated[2];
		int m = Integer.parseInt(separated[1]);
		String year = separated[0];
		String monthName = "";
		if (m == 1) {
			monthName = "January";
		} else if (m == 2) {
			monthName = "February";
			
		} else if (m == 3) {
			monthName = "March";
			
		} else if (m == 4) {
			monthName = "April";
			
		} else if (m == 5) {
			monthName = "May";
			
		} else if (m == 6) {
			monthName = "June";
			
		} else if (m == 7) {
			monthName = "July";
			
		} else if (m == 8) {
			monthName = "August";
			
		} else if (m == 9) {
			monthName = "September";
			
		} else if (m == 10) {
			monthName = "October";
			
		} else if (m == 11) {
			monthName = "November";
			
		} else if (m == 12) {
			monthName = "December";
			
		}
		
		leaveEndDate.setText(day + "-" + monthName + "-" + year);
		
	}
	
	private void setDate(String str_leaveStartDate) {
		String[] separated = str_leaveStartDate.split("-");
		String day = separated[2];
		int m = Integer.parseInt(separated[1]);
		String year = separated[0];
		String monthName = "";
		if (m == 1) {
			monthName = "January";
		} else if (m == 2) {
			monthName = "February";
			
		} else if (m == 3) {
			monthName = "March";
			
		} else if (m == 4) {
			monthName = "April";
			
		} else if (m == 5) {
			monthName = "May";
			
		} else if (m == 6) {
			monthName = "June";
			
		} else if (m == 7) {
			monthName = "July";
			
		} else if (m == 8) {
			monthName = "August";
			
		} else if (m == 9) {
			monthName = "September";
			
		} else if (m == 10) {
			monthName = "October";
			
		} else if (m == 11) {
			monthName = "November";
			
		} else if (m == 12) {
			monthName = "December";
			
		}
		
		leaveStartDate.setText(day + "-" + monthName + "-" + year);
		
	}
	
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		if (item.getItemId() == android.R.id.home) {
			finish();
			overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
			
		}
		
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onBackPressed() {
		finish();
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
	}
	
	public final boolean isInternetOn() {
		
		// get Connectivity Manager object to check connection
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
	
}
