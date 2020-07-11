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

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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
import android.widget.RadioButton;
import android.widget.RelativeLayout;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import com.aaptrix.databeans.DataBeanStudent;
import com.aaptrix.adaptor.StudentAttendanceListAdaptor;
import com.aaptrix.R;
import pl.droidsonroids.gif.GifImageView;

import static com.aaptrix.tools.HttpUrl.ALL_STUDENTS;
import static com.aaptrix.tools.HttpUrl.ALL_STUDENTS_VIEW;
import static com.aaptrix.tools.HttpUrl.SUBMIT_ATTENDANCE;
import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREFS_RW;
import static com.aaptrix.tools.SPClass.PREF_COLOR;

public class StudentListActivity extends AppCompatActivity {
	
	SharedPreferences.Editor editor;
	String userId, roleId, schoolId, userSection, userRollNumber, userClassTeacher;
	//offline
	SharedPreferences.Editor editorColor;
	String selToolColor, selDrawerColor, selStatusColor, selTextColor1, selTextColor2, numberOfUser;
	AppBarLayout appBarLayout;
	TextView tool_title, tv_logout_text;
	ImageView logoutImg;
	String userLoginId, userName, userImg, userrType, userPassword, userSchoolLogo;
	String userSchoolId;
	private SwipeRefreshLayout mSwipeRefreshLayout;
	//
	ListView studentList;
	ArrayList<DataBeanStudent> studentArray = new ArrayList<>();
	DataBeanStudent dbs;
	String studentId, studentName, studentImage, studentAttendStatus;
	StudentAttendanceListAdaptor studentAttendanceListAdaptor;
	String stdSection, value, date, stdSubject;
	LinearLayout submitAttendanceLayout, markAll;
	TextView submit_attendance;
	String formattedDate1;
	
