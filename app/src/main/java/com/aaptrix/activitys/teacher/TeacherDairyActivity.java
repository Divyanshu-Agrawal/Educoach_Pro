package com.aaptrix.activitys.teacher;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
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

import com.aaptrix.activitys.admin.AddNewDiary;
import com.aaptrix.activitys.student.DairyActivity;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
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
import java.util.ArrayList;
import java.util.Objects;

import com.aaptrix.databeans.DataBeanStudent;
import com.aaptrix.adaptor.StudentListAdaptor;
import com.aaptrix.adaptor.StudentListAdaptorView;
import com.aaptrix.R;

import static com.aaptrix.tools.HttpUrl.ALL_BATCHS;
import static com.aaptrix.tools.HttpUrl.ALL_STUDENTS_LIST;
import static com.aaptrix.tools.SPClass.PREFS_DAIRY;
import static com.aaptrix.tools.SPClass.PREFS_NAME;
import static com.aaptrix.tools.SPClass.PREFS_RW;
import static com.aaptrix.tools.SPClass.PREF_COLOR;

public class TeacherDairyActivity extends AppCompatActivity {
	
	SharedPreferences.Editor editor;
	String userId, roleId, schoolId, userClass, userSection, userRollNumber, userClassTeacher;
	AlertDialog.Builder alert;
	SharedPreferences.Editor editorColor;
	String selToolColor;
	String selDrawerColor;
	String selStatusColor;
	String selTextColor1;
	String selTextColor2;
	String numberOfUser;
	AppBarLayout appBarLayout;
	TextView tool_title, tv_logout_text;
	ImageView logoutImg;
	String userID, userLoginId, userName, userImg, userrType, userPassword, userSchoolLogo;
	String userSchoolId;
	LinearLayout logo_layout;
	//
	TextView take_attendance, view_attendance;
	LinearLayout attendance_diary;
	ImageView school_logo;
	//
	MediaPlayer mp;
	Spinner spin_section;
	ProgressBar loader_section, loader_student;
	String batch_array[] = {"Select Batch"}, batch_id[] = {"0"};
	String selBatch;
	TextView tv_student;
	ArrayList<DataBeanStudent> studentArray = new ArrayList<>();
	DataBeanStudent dbs;
	String studentId;
	String studentName;
	String studentImage;
	ListView user_list;
	CheckBox cb_selectAll;
	StudentListAdaptor studentListAdaptor;
	StudentListAdaptorView studentListAdaptorView;
	AlertDialog alertDialog;
	boolean IsVisibleMain = false;
	SharedPreferences.Editor se_dairy;
	TextView cube1, cube2;
	TextView view1;
	private SharedPreferences sp_dairy;
	
