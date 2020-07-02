package com.aaptrix.activitys.admin;

import com.aaptrix.activitys.teacher.StaffAttendance;
import com.aaptrix.adaptor.TeacherAttendanceAdapter;
import com.aaptrix.databeans.StaffData;
import com.aaptrix.R;
import pl.droidsonroids.gif.GifImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import static com.aaptrix.tools.HttpUrl.STAFF_LIST;
import static com.aaptrix.tools.HttpUrl.STAFF_SUBMIT_ATTENDANCE;
import static com.aaptrix.tools.HttpUrl.STAFF_VIEW_ATTENDANCE;
import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREF_COLOR;

public class TeacherAttendance extends AppCompatActivity {
	
	public static final String TEACHER_ATTEN_PREFS = "atten_prefs";
	public static final String ATTEN_JSON = "json_attendance";
	public static final String TEACHER_ARRAY = "teacherArray";
	String userId, schoolId, userSection;
	String selToolColor, selDrawerColor, selStatusColor, selTextColor1, selTextColor2;
	AppBarLayout appBarLayout;
	TextView tool_title;
	ListView staffList;
	LinearLayout submitAttendanceLayout;
	TextView submit_attendance;
	String formattedDate1;
	TextView todayDate, name;
	ImageView previous, next;
	FrameLayout previousF, nextF;
	Calendar c;
	String formattedDate, dayOfTheWeek;
	SimpleDateFormat sdf;
	SimpleDateFormat df, df2;
	SimpleDateFormat df1;
	String atDate;
	RelativeLayout layout;
	String attenType, value, date;
	ArrayList<StaffData> staffArray = new ArrayList<>();
	MediaPlayer mp;
	CardView cardView;
	GifImageView taskStatus;
	