	private SharedPreferences sp_attend;
	SharedPreferences.Editor se_attend;
	public static final String PREFS_ATTENDANCE = "attendance";
	TextView todayDate;
	ImageView previous, next;
	FrameLayout previousF, nextF;
	Calendar c;
	String formattedDate, dayOfTheWeek;
	SimpleDateFormat sdf;
	SimpleDateFormat df, df2;
	SimpleDateFormat df1;
	String atDate;
	CardView cardView;
	GifImageView taskStatus;
	LinearLayout add_layout;
	ImageView addAtten;
	TextView attenMark;
	RelativeLayout layout;
	String selectAll = "null";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.student_list_layout);
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		setTitle("");
		setResult(RESULT_OK);
		appBarLayout = findViewById(R.id.appBarLayout);
		tool_title = findViewById(R.id.tool_title);
		attenMark = findViewById(R.id.atten_marked_by);
		layout = findViewById(R.id.layout);
		markAll = findViewById(R.id.mark_all);
		
		//color
		colorSetSP();
		
		mSwipeRefreshLayout = findViewById(R.id.activity_main_swipe_refresh_layout);
		mSwipeRefreshLayout.setEnabled(false);
		tv_logout_text = findViewById(R.id.tv_logout_text);
		todayDate = findViewById(R.id.todayDate);
		logoutImg = findViewById(R.id.logoutImg);
		submitAttendanceLayout = findViewById(R.id.submitAttendanceLayout);
		submitAttendanceLayout.setVisibility(View.GONE);
		cardView = findViewById(R.id.card_view);
		taskStatus = findViewById(R.id.task_status);
		add_layout = findViewById(R.id.add_layout);
		addAtten = findViewById(R.id.add_atten);
		setValueSP();
		init();
		setDate();
		validation();

		layout.setOnClickListener(v -> {

		});

		layout.setOnTouchListener((v, event) -> false);
		
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
		attenMark.setBackgroundColor(Color.parseColor(selToolColor));
		attenMark.setTextColor(Color.parseColor(selTextColor1));
		GradientDrawable bgShape = (GradientDrawable) add_layout.getBackground();
		bgShape.setColor(Color.parseColor(selToolColor));
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
	
	private void validation() {
		if (value.equals("take")) {
			SharedPreferences settings = getSharedPreferences(PREFS_ATTENDANCE, 0);
			SharedPreferences.Editor editor = settings.edit();
			editor.remove("json_attend");
			editor.remove("studentArray");
			editor.commit();
			
			checkDate(formattedDate1);
			submitAttendanceLayout.setVisibility(View.VISIBLE);
			if (isInternetOn()) {
				studentArray.clear();
				GetAllStudentList b1 = new GetAllStudentList(StudentListActivity.this);
				b1.execute(ALL_STUDENTS, stdSection, schoolId, formattedDate1, stdSubject);
			} else {
				Toast.makeText(StudentListActivity.this, "No internet", Toast.LENGTH_SHORT).show();
			}
			
			submitAttendanceLayout.setOnClickListener(view -> {
				try {
					sp_attend = getSharedPreferences(PREFS_ATTENDANCE, 0);
					se_attend = sp_attend.edit();
					String attendances = sp_attend.getString("json_attend", "");
					String studentArraySize = sp_attend.getString("studentArray", "");
					int a = Integer.parseInt(studentArraySize);
					if ((studentArray.size() == a)) {
						String value = todayDate.getText().toString().trim();
						String[] v1 = value.split(",");
						String[] w1 = v1[1].split("-");
						String[] w2 = formattedDate.split("-");
						
						int aa = Integer.parseInt(w1[0].replace(" ", ""));
						int bb = Integer.parseInt(w2[0].replace(" ", ""));
						if (aa > bb) {
							Toast.makeText(StudentListActivity.this, "Not allowed", Toast.LENGTH_SHORT).show();
						} else {
							layout.setVisibility(View.VISIBLE);
							layout.bringToFront();
							SubmitAttendance b1 = new SubmitAttendance(StudentListActivity.this);
							b1.execute(SUBMIT_ATTENDANCE, attendances, atDate, stdSection, userId, schoolId, stdSubject);
						}
					} else if ((studentArray.size() < a)) {
						Toast.makeText(StudentListActivity.this, "Please re-open the attendance module and than submit", Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(StudentListActivity.this, "Please submit all student attendance", Toast.LENGTH_SHORT).show();
					}
				} catch (Exception e) {
					Toast.makeText(StudentListActivity.this, "Something missing", Toast.LENGTH_SHORT).show();
				}
			});
			
			previousF.setOnClickListener(v -> {
				studentArray.clear();
				c.add(Calendar.DAY_OF_YEAR, -1);
				Date tomorrow = c.getTime();
				dayOfTheWeek = sdf.format(tomorrow);
				df = new SimpleDateFormat("dd-MMM-yyyy");
				String formattedDate = df.format(c.getTime());
				todayDate.setText(dayOfTheWeek + ", " + formattedDate);
				SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
				atDate = df1.format(c.getTime());
				checkDate(atDate);
				GetAllStudentList b1 = new GetAllStudentList(StudentListActivity.this);
				b1.execute(ALL_STUDENTS, stdSection, schoolId, atDate, stdSubject);
			});
			
			nextF.setOnClickListener(v -> {
				studentArray.clear();
				c.add(Calendar.DAY_OF_YEAR, 1);
				Date tomorrow = c.getTime();
				dayOfTheWeek = sdf.format(tomorrow);
				df = new SimpleDateFormat("dd-MMM-yyyy");
				String formattedDate = df.format(c.getTime());
				SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
				atDate = df1.format(c.getTime());
				todayDate.setText(dayOfTheWeek + ", " + formattedDate);
				checkDate(atDate);
				GetAllStudentList b1 = new GetAllStudentList(StudentListActivity.this);
				b1.execute(ALL_STUDENTS, stdSection, schoolId, atDate, stdSubject);
			});
		} else if (value.equals("view")) {
			checkDate(formattedDate1);
			submitAttendanceLayout.setVisibility(View.GONE);
			if (isInternetOn()) {
				GetAllStudentList b1 = new GetAllStudentList(StudentListActivity.this);
				b1.execute(ALL_STUDENTS_VIEW, stdSection, schoolId, formattedDate1, stdSubject);
			} else {
				Toast.makeText(StudentListActivity.this, "No internet", Toast.LENGTH_SHORT).show();
			}
			
			previousF.setOnClickListener(v -> {
				studentArray.clear();
				c.add(Calendar.DAY_OF_YEAR, -1);
				Date tomorrow = c.getTime();
				dayOfTheWeek = sdf.format(tomorrow);
				df = new SimpleDateFormat("dd-MMM-yyyy");
				String formattedDate = df.format(c.getTime());
				todayDate.setText(dayOfTheWeek + ", " + formattedDate);
				SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
				atDate = df1.format(c.getTime());
				checkDate(atDate);
				GetAllStudentList b1 = new GetAllStudentList(StudentListActivity.this);
				b1.execute(ALL_STUDENTS_VIEW, stdSection, schoolId, atDate, stdSubject);
			});
			
			nextF.setOnClickListener(v -> {
				studentArray.clear();
				c.add(Calendar.DAY_OF_YEAR, 1);
				Date tomorrow = c.getTime();
				dayOfTheWeek = sdf.format(tomorrow);
				df = new SimpleDateFormat("dd-MMM-yyyy");
				String formattedDate = df.format(c.getTime());
				c.add(Calendar.DAY_OF_YEAR, 0);
				df2 = new SimpleDateFormat("dd-MM-yyyy");
				todayDate.setText(dayOfTheWeek + ", " + formattedDate);
				SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
				atDate = df1.format(c.getTime());
				checkDate(atDate);
				GetAllStudentList b1 = new GetAllStudentList(StudentListActivity.this);
				b1.execute(ALL_STUDENTS_VIEW, stdSection, schoolId, atDate, stdSubject);
			});
		}
	}
	
	private void setDate() {
		stdSection = getIntent().getStringExtra("batch");
		stdSubject = getIntent().getStringExtra("subject");
		value = getIntent().getStringExtra("value");
		date = getIntent().getStringExtra("date");
		tool_title.setText(stdSubject + ", " + stdSection);
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
		todayDate.setText(dayOfTheWeek + ", " + formattedDate);

		addAtten.setOnClickListener(v -> {
			Intent i1 = new Intent(this, StudentListActivity.class);
			i1.putExtra("batch", stdSection);
			i1.putExtra("subject", stdSubject);
			i1.putExtra("value", "take");
			i1.putExtra("date", date);
			i1.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(i1);
			overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		});
	}
	
	private void init() {
		//
		studentList = findViewById(R.id.studentList);
		submit_attendance = findViewById(R.id.submit_attendance);
		
		previous = findViewById(R.id.ib_prev);
		previousF = findViewById(R.id.ib_prevF);
		nextF = findViewById(R.id.Ib_nextF);
		next = findViewById(R.id.Ib_next);
		
	}
	
	private void setValueSP() {
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		editor = settings.edit();
		userId = settings.getString("userID", "");
		userLoginId = settings.getString("userLoginId", "");
		userName = settings.getString("userName", "");
		userImg = settings.getString("userImg", "");
		userSection = settings.getString("userSection", "");
		userRollNumber = settings.getString("userRollNumber", "");
		userClassTeacher = settings.getString("userTeacherName", "");
		userrType = settings.getString("userrType", "");
		userPassword = settings.getString("userPassword", "");
		roleId = settings.getString("str_role_id", "");
		schoolId = settings.getString("str_school_id", "");
		numberOfUser = settings.getString("numberOfUser", "");
		userSchoolLogo = settings.getString("userSchoolLogo", "");
	}
	
	private void colorSetSP() {
		SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
		editorColor = settingsColor.edit();
		selToolColor = settingsColor.getString("tool", "");
		selDrawerColor = settingsColor.getString("drawer", "");
		selStatusColor = settingsColor.getString("status", "");
		selTextColor1 = settingsColor.getString("text1", "");
		selTextColor2 = settingsColor.getString("text2", "");
	}
	
	@SuppressLint("StaticFieldLeak")
	public class SubmitAttendance extends AsyncTask<String, String, String> {
		Context ctx;
		
		SubmitAttendance(Context ctx) {
			this.ctx = ctx;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		
		@Override
		protected String doInBackground(String... params) {
			String urls = params[0];
			String attendances = params[1];
			String formattedDate1 = params[2];
			String stdSection = params[3];
			String userId = params[4];
			String schoolId = params[5];
			String subject = params[6];
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
						URLEncoder.encode("section_name", "UTF-8") + "=" + URLEncoder.encode(stdSection, "UTF-8") + "&" +
						URLEncoder.encode("current_date", "UTF-8") + "=" + URLEncoder.encode(formattedDate1, "UTF-8") + "&" +
						URLEncoder.encode("subjectNm", "UTF-8") + "=" + URLEncoder.encode(subject, "UTF-8");
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
			mSwipeRefreshLayout.setRefreshing(false);
			if (result.equals("error")) {
				layout.setVisibility(View.GONE);
				Toast.makeText(ctx, "No value", Toast.LENGTH_SHORT).show();
			} else if (result.equals("Already Done")) {
				layout.setVisibility(View.GONE);
				Toast.makeText(ctx, "Already Done", Toast.LENGTH_SHORT).show();
			} else if (result.contains("Sent successfully")) {
				cardView.setVisibility(View.VISIBLE);
				new CountDownTimer(4000, 1000) {
					@Override
					public void onTick(long millisUntilFinished) {

					}

					@Override
					public void onFinish() {
						Intent i = new Intent(StudentListActivity.this, TeacherAttendanceActivity.class);
						i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
						startActivity(i);
						finish();
						overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
					}
				}.start();
			} else if (result.contains("Insufficient")) {
				layout.setVisibility(View.GONE);
				Toast.makeText(ctx, "Attendance Saved Successfully. Insufficient SMS", Toast.LENGTH_SHORT).show();
			} else if (result.equals("date not match")) {
				layout.setVisibility(View.GONE);
				Toast.makeText(ctx, "You take only current date attendance", Toast.LENGTH_SHORT).show();
			}
			super.onPostExecute(result);
		}
	}
	
	@SuppressLint("StaticFieldLeak")
	public class GetAllStudentList extends AsyncTask<String, String, String> {
		Context ctx;
		
		GetAllStudentList(Context ctx) {
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
			
			String branch_url = params[0];
			String sectionName = params[1];
			String school_id = params[2];
			String current_date = params[3];
			String subject = params[4];
			String data;
			
			try {
				
				URL url = new URL(branch_url);
				HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
				httpURLConnection.setRequestMethod("POST");
				httpURLConnection.setDoOutput(true);
				httpURLConnection.setDoInput(true);
				
				OutputStream outputStream = httpURLConnection.getOutputStream();
				
				BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
				data = URLEncoder.encode("section_name", "UTF-8") + "=" + URLEncoder.encode(sectionName, "UTF-8") + "&" +
						URLEncoder.encode("school_id", "UTF-8") + "=" + URLEncoder.encode(school_id, "UTF-8") + "&" +
						URLEncoder.encode("current_date", "UTF-8") + "=" + URLEncoder.encode(current_date, "UTF-8") + "&" +
						URLEncoder.encode("subjectNm", "UTF-8") + "=" + URLEncoder.encode(subject, "UTF-8");
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
				Log.e("response", response.toString());
				return response.toString();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			mSwipeRefreshLayout.setRefreshing(false);
			if (result.contains("{\"result\":null,")) {
				studentArray.clear();
				if (value.equals("take")) {
					Toast.makeText(ctx, "No students", Toast.LENGTH_SHORT).show();
					studentList.setVisibility(View.GONE);
					studentList.setAdapter(null);
				} else if (value.equals("view")) {
					Toast.makeText(ctx, "No attendance", Toast.LENGTH_SHORT).show();
					SharedPreferences sp = getSharedPreferences(PREFS_RW, 0);
					String json = sp.getString("result", "");

					try {
						JSONObject jsonObject = new JSONObject(json);
						JSONArray jsonArray = jsonObject.getJSONArray("result");
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject object = jsonArray.getJSONObject(i);
							if (object.getString("tbl_insti_buzz_cate_name").equals("Attendance")) {
								if (object.getString("tbl_scl_inst_buzz_detl_write_status").equals("Active")) {
									add_layout.setVisibility(View.VISIBLE);
								} else {
									add_layout.setVisibility(View.GONE);
								}
								break;
							}
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
					studentList.setVisibility(View.GONE);
					studentList.setAdapter(null);
				}
			} else {
				studentList.setVisibility(View.VISIBLE);
				try {
					JSONObject jsonRootObject = new JSONObject(result);
					if (value.equals("view")) {
						if (result.contains("{\"attendance_added_by\":null}")) {
							String name = jsonRootObject.getString("attendance_added_by");
							if (name != null && !name.equals("null")) {
								attenMark.setText("Attendance marked by : " + name);
								attenMark.setVisibility(View.VISIBLE);
							} else {
								attenMark.setVisibility(View.GONE);
							}
						} else {
							attenMark.setVisibility(View.GONE);
						}
					}
					JSONArray jsonArray = jsonRootObject.getJSONArray("result");
					studentArray.clear();
					for (int i = 0; i < jsonArray.length(); i++) {
						dbs = new DataBeanStudent();
						JSONObject jsonObject = jsonArray.getJSONObject(i);
						if (value.equals("take")) {
							studentId = jsonObject.getString("tbl_users_id");
							studentName = jsonObject.getString("tbl_users_name");
							studentImage = jsonObject.getString("tbl_users_img");
							studentAttendStatus = jsonObject.getString("dataleave");
							dbs.setUserClass(jsonObject.getString("attendanceStatus"));
						} else if (value.equals("view")) {
							studentId = jsonObject.getString("tbl_users_id");
							studentName = jsonObject.getString("tbl_users_name");
							studentImage = jsonObject.getString("tbl_users_img");
							studentAttendStatus = jsonObject.getString("tbl_attandance_status");
						}
						dbs.setUserID(studentId);
						dbs.setUserName(studentName);
						dbs.setUserImg(studentImage);
						dbs.setUserLoginId(studentAttendStatus);
						studentArray.add(dbs);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (studentArray.size() != 0) {
					listItms(value);
				} else {
					if (value.equals("take")) {
						Toast.makeText(ctx, "No students", Toast.LENGTH_SHORT).show();
						studentList.setVisibility(View.GONE);
						studentList.setAdapter(null);
					} else if (value.equals("view")) {
						Toast.makeText(ctx, "No attendance", Toast.LENGTH_SHORT).show();
						SharedPreferences sp = getSharedPreferences(PREFS_RW, 0);
						String json = sp.getString("result", "");

						try {
							JSONObject jsonObject = new JSONObject(json);
							JSONArray jsonArray = jsonObject.getJSONArray("result");
							for (int i = 0; i < jsonArray.length(); i++) {
								JSONObject object = jsonArray.getJSONObject(i);
								if (object.getString("tbl_insti_buzz_cate_name").equals("Attendance")) {
									if (object.getString("tbl_scl_inst_buzz_detl_write_status").equals("Active")) {
										add_layout.setVisibility(View.VISIBLE);
									} else {
										add_layout.setVisibility(View.GONE);
									}
									break;
								}
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
						studentList.setVisibility(View.GONE);
						studentList.setAdapter(null);
					}
				}
				super.onPostExecute(result);
			}
		}
	}
	
	private void listItms(String value) {
		studentAttendanceListAdaptor = new StudentAttendanceListAdaptor(StudentListActivity.this, R.layout.user_attendance_list_item1, studentArray, value, stdSubject, selectAll);
		studentList.setAdapter(studentAttendanceListAdaptor);
		studentAttendanceListAdaptor.notifyDataSetChanged();

		if (value.equals("take")) {
			markAll.setVisibility(View.VISIBLE);
		}

		RadioButton rb_present = findViewById(R.id.rb_present);
		RadioButton rb_absent = findViewById(R.id.rb_absent);

		rb_present.setOnClickListener(v -> {
			selectAll = "present";
			studentAttendanceListAdaptor = new StudentAttendanceListAdaptor(StudentListActivity.this, R.layout.user_attendance_list_item1, studentArray, value, stdSubject, selectAll);
			studentList.setAdapter(studentAttendanceListAdaptor);
			studentAttendanceListAdaptor.notifyDataSetChanged();
		});

		rb_absent.setOnClickListener(v -> {
			selectAll = "absent";
			studentAttendanceListAdaptor = new StudentAttendanceListAdaptor(StudentListActivity.this, R.layout.user_attendance_list_item1, studentArray, value, stdSubject, selectAll);
			studentList.setAdapter(studentAttendanceListAdaptor);
			studentAttendanceListAdaptor.notifyDataSetChanged();
		});
		
		if(value.equals("view")) {
			studentList.setOnItemClickListener((parent, view, position, id) -> {
				Intent intent = new Intent(this, StudentAttendanceActivity.class);
				intent.putExtra("studentId", studentArray.get(position).getUserID());
				intent.putExtra("studentImg", studentArray.get(position).getUserImg());
				intent.putExtra("subject", stdSubject);
				intent.putExtra("name", studentArray.get(position).getUserrType());
				startActivity(intent);
			});
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
		
		// get Connectivity Manager object to check connection
		ConnectivityManager connec =
				(ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
		
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
