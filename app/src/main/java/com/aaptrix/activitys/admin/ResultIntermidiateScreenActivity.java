package com.aaptrix.activitys.admin;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.appbar.AppBarLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

import com.aaptrix.R;

import static com.aaptrix.tools.HttpUrl.ALL_BATCHS;
import static com.aaptrix.tools.HttpUrl.ALL_EXAM_LIST;
import static com.aaptrix.tools.HttpUrl.ALL_SUBJECT_LIST;
import static com.aaptrix.tools.HttpUrl.RESULT_CHECK;
import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREFS_RW;
import static com.aaptrix.tools.SPClass.PREF_COLOR;

public class ResultIntermidiateScreenActivity extends AppCompatActivity {
	
	SharedPreferences.Editor editor;
	String userId, roleId, schoolId, userClass, userSection, userRollNumber, userClassTeacher;
	
	SharedPreferences.Editor editorColor;
	String selToolColor, selDrawerColor, selStatusColor, selTextColor1, selTextColor2, numberOfUser;
	AppBarLayout appBarLayout;
	TextView tool_title;
	String userID, userLoginId, userName, userImg, userrType, userPassword, userSchoolLogo;
	String userSchoolId;
	//
	TextView view1;
	LinearLayout attendance_diary;
	ImageView school_logo;
	MediaPlayer mp;
	//
	Spinner spin_section;
	Spinner spin_student;
	Spinner spin_subject;
	ProgressBar loader_section, loader_student, loader_subject;
	//
	String[] batch_array = {"Select Batch"};
	String[] batch_id = {"0"};
	String[] student_array = {"Select Exam"};
	String[] student_id = {"0"};
	String[] subject_array = {"Select Subject"};
	String[] subject_id = {"0"};
	String selBatch;
	String selStudent;
	String selSubject;
	String str_tool_title;
	TextView cube1, cube2;
	TextView view2, add_result, viewResult;
	String type1;
	String val;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.result_ntermidiate_layout);
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		
		setTitle("");
		setResult(RESULT_OK);
		appBarLayout = findViewById(R.id.appBarLayout);
		tool_title = findViewById(R.id.tool_title);
		mp = MediaPlayer.create(this, R.raw.button_click);
		init();
	}
	
	private void init() {
		//color
		SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
		editorColor = settingsColor.edit();
		selToolColor = settingsColor.getString("tool", "");
		selDrawerColor = settingsColor.getString("drawer", "");
		selStatusColor = settingsColor.getString("status", "");
		selTextColor1 = settingsColor.getString("text1", "");
		selTextColor2 = settingsColor.getString("text2", "");
		
		str_tool_title = getIntent().getStringExtra("str_tool_title");
		tool_title.setText(str_tool_title);
		//header
		
		SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
		editor = settings.edit();
		userId = settings.getString("userID", "");
		userLoginId = settings.getString("userLoginId", "");
		userName = settings.getString("userName", "");
		userImg = settings.getString("userImg", "");
		userClass = settings.getString("userClass", "");
		userSection = settings.getString("userSection", "");
		userRollNumber = settings.getString("userRollNumber", "");
		userClassTeacher = settings.getString("userTeacherName", "");
		userrType = settings.getString("userrType", "");
		userPassword = settings.getString("userPassword", "");
		roleId = settings.getString("str_role_id", "");
		schoolId = settings.getString("str_school_id", "");
		numberOfUser = settings.getString("numberOfUser", "");
		userSchoolLogo = settings.getString("userSchoolLogo", "");
		cube1 = findViewById(R.id.cube1);
		cube2 = findViewById(R.id.cube2);
		
		view2 = findViewById(R.id.view1);
		add_result = findViewById(R.id.add_result);
		viewResult = findViewById(R.id.viewResult);

		SharedPreferences preferences = getSharedPreferences(PREFS_RW, 0);
		String result = preferences.getString("result", "");

		try {
			JSONObject jsonObject = new JSONObject(result);
			JSONArray jsonArray = jsonObject.getJSONArray("result");
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject object = jsonArray.getJSONObject(i);
				if (object.getString("tbl_insti_buzz_cate_name").equals("Results")) {
					if (object.getString("tbl_scl_inst_buzz_detl_write_status").equals("Active")) {
						add_result.setVisibility(View.VISIBLE);
					} else {
						add_result.setVisibility(View.GONE);
					}
					break;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		//
		view1 = findViewById(R.id.view);
		attendance_diary = findViewById(R.id.attendance_action);
		
		school_logo = findViewById(R.id.school_logo);
		
		spin_section = findViewById(R.id.spin_section);
		spin_student = findViewById(R.id.spin_student);
		spin_subject = findViewById(R.id.spin_subject);
		loader_student = findViewById(R.id.loader_student);
		loader_section = findViewById(R.id.loader_section);
		loader_subject = findViewById(R.id.loader_subject);
		
		settings = getSharedPreferences(selSubject, 0);
		editor = settings.edit();
		type1 = settings.getString("type", "");

		Picasso.with(this).load(R.drawable.large_logo).into(school_logo);
		
		setBatch();
		setExam();
		setSubject();

		try {
			File directory = this.getFilesDir();
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File(directory, "batches")));
			String json = in.readObject().toString();
			in.close();
			if (!json.equals("{\"result\":null}")) {
				try {
					JSONObject jo = new JSONObject(json);
					//Get the instance of JSONArray that contains JSONObjects
					JSONArray ja = jo.getJSONArray("result");
					batch_array = new String[ja.length() + 1];
					batch_id = new String[ja.length() + 1];
					batch_array[0] = "Select Batch";
					batch_id[0] = "0";
					for (int i = 0; i < ja.length(); i++) {
						jo = ja.getJSONObject(i);
						batch_id[i + 1] = jo.getString("tbl_batch_detail_id");
						batch_array[i + 1] = jo.getString("tbl_batch_name");
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				setBatch();
			} else {
				String[] batch_array = {"Select Batch"};
				ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<>(ResultIntermidiateScreenActivity.this, R.layout.spinner_list_item1, batch_array);
				// Drop down layout style - list view with radio button
				dataAdapter1.setDropDownViewResource(R.layout.spinner_list_item1);
				// attaching data adapter to spinner
				spin_section.setAdapter(dataAdapter1);
				Toast.makeText(this, "No Batch", Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			e.printStackTrace();
			GetAllBatches b1 = new GetAllBatches(this);
			b1.execute(schoolId);
		}
		
		appBarLayout.setBackgroundColor(Color.parseColor(selToolColor));
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			Window window = getWindow();
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.setStatusBarColor(Color.parseColor(selStatusColor));
		}
		tool_title.setTextColor(Color.parseColor(selTextColor1));
		viewResult.setTextColor(Color.parseColor(selTextColor1));
		attendance_diary.setBackgroundColor(Color.parseColor(selToolColor));
		GradientDrawable drawable = (GradientDrawable) cube1.getBackground();
		drawable.setStroke(2, Color.parseColor(selToolColor));
		GradientDrawable drawable1 = (GradientDrawable) cube2.getBackground();
		drawable1.setStroke(2, Color.parseColor(selToolColor));
		view2.setBackgroundColor(Color.parseColor(selToolColor));
	}
	
	//
	@SuppressLint("StaticFieldLeak")
	public class GetAllBatches extends AsyncTask<String, String, String> {
		Context ctx;
		
		GetAllBatches(Context ctx) {
			this.ctx = ctx;
		}
		
		@Override
		protected void onPreExecute() {
			loader_section.setVisibility(View.VISIBLE);
			super.onPreExecute();
		}
		
		@Override
		protected String doInBackground(String... params) {
			
			String school_id = params[0];
			String data;
			
			try {
				
				URL url = new URL(ALL_BATCHS);
				HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
				httpURLConnection.setRequestMethod("POST");
				httpURLConnection.setDoOutput(true);
				httpURLConnection.setDoInput(true);
				
				OutputStream outputStream = httpURLConnection.getOutputStream();
				
				BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
				data = URLEncoder.encode("school_id", "UTF-8") + "=" + URLEncoder.encode(school_id, "UTF-8") + "&" +
						URLEncoder.encode("tbl_users_id", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8");
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
			
			loader_section.setVisibility(View.GONE);
			Log.d("result", result);
			//save state for offline
			if (!result.equals("{\"result\":null}")) {
				try {
					JSONObject jo = new JSONObject(result);
					//Get the instance of JSONArray that contains JSONObjects
					JSONArray ja = jo.getJSONArray("result");
					batch_array = new String[ja.length() + 1];
					batch_id = new String[ja.length() + 1];
					batch_array[0] = "Select Batch";
					batch_id[0] = "0";
					for (int i = 0; i < ja.length(); i++) {
						jo = ja.getJSONObject(i);
						batch_id[i + 1] = jo.getString("tbl_batch_detail_id");
						batch_array[i + 1] = jo.getString("tbl_batch_name");
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				setBatch();
			} else {
				String[] batch_array = {"Select Batch"};
				ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<>(ResultIntermidiateScreenActivity.this, R.layout.spinner_list_item1, batch_array);
				// Drop down layout style - list view with radio button
				dataAdapter1.setDropDownViewResource(R.layout.spinner_list_item1);
				// attaching data adapter to spinner
				spin_section.setAdapter(dataAdapter1);
				Toast.makeText(ctx, "No Batch", Toast.LENGTH_SHORT).show();
			}
			
			super.onPostExecute(result);
		}
		
	}
	
	private void setBatch() {
		ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<>(ResultIntermidiateScreenActivity.this, R.layout.spinner_list_item1, batch_array);
		// Drop down layout style - list view with radio button
		dataAdapter1.setDropDownViewResource(R.layout.spinner_list_item1);
		// attaching data adapter to spinner
		spin_section.setAdapter(dataAdapter1);
		
		spin_section.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
				((TextView) adapterView.getChildAt(0)).setTextColor(getResources().getColor(R.color.text_gray));
				selBatch = batch_array[i];
				if (!batch_id[i].equals("0")) {
					GetAllExam b1 = new GetAllExam(ResultIntermidiateScreenActivity.this);
					b1.execute(schoolId, selBatch);
				}
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {
			
			}
		});
	}
	
	@SuppressLint("StaticFieldLeak")
	public class GetAllExam extends AsyncTask<String, String, String> {
		Context ctx;
		
		GetAllExam(Context ctx) {
			this.ctx = ctx;
		}
		
		@Override
		protected void onPreExecute() {
			loader_student.setVisibility(View.VISIBLE);
			super.onPreExecute();
		}
		
		@Override
		protected String doInBackground(String... params) {
			
			String school_id = params[0];
			String section_name = params[1];
			String data;
			
			try {
				
				URL url = new URL(ALL_EXAM_LIST);
				HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
				httpURLConnection.setRequestMethod("POST");
				httpURLConnection.setDoOutput(true);
				httpURLConnection.setDoInput(true);
				
				OutputStream outputStream = httpURLConnection.getOutputStream();
				
				BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
				data = URLEncoder.encode("school_id", "UTF-8") + "=" + URLEncoder.encode(school_id, "UTF-8") + "&" +
						URLEncoder.encode("section_name", "UTF-8") + "=" + URLEncoder.encode(section_name, "UTF-8");
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
			
			loader_student.setVisibility(View.GONE);
			Log.d("result", result);
			//save state for offline
			if (!result.equals("{\"result\":null}")) {
				try {
					JSONObject jo = new JSONObject(result);
					//Get the instance of JSONArray that contains JSONObjects
					JSONArray ja = jo.getJSONArray("result");
					//Iterate the jsonArray and print the info of JSONObjects
					student_array = new String[ja.length() + 1];
					student_id = new String[ja.length() + 1];
					student_array[0] = "Select Exam";
					student_id[0] = "0";
					for (int i = 0; i < ja.length(); i++) {
						jo = ja.getJSONObject(i);
						student_id[i + 1] = jo.getString("tbl_exam_id");
						student_array[i + 1] = jo.getString("tbl_exam_name");
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				setExam();
			} else {
				String[] student_array = {"Select Exam"};
				ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<>(ResultIntermidiateScreenActivity.this, R.layout.spinner_list_item1, student_array);
				// Drop down layout style - list view with radio button
				dataAdapter1.setDropDownViewResource(R.layout.spinner_list_item1);
				// attaching data adapter to spinner
				spin_student.setAdapter(dataAdapter1);
				Toast.makeText(ctx, "No Exam", Toast.LENGTH_SHORT).show();
			}
			
			super.onPostExecute(result);
		}
		
	}
	
	private void setExam() {
		ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<>(ResultIntermidiateScreenActivity.this, R.layout.spinner_list_item1, student_array);
		// Drop down layout style - list view with radio button
		dataAdapter1.setDropDownViewResource(R.layout.spinner_list_item1);
		// attaching data adapter to spinner
		spin_student.setAdapter(dataAdapter1);
		
		spin_student.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
				((TextView) adapterView.getChildAt(0)).setTextColor(getResources().getColor(R.color.text_gray));
				selStudent = student_array[i];
				if (!student_id[i].equals("0")) {
					GetAllSubject b1 = new GetAllSubject(ResultIntermidiateScreenActivity.this);
					b1.execute(schoolId, student_id[i]);
				}
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {
			
			}
		});
	}
	
	@SuppressLint("StaticFieldLeak")
	public class GetAllSubject extends AsyncTask<String, String, String> {
		Context ctx;
		
		GetAllSubject(Context ctx) {
			this.ctx = ctx;
		}
		
		@Override
		protected void onPreExecute() {
			loader_subject.setVisibility(View.VISIBLE);
			super.onPreExecute();
		}
		
		@Override
		protected String doInBackground(String... params) {
			
			String school_id = params[0];
			String section_name = params[1];
			String data;
			
			try {
				
				URL url = new URL(ALL_SUBJECT_LIST);
				HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
				httpURLConnection.setRequestMethod("POST");
				httpURLConnection.setDoOutput(true);
				httpURLConnection.setDoInput(true);
				
				OutputStream outputStream = httpURLConnection.getOutputStream();
				
				BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
				data = URLEncoder.encode("school_id", "UTF-8") + "=" + URLEncoder.encode(school_id, "UTF-8") + "&" +
						URLEncoder.encode("section_name", "UTF-8") + "=" + URLEncoder.encode(section_name, "UTF-8");
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
			
			loader_subject.setVisibility(View.GONE);
			Log.e("GetAllSubject", result);
			//save state for offline
			if (!result.equals("{\"result\":null}")) {
				try {
					JSONObject jo = new JSONObject(result);
					//Get the instance of JSONArray that contains JSONObjects
					JSONArray ja = jo.getJSONArray("result");
					//Iterate the jsonArray and print the info of JSONObjects
					
					
					subject_array = new String[ja.length() + 1];
					subject_id = new String[ja.length() + 1];
					subject_array[0] = "Select Subject";
					subject_id[0] = "0";
					for (int i = 0; i < ja.length(); i++) {
						jo = ja.getJSONObject(i);
						subject_id[i + 1] = jo.getString("tbl_exam_details_id");
						subject_array[i + 1] = jo.getString("tbl_exam_details_subject_name");
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				setSubject();
			} else {
				String[] subject_array = {"Select Subject"};
				ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<>(ResultIntermidiateScreenActivity.this, R.layout.spinner_list_item1, subject_array);
				// Drop down layout style - list view with radio button
				dataAdapter1.setDropDownViewResource(R.layout.spinner_list_item1);
				// attaching data adapter to spinner
				spin_subject.setAdapter(dataAdapter1);
				Toast.makeText(ctx, "No Subject", Toast.LENGTH_SHORT).show();
			}
			
			super.onPostExecute(result);
		}
		
	}
	
	private void setSubject() {
		ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<>(ResultIntermidiateScreenActivity.this, R.layout.spinner_list_item1, subject_array);
		// Drop down layout style - list view with radio button
		dataAdapter1.setDropDownViewResource(R.layout.spinner_list_item1);
		// attaching data adapter to spinner
		spin_subject.setAdapter(dataAdapter1);
		
		spin_subject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
				((TextView) adapterView.getChildAt(0)).setTextColor(getResources().getColor(R.color.text_gray));
				
				selSubject = subject_array[i];
				if (!subject_id[i].equals("0")) {
					GetAllStudentList1 b1 = new GetAllStudentList1(ResultIntermidiateScreenActivity.this);
					b1.execute(RESULT_CHECK, selBatch, schoolId, selStudent, selSubject);
				}
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {
			
			}
		});
	}
	
	@SuppressLint("StaticFieldLeak")
	public class GetAllStudentList1 extends AsyncTask<String, String, String> {
		Context ctx;
		
		GetAllStudentList1(Context ctx) {
			this.ctx = ctx;
		}
		
		@Override
		protected void onPreExecute() {
			Toast.makeText(ctx, "Please wait", Toast.LENGTH_SHORT).show();
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

			viewResult.setOnClickListener(view -> {
				mp.start();
				if (selBatch.equals("Select Batch")) {
					Toast.makeText(ResultIntermidiateScreenActivity.this, "Please select batch", Toast.LENGTH_SHORT).show();
				} else {
					if (selStudent.equals("Select Exam")) {
						Toast.makeText(ResultIntermidiateScreenActivity.this, "Please select exam", Toast.LENGTH_SHORT).show();
					} else {
						if (selSubject.equals("Select Subject")) {
							Toast.makeText(ResultIntermidiateScreenActivity.this, "Please select subject", Toast.LENGTH_SHORT).show();
						} else {
							Intent i = new Intent(ResultIntermidiateScreenActivity.this, AddResultForStudentListActivity.class);
							i.putExtra("selBatch", selBatch);
							i.putExtra("selStudent", selStudent);
							i.putExtra("selSubject", selSubject);
							i.putExtra("value", "view");
							i.putExtra("type", "view");
							i.putExtra("task", "0");
							startActivity(i);
							overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
						}
					}
				}

			});

			add_result.setOnClickListener(view -> {
				mp.start();
				if (selBatch.equals("Select Batch")) {
					Toast.makeText(ResultIntermidiateScreenActivity.this, "Please select batch", Toast.LENGTH_SHORT).show();
				} else {
					if (selStudent.equals("Select Exam")) {
						Toast.makeText(ResultIntermidiateScreenActivity.this, "Please select exam", Toast.LENGTH_SHORT).show();

					} else {
						if (selSubject.equals("Select Subject")) {
							Toast.makeText(ResultIntermidiateScreenActivity.this, "Please select subject", Toast.LENGTH_SHORT).show();
						} else {
							if (result.contains("no")) {
								val = "number";
								Intent i = new Intent(ResultIntermidiateScreenActivity.this, AddResultForStudentListActivity.class);
								i.putExtra("selBatch", selBatch);
								i.putExtra("selStudent", selStudent);
								i.putExtra("selSubject", selSubject);
								i.putExtra("value", "take");
								i.putExtra("type", val);
								i.putExtra("task", "0");
								startActivity(i);
								overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
							} else if (result.equals("saved")) {
								val = "number";
								Intent i = new Intent(ResultIntermidiateScreenActivity.this, AddResultForStudentListActivity.class);
								i.putExtra("selBatch", selBatch);
								i.putExtra("selStudent", selStudent);
								i.putExtra("selSubject", selSubject);
								i.putExtra("value", "take");
								i.putExtra("type", val);
								i.putExtra("task", "updateResult");
								startActivity(i);
								overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
								Toast.makeText(ctx, "Already Result Available", Toast.LENGTH_SHORT).show();
							} else {
								val = "number";
								Intent i = new Intent(ResultIntermidiateScreenActivity.this, AddResultForStudentListActivity.class);
								i.putExtra("selBatch", selBatch);
								i.putExtra("selStudent", selStudent);
								i.putExtra("selSubject", selSubject);
								i.putExtra("value", "take");
								i.putExtra("type", val);
								i.putExtra("task", "0");
								startActivity(i);
								overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
							}
						}
					}
				}
			});
			super.onPostExecute(result);
		}
		
		
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mp.release();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			onBackPressed();
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
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
