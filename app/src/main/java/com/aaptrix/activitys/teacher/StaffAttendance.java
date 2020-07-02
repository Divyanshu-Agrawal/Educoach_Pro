package com.aaptrix.activitys.teacher;

import com.aaptrix.activitys.admin.TeacherAttendance;
import com.aaptrix.R;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

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
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.aaptrix.databeans.StaffData;
import com.google.android.material.appbar.AppBarLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;
import java.util.Objects;

import static com.aaptrix.tools.HttpUrl.STAFF_LIST;
import static com.aaptrix.tools.HttpUrl.STAFF_SUBMIT_ATTENDANCE;
import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREF_COLOR;

public class StaffAttendance extends AppCompatActivity {
	
	AppBarLayout appBarLayout;
	String selToolColor, selStatusColor, selTextColor1;
	TextView tool_title;
	Spinner type;
	String[] category = {"Select Type", "Teacher", "Staff", "Other"};
	Calendar myCalendar = Calendar.getInstance();
	DatePickerDialog.OnDateSetListener datePicker;
	EditText date;
	String attenType;
	ImageView school_logo;
	ProgressBar loader_section;
	TextView cube1, cube2;
	TextView view1;
	RelativeLayout layout;
	CardView cardView;
	ArrayList<StaffData> staffArray = new ArrayList<>();
	MediaPlayer mp;
	TextView take_attendance, view_attendance, mark_holiday;
	String userId, userrType, userSchoolLogo, roleId, schoolId, strDate;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_staff_attendance);
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		appBarLayout = findViewById(R.id.appBarLayout);
		tool_title = findViewById(R.id.tool_title);
		cube1 = findViewById(R.id.cube1);
		cube2 = findViewById(R.id.cube2);
		view1 = findViewById(R.id.view1);
		layout = findViewById(R.id.layout);
		cardView = findViewById(R.id.card_view);
		type = findViewById(R.id.spin_section);
		date = findViewById(R.id.et_date);
		take_attendance = findViewById(R.id.take_attendance);
		view_attendance = findViewById(R.id.view_attendance);
		mark_holiday = findViewById(R.id.mark_holiday);
		school_logo = findViewById(R.id.school_logo);
		loader_section = findViewById(R.id.loader_section);
		mp = MediaPlayer.create(this, R.raw.button_click);
		
		SharedPreferences settings = this.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
		roleId = settings.getString("str_role_id", "");
		schoolId = settings.getString("str_school_id", "");
		userSchoolLogo = settings.getString("userSchoolLogo", "");
		userId = settings.getString("userID", "");
		
		String url = settings.getString("imageUrl", "") + settings.getString("userSchoolId", "") + "/other/" + userSchoolLogo;
		Picasso.with(this).load(url).error(R.drawable.dummy).placeholder(R.drawable.dummy).into(school_logo);
		view_attendance.setClickable(false);
		take_attendance.setClickable(false);
		
		SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
		selToolColor = settingsColor.getString("tool", "");
		selStatusColor = settingsColor.getString("status", "");
		selTextColor1 = settingsColor.getString("text1", "");
		
		appBarLayout.setBackgroundColor(Color.parseColor(selToolColor));
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			Window window = getWindow();
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.setStatusBarColor(Color.parseColor(selStatusColor));
		}
		tool_title.setTextColor(Color.parseColor(selTextColor1));
		take_attendance.setTextColor(Color.parseColor(selTextColor1));
		view_attendance.setTextColor(Color.parseColor(selTextColor1));
		take_attendance.setBackgroundColor(Color.parseColor(selToolColor));
		view_attendance.setBackgroundColor(Color.parseColor(selToolColor));
		GradientDrawable drawable = (GradientDrawable) cube1.getBackground();
		drawable.setStroke(2, Color.parseColor(selToolColor));
		GradientDrawable drawable1 = (GradientDrawable) cube2.getBackground();
		drawable1.setStroke(2, Color.parseColor(selToolColor));
		view1.setBackgroundColor(Color.parseColor(selToolColor));
		
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, R.layout.spinner_list_item1, category);
		dataAdapter.setDropDownViewResource(R.layout.spinner_list_item1);
		type.setAdapter(dataAdapter);
		
		date.setText(new SimpleDateFormat("dd-MM-YYYY", Locale.getDefault()).format(System.currentTimeMillis()));
		strDate = new SimpleDateFormat("dd-MM-YYYY", Locale.getDefault()).format(System.currentTimeMillis());
		
		datePicker = (view, year, monthOfYear, dayOfMonth) -> {
			myCalendar.set(Calendar.YEAR, year);
			myCalendar.set(Calendar.MONTH, monthOfYear);
			myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			String myFormat = "dd-MM-YYYY";
			SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
			date.setText(sdf.format(myCalendar.getTime()));
			strDate = sdf.format(myCalendar.getTime());
		};
		
		date.setOnClickListener(v -> {
			DatePickerDialog datePickerDialog = new DatePickerDialog(StaffAttendance.this, R.style.AlertDialogCustom1, datePicker, myCalendar
					.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
					myCalendar.get(Calendar.DAY_OF_MONTH));
			datePickerDialog.getDatePicker().setMaxDate(System.currentTimeMillis());
			datePickerDialog.show();
		});
		
		type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
			@Override
			public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
				((TextView) parent.getChildAt(0)).setTextColor(getResources().getColor(R.color.text_gray));
				if(!category[position].equals("Select Type")) {
					switch (category[position]) {
						case "Teacher":
							attenType = "2";
							break;
						case "Staff":
							attenType = "4";
							break;
						default:
							attenType = "5";
							break;
					}
				} else {
					attenType = "Select Type";
				}
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> parent) {
			
			}
		});
		
		if (isInternetOn()) {
			take_attendance.setOnClickListener(view2 -> {
				mp.start();
				if (!attenType.equals("Select Type")) {
					Intent i1 = new Intent(this, TeacherAttendance.class);
					i1.putExtra("type", attenType);
					i1.putExtra("value", "take");
					i1.putExtra("date", strDate);
					startActivity(i1);
					overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
				} else {
					Toast.makeText(this, "Select Type", Toast.LENGTH_SHORT).show();
				}
			});
			
			view_attendance.setOnClickListener(view22 -> {
				mp.start();
				if (!attenType.equals("Select Type")) {
					Intent i12 = new Intent(this, TeacherAttendance.class);
					i12.putExtra("type", attenType);
					i12.putExtra("value", "view");
					i12.putExtra("date", strDate);
					startActivity(i12);
					overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
				} else {
					Toast.makeText(this, "Select Type", Toast.LENGTH_SHORT).show();
				}
			});

			mark_holiday.setOnClickListener(view -> {
				mp.start();
				if (!attenType.equals("Select Type")) {
					SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
					SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");
					String formattedDate1;
					try {
						formattedDate1 = df1.format(df.parse(strDate));
						GetAllStaff b1 = new GetAllStaff(this);
						b1.execute(STAFF_LIST, attenType, schoolId, formattedDate1);
					} catch (ParseException e) {
						e.printStackTrace();
					}

				} else {
					Toast.makeText(this, "Select Type", Toast.LENGTH_SHORT).show();
				}
			});
			
		} else {
			Toast.makeText(this, "No internet", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mp.release();
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
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == android.R.id.home) {
			onBackPressed();
		}
		return true;
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
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
				staffArray.clear();
				Toast.makeText(ctx, "No Staff", Toast.LENGTH_SHORT).show();
			} else {
				try {
					JSONObject jsonRootObject = new JSONObject(result);
					JSONArray jsonArray = jsonRootObject.getJSONArray("result");
					staffArray.clear();
					for (int i = 0; i < jsonArray.length(); i++) {
						StaffData staffData = new StaffData();
						JSONObject jsonObject = jsonArray.getJSONObject(i);
						staffData.setId(jsonObject.getString("tbl_users_id"));
						staffData.setName(jsonObject.getString("tbl_users_name"));
						staffData.setImage(jsonObject.getString("tbl_users_img"));
						staffData.setLeaveStatus(jsonObject.getString("dataleave"));
						staffData.setAttenStatus(jsonObject.getString("attendanceStatus"));
						staffData.setType(type);
						staffArray.add(staffData);
					}
					markHoliday();
				} catch (JSONException e) {
					e.printStackTrace();
				} catch (ParseException e) {
					e.printStackTrace();
				}
				super.onPostExecute(result);
			}
		}
	}

	private void markHoliday() throws ParseException {
		ArrayList<StaffData> holidayArray = new ArrayList<>();
		for (int i = 0; i < staffArray.size(); i++) {
			StaffData data = new StaffData();
			data.setId(staffArray.get(i).getId());
			data.setAttenStatus("Holiday");
			holidayArray.add(data);
		}
		Gson gson = new GsonBuilder().create();
		JsonArray myCustomArray = gson.toJsonTree(holidayArray).getAsJsonArray();

		SimpleDateFormat df1 = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat df = new SimpleDateFormat("dd-MM-yyyy");

		SubmitStaffAttendance b1 = new SubmitStaffAttendance(this);
		b1.execute(STAFF_SUBMIT_ATTENDANCE, myCustomArray.toString(), df1.format(df.parse(strDate)), attenType, userId, schoolId);
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
						Intent i = new Intent(ctx, StaffAttendance.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
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
}
