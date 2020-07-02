package com.aaptrix.activitys.teacher;

import com.aaptrix.activitys.student.OnlineReport;
import com.aaptrix.activitys.student.StudentResult;
import com.aaptrix.adaptor.StudentPerformanceAdapter;
import com.aaptrix.databeans.DataBeanStudent;
import com.aaptrix.R;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Objects;

import static com.aaptrix.tools.HttpUrl.ALL_STUDENTS;
import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREF_COLOR;

public class StudentPerformance extends AppCompatActivity {
	
	ArrayList<DataBeanStudent> studentArray = new ArrayList<>();
	SwipeRefreshLayout swipeRefreshLayout;
	String stdSection, schoolId;
	String selToolColor, selStatusColor, selTextColor1;
	AppBarLayout appBarLayout;
	TextView tool_title, no_student;
	String type;
	StudentPerformanceAdapter adapter;
	ListView studentList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_student_performance);
		stdSection = getIntent().getStringExtra("selBatch");
		type = getIntent().getStringExtra("type");
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		setTitle("");
		setResult(RESULT_OK);
		appBarLayout = findViewById(R.id.appBarLayout);
		tool_title = findViewById(R.id.tool_title);
		studentList = findViewById(R.id.studentList);
		no_student = findViewById(R.id.no_student);
		swipeRefreshLayout = findViewById(R.id.activity_main_swipe_refresh_layout);
		
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		schoolId = settings.getString("str_school_id", "");
		
		SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
		selToolColor = settingsColor.getString("tool", "");
		selStatusColor = settingsColor.getString("status", "");
		selTextColor1 = settingsColor.getString("text1", "");
		
		GetAllStudentList list = new GetAllStudentList(this);
		list.execute(stdSection, schoolId);
		
		appBarLayout.setBackgroundColor(Color.parseColor(selToolColor));
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			Window window = getWindow();
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.setStatusBarColor(Color.parseColor(selStatusColor));
		}
		tool_title.setTextColor(Color.parseColor(selTextColor1));
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
			swipeRefreshLayout.setRefreshing(true);
			super.onPreExecute();
		}
		
		@Override
		protected String doInBackground(String... params) {
			
			String sectionName = params[0];
			String school_id = params[1];
			String current_date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(System.currentTimeMillis());
			String data;
			
			try {
				
				URL url = new URL(ALL_STUDENTS);
				HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
				httpURLConnection.setRequestMethod("POST");
				httpURLConnection.setDoOutput(true);
				httpURLConnection.setDoInput(true);
				
				OutputStream outputStream = httpURLConnection.getOutputStream();
				
				BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
				data = URLEncoder.encode("section_name", "UTF-8") + "=" + URLEncoder.encode(sectionName, "UTF-8") + "&" +
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
			swipeRefreshLayout.setRefreshing(false);
			Log.e("result", result);
			if (result.equals("{\"result\":null}")) {
				studentArray.clear();
				no_student.setVisibility(View.VISIBLE);
			} else {
				no_student.setVisibility(View.GONE);
				try {
					JSONObject jsonRootObject = new JSONObject(result);
					JSONArray jsonArray = jsonRootObject.getJSONArray("result");
					studentArray.clear();
					for (int i = 0; i < jsonArray.length(); i++) {
						DataBeanStudent dbs = new DataBeanStudent();
						JSONObject jsonObject = jsonArray.getJSONObject(i);
						dbs.setUserID(jsonObject.getString("tbl_users_id"));
						dbs.setUserName(jsonObject.getString("tbl_users_name"));
						dbs.setUserImg(jsonObject.getString("tbl_users_img"));
						studentArray.add(dbs);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				listItem();
				super.onPostExecute(result);
			}
		}
	}
	
	private void listItem() {
		adapter = new StudentPerformanceAdapter(this, R.layout.list_student_performance, studentArray);
		studentList.setAdapter(adapter);
		adapter.notifyDataSetChanged();

		studentList.setOnItemClickListener((parent, view, position, id) -> {
			if (type.equals("performance")) {
				Intent intent = new Intent(this, StudentResult.class);
				intent.putExtra("studentId", studentArray.get(position).getUserID());
				intent.putExtra("studentImage", studentArray.get(position).getUserImg());
				intent.putExtra("studentName", studentArray.get(position).getUserName());
				intent.putExtra("userSection", stdSection);
				intent.putExtra("userType", "teacher");
				startActivity(intent);
			} else if (type.equals("online")) {
				String examid = getIntent().getStringExtra("examId");
				String examname = getIntent().getStringExtra("examName");
				Intent intent = new Intent(this, OnlineReport.class);
				intent.putExtra("userId", studentArray.get(position).getUserID());
				intent.putExtra("userSection", stdSection);
				intent.putExtra("examId", examid);
				intent.putExtra("examName", examname);
				startActivity(intent);
			} else {
				Intent intent = new Intent(this, StudentAttendanceActivity.class);
				intent.putExtra("studentId", studentArray.get(position).getUserID());
				intent.putExtra("studentImg", studentArray.get(position).getUserImg());
				intent.putExtra("subject", getIntent().getStringExtra("subject"));
				startActivity(intent);
			}
		});
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			onBackPressed();
		}
		return super.onOptionsItemSelected(item);
	}
}