	public static void hideSoftKeyboard(Activity activity) {
		InputMethodManager imm = (InputMethodManager) activity.getSystemService(
				Activity.INPUT_METHOD_SERVICE);
		assert imm != null;
		imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.teacher_dairy_layout);
		Toolbar toolbar = findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);
		Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setDisplayShowTitleEnabled(false);
		mp = MediaPlayer.create(this, R.raw.button_click);
		setTitle("");
		setResult(RESULT_OK);
		appBarLayout = findViewById(R.id.appBarLayout);
		tool_title = findViewById(R.id.tool_title);
		tv_student = findViewById(R.id.tv_student);
		//color
		SharedPreferences settingsColor = getSharedPreferences(PREF_COLOR, 0);
		editorColor = settingsColor.edit();
		selToolColor = settingsColor.getString("tool", "");
		selDrawerColor = settingsColor.getString("drawer", "");
		selStatusColor = settingsColor.getString("status", "");
		selTextColor1 = settingsColor.getString("text1", "");
		selTextColor2 = settingsColor.getString("text2", "");
		
		SwipeRefreshLayout mSwipeRefreshLayout = findViewById(R.id.activity_main_swipe_refresh_layout);
		mSwipeRefreshLayout.setEnabled(false);
		tv_logout_text = findViewById(R.id.tv_logout_text);
		logoutImg = findViewById(R.id.logoutImg);
		logo_layout = findViewById(R.id.logo_layout);
		
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
		
		//
		take_attendance = findViewById(R.id.take_attendance);
		view_attendance = findViewById(R.id.view_attendance);
		attendance_diary = findViewById(R.id.attendance_action);

		SharedPreferences sp = getSharedPreferences(PREFS_RW, 0);
		String result = sp.getString("result", "");

		try {
			JSONObject jsonObject = new JSONObject(result);
			JSONArray jsonArray = jsonObject.getJSONArray("result");
			for (int i = 0; i < jsonArray.length(); i++) {
				JSONObject object = jsonArray.getJSONObject(i);
				if (object.getString("tbl_insti_buzz_cate_name").equals("Remarks")) {
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
		loader_section = findViewById(R.id.loader_section);
		loader_student = findViewById(R.id.loader_student);
		
		cube1 = findViewById(R.id.cube1);
		cube2 = findViewById(R.id.cube2);
		view1 = findViewById(R.id.view1);
		
		Picasso.with(this).load(R.drawable.large_logo).into(school_logo);
		view_attendance.setClickable(false);
		take_attendance.setClickable(false);
		
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
				ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<>(this, R.layout.spinner_list_item1, batch_array);
				dataAdapter1.setDropDownViewResource(R.layout.spinner_list_item1);
				spin_section.setAdapter(dataAdapter1);
				Toast.makeText(this, "No Batch", Toast.LENGTH_SHORT).show();
			}
		} catch (Exception e) {
			e.printStackTrace();
			GetAllBatches b1 = new GetAllBatches(this);
			b1.execute(schoolId);
		}

		
		take_attendance.setOnClickListener(view -> {
			mp.start();
			if (selBatch.equals("Select Batch")) {
				Toast.makeText(TeacherDairyActivity.this, "Please select batch first", Toast.LENGTH_SHORT).show();
			} else {
				if (studentArray.size() != 0) {
					setNumberOfUsers(studentArray);
				} else {
					Toast.makeText(this, "No Students", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		view_attendance.setOnClickListener(view -> {
			mp.start();
			if ((selBatch.equals("Select Batch"))) {
				Toast.makeText(TeacherDairyActivity.this, "Please select batch", Toast.LENGTH_SHORT).show();
			} else {
				if (studentArray.size() != 0) {
					setNumberOfUsersForView(studentArray);
				} else {
					Toast.makeText(this, "No Students", Toast.LENGTH_SHORT).show();
				}
			}
		});
		
		
		appBarLayout.setBackgroundColor(Color.parseColor(selToolColor));
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
			Window window = getWindow();
			window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
			window.setStatusBarColor(Color.parseColor(selStatusColor));
		}
		tool_title.setTextColor(Color.parseColor(selTextColor1));
		take_attendance.setTextColor(Color.parseColor(selTextColor1));
		view_attendance.setTextColor(Color.parseColor(selTextColor1));
		attendance_diary.setBackgroundColor(Color.parseColor(selToolColor));
		GradientDrawable drawable = (GradientDrawable) cube1.getBackground();
		drawable.setStroke(2, Color.parseColor(selToolColor));
		GradientDrawable drawable1 = (GradientDrawable) cube2.getBackground();
		drawable1.setStroke(2, Color.parseColor(selToolColor));
		view1.setBackgroundColor(Color.parseColor(selToolColor));
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mp.release();
	}
	
	private void setBatch() {
		ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<>(TeacherDairyActivity.this, R.layout.spinner_list_item1, batch_array);
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
					if (isInternetOn()) {
						studentArray.clear();
						GetAllStudentList b1 = new GetAllStudentList(TeacherDairyActivity.this);
						b1.execute(ALL_STUDENTS_LIST, selBatch, schoolId);
						view_attendance.setOnClickListener(view2 -> setNumberOfUsersForView(studentArray));
						
					} else {
						Toast.makeText(TeacherDairyActivity.this, "No internet", Toast.LENGTH_SHORT).show();
					}
				}
			}
			
			@Override
			public void onNothingSelected(AdapterView<?> adapterView) {
			
			}
		});
	}
	
	private void setNumberOfUsersForView(final ArrayList<DataBeanStudent> studentArray) {

		if (studentArray.size() != 0) {

			LayoutInflater factory = LayoutInflater.from(TeacherDairyActivity.this);
//text_entry is an Layout XML file containing two text field to display in alert dialog
			final View textEntryView = factory.inflate(R.layout.tudent_select_dialog1, null);

			user_list = textEntryView.findViewById(R.id.user_list);
			studentListAdaptorView = new StudentListAdaptorView(TeacherDairyActivity.this, R.layout.user_select_dialog, studentArray);
			user_list.setAdapter(studentListAdaptorView);
			//

			user_list.setOnItemClickListener((parent, view, position, id) -> {
				String studentId = studentArray.get(position).getUserID();
				Intent i = new Intent(TeacherDairyActivity.this, DairyActivity.class);
				i.putExtra("batch", selBatch);
				i.putExtra("studentId", studentId);
				startActivity(i);
				overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
				alertDialog.dismiss();
				//	view.setSelected(true);
				for (int j = 0; j < parent.getChildCount(); j++)
					parent.getChildAt(j).setBackgroundColor(Color.TRANSPARENT);
				// change the background color of the selected element
				view.setBackgroundColor(getResources().getColor(R.color.light_gray1));


			});

			alert = new AlertDialog.Builder(TeacherDairyActivity.this, R.style.DialogTheme);

			alert.setTitle(selBatch).setView(textEntryView).setPositiveButton("Cancel",
					(dialog, whichButton) -> alertDialog.dismiss());
			//alert.show();
			alertDialog = alert.create();
			alertDialog.show();
			Button theButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
			Button theButton1 = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
			theButton.setTextColor(getResources().getColor(R.color.text_gray));
			theButton1.setTextColor(getResources().getColor(R.color.text_gray));
			alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
					.setEnabled(true);
		} else {
			Toast.makeText(this, "No Students", Toast.LENGTH_SHORT).show();
		}
	}
	
	private void setNumberOfUsers(final ArrayList<DataBeanStudent> studentArray) {
		
		LayoutInflater factory = LayoutInflater.from(TeacherDairyActivity.this);
		final View textEntryView = factory.inflate(R.layout.tudent_select_dialog, null);
		
		user_list = textEntryView.findViewById(R.id.user_list);
		cb_selectAll = textEntryView.findViewById(R.id.cb_selectAll);
		studentListAdaptor = new StudentListAdaptor(TeacherDairyActivity.this, R.layout.user_select_dialog, studentArray, IsVisibleMain);
		user_list.setAdapter(studentListAdaptor);
		
		
		cb_selectAll.setOnCheckedChangeListener((compoundButton, b) -> {
			if (b) {
				studentListAdaptor = new StudentListAdaptor(TeacherDairyActivity.this, R.layout.user_select_dialog, studentArray, b);
				user_list.setAdapter(studentListAdaptor);
				studentListAdaptor.notifyDataSetChanged();
				
				Gson gson = new GsonBuilder().create();
				JsonArray myCustomArray = gson.toJsonTree(studentArray).getAsJsonArray();
				JsonArray studentArray1 = gson.toJsonTree(studentArray).getAsJsonArray();
				sp_dairy = getSharedPreferences(PREFS_DAIRY, 0);
				se_dairy = sp_dairy.edit();
				se_dairy.clear();
				se_dairy.putString("dairy", "" + myCustomArray);
				se_dairy.putString("studentArray", "" + studentArray1);
				se_dairy.commit();
				
			} else {
				studentListAdaptor = new StudentListAdaptor(TeacherDairyActivity.this, R.layout.user_select_dialog, studentArray, b);
				user_list.setAdapter(studentListAdaptor);
				studentListAdaptor.notifyDataSetChanged();
				//studentListAdaptor.unselectAll(b);
				sp_dairy = getSharedPreferences(PREFS_DAIRY, 0);
				se_dairy = sp_dairy.edit();
				se_dairy.clear();
				se_dairy.commit();
			}
		});
		
		user_list.setOnItemClickListener((parent, view, position, id) -> Toast.makeText(TeacherDairyActivity.this, "" + position, Toast.LENGTH_SHORT).show());
		
		alert = new AlertDialog.Builder(TeacherDairyActivity.this, R.style.DialogTheme);
		
		alert.setTitle(selBatch).setView(textEntryView).setPositiveButton("Ok",
				(dialog, whichButton) -> {
					alertDialog.dismiss();
					Intent intent = new Intent(TeacherDairyActivity.this, AddNewDiary.class);
					intent.putExtra("type", "add");
					intent.putExtra("batch", selBatch);
					startActivity(intent);
				}).setNegativeButton("Cancel", null);
		//alert.show();
		alertDialog = alert.create();
		alertDialog.show();
		Button theButton = alertDialog.getButton(DialogInterface.BUTTON_POSITIVE);
		Button theButton1 = alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE);
		theButton.setTextColor(getResources().getColor(R.color.text_gray));
		theButton1.setTextColor(getResources().getColor(R.color.text_gray));
		alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
				.setEnabled(true);
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
					//Iterate the jsonArray and print the info of JSONObjects
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
				ArrayAdapter<String> dataAdapter1 = new ArrayAdapter<>(TeacherDairyActivity.this, R.layout.spinner_list_item1, batch_array);
				// Drop down layout style - list view with radio button
				dataAdapter1.setDropDownViewResource(R.layout.spinner_list_item1);
				// attaching data adapter to spinner
				spin_section.setAdapter(dataAdapter1);
				Toast.makeText(ctx, "No Batch", Toast.LENGTH_SHORT).show();
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
			loader_student.setVisibility(View.VISIBLE);
			super.onPreExecute();
		}
		
		@Override
		protected String doInBackground(String... params) {
			
			String branch_url = params[0];
			String sectionName = params[1];
			String school_id = params[2];
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
						URLEncoder.encode("school_id", "UTF-8") + "=" + URLEncoder.encode(school_id, "UTF-8");
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
			loader_student.setVisibility(View.GONE);
			
			//	pDialog.dismiss();
			if (result.equals("{\"result\":null}")) {
				studentArray.clear();
				Toast.makeText(ctx, "No Value", Toast.LENGTH_SHORT).show();
				
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
						dbs.setUserID(studentId);
						dbs.setUserName(studentName);
						dbs.setUserImg(studentImage);
						studentArray.add(dbs);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				super.onPostExecute(result);
			}
		}
		
	}
}
