package com.aaptrix.activitys.student;

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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
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

import com.aaptrix.activitys.admin.InstituteBuzzActivityDiff;
import com.aaptrix.adaptor.ResultListAdaptor;
import com.aaptrix.databeans.DataBeanExamTt;
import com.aaptrix.R;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import static com.aaptrix.tools.HttpUrl.ALL_NEXT_RESULT;
import static com.aaptrix.tools.HttpUrl.ALL_PREVIOUS_RESULT;
import static com.aaptrix.tools.HttpUrl.USER_RESULT;
import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREF_COLOR;

/**
 * Created by Administrator on 11/29/2017.
 */

public class ResultActivity extends AppCompatActivity {
	
	public static final String PREFS_ABOUTUS = "json_about2";
	SharedPreferences.Editor editor;
	String userId, userSchoolId, userSchoolLogo, userRoleId, userSection;
	AppBarLayout appBarLayout;
	SharedPreferences.Editor editorColor;
	String selToolColor, selDrawerColor, selStatusColor, selTextColor1, selTextColor2;
	TextView tool_title;
	String userType, selBatch;
	ListView exam_list;
	ResultListAdaptor resultListAdaptor;
	TextView no_time_table, examMainName;
	ArrayList<DataBeanExamTt> examTtArray = new ArrayList<>();
	DataBeanExamTt dbe;
	ImageView previous, next;
	FrameLayout previousF, nextF;
	LinearLayout mainLayoutResult, header, header1;
	RelativeLayout resultLayout;
	Button viewReports;
	SharedPreferences.Editor se_aboutUs;
	String userrType;
	TableLayout table;
	private SwipeRefreshLayout mSwipeRefreshLayout;
	private String examId = "", examName, examDate, subjectName;
	//offline
	private SharedPreferences sp_aboutUs;
	MediaPlayer mp;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.result_layout);
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		appBarLayout = findViewById(R.id.appBarLayout);
		tool_title = findViewById(R.id.tool_title);
		resultLayout = findViewById(R.id.result_relative_layout);
		table = findViewById(R.id.result_table);
		viewReports = findViewById(R.id.view_reports);
		mp = MediaPlayer.create(this, R.raw.button_click);
		
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		editor = settings.edit();
		userId = settings.getString("userID", "");
		userSchoolId = settings.getString("str_school_id", "");
		userSchoolLogo = settings.getString("userSchoolLogo", "");
		userRoleId = settings.getString("str_role_id", "");
		userType = settings.getString("userrType", "");
		userSection = settings.getString("userSection", "");
		userrType = settings.getString("userrType", "");
		
		//color
		
		SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
		editorColor = settingsColor.edit();
		selToolColor = settingsColor.getString("tool", "");
		selDrawerColor = settingsColor.getString("drawer", "");
		selStatusColor = settingsColor.getString("status", "");
		selTextColor1 = settingsColor.getString("text1", "");
		selTextColor2 = settingsColor.getString("text2", "");
		
		exam_list = findViewById(R.id.exam_list);
		no_time_table = findViewById(R.id.no_time_table);
		mSwipeRefreshLayout = findViewById(R.id.activity_main_swipe_refresh_layout);
		nextF = findViewById(R.id.Ib_nextF);
		previousF = findViewById(R.id.ib_prevF);
		examMainName = findViewById(R.id.examMainName);
		
		next = findViewById(R.id.Ib_next);
		previous = findViewById(R.id.ib_prev);
		
		mainLayoutResult = findViewById(R.id.mainLayoutResult);
		header = findViewById(R.id.header);
		header1 = findViewById(R.id.header1);
		
		if (userType.equals("Admin") || userType.equals("Teacher")) {
			selBatch = getIntent().getStringExtra("selBatch");
			
			if (isInternetOn()) {
				GetAllExams b1 = new GetAllExams(ResultActivity.this);
				b1.execute(userSchoolId, selBatch);
				
			}
			
			previousF.setOnClickListener(view -> {
				GetAllExamsPrev b1 = new GetAllExamsPrev(view.getContext());
				b1.execute(userSchoolId, selBatch, examId);
			});
			
			nextF.setOnClickListener(view -> {
				GetAllExamsNext b1 = new GetAllExamsNext(view.getContext());
				b1.execute(userSchoolId, selBatch, examId);
			});
		} else {
			sp_aboutUs = getSharedPreferences(PREFS_ABOUTUS, 0);
			se_aboutUs = sp_aboutUs.edit();
			String aboutUs = sp_aboutUs.getString("json_aboutus2", "");
			getAboutUs(aboutUs);
			if (isInternetOn()) {
				GetAllExams b1 = new GetAllExams(ResultActivity.this);
				b1.execute(userSchoolId, userId);
			} else {
				getAboutUs(aboutUs);
			}
			previousF.setOnClickListener(view -> {
				if (isInternetOn()) {
					// Toast.makeText(ResultActivity.this, ""+userSchoolId+userId+examId, Toast.LENGTH_SHORT).show();
					GetAllExamsPrev b1 = new GetAllExamsPrev(view.getContext());
					b1.execute(userSchoolId, userId, examId);
				} else {
					Toast.makeText(ResultActivity.this, "No internet", Toast.LENGTH_SHORT).show();
				}
			});
			
			nextF.setOnClickListener(view -> {
				if (isInternetOn()) {
					GetAllExamsNext b1 = new GetAllExamsNext(view.getContext());
					b1.execute(userSchoolId, userId, examId);
				} else {
					Toast.makeText(ResultActivity.this, "No internet", Toast.LENGTH_SHORT).show();
				}
			});
		}
		
		
		previous.setColorFilter(Color.parseColor(selDrawerColor));
		next.setColorFilter(Color.parseColor(selDrawerColor));
		
		
		appBarLayout.setBackgroundColor(Color.parseColor(selToolColor));
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			Window window = getWindow();
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.setStatusBarColor(Color.parseColor(selStatusColor));
		}
		viewReports.setBackgroundColor(Color.parseColor(selToolColor));
		viewReports.setTextColor(Color.parseColor(selTextColor1));
		tool_title.setTextColor(Color.parseColor(selTextColor1));
		
	}
	
	private void getAboutUs(String aboutUs) {
		if (aboutUs.equals("{\"result\":null}")) {
			no_time_table.setVisibility(View.VISIBLE);
			header1.setVisibility(View.GONE);
			header.setVisibility(View.GONE);
			examTtArray.clear();
		} else {
			no_time_table.setVisibility(View.GONE);
			header1.setVisibility(View.VISIBLE);
			header.setVisibility(View.VISIBLE);
			try {
				JSONObject jsonRootObject = new JSONObject(aboutUs);
				JSONArray jsonArray = jsonRootObject.getJSONArray("result");
				examTtArray.clear();
				for (int i = 0; i < jsonArray.length(); i++) {
					dbe = new DataBeanExamTt();
					
					JSONObject jsonObject = jsonArray.getJSONObject(i);
					examId = jsonObject.getString("tbl_result_id");
					examName = jsonObject.getString("tbl_result_exam_name");
					examDate = jsonObject.getString("tbl_result_details_marks");
					subjectName = jsonObject.getString("tbl_result_details_subject_name");
					dbe.setExamId(examId);
					dbe.setExamName(examName);
					dbe.setExamDate(examDate);
					dbe.setSubjectName(subjectName);
					examTtArray.add(dbe);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			if (examTtArray.size() != 0) {
				listItms();
			}
		}
	}
	
	private void listItms() {
		try {
			examMainName.setText(examName);
			resultListAdaptor = new ResultListAdaptor(ResultActivity.this, R.layout.exam_list_item, examTtArray);
			exam_list.setAdapter(resultListAdaptor);
		} catch (Exception ignored) {
		
		}
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

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		
		if (item.getItemId() == android.R.id.home) {
			if (userrType.equals("Student")) {
				Intent i = new Intent(ResultActivity.this, InstituteBuzzActivity.class);
				startActivity(i);
				finish();
				overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
			} else {
				Intent i = new Intent(ResultActivity.this, InstituteBuzzActivityDiff.class);
				startActivity(i);
				finish();
				overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
			}
			
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		if (userrType.equals("Student")) {
			Intent i = new Intent(ResultActivity.this, InstituteBuzzActivity.class);
			startActivity(i);
			finish();
			overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		} else {
			Intent i = new Intent(ResultActivity.this, InstituteBuzzActivityDiff.class);
			startActivity(i);
			finish();
			overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
		}
	}
	
	//online
	@SuppressLint("StaticFieldLeak")
	public class GetAllExams extends AsyncTask<String, String, String> {
		Context ctx;
		
		GetAllExams(Context ctx) {
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
			
			String schoolId = params[0];
			String sectionName = params[1];
			String data;
			
			try {
				
				URL url = new URL(USER_RESULT);
				HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
				httpURLConnection.setRequestMethod("POST");
				httpURLConnection.setDoOutput(true);
				httpURLConnection.setDoInput(true);
				
				OutputStream outputStream = httpURLConnection.getOutputStream();
				
				BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
				data = URLEncoder.encode("schoolId", "UTF-8") + "=" + URLEncoder.encode(schoolId, "UTF-8") + "&" +
						URLEncoder.encode("userId", "UTF-8") + "=" + URLEncoder.encode(sectionName, "UTF-8") + "&" +
						URLEncoder.encode("user_type", "UTF-8") + "=" + URLEncoder.encode(userrType, "UTF-8");
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
			Log.e("JsonEXAMLIST", "" + result);
			//loader.setVisibility(View.GONE);
			mSwipeRefreshLayout.setRefreshing(false);
			sp_aboutUs = getSharedPreferences(PREFS_ABOUTUS, 0);
			se_aboutUs = sp_aboutUs.edit();
			se_aboutUs.clear();
			se_aboutUs.putString("json_aboutus2", result);
			se_aboutUs.commit();
			if (result.equals("{\"result\":null}")) {
				no_time_table.setVisibility(View.VISIBLE);
				header1.setVisibility(View.GONE);
				header.setVisibility(View.GONE);
				exam_list.setVisibility(View.GONE);
				examTtArray.clear();
			} else {
				no_time_table.setVisibility(View.GONE);
				exam_list.setVisibility(View.VISIBLE);
				header1.setVisibility(View.VISIBLE);
				header.setVisibility(View.VISIBLE);
				try {
					examTtArray.clear();
					JSONObject jsonRootObject = new JSONObject(result);
					JSONArray jsonArray = jsonRootObject.getJSONArray("result");
					examTtArray.clear();
					for (int i = 0; i < jsonArray.length(); i++) {
						dbe = new DataBeanExamTt();
						
						JSONObject jsonObject = jsonArray.getJSONObject(i);
						examId = jsonObject.getString("tbl_result_id");
						examName = jsonObject.getString("tbl_result_exam_name");
						examDate = jsonObject.getString("tbl_result_details_marks");
						subjectName = jsonObject.getString("tbl_result_details_subject_name");
						dbe.setExamId(examId);
						dbe.setExamName(examName);
						dbe.setExamDate(examDate);
						dbe.setSubjectName(subjectName);
						examTtArray.add(dbe);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (examTtArray.size() != 0) {
					listItms();
				}
				super.onPostExecute(result);
				resultListAdaptor.notifyDataSetChanged();
			}
		}
	}
	
	@SuppressLint("StaticFieldLeak")
	public class GetAllExamsNext extends AsyncTask<String, String, String> {
		Context ctx;
		
		GetAllExamsNext(Context ctx) {
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
			String schoolId = params[0];
			String sectionName = params[1];
			String examId = params[2];
			String data;
			
			try {
				
				URL url = new URL(ALL_NEXT_RESULT);
				HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
				httpURLConnection.setRequestMethod("POST");
				httpURLConnection.setDoOutput(true);
				httpURLConnection.setDoInput(true);
				
				OutputStream outputStream = httpURLConnection.getOutputStream();
				
				BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
				data = URLEncoder.encode("schoolId", "UTF-8") + "=" + URLEncoder.encode(schoolId, "UTF-8") + "&" +
						URLEncoder.encode("userId", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8") + "&" +
						URLEncoder.encode("examId", "UTF-8") + "=" + URLEncoder.encode(examId, "UTF-8") + "&" +
						URLEncoder.encode("user_type", "UTF-8") + "=" + URLEncoder.encode(userrType, "UTF-8");
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
			
			if (result.equals("{\"result\":null}")) {
				//examTtArray.clear();
//				exam_list.setVisibility(View.GONE);
//				no_time_table.setVisibility(View.VISIBLE);
//				examTtArray.clear();
				Toast.makeText(ctx, "No more result", Toast.LENGTH_SHORT).show();
			} else {
				no_time_table.setVisibility(View.GONE);
				exam_list.setVisibility(View.VISIBLE);
				try {
					JSONObject jsonRootObject = new JSONObject(result);
					JSONArray jsonArray = jsonRootObject.getJSONArray("result");
					examTtArray.clear();
					for (int i = 0; i < jsonArray.length(); i++) {
						dbe = new DataBeanExamTt();
						
						JSONObject jsonObject = jsonArray.getJSONObject(i);
						examId = jsonObject.getString("tbl_result_id");
						examName = jsonObject.getString("tbl_result_exam_name");
						examDate = jsonObject.getString("tbl_result_details_marks");
						subjectName = jsonObject.getString("tbl_result_details_subject_name");
						dbe.setExamId(examId);
						dbe.setExamName(examName);
						dbe.setExamDate(examDate);
						dbe.setSubjectName(subjectName);
						examTtArray.add(dbe);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (examTtArray.size() != 0) {
					listItms();
				}
				super.onPostExecute(result);
				resultListAdaptor.notifyDataSetChanged();
			}
			
		}
		
	}
	
	@SuppressLint("StaticFieldLeak")
	public class GetAllExamsPrev extends AsyncTask<String, String, String> {
		Context ctx;
		
		GetAllExamsPrev(Context ctx) {
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
			String schoolId = params[0];
			String sectionName = params[1];
			String examId = params[2];
			String data;
			
			try {
				
				URL url = new URL(ALL_PREVIOUS_RESULT);
				HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
				httpURLConnection.setRequestMethod("POST");
				httpURLConnection.setDoOutput(true);
				httpURLConnection.setDoInput(true);
				
				OutputStream outputStream = httpURLConnection.getOutputStream();
				
				BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
				data = URLEncoder.encode("schoolId", "UTF-8") + "=" + URLEncoder.encode(schoolId, "UTF-8") + "&" +
						URLEncoder.encode("userId", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8") + "&" +
						URLEncoder.encode("examId", "UTF-8") + "=" + URLEncoder.encode(examId, "UTF-8") + "&" +
						URLEncoder.encode("user_type", "UTF-8") + "=" + URLEncoder.encode(userrType, "UTF-8");
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
			Log.d("PREVIOUS", "" + result);
			//loader.setVisibility(View.GONE);
			mSwipeRefreshLayout.setRefreshing(false);
			
			if (result.equals("{\"result\":null}")) {
				//examTtArray.clear();
//				exam_list.setVisibility(View.GONE);
//				no_time_table.setVisibility(View.VISIBLE);
//				examTtArray.clear();
				Toast.makeText(ctx, "No more result", Toast.LENGTH_SHORT).show();
			} else {
				exam_list.setVisibility(View.VISIBLE);
				no_time_table.setVisibility(View.GONE);
				
				try {
					JSONObject jsonRootObject = new JSONObject(result);
					JSONArray jsonArray = jsonRootObject.getJSONArray("result");
					examTtArray.clear();
					for (int i = 0; i < jsonArray.length(); i++) {
						dbe = new DataBeanExamTt();
						
						JSONObject jsonObject = jsonArray.getJSONObject(i);
						examId = jsonObject.getString("tbl_result_id");
						examName = jsonObject.getString("tbl_result_exam_name");
						examDate = jsonObject.getString("tbl_result_details_marks");
						subjectName = jsonObject.getString("tbl_result_details_subject_name");
						dbe.setExamId(examId);
						dbe.setExamName(examName);
						dbe.setExamDate(examDate);
						dbe.setSubjectName(subjectName);
						examTtArray.add(dbe);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				if (examTtArray.size() != 0) {
					listItms();
				}
				super.onPostExecute(result);
				resultListAdaptor.notifyDataSetChanged();
			}
		}
		
	}
	
}