	@SuppressLint("SetTextI18n")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_teacher_attendance);
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		setTitle("");
		setResult(RESULT_OK);
		appBarLayout = findViewById(R.id.appBarLayout);
		tool_title = findViewById(R.id.tool_title);
		SwipeRefreshLayout mSwipeRefreshLayout = findViewById(R.id.activity_main_swipe_refresh_layout);
		mSwipeRefreshLayout.setEnabled(false);
		todayDate = findViewById(R.id.todayDate);
		submitAttendanceLayout = findViewById(R.id.submitAttendanceLayout);
		submitAttendanceLayout.setVisibility(View.GONE);
		staffList = findViewById(R.id.staffList);
		submit_attendance = findViewById(R.id.submit_attendance);
		previous = findViewById(R.id.ib_prev);
		layout = findViewById(R.id.layout);
		previousF = findViewById(R.id.ib_prevF);
		nextF = findViewById(R.id.Ib_nextF);
		next = findViewById(R.id.Ib_next);
		name = findViewById(R.id.clgname);
		mp = MediaPlayer.create(this, R.raw.button_click);
		cardView = findViewById(R.id.card_view);
		taskStatus = findViewById(R.id.task_status);
		SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
		selToolColor = settingsColor.getString("tool", "");
		selDrawerColor = settingsColor.getString("drawer", "");
		selStatusColor = settingsColor.getString("status", "");
		selTextColor1 = settingsColor.getString("text1", "");
		selTextColor2 = settingsColor.getString("text2", "");
		
		appBarLayout.setBackgroundColor(Color.parseColor(selToolColor));
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			Window window = getWindow();
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.setStatusBarColor(Color.parseColor(selStatusColor));
		}
		tool_title.setTextColor(Color.parseColor(selTextColor1));
		submitAttendanceLayout.setBackgroundColor(Color.parseColor(selToolColor));
		submit_attendance.setBackgroundColor(Color.parseColor(selToolColor));
		previous.setColorFilter(Color.parseColor(selDrawerColor));
		next.setColorFilter(Color.parseColor(selDrawerColor));
		
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		userId = settings.getString("userID", "");
		userSection = settings.getString("userSection", "");
		schoolId = settings.getString("str_school_id", "");
		
		attenType = getIntent().getStringExtra("type");
		value = getIntent().getStringExtra("value");
		date = getIntent().getStringExtra("date");
		c = Calendar.getInstance();
		System.out.println("Current time => " + date);
		df = new SimpleDateFormat("dd-MM-yyyy");
		df1 = new SimpleDateFormat("yyyy-MM-dd");
		try {
			c.setTime(df.parse(date));
			formattedDate = df.format(df.parse(date));
			formattedDate1 = df1.format(df.parse(date));
			sdf = new SimpleDateFormat("EEEE");
			dayOfTheWeek = sdf.format(df.parse(formattedDate));
			atDate = df1.format(df.parse(formattedDate));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		checkDate(formattedDate1);
		
		switch (attenType) {
			case "2":
				tool_title.setText("Teacher Attendance");
				name.setText("Teacher Details");
				break;
			case "4":
				tool_title.setText("Staff Attendance");
				name.setText("Staff Details");
				break;
			default:
				tool_title.setText("Staff Attendance");
				name.setText("Staff Details");
				break;
		}
		todayDate.setText(dayOfTheWeek + ", " + formattedDate);
		
		if (value.equals("take")) {
			SharedPreferences sp = getSharedPreferences(TEACHER_ATTEN_PREFS, 0);
			SharedPreferences.Editor editor = sp.edit();
			editor.remove(ATTEN_JSON);
			editor.remove(TEACHER_ARRAY);
			editor.commit();
			
			submitAttendanceLayout.setVisibility(View.VISIBLE);
			if (isInternetOn()) {
				GetAllStaff b1 = new GetAllStaff(this);
				b1.execute(STAFF_LIST, attenType, schoolId, formattedDate1);
			} else {
				Toast.makeText(this, "No internet", Toast.LENGTH_SHORT).show();
			}
			
			submitAttendanceLayout.setOnClickListener(view -> {
				mp.start();
				try {
					SharedPreferences sp_attend = getSharedPreferences(TEACHER_ATTEN_PREFS, 0);
					String attendances = sp_attend.getString(ATTEN_JSON, "");
					
					String value = todayDate.getText().toString().trim();
					String[] v1 = value.split(",");
					String[] w1 = v1[1].split("-");
					String[] w2 = formattedDate.split("-");
					
					int aa = Integer.parseInt(w1[0].replace(" ", ""));
					int bb = Integer.parseInt(w2[0].replace(" ", ""));
					if (aa > bb) {
						Toast.makeText(this, "Not allowed", Toast.LENGTH_SHORT).show();
					} else {
						SubmitStaffAttendance b1 = new SubmitStaffAttendance(this);
						b1.execute(STAFF_SUBMIT_ATTENDANCE, attendances, atDate, attenType, userId, schoolId);
					}
				} catch (Exception e) {
					e.printStackTrace();
					Toast.makeText(this, "Something missing", Toast.LENGTH_SHORT).show();
				}
			});
			
			previousF.setOnClickListener(v -> {
				c.add(Calendar.DAY_OF_YEAR, -1);
				Date tomorrow = c.getTime();
				dayOfTheWeek = sdf.format(tomorrow);
				df = new SimpleDateFormat("dd-MMM-yyyy");
				String formattedDate = df.format(c.getTime());
				todayDate.setText(dayOfTheWeek + ", " + formattedDate);
				df = new SimpleDateFormat("yyyy-MM-dd");
				atDate = df.format(c.getTime());
				checkDate(atDate);
				GetAllStaff b1 = new GetAllStaff(this);
				b1.execute(STAFF_LIST, attenType, schoolId, atDate);
			});
			
			nextF.setOnClickListener(v -> {
				checkDate(formattedDate1);
				c.add(Calendar.DAY_OF_YEAR, 1);
				Date tomorrow = c.getTime();
				dayOfTheWeek = sdf.format(tomorrow);
				df = new SimpleDateFormat("dd-MMM-yyyy");
				String formattedDate = df.format(c.getTime());
				df = new SimpleDateFormat("yyyy-MM-dd");
				atDate = df.format(c.getTime());
				checkDate(atDate);
				todayDate.setText(dayOfTheWeek + ", " + formattedDate);
				GetAllStaff b1 = new GetAllStaff(this);
				b1.execute(STAFF_LIST, attenType, schoolId, atDate);
			});
		} else if (value.equals("view")) {
			submitAttendanceLayout.setVisibility(View.GONE);
			if (isInternetOn()) {
				GetAllStaff b1 = new GetAllStaff(this);
				b1.execute(STAFF_VIEW_ATTENDANCE, attenType, schoolId, formattedDate1);
			} else {
				Toast.makeText(this, "No internet", Toast.LENGTH_SHORT).show();
			}
			
			previousF.setOnClickListener(v -> {
				mp.start();
				c.add(Calendar.DAY_OF_YEAR, -1);
				Date tomorrow = c.getTime();
				dayOfTheWeek = sdf.format(tomorrow);
				df = new SimpleDateFormat("dd-MMM-yyyy");
				String formattedDate = df.format(c.getTime());
				todayDate.setText(dayOfTheWeek + ", " + formattedDate);
				df = new SimpleDateFormat("yyyy-MM-dd");
				atDate = df.format(c.getTime());
				checkDate(atDate);
				GetAllStaff b1 = new GetAllStaff(this);
				b1.execute(STAFF_VIEW_ATTENDANCE, attenType, schoolId, atDate);
			});
			
			nextF.setOnClickListener(v -> {
				mp.start();
				checkDate(formattedDate1);
				c.add(Calendar.DAY_OF_YEAR, 1);
				Date tomorrow = c.getTime();
				Log.e("date", tomorrow.toString());
				dayOfTheWeek = sdf.format(tomorrow);
				df = new SimpleDateFormat("dd-MMM-yyyy");
				String formattedDate = df.format(c.getTime());
				c.add(Calendar.DAY_OF_YEAR, 0);
				df2 = new SimpleDateFormat("dd-MM-yyyy");
				todayDate.setText(dayOfTheWeek + ", " + formattedDate);
				df = new SimpleDateFormat("yyyy-MM-dd");
				atDate = df.format(c.getTime());
				checkDate(atDate);
				GetAllStaff b1 = new GetAllStaff(this);
				b1.execute(STAFF_VIEW_ATTENDANCE, attenType, schoolId, atDate);
			});
		}
	}
	
	private void checkDate(@NonNull String date) {
		Calendar calendar = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		if (date.equals(sdf.format(calendar.getTimeInMillis()))) {
			nextF.setEnabled(false);
			nextF.setVisibility(View.GONE);
		} else {
			nextF.setEnabled(true);
			nextF.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mp.release();
	}
	
	@SuppressLint("StaticFieldLeak")
	public class GetAllStaff extends AsyncTask<String, String, String> {
		Context ctx;
		String type;
		
		GetAllStaff(Context ctx) {
			this.ctx = ctx;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		
		@Override
		protected String doInBackground(String... params) {
			
			String branch_url = params[0];
			type = params[1];
			String school_id = params[2];
			String current_date = params[3];
			String data;
			
			try {
				
				URL url = new URL(branch_url);
				HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
				httpURLConnection.setRequestMethod("POST");
				httpURLConnection.setDoOutput(true);
				httpURLConnection.setDoInput(true);
				
				OutputStream outputStream = httpURLConnection.getOutputStream();
				
				BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
				data = URLEncoder.encode("user_type", "UTF-8") + "=" + URLEncoder.encode(type, "UTF-8") + "&" +
						URLEncoder.encode("school_id", "UTF-8") + "=" + URLEncoder.encode(school_id, "UTF-8") + "&" +
						URLEncoder.encode("current_date", "UTF-8") + "=" + URLEncoder.encode(current_date, "UTF-8");
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
			Log.e("atte Json", "" + result);
			if (result.equals("{\"result\":null}")) {
				staffList.setAdapter(null);
				staffArray.clear();
				if (value.equals("take")) {
					Toast.makeText(ctx, "No Staff", Toast.LENGTH_SHORT).show();
				} else if (value.equals("view")) {
					Toast.makeText(ctx, "No attendance", Toast.LENGTH_SHORT).show();
				}
			} else {
				try {
					JSONObject jsonRootObject = new JSONObject(result);
					JSONArray jsonArray = jsonRootObject.getJSONArray("result");
					staffArray.clear();
					for (int i = 0; i < jsonArray.length(); i++) {
						StaffData staffData = new StaffData();
						JSONObject jsonObject = jsonArray.getJSONObject(i);
						if (value.equals("take")) {
							staffData.setId(jsonObject.getString("tbl_users_id"));
							staffData.setName(jsonObject.getString("tbl_users_name"));
							staffData.setImage(jsonObject.getString("tbl_users_img"));
							staffData.setLeaveStatus(jsonObject.getString("dataleave"));
							staffData.setAttenStatus(jsonObject.getString("attendanceStatus"));
							staffData.setType(type);
						} else if (value.equals("view")) {
							staffData.setId(jsonObject.getString("tbl_users_id"));
							staffData.setName(jsonObject.getString("tbl_users_name"));
							staffData.setImage(jsonObject.getString("tbl_users_img"));
							staffData.setAttenStatus(jsonObject.getString("tbl_attandance_status"));
							staffData.setType(type);
						}
						staffArray.add(staffData);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				listItms(value);
				super.onPostExecute(result);
			}
		}
	}
	
	private void listItms(String value) {
		TeacherAttendanceAdapter adapter = new TeacherAttendanceAdapter(this, R.layout.list_staff_attendance, staffArray, value);
		staffList.setAdapter(adapter);
	}
	
	@SuppressLint("StaticFieldLeak")
	public class SubmitStaffAttendance extends AsyncTask<String, String, String> {
		Context ctx;
		
		SubmitStaffAttendance(Context ctx) {
			this.ctx = ctx;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			layout.setVisibility(View.VISIBLE);
			layout.bringToFront();
		}
		
		@Override
		protected String doInBackground(String... params) {
			String urls = params[0];
			String attendances = params[1];
			String formattedDate1 = params[2];
			String type = params[3];
			String userId = params[4];
			String schoolId = params[5];
			String data;
			
			try {
				
				URL url = new URL(urls);
				HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
				httpURLConnection.setRequestMethod("POST");
				httpURLConnection.setDoOutput(true);
				httpURLConnection.setDoInput(true);
				
				OutputStream outputStream = httpURLConnection.getOutputStream();
				
				BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
				data = URLEncoder.encode("attendance_array", "UTF-8") + "=" + URLEncoder.encode(attendances, "UTF-8") + "&" +
						URLEncoder.encode("user_id", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8") + "&" +
						URLEncoder.encode("school_id", "UTF-8") + "=" + URLEncoder.encode(schoolId, "UTF-8") + "&" +
						URLEncoder.encode("user_type", "UTF-8") + "=" + URLEncoder.encode(type, "UTF-8") + "&" +
						URLEncoder.encode("current_date", "UTF-8") + "=" + URLEncoder.encode(formattedDate1, "UTF-8");
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
			Log.e("STUDENT_ATTENDANCE", "" + result);
			if (result.equals("error")) {
				layout.setVisibility(View.GONE);
				Toast.makeText(ctx, "No value", Toast.LENGTH_SHORT).show();
			} else if (result.equals("Already Done")) {
				layout.setVisibility(View.GONE);
				Toast.makeText(ctx, "Already Done", Toast.LENGTH_SHORT).show();
			} else if (result.contains("success")) {
				cardView.setVisibility(View.VISIBLE);
				new CountDownTimer(4000, 1000) {
					@Override
					public void onTick(long millisUntilFinished) {

					}

					@Override
					public void onFinish() {
						Intent i = new Intent(ctx, StaffAttendance.class);
						startActivity(i);
						finish();
						overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
					}
				}.start();
			} else if (result.contains("Insufficient")) {
				layout.setVisibility(View.GONE);
				Toast.makeText(ctx, "Insufficient SMS", Toast.LENGTH_SHORT).show();
			} else if (result.equals("date not match")) {
				layout.setVisibility(View.GONE);
				Toast.makeText(ctx, "You take only current date attendance", Toast.LENGTH_SHORT).show();
			}
			super.onPostExecute(result);
		}
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
		ConnectivityManager connec = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		assert connec != null;
		if (connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
				connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
				connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
				connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED) {
			return true;
		} else if (
				connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
						connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED) {
			return false;
		}
		return false;
	}
}
