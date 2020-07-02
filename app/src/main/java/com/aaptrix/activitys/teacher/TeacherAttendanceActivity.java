package com.aaptrix.activitys.teacher;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
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

import com.aaptrix.databeans.DataBeanStudent;
import com.google.android.material.appbar.AppBarLayout;

import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.CountDownTimer;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import com.aaptrix.R;

import static com.aaptrix.tools.HttpUrl.ALL_BATCHS;
import static com.aaptrix.tools.HttpUrl.ALL_STUDENTS;
import static com.aaptrix.tools.HttpUrl.GET_SUBJECT;
import static com.aaptrix.tools.HttpUrl.SUBMIT_ATTENDANCE;
import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREFS_RW;
import static com.aaptrix.tools.SPClass.PREF_COLOR;

public class TeacherAttendanceActivity extends AppCompatActivity {
	
	SharedPreferences.Editor editor;
	String userId, roleId, schoolId, userSection, userRollNumber, userClassTeacher;

	SharedPreferences.Editor editorColor;
	String selToolColor, selDrawerColor, selStatusColor, selTextColor1, selTextColor2, numberOfUser;
	AppBarLayout appBarLayout;
	TextView tool_title;
	String userID, userLoginId, userName, userImg, userrType, userPassword, userSchoolLogo;
	String userSchoolId;
	LinearLayout logo_layout;
	//
	TextView take_attendance, view_attendance, markHoliday;
	LinearLayout attendance_action;
	ImageView school_logo;
	MediaPlayer mp;
	View v;
	//
	Spinner spin_section, spin_subject;
	EditText et_date;
	ProgressBar loader_section, loader_subject;
	//
	String batch_array[] = {"Select Batch"}, batch_id[] = {"0"};
	String[] subject_array = {"Select Subject"};
	String selBatch, selSubject;
	TextView cube1, cube2;
	TextView view1;
	Calendar myCalendar = Calendar.getInstance();
	DatePickerDialog.OnDateSetListener date;
	ArrayList<DataBeanStudent> studentArray = new ArrayList<>();
	RelativeLayout layout;
	CardView cardView;

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.teacher_attendance_layout);
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		
		setTitle("");
		setResult(RESULT_OK);
		appBarLayout = findViewById(R.id.appBarLayout);
		tool_title = findViewById(R.id.tool_title);
		layout = findViewById(R.id.layout);
		cardView = findViewById(R.id.card_view);
		//color
		mp = MediaPlayer.create(this, R.raw.button_click);
		SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
		editorColor = settingsColor.edit();
		selToolColor = settingsColor.getString("tool", "");
		selDrawerColor = settingsColor.getString("drawer", "");
		selStatusColor = settingsColor.getString("status", "");
		selTextColor1 = settingsColor.getString("text1", "");
		selTextColor2 = settingsColor.getString("text2", "");

		logo_layout = findViewById(R.id.logo_layout);
		
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
		cube1 = findViewById(R.id.cube1);
		cube2 = findViewById(R.id.cube2);
		view1 = findViewById(R.id.view1);

		//
		take_attendance = findViewById(R.id.take_attendance);
		view_attendance = findViewById(R.id.view_attendance);
		markHoliday = findViewById(R.id.mark_holiday);
		attendance_action = findViewById(R.id.attendance_action);

		SharedPreferences sp = getSharedPreferences(PREFS_RW, 0);
		String result = sp.getString("result", "");

		try {
			JSONObject jsonObject = new JSONObject(result);
			JSONArray jsonArray = jsonObject.getJSONArray("result");
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject object = jsonArray.getJSONObject(i);
				if (object.getString("tbl_insti_buzz_cate_name").equals("Attendance")) {
					if (object.getString("tbl_scl_inst_buzz_detl_write_status").equals("Active")) {
						take_attendance.setVisibility(View.VISIBLE);
					} else {
						take_attendance.setVisibility(View.GONE);
					}
					break;
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		school_logo = findViewById(R.id.school_logo);
		
		spin_section = findViewById(R.id.spin_section);
		spin_subject = findViewById(R.id.spin_subject);
		et_date = findViewById(R.id.et_date);
		et_date.setFocusable(false);
		String myFormat = "dd-MM-yyyy"; //In which you need put here
		SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
		et_date.setText(sdf.format(myCalendar.getTime()));
		loader_section = findViewById(R.id.loader_section);
		loader_subject = findViewById(R.id.loader_subject);
		
		Picasso.with(this).load(R.drawable.large_logo).into(school_logo);
		view_attendance.setClickable(false);
		take_attendance.setClickable(false);

		String[] subject_array = {"Select Subject"};
		ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<>(TeacherAttendanceActivity.this, R.layout.spinner_list_item1, subject_array);
		dataAdapter1.setDropDownViewResource(R.layout.spinner_list_item1);
		spin_subject.setAdapter(dataAdapter1);
		spin_subject.setEnabled(false);
		
		setBatch();

		try {
			File directory = this.getFilesDir();
			ObjectInputStream in = new ObjectInputStream(new FileInputStream(new File(directory, "batches")));
			String json = in.readObject().toString();
			in.close();
			if (!json.equals("{\"result\":null}")) {
				try {
					JSONObject jo = new JSONObject(json);
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
				String batch_array[] = {"Select Batch"};
				ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<>(this, R.layout.spinner_list_item1, batch_array);
				dataAdapter2.setDropDownViewResource(R.layout.spinner_list_item1);
				spin_section.setAdapter(dataAdapter2);
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
		take_attendance.setTextColor(Color.parseColor(selTextColor1));
		view_attendance.setTextColor(Color.parseColor(selTextColor1));
		attendance_action.setBackgroundColor(Color.parseColor(selToolColor));
		GradientDrawable drawable = (GradientDrawable) cube1.getBackground();
		drawable.setStroke(2, Color.parseColor(selToolColor));
		GradientDrawable drawable1 = (GradientDrawable) cube2.getBackground();
		drawable1.setStroke(2, Color.parseColor(selToolColor));
		view1.setBackgroundColor(Color.parseColor(selToolColor));
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
			if (!result.equals("{\"result\":null}")) {
				try {
					JSONObject jo = new JSONObject(result);
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
				String batch_array[] = {"Select Batch"};
				ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<>(TeacherAttendanceActivity.this, R.layout.spinner_list_item1, batch_array);
				dataAdapter1.setDropDownViewResource(R.layout.spinner_list_item1);
				spin_section.setAdapter(dataAdapter1);
				Toast.makeText(ctx, "No Batch", Toast.LENGTH_SHORT).show();
			}
			super.onPostExecute(result);
		}
	}
	
	private void setBatch() {
		ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<>(TeacherAttendanceActivity.this, R.layout.spinner_list_item1, batch_array);
		// Drop down layout style - list view with radio button
		dataAdapter1.setDropDownViewResource(R.layout.spinner_list_item1);
		// attaching data adapter to spinner
		spin_section.setAdapter(dataAdapter1);
		
		spin_section.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
				selBatch = batch_array[i];
				if (!selBatch.equals("Select Batch")) {
					spin_subject.setEnabled(true);
					GetSubjects getSubjects = new GetSubjects(TeacherAttendanceActivity.this);
					getSubjects.execute(schoolId, selBatch);
				} else {
					spin_subject.setEnabled(false);
					Toast.makeText(TeacherAttendanceActivity.this, "Select Batch", Toast.LENGTH_SHORT).show();
				}
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {
			
			}
		});
	}

	@SuppressLint("StaticFieldLeak")
	public class GetSubjects extends AsyncTask<String, String, String> {
		Context ctx;

		GetSubjects(Context ctx) {
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
			String batchNm = params[1];
			String data;

			try {

				URL url = new URL(GET_SUBJECT);
				HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
				httpURLConnection.setRequestMethod("POST");
				httpURLConnection.setDoOutput(true);
				httpURLConnection.setDoInput(true);

				OutputStream outputStream = httpURLConnection.getOutputStream();

				BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream, StandardCharsets.UTF_8));
				data = URLEncoder.encode("school_id", "UTF-8") + "=" + URLEncoder.encode(school_id, "UTF-8") + "&" +
						URLEncoder.encode("batchNm", "UTF-8") + "=" + URLEncoder.encode(batchNm, "UTF-8");
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
			Log.e("res", result);
			if (!result.equals("{\"result\":null}")) {
				try {
					JSONObject jo = new JSONObject(result);
					JSONArray ja = jo.getJSONArray("SubjectList");
					subject_array = new String[ja.length() + 1];
					subject_array[0] = "Select Subject";
					for (int i = 0; i < ja.length(); i++) {
						jo = ja.getJSONObject(i);
						subject_array[i + 1] = jo.getString("tbl_batch_subjct_name");
						Log.e("json", jo.getString("tbl_batch_subjct_name"));
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				setSubject();
			} else {
				String[] subject_array = {"Select Subject"};
				ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<>(TeacherAttendanceActivity.this, R.layout.spinner_list_item1, subject_array);
				dataAdapter1.setDropDownViewResource(R.layout.spinner_list_item1);
				spin_subject.setAdapter(dataAdapter1);
				Toast.makeText(ctx, "No Subject", Toast.LENGTH_SHORT).show();
			}
			super.onPostExecute(result);
		}
	}

	private void setSubject() {
		ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<>(TeacherAttendanceActivity.this, R.layout.spinner_list_item1, subject_array);
		dataAdapter1.setDropDownViewResource(R.layout.spinner_list_item1);
		spin_subject.setAdapter(dataAdapter1);

		spin_subject.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
				((TextView) adapterView.getChildAt(0)).setTextColor(getResources().getColor(R.color.text_gray));

				date = (view23, year, monthOfYear, dayOfMonth) -> {
					// TODO Auto-generated method stub
					myCalendar.set(Calendar.YEAR, year);
					myCalendar.set(Calendar.MONTH, monthOfYear);
					myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
					String myFormat = "dd-MM-yyyy"; //In which you need put here
					SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.getDefault());
					et_date.setText(sdf.format(myCalendar.getTime()));
				};

				et_date.setOnClickListener(v -> {
					DatePickerDialog datePickerDialog = new DatePickerDialog(TeacherAttendanceActivity.this, R.style.AlertDialogCustom1, date, myCalendar
							.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
							myCalendar.get(Calendar.DAY_OF_MONTH));
					datePickerDialog.show();
				});

				selSubject = subject_array[i];
				if (isInternetOn()) {
					take_attendance.setOnClickListener(view2 -> {
						mp.start();
						if (!selBatch.equals("Select Batch")) {
							if (!selSubject.equals("Select Subject")) {
								Intent i1 = new Intent(TeacherAttendanceActivity.this, StudentListActivity.class);
								i1.putExtra("batch", selBatch);
								i1.putExtra("subject", selSubject);
								i1.putExtra("value", "take");
								i1.putExtra("date", et_date.getText().toString());
								startActivity(i1);
								overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
							} else {
								Toast.makeText(TeacherAttendanceActivity.this, "Select Subject", Toast.LENGTH_SHORT).show();
							}
						} else {
							Toast.makeText(TeacherAttendanceActivity.this, "Select Batch", Toast.LENGTH_SHORT).show();
						}
					});

					view_attendance.setOnClickListener(view22 -> {
						mp.start();
						if (!selBatch.equals("Select Batch")) {
							if (!selSubject.equals("Select Subject")) {
								Intent i12 = new Intent(TeacherAttendanceActivity.this, StudentListActivity.class);
								i12.putExtra("batch", selBatch);
								i12.putExtra("subject", selSubject);
								i12.putExtra("value", "view");
								i12.putExtra("date", et_date.getText().toString());
								startActivity(i12);
								overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
							} else {
								Toast.makeText(TeacherAttendanceActivity.this, "Select Subject", Toast.LENGTH_SHORT).show();
							}
						} else {
							Toast.makeText(TeacherAttendanceActivity.this, "Select Batch", Toast.LENGTH_SHORT).show();
						}
					});

					markHoliday.setOnClickListener(view2 -> {
						mp.start();
						if (!selBatch.equals("Select Batch")) {
							if (!selSubject.equals("Select Subject")) {
								new AlertDialog.Builder(TeacherAttendanceActivity.this)
										.setMessage("Are you sure you want to mark " + et_date.getText().toString() + " as holiday for " + selBatch + "?")
										.setPositiveButton("Yes", (dialogInterface, i13) -> {
											SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
											SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
											String formattedDate1;
											try {
												formattedDate1 = df1.format(df.parse(et_date.getText().toString()));
												GetAllStudentList b1 = new GetAllStudentList(TeacherAttendanceActivity.this);
												b1.execute(ALL_STUDENTS, selBatch, schoolId, formattedDate1, selSubject);
											} catch (ParseException e) {
												e.printStackTrace();
											}
										})
										.setNegativeButton("No", null)
										.show();
							} else {
								Toast.makeText(TeacherAttendanceActivity.this, "Select Subject", Toast.LENGTH_SHORT).show();
							}
						} else {
							Toast.makeText(TeacherAttendanceActivity.this, "Select Batch", Toast.LENGTH_SHORT).show();
						}
					});

				} else {
					Toast.makeText(TeacherAttendanceActivity.this, "No internet", Toast.LENGTH_SHORT).show();
				}
			}

			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {

			}
		});
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
			layout.setVisibility(View.VISIBLE);
			layout.bringToFront();
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
			Log.e("date", formattedDate1);

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
						Intent i = new Intent(ctx, TeacherAttendanceActivity.class);
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
			if (result.contains("{\"result\":null,")) {
				Toast.makeText(ctx, "No students", Toast.LENGTH_SHORT).show();
			} else {
				try {
					JSONObject jsonRootObject = new JSONObject(result);
					JSONArray jsonArray = jsonRootObject.getJSONArray("result");
					studentArray.clear();
					for (int i = 0; i < jsonArray.length(); i++) {
						DataBeanStudent dbs = new DataBeanStudent();
						JSONObject jsonObject = jsonArray.getJSONObject(i);
						dbs.setUserClass(jsonObject.getString("attendanceStatus"));
						dbs.setUserID(jsonObject.getString("tbl_users_id"));
						dbs.setUserName(jsonObject.getString("tbl_users_name"));
						dbs.setUserImg(jsonObject.getString("tbl_users_img"));
						dbs.setUserLoginId(jsonObject.getString("dataleave"));
						studentArray.add(dbs);
					}
					makeStudentArray();
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (ParseException e) {
					e.printStackTrace();
				}
				super.onPostExecute(result);
			}
		}
	}

	private void makeStudentArray() throws ParseException {
		ArrayList<DataBeanStudent> holidayArray = new ArrayList<>();
		for (int i = 0; i < studentArray.size(); i++) {
			DataBeanStudent dbs = new DataBeanStudent();
			dbs.setUserID(studentArray.get(i).getUserID());
			dbs.setUserLoginId("Holiday");
			holidayArray.add(dbs);
		}

		Gson gson = new GsonBuilder().create();
		JsonArray myCustomArray = gson.toJsonTree(holidayArray).getAsJsonArray();

		SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");

		SubmitAttendance b1 = new SubmitAttendance(TeacherAttendanceActivity.this);
		b1.execute(SUBMIT_ATTENDANCE, myCustomArray.toString(), df1.format(df.parse(et_date.getText().toString())), selBatch, userId, schoolId, selSubject);
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
