package com.aaptrix.activitys.admin;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.appbar.AppBarLayout;

import androidx.cardview.widget.CardView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import com.aaptrix.databeans.DataBeanStudent;
import com.aaptrix.adaptor.StudentResultListRecycleAdaptorEditText;
import com.aaptrix.R;
import pl.droidsonroids.gif.GifImageView;

import static com.aaptrix.tools.HttpUrl.ALL_STUDENTS_FOR_RESULT;
import static com.aaptrix.tools.HttpUrl.ALL_STUDENTS_FOR_VIEW_RESULT;
import static com.aaptrix.tools.HttpUrl.SAVED_RESULT_LIST;
import static com.aaptrix.tools.HttpUrl.SAVE_RESULT;
import static com.aaptrix.tools.HttpUrl.SUBMIT_RESULT;
import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREFS_RW;
import static com.aaptrix.tools.SPClass.PREF_COLOR;

public class AddResultForStudentListActivity extends AppCompatActivity {
	
	SharedPreferences.Editor editor;
	String userId, roleId, schoolId, userSection, userRollNumber, userClassTeacher;
	//offline
	AlertDialog.Builder alert;
	SharedPreferences.Editor editorColor;
	String selToolColor, selDrawerColor, selStatusColor, selTextColor1, selTextColor2, numberOfUser;
	AppBarLayout appBarLayout;
	TextView tool_title;
	String userLoginId, userName, userImg, userrType, userPassword, userSchoolLogo;
	String userSchoolId;
	private SwipeRefreshLayout mSwipeRefreshLayout;
	RecyclerView studentList;
	String value;
	LinearLayout submitResultLayout;
	String selBatch, selStudent, selSubject, type;
	ArrayList<DataBeanStudent> studentArray = new ArrayList<>();
	DataBeanStudent dbs;
	String studentId, studentName, studentImage, studentResultStatus;
	TextView submit_result, save_result, update_result;
	private SharedPreferences sp_attend;
	SharedPreferences.Editor se_attend;
	public static final String PREFS_ATTENDANCE = "attendance";
	String studentArraySize = "0";
	String resultSaved, selExam, selSubj, type1;
	LinearLayout edit_layout, result_diary;
	String task = "";
	View lineView;
	LinearLayout add_layout;
	ImageView addResult;
	CardView cardView;
	GifImageView taskStatus;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.student_list_result_layout);
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		setTitle("");
		cardView = findViewById(R.id.card_view);
		taskStatus = findViewById(R.id.task_status);
		setResult(RESULT_OK);
		appBarLayout = findViewById(R.id.appBarLayout);
		tool_title = findViewById(R.id.tool_title);
		SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
		editorColor = settingsColor.edit();
		selToolColor = settingsColor.getString("tool", "");
		selDrawerColor = settingsColor.getString("drawer", "");
		selStatusColor = settingsColor.getString("status", "");
		selTextColor1 = settingsColor.getString("text1", "");
		selTextColor2 = settingsColor.getString("text2", "");
		mSwipeRefreshLayout = findViewById(R.id.activity_main_swipe_refresh_layout);
		mSwipeRefreshLayout.setEnabled(false);
		submitResultLayout = findViewById(R.id.submitAttendanceLayout);
		submitResultLayout.setVisibility(View.GONE);
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
		result_diary = findViewById(R.id.attendance_action);
		result_diary.setBackgroundColor(Color.parseColor(selToolColor));
		studentList = findViewById(R.id.studentList);
		submit_result = findViewById(R.id.submit_attendance);
		save_result = findViewById(R.id.save_result);
		update_result = findViewById(R.id.update_result);
		add_layout = findViewById(R.id.add_layout);
		addResult = findViewById(R.id.add_result);
		lineView = findViewById(R.id.lineView);
		edit_layout = findViewById(R.id.edit_layout);
		value = getIntent().getStringExtra("value");
		selBatch = getIntent().getStringExtra("selBatch");
		selStudent = getIntent().getStringExtra("selStudent");
		selSubject = getIntent().getStringExtra("selSubject");
		type = getIntent().getStringExtra("type");
		
		task = getIntent().getStringExtra("task");
		if (task.equals("updateResult")) {
			lineView.setVisibility(View.GONE);
			save_result.setVisibility(View.GONE);
			submit_result.setVisibility(View.GONE);
			update_result.setVisibility(View.VISIBLE);
			
			update_result.setOnClickListener(view -> {
				sp_attend = getSharedPreferences(PREFS_ATTENDANCE, 0);
				se_attend = sp_attend.edit();
				String results = sp_attend.getString("json_attend", "");
				studentArraySize = sp_attend.getString("studentArray", "");
				try {
					LayoutInflater factory = LayoutInflater.from(AddResultForStudentListActivity.this);
					View textEntryView = factory.inflate(R.layout.submit_result_popup_layout, null);
					alert = new AlertDialog.Builder(AddResultForStudentListActivity.this, R.style.DialogTheme);
					alert.setView(textEntryView).setPositiveButton("Yes",
							(dialog, whichButton) -> {
								SubmitResult b1 = new SubmitResult(AddResultForStudentListActivity.this);
								b1.execute(SUBMIT_RESULT, results, selBatch, selStudent, selSubject, userId, schoolId);
							}).setNegativeButton("No",
							(dialog, whichButton) -> {
							});
					AlertDialog alertDialog = alert.create();
					alertDialog.show();
					Button theButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
					Button theButton1 = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
					theButton.setTextColor(Color.parseColor(selToolColor));
					theButton1.setTextColor(Color.parseColor(selToolColor));
				} catch (Exception e) {
					Toast.makeText(AddResultForStudentListActivity.this, "Please submit all student result", Toast.LENGTH_SHORT).show();
				}
			});
		} else {
			save_result.setVisibility(View.VISIBLE);
			lineView.setVisibility(View.VISIBLE);
			submit_result.setVisibility(View.VISIBLE);
			update_result.setVisibility(View.GONE);
		}

		addResult.setOnClickListener(v -> {
			Intent i = new Intent(this, AddResultForStudentListActivity.class);
			i.putExtra("selBatch", selBatch);
			i.putExtra("selStudent", selStudent);
			i.putExtra("selSubject", selSubject);
			i.putExtra("value", "take");
			i.putExtra("type", "number");
			i.putExtra("task", "0");
			startActivity(i);
			overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		});
		
		tool_title.setText(selSubject + ", " + selStudent);
		settings = getSharedPreferences(selSubject, 0);
		editor = settings.edit();
		resultSaved = settings.getString("resultSaved", "");
		selExam = settings.getString("selExam", "");
		selSubj = settings.getString("selSubject", "");
		type1 = settings.getString("type", "");
		
		edit_layout.setOnClickListener(view -> {
			value = "update";
			if (isInternetOn()) {
				studentArray.clear();
				GetAllStudentList b1 = new GetAllStudentList(AddResultForStudentListActivity.this);
				b1.execute(ALL_STUDENTS_FOR_VIEW_RESULT, selBatch, schoolId, selStudent, selSubject);
			} else {
				Toast.makeText(AddResultForStudentListActivity.this, "No internet", Toast.LENGTH_SHORT).show();
			}
		});
		
		if (value.equals("take")) {
			if (isInternetOn()) {
				if (resultSaved.equals("resultSaved") && selExam.equals(selStudent) && selSubj.equals(selSubject) && type1.equals(type)) {
					studentArray.clear();
					GetAllStudentList b1 = new GetAllStudentList(AddResultForStudentListActivity.this);
					b1.execute(SAVED_RESULT_LIST, selBatch, schoolId, selStudent, selSubject);
				} else {
					if (task.equals("updateResult")) {
						GetAllStudentList b1 = new GetAllStudentList(AddResultForStudentListActivity.this);
						b1.execute(ALL_STUDENTS_FOR_RESULT, selBatch, schoolId, selStudent, selSubject);
					} else {
						studentArray.clear();
						GetAllStudentList b1 = new GetAllStudentList(AddResultForStudentListActivity.this);
						b1.execute(ALL_STUDENTS_FOR_RESULT, selBatch, schoolId, selStudent, selSubject);
					}
					
				}
			} else {
				Toast.makeText(AddResultForStudentListActivity.this, "No internet", Toast.LENGTH_SHORT).show();
			}
			
			submitResultLayout.setVisibility(View.VISIBLE);
			if (!isInternetOn()) {
				Toast.makeText(AddResultForStudentListActivity.this, "No internet", Toast.LENGTH_SHORT).show();
			}
			
			submit_result.setOnClickListener(view -> {
				sp_attend = getSharedPreferences(PREFS_ATTENDANCE, 0);
				se_attend = sp_attend.edit();
				String results = sp_attend.getString("json_attend", "");
				studentArraySize = sp_attend.getString("studentArray", "");
				Log.e("results", "" + results);
				
				try {
					LayoutInflater factory = LayoutInflater.from(AddResultForStudentListActivity.this);
					View textEntryView = factory.inflate(R.layout.submit_result_popup_layout, null);
					alert = new AlertDialog.Builder(AddResultForStudentListActivity.this, R.style.DialogTheme);
					alert.setView(textEntryView).setPositiveButton("Yes",
							(dialog, whichButton) -> {
								SharedPreferences settings1 = getSharedPreferences(selSubject, 0);
								SharedPreferences.Editor editor1 = settings1.edit();
								editor1.remove("resultSaved");
								editor1.remove("selExam");
								editor1.remove("selSubject");
								editor1.remove("type");
								editor1.commit();
								Log.e("results", "" + results);
								SubmitResult b1 = new SubmitResult(AddResultForStudentListActivity.this);
								b1.execute(SUBMIT_RESULT, results, selBatch, selStudent, selSubject, userId, schoolId);
							}).setNegativeButton("No",
							(dialog, whichButton) -> {
							});
					AlertDialog alertDialog = alert.create();
					alertDialog.show();
					Button theButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
					Button theButton1 = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
					theButton.setTextColor(Color.parseColor(selToolColor));
					theButton1.setTextColor(Color.parseColor(selToolColor));
				} catch (Exception e) {
					Toast.makeText(AddResultForStudentListActivity.this, "Please submit all student result", Toast.LENGTH_SHORT).show();
				}
			});
			
			save_result.setOnClickListener(view -> {
				sp_attend = getSharedPreferences(PREFS_ATTENDANCE, 0);
				se_attend = sp_attend.edit();
				String results = sp_attend.getString("json_attend", "");
				studentArraySize = sp_attend.getString("studentArray", "");
				Log.e("results", "" + results);
				try {
					if ((studentArray.size() == 0)) {
						Toast.makeText(AddResultForStudentListActivity.this, "Please enter at least one student result", Toast.LENGTH_SHORT).show();
					} else {
						SharedPreferences settings12 = getSharedPreferences(selSubject, 0);
						SharedPreferences.Editor editor12 = settings12.edit();
						editor12.putString("resultSaved", "resultSaved");
						editor12.putString("selExam", selStudent);
						editor12.putString("selSubject", selSubject);
						editor12.putString("type", type);
						editor12.commit();
						SubmitResult b1 = new SubmitResult(AddResultForStudentListActivity.this);
						b1.execute(SAVE_RESULT, results, selBatch, selStudent, selSubject, userId, schoolId);
					}
				} catch (Exception e) {
					Toast.makeText(AddResultForStudentListActivity.this, "Please submit all student result", Toast.LENGTH_SHORT).show();
				}
			});
			
		} else if (value.equals("view")) {
			if (isInternetOn()) {
				studentArray.clear();
				GetAllStudentList1 b1 = new GetAllStudentList1(AddResultForStudentListActivity.this);
				b1.execute(ALL_STUDENTS_FOR_VIEW_RESULT, selBatch, schoolId, selStudent, selSubject);
			} else {
				Toast.makeText(AddResultForStudentListActivity.this, "No internet", Toast.LENGTH_SHORT).show();
			}
			
		}
		
		appBarLayout.setBackgroundColor(Color.parseColor(selToolColor));
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			Window window = getWindow();
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.setStatusBarColor(Color.parseColor(selStatusColor));
		}
		GradientDrawable bgShape = (GradientDrawable) add_layout.getBackground();
		bgShape.setColor(Color.parseColor(selToolColor));
		tool_title.setTextColor(Color.parseColor(selTextColor1));
		submitResultLayout.setBackgroundColor(Color.parseColor(selToolColor));
	}
	
	@SuppressLint("StaticFieldLeak")
	public class SubmitResult extends AsyncTask<String, String, String> {
		Context ctx;
		
		SubmitResult(Context ctx) {
			this.ctx = ctx;
		}
		
		@Override
		protected void onPreExecute() {
			mSwipeRefreshLayout.setRefreshing(true);
			super.onPreExecute();
		}
		
		@Override
		protected String doInBackground(String... params) {
			String urls = params[0];
			String results = params[1];
			String selBatch = params[2];
			String selExam = params[3];
			String selSubject = params[4];
			String userId = params[5];
			String schoolId = params[6];
			String data;
			
			try {
				URL url = new URL(urls);
				HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
				httpURLConnection.setRequestMethod("POST");
				httpURLConnection.setDoOutput(true);
				httpURLConnection.setDoInput(true);
				
				OutputStream outputStream = httpURLConnection.getOutputStream();
				
				BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
				data = URLEncoder.encode("result_array", "UTF-8") + "=" + URLEncoder.encode(results, "UTF-8") + "&" +
						URLEncoder.encode("selBatch", "UTF-8") + "=" + URLEncoder.encode(selBatch, "UTF-8") + "&" +
						URLEncoder.encode("selExam", "UTF-8") + "=" + URLEncoder.encode(selExam, "UTF-8") + "&" +
						URLEncoder.encode("selSubject", "UTF-8") + "=" + URLEncoder.encode(selSubject, "UTF-8") + "&" +
						URLEncoder.encode("userId", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8") + "&" +
						URLEncoder.encode("schoolId", "UTF-8") + "=" + URLEncoder.encode(schoolId, "UTF-8");
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
			switch (result) {
				case "error":
					Toast.makeText(ctx, "Please submit valid result", Toast.LENGTH_SHORT).show();
					break;
				case "Already Done":
					Toast.makeText(ctx, "Already Done", Toast.LENGTH_SHORT).show();
					break;
				case "wait":
					cardView.setVisibility(View.VISIBLE);
					new CountDownTimer(4000, 1000) {
						@Override
						public void onTick(long millisUntilFinished) {

						}

						@Override
						public void onFinish() {
							Intent i = new Intent(AddResultForStudentListActivity.this, ResultIntermidiateScreenActivity.class);
							i.putExtra("str_tool_title", "Result");
							startActivity(i);
							finish();
							overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
						}
					}.start();
					break;
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
			mSwipeRefreshLayout.setRefreshing(true);
			super.onPreExecute();
		}
		
		@Override
		protected String doInBackground(String... params) {
			
			String branch_url = params[0];
			String sectionName = params[1];
			String school_id = params[2];
			String selStudent = params[3];
			String selSubject = params[4];
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
						URLEncoder.encode("selStudent", "UTF-8") + "=" + URLEncoder.encode(selStudent, "UTF-8") + "&" +
						URLEncoder.encode("selSubject", "UTF-8") + "=" + URLEncoder.encode(selSubject, "UTF-8");
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
				Log.e("res", response.toString());
				return response.toString();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			Log.e("result data", "" + result);
			mSwipeRefreshLayout.setRefreshing(false);
			if (result.equals("no")) {
				studentList.setAdapter(null);
				studentArray.clear();
				if (value.equals("take")) {
					Toast.makeText(ctx, "No students", Toast.LENGTH_SHORT).show();
				} else if (value.equals("view")) {
					Toast.makeText(ctx, "No result", Toast.LENGTH_SHORT).show();
				}
			} else {
				try {
					Log.e("result", result);
					JSONObject jsonRootObject = new JSONObject(result);
					JSONArray jsonArray = jsonRootObject.getJSONArray("result");
					studentArray.clear();
					for (int i = 0; i < jsonArray.length(); i++) {
						dbs = new DataBeanStudent();
						JSONObject jsonObject = jsonArray.getJSONObject(i);
						if (value.equals("take")) {
							studentId = jsonObject.getString("tbl_users_id");
							studentName = jsonObject.getString("tbl_users_name");
							studentImage = jsonObject.getString("tbl_users_img");
							studentResultStatus = jsonObject.getString("tbl_result_details_marks");
						} else if (value.equals("view")) {
							Log.e("in", "view");
							studentId = jsonObject.getString("tbl_users_id");
							studentName = jsonObject.getString("tbl_users_name");
							studentImage = jsonObject.getString("tbl_users_img");
							studentResultStatus = jsonObject.getString("tbl_result_details_marks");
						}
						dbs.setUserID(studentId);
						dbs.setUserName(studentName);
						dbs.setUserImg(studentImage);
						//attendance
						dbs.setUserLoginId(studentResultStatus);
						studentArray.add(dbs);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				listItms(value);
				super.onPostExecute(result);
			}
		}
	}
	
	@SuppressLint("StaticFieldLeak")
	public class GetAllStudentList1 extends AsyncTask<String, String, String> {
		Context ctx;
		
		GetAllStudentList1(Context ctx) {
			this.ctx = ctx;
		}
		
		@Override
		protected void onPreExecute() {
			mSwipeRefreshLayout.setRefreshing(true);
			super.onPreExecute();
		}
		
		@Override
		protected String doInBackground(String... params) {
			
			String branch_url = params[0];
			String sectionName = params[1];
			String school_id = params[2];
			String selStudent = params[3];
			String selSubject = params[4];
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
						URLEncoder.encode("selStudent", "UTF-8") + "=" + URLEncoder.encode(selStudent, "UTF-8") + "&" +
						URLEncoder.encode("selSubject", "UTF-8") + "=" + URLEncoder.encode(selSubject, "UTF-8");
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
			Log.e("Json", "" + result);
			mSwipeRefreshLayout.setRefreshing(false);
			if (result.equals("no")) {
				studentList.setAdapter(null);
				studentArray.clear();
				if (value.equals("take")) {
					Toast.makeText(ctx, "No students", Toast.LENGTH_SHORT).show();
				} else if (value.equals("view")) {
					Toast.makeText(ctx, "No result", Toast.LENGTH_SHORT).show();
					SharedPreferences sp = getSharedPreferences(PREFS_RW, 0);
					String json = sp.getString("result", "");

					try {
						JSONObject jsonObject = new JSONObject(json);
						JSONArray jsonArray = jsonObject.getJSONArray("result");
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject object = jsonArray.getJSONObject(i);
							if (object.getString("tbl_insti_buzz_cate_name").equals("Results")) {
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
				}
			} else {
				try {
					JSONObject jsonRootObject = new JSONObject(result);
					JSONArray jsonArray = jsonRootObject.getJSONArray("result");
					studentArray.clear();
					for (int i = 0; i < jsonArray.length(); i++) {
						dbs = new DataBeanStudent();
						JSONObject jsonObject = jsonArray.getJSONObject(i);
						studentId = jsonObject.getString("tbl_users_id");
						studentName = jsonObject.getString("tbl_users_name");
						studentImage = jsonObject.getString("tbl_users_img");
						studentResultStatus = jsonObject.getString("tbl_result_details_marks");
						dbs.setUserID(studentId);
						dbs.setUserName(studentName);
						dbs.setUserImg(studentImage);
						dbs.setUserLoginId(studentResultStatus);
						studentArray.add(dbs);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				listItms(value);
				super.onPostExecute(result);
			}
		}
	}
	
	@SuppressLint("ClickableViewAccessibility")
	private void listItms(String value) {
        studentList.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        studentList.setLayoutManager(layoutManager);
        RecyclerView.Adapter adapter = new StudentResultListRecycleAdaptorEditText(studentArray, this, value, selBatch);
        studentList.setAdapter(adapter);
        studentList.setOnTouchListener((v, event) -> {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            assert imm != null;
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            return false;
        });
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
		ConnectivityManager connec =
				(ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
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
